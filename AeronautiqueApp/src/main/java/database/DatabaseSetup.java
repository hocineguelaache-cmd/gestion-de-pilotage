package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void createDatabase() {
        String[] createTables = {
            "CREATE DATABASE IF NOT EXISTS compagnie_aerienne",
            
            "USE compagnie_aerienne",
            
            "CREATE TABLE IF NOT EXISTS utilisateurs (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "nom_compagnie VARCHAR(100) NOT NULL," +
            "login VARCHAR(50) UNIQUE NOT NULL," +
            "mot_de_passe VARCHAR(255) NOT NULL," +
            "email VARCHAR(100)," +
            "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
            
            "CREATE TABLE IF NOT EXISTS pilotes (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "nom VARCHAR(50) NOT NULL," +
            "prenom VARCHAR(50) NOT NULL," +
            "matricule VARCHAR(20) UNIQUE NOT NULL," +
            "date_naissance DATE," +
            "heures_vol_total INT DEFAULT 0," +
            "statut ENUM('actif', 'inactif', 'en_conge') DEFAULT 'actif')",
            
            "CREATE TABLE IF NOT EXISTS avions (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "modele VARCHAR(50) NOT NULL," +
            "immatriculation VARCHAR(20) UNIQUE NOT NULL," +
            "capacite_passagers INT," +
            "date_mise_en_service DATE," +
            "statut ENUM('disponible', 'entretien', 'hors_service') DEFAULT 'disponible')",
            
            "CREATE TABLE IF NOT EXISTS vols (" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "numero_vol VARCHAR(10) UNIQUE NOT NULL," +
            "pilote_id INT," +
            "avion_id INT," +
            "aeroport_depart VARCHAR(100)," +
            "aeroport_arrivee VARCHAR(100)," +
            "date_depart DATETIME," +
            "date_arrivee DATETIME," +
            "statut ENUM('programme', 'en_cours', 'termine', 'annule')," +
            "FOREIGN KEY (pilote_id) REFERENCES pilotes(id)," +
            "FOREIGN KEY (avion_id) REFERENCES avions(id))",
            
            "INSERT INTO utilisateurs (nom_compagnie, login, mot_de_passe, email) " +
            "SELECT 'Air France', 'admin', SHA2('admin123', 256), 'contact@airfrance.fr' " +
            "WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE login = 'admin')"
        };
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String query : createTables) {
                try {
                    stmt.executeUpdate(query);
                } catch (Exception e) {
                    System.out.println("Note: " + e.getMessage());
                }
            }
            System.out.println("Base de données configurée avec succès.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}