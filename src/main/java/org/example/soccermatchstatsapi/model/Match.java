package org.example.soccermatchstatsapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "`match`")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @NotNull(message = "The match date is required")
    @PastOrPresent(message = "The date must be played at the past or present.")
    private OffsetDateTime matchDate;

    @NotNull
    private Integer homeTeamScore;

    @NotNull
    private Integer awayTeamScore;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;
}
