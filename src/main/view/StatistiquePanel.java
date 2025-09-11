package main.view;

import main.controller.PharmacieController;
import main.view.PharmacieMainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Panel des statistiques
 */
public class StatistiquePanel extends JPanel {
    private final PharmacieController controller;

    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    // Composants pour afficher les stats
    private JLabel caJourLabel, caMoisLabel, caAnneeLabel;
    private JLabel nbVentesLabel, stockTotalLabel, rupturesLabel;
    private JButton actualiserBtn, exporterBtn;

    public StatistiquePanel(PharmacieController controller) {
        this.controller = controller;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        actualiserStatistiques();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Labels pour les stats
        caJourLabel = new JLabel("0.00 €");
        caMoisLabel = new JLabel("0.00 €");
        caAnneeLabel = new JLabel("0.00 €");
        nbVentesLabel = new JLabel("0");
        stockTotalLabel = new JLabel("0");
        rupturesLabel = new JLabel("0");

        // Boutons
        actualiserBtn = createStyledButton("🔄 Actualiser", SECONDARY_COLOR);
        exporterBtn = createStyledButton("📊 Exporter", PRIMARY_COLOR);
    }

    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Tableau de Bord & Statistiques");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(actualiserBtn);
        buttonPanel.add(exporterBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Statistiques principales
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Cartes de statistiques
        statsPanel.add(createStatCard("📈", "CA Aujourd'hui", caJourLabel, ACCENT_COLOR));
        statsPanel.add(createStatCard("📊", "CA Ce Mois", caMoisLabel, PRIMARY_COLOR));
        statsPanel.add(createStatCard("💰", "CA Cette Année", caAnneeLabel, SECONDARY_COLOR));
        statsPanel.add(createStatCard("🛒", "Ventes (30j)", nbVentesLabel, new Color(155, 89, 182)));
        statsPanel.add(createStatCard("📦", "Stock Total", stockTotalLabel, WARNING_COLOR));
        statsPanel.add(createStatCard("⚠️", "Ruptures", rupturesLabel, ERROR_COLOR));

        // Panel d'informations détaillées
        JPanel detailsPanel = createDetailsPanel();

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        mainPanel.add(detailsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String icon, String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);

        return card;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel detailsTitle = new JLabel("Informations Détaillées");
        detailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsTitle.setForeground(TEXT_COLOR);

        JTextArea detailsArea = new JTextArea(8, 50);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(248, 249, 250));
        detailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setBorder(null);

