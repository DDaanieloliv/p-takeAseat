package com.ddaaniel.armchair_management.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_grid")
public class GridEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "gridid")
  private UUID grid;

  @Builder.Default
  @Column(name = "row_number")
  private Integer rowNumber = 10;

  @Builder.Default
  @Column(name = "column_number")
  private Integer columnNumber = 24;

  @Builder.Default
  @Column(name = "is_initial")
  private Boolean isInitialGrid = true;

  @Builder.Default
  @Column(name = "is_currentgrid")
  private Boolean is_currentGrid = true;

  @OneToMany(mappedBy = "currentGrid") // <- O relacionamento está mapeado pelo campo currentGrid em Seat
  private List<Seat> seatList;
}

