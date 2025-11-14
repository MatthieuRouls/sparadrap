package main.model.Document;

import main.model.Medicament.CategorieMedicament;

import java.util.Date;

public abstract class Document {

    private Date dateCreation;
    private String nomMedecin;
    private String nomPatient;

    public Document(Date dateCreation, String nomMedecin, String nomPatient) {
        this.dateCreation = dateCreation;
        this.nomMedecin = nomMedecin;
        this.nomPatient = nomPatient;
    }

    public Date getDateCreation() {
        return dateCreation;
    }
    public String getNomMedecin() {
        return nomMedecin;
    }
    public String getNomPatient() {
        return nomPatient;
    }
    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    public void setNomMedecin(String nomMedecin) {
        this.nomMedecin = nomMedecin;
    }
    public void setNomPatient(String nomPatient) {
        this.nomPatient = nomPatient;
    }

    public abstract void genererDocument();

    @Override
    public String toString() {
        return "Document{" +
                "date de creation: " + dateCreation +
                ", nom du medecin: " + nomMedecin + "\"" +
                ", nom du patient: " + nomPatient + "\"" +
                "}";
    }


}

