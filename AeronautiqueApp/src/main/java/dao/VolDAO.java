package dao;

import database.DatabaseConnection;
import models.Vol;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VolDAO {
    
    public List<Vol> getAllVols() {
        List<Vol> vols = new ArrayList<>();
        String query = "SELECT v.*, CONCAT(p.nom, ' ', p.prenom) as pilote_nom, " +
                      "a.modele as avion_modele FROM vols v " +
                      "LEFT JOIN pilotes p ON v.pilote_id = p.id " +
                      "LEFT JOIN avions a ON v.avion_id = a.id " +
                      "ORDER BY v.date_depart DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Vol vol = new Vol();
                vol.setId(rs.getInt("id"));
                vol.setNumeroVol(rs.getString("numero_vol"));
                vol.setPiloteId(rs.getInt("pilote_id"));
                vol.setAvionId(rs.getInt("avion_id"));
                vol.setAeroportDepart(rs.getString("aeroport_depart"));
                vol.setAeroportArrivee(rs.getString("aeroport_arrivee"));
                vol.setDateDepart(rs.getTimestamp("date_depart"));
                vol.setDateArrivee(rs.getTimestamp("date_arrivee"));
                vol.setStatut(rs.getString("statut"));
                vol.setPiloteNomComplet(rs.getString("pilote_nom"));
                vol.setAvionModele(rs.getString("avion_modele"));
                vols.add(vol);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vols;
    }
    
    public boolean addVol(Vol vol) {
        String query = "INSERT INTO vols (numero_vol, pilote_id, avion_id, " +
                      "aeroport_depart, aeroport_arrivee, date_depart, date_arrivee, statut) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, vol.getNumeroVol());
            pstmt.setInt(2, vol.getPiloteId());
            pstmt.setInt(3, vol.getAvionId());
            pstmt.setString(4, vol.getAeroportDepart());
            pstmt.setString(5, vol.getAeroportArrivee());
            pstmt.setTimestamp(6, vol.getDateDepart());
            pstmt.setTimestamp(7, vol.getDateArrivee());
            pstmt.setString(8, vol.getStatut());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateVol(Vol vol) {
        String query = "UPDATE vols SET numero_vol = ?, pilote_id = ?, avion_id = ?, " +
                      "aeroport_depart = ?, aeroport_arrivee = ?, date_depart = ?, " +
                      "date_arrivee = ?, statut = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, vol.getNumeroVol());
            pstmt.setInt(2, vol.getPiloteId());
            pstmt.setInt(3, vol.getAvionId());
            pstmt.setString(4, vol.getAeroportDepart());
            pstmt.setString(5, vol.getAeroportArrivee());
            pstmt.setTimestamp(6, vol.getDateDepart());
            pstmt.setTimestamp(7, vol.getDateArrivee());
            pstmt.setString(8, vol.getStatut());
            pstmt.setInt(9, vol.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteVol(int id) {
        String query = "DELETE FROM vols WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Vol> searchVols(String keyword) {
        List<Vol> vols = new ArrayList<>();
        String query = "SELECT v.*, CONCAT(p.nom, ' ', p.prenom) as pilote_nom, " +
                      "a.modele as avion_modele FROM vols v " +
                      "LEFT JOIN pilotes p ON v.pilote_id = p.id " +
                      "LEFT JOIN avions a ON v.avion_id = a.id " +
                      "WHERE v.numero_vol LIKE ? OR v.aeroport_depart LIKE ? " +
                      "OR v.aeroport_arrivee LIKE ? OR v.statut LIKE ? " +
                      "OR CONCAT(p.nom, ' ', p.prenom) LIKE ? OR a.modele LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 6; i++) {
                pstmt.setString(i, searchPattern);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vol vol = new Vol();
                vol.setId(rs.getInt("id"));
                vol.setNumeroVol(rs.getString("numero_vol"));
                vol.setPiloteId(rs.getInt("pilote_id"));
                vol.setAvionId(rs.getInt("avion_id"));
                vol.setAeroportDepart(rs.getString("aeroport_depart"));
                vol.setAeroportArrivee(rs.getString("aeroport_arrivee"));
                vol.setDateDepart(rs.getTimestamp("date_depart"));
                vol.setDateArrivee(rs.getTimestamp("date_arrivee"));
                vol.setStatut(rs.getString("statut"));
                vol.setPiloteNomComplet(rs.getString("pilote_nom"));
                vol.setAvionModele(rs.getString("avion_modele"));
                vols.add(vol);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vols;
    }
    
    public List<Vol> getVolsByStatut(String statut) {
        List<Vol> vols = new ArrayList<>();
        String query = "SELECT v.*, CONCAT(p.nom, ' ', p.prenom) as pilote_nom, " +
                      "a.modele as avion_modele FROM vols v " +
                      "LEFT JOIN pilotes p ON v.pilote_id = p.id " +
                      "LEFT JOIN avions a ON v.avion_id = a.id " +
                      "WHERE v.statut = ? ORDER BY v.date_depart DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vol vol = new Vol();
                vol.setId(rs.getInt("id"));
                vol.setNumeroVol(rs.getString("numero_vol"));
                vol.setPiloteId(rs.getInt("pilote_id"));
                vol.setAvionId(rs.getInt("avion_id"));
                vol.setAeroportDepart(rs.getString("aeroport_depart"));
                vol.setAeroportArrivee(rs.getString("aeroport_arrivee"));
                vol.setDateDepart(rs.getTimestamp("date_depart"));
                vol.setDateArrivee(rs.getTimestamp("date_arrivee"));
                vol.setStatut(rs.getString("statut"));
                vol.setPiloteNomComplet(rs.getString("pilote_nom"));
                vol.setAvionModele(rs.getString("avion_modele"));
                vols.add(vol);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vols;
    }
    
    public List<Vol> getVolsByDateRange(Timestamp startDate, Timestamp endDate) {
        List<Vol> vols = new ArrayList<>();
        String query = "SELECT v.*, CONCAT(p.nom, ' ', p.prenom) as pilote_nom, " +
                      "a.modele as avion_modele FROM vols v " +
                      "LEFT JOIN pilotes p ON v.pilote_id = p.id " +
                      "LEFT JOIN avions a ON v.avion_id = a.id " +
                      "WHERE v.date_depart BETWEEN ? AND ? ORDER BY v.date_depart";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vol vol = new Vol();
                vol.setId(rs.getInt("id"));
                vol.setNumeroVol(rs.getString("numero_vol"));
                vol.setPiloteId(rs.getInt("pilote_id"));
                vol.setAvionId(rs.getInt("avion_id"));
                vol.setAeroportDepart(rs.getString("aeroport_depart"));
                vol.setAeroportArrivee(rs.getString("aeroport_arrivee"));
                vol.setDateDepart(rs.getTimestamp("date_depart"));
                vol.setDateArrivee(rs.getTimestamp("date_arrivee"));
                vol.setStatut(rs.getString("statut"));
                vol.setPiloteNomComplet(rs.getString("pilote_nom"));
                vol.setAvionModele(rs.getString("avion_modele"));
                vols.add(vol);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vols;
    }
    
    public int countVolsByStatut(String statut) {
        String query = "SELECT COUNT(*) as total FROM vols WHERE statut = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countVolsEnRetardByCompagnie(String nomCompagnie) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int countVolsByDateAndCompagnie(LocalDate today, String nomCompagnie) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Vol getVolById(int volId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}