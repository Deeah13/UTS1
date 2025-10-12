package com.bps.uts.sipakjabat.repository;

import com.bps.uts.sipakjabat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNip(String nip);
}