package main.controller;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.Medicament;
import main.model.Medicament.TypeAchat;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Personne.CategoriePersonne.Pharmacien;
import main.model.Transaction.TypeTransaction.Achat;
import main.model.service.GestPharmacieService;
import main.model.security.SecurityValidator;

import java.util.*;

/**
 * Contrôleur principal de l'application Pharmacie Sparadrap
 * Gère la logique métier et fait le lien entre la vue et le service
 */
public class PharmacieController {
    private final GestPharmacieService service;
    private final Map<String, Medicament> inventaire;
    private List<Client> clients;


    private Pharmacien pharmacienConnecte;

    public PharmacieController() {
        this.service = new GestPharmacieService();
        this.inventaire = new HashMap<>();
        this.clients = new ArrayList<>();
        initialiserDonneesDemo();
    }

    // =================== GESTION DES CLIENTS ===================
    public int getNombreClients() {
        return clients != null ? clients.size() : 0;
    }

    public String ajouterClient(String nom, String prenom, String adresse, String codePostal,
                                String ville, String telephone, String email, String identifiant,
                                String numeroSecu) {
        try {
            Client client = new Client(nom, prenom, adresse, codePostal, ville,
                    telephone, email, identifiant, numeroSecu, null, null);
            service.ajouterClient(client);
            clients.add(client);
            return "Client ajouté avec succès";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public Optional<Client> rechercherClient(String identifiant) {
        try {
            return service.rechercherClient(identifiant);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String modifierClient(Client client) {
        try {
            service.modifierClient(client);
            return "Client modifié avec succès";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public String supprimerClient(String identifiant) {
        try {
            boolean supprime = service.supprimerClient(identifiant);
            return supprime ? "Client supprimé avec succès" : "Client non trouvé";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    // =================== GESTION DES MEDECINS ===================

    public String ajouterMedecin(String nom, String prenom, String adresse, String codePostal,
                                 String ville, String telephone, String email, String identifiant,
                                 String numeroRPPS) {
        try {
            Medecin medecin = new Medecin(nom, prenom, adresse, codePostal, ville,
                    telephone, email, identifiant, numeroRPPS);
            service.ajouterMedecin(medecin);
            return "Médecin ajouté avec succès";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public Optional<Medecin> rechercherMedecin(String identifiant) {
        try {
            return service.rechercherMedecin(identifiant);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String modifierMedecin(Medecin medecin) {
        try {
            service.modifierMedecin(medecin);
            return "Médecin modifié avec succès";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public String supprimerMedecin(String identifiant) {
        try {
            boolean supprime = service.supprimerMedecin(identifiant);
            return supprime ? "Médecin supprimé avec succès" : "Médecin non trouvé";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    // =================== GESTION DES MUTUELLES ===================

    public String ajouterMutuelle(String nom, String codePostal, String ville,
                                  String telephone, double tauxRemboursement) {
        try {
            Mutuelle mutuelle = new Mutuelle(nom, codePostal, ville, telephone, tauxRemboursement);
            service.ajouterMutuelle(mutuelle);
            return "Mutuelle ajoutée avec succès";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public Optional<Mutuelle> rechercherMutuelle(String nom) {
        return service.rechercherMutuelle(nom);
    }

    // =================== GESTION DES MEDICAMENTS ===================

    public int getNombreMedicamentsEnStock() {
        int total = 0;
        for (Medicament medicament : inventaire.values()) {
            total += medicament.getQuantiteStock();
        }
        return total;
    }

    public Collection<Medicament> getInventaire() {
        return new ArrayList<>(inventaire.values());
    }

    public String ajouterMedicament(Medicament medicament) {
        try {
            inventaire.put(medicament.getNom().toLowerCase(), medicament);
            return "Médicament ajouté avec succès";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public Optional<Medicament> rechercherMedicament(String nom) {
        return Optional.ofNullable(inventaire.get(nom.toLowerCase()));
    }

    public String modifierStockMedicament(String nom, int nouvelleQuantite) {
        try {
            Medicament medicament = inventaire.get(nom.toLowerCase());
            if (medicament == null) {
                return "Médicament non trouvé";
            }
            medicament.setQuantiteStock(nouvelleQuantite);
            return "Stock mis à jour avec succès";
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    // =================== GESTION DES VENTES ===================

    public String effectuerVenteDirecte(String identifiantClient, Map<String, Integer> medicamentsQuantites) {
        try {
            Optional<Client> clientOpt = service.rechercherClient(identifiantClient);
            if (clientOpt.isEmpty()) {
                return "Client non trouvé";
            }

            Client client = clientOpt.get();
            List<Medicament> medicaments = new ArrayList<>();
            Map<Medicament, Integer> quantites = new HashMap<>();

            // Vérifier disponibilité et préparer la vente
            for (Map.Entry<String, Integer> entry : medicamentsQuantites.entrySet()) {
                String nomMed = entry.getKey();
                int quantiteDemandee = entry.getValue();

                Medicament medicament = inventaire.get(nomMed.toLowerCase());
                if (medicament == null) {
                    return "Médicament non trouvé : " + nomMed;
                }

                if (!medicament.isDisponible(quantiteDemandee)) {
                    return "Stock insuffisant pour " + nomMed +
                            " (demandé: " + quantiteDemandee + ", disponible: " + medicament.getQuantiteStock() + ")";
                }

                medicaments.add(medicament);
                quantites.put(medicament, quantiteDemandee);
            }

            // Effectuer la vente
            for (Map.Entry<Medicament, Integer> entry : quantites.entrySet()) {
                entry.getKey().reduireStock(entry.getValue());
            }

            // Enregistrer l'achat
            Achat achat = new Achat(new Date(), client, pharmacienConnecte,
                    "ACH" + System.currentTimeMillis(),
                    TypeAchat.DIRECT, medicaments, quantites);

            service.enregistrerAchat(achat);
            return "Vente effectuée avec succès";

        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    public String effectuerVenteOrdonnance(String identifiantClient, String identifiantMedecin,
                                           Map<String, Integer> medicamentsQuantites) {
        try {
            Optional<Client> clientOpt = service.rechercherClient(identifiantClient);
            Optional<Medecin> medecinOpt = service.rechercherMedecin(identifiantMedecin);

            if (clientOpt.isEmpty()) return "Client non trouvé";
            if (medecinOpt.isEmpty()) return "Médecin non trouvé";

            Client client = clientOpt.get();
            Medecin medecin = medecinOpt.get();

            List<Medicament> medicaments = new ArrayList<>();
            Map<Medicament, Integer> quantites = new HashMap<>();

            // Logique similaire à la vente directe mais avec ordonnance
            for (Map.Entry<String, Integer> entry : medicamentsQuantites.entrySet()) {
                String nomMed = entry.getKey();
                int quantiteDemandee = entry.getValue();

                Medicament medicament = inventaire.get(nomMed.toLowerCase());
                if (medicament == null) {
                    return "Médicament non trouvé : " + nomMed;
                }

                // Vérification de péremption
                if (new Date().after(medicament.getDatePeremption())) {
                    return "Médicament périmé : " + nomMed;
                }

                if (!medicament.isDisponible(quantiteDemandee)) {
                    return "Stock insuffisant pour " + nomMed;
                }

                medicaments.add(medicament);
                quantites.put(medicament, quantiteDemandee);
            }

            // Réduire les stocks
            for (Map.Entry<Medicament, Integer> entry : quantites.entrySet()) {
                entry.getKey().reduireStock(entry.getValue());
            }

            // Créer l'ordonnance
            String refOrdonnance = "ORD" + System.currentTimeMillis();
            Ordonnance ordonnance = new Ordonnance(new Date(), medecin, client,
                    medicaments, quantites, refOrdonnance);
            service.enregistrerOrdonnance(ordonnance);

            // Créer l'achat
            Achat achat = new Achat(new Date(), client, pharmacienConnecte,
                    "ACH" + System.currentTimeMillis(),
                    TypeAchat.ORDONNANCE, medicaments, quantites);
            service.enregistrerAchat(achat);

            return "Vente sur ordonnance effectuée avec succès";

        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }

    // =================== STATISTIQUES ===================

    public Map<String, Object> obtenirStatistiques(Date debut, Date fin) {
        Map<String, Object> stats = new HashMap<>();

        try {
            double chiffreAffaires = service.calculerChiffreAffaires(debut, fin);
            double montantRembourse = service.calculerMontantRembourse(debut, fin);
            List<Achat> achats = service.getAchatsParPeriode(debut, fin);

            int stockTotal = inventaire.values().stream()
                    .mapToInt(Medicament::getQuantiteStock)
                    .sum();

            long ruptureStock = inventaire.values().stream()
                    .filter(med -> med.getQuantiteStock() == 0)
                    .count();

            stats.put("chiffreAffaires", chiffreAffaires);
            stats.put("montantRembourse", montantRembourse);
            stats.put("nombreVentes", achats.size());
            stats.put("stockTotal", stockTotal);
            stats.put("ruptureStock", ruptureStock);
            stats.put("beneficeNet", chiffreAffaires - montantRembourse);

        } catch (Exception e) {
            stats.put("erreur", e.getMessage());
        }

        return stats;
    }

    // =================== HISTORIQUE ===================

    public List<Achat> obtenirHistoriqueVentes(Date debut, Date fin) {
        try {
            return service.getAchatsParPeriode(debut, fin);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Achat> obtenirVentesClient(String identifiantClient) {
        try {
            Optional<Client> client = service.rechercherClient(identifiantClient);
            return client.map(service::getAchatsParClient).orElse(new ArrayList<>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // =================== UTILITAIRES ===================

    public void setPharmacienConnecte(Pharmacien pharmacien) {
        this.pharmacienConnecte = pharmacien;
    }

    public Pharmacien getPharmacienConnecte() {
        return pharmacienConnecte;
    }

    private void initialiserDonneesDemo() {
        try {
            // Créer des dates valides
            Date maintenant = new Date();
            Date peremption = new Date(maintenant.getTime() + 365L * 24 * 60 * 60 * 1000);

            // Médicaments de démonstration
            ajouterMedicament(new Medicament("Doliprane",
                    main.model.Medicament.CategorieMedicament.ANALGESIQUES,
                    5.99, 100, maintenant, peremption));

            ajouterMedicament(new Medicament("Aspirine",
                    main.model.Medicament.CategorieMedicament.ANALGESIQUES,
                    3.50, 75, maintenant, peremption));

            ajouterMedicament(new Medicament("Amoxicilline",
                    main.model.Medicament.CategorieMedicament.ANTIBIOTIQUES,
                    12.99, 50, maintenant, peremption));

            // Client de démonstration
            ajouterClient("Martin", "Pierre", "12 Rue de Paris", "75000", "Paris",
                    "0123456789", "pierre.martin@email.com", "CL001", "123456789012345");

            // Médecin de démonstration
            ajouterMedecin("Dupont", "Marie", "15 Avenue des Médecins", "75001", "Paris",
                    "0123456790", "marie.dupont@hopital.fr", "MED001", "12345678901");

            // Mutuelle de démonstration
            ajouterMutuelle("Mutuelle Santé Plus", "75000", "Paris", "0123456800", 70.0);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des données demo : " + e.getMessage());
        }
    }
}

