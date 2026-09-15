package com.dante.worldroster.model;

import java.util.List;

public record KnockoutSeriesResult(
        String stage,
        String teamOne,
        String teamTwo,
        int teamOneWins,
        int teamTwoWins,
        String winner,
        List<String> games
) {
}
