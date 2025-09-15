package com.ddaaniel.armchair_management.model.record;

import java.util.Optional;

public record SeatResponseDTO(Integer position,
                              boolean free,
                              Integer row,
                              Integer column,
                              Optional<PersonDTO> occupant) {

    public record PersonDTO(String name,
                            String cpf) {
    }

}


