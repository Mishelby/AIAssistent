package ru.development.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.development.core.model.GigaChatModelInfo;

@Repository
public interface GigaChatModelInfoRepository extends JpaRepository<GigaChatModelInfo, Long> {
}