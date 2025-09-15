package com.ddaaniel.armchair_management.controller.service.mapper;

import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
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
                Optional.ofNullable(seat.getPerson())
                        .map(person -> new SeatResponseDTO.PersonDTO(person.getName(), person.getCpf()))
        );
    }

    public List<SeatResponseDTO> toDTOList(List<Seat> seats) {
        return seats.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

