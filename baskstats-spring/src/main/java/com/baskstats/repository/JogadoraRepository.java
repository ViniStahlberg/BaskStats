package com.baskstats.repository;

import com.baskstats.model.Jogadora;
import com.baskstats.model.Lance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JogadoraRepository extends JpaRepository<Jogadora, Integer> {
}
