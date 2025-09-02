package test;

import main.model.Organisme.TypeOrganisme.Mutuelle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MutuelleTest {
    private Mutuelle mutuelle;

    @BeforeEach
    public void setUp() {
        mutuelle = new Mutuelle("Mutuelle Générale", "75000", "Paris", "0123456789", 70.0);
    }

    @Test
    public void testConstructeurEtGetters() {
        assertEquals("Mutuelle Générale", mutuelle.getNom());
        assertEquals("75000", mutuelle.getCodePostal());
        assertEquals("Paris", mutuelle.getVille());
        assertEquals("0123456789", mutuelle.getTelephone());
        assertEquals(70.0, mutuelle.getTauxRemboursement());
    }

    @Test
    public void testSetters() {
        mutuelle.setNom("Nouvelle Mutuelle");
        mutuelle.setCodePostal("69000");
        mutuelle.setVille("Lyon");
        mutuelle.setTelephone("0987654321");
        mutuelle.setTauxRemboursement(80.0);

        assertEquals("Nouvelle Mutuelle", mutuelle.getNom());
        assertEquals("69000", mutuelle.getCodePostal());
        assertEquals("Lyon", mutuelle.getVille());
        assertEquals("0987654321", mutuelle.getTelephone());
        assertEquals(80.0, mutuelle.getTauxRemboursement());
    }

    @Test
    public void testToString() {
        String toStringResult = mutuelle.toString();
        assertTrue(toStringResult.contains("Mutuelle Générale"));
        assertTrue(toStringResult.contains("75000"));
        assertTrue(toStringResult.contains("Paris"));
        assertTrue(toStringResult.contains("70.0"));
    }
}