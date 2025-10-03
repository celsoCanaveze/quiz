package com.example.brainup.brainup_backend.service;


import com.example.brainup.brainup_backend.model.Player;
import com.example.brainup.brainup_backend.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuizService {

    @Autowired
    private QuizRepository repository;

    public UUID addPlayer(String playerName) {
        UUID playerId = UUID.randomUUID();
        Player player = Player.builder()
                .id(playerId)
                .name(playerName)
                .active(true)
                .build();

        repository.addPlayer(player);
        repository.notifyAdmins("player.joined", player);

        return playerId;
    }

    public void removePlayer(UUID playerId) {
        repository.deactivatePlayer(playerId);
        Player player = repository.getQuiz().getPlayers().get(playerId);
        repository.removePlayer(playerId);
        repository.notifyAdmins("player.exited", player);
    }
}
