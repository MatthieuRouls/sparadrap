# Travail de Veille et Recherche : Logger en Java

## 1. Introduction au Logging en Java

Le logging est essentiel pour :
- **Déboguer** les applications en production
- **Tracer** les événements et erreurs
- **Monitorer** les performances
- **Auditer** les actions utilisateurs

## 2. Solutions de Logging en Java

### 2.1 Frameworks disponibles

| Framework | Description | Avantages | Inconvénients |
|-----------|-------------|-----------|---------------|
| **java.util.logging (JUL)** | Logger natif Java | Intégré au JDK, pas de dépendance | Peu flexible, configuration complexe |
| **Log4j 2** | Framework Apache populaire | Très performant, flexible | Configuration parfois complexe |
| **Logback** | Successeur de Log4j 1.x | Simple, performant, natif avec SLF4J | - |
| **SLF4J** | Facade/abstraction | Indépendant de l'implémentation | Nécessite une implémentation |

### 2.2 Architecture SLF4J + Logback (Solution retenue)

```
┌─────────────────┐
│  Votre Code     │
│  (utilise SLF4J)│
└────────┬────────┘
         │
┌────────▼────────┐
│     SLF4J       │ ← API/Facade (indépendante)
│   (interface)   │
└────────┬────────┘
         │
┌────────▼────────┐
│    Logback      │ ← Implémentation (moteur de logging)
│  (implémentation)│
└─────────────────┘
```

