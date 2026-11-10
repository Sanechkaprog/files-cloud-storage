package com.alexkrug.files_cloud_storage.auth.infrastructure;

import com.alexkrug.files_cloud_storage.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsername(String username);

    User getUserByUsername(String username);

    User getUserByUserId(UUID userId);

}