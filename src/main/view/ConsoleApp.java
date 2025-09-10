package main.view;

import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;
import main.model.Medicament.TypeAchat;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
import main.model.service.GestPharmacieService;

import java.util.*;

public class ConsoleApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GestPharmacieService service = new GestPharmacieService();

    // Données de démonstration
    private static Map<String, Medicament> inventaire = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("=== SYSTÈME DE GESTION PHARMACIE SPARADRAP ===");

        // Initialiser des données de test
        initialiserDonnees();

        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            int choix = lireEntier();

            switch (choix) {
                case 1 -> gestionClients();
                case 2 -> gestionMedecins();
                case 3 -> gestionMedicaments();
                case 4 -> gestionVentes();
                case 5 -> afficherStatistiques();
                case 0 -> {
                    continuer = false;
                    System.out.println("Au revoir !");
                }
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MENU PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. Gestion des clients");
        System.out.println("2. Gestion des médecins");
        System.out.println("3. Gestion des médicaments");
        System.out.println("4. Gestion des ventes");
        System.out.println("5. Statistiques");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private static void gestionClients() {
        System.out.println("\n=== GESTION DES CLIENTS ===");
        System.out.println("1. Ajouter un client");
        System.out.println("2. Rechercher un client");
        System.out.println("3. Modifier un client");
        System.out.println("4. Supprimer un client");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        int choix = lireEntier();
        switch (choix) {
            case 1 -> ajouterClient();
            case 2 -> rechercherClient();
            //case 3 -> modifierClient();
            case 4 -> supprimerClient();
        }
    }

    private static void gestionMedicaments() {
        System.out.println("\n=== GESTION DES MÉDICAMENTS ===");
        System.out.println("1. Afficher l'inventaire");
        System.out.println("2. Ajouter un médicament");
        System.out.println("3. Modifier le stock");
        System.out.println("4. Rechercher un médicament");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        int choix = lireEntier();
        switch (choix) {
            case 1 -> afficherInventaire();
            case 2 -> ajouterMedicament();
            case 3 -> modifierStock();
            case 4 -> rechercherMedicament();
        }
    }

    private static void gestionVentes() {
        System.out.println("\n=== GESTION DES VENTES ===");
        System.out.println("1. Nouvelle vente");
        System.out.println("2. Vente sur ordonnance");
        System.out.println("3. Historique des ventes");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        int choix = lireEntier();
        switch (choix) {
            case 1 -> nouvelleVente();
            case 2 -> venteOrdonnance();
            case 3 -> historiqueVentes();
        }
    }

    private static void ajouterClient() {
        System.out.println("\n--- Ajouter un client ---");
        try {
            System.out.print("Nom : ");
            String nom = scanner.nextLine();
            System.out.print("Prénom : ");
            String prenom = scanner.nextLine();
            System.out.print("Numero + rue : ");
            String adresse = scanner.nextLine();
            System.out.print("Code postal : ");
            String codePostal = scanner.nextLine();
            System.out.print("Ville : ");
            String ville = scanner.nextLine();
            System.out.print("Téléphone : ");
            String telephone = scanner.nextLine();
            System.out.print("Email : ");
            String email = scanner.nextLine();
            System.out.print("Identifiant : ");
            String identifiant = scanner.nextLine();
            System.out.print("Numéro sécurité sociale : ");
            String numeroSecu = scanner.nextLine();

            Client client = new Client(nom, prenom, adresse, codePostal, ville,
                    telephone, email, identifiant, numeroSecu, null, null);

            service.ajouterClient(client);
            System.out.println("✅ Client ajouté avec succès !");

        } catch (Exception e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }

    private static void rechercherClient() {
        System.out.print("Identifiant du client : ");
        String identifiant = scanner.nextLine();

        Optional<Client> client = service.rechercherClient(identifiant);
        if (client.isPresent()) {
            System.out.println("✅ Client trouvé :");
            afficherClient(client.get());
        } else {
            System.out.println("❌ Client non trouvé.");
        }
    }

    private static void afficherInventaire() {
        System.out.println("\n=== INVENTAIRE DES MÉDICAMENTS ===");
        System.out.printf("%-20s %-15s %-10s %-10s%n", "NOM", "CATÉGORIE", "PRIX", "STOCK");
        System.out.println("-".repeat(65));

        for (Medicament med : inventaire.values()) {
            System.out.printf("%-20s %-15s %-10.2f€ %-10d%n",
                    med.getNom(),
                    med.getCategorie(),
                    med.getPrix(),
                    med.getQuantiteStock());
        }
    }

    private static void nouvelleVente() {
        System.out.println("\n--- Nouvelle vente ---");

        // Rechercher le client
        System.out.print("Identifiant du client : ");
        String identifiantClient = scanner.nextLine();
        Optional<Client> clientOpt = service.rechercherClient(identifiantClient);

        if (clientOpt.isEmpty()) {
            System.out.println("❌ Client non trouvé.");
            return;
        }

        Client client = clientOpt.get();
        List<Medicament> medicamentsVente = new ArrayList<>();
        Map<Medicament, Integer> quantites = new HashMap<>();

        boolean ajouterMedicaments = true;
        while (ajouterMedicaments) {
            afficherInventaire();
            System.out.print("\nNom du médicament (ou 'fin' pour terminer) : ");
            String nomMed = scanner.nextLine();

            if ("fin".equalsIgnoreCase(nomMed)) {
                ajouterMedicaments = false;
                continue;
            }

            Medicament medicament = inventaire.get(nomMed);
            if (medicament == null) {
                System.out.println("❌ Médicament non trouvé.");
                continue;
            }

            System.out.print("Quantité : ");
            int quantite = lireEntier();

            if (!medicament.isDisponible(quantite)) {
                System.out.println("❌ Stock insuffisant. Stock disponible : " +
                        medicament.getQuantiteStock());
                continue;
            }

            medicamentsVente.add(medicament);
            quantites.put(medicament, quantite);

            // Réduire le stock
            try {
                medicament.reduireStock(quantite);
                System.out.println("✅ " + quantite + "x " + nomMed + " ajouté(s) à la vente.");
            } catch (Exception e) {
                System.out.println("❌ Erreur : " + e.getMessage());
            }
        }

        if (!medicamentsVente.isEmpty()) {
            // Créer l'achat
            Achat achat = new Achat(new Date(), client, null,
                    "ACH" + System.currentTimeMillis(),
                    TypeAchat.DIRECT, medicamentsVente, quantites);

            service.enregistrerAchat(achat);

            // Afficher le récapitulatif
            System.out.println("\n=== RÉCAPITULATIF DE LA VENTE ===");
            System.out.println("Client : " + client.getNom() + " " + client.getPrenom());
            System.out.println("Date : " + new Date());
            System.out.println("\nMédicaments :");
            for (Map.Entry<Medicament, Integer> entry : quantites.entrySet()) {
                Medicament med = entry.getKey();
                int qty = entry.getValue();
                double sousTotal = med.getPrix() * qty;
                System.out.printf("- %s x%d = %.2f€%n", med.getNom(), qty, sousTotal);
            }
            System.out.printf("\nMontant total : %.2f€%n", achat.getMontantTotal());
            if (achat.getMontantRembourse() > 0) {
                System.out.printf("Montant remboursé : %.2f€%n", achat.getMontantRembourse());
                System.out.printf("Reste à payer : %.2f€%n",
                        achat.getMontantTotal() - achat.getMontantRembourse());
            }
            System.out.println("✅ Vente enregistrée avec succès !");
        }
    }

    private static void afficherStatistiques() {
        System.out.println("\n=== STATISTIQUES ===");

        // Période : derniers 30 jours
        Date maintenant = new Date();
        Date il_y_a_30_jours = new Date(maintenant.getTime() - 30L * 24 * 60 * 60 * 1000);

        double chiffreAffaires = service.calculerChiffreAffaires(il_y_a_30_jours, maintenant);
        System.out.printf("Chiffre d'affaires (30 derniers jours) : %.2f€%n", chiffreAffaires);

        // Stock total
        int stockTotal = inventaire.values().stream()
                .mapToInt(Medicament::getQuantiteStock)
                .sum();
        System.out.println("Stock total : " + stockTotal + " unités");

        // Médicaments en rupture
        long ruptureStock = inventaire.values().stream()
                .filter(med -> med.getQuantiteStock() == 0)
                .count();
        System.out.println("Médicaments en rupture : " + ruptureStock);
    }

    // Méthodes utilitaires
    private static void initialiserDonnees() {
        try {
            // Créer des dates valides
            Date maintenant = new Date();
            Date peremption = new Date(maintenant.getTime() + 365L * 24 * 60 * 60 * 1000);

            // Quelques médicaments d'exemple
            Medicament doliprane = new Medicament("Doliprane", CategorieMedicament.ANALGESIQUES,
                    5.99, 100, maintenant, peremption);
            Medicament aspirine = new Medicament("Aspirine", CategorieMedicament.ANALGESIQUES,
                    3.50, 75, maintenant, peremption);
            Medicament amoxicilline = new Medicament("Amoxicilline", CategorieMedicament.ANTIBIOTIQUES,
                    12.99, 50, maintenant, peremption);

            inventaire.put("Doliprane", doliprane);
            inventaire.put("Aspirine", aspirine);
            inventaire.put("Amoxicilline", amoxicilline);

            // Quelques clients
            Client client1 = new Client("Martin", "Pierre", "12 Rue de Paris", "75000", "Paris",
                    "0123456789", "pierre.martin@email.com", "CL001",
                    "123456789012345", null, null);
            service.ajouterClient(client1);

            System.out.println("✅ Données d'exemple initialisées.");

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    private static void afficherClient(Client client) {
        System.out.println("Nom : " + client.getNom() + " " + client.getPrenom());
        System.out.println("Adresse : " + client.getAdresse() + ", " +
                client.getCodePostal() + " " + client.getVille());
        System.out.println("Téléphone : " + client.getNumTelephone());
        System.out.println("Email : " + client.getEmail());
        System.out.println("Numéro SS : " + client.getNumeroSecuriteSocial());
    }

    private static int lireEntier() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Méthodes à implémenter pour un prototype complet
    private static void gestionMedecins() {
        System.out.println("\n=== GESTION DES MEDECINS ===");
        System.out.println("1. Ajouter un médecin");
        System.out.println("2. Rechercher un médecin");
        System.out.println("3. Modifier un médecin");
        System.out.println("4. Supprimer un médecin");
        System.out.println("0. Retour");
        System.out.println("Votre choix : ");
        int choix = lireEntier();
        switch (choix) {
            case 1 -> ajouterMedecin();
            case 2 -> rechercherMedecin();
            case 3 -> modifierMedecin();
            //case 4 -> supprimerMedecin();
        }
    }

    private static void ajouterMedecin() {
        System.out.println("\n--- Ajouter un médecin ---");
        try {
            System.out.print("Nom : ");
            String nom = scanner.nextLine();
            System.out.print("Prénom : ");
            String prenom = scanner.nextLine();
            System.out.print("Adresse : ");
            String adresse = scanner.nextLine();
            System.out.print("CodePostal : ");
            String codePostal = scanner.nextLine();
            System.out.print("Ville : ");
            String ville = scanner.nextLine();
            System.out.print("Téléphone : ");
            String numTelephone = scanner.nextLine();
            System.out.print("Email : ");
            String email = scanner.nextLine();
            System.out.println("Numero RPPS : ");
            String numRPPS = scanner.nextLine();
            System.out.println("Identifiant : ");
            String identifiant = scanner.nextLine();

            System.out.println("✅ Médecin ajouté avec succes !");
        } catch (Exception e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }

    private static void rechercherMedecin() {
        System.out.println("Identifiant du médecin : ");
        String numRPPS = scanner.nextLine();

        Optional<Medecin> medecin = service.rechercherMedecin(numRPPS);
        if (medecin.isPresent()) {
            System.out.println("✅ Médecin trouvé :");
            afficherMedecin(medecin.get());
        } else {
            System.out.println("❌ Médecin non trouvé.");
        }
    }

    private static void afficherMedecin(Medecin medecin) {
        System.out.println("Nom : " + medecin.getNom() + " " + medecin.getPrenom());
        System.out.println("Adresse : " + medecin.getAdresse() + ", " +
                medecin.getCodePostal() + " " + medecin.getVille());
        System.out.println("Téléphone : " + medecin.getNumTelephone());
        System.out.println("Email : " + medecin.getEmail());
        System.out.println("Numéro RPPS : " + medecin.getNumeroRPPS());
        System.out.println("Identifiant : " + medecin.getIdentifiant());
        System.out.println("Patients : " + medecin.getPatients());
    }

    private static void modifierMedecin() {
        System.out.println("Numero RPPS du medecin a modifier :");
        String numRPPS = scanner.nextLine();

        Optional<Medecin> medecinOpt = service.rechercherMedecin(numRPPS);
        if (medecinOpt.isEmpty()) {
            System.out.println("❌ Médecin non trouvé.");
            return;
        }
        Medecin medecin = medecinOpt.get();
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n--- Modifier le medecin : " + medecin.getNom() + " " + medecin.getPrenom());
            System.out.println("1. Nom (" + medecin.getNom() + ")");
            System.out.println("2. Prenom (" + medecin.getPrenom() + ")");
            System.out.println("3. Adresse (" + medecin.getAdresse() + ")");
            System.out.println("4. Téléphone  (" + medecin.getNumTelephone() + ")");
            System.out.println("5. Email (" + medecin.getEmail() + ")");
            System.out.println("6. Numero RPPS (" + medecin.getNumeroRPPS() + ")");
            System.out.println("7. Identifiant (" + medecin.getIdentifiant() + ")");
            System.out.println("0. Terminer la modification");
            System.out.println("Que souhaitez-vous modifier (0-9) : ");

            int choix = lireEntier();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.println("Nouveau nom : ");
                    medecin.setNom(scanner.nextLine());
                    break;
                case 2:
                    System.out.println("Nouveau prenom : ");
                    medecin.setPrenom(scanner.nextLine());
                    break;
                case 3:
                    System.out.println("Nouvelle adresse : ");
                    medecin.setAdresse(scanner.nextLine());
                    System.out.println("Code postal : ");
                    medecin.setCodePostal(scanner.nextLine());
                    System.out.println("Ville : ");
                    medecin.setVille(scanner.nextLine());
                    break;
                case 4:
                    System.out.println("Nouveau numero de telephone : ");
                    medecin.setNumTelephone(scanner.nextLine());
                    break;
                case 5:
                    System.out.println("Nouvel email : ");
                    medecin.setEmail(scanner.nextLine());
                    break;
                case 6:
                    System.out.println("Nouveau numero RPPS : ");
                    medecin.setNumeroRPPS(scanner.nextLine());
                    break;
                case 7:
                    System.out.println("Nouvel identifiant : ");
                    medecin.setIdentifiant(scanner.nextLine());
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide.");
            }
        }
        service.modifierMedecin(medecin);
        System.out.println("✅ Médecin modifié avec succès !");
    }
    private static void supprimerClient() { /* À implémenter */ }
    private static void ajouterMedicament() { /* À implémenter */ }
    private static void modifierStock() { /* À implémenter */ }
    private static void rechercherMedicament() { /* À implémenter */ }
    private static void venteOrdonnance() { /* À implémenter */ }
    private static void historiqueVentes() { /* À implémenter */ }
}
