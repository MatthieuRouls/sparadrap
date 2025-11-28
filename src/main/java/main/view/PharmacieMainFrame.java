package main.view;

import main.controller.PharmacieController;
import main.model.Personne.CategoriePersonne.Pharmacien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;

/**
 * Fenêtre principale de l'application Pharmacie Sparadrap
 */
public class PharmacieMainFrame extends JFrame implements DataRefreshListener {
    private final PharmacieController controller;
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel welcomeLabel;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private JLabel venteCountLabel;
    private JLabel caCountLabel;
    private JLabel stockCountLabel;

    // Références des panels pour accéder à des actions spécifiques (focus, etc.)
    private ClientPanel clientPanelRef;
    private MedicamentPanel medicamentPanelRef;

    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(1, 17, 9);
    private static final Color SECONDARY_COLOR = new Color(0, 62, 28, 255);
    private static final Color ACCENT_COLOR = new Color(117, 187, 153);
    private static final Color ERROR_COLOR = new Color(187, 45, 12);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SIDEBAR_COLOR = new Color(217, 243, 228);

/**
 * Construit la fenêtre principale (contrôleur, composants, styles, listeners) et configure le frame.
 */
public PharmacieMainFrame() {
    this.controller = new PharmacieController();

    // Initialiser le pharmacien connecté
    initialiserPharmacienDemo();

    initializeComponents();
    setupLayout();
    setupStyles();
    setupEventListeners();

    DataEventManager.getInstance().addListener(this);

    ImageIcon icon = new ImageIcon("icons/cross.png");
    Image iconImage = icon.getImage();

    // Configuration de la fenêtre
    setTitle("Pharmacie Sparadrap - Système de Gestion");
    setIconImage(iconImage);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);
    setMinimumSize(new Dimension(1000, 600));

    // Listener déjà ajouté plus haut
}

    private PharmacieMainFrame mainFrame;

/**
 * Initialise les composants Swing principaux (sidebar, zone de contenu, labels).
 */
private void initializeComponents() {
    // Panel principal
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(BACKGROUND_COLOR);

    // Sidebar
    sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
    sidebarPanel.setBackground(SIDEBAR_COLOR);
    sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
    sidebarPanel.setPreferredSize(new Dimension(280, 0));

    // Zone de contenu
    contentPanel = new JPanel(new CardLayout());
    contentPanel.setBackground(BACKGROUND_COLOR);
    contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

    // Labels
    welcomeLabel = new JLabel("Bienvenue dans votre Pharmacie");
    statusLabel = new JLabel("Prêt");
}

/**
 * Construit la sidebar, les boutons de navigation et installe les panels de contenu.
 */
