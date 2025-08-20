package ru.development.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.development.main.model.CreateCollectionRequest;
import ru.development.main.service.MilvusVectorService;

/**
 * Контроллер векторной БД Milvus
 */

@RestController
@RequestMapping("/api/v1/vectors")
@RequiredArgsConstructor
public class MilvusVectorController {
    private final MilvusVectorService milvusVectorService;

    @PostMapping("/collection")
    public ResponseEntity<String> createCollection(@RequestBody CreateCollectionRequest request) {
        milvusVectorService.createCollectionInVectorDB(
                request.getCollectionName(),
                request.getFieldName(),
                request.getIndexName(),
                request.getDescription()
        );

        if (request.getPartitionName() != null && !request.getPartitionName().isBlank()) {
            milvusVectorService.createPartitionInCollection(
                    request.getCollectionName(),
                    request.getPartitionName()
            );
        }

        return ResponseEntity.ok("Collection created successfully: " + request.getCollectionName());
    }
}
