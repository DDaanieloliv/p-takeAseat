package com.ddaaniel.armchair_management.model.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ddaaniel.armchair_management.model.GridEntity;

@Repository
public interface IGridRepository extends JpaRepository<GridEntity, UUID> {

  @Query(value = "SELECT * FROM tb_grid WHERE isinitial = true;")
  Optional<GridEntity> initialGrid();
}
