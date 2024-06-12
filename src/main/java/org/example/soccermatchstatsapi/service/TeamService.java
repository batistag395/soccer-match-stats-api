package org.example.soccermatchstatsapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.interfaces.TeamInterface;
import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.model.State;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.repository.MatchRepository;
import org.example.soccermatchstatsapi.repository.StateRepository;
import org.example.soccermatchstatsapi.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TeamService implements TeamInterface {

    private TeamRepository teamRepository;
    private StateRepository stateRepository;
    private MatchRepository matchRepository;
    @Override
    public void createTeam(Team team) {
        if(team.getName() == null || team.getState() == null || team.getCreationDate() == null){
            throw new IllegalArgumentException("Requered data is missing.");
        }
        if(team.getName().length() < 2){
            throw new IllegalArgumentException("Team name must be at least 2 characters.");
        }
        Optional<State> state = stateRepository.findByName(team.getState().getStateAbbreviation());
        if(state.isEmpty()){
            throw new IllegalArgumentException("Team state is not belong for Brazil.");
        }
        Optional<Team> thisTeamExists = teamRepository.findByNameAndState(team.getName(), team.getState());
        if (thisTeamExists.isPresent()) {
            throw new IllegalArgumentException("Team already exists.");
        }
        if(team.getCreationDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Team creation date is incorrect.");
        }
        teamRepository.save(team); ;
    }

    @Override
    public void updateTeam(long id, Team team) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            if(!team.getName().isEmpty() && !team.getState().getStateAbbreviation().isEmpty()){
                Optional<Team> teamVerificationData = teamRepository.findByNameAndState(team.getName(), team.getState());
                if (teamVerificationData.isPresent()) {
                    throw new IllegalArgumentException("Team already exists.");
                }
            }
            Team updatedTeam = optionalTeam.get();
            if(team.getName() != null && team.getName().length() < 2){
                throw new IllegalArgumentException("Team name must be at least 2 characters.");
            }
            if(team.getState() != null){
                Optional<State> optionalState = stateRepository.findByName(team.getState().getStateAbbreviation());
                if(optionalState.isPresent()){
                    updatedTeam.setState(team.getState());
                }else{
                    throw new IllegalArgumentException("Team state is not belong for Brazil or doesnt exist..");
                }
            }
            if(team.getCreationDate() != null && !team.getCreationDate().isAfter(LocalDate.now())){
                List<Match> matchFound = matchRepository.findByTeam(updatedTeam).stream()
                        .filter(creationData -> creationData.getMatchDate().isBefore(OffsetDateTime.from(team.getCreationDate())))
                        .toList();
                if(!matchFound.isEmpty()){
                    throw new IllegalArgumentException("Creation date is incorrect, because is after a match date of the team.");
                }
            }
            updatedTeam.setName(team.getName());
            updatedTeam.setCreationDate(team.getCreationDate());
            updatedTeam.setState(team.getState());
            teamRepository.save(updatedTeam);
        }else{
            throw new IllegalArgumentException("Team not found.");
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
