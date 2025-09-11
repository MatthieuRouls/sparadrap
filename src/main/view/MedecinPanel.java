package main.view;

import main.controller.PharmacieController;
import main.model.Personne.CategoriePersonne.Medecin;
import main.view.PharmacieMainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;

/**
 * Panel de gestion des médecins
 */
public class MedecinPanel extends JPanel {
    private final PharmacieController controller;

    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    // Composants
    private JTextField rechercheField;
    private JTable medecinsTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JButton ajouterBtn, modifierBtn, supprimerBtn, rechercherBtn;

    // Formulaire
    private JTextField nomField, prenomField, adresseField, codePostalField;
    private JTextField villeField, telephoneField, emailField, identifiantField, numeroRPPSField;

    public MedecinPanel(PharmacieController controller) {
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
        rechercherBtn = createStyledButton("🔍 Rechercher", SECONDARY_COLOR);

        // Table des médecins
        String[] colonnes = {"Identifiant", "Nom", "Prénom", "Ville", "Téléphone", "N° RPPS"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medecinsTable = new JTable(tableModel);
        medecinsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medecinsTable.setRowHeight(25);
        medecinsTable.getTableHeader().setReorderingAllowed(false);

        // Boutons d'action
        ajouterBtn = createStyledButton("➕ Ajouter", ACCENT_COLOR);
        modifierBtn = createStyledButton("✏️ Modifier", PRIMARY_COLOR);
        supprimerBtn = createStyledButton("🗑️ Supprimer", ERROR_COLOR);

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
        prenomField = createStyledTextField();
        adresseField = createStyledTextField();
        codePostalField = createStyledTextField();
        villeField = createStyledTextField();
        telephoneField = createStyledTextField();
        emailField = createStyledTextField();
        identifiantField = createStyledTextField();
        numeroRPPSField = createStyledTextField();
    }

    private void setupLayout() {
        // Panel du haut
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Médecins");
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
        JScrollPane tableScrollPane = new JScrollPane(medecinsTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 0));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // Panel des boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.add(ajouterBtn);
        buttonsPanel.add(modifierBtn);
        buttonsPanel.add(supprimerBtn);

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

        JLabel formTitle = new JLabel("Informations Médecin");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(TEXT_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(formTitle);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Formulaire en deux colonnes
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 1
        addFormField(formPanel, gbc, "Nom :", nomField, 0);
        addFormField(formPanel, gbc, "Prénom :", prenomField, 1);

        // Ligne 2
        addFormField(formPanel, gbc, "Identifiant :", identifiantField, 2);
        addFormField(formPanel, gbc, "N° RPPS :", numeroRPPSField, 3);

        // Ligne 3
        addFormField(formPanel, gbc, "Téléphone :", telephoneField, 4);
        addFormField(formPanel, gbc, "Email :", emailField, 5);

        // Ligne 4 (adresse sur toute la largeur)
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Adresse :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(adresseField, gbc);

        // Ligne 5
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        addFormField(formPanel, gbc, "Code Postal :", codePostalField, 7);
        addFormField(formPanel, gbc, "Ville :", villeField, 8);

        detailsPanel.add(formPanel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Boutons du formulaire
        JPanel formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formButtonsPanel.setBackground(Color.WHITE);
        formButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton sauvegarderBtn = createStyledButton("💾 Sauvegarder", ACCENT_COLOR);
        JButton annulerBtn = createStyledButton("❌ Annuler", ERROR_COLOR);

        formButtonsPanel.add(sauvegarderBtn);
        formButtonsPanel.add(annulerBtn);

        detailsPanel.add(formButtonsPanel);

        detailsPanel.setPreferredSize(new Dimension(350, 0));
        detailsPanel.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
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
        rechercherBtn.addActionListener(e -> rechercherMedecin());
        rechercheField.addActionListener(e -> rechercherMedecin());

        // Actions sur la table
        ajouterBtn.addActionListener(e -> nouveauMedecin());
        modifierBtn.addActionListener(e -> modifierMedecin());
        supprimerBtn.addActionListener(e -> supprimerMedecin());

        // Sélection dans la table
        medecinsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chargerMedecinSelectionne();
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
                            btn.addActionListener(e -> sauvegarderMedecin());
                        } else if (btn.getText().contains("Annuler")) {
                            btn.addActionListener(e -> annulerSaisie());
                        }
                    }
                }
            }
        }
    }

    private void setupStyles() {
        medecinsTable.setGridColor(new Color(230, 230, 230));
        medecinsTable.setShowGrid(true);
        medecinsTable.setIntercellSpacing(new Dimension(1, 1));
        medecinsTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
        medecinsTable.getTableHeader().setBackground(new Color(250, 250, 250));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
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

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private void rechercherMedecin() {
        String identifiant = rechercheField.getText().trim();
        if (identifiant.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un identifiant", "Recherche", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Medecin> medecin = controller.rechercherMedecin(identifiant);
        if (medecin.isPresent()) {
            tableModel.setRowCount(0);
            ajouterMedecinATable(medecin.get());
            afficherMessage("Médecin trouvé", false);
        } else {
            afficherMessage("Médecin non trouvé", true);
        }
    }

    private void ajouterMedecinATable(Medecin medecin) {
        Object[] row = {
                medecin.getIdentifiant(),
                medecin.getNom(),
                medecin.getPrenom(),
                medecin.getVille(),
                medecin.getNumTelephone(),
                medecin.getNumeroRPPS()
        };
        tableModel.addRow(row);
    }

    private void chargerMedecinSelectionne() {
        int selectedRow = medecinsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String identifiant = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Medecin> medecinOpt = controller.rechercherMedecin(identifiant);

            if (medecinOpt.isPresent()) {
                Medecin medecin = medecinOpt.get();
                remplirFormulaire(medecin);
            }
        }
    }

    private void remplirFormulaire(Medecin medecin) {
        nomField.setText(medecin.getNom());
        prenomField.setText(medecin.getPrenom());
        adresseField.setText(medecin.getAdresse());
        codePostalField.setText(medecin.getCodePostal());
        villeField.setText(medecin.getVille());
        telephoneField.setText(medecin.getNumTelephone());
        emailField.setText(medecin.getEmail());
        identifiantField.setText(medecin.getIdentifiant());
        numeroRPPSField.setText(medecin.getNumeroRPPS());
    }

    private void viderFormulaire() {
        nomField.setText("");
        prenomField.setText("");
        adresseField.setText("");
        codePostalField.setText("");
        villeField.setText("");
        telephoneField.setText("");
        emailField.setText("");
        identifiantField.setText("");
        numeroRPPSField.setText("");
    }

    private void nouveauMedecin() {
        viderFormulaire();
        identifiantField.setEnabled(true);
        numeroRPPSField.setEnabled(true);
    }

    private void modifierMedecin() {
        int selectedRow = medecinsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner un médecin à modifier", true);
            return;
        }
        identifiantField.setEnabled(false);
        numeroRPPSField.setEnabled(false);
    }

    private void sauvegarderMedecin() {
        try {
            String resultat;
            boolean isModification = !identifiantField.isEnabled();

            if (isModification) {
                String identifiant = identifiantField.getText().trim();
                Optional<Medecin> medecinOpt = controller.rechercherMedecin(identifiant);

                if (medecinOpt.isPresent()) {
                    Medecin medecin = medecinOpt.get();
                    medecin.setNom(nomField.getText().trim());
                    medecin.setPrenom(prenomField.getText().trim());
                    medecin.setAdresse(adresseField.getText().trim());
                    medecin.setCodePostal(codePostalField.getText().trim());
                    medecin.setVille(villeField.getText().trim());
                    medecin.setNumTelephone(telephoneField.getText().trim());
                    medecin.setEmail(emailField.getText().trim());

                    resultat = controller.modifierMedecin(medecin);
                } else {
                    resultat = "Erreur : Médecin non trouvé";
                }
            } else {
                resultat = controller.ajouterMedecin(
                        nomField.getText().trim(),
                        prenomField.getText().trim(),
                        adresseField.getText().trim(),
                        codePostalField.getText().trim(),
                        villeField.getText().trim(),
                        telephoneField.getText().trim(),
                        emailField.getText().trim(),
                        identifiantField.getText().trim(),
                        numeroRPPSField.getText().trim()
                );
            }

            boolean isError = resultat.contains("Erreur");
            afficherMessage(resultat, isError);

            if (!isError) {
                viderFormulaire();
                identifiantField.setEnabled(true);
                numeroRPPSField.setEnabled(true);
                if (isModification) {
                    rechercherMedecin();
                }
            }

        } catch (Exception e) {
            afficherMessage("Erreur : " + e.getMessage(), true);
        }
    }

    private void annulerSaisie() {
        viderFormulaire();
        identifiantField.setEnabled(true);
        numeroRPPSField.setEnabled(true);
        medecinsTable.clearSelection();
    }

    private void supprimerMedecin() {
        int selectedRow = medecinsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner un médecin à supprimer", true);
            return;
        }

        String identifiant = (String) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String prenom = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer le médecin " + nom + " " + prenom + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String resultat = controller.supprimerMedecin(identifiant);
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
