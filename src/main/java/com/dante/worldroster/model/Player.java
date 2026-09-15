package com.dante.worldroster.model;

public record Player(
        Long id,
        String nickname,
        String team,
        int year,
        Position position,
        Region region,
        int mechanics,
        int consistency,
        int experience,
        int teamwork,
        String peakLabel,
        String achievement,
        String ratingType,
        String dataScope
) {
    public double calculateRating() {
        return mechanics * 0.35 + consistency * 0.25 + experience * 0.20 + teamwork * 0.20;
    }
}
