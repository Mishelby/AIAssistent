package ru.development.core.model.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Builder
public final class GigaChatModelInfoDto implements GigaChatResponse {
    private String modelName;

    @Column(nullable = false)
    private String userRequestId;

    @Column(nullable = false, length = 5000)
    @Lob
    private String message;

    @Lob
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

}
