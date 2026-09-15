package com.dante.worldroster.model;

import java.util.List;

public record TournamentSimulation(
        long seed,
        double teamRating,
        List<GroupStageResult> groups,
        String userGroupName,
        int userGroupPosition,
        boolean reachedKnockoutStage,
        String finalStage,
        boolean champion,
        String worldChampion,
        List<KnockoutSeriesResult> bracket
) {
}
