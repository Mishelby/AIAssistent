package ru.development.core.service;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.langchain4j.GigaChatEmbeddingModel;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.development.core.model.dto.CreateEmbeddingRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatEmbeddingModelService {

    protected EmbeddingResponse createEmbedding(String authKey, CreateEmbeddingRequestDto createEmbeddingRequestDto) {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(authKey)
                        .build())
                .logRequests(true)
                .logResponses(true)
                .build();

        try {
            return client.embeddings(EmbeddingRequest.builder()
                    .model(createEmbeddingRequestDto.getModel())
                    .input(createEmbeddingRequestDto.getInput())
                    .build());
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
