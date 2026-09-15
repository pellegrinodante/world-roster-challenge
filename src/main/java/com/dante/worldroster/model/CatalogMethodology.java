package com.dante.worldroster.model;

import java.util.List;

public record CatalogMethodology(
        List<String> factualFields,
        List<String> derivedFields,
        String ratingScale,
        String note
) {
}
