package main.model.demo;

import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.service.GestPharmacieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Démonstration de la persistance des données en base MySQL.
 * Montre comment créer, modifier, rechercher et supprimer des entités.
 */
public class PersistenceDemo {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceDemo.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("DÉMONSTRATION DE LA PERSISTANCE MySQL");
        logger.info("========================================");

        GestPharmacieService service = new GestPharmacieService();

        // 1. Démonstration des Mutuelles
        demonstrationMutuelles(service);

        // 2. Démonstration des Médecins
        demonstrationMedecins(service);

        // 3. Démonstration des Clients
        demonstrationClients(service);

        logger.info("========================================");
        logger.info("FIN DE LA DÉMONSTRATION");
        logger.info("========================================");
        logger.info("Vérifiez la base de données MySQL pour voir les données persistées !");
    }

    /**
     * Démonstration des opérations sur les Mutuelles
     */
    private static void demonstrationMutuelles(GestPharmacieService service) {
        logger.info("\n--- 1. DÉMONSTRATION MUTUELLES ---");

        // Créer une mutuelle
        logger.info("Création d'une nouvelle mutuelle...");
        Mutuelle mutuelle1 = new Mutuelle(
            "Mutuelle Test",
            "10 Rue de la Santé",
            "75013",
            "Paris",
            "0140000000",
            "contact@mutuelle-test.fr",
            75.0
        );

        try {
            service.ajouterMutuelle(mutuelle1);
            logger.info("✓ Mutuelle créée avec succès !");
        } catch (Exception e) {
            logger.error("✗ Erreur lors de la création: {}", e.getMessage());
        }

        // Lire toutes les mutuelles
        logger.info("\nLecture de toutes les mutuelles...");
        Collection<Mutuelle> mutuelles = service.getToutesMutuelles();
        logger.info("Nombre de mutuelles en base: {}", mutuelles.size());
        mutuelles.forEach(m -> logger.info("  - {} (Taux: {}%)", m.getNom(), m.getTauxRemboursement()));

        // Rechercher une mutuelle
        logger.info("\nRecherche de la mutuelle 'Mutuelle Test'...");
        service.rechercherMutuelle("Mutuelle Test").ifPresentOrElse(
            m -> logger.info("✓ Mutuelle trouvée: {} - {}", m.getNom(), m.getVille()),
            () -> logger.warn("✗ Mutuelle non trouvée")
        );

        // Modifier une mutuelle
        logger.info("\nModification de la mutuelle (nouveau taux: 80%)...");
        mutuelle1.setTauxRemboursement(80.0);
        try {
            service.modifierMutuelle(mutuelle1);
            logger.info("✓ Mutuelle modifiée avec succès !");
        } catch (Exception e) {
            logger.error("✗ Erreur lors de la modification: {}", e.getMessage());
        }
    }

    /**
     * Démonstration des opérations sur les Médecins
     */
    private static void demonstrationMedecins(GestPharmacieService service) {
        logger.info("\n--- 2. DÉMONSTRATION MÉDECINS ---");

        // Créer un médecin
        logger.info("Création d'un nouveau médecin...");
        Medecin medecin1 = new Medecin(
            "Dupont",
            "Marie",
            "20 Avenue de la République",
            "69002",
            "Lyon",
            "0478123456",
            "marie.dupont@cabinet.fr",
            "mdupont01",
            "12345678901"
        );

        try {
            service.ajouterMedecin(medecin1);
            logger.info("✓ Médecin créé avec succès !");
        } catch (Exception e) {
            logger.error("✗ Erreur lors de la création: {}", e.getMessage());
        }

        // Lire tous les médecins
        logger.info("\nLecture de tous les médecins...");
        Collection<Medecin> medecins = service.getTousMedecins();
        logger.info("Nombre de médecins en base: {}", medecins.size());
        medecins.forEach(m -> logger.info("  - Dr {} {} (RPPS: {})",
            m.getPrenom(), m.getNom(), m.getNumeroRPPS()));

        // Rechercher un médecin
        logger.info("\nRecherche du médecin avec RPPS: 12345678901...");
        service.rechercherMedecin("12345678901").ifPresentOrElse(
            m -> logger.info("✓ Médecin trouvé: Dr {} {}", m.getPrenom(), m.getNom()),
            () -> logger.warn("✗ Médecin non trouvé")
        );

        // Modifier un médecin
        logger.info("\nModification du médecin (nouveau téléphone)...");
        medecin1.setNumTelephone("0478999999");
        try {
            service.modifierMedecin(medecin1);
            logger.info("✓ Médecin modifié avec succès !");
        } catch (Exception e) {
            logger.error("✗ Erreur lors de la modification: {}", e.getMessage());
        }
    }

    /**
     * Démonstration des opérations sur les Clients
     */
    private static void demonstrationClients(GestPharmacieService service) {
        logger.info("\n--- 3. DÉMONSTRATION CLIENTS ---");

        // Récupérer une mutuelle pour l'associer au client
        Mutuelle mutuelle = service.rechercherMutuelle("Mutuelle Test").orElse(null);

        // Créer un client
        logger.info("Création d'un nouveau client...");
        Client client1 = new Client(
            "Martin",
            "Jean",
            "5 Rue des Fleurs",
            "75012",
            "Paris",
            "0601020304",
            "jean.martin@example.com",
            "JEMAR01",
            "123456789012345",
            mutuelle,
            null // pas de médecin traitant pour l'instant
        );

        try {
            service.ajouterClient(client1);
            logger.info("✓ Client créé avec succès !");
        } catch (Exception e) {
            logger.error("✗ Erreur lors de la création: {}", e.getMessage());
        }

        // Lire tous les clients
        logger.info("\nLecture de tous les clients...");
        Collection<Client> clients = service.getTousClients();
        logger.info("Nombre de clients en base: {}", clients.size());
        clients.forEach(c -> {
            String mutuelleName = c.getMutuelle() != null ? c.getMutuelle().getNom() : "Aucune";
            logger.info("  - {} {} (ID: {}, Mutuelle: {})",
                c.getPrenom(), c.getNom(), c.getIdentifiant(), mutuelleName);
        });

        // Rechercher un client
        logger.info("\nRecherche du client avec identifiant: JEMAR01...");
        service.rechercherClient("JEMAR01").ifPresentOrElse(
            c -> logger.info("✓ Client trouvé: {} {} - {}", c.getPrenom(), c.getNom(), c.getVille()),
            () -> logger.warn("✗ Client non trouvé")
        );

        // Modifier un client
        logger.info("\nModification du client (nouveau numéro de téléphone)...");
        client1.setNumTelephone("0609876543");
        try {
            service.modifierClient(client1);
            logger.info("✓ Client modifié avec succès !");
        } catch (Exception e) {
            logger.error("✗ Erreur lors de la modification: {}", e.getMessage());
        }

        // Générer un identifiant unique
        logger.info("\nGénération d'un identifiant unique pour 'Sophie Durand'...");
        String newId = service.generateClientIdentifiant("Sophie", "Durand");
        logger.info("Identifiant généré: {}", newId);

        // Statistiques finales
        logger.info("\n--- STATISTIQUES FINALES ---");
        logger.info("Total clients: {}", service.getNombreClients());
        logger.info("Total médecins: {}", service.getTousMedecins().size());
        logger.info("Total mutuelles: {}", service.getToutesMutuelles().size());
    }
}
