package models;

import java.sql.Date;

public class Avion {
    private int id;
    private String modele;
    private String immatriculation;
    private int capacitePassagers;
    private Date dateMiseEnService;
    private String statut;
    
    public Avion() {}
    
    public Avion(String modele, String immatriculation, int capacitePassagers, 
                 Date dateMiseEnService, String statut) {
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.capacitePassagers = capacitePassagers;
        this.dateMiseEnService = dateMiseEnService;
        this.statut = statut;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    
    public int getCapacitePassagers() { return capacitePassagers; }
    public void setCapacitePassagers(int capacitePassagers) { this.capacitePassagers = capacitePassagers; }
    
    public Date getDateMiseEnService() { return dateMiseEnService; }
    public void setDateMiseEnService(Date dateMiseEnService) { this.dateMiseEnService = dateMiseEnService; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    @Override
    public String toString() {
        return modele + " (" + immatriculation + ")";
    }
}