package main.model.Document.TypeDocument;

import main.model.Document.Document;
import main.model.Medicament.Medicament;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Ordonnance extends Document {
    private List<Medicament> medicaments;
    private Map<Medicament, Integer> quantites;
    private String reference;
    private double montantTotal;
    private Client patient;
    private Medecin medecin;

    public Ordonnance(Date dateCreation, Medecin medecin, Client patient,
                      List<Medicament> medicaments, Map<Medicament, Integer> quantites, String reference) {
        super(dateCreation, medecin.getNom(), patient.getNom());
        this.medecin = medecin;
        this.patient = patient;
        this.medicaments = medicaments;
        this.quantites = quantites;
        this.reference = reference;
        this.montantTotal = calculerMontantTotal();
    }

    public Client getPatient() {
        return patient;
    }
    public Medecin getMedecin() {
        return medecin;
    }

    public List<Medicament> getMedicaments() {
        return medicaments;
    }
    public Map<Medicament, Integer> getQuantites() {
        return quantites;
    }
    public String getReference() {
        return reference;
    }
    public double getMontantTotal() {
        return montantTotal;
    }
    public void setMedicaments(List<Medicament> medicaments) {
        this.medicaments = medicaments;
    }
    public void setQuantites(Map<Medicament, Integer> quantites) {
        this.quantites = quantites;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public void ajouterMedicament(Medicament medicament, int quantite) {
        medicaments.add(medicament);
        quantites.put(medicament, quantite);
        montantTotal = calculerMontantTotal();
    }

    public void retirerMedicament(Medicament medicament) {
        medicaments.remove(medicament);
        quantites.remove(medicament);
        montantTotal = calculerMontantTotal();
    }

    public double calculerMontantTotal() {
        double total = 0;
        for (Map.Entry<Medicament, Integer> entry : quantites.entrySet()) {
            total += entry.getKey().getPrix() * entry.getValue();
        }
        return total;
    }

    public String getNomMedecin() {
        return medecin.getNom() + " " + medecin.getPrenom();
    }

    public String getNomPatient() {
        return patient.getNom() + " " + patient.getPrenom();

    }
    @Override
    public void genererDocument() {
        System.out.println("Generation de l'ordonnance pour le patient " + getNomPatient());
    }

    @Override
    public String toString() {
        return "Ordonnance{" +
                super.toString().replace("Document", "") +
                ", medicaments=" + medicaments +
                ", quantites=" + quantites +
                ", reference='" + reference + '\'' +
                ", montantTotal=" + montantTotal +
                '}';
    }
}
