package com.meteowatch.station.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String ville;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Double altitude;

    private LocalDate dateInstallation;

    /** Suppression logique : une station supprimee est desactivee, son historique reste valide. */
    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Capteur> capteurs = new ArrayList<>();

    public Station() {
    }

    public void ajouterCapteur(Capteur capteur) {
        capteurs.add(capteur);
        capteur.setStation(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getAltitude() { return altitude; }
    public void setAltitude(Double altitude) { this.altitude = altitude; }

    public LocalDate getDateInstallation() { return dateInstallation; }
    public void setDateInstallation(LocalDate dateInstallation) { this.dateInstallation = dateInstallation; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<Capteur> getCapteurs() { return capteurs; }
    public void setCapteurs(List<Capteur> capteurs) { this.capteurs = capteurs; }
}
