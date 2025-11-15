package com.ddaaniel.armchair_management.model.record;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChartsResponceDTO {

  public Float percentOccupied;
  public Integer seatsUnoccupied;
  public List<RowOccupacyDTO> rowOccupacyDTO;
  public List<RoomOccupacyDTO> roomOccupacyDTOs;

}
