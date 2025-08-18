package ru.development.core.controller;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.development.core.model.GigaChatResponse;
import ru.development.core.service.GigaChatMessageService;


@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class GigaChatController {
    private final GigaChatMessageService gigaChatMessageService;

    @PostMapping("/ask")
    public ResponseEntity<GigaChatResponse> sendMessage(
            HttpServletRequest servletRequest,
            @RequestHeader HttpHeaders headers,
            @RequestParam String message
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gigaChatMessageService.sendMessage(servletRequest, headers, message));
    }

    @PostMapping
    public ResponseEntity<?> createEmbedding() {
        return ResponseEntity.ok().build();
    }
}
