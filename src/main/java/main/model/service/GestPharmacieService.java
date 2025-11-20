package main.model.service;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.TypeAchat;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
import main.model.dao.ClientDAO;
import main.model.dao.MedecinDAO;
import main.model.dao.MutuelleDAO;
import main.model.security.SecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Service métier central de la pharmacie.
 * Gère clients, médecins, mutuelles avec persistance en base de données MySQL.
 * Les ventes et ordonnances restent en mémoire pour l'instant.
 */
public class GestPharmacieService {
    private static final Logger logger = LoggerFactory.getLogger(GestPharmacieService.class);

    // DAO pour la persistance en base de données
    private final ClientDAO clientDAO;
    private final MedecinDAO medecinDAO;
    private final MutuelleDAO mutuelleDAO;

    // Listes en mémoire (pour achats et ordonnances - à migrer plus tard si nécessaire)
    private final List<Achat> achats = new CopyOnWriteArrayList<>();
    private final List<Ordonnance> ordonnances = new CopyOnWriteArrayList<>();

    // Mutuelle et taux génériques (utilisés pour les nouveaux clients)
    private volatile Mutuelle mutuelleGenerique;
    private volatile double tauxRemboursementGenerique = 70.0; // valeur par défaut; peut être ajustée via setter

    // Persistance simple des ventes
    private final java.io.File ventesFile = new java.io.File("data/achats.csv");

