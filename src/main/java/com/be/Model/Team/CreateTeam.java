package com.be.Model.Team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTeam {
    private Long id;
    private String name;
    private List<Long> members;
    private List<Long> victims;
    private String color;
}
