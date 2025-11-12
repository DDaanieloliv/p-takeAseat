package com.ddaaniel.armchair_management.controller;

import com.ddaaniel.armchair_management.controller.service.IGridService;
import com.ddaaniel.armchair_management.controller.service.IPersonService;
import com.ddaaniel.armchair_management.controller.service.ISeatService;
import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.record.ChartsResponceDTO;
import com.ddaaniel.armchair_management.model.record.GridDTO;
import com.ddaaniel.armchair_management.model.record.GridEntityDTO;
import com.ddaaniel.armchair_management.model.record.MessageResponseDTO;
import com.ddaaniel.armchair_management.model.record.RequestAllocationDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.record.SeatsUpdatedDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/seats")
public class SeatController {

  private final ISeatService serviceSeat;
  private final IPersonService servicePessoa;
  private final IGridService gridService;

  @Autowired
  public SeatController(ServiceSeatImpl serviceSeat, ServicePersonImpl servicePessoa, IGridService gridService) {
    this.serviceSeat = serviceSeat;
    this.servicePessoa = servicePessoa;
    this.gridService = gridService;
  }

  @GetMapping
  public ResponseEntity<List<SeatResponseDTO>> getAllStatusPoltronas() {
    var response = serviceSeat.listStatusOfAllSeats();
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/{row}-{column}")
  public ResponseEntity<SeatResponseDTO> getBySeat(@PathVariable Integer row, @PathVariable Integer column) {
    var response = serviceSeat.detailsFromSpecificSeat(row, column);
    return ResponseEntity.ok().body(response);
  }

  @PutMapping("/allocate")
  public ResponseEntity<MessageResponseDTO> addPersonToSeat(@RequestBody RequestAllocationDTO dto) {
    serviceSeat.allocateSeatToPessoa(dto.row(), dto.column(), dto.name(), dto.cpf());

    MessageResponseDTO message = new MessageResponseDTO("Poltrona alocada com sucesso.");
    return ResponseEntity.ok(message);
  }

  @PutMapping("/remove/{row}-{column}")
  public ResponseEntity<MessageResponseDTO> removePersonFromSeat(@PathVariable Integer row, @PathVariable Integer column) {
    servicePessoa.removePessoaFromSeat(row, column);

    MessageResponseDTO message = new MessageResponseDTO("Pessoa removida da Poltrona.");
    return ResponseEntity.ok(message);
  }

  @GetMapping("/charts/{gridId}")
  public ResponseEntity<ChartsResponceDTO> charts(@PathVariable UUID gridId) {
    return ResponseEntity.ok(serviceSeat.charts(gridId));
  }

  @GetMapping("/grid")
  public ResponseEntity<GridDTO> GridResponseEntity() {
    return ResponseEntity.ok(gridService.currentGrid());
  }

  @GetMapping("/gridEntity")
  public ResponseEntity<GridEntityDTO> CurrentGridEntity() {
    return ResponseEntity.ok(gridService.currentGridEntity());
  }


  @PutMapping("/grid/update")
  public ResponseEntity<GridDTO> UpdateGrid(@RequestBody GridDTO gridUpdatedDTO) {
    gridService.updateCurrentGrid(gridUpdatedDTO.entity());
    serviceSeat.updateModifiedSeats(gridUpdatedDTO.grid(), gridUpdatedDTO.entity().getGrid());
    return ResponseEntity.ok(gridUpdatedDTO);
  }


  @PutMapping("/grid/erase")
  public ResponseEntity<?> EraseGridState(@RequestBody GridEntityDTO entityGrid) {
    var gridOpt = gridService.findGridEntityById(entityGrid.getGrid());
    if (gridOpt.isPresent()) {
        serviceSeat.eraseAllSeatsState(entityGrid.getGrid());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Estados dos assentos foram limpos...");
        response.put("status", "SUCCESS");

        return ResponseEntity.ok(response);
    }

    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", "Nenhuma Entidade Grid relacionada foi encontrada...");
    errorResponse.put("status", "NOT_FOUND");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }


  @GetMapping("/grid/list")
  public ResponseEntity<List<GridEntityDTO>> GridList(){
    return ResponseEntity.ok(gridService.gridList());
  }



  @PostMapping("/grid/newroom")
  public ResponseEntity<?> CreateNewRoomWithSeatGrid() {
    return null;
  }

}
