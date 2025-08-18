package ru.development.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class GigaChatResponseDto implements GigaChatResponse {
    private String modelName;
    private String message;
    private Long gigaChatId;

    @Override
    public String getType() {
        return "GigaChatResponseDto";
    }
}
