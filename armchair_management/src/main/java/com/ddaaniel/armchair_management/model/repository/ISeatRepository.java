package com.ddaaniel.armchair_management.model.repository;

import com.ddaaniel.armchair_management.model.Seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISeatRepository extends JpaRepository<Seat, UUID> {


  Optional<Seat> findByPosition(Integer position);


  void deleteById(UUID seatIdToDelete);

  @Query(value = "SELECT * FORM tb_seats WHERE tb_seats.currentgrid = :gridId;", nativeQuery = true)
  List<Seat> findByGridId(@Param("gridId") UUID grid_uuid);


  @Query(value = "SELECT COUNT(*) FROM tb_seats WHERE tb_seats.free = false;", nativeQuery = true)
  Integer countSeatsOccupied();

  @Query(value = "SELECT COUNT(*) FROM tb_seats WHERE tb_seats.free = true;", nativeQuery = true)
  Integer countSeatsUnoccupied();

  @Query(value = "SELECT COUNT(*) FROM tb_seats;", nativeQuery = true)
  Integer countAllSeats();

}
