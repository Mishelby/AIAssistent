package ru.development.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.development.main.model.GigaChatModelInfo;

@Repository
public interface GigaChatModelInfoRepository extends JpaRepository<GigaChatModelInfo, Long> {
}