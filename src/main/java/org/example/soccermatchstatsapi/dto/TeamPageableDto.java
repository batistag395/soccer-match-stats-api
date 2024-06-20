package org.example.soccermatchstatsapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamPageableDto {
    private long totalPages;
    private long totalElements;
    private List<TeamDto> content;
}
