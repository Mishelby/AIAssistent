package ru.development.core.controller;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.development.core.model.GigaChatResponse;
import ru.development.core.service.GigaChatChatModelService;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class GigaChatController {
    private final GigaChatChatModelService gigaChatChatModelService;

    @PostMapping("/ask")
    @SystemMessage(value = "Ты учитель математики и физики")
    @UserMessage(value = "Привет пользователь!")
    public ResponseEntity<GigaChatResponse> sendMessage(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) String requestId,
            @RequestParam String message
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gigaChatChatModelService.sendMessage(servletRequest, requestId, message));
    }

    @PostMapping
    public ResponseEntity<?> createEmbedding() {
        return ResponseEntity.ok().build();
    }
}
