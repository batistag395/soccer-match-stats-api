package org.example.soccermatchstatsapi.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.soccermatchstatsapi.dto.StadiumPageableDto;
import org.example.soccermatchstatsapi.model.Stadium;
import org.example.soccermatchstatsapi.model.Team;
import org.example.soccermatchstatsapi.service.StadiumService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/stadium")
public class StadiumController {
    private StadiumService stadiumService;

    @PostMapping("/create")
    public ResponseEntity createStadium(@Valid @RequestBody Stadium stadium){
        try {
            System.out.println("controller" + stadium);
            stadiumService.createStadium(stadium);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (IllegalArgumentException e){
            if(e.getMessage().equals("Stadium already exists")){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity updateStadium(@Valid @PathVariable long id, @Valid @RequestBody Stadium stadium){
        try {
            stadiumService.updateStadium(id, stadium);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (IllegalArgumentException e){
            if(e.getMessage().equals("Stadium does not exist")){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }else if(e.getMessage().equals("Stadium already exists with the same name.")){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Stadium> findById(@Valid @PathVariable long id){
        try {
            return ResponseEntity.ok(stadiumService.findStadiumById(id));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/findAll")
    public ResponseEntity<StadiumPageableDto> findAllStadium(Pageable pageable) {
        try {
            StadiumPageableDto stadiumList = stadiumService.findAllStadiums(pageable);
            return ResponseEntity.ok(stadiumList);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteStadium(@Valid @PathVariable long id){
        try {
            stadiumService.deleteStadium(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
