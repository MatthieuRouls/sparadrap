package main.model.service;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.Medicament;
import main.model.Medicament.TypeAchat;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
import main.model.security.SecurityValidator;
import org.junit.jupiter.api.Order;

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


    public void ajouterClient(Client client) {
        SecurityValidator.validateNotNull(client, "Client");
        if (clients.containsKey(client.getIdentifiant())) {
            throw new IllegalArgumentException("Un client avec cet identifiant existe déjà");
        }
        clients.put(client.getIdentifiant(), client);
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


    public void ajouterMedecin(Medecin medecin) {
        if (medecin == null) {
            throw new IllegalArgumentException("Le medecin ne peut pas etre null");
        }
        if (medecins.containsKey(medecin.getIdentifiant())) {
            throw new IllegalArgumentException("Un medecin avec cet identifiant existe deja");
        }
        medecins.put(medecin.getIdentifiant(), medecin);
    }

    public void modifierMedecin(Medecin medecin) {
        if (medecin == null) {
            throw new IllegalArgumentException("Le medecin ne peut pas etre null");
        }
        if (!medecins.containsKey(medecin.getIdentifiant())) {
            throw new IllegalArgumentException("Aucun medecin trouve avec cet identifiant");
        }
        medecins.put(medecin.getIdentifiant(), medecin);
    }

    public boolean supprimerMedecin(String identifiant) {
        if (identifiant == null || identifiant.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant ne peut pas etre null");
        }
        return medecins.remove(identifiant) != null;
    }

    public Optional<Medecin> rechercherMedecin(String numRPPS) {
        if (medecins.containsKey(numRPPS)) {
            return Optional.of(medecins.get(numRPPS)); // Retourne le médecin trouvé
        } else {
            return Optional.empty(); // Médecin non trouvé
        }
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
    }

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
        return getAchatsParPeriode(debut, fin).stream()
                .mapToDouble(Achat::getMontantRembourse)
                .sum();
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
        return getAchatsParPeriode(debut, fin).stream().mapToDouble(Achat::getMontantTotal).sum();
    }
}
