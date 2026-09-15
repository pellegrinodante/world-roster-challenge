package com.dante.worldroster.model;

import java.util.List;

public record DraftOption(Position position, int round, List<Player> players) {
}
