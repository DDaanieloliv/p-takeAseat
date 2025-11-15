package com.ddaaniel.armchair_management.controller.service.mapper;

import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.GridEntityDTO;
import com.ddaaniel.armchair_management.model.record.SeatDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.record.ShortSeatDTO;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SeatMapper {

  public SeatResponseDTO toDTO(Seat seat) {
    return new SeatResponseDTO(
        seat.getPosition(),
        seat.getFree(),
        seat.getRow(),
        seat.getColumn(),
        seat.getStatus(),
        Optional.ofNullable(seat.getPerson())
            .map(person -> new SeatResponseDTO.PersonDTO(person.getName(), person.getCpf())));
  }


  public List<ShortSeatDTO> toShortDTOList(List<Seat> listSeats) {
    List<ShortSeatDTO> list = listSeats.stream()
        .map(seat -> new ShortSeatDTO(
            seat.getPosition(),
            seat.getRow(),
            seat.getColumn(),
            seat.getFree(),
            seat.getStatus(),
            gridEntityConverter(seat.getCurrentGrid())))
        .toList();

    return list;
  }

  public GridEntityDTO gridEntityConverter(GridEntity entity) {
    return GridEntityDTO.builder()
        .grid(entity.getGrid())
        .rowNumber(entity.getRowNumber())
        .columnNumber(entity.getColumnNumber())
        .is_currentGrid(entity.getIs_currentGrid()).build();
  }



  public List<SeatResponseDTO> toDTOList(List<Seat> seats) {
    return seats.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  public static Seat seatDtoToEntity(SeatDTO dto) {
    return Seat.builder()
        .position(dto.getPosition())
        .row(dto.getRow())
        .column(dto.getColumn())
        .status(dto.getStatus())
        .free(dto.getFree())
        // .currentGrid(dto.getGrid())
        // .person(dto.getPerson()) Should be a Optional maybe(because we don't have
        // sure that response come with the 'person' propertie not null)
        .build();
  }

}
