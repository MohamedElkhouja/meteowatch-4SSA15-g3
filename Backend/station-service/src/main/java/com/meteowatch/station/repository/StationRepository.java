package com.meteowatch.station.repository;

import com.meteowatch.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByVilleIgnoreCase(String ville);
}
