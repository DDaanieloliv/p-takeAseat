package com.ddaaniel.armchair_management.model;

import com.ddaaniel.armchair_management.model.enums.SeatType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Random;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_seats")
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "seatid")
  private UUID seatID;

  @Column(name = "position")
  private Integer position;

  @Column(name = "row")
  private Integer row;

  @Column(name = "column")
  private Integer column;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(name = "status")
  private SeatType status =  SeatType.AVAILABLE;

  @Builder.Default
  @Column(name = "free")
  private Boolean free = true;

  @JoinColumn(name = "person_id", unique = true) // <- Chave estrangeira.
  @JsonIgnoreProperties({ "seat" })
  @OneToOne
  @Column(name = "person")
  private Person person;

  @JoinColumn(name = "grid_id")
  @ManyToOne
  @Column(name = "currentgrid")
  private GridEntity currentGrid;

  @Override
  public String toString() {
    return "Seat{id=" + seatID + ", position=" + position + ", free=" + free + "}";
  }


}
