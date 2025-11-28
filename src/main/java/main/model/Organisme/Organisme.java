package main.model.Organisme;

import main.model.security.SecurityValidator;

public abstract class Organisme {

    private String nom;
    private String adresse;
    private String codePostal;
    private String ville;
    private String telephone;
    private String email;

    public Organisme(String nom, String adresse, String codePostal, String ville, String telephone, String email) {
        this.nom = SecurityValidator.validateAndTrimString(nom, "Nom de l'organisme");
        this.adresse = SecurityValidator.validateAndTrimStringOptional(adresse, "Adresse de l'organisme");
        this.codePostal = SecurityValidator.validatePostalCode(codePostal);
        this.ville = SecurityValidator.validateCity(ville);
        this.telephone = SecurityValidator.validatePhoneNumber(telephone);
        this.email = SecurityValidator.validateEmailOptional(email);
    }

    public String getNom() {
        return nom;
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
    public String getTelephone() {
        return telephone;
    }
    public String getEmail() {
        return email;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    public void setVille(String ville) {
        this.ville = ville;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Organisme{" +
                "nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", ville='" + ville + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
