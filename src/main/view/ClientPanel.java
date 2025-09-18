package main.view;

import main.controller.PharmacieController;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Organisme.TypeOrganisme.Mutuelle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;

/**
 * Panel de gestion des clients
 */
public class ClientPanel extends JPanel {
    private final PharmacieController controller;

    // Couleurs du thème (identiques à MainFrame)
    private static final Color PRIMARY_COLOR = new Color(0, 62, 28);
    private static final Color SECONDARY_COLOR = new Color(117, 187, 153);
    private static final Color ACCENT_COLOR = new Color(217, 243, 228);
    private static final Color ERROR_COLOR = new Color(187, 45, 12);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(1, 17, 9);

    // Composants
    private JTextField rechercheField;
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JButton ajouterBtn, modifierBtn, supprimerBtn, rechercherBtn;
    private PharmacieMainFrame mainFrame;

    // Formulaire
    private JTextField nomField, prenomField, adresseField, codePostalField;
    private JTextField villeField, telephoneField, emailField, identifiantField, numeroSecuField;
    private JComboBox<String> mutuelleCombo; // sélection facultative d'une mutuelle
    private JComboBox<String> medecinCombo;  // sélection du médecin traitant (RPPS ou "Aucun")
    private boolean editingClient = false;

    /**
     * Construit le panel de gestion des clients (UI, événements, styles).
     */
    public ClientPanel(PharmacieController controller, PharmacieMainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
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
        rechercherBtn = createStyledButton("icons/search1.png","Rechercher", PRIMARY_COLOR);

        // Table des clients
        String[] colonnes = {"Identifiant", "Nom", "Prénom", "Ville", "Téléphone", "Email"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientsTable = new JTable(tableModel);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientsTable.setRowHeight(25);
        clientsTable.getTableHeader().setReorderingAllowed(false);

        // Boutons d'action
        ajouterBtn = createStyledButton("icons/user-add.png","Ajouter", PRIMARY_COLOR);
        modifierBtn = createStyledButton("icons/edit1.png","Modifier", PRIMARY_COLOR);
        supprimerBtn = createStyledButton("icons/trash.png","Supprimer", ERROR_COLOR);

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
        numeroSecuField = createStyledTextField();
        // Combo mutuelles (optionnelle, avec entrée "Aucune")
        mutuelleCombo = new JComboBox<>();
        mutuelleCombo.addItem("Aucune");
        try {
            for (Mutuelle m : controller.getToutesMutuelles()) {
                mutuelleCombo.addItem(m.getNom());
            }
        } catch (Exception ignore) {}

        // Combo médecins (médecin traitant)
        medecinCombo = new JComboBox<>();
        medecinCombo.addItem("Aucun");
        try {
            for (main.model.Personne.CategoriePersonne.Medecin md : controller.getTousMedecins()) {
                // Affiche "RPPS - NOM Prénom"
                medecinCombo.addItem(md.getNumeroRPPS() + " - " + md.getNom() + " " + md.getPrenom());
            }
        } catch (Exception ignore) {}
    }

    private void setupLayout() {
        // Panel du haut : titre et recherche
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Clients");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel recherchePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        recherchePanel.setBackground(BACKGROUND_COLOR);
        recherchePanel.add(new JLabel("Rechercher : "));
        recherchePanel.add(rechercheField);
        recherchePanel.add(rechercherBtn);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(recherchePanel, BorderLayout.EAST);

        // Panel central : table + détails
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);

        // Table avec scroll
        JScrollPane tableScrollPane = new JScrollPane(clientsTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 0));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // Panel des boutons d'action (au-dessus du tableau)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.add(ajouterBtn);
        buttonsPanel.add(modifierBtn);
        buttonsPanel.add(supprimerBtn);
        JButton afficherTousBtn = createStyledButton("icons/users-alt.png","Afficher tous", PRIMARY_COLOR);
        afficherTousBtn.addActionListener(e -> rechargerTousClients());
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

        JLabel formTitle = new JLabel("Informations Client");
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
        addFormField(formPanel, gbc, "Tél :", telephoneField, 3);

        // Ligne 3
        addFormField(formPanel, gbc, "Email :", emailField, 4);
        addFormField(formPanel, gbc, "N° Sécu :", numeroSecuField, 5);

        // Ligne 4 (adresse sur toute la largeur)
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Adresse :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(adresseField, gbc);

        // Ligne 5
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        addFormField(formPanel, gbc, "Code Postal :", codePostalField, 7);
        addFormField(formPanel, gbc, "Ville :", villeField, 8);

        // Ligne 6 (Mutuelle optionnelle)
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Mutuelle :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(mutuelleCombo, gbc);

