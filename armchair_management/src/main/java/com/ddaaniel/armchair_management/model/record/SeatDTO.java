package com.ddaaniel.armchair_management.model.record;

import com.ddaaniel.armchair_management.model.enums.SeatType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SeatDTO {

    private String position;
    private Integer row;
    @Builder.Default
    private Boolean selected = false;
    private Integer column;
    private SeatType type;
    private Boolean free;


}
