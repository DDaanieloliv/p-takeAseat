package com.ddaaniel.armchair_management.controller.service.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddaaniel.armchair_management.controller.service.IGridService;
import com.ddaaniel.armchair_management.controller.service.ISeatService;
import com.ddaaniel.armchair_management.model.GridEntity;
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
  public ArrayList<ArrayList<Seat>> currentGrid() {
    return null;
  }



  List<List<Seat>> grid = new ArrayList<>();
  int position = 0;

  public List<List<Seat>> generateGrid() {
    var initialEntity = gridRepository.initialGrid();
    var rows = initialEntity.getRowNumber();
    var columns = initialEntity.getColumnNumber();

    for (int rowCount = 1; rowCount <= rows; rowCount++) {

      ArrayList<Seat> rowList = new ArrayList<>();
      grid.add(generateColumns(rowCount, columns, rowList));
    }

    return grid;
  }

  private List<Seat> generateColumns(int rowCount, int columns, List<Seat> rowList){
    for (int c = 0; c <= columns; c++) {

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


  public GridDTO pushToGridDTO() {
    return null;
  }

}