private void setupLayout() {
    // Header de la sidebar
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    headerPanel.setBackground(SIDEBAR_COLOR);
    headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

    // Charger l'icône du logo
    ImageIcon logoIcon = new ImageIcon("icons/cross.png");
    // Redimensionner l'icône
    Image originalImage = logoIcon.getImage();
    Image scaledImage = originalImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Redimensionne à 30x30 pixels
    ImageIcon scaledIcon = new ImageIcon(scaledImage);
    JLabel logoLabel = new JLabel(scaledIcon);

    JLabel titleLabel = new JLabel("Sparadrap");
    titleLabel.setFont(new Font("San Francisco", Font.BOLD, 18));
    titleLabel.setForeground(PRIMARY_COLOR);

    headerPanel.add(logoLabel);
    headerPanel.add(Box.createHorizontalStrut(10));
    headerPanel.add(titleLabel);

    sidebarPanel.add(headerPanel);
    sidebarPanel.add(Box.createVerticalStrut(30));

    // Boutons de navigation
    addNavigationButton("Accueil", "accueil", true);
    addNavigationButton("Clients", "clients",  false);
    addNavigationButton("Médecins", "medecins",  false);
    addNavigationButton("Mutuelles", "mutuelles",  false);
    addNavigationButton("Médicaments", "medicaments",  false);
    addNavigationButton("Ventes", "ventes",  false);
    addNavigationButton("Statistiques", "statistiques",  false);

    sidebarPanel.add(Box.createVerticalGlue());

    // Status en bas
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusPanel.setBackground(SIDEBAR_COLOR);
    statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    // Utiliser le champ d'instance statusLabel (éviter l'ombre locale)
    statusLabel.setFont(new Font("San Francisco", Font.PLAIN, 12));
    statusLabel.setForeground(Color.GRAY);

    statusPanel.add(statusLabel);
    sidebarPanel.add(statusPanel);

    // Configuration du layout principal
    mainPanel.add(sidebarPanel, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // Panels de contenu
    setupContentPanels();
    setContentPane(mainPanel);
}

/**
 * Ajoute un bouton de navigation à la sidebar et configure sa sélection et son action.
 */
private void addNavigationButton(String text, String command, boolean selected) {
    // Créer le bouton avec l'icône et le texte
    JButton button = new JButton(text);
    button.setActionCommand(command);
    button.setHorizontalAlignment(SwingConstants.LEFT);
    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
    button.setPreferredSize(new Dimension(250, 45));
    button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    button.setBorder(new EmptyBorder(10, 15, 10, 15));
    button.setFocusPainted(false);

    // Couleurs en fonction de la sélection
    if (selected) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
    } else {
        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(TEXT_COLOR);
    }

    // Effet hover
    button.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            if (!button.getBackground().equals(PRIMARY_COLOR)) {
                button.setBackground(BACKGROUND_COLOR);
            }
        }
        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            if (!button.getBackground().equals(PRIMARY_COLOR)) {
                button.setBackground(SIDEBAR_COLOR);
            }
        }
    });

    // Action du bouton
    button.addActionListener(e -> {
        // Désélectionner tous les autres boutons
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(SIDEBAR_COLOR);
                btn.setForeground(TEXT_COLOR);
            }
        }
        // Sélectionner ce bouton
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        // Changer le contenu
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, command);
        updateStatusLabel("Navigation : " + text);
    });

    sidebarPanel.add(button);
    sidebarPanel.add(Box.createVerticalStrut(5));
}

/**
 * Installe les panels de contenu (accueil, clients, médecins, médicaments, ventes, statistiques).
 */
private void setupContentPanels() {
    // Panel d'accueil (existant)
    JPanel accueilPanel = createAccueilPanel();
    contentPanel.add(accueilPanel, "accueil");

    // Clients
    clientPanelRef = new ClientPanel(controller, this);
    contentPanel.add(clientPanelRef, "clients");

    // Médecins
    MedecinPanel medecinPanel = new MedecinPanel(controller);
    contentPanel.add(medecinPanel, "medecins");

    // Mutuelles
    MutuellePanel mutuellePanel = new MutuellePanel(controller);
    contentPanel.add(mutuellePanel, "mutuelles");

    // Médicaments
    medicamentPanelRef = new MedicamentPanel(controller, this);
    contentPanel.add(medicamentPanelRef, "medicaments");

    VentePanel ventePanel = new VentePanel(controller);
    contentPanel.add(ventePanel, "ventes");

    StatistiquePanel statistiquePanel = new StatistiquePanel(controller);
    contentPanel.add(statistiquePanel, "statistiques");
}

/**
 * Construit le panel d'accueil avec statistiques rapides et actions.
 */
