package com.ddaaniel.armchair_management.model.record;

import java.util.List;


public record SeatsUpdatedDTO(
  GridEntityDTO entity,
  List<SeatDTO> grid
) {
}
