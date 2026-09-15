package com.dante.worldroster.service;

import com.dante.worldroster.model.DraftOption;
import com.dante.worldroster.model.Player;
import com.dante.worldroster.model.Position;
import com.dante.worldroster.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DraftService {
    private final PlayerRepository repository;

    public DraftService(PlayerRepository repository) {
        this.repository = repository;
    }

    public DraftOption generateOptions(Position position, int optionCount) {
        List<Player> candidates = new ArrayList<>(repository.findByPosition(position));
        Collections.shuffle(candidates);
        List<Player> options = candidates.stream().limit(optionCount).toList();
        return new DraftOption(position, position.ordinal() + 1, options);
    }
}
