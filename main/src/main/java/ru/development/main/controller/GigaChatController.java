package ru.development.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.development.main.aop.HttpRequestsCounter;
import ru.development.main.model.dto.GigaChatResponse;
import ru.development.main.service.GigaChatMessageService;


@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class GigaChatController {
    private final GigaChatMessageService gigaChatMessageService;

    @PostMapping("/ask")
    @HttpRequestsCounter
    public ResponseEntity<GigaChatResponse> sendMessage(
            HttpServletRequest servletRequest,
            @RequestHeader HttpHeaders headers,
            @RequestParam String message
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gigaChatMessageService.sendMessage(servletRequest, headers, message)
        );
    }

}
