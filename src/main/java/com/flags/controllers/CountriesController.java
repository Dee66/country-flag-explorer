package com.flags.controllers;

import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.services.CountriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Countries", description = "Operations related to countries")
@Controller
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountriesController {

    private final CountriesService service;

    @Operation(
            summary = "Get all countries",
            description = "Returns a list of all countries with their basic information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of countries retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CountryDto.class)))
    })
    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<CountryDto> getAllCountries() {
        return service.getAllCountries();
    }

    @Operation(
            summary = "Get details for a country by name",
            description = "Returns detailed information about the specified country"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Country details retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CountryDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    @GetMapping(value = "/{name}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CountryDetailDto> getCountryByName(
            @Parameter(description = "Name of the country", required = true) @PathVariable String name) {
        CountryDetailDto country = service.getCountryByName(name);
        if (country == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(country);
    }

    @Operation(
            summary = "Create a new country entry",
            description = "Adds a new country to the database"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Country created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CountryDetailDto.class)))
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CountryDetailDto> createCountry(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Country details", required = true,
                    content = @Content(schema = @Schema(implementation = CountryDetailDto.class))
            )
            @RequestBody CountryDetailDto newCountry) {
        CountryDetailDto created = service.createCountry(newCountry);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Update a country's information by name",
            description = "Updates an existing country with new values"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Country updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CountryDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    @PutMapping(value = "/{name}", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CountryDetailDto> updateCountry(
            @Parameter(description = "Name of the country", required = true) @PathVariable String name,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated country details", required = true,
                    content = @Content(schema = @Schema(implementation = CountryDetailDto.class))
            )
            @RequestBody CountryDetailDto updatedCountry) {
        CountryDetailDto updated = service.updateCountry(name, updatedCountry);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete a country by name",
            description = "Removes the specified country"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Country successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    @DeleteMapping("/{name}")
    @ResponseBody
    public ResponseEntity<Void> deleteCountry(
            @Parameter(description = "Name of the country", required = true) @PathVariable String name) {
        boolean deleted = service.deleteCountry(name);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // No Swagger annotations for HTML view endpoints (for browsers only)
    @GetMapping(path = "", produces = "text/html")
    public String listCountriesView(@RequestParam(name = "search", required = false) String search, Model model) {
        List<CountryDto> countries;
        if (search != null && !search.trim().isEmpty()) {
            countries = service.searchCountriesByName(search);
        } else {
            countries = service.getAllCountries();
        }
        model.addAttribute("countries", countries);
        model.addAttribute("param", Map.of("search", search != null ? search : ""));
        return "countries";
    }

    @GetMapping(path = "/{name}", produces = "text/html")
    public String getCountryDetailView(@PathVariable String name, Model model) {
        model.addAttribute("country", service.getCountryByName(name));
        return "country-detail";
    }
}