private JPanel createAccueilPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(BACKGROUND_COLOR);

    // Header
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    headerPanel.setBackground(BACKGROUND_COLOR);

    JLabel welcomeLabel = new JLabel("Bienvenue dans votre Pharmacie");
    welcomeLabel.setFont(new Font("San Francisco", Font.BOLD, 24));
    welcomeLabel.setForeground(TEXT_COLOR);
    headerPanel.add(welcomeLabel);

    panel.add(headerPanel, BorderLayout.NORTH);

    // Cards avec statistiques rapides
    JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
    cardsPanel.setBackground(BACKGROUND_COLOR);
    cardsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

    // Card - Ventes du jour
    JLabel[] venteLabelHolder = new JLabel[1];
    JPanel venteCard = createStatsCard("icons/achats.png", "Ventes du jour", "0", "ventes", venteLabelHolder);
    venteCountLabel = venteLabelHolder[0];
    cardsPanel.add(venteCard);

    // Card - Stock
    JLabel[] stockLabelHolder = new JLabel[1];
    JPanel stockCard = createStatsCard("icons/stock.png", "Médicaments en stock", String.valueOf(controller.getNombreMedicamentsEnStock()), "unités", stockLabelHolder);
    stockCountLabel = stockLabelHolder[0];
    cardsPanel.add(stockCard);

    // Card - Clients
    JLabel[] clientLabelHolder = new JLabel[1];
    JPanel clientCard = createStatsCard("icons/clients.png", "Clients enregistrés", String.valueOf(controller.getNombreClients()), "clients", clientLabelHolder);
    clientCountLabel = clientLabelHolder[0];
    cardsPanel.add(clientCard);

    // Card - Chiffre d'affaires
    JLabel[] caLabelHolder = new JLabel[1];
    JPanel caCard = createStatsCard("icons/sales.png", "CA du mois", "0.00 €", "", caLabelHolder);
    caCountLabel = caLabelHolder[0];
    cardsPanel.add(caCard);

    // Actions rapides
    JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
    actionsPanel.setBackground(BACKGROUND_COLOR);

    JLabel actionsLabel = new JLabel("Actions rapides");
    actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    actionsLabel.setForeground(TEXT_COLOR);

    JButton nouvelleVenteBtn = createActionButton("Nouvelle vente", ACCENT_COLOR);
    nouvelleVenteBtn.addActionListener(e -> {
        // Afficher l'onglet Ventes
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "ventes");
        updateStatusLabel("Navigation : Ventes - Nouvelle vente");

        // Sélection visuelle du bouton sidebar "Ventes"
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                boolean isVentes = "ventes".equals(btn.getActionCommand());
                btn.setBackground(isVentes ? PRIMARY_COLOR : SIDEBAR_COLOR);
                btn.setForeground(isVentes ? Color.WHITE : TEXT_COLOR);
            }
        }

        // Tenter de sélectionner l'onglet "Vente Directe" si disponible
        try {
            for (Component comp : contentPanel.getComponents()) {
                if (comp instanceof VentePanel) {
                    VentePanel vp = (VentePanel) comp;
                    // Sélectionner le premier onglet (Vente Directe)
                    java.lang.reflect.Field tabField = VentePanel.class.getDeclaredField("tabbedPane");
                    tabField.setAccessible(true);
                    JTabbedPane tabs = (JTabbedPane) tabField.get(vp);
                    if (tabs != null && tabs.getTabCount() > 0) {
                        tabs.setSelectedIndex(0);
                    }
                    break;
                }
            }
        } catch (Exception ignore) {
            // En cas d'échec de la réflexion, on reste simplement sur le panel Ventes
        }
    });
    JButton rechercherClientBtn = createActionButton("Rechercher client", SECONDARY_COLOR);
    rechercherClientBtn.addActionListener(e -> ouvrirRechercheClientAccueil());
    JButton stockBtn = createActionButton("Gérer stock", PRIMARY_COLOR);
    stockBtn.addActionListener(e -> ouvrirGestionStockAccueil());

    actionsPanel.add(actionsLabel);
    actionsPanel.add(Box.createHorizontalStrut(20));
    actionsPanel.add(nouvelleVenteBtn);
    actionsPanel.add(rechercherClientBtn);
    actionsPanel.add(stockBtn);

    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(cardsPanel, BorderLayout.CENTER);
    panel.add(actionsPanel, BorderLayout.SOUTH);

    // Initialiser avec les vraies données à l'instant T
    try {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Date debutJour = cal.getTime();
        java.util.Date maintenant = new java.util.Date();

        // Début du mois
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        java.util.Date debutMois = cal.getTime();

        java.util.Map<String, Object> statsJour = controller.obtenirStatistiques(debutJour, maintenant);
        java.util.Map<String, Object> statsMois = controller.obtenirStatistiques(debutMois, maintenant);

        int nbVentesJour = (Integer) statsJour.getOrDefault("nombreVentes", 0);
        double caMois = (Double) statsMois.getOrDefault("chiffreAffaires", 0.0);

        if (venteCountLabel != null) {
            venteCountLabel.setText(String.valueOf(nbVentesJour));
        }
        if (caCountLabel != null) {
            caCountLabel.setText(String.format("%.2f €", caMois));
        }
    } catch (Exception e) {
        // En cas d'erreur, laisser les valeurs par défaut
    }

    return panel;
}

