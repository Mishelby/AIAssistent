package ru.development.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.development.api.model.enums.ProgrammingLanguage;
import ru.development.api.utils.ProgrammingLanguageConverter;

@Entity
@Table(name = "language", schema = "students")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgrammingLanguageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Convert(converter = ProgrammingLanguageConverter.class)
    private ProgrammingLanguage language;

    private Integer levelNumber;
}
