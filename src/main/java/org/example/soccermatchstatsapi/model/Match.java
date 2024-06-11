package org.example.soccermatchstatsapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @NotNull(message = "The match date is required")
    @PastOrPresent(message = "The date must be played at the past or present.")
    private OffsetDateTime matchDate;

    @NotNull
    private int homeTeamScore;

    @NotNull
    private int awayTeamScore;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium localStadium;
}
