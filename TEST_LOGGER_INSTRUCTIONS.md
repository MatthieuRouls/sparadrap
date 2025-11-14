# Instructions de Test du Logger

## 🎯 Objectif

Tester le système de logging configuré avec SLF4J + Logback pour vérifier que :
- ✅ Les logs s'affichent dans la console avec des couleurs
- ✅ Les logs sont enregistrés dans des fichiers
- ✅ La rotation des fichiers fonctionne
- ✅ Les différents niveaux de log fonctionnent correctement

---

## 📋 Prérequis

Assurez-vous que le projet est compilé :

```bash
mvn clean compile
```

---

## 🧪 Test 1 : Exécuter la Démonstration Complète

### Commande

```bash
mvn exec:java -Dexec.mainClass="main.model.demo.LoggerDemo"
```

### Résultats Attendus

#### Dans la Console

Vous devriez voir :

```
13:45:30.123 INFO  [main] main.model.demo.LoggerDemo - ========================================
13:45:30.125 INFO  [main] main.model.demo.LoggerDemo -   DÉMONSTRATION DU SYSTÈME DE LOGGING
13:45:30.126 INFO  [main] main.model.demo.LoggerDemo - ========================================

--- 1. NIVEAUX DE LOG ---
13:45:30.130 DEBUG [main] main.model.demo.LoggerDemo - DEBUG: Variable de débogage
13:45:30.131 INFO  [main] main.model.demo.LoggerDemo - INFO: L'application fonctionne normalement
13:45:30.132 WARN  [main] main.model.demo.LoggerDemo - WARN: Fichier de configuration non trouvé
13:45:30.133 ERROR [main] main.model.demo.LoggerDemo - ERROR: Erreur critique détectée
...
```

Les niveaux devraient être **colorés** :
- 🔴 ERROR en rouge
- 🟡 WARN en jaune
- 🔵 INFO en bleu clair
- ⚪ DEBUG en gris

#### Dans les Fichiers

Vérifiez les fichiers créés :

```bash
ls -lh logs/
```

Vous devriez voir :

```
logs/
├── sparadrap.log              ← Tous les logs
├── sparadrap-errors.log       ← Erreurs uniquement
└── sparadrap-2025-11-14.log   ← Archive (si changement de jour)
```

Consultez le fichier principal :

```bash
cat logs/sparadrap.log
```

Vous devriez voir tous les logs sans couleurs mais avec timestamps complets :

```
2025-11-14 13:45:30.123 INFO  [main] main.model.demo.LoggerDemo - ========================================
2025-11-14 13:45:30.130 DEBUG [main] main.model.demo.LoggerDemo - DEBUG: Variable de débogage
...
```

Consultez le fichier d'erreurs :

```bash
cat logs/sparadrap-errors.log
```

Vous devriez voir **uniquement** les logs de niveau ERROR :

```
2025-11-14 13:45:30.133 ERROR [main] main.model.demo.LoggerDemo - ERROR: Erreur critique détectée
2025-11-14 13:45:30.145 ERROR [main] main.model.demo.LoggerDemo - Erreur SQL lors de la requête: Table 'clients' introuvable
java.sql.SQLException: Table 'clients' introuvable
    at main.model.demo.LoggerDemo.simulerErreurSQL(LoggerDemo.java:...)
    ...
```

---

## 🧪 Test 2 : Tester avec DatabaseInitializer

### Test de Connexion

```bash
mvn exec:java -Dexec.mainClass="main.model.database.DatabaseInitializer" -Dexec.args="test"
```

### Résultats Attendus

Dans la **console** :

