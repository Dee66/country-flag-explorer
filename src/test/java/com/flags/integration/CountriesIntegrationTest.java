package com.flags.integration;

import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.models.Country;
import com.flags.repositories.CountryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CountriesIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CountryRepository countryRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/countries";
        countryRepository.deleteAll();

        Country france = new Country("France", "fr.png", 67000000, "Paris");
        Country spain = new Country("Spain", "es.png", 47000000, "Madrid");
        countryRepository.save(france);
        countryRepository.save(spain);
    }

    @Test
    void getAllCountries_returnsAllCountries() {
        ResponseEntity<CountryDto[]> response = restTemplate.getForEntity(baseUrl, CountryDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(List.of(response.getBody()))
                .extracting(CountryDto::getName)
                .contains("France", "Spain");
    }

    @Test
    void getCountryByName_returnsDetails() {
        ResponseEntity<CountryDetailDto> response = restTemplate.getForEntity(baseUrl + "/France", CountryDetailDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getCapital()).isEqualTo("Paris");
    }

    @Test
    void getCountryByName_notFound_returns404() {
        ResponseEntity<CountryDetailDto> response = restTemplate.getForEntity(baseUrl + "/Noland", CountryDetailDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createCountry_addsAndReturnsCountry() {
        CountryDetailDto newCountry = new CountryDetailDto();
        newCountry.setName("Italy");
        newCountry.setFlag("it.png");
        newCountry.setPopulation(60000000);
        newCountry.setCapital("Rome");

        ResponseEntity<CountryDetailDto> response = restTemplate.postForEntity(baseUrl, newCountry, CountryDetailDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getName()).isEqualTo("Italy");

        // Verify it's now in the database
        assertThat(countryRepository.findById("Italy")).isPresent();
    }

    @Test
    void updateCountry_whenPresent_updatesAndReturns() {
        CountryDetailDto update = new CountryDetailDto();
        update.setName("France");
        update.setPopulation(68000000);
        update.setCapital("Paris");
        update.setFlag("fr2.png");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CountryDetailDto> entity = new HttpEntity<>(update, headers);
        ResponseEntity<CountryDetailDto> response =
                restTemplate.exchange(baseUrl + "/France", HttpMethod.PUT, entity, CountryDetailDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getFlag()).isEqualTo("fr2.png");
    }

    @Test
    void updateCountry_whenNotPresent_returns404() {
        CountryDetailDto update = new CountryDetailDto();
        update.setName("Nowhere");
        update.setPopulation(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CountryDetailDto> entity = new HttpEntity<>(update, headers);
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl + "/Nowhere", HttpMethod.PUT, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteCountry_whenPresent_removesAndReturnsNoContent() {
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl + "/Spain", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(countryRepository.findById("Spain")).isNotPresent();
    }

    @Test
    void deleteCountry_whenNotPresent_returns404() {
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl + "/Atlantis", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}