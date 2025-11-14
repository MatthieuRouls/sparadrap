package main.model.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour initialiser la base de données.
 * Permet d'exécuter le script SQL d'initialisation.
 */
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final String INIT_SCRIPT = "sql/init_database.sql";

    /**
     * Exécute le script d'initialisation de la base de données.
     *
     * @return true si l'initialisation est réussie, false sinon
     */
    public static boolean initializeDatabase() {
        logger.info("Démarrage de l'initialisation de la base de données...");

        try {
            String sqlScript = loadSqlScript();
            if (sqlScript == null || sqlScript.isEmpty()) {
                logger.error("Script SQL vide ou non trouvé.");
                return false;
            }

            executeSqlScript(sqlScript);
            logger.info("Base de données initialisée avec succès.");
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de la base de données: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Charge le script SQL depuis les ressources.
     */
    private static String loadSqlScript() {
        try (InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(INIT_SCRIPT)) {
            if (inputStream == null) {
                logger.error("Fichier {} non trouvé dans les ressources.", INIT_SCRIPT);
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la lecture du script SQL: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Exécute un script SQL.
     * Le script est divisé en plusieurs commandes séparées par des points-virgules.
     */
    private static void executeSqlScript(String script) throws SQLException {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Diviser le script en commandes individuelles
            String[] commands = script.split(";");

            int executedCommands = 0;
            for (String command : commands) {
                String trimmedCommand = command.trim();

                // Ignorer les commentaires et les lignes vides
                if (trimmedCommand.isEmpty() || trimmedCommand.startsWith("--")) {
                    continue;
                }

                try {
                    statement.execute(trimmedCommand);
                    executedCommands++;
                    logger.debug("Commande SQL exécutée: {}", trimmedCommand.substring(0, Math.min(50, trimmedCommand.length())));
                } catch (SQLException e) {
                    // Certaines erreurs peuvent être ignorées (ex: table déjà existante)
                    if (e.getMessage().contains("already exists") || e.getMessage().contains("Duplicate")) {
                        logger.warn("Commande ignorée (déjà existant): {}", e.getMessage());
                    } else {
                        logger.error("Erreur lors de l'exécution de la commande: {}", trimmedCommand, e);
                        throw e;
                    }
                }
            }

            logger.info("Total de commandes SQL exécutées: {}", executedCommands);

        } catch (SQLException e) {
            logger.error("Erreur lors de l'exécution du script SQL: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Teste la connexion à la base de données et affiche les informations.
     */
    public static boolean testConnection() {
        logger.info("Test de connexion à la base de données...");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        boolean result = dbConnection.testConnection();

        if (result) {
            logger.info("Connexion réussie!");
            logger.info(dbConnection.getPoolStats());
        } else {
            logger.error("Échec de la connexion à la base de données.");
        }

        return result;
    }

    /**
     * Point d'entrée pour tester la connexion et initialiser la base.
     * Usage: java main.model.database.DatabaseInitializer [init|test]
     */
    public static void main(String[] args) {
        String command = args.length > 0 ? args[0] : "test";

        switch (command.toLowerCase()) {
            case "init":
                System.out.println("=== Initialisation de la base de données ===");
                boolean initSuccess = initializeDatabase();
                if (initSuccess) {
                    System.out.println("✓ Base de données initialisée avec succès!");
                } else {
                    System.out.println("✗ Échec de l'initialisation de la base de données.");
                    System.exit(1);
                }
                break;

            case "test":
                System.out.println("=== Test de connexion ===");
                boolean testSuccess = testConnection();
                if (testSuccess) {
                    System.out.println("✓ Connexion réussie!");
                } else {
                    System.out.println("✗ Échec de la connexion.");
                    System.exit(1);
                }
                break;

            default:
                System.out.println("Usage: java main.model.database.DatabaseInitializer [init|test]");
                System.out.println("  init - Initialise la base de données avec le script SQL");
                System.out.println("  test - Teste la connexion à la base de données");
                System.exit(1);
        }

        // Fermer proprement
        DatabaseConnection.getInstance().close();
        System.exit(0);
    }
}
