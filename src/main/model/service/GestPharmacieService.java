package main.model.service;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.TypeAchat;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
import main.model.security.SecurityValidator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GestPharmacieService {

    private final Map<String, Client> clients = new ConcurrentHashMap<>();
    private final Map<String, Medecin> medecins = new ConcurrentHashMap<>();
    private final Map<String, Mutuelle> mutuelles = new ConcurrentHashMap<>();
    private final List<Achat> achats = new CopyOnWriteArrayList<>();
    private final List<Ordonnance> ordonnances = new CopyOnWriteArrayList<>();

    // Persistance simple des ventes
    private final java.io.File ventesFile = new java.io.File("data/achats.csv");


    public void ajouterClient(Client client) {
        SecurityValidator.validateNotNull(client, "Client");
        if (clients.containsKey(client.getIdentifiant())) {
            throw new IllegalArgumentException("Un client avec cet identifiant existe déjà");
        }
        clients.put(client.getIdentifiant(), client);
    }

    public int getNombreClients() {
        return clients.size();
    }

    public void modifierClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas etre null");
        }
        if (!clients.containsKey(client.getIdentifiant())) {
            throw new IllegalArgumentException("Aucun client trouve avec cet identifiant");
        }
        clients.put(client.getIdentifiant(), client);
    }

    public boolean supprimerClient(String identifiant) {
        String validIdentifiant = SecurityValidator.validateIdentifiant(identifiant);
        return clients.remove(validIdentifiant) != null;
    }

    public Optional<Client> rechercherClient(String identifiant) {
        if (identifiant == null || identifiant.trim().isEmpty()) {
            return Optional.empty();
        }
        String validIdentifiant = SecurityValidator.validateIdentifiant(identifiant);
        return Optional.ofNullable(clients.get(validIdentifiant));
    }

    public java.util.Collection<Client> getTousClients() {
        return new ArrayList<>(clients.values());
    }

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
        while (clients.containsKey(candidate)) {
            candidate = base + String.format("%02d", suffix++);
        }
        return candidate;
    }


    public void ajouterMedecin(Medecin medecin) {
        if (medecin == null) {
            throw new IllegalArgumentException("Le medecin ne peut pas etre null");
        }
        // Utiliser le numéro RPPS comme clé unique
        String rpps = medecin.getNumeroRPPS();
        if (rpps == null || rpps.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro RPPS est requis");
        }
        if (medecins.containsKey(rpps)) {
            throw new IllegalArgumentException("Un medecin avec ce numéro RPPS existe deja");
        }
        medecins.put(rpps, medecin);
    }

    public void modifierMedecin(Medecin medecin) {
        if (medecin == null) {
            throw new IllegalArgumentException("Le medecin ne peut pas etre null");
        }
        String rpps = medecin.getNumeroRPPS();
        if (!medecins.containsKey(rpps)) {
            throw new IllegalArgumentException("Aucun medecin trouve avec ce numéro RPPS");
        }
        medecins.put(rpps, medecin);
    }

    public boolean supprimerMedecin(String numeroRPPS) {
        if (numeroRPPS == null || numeroRPPS.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro RPPS ne peut pas etre vide");
        }
        return medecins.remove(numeroRPPS) != null;
    }

    public Optional<Medecin> rechercherMedecin(String numRPPS) {
        if (numRPPS == null || numRPPS.trim().isEmpty()) {
            return Optional.empty();
        }
        if (medecins.containsKey(numRPPS)) {
            return Optional.of(medecins.get(numRPPS));
        } else {
            return Optional.empty();
        }
    }

    public java.util.Collection<Medecin> getTousMedecins() {
        return new ArrayList<>(medecins.values());
    }

    public void ajouterMutuelle(Mutuelle mutuelle) {
        if (mutuelle == null) {
            throw new IllegalArgumentException("Le mutuelle ne peut pas etre null");
        }
        if (mutuelles.containsKey(mutuelle.getNom())) {
            throw new IllegalArgumentException("Cette mutuelle existe deja");
        }
        mutuelles.put(mutuelle.getNom(), mutuelle);
    }

    public void modifierMutuelle(Mutuelle mutuelle) {
        if (mutuelle == null) {
            throw new IllegalArgumentException("La mutuelle ne peut pas etre null");
        }
        if (!medecins.containsKey(mutuelle.getNom())) {
            throw new IllegalArgumentException("Aucune mutuelle trouvee avec ce nom");
        }
        mutuelles.put(mutuelle.getNom(), mutuelle);
    }

    public boolean supprimerMutuelle(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas etre null");
        }
        return mutuelles.remove(nom) != null;
    }

    public Optional<Mutuelle> rechercherMutuelle(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(mutuelles.get(nom.trim()));
    }

    public void enregistrerAchat(Achat achat) {
        if (achat == null) {
            throw new IllegalArgumentException("L'achat ne peut pas etre null");
        }
        achats.add(achat);
        // Persister la vente pour conserver l'historique entre les relances
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
            // Ne pas bloquer l'appli si l'écriture échoue
        }
    }

    public List<Achat> getAchatsParPeriode(Date debut, Date fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Les dates ne peuvent pas être null");
        }
        if (debut.after(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }
        // Préserver comportement existant (en mémoire)
        return achats.stream()
                .filter(achat -> !achat.getDateTransaction().before(debut) &&
                        !achat.getDateTransaction().after(fin))
                .collect(Collectors.toList());
    }

    public int getNombreVentesParPeriode(Date debut, Date fin) {
        try {
            return (int) readVentesBetween(debut, fin).count();
        } catch (Exception e) {
            // Fallback mémoire
            return getAchatsParPeriode(debut, fin).size();
        }
    }

    public List<Achat> getAchatsParClient(Client client) {
        List<Achat> achatsParClient = new ArrayList<>();
        for (Achat achat : achats) {
            if (achat.getClient().equals(client)) {
                achatsParClient.add(achat);
            }
        }
        return achatsParClient;
    }

    public List<Achat> getAchatsParMedecin(Medecin medecin) {
        SecurityValidator.validateNotNull(medecin, "Médecin");
        return achats.stream()
                .filter(achat -> {
                    // Vérifier si l'achat est lié à une ordonnance de ce médecin
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

    public void enregistrerOrdonnance(Ordonnance ordonnance) {
        ordonnances.add(ordonnance);
    }

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

    public List<Ordonnance> getOrdonnancesParMedecin(Medecin medecin) {
        SecurityValidator.validateNotNull(medecin, "Médecin");
        return ordonnances.stream()
                .filter(ordonnance -> ordonnance.getMedecin().equals(medecin))
                .collect(Collectors.toList());
    }

    public List<Ordonnance> getToutesLesOrdonnances() {
        return new ArrayList<>(ordonnances);
    }

    public Optional<Ordonnance> rechercherOrdonnance(String reference) {
        if (reference == null || reference.trim().isEmpty()) {
            return Optional.empty();
        }

        return ordonnances.stream()
                .filter(ordonnance -> ordonnance.getReference().equals(reference.trim()))
                .findFirst();
    }

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

    public List<String> verifierCoherenceOrdonnancesAchats() {
        List<String> problemes = new ArrayList<>();

        for (Ordonnance ordonnance : ordonnances) {
            // Rechercher l'achat correspondant
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

    public double calculerChiffreAffaires(Date debut, Date fin) {
        try {
            return readVentesBetween(debut, fin)
                    .mapToDouble(r -> r.montantTotal)
                    .sum();
        } catch (Exception e) {
            return getAchatsParPeriode(debut, fin).stream().mapToDouble(Achat::getMontantTotal).sum();
        }
    }

    private void ensureVentesStorage() throws java.io.IOException {
        java.io.File dir = ventesFile.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (!ventesFile.exists()) {
            ventesFile.createNewFile();
        }
    }

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
