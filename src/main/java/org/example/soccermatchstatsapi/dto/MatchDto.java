package org.example.soccermatchstatsapi.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import org.example.soccermatchstatsapi.model.Stadium;
import org.example.soccermatchstatsapi.model.Team;

import java.time.OffsetDateTime;

@Data
@Builder
public class MatchDto {

    private long id;
    private Team homeTeam;
    private Team awayTeam;
    private OffsetDateTime matchDate;
    private Integer homeTeamScore;
    private Integer awayTeamScore;
    private Stadium stadium;
}
