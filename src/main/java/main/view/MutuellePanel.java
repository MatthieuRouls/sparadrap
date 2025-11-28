package main.view;

import main.controller.PharmacieController;
import main.model.Organisme.TypeOrganisme.Mutuelle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;

/**
 * Panel de gestion des mutuelles
 */
public class MutuellePanel extends JPanel {
    private final PharmacieController controller;

    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(0, 62, 28);
    private static final Color SECONDARY_COLOR = new Color(117, 187, 153);
    private static final Color ACCENT_COLOR = new Color(217, 243, 228);
    private static final Color ERROR_COLOR = new Color(187, 45, 12);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(1, 17, 9);

    // Composants
    private JTextField rechercheField;
    private JTable mutuellesTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JButton ajouterBtn, modifierBtn, supprimerBtn, rechercherBtn;

    // Formulaire
    private JTextField nomField, adresseField, codePostalField;
    private JTextField villeField, telephoneField, emailField, tauxField;
    private boolean editingMutuelle = false;
    private String currentMutuelleName = null;

    /**
     * Construit le panel de gestion des mutuelles (UI, événements, styles).
     */
    public MutuellePanel(PharmacieController controller) {
        this.controller = controller;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupStyles();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Champ de recherche
        rechercheField = new JTextField(20);
        rechercheField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rechercherBtn = createStyledButton("icons/search1.png", "Rechercher", PRIMARY_COLOR);

        // Table des mutuelles
        String[] colonnes = {"Nom", "Ville", "Téléphone", "Email", "Taux (%)"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mutuellesTable = new JTable(tableModel);
        mutuellesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mutuellesTable.setRowHeight(25);
        mutuellesTable.getTableHeader().setReorderingAllowed(false);

        // Boutons d'action
        ajouterBtn = createStyledButton("icons/user-add.png", "Ajouter", PRIMARY_COLOR);
        modifierBtn = createStyledButton("icons/edit1.png", "Modifier", PRIMARY_COLOR);
        supprimerBtn = createStyledButton("icons/trash.png", "Supprimer", ERROR_COLOR);

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
        adresseField = createStyledTextField();
        codePostalField = createStyledTextField();
        villeField = createStyledTextField();
        telephoneField = createStyledTextField();
        emailField = createStyledTextField();
        tauxField = createStyledTextField();
    }

    private void setupLayout() {
        // Panel du haut : titre et recherche
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Mutuelles");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel recherchePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        recherchePanel.setBackground(BACKGROUND_COLOR);
        recherchePanel.add(new JLabel("Rechercher (Nom) : "));
        recherchePanel.add(rechercheField);
        recherchePanel.add(rechercherBtn);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(recherchePanel, BorderLayout.EAST);

        // Panel central : table + détails
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);

        // Table avec scroll
        JScrollPane tableScrollPane = new JScrollPane(mutuellesTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 0));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // Panel des boutons d'action
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.add(ajouterBtn);
        buttonsPanel.add(modifierBtn);
        buttonsPanel.add(supprimerBtn);
        JButton afficherTousBtn = createStyledButton("icons/users-alt.png", "Afficher toutes", PRIMARY_COLOR);
        afficherTousBtn.addActionListener(e -> rechargerToutesMutuelles());
        buttonsPanel.add(afficherTousBtn);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.add(buttonsPanel, BorderLayout.NORTH);
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Setup du formulaire
        setupFormulaire();

        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(detailsPanel, BorderLayout.EAST);

