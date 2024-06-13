package org.example.soccermatchstatsapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The name field is required.")
    @Size(max = 100, message = "The name field required a max length of 100.")
    private String name;

    @NotBlank(message = "The state field is required.")
    @Size(min = 2, max = 2, message = "The state field requires a length of 2.")
    private String state;

    @NotBlank(message = "The creation date field is required.")
    @PastOrPresent(message = "The creation date must be in the past or present.")
    private LocalDate creationDate;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isActive = true;
}