        // Ligne 7 (Médecin traitant)
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Médecin traitant :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(medecinCombo, gbc);

        detailsPanel.add(formPanel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Boutons du formulaire
        JPanel formButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formButtonsPanel.setBackground(Color.WHITE);
        formButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton sauvegarderBtn = createStyledButton("icons/disk.png","Sauvegarder", PRIMARY_COLOR);
        JButton annulerBtn = createStyledButton("icons/trash.png","Annuler", ERROR_COLOR);

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
        rechercherBtn.addActionListener(e -> rechercherClient());
        rechercheField.addActionListener(e -> rechercherClient());

        // Actions sur la table
        ajouterBtn.addActionListener(e -> nouveauClient());
        modifierBtn.addActionListener(e -> modifierClient());
        supprimerBtn.addActionListener(e -> supprimerClient());

        // Sélection dans la table
        clientsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chargerClientSelectionne();
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
                            btn.addActionListener(e -> sauvegarderClient());
                        } else if (btn.getText().contains("Annuler")) {
                            btn.addActionListener(e -> annulerSaisie());
                        }
                    }
                }
            }
        }
    }

    private void setupStyles() {
        // Style de la table
        clientsTable.setGridColor(new Color(230, 230, 230));
        clientsTable.setShowGrid(true);
        clientsTable.setIntercellSpacing(new Dimension(1, 1));
        clientsTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
        clientsTable.getTableHeader().setBackground(new Color(250, 250, 250));
    }

    /**
     * Place le focus dans le champ de recherche clients.
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

    private void rechercherClient() {
        String identifiant = rechercheField.getText().trim();
        if (identifiant.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un identifiant", "Recherche", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Client> client = controller.rechercherClient(identifiant);
        if (client.isPresent()) {
            // Effacer la table et ajouter le client trouvé
            tableModel.setRowCount(0);
            ajouterClientATable(client.get());
            afficherMessage("Client trouvé", false);
        } else {
            afficherMessage("Client non trouvé", true);
        }
    }

    private void rechargerTousClients() {
        tableModel.setRowCount(0);
        for (Client c : controller.getTousClients()) {
            ajouterClientATable(c);
        }
        afficherMessage("Liste des clients chargée", false);
    }

    private void ajouterClientATable(Client client) {
        Object[] row = {
                client.getIdentifiant(),
                client.getNom(),
                client.getPrenom(),
                client.getVille(),
                client.getNumTelephone(),
                client.getEmail()
        };
        tableModel.addRow(row);
    }

    private void chargerClientSelectionne() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String identifiant = (String) tableModel.getValueAt(selectedRow, 0);
            Optional<Client> clientOpt = controller.rechercherClient(identifiant);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();
                remplirFormulaire(client);
            }
        }
    }

    private void remplirFormulaire(Client client) {
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        adresseField.setText(client.getAdresse());
        codePostalField.setText(client.getCodePostal());
        villeField.setText(client.getVille());
        telephoneField.setText(client.getNumTelephone());
        emailField.setText(client.getEmail());
        identifiantField.setText(client.getIdentifiant());
        numeroSecuField.setText(client.getNumeroSecuriteSocial());

        // Sélectionner le médecin traitant dans la combo si présent
        String target = "Aucun";
        if (client.getMedecinTraitant() != null) {
            target = client.getMedecinTraitant().getNumeroRPPS() + " - " + client.getMedecinTraitant().getNom() + " " + client.getMedecinTraitant().getPrenom();
        }
        medecinCombo.setSelectedItem(target);
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
        numeroSecuField.setText("");
    }

    private void nouveauClient() {
        viderFormulaire();
        identifiantField.setEnabled(false);
        // Générer l'identifiant à partir du prénom/nom (si déjà saisis)
        identifiantField.setText("");
        prenomField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { majId(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { majId(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { majId(); }
            private void majId() {
                String id = mainFrame != null ? mainFrame.getController().generateClientIdentifiant(prenomField.getText().trim(), nomField.getText().trim())
                        : controller.generateClientIdentifiant(prenomField.getText().trim(), nomField.getText().trim());
                identifiantField.setText(id);
            }
        });
        nomField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { majId(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { majId(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { majId(); }
            private void majId() {
                String id = mainFrame != null ? mainFrame.getController().generateClientIdentifiant(prenomField.getText().trim(), nomField.getText().trim())
                        : controller.generateClientIdentifiant(prenomField.getText().trim(), nomField.getText().trim());
                identifiantField.setText(id);
            }
        });
        editingClient = false;
    }

    private void modifierClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner un client à modifier", true);
            return;
        }
        identifiantField.setEnabled(false);
        editingClient = true;
    }

    private void sauvegarderClient() {
        try {
            String resultat;
            boolean isModification = editingClient;

            if (isModification) {
                // Modification d'un client existant
                String identifiant = identifiantField.getText().trim();
                Optional<Client> clientOpt = controller.rechercherClient(identifiant);

                if (clientOpt.isPresent()) {
                    Client client = clientOpt.get();
                    client.setNom(nomField.getText().trim());
                    client.setPrenom(prenomField.getText().trim());
                    client.setAdresse(adresseField.getText().trim());
                    client.setCodePostal(codePostalField.getText().trim());
                    client.setVille(villeField.getText().trim());
                    client.setNumTelephone(telephoneField.getText().trim());
                    client.setEmail(emailField.getText().trim());
                    client.setNumeroSecuriteSocial(numeroSecuField.getText().trim());

                    // Mise à jour médecin traitant
                    String selectionMed = (String) medecinCombo.getSelectedItem();
                    String rpps = null;
                    if (selectionMed != null && !selectionMed.equals("Aucun")) {
                        int idx = selectionMed.indexOf(" - ");
                        if (idx > 0) rpps = selectionMed.substring(0, idx).trim();
                    }
                    controller.assignerMedecinClient(client.getIdentifiant(), rpps);

                    resultat = controller.modifierClient(client);

                    // NOUVEAU : Déclencher l'événement de modification
                    if (!resultat.contains("Erreur")) {
                        DataEventManager.ClientEvents.clientUpdated();
                    }
                } else {
                    resultat = "Erreur : Client non trouvé";
                }
            } else {
                // Nouveau client - utiliser l'identifiant généré dans le champ
                String identifiantGenere = identifiantField.getText().trim();
                if (identifiantGenere.isEmpty()) {
                    // fallback si pas encore généré
                    identifiantGenere = (mainFrame != null ? mainFrame.getController() : controller)
                            .generateClientIdentifiant(prenomField.getText().trim(), nomField.getText().trim());
                    identifiantField.setText(identifiantGenere);
                }
                String choixMutuelle = (String) mutuelleCombo.getSelectedItem();
                resultat = controller.ajouterClient(
                        nomField.getText().trim(),
                        prenomField.getText().trim(),
                        adresseField.getText().trim(),
                        codePostalField.getText().trim(),
                        villeField.getText().trim(),
                        telephoneField.getText().trim(),
                        emailField.getText().trim(),
                        identifiantGenere,
                        numeroSecuField.getText().trim(),
                        choixMutuelle
                );

                // Associer le médecin traitant si sélectionné
                String selectionMed = (String) medecinCombo.getSelectedItem();
                String rpps = null;
                if (selectionMed != null && !selectionMed.equals("Aucun")) {
                    int idx = selectionMed.indexOf(" - ");
                    if (idx > 0) rpps = selectionMed.substring(0, idx).trim();
                }
                if (rpps != null && !rpps.isEmpty()) {
                    controller.assignerMedecinClient(identifiantGenere, rpps);
                }

                if (!resultat.contains("Erreur")) {
                    DataEventManager.ClientEvents.clientAdded();
                    // Ajouter à la table immédiatement
                    Optional<Client> created = controller.rechercherClient(identifiantGenere);
                    created.ifPresent(this::ajouterClientATable);
                }
            }

            boolean isError = resultat.contains("Erreur");
            afficherMessage(resultat, isError);
            if (!isError) {
                viderFormulaire();
                identifiantField.setEnabled(false);
                editingClient = false;
            }
        } catch (Exception e) {
            afficherMessage("Erreur : " + e.getMessage(), true);
        }
    }

    private void annulerSaisie() {
        viderFormulaire();
        identifiantField.setEnabled(false);
        clientsTable.clearSelection();
        editingClient = false;
    }

    private void supprimerClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow < 0) {
            afficherMessage("Veuillez sélectionner un client à supprimer", true);
            return;
        }

        String identifiant = (String) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String prenom = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer le client " + nom + " " + prenom + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String resultat = controller.supprimerClient(identifiant);
            boolean isError = resultat.contains("Erreur");
            afficherMessage(resultat, isError);

            if (!isError) {
                tableModel.removeRow(selectedRow);
                viderFormulaire();

                DataEventManager.ClientEvents.clientDeleted();
            }
        }
    }

    private void afficherMessage(String message, boolean isError) {
        // Trouver la fenêtre parent pour afficher le message
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof PharmacieMainFrame) {
            ((PharmacieMainFrame) parent).showMessage(message, isError);
        }
    }
}