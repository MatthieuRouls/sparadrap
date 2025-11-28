# Pharmacie Sparadrap - Application de Gestion

Application Java Swing complète pour la gestion d'une pharmacie : clients, médecins, médicaments, ventes et statistiques.

## Table des matières

- [Prérequis](#prérequis)
- [Démarrage rapide](#démarrage-rapide)
- [Architecture de l'application](#architecture-de-lapplication)
- [Gestion des données](#gestion-des-données)
- [Modèle de données](#modèle-de-données)
- [Fonctionnalités](#fonctionnalités)
- [Navigation](#navigation)
- [Guide d'utilisation](#guide-dutilisation)
- [Jeux de données](#jeux-de-données)

## Prérequis

- **JDK 21** ou supérieur
- **IntelliJ IDEA** (ou tout IDE Java capable d'exécuter une application Swing)
- OS : Windows, macOS ou Linux

## Démarrage rapide

1. Ouvrir le projet dans **IntelliJ IDEA**
2. Localiser la classe `main.view.PharmacieMainFrame`
3. Exécuter la méthode `main()`
4. L'application démarre sur l'onglet **Accueil** avec statistiques et actions rapides

## Architecture de l'application

L'application suit une architecture MVC (Model-View-Controller) classique :

```
src/
├── main/
│   ├── controller/          # Contrôleurs
│   │   └── PharmacieController.java
│   ├── model/              # Modèles métier
│   │   ├── Document/       # Documents (Ordonnances)
│   │   ├── Medicament/     # Médicaments et catégories
│   │   ├── Organisme/      # Organismes (Mutuelles)
│   │   ├── Personne/       # Personnes (Client, Médecin, Pharmacien)
│   │   ├── Transaction/    # Transactions (Achats)
│   │   ├── security/       # Validateurs de sécurité
│   │   └── service/        # Services métier
│   │       └── GestPharmacieService.java
│   └── view/               # Interfaces graphiques
│       ├── PharmacieMainFrame.java
│       ├── ClientPanel.java
│       ├── MedecinPanel.java
│       ├── MedicamentPanel.java
│       ├── VentePanel.java
│       └── StatistiquePanel.java
└── test/                   # Tests unitaires
```

### Couches applicatives

#### 1. Couche Modèle (Model)

**Classes principales :**
- `Client` : Représente un client avec ses informations personnelles, mutuelle et médecin traitant
- `Medecin` : Représente un médecin avec son numéro RPPS et sa liste de patients
- `Pharmacien` : Représente un pharmacien avec son numéro RPPS et spécialité
- `Medicament` : Représente un médicament avec son stock, prix, catégorie et dates
- `Mutuelle` : Représente une mutuelle avec son taux de remboursement
- `Achat` : Représente une transaction de vente (directe ou sur ordonnance)
- `Ordonnance` : Représente une prescription médicale

**Sécurité et validation :**
- La classe `SecurityValidator` centralise toutes les validations (formats, longueurs, contraintes métier)
- Validation des numéros de sécurité sociale (15 chiffres)
- Validation des numéros RPPS (11 chiffres)
- Validation des emails, téléphones, codes postaux
- Validation des montants et stocks

#### 2. Couche Service

**GestPharmacieService** : Service métier central qui gère :
- Le stockage en mémoire (ConcurrentHashMap pour thread-safety)
- Les opérations CRUD sur toutes les entités
- La persistance simple des ventes (CSV)
- Le calcul des statistiques
- La gestion des relations entre entités

**Architecture de stockage :**
```java
// Stockage en mémoire thread-safe
private final Map<String, Client> clients = new ConcurrentHashMap<>();
private final Map<String, Medecin> medecins = new ConcurrentHashMap<>();
private final Map<String, Mutuelle> mutuelles = new ConcurrentHashMap<>();
private final List<Achat> achats = new CopyOnWriteArrayList<>();
private final List<Ordonnance> ordonnances = new CopyOnWriteArrayList<>();
```

#### 3. Couche Contrôleur

**PharmacieController** : Contrôleur principal qui :
- Fait l'interface entre les vues et le service
- Gère l'inventaire des médicaments (HashMap)
- Coordonne les opérations complexes (ventes, statistiques)
- Maintient un cache local pour les clients
- Gère le pharmacien connecté

#### 4. Couche Vue

**Panels principaux :**
- `PharmacieMainFrame` : Fenêtre principale avec navigation sidebar
- `ClientPanel` : Gestion CRUD des clients avec recherche et formulaire
- `MedecinPanel` : Gestion CRUD des médecins avec visualisation des ordonnances
- `MedicamentPanel` : Gestion de l'inventaire avec alertes de stock
- `VentePanel` : Système de panier pour ventes directes et sur ordonnance
- `StatistiquePanel` : Tableaux de bord et rapports

**Système d'événements :**
- `DataEventManager` : Gestionnaire centralisé d'événements
- `DataRefreshListener` : Interface pour l'écoute des mises à jour
- Rafraîchissement automatique des compteurs après modifications

## Gestion des données

### Stockage en mémoire

L'application utilise un système de **stockage en mémoire** avec structures thread-safe :

```java
// Clients indexés par identifiant
Map<String, Client> clients

// Médecins indexés par numéro RPPS
Map<String, Medecin> medecins

// Mutuelles indexées par nom
Map<String, Mutuelle> mutuelles

// Médicaments indexés par nom (en minuscules)
Map<String, Medicament> inventaire

// Listes thread-safe pour historiques
List<Achat> achats
List<Ordonnance> ordonnances
```

### Persistance simple

**Fichier : `data/achats.csv`**

Format : `timestamp,montantTotal,montantRembourse,typeAchat`

```csv
1757666840546,17.97,0.0,DIRECT
1757668588245,23.96,0.0,DIRECT
1757669152973,599.0,0.0,DIRECT
```

**Fonctionnement :**
- Écriture en mode append (ajout) après chaque vente
- Lecture pour calculer les statistiques de ventes
- Fallback sur la mémoire si le fichier est inaccessible
- Non bloquant : l'application continue même si l'écriture échoue

### Relations entre entités

```
Client
  ├── mutuelle (0..1) ──> Mutuelle
  └── medecinTraitant (0..1) ──> Medecin

Medecin
  └── patients (0..*) ──> Client

Achat
  ├── client (1) ──> Client
  ├── pharmacien (1) ──> Pharmacien
  └── medicaments (1..*) ──> Medicament
      └── quantites : Map<Medicament, Integer>

Ordonnance
  ├── medecin (1) ──> Medecin
  ├── patient (1) ──> Client
  └── medicaments (1..*) ──> Medicament
      └── quantites : Map<Medicament, Integer>
```

**Gestion des relations :**

1. **Client ↔ Mutuelle** : Association optionnelle, gérée par référence
   - Permet le calcul automatique des remboursements

2. **Client ↔ Médecin traitant** : Association optionnelle bidirectionnelle
   - Gérée via `assignerMedecinClient()`
   - Le médecin maintient sa liste de patients

3. **Achat → Client/Pharmacien** : Association obligatoire
   - Chaque vente est liée à un client et un pharmacien

4. **Achat → Médicaments** : Association multiple avec quantités
   - Map pour stocker médicament + quantité vendue
   - Mise à jour automatique du stock lors de la vente

5. **Ordonnance → Médecin/Client** : Association obligatoire
   - Lien entre prescription et achat via date et référence

## Modèle de données

### Client

```java
Attributs :
- identifiant : String (généré : 2 lettres prénom + 3 lettres nom)
- nom, prenom : String
- adresse, codePostal, ville : String
- telephone : String (format : 0XXXXXXXXX)
- email : String (validé)
- numeroSecuriteSocial : String (15 chiffres, masqué à l'affichage)
- mutuelle : Mutuelle (optionnel)
- medecinTraitant : Medecin (optionnel)
```

### Medecin

```java
Attributs :
- identifiant : String (souvent = RPPS)
- numeroRPPS : String (11 chiffres obligatoires)
- nom, prenom : String
- adresse, codePostal, ville : String
- telephone, email : String
- patients : List<Client>
```

### Medicament

```java
Attributs :
- nom : String (unique, clé de recherche)
- categorie : CategorieMedicament (enum)
- prix : double (> 0)
- quantiteStock : int (>= 0)
- dateMiseEnService : Date
- datePeremption : Date (> dateMiseEnService)
```

**Catégories disponibles :**
- ANALGESIQUES
- ANTI_INFLAMMATOIRES
- ANTIVIRAUX
- ANTIHISTAMINIQUES
- ANTISPASMODIQUES
- ANTIBIOTIQUES
- AUTRES

### Achat

```java
Attributs :
- reference : String (ex: ACH1757666840546)
- dateTransaction : Date
- client : Client
- pharmacien : Pharmacien
- type : TypeAchat (DIRECT ou ORDONNANCE)
- medicaments : List<Medicament>
- quantites : Map<Medicament, Integer>
- montantTotal : double (calculé)
- montantRembourse : double (calculé selon mutuelle)
```

### Ordonnance

```java
Attributs :
- reference : String (ex: ORD1757922496901)
- dateCreation : Date
- medecin : Medecin
- patient : Client
- medicaments : List<Medicament>
- quantites : Map<Medicament, Integer>
- montantTotal : double (calculé)
```

## Fonctionnalités

### Gestion des clients

- **Ajout** : Génération automatique de l'identifiant (2+3 caractères)
- **Recherche** : Par identifiant unique
- **Modification** : Mise à jour de toutes les informations
- **Suppression** : Avec confirmation
- **Association** : Mutuelle et médecin traitant optionnels
- **Validation** : Tous les champs sont validés avant enregistrement

### Gestion des médecins

- **Ajout** : Avec numéro RPPS obligatoire (11 chiffres)
- **Recherche** : Par numéro RPPS
- **Modification** : Mise à jour des informations
- **Suppression** : Avec confirmation
- **Suivi** : Consultation des ordonnances émises par médecin

### Gestion des médicaments

- **Inventaire** : Visualisation complète avec statuts (OK, Stock bas, Rupture, Périmé)
- **Ajout** : Nouveau médicament avec validation des dates
- **Modification stock** : Ajustement des quantités
- **Recherche** : Par nom (insensible à la casse)
- **Alertes** :
  - Rupture de stock (quantité = 0)
  - Stock bas (quantité < 10)
  - Médicament périmé (date > date du jour)
  - Expire bientôt (< 30 jours)

### Gestion des ventes

#### Vente directe (sans ordonnance)
1. Sélection du client par identifiant
2. Ajout de médicaments au panier
3. Vérification automatique du stock
4. Validation et génération de référence
5. Calcul automatique du remboursement mutuelle
6. Mise à jour immédiate du stock

#### Vente sur ordonnance
1. Sélection client et médecin (RPPS)
2. Ajout des médicaments prescrits
3. Vérification stock + péremption
4. Création de l'ordonnance
5. Enregistrement de l'achat associé
6. Calcul du remboursement

#### Historique
- Filtres : Toutes, Aujourd'hui, Cette semaine, Ce mois
- Recherche par référence
- Statistiques : Total ventes, Total €, Remboursé, Net
- Détails complets par vente (double-clic)

### Statistiques

**Métriques disponibles :**
- Chiffre d'affaires (jour, mois, année)
- Nombre de ventes (30 derniers jours)
- Stock total et ruptures
- Montants remboursés
- Bénéfice net (CA - Remboursements)

**Périodes :**
- Aujourd'hui
- Ce mois
- Cette année
- 30 derniers jours

## Navigation

### Écran d'accueil

**Statistiques en temps réel :**
- Ventes du jour
- Médicaments en stock
- Clients enregistrés
- CA du mois

**Actions rapides :**
- **Nouvelle vente** : Ouvre Ventes > Vente directe
- **Rechercher client** : Ouvre Clients avec focus sur recherche
- **Gérer stock** : Ouvre Médicaments avec focus sur recherche

### Barre latérale

- **Accueil** : Dashboard et actions rapides
- **Clients** : Gestion complète des clients
- **Médecins** : Gestion complète des médecins
- **Médicaments** : Gestion de l'inventaire
- **Ventes** : Ventes et historique (onglets)
- **Statistiques** : Rapports et synthèses

## Guide d'utilisation

### Scénario complet : Vente avec remboursement

1. **Créer une mutuelle** (si nécessaire)
   - Nom : "Mutuelle Santé Plus"
   - Taux de remboursement : 70%

2. **Créer un client**
   - Navigation : Clients > Ajouter
   - Remplir le formulaire (identifiant généré automatiquement)
   - Sélectionner la mutuelle
   - Sauvegarder

3. **Ajouter des médicaments**
   - Navigation : Médicaments > Ajouter
   - Nom, catégorie, prix, stock
   - Date de péremption (future)
   - Sauvegarder

4. **Effectuer une vente**
   - Navigation : Ventes > Vente directe
   - Rechercher le client
   - Ajouter médicaments au panier
   - Valider
   - Le remboursement est calculé automatiquement

5. **Consulter l'historique**
   - Navigation : Ventes > Historique
   - Voir le détail (double-clic)
   - Consulter totaux et statistiques

### Scénario : Vente sur ordonnance

1. **Créer un médecin**
   - Navigation : Médecins > Ajouter
   - Numéro RPPS obligatoire (11 chiffres)
   - Ex : 12345678901

2. **Associer le médecin au client** (optionnel)
   - Modifier le client
   - Sélectionner le médecin traitant

3. **Effectuer la vente**
   - Navigation : Ventes > Vente sur ordonnance
   - Sélectionner client et médecin
   - Ajouter médicaments
   - Valider
   - L'ordonnance et l'achat sont créés

4. **Consulter les ordonnances**
   - Navigation : Médecins
   - Sélectionner un médecin
   - Cliquer "Ordonnances par médecin"

## Jeux de données

### Données de démonstration

**Au démarrage, l'application charge :**

```java
// Médicaments
- Doliprane (Analgésique, 5.99€, stock: 100)
- Aspirine (Analgésique, 3.50€, stock: 75)
- Amoxicilline (Antibiotique, 12.99€, stock: 50)

// Client
- Martin Pierre (CL001)

// Médecin
- Dupont Marie (RPPS: 12345678901)

// Mutuelle
- Mutuelle Santé Plus (70% de remboursement)
```

### Fichiers CSV d'exemple

#### data/clients.csv

```csv
identifiant;nom;prenom;adresse;codePostal;ville;telephone;email;numeroSecu;mutuelle
cdurand01;Durand;Camille;12 Rue des Fleurs;75012;Paris;0601020304;camille.durand@example.com;1987654321098;Aucune
jmartin02;Martin;Julien;5 Av. République;69002;Lyon;0611223344;julien.martin@example.com;1765432198765;Aucune
asoumah03;Soumah;Aïcha;10 Bd Liberté;13001;Marseille;0622334455;aicha.soumah@example.com;2001122233344;Aucune
```

#### data/medecins.csv

```csv
identifiant;nom;prenom;adresse;codePostal;ville;telephone;email;numeroRPPS
10101010101;Bernard;Claire;20 Rue Victor Hugo;33000;Bordeaux;0556123456;claire.bernard@cabinet.fr;10101010101
20202020202;Leroy;Thomas;8 Rue Pasteur;44000;Nantes;0251782345;thomas.leroy@cabinet.fr;20202020202
30303030303;Nguyen;Sophie;3 Allée des Tilleuls;31000;Toulouse;0561789988;sophie.nguyen@cabinet.fr;30303030303
```

**Note :** Ces fichiers servent de référence pour saisie manuelle. Pas d'import automatique implémenté.

## Dépannage

### L'interface ne s'ouvre pas
- Vérifier la version du JDK (21 minimum requis)
- Vérifier les logs d'erreur dans la console

### Icônes manquantes
- Vérifier la présence du dossier `icons/` à la racine
- Les icônes sont chargées avec chemin relatif

### Données perdues au redémarrage
- Normal : stockage en mémoire
- Seuls les achats sont persistés (data/achats.csv)
- Les données de démo sont rechargées automatiquement

### Stock incohérent
- Le stock est mis à jour lors des ventes
- Vérifier l'historique des ventes pour tracer les mouvements
- Utiliser "Inventaire Complet" pour voir l'état global

### Erreurs de validation
- Messages explicites affichés dans la barre de statut
- Champs en rouge avec tooltip explicatif
- Vérifier formats : téléphone (10 chiffres), RPPS (11 chiffres), etc.

## Tests

L'application inclut des tests unitaires JUnit :

```
src/test/
├── GestPharmacieServiceTest.java    # Tests du service
└── model/
    ├── AchatTest.java               # Tests des achats
    ├── MedecinTest.java             # Tests des médecins
    ├── MedicamentTest.java          # Tests des médicaments
    ├── MutuelleTest.java            # Tests des mutuelles
    └── OrdonnanceTest.java          # Tests des ordonnances
```

Pour exécuter les tests : `Run All Tests` dans IntelliJ

## Points techniques importants

### Thread-safety
- Utilisation de `ConcurrentHashMap` pour les maps
- Utilisation de `CopyOnWriteArrayList` pour les listes
- Évite les problèmes de concurrence dans l'interface

### Validation centralisée
- Toutes les validations dans `SecurityValidator`
- Validation à la création ET à la modification
- Messages d'erreur explicites

### Gestion d'événements
- `DataEventManager` pour la communication inter-panels
- Rafraîchissement automatique des compteurs
- Pattern Observer pour la cohérence des données

### Calculs automatiques
- Montants totaux calculés automatiquement
- Remboursements selon taux de mutuelle
- Stocks mis à jour en temps réel

---

**Projet développé avec Java 21 + Swing pour la gestion complète d'une pharmacie**
