package org.example.soccermatchstatsapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.dto.*;
import org.example.soccermatchstatsapi.enums.StateEnum;
import org.example.soccermatchstatsapi.interfaces.TeamInterface;
import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.repository.MatchRepository;
import org.example.soccermatchstatsapi.repository.TeamRepository;
import org.example.soccermatchstatsapi.specification.TeamSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
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
                List<Match> matchFound = matchRepository.findById(updatedTeam.getId()).stream()
                        .filter(creationData -> creationData.getMatchDate().toLocalDate().isBefore(team.getCreationDate()))
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
        if (teamToUpdate.isPresent() && teamToUpdate.get().isActive()) {
            Team team = teamToUpdate.get();
            team.setActive(false);
            teamRepository.save(team);
        }else {
            throw new IllegalArgumentException("the team with id " + id + " was not found.");
        }
    }

    @Override
    public Team getTeamById(long id) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isPresent()) {
            return optionalTeam.get();
        }
        throw new IllegalArgumentException("the team with id " + id + " was not found");
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
    public TeamPageableDto getTeamsByNameAndStateAndStatusActive(String name, String state, Boolean isActive, Pageable pageable){
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
        System.out.println("filters: "+teamFilterSpecifications);
        Page<Team> page = teamRepository.findAll(teamFilterSpecifications, pageable);

        List<TeamDto> listTeam = page.map(this::mapDto).toList();

        return TeamPageableDto.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(listTeam)
                .build();
    }

    private TeamDto mapDto(Team team){
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .state(team.getState())
                .creationDate(team.getCreationDate())
                .isActive(team.isActive())
                .build();
    }
    @Override
    public List<TeamMatchHistoryDto> teamMatchHistory(long id){
        Optional<Team> teamOptional = teamRepository.findById(id);
        if(teamOptional.isPresent()){
            Team team = teamOptional.get();
            List<Match> matchList = matchRepository.findAll();

            return matchList.stream()
                    .filter(match -> match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team))
                    .collect(Collectors.groupingBy(match -> match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam()))
                    .entrySet().stream()
                    .map(entry ->{
                        Team opponent = entry.getKey();
                        List<Match> matchAgainstSomeTeam = entry.getValue();

                        long win = matchAgainstSomeTeam.stream().filter(match ->
                                (match.getHomeTeam().equals(team) && match.getHomeTeamScore() > match.getAwayTeamScore()) ||
                                        (match.getAwayTeam().equals(team) && match.getAwayTeamScore() > match.getHomeTeamScore()))
                            .count();
                        long loss = matchAgainstSomeTeam.stream().filter(match ->
                                (match.getHomeTeam().equals(team) && match.getHomeTeamScore() < match.getAwayTeamScore()) ||
                                        (match.getAwayTeam().equals(team) && match.getAwayTeamScore() < match.getHomeTeamScore()))
                            .count();
                        long draw = matchAgainstSomeTeam.stream().filter(match ->
                                match.getHomeTeamScore() == match.getAwayTeamScore())
                            .count();
                        int goals_scored =  matchAgainstSomeTeam.stream().mapToInt(match ->
                                        (match.getHomeTeam().equals(team) ? match.getHomeTeamScore() : match.getAwayTeamScore()))
                                .sum();
                        int goals_against =  matchAgainstSomeTeam.stream().mapToInt(match ->
                                        (match.getHomeTeam().equals(team) ? match.getAwayTeamScore() : match.getHomeTeamScore()))
                                .sum();
                        return TeamMatchHistoryDto.builder()
                                .team(team)
                                .opponent(opponent)
                                .win(win)
                                .loss(loss)
                                .draw(draw)
                                .goals_scored(goals_scored)
                                .goals_against(goals_against)
                                .build();
                    }).toList();
        }else{
            throw new IllegalArgumentException("Team not found.");
        }
    }
    @Override
    public TeamMatchHistoryStatsDto findMatchHistoryBetweenTeams(long id_team1, long id_team2){
        Optional<Team> team1 = teamRepository.findById(id_team1);
        Optional<Team> team2 = teamRepository.findById(id_team2);

        if(team1.isPresent() && team2.isPresent()){
            List<Match> matches = matchRepository.findAll();
            List<Match> matchesBetweenTeams = matches.stream()
                    .filter(match ->
                            match.getHomeTeam().getId() == id_team1 && match.getAwayTeam().getId() == id_team2 ||
                            match.getAwayTeam().getId() == id_team1 && match.getHomeTeam().getId() == id_team2
                    ).toList();
            long win_team_1 = matchesBetweenTeams.stream().filter(team ->
                            (team.getHomeTeam().getId() == id_team1 && team.getHomeTeamScore() > team.getAwayTeamScore()) ||
                            (team.getAwayTeam().getId() == id_team1 && team.getAwayTeamScore() > team.getHomeTeamScore())).count();
            long win_team_2 = matchesBetweenTeams.stream().filter(team ->
                            (team.getHomeTeam().getId() == id_team2 && team.getHomeTeamScore() > team.getAwayTeamScore()) ||
                            (team.getAwayTeam().getId() == id_team2 && team.getAwayTeamScore() > team.getHomeTeamScore())).count();


            long draw = matchesBetweenTeams.stream().filter(team ->
                    team.getHomeTeamScore() == team.getAwayTeamScore()).count();

            long loss_team_1 = ((matchesBetweenTeams.size() - draw)- win_team_1);
            long loss_team_2 = ((matchesBetweenTeams.size() - draw) - win_team_2);

            int gols_agains = matchesBetweenTeams.stream().filter(team ->
                    (team.getHomeTeam().getId() == id_team1)).mapToInt(Match::getAwayTeamScore).sum() +
                        matchesBetweenTeams.stream().filter(team ->
                                team.getAwayTeam().getId() ==id_team1).mapToInt(Match::getHomeTeamScore).sum();

            int goals_scored = matchesBetweenTeams.stream().filter(team ->
                    (team.getHomeTeam().getId() == id_team1)).mapToInt(Match::getHomeTeamScore).sum() +
                    matchesBetweenTeams.stream().filter(team ->
                                    (team.getAwayTeam().getId() == id_team1)).mapToInt(Match::getAwayTeamScore).sum();

            TeamStatsDto stats_team_1 = TeamStatsDto.builder()
                    .team(team1.get())
                    .win(win_team_1)
                    .loss(loss_team_1)
                    .draw(draw)
                    .goals_scored(goals_scored)
                    .goals_against(gols_agains)
                    .build();

            TeamStatsDto stats_team_2 = TeamStatsDto.builder()
                    .team(team2.get())
                    .win(win_team_2)
                    .loss(loss_team_2)
                    .draw(draw)
                    .goals_scored(gols_agains)
                    .goals_against(goals_scored)
                    .build();

            return TeamMatchHistoryStatsDto.builder()
                    .matchList(matchesBetweenTeams)
                    .team1(stats_team_1)
                    .team2(stats_team_2)
                    .build();
        }else {
            throw new IllegalArgumentException("Team not found.");
        }
    }
    public List<TeamMatchRankingDto> teamMatchRanking(){

        List<Match> matchList = matchRepository.findAll();

        List<TeamMatchRankingDto> matchRanking = teamRepository.findAll().stream().map(teamInMatch -> {
            long win = matchList.stream().filter(teamWin -> (teamWin.getHomeTeam().equals(teamInMatch) && teamWin.getHomeTeamScore() > teamWin.getAwayTeamScore()) ||
                    (teamWin.getAwayTeam().equals(teamInMatch) && teamWin.getAwayTeamScore() > teamWin.getHomeTeamScore())
            ).count();
            long loss = matchList.stream().filter(teamWin -> (teamWin.getHomeTeam().equals(teamInMatch) && teamWin.getHomeTeamScore() < teamWin.getAwayTeamScore()) ||
                    (teamWin.getAwayTeam().equals(teamInMatch) && teamWin.getAwayTeamScore() < teamWin.getHomeTeamScore())
            ).count();

            long draw = matchList.stream().filter(teamDraw ->
                    (teamDraw.getHomeTeam().equals(teamInMatch) || teamDraw.getAwayTeam().equals(teamInMatch)) &&
                            (teamDraw.getHomeTeamScore() == teamDraw.getAwayTeamScore())).count();

            long goals_scored = matchList.stream().filter(teamGoals -> teamGoals.getHomeTeam().equals(teamInMatch)).mapToInt(Match::getHomeTeamScore).sum() +
                    matchList.stream().filter(teamGoals -> teamGoals.getAwayTeam().equals(teamInMatch)).mapToInt(Match::getAwayTeamScore).sum();

            long points = ((win * 3) + draw);

            return TeamMatchRankingDto.builder()
                    .team(teamInMatch)
                    .totalMatches((win + loss + draw))
                    .win(win)
                    .goals_scored(goals_scored)
                    .points(points)
                    .build();

        }).toList();
        return matchRanking.stream().filter(team ->
                team.getTotalMatches() > 0 || team.getGoals_scored() > 0 || team.getWin() > 0 || team.getPoints() > 0)
                .sorted(Comparator.comparingLong(TeamMatchRankingDto::getTotalMatches).reversed()
                        .thenComparingLong(TeamMatchRankingDto::getWin).reversed()
                        .thenComparingLong(TeamMatchRankingDto::getGoals_scored).reversed()
                        .thenComparingLong(TeamMatchRankingDto::getPoints).reversed()
                ).toList();
    }

    @Override
    public TeamStatsDto teamStats(long id) {
        Optional<Team> teamOptional = teamRepository.findById(id);

        if(teamOptional.isEmpty()){
            throw new IllegalArgumentException("Team not found.");
        }

        Team team = teamOptional.get();
        List<Match> matches = matchRepository.findAll();

        int wins = (int) matches.stream().filter(match ->
                (match.getHomeTeam().equals(team) && match.getHomeTeamScore() > match.getAwayTeamScore()) ||
                        (match.getAwayTeam().equals(team) && match.getAwayTeamScore() > match.getHomeTeamScore())
        ).count();

        int loss = (int) matches.stream().filter(match ->
                (match.getHomeTeam().equals(team) && match.getHomeTeamScore() < match.getAwayTeamScore() ||
                        (match.getAwayTeam().equals(team) && match.getAwayTeamScore() < match.getHomeTeamScore()))
        ).count();

        int draw = (int) matches.stream().filter(match ->
                (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team) &&
                        (match.getAwayTeamScore() == match.getHomeTeamScore()))
        ).count();

        int goals_scored = matches.stream().filter(match ->
                match.getHomeTeam().equals(team)).mapToInt(Match::getHomeTeamScore).sum() +
                matches.stream().filter(match ->
                        match.getAwayTeam().equals(team)).mapToInt(Match::getAwayTeamScore).sum();

        int goals_against = matches.stream().filter(match ->
                match.getHomeTeam().equals(team)).mapToInt(Match::getAwayTeamScore).sum() +
                matches.stream().filter(match ->
                        match.getAwayTeam().equals(team)).mapToInt(Match::getHomeTeamScore).sum();

        return TeamStatsDto.builder()
                .team(team)
                .win(wins)
                .loss(loss)
                .draw(draw)
                .goals_scored(goals_scored)
                .goals_against(goals_against)
                .build();
    }
}