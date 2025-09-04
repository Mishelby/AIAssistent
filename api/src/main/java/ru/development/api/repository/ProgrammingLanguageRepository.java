package ru.development.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.development.api.model.ProgrammingLanguageEntity;

@Repository
public interface ProgrammingLanguageRepository extends
        JpaRepository<ProgrammingLanguageEntity, Long>,
        JpaSpecificationExecutor<ProgrammingLanguageEntity> {
}