package ru.development.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grade", schema = "students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
public class GradeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

}
