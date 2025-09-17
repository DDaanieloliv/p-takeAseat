package com.ddaaniel.armchair_management.model.record;

import com.ddaaniel.armchair_management.model.enums.SeatType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SeatDTO {

    private Integer position;
    private Integer row;
    private Integer column;
    private SeatType type;
    private Boolean free;


}
