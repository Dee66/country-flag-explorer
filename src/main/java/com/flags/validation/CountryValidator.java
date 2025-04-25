package com.flags.validation;

import com.flags.dto.CountryDetailDto;

public interface CountryValidator {
    void validateForCreate(CountryDetailDto dto);

    void validateForUpdate(String name, CountryDetailDto dto);
}