package org.example.soccermatchstatsapi.repository;

import org.example.soccermatchstatsapi.model.State;
import org.example.soccermatchstatsapi.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByName(String name);
    List<Team> findByState(State state);
    List<Team> findByStatusActive(boolean isActive);
    Team findByNameAndState(String name, State state);

}
