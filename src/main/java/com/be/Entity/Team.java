package com.be.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String color;

    @JsonManagedReference
    @OneToMany(mappedBy = "teamVictim")
    private List<Device> victims;

    @JsonManagedReference
    @OneToMany(mappedBy = "teamRescue")
    private List<Device> rescuer;
}
