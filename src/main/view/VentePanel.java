package main.view;

import main.controller.PharmacieController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VentePanel extends JPanel {
    private final PharmacieController controller;

    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public VentePanel(PharmacieController controller) {
        this.controller = controller;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Panel principal centré
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Titre
        JLabel titleLabel = new JLabel("Gestion des Ventes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Message temporaire
        JLabel messageLabel = new JLabel("🚧 Module en cours de développement");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Cards avec options
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(BACKGROUND_COLOR);
        cardsPanel.setMaximumSize(new Dimension(600, 300));

        // Card - Vente directe
        JPanel venteDirecteCard = createOptionCard(
                "💳", "Vente Directe",
                "Vendre des médicaments sans ordonnance",
                PRIMARY_COLOR
        );

        // Card - Vente sur ordonnance
        JPanel venteOrdonnanceCard = createOptionCard(
                "📋", "Vente sur Ordonnance",
                "Traiter une prescription médicale",
                ACCENT_COLOR
        );

        // Card - Historique
        JPanel historiqueCard = createOptionCard(
                "📊", "Historique des Ventes",
                "Consulter les ventes précédentes",
                SECONDARY_COLOR
        );

        // Card - Recherche
        JPanel rechercheCard = createOptionCard(
                "🔍", "Rechercher une Vente",
                "Retrouver une transaction spécifique",
                new Color(155, 89, 182)
        );

        cardsPanel.add(venteDirecteCard);
        cardsPanel.add(venteOrdonnanceCard);
        cardsPanel.add(historiqueCard);
        cardsPanel.add(rechercheCard);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(cardsPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createOptionCard(String icon, String title, String description, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 20, 25, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        iconLabel.setForeground(color);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);

        // Effet hover
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                afficherMessage("Fonctionnalité '" + title + "' en cours de développement", false);
            }
        });

        return card;
    }

    private void afficherMessage(String message, boolean isError) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof PharmacieMainFrame) {
            ((PharmacieMainFrame) parent).showMessage(message, isError);
        }
    }
}
