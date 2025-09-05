package ru.development.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat", schema = "students")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "chat_number",nullable = false, unique = true)
    private String chatNumber;

    @OneToOne(mappedBy = "chat")
    private UserEntity user;
}
