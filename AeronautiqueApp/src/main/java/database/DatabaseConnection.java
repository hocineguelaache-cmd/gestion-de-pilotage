package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Configuration XAMPP par défaut
    private static final String URL = "jdbc:mysql://localhost:3306/compagnie_aerienne?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";      // Utilisateur par défaut XAMPP
    private static final String PASSWORD = "";      // Mot de passe vide par défaut
    
    private static Connection connection;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println(" Connexion à XAMPP MySQL établie.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println(" Driver MySQL non trouvé!");
            showXMAMPPTroubleshooting();
            throw new RuntimeException("Driver MySQL requis", e);
        } catch (SQLException e) {
            System.err.println(" Erreur de connexion à XAMPP!");
            showXMAMPPTroubleshooting();
            System.err.println("Erreur SQL: " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à XAMPP", e);
        }
        return connection;
    }
    
    private static void showXMAMPPTroubleshooting() {
        System.err.println("\n=== DÉPANNAGE XAMPP ===");
        System.err.println("1. Vérifiez que XAMPP est démarré");
        System.err.println("2. Démarrez Apache et MySQL dans le panneau de contrôle XAMPP");
        System.err.println("3. Accédez à http://localhost/phpmyadmin");
        System.err.println("4. Créez manuellement la base 'compagnie_aerienne' si nécessaire");
        System.err.println("5. Vérifiez que le port MySQL est 3306");
        System.err.println("6. Vérifiez vos identifiants dans phpMyAdmin");
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture: " + e.getMessage());
            }
        }
    }
}
