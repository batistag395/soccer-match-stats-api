package org.example.soccermatchstatsapi.repository;

import org.example.soccermatchstatsapi.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    List<Team> findByName(String name);
    Optional<Team> findByNameAndStateIgnoreCase(String name, String state);

}
