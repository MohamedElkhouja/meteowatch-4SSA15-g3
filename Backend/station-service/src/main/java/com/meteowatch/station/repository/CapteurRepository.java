package com.meteowatch.station.repository;

import com.meteowatch.station.entity.Capteur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapteurRepository extends JpaRepository<Capteur, Long> {
}
