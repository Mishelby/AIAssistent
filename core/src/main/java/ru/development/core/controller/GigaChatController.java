package ru.development.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.MetadataDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.development.core.service.GigaChatChatModelService;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class GigaChatController {
    private final GigaChatChatModelService gigaChatChatModelService;

    @PostMapping("/ask")
    public ResponseEntity<?> sendMessage(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) String requestId,
            @RequestParam String message
    ) {
        return ResponseEntity.ok().body(gigaChatChatModelService.sendMessage(servletRequest, requestId, message));
    }
}
