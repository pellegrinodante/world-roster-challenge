package com.dante.worldroster.controller;

import com.dante.worldroster.model.PlayerCatalog;
import com.dante.worldroster.repository.PlayerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    private final PlayerRepository repository;

    public CatalogController(PlayerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/players")
    public PlayerCatalog players() {
        return repository.getCatalog();
    }
}
