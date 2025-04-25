package com.flags.mappers;

import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.models.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDto toCountryDto(Country entity);

    CountryDetailDto toCountryDetailDto(Country entity);

    Country toCountry(CountryDetailDto dto);
}