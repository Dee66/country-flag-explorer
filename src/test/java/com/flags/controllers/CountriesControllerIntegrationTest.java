package com.flags.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flags.dto.CountryDetailDto;
import com.flags.models.Country;
import com.flags.repositories.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CountriesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        countryRepository.deleteAll();
        countryRepository.save(new Country("France", "🇫🇷", 67000000, "Paris"));
    }

    // Test: GET /countries returns a list of countries
    @Test
    @DisplayName("GET /countries returns all countries")
    void getAllCountries() throws Exception {
        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    // Test: GET /countries/{name} returns a country's details
    @Test
    @DisplayName("GET /countries/{name} returns country details")
    void getCountryByName() throws Exception {
        // Assumes "France" exists in test data setup
        mockMvc.perform(get("/countries/France"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.name").value("France"));
    }

    @Test
    @DisplayName("POST /countries creates new country")
    void createCountry() throws Exception {
        CountryDetailDto dto = new CountryDetailDto();
        dto.setName("Japan");
        dto.setFlag("🇯🇵");
        dto.setPopulation(125000000);
        dto.setCapital("Tokyo");
        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Japan")));
    }

    @Test
    @DisplayName("PUT /countries/{name} updates country")
    void updateCountry() throws Exception {
        CountryDetailDto dto = new CountryDetailDto();
        dto.setName("France");
        dto.setFlag("🇫🇷");
        dto.setPopulation(70000000);
        dto.setCapital("Paris");

        String body = objectMapper.writeValueAsString(dto);
        mockMvc.perform(put("/countries/France")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.population", is(70000000)));
    }

    // Test: GET /countries/{name} for a non-existent country returns 404
    @Test
    @DisplayName("GET /countries/{name} returns 404 for unknown country")
    void getCountryByNameNotFound() throws Exception {
        mockMvc.perform(get("/countries/NonExistentCountry"))
                .andExpect(status().isNotFound());
    }

    // Test: DELETE /countries/{name} removes a country
    @Test
    @DisplayName("DELETE /countries/{name} deletes the country")
    void deleteCountry() throws Exception {
        // First, ensure the country exists (add as setup if needed)
        mockMvc.perform(delete("/countries/France"))
                .andExpect(status().isNoContent());
    }

    // Test: DELETE /countries/{name} for non-existent country
    @Test
    @DisplayName("DELETE /countries/{name} returns 404 for unknown country")
    void deleteUnknownCountry() throws Exception {
        mockMvc.perform(delete("/countries/NonExistentCountry"))
                .andExpect(status().isNotFound());
    }

    // Test: POST /countries with invalid payload returns 400
    @Test
    @DisplayName("POST /countries with missing fields returns 400")
    void createCountryValidationError() throws Exception {
        String body = "{\"flag\":\"\",\"population\":null}";
        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void countriesPageRendersWithCountriesList() throws Exception {
        mockMvc.perform(
                        get("/countries")
                                .accept("text/html"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries"))
                .andExpect(model().attributeExists("countries"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Country Flags")));
    }


}