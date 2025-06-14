package com.example.outsourcingproject.domain.user.repository;

import com.example.outsourcingproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isDeleted = false")
    Optional<User> findActiveByUsername(@Param("username") String username);

    Optional<User> findByIdAndIsDeletedFalse(Long id);
}