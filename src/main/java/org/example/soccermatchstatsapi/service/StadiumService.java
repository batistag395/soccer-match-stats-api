package org.example.soccermatchstatsapi.service;

import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.interfaces.StadiumInterface;
import org.example.soccermatchstatsapi.model.Stadium;
import org.example.soccermatchstatsapi.repository.StadiumRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class StadiumService implements StadiumInterface {
    private StadiumRepository stadiumRepository;
    @Override
    public void createStadium(Stadium stadium) {
        System.out.println("service" + stadium);
        if(stadium.getName() != null) {
            if(stadium.getName().length() < 3) {
                throw new IllegalArgumentException("Stadium name cannot be less than 3 characters");
            }
            Optional<Stadium> optionalStadium = stadiumRepository.findByNameIgnoreCase(stadium.getName());
            if(optionalStadium.isPresent()) {
                throw new IllegalArgumentException("Stadium already exists");
            }
            stadiumRepository.save(stadium);
        }else {
            throw new IllegalArgumentException("Stadium name cannot be null");
        }
    }
    @Override
    public void updateStadium(long id, Stadium stadium) {
        Optional<Stadium> existingStadium = stadiumRepository.findById(id);
        if(existingStadium.isPresent()) {
            if(stadium.getName() != null || !stadium.getName().isEmpty()) {
                if(stadium.getName().length() < 3) {
                    throw new IllegalArgumentException("Stadium name cannot be less than 3 characters");
                }
                Optional<Stadium> optionalStadium = stadiumRepository.findByNameIgnoreCase(stadium.getName());
                if(optionalStadium.isPresent()) {
                    throw new IllegalArgumentException("Stadium already exists with the same name.");
                }
                existingStadium.get().setName(stadium.getName());
                stadiumRepository.save(existingStadium.get());
            }else{
                throw new IllegalArgumentException("Stadium name cannot be null or empty");
            }
        }else{
            throw new IllegalArgumentException("Stadium does not exist");
        }
    }

    @Override
    public Stadium findStadiumById(long id) {
        Optional<Stadium> optionalStadium = stadiumRepository.findById(id);
        if(optionalStadium.isPresent()) {
            return optionalStadium.get();
        }else{
            throw new IllegalArgumentException("Stadium not found");
        }
    }

    @Override
    public List<Stadium> findAllStadiums() {
        return stadiumRepository.findAll();
    }

    @Override
    public void deleteStadium(long id){
        Optional<Stadium> optionalStadium = stadiumRepository.findById(id);
        if(optionalStadium.isPresent()){
            stadiumRepository.deleteById(id);
        }else{
            throw new IllegalArgumentException("Stadium doesnt exist.");
        }
    }
}
