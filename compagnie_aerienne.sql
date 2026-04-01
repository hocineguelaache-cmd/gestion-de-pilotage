-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : jeu. 11 déc. 2025 à 08:56
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `compagnie_aerienne`
--

-- --------------------------------------------------------

--
-- Structure de la table `avions`
--

CREATE TABLE `avions` (
  `id` int(11) NOT NULL,
  `modele` varchar(50) NOT NULL,
  `immatriculation` varchar(20) NOT NULL,
  `capacite_passagers` int(11) DEFAULT NULL,
  `date_mise_en_service` date DEFAULT NULL,
  `statut` enum('disponible','entretien','hors_service') DEFAULT 'disponible'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `avions`
--

INSERT INTO `avions` (`id`, `modele`, `immatriculation`, `capacite_passagers`, `date_mise_en_service`, `statut`) VALUES
(1, 'Airbus A320', 'F-GKXA', 180, '2015-06-10', 'disponible'),
(2, 'Boeing 737', 'F-GZHJ', 189, '2018-03-22', 'disponible'),
(3, 'Airbus A330', 'F-HLKM', 293, '2012-11-05', 'entretien'),
(4, 'Boeing 777', 'F-GSQH', 350, '2019-09-15', 'disponible'),
(5, 'Airbus A350', 'F-HMIL', 325, '2020-01-30', 'hors_service');

-- --------------------------------------------------------

--
-- Structure de la table `pilotes`
--

CREATE TABLE `pilotes` (
  `id` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `matricule` varchar(20) NOT NULL,
  `date_naissance` date DEFAULT NULL,
  `heures_vol_total` int(11) DEFAULT 0,
  `statut` enum('actif','inactif','en_conge') DEFAULT 'actif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `pilotes`
--

INSERT INTO `pilotes` (`id`, `nom`, `prenom`, `matricule`, `date_naissance`, `heures_vol_total`, `statut`) VALUES
(1, 'Dubois', 'Jean', 'PIL001', '1985-03-15', 12000, 'inactif'),
(2, 'Martin', 'Sophie', 'PIL002', '1990-07-22', 8000, 'actif'),
(3, 'Bernard', 'Pierre', 'PIL003', '1978-11-30', 15000, 'actif'),
(4, 'Thomas', 'Marie', 'PIL004', '1992-05-18', 5000, 'actif'),
(5, 'Petit', 'Luc', 'PIL005', '1988-09-10', 10000, 'actif'),
(7, 'kamel', 'bkn', '2001', '2005-10-05', 0, 'actif'),
(8, 'Gl', 'Houcine', '2005', '2005-05-24', 0, 'en_conge'),
(10, '*', 'KAMEL', 'PIL013', '2005-10-06', 0, 'actif');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateurs`
--

CREATE TABLE `utilisateurs` (
  `id` int(11) NOT NULL,
  `nom_compagnie` varchar(100) NOT NULL,
  `login` varchar(50) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `nom_compagnie`, `login`, `mot_de_passe`, `email`, `date_creation`) VALUES
(1, 'Air France', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@airfrance.fr', '2025-12-06 15:19:24');

-- --------------------------------------------------------

--
-- Structure de la table `vols`
--

CREATE TABLE `vols` (
  `id` int(11) NOT NULL,
  `numero_vol` varchar(10) NOT NULL,
  `pilote_id` int(11) DEFAULT NULL,
  `avion_id` int(11) DEFAULT NULL,
  `aeroport_depart` varchar(100) DEFAULT NULL,
  `aeroport_arrivee` varchar(100) DEFAULT NULL,
  `date_depart` datetime DEFAULT NULL,
  `date_arrivee` datetime DEFAULT NULL,
  `statut` enum('programme','en_cours','termine','annule') DEFAULT 'programme'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `vols`
--

INSERT INTO `vols` (`id`, `numero_vol`, `pilote_id`, `avion_id`, `aeroport_depart`, `aeroport_arrivee`, `date_depart`, `date_arrivee`, `statut`) VALUES
(1, 'AF123', 1, 1, 'CDG Paris', 'JFK New York', '2024-01-15 14:30:00', '2024-01-15 18:00:00', 'termine'),
(2, 'AF456', 2, 2, 'ORY Paris', 'LHR Londres', '2024-01-16 09:00:00', '2024-01-16 10:15:00', 'en_cours'),
(3, 'AF789', 1, 3, 'CDG Paris', 'DXB Dubaï', '2024-01-17 22:00:00', '2024-01-18 06:30:00', 'programme'),
(4, 'AF321', 4, 4, 'NCE Nice', 'CDG Paris', '2024-01-15 07:45:00', '2024-01-15 09:00:00', 'termine'),
(5, 'AF654', 2, 5, 'CDG Paris', 'NRT Tokyo', '2024-01-18 13:20:00', '2024-01-19 08:45:00', 'annule'),
(6, '9999999', 8, 1, 'alger', 'paris', '2025-12-12 21:20:10', '2034-12-12 22:20:10', 'programme'),
(7, '120', 10, 5, 'ALGER', 'PARIS', '2025-12-11 07:30:00', '2025-12-12 07:30:00', 'programme');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `avions`
--
ALTER TABLE `avions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `immatriculation` (`immatriculation`);

--
-- Index pour la table `pilotes`
--
ALTER TABLE `pilotes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `matricule` (`matricule`);

--
-- Index pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`);

--
-- Index pour la table `vols`
--
ALTER TABLE `vols`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero_vol` (`numero_vol`),
  ADD KEY `pilote_id` (`pilote_id`),
  ADD KEY `avion_id` (`avion_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `avions`
--
ALTER TABLE `avions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `pilotes`
--
ALTER TABLE `pilotes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `vols`
--
ALTER TABLE `vols`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `vols`
--
ALTER TABLE `vols`
  ADD CONSTRAINT `vols_ibfk_1` FOREIGN KEY (`pilote_id`) REFERENCES `pilotes` (`id`),
  ADD CONSTRAINT `vols_ibfk_2` FOREIGN KEY (`avion_id`) REFERENCES `avions` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
