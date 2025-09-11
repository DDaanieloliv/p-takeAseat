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
  @Column(name = "rowNumber")
  private Integer rowNumber = 10;

  @Builder.Default
  @Column(name = "columnNumber")
  private Integer columnNumber = 24;

  @Builder.Default
  @Column(name = "isinitial")
  private Boolean isInitialGrid = true;

  @OneToMany(mappedBy = "seat")
  private List<Seat> seatList;
}

