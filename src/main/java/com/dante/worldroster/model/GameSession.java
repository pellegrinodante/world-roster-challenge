package com.dante.worldroster.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GameSession {
    private final FantasyTeam team = new FantasyTeam();
    private final Map<Position, List<Long>> offeredPlayerIds = new EnumMap<>(Position.class);

    public FantasyTeam getTeam() {
        return team;
    }

    public void registerOffer(Position position, List<Player> players) {
        offeredPlayerIds.put(position, players.stream().map(Player::id).toList());
    }

    public boolean wasOffered(Player player) {
        return offeredPlayerIds.getOrDefault(player.position(), List.of()).contains(player.id());
    }
}
