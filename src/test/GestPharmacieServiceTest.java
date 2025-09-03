package test;

import main.model.Document.TypeDocument.Ordonnance;
import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;
import main.model.Medicament.TypeAchat;
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.Transaction.TypeTransaction.Achat;
import main.model.service.GestPharmacieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GestPharmacieServiceTest {
    private GestPharmacieService service;
    private Client client;
    private Medecin medecin;
    private Mutuelle mutuelle;
    private Medicament medicament1;
    private Medicament medicament2;
    private Achat achat;
    private Ordonnance ordonnance;
    private Map<Medicament, Integer> quantites;
    private List<Medicament> medicaments;

    @BeforeEach
    public void setUp() {
        service = new GestPharmacieService();

        client = new Client(
                "Martin", "Pierre", "12 Rue de Paris", "75000", "Paris",
                "0123456789", "pierre.martin@example.com", "CL001",
                "123456789012345", null, null
        );

        medecin = new Medecin(
                "Dupont", "Jean", "10 Rue des Docteurs", "75000", "Paris",
                "0123456789", "jean.dupont@example.com", "MED001",
                "AGR123456789"
        );

        mutuelle = new Mutuelle(
                "Mutuelle Générale", "75000", "Paris", "0123456789", 70.0
        );

        medicament1 = new Medicament(
                "Doliprane", CategorieMedicament.ANALGESIQUES, 5.99, 100, new Date(), new Date()
        );

        medicament2 = new Medicament(
                "Amoxicilline", CategorieMedicament.ANTIBIOTIQUES, 10.99, 50, new Date(), new Date()
        );

        medicaments = new ArrayList<>();
        medicaments.add(medicament1);
        medicaments.add(medicament2);

        quantites = new HashMap<>();
        quantites.put(medicament1, 2);
        quantites.put(medicament2, 1);

        achat = new Achat(
                new Date(), client, null, "ACH001", TypeAchat.ORDONNANCE, medicaments, quantites
        );

        ordonnance = new Ordonnance(
                new Date(), medecin, client, medicaments, quantites, "ORD001"
        );
    }

    @Test
    public void testAjouterClient() {
        service.ajouterClient(client);
        assertNotNull(service.rechercherClient("CL001"));
    }

    @Test
    public void testModifierClient() {
        service.ajouterClient(client);
        Client newClient = new Client(
                "Martin", "Pierre", "15 Rue de Paris", "75000", "Paris",
                "0123456789", "pierre.martin@example.com", "CL001",
                "123456789012345", null, null
        );
        service.modifierClient(newClient);
        assertEquals("15 Rue de Paris", service.rechercherClient("CL001").getAdresse());
    }

    @Test
    public void testSupprimerClient() {
        service.ajouterClient(client);
        service.supprimerClient("CL001");
        assertNull(service.rechercherClient("CL001"));
    }

    @Test
    public void testAjouterMedecin() {
        service.ajouterMedecin(medecin);
        assertNotNull(service.rechercherMedecin("MED001"));
    }

    @Test
    public void testAjouterMutuelle() {
        service.ajouterMutuelle(mutuelle);
        assertNotNull(service.rechercherMutuelle("Mutuelle Générale"));
    }

    @Test
    public void testEnregistrerAchat() {
        service.enregistrerAchat(achat);
        List<Achat> achats = service.getAchatsParPeriode(new Date(0), new Date());
        assertEquals(1, achats.size());
    }

    @Test
    public void testEnregistrerOrdonnance() {
        service.enregistrerOrdonnance(ordonnance);
        List<Ordonnance> ordonnances = service.getOrdonnancesParClient(client);
        assertEquals(1, ordonnances.size());
    }

    @Test
    public void testCalculerChiffreAffaires() {
        service.enregistrerAchat(achat);
        double chiffreAffaires = service.calculerChiffreAffaires(new Date(0), new Date());
        assertEquals(achat.getMontantTotal(), chiffreAffaires, 0.001);
    }
}