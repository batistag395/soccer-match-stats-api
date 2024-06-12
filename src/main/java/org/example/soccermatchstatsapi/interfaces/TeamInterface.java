package org.example.soccermatchstatsapi.interfaces;

import org.example.soccermatchstatsapi.model.State;
import org.example.soccermatchstatsapi.model.Team;

import java.util.List;

public interface TeamInterface<T, L extends Number> {
    public void createTeam(Team team);
    public void updateTeam(long id, Team team);
    public void deleteTeam(long id);
    public Team getTeamById(long id);
    public List<Team> getAllTeams();
    public List<Team> getTeamByName(String teamName);
    public List<Team> getTeamByState(State state);
    public List<Team> getTeamsByStatusActive(boolean isActive);
}