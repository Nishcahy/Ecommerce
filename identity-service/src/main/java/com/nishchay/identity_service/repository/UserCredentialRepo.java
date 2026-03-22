package com.nishchay.identity_service.repository;

import com.nishchay.identity_service.entity.UserCredentials;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepo extends JpaRepository<UserCredentials,Long> {

    Optional<UserCredentials> findByName(String username);
}
