package com.ddaaniel.armchair_management.controller.service;

import com.ddaaniel.armchair_management.model.record.SeatDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.record.ShartsResponceDTO;

import java.util.List;

public interface ISeatService {

  List<SeatResponseDTO> listStatusOfAllSeats();

  SeatResponseDTO detailsFromSpecificSeat (Integer position);

  void allocateSeatToPessoa(Integer position, String name, String cpf);

  ShartsResponceDTO sharts();

  void updateModifiedSeats(List<SeatDTO> seatListDTO);
}
