package main.model.Personne.CategoriePersonne;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Personne.Personne;
import main.model.Transaction.TypeTransaction.Achat;

import java.util.Date;

public class Pharmacien extends Personne {
    private String numeroRPPS;
    private String specialite;
    private Date dateEmbauche;

    public Pharmacien(String nom, String prenom, String adresse, String codePostal, String ville, String numTelephone, String email, String identifiant, String numeroRPPS, String specialite, Date dateEmbauche) {
        super(nom, prenom, adresse, codePostal, ville, numTelephone, email, identifiant);
        this.numeroRPPS = numeroRPPS;
        this.specialite = specialite;
        this.dateEmbauche = dateEmbauche;
    }

    public String getNumeroRPPS() {
        return numeroRPPS;
    }
    public String getSpecialite() {
        return specialite;
    }
    public Date getDateEmbauche() {
        return dateEmbauche;
    }
    public void setDateEmbauche(Date dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }
    public void setNumeroRPPS(String numeroRPPS) {
        this.numeroRPPS = numeroRPPS;
    }
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public boolean validerOrdonnance(Ordonnance ordonnance) {
        return true;
    }

    public void effectuerVente(Achat achat) {
        System.out.println("Vente effectuee par le pharmacien " + this.getNom() + " " + this.getPrenom());
    }

    @Override
    public String toString() {
        return "Pharmacien{" +
                super.toString().replace("Personne", "") +
                ", numOrdrePharmacien='" + numeroRPPS + '\'' +
                ", specialite='" + specialite + '\'' +
                ", dateEmbauche=" + dateEmbauche +
                '}';
    }

}
