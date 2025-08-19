package ru.development.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.development.main.model.GigaChatModelInfo;
import ru.development.main.model.dto.GigaChatModelInfoDto;

import java.time.LocalDateTime;

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

    @Mapping(target = "modelName", source = "model")
    @Mapping(target = "userRequestId", source = "userRequestId")
    @Mapping(target = "message", source = "userMessage")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "timestamp", source = "timestamp")
    GigaChatModelInfoDto toDto(String model,
                               String content,
                               String userRequestId,
                               String userMessage,
                               LocalDateTime timestamp);
}
