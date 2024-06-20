package org.example.soccermatchstatsapi.specification;

import org.example.soccermatchstatsapi.model.Match;
import org.springframework.data.jpa.domain.Specification;

public class MatchSpecifications {
    public static Specification<Match> hasTeam(String teamName) {
        return(root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("homeTeam").get("name")),  "%" + teamName.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("awayTeam").get("name")),  "%" + teamName.toLowerCase() + "%")
            );
    }
    public static Specification<Match> hasStadium(String team) {
        return(root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("stadium").get("name"), team.trim().toLowerCase());
    }
}

