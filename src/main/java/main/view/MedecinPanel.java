package main.view;

import main.controller.PharmacieController;
import main.model.Personne.CategoriePersonne.Medecin;

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
    private static final Color PRIMARY_COLOR = new Color(0, 62, 28);
    private static final Color SECONDARY_COLOR = new Color(117, 187, 153);
    private static final Color ACCENT_COLOR = new Color(217, 243, 228);
    private static final Color ERROR_COLOR = new Color(187, 45, 12);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(1, 17, 9);

    // Composants
    private JTextField rechercheField;
    private JTable medecinsTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JButton ajouterBtn, modifierBtn, supprimerBtn, rechercherBtn;

    // Formulaire
    private JTextField nomField, prenomField, adresseField, codePostalField;
    private JTextField villeField, telephoneField, emailField, numeroRPPSField, identifiantField;

    /**
     * Construit le panel de gestion des médecins (UI, événements, styles).
     */
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
        rechercherBtn = createStyledButton("icons/search1.png", "Rechercher", PRIMARY_COLOR);

        // Table des médecins
        String[] colonnes = {"N° RPPS", "Nom", "Prénom", "Ville", "Téléphone", "Email"};
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
        prenomField = createStyledTextField();
        adresseField = createStyledTextField();
        codePostalField = createStyledTextField();
        villeField = createStyledTextField();
        telephoneField = createStyledTextField();
        emailField = createStyledTextField();
        numeroRPPSField = createStyledTextField();
        identifiantField = createStyledTextField();
    }

    private void setupLayout() {
        // Panel du haut : titre et recherche
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Médecins");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel recherchePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        recherchePanel.setBackground(BACKGROUND_COLOR);
        recherchePanel.add(new JLabel("Rechercher (N° RPPS) : "));
        recherchePanel.add(rechercheField);
        recherchePanel.add(rechercherBtn);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(recherchePanel, BorderLayout.EAST);

        // Panel central : table + détails
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);

        // Table avec scroll
        JScrollPane tableScrollPane = new JScrollPane(medecinsTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 0));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // Panel des boutons d'action
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.add(ajouterBtn);
        buttonsPanel.add(modifierBtn);
        buttonsPanel.add(supprimerBtn);
        JButton afficherTousBtn = createStyledButton("icons/users-alt.png", "Afficher tous", PRIMARY_COLOR);
        afficherTousBtn.addActionListener(e -> rechargerTousMedecins());
        buttonsPanel.add(afficherTousBtn);
        JButton ordonnancesParMedBtn = createStyledButton("icons/list.png", "Ordonnances par médecin", PRIMARY_COLOR);
        ordonnancesParMedBtn.addActionListener(e -> afficherOrdonnancesParMedecinSelectionne());
        buttonsPanel.add(ordonnancesParMedBtn);

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
        addFormField(formPanel, gbc, "N° RPPS :", numeroRPPSField, 2);
        // Ne plus afficher de champ identifiant séparé

        // Ligne 3
        addFormField(formPanel, gbc, "Tél :", telephoneField, 4);
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

        JButton sauvegarderBtn = createStyledButton("icons/disk.png", "Sauvegarder", PRIMARY_COLOR);
        JButton annulerBtn = createStyledButton("icons/trash.png", "Annuler", ERROR_COLOR);

        formButtonsPanel.add(sauvegarderBtn);
        formButtonsPanel.add(annulerBtn);

        // Event listeners pour les boutons du formulaire
        sauvegarderBtn.addActionListener(e -> sauvegarderMedecin());
        annulerBtn.addActionListener(e -> annulerSaisie());

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

        // Double-clic sur un médecin -> afficher ses ordonnances
        medecinsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && medecinsTable.getSelectedRow() != -1) {
                    afficherOrdonnancesParMedecinSelectionne();
                }
            }
        });
    }

    private void setupStyles() {
        // Style de la table
        medecinsTable.setGridColor(new Color(230, 230, 230));
        medecinsTable.setShowGrid(true);
        medecinsTable.setIntercellSpacing(new Dimension(1, 1));
        medecinsTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
        medecinsTable.getTableHeader().setBackground(new Color(250, 250, 250));
    }

    private JButton createStyledButton(String iconPath, String text, Color color) {
        JButton button = new JButton(text);

            ImageIcon originalIcon = new ImageIcon(iconPath);
            Image img = originalIcon.getImage();
            Image scaleImg = img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaleImg);
            button.setIcon(icon);


        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(color);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),  // Contour coloré de 1px
                new EmptyBorder(6, 6, 6, 6)    // Espacement interne
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);

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

    private void rechercherMedecin() {
        String numeroRPPS = rechercheField.getText().trim();
        if (numeroRPPS.isEmpty()) {
            afficherMessage("Veuillez saisir un numéro RPPS", true);
            return;
        }

        Optional<Medecin> medecin = controller.rechercherMedecin(numeroRPPS);
        if (medecin.isPresent()) {
            // Effacer la table et ajouter le médecin trouvé
            tableModel.setRowCount(0);
            ajouterMedecinATable(medecin.get());
            afficherMessage("Médecin trouvé", false);
        } else {
            afficherMessage("Médecin non trouvé", true);
        }
    }

    private void rechargerTousMedecins() {
        tableModel.setRowCount(0);
        for (Medecin m : controller.getTousMedecins()) {
            ajouterMedecinATable(m);
        }
        afficherMessage("Liste des médecins chargée", false);
    }

    private void afficherOrdonnancesParMedecinSelectionne() {
        int selectedRow = medecinsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Sélectionnez un médecin", true);
            return;
        }
        String rpps = (String) tableModel.getValueAt(selectedRow, 0);
        java.util.List<main.model.Document.TypeDocument.Ordonnance> ordos = controller.obtenirOrdonnancesParMedecin(rpps);
        if (ordos.isEmpty()) {
            afficherMessage("Aucune ordonnance trouvée pour ce médecin", false);
            return;
        }
        StringBuilder sb = new StringBuilder();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (main.model.Document.TypeDocument.Ordonnance o : ordos) {
            sb.append(String.format("- %s | %s | Patient: %s %s | Total: %.2f €%n",
                    o.getReference(), df.format(o.getDateCreation()),
                    o.getPatient().getNom(), o.getPatient().getPrenom(), o.getMontantTotal()));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(new EmptyBorder(10,10,10,10));
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Ordonnances du médecin " + rpps, JOptionPane.INFORMATION_MESSAGE);
    }

    private void ajouterMedecinATable(Medecin medecin) {
        Object[] row = {
                medecin.getNumeroRPPS(),
                medecin.getNom(),
                medecin.getPrenom(),
                medecin.getVille(),
                medecin.getNumTelephone(),
                medecin.getEmail()
        };
        tableModel.addRow(row);
    }

    private void chargerMedecinSelectionne() {
        int selectedRow = medecinsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String numeroRPPS = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Medecin> medecinOpt = controller.rechercherMedecin(numeroRPPS);

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
        numeroRPPSField.setText(medecin.getNumeroRPPS());
        identifiantField.setText(medecin.getIdentifiant());

        // Afficher le nombre d'ordonnances de ce médecin (info rapide)
        try {
            int nbOrdos = controller.obtenirOrdonnancesParMedecin(medecin.getNumeroRPPS()).size();
            afficherMessage("Ordonnances de ce médecin : " + nbOrdos, false);
        } catch (Exception ignore) {}
    }

    private void viderFormulaire() {
        nomField.setText("");
        prenomField.setText("");
        adresseField.setText("");
        codePostalField.setText("");
        villeField.setText("");
        telephoneField.setText("");
        emailField.setText("");
        numeroRPPSField.setText("");
        identifiantField.setText("");
    }

    private void nouveauMedecin() {
        viderFormulaire();
        numeroRPPSField.setEnabled(true);
        identifiantField.setEnabled(false);
    }

    private void modifierMedecin() {
        int selectedRow = medecinsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner un médecin à modifier", true);
            return;
        }
        numeroRPPSField.setEnabled(false);
        identifiantField.setEnabled(false);
    }

    private void sauvegarderMedecin() {
        try {
            String resultat;
            boolean isModification = !numeroRPPSField.isEnabled();

            if (isModification) {
                // Modification d'un médecin existant
                String numeroRPPS = numeroRPPSField.getText().trim();
                Optional<Medecin> medecinOpt = controller.rechercherMedecin(numeroRPPS);

                if (medecinOpt.isPresent()) {
                    Medecin medecin = medecinOpt.get();
                    // Validation des champs
                    if (!validerChamps()) {
                        afficherMessage("Veuillez corriger les erreurs en rouge", true);
                        return;
                    }
                    // Mise à jour des champs
                    medecin.setNom(nomField.getText().trim());
                    medecin.setPrenom(prenomField.getText().trim());
                    medecin.setAdresse(adresseField.getText().trim());
                    medecin.setCodePostal(codePostalField.getText().trim());
                    medecin.setVille(villeField.getText().trim());
                    medecin.setNumTelephone(telephoneField.getText().trim());
                    medecin.setEmail(emailField.getText().trim());
                    // Identifiant = RPPS
                    medecin.setIdentifiant(numeroRPPS);

                    resultat = controller.modifierMedecin(medecin);
                } else {
                    resultat = "Erreur : Médecin non trouvé";
                }
            } else {
                // Nouveau médecin
                if (!validerChamps()) {
                    afficherMessage("Veuillez corriger les erreurs en rouge", true);
                    return;
                }
                String rpps = numeroRPPSField.getText().trim();
                resultat = controller.ajouterMedecin(
                        nomField.getText().trim(),
                        prenomField.getText().trim(),
                        adresseField.getText().trim(),
                        codePostalField.getText().trim(),
                        villeField.getText().trim(),
                        telephoneField.getText().trim(),
                        emailField.getText().trim(),
                        rpps, // identifiant = RPPS
                        rpps  // numeroRPPS
                );
                // Si succès, ajouter à la table
                if (!resultat.contains("Erreur")) {
                    Optional<Medecin> medecinCree = controller.rechercherMedecin(rpps);
                    medecinCree.ifPresent(this::ajouterMedecinATable);
                    // Émettre un événement pour MAJ tableau de bord si nécessaire
                    try {
                        DataEventManager.getInstance().fireEvent(DataEventManager.EventType.MEDECIN_ADDED);
                    } catch (Exception ignored) {}
                }
            }

            boolean isError = resultat.contains("Erreur");
            afficherMessage(resultat, isError);
            if (!isError) {
                viderFormulaire();
                numeroRPPSField.setEnabled(true);
                identifiantField.setEnabled(false);
                // Nettoyer styles d'erreur
                clearFieldError(nomField);
                clearFieldError(prenomField);
                clearFieldError(adresseField);
                clearFieldError(codePostalField);
                clearFieldError(villeField);
                clearFieldError(telephoneField);
                clearFieldError(emailField);
                clearFieldError(numeroRPPSField);
                clearFieldError(identifiantField);
            }
        } catch (Exception e) {
            afficherMessage("Erreur : " + e.getMessage(), true);
        }
    }

    private void annulerSaisie() {
        viderFormulaire();
        numeroRPPSField.setEnabled(true);
        identifiantField.setEnabled(true);
        medecinsTable.clearSelection();
    }

    private void supprimerMedecin() {
        int selectedRow = medecinsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner un médecin à supprimer", true);
            return;
        }

        String numeroRPPS = (String) tableModel.getValueAt(selectedRow, 0);
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
            String resultat = controller.supprimerMedecin(numeroRPPS);
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

    // ================= Validation et styles d'erreur =================

    private boolean validerChamps() {
        boolean ok = true;
        // Réinitialiser
        clearFieldError(nomField);
        clearFieldError(prenomField);
        clearFieldError(adresseField);
        clearFieldError(codePostalField);
        clearFieldError(villeField);
        clearFieldError(telephoneField);
        clearFieldError(emailField);
        clearFieldError(numeroRPPSField);
        clearFieldError(identifiantField);

        if (nomField.getText().trim().isEmpty()) { setFieldError(nomField, "Nom requis"); ok = false; }
        if (prenomField.getText().trim().isEmpty()) { setFieldError(prenomField, "Prénom requis"); ok = false; }
        if (numeroRPPSField.getText().trim().isEmpty()) { setFieldError(numeroRPPSField, "Numéro RPPS requis"); ok = false; }
        else if (!numeroRPPSField.getText().trim().matches("\\d{9,15}")) { setFieldError(numeroRPPSField, "RPPS invalide (9-15 chiffres)"); ok = false; }

        // Identifiant = RPPS, pas de saisie nécessaire
        if (codePostalField.getText().trim().isEmpty()) { setFieldError(codePostalField, "Code postal requis"); ok = false; }
        else if (!codePostalField.getText().trim().matches("\\d{4,6}")) { setFieldError(codePostalField, "Code postal invalide"); ok = false; }

        if (villeField.getText().trim().isEmpty()) { setFieldError(villeField, "Ville requise"); ok = false; }
        if (telephoneField.getText().trim().isEmpty()) { setFieldError(telephoneField, "Téléphone requis"); ok = false; }
        else if (!telephoneField.getText().trim().matches("[\\d +().-]{6,}")) { setFieldError(telephoneField, "Téléphone invalide"); ok = false; }

        if (emailField.getText().trim().isEmpty()) { setFieldError(emailField, "Email requis"); ok = false; }
        else if (!emailField.getText().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) { setFieldError(emailField, "Email invalide"); ok = false; }

        // L'adresse peut être vide, mais si remplie, on tronque espaces
        adresseField.setText(adresseField.getText().trim());

        return ok;
    }

    private void setFieldError(JTextField field, String message) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setToolTipText(message);
    }

    private void clearFieldError(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setToolTipText(null);
    }
}
