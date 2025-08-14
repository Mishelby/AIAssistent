package ru.development.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String message;

    private String role;

    @Lob
    private String content;

    private Long created;

    private String status;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
