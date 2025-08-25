package ru.development.main.model;

import lombok.Builder;

import java.util.List;

@Builder
public record MetadataInfo(
        String methodName,
        String pathInfo,
        List<String> params
) {
}
