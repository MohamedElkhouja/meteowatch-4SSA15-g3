package com.meteowatch.readings.repository;

import com.meteowatch.readings.model.Releve;
import com.meteowatch.readings.model.StatutReleve;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ReleveRepository extends MongoRepository<Releve, String> {

    boolean existsByStationIdAndHorodatage(String stationId, Instant horodatage);

    /** Derniers relevés d'une station (le Pageable ne sert qu'à limiter le nombre de résultats). */
    List<Releve> findByStationIdOrderByHorodatageDesc(String stationId, Pageable limite);

    List<Releve> findTop100ByStatutOrderByHorodatageAsc(StatutReleve statut);
}
