package com.example.brainup.brainup_backend.repository;

import com.example.brainup.brainup_backend.model.Player;
import com.example.brainup.brainup_backend.model.Quiz;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class QuizRepository {

    private final Quiz quiz = Quiz.builder()
            .name("BrainUp Quiz")
            .players(new ConcurrentHashMap<>())
            .build();

    private final Map<UUID, SseEmitter> adminEmitters = new ConcurrentHashMap<>();

    public Quiz getQuiz() {
        return quiz;
    }

    public void addPlayer(Player player) {
        quiz.getPlayers().put(player.getId(), player);
    }

    public void removePlayer(UUID playerId) {
        quiz.getPlayers().remove(playerId);
    }

    public void deactivatePlayer(UUID playerId) {
        Player player = quiz.getPlayers().get(playerId);
        if (player != null) {
            player.setActive(false);
        }
    }

    public void addAdminEmitter(UUID adminId, SseEmitter emitter) {
        adminEmitters.put(adminId, emitter);
    }

    public void removeAdminEmitter(UUID adminId) {
        adminEmitters.remove(adminId);
    }

    public void notifyAdmins(String eventName, Object payload) {
        adminEmitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(payload));
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
    }
}