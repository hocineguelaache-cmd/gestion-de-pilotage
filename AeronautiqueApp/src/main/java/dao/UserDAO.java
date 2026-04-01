package dao;

import database.DatabaseConnection;
import models.User;
import java.sql.*;

public class UserDAO {
    
    public User authenticate(String login, String password) {
        String query = "SELECT id, nom_compagnie, login, email, date_creation " +
                      "FROM utilisateurs WHERE login = ? AND mot_de_passe = SHA2(?, 256)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("nom_compagnie"),
                    rs.getString("login"),
                    rs.getString("email"),
                    rs.getTimestamp("date_creation")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}