**Avantages** :
- ✅ SLF4J = Abstraction (changement d'implémentation facile)
- ✅ Logback = Performance optimale, configuration XML simple
- ✅ Intégration native entre les deux (même auteur)
- ✅ Support MDC (Mapped Diagnostic Context) pour logs contextuels
- ✅ Rechargement automatique de la configuration

## 3. Niveaux de Log

Par ordre de gravité croissante :

| Niveau | Usage | Exemple |
|--------|-------|---------|
| **TRACE** | Informations très détaillées | Entrée/sortie de chaque méthode |
| **DEBUG** | Informations de débogage | Valeurs de variables, état interne |
| **INFO** | Informations générales | Démarrage, arrêt, configuration |
| **WARN** | Avertissements | Utilisation d'une API dépréciée |
| **ERROR** | Erreurs | Exceptions, erreurs critiques |

## 4. Configuration Logback

### 4.1 Structure du fichier logback.xml

```xml
<configuration>
  <!-- Définition des appenders (destinations) -->
  <appender name="..." class="...">
    <!-- Configuration de l'appender -->
  </appender>

  <!-- Configuration des loggers -->
  <logger name="..." level="...">
    <appender-ref ref="..." />
  </logger>

  <!-- Logger racine (par défaut) -->
  <root level="...">
    <appender-ref ref="..." />
  </root>
</configuration>
```

### 4.2 Types d'Appenders

#### ConsoleAppender
Affiche les logs dans la console (System.out ou System.err).

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
  <encoder>
    <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
  </encoder>
</appender>
```

#### FileAppender
Écrit les logs dans un fichier.

```xml
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
  <file>logs/application.log</file>
  <encoder>
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
  </encoder>
</appender>
```

#### RollingFileAppender
Crée des fichiers avec rotation (par taille ou date).

```xml
<appender name="ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
  <file>logs/application.log</file>
  <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
    <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
    <maxHistory>30</maxHistory>
  </rollingPolicy>
  <encoder>
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>
  </encoder>
</appender>
```

### 4.3 Patterns de formatage

| Pattern | Description | Exemple |
|---------|-------------|---------|
| `%d{format}` | Date/heure | `2025-11-14 13:45:30.123` |
| `%thread` | Nom du thread | `main` |
| `%level` | Niveau de log | `INFO` |
| `%-5level` | Niveau aligné sur 5 caractères | `INFO ` |
| `%logger{length}` | Nom du logger | `com.sparadrap.Main` |
| `%logger{36}` | Logger tronqué à 36 caractères | `c.s.Main` |
| `%msg` | Message de log | `Application démarrée` |
| `%n` | Nouvelle ligne | Saut de ligne |
| `%class` | Nom de la classe | `Main` |
| `%method` | Nom de la méthode | `main` |
| `%line` | Numéro de ligne | `42` |
| `%ex` | Exception avec stack trace | (pile complète) |

Exemple de pattern complet :
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
→ 2025-11-14 13:45:30.123 [main] INFO  c.s.database.DatabaseConnection - Connexion établie
```

## 5. Bonnes Pratiques

### 5.1 Déclaration du Logger

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaClasse {
    // Logger statique privé final
    private static final Logger logger = LoggerFactory.getLogger(MaClasse.class);
}
```

### 5.2 Utilisation des Niveaux

```java
// TRACE : Très détaillé
logger.trace("Entrée dans la méthode avec param={}", param);

// DEBUG : Débogage
logger.debug("Valeur calculée: {}", valeur);

// INFO : Information générale
logger.info("Connexion établie avec succès");

// WARN : Avertissement
logger.warn("Fichier de configuration non trouvé, utilisation des valeurs par défaut");

// ERROR : Erreur
logger.error("Erreur lors de la connexion: {}", e.getMessage(), e);
```

### 5.3 Paramètres et Performance

❌ **Mauvais** (concaténation coûteuse) :
```java
logger.debug("Valeur = " + valeur + ", État = " + etat);
```

✅ **Bon** (évaluation paresseuse) :
```java
logger.debug("Valeur = {}, État = {}", valeur, etat);
```

### 5.4 Logging des Exceptions

```java
try {
    // Code
} catch (SQLException e) {
    // ✅ Inclure l'exception pour avoir la stack trace
    logger.error("Erreur SQL lors de la requête: {}", e.getMessage(), e);
}
```

### 5.5 Loggers par Package

```xml
<!-- Logger spécifique pour votre code -->
<logger name="main.model.database" level="DEBUG" />

<!-- Logger pour les librairies tierces -->
<logger name="com.zaxxer.hikari" level="INFO" />
<logger name="org.springframework" level="WARN" />
```

## 6. Avantages de Notre Configuration

### Console + Fichier
- **Console** : Utile en développement, retour immédiat
- **Fichier** : Historique permanent, analyse post-mortem

### Rotation des Logs
- Évite les fichiers géants
- Conservation de l'historique (30 jours par défaut)
- Nommage par date : `application-2025-11-14.log`

### Performance
- Logback est asynchrone par défaut pour les I/O
- Évaluation paresseuse des paramètres
- Pas d'impact significatif sur les performances

## 7. Comparaison avec d'autres solutions

### vs. System.out.println()

| Critère | System.out | Logger |
|---------|------------|--------|
| Niveau de log | ❌ Non | ✅ Oui (DEBUG, INFO, etc.) |
| Formatage | ❌ Manuel | ✅ Automatique |
| Destination | ❌ Console uniquement | ✅ Console, fichier, réseau... |
| Performance | ❌ Bloquant | ✅ Optimisé |
| Activation/désactivation | ❌ Difficile | ✅ Par configuration |
| Contexte (date, classe) | ❌ Manuel | ✅ Automatique |

### vs. Log4j2

| Critère | Log4j2 | Logback |
|---------|--------|---------|
| Performance | Excellent | Excellent |
| Configuration | XML/JSON/YAML | XML/Groovy |
| Intégration SLF4J | Via adaptateur | Native |
| Communauté | Apache | Large |
| Maintenance | Active | Active |

**Conclusion** : Logback est plus simple pour débuter et s'intègre parfaitement avec SLF4J.

## 8. Architecture du Logging dans Sparadrap

```
Application Sparadrap
├── Console (temps réel)
│   ├── Niveau DEBUG en développement
│   └── Couleurs pour meilleure lisibilité
│
└── Fichiers logs/
    ├── application.log (fichier courant)
    ├── application-2025-11-14.log (archives)
    ├── application-2025-11-13.log
    └── ... (30 jours d'historique)
```

## 9. Cas d'Usage Sparadrap

### Connexion Base de Données
```java
logger.info("Initialisation du pool de connexions HikariCP");
logger.debug("Configuration: URL={}, MaxPoolSize={}", url, maxPoolSize);
logger.error("Erreur de connexion à MySQL: {}", e.getMessage(), e);
```

### Transactions
```java
logger.info("Nouvelle transaction: Client={}, Montant={}", clientId, montant);
logger.warn("Stock faible pour le médicament: {}", medicamentNom);
```

### Sécurité
```java
logger.warn("Tentative d'accès non autorisé: IP={}", ipAddress);
logger.error("Injection SQL détectée dans la requête: {}", query);
```

## 10. Ressources et Documentation

- **SLF4J** : https://www.slf4j.org/
- **Logback** : https://logback.qos.ch/
- **Log4j2** : https://logging.apache.org/log4j/2.x/
- **Baeldung Guide** : https://www.baeldung.com/logback

## Conclusion

Le système SLF4J + Logback offre :
- ✅ Simplicité d'utilisation
- ✅ Configuration flexible (XML)
- ✅ Double sortie (console + fichier)
- ✅ Rotation automatique des logs
- ✅ Performance optimale
- ✅ Standard de l'industrie

C'est la solution recommandée pour les applications Java modernes.
