package com.flags;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flags.models.Country;
import com.flags.repositories.CountryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class CountryFlags {

    public static void main(String[] args) {
        SpringApplication.run(CountryFlags.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(CountryRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                RestTemplate restTemplate = new RestTemplate();
                String url = "https://restcountries.com/v3.1/all";

                ResponseEntity<RestCountry[]> response = restTemplate.getForEntity(url, RestCountry[].class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    for (RestCountry rc : response.getBody()) {
                        if (rc == null || rc.name == null || rc.name.common == null || rc.flags == null || rc.flags.svg == null)
                            continue;
                        String name = rc.name.common;
                        String flag = rc.flags.svg;
                        Integer population = rc.population;
                        String capital = (rc.capital != null && !rc.capital.isEmpty()) ? rc.capital.get(0) : null;
                        repo.save(new Country(name, flag, population, capital));
                    }
                }
            }
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class RestCountry {
        public Name name;
        public Flags flags;
        public Integer population;
        public List<String> capital;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Name {
            public String common;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Flags {
            @JsonProperty("svg")
            public String svg;
        }
    }
}