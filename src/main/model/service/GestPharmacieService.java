package main.model.service;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.Medicament;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
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
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas etre null");
        }
        if (clients.containsKey(client.getIdentifiant())) {
            throw new IllegalArgumentException("Un client avec cet identifiant existe deja");
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
        if (identifiant == null || identifiant.trim().isEmpty()) {
        throw new IllegalArgumentException("L'identifiant ne peut pas etre null ou vide");
        }
        return clients.remove(identifiant) != null;
    }

    public Optional<Client> rechercherClient(String identifiant) {
        if (identifiant == null || identifiant.trim().isEmpty()) {
            return Optional.empty();
            }
            return Optional.ofNullable(clients.get(identifiant.trim()));
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

    public Optional<Medecin> rechercherMedecin(String identifiant) {
        if (identifiant == null || identifiant.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(medecins.get(identifiant.trim()));
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
        List<Achat> achatsParMedecin = new ArrayList<>();
        for (Achat achat : achats) {
            achatsParMedecin.add(achat);
        }
        return achatsParMedecin;
    }

    public void enregistrerOrdonnance(Ordonnance ordonnance) {
        ordonnances.add(ordonnance);
    }

    public List<Ordonnance> getOrdonnancesParClient(Client client) {
        List<Ordonnance> ordonnancesParClient = new ArrayList<>();
        for (Ordonnance ordonnance : ordonnances) {
            if (ordonnance.getNomPatient().equals(client.getPrenom() + " " + client.getNom())) {
                ordonnancesParClient.add(ordonnance);
            }
        }
        return ordonnancesParClient;
    }

    public List<Ordonnance> getOrdonnancesParMedecin(Medecin medecin) {
        List<Ordonnance> ordonnancesParMedecin = new ArrayList<>();
        for (Ordonnance ordonnance : ordonnances) {
            if (ordonnance.getNomMedecin().equals(medecin.getNom())) {
                ordonnancesParMedecin.add(ordonnance);
            }
        }
        return ordonnancesParMedecin;
    }

    public double calculerChiffreAffaires(Date debut, Date fin) {
        return getAchatsParPeriode(debut, fin).stream().mapToDouble(Achat::getMontantTotal).sum();
    }
}
