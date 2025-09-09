package com.ddaaniel.armchair_management.controller.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddaaniel.armchair_management.controller.service.IGridService;
import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.GridDTO;
import com.ddaaniel.armchair_management.model.repository.IGridRepository;

@Service
public class ServiceGridImpl implements IGridService {

  private final IGridRepository gridRepository;

  @Autowired
  public ServiceGridImpl (IGridRepository gridRepository) {
    this.gridRepository = gridRepository;
  }

  @Override
  public GridDTO currentGrid() {
    var initialEntity = gridRepository.initialGrid();
    var rows = initialEntity.getRows();
    var columns = initialEntity.getColumns();

    ArrayList<ArrayList<Seat>> array = new ArrayList<>();


    GridDTO dto = new GridDTO();
    return dto;
  }
}
