package org.example.soccermatchstatsapi.dto;

import lombok.Builder;
import lombok.Data;
import org.example.soccermatchstatsapi.model.Team;

@Data
@Builder
public class TeamMatchRankingDto {
    private Team team;
    private long points;
    private long win;
    private long totalMatches;
    private long goals_scored;
}
