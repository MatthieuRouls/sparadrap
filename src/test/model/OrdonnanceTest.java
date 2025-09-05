package test.model;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrdonnanceTest {
    private Ordonnance ordonnance;
    private Date dateCreation;
    private Medecin medecin;
    private Client patient;
    private Medicament medicament1;
    private Medicament medicament2;
    private List<Medicament> medicaments;
    private Map<Medicament, Integer> quantites;

    @BeforeEach
    public void setUp() {
        dateCreation = new Date();

        medecin = new Medecin(
                "Dupont", "Jean", "10 Rue des Docteurs", "75000", "Paris",
                "0123456789", "jean.dupont@example.com", "MED001",
                "83456789011"
        );

        patient = new Client(
                "Martin", "Pierre", "12 Rue de Paris", "75000", "Paris",
                "0123456789", "pierre.martin@example.com", "CL001",
                "123456789012345", null, medecin
        );

        medicament1 = new Medicament("Doliprane", CategorieMedicament.ANALGESIQUES, 5.99, 100, new Date(), new Date());
        medicament2 = new Medicament("Amoxicilline", CategorieMedicament.ANTIBIOTIQUES, 10.99, 50, new Date(), new Date());

        List<Medicament> medicaments = new ArrayList<>();
        medicaments.add(medicament1);
        medicaments.add(medicament2);

        Map<Medicament, Integer> quantites = new HashMap<>();
        quantites.put(medicament1, 2);
        quantites.put(medicament2, 1);

        ordonnance = new Ordonnance(dateCreation, medecin, patient, medicaments, quantites, "ORD001");
    }

    @Test
    public void testConstructeurEtGetters() {
        assertEquals("Dr. Dupont Jean", ordonnance.getNomMedecin());
        assertEquals("Martin Pierre", ordonnance.getNomPatient());
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