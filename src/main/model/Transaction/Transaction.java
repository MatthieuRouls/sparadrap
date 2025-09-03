package main.model.Transaction;

import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Pharmacien;

import java.util.Date;

public abstract class Transaction {
    protected Date dateTransaction;
    protected Client client;
    protected Pharmacien pharmacien;
    protected String reference;
    protected double montantTotal;

    public Transaction(Date dateTransaction,Client client, Pharmacien pharmacien, String reference) {
        this.dateTransaction = dateTransaction;
        this.client = client;
        this.pharmacien = pharmacien;
        this.reference = reference;
        this.montantTotal = 0;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public Client getClient() {
        return client;
    }

    public Pharmacien getPharmacien() {
        return pharmacien;
    }

    public String getReference() {
        return reference;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setPharmacien(Pharmacien pharmacien) {
        this.pharmacien = pharmacien;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public abstract void calculerMontantTotal();

    @Override
    public String toString() {
        return "Transaction{" +
                "dateTransaction=" + dateTransaction +
                ", client=" + client.getNom() +
                ", pharmacien=" + pharmacien.getNom() +
                ", reference='" + reference + '\'' +
                ", montantTotal=" + montantTotal +
                '}';
    }
}