        panel.add(detailsTitle, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void setupEventListeners() {
        actualiserBtn.addActionListener(e -> actualiserStatistiques());
        exporterBtn.addActionListener(e -> exporterStatistiques());
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void actualiserStatistiques() {
        try {
            Date maintenant = new Date();

            // Calculer les différentes périodes
            Calendar cal = Calendar.getInstance();

            // Aujourd'hui
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date debutJour = cal.getTime();

            // Ce mois
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date debutMois = cal.getTime();

            // Cette année
            cal.set(Calendar.MONTH, 0);
            Date debutAnnee = cal.getTime();

            // 30 derniers jours
            Date debut30Jours = new Date(maintenant.getTime() - 30L * 24 * 60 * 60 * 1000);

            // Obtenir les statistiques du contrôleur
            Map<String, Object> statsJour = controller.obtenirStatistiques(debutJour, maintenant);
            Map<String, Object> statsMois = controller.obtenirStatistiques(debutMois, maintenant);
            Map<String, Object> statsAnnee = controller.obtenirStatistiques(debutAnnee, maintenant);
            Map<String, Object> stats30J = controller.obtenirStatistiques(debut30Jours, maintenant);

            // Mettre à jour l'affichage
            caJourLabel.setText(String.format("%.2f €", (Double) statsJour.getOrDefault("chiffreAffaires", 0.0)));
            caMoisLabel.setText(String.format("%.2f €", (Double) statsMois.getOrDefault("chiffreAffaires", 0.0)));
            caAnneeLabel.setText(String.format("%.2f €", (Double) statsAnnee.getOrDefault("chiffreAffaires", 0.0)));

            nbVentesLabel.setText(String.valueOf((Integer) stats30J.getOrDefault("nombreVentes", 0)));
            stockTotalLabel.setText(String.valueOf((Integer) statsJour.getOrDefault("stockTotal", 0)));
            rupturesLabel.setText(String.valueOf((Long) statsJour.getOrDefault("ruptureStock", 0L)));

            // Mettre à jour les détails
            updateDetailsArea(statsJour, statsMois, statsAnnee, stats30J);

            afficherMessage("Statistiques actualisées", false);

        } catch (Exception e) {
            afficherMessage("Erreur lors de l'actualisation : " + e.getMessage(), true);
        }
    }

    private void updateDetailsArea(Map<String, Object> statsJour, Map<String, Object> statsMois,
                                   Map<String, Object> statsAnnee, Map<String, Object> stats30J) {

        StringBuilder details = new StringBuilder();
        details.append("=== RAPPORT DÉTAILLÉ ===\n\n");

        details.append("📊 CHIFFRES D'AFFAIRES :\n");
        details.append(String.format("• Aujourd'hui : %.2f €\n", (Double) statsJour.getOrDefault("chiffreAffaires", 0.0)));
        details.append(String.format("• Ce mois : %.2f €\n", (Double) statsMois.getOrDefault("chiffreAffaires", 0.0)));
        details.append(String.format("• Cette année : %.2f €\n", (Double) statsAnnee.getOrDefault("chiffreAffaires", 0.0)));

        details.append("\n🛒 ACTIVITÉ DE VENTE :\n");
        details.append(String.format("• Ventes (30 derniers jours) : %d transactions\n",
                (Integer) stats30J.getOrDefault("nombreVentes", 0)));
        details.append(String.format("• Montant moyen par vente : %.2f €\n",
                calculateAverageTransaction(stats30J)));

        details.append("\n📦 GESTION DES STOCKS :\n");
        details.append(String.format("• Total des unités en stock : %d\n",
                (Integer) statsJour.getOrDefault("stockTotal", 0)));
        details.append(String.format("• Médicaments en rupture : %d\n",
                (Long) statsJour.getOrDefault("ruptureStock", 0L)));

        details.append("\n💰 REMBOURSEMENTS :\n");
        details.append(String.format("• Montant remboursé (30j) : %.2f €\n",
                (Double) stats30J.getOrDefault("montantRembourse", 0.0)));
        details.append(String.format("• Bénéfice net (30j) : %.2f €\n",
                (Double) stats30J.getOrDefault("beneficeNet", 0.0)));

        // Trouver la JTextArea dans le panel des détails
        findAndUpdateTextArea(details.toString());
    }

    private double calculateAverageTransaction(Map<String, Object> stats) {
        double ca = (Double) stats.getOrDefault("chiffreAffaires", 0.0);
        int nbVentes = (Integer) stats.getOrDefault("nombreVentes", 0);
        return nbVentes > 0 ? ca / nbVentes : 0.0;
    }

    private void findAndUpdateTextArea(String text) {
        // Parcourir les composants pour trouver la JTextArea
        Component[] components = getComponents();
        for (Component comp : components) {
            JTextArea textArea = findTextAreaRecursive(comp);
            if (textArea != null) {
                textArea.setText(text);
                textArea.setCaretPosition(0); // Remonter en haut
                return;
            }
        }
    }

    private JTextArea findTextAreaRecursive(Component comp) {
        if (comp instanceof JTextArea) {
            return (JTextArea) comp;
        }
        if (comp instanceof Container) {
            Container container = (Container) comp;
            for (Component child : container.getComponents()) {
                JTextArea result = findTextAreaRecursive(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private void exporterStatistiques() {
        afficherMessage("Fonctionnalité d'export en cours de développement", false);
    }

    private void afficherMessage(String message, boolean isError) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof PharmacieMainFrame) {
            ((PharmacieMainFrame) parent).showMessage(message, isError);
        }
    }
}
