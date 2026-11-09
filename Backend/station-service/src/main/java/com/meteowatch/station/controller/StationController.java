package com.meteowatch.station.controller;

import com.meteowatch.station.dto.*;
import com.meteowatch.station.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService service;

    public StationController(StationService service) {
        this.service = service;
    }

    /** GET /api/stations et GET /api/stations?ville=Tunis -> 200 */
    @GetMapping
    public List<StationResponse> lister(@RequestParam(required = false) String ville) {
        return service.lister(ville);
    }

    /** GET /api/stations/{id} -> 200 / 404 (appele par Releves et Previsions via OpenFeign) */
    @GetMapping("/{id}")
    public StationResponse trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    /** POST /api/stations -> 201 / 400 */
    @PostMapping
    public ResponseEntity<StationResponse> creer(@Valid @RequestBody StationRequest req) {
        StationResponse cree = service.creer(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(cree.id()).toUri();
        return ResponseEntity.created(location).body(cree);
    }

    /** PUT /api/stations/{id} -> 200 / 400 / 404 */
    @PutMapping("/{id}")
    public StationResponse modifier(@PathVariable Long id, @Valid @RequestBody StationRequest req) {
        return service.modifier(id, req);
    }

    /** DELETE /api/stations/{id} -> 204 / 404 + evenement StationDeactivated */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }

    /** POST /api/stations/{id}/capteurs -> 201 / 400 / 404 */
    @PostMapping("/{id}/capteurs")
    @ResponseStatus(HttpStatus.CREATED)
    public CapteurResponse ajouterCapteur(@PathVariable Long id, @Valid @RequestBody CapteurRequest req) {
        return service.ajouterCapteur(id, req);
    }
}
