package ru.development.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.development.main.model.dto.CreateEmbeddingRequestDto;
import ru.development.main.service.EmbeddingService;

@Slf4j
@RestController
@RequestMapping("/api/v1/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {
    private final EmbeddingService embeddingService;

    @PostMapping
    public ResponseEntity<String> createEmbedding(
            HttpServletRequest httpServletRequest,
            @RequestBody CreateEmbeddingRequestDto createEmbeddingRequestDto) {
        String embedding = embeddingService.createEmbedding(httpServletRequest, createEmbeddingRequestDto);
        return ResponseEntity.ok().body(embedding.toString());
    }
}
