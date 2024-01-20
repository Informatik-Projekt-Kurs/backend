package com.MeetMate.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // SELECT * FROM users WHERE email = :email;
  // @Query("SELECT s FROM User s WHERE s.email = ?1")
  Optional<User> findUserByEmail(String email);

  Optional<User> findUserById(Long id);
}
