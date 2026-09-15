package com.dante.worldroster.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.rules")
public record GameRules(
        int draftOptions,
        int groupSize,
        int groupRoundRobins,
        int groupQualifiers,
        int knockoutWinsRequired
) {
}
