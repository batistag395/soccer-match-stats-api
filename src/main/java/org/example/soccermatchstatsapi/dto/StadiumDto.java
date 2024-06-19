package org.example.soccermatchstatsapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StadiumDto {
    private long id;
    private String name;
}
