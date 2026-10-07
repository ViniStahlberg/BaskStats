package com.time.api.repository;

import com.time.api.model.Lance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanceRepository extends JpaRepository<Lance, Integer> {
}