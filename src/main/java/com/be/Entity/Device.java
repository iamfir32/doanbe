package com.be.Entity;

import com.be.Const.UserRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;
    private String avatar;
    private Integer age;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double temperature;
    private Integer humidity;
    private Integer light;
    private Double body_temperature;
    private Double heart_rate;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Date last_update;

    @JsonBackReference
    @ManyToOne(cascade = {CascadeType.ALL})
    private Team teamRescue;

    @JsonBackReference
    @ManyToOne(cascade = {CascadeType.ALL})
    private Team teamVictim;
}
