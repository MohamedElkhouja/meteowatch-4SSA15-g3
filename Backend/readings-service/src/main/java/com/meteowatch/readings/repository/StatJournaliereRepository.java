package com.meteowatch.readings.repository;

import com.meteowatch.readings.model.StatJournaliere;
import com.meteowatch.readings.model.TypeMesure;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StatJournaliereRepository extends MongoRepository<StatJournaliere, String> {

    Optional<StatJournaliere> findByStationIdAndDateAndType(String stationId, LocalDate date, TypeMesure type);
}
