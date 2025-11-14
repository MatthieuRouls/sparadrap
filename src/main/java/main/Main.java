package main;

import javax.swing.SwingUtilities;
import main.view.PharmacieMainFrame;

/**
 * Point d'entrée de l'application.
 * Délègue l'exécution à l'interface graphique (PharmacieMainFrame).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PharmacieMainFrame frame = new PharmacieMainFrame();
            frame.setVisible(true);
        });
    }
}