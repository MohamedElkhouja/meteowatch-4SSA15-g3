package com.meteowatch.readings.controller;

import com.meteowatch.readings.dto.ReleveResponse;
import com.meteowatch.readings.exception.ReleveDejaExistantException;
import com.meteowatch.readings.exception.StationIntrouvableException;
import com.meteowatch.readings.model.StatutReleve;
import com.meteowatch.readings.service.ReleveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReleveController.class)
@WithMockUser(roles = "ADMIN")
class ReleveControllerTest {

    private static final String JSON_VALIDE = """
            {"stationId":"st-1","horodatage":"2020-01-01T10:00:00Z","temperature":22.4,
             "humidite":61,"pression":1014,"vitesseVent":18,"precipitation":0.2}
            """;

    private static final String JSON_HUMIDITE_INVALIDE = """
            {"stationId":"st-1","horodatage":"2020-01-01T10:00:00Z","temperature":22.4,
             "humidite":150,"pression":1014,"vitesseVent":18,"precipitation":0.2}
            """;

    @Autowired MockMvc mockMvc;
    @MockBean ReleveService releveService;

    private ReleveResponse reponse(StatutReleve statut) {
        return new ReleveResponse("id-1", "st-1", Instant.parse("2020-01-01T10:00:00Z"),
                22.4, 61.0, 1014.0, 18.0, 0.2, "Tunis", statut);
    }

    @Test
    void creer_releveValide_retourne201AvecLocation() throws Exception {
        when(releveService.enregistrer(any())).thenReturn(reponse(StatutReleve.VALIDE));

        mockMvc.perform(post("/api/releves").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDE))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/releves/id-1")))
                .andExpect(jsonPath("$.statut").value("VALIDE"));
    }

    @Test
    void creer_stationIndisponible_retourne202AValider() throws Exception {
        when(releveService.enregistrer(any())).thenReturn(reponse(StatutReleve.A_VALIDER));

        mockMvc.perform(post("/api/releves").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDE))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.statut").value("A_VALIDER"));
    }

    @Test
    void creer_humiditeInvalide_retourne400() throws Exception {
        mockMvc.perform(post("/api/releves").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_HUMIDITE_INVALIDE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creer_stationInconnue_retourne404() throws Exception {
        when(releveService.enregistrer(any())).thenThrow(new StationIntrouvableException("st-1"));

        mockMvc.perform(post("/api/releves").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void creer_doublon_retourne409() throws Exception {
        when(releveService.enregistrer(any()))
                .thenThrow(new ReleveDejaExistantException("st-1", "2020-01-01T10:00:00Z"));

        mockMvc.perform(post("/api/releves").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDE))
                .andExpect(status().isConflict());
    }
}
