package com.be.Model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class UpdateUser {
    private Long id;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private double temperature;
    private int humidity;
    private int light;
    private double body_temperature;
    private double heart_rate;

}