    public GestPharmacieService() {
        this.clientDAO = new ClientDAO();
        this.medecinDAO = new MedecinDAO();
        this.mutuelleDAO = new MutuelleDAO();
        logger.info("GestPharmacieService initialisé avec persistance base de données");
    }


/**
 * Ajoute un client (clé: identifiant unique).
 * Persiste le client en base de données.
 */
public void ajouterClient(Client client) {
    SecurityValidator.validateNotNull(client, "Client");
    logger.info("Ajout du client: {}", client.getIdentifiant());

    try {
        // Vérifier si le client existe déjà
        if (clientDAO.findByIdentifiant(client.getIdentifiant()).isPresent()) {
            throw new IllegalArgumentException("Un client avec cet identifiant existe déjà");
        }

        clientDAO.create(client);
        logger.info("Client '{}' ajouté avec succès en base de données", client.getIdentifiant());

    } catch (SQLException e) {
        logger.error("Erreur lors de l'ajout du client: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de l'ajout du client en base de données", e);
    }
}

/**
 * Retourne le nombre de clients en base de données.
 */
public int getNombreClients() {
    try {
        int count = clientDAO.findAll().size();
        logger.debug("Nombre de clients: {}", count);
        return count;
    } catch (SQLException e) {
        logger.error("Erreur lors du comptage des clients: {}", e.getMessage(), e);
        return 0;
    }
}

/**
 * Met à jour un client existant en base de données.
 */
public void modifierClient(Client client) {
    if (client == null) {
        throw new IllegalArgumentException("Le client ne peut pas etre null");
    }
    logger.info("Modification du client: {}", client.getIdentifiant());

    try {
        if (clientDAO.findByIdentifiant(client.getIdentifiant()).isEmpty()) {
            throw new IllegalArgumentException("Aucun client trouve avec cet identifiant");
        }

        clientDAO.update(client);
        logger.info("Client '{}' modifié avec succès", client.getIdentifiant());

    } catch (SQLException e) {
        logger.error("Erreur lors de la modification du client: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de la modification du client en base de données", e);
    }
}

/**
 * Supprime un client par identifiant.
 *
 * @return true si un client a été retiré
 */
public boolean supprimerClient(String identifiant) {
    String validIdentifiant = SecurityValidator.validateIdentifiant(identifiant);
    logger.info("Suppression du client: {}", validIdentifiant);

    try {
        boolean deleted = clientDAO.delete(validIdentifiant);
        if (deleted) {
            logger.info("Client '{}' supprimé avec succès", validIdentifiant);
        } else {
            logger.warn("Aucun client trouvé pour suppression: {}", validIdentifiant);
        }
        return deleted;

    } catch (SQLException e) {
        logger.error("Erreur lors de la suppression du client: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de la suppression du client en base de données", e);
    }
}

/**
 * Recherche un client par identifiant validé.
 */
public Optional<Client> rechercherClient(String identifiant) {
    if (identifiant == null || identifiant.trim().isEmpty()) {
        return Optional.empty();
    }
    String validIdentifiant = SecurityValidator.validateIdentifiant(identifiant);
    logger.debug("Recherche du client: {}", validIdentifiant);

    try {
        return clientDAO.findByIdentifiant(validIdentifiant);
    } catch (SQLException e) {
        logger.error("Erreur lors de la recherche du client: {}", e.getMessage(), e);
        return Optional.empty();
    }
}

/**
 * Retourne tous les clients depuis la base de données.
 */
public java.util.Collection<Client> getTousClients() {
    try {
        List<Client> clients = clientDAO.findAll();
        logger.debug("Récupération de {} clients", clients.size());
        return clients;
    } catch (SQLException e) {
        logger.error("Erreur lors de la récupération des clients: {}", e.getMessage(), e);
        return new ArrayList<>();
    }
}

/**
 * Retourne toutes les mutuelles depuis la base de données.
 */
public java.util.Collection<Mutuelle> getToutesMutuelles() {
    try {
        List<Mutuelle> mutuelles = mutuelleDAO.findAll();
        logger.debug("Récupération de {} mutuelles", mutuelles.size());
        return mutuelles;
    } catch (SQLException e) {
        logger.error("Erreur lors de la récupération des mutuelles: {}", e.getMessage(), e);
        return new ArrayList<>();
    }
}

/**
 * Génère un identifiant client unique à partir du prénom/nom.
 */
public String generateClientIdentifiant(String prenom, String nom) {
    String p = prenom == null ? "" : prenom.trim();
    String n = nom == null ? "" : nom.trim();
    String base = (p.length() >= 2 ? p.substring(0, 2) : p)
            + (n.length() >= 3 ? n.substring(0, 3) : n);
    base = base.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    if (base.isEmpty()) {
        base = "CL";
    }
    String candidate = base;
    int suffix = 1;

    try {
        // Vérifier l'unicité en base de données
        while (clientDAO.findByIdentifiant(candidate).isPresent()) {
            candidate = base + String.format("%02d", suffix++);
        }
    } catch (SQLException e) {
        logger.error("Erreur lors de la génération de l'identifiant: {}", e.getMessage(), e);
    }

    return candidate;
}


/**
 * Ajoute un médecin en base de données.
 */
public void ajouterMedecin(Medecin medecin) {
    if (medecin == null) {
        throw new IllegalArgumentException("Le medecin ne peut pas etre null");
    }
    String rpps = medecin.getNumeroRPPS();
    if (rpps == null || rpps.trim().isEmpty()) {
        throw new IllegalArgumentException("Le numéro RPPS est requis");
    }
    logger.info("Ajout du médecin: {} {} (RPPS: {})", medecin.getPrenom(), medecin.getNom(), rpps);

    try {
        if (medecinDAO.findByRPPS(rpps).isPresent()) {
            throw new IllegalArgumentException("Un medecin avec ce numéro RPPS existe deja");
        }

        medecinDAO.create(medecin);
        logger.info("Médecin '{}' ajouté avec succès en base de données", medecin.getIdentifiant());

    } catch (SQLException e) {
        logger.error("Erreur lors de l'ajout du médecin: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de l'ajout du médecin en base de données", e);
    }
}

/**
 * Met à jour un médecin existant en base de données.
 */
public void modifierMedecin(Medecin medecin) {
    if (medecin == null) {
        throw new IllegalArgumentException("Le medecin ne peut pas etre null");
    }
    logger.info("Modification du médecin: {}", medecin.getIdentifiant());

    try {
        if (medecinDAO.findByIdentifiant(medecin.getIdentifiant()).isEmpty()) {
            throw new IllegalArgumentException("Aucun medecin trouve avec cet identifiant");
        }

        medecinDAO.update(medecin);
        logger.info("Médecin '{}' modifié avec succès", medecin.getIdentifiant());

    } catch (SQLException e) {
        logger.error("Erreur lors de la modification du médecin: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de la modification du médecin en base de données", e);
    }
}

/**
 * Supprime un médecin par identifiant.
 *
 * @return true si un médecin a été retiré
 */
public boolean supprimerMedecin(String identifiant) {
    if (identifiant == null || identifiant.trim().isEmpty()) {
        throw new IllegalArgumentException("L'identifiant ne peut pas etre vide");
    }
    logger.info("Suppression du médecin: {}", identifiant);

    try {
        boolean deleted = medecinDAO.delete(identifiant);
        if (deleted) {
            logger.info("Médecin '{}' supprimé avec succès", identifiant);
        } else {
            logger.warn("Aucun médecin trouvé pour suppression: {}", identifiant);
        }
        return deleted;

    } catch (SQLException e) {
        logger.error("Erreur lors de la suppression du médecin: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de la suppression du médecin en base de données", e);
    }
}

/**
 * Recherche un médecin par RPPS.
 */
public Optional<Medecin> rechercherMedecin(String numRPPS) {
    if (numRPPS == null || numRPPS.trim().isEmpty()) {
        return Optional.empty();
    }
    logger.debug("Recherche du médecin par RPPS: {}", numRPPS);

    try {
        return medecinDAO.findByRPPS(numRPPS);
    } catch (SQLException e) {
        logger.error("Erreur lors de la recherche du médecin: {}", e.getMessage(), e);
        return Optional.empty();
    }
}

/**
 * Retourne tous les médecins depuis la base de données.
 */
public java.util.Collection<Medecin> getTousMedecins() {
    try {
        List<Medecin> medecins = medecinDAO.findAll();
        logger.debug("Récupération de {} médecins", medecins.size());
        return medecins;
    } catch (SQLException e) {
        logger.error("Erreur lors de la récupération des médecins: {}", e.getMessage(), e);
        return new ArrayList<>();
    }
}

/**
 * Ajoute une mutuelle en base de données.
 */
public void ajouterMutuelle(Mutuelle mutuelle) {
    if (mutuelle == null) {
        throw new IllegalArgumentException("La mutuelle ne peut pas etre null");
    }
    logger.info("Ajout de la mutuelle: {}", mutuelle.getNom());

    try {
        if (mutuelleDAO.findByNom(mutuelle.getNom()).isPresent()) {
            throw new IllegalArgumentException("Cette mutuelle existe deja");
        }

        mutuelleDAO.create(mutuelle);
        logger.info("Mutuelle '{}' ajoutée avec succès en base de données", mutuelle.getNom());

        if ("Mutuelle Générique".equals(mutuelle.getNom())) {
            this.mutuelleGenerique = mutuelle;
            this.tauxRemboursementGenerique = mutuelle.getTauxRemboursement();
        }

    } catch (SQLException e) {
        logger.error("Erreur lors de l'ajout de la mutuelle: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de l'ajout de la mutuelle en base de données", e);
    }
}

/**
 * Met à jour une mutuelle existante en base de données.
 */
public void modifierMutuelle(Mutuelle mutuelle) {
    if (mutuelle == null) {
        throw new IllegalArgumentException("La mutuelle ne peut pas etre null");
    }
    logger.info("Modification de la mutuelle: {}", mutuelle.getNom());

    try {
        if (mutuelleDAO.findByNom(mutuelle.getNom()).isEmpty()) {
            throw new IllegalArgumentException("Aucune mutuelle trouvee avec ce nom");
        }

        mutuelleDAO.update(mutuelle);
        logger.info("Mutuelle '{}' modifiée avec succès", mutuelle.getNom());

        if (mutuelleGenerique != null && mutuelleGenerique.getNom().equals(mutuelle.getNom())) {
            mutuelleGenerique = mutuelle;
            tauxRemboursementGenerique = mutuelle.getTauxRemboursement();
        }

    } catch (SQLException e) {
        logger.error("Erreur lors de la modification de la mutuelle: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de la modification de la mutuelle en base de données", e);
    }
}

/**
 * Supprime une mutuelle par nom.
 *
 * @return true si une mutuelle a été retirée
 */
public boolean supprimerMutuelle(String nom) {
    if (nom == null || nom.trim().isEmpty()) {
        throw new IllegalArgumentException("Le nom ne peut pas etre null");
    }
    logger.info("Suppression de la mutuelle: {}", nom);

    try {
        boolean deleted = mutuelleDAO.delete(nom);
        if (deleted) {
            logger.info("Mutuelle '{}' supprimée avec succès", nom);
        } else {
            logger.warn("Aucune mutuelle trouvée pour suppression: {}", nom);
        }
        return deleted;

    } catch (SQLException e) {
        logger.error("Erreur lors de la suppression de la mutuelle: {}", e.getMessage(), e);
        throw new RuntimeException("Erreur lors de la suppression de la mutuelle en base de données", e);
    }
}

/**
 * Recherche une mutuelle par nom exact.
 */
public Optional<Mutuelle> rechercherMutuelle(String nom) {
    if (nom == null || nom.trim().isEmpty()) {
        return Optional.empty();
    }
    logger.debug("Recherche de la mutuelle: {}", nom);

    try {
        return mutuelleDAO.findByNom(nom.trim());
    } catch (SQLException e) {
        logger.error("Erreur lors de la recherche de la mutuelle: {}", e.getMessage(), e);
        return Optional.empty();
    }
}

/**
 * Enregistre un achat en mémoire et persiste un résumé CSV minimal.
 */
public void enregistrerAchat(Achat achat) {
    if (achat == null) {
        throw new IllegalArgumentException("L'achat ne peut pas etre null");
    }
    achats.add(achat);
    try {
        ensureVentesStorage();
        try (java.io.FileWriter fw = new java.io.FileWriter(ventesFile, true)) {
            String line = String.join(",",
                    String.valueOf(achat.getDateTransaction().getTime()),
                    String.valueOf(achat.getMontantTotal()),
                    String.valueOf(achat.getMontantRembourse()),
                    achat.getType().name()
            );
            fw.write(line + System.lineSeparator());
        }
    } catch (Exception ignored) {
        // Non bloquant si l'écriture échoue
    }
}

/**
 * Retourne la liste des achats entre deux dates (incluses).
 */
public List<Achat> getAchatsParPeriode(Date debut, Date fin) {
    if (debut == null || fin == null) {
        throw new IllegalArgumentException("Les dates ne peuvent pas être null");
    }
    if (debut.after(fin)) {
        throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
    }
    return achats.stream()
            .filter(achat -> !achat.getDateTransaction().before(debut) &&
                    !achat.getDateTransaction().after(fin))
            .collect(Collectors.toList());
}

/**
 * Recherche un achat par référence exacte.
 */
public Optional<Achat> getAchatParReference(String reference) {
    if (reference == null || reference.trim().isEmpty()) {
        return Optional.empty();
    }
    return achats.stream()
            .filter(a -> reference.equals(a.getReference()))
            .findFirst();
}

/**
 * Calcule le nombre de ventes sur une période (lecture CSV, sinon mémoire).
 */
public int getNombreVentesParPeriode(Date debut, Date fin) {
    try {
        return (int) readVentesBetween(debut, fin).count();
    } catch (Exception e) {
        return getAchatsParPeriode(debut, fin).size();
    }
}

/**
 * Retourne les achats d'un client.
 */
public List<Achat> getAchatsParClient(Client client) {
    List<Achat> achatsParClient = new ArrayList<>();
    for (Achat achat : achats) {
        if (achat.getClient().equals(client)) {
            achatsParClient.add(achat);
        }
    }
    return achatsParClient;
}

/**
 * Retourne les achats associés à un médecin (via correspondance avec ordonnances).
 */
public List<Achat> getAchatsParMedecin(Medecin medecin) {
    SecurityValidator.validateNotNull(medecin, "Médecin");
    return achats.stream()
            .filter(achat -> {
                List<Ordonnance> ordonnancesMedecin = getOrdonnancesParMedecin(medecin);
                for (Ordonnance ord : ordonnancesMedecin) {
                    if (achat.getReference().contains(ord.getReference()) ||
                            achat.getDateTransaction().equals(ord.getDateCreation())) {
                        return true;
                    }
                }
                return false;
            })
            .collect(Collectors.toList());
}

/**
 * Enregistre une ordonnance.
 */
public void enregistrerOrdonnance(Ordonnance ordonnance) {
    ordonnances.add(ordonnance);
}

/**
 * Liste les ordonnances d'un client.
 */
public List<Ordonnance> getOrdonnancesParClient(Client client) {
    SecurityValidator.validateNotNull(client, "Client");
    List<Ordonnance> ordonnancesParClient = new ArrayList<>();
    for (Ordonnance ordonnance : ordonnances) {
        if (ordonnance.getPatient().equals(client)) {
            ordonnancesParClient.add(ordonnance);
        }
    }
    return ordonnancesParClient;
}

/**
 * Liste les ordonnances d'un médecin.
 */
public List<Ordonnance> getOrdonnancesParMedecin(Medecin medecin) {
    SecurityValidator.validateNotNull(medecin, "Médecin");
    return ordonnances.stream()
            .filter(ordonnance -> ordonnance.getMedecin().equals(medecin))
            .collect(Collectors.toList());
}

/**
 * Retourne toutes les ordonnances en mémoire.
 */
public List<Ordonnance> getToutesLesOrdonnances() {
    return new ArrayList<>(ordonnances);
}

/**
 * Recherche une ordonnance par référence exacte.
 */
public Optional<Ordonnance> rechercherOrdonnance(String reference) {
    if (reference == null || reference.trim().isEmpty()) {
        return Optional.empty();
    }

    return ordonnances.stream()
            .filter(ordonnance -> ordonnance.getReference().equals(reference.trim()))
            .findFirst();
}

/**
 * Calcule la somme des montants remboursés sur une période.
 */
public double calculerMontantRembourse(Date debut, Date fin) {
    try {
        return readVentesBetween(debut, fin)
                .mapToDouble(r -> r.montantRembourse)
                .sum();
    } catch (Exception e) {
        return getAchatsParPeriode(debut, fin).stream()
                .mapToDouble(Achat::getMontantRembourse)
                .sum();
    }
}

/**
 * Statistiques de remboursement pour une mutuelle donnée sur une période.
 */
public Map<String, Double> getStatistiquesRemboursementMutuelle(Mutuelle mutuelle, Date debut, Date fin) {
    SecurityValidator.validateNotNull(mutuelle, "Mutuelle");

    List<Achat> achatsAvecMutuelle = getAchatsParPeriode(debut, fin).stream()
            .filter(achat -> achat.getClient().getMutuelle() != null &&
                    achat.getClient().getMutuelle().equals(mutuelle))
            .collect(Collectors.toList());

    double totalAchats = achatsAvecMutuelle.stream()
            .mapToDouble(Achat::getMontantTotal)
            .sum();

    double totalRembourse = achatsAvecMutuelle.stream()
            .mapToDouble(Achat::getMontantRembourse)
            .sum();

    Map<String, Double> stats = new HashMap<>();
    stats.put("totalAchats", totalAchats);
    stats.put("totalRembourse", totalRembourse);
    stats.put("nombreAchats", (double) achatsAvecMutuelle.size());
    stats.put("tauxRemboursementEffectif", totalAchats > 0 ? (totalRembourse / totalAchats) * 100 : 0);

    return stats;
}

/**
 * Vérifie la cohérence entre ordonnances et achats associés.
 * Retourne la liste des problèmes détectés.
 */
public List<String> verifierCoherenceOrdonnancesAchats() {
    List<String> problemes = new ArrayList<>();

    for (Ordonnance ordonnance : ordonnances) {
        boolean achatTrouve = achats.stream()
                .anyMatch(achat ->
                        achat.getClient().equals(ordonnance.getPatient()) &&
                                achat.getDateTransaction().equals(ordonnance.getDateCreation()) &&
                                achat.getType() == TypeAchat.ORDONNANCE
                );

        if (!achatTrouve) {
            problemes.add("Ordonnance " + ordonnance.getReference() +
                    " sans achat correspondant");
        }
    }

    return problemes;
}

/**
 * Calcule le chiffre d'affaires sur la période.
 */
public double calculerChiffreAffaires(Date debut, Date fin) {
    try {
        return readVentesBetween(debut, fin)
                .mapToDouble(r -> r.montantTotal)
                .sum();
    } catch (Exception e) {
        return getAchatsParPeriode(debut, fin).stream().mapToDouble(Achat::getMontantTotal).sum();
    }
}

/**
 * S'assure que le fichier des ventes existe (création dossier/fichier si nécessaire).
 */
private void ensureVentesStorage() throws java.io.IOException {
    java.io.File dir = ventesFile.getParentFile();
    if (dir != null && !dir.exists()) {
        dir.mkdirs();
    }
    if (!ventesFile.exists()) {
        ventesFile.createNewFile();
    }
}

/**
 * Lit les enregistrements CSV de ventes dans un intervalle [debut, fin].
 */
private java.util.stream.Stream<SaleRecord> readVentesBetween(Date debut, Date fin) throws java.io.IOException {
    ensureVentesStorage();
    java.nio.file.Path path = ventesFile.toPath();
    final long start = debut.getTime();
    final long end = fin.getTime();
    return java.nio.file.Files.lines(path)
            .filter(line -> !line.trim().isEmpty())
            .map(line -> line.split(","))
            .filter(parts -> parts.length >= 3)
            .map(parts -> {
                try {
                    long ts = Long.parseLong(parts[0]);
                    double total = Double.parseDouble(parts[1]);
                    double rembourse = Double.parseDouble(parts[2]);
                    return new SaleRecord(ts, total, rembourse);
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .filter(r -> r.timestamp >= start && r.timestamp <= end);
}

    private static class SaleRecord {
        final long timestamp;
        final double montantTotal;
        final double montantRembourse;
        SaleRecord(long timestamp, double montantTotal, double montantRembourse) {
            this.timestamp = timestamp;
            this.montantTotal = montantTotal;
            this.montantRembourse = montantRembourse;
        }
    }
}
