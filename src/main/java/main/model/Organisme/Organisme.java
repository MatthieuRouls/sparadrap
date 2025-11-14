package main.model.Organisme;

import main.model.security.SecurityValidator;

public abstract class Organisme {

    private String nom;
    private String codePostal;
    private String ville;
    private String telephone;

    public Organisme(String nom, String codePostal, String ville, String telephone) {
        this.nom = SecurityValidator.validateAndTrimString(nom, "Nom de l'organisme");
        this.codePostal = SecurityValidator.validatePostalCode(codePostal);
        this.ville = SecurityValidator.validateCity(ville);
        this.telephone = SecurityValidator.validatePhoneNumber(telephone);
    }

    public String getNom() {
        return nom;
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
    public void setNom(String nom) {
        this.nom = nom;
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

    @Override
    public String toString() {
        return "Organisme{" +
                "nom='" + nom + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", ville='" + ville + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
