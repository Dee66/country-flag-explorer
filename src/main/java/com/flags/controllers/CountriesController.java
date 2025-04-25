package com.flags.controllers;

import com.flags.dto.CountryDetailDto;
import com.flags.dto.CountryDto;
import com.flags.services.CountriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountriesController {

    private final CountriesService service;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<CountryDto> getAllCountries() {
        return service.getAllCountries();
    }

    @GetMapping(value = "/{name}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CountryDetailDto> getCountryByName(@PathVariable String name) {
        CountryDetailDto country = service.getCountryByName(name);
        if (country == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(country);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CountryDetailDto> createCountry(@RequestBody CountryDetailDto newCountry) {
        CountryDetailDto created = service.createCountry(newCountry);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{name}", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CountryDetailDto> updateCountry(
            @PathVariable String name,
            @RequestBody CountryDetailDto updatedCountry) {
        CountryDetailDto updated = service.updateCountry(name, updatedCountry);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{name}")
    @ResponseBody
    public ResponseEntity<Void> deleteCountry(@PathVariable String name) {
        boolean deleted = service.deleteCountry(name);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

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