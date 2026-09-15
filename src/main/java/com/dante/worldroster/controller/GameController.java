package com.dante.worldroster.controller;

import com.dante.worldroster.model.DraftOption;
import com.dante.worldroster.model.FantasyTeam;
import com.dante.worldroster.model.Position;
import com.dante.worldroster.model.TournamentSimulation;
import com.dante.worldroster.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public Map<String, UUID> start() {
        return Map.of("gameId", gameService.startGame());
    }

    @GetMapping("/{gameId}/draft/{position}")
    public DraftOption draft(@PathVariable UUID gameId, @PathVariable Position position) {
        return gameService.getDraft(gameId, position);
    }

    @PostMapping("/{gameId}/players/{playerId}")
    public FantasyTeam select(@PathVariable UUID gameId, @PathVariable Long playerId) {
        return gameService.select(gameId, playerId);
    }

    @PostMapping("/{gameId}/simulate")
    public TournamentSimulation simulate(
            @PathVariable UUID gameId,
            @RequestParam(required = false) Long seed
    ) {
        long effectiveSeed = seed != null ? seed : new java.security.SecureRandom().nextLong();
        return gameService.simulate(gameId, effectiveSeed);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handle(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
}
