package main.view;

import main.controller.PharmacieController;
import main.model.Medicament.CategorieMedicament;
import main.model.Medicament.Medicament;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * Panel de gestion des médicaments
 */
public class MedicamentPanel extends JPanel implements DataRefreshListener {
    private final PharmacieController controller;

    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(0, 62, 28);
    private static final Color SECONDARY_COLOR = new Color(0, 62, 28);
    private static final Color ACCENT_COLOR = new Color(0, 62, 28);
    private static final Color ERROR_COLOR = new Color(163, 35, 43);
    private static final Color WARNING_COLOR = new Color(163, 35, 43);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(0, 37, 15);

    // Composants
    private JTextField rechercheField;
    private JTable medicamentsTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JButton ajouterBtn, modifierStockBtn, rechercherBtn, inventaireBtn;
    private PharmacieMainFrame mainFrame;

    // Formulaire
    private JTextField nomField, prixField, stockField;
    private JComboBox<CategorieMedicament> categorieCombo;
    private JFormattedTextField datePeremptionField;

    /**
     * Construit le panel de gestion des médicaments et branche l'écoute des mises à jour de stock.
     */
    public MedicamentPanel(PharmacieController controller, PharmacieMainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupStyles();
        chargerInventaire();
        // Écouter les mises à jour de stock déclenchées par les ventes
        DataEventManager.getInstance().addListener(this);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Champ de recherche
        rechercheField = new JTextField(20);
        rechercheField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rechercherBtn = createStyledButton("icons/search1.png", "Rechercher", SECONDARY_COLOR);

        // Table des médicaments
        String[] colonnes = {"Nom", "Catégorie", "Prix", "Stock", "Date Péremption", "Statut"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicamentsTable = new JTable(tableModel);
        medicamentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicamentsTable.setRowHeight(25);
        medicamentsTable.getTableHeader().setReorderingAllowed(false);

        // Boutons d'action
        ajouterBtn = createStyledButton("icons/add-document.png", "Ajouter", ACCENT_COLOR);
        modifierStockBtn = createStyledButton("icons/edit1.png", "Modifier Stock", WARNING_COLOR);
        inventaireBtn = createStyledButton("icons/list.png", "Inventaire Complet", PRIMARY_COLOR);

        // Panel de détails/formulaire
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Champs du formulaire
        nomField = createStyledTextField();
        prixField = createStyledTextField();
        stockField = createStyledTextField();
        categorieCombo = new JComboBox<>(CategorieMedicament.values());

        // Utiliser un DateFormatter pour que setValue accepte un java.util.Date
        datePeremptionField = new JFormattedTextField();
        javax.swing.text.DateFormatter dateFormatter = new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy"));
        datePeremptionField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(dateFormatter));
        datePeremptionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        datePeremptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void setupLayout() {
        // Panel du haut
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Médicaments");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel recherchePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        recherchePanel.setBackground(BACKGROUND_COLOR);
        recherchePanel.add(new JLabel("Rechercher : "));
        recherchePanel.add(rechercheField);
        recherchePanel.add(rechercherBtn);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(recherchePanel, BorderLayout.EAST);

        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);

        // Table avec scroll
        JScrollPane tableScrollPane = new JScrollPane(medicamentsTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 0));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // Panel des boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.add(ajouterBtn);
        buttonsPanel.add(modifierStockBtn);
        buttonsPanel.add(inventaireBtn);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.add(buttonsPanel, BorderLayout.NORTH);
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Setup du formulaire
        setupFormulaire();

        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(detailsPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupFormulaire() {
        detailsPanel.removeAll();

        JLabel formTitle = new JLabel("Informations Médicament");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(TEXT_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(formTitle);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Nom
        addFormField(formPanel, gbc, "Nom :", nomField, 0);

        // Catégorie
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Catégorie :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        categorieCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(categorieCombo, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        // Prix et Stock
        addFormField(formPanel, gbc, "Prix (€) :", prixField, 2);
        addFormField(formPanel, gbc, "Stock :", stockField, 3);

        // Date de péremption
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Péremption :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(datePeremptionField, gbc);

        detailsPanel.add(formPanel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Note d'information
        JTextArea noteArea = new JTextArea(
                "📝 Note :\n" +
                        "• La date de péremption doit être au format JJ/MM/AAAA\n" +
                        "• Le prix doit être un nombre décimal (ex: 12.50)\n" +
                        "• Le stock doit être un nombre entier"
        );
        noteArea.setBackground(new Color(245, 245, 245));
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        noteArea.setEditable(false);
        noteArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        noteArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(noteArea);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Boutons du formulaire
        JPanel formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formButtonsPanel.setBackground(Color.WHITE);
        formButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton sauvegarderBtn = createStyledButton("icons/disk.png", "Sauvegarder", ACCENT_COLOR);
        JButton annulerBtn = createStyledButton("icons/trash.png", "Annuler", ERROR_COLOR);

        formButtonsPanel.add(sauvegarderBtn);
        formButtonsPanel.add(annulerBtn);

        detailsPanel.add(formButtonsPanel);

        detailsPanel.setPreferredSize(new Dimension(300, 0));
        detailsPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(field, gbc);

        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
    }

    private void setupEventListeners() {
        // Recherche
        rechercherBtn.addActionListener(e -> rechercherMedicament());
        rechercheField.addActionListener(e -> rechercherMedicament());

        // Actions sur la table
        ajouterBtn.addActionListener(e -> nouveauMedicament());
        modifierStockBtn.addActionListener(e -> modifierStock());
        inventaireBtn.addActionListener(e -> chargerInventaire());

        // Sélection dans la table
        medicamentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chargerMedicamentSelectionne();
            }
        });

        // Boutons du formulaire
        Component[] components = detailsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component subComp : panel.getComponents()) {
                    if (subComp instanceof JButton) {
                        JButton btn = (JButton) subComp;
                        if (btn.getText().contains("Sauvegarder")) {
                            btn.addActionListener(e -> sauvegarderMedicament());
                        } else if (btn.getText().contains("Annuler")) {
                            btn.addActionListener(e -> annulerSaisie());
                        }
                    }
                }
            }
        }
    }

    private void setupStyles() {
        medicamentsTable.setGridColor(new Color(230, 230, 230));
        medicamentsTable.setShowGrid(true);
        medicamentsTable.setIntercellSpacing(new Dimension(1, 1));
        medicamentsTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
        medicamentsTable.getTableHeader().setBackground(new Color(250, 250, 250));

        // Style pour la combo box
        categorieCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    /**
     * Place le focus dans le champ de recherche médicaments.
     */
    public void focusRecherche() {
        SwingUtilities.invokeLater(() -> {
            if (rechercheField != null) {
                rechercheField.requestFocusInWindow();
            }
        });
    }

    private JButton createStyledButton(String iconPath, String text, Color color) {
        JButton button = new JButton(text);

        ImageIcon originalIcon = new ImageIcon(iconPath);
        Image img = originalIcon.getImage();
        Image scaleImg = img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaleImg);

        button.setIcon(icon);

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Style initial : fond transparent avec contour coloré
        button.setForeground(color);           // Texte de la couleur du bouton
        button.setBackground(Color.WHITE);     // Fond blanc/transparent
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),  // Contour coloré de 2px
                new EmptyBorder(6, 6, 6, 6)               // Espacement interne
        ));

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);     // Permettre le remplissage du fond

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Au survol : fond coloré, texte blanc
                button.setBackground(color);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.darker(), 1),
                        new EmptyBorder(6, 6, 6, 6)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Retour à l'état initial : fond transparent, texte coloré
                button.setBackground(Color.WHITE);
                button.setForeground(color);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 1),
                        new EmptyBorder(6, 6, 6, 6)
                ));
            }
        });

        return button;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private void chargerInventaire() {
        tableModel.setRowCount(0);
        Collection<Medicament> medicaments = controller.getInventaire();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date maintenant = new Date();

        for (Medicament med : medicaments) {
            String statut = "✅ OK";
            if (med.getQuantiteStock() == 0) {
                statut = "❌ Rupture";
            } else if (med.getQuantiteStock() < 10) {
                statut = "⚠️ Stock bas";
            } else if (med.getDatePeremption().before(maintenant)) {
                statut = "🚫 Périmé";
            } else if (med.getDatePeremption().before(new Date(maintenant.getTime() + 30L * 24 * 60 * 60 * 1000))) {
                statut = "⏰ Expire bientôt";
            }

            Object[] row = {
                    med.getNom(),
                    med.getCategorie(),
                    String.format("%.2f €", med.getPrix()),
                    med.getQuantiteStock(),
                    dateFormat.format(med.getDatePeremption()),
                    statut
            };
            tableModel.addRow(row);
        }

        afficherMessage("Inventaire chargé (" + medicaments.size() + " médicaments)", false);
    }

    private void rechercherMedicament() {
        String nom = rechercheField.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un nom de médicament", "Recherche", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Medicament> medicament = controller.rechercherMedicament(nom);
        if (medicament.isPresent()) {
            tableModel.setRowCount(0);
            ajouterMedicamentATable(medicament.get());
            afficherMessage("Médicament trouvé", false);
        } else {
            afficherMessage("Médicament non trouvé", true);
        }
    }

    private void ajouterMedicamentATable(Medicament med) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date maintenant = new Date();
        String statut = "✅ OK";

        if (med.getQuantiteStock() == 0) {
            statut = "❌ Rupture";
        } else if (med.getQuantiteStock() < 10) {
            statut = "⚠️ Stock bas";
        } else if (med.getDatePeremption().before(maintenant)) {
            statut = "🚫 Périmé";
        } else if (med.getDatePeremption().before(new Date(maintenant.getTime() + 30L * 24 * 60 * 60 * 1000))) {
            statut = "⏰ Expire bientôt";
        }

        Object[] row = {
                med.getNom(),
                med.getCategorie(),
                String.format("%.2f €", med.getPrix()),
                med.getQuantiteStock(),
                dateFormat.format(med.getDatePeremption()),
                statut
        };
        tableModel.addRow(row);
    }

    private void chargerMedicamentSelectionne() {
        int selectedRow = medicamentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String nom = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Medicament> medicamentOpt = controller.rechercherMedicament(nom);

            if (medicamentOpt.isPresent()) {
                Medicament medicament = medicamentOpt.get();
                remplirFormulaire(medicament);
            }
        }
    }

    private void remplirFormulaire(Medicament medicament) {
        nomField.setText(medicament.getNom());
        categorieCombo.setSelectedItem(medicament.getCategorie());
        prixField.setText(String.valueOf(medicament.getPrix()));
        stockField.setText(String.valueOf(medicament.getQuantiteStock()));

        datePeremptionField.setValue(medicament.getDatePeremption());
    }

    private void viderFormulaire() {
        nomField.setText("");
        categorieCombo.setSelectedIndex(0);
        prixField.setText("");
        stockField.setText("");
        datePeremptionField.setValue(null);
    }

    private void nouveauMedicament() {
        viderFormulaire();
        nomField.setEnabled(true);
    }

    private void sauvegarderMedicament() {
        try {
            String nom = nomField.getText().trim();
            if (nom.isEmpty()) {
                afficherMessage("Veuillez saisir un nom", true);
                return;
            }
            CategorieMedicament categorie = (CategorieMedicament) categorieCombo.getSelectedItem();
            double prix;
            try {
                prix = Double.parseDouble(prixField.getText().trim());
            } catch (NumberFormatException e) {
                afficherMessage("Prix invalide", true);
                return;
            }
            int stock;
            try {
                stock = Integer.parseInt(stockField.getText().trim());
            } catch (NumberFormatException e) {
                afficherMessage("Stock invalide", true);
                return;
            }
            Date datePeremption;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateStr = datePeremptionField.getText().trim();
                if (dateStr.isEmpty()) {
                    afficherMessage("Veuillez saisir une date de péremption", true);
                    return;
                }
                datePeremption = dateFormat.parse(dateStr);
            } catch (Exception e) {
                afficherMessage("Date de péremption invalide (format: JJ/MM/AAAA)", true);
                return;
            }
            Date maintenant = new Date();
            Medicament medicament = new Medicament(nom, categorie, prix, stock, maintenant, datePeremption);
            String resultat = controller.ajouterMedicament(medicament);
            boolean isError = resultat.contains("Erreur");
            afficherMessage(resultat, isError);
            if (!isError) {
                viderFormulaire();
                chargerInventaire();
                // Notifier l'accueil et autres listeners du stock
                DataEventManager.MedicamentEvents.stockUpdated();
            }
        } catch (Exception e) {
            afficherMessage("Erreur : " + e.getMessage(), true);
        }
    }

    private void modifierStock() {
        int selectedRow = medicamentsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner un médicament", true);
            return;
        }

        String nom = (String) tableModel.getValueAt(selectedRow, 0);
        String stockActuel = String.valueOf(tableModel.getValueAt(selectedRow, 3));

        String input = JOptionPane.showInputDialog(
                this,
                "Stock actuel : " + stockActuel + "\nNouveau stock :",
                "Modifier Stock - " + nom,
                JOptionPane.QUESTION_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                int nouveauStock = Integer.parseInt(input.trim());
                String resultat = controller.modifierStockMedicament(nom, nouveauStock);
                boolean isError = resultat.contains("Erreur");
                afficherMessage(resultat, isError);

                if (!isError) {
                    chargerInventaire();
                    // Notifier l'accueil et autres listeners du stock
                    DataEventManager.MedicamentEvents.stockUpdated();
                }
            } catch (NumberFormatException e) {
                afficherMessage("Stock invalide", true);
            }
        }
    }

    private void annulerSaisie() {
        viderFormulaire();
        nomField.setEnabled(true);
        medicamentsTable.clearSelection();
    }

    private void afficherMessage(String message, boolean isError) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof PharmacieMainFrame) {
            ((PharmacieMainFrame) parent).showMessage(message, isError);
        }
    }

    // ================= DataRefreshListener =================
    @Override
    public void refreshClientCount() { /* sans objet ici */ }

    @Override
    public void refreshMedecinCount() { /* sans objet ici */ }

    @Override
    public void refreshStockCount() {
        SwingUtilities.invokeLater(this::chargerInventaire);
    }

    @Override
    public void refreshVenteCount(int nombreVentes) { /* sans objet ici */ }

    @Override
    public void refreshCaCount(String caValue) { /* sans objet ici */ }
}
