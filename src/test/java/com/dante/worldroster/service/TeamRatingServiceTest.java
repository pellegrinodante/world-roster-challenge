package com.dante.worldroster.service;

import com.dante.worldroster.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamRatingServiceTest {
    @Test
    void completeTeamProducesValidRating() {
        FantasyTeam team = new FantasyTeam();
        Position[] positions = Position.values();
        for (int i = 0; i < positions.length; i++) {
            team.addPlayer(new Player((long) i, "P" + i, "Team", 2023, positions[i], Region.LCK,
                    90, 90, 90, 90, "P" + i + " 2023", "Test", "TEST", "Test"));
        }
        double result = new TeamRatingService().calculate(team);
        assertTrue(result >= 90 && result <= 100);
    }
}
