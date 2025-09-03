package main.model.service;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.Medicament;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestPharmacieService {

    private List<Client> clients;
    private List<Medecin> medecins;
    private List<Mutuelle> mutuelles;
    private List<Achat> achats;
    private List<Ordonnance> ordonnances;

    public GestPharmacieService() {
        this.clients = new ArrayList<>();
        this.medecins = new ArrayList<>();
        this.mutuelles = new ArrayList<>();
        this.achats = new ArrayList<>();
        this.ordonnances = new ArrayList<>();
    }


    public void ajouterClient(Client client) {
        clients.add(client);
    }

    public void modifierClient(Client client) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getIdentifiant().equals(client.getIdentifiant())) {
                clients.set(i, client);
                break;
            }
        }
    }

    public void supprimerClient(String identifiant) {
        clients.removeIf(client -> client.getIdentifiant().equals(identifiant));
    }

    public Client rechercherClient(String identifiant) {
        for (Client client : clients) {
            if (client.getIdentifiant().equals(identifiant)) {
                return client;
            }
        }
        return null;
    }

    public void ajouterMedecin(Medecin medecin) {
        medecins.add(medecin);
    }

    public void modifierMedecin(Medecin medecin) {
        for (int i = 0; i < medecins.size(); i++) {
            if (medecins.get(i).getIdentifiant().equals(medecin.getIdentifiant())) {
                medecins.set(i, medecin);
                break;
            }
        }
    }
    public void supprimerMedecin(String identifiant) {
        medecins.removeIf(medecin -> medecin.getIdentifiant().equals(identifiant));
    }

    public Medecin rechercherMedecin(String identifiant) {
        for (Medecin medecin : medecins) {
            if (medecin.getIdentifiant().equals(identifiant)) {
                return medecin;
            }
        }
        return null;
    }

    public void ajouterMutuelle(Mutuelle mutuelle) {
        mutuelles.add(mutuelle);
    }

    public void modifierMutuelle(Mutuelle mutuelle) {
        for (int i = 0; i < mutuelles.size(); i++) {
            if (mutuelles.get(i).getNom().equals(mutuelle.getNom())) {
                mutuelles.set(i, mutuelle);
                break;
            }
        }
    }
    public void supprimerMutuelle(String nom) {
        mutuelles.removeIf(mutuelle -> mutuelle.getNom().equals(nom));
    }

    public Mutuelle rechercherMutuelle(String nom) {
        for (Mutuelle mutuelle : mutuelles) {
            if (mutuelle.getNom().equals(nom)) {
                return mutuelle;
            }
        }
        return null;
    }

    public void enregistrerAchat(Achat achat) {
        achats.add(achat);
    }

    public List<Achat> getAchatsParPeriode(Date debut, Date fin) {
        List<Achat> achatsParPeriode = new ArrayList<>();
        for (Achat achat : achats) {
            if (!achat.getDateTransaction().before(debut) && !achat.getDateTransaction().after(fin)) {
                achatsParPeriode.add(achat);
            }
        }
        return achatsParPeriode;
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
        double chiffreAffaires = 0.0;
        for (Achat achat : getAchatsParPeriode(debut, fin)) {
            chiffreAffaires += achat.getMontantTotal();
        }
        return chiffreAffaires;
    }
}
