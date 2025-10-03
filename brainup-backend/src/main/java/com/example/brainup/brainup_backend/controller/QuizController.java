package com.example.brainup.brainup_backend.controller;


import com.example.brainup.brainup_backend.config.SseConfig;
import com.example.brainup.brainup_backend.repository.QuizRepository;
import com.example.brainup.brainup_backend.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizRepository repository;

    @Autowired
    private Executor sseExecutor;

    @Value("${sse.timeout.millis}")
    private Long sseTimeoutMillis;


    @PostMapping("/start")
    public StartResponse start(@RequestBody StartRequest request) {
        UUID playerId = quizService.addPlayer(request.getPlayerName());
        return new StartResponse(playerId);
    }


    @PostMapping("/exit")
    public void exit(@RequestBody ExitRequest request) {
        quizService.removePlayer(request.getPlayerId());
    }


    @GetMapping("/stream/admin")
    public SseEmitter streamAdmin() {
        UUID adminId = UUID.randomUUID();
        SseEmitter emitter = new SseEmitter(sseTimeoutMillis);
        repository.addAdminEmitter(adminId, emitter);

        emitter.onCompletion(() -> repository.removeAdminEmitter(adminId));
        emitter.onTimeout(() -> repository.removeAdminEmitter(adminId));

        return emitter;
    }


    public static class StartRequest {
        private String playerName;
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
    }

    public static class StartResponse {
        private UUID playerId;
        public StartResponse(UUID playerId) { this.playerId = playerId; }
        public UUID getPlayerId() { return playerId; }
        public void setPlayerId(UUID playerId) { this.playerId = playerId; }
    }

    public static class ExitRequest {
        private UUID playerId;
        public UUID getPlayerId() { return playerId; }
        public void setPlayerId(UUID playerId) { this.playerId = playerId; }
    }
}