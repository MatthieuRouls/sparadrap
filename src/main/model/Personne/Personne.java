package main.model.Personne;
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
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
        this.numTelephone = numTelephone;
        this.email = email;
        this.identifiant = identifiant;
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
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
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

    public void setNumTelephone(String numTelephone) {
        this.numTelephone = numTelephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
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
