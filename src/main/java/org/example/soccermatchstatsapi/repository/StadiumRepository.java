package org.example.soccermatchstatsapi.repository;

import org.example.soccermatchstatsapi.model.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {}
