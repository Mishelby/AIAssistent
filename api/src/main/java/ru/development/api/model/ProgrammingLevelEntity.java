package ru.development.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.development.api.model.enums.ProgrammingLevel;
import ru.development.api.utils.ProgrammingLevelConverter;

@Entity
@Table(name = "level", schema = "students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgrammingLevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Convert(converter = ProgrammingLevelConverter.class)
    @Column(name = "code")
    private ProgrammingLevel code;

    @Column(name = "number")
    private Integer levelNumber;
}