```
13:50:00.123 INFO  [main] main.model.database.DatabaseConfig - Configuration de la base de données chargée avec succès depuis database.properties
13:50:00.125 DEBUG [main] main.model.database.DatabaseConfig - URL: jdbc:mysql://localhost:3306/sparadrap_db, Username: root
13:50:00.200 INFO  [main] main.model.database.DatabaseConnection - Initialisation du pool de connexions HikariCP...
13:50:00.450 INFO  [main] main.model.database.DatabaseConnection - Pool de connexions HikariCP initialisé avec succès.
13:50:00.455 INFO  [main] main.model.database.DatabaseInitializer - Test de connexion à la base de données...
13:50:00.460 DEBUG [main] main.model.database.DatabaseConnection - Connexion récupérée depuis le pool.
13:50:00.465 INFO  [main] main.model.database.DatabaseInitializer - Connexion réussie!
13:50:00.466 INFO  [main] main.model.database.DatabaseInitializer - Pool Stats - Active: 0, Idle: 1, Total: 1, Waiting: 0
```

Dans le **fichier** `logs/sparadrap.log` :

Les mêmes messages avec timestamps complets.

---

## 🧪 Test 3 : Vérifier les Niveaux de Log

### Modifier le Niveau dans logback.xml

Éditez `src/main/resources/logback.xml` :

```xml
<!-- Changer le niveau de DEBUG à INFO -->
<logger name="main.model.database" level="INFO" />
```

Recompilez et relancez :

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="main.model.database.DatabaseInitializer" -Dexec.args="test"
```

### Résultat Attendu

Les logs **DEBUG** ne devraient **plus apparaître** :

```
❌ Absent : 13:50:00.125 DEBUG [main] main.model.database.DatabaseConfig - URL: jdbc:mysql://localhost:3306/sparadrap_db
✅ Présent : 13:50:00.123 INFO  [main] main.model.database.DatabaseConfig - Configuration chargée
```

Remettez le niveau à DEBUG après le test.

---

## 🧪 Test 4 : Vérifier la Rotation des Fichiers

### Simulation

Pour simuler un changement de jour (et donc la rotation), modifiez temporairement `logback.xml` :

```xml
<!-- Rotation par minute au lieu de par jour -->
<fileNamePattern>${LOG_DIR}/${APP_NAME}-%d{yyyy-MM-dd-HH-mm}.log</fileNamePattern>
```

Recompilez et exécutez la démo plusieurs fois à quelques minutes d'intervalle :

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="main.model.demo.LoggerDemo"
# Attendre 1 minute
mvn exec:java -Dexec.mainClass="main.model.demo.LoggerDemo"
```

Vérifiez les fichiers :

```bash
ls -lh logs/
```

Vous devriez voir :

```
sparadrap.log
sparadrap-2025-11-14-13-45.log  ← Archive minute 45
sparadrap-2025-11-14-13-46.log  ← Archive minute 46
```

**Remettez la configuration d'origine après le test !**

---

## 🧪 Test 5 : Logs depuis l'Application Principale

### Ajouter des Logs dans Main.java

Éditez `src/main/java/main/Main.java` :

```java
package main;

import javax.swing.SwingUtilities;
import main.view.PharmacieMainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("Démarrage de l'application Sparadrap");
        logger.info("========================================");

        SwingUtilities.invokeLater(() -> {
            logger.debug("Initialisation de l'interface graphique...");
            PharmacieMainFrame frame = new PharmacieMainFrame();
            frame.setVisible(true);
            logger.info("Interface graphique affichée avec succès");
        });
    }
}
```

Exécutez l'application :

```bash
mvn exec:java -Dexec.mainClass="main.Main"
```

### Résultat Attendu

Dans la console :

```
13:55:00.100 INFO  [main] main.Main - ========================================
13:55:00.101 INFO  [main] main.Main - Démarrage de l'application Sparadrap
13:55:00.102 INFO  [main] main.Main - ========================================
13:55:00.250 DEBUG [AWT-EventQueue-0] main.Main - Initialisation de l'interface graphique...
13:55:00.450 INFO  [AWT-EventQueue-0] main.Main - Interface graphique affichée avec succès
```

Dans `logs/sparadrap.log`, les mêmes messages sont enregistrés.

---

## 🧪 Test 6 : Recherche dans les Logs

### Générer des Logs

```bash
mvn exec:java -Dexec.mainClass="main.model.demo.LoggerDemo"
```

