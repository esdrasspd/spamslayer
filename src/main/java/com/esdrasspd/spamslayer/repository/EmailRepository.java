package com.esdrasspd.spamslayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esdrasspd.spamslayer.model.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    
}
