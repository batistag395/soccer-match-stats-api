package org.example.soccermatchstatsapi.specification;

import org.example.soccermatchstatsapi.model.Team;
import org.springframework.data.jpa.domain.Specification;

public class TeamSpecifications {
    public static Specification<Team> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("name"), name.trim().toLowerCase());
    }

    public static Specification<Team> hasState(String state) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), state.trim().toLowerCase());
    }

    public static Specification<Team> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }
}
