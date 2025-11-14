package main.model.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Random;

/**
 * Classe de démonstration pour le système de logging.
 * Montre les différents niveaux de log et les bonnes pratiques.
 */
public class LoggerDemo {

    // Déclaration du logger (statique, privé, final)
    private static final Logger logger = LoggerFactory.getLogger(LoggerDemo.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("  DÉMONSTRATION DU SYSTÈME DE LOGGING");
        logger.info("========================================");

        // 1. Démonstration des différents niveaux
        demonstrationNiveaux();

        // 2. Démonstration avec paramètres
        demonstrationParametres();

        // 3. Démonstration avec exceptions
        demonstrationExceptions();

        // 4. Démonstration cas d'usage métier
        demonstrationCasMetier();

        logger.info("========================================");
        logger.info("  FIN DE LA DÉMONSTRATION");
        logger.info("========================================");
        logger.info("Consultez les fichiers de log dans le dossier 'logs/'");
    }

    /**
     * Démontre les 5 niveaux de log : TRACE, DEBUG, INFO, WARN, ERROR
     */
    private static void demonstrationNiveaux() {
        logger.info("\n--- 1. NIVEAUX DE LOG ---");

        // TRACE : Informations très détaillées (généralement désactivé en production)
        logger.trace("TRACE: Entrée dans la méthode demonstrationNiveaux()");

        // DEBUG : Informations de débogage
        logger.debug("DEBUG: Variable de débogage, état interne de l'application");

        // INFO : Informations générales importantes
        logger.info("INFO: L'application fonctionne normalement");

        // WARN : Avertissements (pas bloquant mais mérite attention)
        logger.warn("WARN: Fichier de configuration non trouvé, utilisation des valeurs par défaut");

        // ERROR : Erreurs critiques
        logger.error("ERROR: Erreur critique détectée dans le système");
    }

    /**
     * Démontre l'utilisation de paramètres dans les logs (bonnes pratiques)
     */
    private static void demonstrationParametres() {
        logger.info("\n--- 2. LOGS AVEC PARAMÈTRES ---");

        String nomClient = "Durand";
        int age = 35;
        double montant = 125.50;

        // ✅ BONNE PRATIQUE : Utiliser {} pour les paramètres
        logger.info("Client: {}, Age: {}, Montant: {}", nomClient, age, montant);

        // Multiple paramètres
        logger.debug("Connexion établie - Host: {}, Port: {}, User: {}",
                    "localhost", 3306, "root");

        // ❌ MAUVAISE PRATIQUE : Concaténation (coûteuse)
        // logger.info("Client: " + nomClient + ", Age: " + age);
    }

    /**
     * Démontre le logging des exceptions avec stack trace
     */
    private static void demonstrationExceptions() {
        logger.info("\n--- 3. LOGS D'EXCEPTIONS ---");

        try {
            // Simulation d'une erreur
            simulerErreurSQL();
        } catch (SQLException e) {
            // ✅ Inclure l'exception pour avoir la stack trace complète
            logger.error("Erreur SQL lors de la requête: {}", e.getMessage(), e);
        }

        try {
            // Simulation d'une division par zéro
            int resultat = 10 / 0;
        } catch (ArithmeticException e) {
            logger.error("Erreur arithmétique: {}", e.getMessage(), e);
        }

        try {
            // Simulation d'un accès null
            String texte = null;
            texte.length();
        } catch (NullPointerException e) {
            logger.error("NullPointerException détectée", e);
        }
    }

    /**
     * Démontre des cas d'usage métier réels pour Sparadrap
     */
    private static void demonstrationCasMetier() {
        logger.info("\n--- 4. CAS D'USAGE MÉTIER ---");

        // Cas 1: Connexion base de données
        logger.info("Tentative de connexion à la base de données MySQL...");
        logger.debug("URL: jdbc:mysql://localhost:3306/sparadrap_db");
        logger.info("Connexion établie avec succès");
        logger.debug("Pool de connexions initialisé - Taille max: 10");

        // Cas 2: Transaction de vente
        String clientId = "cdurand01";
        String medicament = "Doliprane 500mg";
        int quantite = 2;
        double prixTotal = 4.20;

        logger.info("Nouvelle transaction: Client={}, Médicament={}, Quantité={}",
                   clientId, medicament, quantite);
        logger.debug("Calcul du prix: {} x {} = {}", quantite, prixTotal/quantite, prixTotal);
        logger.info("Transaction validée - Montant total: {}€", prixTotal);

        // Cas 3: Stock faible (warning)
        int stockActuel = 5;
        int seuilAlerte = 10;
        if (stockActuel < seuilAlerte) {
            logger.warn("Stock faible pour '{}' : {} unités restantes (seuil: {})",
                       medicament, stockActuel, seuilAlerte);
        }

        // Cas 4: Validation ordonnance
        String numeroRPPS = "10101010101";
        logger.info("Validation ordonnance - Médecin RPPS: {}", numeroRPPS);

        boolean ordonnanceValide = new Random().nextBoolean();
        if (ordonnanceValide) {
            logger.info("Ordonnance validée avec succès");
        } else {
            logger.error("Ordonnance invalide - Vérification nécessaire");
        }

        // Cas 5: Performance monitoring
        long startTime = System.currentTimeMillis();
        simulerTraitement();
        long endTime = System.currentTimeMillis();
        long duree = endTime - startTime;

        logger.debug("Traitement terminé en {} ms", duree);
        if (duree > 1000) {
            logger.warn("Traitement lent détecté: {} ms", duree);
        }

        // Cas 6: Sécurité
        String ipAddress = "192.168.1.100";
        String action = "suppression_client";
        logger.warn("Action sensible détectée: {} depuis IP: {}", action, ipAddress);
    }

    /**
     * Simule une erreur SQL pour la démo
     */
    private static void simulerErreurSQL() throws SQLException {
        throw new SQLException("Table 'clients' introuvable", "42S02", 1146);
    }

    /**
     * Simule un traitement long
     */
    private static void simulerTraitement() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
