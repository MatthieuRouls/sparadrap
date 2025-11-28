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
/**
 * Retourne le nombre de clients connus.
 * Préfère la source service et retombe sur le cache local en cas d'erreur.
 *
 * @return nombre total de clients
 */
public int getNombreClients() {
    try {
        return service.getNombreClients();
    } catch (Exception e) {
        return clients != null ? clients.size() : 0;
    }
}

/**
 * Ajoute un client sans mutuelle.
 * L'identifiant est utilisé tel quel (aucune génération ici).
 *
 * @return message d'issue opérationnelle
 */
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

/**
 * Ajoute un client avec mutuelle optionnelle.
 * Si {@code nomMutuelle} est vide ou "Aucune", aucune mutuelle n'est associée.
 *
 * @param nomMutuelle nom exact d'une mutuelle existante ou "Aucune"
 * @return message d'issue opérationnelle
 */
public String ajouterClient(String nom, String prenom, String adresse, String codePostal,
                            String ville, String telephone, String email, String identifiant,
                            String numeroSecu, String nomMutuelle) {
    try {
        Mutuelle mutuelle = null;
        if (nomMutuelle != null) {
            String nm = nomMutuelle.trim();
            if (!nm.isEmpty() && !"Aucune".equalsIgnoreCase(nm)) {
                mutuelle = service.rechercherMutuelle(nm).orElse(null);
            }
        }
        Client client = new Client(nom, prenom, adresse, codePostal, ville,
                telephone, email, identifiant, numeroSecu, mutuelle, null);
        service.ajouterClient(client);
        clients.add(client);
        return "Client ajouté avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Ajoute un client en générant automatiquement l'identifiant à partir prénom/nom.
 *
 * @return message d'issue opérationnelle
 */
public String ajouterClient(String nom, String prenom, String adresse, String codePostal,
                            String ville, String telephone, String email, String numeroSecu) {
    try {
        String identifiantGenere = service.generateClientIdentifiant(prenom, nom);
        Client client = new Client(nom, prenom, adresse, codePostal, ville,
                telephone, email, identifiantGenere, numeroSecu, null, null);
        service.ajouterClient(client);
        clients.add(client);
        return "Client ajouté avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Recherche un client par identifiant.
 *
 * @param identifiant identifiant exact du client
 * @return client s'il existe
 */
public Optional<Client> rechercherClient(String identifiant) {
    try {
        return service.rechercherClient(identifiant);
    } catch (Exception e) {
        return Optional.empty();
    }
}

/**
 * Retourne tous les clients.
 */
public Collection<Client> getTousClients() {
    return service.getTousClients();
}

/**
 * Retourne toutes les mutuelles connues.
 */
public Collection<Mutuelle> getToutesMutuelles() {
    return service.getToutesMutuelles();
}

/**
 * Génère un identifiant client à partir du prénom et du nom.
 *
 * @return identifiant proposé
 */
public String generateClientIdentifiant(String prenom, String nom) {
    return service.generateClientIdentifiant(prenom, nom);
}

/**
 * Modifie les informations d'un client existant.
 *
 * @param client instance à jour
 * @return message d'issue opérationnelle
 */
public String modifierClient(Client client) {
    try {
        service.modifierClient(client);
        return "Client modifié avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Supprime un client par identifiant.
 *
 * @param identifiant identifiant exact
 * @return message d'issue opérationnelle
 */
public String supprimerClient(String identifiant) {
    try {
        boolean supprime = service.supprimerClient(identifiant);
        if (supprime) {
            clients.removeIf(c -> identifiant.equals(c.getIdentifiant()));
            return "Client supprimé avec succès";
        }
        return "Client non trouvé";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Associe (ou retire) un médecin traitant à un client.
 * Si rppsMedecin est null ou vide → retire l'association.
 */
public String assignerMedecinClient(String identifiantClient, String rppsMedecin) {
    try {
        Optional<Client> clientOpt = service.rechercherClient(identifiantClient);
        if (clientOpt.isEmpty()) return "Client non trouvé";
        Client client = clientOpt.get();

        if (rppsMedecin == null || rppsMedecin.trim().isEmpty()) {
            client.setMedecinTraitant(null);
        } else {
            Optional<Medecin> medecinOpt = service.rechercherMedecin(rppsMedecin.trim());
            if (medecinOpt.isEmpty()) return "Médecin non trouvé";
            client.setMedecinTraitant(medecinOpt.get());
        }
        service.modifierClient(client);
        return "Médecin traitant mis à jour";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

    // =================== GESTION DES MEDECINS ===================

/**
 * Ajoute un médecin avec identifiant distinct et RPPS.
 *
 * @return message d'issue opérationnelle
 */
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

/**
 * Ajoute un médecin avec identifiant = RPPS.
 *
 * @return message d'issue opérationnelle
 */
public String ajouterMedecin(String nom, String prenom, String adresse, String codePostal,
                             String ville, String telephone, String email, String numeroRPPS) {
    try {
        Medecin medecin = new Medecin(nom, prenom, adresse, codePostal, ville,
                telephone, email, numeroRPPS, numeroRPPS);
        service.ajouterMedecin(medecin);
        return "Médecin ajouté avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Recherche un médecin par identifiant (RPPS).
 *
 * @param identifiant RPPS
 * @return médecin s'il existe
 */
public Optional<Medecin> rechercherMedecin(String identifiant) {
    try {
        return service.rechercherMedecin(identifiant);
    } catch (Exception e) {
        return Optional.empty();
    }
}

/**
 * Retourne tous les médecins.
 */
public Collection<Medecin> getTousMedecins() {
    return service.getTousMedecins();
}

/**
 * Modifie les informations d'un médecin.
 *
 * @return message d'issue opérationnelle
 */
public String modifierMedecin(Medecin medecin) {
    try {
        service.modifierMedecin(medecin);
        return "Médecin modifié avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Supprime un médecin par identifiant (RPPS).
 *
 * @param identifiant RPPS
 * @return message d'issue opérationnelle
 */
public String supprimerMedecin(String identifiant) {
    try {
        boolean supprime = service.supprimerMedecin(identifiant);
        return supprime ? "Médecin supprimé avec succès" : "Médecin non trouvé";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

    // =================== GESTION DES MUTUELLES ===================

/**
 * Ajoute une mutuelle.
 *
 * @param tauxRemboursement pourcentage [0–100]
 * @return message d'issue opérationnelle
 */
public String ajouterMutuelle(String nom, String adresse, String codePostal, String ville,
                              String telephone, String email, double tauxRemboursement) {
    try {
        Mutuelle mutuelle = new Mutuelle(nom, adresse, codePostal, ville, telephone, email, tauxRemboursement);
        service.ajouterMutuelle(mutuelle);
        return "Mutuelle ajoutée avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Recherche une mutuelle par nom exact.
 */
public Optional<Mutuelle> rechercherMutuelle(String nom) {
    return service.rechercherMutuelle(nom);
}

/**
 * Modifie une mutuelle existante.
 */
public String modifierMutuelle(Mutuelle mutuelle) {
    try {
        service.modifierMutuelle(mutuelle);
        return "Mutuelle modifiée avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Supprime une mutuelle par son nom.
 */
public String supprimerMutuelle(String nom) {
    try {
        service.supprimerMutuelle(nom);
        return "Mutuelle supprimée avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Liste les ordonnances émises par un médecin donné.
 */
public java.util.List<Ordonnance> getOrdonnancesParMedecin(Medecin medecin) {
    return service.getOrdonnancesParMedecin(medecin);
}

/**
 * Liste les ordonnances d'un client.
 */
public java.util.List<Ordonnance> getOrdonnancesParClient(Client client) {
    return service.getOrdonnancesParClient(client);
}

/**
 * Liste les achats associés à un médecin (via ordonnances).
 */
public java.util.List<Achat> getAchatsParMedecin(Medecin medecin) {
    return service.getAchatsParMedecin(medecin);
}

    // =================== GESTION DES MEDICAMENTS ===================

/**
 * Calcule le nombre total d'unités en stock (tous médicaments confondus).
 */
public int getNombreMedicamentsEnStock() {
    int total = 0;
    for (Medicament medicament : inventaire.values()) {
        total += medicament.getQuantiteStock();
    }
    return total;
}

/**
 * Retourne une vue de l'inventaire courant.
 */
public Collection<Medicament> getInventaire() {
    return new ArrayList<>(inventaire.values());
}

/**
 * Ajoute un médicament à l'inventaire (clé: nom en minuscules).
 *
 * @return message d'issue opérationnelle
 */
public String ajouterMedicament(Medicament medicament) {
    try {
        inventaire.put(medicament.getNom().toLowerCase(), medicament);
        return "Médicament ajouté avec succès";
    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Recherche un médicament par nom (insensible à la casse).
 */
public Optional<Medicament> rechercherMedicament(String nom) {
    return Optional.ofNullable(inventaire.get(nom.toLowerCase()));
}

/**
 * Modifie la quantité en stock d'un médicament.
 *
 * @param nom nom du médicament
 * @param nouvelleQuantite quantité cible
 * @return message d'issue opérationnelle
 */
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

/**
 * Effectue une vente directe (sans ordonnance) pour un client.
 * Vérifie le stock, met à jour les quantités et enregistre l'achat.
 * Si le client a une mutuelle, applique le taux de remboursement.
 *
 * @param identifiantClient identifiant du client
 * @param medicamentsQuantites map nom → quantité demandée
 * @return récapitulatif avec référence, montants et reste à payer
 */
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

        double montantTotal = achat.getMontantTotal();
        double tauxMutuelle = 0.0;
        double montantRembourse = 0.0;
        if (client.getMutuelle() != null) {
            tauxMutuelle = client.getMutuelle().getTauxRemboursement();
            montantRembourse = achat.getMontantRembourse();
        }
        double resteAPayer = montantTotal - montantRembourse;

        return String.format(
                "Vente effectuée avec succès\n" +
                "Référence achat : %s\n" +
                "Montant total : %.2f €\n" +
                (client.getMutuelle() != null ? "Taux mutuelle : %.1f%%\nMontant remboursé : %.2f €\n" : "") +
                "Reste à payer : %.2f €",
                achat.getReference(),
                montantTotal,
                tauxMutuelle,
                montantRembourse,
                resteAPayer
        );

    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

/**
 * Effectue une vente sur ordonnance.
 * Valide la présence du client et du médecin, vérifie péremption des médicaments et met à jour les stocks.
 * Si le client a une mutuelle, applique le taux de remboursement.
 *
 * @param identifiantClient identifiant du client
 * @param identifiantMedecin RPPS du médecin
 * @param medicamentsQuantites map nom → quantité demandée
 * @return récapitulatif: référence, montant total, (taux/remboursement), reste à payer
 */
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
        String refAchat = "ACH" + System.currentTimeMillis();
        Achat achat = new Achat(new Date(), client, pharmacienConnecte,
                refAchat,
                TypeAchat.ORDONNANCE, medicaments, quantites);
        service.enregistrerAchat(achat);

        // Détails pour retour utilisateur
        double montantTotal = achat.getMontantTotal();
        double tauxMutuelle = 0.0;
        double montantRembourse = 0.0;
        if (client.getMutuelle() != null) {
            tauxMutuelle = client.getMutuelle().getTauxRemboursement();
            montantRembourse = achat.getMontantRembourse();
        }
        double resteAPayer = montantTotal - montantRembourse;

        return String.format(
                "Vente sur ordonnance effectuée avec succès\n" +
                "Référence ordonnance : %s\n" +
                "Référence achat : %s\n" +
                "Montant total : %.2f €\n" +
                (client.getMutuelle() != null ? "Taux mutuelle : %.1f%%\nMontant remboursé : %.2f €\n" : "") +
                "Reste à payer : %.2f €",
                refOrdonnance,
                refAchat,
                montantTotal,
                tauxMutuelle,
                montantRembourse,
                resteAPayer
        );

    } catch (Exception e) {
        return "Erreur : " + e.getMessage();
    }
}

    // =================== STATISTIQUES ===================

/**
 * Agrège des statistiques métier sur une période.
 * Retourne CA, montant remboursé, nombre de ventes, stock total, ruptures et bénéfice net estimé.
 *
 * @param debut date de début (incluse)
 * @param fin date de fin (incluse)
 * @return map de valeurs numériques avec clés documentées
 */
public Map<String, Object> obtenirStatistiques(Date debut, Date fin) {
    Map<String, Object> stats = new HashMap<>();

    try {
        double chiffreAffaires = service.calculerChiffreAffaires(debut, fin);
        double montantRembourse = service.calculerMontantRembourse(debut, fin);
        int nombreVentes = service.getNombreVentesParPeriode(debut, fin);

        int stockTotal = inventaire.values().stream()
                .mapToInt(Medicament::getQuantiteStock)
                .sum();

        long ruptureStock = inventaire.values().stream()
                .filter(med -> med.getQuantiteStock() == 0)
                .count();

        stats.put("chiffreAffaires", chiffreAffaires);
        stats.put("montantRembourse", montantRembourse);
        stats.put("nombreVentes", nombreVentes);
        stats.put("stockTotal", stockTotal);
        stats.put("ruptureStock", ruptureStock);
        stats.put("beneficeNet", chiffreAffaires - montantRembourse);

    } catch (Exception e) {
        stats.put("erreur", e.getMessage());
    }

    return stats;
}

    // =================== HISTORIQUE ===================

/**
 * Liste les ventes dans une période.
 */
public List<Achat> obtenirHistoriqueVentes(Date debut, Date fin) {
    try {
        return service.getAchatsParPeriode(debut, fin);
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

/**
 * Recherche un achat par sa référence.
 */
public Optional<Achat> rechercherAchatParReference(String reference) {
    try {
        return service.getAchatParReference(reference);
    } catch (Exception e) {
        return Optional.empty();
    }
}

/**
 * Liste les ventes d'un client donné.
 */
public List<Achat> obtenirVentesClient(String identifiantClient) {
    try {
        Optional<Client> client = service.rechercherClient(identifiantClient);
        return client.map(service::getAchatsParClient).orElse(new ArrayList<>());
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

/**
 * Liste les ordonnances établies par un médecin (via son RPPS).
 */
public List<Ordonnance> obtenirOrdonnancesParMedecin(String numeroRPPS) {
    try {
        Optional<Medecin> med = service.rechercherMedecin(numeroRPPS);
        return med.map(service::getOrdonnancesParMedecin).orElse(new ArrayList<>());
    } catch (Exception e) {
        return new ArrayList<>();
    }
}

    // =================== UTILITAIRES ===================

/**
 * Définit le pharmacien actuellement connecté.
 */
public void setPharmacienConnecte(Pharmacien pharmacien) {
    this.pharmacienConnecte = pharmacien;
}

/**
 * Retourne le pharmacien actuellement connecté (peut être null).
 */
public Pharmacien getPharmacienConnecte() {
    return pharmacienConnecte;
}

/**
 * Charge des données de démonstration en mémoire (médicaments, client, médecin, mutuelle).
 * Utilisé au démarrage pour proposer un environnement utilisable.
 */
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

        // Médecin de démonstration (identifiant = RPPS)
        ajouterMedecin("Dupont", "Marie", "15 Avenue des Médecins", "75001", "Paris",
                "0123456790", "marie.dupont@hopital.fr", "12345678901");

        // Mutuelle de démonstration
        ajouterMutuelle("Mutuelle Santé Plus", "20 Rue de la Santé", "75000", "Paris", "0123456800", "contact@mutuelle-sante.fr", 70.0);

    } catch (Exception e) {
        System.err.println("Erreur lors de l'initialisation des données demo : " + e.getMessage());
    }
}
}

