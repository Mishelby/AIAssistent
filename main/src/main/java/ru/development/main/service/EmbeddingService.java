package ru.development.main.service;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.development.main.model.AccessToken;
import ru.development.main.model.dto.CreateEmbeddingRequestDto;
import ru.development.main.model.dto.DataForVectorDB;

import java.util.List;

/**
 * Проверить пока не могу, надо покупать токены :)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final MilvusVectorService milvusVectorService;
    private final CheckTokenService checkTokenService;

    public String createEmbedding(
            HttpServletRequest httpServletRequest,
            CreateEmbeddingRequestDto createEmbeddingRequestDto
    ) {
        AccessToken accessToken = checkTokenService.checkAccessToken(httpServletRequest.getRemoteAddr());

        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(accessToken.getAccessToken())
                        .build())
                .logRequests(true)
                .logResponses(true)
                .build();

        try {
            EmbeddingResponse embedding = client.embeddings(EmbeddingRequest.builder()
                    .model(createEmbeddingRequestDto.getModel())
                    .input(createEmbeddingRequestDto.getInput())
                    .build());

            List<DataForVectorDB> dataForVectorDBS = embedding.data()
                    .stream()
                    .map(embeddingData -> {
                        List<Float> vector = embeddingData.embedding();
                        return DataForVectorDB.builder()
                                .embeddingId(1L)
                                .embeddingVector(vector)
                                .model(embedding.model())
                                .category(createEmbeddingRequestDto.getCategory())
                                .build();
                    }).toList();

            milvusVectorService.insertDataIntoVectorDB(
                    createEmbeddingRequestDto.getCollectionName(),
                    createEmbeddingRequestDto.getPartitionName(),
                    dataForVectorDBS
            );

            return "Embedding was created: " + embedding;
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
