package main.model.Medicament;

import main.model.security.SecurityValidator;

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
        this.nom = SecurityValidator.validateMedicamentName(nom);
        this.categorie = SecurityValidator.validateNotNull(categorie, "Catégorie");
        this.prix = SecurityValidator.validatePrix(prix);
        this.quantiteStock = SecurityValidator.validateStock(quantiteStock);
        this.dateMiseEnService = SecurityValidator.validateDate(dateMiseEnService, "Date de mise en service");
        this.datePeremption = SecurityValidator.validateFutureDate(datePeremption, "Date de péremption");
        SecurityValidator.validateDateOrder(dateMiseEnService, datePeremption,
                "Date de mise en service", "Date de péremption");
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
        this.prix = SecurityValidator.validatePrix(prix);
    }
    public void setQuantiteStock(int nouvelleQuantite) {
        int quantiteValidee = SecurityValidator.validateStock(nouvelleQuantite);
        this.quantiteStock = quantiteValidee;
    }
    public void setDatePeremption(Date datePeremption) {
        this.datePeremption = datePeremption;
    }

    public void reduireStock(int quantite) throws StockInsuffisantException {
        SecurityValidator.validateStockOperation(this.quantiteStock, quantite, "REDUCTION");
        quantiteStock -= quantite;
    }

    public void augmenterStock(int quantite) {
        SecurityValidator.validateStockOperation(this.quantiteStock, quantite, "ADDITION");
        quantiteStock += quantite;
    }

    public boolean isDisponible(int quantite) {
        SecurityValidator.validateQuantite(quantite);
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
