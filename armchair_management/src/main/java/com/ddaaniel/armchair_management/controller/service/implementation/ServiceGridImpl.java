package com.ddaaniel.armchair_management.controller.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddaaniel.armchair_management.controller.exception.InitialGridNotFoundException;
import com.ddaaniel.armchair_management.controller.service.IGridService;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.GridDTO;
import com.ddaaniel.armchair_management.model.repository.IGridRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;

@Service
public class ServiceGridImpl implements IGridService {

  private final IGridRepository gridRepository;
  private final ISeatRepository seatRepository;

  @Autowired
  public ServiceGridImpl(IGridRepository gridRepository, ISeatRepository seatRepository) {
    this.gridRepository = gridRepository;
    this.seatRepository = seatRepository;
  }


  @Override
  public GridDTO currentGrid() {
    List<List<Seat>> currentGrid = generateGrid();
    var gridID = gridRepository.initialGrid().orElseThrow(() -> new InitialGridNotFoundException("Grid inicial não encontrado."));
    var allSeats = seatRepository.findSeatsByGridId(gridID.getGrid());

    for (List<Seat> seatList: currentGrid) {
      parseSeatList(seatList, allSeats);
    }
    return pushToGridDTO(currentGrid);
  }


  private void parseSeatList(List<Seat> seatList, List<Seat> allSeats){
    for (Seat seat : seatList) {
      for (Seat mirror: allSeats) {
        if (
        seat.getPosition() == mirror.getPosition() &&
        seat.getRow() == mirror.getRow() &&
        seat.getColumn() == mirror.getColumn()) {

          // seat.setPosition(mirror.getPosition());
          // seat.setRow(mirror.getRow());
          // seat.setColumn(mirror.getColumn());
          seat.setSeatID(mirror.getSeatID());
          seat.setStatus(mirror.getStatus());
          seat.setFree(mirror.getFree());
          seat.setPerson(mirror.getPerson());
          seat.setCurrentGrid(mirror.getCurrentGrid());
          break;
        }
      }
    }
    // return seatList;
  }



  private List<List<Seat>> generateGrid() {
    List<List<Seat>> grid = new ArrayList<>();
    int position = 0;

    var initialEntity = gridRepository.initialGrid();
    var rows = initialEntity.get().getRowNumber();
    var columns = initialEntity.get().getColumnNumber();

    for (int rowCount = 1; rowCount <= rows; rowCount++) {

      ArrayList<Seat> rowList = new ArrayList<>();
      grid.add(generateColumns(rowCount, columns, rowList, position));
      position = position + columns;
    }

    return grid;
  }

  private List<Seat> generateColumns(int rowCount, int columns, List<Seat> rowList, int position){
    for (int c = 1; c <= columns; c++) {

     rowList.add(Seat.builder()
        .position(position + 1)
        .row(rowCount)
        .column(c)
        .build()
      );
      position++;
    }
    return rowList;
  }


  public GridDTO pushToGridDTO(List<List<Seat>> grid) {
    // List<List<Seat>> seatList2D = new ArrayList<>();
    // for (List<Seat> l : grid) {
    //   seatList2D.add(l);
    // }
    // GridDTO dto = new GridDTO(seatList2D);

    GridDTO dto = new GridDTO(grid);
    return dto;
  }

}
