package org.example.soccermatchstatsapi.repository;

import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.model.Stadium;
import org.example.soccermatchstatsapi.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByHomeTeam(Team homeTeam);
    List<Match> findByAwayTeam(Team awayTeam);
    List<Match> findByStadium(Stadium stadium );

    default List<Match> findByTeam(Team team) {
        List<Match> homeMatches = findByHomeTeam(team);
        List<Match> awayMatches = findByAwayTeam(team);
        homeMatches.addAll(awayMatches);
        return homeMatches;
    }
}