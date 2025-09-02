package test;

import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedecinTest {
    private Medecin medecin;
    private Client client1;
    private Client client2;

    @BeforeEach
    public void setUp() {
        medecin = new Medecin(
                "Dupont", "Jean", "10 Rue des Docteurs", "75000", "Paris",
                "0123456789", "jean.dupont@example.com", "MED001",
                "AGR123456789"
        );

        client1 = new Client(
                "Martin", "Pierre", "12 Rue de Paris", "75000", "Paris",
                "0123456789", "pierre.martin@example.com", "CL001",
                "123456789012345", null, null
        );

        client2 = new Client(
                "Bernard", "Marie", "15 Rue de Lyon", "69000", "Lyon",
                "0987654321", "marie.bernard@example.com", "CL002",
                "987654321098765", null, null
        );
    }

    @Test
    public void testAjouterPatient() {
        medecin.ajouterPatient(client1);
        assertEquals(1, medecin.getPatients().size());
        assertTrue(medecin.getPatients().contains(client1));
    }

    @Test
    public void testRetirerPatient() {
        medecin.ajouterPatient(client1);
        medecin.retirerPatient(client1);
        assertTrue(medecin.getPatients().isEmpty());
    }

    @Test
    public void testAjouterPatientEnDouble() {
        medecin.ajouterPatient(client1);
        medecin.ajouterPatient(client1);
        assertEquals(1, medecin.getPatients().size());
    }
}