/**
 * Crée une carte de statistique avec icône, titre et valeur, et expose l'étiquette.
 */
private JPanel createStatsCard(String iconPath, String title, String value, String unit, JLabel[] outLabel) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
    ));

    // Charger et redimensionner l'icône
    ImageIcon originalIcon = new ImageIcon(iconPath);
    Image img = originalIcon.getImage();
    Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
    ImageIcon icon = new ImageIcon(scaledImg);

    JLabel iconLabel = new JLabel(icon);
    iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    titleLabel.setForeground(Color.GRAY);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    valuePanel.setBackground(Color.WHITE);
    valuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    valueLabel.setForeground(PRIMARY_COLOR);

    JLabel unitLabel = new JLabel(unit.isEmpty() ? "" : " " + unit);
    unitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    unitLabel.setForeground(Color.GRAY);

    valuePanel.add(valueLabel);
    if (!unit.isEmpty()) {
        valuePanel.add(unitLabel);
    }

    card.add(iconLabel);
    card.add(Box.createVerticalStrut(10));
    card.add(titleLabel);
    card.add(Box.createVerticalStrut(5));
    card.add(valuePanel);

    if (outLabel != null) {
        outLabel[0] = valueLabel;
    }

    return card;
}

/**
 * Crée un bouton d'action stylé avec effet hover.
 */
private JButton createActionButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setForeground(Color.WHITE);
    button.setBackground(color);
    button.setBorder(new EmptyBorder(12, 20, 12, 20));
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

/**
 * Applique un style global.
 */
private void setupStyles() {
    // Configuration du look
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Style global
    Font globalFont = new Font("Segoe UI", Font.PLAIN, 13);
    UIManager.put("Label.font", globalFont);
    UIManager.put("Button.font", globalFont);
    UIManager.put("TextField.font", globalFont);
    UIManager.put("TextArea.font", globalFont);
    UIManager.put("ComboBox.font", globalFont);
    UIManager.put("Table.font", globalFont);
    UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
}

/**
 * Pour brancher des listeners supplémentaires si besoin.
 */
private void setupEventListeners() {
    // Listeners généraux (rien à ajouter ici pour l’instant)
}

/**
 * Action Accueil: ouvre le module Clients et place le focus dans le champ de recherche.
 */
private void ouvrirRechercheClientAccueil() {
    // Afficher l’onglet Clients
    CardLayout cl = (CardLayout) contentPanel.getLayout();
    cl.show(contentPanel, "clients");
    updateStatusLabel("Navigation : Clients - Rechercher");

    // Sélection visuelle du bouton sidebar "Clients"
    for (Component comp : sidebarPanel.getComponents()) {
        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            boolean isTarget = "clients".equals(btn.getActionCommand());
            btn.setBackground(isTarget ? PRIMARY_COLOR : SIDEBAR_COLOR);
            btn.setForeground(isTarget ? Color.WHITE : TEXT_COLOR);
        }
    }

    // Donner le focus au champ de recherche du ClientPanel
    if (clientPanelRef != null) {
        try {
            clientPanelRef.focusRecherche();
        } catch (Exception ignore) {}
    }
}

/**
 * Action Accueil: ouvre le module Médicaments et place le focus dans le champ de recherche.
 */
