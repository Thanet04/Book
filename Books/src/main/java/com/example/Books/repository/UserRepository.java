package com.example.Books.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Books.bean.User;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "select * from user u where u.username = :username", nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = "select * from user where email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);
}
