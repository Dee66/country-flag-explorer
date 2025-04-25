package com.flags.validation;

import com.flags.dto.CountryDetailDto;
import com.flags.exceptions.InvalidCountryDataException;
import org.springframework.stereotype.Component;

@Component
public class SimpleCountryValidator implements CountryValidator {
    @Override
    public void validateForCreate(CountryDetailDto dto) {
        if (dto == null
                || dto.getName() == null || dto.getName().trim().isEmpty()
                || dto.getFlag() == null || dto.getFlag().trim().isEmpty()
                || dto.getPopulation() == null || dto.getPopulation() <= 0
                || dto.getCapital() == null || dto.getCapital().trim().isEmpty()) {
            throw new InvalidCountryDataException("Country data is invalid or incomplete.");
        }
    }

    @Override
    public void validateForUpdate(String name, CountryDetailDto dto) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidCountryDataException("Country name must be provided for update.");
        }
        validateForCreate(dto);
    }
}