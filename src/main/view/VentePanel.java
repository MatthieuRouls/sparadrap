package main.view;

import main.controller.PharmacieController;
import main.model.Medicament.Medicament;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel complet pour la gestion des ventes
 */
public class VentePanel extends JPanel {
    private final PharmacieController controller;

    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(0, 62, 28);
    private static final Color SECONDARY_COLOR = new Color(117, 187, 153);
    private static final Color ACCENT_COLOR = new Color(217, 243, 228);
    private static final Color ERROR_COLOR = new Color(187, 45, 12);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(1, 17, 9);

    // Composants principaux
    private JTabbedPane tabbedPane;
    private VenteDirectePanel venteDirectePanel;
    private VenteOrdonnancePanel venteOrdonnancePanel;
    private HistoriqueVentePanel historiquePanel;

    public VentePanel(PharmacieController controller) {
        this.controller = controller;
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Créer les onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(BACKGROUND_COLOR);

        // Panels des différents types de vente
        venteDirectePanel = new VenteDirectePanel(controller);
        venteOrdonnancePanel = new VenteOrdonnancePanel(controller);
        historiquePanel = new HistoriqueVentePanel(controller);

        // Ajouter les onglets
        tabbedPane.addTab("💳 Vente Directe", venteDirectePanel);
        tabbedPane.addTab("📋 Vente sur Ordonnance", venteOrdonnancePanel);
        tabbedPane.addTab("📊 Historique", historiquePanel);
    }

    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Ventes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Panel pour les ventes directes (sans ordonnance)
     */
    private class VenteDirectePanel extends JPanel {
        private final PharmacieController controller;

        private JTextField clientIdField;
        private JTextField medicamentField;
        private JSpinner quantiteSpinner;
        private JTable panierTable;
        private DefaultTableModel panierModel;
        private JLabel totalLabel;

        private Map<Medicament, Integer> panier = new HashMap<>();
        private Client clientSelectionne;

        public VenteDirectePanel(PharmacieController controller) {
            this.controller = controller;
            initializeComponents();
            setupLayout();
            setupEventListeners();
        }

        private void initializeComponents() {
            setLayout(new BorderLayout());
            setBackground(BACKGROUND_COLOR);

            // Champs de saisie
            clientIdField = new JTextField(15);
            medicamentField = new JTextField(15);
            quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

            // Table du panier
            String[] colonnes = {"Médicament", "Prix unitaire", "Quantité", "Sous-total"};
            panierModel = new DefaultTableModel(colonnes, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            panierTable = new JTable(panierModel);
            panierTable.setRowHeight(25);

            // Label total
            totalLabel = new JLabel("Total: 0.00 €");
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            totalLabel.setForeground(PRIMARY_COLOR);
        }

        private void setupLayout() {
            // Panel de sélection client
            JPanel clientPanel = createSectionPanel("👤 Client", createClientSelectionPanel());

            // Panel de sélection médicaments
            JPanel medicamentPanel = createSectionPanel("💊 Ajouter au panier", createMedicamentSelectionPanel());

            // Panel du panier
            JPanel panierPanel = createSectionPanel("🛒 Panier", createPanierPanel());

            // Panel des boutons de validation
            JPanel actionPanel = createActionPanel();

            // Layout principal
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setBackground(BACKGROUND_COLOR);
            leftPanel.add(clientPanel);
            leftPanel.add(Box.createVerticalStrut(15));
            leftPanel.add(medicamentPanel);
            leftPanel.add(Box.createVerticalStrut(15));
            leftPanel.add(actionPanel);

            add(leftPanel, BorderLayout.WEST);
            add(panierPanel, BorderLayout.CENTER);
        }

        private JPanel createClientSelectionPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.WHITE);

            panel.add(new JLabel("ID Client:"));
            panel.add(clientIdField);

            JButton rechercherClientBtn = createStyledButton("Rechercher", SECONDARY_COLOR);
            panel.add(rechercherClientBtn);

            JLabel clientInfoLabel = new JLabel("Aucun client sélectionné");
            clientInfoLabel.setForeground(Color.GRAY);
            panel.add(clientInfoLabel);

            return panel;
        }