### Rechercher des Informations

```bash
# Chercher tous les logs ERROR
grep "ERROR" logs/sparadrap.log

# Chercher les logs concernant SQL
grep "SQL" logs/sparadrap.log

# Chercher les logs d'une classe spécifique
grep "DatabaseConnection" logs/sparadrap.log

# Afficher seulement les timestamps et niveaux
awk '{print $1, $2, $3}' logs/sparadrap.log

# Compter les logs par niveau
grep -c "INFO" logs/sparadrap.log
grep -c "ERROR" logs/sparadrap.log
grep -c "WARN" logs/sparadrap.log
```

---

## 🧪 Test 7 : Surveillance en Temps Réel

### Suivre les Logs en Direct

Ouvrez deux terminaux :

**Terminal 1** : Suivre les logs

```bash
tail -f logs/sparadrap.log
```

**Terminal 2** : Exécuter l'application

```bash
mvn exec:java -Dexec.mainClass="main.model.demo.LoggerDemo"
```

Vous verrez les logs apparaître en temps réel dans le Terminal 1.

---

## ✅ Critères de Validation

| Test | Critère | Résultat |
|------|---------|----------|
| 1 | Les logs s'affichent dans la console | ✅ / ❌ |
| 2 | Les logs sont colorés dans la console | ✅ / ❌ |
| 3 | Le fichier `logs/sparadrap.log` est créé | ✅ / ❌ |
| 4 | Le fichier `logs/sparadrap-errors.log` est créé | ✅ / ❌ |
| 5 | Les logs ERROR sont dans sparadrap-errors.log | ✅ / ❌ |
| 6 | Les niveaux de log fonctionnent (DEBUG, INFO, etc.) | ✅ / ❌ |
| 7 | La rotation crée des archives | ✅ / ❌ |
| 8 | Les exceptions montrent la stack trace | ✅ / ❌ |
| 9 | Les paramètres {} sont remplacés correctement | ✅ / ❌ |
| 10 | DatabaseConfig utilise le logger | ✅ / ❌ |

---

## 🐛 Dépannage

### Problème : Aucun Log ne S'affiche

**Solution** :

```bash
# Vérifier que logback.xml existe
ls -l src/main/resources/logback.xml

# Recompiler complètement
mvn clean compile

# Vérifier les dépendances
mvn dependency:tree | grep logback
```

### Problème : Fichiers de Log Non Créés

**Solution** :

```bash
# Créer le dossier logs manuellement
mkdir -p logs

# Vérifier les permissions
chmod 755 logs

# Relancer
mvn exec:java -Dexec.mainClass="main.model.demo.LoggerDemo"
```

### Problème : Pas de Couleurs dans la Console

Les couleurs fonctionnent sur la plupart des terminaux Linux/Mac. Sur Windows, utilisez :
- Git Bash
- Windows Terminal
- PowerShell 7+

---

## 📊 Exemple de Rapport de Test

```
=== RAPPORT DE TEST DU LOGGER ===
Date: 2025-11-14
Testeur: [Votre nom]

✅ Test 1 - Démonstration complète : RÉUSSI
✅ Test 2 - DatabaseInitializer : RÉUSSI
✅ Test 3 - Niveaux de log : RÉUSSI
✅ Test 4 - Rotation : RÉUSSI
✅ Test 5 - Application principale : RÉUSSI
✅ Test 6 - Recherche : RÉUSSI
✅ Test 7 - Temps réel : RÉUSSI

Fichiers créés :
- logs/sparadrap.log (15 KB)
- logs/sparadrap-errors.log (3 KB)

Conclusion : Le système de logging fonctionne parfaitement.
```

---

## 📚 Ressources

- Documentation complète : `RECHERCHE_LOGGER.md`
- Guide d'utilisation : `GUIDE_UTILISATION_LOGGER.md`
- Configuration : `src/main/resources/logback.xml`
- Démo : `src/main/java/main/model/demo/LoggerDemo.java`

---

**Bon test ! 🚀**
