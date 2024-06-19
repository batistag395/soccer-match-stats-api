package org.example.soccermatchstatsapi.dto;

import lombok.Builder;
import lombok.Data;
import org.example.soccermatchstatsapi.model.Team;
@Data
@Builder
public class TeamMatchHistoryDto {
    private Team team;
    private Team opponent;
    private long win;
    private long loss;
    private long draw;
    private int goals_scored;
    private int goals_against;
}
