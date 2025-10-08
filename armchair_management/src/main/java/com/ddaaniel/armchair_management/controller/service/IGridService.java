package com.ddaaniel.armchair_management.controller.service;

import java.util.Optional;
import java.util.UUID;

import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.record.GridDTO;

public interface IGridService {

  GridDTO currentGrid();

  Optional<GridEntity> findGridEntityById(UUID uuid);
}
