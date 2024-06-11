package org.example.soccermatchstatsapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The stadium name field is requiered.")
    @Size(min = 3, message = "The stadium name field required a min length of 3.")
    @Column(unique = true, nullable = false)
    private String name;
}
