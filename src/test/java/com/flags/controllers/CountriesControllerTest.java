package com.flags.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.services.CountriesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CountriesControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CountriesService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(CountriesService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CountriesController(service)).build();
    }

    @Test
    void getAllCountries_returnsJsonArray() throws Exception {
        CountryDto dto = new CountryDto();
        when(service.getAllCountries()).thenReturn(List.of(dto));

        mockMvc.perform(get("/countries").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCountryByName_found_returnsJson() throws Exception {
        CountryDetailDto dto = new CountryDetailDto();
        when(service.getCountryByName("France")).thenReturn(dto);

        mockMvc.perform(get("/countries/France").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCountryByName_notFound_returns404() throws Exception {
        when(service.getCountryByName("Nowhere")).thenReturn(null);

        mockMvc.perform(get("/countries/Nowhere").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCountry_returns201Created() throws Exception {
        CountryDetailDto inputDto = new CountryDetailDto();
        inputDto.setName("Testland");
        CountryDetailDto savedDto = new CountryDetailDto();
        savedDto.setName("Testland");
        when(service.createCountry(any(CountryDetailDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCountry_found_returnsUpdated() throws Exception {
        CountryDetailDto updateDto = new CountryDetailDto();
        updateDto.setName("UpdateLand");
        CountryDetailDto responseDto = new CountryDetailDto();
        responseDto.setName("UpdateLand");

        when(service.updateCountry(eq("UpdateLand"), any(CountryDetailDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/countries/UpdateLand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCountry_notFound_returns404() throws Exception {
        CountryDetailDto updateDto = new CountryDetailDto();
        updateDto.setName("Missing");

        when(service.updateCountry(eq("Missing"), any(CountryDetailDto.class))).thenReturn(null);

        mockMvc.perform(put("/countries/Missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCountry_found_returnsNoContent() throws Exception {
        when(service.deleteCountry("DeleteLand")).thenReturn(true);

        mockMvc.perform(delete("/countries/DeleteLand"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCountry_notFound_returns404() throws Exception {
        when(service.deleteCountry("MissingLand")).thenReturn(false);

        mockMvc.perform(delete("/countries/MissingLand"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listCountriesView_returnsHtml() throws Exception {
        when(service.getAllCountries()).thenReturn(List.of(new CountryDto()));

        mockMvc.perform(get("/countries").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("countries"));
    }

    @Test
    void getCountryDetailView_returnsHtml() throws Exception {
        when(service.getCountryByName("France")).thenReturn(new CountryDetailDto());

        mockMvc.perform(get("/countries/France").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("country-detail"));
    }
}