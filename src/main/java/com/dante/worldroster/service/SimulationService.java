package com.dante.worldroster.service;

import com.dante.worldroster.config.GameRules;
import com.dante.worldroster.model.*;
import com.dante.worldroster.random.SeededRandom;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimulationService {
    private static final List<String> GROUP_NAMES = List.of("Grupo A", "Grupo B", "Grupo C", "Grupo D");

    private final TeamRatingService ratingService;
    private final GroupStageService groupStageService;
    private final GameRules rules;
    private final com.dante.worldroster.repository.PlayerRepository playerRepository;

    public SimulationService(TeamRatingService ratingService, GroupStageService groupStageService,
                             GameRules rules, com.dante.worldroster.repository.PlayerRepository playerRepository) {
        this.ratingService = ratingService;
        this.groupStageService = groupStageService;
        this.rules = rules;
        this.playerRepository = playerRepository;
    }

    public TournamentSimulation simulate(FantasyTeam fantasyTeam, long seed) {
        double userRating = ratingService.calculate(fantasyTeam);
        SeededRandom random = new SeededRandom(seed);
        Map<String, Double> ratings = new HashMap<>();
        ratings.put(GroupStageService.USER_TEAM, userRating);

        List<TournamentTeam> entrants = createEntrants(userRating, random, ratings);
        random.shuffle(entrants);
        moveUserIntoRandomGroup(entrants, random);

        List<GroupStageResult> groups = new ArrayList<>();
        String userGroupName = "";
        int userPosition = 0;
        for (int groupIndex = 0; groupIndex < GROUP_NAMES.size(); groupIndex++) {
            int from = groupIndex * rules.groupSize();
            List<TournamentTeam> groupTeams = new ArrayList<>(entrants.subList(from, from + rules.groupSize()));
            GroupStageResult result = groupStageService.simulate(GROUP_NAMES.get(groupIndex), groupTeams, random);
            groups.add(result);
            if (result.userPosition() > 0) {
                userGroupName = result.groupName();
                userPosition = result.userPosition();
            }
        }

        boolean userQualified = userPosition > 0 && userPosition <= rules.groupQualifiers();
        List<KnockoutSeriesResult> bracket = new ArrayList<>();
        List<String> quarterWinners = simulateQuarterfinals(groups, ratings, random, bracket);
        List<String> semifinalWinners = new ArrayList<>();
        semifinalWinners.add(playSeries("Semifinal 1", quarterWinners.get(0), quarterWinners.get(1), ratings, random, bracket));
        semifinalWinners.add(playSeries("Semifinal 2", quarterWinners.get(2), quarterWinners.get(3), ratings, random, bracket));
        String champion = playSeries("Final", semifinalWinners.get(0), semifinalWinners.get(1), ratings, random, bracket);

        String finalStage = determineUserStage(userQualified, bracket, champion);
        return new TournamentSimulation(seed, userRating, groups, userGroupName, userPosition,
                userQualified, finalStage, champion.equals(GroupStageService.USER_TEAM), champion, bracket);
    }

    private List<TournamentTeam> createEntrants(double userRating, SeededRandom random,
                                                 Map<String, Double> ratings) {
        List<TournamentTeam> entrants = new ArrayList<>();
        entrants.add(new TournamentTeam(GroupStageService.USER_TEAM, userRating));
        for (HistoricalTeam historicalTeam : playerRepository.findHistoricalTeams()) {
            String name = historicalTeam.displayName();
            double rating = historicalTeam.rating();
            ratings.put(name, rating);
            entrants.add(new TournamentTeam(name, rating));
        }
        return entrants;
    }

    private void moveUserIntoRandomGroup(List<TournamentTeam> entrants, SeededRandom random) {
        int desiredGroup = random.nextInt(GROUP_NAMES.size());
        int desiredIndex = desiredGroup * rules.groupSize();
        int userIndex = 0;
        for (int index = 0; index < entrants.size(); index++) {
            if (entrants.get(index).getName().equals(GroupStageService.USER_TEAM)) userIndex = index;
        }
        TournamentTeam temporary = entrants.get(desiredIndex);
        entrants.set(desiredIndex, entrants.get(userIndex));
        entrants.set(userIndex, temporary);
    }

    private List<String> simulateQuarterfinals(List<GroupStageResult> groups, Map<String, Double> ratings,
                                                SeededRandom random, List<KnockoutSeriesResult> bracket) {
        List<String> winners = new ArrayList<>();
        winners.add(playSeries("Cuartos 1", groups.get(0).firstPlace(), groups.get(1).secondPlace(), ratings, random, bracket));
        winners.add(playSeries("Cuartos 2", groups.get(2).firstPlace(), groups.get(3).secondPlace(), ratings, random, bracket));
        winners.add(playSeries("Cuartos 3", groups.get(1).firstPlace(), groups.get(0).secondPlace(), ratings, random, bracket));
        winners.add(playSeries("Cuartos 4", groups.get(3).firstPlace(), groups.get(2).secondPlace(), ratings, random, bracket));
        return winners;
    }

    private String playSeries(String stage, String teamOne, String teamTwo, Map<String, Double> ratings,
                              SeededRandom random, List<KnockoutSeriesResult> bracket) {
        int oneWins = 0;
        int twoWins = 0;
        List<String> games = new ArrayList<>();
        while (oneWins < rules.knockoutWinsRequired() && twoWins < rules.knockoutWinsRequired()) {
            boolean firstWon = ratings.get(teamOne) + random.nextDouble(-11, 11)
                    >= ratings.get(teamTwo) + random.nextDouble(-11, 11);
            if (firstWon) oneWins++; else twoWins++;
            games.add("Partida " + (games.size() + 1) + ": " + (firstWon ? teamOne : teamTwo));
        }
        String winner = oneWins > twoWins ? teamOne : teamTwo;
        bracket.add(new KnockoutSeriesResult(stage, teamOne, teamTwo, oneWins, twoWins, winner, games));
        return winner;
    }

    private String determineUserStage(boolean qualified, List<KnockoutSeriesResult> bracket, String champion) {
        if (!qualified) return "Fase de grupos";
        if (champion.equals(GroupStageService.USER_TEAM)) return "Campeón";
        for (KnockoutSeriesResult series : bracket) {
            boolean participated = series.teamOne().equals(GroupStageService.USER_TEAM)
                    || series.teamTwo().equals(GroupStageService.USER_TEAM);
            if (participated && !series.winner().equals(GroupStageService.USER_TEAM)) {
                if (series.stage().startsWith("Cuartos")) return "Cuartos de final";
                if (series.stage().startsWith("Semifinal")) return "Semifinales";
                return "Final";
            }
        }
        return "Fase de grupos";
    }
}
