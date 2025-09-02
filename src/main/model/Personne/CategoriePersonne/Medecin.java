package main.model.Personne.CategoriePersonne;

import main.model.Personne.Personne;

import java.util.ArrayList;
import java.util.List;

public class Medecin extends Personne {
    private String numeroRPPS;
    private List<Client> patients;

    public Medecin(String nom, String prenom, String adresse, String codePostal,
                   String ville, String numTelephone, String email, String identifiant,
                   String numeroRPPS) {
        super(nom, prenom, adresse, codePostal, ville, numTelephone, email, identifiant);
        this.numeroRPPS = numeroRPPS;
        patients = new ArrayList<>();
    }

    public String getNumeroRPPS() {
        return numeroRPPS;
    }

    public List<Client> getPatients() {
        return patients;
    }

    public void setNumeroRPPS(String numeroRPPS) {
        this.numeroRPPS = numeroRPPS;
    }

    public void ajouterPatient(Client client) {
        if (!patients.contains(client)) {
            patients.add(client);
        }
    }

    public void retirerPatient(Client client) {
        patients.remove(client);
    }

    @Override
    public String toString() {
        return "Medecin{" +
                super.toString().replace("Personne", "") +
                ", numeroAgrement='" + numeroRPPS + '\'' +
                ", nombre de patients=" + patients.size() +
                '}';
    }
}
