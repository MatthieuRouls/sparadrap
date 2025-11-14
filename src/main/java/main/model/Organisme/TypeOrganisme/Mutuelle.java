package main.model.Organisme.TypeOrganisme;

import main.model.Organisme.Organisme;
import main.model.security.SecurityValidator;

public class Mutuelle extends Organisme {

    private double tauxRemboursement;

    public Mutuelle(String nom, String codePostal, String ville, String telephone, double tauxRemboursement) {
        super(nom, codePostal, ville, telephone);
        this.tauxRemboursement = SecurityValidator.validateTauxRemboursement(tauxRemboursement);
    }

    public double getTauxRemboursement() {
        return tauxRemboursement;
    }
    public void setTauxRemboursement(double tauxRemboursement) {
        this.tauxRemboursement = tauxRemboursement;
    }

    @Override
    public String toString() {
        return "Mutuelle{" +
                super.toString().replace("Organisme", "") +
                ", taux de remboursements = " + tauxRemboursement + "}";
    }

}
