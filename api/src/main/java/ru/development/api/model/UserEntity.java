package ru.development.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users", schema = "students")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserEntity {
    // TODO вынести в утилитный класс
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String USER_NAME = "user_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @JsonProperty(FIRST_NAME)
    private String firstName;

    @JsonProperty(LAST_NAME)
    private String lastName;

    @JsonProperty(USER_NAME)
    private String userName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<GradeEntity> grade;

    @OneToOne(cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH},
            fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private ChatEntity chat;

}
