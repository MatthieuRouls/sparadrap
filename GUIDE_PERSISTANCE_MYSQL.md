# Guide de Persistance MySQL - Sparadrap

## 🎯 Objectif

Ce guide explique comment l'application Sparadrap persiste les données (clients, médecins, mutuelles) dans une base de données MySQL.

## 📋 Prérequis

Avant d'utiliser la persistance, vous devez :

1. **Avoir MySQL installé et démarré**
2. **Avoir initialisé la base de données** (voir ci-dessous)
3. **Avoir configuré** `src/main/resources/database.properties`

## 🗄️ Initialisation de la Base de Données

### Étape 1 : Configurer MySQL

Éditez `src/main/resources/database.properties` :

```properties
db.url=jdbc:mysql://localhost:3306/sparadrap_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=votre_mot_de_passe
```

### Étape 2 : Créer la Base de Données

**Option A : Via le script SQL**

```bash
mysql -u root -p < src/main/resources/sql/init_database.sql
```

**Option B : Via DatabaseInitializer**

```bash
mvn exec:java -Dexec.mainClass="main.model.database.DatabaseInitializer" -Dexec.args="init"
```

### Étape 3 : Vérifier la Connexion

```bash
mvn exec:java -Dexec.mainClass="main.model.database.DatabaseInitializer" -Dexec.args="test"
```

Vous devriez voir :
```
✓ Test de connexion réussi!
```

## 🏗️ Architecture de la Persistance

### Classes DAO (Data Access Object)

L'application utilise le pattern DAO pour gérer la persistance :

```
src/main/java/main/model/dao/
├── ClientDAO.java        ← Gestion des clients
├── MedecinDAO.java       ← Gestion des médecins
└── MutuelleDAO.java      ← Gestion des mutuelles
```

### Service Métier

`GestPharmacieService` utilise les DAO pour persister les données :

```java
public class GestPharmacieService {
    private final ClientDAO clientDAO;
    private final MedecinDAO medecinDAO;
    private final MutuelleDAO mutuelleDAO;

    // Les opérations sont automatiquement persistées
    public void ajouterClient(Client client) {
        clientDAO.create(client);  // ← Sauvegarde en BDD
    }
}
```

## 💻 Utilisation dans l'Application

### Créer un Client

```java
GestPharmacieService service = new GestPharmacieService();

Client client = new Client(
    "Dupont",           // nom
    "Jean",             // prenom
    "10 Rue de Paris",  // adresse
    "75001",            // code postal
    "Paris",            // ville
    "0612345678",       // téléphone
    "jean@example.com", // email
    "JEDUPONT01",       // identifiant
    "123456789012345",  // numéro sécu
    mutuelle,           // mutuelle (ou null)
    null                // médecin traitant (ou null)
);

service.ajouterClient(client);  // ← Automatiquement sauvegardé en BDD
```

✅ **Le client est maintenant persisté dans la table `clients` de MySQL !**

### Créer un Médecin

```java
Medecin medecin = new Medecin(
    "Martin",
    "Sophie",
    "5 Avenue République",
    "69002",
    "Lyon",
    "0478123456",
    "sophie.martin@cabinet.fr",
    "smartin01",
    "12345678901"  // Numéro RPPS (11 chiffres)
);

service.ajouterMedecin(medecin);  // ← Sauvegardé en BDD
```

### Créer une Mutuelle

```java
Mutuelle mutuelle = new Mutuelle(
    "Mutuelle Santé Plus",
    "20 Rue de la Santé",
    "75013",
    "Paris",
    "0140000000",
    "contact@mutuelle.fr",
    75.0  // Taux de remboursement en %
);

service.ajouterMutuelle(mutuelle);  // ← Sauvegardé en BDD
```

### Rechercher un Client

```java
Optional<Client> clientOpt = service.rechercherClient("JEDUPONT01");

if (clientOpt.isPresent()) {
    Client client = clientOpt.get();
    System.out.println("Client trouvé : " + client.getNom());
} else {
    System.out.println("Client non trouvé");
}
```

