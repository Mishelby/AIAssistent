package ru.development.main.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Builder
public final class GigaChatModelInfoDto implements GigaChatResponse {
    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("userRequestId")
    private String userRequestId;

    @Lob
    @JsonProperty("message")
    private String message;

    @Lob
    @JsonProperty("content")
    private String content;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}
