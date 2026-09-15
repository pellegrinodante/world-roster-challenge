package com.dante.worldroster.model;

import java.util.List;

public record GroupStageResult(
        String groupName,
        int userPosition,
        int userWins,
        int userLosses,
        boolean qualified,
        List<GroupStanding> standings,
        List<String> matchHistory
) {
    public String firstPlace() {
        return standings.get(0).teamName();
    }

    public String secondPlace() {
        return standings.get(1).teamName();
    }
}
