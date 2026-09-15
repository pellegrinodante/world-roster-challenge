package com.dante.worldroster.model;

import java.util.List;

public record PlayerCatalog(
        int schemaVersion,
        String datasetName,
        String description,
        CatalogMethodology methodology,
        List<CatalogSource> sources,
        List<Player> players,
        List<HistoricalTeam> historicalTeams
) {
}
