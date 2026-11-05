Application de Gestion des Vols pour Compagnie Aérienne

Projet universitaire — 3ème année ING (module : Génie Logiciel) Université Abderrahmane Mira de Béjaïa — Faculté des Sciences Exactes, Département d'Informatique.

📄 Document du projet : le rapport complet est disponible ici : 
(<docs/RapportFinale.pdf>). Il est maintenant(.docs/RapportFinale.pdf)

Présentation du projet

Ce projet consiste en l'analyse, la modélisation UML et le développement d'une application informatique complète de gestion des opérations aériennes pour une compagnie. L'application permet d'organiser et d'assurer le suivi des avions, des pilotes et des compagnies, ainsi que la programmation des vols, tout en garantissant l'intégrité et la cohérence des données.

Fonctionnalités principales
1. Authentification et sécurité
Connexion sécurisée par identifiant (login) et mot de passe pour chaque compagnie.
Contrôle d'accès et isolation des données par compagnie.
2. Gestion des avions
CRUD complet : ajout, modification, suppression et consultation des avions (code, type, modèle, capacité en passagers).
Contrainte d'intégrité : interdiction de supprimer un avion s'il est affecté à un vol actif.
3. Gestion des pilotes
CRUD complet : ajout, modification, suppression et consultation des pilotes (identifiant, nom, prénom).
Contrainte d'intégrité : interdiction de supprimer un pilote affecté à un vol programmé.
4. Gestion et programmation des vols
Création et mise à jour des vols (numéro de vol, ville de départ et d'arrivée, date, affectation d'un avion et d'un pilote).
Vérification automatique de disponibilité : détection des conflits pour éviter d'affecter un pilote ou un avion indisponible le même jour.
Règle d'association stricte : 1 vol = 1 avion + 1 pilote + 1 compagnie.
5. Recherche et filtrage avancés
Recherche par numéro de vol (exact), par avion (code ou ID), par pilote ou par date.
Filtrage par ville de départ et par ville d'arrivée.
Recherche multicritère combinée.
Technologies et outils utilisés
Langage backend : Java
Base de données : MySQL
Modélisation UML : StarUML
Scripting SQL : DDL et DML avec contraintes d'intégrité référentielle (PRIMARY KEY, FOREIGN KEY, CHECK)
Structure de la base de données (SQL)

Le schéma relationnel est composé de 4 tables principales :

sql
-- 1. Table Compagnie
CREATE TABLE Compagnie (
    codeCompagnie VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    siegeSocial VARCHAR(150),
    login VARCHAR(50) UNIQUE NOT NULL,
    motDePasse VARCHAR(100) NOT NULL
);

-- 2. Table Avion
CREATE TABLE Avion (
    codeAv VARCHAR(10) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    nbPassagers INT CHECK (nbPassagers > 0),
    codeCompagnie VARCHAR(10) NOT NULL,
    CONSTRAINT fk_Avion_Compagnie FOREIGN KEY (codeCompagnie)
        REFERENCES Compagnie (codeCompagnie) ON DELETE CASCADE
);

-- 3. Table Pilote
CREATE TABLE Pilote (
    idPilote INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    codeCompagnie VARCHAR(10) NOT NULL,
    CONSTRAINT fk_Pilote_Compagnie FOREIGN KEY (codeCompagnie)
        REFERENCES Compagnie (codeCompagnie) ON DELETE CASCADE
);

-- 4. Table Vol
CREATE TABLE Vol (
    numeroVol VARCHAR(10) PRIMARY KEY,
    villeDepart VARCHAR(100) NOT NULL,
    villeArrivee VARCHAR(100) NOT NULL,
    dateVol DATE NOT NULL,
    codeCompagnie VARCHAR(10) NOT NULL,
    codeAv VARCHAR(10) NOT NULL,
    idPilote INT NOT NULL,
    CONSTRAINT fk_Vol_Compagnie FOREIGN KEY (codeCompagnie)
        REFERENCES Compagnie (codeCompagnie) ON DELETE CASCADE,
    CONSTRAINT fk_Vol_Avion FOREIGN KEY (codeAv)
        REFERENCES Avion (codeAv) ON DELETE RESTRICT,
    CONSTRAINT fk_Vol_Pilote FOREIGN KEY (idPilote)
        REFERENCES Pilote (idPilote) ON DELETE RESTRICT
);

Auteurs

Projet réalisé par :
Guelaache Hocine
Boukhenak kamel
