package models;

import java.sql.Timestamp;

public class User {
    private int id;
    private String nomCompagnie;
    private String login;
    private String email;
    private Timestamp dateCreation;
    
    public User() {}
    
    public User(int id, String nomCompagnie, String login, String email, Timestamp dateCreation) {
        this.id = id;
        this.nomCompagnie = nomCompagnie;
        this.login = login;
        this.email = email;
        this.dateCreation = dateCreation;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNomCompagnie() { return nomCompagnie; }
    public void setNomCompagnie(String nomCompagnie) { this.nomCompagnie = nomCompagnie; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public String getNomComparnie() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}