# Guide d'Utilisation du Logger - Sparadrap

## 🎯 Objectif

Ce guide vous montre comment utiliser le système de logging configuré dans l'application Sparadrap.

## 📋 Table des Matières

1. [Démarrage Rapide](#démarrage-rapide)
2. [Déclaration du Logger](#déclaration-du-logger)
3. [Niveaux de Log](#niveaux-de-log)
4. [Exemples Pratiques](#exemples-pratiques)
5. [Où Consulter les Logs](#où-consulter-les-logs)
6. [Configuration](#configuration)

---

## Démarrage Rapide

### 1. Tester le Logger

```bash
# Compiler le projet
mvn clean compile

# Exécuter la démo
mvn exec:java -Dexec.mainClass="main.model.demo.LoggerDemo"

# Consulter les logs générés
cat logs/sparadrap.log
```

### 2. Intégrer dans Vos Classes

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaClasse {
    // Déclarer le logger (toujours en haut de la classe)
    private static final Logger logger = LoggerFactory.getLogger(MaClasse.class);

    public void maMethode() {
        logger.info("Message de log");
    }
}
```

---

## Déclaration du Logger

### ✅ Bonne Pratique

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    // Logger statique, privé, final
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    // Reste de la classe...
}
```

### 📝 Explications

- `private` : Le logger ne doit pas être accessible depuis l'extérieur
- `static` : Un seul logger par classe (pas un par instance)
- `final` : Le logger ne change jamais une fois créé
- `LoggerFactory.getLogger(MaClasse.class)` : Crée un logger nommé d'après la classe

---

## Niveaux de Log

### Tableau Récapitulatif

| Niveau | Quand l'utiliser | Exemple |
|--------|------------------|---------|
| `TRACE` | Détails extrêmes (rarement utilisé) | `logger.trace("Entrée méthode avec param={}", param);` |
| `DEBUG` | Informations de débogage | `logger.debug("Valeur calculée: {}", valeur);` |
| `INFO` | Informations importantes | `logger.info("Connexion établie");` |
| `WARN` | Avertissements non bloquants | `logger.warn("Stock faible: {}", stock);` |
| `ERROR` | Erreurs critiques | `logger.error("Erreur: {}", e.getMessage(), e);` |

### Hiérarchie

```
TRACE < DEBUG < INFO < WARN < ERROR
```

Si vous configurez le niveau à `INFO`, vous verrez : INFO, WARN, ERROR (mais pas DEBUG ni TRACE).

---

## Exemples Pratiques

### 1. Logs Simples

```java
// Message simple
logger.info("Application démarrée");

// Message avec une variable
String utilisateur = "admin";
logger.info("Connexion de l'utilisateur: {}", utilisateur);

// Message avec plusieurs variables
logger.info("Transaction: Client={}, Montant={}", clientId, montant);
```

### 2. Logging dans la Base de Données

#### Exemple : DatabaseConnection.java

```java
package main.model.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    public Connection getConnection() throws SQLException {
        logger.debug("Tentative de récupération d'une connexion depuis le pool");

        try {
            Connection conn = dataSource.getConnection();
            logger.debug("Connexion obtenue - Active: {}, Idle: {}",
                        dataSource.getHikariPoolMXBean().getActiveConnections(),
                        dataSource.getHikariPoolMXBean().getIdleConnections());
            return conn;

        } catch (SQLException e) {
            logger.error("Impossible d'obtenir une connexion: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean testConnection() {
        logger.info("Test de connexion à la base de données...");

        try (Connection conn = getConnection()) {
            boolean isValid = conn.isValid(5);

            if (isValid) {
                logger.info("✓ Test de connexion réussi");
            } else {
                logger.error("✗ Test de connexion échoué");
            }

            return isValid;

        } catch (SQLException e) {
            logger.error("Erreur lors du test de connexion", e);
            return false;
        }
    }
}
```

### 3. Logging dans les Services

#### Exemple : GestPharmacieService.java

```java
package main.model.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GestPharmacieService {
    private static final Logger logger = LoggerFactory.getLogger(GestPharmacieService.class);

    public void ajouterClient(Client client) {
        logger.info("Ajout d'un nouveau client: {} {}", client.getPrenom(), client.getNom());
        logger.debug("Détails client - ID: {}, Email: {}", client.getIdentifiant(), client.getEmail());

        try {
            // Validation
            if (client.getIdentifiant() == null) {
                logger.warn("Client sans identifiant, génération automatique...");
                client.genererIdentifiant();
            }

            // Sauvegarde
            clients.add(client);
            logger.info("Client ajouté avec succès - ID: {}", client.getIdentifiant());

        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du client: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void effectuerVente(String clientId, List<LigneAchat> lignes) {
        logger.info("Nouvelle vente - Client: {}, Nombre d'articles: {}", clientId, lignes.size());

        double total = 0.0;
        for (LigneAchat ligne : lignes) {
            logger.debug("  - {} x {} = {}€",
                        ligne.getMedicament().getNom(),
                        ligne.getQuantite(),
                        ligne.getMontant());
            total += ligne.getMontant();

            // Vérifier le stock
            if (ligne.getMedicament().getStock() < ligne.getQuantite()) {
                logger.warn("Stock insuffisant pour {}: demandé={}, disponible={}",
                           ligne.getMedicament().getNom(),
                           ligne.getQuantite(),
                           ligne.getMedicament().getStock());
            }
        }

        logger.info("Vente validée - Total: {}€", total);
    }
}
```

### 4. Logging dans les Contrôleurs

#### Exemple : PharmacieController.java

```java
package main.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PharmacieController {
    private static final Logger logger = LoggerFactory.getLogger(PharmacieController.class);

    public void initialiser() {
        logger.info("Initialisation du contrôleur Pharmacie");

        try {
            chargerDonnees();
            logger.info("Données chargées avec succès");

        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation", e);
            throw new RuntimeException("Échec de l'initialisation", e);
        }
    }

    private void chargerDonnees() {
        logger.debug("Chargement des données depuis la base...");
        long startTime = System.currentTimeMillis();

        // Chargement...

        long duration = System.currentTimeMillis() - startTime;
        logger.debug("Données chargées en {} ms", duration);

        if (duration > 2000) {
            logger.warn("Temps de chargement élevé: {} ms", duration);
        }
    }
}
```

### 5. Logging des Exceptions

```java
try {
    // Code qui peut lever une exception
    connexion.execute(query);

} catch (SQLException e) {
    // ✅ CORRECT : Inclure l'exception pour avoir la stack trace
    logger.error("Erreur SQL: {}", e.getMessage(), e);

    // ❌ INCORRECT : Ne pas inclure l'exception
    // logger.error("Erreur SQL: " + e.getMessage());
}

// Avec contexte additionnel
try {
    supprimerClient(clientId);

} catch (Exception e) {
    logger.error("Impossible de supprimer le client {}: {}",
                clientId, e.getMessage(), e);
    throw e;
}
```

### 6. Logging Conditionnel

```java
// Vérifier si le niveau DEBUG est activé avant de faire un calcul coûteux
if (logger.isDebugEnabled()) {
    String detailsComplexes = calculerDetailsComplexes(); // Opération coûteuse
    logger.debug("Détails: {}", detailsComplexes);
}

// Avec les paramètres {}, ce n'est généralement pas nécessaire car l'évaluation est paresseuse
logger.debug("Valeur: {}", valeur); // ✅ Pas besoin de if()
```

### 7. Logging de Sécurité

```java
public void supprimerClient(String clientId, String utilisateur) {
    logger.warn("ACTION SENSIBLE - Suppression du client {} par {}", clientId, utilisateur);

    // Log de l'IP (si disponible)
    String ipAddress = request.getRemoteAddr();
    logger.info("Suppression depuis l'IP: {}", ipAddress);

    // Validation
    if (!utilisateur.hasPermission("DELETE_CLIENT")) {
        logger.error("SÉCURITÉ - Tentative de suppression non autorisée par {}", utilisateur);
        throw new SecurityException("Non autorisé");
    }

    // Action
    clientRepository.delete(clientId);
    logger.info("Client {} supprimé avec succès", clientId);
}
```

---

## Où Consulter les Logs

### Structure des Fichiers

```
sparadrap/
└── logs/
    ├── sparadrap.log              ← Tous les logs (fichier actif)
    ├── sparadrap-errors.log       ← Erreurs uniquement (fichier actif)
    ├── sparadrap-2025-11-14.log   ← Archive du 14 novembre
    ├── sparadrap-2025-11-13.log   ← Archive du 13 novembre
    └── ...
```

### Console

Pendant l'exécution, les logs s'affichent également dans la console avec des **couleurs** :

- 🔴 **ERROR** en rouge
- 🟡 **WARN** en jaune
- 🔵 **INFO** en bleu
- ⚪ **DEBUG** en gris

### Lire les Logs

```bash
# Voir les derniers logs
tail -f logs/sparadrap.log

# Voir seulement les erreurs
tail -f logs/sparadrap-errors.log

# Chercher un client spécifique
grep "cdurand01" logs/sparadrap.log

# Voir les logs d'aujourd'hui
cat logs/sparadrap-$(date +%Y-%m-%d).log
```

---

## Configuration

### Modifier les Niveaux de Log

Éditez `src/main/resources/logback.xml` :

```xml
<!-- Pour plus de détails en développement -->
<logger name="main.model.database" level="DEBUG" />

<!-- Moins de logs pour les vues -->
<logger name="main.view" level="WARN" />

<!-- Niveau global -->
<root level="INFO">
    ...
</root>
```

### Changer le Dossier des Logs

Dans `logback.xml` :

```xml
<property name="LOG_DIR" value="mon_dossier_logs" />
```

### Changer la Durée de Conservation

```xml
<!-- Conservation de 90 jours au lieu de 30 -->
<maxHistory>90</maxHistory>
```

---

## ✅ Bonnes Pratiques

### ✓ À Faire

```java
// Utiliser les paramètres {}
logger.info("Client: {}, Montant: {}", client, montant);

// Inclure les exceptions
logger.error("Erreur: {}", e.getMessage(), e);

// Logger aux bons niveaux
logger.debug("Détails techniques");
logger.info("Événement important");
logger.warn("Situation anormale mais non bloquante");
logger.error("Erreur critique");
```

### ✗ À Éviter

```java
// ❌ Concaténation de chaînes (coûteux)
logger.info("Client: " + client + ", Montant: " + montant);

// ❌ Logger l'exception sans la stack trace
logger.error("Erreur: " + e.getMessage());

// ❌ Utiliser System.out.println()
System.out.println("Message");  // Ne pas faire !

// ❌ Logger à tous les niveaux
logger.error("Simple information");  // Mauvais niveau !
```

---

## 🔍 Débogage

### Problème : Aucun Log ne s'affiche

1. Vérifiez que `logback.xml` est dans `src/main/resources/`
2. Vérifiez le niveau de log dans `logback.xml`
3. Assurez-vous d'avoir les dépendances Logback dans `pom.xml`

```bash
# Recompiler
mvn clean compile
```

### Problème : Fichiers de log non créés

1. Vérifiez les permissions du dossier `logs/`
2. Le dossier est créé automatiquement au premier log

```bash
# Créer manuellement si nécessaire
mkdir -p logs
```

### Activer le Mode DEBUG pour Logback

Ajoutez en haut de `logback.xml` :

```xml
<configuration debug="true">
```

Cela affichera les informations de configuration de Logback au démarrage.

---

## 📚 Ressources

- **Documentation complète** : Voir `RECHERCHE_LOGGER.md`
- **Classe de démo** : `src/main/java/main/model/demo/LoggerDemo.java`
- **Configuration** : `src/main/resources/logback.xml`
- **SLF4J** : https://www.slf4j.org/
- **Logback** : https://logback.qos.ch/

---

## 🎓 Exercice Pratique

Ajoutez des logs dans une de vos classes existantes :

1. Importez Logger et LoggerFactory
2. Déclarez le logger
3. Ajoutez des logs INFO pour les actions importantes
4. Ajoutez des logs DEBUG pour les détails
5. Ajoutez des logs ERROR dans les blocs catch
6. Exécutez et consultez `logs/sparadrap.log`

Bon logging ! 🚀
