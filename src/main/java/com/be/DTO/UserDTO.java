package com.be.DTO;

import com.be.Const.UserRole;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class UserDTO {
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

    private UserRole role;
    private String password;
    private Date last_update;

    private TeamDTO teamRescue;

    private TeamDTO teamVictim;
}
