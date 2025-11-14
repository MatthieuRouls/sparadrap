package main.model.Personne.CategoriePersonne;

import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.Personne;
import main.model.security.SecurityValidator;

public class Client extends Personne {
    private String numeroSecuriteSocial;
    private Mutuelle mutuelle;
    private Medecin medecinTraitant;

    public Client(String nom, String prenom, String adresse, String codePostal, String ville, String numTelephone, String email, String identifiant, String numeroSecuriteSocial, Mutuelle mutuelle, Medecin medecinTraitant) {
        super(nom, prenom, adresse, codePostal, ville, numTelephone, email, identifiant);
        this.numeroSecuriteSocial = SecurityValidator.validateNumeroSecuriteSociale(numeroSecuriteSocial);
        this.mutuelle = mutuelle;
        this.medecinTraitant = medecinTraitant;
    }

    public String getNumeroSecuriteSocial() {
        return SecurityValidator.maskSecuriteSociale(numeroSecuriteSocial);
    }

    public Mutuelle getMutuelle() {
        return mutuelle;
    }

    public Medecin getMedecinTraitant() {
        return medecinTraitant;
    }

    public void setNumeroSecuriteSocial(String numeroSecuriteSocial) {
        this.numeroSecuriteSocial = numeroSecuriteSocial;
    }

    public void setMutuelle(Mutuelle mutuelle) {
        this.mutuelle = mutuelle;
    }

    public void setMedecinTraitant(Medecin medecinTraitant) {
        this.medecinTraitant = medecinTraitant;
    }

    @Override
    public String toString() {
        return "Client{" +
                super.toString().replace("Personne", "") +
                ", numeroSecuriteSocial='" + numeroSecuriteSocial + '\'' +
                ", mutuelle=" + (mutuelle != null ? mutuelle.getNom() : "Aucune") +
                ", medecinTraitant=" + (medecinTraitant != null ? medecinTraitant.getNom() : "Aucun") +
                '}';

    }
}
