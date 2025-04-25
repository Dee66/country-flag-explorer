package com.flags.controllers;

import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.services.CountriesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountriesController.class)
@Import(CountriesControllerViewTest.CountriesServiceTestConfig.class)
class CountriesControllerViewTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountriesService countriesService;

    @TestConfiguration
    static class CountriesServiceTestConfig {
        @Bean
        public CountriesService countriesService() {
            return Mockito.mock(CountriesService.class);
        }
    }

    @Test
    void listCountriesView_returnsCountriesView() throws Exception {
        CountryDto country1 = new CountryDto();
        country1.setName("France");
        CountryDto country2 = new CountryDto();
        country2.setName("Spain");
        when(countriesService.getAllCountries()).thenReturn(List.of(country1, country2));

        mockMvc.perform(get("/countries").accept("text/html"))
                .andExpect(status().isOk())
                .andExpect(view().name("countries"))
                .andExpect(model().attributeExists("countries"));
    }

    @Test
    void getCountryDetailView_returnsCountryDetailView() throws Exception {
        CountryDetailDto detail = new CountryDetailDto();
        detail.setName("France");
        detail.setCapital("Paris");
        detail.setFlag("fr.png");
        when(countriesService.getCountryByName("France")).thenReturn(detail);

        mockMvc.perform(get("/countries/France").accept("text/html"))
                .andExpect(status().isOk())
                .andExpect(view().name("country-detail"))
                .andExpect(model().attributeExists("country"));
    }

    @Test
    void getCountryDetailView_notFound_returns404() throws Exception {
        when(countriesService.getCountryByName(anyString())).thenReturn(null);

        mockMvc.perform(get("/countries/Noland").accept("text/html"))
                .andExpect(status().isNotFound());
    }
}