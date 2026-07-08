
package com.parking.backend.repository;

import com.parking.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByDocument(String document);

    boolean existsByUsername(String username);

    boolean existsByDocument(String document);

    Optional<User> findByDocumentAndRole_Name(String document, String roleName);
}

