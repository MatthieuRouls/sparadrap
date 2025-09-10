package test.model;

import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;
import main.model.Medicament.TypeAchat;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Pharmacien;
import main.model.Transaction.TypeTransaction.Achat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AchatTest {
    private Achat achat;
    private Client client;
    private Pharmacien pharmacien;
    private Medicament medicament1;
    private Medicament medicament2;
    private Date dateTransaction;
    private Date dateMiseEnService;
    private Date datePeremption;
    private Map<Medicament, Integer> quantites;
    private Mutuelle mutuelle;

    @BeforeEach
    public void setUp() {
        dateTransaction = new Date();
        dateMiseEnService = new Date();
        datePeremption = new Date(dateMiseEnService.getTime() + 365L * 24 * 60 * 60 * 1000);;
        mutuelle = new Mutuelle("Mutuelle Générale", "75000", "Paris", "0123456789", 70.0);
        client = new Client("Martin", "Pierre", "12 Rue de Paris", "75000", "Paris",
                "0123456789", "pierre.martin@example.com", "CL001",
                "123456789012345", mutuelle, null);
        pharmacien = new Pharmacien("Dupont", "Jean", "10 Rue des Pharmaciens", "75000", "Paris",
                "0123456789", "jean.dupont@example.com", "PH001",
                "12345678912", "Pharmacie Clinique", new Date());

        medicament1 = new Medicament("Doliprane", CategorieMedicament.ANALGESIQUES, 5.99, 100, dateMiseEnService, datePeremption);
        medicament2 = new Medicament("Amoxicilline", CategorieMedicament.ANTIBIOTIQUES, 10.99, 50, dateMiseEnService, datePeremption);

        List<Medicament> medicaments = new ArrayList<>();
        medicaments.add(medicament1);
        medicaments.add(medicament2);

        quantites = new HashMap<>();
        quantites.put(medicament1, 2);
        quantites.put(medicament2, 1);

        achat = new Achat(dateTransaction, client, pharmacien, "ACH001", TypeAchat.ORDONNANCE, medicaments, quantites);
    }

    @Test
    public void testConstructeurEtGetters() {
        assertEquals(dateTransaction, achat.getDateTransaction());
        assertEquals(client, achat.getClient());
        assertEquals(pharmacien, achat.getPharmacien());
        assertEquals("ACH001", achat.getReference());
        assertEquals(TypeAchat.ORDONNANCE, achat.getType());
        assertEquals(2, achat.getMedicaments().size());
        assertEquals(2, achat.getQuantites().size());
    }

    @Test
    public void testCalculerMontantTotal() {
        double expectedTotal = (5.99 * 2) + (10.99 * 1);
        assertEquals(expectedTotal, achat.getMontantTotal(), 0.001);
    }

    @Test
    public void testCalculerMontantRembourse() {
        double expectedMontantRembourse = ((5.99 * 2) + (10.99 * 1)) * 0.7; // 70% de remboursement
        assertEquals(expectedMontantRembourse, achat.getMontantRembourse(), 0.001);
    }

    @Test
    public void testAjouterMedicament() {
        Medicament medicament3 = new Medicament("Ibuprofene", CategorieMedicament.ANTI_INFLAMMATOIRES, 7.99, 80, dateMiseEnService, datePeremption);
        achat.ajouterMedicament(medicament3, 1);
        assertEquals(3, achat.getMedicaments().size());
        assertEquals(5.99 * 2 + 10.99 * 1 + 7.99 * 1, achat.getMontantTotal(), 0.001);
        assertEquals((5.99 * 2 + 10.99 * 1 + 7.99 * 1) * 0.7, achat.getMontantRembourse(), 0.001);
    }

    @Test
    public void testRetirerMedicament() {
        achat.retirerMedicament(medicament1);
        assertEquals(1, achat.getMedicaments().size());
        assertEquals(10.99 * 1, achat.getMontantTotal(), 0.001);
        assertEquals(10.99 * 1 * 0.7, achat.getMontantRembourse(), 0.001);
    }

    @Test
    public void testToString() {
        String toStringResult = achat.toString();
        assertTrue(toStringResult.contains("ACH001"));
        assertTrue(toStringResult.contains("ORDONNANCE"));
    }

    @Test
    public void testClientSansMutuelle() {
        Client clientSansMutuelle = new Client("Durand", "Paul", "14 Rue de Marseille", "13000", "Marseille",
                "0987654321", "paul.durand@example.com", "CL002",
                "197035767242191", null, null);
        Achat achatSansMutuelle = new Achat(dateTransaction, clientSansMutuelle, pharmacien, "ACH002", TypeAchat.DIRECT, new ArrayList<>(), new HashMap<>());
        assertEquals(0.0, achatSansMutuelle.getMontantRembourse(), 0.001);
    }
}