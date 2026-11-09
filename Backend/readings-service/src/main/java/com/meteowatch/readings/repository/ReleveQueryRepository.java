package com.meteowatch.readings.repository;

import com.meteowatch.readings.model.Releve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Requêtes dynamiques (filtre par période) et agrégation des statistiques journalières. */
@Repository
public class ReleveQueryRepository {

    /** Résultat brut du pipeline d'agrégation. */
    public record Agregat(Double min, Double max, Double moyenne, Integer nombreReleves) {
    }

    private final MongoTemplate mongoTemplate;

    public ReleveQueryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /** Relevés d'une station, filtrés par période (bornes incluses, optionnelles), paginés. */
    public Page<Releve> rechercher(String stationId, Instant from, Instant to, Pageable pageable) {
        Criteria criteria = Criteria.where("stationId").is(stationId);
        if (from != null && to != null) {
            criteria = criteria.and("horodatage").gte(from).lte(to);
        } else if (from != null) {
            criteria = criteria.and("horodatage").gte(from);
        } else if (to != null) {
            criteria = criteria.and("horodatage").lte(to);
        }
        Query query = new Query(criteria);
        long total = mongoTemplate.count(query, Releve.class);
        List<Releve> content = mongoTemplate.find(query.with(pageable), Releve.class);
        return new PageImpl<>(content, pageable, total);
    }

    /** Min, max, moyenne d'un champ sur [debut, fin[ ; vide s'il n'y a aucun relevé. */
    public Optional<Agregat> agreger(String stationId, Instant debut, Instant fin, String champ) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("stationId").is(stationId)
                        .and("horodatage").gte(debut).lt(fin)),
                Aggregation.group()
                        .min(champ).as("min")
                        .max(champ).as("max")
                        .avg(champ).as("moyenne")
                        .count().as("nombreReleves"));
        AggregationResults<Agregat> results = mongoTemplate.aggregate(aggregation, "releves", Agregat.class);
        return Optional.ofNullable(results.getUniqueMappedResult());
    }
}
