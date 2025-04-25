package com.flags.services;

import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.mappers.CountryMapper;
import com.flags.models.Country;
import com.flags.repositories.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CountriesServiceTest {

    private CountryRepository countryRepository;
    private CountryMapper countryMapper;
    private CountriesService service;

    @BeforeEach
    void setUp() {
        countryRepository = mock(CountryRepository.class);
        countryMapper = mock(CountryMapper.class);
        service = new CountriesService(countryRepository, countryMapper);
    }

    @Test
    void getAllCountries_returnsList() {
        Country country = new Country("Testland", "test.png", 123, "Testville");
        when(countryRepository.findAll()).thenReturn(List.of(country));
        CountryDto dto = new CountryDto();
        when(countryMapper.toCountryDto(country)).thenReturn(dto);

        List<CountryDto> result = service.getAllCountries();

        assertThat(result).containsExactly(dto);
        verify(countryRepository).findAll();
    }

    @Test
    void getCountryByName_returnsCountryDetail() {
        Country country = new Country("Testland", "test.png", 123, "Testville");
        when(countryRepository.findById("Testland")).thenReturn(Optional.of(country));
        CountryDetailDto dto = new CountryDetailDto();
        when(countryMapper.toCountryDetailDto(country)).thenReturn(dto);

        CountryDetailDto result = service.getCountryByName("Testland");

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getCountryByName_returnsNullIfNotFound() {
        when(countryRepository.findById("Nowhere")).thenReturn(Optional.empty());

        CountryDetailDto result = service.getCountryByName("Nowhere");

        assertThat(result).isNull();
    }

    @Test
    void createCountry_savesAndReturnsCreatedCountry() {
        CountryDetailDto dto = new CountryDetailDto();
        Country country = new Country("Testland", "flag.png", 1000, "Capital");
        when(countryMapper.toCountry(dto)).thenReturn(country);
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toCountryDetailDto(country)).thenReturn(dto);

        CountryDetailDto result = service.createCountry(dto);

        assertThat(result).isEqualTo(dto);
        verify(countryRepository).save(country);
    }

    @Test
    void updateCountry_whenExists_returnsUpdatedCountry() {
        String name = "Testland";
        CountryDetailDto updateDto = new CountryDetailDto();
        Country existing = new Country("Testland", "flag.png", 1000, "Capital");
        Country updated = new Country("Testland", "newflag.png", 1200, "NewCapital");
        when(countryRepository.findById(name)).thenReturn(Optional.of(existing));
        when(countryMapper.toCountry(updateDto)).thenReturn(updated);
        when(countryRepository.save(any(Country.class))).thenReturn(updated);
        when(countryMapper.toCountryDetailDto(updated)).thenReturn(updateDto);

        CountryDetailDto result = service.updateCountry(name, updateDto);

        assertThat(result).isEqualTo(updateDto);
        verify(countryRepository).save(any(Country.class));
    }

    @Test
    void updateCountry_whenNotExists_returnsNull() {
        String name = "Unknown";
        CountryDetailDto updateDto = new CountryDetailDto();
        when(countryRepository.findById(name)).thenReturn(Optional.empty());

        CountryDetailDto result = service.updateCountry(name, updateDto);

        assertThat(result).isNull();
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void deleteCountry_whenExists_returnsTrue() {
        String name = "Testland";
        when(countryRepository.existsById(name)).thenReturn(true);

        boolean result = service.deleteCountry(name);

        assertThat(result).isTrue();
        verify(countryRepository).deleteById(name);
    }

    @Test
    void deleteCountry_whenNotExists_returnsFalse() {
        String name = "Nowhere";
        when(countryRepository.existsById(name)).thenReturn(false);

        boolean result = service.deleteCountry(name);

        assertThat(result).isFalse();
        verify(countryRepository, never()).deleteById(anyString());
    }
}