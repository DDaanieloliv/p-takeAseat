package com.ddaaniel.armchair_management.model.record;

import com.ddaaniel.armchair_management.model.enums.SeatType;

public record ShortSeatDTO(
    String position,
    Integer row,
    Integer column,
    Boolean free,
    SeatType type,
    GridEntityDTO gridEntity) {
}
