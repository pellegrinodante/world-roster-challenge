package com.dante.worldroster.model;

public record GroupStanding(
        int position,
        String teamName,
        int wins,
        int losses,
        int performanceDifference
) {
}
