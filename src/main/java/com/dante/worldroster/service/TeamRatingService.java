package com.dante.worldroster.service;

import com.dante.worldroster.model.FantasyTeam;
import com.dante.worldroster.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeamRatingService {
    public double calculate(FantasyTeam team) {
        if (!team.isComplete()) {
            throw new IllegalStateException("El equipo todavía no está completo");
        }
        List<Player> players = team.getPlayers().stream().toList();
        double base = players.stream().mapToDouble(Player::calculateRating).average().orElse(0);
        return Math.min(100, base + regionBonus(players) + organizationBonus(players));
    }

    private double regionBonus(List<Player> players) {
        Map<Object, Long> counts = players.stream()
                .collect(Collectors.groupingBy(Player::region, Collectors.counting()));
        long largest = counts.values().stream().mapToLong(Long::longValue).max().orElse(1);
        return switch ((int) largest) {
            case 5 -> 5.0; case 4 -> 3.5; case 3 -> 2.0; case 2 -> 1.0; default -> 0.0;
        };
    }

    private double organizationBonus(List<Player> players) {
        Map<String, Long> counts = players.stream()
                .collect(Collectors.groupingBy(Player::team, Collectors.counting()));
        long largest = counts.values().stream().mapToLong(Long::longValue).max().orElse(1);
        return switch ((int) largest) {
            case 5 -> 5.0; case 4 -> 4.0; case 3 -> 2.5; case 2 -> 1.5; default -> 0.0;
        };
    }
}
