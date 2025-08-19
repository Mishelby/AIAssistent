package ru.development.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(fluent = true)
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GigaChatModelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String modelName;

    @Column(nullable = false)
    private String userRequestId;

    @Column(nullable = false, length = 5000)
    @Lob
    private String message;

    private String role;

    @Lob
    private String content;

    private Long created;

    private String status;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
