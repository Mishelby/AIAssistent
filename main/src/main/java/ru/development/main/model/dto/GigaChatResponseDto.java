package ru.development.main.model.dto;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Builder
public final class GigaChatResponseDto implements GigaChatResponse {
    String modelName;
    String message;
    Long gigaChatId;
}