        // Assembly final
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupFormulaire() {
        detailsPanel.removeAll();

        JLabel formTitle = new JLabel("Informations Mutuelle");
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

        // Téléphone et Email
        addFormField(formPanel, gbc, "Téléphone :", telephoneField, 1);
        addFormField(formPanel, gbc, "Email :", emailField, 2);

        // Adresse sur toute la largeur
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Adresse :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(adresseField, gbc);

        // Code Postal et Ville
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        addFormField(formPanel, gbc, "Code Postal :", codePostalField, 4);
        addFormField(formPanel, gbc, "Ville :", villeField, 5);

        // Taux de remboursement
        addFormField(formPanel, gbc, "Taux (%) :", tauxField, 6);

        detailsPanel.add(formPanel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Boutons du formulaire
        JPanel formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formButtonsPanel.setBackground(Color.WHITE);
        formButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton sauvegarderBtn = createStyledButton("icons/disk.png", "Sauvegarder", PRIMARY_COLOR);
        JButton annulerBtn = createStyledButton("icons/trash.png", "Annuler", ERROR_COLOR);

        sauvegarderBtn.addActionListener(e -> sauvegarderMutuelle());
        annulerBtn.addActionListener(e -> annulerSaisie());

        formButtonsPanel.add(sauvegarderBtn);
        formButtonsPanel.add(annulerBtn);

        detailsPanel.add(formButtonsPanel);

        detailsPanel.setPreferredSize(new Dimension(380, 0));
        detailsPanel.setMaximumSize(new Dimension(380, Integer.MAX_VALUE));
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
        rechercherBtn.addActionListener(e -> rechercherMutuelle());
        rechercheField.addActionListener(e -> rechercherMutuelle());

        // Actions sur la table
        ajouterBtn.addActionListener(e -> nouvelleMutuelle());
        modifierBtn.addActionListener(e -> modifierMutuelle());
        supprimerBtn.addActionListener(e -> supprimerMutuelle());

        // Sélection dans la table
        mutuellesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chargerMutuelleSelectionnee();
            }
        });
    }

    private void setupStyles() {
        // Style de la table
        mutuellesTable.setGridColor(new Color(230, 230, 230));
        mutuellesTable.setShowGrid(true);
        mutuellesTable.setIntercellSpacing(new Dimension(1, 1));
        mutuellesTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
        mutuellesTable.getTableHeader().setBackground(new Color(250, 250, 250));
    }

    private JButton createStyledButton(String iconPath, String text, Color color) {
        JButton button = new JButton(text);

        try {
            ImageIcon originalIcon = new ImageIcon(iconPath);
            Image img = originalIcon.getImage();
            Image scaleImg = img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaleImg);
            button.setIcon(icon);
        } catch (Exception e) {
            // Icône non trouvée, continuer sans
        }

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(color);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(6, 6, 6, 6)
        ));

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.darker(), 1),
                        new EmptyBorder(6, 6, 6, 6)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
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

    private void rechercherMutuelle() {
        String nom = rechercheField.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un nom de mutuelle", "Recherche", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Mutuelle> mutuelle = controller.rechercherMutuelle(nom);
        if (mutuelle.isPresent()) {
            tableModel.setRowCount(0);
            ajouterMutuelleATable(mutuelle.get());
            afficherMessage("Mutuelle trouvée", false);
        } else {
            afficherMessage("Mutuelle non trouvée", true);
        }
    }

    private void rechargerToutesMutuelles() {
        tableModel.setRowCount(0);
        try {
            for (Mutuelle m : controller.getToutesMutuelles()) {
                ajouterMutuelleATable(m);
            }
            afficherMessage("Liste des mutuelles chargée", false);
        } catch (Exception e) {
            afficherMessage("Erreur lors du chargement: " + e.getMessage(), true);
        }
    }

    private void ajouterMutuelleATable(Mutuelle mutuelle) {
        Object[] row = {
                mutuelle.getNom(),
                mutuelle.getVille(),
                mutuelle.getTelephone(),
                mutuelle.getEmail(),
                mutuelle.getTauxRemboursement()
        };
        tableModel.addRow(row);
    }

    private void chargerMutuelleSelectionnee() {
        int selectedRow = mutuellesTable.getSelectedRow();
        if (selectedRow >= 0) {
            String nom = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Mutuelle> mutuelleOpt = controller.rechercherMutuelle(nom);

            if (mutuelleOpt.isPresent()) {
                Mutuelle mutuelle = mutuelleOpt.get();
                remplirFormulaire(mutuelle);
            }
        }
    }

    private void remplirFormulaire(Mutuelle mutuelle) {
        nomField.setText(mutuelle.getNom());
        adresseField.setText(mutuelle.getAdresse());
        codePostalField.setText(mutuelle.getCodePostal());
        villeField.setText(mutuelle.getVille());
        telephoneField.setText(mutuelle.getTelephone());
        emailField.setText(mutuelle.getEmail());
        tauxField.setText(String.valueOf(mutuelle.getTauxRemboursement()));
    }

    private void viderFormulaire() {
        nomField.setText("");
        adresseField.setText("");
        codePostalField.setText("");
        villeField.setText("");
        telephoneField.setText("");
        emailField.setText("");
        tauxField.setText("");
    }

    private void nouvelleMutuelle() {
        viderFormulaire();
        nomField.setEnabled(true);
        editingMutuelle = false;
        currentMutuelleName = null;
    }

    private void modifierMutuelle() {
        int selectedRow = mutuellesTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner une mutuelle à modifier", true);
            return;
        }
        currentMutuelleName = (String) tableModel.getValueAt(selectedRow, 0);
        nomField.setEnabled(false);
        editingMutuelle = true;
    }

    private void sauvegarderMutuelle() {
        try {
            String nom = nomField.getText().trim();
            String adresse = adresseField.getText().trim();
            String codePostal = codePostalField.getText().trim();
            String ville = villeField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String email = emailField.getText().trim();
            double taux = Double.parseDouble(tauxField.getText().trim());

            String resultat;

            if (editingMutuelle && currentMutuelleName != null) {
                // Modification
                Optional<Mutuelle> mutuelleOpt = controller.rechercherMutuelle(currentMutuelleName);
                if (mutuelleOpt.isPresent()) {
                    Mutuelle mutuelle = mutuelleOpt.get();
                    mutuelle.setAdresse(adresse);
                    mutuelle.setCodePostal(codePostal);
                    mutuelle.setVille(ville);
                    mutuelle.setTelephone(telephone);
                    mutuelle.setEmail(email);
                    mutuelle.setTauxRemboursement(taux);
                    resultat = controller.modifierMutuelle(mutuelle);
                } else {
                    resultat = "Erreur : Mutuelle non trouvée";
                }
            } else {
                // Nouvelle mutuelle
                resultat = controller.ajouterMutuelle(nom, adresse, codePostal, ville, telephone, email, taux);
            }

            boolean isError = resultat.contains("Erreur");
            afficherMessage(resultat, isError);

            if (!isError) {
                viderFormulaire();
                nomField.setEnabled(true);
                editingMutuelle = false;
                currentMutuelleName = null;
                rechargerToutesMutuelles();
            }
        } catch (NumberFormatException e) {
            afficherMessage("Erreur : Le taux de remboursement doit être un nombre", true);
        } catch (Exception e) {
            afficherMessage("Erreur : " + e.getMessage(), true);
        }
    }

    private void annulerSaisie() {
        viderFormulaire();
        nomField.setEnabled(true);
        mutuellesTable.clearSelection();
        editingMutuelle = false;
        currentMutuelleName = null;
    }

    private void supprimerMutuelle() {
        int selectedRow = mutuellesTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner une mutuelle à supprimer", true);
            return;
        }

        String nom = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer la mutuelle " + nom + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String resultat = controller.supprimerMutuelle(nom);
            boolean isError = resultat.contains("Erreur");
            afficherMessage(resultat, isError);

            if (!isError) {
                tableModel.removeRow(selectedRow);
                viderFormulaire();
            }
        }
    }

    private void afficherMessage(String message, boolean isError) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof PharmacieMainFrame) {
            ((PharmacieMainFrame) parent).showMessage(message, isError);
        }
    }
}
