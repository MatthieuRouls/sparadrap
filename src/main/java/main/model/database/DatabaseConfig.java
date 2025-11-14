package main.model.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe de configuration pour la base de données.
 * Charge les paramètres depuis le fichier database.properties.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String CONFIG_FILE = "database.properties";
    private static DatabaseConfig instance;
    private final Properties properties;

    private DatabaseConfig() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Récupère l'instance unique de DatabaseConfig (Singleton).
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Charge les propriétés depuis le fichier de configuration.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.warn("Fichier {} non trouvé, utilisation des valeurs par défaut", CONFIG_FILE);
                // Utiliser des valeurs par défaut
                setDefaultProperties();
                return;
            }
            properties.load(input);
            logger.info("Configuration de la base de données chargée avec succès depuis {}", CONFIG_FILE);
            logger.debug("URL: {}, Username: {}", getUrl(), getUsername());
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la configuration: {}", e.getMessage(), e);
            logger.warn("Utilisation de la configuration par défaut");
            setDefaultProperties();
        }
    }

    /**
     * Définit des propriétés par défaut si le fichier n'est pas trouvé.
     */
    private void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/sparadrap_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.name", "sparadrap_db");
        properties.setProperty("db.pool.maximumPoolSize", "10");
        properties.setProperty("db.pool.minimumIdle", "5");
        properties.setProperty("db.pool.connectionTimeout", "30000");
        properties.setProperty("db.pool.idleTimeout", "600000");
        properties.setProperty("db.pool.maxLifetime", "1800000");
    }

    /**
     * Récupère l'URL de connexion à la base de données.
     */
    public String getUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Récupère le nom d'utilisateur pour la connexion.
     */
    public String getUsername() {
        return properties.getProperty("db.username");
    }

    /**
     * Récupère le mot de passe pour la connexion.
     */
    public String getPassword() {
        return properties.getProperty("db.password");
    }

    /**
     * Récupère le nom de la base de données.
     */
    public String getDatabaseName() {
        return properties.getProperty("db.name");
    }

    /**
     * Récupère la taille maximale du pool de connexions.
     */
    public int getMaximumPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.maximumPoolSize", "10"));
    }

    /**
     * Récupère le nombre minimum de connexions inactives.
     */
    public int getMinimumIdle() {
        return Integer.parseInt(properties.getProperty("db.pool.minimumIdle", "5"));
    }

    /**
     * Récupère le timeout de connexion en millisecondes.
     */
    public long getConnectionTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "30000"));
    }

    /**
     * Récupère le timeout d'inactivité en millisecondes.
     */
    public long getIdleTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.idleTimeout", "600000"));
    }

    /**
     * Récupère la durée de vie maximale d'une connexion en millisecondes.
     */
    public long getMaxLifetime() {
        return Long.parseLong(properties.getProperty("db.pool.maxLifetime", "1800000"));
    }
}
