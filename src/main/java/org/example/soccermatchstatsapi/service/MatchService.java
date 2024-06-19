package org.example.soccermatchstatsapi.service;

import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.dto.MatchDto;
import org.example.soccermatchstatsapi.dto.MatchPageableDto;
import org.example.soccermatchstatsapi.interfaces.MatchInterface;
import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.repository.MatchRepository;
import org.example.soccermatchstatsapi.repository.StadiumRepository;
import org.example.soccermatchstatsapi.repository.TeamRepository;
import org.example.soccermatchstatsapi.specification.MatchSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MatchService implements MatchInterface {
    private MatchRepository matchRepository;
    private TeamRepository teamRepository;
    private StadiumRepository stadiumRepository;
    @Override
    public void createMatch(Match match) {
        if(
                match.getHomeTeam() != null &&
                match.getAwayTeam() != null &&
                match.getMatchDate() != null &&
                match.getStadium() != null &&
                match.getHomeTeamScore() != null &&
                match.getAwayTeamScore() != null
        ){
            if(match.getHomeTeam().equals(match.getAwayTeam())){
                throw new IllegalArgumentException("away and home team cannot be the same.");
            }
            if(match.getMatchDate().isAfter(OffsetDateTime.now())){
                throw new IllegalArgumentException("Date cannot be beyond the current date");
            }
            if(match.getHomeTeamScore() < 0 || match.getAwayTeamScore() < 0){
                throw new IllegalArgumentException("Team score cannot be negative.");
            }

            try {
                teamExists(match.getAwayTeam().getId());
                teamExists(match.getHomeTeam().getId());
                teamIsActive(match.getHomeTeam().getId());
                teamIsActive(match.getAwayTeam().getId());
                stadiumExists(match.getStadium().getId());
                matchDateIsBeforeTeamCreationDateWhenCreate(match);
                matchDateHasConflict(match);
                if(match.getMatchDate() != null && match.getStadium() != null){
                    verifyStadiumHasMatch(match);
                }
            }catch (IllegalArgumentException e){
                throw e;
            }
            matchRepository.save(match);

        }else{
            throw new IllegalArgumentException("Required data is missing.");
        }
    }

    @Override
    public void updateMatch(long id, Match match) {
        String errorMessageTeam = "Home team and away team cannot be the same";
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match matchToUpdate = matchOptional.get();

            if(match.getHomeTeam() != null && match.getAwayTeam() != null) {
                if (match.getHomeTeam().equals(match.getAwayTeam())) {
                    throw new IllegalArgumentException(errorMessageTeam);
                }
                try {
                    teamExists(match.getHomeTeam().getId());
                    teamIsActive(match.getHomeTeam().getId());

                    teamExists(match.getAwayTeam().getId());
                    teamIsActive(match.getAwayTeam().getId());

                    verifyIfBothTeamsAreTheSame(match);
                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException("Team: " + e.getMessage());
                }
            }
            if (match.getHomeTeam() != null && match.getAwayTeam() == null) {
                try {
                    teamExists(match.getHomeTeam().getId());
                    teamIsActive(match.getAwayTeam().getId());
                    verifyIfBothTeamsAreTheSame(match);
                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException("Home team: " + e.getMessage());
                }
            }
            if (match.getAwayTeam() != null && match.getHomeTeam() == null) {
                try {
                    teamExists(match.getAwayTeam().getId());
                    teamIsActive(match.getAwayTeam().getId());
                    verifyIfBothTeamsAreTheSame(match);
                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException("Away team: " + e.getMessage());
                }
            }
            if(match.getStadium()!= null){
                try {
                    stadiumExists(match.getStadium().getId());
                    verifyStadiumHasMatch(match);
                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            if (match.getHomeTeamScore() != null && match.getHomeTeamScore() < 0 ||
                    match.getAwayTeamScore() != null && match.getAwayTeamScore() < 0) {
                throw new IllegalArgumentException("Scores cannot be negative.");
            }

            if(match.getMatchDate() != null && match.getMatchDate().isAfter(OffsetDateTime.now())){
                throw new IllegalArgumentException("Match dates cannot be after current day.");
            }
            if(match.getMatchDate() != null){
                try {
                    matchDateHasConflict(match);
                    matchDateIsBeforeTeamCreationDateWhenUpdate(match);

                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
           matchToUpdate.setMatchDate(match.getMatchDate() != null ? match.getMatchDate() : matchToUpdate.getMatchDate());
           matchToUpdate.setHomeTeam(match.getHomeTeam() != null ? match.getHomeTeam() : matchToUpdate.getHomeTeam());
           matchToUpdate.setAwayTeam(match.getAwayTeam() != null ? match.getAwayTeam() : matchToUpdate.getAwayTeam());
           matchToUpdate.setHomeTeamScore(match.getHomeTeamScore() != null ? match.getHomeTeamScore() : matchToUpdate.getHomeTeamScore());
           matchToUpdate.setAwayTeamScore(match.getAwayTeamScore() != null? match.getAwayTeamScore() : matchToUpdate.getAwayTeamScore());
           matchToUpdate.setStadium(match.getStadium() != null ? match.getStadium() : matchToUpdate.getStadium());

           matchRepository.save(matchToUpdate);
        }else{
            throw new IllegalArgumentException("Match not found.");
        }
    }
    private void matchDateIsBeforeTeamCreationDateWhenUpdate(Match match){
        Optional<Match> matchOptional = matchRepository.findById(match.getId());
        if(matchOptional.isPresent()){
            boolean dateIsBeforeTheTeamCreationDate1 =
                    (match.getHomeTeam() != null ? match.getMatchDate().toLocalDate().isBefore(match.getHomeTeam().getCreationDate())
                            : match.getMatchDate().toLocalDate().isBefore(matchOptional.get().getHomeTeam().getCreationDate())) ||
                            (match.getAwayTeam() != null ? match.getMatchDate().toLocalDate().isBefore(match.getAwayTeam().getCreationDate())
                                    : match.getMatchDate().toLocalDate().isBefore(matchOptional.get().getAwayTeam().getCreationDate()));
            if(dateIsBeforeTheTeamCreationDate1){
                throw new IllegalArgumentException("Match date cannot be before team creation date.");
            }
        }
    }private void matchDateIsBeforeTeamCreationDateWhenCreate(Match match){
        boolean dateIsBeforeTheTeamCreationDate1 =
                (match.getMatchDate().toLocalDate().isBefore(match.getHomeTeam().getCreationDate()) ||
                        (match.getMatchDate().toLocalDate().isBefore(match.getAwayTeam().getCreationDate())));
        if(dateIsBeforeTheTeamCreationDate1){
            throw new IllegalArgumentException("Match date cannot be before team creation date.");
        }
    }
    private void matchDateHasConflict(Match match){
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        Optional<Match> dbMatch = matchRepository.findById(match.getId());
        if(dbMatch.isPresent()){
            if (homeTeam == null) {
                homeTeam = dbMatch.get().getHomeTeam();
            } else {
                homeTeam = match.getHomeTeam();
            }
            if (awayTeam == null) {
                awayTeam = dbMatch.get().getAwayTeam();
            }
        }

        Team finalHomeTeam = homeTeam;
        Team finalAwayTeam = awayTeam;

        boolean someTeamPlayedTheMatch = matchRepository.findAll().stream()
                .filter(dt -> dt.getId() != match.getId())
                .anyMatch(m ->
                        (m.getHomeTeam().equals(finalHomeTeam) || m.getAwayTeam().equals(finalHomeTeam) ||
                                m.getHomeTeam().equals(finalAwayTeam) || m.getAwayTeam().equals(finalAwayTeam)) &&
                                m.getMatchDate().isAfter(match.getMatchDate().minusHours(48)) &&
                                m.getMatchDate().isBefore(match.getMatchDate().plusHours(48))
                );
        if(someTeamPlayedTheMatch){
            throw new IllegalArgumentException("Theres hour conflict at the new match date.");
        }
    }
    private void teamExists(long id){
        boolean team = teamRepository.existsById(id);
        if(!team){
            throw new IllegalArgumentException("The team doesnt exist");
        }
    }
    private void teamIsActive(long id){
        Optional<Team> teamIsActive = teamRepository.findById(id);
        if(teamIsActive.isPresent() && !teamIsActive.get().isActive()){
            throw new IllegalArgumentException("Its not possible to use inactive teams");
        }
    }
    private void verifyIfBothTeamsAreTheSame(Match match){
        boolean verifyTeamInTheMatch = matchRepository.findById(match.getId()).stream()
                .anyMatch(m ->
                        (match.getHomeTeam() != null && m.getAwayTeam().equals(match.getHomeTeam())) ||
                        (match.getAwayTeam() != null && m.getHomeTeam().equals(match.getAwayTeam())));

        if(verifyTeamInTheMatch){
            throw new IllegalArgumentException("The home team and away team cannot be the same in a match.");
        }
    }
    private void verifyStadiumHasMatch(Match match) {
        List<Match> matchOptional = matchRepository.findAll();
        boolean stadiumHasMatchInthisDate = matchOptional.stream()
                .anyMatch(dt -> (dt.getStadium().equals(match.getStadium())) && dt.getMatchDate().toLocalDate().isEqual(match.getMatchDate().toLocalDate()));
        if (stadiumHasMatchInthisDate) {
            throw new IllegalArgumentException("Has game at this stadium on this date.");
        }
    }
    private void stadiumExists(long id){
        boolean stadiumExists = stadiumRepository.existsById(id);
        if(!stadiumExists){
            throw new IllegalArgumentException("Stadium doesnt exists.");
        }
    }

    @Override
    public void deleteMatch(long id) {
        Optional<Match> match = matchRepository.findById(id);
        if (!match.isPresent()) {
            throw new IllegalArgumentException("Match not found");
        }
        matchRepository.deleteById(id);
    }

    @Override
    public Match getMatchById(long id) {
        Optional<Match> match = matchRepository.findById(id);
        if (!match.isPresent()) {
            throw new IllegalArgumentException("Match not found");
        }
        return match.get();
    }

    @Override
    public MatchPageableDto getMatchesWithFilter(String team, String stadium, Pageable pageable) {
        Specification<Match> matchSpecification = Specification.where(null);
        if(team != null) {
            matchSpecification = matchSpecification.and(MatchSpecifications.hasTeam(team));
        } if(stadium != null) {
            matchSpecification = matchSpecification.and(MatchSpecifications.hasStadium(stadium));
        }
        Page<Match> page = matchRepository.findAll(matchSpecification, pageable);

        List<MatchDto> listMatch = page.map(this::mapDto).toList();

        return MatchPageableDto.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .content(listMatch)
                .build();
    }

    private MatchDto mapDto(Match match){
        return MatchDto.builder()
                .id(match.getId())
                .homeTeam(match.getHomeTeam())
                .awayTeam(match.getAwayTeam())
                .matchDate(match.getMatchDate())
                .homeTeamScore(match.getHomeTeamScore())
                .awayTeamScore(match.getAwayTeamScore())
                .stadium(match.getStadium())
                .build();
    }
}