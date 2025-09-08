package com.ddaaniel.armchair_management.model.repository;

import com.ddaaniel.armchair_management.model.Seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISeatRepository extends JpaRepository<Seat, UUID> {


  Optional<Seat> findByPosition(Integer position);


  void deleteById(UUID seatIdToDelete);


  @Query(value = "SELECT COUNT(*) FROM tb_seats WHERE tb_seats.free = false;", nativeQuery = true)
  Integer countSeatsOccupied();

  @Query(value = "SELECT COUNT(*) FROM tb_seats WHERE tb_seats.free = true;", nativeQuery = true)
  Integer countSeatsUnoccupied();

  @Query(value = "SELECT COUNT(*) FROM tb_seats;", nativeQuery = true)
  Integer countAllSeats();

}
