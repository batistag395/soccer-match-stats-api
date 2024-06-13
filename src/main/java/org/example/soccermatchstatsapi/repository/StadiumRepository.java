package org.example.soccermatchstatsapi.repository;

import org.example.soccermatchstatsapi.model.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {
    Optional<Stadium> findByNameIgnoreCase(String name);
}
