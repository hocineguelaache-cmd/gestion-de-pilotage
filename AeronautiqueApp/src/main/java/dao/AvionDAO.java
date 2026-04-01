package dao;

import database.DatabaseConnection;
import models.Avion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvionDAO {
    
    public List<Avion> getAllAvions() {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM avions ORDER BY modele";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setModele(rs.getString("modele"));
                avion.setImmatriculation(rs.getString("immatriculation"));
                avion.setCapacitePassagers(rs.getInt("capacite_passagers"));
                avion.setDateMiseEnService(rs.getDate("date_mise_en_service"));
                avion.setStatut(rs.getString("statut"));
                avions.add(avion);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }
    
    public boolean addAvion(Avion avion) {
        String query = "INSERT INTO avions (modele, immatriculation, capacite_passagers, " +
                      "date_mise_en_service, statut) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, avion.getModele());
            pstmt.setString(2, avion.getImmatriculation());
            pstmt.setInt(3, avion.getCapacitePassagers());
            pstmt.setDate(4, avion.getDateMiseEnService());
            pstmt.setString(5, avion.getStatut());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAvion(Avion avion) {
        String query = "UPDATE avions SET modele = ?, immatriculation = ?, " +
                      "capacite_passagers = ?, date_mise_en_service = ?, statut = ? " +
                      "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, avion.getModele());
            pstmt.setString(2, avion.getImmatriculation());
            pstmt.setInt(3, avion.getCapacitePassagers());
            pstmt.setDate(4, avion.getDateMiseEnService());
            pstmt.setString(5, avion.getStatut());
            pstmt.setInt(6, avion.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteAvion(int id) {
        String query = "DELETE FROM avions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Avion> searchAvions(String keyword) {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM avions WHERE modele LIKE ? OR immatriculation LIKE ? " +
                      "OR statut LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setModele(rs.getString("modele"));
                avion.setImmatriculation(rs.getString("immatriculation"));
                avion.setCapacitePassagers(rs.getInt("capacite_passagers"));
                avion.setDateMiseEnService(rs.getDate("date_mise_en_service"));
                avion.setStatut(rs.getString("statut"));
                avions.add(avion);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }
    
    public List<Avion> getAvionsByStatut(String statut) {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM avions WHERE statut = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setModele(rs.getString("modele"));
                avion.setImmatriculation(rs.getString("immatriculation"));
                avion.setCapacitePassagers(rs.getInt("capacite_passagers"));
                avion.setDateMiseEnService(rs.getDate("date_mise_en_service"));
                avion.setStatut(rs.getString("statut"));
                avions.add(avion);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }
    
    public Avion getAvionById(int id) {
        String query = "SELECT * FROM avions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setModele(rs.getString("modele"));
                avion.setImmatriculation(rs.getString("immatriculation"));
                avion.setCapacitePassagers(rs.getInt("capacite_passagers"));
                avion.setDateMiseEnService(rs.getDate("date_mise_en_service"));
                avion.setStatut(rs.getString("statut"));
                return avion;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int countAvionsByCompagnie() {
    String query = "SELECT COUNT(*) FROM avion";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}

}
