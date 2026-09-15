package com.dante.worldroster;

import com.dante.worldroster.config.GameRules;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GameRules.class)
public class WorldRosterApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorldRosterApplication.class, args);
    }
}
