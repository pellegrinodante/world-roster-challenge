package com.dante.worldroster.service;

import com.dante.worldroster.config.GameRules;
import com.dante.worldroster.model.GroupStageResult;
import com.dante.worldroster.model.GroupStanding;
import com.dante.worldroster.model.TournamentTeam;
import com.dante.worldroster.random.SeededRandom;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GroupStageService {
    public static final String USER_TEAM = "Tu equipo";
    private final GameRules rules;

    public GroupStageService(GameRules rules) {
        this.rules = rules;
    }

    public GroupStageResult simulate(String groupName, List<TournamentTeam> teams, SeededRandom random) {
        if (teams.size() != rules.groupSize()) {
            throw new IllegalArgumentException("El grupo debe tener " + rules.groupSize() + " equipos");
        }
        List<String> history = new ArrayList<>();
        for (int roundRobin = 1; roundRobin <= rules.groupRoundRobins(); roundRobin++) {
            for (int first = 0; first < teams.size(); first++) {
                for (int second = first + 1; second < teams.size(); second++) {
                    TournamentTeam home = roundRobin % 2 == 1 ? teams.get(first) : teams.get(second);
                    TournamentTeam away = roundRobin % 2 == 1 ? teams.get(second) : teams.get(first);
                    play(home, away, history, roundRobin, random);
                }
            }
        }

        teams.sort(Comparator.comparingInt(TournamentTeam::getWins).reversed()
                .thenComparing(Comparator.comparingInt(TournamentTeam::getPerformanceDifference).reversed())
                .thenComparing(Comparator.comparingDouble(TournamentTeam::getRating).reversed())
                .thenComparing(TournamentTeam::getName));

        int userPosition = 0;
        int userWins = 0;
        int userLosses = 0;
        List<GroupStanding> standings = new ArrayList<>();
        for (int index = 0; index < teams.size(); index++) {
            TournamentTeam team = teams.get(index);
            standings.add(new GroupStanding(index + 1, team.getName(), team.getWins(),
                    team.getLosses(), team.getPerformanceDifference()));
            if (team.getName().equals(USER_TEAM)) {
                userPosition = index + 1;
                userWins = team.getWins();
                userLosses = team.getLosses();
            }
        }
        return new GroupStageResult(groupName, userPosition, userWins, userLosses,
                userPosition > 0 && userPosition <= rules.groupQualifiers(), standings, history);
    }

    private void play(TournamentTeam first, TournamentTeam second, List<String> history,
                      int roundRobin, SeededRandom random) {
        double firstPerformance = first.getRating() + random.nextDouble(-12, 12);
        double secondPerformance = second.getRating() + random.nextDouble(-12, 12);
        TournamentTeam winner = firstPerformance >= secondPerformance ? first : second;
        TournamentTeam loser = winner == first ? second : first;
        int difference = Math.max(1, (int) Math.round(Math.abs(firstPerformance - secondPerformance)));
        winner.registerWin(difference);
        loser.registerLoss(difference);
        history.add("Vuelta " + roundRobin + ": " + winner.getName() + " derrotó a " + loser.getName());
    }
}
