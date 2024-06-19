package org.example.soccermatchstatsapi.dto;

import lombok.Builder;
import lombok.Data;
import org.example.soccermatchstatsapi.model.Match;

import java.util.List;

@Data
@Builder
public class TeamMatchHistoryStatsDto {
    private List<Match> matchList;
    private TeamStatsDto team1;
    private TeamStatsDto team2;
}
