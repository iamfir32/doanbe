package com.be.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamDTO {
    private Long id;

    private String name;
    private String color;

    private List<UserDTO> victims;
    private List<UserDTO> rescuer;
}
