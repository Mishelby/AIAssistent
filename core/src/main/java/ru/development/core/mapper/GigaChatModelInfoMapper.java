package ru.development.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.development.core.model.GigaChatModelInfo;

@Mapper(componentModel = "spring")
public interface GigaChatModelInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(System.currentTimeMillis())")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "modelName", source = "model")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "message", source = "userMessage")
    @Mapping(target = "userRequestId", source = "userRequestId")
    @Mapping(target = "status", source = "status")
    GigaChatModelInfo toEntity(String model,
                               String content,
                               String role,
                               String userRequestId,
                               String userMessage,
                               String status);

}
