package ru.development.core.service;

import chat.giga.client.auth.AuthClient;
import chat.giga.langchain4j.GigaChatEmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatEmbeddingModelService {


    protected GigaChatEmbeddingModel getGigaChatEmbeddingModel(String authKey) {
        return GigaChatEmbeddingModel.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(authKey)
                        .build())
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
