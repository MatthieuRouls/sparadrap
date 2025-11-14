-- Script d'initialisation de la base de données Sparadrap
-- À exécuter pour créer la base et les tables nécessaires

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS sparadrap_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sparadrap_db;

-- Table des mutuelles
CREATE TABLE IF NOT EXISTS mutuelles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    adresse VARCHAR(255),
    code_postal VARCHAR(10),
    ville VARCHAR(100),
    telephone VARCHAR(20),
    email VARCHAR(100),
    taux_remboursement DECIMAL(5,2) DEFAULT 0.00,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table des clients
CREATE TABLE IF NOT EXISTS clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    identifiant VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    code_postal VARCHAR(10),
    ville VARCHAR(100),
    telephone VARCHAR(20),
    email VARCHAR(100),
    numero_secu VARCHAR(15),
    mutuelle_id INT,
    date_naissance DATE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (mutuelle_id) REFERENCES mutuelles(id) ON DELETE SET NULL,
    INDEX idx_identifiant (identifiant),
    INDEX idx_nom (nom),
    INDEX idx_prenom (prenom)
) ENGINE=InnoDB;

-- Table des médecins
CREATE TABLE IF NOT EXISTS medecins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    identifiant VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255),
    code_postal VARCHAR(10),
    ville VARCHAR(100),
    telephone VARCHAR(20),
    email VARCHAR(100),
    numero_rpps VARCHAR(11) NOT NULL UNIQUE,
    specialite VARCHAR(100),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_identifiant (identifiant),
    INDEX idx_rpps (numero_rpps)
) ENGINE=InnoDB;

-- Table des catégories de médicaments
CREATE TABLE IF NOT EXISTS categories_medicaments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB;

-- Table des médicaments
CREATE TABLE IF NOT EXISTS medicaments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    categorie_id INT,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    date_mise_marche DATE,
    est_disponible BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categorie_id) REFERENCES categories_medicaments(id) ON DELETE SET NULL,
    INDEX idx_nom (nom),
    INDEX idx_categorie (categorie_id)
) ENGINE=InnoDB;

-- Table des ordonnances
CREATE TABLE IF NOT EXISTS ordonnances (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    date_emission DATE NOT NULL,
    client_id INT NOT NULL,
    medecin_id INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (medecin_id) REFERENCES medecins(id) ON DELETE CASCADE,
    INDEX idx_numero (numero),
    INDEX idx_client (client_id),
    INDEX idx_medecin (medecin_id)
) ENGINE=InnoDB;

-- Table des lignes d'ordonnance (médicaments prescrits)
CREATE TABLE IF NOT EXISTS lignes_ordonnances (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ordonnance_id INT NOT NULL,
    medicament_id INT NOT NULL,
    quantite INT NOT NULL,
    posologie TEXT,
    FOREIGN KEY (ordonnance_id) REFERENCES ordonnances(id) ON DELETE CASCADE,
    FOREIGN KEY (medicament_id) REFERENCES medicaments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table des achats/transactions
CREATE TABLE IF NOT EXISTS achats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_transaction VARCHAR(50) NOT NULL UNIQUE,
    date_achat DATETIME NOT NULL,
    client_id INT NOT NULL,
    ordonnance_id INT,
    montant_total DECIMAL(10,2) NOT NULL,
    montant_rembourse DECIMAL(10,2) DEFAULT 0.00,
    montant_net DECIMAL(10,2) NOT NULL,
    type_achat ENUM('DIRECT', 'ORDONNANCE') NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (ordonnance_id) REFERENCES ordonnances(id) ON DELETE SET NULL,
    INDEX idx_numero (numero_transaction),
    INDEX idx_client (client_id),
    INDEX idx_date (date_achat)
) ENGINE=InnoDB;

-- Table des lignes d'achat (détails des médicaments achetés)
CREATE TABLE IF NOT EXISTS lignes_achats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    achat_id INT NOT NULL,
    medicament_id INT NOT NULL,
    quantite INT NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    montant_ligne DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (achat_id) REFERENCES achats(id) ON DELETE CASCADE,
    FOREIGN KEY (medicament_id) REFERENCES medicaments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Insertion de données de test pour les catégories de médicaments
INSERT INTO categories_medicaments (nom, description) VALUES
('Antalgiques', 'Médicaments contre la douleur'),
('Antibiotiques', 'Traitement des infections bactériennes'),
('Anti-inflammatoires', 'Réduction de l\'inflammation'),
('Antihistaminiques', 'Traitement des allergies'),
('Vitamines', 'Compléments vitaminiques'),
('Cardiovasculaires', 'Traitement des maladies cardiaques'),
('Dermatologie', 'Soins de la peau'),
('Gastro-entérologie', 'Traitement des troubles digestifs');

-- Insertion de quelques mutuelles de test
INSERT INTO mutuelles (nom, adresse, code_postal, ville, telephone, email, taux_remboursement) VALUES
('Mutuelle Générale', '10 Rue de la Santé', '75013', 'Paris', '0140000000', 'contact@mutuelle-generale.fr', 70.00),
('AssurSanté Plus', '25 Avenue de la République', '69002', 'Lyon', '0478000000', 'info@assursante.fr', 80.00),
('PrévoyanceSanté', '5 Boulevard Victor Hugo', '31000', 'Toulouse', '0561000000', 'contact@prevoyance.fr', 75.00);

-- Insertion de quelques médicaments de test
INSERT INTO medicaments (nom, categorie_id, prix_unitaire, stock, description) VALUES
('Paracétamol 1g', 1, 2.50, 100, 'Antalgique et antipyrétique'),
('Ibuprofène 400mg', 3, 3.20, 80, 'Anti-inflammatoire non stéroïdien'),
('Amoxicilline 500mg', 2, 5.80, 50, 'Antibiotique à large spectre'),
('Doliprane 500mg', 1, 2.10, 150, 'Paracétamol dosé à 500mg'),
('Aspirine 100mg', 3, 1.90, 120, 'Antiagrégant plaquettaire'),
('Vitamine C 500mg', 5, 4.50, 90, 'Complément vitaminique'),
('Biafine', 7, 6.80, 60, 'Émulsion pour soins de la peau');

COMMIT;

-- Afficher un message de confirmation
SELECT 'Base de données initialisée avec succès!' as message;
