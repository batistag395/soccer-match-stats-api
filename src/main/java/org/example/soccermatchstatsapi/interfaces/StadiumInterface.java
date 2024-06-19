package org.example.soccermatchstatsapi.interfaces;


import org.example.soccermatchstatsapi.dto.StadiumPageableDto;
import org.example.soccermatchstatsapi.model.Stadium;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StadiumInterface {
    void createStadium(Stadium stadium);
    void updateStadium(long id, Stadium stadium);
    void deleteStadium(long id);
    Stadium findStadiumById(long id);
    StadiumPageableDto findAllStadiums(Pageable pageable);
}