private void ouvrirGestionStockAccueil() {
    // Afficher l’onglet Médicaments
    CardLayout cl = (CardLayout) contentPanel.getLayout();
    cl.show(contentPanel, "medicaments");
    updateStatusLabel("Navigation : Médicaments - Gérer stock");

    // Sélection visuelle du bouton sidebar "Médicaments"
    for (Component comp : sidebarPanel.getComponents()) {
        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            boolean isTarget = "medicaments".equals(btn.getActionCommand());
            btn.setBackground(isTarget ? PRIMARY_COLOR : SIDEBAR_COLOR);
            btn.setForeground(isTarget ? Color.WHITE : TEXT_COLOR);
        }
    }

    // Donner le focus au champ de recherche
    if (medicamentPanelRef != null) {
        try {
            medicamentPanelRef.focusRecherche();
        } catch (Exception ignore) {}
    }
}
    @Override
    public void refreshClientCount() {
        SwingUtilities.invokeLater(() -> {
            if (clientCountLabel != null) {
                try {
                    int count = controller.getNombreClients();
                    clientCountLabel.setText(String.valueOf(count));
                } catch (Exception e) {
                    System.err.println("Erreur actualisation clients: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void refreshMedecinCount() {

    }


/**
 * Met à jour l'affichage du nombre de ventes du jour.
 */
public void refreshVenteCount(int nombreVentes) {
    SwingUtilities.invokeLater(() -> {
        if (venteCountLabel != null) {
            venteCountLabel.setText(String.valueOf(nombreVentes));
        }
    });
}

    @Override
    public void refreshStockCount() {
        SwingUtilities.invokeLater(() -> {
            if (stockCountLabel != null) {
                try {
                    int count = controller.getNombreMedicamentsEnStock();
                    stockCountLabel.setText(String.valueOf(count));
                } catch (Exception e) {
                    System.err.println("Erreur actualisation stock: " + e.getMessage());
                }
            }
        });
    }

/**
 * Met à jour l'affichage du chiffre d'affaires.
 */
public void refreshCaCount(String caValue) {
    SwingUtilities.invokeLater(() -> {
        if (caCountLabel != null) {
            caCountLabel.setText(caValue);
        }
    });
}

    @Override
    public void refreshAllCounts() {
        DataRefreshListener.super.refreshAllCounts();
    }

    @Override
    public void dispose() {
        DataEventManager.getInstance().removeListener(this);
        super.dispose();
    }


    private void initialiserPharmacienDemo() {
        try {
            // Créer un pharmacien pour la démo
            Pharmacien pharmacien = new Pharmacien(
                    "Durand", "Sophie", "123 Rue de la Pharmacie", "75000", "Paris",
                    "0123456789", "sophie.durand@pharmacie.fr", "PHARM001",
                    "12345678901", "Pharmacien titulaire", new Date()
            );
            controller.setPharmacienConnecte(pharmacien);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du pharmacien : " + e.getMessage());
        }
    }

    public void refreshAccueilPanel() {
        Component[] components = contentPanel.getComponents();
        for (Component component : components) {
            if ("accueil".equals(((JPanel) component).getName())) {
                contentPanel.remove(component);
                break;
            }
        }

        // Ajouter la nouvelle carte "accueil"
        JPanel accueilPanel = createAccueilPanel();
        accueilPanel.setName("accueil");
        contentPanel.add(accueilPanel, "accueil");

        // Rafraîchir l'affichage
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateStatusLabel(String message) {
        statusLabel.setText(message);
        // Auto-clear après 3 secondes
        Timer timer = new Timer(3000, e -> statusLabel.setText("Prêt"));
        timer.setRepeats(false);
        timer.start();
    }

    public void showMessage(String message, boolean isError) {
        Color color = isError ? ERROR_COLOR : ACCENT_COLOR;
        String prefix = isError ? "❌ " : "✅ ";

        // Afficher dans la bar de status
        statusLabel.setText(prefix + message);
        statusLabel.setForeground(color);

        // Se remet a l'etat initial après 5 secondes
        Timer timer = new Timer(5000, e -> {
            statusLabel.setText("Prêt");
            statusLabel.setForeground(Color.GRAY);
        });
        timer.setRepeats(false);
        timer.start();
    }

    public PharmacieController getController() {
        return controller;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PharmacieMainFrame().setVisible(true);
        });
    }
}
