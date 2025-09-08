package ru.development.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.development.api.model.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    @Query("""
            SELECT (COUNT(u.userName) > 0)
            FROM UserEntity u
            WHERE u.userName = :userName
            """)
    boolean existsByUsername(@Param("userName") String userName);

    @Query("""
            SELECT u
            FROM UserEntity u
            WHERE u.userName = :userName
            """)
    Optional<UserEntity> findByUserName(@Param("userName") String userName);
}
