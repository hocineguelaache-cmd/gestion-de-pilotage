package models;

import java.sql.Date;

public class Pilote {
    private int id;
    private String nom;
    private String prenom;
    private String matricule;
    private Date dateNaissance;
    private int heuresVolTotal;
    private String statut;
    
    public Pilote() {}
    
    public Pilote(String nom, String prenom, String matricule, Date dateNaissance, String statut) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.dateNaissance = dateNaissance;
        this.statut = statut;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    
    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }
    
    public int getHeuresVolTotal() { return heuresVolTotal; }
    public void setHeuresVolTotal(int heuresVolTotal) { this.heuresVolTotal = heuresVolTotal; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    @Override
    public String toString() {
        return nom + " " + prenom + " (" + matricule + ")";
    }
}