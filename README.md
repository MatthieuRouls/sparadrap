# Logicielle Pharmacie Sparadrap — Guide d’utilisation

Ce projet est une application Java Swing pour la gestion d’une pharmacie: clients, médecins, médicaments, ventes et statistiques.

## Prérequis
- **JDK 21**
- **IntelliJ IDEA** (ou tout IDE Java capable d’exécuter une application Swing)
- OS: Windows, macOS ou Linux

## Démarrage rapide
1. Ouvrir le projet dans **IntelliJ**.
2. Ouvrir la classe `main.view.PharmacieMainFrame`.
3. Lancer la méthode `main` de `PharmacieMainFrame`.
4. L’application s’ouvre sur l’onglet **Accueil** avec des stats et **Actions rapides**.

## Navigation principale
- **Accueil**: accès rapide aux modules, stats synthétiques.
  - Boutons d’actions: 
    - "Nouvelle vente" (ouvre Ventes > Vente directe)
    - "Rechercher client" (ouvre Clients et place le focus sur la recherche)
    - "Gérer stock" (ouvre Médicaments et place le focus sur la recherche)
- **Clients**: créer/rechercher/modifier/supprimer des clients.
- **Médecins**: créer/rechercher/modifier/supprimer des médecins.
- **Médicaments**: inventaire, recherche, modification du stock.
- **Ventes**: vente directe, vente sur ordonnance, historique.
- **Statistiques**: synthèses.

## Scénarios de test (conseillés à l’examinateur)
1. Créer un **client** dans l’onglet Clients:
   - Renseigner les champs requis puis cliquer **Sauvegarder**.
   - L'identifiant est initialisé automatiquement et ne peut pas être modifié (on utilise les 2 premieres lettres du
   prenom et les 3 premieres du nom).
2. Créer un **médecin** dans l’onglet Médecins:
   - Renseigner les champs, notamment le **RPPS** (ex: `10101010101`).
3. Rechercher dans **Accueil**:
   - Cliquer **Rechercher client** pour aller au module Clients et taper l’identifiant.
   - Cliquer **Gérer stock** pour aller au module Médicaments et modifier un stock.
4. Ventes:
   - Aller dans **Ventes > Vente directe**.
   - Sélectionner un client par identifiant, ajouter un médicament au panier, valider la vente.
5. Historique:
   - Onglet **Ventes > Historique**: vérifier le récapitulatif et les totaux (Total / Remboursé / Net) en bas.

## Jeu de données (CSV) fourni
Des fichiers CSV simples sont fournis pour aider aux tests manuels (vous pouvez les ouvrir et copier/coller les lignes)
- `data/clients.csv`
- `data/medecins.csv`

Import automatique non requis: utilisez ces CSV comme **référence** pour saisir des données rapidement via l’UI.

### data/clients.csv
Format des colonnes:
- identifiant;nom;prenom;adresse;codePostal;ville;telephone;email;numeroSecu;mutuelle

Exemple:
```
cdurand01;Durand;Camille;12 Rue des Fleurs;75012;Paris;0601020304;camille.durand@example.com;1987654321098;Aucune
jmartin02;Martin;Julien;5 Av. République;69002;Lyon;0611223344;julien.martin@example.com;1765432198765;Aucune
asoumah03;Soumah;Aïcha;10 Bd Liberté;13001;Marseille;0622334455;aicha.soumah@example.com;2001122233344;Aucune
```

Notes:
- La colonne `mutuelle` peut être `Aucune` ou le nom exact d’une mutuelle existante.
- L’identifiant peut être saisi, ou généré dans l’UI à partir prénom/nom.

### data/medecins.csv
Format des colonnes:
- identifiant;nom;prenom;adresse;codePostal;ville;telephone;email;numeroRPPS

Exemple:
```
10101010101;Bernard;Claire;20 Rue Victor Hugo;33000;Bordeaux;0556123456;claire.bernard@cabinet.fr;10101010101
20202020202;Leroy;Thomas;8 Rue Pasteur;44000;Nantes;0251782345;thomas.leroy@cabinet.fr;20202020202
30303030303;Nguyen;Sophie;3 Allée des Tilleuls;31000;Toulouse;0561789988;sophie.nguyen@cabinet.fr;30303030303
```

Notes:
- `identifiant` peut être le RPPS.
- Le champ `numeroRPPS` est requis.

## Conseils d’utilisation
- Utiliser les boutons d’actions dans Accueil pour accéder rapidement aux modules.
- Dans les onglets Clients/Médecins/Ventes, cliquez sur **Ajouter** pour créer un nouveau client/médecin/vente.
- Pour rechercher un client ou un médecin, tapez son identifiant dans la barre de recherche correspondante.
- Pour modifier un client ou un médecin, double-cliquez sur une ligne dans le tableau correspondant.
- Pour supprimer un client ou un médecin, sélectionnez une ligne et cliquez sur **Supprimer**.
- La recherche des clients se fait par **identifiant**.
- Les médicaments de démonstration sont chargés au démarrage; vous pouvez ajuster les **stocks** dans l’onglet Médicaments.
- L’historique calcule et affiche les **totaux** (ventes, Total, Remboursé, Net) selon le filtre sélectionné.

## Dépannage rapide
- Si l’UI ne s’ouvre pas: vérifier la version **JDK** utilisée par le projet.
- Icônes manquantes: vérifier que le dossier `icons/` est présent à la racine du projet.
- Données absentes: re-lancer l’application; des données de démo sont initialisées.

---
Projet préparé pour évaluation: contenu simplifié, actions rapides depuis l’Accueil, et dataset CSV d’exemple.