### Modifier un Client

```java
Optional<Client> clientOpt = service.rechercherClient("JEDUPONT01");

if (clientOpt.isPresent()) {
    Client client = clientOpt.get();
    client.setNumTelephone("0699999999");  // Modifier

    service.modifierClient(client);  // ← Mise à jour en BDD
}
```

### Supprimer un Client

```java
boolean deleted = service.supprimerClient("JEDUPONT01");

if (deleted) {
    System.out.println("Client supprimé");
} else {
    System.out.println("Client non trouvé");
}
```

### Lister Tous les Clients

```java
Collection<Client> clients = service.getTousClients();

for (Client c : clients) {
    System.out.println(c.getPrenom() + " " + c.getNom());
}
```

## 🧪 Tester la Persistance

### Exécuter la Démonstration

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="main.model.demo.PersistenceDemo"
```

Cette démonstration va :

1. ✅ Créer une mutuelle en base
2. ✅ Créer un médecin en base
3. ✅ Créer un client en base
4. ✅ Modifier ces entités
5. ✅ Les rechercher
6. ✅ Afficher les statistiques

### Vérifier dans MySQL

```bash
mysql -u root -p

USE sparadrap_db;

# Voir tous les clients
SELECT * FROM clients;

# Voir tous les médecins
SELECT * FROM medecins;

# Voir toutes les mutuelles
SELECT * FROM mutuelles;

# Compter les enregistrements
SELECT COUNT(*) FROM clients;
```

## 📊 Tables de la Base de Données

### Table `clients`

| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant auto-incrémenté |
| identifiant | VARCHAR(50) | Identifiant unique du client |
| nom | VARCHAR(100) | Nom du client |
| prenom | VARCHAR(100) | Prénom du client |
| adresse | VARCHAR(255) | Adresse |
| code_postal | VARCHAR(10) | Code postal |
| ville | VARCHAR(100) | Ville |
| telephone | VARCHAR(20) | Téléphone |
| email | VARCHAR(100) | Email |
| numero_secu | VARCHAR(15) | Numéro de sécurité sociale |
| mutuelle_id | INT | ID de la mutuelle (clé étrangère) |
| date_creation | TIMESTAMP | Date de création |
| date_modification | TIMESTAMP | Date de dernière modification |

### Table `medecins`

| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant auto-incrémenté |
| identifiant | VARCHAR(50) | Identifiant unique du médecin |
| nom | VARCHAR(100) | Nom |
| prenom | VARCHAR(100) | Prénom |
| adresse | VARCHAR(255) | Adresse |
| code_postal | VARCHAR(10) | Code postal |
| ville | VARCHAR(100) | Ville |
| telephone | VARCHAR(20) | Téléphone |
| email | VARCHAR(100) | Email |
| numero_rpps | VARCHAR(11) | Numéro RPPS (unique) |
| date_creation | TIMESTAMP | Date de création |

### Table `mutuelles`

| Colonne | Type | Description |
|---------|------|-------------|
| id | INT | Identifiant auto-incrémenté |
| nom | VARCHAR(100) | Nom de la mutuelle (unique) |
| adresse | VARCHAR(255) | Adresse |
| code_postal | VARCHAR(10) | Code postal |
| ville | VARCHAR(100) | Ville |
| telephone | VARCHAR(20) | Téléphone |
| email | VARCHAR(100) | Email |
| taux_remboursement | DECIMAL(5,2) | Taux en % |
| date_creation | TIMESTAMP | Date de création |

## 🔍 Logs de Persistance

Tous les logs sont enregistrés dans `logs/sparadrap.log` :

```bash
# Voir les logs de persistance
tail -f logs/sparadrap.log

