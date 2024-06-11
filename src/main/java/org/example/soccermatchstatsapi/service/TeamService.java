package org.example.soccermatchstatsapi.service;

import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.interfaces.TeamInterface;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.repository.TeamRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TeamService implements TeamInterface {

    private TeamRepository teamRepository;
    @Override
    public void createTeam(Team team) {
        teamRepository.save(team); ;
    }

    @Override
    public void updateTeam(long id, Team team) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            Team updatedTeam = optionalTeam.get();
            if(team.getName() != null){
                updatedTeam.setName(team.getName());
            }
            teamRepository.save(updatedTeam);
        }
    }

    @Override
    public void deleteTeam(long id) {
        Optional<Team> teamToUpdate = teamRepository.findById(id);
        if (teamToUpdate.isPresent()) {
            Team team = teamToUpdate.get();
            if(team.isActive()){
                team.setActive(false);
                teamRepository.save(team);
            }
        }

    }

    @Override
    public Team getTeamById(long id) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        try{
            if (optionalTeam.isPresent()) {
                return optionalTeam.get();
            }
        }catch (Exception e){
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    @Override
    public List<Team> getAllTeams() {
        return List.of();
    }

    @Override
    public Team getTeamByName(String teamName) {
        return null;
    }

    @Override
    public Team getTeamByState(String state) {
        return null;
    }

    @Override
    public List<Team> getTeamsByStatusActive(boolean isActive) {
        return List.of();
    }
}
