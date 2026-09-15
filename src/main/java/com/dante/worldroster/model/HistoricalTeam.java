package com.dante.worldroster.model;

public record HistoricalTeam(
        Long id,
        String name,
        int year,
        Region region,
        String achievement,
        double rating
) {
    public String displayName() {
        return name + " " + year;
    }
}