        private JPanel createMedicamentSelectionPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.WHITE);

            panel.add(new JLabel("Médicament:"));
            panel.add(medicamentField);
            panel.add(new JLabel("Quantité:"));
            panel.add(quantiteSpinner);

            JButton ajouterBtn = createStyledButton("Ajouter", PRIMARY_COLOR);
            panel.add(ajouterBtn);

            return panel;
        }

        private JPanel createPanierPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                    new EmptyBorder(15, 15, 15, 15)
            ));

            JScrollPane scrollPane = new JScrollPane(panierTable);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            totalPanel.setBackground(Color.WHITE);
            totalPanel.add(totalLabel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(Color.WHITE);
            JButton retirerBtn = createStyledButton("Retirer sélection", ERROR_COLOR);
            JButton viderBtn = createStyledButton("Vider panier", ERROR_COLOR);
            buttonPanel.add(retirerBtn);
            buttonPanel.add(viderBtn);

            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(totalPanel, BorderLayout.SOUTH);
            panel.add(buttonPanel, BorderLayout.NORTH);

            return panel;
        }

        private JPanel createActionPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JButton validerBtn = createStyledButton("💳 Valider la vente", SUCCESS_COLOR);
            validerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            validerBtn.setPreferredSize(new Dimension(200, 40));

            JButton annulerBtn = createStyledButton("❌ Annuler", ERROR_COLOR);
            annulerBtn.setPreferredSize(new Dimension(120, 40));

            panel.add(validerBtn);
            panel.add(Box.createHorizontalStrut(20));
            panel.add(annulerBtn);

            return panel;
        }

        private void setupEventListeners() {
            // Implémentation des listeners pour la vente directe
            // À compléter avec la logique métier
        }
    }

    /**
     * Panel pour les ventes sur ordonnance
     */
    private class VenteOrdonnancePanel extends JPanel {
        private final PharmacieController controller;

        private JTextField clientIdField;
        private JTextField medecinIdField;
        private JTextField ordonnanceRefField;
        private JTable medicamentsTable;
        private DefaultTableModel medicamentsModel;

        public VenteOrdonnancePanel(PharmacieController controller) {
            this.controller = controller;
            initializeComponents();
            setupLayout();
        }

        private void initializeComponents() {
            setLayout(new BorderLayout());
            setBackground(BACKGROUND_COLOR);

            clientIdField = new JTextField(15);
            medecinIdField = new JTextField(15);
            ordonnanceRefField = new JTextField(15);

            String[] colonnes = {"Médicament", "Quantité prescrite", "Quantité disponible", "Quantité délivrée", "Prix"};
            medicamentsModel = new DefaultTableModel(colonnes, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3; // Seule la quantité délivrée est modifiable
                }
            };
            medicamentsTable = new JTable(medicamentsModel);
            medicamentsTable.setRowHeight(25);
        }

        private void setupLayout() {
            // Panel d'informations de l'ordonnance
            JPanel infoPanel = createSectionPanel("📋 Informations Ordonnance", createOrdonnanceInfoPanel());

            // Panel des médicaments prescrits
            JPanel medicamentsPanel = createSectionPanel("💊 Médicaments prescrits", createMedicamentsPanel());

            // Panel de validation
            JPanel validationPanel = createOrdonnanceActionPanel();

            add(infoPanel, BorderLayout.NORTH);
            add(medicamentsPanel, BorderLayout.CENTER);
            add(validationPanel, BorderLayout.SOUTH);
        }

        private JPanel createOrdonnanceInfoPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // Ligne 1
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Client ID:"), gbc);
            gbc.gridx = 1;
            panel.add(clientIdField, gbc);
            gbc.gridx = 2;
            panel.add(createStyledButton("🔍", SECONDARY_COLOR), gbc);

            // Ligne 2
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Médecin ID:"), gbc);
            gbc.gridx = 1;
            panel.add(medecinIdField, gbc);
            gbc.gridx = 2;
            panel.add(createStyledButton("🔍", SECONDARY_COLOR), gbc);

            // Ligne 3
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Réf. Ordonnance:"), gbc);
            gbc.gridx = 1;
            panel.add(ordonnanceRefField, gbc);

            return panel;
        }

        private JPanel createMedicamentsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(medicamentsTable);
            scrollPane.setPreferredSize(new Dimension(0, 200));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(createStyledButton("Ajouter médicament", PRIMARY_COLOR));
            buttonPanel.add(createStyledButton("Retirer sélection", ERROR_COLOR));

            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createOrdonnanceActionPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton validerBtn = createStyledButton("Valider ordonnance", SUCCESS_COLOR);
            validerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            validerBtn.setPreferredSize(new Dimension(200, 40));

            JButton annulerBtn = createStyledButton("Annuler", ERROR_COLOR);
            annulerBtn.setPreferredSize(new Dimension(120, 40));

            panel.add(validerBtn);
            panel.add(Box.createHorizontalStrut(20));
            panel.add(annulerBtn);

            return panel;
        }
    }

    /**
     * Panel pour l'historique des ventes
     */
    private class HistoriqueVentePanel extends JPanel {
        private final PharmacieController controller;

        private JTable historiqueTable;
        private DefaultTableModel historiqueModel;
        private JComboBox<String> filtreCombo;
        private JTextField rechercheField;

        public HistoriqueVentePanel(PharmacieController controller) {
            this.controller = controller;
            initializeComponents();
            setupLayout();
            setupEventListeners();
        }

        private void initializeComponents() {
            setLayout(new BorderLayout());
            setBackground(BACKGROUND_COLOR);

            // Table de l'historique
            String[] colonnes = {"Référence", "Date", "Client", "Type", "Montant", "Remboursé", "Net"};
            historiqueModel = new DefaultTableModel(colonnes, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            historiqueTable = new JTable(historiqueModel);
            historiqueTable.setRowHeight(25);
            historiqueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Composants de filtre
            filtreCombo = new JComboBox<>(new String[]{
                    "Toutes les ventes", "Aujourd'hui", "Cette semaine", "Ce mois", "Ventes directes", "Ordonnances"
            });
            rechercheField = new JTextField(15);
        }

        private void setupLayout() {
            // Panel de filtres
            JPanel filtrePanel = createFiltrePanel();

            // Panel de la table
            JScrollPane scrollPane = new JScrollPane(historiqueTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

            // Panel des statistiques
            JPanel statsPanel = createStatsPanel();

            // Panel principal
            JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
            mainPanel.setBackground(BACKGROUND_COLOR);
            mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

            mainPanel.add(filtrePanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(statsPanel, BorderLayout.SOUTH);

            add(mainPanel, BorderLayout.CENTER);
        }

        private JPanel createFiltrePanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(BACKGROUND_COLOR);

            panel.add(new JLabel("Filtre:"));
            panel.add(filtreCombo);
            panel.add(Box.createHorizontalStrut(20));
            panel.add(new JLabel("Rechercher:"));
            panel.add(rechercheField);
            panel.add(createStyledButton("🔍", SECONDARY_COLOR));
            panel.add(createStyledButton("🔄 Actualiser", PRIMARY_COLOR));

            return panel;
        }

        private JPanel createStatsPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(new Color(250, 250, 250));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    new EmptyBorder(10, 15, 10, 15)
            ));

            JLabel statsLabel = new JLabel("Statistiques: 0 ventes - Total: 0.00 € - Remboursé: 0.00 € - Net: 0.00 €");
            statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            statsLabel.setForeground(TEXT_COLOR);
            panel.add(statsLabel);

            return panel;
        }

        private void setupEventListeners() {
            filtreCombo.addActionListener(e -> appliquerFiltre());
            rechercheField.addActionListener(e -> effectuerRecherche());

            // Double-clic pour voir les détails d'une vente
            historiqueTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2 && historiqueTable.getSelectedRow() != -1) {
                        afficherDetailsVente();
                    }
                }
            });
        }

        private void appliquerFiltre() {
            String filtre = (String) filtreCombo.getSelectedItem();
            // Logique de filtrage à implémenter
            afficherMessage("Filtre appliqué: " + filtre, false);
        }

        private void effectuerRecherche() {
            String terme = rechercheField.getText().trim();
            if (!terme.isEmpty()) {
                // Logique de recherche à implémenter
                afficherMessage("Recherche: " + terme, false);
            }
        }

        private void afficherDetailsVente() {
            int selectedRow = historiqueTable.getSelectedRow();
            if (selectedRow >= 0) {
                String reference = (String) historiqueModel.getValueAt(selectedRow, 0);
                // Créer et afficher un dialog avec les détails
                new DetailVenteDialog(SwingUtilities.getWindowAncestor(this), controller, reference).setVisible(true);
            }
        }
    }

    /**
     * Dialog pour afficher les détails d'une vente
     */
    private class DetailVenteDialog extends JDialog {
        public DetailVenteDialog(Window parent, PharmacieController controller, String reference) {
            super(parent, "Détails de la vente - " + reference, ModalityType.APPLICATION_MODAL);
            initializeDialog();
        }

        private void initializeDialog() {
            setSize(600, 400);
            setLocationRelativeTo(getOwner());

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            // Informations générales
            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createTitledBorder("Informations générales"));
            // Ajouter les informations de la vente ici

            // Détail des médicaments
            JPanel medicamentsPanel = new JPanel(new BorderLayout());
            medicamentsPanel.setBorder(BorderFactory.createTitledBorder("Médicaments vendus"));
            // Ajouter la table des médicaments ici

            // Boutons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton reimprimer = createStyledButton("🖨️ Réimprimer", SECONDARY_COLOR);
            JButton fermer = createStyledButton("Fermer", PRIMARY_COLOR);
            fermer.addActionListener(e -> dispose());
            buttonPanel.add(reimprimer);
            buttonPanel.add(fermer);

            contentPanel.add(infoPanel, BorderLayout.NORTH);
            contentPanel.add(medicamentsPanel, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(contentPanel);
        }
    }

    // Méthodes utilitaires communes

    private JPanel createSectionPanel(String title, JPanel content) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BACKGROUND_COLOR);
        section.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        contentWrapper.add(content, BorderLayout.CENTER);

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(contentWrapper, BorderLayout.CENTER);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));

        return section;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
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

    private void afficherMessage(String message, boolean isError) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof PharmacieMainFrame) {
            ((PharmacieMainFrame) parent).showMessage(message, isError);
        }
    }

    // Méthodes publiques pour l'actualisation
    public void actualiserHistorique() {
        if (historiquePanel != null) {
            // Logique pour recharger l'historique
            afficherMessage("Historique actualisé", false);
        }
    }

    public void viderPanier() {
        if (venteDirectePanel != null) {
            venteDirectePanel.panier.clear();
            venteDirectePanel.panierModel.setRowCount(0);
            venteDirectePanel.totalLabel.setText("Total: 0.00 €");
            afficherMessage("Panier vidé", false);
        }
    }
}
