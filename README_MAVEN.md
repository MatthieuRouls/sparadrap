# Sparadrap Pharmacie - Configuration Maven & MySQL

## Prérequis

- **JDK 21** ou supérieur
- **Maven 3.8+** ([Télécharger Maven](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Télécharger MySQL](https://dev.mysql.com/downloads/))
- Un IDE Java (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Structure du projet Maven

```
sparadrap/
├── pom.xml                          # Configuration Maven
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── main/                # Code source de l'application
│   │   │       ├── Main.java        # Point d'entrée
│   │   │       ├── model/           # Modèles métier
│   │   │       │   └── database/    # Gestion de la base de données
│   │   │       ├── view/            # Interfaces graphiques (Swing)
│   │   │       └── controller/      # Contrôleurs
│   │   └── resources/
│   │       ├── database.properties  # Configuration MySQL
│   │       └── sql/
│   │           └── init_database.sql # Script d'initialisation DB
│   └── test/
│       └── java/                    # Tests unitaires
├── data/                            # Données CSV de test
└── icons/                           # Icônes de l'application
```

## Installation et Configuration

### 1. Vérifier l'installation de Maven

```bash
mvn --version
```

Si Maven n'est pas installé, suivez les instructions sur [maven.apache.org](https://maven.apache.org/install.html).

### 2. Configuration de MySQL

#### Installation de MySQL

- **Windows/Mac**: Téléchargez et installez MySQL Server depuis [dev.mysql.com](https://dev.mysql.com/downloads/)
- **Linux (Ubuntu/Debian)**:
  ```bash
  sudo apt update
  sudo apt install mysql-server
  sudo systemctl start mysql
  ```

#### Démarrer le serveur MySQL

- **Windows**: Le service démarre automatiquement
- **Mac**: `mysql.server start` ou via System Preferences
- **Linux**: `sudo systemctl start mysql`

#### Créer un utilisateur MySQL (optionnel)

```sql
-- Se connecter à MySQL en tant que root
mysql -u root -p

-- Créer un utilisateur pour l'application (optionnel)
CREATE USER 'sparadrap_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON sparadrap_db.* TO 'sparadrap_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configuration de la connexion à la base de données

Modifiez le fichier `src/main/resources/database.properties` selon votre configuration MySQL :

```properties
# URL de connexion MySQL (ajustez le port si nécessaire)
db.url=jdbc:mysql://localhost:3306/sparadrap_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# Identifiants MySQL
db.username=root
db.password=votre_mot_de_passe

# Nom de la base
db.name=sparadrap_db

# Configuration du pool de connexions (valeurs recommandées)
db.pool.maximumPoolSize=10
db.pool.minimumIdle=5
db.pool.connectionTimeout=30000
db.pool.idleTimeout=600000
db.pool.maxLifetime=1800000
```

⚠️ **Important**: Ne committez jamais vos mots de passe réels dans Git!

## Commandes Maven

### Compiler le projet

```bash
mvn clean compile
```

### Exécuter les tests

```bash
mvn test
```

### Créer le package JAR

```bash
mvn clean package
```

Cela génère deux fichiers JAR dans `target/`:
- `sparadrap-pharmacie-1.0.0.jar` - JAR simple
- `sparadrap-pharmacie-1.0.0-jar-with-dependencies.jar` - JAR exécutable avec toutes les dépendances

### Installer dans le repository local Maven

```bash
mvn clean install
```

### Nettoyer le projet

```bash
mvn clean
```

## Initialisation de la base de données

### Option 1: Utiliser la classe Java DatabaseInitializer

```bash
# Compiler le projet
mvn clean compile

# Tester la connexion
mvn exec:java -Dexec.mainClass="main.model.database.DatabaseInitializer" -Dexec.args="test"

# Initialiser la base de données
mvn exec:java -Dexec.mainClass="main.model.database.DatabaseInitializer" -Dexec.args="init"
```

### Option 2: Exécuter le script SQL manuellement

```bash
# Se connecter à MySQL
mysql -u root -p

# Exécuter le script
source src/main/resources/sql/init_database.sql
```

Ou en une seule commande:
```bash
mysql -u root -p < src/main/resources/sql/init_database.sql
```

## Exécution de l'application

### Avec Maven

```bash
mvn clean compile exec:java -Dexec.mainClass="main.Main"
```

### Avec le JAR généré

```bash
# Après avoir exécuté mvn package
java -jar target/sparadrap-pharmacie-1.0.0-jar-with-dependencies.jar
```

### Depuis votre IDE

1. Ouvrez le projet dans votre IDE
2. Importez-le en tant que projet Maven
3. Attendez que Maven télécharge toutes les dépendances
4. Exécutez la classe `main.Main`

## Dépendances Maven

Le projet utilise les dépendances suivantes (définies dans `pom.xml`):

- **MySQL Connector/J 8.2.0** - Driver JDBC pour MySQL
- **HikariCP 5.1.0** - Pool de connexions haute performance
- **JUnit Jupiter 5.10.1** - Framework de tests unitaires
- **SLF4J 2.0.9** - API de logging

## Gestion de la connexion à la base de données

### Classes principales

1. **DatabaseConfig** (`main.model.database.DatabaseConfig`)
   - Singleton qui charge la configuration depuis `database.properties`
   - Gère tous les paramètres de connexion

2. **DatabaseConnection** (`main.model.database.DatabaseConnection`)
   - Singleton qui gère le pool de connexions HikariCP
   - Fournit des connexions via `getConnection()`
   - Se ferme automatiquement à l'arrêt de l'application

3. **DatabaseInitializer** (`main.model.database.DatabaseInitializer`)
   - Utilitaire pour initialiser/tester la base de données
   - Peut être exécuté en ligne de commande

### Utilisation dans votre code

```java
import main.model.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class ExampleDAO {
    public void exempleRequete() {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();

        try (Connection conn = dbConn.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {

            while (rs.next()) {
                String nom = rs.getString("nom");
                System.out.println(nom);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

## Structure de la base de données

Le script `init_database.sql` crée les tables suivantes:

- **mutuelles** - Organismes de mutuelle
- **clients** - Clients de la pharmacie
- **medecins** - Médecins prescripteurs
- **categories_medicaments** - Catégories de médicaments
- **medicaments** - Inventaire des médicaments
- **ordonnances** - Ordonnances médicales
- **lignes_ordonnances** - Détails des médicaments prescrits
- **achats** - Transactions de vente
- **lignes_achats** - Détails des achats

## Dépannage

### Erreur: "Access denied for user"

Vérifiez vos identifiants dans `database.properties` et assurez-vous que l'utilisateur MySQL a les permissions nécessaires.

```sql
GRANT ALL PRIVILEGES ON sparadrap_db.* TO 'votre_user'@'localhost';
FLUSH PRIVILEGES;
```

### Erreur: "Communications link failure"

- Vérifiez que le serveur MySQL est démarré
- Vérifiez le port (par défaut 3306) dans l'URL de connexion
- Sur Linux, vérifiez: `sudo systemctl status mysql`

### Erreur: "Public Key Retrieval is not allowed"

Ajoutez `allowPublicKeyRetrieval=true` à l'URL JDBC (déjà présent dans la config par défaut).

### Maven ne trouve pas les dépendances

```bash
# Forcer le téléchargement des dépendances
mvn dependency:purge-local-repository
mvn clean install
```

### Erreur de compilation

Vérifiez que vous utilisez JDK 21:
```bash
java -version
mvn -version
```

Si nécessaire, configurez la variable `JAVA_HOME`:
- **Windows**: Panneau de configuration > Variables d'environnement
- **Linux/Mac**: Ajoutez dans `.bashrc` ou `.zshrc`:
  ```bash
  export JAVA_HOME=/chemin/vers/jdk-21
  export PATH=$JAVA_HOME/bin:$PATH
  ```

## Migration depuis le projet IntelliJ IDEA

Le projet a été migré de la structure IntelliJ vers Maven. Les changements principaux:

1. ✅ Ajout du fichier `pom.xml` avec toutes les dépendances
2. ✅ Réorganisation en structure Maven standard (`src/main/java`, `src/main/resources`)
3. ✅ Migration de JUnit 4/5 vers JUnit 5 (Jupiter)
4. ✅ Ajout des classes de gestion de base de données MySQL
5. ✅ Configuration HikariCP pour le pool de connexions
6. ✅ Script SQL d'initialisation de la base

## Support et Documentation

- [Documentation Maven](https://maven.apache.org/guides/)
- [Documentation MySQL](https://dev.mysql.com/doc/)
- [Documentation HikariCP](https://github.com/brettwooldridge/HikariCP)
- [Documentation JDBC](https://docs.oracle.com/javase/tutorial/jdbc/)

---

**Projet Sparadrap - Application de gestion de pharmacie**
Version Maven 1.0.0
