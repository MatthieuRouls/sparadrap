package test;

import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class MedicamentTest {
    private Medicament medicament;
    private Date dateMiseEnService;
    private Date datePeremption;

    @BeforeEach
    public void setUp() {
        dateMiseEnService = new Date();
        datePeremption = new Date(dateMiseEnService.getTime() + 31536000000L); // Ajoute 1 an en millisecondes
        medicament = new Medicament("Doliprane", CategorieMedicament.ANALGESIQUES, 5.99, 100, dateMiseEnService, datePeremption);
    }

    @Test
    public void testReduireStock() {
        medicament.reduireStock(20);
        assertEquals(80, medicament.getQuantiteStock());
    }

    @Test
    public void testReduireStockInsuffisant() {
        Medicament.StockInsuffisantException exception = assertThrows(Medicament.StockInsuffisantException.class, () -> {
            medicament.reduireStock(150);
        });
        assertTrue(exception.getMessage().contains("Stock insuffisant pour réduire de 150. Stock actuel : 100."));
    }

    @Test
    public void testReduireStockQuantiteNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            medicament.reduireStock(-10);
        });
        assertEquals("La quantité doit être positive.", exception.getMessage());
    }

    @Test
    public void testAugmenterStock() {
        medicament.augmenterStock(30);
        assertEquals(130, medicament.getQuantiteStock());
    }

    @Test
    public void testAugmenterStockQuantiteNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            medicament.augmenterStock(-10);
        });
        assertEquals("La quantité doit être positive.", exception.getMessage());
    }
}