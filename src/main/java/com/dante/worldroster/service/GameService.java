package com.dante.worldroster.service;

import com.dante.worldroster.config.GameRules;
import com.dante.worldroster.model.*;
import com.dante.worldroster.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<UUID, GameSession> sessions = new ConcurrentHashMap<>();
    private final DraftService draftService;
    private final PlayerRepository playerRepository;
    private final SimulationService simulationService;
    private final GameRules gameRules;

    public GameService(DraftService draftService, PlayerRepository playerRepository,
                       SimulationService simulationService, GameRules gameRules) {
        this.draftService = draftService;
        this.playerRepository = playerRepository;
        this.simulationService = simulationService;
        this.gameRules = gameRules;
    }

    public UUID startGame() {
        UUID id = UUID.randomUUID();
        sessions.put(id, new GameSession());
        return id;
    }

    public DraftOption getDraft(UUID gameId, Position position) {
        GameSession session = requireSession(gameId);
        if (session.getTeam().getRoster().containsKey(position)) {
            throw new IllegalStateException("La posición ya está ocupada");
        }
        DraftOption option = draftService.generateOptions(position, gameRules.draftOptions());
        session.registerOffer(position, option.players());
        return option;
    }

    public FantasyTeam select(UUID gameId, Long playerId) {
        GameSession session = requireSession(gameId);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Jugador inexistente"));
        if (!session.wasOffered(player)) {
            throw new IllegalStateException("El jugador no pertenece a la tirada actual");
        }
        session.getTeam().addPlayer(player);
        return session.getTeam();
    }

    public TournamentSimulation simulate(UUID gameId, long seed) {
        return simulationService.simulate(requireSession(gameId).getTeam(), seed);
    }

    private GameSession requireSession(UUID gameId) {
        GameSession session = sessions.get(gameId);
        if (session == null) throw new IllegalArgumentException("Partida inexistente o vencida");
        return session;
    }
}
