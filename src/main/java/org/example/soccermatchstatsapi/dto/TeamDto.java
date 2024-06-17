package org.example.soccermatchstatsapi.dto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TeamDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(max = 100, message = "The name field required a max length of 100.")
    private String name;

    @Size(min = 2, max = 2, message = "The state field requires a length of 2.")
    private String state;

    @PastOrPresent(message = "The creation date must be in the past or present.")
    private LocalDate creationDate;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isActive = true;
}
