package com.ddaaniel.armchair_management.model.record;

import java.util.Optional;

import com.ddaaniel.armchair_management.model.enums.SeatType;

public record SeatResponseDTO(String position,
                              boolean free,
                              Integer row,
                              Integer column,
                              SeatType type,
                              Optional<PersonDTO> occupant) {

    public record PersonDTO(String name,
                            String cpf) {
    }

}


