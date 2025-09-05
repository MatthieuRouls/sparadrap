package main.model.Transaction.TypeTransaction;

import main.model.Medicament.Medicament;
import main.model.Medicament.TypeAchat;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Pharmacien;
import main.model.Transaction.Transaction;
import main.model.security.SecurityValidator;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Achat extends Transaction {

    private TypeAchat type;
    private List<Medicament> medicaments;
    private Map<Medicament, Integer> quantites;
    private double montantRembourse;

    public Achat(Date dateTransaction, Client client, Pharmacien pharmacien, String reference, TypeAchat type,
                 List<Medicament> medicaments, Map<Medicament, Integer> quantites) {
        super(dateTransaction, client, pharmacien, reference);
        this.type = type;
        this.medicaments = medicaments;
        this.quantites = quantites;
        calculerMontantTotal();
        calculerMontantRembourse();
    }

    public TypeAchat getType() {
        return type;
    }

    public List<Medicament> getMedicaments() {
        return medicaments;
    }

    public Map<Medicament, Integer> getQuantites() {
        return quantites;
    }

    public double getMontantRembourse() {
        return montantRembourse;
    }

    public void setType(TypeAchat type) {
        this.type = type;
    }

    public void setMedicaments(List<Medicament> medicaments) {
        this.medicaments = medicaments;
        calculerMontantRembourse();
        calculerMontantTotal();
    }

    public void setQuantites(Map<Medicament, Integer> quantites) {
        this.quantites = quantites;
        calculerMontantRembourse();
        calculerMontantTotal();
    }

    public void setMontantRembourse(double montantRembourse) {
        this.montantRembourse = montantRembourse;
    }

    public void ajouterMedicament(Medicament medicament, int quantite) {
        SecurityValidator.validateNotNull(medicament, "Médicament");
        SecurityValidator.validateQuantite(quantite);
        SecurityValidator.validateMedicamentNotExpired(medicament.getDatePeremption(), medicament.getNom());
        medicaments.add(medicament);
        quantites.put(medicament, quantite);
        calculerMontantTotal();
        calculerMontantRembourse();
    }

    public void retirerMedicament(Medicament medicament) {
        medicaments.remove(medicament);
        quantites.remove(medicament);
        calculerMontantTotal();
        calculerMontantRembourse();
    }


    @Override
    public void calculerMontantTotal() {
        double total = 0.0;
        for (Map.Entry<Medicament, Integer> entry : quantites.entrySet()) {
            total += entry.getKey().getPrix() * entry.getValue();
        }
        setMontantTotal(total);
        SecurityValidator.validateTransaction(getMontantTotal(), getMontantRembourse());
    }

    private void calculerMontantRembourse() {
        if (getClient().getMutuelle() != null) {
            double tauxRemboursement = getClient().getMutuelle().getTauxRemboursement();
            this.montantRembourse = getMontantTotal() * (tauxRemboursement / 100.0);
        } else {
            this.montantRembourse = 0.0;
        }
    }

    @Override
    public String toString() {
        return "Achat{" +
                super.toString().replace("Transaction", "") +
                ", type=" + type +
                ", medicaments=" + medicaments +
                ", quantites=" + quantites +
                ", montantRembourse=" + montantRembourse +
                '}';
    }
}
