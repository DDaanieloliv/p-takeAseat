package com.ddaaniel.armchair_management.model.record;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GridEntityDTO {

  private UUID grid;
  private Integer rowNumber;
  private Integer columnNumber;
  private Boolean is_currentGrid;

}
