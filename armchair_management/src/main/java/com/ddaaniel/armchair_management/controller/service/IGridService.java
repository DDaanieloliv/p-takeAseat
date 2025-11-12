package com.ddaaniel.armchair_management.controller.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.record.GridDTO;
import com.ddaaniel.armchair_management.model.record.GridEntityDTO;

public interface IGridService {

  GridEntityDTO currentGridEntity();

  GridDTO currentGrid();

  Optional<GridEntity> findGridEntityById(UUID uuid);

  void updateCurrentGrid(GridEntityDTO entity);

  List<GridEntityDTO> gridList();
}
