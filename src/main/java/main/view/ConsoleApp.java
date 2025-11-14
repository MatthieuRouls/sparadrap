package main.view;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;
import main.model.Medicament.TypeAchat;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
import main.model.service.GestPharmacieService;

import java.text.SimpleDateFormat;
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
        boolean continuer = true;
        while (continuer) {
        System.out.println("\n=== GESTION DES CLIENTS ===");
        System.out.println("1. Ajouter un client");
        System.out.println("2. Rechercher un client");
        System.out.println("3. Modifier un client");
        System.out.println("4. Supprimer un client");
        System.out.println("0. Retour au menu principal");
        System.out.println("Votre choix : ");
        int choix = lireEntier();

        switch (choix) {
            case 1 -> ajouterClient();
            case 2 -> rechercherClient();
            case 3 -> modifierClient();
            case 4 -> supprimerClient();
            case 0 -> continuer = false;
            default -> System.out.println("Choix invalide !");
        }
        }
    }

    private static void gestionMedecins() {
        boolean continuer = true;
        while (continuer) {
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
                case 4 -> supprimerMedecin();
                case 0 -> continuer = false;
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    private static void gestionMedicaments() {
        boolean continuer = true;
        while (continuer) {
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
                case 0 -> continuer = false;
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    private static void gestionVentes() {
        boolean continuer = true;
        while (continuer) {
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
                case 0 -> continuer = false;
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    private static void ajouterClient() {
        boolean continuer = false;
        do {
            continuer = false;
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
                System.out.print("Reessayer ? (Oui/Non) ");
                String choix = scanner.nextLine();
                continuer = choix.equalsIgnoreCase("Oui");
            }
        } while (continuer);
    }

    private static void rechercherClient() {
        boolean continuer = true;
        do {
            continuer = false;
            System.out.print("Identifiant du client : ");
            String identifiant = scanner.nextLine();

            Optional<Client> client = service.rechercherClient(identifiant);
            if (client.isPresent()) {
                System.out.println("✅ Client trouvé :");
                afficherClient(client.get());
            } else {
                System.out.println("❌ Client non trouvé.");
                System.out.print("Reessayer ? (Oui/Non) ");
                String choix = scanner.nextLine();
                continuer = choix.equalsIgnoreCase("Oui");
            }
        } while (continuer);
    }

    private static void afficherClient(Client client) {
        System.out.println("Nom : " + client.getNom() + " " + client.getPrenom());
        System.out.println("Adresse : " + client.getAdresse() + ", " +
                client.getCodePostal() + " " + client.getVille());
        System.out.println("Téléphone : " + client.getNumTelephone());
        System.out.println("Email : " + client.getEmail());
        System.out.println("Numéro SS : " + client.getNumeroSecuriteSocial());
    }

    private static void modifierClient() {
        System.out.println("Identifiant du client a modifier :");
        String identifiant = scanner.nextLine();

        Optional<Client> clientOpt = service.rechercherClient(identifiant);
        if (clientOpt.isEmpty()) {
            System.out.println("❌ Client non trouvé.");
            return;
        }
        Client client = clientOpt.get();
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n--- Modifier le client : " + client.getNom() + " " + client.getPrenom());
            System.out.println("1. Nom (" + client.getNom() + ")");
            System.out.println("2. Prenom (" + client.getPrenom() + ")");
            System.out.println("3. Adresse (" + client.getAdresse() + ")");
            System.out.println("4. Téléphone  (" + client.getNumTelephone() + ")");
            System.out.println("5. Email (" + client.getEmail() + ")");
            System.out.println("6. Identifiant (" + client.getIdentifiant() + ")");
            System.out.println("0. Terminer la modification");
            System.out.println("Que souhaitez-vous modifier (0-6) : ");

            int choix = lireEntier();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.println("Nouveau nom : ");
                    client.setNom(scanner.nextLine());
                    break;
                case 2:
                    System.out.println("Nouveau prenom : ");
                    client.setPrenom(scanner.nextLine());
                    break;
                case 3:
                    System.out.println("Nouvelle adresse : ");
                    client.setAdresse(scanner.nextLine());
                    System.out.println("Code postal : ");
                    client.setCodePostal(scanner.nextLine());
                    System.out.println("Ville : ");
                    client.setVille(scanner.nextLine());
                    break;
                case 4:
                    System.out.println("Nouveau numero de telephone : ");
                    client.setNumTelephone(scanner.nextLine());
                    break;
                case 5:
                    System.out.println("Nouvel email : ");
                    client.setEmail(scanner.nextLine());
                    break;
                case 6:
                    System.out.println("Nouvel identifiant : ");
                    client.setIdentifiant(scanner.nextLine());
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide.");
            }
        }
        service.modifierClient(client);
        System.out.println("✅ Médecin modifié avec succès !");
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

            Medicament medicament = null;
            for (String nomInventaire : inventaire.keySet()) {
            if (nomInventaire.equalsIgnoreCase(nomMed)) {
                medicament = inventaire.get(nomInventaire);
                break;
            }
            }
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
            Medicament doliprane = new Medicament("DOLIPRANE", CategorieMedicament.ANALGESIQUES,
                    5.99, 100, maintenant, peremption);
            Medicament aspirine = new Medicament("ASPIRINE", CategorieMedicament.ANALGESIQUES,
                    3.50, 75, maintenant, peremption);
            Medicament amoxicilline = new Medicament("AMOXICILLINE", CategorieMedicament.ANTIBIOTIQUES,
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


    private static int lireEntier() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
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

            Medecin medecin = new Medecin(nom, prenom, adresse, codePostal, ville,
                                    numTelephone, email, numRPPS, identifiant);

            service.ajouterMedecin(medecin);
            System.out.println("Medecin ajoute avec succes !");

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

    private static void supprimerMedecin() {
        System.out.println("Identifiant du Medecin a supprimer : ");
        String numRPPS = scanner.nextLine();
        Optional<Medecin> medecinOpt = service.rechercherMedecin(numRPPS);
        if (medecinOpt.isEmpty()) {
            System.out.println("❌ Medecin non trouve.");
            return;
        }
        System.out.println("Confirmer la suppression (Oui/Non) ? ");
        String confirmation = scanner.nextLine();
        if ("Oui".equalsIgnoreCase(confirmation)) {
            service.supprimerMedecin(numRPPS);
            System.out.println("✅ Medecin supprimé avec succès !");
        }
    }

    private static void supprimerClient() {
        System.out.println("Identifiant du client a supprimer : ");
        String identifiant = scanner.nextLine();
        Optional<Client> clientOpt = service.rechercherClient(identifiant);
        if (clientOpt.isEmpty()) {
            System.out.println("❌ Client non trouve.");
            return;
        }
        System.out.println("Confirmer la suppression (Oui/Non) ? ");
        String confirmation = scanner.nextLine();
        if ("Oui".equalsIgnoreCase(confirmation)) {
            service.supprimerClient(identifiant);
            System.out.println("✅ Client supprimé avec succès !");
        }
    }

    private static void ajouterMedicament() {
        System.out.println("\n--- Ajouter un medicament ---");
        try {
            System.out.print("Nom : ");
            String nom = scanner.nextLine().toUpperCase(Locale.ROOT);
            System.out.print("Categorie (ANALGESIQUES/ANTIBIOTIQUES/...)");
            CategorieMedicament categorie = CategorieMedicament.valueOf(scanner.nextLine().toUpperCase(Locale.ROOT));
            System.out.print("Prix : ");
            double prix = Double.parseDouble(scanner.nextLine());
            System.out.print("Quantite en stock : ");
            int quantite = lireEntier();
            System.out.print("Date de peremption (format : mm/jj/aaaa) : ");
            Date peremption = new SimpleDateFormat("MM/dd/yyyy").parse(scanner.nextLine());
            Medicament medicament = new Medicament(nom, categorie, prix, quantite, new Date(), peremption);
            inventaire.put(nom, medicament);
            System.out.println("✅ Médicament ajouté avec succès !");
        } catch (Exception e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }

    private static void modifierStock() {
        afficherInventaire();
        System.out.println("\nNom du medicament : ");
        String nom = scanner.nextLine();
        Medicament medicament = inventaire.get(nom);
        if (medicament == null) {
            System.out.println("❌ Médicament non trouvé.");
            return;
        }
        System.out.println("Nouvelle quanitte en stock : ");
        int nouvelleQuantite = lireEntier();
        medicament.setQuantiteStock(nouvelleQuantite);
        System.out.println("✅ Stock mis à jour avec succès !");
    }

    private static void rechercherMedicament() {
        System.out.println("Nom du medicament : ");
        String nom = scanner.nextLine().toUpperCase(Locale.ROOT);
        Medicament medicament = inventaire.get(nom);
        if (medicament != null) {
            System.out.println("Medicament trouve : ");
            System.out.printf("%-20s %-15s %-10.2f€ %-10d%n",
                    medicament.getNom(),
                    medicament.getCategorie(),
                    medicament.getPrix(),
                    medicament.getQuantiteStock());
        } else {
            System.out.println("❌ Médicament non trouvé.");
        }
    }

    private static String genererReferenceOrdonnance() {
        return "ORD" + System.currentTimeMillis();
    }

    private static String genererReferencAchat() {
        return "ACH" + System.currentTimeMillis();
    }

    private static void venteOrdonnance() {
        System.out.println("\n--- Vente sur ordonnance ---");
        System.out.println("Identifiant du client : ");
        String identifiantClient = scanner.nextLine();
        Optional<Client> clientOpt = service.rechercherClient(identifiantClient);

        if (clientOpt.isEmpty()) {
            System.out.println("❌ Client non trouvé.");
            return;
        }
        Client client = clientOpt.get();

        System.out.println("Identifiant du medecin prescripteur : ");
        String identifiantMedecin = scanner.nextLine();

        Optional<Medecin> medecinOpt = service.rechercherMedecin(identifiantMedecin);

        if (medecinOpt.isEmpty()) {
            System.out.println("❌ Médecin non trouvé.");
            return;
        }
        Medecin medecin = medecinOpt.get();

        List<Medicament> medicamentsVente = new ArrayList<>();
        Map<Medicament, Integer> quantites = new HashMap<>();
        List<String> medicamentsNonDisponibles = new ArrayList<>();
        List<String> medicamentsPerimes = new ArrayList<>();

        boolean ajouterMedicaments = true;
        while (ajouterMedicaments) {
            afficherInventaire();
            System.out.print("\nNom du medicament (ou 'fin' pour terminer) : ");
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

            Date now = new Date();
            if (now.after(medicament.getDatePeremption())) {
                medicamentsPerimes.add(nomMed + " (périmé le " + medicament.getDatePeremption() + ")");
                System.out.println("⚠️ Attention : " + nomMed + " est périmé !");
                System.out.print("Continuer quand meme ? (Oui/Non) : ");
                String continuer = scanner.nextLine();
                if ("Oui".equalsIgnoreCase(continuer)) {
                    continue;
                }
            }

            System.out.println("Quantite prescrite : ");
            int quantitePrescrite = lireEntier();

            if (quantitePrescrite <= 0) {
                System.out.println("❌ Quantité invalide.");
                continue;
            }

            if (!medicament.isDisponible(quantitePrescrite)) {
                medicamentsNonDisponibles.add(nomMed + " (demande: " + quantitePrescrite +
                        ", disponible: " + medicament.getQuantiteStock() + ")");
                System.out.println("⚠️ Stock insuffisant pour " + nomMed);
                System.out.print("Stock disponible : " + medicament.getQuantiteStock());
                System.out.print("Quantite a delivrer (0 pour ignorer) : ");
                int quantiteDelivree = lireEntier();

                if (quantiteDelivree <= 0) {
                    continue;
                }
                if (quantiteDelivree > medicament.getQuantiteStock()) {
                    System.out.println("❌ Quantité supérieure au stock disponible.");
                    continue;
                }
                quantitePrescrite = quantiteDelivree;
            }

            medicamentsVente.add(medicament);
            quantites.put(medicament, quantitePrescrite);

            try {
                medicament.reduireStock(quantitePrescrite);
                System.out.println("✅ " + quantitePrescrite + "x " + nomMed + " ajouté(s) à la vente.");
            } catch (Exception e) {
                System.out.println("❌ Erreur lors de la réduction du stock : " + e.getMessage());
                medicamentsVente.remove(medicament);
                quantites.remove(medicament);
            }
        }

        if (medicamentsVente.isEmpty()) {
            System.out.println("❌ Aucun médicament sélectionné. Vente annulée.");
            return;
        }

        String referenceOrdonnance = genererReferenceOrdonnance();
        Ordonnance ordonnance = new Ordonnance(new Date(), medecin, client,
                new ArrayList<>(medicamentsVente),
                new HashMap<>(), referenceOrdonnance);

        service.enregistrerOrdonnance(ordonnance);

        String referenceAchat = genererReferencAchat();
        Achat achat = new Achat(new Date(), client, null, referenceAchat,
                TypeAchat.ORDONNANCE, medicamentsVente, quantites);

        service.enregistrerAchat(achat);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("           RÉCAPITULATIF VENTE SUR ORDONNANCE");
        System.out.println("=".repeat(60));

        System.out.println("📋 INFORMATIONS GÉNÉRALES");
        System.out.println("   Client : " + client.getNom() + " " + client.getPrenom());
        System.out.println("   Médecin prescripteur : " + medecin.getNom() + " " + medecin.getPrenom());
        System.out.println("   Date : " + new Date());
        System.out.println("   Référence ordonnance : " + referenceOrdonnance);
        System.out.println("   Référence achat : " + referenceAchat);

        System.out.println("\n💊 MÉDICAMENTS DÉLIVRÉS");
        for (Map.Entry<Medicament, Integer> entry : quantites.entrySet()) {
            Medicament med = entry.getKey();
            int quantite = entry.getValue();
            double sousTotal = med.getPrix() * quantite;
            System.out.printf("   - %-20s x%-3d = %8.2f€ (expire le %s)%n",
                    med.getNom(), quantite, sousTotal, med.getDatePeremption());
        }

        if (!medicamentsNonDisponibles.isEmpty()) {
            System.out.println("\n⚠️  MÉDICAMENTS NON DISPONIBLES EN QUANTITÉ SUFFISANTE");
            medicamentsNonDisponibles.forEach(msg -> System.out.println("   - " + msg));
        }

        if (!medicamentsPerimes.isEmpty()) {
            System.out.println("\n⚠️  MÉDICAMENTS PÉRIMÉS DÉLIVRÉS");
            medicamentsPerimes.forEach((msg -> System.out.println("   - " + msg)));
        }

        System.out.println("\n CALCULS FINANCIERS");
        System.out.printf("  Montant total: %10.2f€%n", achat.getMontantTotal());

        if (client.getMutuelle() != null) {
            System.out.printf("   Taux remboursement mutuelle : %6.1f%%%n", client.getMutuelle().getTauxRemboursement());
            System.out.printf("   Montant remboursé mutuelle : %8.2f€%n", achat.getMontantRembourse());
            double resteAPayer = achat.getMontantTotal() - achat.getMontantRembourse();
            System.out.printf("   Reste à payer : %13.2f€%n", resteAPayer);
        } else {
            System.out.println("   Pas de mutuelle - Montant intégral à payer");
        }


        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Vente sur ordonnance enregistrée avec succès !");
    }


    private static void historiqueVentes() {
        System.out.println("\n=== HISTORIQUE DES VENTES ===");
        System.out.println("1. Toutes les ventes");
        System.out.println("2. Ventes par période");
        System.out.println("3. Ventes par client");
        System.out.println("4. Historique des ordonnances");
        System.out.println("5. Rechercher une ordonnance");
        System.out.println("0. Retour");
        System.out.print("Votre choix : ");

        int choix = lireEntier();
        scanner.nextLine();

        switch (choix) {
            case 1 -> afficherToutesLesVentes();
            case 2 -> afficherVentesParPeriode();
            case 3 -> afficherVentesParClient();
            case 4 -> afficherHistoriqueOrdonnances();
            case 5 -> rechercherOrdonnance();
        }
    }

    private static void afficherToutesLesVentes() {
        System.out.println("\n=== TOUTES LES VENTES ===");

        Date debutPeriode = new Date(0);
        Date finPeriode = new Date();

        List<Achat> achats = service.getAchatsParPeriode(debutPeriode, finPeriode);

        if (achats.isEmpty()) {
            System.out.println("Aucune vente enregistree.");
            return;
        }

        System.out.printf("%-15s %-20s %-12s %-10s %-10s %-10s%n",
                "RÉFÉRENCE", "CLIENT", "TYPE", "MONTANT", "REMBOURSÉ", "NET");
        System.out.println("-".repeat(90));

        double totalVentes = 0;
        double totalRembourse = 0;

        for (Achat achat : achats) {
            double net = achat.getMontantTotal() - achat.getMontantRembourse();
            System.out.printf("%-15s %-20s %-12s %9.2f€ %9.2f€ %9.2f€%n",
                    achat.getReference(),
                    (achat.getClient().getNom() + " " + achat.getClient().getPrenom()).substring(0, Math.min(20, achat.getClient().getNom().length() + achat.getClient().getPrenom().length() + 1)),
                    achat.getType(),
                    achat.getMontantTotal(),
                    achat.getMontantRembourse(),
                    net);

            totalVentes += achat.getMontantTotal();
            totalRembourse += achat.getMontantRembourse();
        }

        System.out.println("-".repeat(90));
        System.out.printf("TOTAL (%d ventes) : %32.2f€ %9.2f€ %9.2f€%n",
                achats.size(), totalVentes, totalRembourse, (totalVentes - totalRembourse));
    }

    private static void afficherVentesParPeriode() {
        System.out.println("\n=== VENTES PAR PÉRIODE ===");
        System.out.println("1. Aujourd'hui");
        System.out.println("2. Cette semaine");
        System.out.println("3. Ce mois");
        System.out.println("4. Période personnalisée");
        System.out.print("Votre choix : ");

        int choix = lireEntier();
        Date now = new Date();
        Date debut;
        String libellePeriode;

        switch (choix) {
            case 1:
                // Aujourd'hui (minuit à maintenant)
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                debut = cal.getTime();
                libellePeriode = "Aujourd'hui";
                break;
            case 2:
                // Cette semaine (lundi à maintenant)
                Calendar calSemaine = Calendar.getInstance();
                calSemaine.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                calSemaine.set(Calendar.HOUR_OF_DAY, 0);
                calSemaine.set(Calendar.MINUTE, 0);
                calSemaine.set(Calendar.SECOND, 0);
                calSemaine.set(Calendar.MILLISECOND, 0);
                debut = calSemaine.getTime();
                libellePeriode = "Cette semaine";
                break;
            case 3:
                // Ce mois (1er du mois à maintenant)
                Calendar calMois = Calendar.getInstance();
                calMois.set(Calendar.DAY_OF_MONTH, 1);
                calMois.set(Calendar.HOUR_OF_DAY, 0);
                calMois.set(Calendar.MINUTE, 0);
                calMois.set(Calendar.SECOND, 0);
                calMois.set(Calendar.MILLISECOND, 0);
                debut = calMois.getTime();
                libellePeriode = "Ce mois";
                break;
            case 4:
                System.out.println("Période personnalisée non implémentée pour ce prototype.");
                return;
            default:
                System.out.println("Choix invalide.");
                return;
        }

        List<Achat> achats = service.getAchatsParPeriode(debut, now);

        System.out.println("\n=== VENTES - " + libellePeriode.toUpperCase() + " ===");
        if (achats.isEmpty()) {
            System.out.println("Aucune vente enregistre pour cette periode.");
            return;
        }

        System.out.printf("%-15s %-20s %-12s %-10s %-10s %-10s%n",
                "RÉFÉRENCE", "CLIENT", "TYPE", "MONTANT", "REMBOURSÉ", "NET");
        System.out.println("-".repeat(90));

        double totalPeriode = 0;
        double totalRemboursePeriode = 0;

        for (Achat achat : achats) {
            double net = achat.getMontantTotal() - achat.getMontantRembourse();
            String nomComplet = achat.getClient().getNom() + " " + achat.getClient().getPrenom();
            System.out.printf("%-15s %-20s %-12s %9.2f€ %9.2f€ %9.2f€%n",
                    achat.getReference(),
                    nomComplet.length() > 20 ? nomComplet.substring(0, 20) : nomComplet,
                    achat.getType(),
                    achat.getMontantTotal(),
                    achat.getMontantRembourse(),
                    net);

            totalPeriode += achat.getMontantTotal();
            totalRemboursePeriode += achat.getMontantRembourse();
        }

        System.out.println("-".repeat(90));
        System.out.printf("TOTAL %s : %25.2f€ %9.2f€ %9.2f€%n",
                libellePeriode, totalPeriode, totalRemboursePeriode,
                (totalPeriode - totalRemboursePeriode));
    }

    private static void afficherVentesParClient() {
        System.out.print("Identifiant du client : ");
        String identifiant = scanner.nextLine();

        Optional<Client> clientOpt = service.rechercherClient(identifiant);
        if (clientOpt.isEmpty()) {
            System.out.println("Client non trouve.");
            return;
        }

        Client client = clientOpt.get();
        List<Achat> achats = service.getAchatsParClient(client);

        if (achats.isEmpty()) {
            System.out.println("Aucun achat trouve pour ce client.");
            return;
        }

        System.out.println("\n=== ACHATS DE " + client.getNom().toUpperCase() + " " +
                client.getPrenom().toUpperCase() + " ===");

        System.out.printf("%-15s %-12s %-15s %-10s %-10s %-10s%n",
                "RÉFÉRENCE", "DATE", "TYPE", "MONTANT", "REMBOURSÉ", "PAYÉ");
        System.out.println("-".repeat(85));

        double totalClient = 0;
        double totalRembourseClient = 0;

        for (Achat achat : achats) {
            double paye = achat.getMontantTotal() - achat.getMontantRembourse();
            System.out.printf("%-15s %-12s %-15s %9.2f€ %9.2f€ %9.2f€%n",
                    achat.getReference(),
                    achat.getDateTransaction().toString().substring(0, 10),
                    achat.getType(),
                    achat.getMontantTotal(),
                    achat.getMontantRembourse(),
                    paye);

            totalClient += achat.getMontantTotal();
            totalRembourseClient += achat.getMontantRembourse();
        }

        System.out.println("-".repeat(85));
        System.out.printf("TOTAL CLIENT : %28.2f€ %9.2f€ %9.2f€%n",
                totalClient, totalRembourseClient, (totalClient - totalRembourseClient));

        if (client.getMutuelle() != null) {
            System.out.println("\n📋 Mutuelle : " + client.getMutuelle().getNom() +
                    " (Taux : " + client.getMutuelle().getTauxRemboursement() + "%)");;
        } else {
            System.out.println("\n📋 Aucune mutuelle enregistrée");
        }
    }

    private static void afficherHistoriqueOrdonnances() {
        System.out.println("\n=== HISTORIQUE DES ORDONNANCES ===");

        // Pour le prototype, on récupère toutes les ordonnances
        // Note: Il faudrait ajouter une méthode getToutesLesOrdonnances() dans le service
        System.out.println("Cette fonctionnalité nécessite l'ajout d'une méthode dans GestPharmacieService.");
        System.out.println("Vous pouvez rechercher une ordonnance spécifique avec l'option 5.");
    }

    private static void rechercherOrdonnance() {
        System.out.print("Reference de l'ordonnance : ");
        String reference = scanner.nextLine();

        // Pour le prototype, on simule la recherche
        // Il faudrait ajouter une méthode rechercherOrdonnance(String reference) dans le service
        System.out.println("Recherche d'ordonnance par référence non implémentée dans ce prototype.");
        System.out.println("Référence recherchée : " + reference);
    }
}


