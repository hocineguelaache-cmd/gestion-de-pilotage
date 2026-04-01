package dao;

import database.DatabaseConnection;
import models.Pilote;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PiloteDAO {
    
    public List<Pilote> getAllPilotes() {
        List<Pilote> pilotes = new ArrayList<>();
        String query = "SELECT * FROM pilotes ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Pilote pilote = new Pilote();
                pilote.setId(rs.getInt("id"));
                pilote.setNom(rs.getString("nom"));
                pilote.setPrenom(rs.getString("prenom"));
                pilote.setMatricule(rs.getString("matricule"));
                pilote.setDateNaissance(rs.getDate("date_naissance"));
                pilote.setHeuresVolTotal(rs.getInt("heures_vol_total"));
                pilote.setStatut(rs.getString("statut"));
                pilotes.add(pilote);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pilotes;
    }
    
    public boolean addPilote(Pilote pilote) {
        String query = "INSERT INTO pilotes (nom, prenom, matricule, date_naissance, statut) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, pilote.getNom());
            pstmt.setString(2, pilote.getPrenom());
            pstmt.setString(3, pilote.getMatricule());
            pstmt.setDate(4, pilote.getDateNaissance());
            pstmt.setString(5, pilote.getStatut());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePilote(Pilote pilote) {
        String query = "UPDATE pilotes SET nom = ?, prenom = ?, matricule = ?, " +
                      "date_naissance = ?, heures_vol_total = ?, statut = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, pilote.getNom());
            pstmt.setString(2, pilote.getPrenom());
            pstmt.setString(3, pilote.getMatricule());
            pstmt.setDate(4, pilote.getDateNaissance());
            pstmt.setInt(5, pilote.getHeuresVolTotal());
            pstmt.setString(6, pilote.getStatut());
            pstmt.setInt(7, pilote.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePilote(int id) {
        String query = "DELETE FROM pilotes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Pilote> searchPilotes(String keyword) {
        List<Pilote> pilotes = new ArrayList<>();
        String query = "SELECT * FROM pilotes WHERE nom LIKE ? OR prenom LIKE ? OR matricule LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Pilote pilote = new Pilote();
                pilote.setId(rs.getInt("id"));
                pilote.setNom(rs.getString("nom"));
                pilote.setPrenom(rs.getString("prenom"));
                pilote.setMatricule(rs.getString("matricule"));
                pilote.setDateNaissance(rs.getDate("date_naissance"));
                pilote.setHeuresVolTotal(rs.getInt("heures_vol_total"));
                pilote.setStatut(rs.getString("statut"));
                pilotes.add(pilote);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pilotes;
    }

    public int countPilotesByCompagnie() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Pilote> getPilotesByStatut(String selectedStatut) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
   
}

