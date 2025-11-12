package com.ddaaniel.armchair_management.model.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ddaaniel.armchair_management.model.GridEntity;

@Repository
public interface IGridRepository extends JpaRepository<GridEntity, UUID> {


  @Query(value = "SELECT tb_grid.gridid FROM tb_grid WHERE is_currentgrid = true;", nativeQuery = true)
  Optional<GridEntity> getCurrentGridEntity();

  @Query(value = "SELECT * FROM tb_grid WHERE is_currentgrid = true;", nativeQuery = true)
  Optional<GridEntity> currentGrid();

  @Query(value = "SELECT * FROM tb_grid WHERE gridid = ?1;", nativeQuery = true)
  Optional<GridEntity> findGridEntityById(UUID uuid);
}
