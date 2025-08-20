package ru.development.main.service;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.partition.request.CreatePartitionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.response.InsertResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.development.main.model.dto.DataForVectorDB;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для создания схемы для векторной БД Milvus
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MilvusVectorService {
    private final MilvusClientV2 milvusClientV2;
    private final ObjectMapper objectMapper;

    public static CreateCollectionReq.FieldSchema createFieldSchema(
            String name, String desc, DataType dataType,
            boolean isPrimary, Integer dimension
    ) {
        CreateCollectionReq.FieldSchema fieldSchema = CreateCollectionReq.FieldSchema.builder()
                .name(name)
                .description(desc)
                .autoID(false)
                .isPrimaryKey(isPrimary)
                .dataType(dataType)
                .build();
        if (null != dimension) {
            fieldSchema.setDimension(dimension);
        }
        return fieldSchema;
    }

    private static List<CreateCollectionReq.FieldSchema> createFieldSchemas() {
        return List.of(
                createFieldSchema("embedding_id", "Primary key", DataType.Int64, true, null),
                createFieldSchema("model", "Embedding model", DataType.VarChar, false, null),
                createFieldSchema("category", "Tag/Category", DataType.VarChar, false, null),
                createFieldSchema("embedding_vector", "Vector", DataType.FloatVector, false, 5)
        );
    }

    private static CreateCollectionReq.CollectionSchema createCollectionSchema(
            List<CreateCollectionReq.FieldSchema> fieldSchemas) {
        return CreateCollectionReq.CollectionSchema.builder()
                .fieldSchemaList(fieldSchemas)
                .build();
    }

    public void createCollectionInVectorDB(String collectionName,
                                           String fieldName,
                                           String indexName,
                                           String description) {
        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                .collectionName(collectionName)
                .indexParams(List.of(createIndexParam(fieldName, indexName)))
                .description(description)
                .collectionSchema(createCollectionSchema(createFieldSchemas()))
                .build();
        milvusClientV2.createCollection(createCollectionReq);
    }

    private IndexParam createIndexParam(String fieldName, String indexName) {
        return IndexParam.builder()
                .fieldName(fieldName)
                .indexName(indexName)
                .metricType(IndexParam.MetricType.COSINE)
                .indexType(IndexParam.IndexType.AUTOINDEX)
                .build();
    }

    public void createPartitionInCollection(String collectionName, String partitionName) {
        CreatePartitionReq createPartitionReq = CreatePartitionReq.builder()
                .collectionName(collectionName)
                .partitionName(partitionName)
                .build();
        milvusClientV2.createPartition(createPartitionReq);
    }

    public void insertDataIntoVectorDB(String collectionName, String partition, List<DataForVectorDB> data){
        List<JSONObject> bookJsons = new ArrayList<>();
        for (DataForVectorDB dataObj : data) {
            JSONObject jsonObject = objectMapper.convertValue(dataObj, JSONObject.class);
            bookJsons.add(jsonObject);
        }

        InsertReq insertReq = InsertReq.builder()
                .collectionName(collectionName)
                .partitionName(partition)
                .data(bookJsons)
                .build();

        milvusClientV2.insert(insertReq);
    }
}
