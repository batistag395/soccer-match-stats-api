package org.example.soccermatchstatsapi.interfaces;

import org.example.soccermatchstatsapi.dto.*;
import org.example.soccermatchstatsapi.model.Team;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeamInterface<T, L extends Number> {
    public void createTeam(Team team);
    public void updateTeam(long id, TeamDto team);
    public void deleteTeam(long id);
    public Team getTeamById(long id);
    public List<Team> getAllTeams();
    public TeamPageableDto getTeamsByNameAndStateAndStatusActive(String name, String state, Boolean isActive, Pageable pageable);
    public TeamStatsDto teamStats(long id);
    public List<TeamMatchHistoryDto> teamMatchHistory(long id);
    public TeamMatchHistoryStatsDto findMatchHistoryBetweenTeams(long id_team1, long id_team2);
}
