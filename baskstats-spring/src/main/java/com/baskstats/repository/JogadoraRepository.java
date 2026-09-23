package com.time.api.repository;

import com.time.api.model.Jogadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JogadoraRepository extends JpaRepository<Jogadora, Integer> {
}
