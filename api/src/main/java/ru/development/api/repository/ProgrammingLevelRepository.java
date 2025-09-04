package ru.development.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.development.api.model.ProgrammingLevelEntity;

@Repository
public interface ProgrammingLevelRepository extends
        JpaRepository<ProgrammingLevelEntity, Long>,
        JpaSpecificationExecutor<ProgrammingLevelEntity> {
}