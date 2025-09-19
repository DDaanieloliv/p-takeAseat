package com.ddaaniel.armchair_management.model.record;

import java.util.List;


public record GridDTO(
  GridEntityDTO entity,
  List<List<SeatDTO>> grid) {
}
