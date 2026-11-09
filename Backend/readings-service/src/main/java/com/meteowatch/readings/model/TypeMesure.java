package com.meteowatch.readings.model;

/** Les grandeurs mesurées par une station. */
public enum TypeMesure {
    TEMPERATURE("temperature", "°C"),
    HUMIDITE("humidite", "%"),
    PRESSION("pression", "hPa"),
    VENT("vitesseVent", "km/h"),
    PRECIPITATION("precipitation", "mm");

    private final String champ;
    private final String unite;

    TypeMesure(String champ, String unite) {
        this.champ = champ;
        this.unite = unite;
    }

    /** Nom du champ dans la collection MongoDB "releves". */
    public String champ() {
        return champ;
    }

    public String unite() {
        return unite;
    }

    public Double valeurDe(Releve releve) {
        return switch (this) {
            case TEMPERATURE -> releve.temperature();
            case HUMIDITE -> releve.humidite();
            case PRESSION -> releve.pression();
            case VENT -> releve.vitesseVent();
            case PRECIPITATION -> releve.precipitation();
        };
    }
}
