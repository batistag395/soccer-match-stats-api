package org.example.soccermatchstatsapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.example.soccermatchstatsapi.interfaces.TeamInterface;
import org.example.soccermatchstatsapi.model.State;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TeamService implements TeamInterface {

    private TeamRepository teamRepository;
    @Override
    public void createTeam(Team team) {
//        Team thisTeamExists = teamRepository.findByNameAndState(team.getName(), team.getState());
//        if (thisTeamExists == null && ) {
//
//        }

        teamRepository.save(team); ;
    }

    @Override
    public void updateTeam(long id, Team team) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            Team updatedTeam = optionalTeam.get();
            if(team.getName() != null && team.getName().length() <= 2){
                updatedTeam.setName(team.getName());
            }
            if(team.getState() != null){
                updatedTeam.setState(team.getState());
            }
            if(team.getCreationDate() != null && !team.getCreationDate().isAfter(LocalDate.now())){
                updatedTeam.setCreationDate(team.getCreationDate());
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
            }else {
                throw new EntityNotFoundException("the team with id " + id + " was not found as an active team.");
            }
        }else {
            throw new EntityNotFoundException("the team with id " + id + " was not found.");
        }
    }

    @Override
    public Team getTeamById(long id) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            if(optionalTeam.get().isActive()){
                return optionalTeam.get();
            }
        }
        throw new EntityNotFoundException("the team with id " + id + " was not found");
    }

    @Override
    public List<Team> getAllTeams() {
        List<Team> teamList = teamRepository.findAll().stream()
                .filter(Team::isActive)
                .collect(Collectors.toList());
        if(teamList.isEmpty()){
            throw new EntityNotFoundException("the team list is empty");
        }
        return teamList;
    }

    @Override
    public List<Team> getTeamByName(String teamName) {
        List<Team> teamList = teamRepository.findByName(teamName).stream()
                .filter(Team::isActive)
                .toList();
        if(teamList.isEmpty()){
            throw new EntityNotFoundException("the team with name " + teamName + " was not found");
        }
        return teamList;
    }

    @Override
    public List<Team> getTeamByState(State state) {
        List<Team> teamList = teamRepository.findByState(state).stream()
                .filter(Team::isActive)
                .toList();
        if(teamList.isEmpty()){
            throw new EntityNotFoundException("the team with state " + state + " was not found");
        }
        return teamList;
    }

    @Override
    public List<Team> getTeamsByStatusActive(boolean isActive) {
        List<Team> teamList = teamRepository.findByStatusActive(isActive);
        if(teamList.isEmpty()){
            throw new EntityNotFoundException("the team with status " + isActive + " was not found");
        }
        return teamList;
    }
}
