package ru.development.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.development.api.model.ProgrammingLanguageEntity;
import ru.development.api.model.enums.ProgrammingLanguage;

import java.util.Optional;

@Repository
public interface ProgrammingLanguageRepository extends
        JpaRepository<ProgrammingLanguageEntity, Long>,
        JpaSpecificationExecutor<ProgrammingLanguageEntity> {

    @Query("""
            SELECT ple
            FROM ProgrammingLanguageEntity ple
            WHERE ple.name = :languageName
            """)
    Optional<ProgrammingLanguageEntity> findByLanguageName(ProgrammingLanguage languageName);
}
