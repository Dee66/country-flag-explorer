package com.flags.services;

import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.exceptions.CountryAlreadyExistsException;
import com.flags.exceptions.CountryNotFoundException;
import com.flags.exceptions.InvalidCountryDataException;
import com.flags.mappers.CountryMapper;
import com.flags.models.Country;
import com.flags.repositories.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CountriesService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toCountryDto)
                .collect(Collectors.toList());
    }

    public CountryDetailDto getCountryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCountryDataException("Country name must be provided.");
        }
        return countryRepository.findById(name)
                .map(countryMapper::toCountryDetailDto)
                .orElseThrow(() -> new CountryNotFoundException(name));
    }


    // For illustration
    public CountryDetailDto createCountry(CountryDetailDto newCountry) {
        if (newCountry == null || newCountry.getName() == null || newCountry.getName().trim().isEmpty()) {
            throw new InvalidCountryDataException("Country data is invalid.");
        }
        if (countryRepository.existsById(newCountry.getName())) {
            throw new CountryAlreadyExistsException(newCountry.getName());
        }
        Country country = countryMapper.toCountry(newCountry);
        Country saved = countryRepository.save(country);
        return countryMapper.toCountryDetailDto(saved);
    }

    // For illustration
    public CountryDetailDto updateCountry(String name, CountryDetailDto updatedCountry) {
        if (updatedCountry == null) {
            throw new InvalidCountryDataException("Country data is invalid.");
        }
        if (!countryRepository.existsById(name)) {
            throw new CountryNotFoundException(name);
        }
        Country country = countryMapper.toCountry(updatedCountry);
        country.setName(name);
        Country saved = countryRepository.save(country);
        return countryMapper.toCountryDetailDto(saved);
    }

    // For illustration
    public boolean deleteCountry(String name) {
        if (!countryRepository.existsById(name)) {
            throw new CountryNotFoundException(name);
        }
        countryRepository.deleteById(name);
        return true;
    }

    public List<CountryDto> searchCountriesByName(String search) {
        if (search == null || search.trim().isEmpty()) {
            return getAllCountries();
        }
        return countryRepository.findByNameContainingIgnoreCase(search).stream()
                .map(countryMapper::toCountryDto)
                .collect(Collectors.toList());
    }
}