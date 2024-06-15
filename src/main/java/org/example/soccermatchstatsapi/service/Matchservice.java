package org.example.soccermatchstatsapi.service;

import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.interfaces.MatchInterface;
import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.model.Stadium;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.repository.MatchRepository;
import org.example.soccermatchstatsapi.repository.StadiumRepository;
import org.example.soccermatchstatsapi.repository.TeamRepository;
import org.example.soccermatchstatsapi.specification.MatchSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class Matchservice implements MatchInterface {
    private MatchRepository matchRepository;
    private TeamRepository teamRepository;
    private StadiumRepository stadiumRepository;
    @Override
    public void createMatch(Match match) {

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
                    verifyStadiumHasMatch(id, match.getStadium(), match.getMatchDate());
                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            if(match.getHomeTeamScore() < 0 || match.getAwayTeamScore() < 0){
                throw  new IllegalArgumentException("Scores cannot be negative.");
            }
            if(match.getMatchDate() != null && match.getMatchDate().isAfter(OffsetDateTime.now())){
                throw new IllegalArgumentException("Match dates cannot be after current day.");
            }
            if(match.getMatchDate() != null){
                try {
                    matchDateHasConflict(match);
                    matchDateIsBeforeTeamCreationDate(match);

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
    private void matchDateIsBeforeTeamCreationDate(Match match){
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

    }
    private void matchDateHasConflict(Match match){
        boolean someTeamPlayedTheMatch = matchRepository.findAll().stream()
                .filter(dt -> dt.getId() != match.getId())
                .anyMatch(m ->
                        ((match.getAwayTeam() != null && m.getHomeTeam().equals(match.getAwayTeam())) ||
                        (match.getHomeTeam() != null && m.getAwayTeam().equals(match.getHomeTeam())) ||
                        (match.getHomeTeam() != null && m.getHomeTeam().equals(match.getHomeTeam())) ||
                        (match.getAwayTeam() != null && m.getAwayTeam().equals(match.getAwayTeam()))) &&
                        (m.getMatchDate().isAfter(match.getMatchDate().minusHours(48)) &&
                                m.getMatchDate().isBefore(match.getMatchDate().plusHours(48)))
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
    private void verifyStadiumHasMatch(long id, Stadium stadium, OffsetDateTime matchDate){
        Optional<Match> match = matchRepository.findById(id);
        boolean stadiumHasMatchInthisDate = match.stream()
                .anyMatch(dt -> dt.getStadium().equals(stadium ) && dt.getMatchDate().toLocalDate().isEqual(matchDate.toLocalDate()));
        if(!stadiumHasMatchInthisDate){
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
    public List<Match> getMatchesWithFilter(String team, String stadium) {
        Specification<Match> matchSpecification = Specification.where(null);
        if(team != null) {
            matchSpecification = matchSpecification.and(MatchSpecifications.hasTeam(team));
        } if(stadium != null) {
            matchSpecification = matchSpecification.and(MatchSpecifications.hasStadium(stadium));
        }
        return matchRepository.findAll(matchSpecification);
    }
}