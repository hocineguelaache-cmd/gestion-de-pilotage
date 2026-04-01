package models;

import java.sql.Timestamp;

public class Vol {
    private int id;
    private String numeroVol;
    private int piloteId;
    private int avionId;
    private String aeroportDepart;
    private String aeroportArrivee;
    private Timestamp dateDepart;
    private Timestamp dateArrivee;
    private String statut;
    private String piloteNomComplet; // Pour l'affichage
    private String avionModele; // Pour l'affichage
    
    public Vol() {}
    
    public Vol(String numeroVol, int piloteId, int avionId, String aeroportDepart, 
               String aeroportArrivee, Timestamp dateDepart, Timestamp dateArrivee, String statut) {
        this.numeroVol = numeroVol;
        this.piloteId = piloteId;
        this.avionId = avionId;
        this.aeroportDepart = aeroportDepart;
        this.aeroportArrivee = aeroportArrivee;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.statut = statut;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNumeroVol() { return numeroVol; }
    public void setNumeroVol(String numeroVol) { this.numeroVol = numeroVol; }
    
    public int getPiloteId() { return piloteId; }
    public void setPiloteId(int piloteId) { this.piloteId = piloteId; }
    
    public int getAvionId() { return avionId; }
    public void setAvionId(int avionId) { this.avionId = avionId; }
    
    public String getAeroportDepart() { return aeroportDepart; }
    public void setAeroportDepart(String aeroportDepart) { this.aeroportDepart = aeroportDepart; }
    
    public String getAeroportArrivee() { return aeroportArrivee; }
    public void setAeroportArrivee(String aeroportArrivee) { this.aeroportArrivee = aeroportArrivee; }
    
    public Timestamp getDateDepart() { return dateDepart; }
    public void setDateDepart(Timestamp dateDepart) { this.dateDepart = dateDepart; }
    
    public Timestamp getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(Timestamp dateArrivee) { this.dateArrivee = dateArrivee; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getPiloteNomComplet() { return piloteNomComplet; }
    public void setPiloteNomComplet(String piloteNomComplet) { this.piloteNomComplet = piloteNomComplet; }
    
    public String getAvionModele() { return avionModele; }
    public void setAvionModele(String avionModele) { this.avionModele = avionModele; }

    public Object getVilleDepart() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getVilleArrivee() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getDateHeureDepart() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getNumVol() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}