package com.dante.worldroster.repository;

import com.dante.worldroster.model.Player;
import com.dante.worldroster.model.HistoricalTeam;
import com.dante.worldroster.model.PlayerCatalog;
import com.dante.worldroster.model.Position;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PlayerRepository {
    private final PlayerCatalog catalog;
    private final List<Player> players;

    public PlayerRepository(
            ObjectMapper objectMapper,
            @Value("${game.players.resource:classpath:data/players.json}") Resource resource
    ) {
        this.catalog = loadCatalog(objectMapper, resource);
        this.players = List.copyOf(catalog.players());
        validateCatalog(catalog);
    }

    public Optional<Player> findById(Long id) {
        return players.stream().filter(player -> player.id().equals(id)).findFirst();
    }

    public List<Player> findByPosition(Position position) {
        return players.stream().filter(player -> player.position() == position).toList();
    }

    public List<Player> findAll() {
        return players;
    }

    public List<HistoricalTeam> findHistoricalTeams() {
        return List.copyOf(catalog.historicalTeams());
    }

    public PlayerCatalog getCatalog() {
        return catalog;
    }

    private PlayerCatalog loadCatalog(ObjectMapper mapper, Resource resource) {
        try (var input = resource.getInputStream()) {
            return mapper.readValue(input, PlayerCatalog.class);
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo cargar el catálogo de jugadores", exception);
        }
    }

    private void validateCatalog(PlayerCatalog catalog) {
        if (catalog.schemaVersion() != 2) {
            throw new IllegalStateException("Versión de catálogo no soportada: " + catalog.schemaVersion());
        }
        if (catalog.players() == null || catalog.players().isEmpty()) {
            throw new IllegalStateException("El catálogo de jugadores está vacío");
        }

        Set<Long> ids = new HashSet<>();
        for (Player player : catalog.players()) {
            if (player.id() == null || !ids.add(player.id())) {
                throw new IllegalStateException("Hay identificadores nulos o repetidos en el catálogo");
            }
            if (player.nickname() == null || player.nickname().isBlank()
                    || player.team() == null || player.team().isBlank()
                    || player.position() == null || player.region() == null) {
                throw new IllegalStateException("Hay un jugador con datos obligatorios incompletos");
            }
            validateScore(player.mechanics(), player.nickname(), "mechanics");
            validateScore(player.consistency(), player.nickname(), "consistency");
            validateScore(player.experience(), player.nickname(), "experience");
            validateScore(player.teamwork(), player.nickname(), "teamwork");
        }

        if (catalog.historicalTeams() == null || catalog.historicalTeams().size() < 15) {
            throw new IllegalStateException("Se requieren al menos 15 equipos históricos");
        }

        for (Position position : Position.values()) {
            long count = catalog.players().stream().filter(player -> player.position() == position).count();
            if (count < 3) {
                throw new IllegalStateException("Se requieren al menos 3 jugadores para " + position);
            }
        }
    }

    private void validateScore(int value, String nickname, String field) {
        if (value < 0 || value > 100) {
            throw new IllegalStateException("Valor inválido en " + nickname + ": " + field);
        }
    }
}
