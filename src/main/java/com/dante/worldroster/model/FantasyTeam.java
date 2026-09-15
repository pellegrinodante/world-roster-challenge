package com.dante.worldroster.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class FantasyTeam {
    private final Map<Position, Player> roster = new EnumMap<>(Position.class);

    public void addPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("El jugador no puede ser nulo");
        }
        if (roster.containsKey(player.position())) {
            throw new IllegalStateException("La posición " + player.position() + " ya está ocupada");
        }
        boolean repeated = roster.values().stream()
                .anyMatch(selected -> selected.nickname().equalsIgnoreCase(player.nickname()));
        if (repeated) {
            throw new IllegalStateException("El jugador ya fue seleccionado");
        }
        roster.put(player.position(), player);
    }

    public Collection<Player> getPlayers() {
        return roster.values();
    }

    public Map<Position, Player> getRoster() {
        return Map.copyOf(roster);
    }

    public boolean isComplete() {
        return roster.size() == Position.values().length;
    }
}
