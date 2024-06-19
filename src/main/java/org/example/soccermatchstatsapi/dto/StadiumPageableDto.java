package org.example.soccermatchstatsapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StadiumPageableDto {
    private long totalPages;
    private long totalElements;
    private List<StadiumDto> content;
}
