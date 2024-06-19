package org.example.soccermatchstatsapi.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.dto.*;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.service.TeamService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/team")
public class TeamController {
    private TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity createTeam(@Valid @RequestBody Team team) {
        try {
            teamService.createTeam(team);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (IllegalArgumentException e){
            if(e.getMessage().equals("Team already exists.")){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity updateTeam(@Valid @PathVariable long id, @Valid @RequestBody TeamDto team) {
        try {
            teamService.updateTeam(id, team);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (IllegalArgumentException e){
            if (e.getMessage().equals("Team name must be at least 2 characters.") ||
                e.getMessage().equals("Team state doesnt belong to Brazil or doesnt exist..") ||
                    e.getMessage().equals("Creation date is incorrect, because is on the future.")
            ){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else if (
                    e.getMessage().equals("Creation date is incorrect, because is after a match date of the team..") ||
                    e.getMessage().equals("Team already exists.")
            ) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable long id) {
        try {
            return ResponseEntity.ok(teamService.getTeamById(id));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/findAll")
    public ResponseEntity<List<Team>> getAllTeams() {
        try {
            return ResponseEntity.ok(teamService.getAllTeams());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/listWithFilters")
    public ResponseEntity<TeamPageableDto> listTeamsWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable
        ){
        TeamPageableDto teamsReturned =  teamService.getTeamsByNameAndStateAndStatusActive(name, state, isActive, pageable);
        return ResponseEntity.ok(teamsReturned);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteTeam(@PathVariable long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/teamStats/{id}")
    public ResponseEntity<TeamStatsDto> TeamStats(@Valid @PathVariable long id){
        try {
            return ResponseEntity.ok(teamService.teamStats(id));
        }catch (IllegalArgumentException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/teamMatchHistory/{id}")
    public ResponseEntity<List<TeamMatchHistoryDto>> teamMatchHistory(@Valid @PathVariable long id){
        try {
            return ResponseEntity.ok(teamService.teamMatchHistory(id));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/teamMatchHistoryStats")
    public ResponseEntity<TeamMatchHistoryStatsDto> teamMatchHistoryStats(@RequestParam long id_team_1, @RequestParam long id_team_2){
        try {
            return ResponseEntity.ok(teamService.findMatchHistoryBetweenTeams(id_team_1, id_team_2));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/matchRanking")
    public ResponseEntity<List<TeamMatchRankingDto>> teamMatchRanking(){
        return ResponseEntity.ok(teamService.teamMatchRanking());
    }
}
