package com.meteowatch.station.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "capteurs")
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCapteur type;

    @Column(nullable = false)
    private String unite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatCapteur etat = EtatCapteur.ACTIF;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id")
    private Station station;

    public Capteur() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TypeCapteur getType() { return type; }
    public void setType(TypeCapteur type) { this.type = type; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public EtatCapteur getEtat() { return etat; }
    public void setEtat(EtatCapteur etat) { this.etat = etat; }

    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }
}
