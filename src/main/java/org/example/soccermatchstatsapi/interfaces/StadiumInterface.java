package org.example.soccermatchstatsapi.interfaces;


import org.example.soccermatchstatsapi.model.Stadium;

import java.util.List;

public interface StadiumInterface {
    void createStadium(Stadium stadium);
    void updateStadium(long id, Stadium stadium);
    void deleteStadium(long id);
    Stadium findStadiumById(long id);
    List<Stadium> findAllStadiums();
}
