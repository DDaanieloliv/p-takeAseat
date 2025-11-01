package com.ddaaniel.armchair_management.controller.service;

import com.ddaaniel.armchair_management.model.record.SeatDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.record.ChartsResponceDTO;

import java.util.List;
import java.util.UUID;

public interface ISeatService {

  List<SeatResponseDTO> listStatusOfAllSeats();

  SeatResponseDTO detailsFromSpecificSeat (Integer row, Integer column);

  void allocateSeatToPessoa(Integer row, Integer column, String name, String cpf);

  ChartsResponceDTO charts();

  void updateModifiedSeats(List<List<SeatDTO>> seatGridDTO, UUID gridId);

  void eraseAllSeatsState(UUID uuid);
}
