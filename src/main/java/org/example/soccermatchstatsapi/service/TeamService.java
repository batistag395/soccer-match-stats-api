package org.example.soccermatchstatsapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.dto.TeamDto;
import org.example.soccermatchstatsapi.enums.StateEnum;
import org.example.soccermatchstatsapi.interfaces.TeamInterface;
import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.repository.MatchRepository;
import org.example.soccermatchstatsapi.repository.TeamRepository;
import org.example.soccermatchstatsapi.specification.TeamSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class TeamService implements TeamInterface {

    private TeamRepository teamRepository;
    private MatchRepository matchRepository;
    @Override
    public void createTeam(Team team) {
        if(team.getName() == null || team.getName().isEmpty() ||
            team.getState() == null || team.getState().isEmpty() ||
            team.getCreationDate() == null){
            throw new IllegalArgumentException("Requered data is missing.");
        }
        if(team.getName().length() < 2){
            throw new IllegalArgumentException("Team name must be at least 2 characters.");
        }
        boolean stateExists = Stream.of(StateEnum.values())
                .anyMatch(estado -> estado.getSigla().equals(team.getState().toUpperCase()));
        if(!stateExists){
            throw new IllegalArgumentException("Team state doesnt belong to Brazil.");
        }
        Optional<Team> thisTeamExists = teamRepository.findByNameAndStateIgnoreCase(team.getName(), team.getState());
        if(team.getCreationDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Team creation date is incorrect.");
        }
        if (thisTeamExists.isPresent()) {
            throw new IllegalArgumentException("Team already exists.");
        }
        teamRepository.save(team); ;
    }

    @Override
    public void updateTeam(long id, TeamDto team) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            if(team.getName() != null && !team.getName().isEmpty()){
                String stateToCheck = team.getState() == null ? optionalTeam.get().getState() : team.getState();
                Optional<Team> teamVerificationData = teamRepository.findByNameAndStateIgnoreCase(team.getName(), optionalTeam.get().getState());

                if (teamVerificationData.isPresent()) {
                    throw new IllegalArgumentException("Team already exists.");
                }
            }
            Team updatedTeam = optionalTeam.get();
            if(team.getName() != null && team.getName().length() < 2){
                throw new IllegalArgumentException("Team name must be at least 2 characters.");
            }
            if(team.getState() != null && !team.getState().isEmpty()){
                boolean stateExists = Stream.of(StateEnum.values())
                        .anyMatch(state -> state.getSigla().equals(team.getState().toUpperCase()));
                if(stateExists){
                    updatedTeam.setState(team.getState());
                }else{
                    throw new IllegalArgumentException("Team state doesnt belong to Brazil or doesnt exist..");
                }
            }
            if(team.getCreationDate() != null && !team.getCreationDate().isAfter(LocalDate.now())){
                List<Match> matchFound = matchRepository.findByTeam(updatedTeam).stream()
                        .filter(creationData -> creationData.getMatchDate().isBefore(OffsetDateTime.from(team.getCreationDate())))
                        .toList();
                if(!matchFound.isEmpty() && team.getCreationDate().isAfter(LocalDate.now())){
                    throw new IllegalArgumentException("Creation date is incorrect, because is after a match date of the team..");
                }
                if(matchFound.isEmpty() && team.getCreationDate().isAfter(LocalDate.now())){
                    throw new IllegalArgumentException("Creation date is incorrect, because is on the future.");
                }
            }
            updatedTeam.setName(team.getName() == null ? optionalTeam.get().getName() : team.getName());
            updatedTeam.setCreationDate(team.getCreationDate() == null ? optionalTeam.get().getCreationDate() : team.getCreationDate());
            updatedTeam.setState(team.getState() == null ? optionalTeam.get().getState() : team.getState());
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
                throw new IllegalArgumentException("the team with id " + id + " was not found as an active team.");
            }
        }else {
            throw new IllegalArgumentException("the team with id " + id + " was not found.");
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
            throw new IllegalArgumentException("the team list is empty");
        }
        return teamList;
    }

    @Override
    public List<Team> getTeamsByNameAndStateAndStatusActive(String name, String state, Boolean isActive){
        Specification<Team> teamFilterSpecifications = Specification.where(null);
        if (name != null) {
            teamFilterSpecifications = teamFilterSpecifications.and(TeamSpecifications.hasName(name));
        }
        if (state != null) {
            teamFilterSpecifications = teamFilterSpecifications.and(TeamSpecifications.hasState(state));
        }
        if (isActive != null) {
            teamFilterSpecifications = teamFilterSpecifications.and(TeamSpecifications.isActive(isActive));
        }
        return teamRepository.findAll(teamFilterSpecifications);
    }
}
