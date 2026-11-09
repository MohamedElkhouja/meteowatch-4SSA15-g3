package com.meteowatch.readings.controller;

import com.meteowatch.readings.dto.PageResponse;
import com.meteowatch.readings.dto.ReleveRequest;
import com.meteowatch.readings.dto.ReleveResponse;
import com.meteowatch.readings.dto.StatJournaliereResponse;
import com.meteowatch.readings.model.StatutReleve;
import com.meteowatch.readings.model.TypeMesure;
import com.meteowatch.readings.service.ReleveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/releves")
@Tag(name = "Relevés météo")
public class ReleveController {

    private final ReleveService releveService;

    public ReleveController(ReleveService releveService) {
        this.releveService = releveService;
    }

    /**
     * POST /api/releves : 201 (station vérifiée), 202 (Station Service indisponible : relevé « à valider »),
     * 400 (données invalides), 404 (station inexistante), 409 (relevé déjà enregistré).
     */
    @PostMapping
    public ResponseEntity<ReleveResponse> creer(@Valid @RequestBody ReleveRequest requete) {
        ReleveResponse cree = releveService.enregistrer(requete);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(cree.id()).toUri();
        HttpStatus status = cree.statut() == StatutReleve.VALIDE ? HttpStatus.CREATED : HttpStatus.ACCEPTED;
        return ResponseEntity.status(status).location(location).body(cree);
    }

    /** GET /api/releves?stationId=&from=&to=&page=&size= : 200, 400. */
    @GetMapping
    public PageResponse<ReleveResponse> rechercher(
            @RequestParam("stationId") @NotBlank String stationId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) int size) {
        return releveService.rechercher(stationId, from, to, page, size);
    }

    /** GET /api/releves/derniers?stationId=&limite= : 200 (les relevés les plus récents d'abord). */
    @GetMapping("/derniers")
    public List<ReleveResponse> derniers(
            @RequestParam("stationId") @NotBlank String stationId,
            @RequestParam(value = "limite", defaultValue = "10") @Min(1) @Max(100) int limite) {
        return releveService.derniers(stationId, limite);
    }

    /** GET /api/releves/stats?stationId=&date=&type= : 200, 400, 404 (aucun relevé ce jour-là). */
    @GetMapping("/stats")
    public StatJournaliereResponse statistiques(
            @RequestParam("stationId") @NotBlank String stationId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "type", defaultValue = "TEMPERATURE") TypeMesure type) {
        LocalDate jour = date != null ? date : LocalDate.now(ZoneOffset.UTC);
        return releveService.statistiques(stationId, jour, type);
    }

    /** GET /api/releves/{id} : 200, 404. */
    @GetMapping("/{id}")
    public ReleveResponse parId(@PathVariable("id") String id) {
        return releveService.trouverParId(id);
    }

    /** DELETE /api/releves/{id} : 204, 404 (rôle ADMIN). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable("id") String id) {
        releveService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
