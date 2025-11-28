# Pharmacie Sparadrap — Documentation Technique

**Application Java Swing de gestion complète pour pharmacie**
*(Clients, médecins, médicaments, ventes et statistiques)*

---

## Prérequis
   Élément               | Version/Type                     |
 |-----------------------|----------------------------------|
| JDK                   | 21                               |
| IDE                   | IntelliJ IDEA (ou autre IDE Java)|
| Base de données       | MySQL 8.0+                       |
| Système d'exploitation| Windows, macOS ou Linux          |

---

## Modèle de Données

### Hiérarchie des Classes

- **Personne** (abstraite)
    - Client : patients avec mutuelle optionnelle et médecin traitant
    - Medecin : prescripteurs avec numéro RPPS
    - Pharmacien : gestionnaires avec spécialité

- **Organisme** (abstraite)
    - Mutuelle : organismes de remboursement (taux en %)

- **Document** (abstraite)
    - Ordonnance : prescriptions médicales

- **Transaction** (abstraite)
    - Achat : ventes directes ou sur ordonnance

- **Medicament** : produits avec stock, prix, dates de péremption

---

## Connexion à la Base de Données

### Configuration Actuelle

- **Stockage en mémoire** : Toutes les données sont chargées et manipulées en RAM via des structures Java (`Maps`, `Lists`).
- **Persistance CSV minimale** : Les achats sont sauvegardés dans `data/achats.csv` pour les statistiques.

#### Avantages
✅ Démarrage rapide sans dépendance externe
✅ Idéal pour démonstration et prototypage
✅ Pas de configuration JDBC complexe

#### Limitations
⚠️ Données perdues au redémarrage (sauf achats)
⚠️ Non adapté pour usage production

---

### Migration vers MySQL (Planifiée)

#### 1. Configuration de la Connexion
```java
// config/DatabaseConfig.java
public class DatabaseConfig {
    private static final String URL = "jdbc\:mysql://localhost:3306/pharmacie_sparadrap";
    private static final String USER = "root";
    private static final String PASSWORD = "votre_mdp";

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

```

#### 2. Schéma de Base de Données
```sql

CREATE DATABASE pharmacie_sparadrap CHARACTER SET utf8mb4;
USE pharmacie_sparadrap;

-- Tables principales
CREATE TABLE mutuelles (
    nom VARCHAR(100) PRIMARY KEY,
    code_postal VARCHAR(10) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    taux_remboursement DECIMAL(5,2) NOT NULL CHECK (taux_remboursement BETWEEN 0 AND 100)
);

CREATE TABLE medecins (
    numero_rpps VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    adresse VARCHAR(200),
    code_postal VARCHAR(10),
    ville VARCHAR(100),
    telephone VARCHAR(20),
    email VARCHAR(100),
    identifiant VARCHAR(20) UNIQUE
);

-- ... (autres tables : clients, medicaments, ordonnances, achats, etc.)
```

#### 3. Pattern DAO pour les Requêtes
```java

// dao/ClientDAO.java
public class ClientDAO {
    public void ajouter(Client client) throws SQLException {
        String sql = "INSERT INTO clients VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getIdentifiant());
            stmt.setString(2, client.getNom());
            // ... autres paramètres
            stmt.executeUpdate();
        }
    }

    public Optional<Client> rechercherParId(String id) throws SQLException {
        String sql = "SELECT * FROM clients WHERE identifiant = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        }
        return Optional.empty();
    }
}
```


## Gestion des Relations

### Relations Actuelles (En Mémoire)

- **Client → Mutuelle** (Many-to-One)
- **Client → Médecin Traitant** (Many-to-One)
- **Ordonnance → Médicaments** (Many-to-Many)
- **Achat → Médicaments** (Many-to-Many avec quantités)

### Relations avec MySQL (Architecture Prévue)

- **Clés étrangères et contraintes** : Assurent l'intégrité référentielle.
- **Tables de liaison** pour les relations Many-to-Many.

---

## Démarrage Rapide

### Lancement de l'Application

1. Ouvrir le projet dans IntelliJ IDEA.
2. Configurer le JDK 21 dans Project Structure.
3. Lancer `main.Main.java` ou `main.view.PharmacieMainFrame.java`.
4. L'application démarre avec des données de démonstration.

### Interface Utilisateur

- **Tableau de bord** : Statistiques en temps réel (ventes du jour, stock, clients, CA).
- **Actions rapides** : Nouvelle vente, Rechercher client, Gérer stock.
- **Navigation** : Sidebar gauche avec 6 modules (Accueil, Clients, Médecins, Médicaments, Ventes, Statistiques).

---

## Fonctionnalités Principales

### Gestion des Clients

- Génération automatique d'identifiant.
- Association optionnelle avec mutuelle.
- Assignation de médecin traitant.
- Validation stricte (email, téléphone, n° sécurité sociale).

### Gestion des Ventes

- **Vente directe** : Panier interactif avec vérification stock temps réel.
- **Vente sur ordonnance** : Validation patient + prescripteur, gestion des quantités prescrites.

### Statistiques en Temps Réel

- CA par période (jour/mois/année).
- Nombre de ventes.
- Stock total et ruptures.
- Montants remboursés et bénéfice net.

### Système d'Événements

- Actualisation automatique après opération.

---

## Jeu de Données de Test

**clients.csv** et **medecins.csv** : Données de référence pour saisie rapide via l'UI.

---

## Sécurité et Validation

**SecurityValidator** : Classe centrale de validation avec règles strictes (email, téléphone, code postal, n° sécu, RPPS, prix, stock, taux remboursement).

---

## Tests

### Structure des Tests

```
src/test/
├── GestPharmacieServiceTest.java    # Tests service métier
└── model/
    ├── AchatTest.java               # Transactions
    ├── MedecinTest.java             # Gestion patients
    ├── MedicamentTest.java          # Gestion stock
    ├── MutuelleTest.java            # Organismes
    └── OrdonnanceTest.java          # Prescriptions
```

### Exécution

- **Via IntelliJ** : Run > All Tests
- **Via Maven** : `mvn test`

---

## Roadmap Technique

| Échéance | Tâches |
|----------|--------|
| Court terme | Connexion MySQL avec HikariCP, Migration progressive des DAOs |
| Moyen terme | Export PDF des ordonnances, Statistiques avancées (graphiques) |
| Long terme | API REST pour intégration externe, Application mobile |

---

## Dépannage

### Problèmes Courants

| Problème | Solution |
|----------|----------|
| L'UI ne s'ouvre pas | Vérifier la configuration du JDK 21 et rebuild le projet. |
| Icônes manquantes | S'assurer que le dossier `icons/` est présent à la racine. |
| Données non persistées | Normal : stockage mémoire par défaut (seuls les achats sont sauvegardés). |
| Erreurs de validation | Consulter `SecurityValidator.java` pour les patterns attendus. |

---

## Architecture MVC

```
View (Swing)          Controller              Model (Métier)         Service (DAO)
     ↓                     ↓                         ↓                      ↓
ClientPanel  →  PharmacieController  →  Client, Mutuelle, etc.  →  GestPharmacieService
                                                                           ↓
                                                                    En mémoire (actuel)
                                                                    ou MySQL (futur)
```





