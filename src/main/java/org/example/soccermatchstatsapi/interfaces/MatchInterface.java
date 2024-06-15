package org.example.soccermatchstatsapi.interfaces;

import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.model.Stadium;
import org.example.soccermatchstatsapi.model.Team;

import java.util.List;

public interface MatchInterface {
    void createMatch(Match match);
    void updateMatch(long id, Match match);
    void deleteMatch(long id);
    Match getMatchById(long id);
    List<Match> getMatchesWithFilter(String team, String stadium);
}