# Filtrer par opération
grep "créé avec succès" logs/sparadrap.log
grep "ERROR" logs/sparadrap.log
```

Exemple de logs :

```
2025-11-14 14:30:15.123 INFO  [main] main.model.service.GestPharmacieService - Ajout du client: JEDUPONT01
2025-11-14 14:30:15.145 DEBUG [main] main.model.dao.ClientDAO - Création d'un nouveau client: Jean Dupont
2025-11-14 14:30:15.167 INFO  [main] main.model.dao.ClientDAO - Client 'JEDUPONT01' créé avec succès
2025-11-14 14:30:15.170 INFO  [main] main.model.service.GestPharmacieService - Client 'JEDUPONT01' ajouté avec succès en base de données
```

## ⚠️ Gestion des Erreurs

### Client déjà existant

```java
try {
    service.ajouterClient(client);
} catch (RuntimeException e) {
    System.err.println("Erreur : " + e.getMessage());
    // "Un client avec cet identifiant existe déjà"
}
```

### Erreur de connexion MySQL

```java
try {
    service.getTousClients();
} catch (RuntimeException e) {
    System.err.println("Erreur de base de données : " + e.getMessage());
    // Vérifier que MySQL est démarré
    // Vérifier database.properties
}
```

## 🔄 Migration depuis l'Ancienne Version

**Avant** (stockage en mémoire) :
```java
// Les données étaient perdues à la fermeture de l'application
Map<String, Client> clients = new HashMap<>();
clients.put(client.getIdentifiant(), client);
```

**Maintenant** (stockage en base de données) :
```java
// Les données sont persistées dans MySQL
service.ajouterClient(client);
// ← Sauvegardé définitivement
```

## 📈 Avantages de la Persistance

| Avant | Maintenant |
|-------|------------|
| ❌ Données perdues au redémarrage | ✅ Données persistées |
| ❌ Pas d'historique | ✅ Historique complet (date_creation, date_modification) |
| ❌ Diffic ile à partager | ✅ Base partageable |
| ❌ Pas de requêtes complexes | ✅ SQL disponible pour statistiques |
| ❌ Limité à la RAM | ✅ Scalable (plusieurs Go de données) |

## 🎓 Exemple Complet

```java
import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.service.GestPharmacieService;

public class ExemplePersistance {
    public static void main(String[] args) {
        // Initialiser le service (se connecte à MySQL)
        GestPharmacieService service = new GestPharmacieService();

        // 1. Créer une mutuelle
        Mutuelle mutuelle = new Mutuelle(
            "Mutuelle Santé", "10 Rue Santé", "75013", "Paris",
            "0140000000", "contact@mutuelle.fr", 75.0
        );
        service.ajouterMutuelle(mutuelle);

        // 2. Créer un médecin
        Medecin medecin = new Medecin(
            "Dupont", "Marie", "5 Avenue République", "69002", "Lyon",
            "0478123456", "marie@cabinet.fr", "mdupont", "12345678901"
        );
        service.ajouterMedecin(medecin);

        // 3. Créer un client
        Client client = new Client(
            "Martin", "Jean", "20 Rue Fleurs", "75012", "Paris",
            "0601020304", "jean@example.com", "JEMAR01",
            "123456789012345", mutuelle, medecin
        );
        service.ajouterClient(client);

        // 4. Rechercher le client (même après redémarrage !)
        service.rechercherClient("JEMAR01").ifPresent(c ->
            System.out.println("Client trouvé : " + c.getNom())
        );

        System.out.println("Total clients en base : " + service.getNombreClients());
    }
}
```

## 📚 Ressources

- Script SQL : `src/main/resources/sql/init_database.sql`
- Configuration : `src/main/resources/database.properties`
- DAO : `src/main/java/main/model/dao/`
- Démo : `src/main/java/main/model/demo/PersistenceDemo.java`
- Logs : `logs/sparadrap.log`

---

**Félicitations ! Votre application Sparadrap persiste maintenant toutes les données dans MySQL ! 🎉**
