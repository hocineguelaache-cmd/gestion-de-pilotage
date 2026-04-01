/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.aeronautiqueapp;

import database.DatabaseSetup;
import views.LoginFrame;
import javax.swing.*;


public class AeronautiqueApp {

    public static void main(String[] args) {
        // Initialiser la base de données
        DatabaseSetup.createDatabase();
        
        // Lancer l'interface graphique
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Définir le look and feel du système
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    // Afficher la fenêtre de connexion
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Erreur lors du démarrage de l'application: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
