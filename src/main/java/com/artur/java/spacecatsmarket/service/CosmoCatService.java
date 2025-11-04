package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.aop.FeatureToggle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CosmoCatService {

    @FeatureToggle("cosmoCats")
    public List<String> getCosmoCats() {
        log.info("Fetching all cosmo cats");
        return List.of(
                "Nebula the Navigator",
                "Stardust the Explorer",
                "Galaxy the Guardian",
                "Orbit the Observer"
        );
    }

    @FeatureToggle("kittyProducts")
    public List<String> getKittyProducts() {
        log.info("Fetching kitty products");
        return List.of(
                "Space Catnip",
                "Cosmic Scratching Post",
                "Galactic Laser Toy"
        );
    }
}