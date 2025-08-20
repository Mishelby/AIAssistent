package ru.development.main.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCollectionRequest {
    private String collectionName;
    private String description;
    private String fieldName;
    private String indexName;
    private String partitionName;
}
