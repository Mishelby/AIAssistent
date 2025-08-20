package ru.development.main.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record DataForVectorDB(
        @JsonProperty("embedding_id")
        Long embeddingId,
        String model,
        String category,
        @JsonProperty("embedding_vector")
        List<Float> embeddingVector
) {
}
