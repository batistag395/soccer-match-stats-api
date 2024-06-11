package org.example.soccermatchstatsapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.OffsetDateTime;

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
    @Size(min = 2, max = 2, message = "The state field required a max length of 100.")
    @Column(unique = true)
    private String state;

    @NotBlank(message = "The creation date field is required.")
    @PastOrPresent(message = "The creation date must be in the past or present.")
    private OffsetDateTime creationDate;
}
