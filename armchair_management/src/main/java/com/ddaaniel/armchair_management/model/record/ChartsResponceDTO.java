package com.ddaaniel.armchair_management.model.record;

import java.util.Map;

public record ChartsResponceDTO (
  Float percentOccupiedFloat,
  Integer seatsUnoccupied,
  Map<String, Long> occupacyByRow) {

}
