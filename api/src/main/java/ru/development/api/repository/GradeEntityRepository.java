package ru.development.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.development.api.model.GradeEntity;

@Repository
public interface GradeEntityRepository extends
        JpaRepository<GradeEntity, Long>,
        JpaSpecificationExecutor<GradeEntity> {
}
