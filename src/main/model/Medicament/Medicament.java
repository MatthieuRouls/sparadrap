package main.model.Medicament;

import java.util.Date;

public class Medicament {

    private String nom;
    private CategorieMedicament categorie;
    private double prix;
    private int quantiteStock;
    private Date dateMiseEnService;
    private Date datePeremption;

    public static class StockInsuffisantException extends RuntimeException {
        public StockInsuffisantException(String message) {
            super(message);
        }
    }

    public Medicament(String nom, CategorieMedicament categorie, double prix, int quantiteStock,Date dateMiseEnService, Date datePeremption) {
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.dateMiseEnService = dateMiseEnService;
        this.datePeremption = datePeremption;
    }

    public String getNom() {
        return nom;
    }
    public CategorieMedicament getCategorie() {
        return categorie;
    }
    public double getPrix() {
        return prix;
    }
    public int getQuantiteStock() {
        return quantiteStock;
    }
    public Date getDateMiseEnService() {
        return dateMiseEnService;
    }
    public Date getDatePeremption() {
        return new Date(datePeremption.getTime());
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setCategorie(CategorieMedicament categorie) {
        this.categorie = categorie;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }
    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }
    public void setDatePeremption(Date datePeremption) {
        this.datePeremption = datePeremption;
    }

    public void reduireStock(int quantite) throws StockInsuffisantException {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive.");
        }
        if (quantiteStock < quantite) {
            throw new StockInsuffisantException("Stock insuffisant pour réduire de " + quantite + ". Stock actuel : " + quantiteStock);
        }
        quantiteStock -= quantite;
    }

    public void augmenterStock(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive.");
        }
        quantiteStock += quantite;
    }

    public boolean isDisponible(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive.");
        }
        return quantiteStock >= quantite;
    }

    @Override
    public String toString() {
        return "Medicament{" +
                "nom='" + nom + '\'' +
                ", categorie=" + categorie +
                ", prix=" + prix +
                ", quantiteStock=" + quantiteStock +
                ", dateMiseEnService=" + dateMiseEnService +
                ", datePeremption=" + datePeremption +
                '}';
    }
}
