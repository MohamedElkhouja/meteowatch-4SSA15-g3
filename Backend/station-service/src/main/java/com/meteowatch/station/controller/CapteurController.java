package com.meteowatch.station.controller;

import com.meteowatch.station.dto.CapteurResponse;
import com.meteowatch.station.dto.EtatCapteurRequest;
import com.meteowatch.station.service.StationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/capteurs")
public class CapteurController {

    private final StationService service;

    public CapteurController(StationService service) {
        this.service = service;
    }

    /** PATCH /api/capteurs/{id}/etat -> 200 / 400 / 404 */
    @PatchMapping("/{id}/etat")
    public CapteurResponse changerEtat(@PathVariable Long id, @Valid @RequestBody EtatCapteurRequest req) {
        return service.changerEtatCapteur(id, req);
    }
}
