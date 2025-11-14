package main.model.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion à la base de données MySQL avec pool de connexions HikariCP.
 * Utilise le pattern Singleton pour garantir une seule instance du pool.
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private final HikariDataSource dataSource;
    private final DatabaseConfig config;

    /**
     * Constructeur privé pour le pattern Singleton.
     * Initialise le pool de connexions HikariCP.
     */
    private DatabaseConnection() {
        config = DatabaseConfig.getInstance();
        dataSource = createDataSource();
    }

    /**
     * Récupère l'instance unique de DatabaseConnection (Singleton).
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Crée et configure le DataSource HikariCP.
     */
    private HikariDataSource createDataSource() {
        try {
            HikariConfig hikariConfig = new HikariConfig();

            // Configuration de la connexion
            hikariConfig.setJdbcUrl(config.getUrl());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());

            // Configuration du pool
            hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
            hikariConfig.setMinimumIdle(config.getMinimumIdle());
            hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
            hikariConfig.setIdleTimeout(config.getIdleTimeout());
            hikariConfig.setMaxLifetime(config.getMaxLifetime());

            // Configuration additionnelle pour MySQL
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
            hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
            hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
            hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
            hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
            hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

            // Nom du pool pour identification dans les logs
            hikariConfig.setPoolName("SparadrapHikariPool");

            logger.info("Initialisation du pool de connexions HikariCP...");
            HikariDataSource ds = new HikariDataSource(hikariConfig);
            logger.info("Pool de connexions HikariCP initialisé avec succès.");

            return ds;
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation du pool de connexions: " + e.getMessage(), e);
            throw new RuntimeException("Impossible d'initialiser la connexion à la base de données", e);
        }
    }

    /**
     * Récupère une connexion depuis le pool.
     *
     * @return Une connexion à la base de données
     * @throws SQLException Si une erreur survient lors de la récupération de la connexion
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            logger.debug("Connexion récupérée depuis le pool.");
            return connection;
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération d'une connexion: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Teste la connexion à la base de données.
     *
     * @return true si la connexion est réussie, false sinon
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn.isValid(5);
            if (isValid) {
                logger.info("Test de connexion réussi.");
            } else {
                logger.warn("Test de connexion échoué.");
            }
            return isValid;
        } catch (SQLException e) {
            logger.error("Erreur lors du test de connexion: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Récupère des informations sur l'état du pool de connexions.
     */
    public String getPoolStats() {
        if (dataSource != null) {
            return String.format(
                "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
            );
        }
        return "Pool non initialisé";
    }

    /**
     * Ferme le pool de connexions.
     * À appeler lors de l'arrêt de l'application.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Fermeture du pool de connexions...");
            dataSource.close();
            logger.info("Pool de connexions fermé.");
        }
    }

    /**
     * Hook pour fermer le pool lors de l'arrêt de la JVM.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.close();
            }
        }));
    }
}
