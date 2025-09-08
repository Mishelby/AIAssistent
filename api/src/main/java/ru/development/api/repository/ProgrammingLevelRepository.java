package ru.development.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.development.api.model.ProgrammingLevelEntity;
import ru.development.api.model.enums.ProgrammingLevel;

import java.util.Optional;

@Repository
public interface ProgrammingLevelRepository extends
        JpaRepository<ProgrammingLevelEntity, Long>,
        JpaSpecificationExecutor<ProgrammingLevelEntity> {

    @Query("""
            SELECT ple
            FROM ProgrammingLevelEntity ple
            WHERE ple.code = :levelName
            """)
    Optional<ProgrammingLevelEntity> findByLevelName(ProgrammingLevel levelName);
}
