package com.dante.worldroster.model;

public class TournamentTeam {
    private final String name;
    private final double rating;
    private int wins;
    private int losses;
    private int performanceDifference;

    public TournamentTeam(String name, double rating) {
        this.name = name;
        this.rating = rating;
    }

    public void registerWin(int difference) {
        wins++;
        performanceDifference += Math.abs(difference);
    }

    public void registerLoss(int difference) {
        losses++;
        performanceDifference -= Math.abs(difference);
    }

    public String getName() { return name; }
    public double getRating() { return rating; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getPerformanceDifference() { return performanceDifference; }
}
