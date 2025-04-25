package com.flags.validation;

import com.flags.dto.CountryDetailDto;
import com.flags.exceptions.InvalidCountryDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleCountryValidatorTest {

    private final SimpleCountryValidator validator = new SimpleCountryValidator();

    @Test
    void validCountryDoesNotThrow() {
        CountryDetailDto dto = new CountryDetailDto();
        dto.setName("Italy");
        dto.setFlag("🇮🇹");
        dto.setPopulation(60000000);
        dto.setCapital("Rome");
        assertDoesNotThrow(() -> validator.validateForCreate(dto));
    }

    @Test
    void invalidCountryThrows() {
        CountryDetailDto dto = new CountryDetailDto();
        dto.setFlag("");
        dto.setPopulation(-1);
        dto.setCapital("");
        assertThrows(InvalidCountryDataException.class, () -> validator.validateForCreate(dto));
    }

    @Test
    void updateNullNameThrows() {
        CountryDetailDto dto = new CountryDetailDto();
        dto.setName("Spain");
        dto.setFlag("🇪🇸");
        dto.setPopulation(47000000);
        dto.setCapital("Madrid");
        assertThrows(InvalidCountryDataException.class, () -> validator.validateForUpdate(null, dto));
    }
}