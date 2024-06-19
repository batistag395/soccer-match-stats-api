package org.example.soccermatchstatsapi.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soccermatchstatsapi.dto.MatchPageableDto;
import org.example.soccermatchstatsapi.dto.TeamStatsDto;
import org.example.soccermatchstatsapi.model.Match;
import org.example.soccermatchstatsapi.service.MatchService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchController {
    private MatchService matchService;
    @PostMapping("/create")
    public ResponseEntity createMatch(@Valid @RequestBody Match match){
        try{
            matchService.createMatch(match);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (IllegalArgumentException e){
            log.info(e.getMessage());
            if(
                    e.getMessage().equals("Required data is missing.") ||
                    e.getMessage().equals("away and home team cannot be the same.") ||
                    e.getMessage().equals("The team doesnt exist") ||
                    e.getMessage().equals("Stadium doesnt exists.") ||
                    e.getMessage().equals("Team score cannot be negative.") ||
                    e.getMessage().equals("Date cannot be beyond the current date")
            ){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity updateMatch(@Valid @PathVariable long id, @RequestBody Match match){
        try {
            matchService.updateMatch(id, match);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (IllegalArgumentException e){
            if(e.getMessage().equals("Match not found.")){
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (
                    e.getMessage().equals("Home team and away team cannot be the same") ||
                    e.getMessage().equals("The team doesnt exist") ||
                    e.getMessage().equals("Stadium doesnt exists.") ||
                    e.getMessage().equals("Scores cannot be negative.") ||
                    e.getMessage().equals("Match dates cannot be after current day.")

            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteMatch(@Valid @PathVariable long id){
        try {
            matchService.deleteMatch(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (IllegalArgumentException e){
            log.info(e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@Valid @PathVariable long id){
        try {
            return ResponseEntity.ok(matchService.getMatchById(id));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/listWithFilters")
    public ResponseEntity<MatchPageableDto> getMatchByFilter(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String stadium,
            @RequestParam(required = false) Boolean blowout,
            Pageable pageable
    ){
        MatchPageableDto match = matchService.getMatchesWithFilter(team, stadium, blowout, pageable);
        return ResponseEntity.ok(match);
    }
}
