package main.model.Personne;
import main.model.security.SecurityValidator;

import java.util.Objects;

/**
 * Classe abstraite représentant une personne générique.
 * Elle est étendue par les classes concrètes comme `Client` et `Pharmacien`.
 */
public abstract class Personne {

    private String nom;
    private String prenom;
    private String adresse;
    private String codePostal;
    private String ville;
    private String numTelephone;
    private String email;
    private String identifiant;


    public Personne(String nom, String prenom, String adresse, String codePostal,
                    String ville, String numTelephone, String email, String identifiant) {
        this.nom = SecurityValidator.validatePersonName(nom, "Nom");
        this.prenom = SecurityValidator.validatePersonName(prenom, "Prenom");
        this.adresse = SecurityValidator.validateAndTrimString(adresse, "Adresse");
        this.codePostal = SecurityValidator.validatePostalCode(codePostal);
        this.ville = SecurityValidator.validateCity(ville);
        this.numTelephone = SecurityValidator.validatePhoneNumber(numTelephone);
        this.email = SecurityValidator.validateEmail(email);
        this.identifiant = SecurityValidator.validateIdentifiant(identifiant);
    }


    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getVille() {
        return ville;
    }

    public String getNumTelephone() {
        return numTelephone;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = SecurityValidator.validatePersonName(nom, "Nom");
    }

    public void setPrenom(String prenom) {
        this.prenom = SecurityValidator.validatePersonName(prenom, "Prénom");
    }

    public void setAdresse(String adresse) {
        this.adresse = SecurityValidator.validateAndTrimString(adresse, "Adresse");
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = SecurityValidator.validatePostalCode(codePostal);
    }

    public void setVille(String ville) {
        this.ville = SecurityValidator.validateCity(ville);
    }

    public void setNumTelephone(String numTelephone) {
        this.numTelephone = SecurityValidator.validatePhoneNumber(numTelephone);
    }

    public void setEmail(String email) {
        this.email = SecurityValidator.validateEmail(email);;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = SecurityValidator.validateIdentifiant(identifiant);
    }

    // Méthode pour afficher les informations de la personne
    @Override
    public String toString() {
        return "Personne{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", ville='" + ville + '\'' +
                ", numTelephone='" + numTelephone + '\'' +
                ", email='" + email + '\'' +
                ", identifiant='" + identifiant + '\'' +
                '}';
    }

    // Méthode pour comparer deux personnes par leur identifiant
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personne personne = (Personne) o;
        return identifiant.equals(personne.identifiant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifiant);
    }
}
