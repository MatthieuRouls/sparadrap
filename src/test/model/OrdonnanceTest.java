package test.model;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrdonnanceTest {
    private Ordonnance ordonnance;
    private Date dateCreation;
    private Medicament medicament1;
    private Medicament medicament2;

    @BeforeEach
    public void setUp() {
        dateCreation = new Date();
        medicament1 = new Medicament("Doliprane", CategorieMedicament.ANALGESIQUES, 5.99, 100, new Date(), new Date());
        medicament2 = new Medicament("Amoxicilline", CategorieMedicament.ANTIBIOTIQUES, 10.99, 50, new Date(), new Date());

        List<Medicament> medicaments = new ArrayList<>();
        medicaments.add(medicament1);
        medicaments.add(medicament2);

        Map<Medicament, Integer> quantites = new HashMap<>();
        quantites.put(medicament1, 2);
        quantites.put(medicament2, 1);

        ordonnance = new Ordonnance(dateCreation, "Dr. Dupont", "Pierre Martin", medicaments, quantites, "ORD001");
    }

    @Test
    public void testConstructeurEtGetters() {
        assertEquals("Dr. Dupont", ordonnance.getNomMedecin());
        assertEquals("Pierre Martin", ordonnance.getNomPatient());
        assertEquals(2, ordonnance.getMedicaments().size());
        assertEquals(2, ordonnance.getQuantites().size());
        assertEquals("ORD001", ordonnance.getReference());
        assertEquals(5.99 * 2 + 10.99 * 1, ordonnance.getMontantTotal(), 0.001);
    }

    @Test
    public void testAjouterMedicament() {
        Medicament medicament3 = new Medicament("Ibuprofène", CategorieMedicament.ANTI_INFLAMMATOIRES, 7.99, 80, new Date(), new Date());
        ordonnance.ajouterMedicament(medicament3, 1);
        assertEquals(3, ordonnance.getMedicaments().size());
        assertEquals(5.99 * 2 + 10.99 * 1 + 7.99 * 1, ordonnance.getMontantTotal(), 0.001);
    }

    @Test
    public void testRetirerMedicament() {
        ordonnance.retirerMedicament(medicament1);
        assertEquals(1, ordonnance.getMedicaments().size());
        assertEquals(10.99 * 1, ordonnance.getMontantTotal(), 0.001);
    }

    @Test
    public void testCalculerMontantTotal() {
        assertEquals(5.99 * 2 + 10.99 * 1, ordonnance.calculerMontantTotal(), 0.001);
    }
}