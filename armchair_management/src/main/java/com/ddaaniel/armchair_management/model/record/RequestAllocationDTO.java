package com.ddaaniel.armchair_management.model.record;

public record RequestAllocationDTO(Integer row,
                                   Integer column,
                                   String name,
                                   String cpf) {
}
