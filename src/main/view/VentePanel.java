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
        private JButton rechercherClientBtn;
        private JLabel clientInfoLabel;
        private JButton ajouterBtn;
        private JButton retirerBtn;
        private JButton viderBtn;
        private JButton validerBtn;
        private JButton annulerBtn;

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

            rechercherClientBtn = createStyledButton("Rechercher", SECONDARY_COLOR);
            panel.add(rechercherClientBtn);

            clientInfoLabel = new JLabel("Aucun client sélectionné");
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

            ajouterBtn = createStyledButton("Ajouter", PRIMARY_COLOR);
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
            retirerBtn = createStyledButton("Retirer sélection", ERROR_COLOR);
            viderBtn = createStyledButton("Vider panier", ERROR_COLOR);
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

            validerBtn = createStyledButton("💳 Valider la vente", SUCCESS_COLOR);
            validerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            validerBtn.setPreferredSize(new Dimension(200, 40));

            annulerBtn = createStyledButton("❌ Annuler", ERROR_COLOR);
            annulerBtn.setPreferredSize(new Dimension(120, 40));

            panel.add(validerBtn);
            panel.add(Box.createHorizontalStrut(20));
            panel.add(annulerBtn);

            return panel;
        }

        private void setupEventListeners() {
            // Recherche client
            rechercherClientBtn.addActionListener(e -> {
                String id = clientIdField.getText().trim();
                if (id.isEmpty()) {
                    afficherMessage("Veuillez saisir l'identifiant client", true);
                    return;
                }
                Optional<Client> clientOpt = controller.rechercherClient(id);
                if (clientOpt.isPresent()) {
                    clientSelectionne = clientOpt.get();
                    clientInfoLabel.setText(clientSelectionne.getNom() + " " + clientSelectionne.getPrenom());
                    clientInfoLabel.setForeground(PRIMARY_COLOR);
                    afficherMessage("Client sélectionné", false);
                } else {
                    clientSelectionne = null;
                    clientInfoLabel.setText("Client introuvable");
                    clientInfoLabel.setForeground(ERROR_COLOR);
                    afficherMessage("Client non trouvé", true);
                }
            });

            // Ajouter médicament au panier
            ajouterBtn.addActionListener(e -> {
                String nomMed = medicamentField.getText().trim();
                int quantite = (Integer) quantiteSpinner.getValue();
                if (nomMed.isEmpty()) {
                    afficherMessage("Saisissez un médicament", true);
                    return;
                }
                Optional<Medicament> medOpt = controller.rechercherMedicament(nomMed);
                if (medOpt.isEmpty()) {
                    afficherMessage("Médicament introuvable", true);
                    return;
                }
                Medicament medicament = medOpt.get();
                // Vérifier disponibilité
                int deja = panier.getOrDefault(medicament, 0);
                int demande = deja + quantite;
                if (!medicament.isDisponible(demande)) {
                    afficherMessage("Stock insuffisant (disponible: " + medicament.getQuantiteStock() + ")", true);
                    return;
                }
                panier.put(medicament, demande);
                rafraichirPanierTable();
                mettreAJourTotal();
                medicamentField.setText("");
                quantiteSpinner.setValue(1);
            });

            // Retirer sélection
            retirerBtn.addActionListener(e -> {
                int row = panierTable.getSelectedRow();
                if (row < 0) {
                    afficherMessage("Sélectionnez un article à retirer", true);
                    return;
                }
                String nom = (String) panierModel.getValueAt(row, 0);
                // Trouver l'objet Medicament par nom dans le panier
                Medicament toRemove = null;
                for (Medicament m : panier.keySet()) {
                    if (m.getNom().equalsIgnoreCase(nom)) { toRemove = m; break; }
                }
                if (toRemove != null) {
                    panier.remove(toRemove);
                    rafraichirPanierTable();
                    mettreAJourTotal();
                }
            });

            // Vider panier
            viderBtn.addActionListener(e -> {
                panier.clear();
                rafraichirPanierTable();
                mettreAJourTotal();
            });

            // Valider vente
            validerBtn.addActionListener(e -> {
                if (clientSelectionne == null) {
                    afficherMessage("Sélectionnez un client", true);
                    return;
                }
                if (panier.isEmpty()) {
                    afficherMessage("Le panier est vide", true);
                    return;
                }
                // Construire map nom -> quantite
                Map<String, Integer> medicamentsQuantites = new HashMap<>();
                for (Map.Entry<Medicament, Integer> entry : panier.entrySet()) {
                    medicamentsQuantites.put(entry.getKey().getNom(), entry.getValue());
                }
                String resultat = controller.effectuerVenteDirecte(clientSelectionne.getIdentifiant(), medicamentsQuantites);
                boolean isError = resultat.toLowerCase().contains("erreur") || resultat.toLowerCase().contains("insuffisant");
                afficherMessage(resultat, isError);
                if (!isError) {
                    // Mettre à jour les compteurs via événements
                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        Date debutJour = cal.getTime();
                        Date maintenant = new Date();

                        Map<String, Object> statsJour = controller.obtenirStatistiques(debutJour, maintenant);
                        int nbVentes = (Integer) statsJour.getOrDefault("nombreVentes", 0);
                        double ca = (Double) statsJour.getOrDefault("chiffreAffaires", 0.0);
                        String caStr = String.format("%.2f €", ca);
                        DataEventManager.VenteEvents.venteCompleted(nbVentes, caStr);
                    } catch (Exception ex) {
                        // Ignorer les erreurs d'actualisation d'UI
                    }
                    // Rafraîchir stock
                    DataEventManager.MedicamentEvents.stockUpdated();
                    // Forcer aussi l'accueil à rafraîchir le stock immédiatement
                    DataEventManager.getInstance().fireEvent(DataEventManager.EventType.STOCK_UPDATED);
                    // Reset UI
                    panier.clear();
                    rafraichirPanierTable();
                    mettreAJourTotal();
                }
            });

            // Annuler
            annulerBtn.addActionListener(e -> {
                clientSelectionne = null;
                clientInfoLabel.setText("Aucun client sélectionné");
                clientInfoLabel.setForeground(Color.GRAY);
                clientIdField.setText("");
                panier.clear();
                rafraichirPanierTable();
                mettreAJourTotal();
                medicamentField.setText("");
                quantiteSpinner.setValue(1);
            });
        }

        private void rafraichirPanierTable() {
            panierModel.setRowCount(0);
            for (Map.Entry<Medicament, Integer> entry : panier.entrySet()) {
                Medicament m = entry.getKey();
                int qte = entry.getValue();
                double sousTotal = m.getPrix() * qte;
                panierModel.addRow(new Object[]{m.getNom(), String.format("%.2f €", m.getPrix()), qte, String.format("%.2f €", sousTotal)});
            }
        }

        private void mettreAJourTotal() {
            double total = 0.0;
            for (Map.Entry<Medicament, Integer> entry : panier.entrySet()) {
                total += entry.getKey().getPrix() * entry.getValue();
            }
            totalLabel.setText(String.format("Total: %.2f €", total));
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
        private JButton rechercherClientBtn;
        private JButton rechercherMedecinBtn;
        private JButton ajouterMedicamentBtn;
        private JButton retirerSelectionBtn;
        private JButton validerBtn;
        private JButton annulerBtn;

        public VenteOrdonnancePanel(PharmacieController controller) {
            this.controller = controller;
            initializeComponents();
            setupLayout();
            setupEventListeners();
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
            rechercherClientBtn = createStyledButton("🔍", SECONDARY_COLOR);
            panel.add(rechercherClientBtn, gbc);

            // Ligne 2
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Médecin ID:"), gbc);
            gbc.gridx = 1;
            panel.add(medecinIdField, gbc);
            gbc.gridx = 2;
            rechercherMedecinBtn = createStyledButton("🔍", SECONDARY_COLOR);
            panel.add(rechercherMedecinBtn, gbc);

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
            ajouterMedicamentBtn = createStyledButton("Ajouter médicament", PRIMARY_COLOR);
            retirerSelectionBtn = createStyledButton("Retirer sélection", ERROR_COLOR);
            buttonPanel.add(ajouterMedicamentBtn);
            buttonPanel.add(retirerSelectionBtn);

            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createOrdonnanceActionPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setBorder(new EmptyBorder(20, 0, 0, 0));

            validerBtn = createStyledButton("Valider ordonnance", SUCCESS_COLOR);
            validerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            validerBtn.setPreferredSize(new Dimension(200, 40));

            annulerBtn = createStyledButton("Annuler", ERROR_COLOR);
            annulerBtn.setPreferredSize(new Dimension(120, 40));

            panel.add(validerBtn);
            panel.add(Box.createHorizontalStrut(20));
            panel.add(annulerBtn);

            return panel;
        }

        private void setupEventListeners() {
            // Recherche client
            if (rechercherClientBtn != null) {
                rechercherClientBtn.addActionListener(e -> {
                    String id = clientIdField.getText().trim();
                    if (id.isEmpty()) { afficherMessage("Veuillez saisir l'identifiant client", true); return; }
                    Optional<Client> clientOpt = controller.rechercherClient(id);
                    afficherMessage(clientOpt.isPresent() ? "Client sélectionné" : "Client non trouvé", clientOpt.isEmpty());
                });
            }
            // Recherche médecin
            if (rechercherMedecinBtn != null) {
                rechercherMedecinBtn.addActionListener(e -> {
                    String id = medecinIdField.getText().trim();
                    if (id.isEmpty()) { afficherMessage("Veuillez saisir l'identifiant médecin", true); return; }
                    Optional<Medecin> medOpt = controller.rechercherMedecin(id);
                    afficherMessage(medOpt.isPresent() ? "Médecin sélectionné" : "Médecin non trouvé", medOpt.isEmpty());
                });
            }

            // Ajouter médicament
            if (ajouterMedicamentBtn != null) {
                ajouterMedicamentBtn.addActionListener(e -> {
                    String nom = JOptionPane.showInputDialog(this, "Nom du médicament :", "Ajouter médicament", JOptionPane.QUESTION_MESSAGE);
                    if (nom == null || nom.trim().isEmpty()) return;
                    String qteStr = JOptionPane.showInputDialog(this, "Quantité prescrite :", "1");
                    if (qteStr == null || qteStr.trim().isEmpty()) return;
                    int qtePrescrite;
                    try { qtePrescrite = Integer.parseInt(qteStr.trim()); } catch (Exception ex) { afficherMessage("Quantité invalide", true); return; }
                    Optional<Medicament> medOpt = controller.rechercherMedicament(nom.trim());
                    if (medOpt.isEmpty()) { afficherMessage("Médicament introuvable", true); return; }
                    Medicament m = medOpt.get();
                    int disponible = m.getQuantiteStock();
                    int delivree = Math.min(qtePrescrite, disponible);
                    medicamentsModel.addRow(new Object[]{m.getNom(), qtePrescrite, disponible, delivree, String.format("%.2f €", m.getPrix())});
                });
            }

            // Retirer sélection
            if (retirerSelectionBtn != null) {
                retirerSelectionBtn.addActionListener(e -> {
                    int row = medicamentsTable.getSelectedRow();
                    if (row >= 0) {
                        medicamentsModel.removeRow(row);
                    } else {
                        afficherMessage("Sélectionnez une ligne à retirer", true);
                    }
                });
            }

            // Valider ordonnance
            if (validerBtn != null) {
                validerBtn.addActionListener(e -> {
                    String clientId = clientIdField.getText().trim();
                    String medecinId = medecinIdField.getText().trim();
                    if (clientId.isEmpty() || medecinId.isEmpty()) { afficherMessage("Renseignez client et médecin", true); return; }
                    Map<String, Integer> delivre = new HashMap<>();
                    for (int i = 0; i < medicamentsModel.getRowCount(); i++) {
                        String nom = (String) medicamentsModel.getValueAt(i, 0);
                        Object qObj = medicamentsModel.getValueAt(i, 3);
                        int qte;
                        try {
                            qte = (qObj instanceof Integer) ? (Integer) qObj : Integer.parseInt(String.valueOf(qObj));
                        } catch (Exception ex) {
                            afficherMessage("Quantité délivrée invalide à la ligne " + (i+1), true);
                            return;
                        }
                        if (qte > 0) delivre.put(nom, qte);
                    }
                    if (delivre.isEmpty()) { afficherMessage("Aucune quantité délivrée", true); return; }
                    String res = controller.effectuerVenteOrdonnance(clientId, medecinId, delivre);
                    boolean isError = res.toLowerCase().contains("erreur") || res.toLowerCase().contains("insuffisant") || res.toLowerCase().contains("périmé");
                    if (isError) {
                        afficherMessage(res, true);
                    } else {
                        // Afficher un récapitulatif détaillé dans un dialog
                        JTextArea area = new JTextArea(res);
                        area.setEditable(false);
                        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        area.setBorder(new EmptyBorder(10, 10, 10, 10));
                        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Ordonnance validée", JOptionPane.INFORMATION_MESSAGE);
                        afficherMessage("Ordonnance validée", false);
                    }
                    if (!isError) {
                        // Rafraîchir stats/stock
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            Date debutJour = cal.getTime();
                            Date maintenant = new Date();
                            Map<String, Object> statsJour = controller.obtenirStatistiques(debutJour, maintenant);
                            int nbVentes = (Integer) statsJour.getOrDefault("nombreVentes", 0);
                            double ca = (Double) statsJour.getOrDefault("chiffreAffaires", 0.0);
                            String caStr = String.format("%.2f €", ca);
                            DataEventManager.VenteEvents.venteCompleted(nbVentes, caStr);
                        } catch (Exception ignored) {}
                        DataEventManager.MedicamentEvents.stockUpdated();
                        DataEventManager.getInstance().fireEvent(DataEventManager.EventType.STOCK_UPDATED);
                        // Reset
                        clientIdField.setText("");
                        medecinIdField.setText("");
                        ordonnanceRefField.setText("");
                        medicamentsModel.setRowCount(0);
                    }
                });
            }

            // Annuler
            if (annulerBtn != null) {
                annulerBtn.addActionListener(e -> {
                    clientIdField.setText("");
                    medecinIdField.setText("");
                    ordonnanceRefField.setText("");
                    medicamentsModel.setRowCount(0);
                    afficherMessage("Saisie annulée", false);
                });
            }
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
            String[] colonnes = {"Référence", "Date", "Heure", "Client", "Type", "Montant", "Remboursé", "Net"};
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
            filtreCombo.addActionListener(e -> rechargerHistorique());
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

        private void effectuerRecherche() {
            String terme = rechercheField.getText().trim();
            if (!terme.isEmpty()) {
                // recharger et sélectionner la ligne par référence
                rechargerHistorique();
                for (int i = 0; i < historiqueModel.getRowCount(); i++) {
                    if (terme.equals(historiqueModel.getValueAt(i, 0))) {
                        historiqueTable.setRowSelectionInterval(i, i);
                        historiqueTable.scrollRectToVisible(historiqueTable.getCellRect(i, 0, true));
                        break;
                    }
                }
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

        private void rechargerHistorique() {
            // Déterminer la période selon le filtre
            Calendar cal = Calendar.getInstance();
            Date fin = cal.getTime();
            String filtre = (String) filtreCombo.getSelectedItem();
            if ("Aujourd'hui".equals(filtre)) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else if ("Cette semaine".equals(filtre)) {
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else if ("Ce mois".equals(filtre)) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else {
                // Par défaut: dernier mois
                cal.add(Calendar.MONTH, -1);
            }
            Date debut = cal.getTime();

            java.util.List<main.model.Transaction.TypeTransaction.Achat> achats = controller.obtenirHistoriqueVentes(debut, fin);

            // Remplir le tableau
            historiqueModel.setRowCount(0);
            java.text.SimpleDateFormat dfDate = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.text.SimpleDateFormat dfHeure = new java.text.SimpleDateFormat("HH:mm:ss");
            for (main.model.Transaction.TypeTransaction.Achat a : achats) {
                String type = a.getType() == main.model.Medicament.TypeAchat.ORDONNANCE ? "Ordonnance" : "Directe";
                double total = a.getMontantTotal();
                double remb = a.getMontantRembourse();
                double net = total - remb;
                historiqueModel.addRow(new Object[]{
                        a.getReference(),
                        dfDate.format(a.getDateTransaction()),
                        dfHeure.format(a.getDateTransaction()),
                        a.getClient().getNom() + " " + a.getClient().getPrenom(),
                        type,
                        String.format("%.2f €", total),
                        String.format("%.2f €", remb),
                        String.format("%.2f €", net)
                });
            }
        }
    }

    /**
     * Dialog pour afficher les détails d'une vente
     */
    private class DetailVenteDialog extends JDialog {
        private final PharmacieController controller;
        private final String reference;

        public DetailVenteDialog(Window parent, PharmacieController controller, String reference) {
            super(parent, "Détails de la vente - " + reference, ModalityType.APPLICATION_MODAL);
            this.controller = controller;
            this.reference = reference;
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
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            Optional<main.model.Transaction.TypeTransaction.Achat> achatOpt = controller.rechercherAchatParReference(reference);
            if (achatOpt.isPresent()) {
                main.model.Transaction.TypeTransaction.Achat achat = achatOpt.get();
                java.text.SimpleDateFormat dfDate = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.text.SimpleDateFormat dfHeure = new java.text.SimpleDateFormat("HH:mm:ss");

                int y = 0;
                gbc.gridx = 0; gbc.gridy = y; infoPanel.add(new JLabel("Référence:"), gbc);
                gbc.gridx = 1; infoPanel.add(new JLabel(achat.getReference()), gbc); y++;

                gbc.gridx = 0; gbc.gridy = y; infoPanel.add(new JLabel("Date:"), gbc);
                gbc.gridx = 1; infoPanel.add(new JLabel(dfDate.format(achat.getDateTransaction())), gbc);
                gbc.gridx = 2; infoPanel.add(new JLabel("Heure:"), gbc);
                gbc.gridx = 3; infoPanel.add(new JLabel(dfHeure.format(achat.getDateTransaction())), gbc); y++;

                gbc.gridx = 0; gbc.gridy = y; infoPanel.add(new JLabel("Client:"), gbc);
                gbc.gridx = 1; infoPanel.add(new JLabel(achat.getClient().getNom() + " " + achat.getClient().getPrenom()), gbc); y++;

                gbc.gridx = 0; gbc.gridy = y; infoPanel.add(new JLabel("Type:"), gbc);
                String type = achat.getType() == main.model.Medicament.TypeAchat.ORDONNANCE ? "Ordonnance" : "Directe";
                gbc.gridx = 1; infoPanel.add(new JLabel(type), gbc); y++;

                gbc.gridx = 0; gbc.gridy = y; infoPanel.add(new JLabel("Montant total:"), gbc);
                gbc.gridx = 1; infoPanel.add(new JLabel(String.format("%.2f €", achat.getMontantTotal())), gbc); y++;

                gbc.gridx = 0; gbc.gridy = y; infoPanel.add(new JLabel("Remboursé:"), gbc);
                gbc.gridx = 1; infoPanel.add(new JLabel(String.format("%.2f €", achat.getMontantRembourse())), gbc); y++;

                gbc.gridx = 0; gbc.gridy = y; infoPanel.add(new JLabel("Net:"), gbc);
                gbc.gridx = 1; infoPanel.add(new JLabel(String.format("%.2f €", achat.getMontantTotal() - achat.getMontantRembourse())), gbc); y++;
            } else {
                infoPanel.add(new JLabel("Achat non trouvé"));
            }

            // Détail des médicaments
            JPanel medicamentsPanel = new JPanel(new BorderLayout());
            medicamentsPanel.setBorder(BorderFactory.createTitledBorder("Médicaments vendus"));
            String[] cols = {"Médicament", "Quantité", "Prix", "Variation stock"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
            JTable table = new JTable(model);
            table.setRowHeight(24);

            Optional<main.model.Transaction.TypeTransaction.Achat> achatOpt2 = controller.rechercherAchatParReference(reference);
            if (achatOpt2.isPresent()) {
                main.model.Transaction.TypeTransaction.Achat achat = achatOpt2.get();
                Map<main.model.Medicament.Medicament, Integer> qtes = achat.getQuantites();
                for (Map.Entry<main.model.Medicament.Medicament, Integer> e : qtes.entrySet()) {
                    main.model.Medicament.Medicament m = e.getKey();
                    int q = e.getValue();
                    model.addRow(new Object[]{m.getNom(), q, String.format("%.2f €", m.getPrix()), String.format("-%d", q)});
                }
            }
            medicamentsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

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
