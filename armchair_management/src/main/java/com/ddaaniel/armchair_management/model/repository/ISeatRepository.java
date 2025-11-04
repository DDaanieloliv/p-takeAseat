package com.ddaaniel.armchair_management.model.repository;

import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.RowOccupacyDTO;
import com.ddaaniel.armchair_management.model.record.RowOccupacyDTOooo;
import com.ddaaniel.armchair_management.model.record.RowOccupacyProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISeatRepository extends JpaRepository<Seat, UUID> {


  @Query(value =
    """
    SELECT
      tb_seats.seat_row as fileira,
      COUNT(tb_seats.seat_row) as totalAssentos,
      COUNT( CASE WHEN status = 'AVAILABLE' AND free = true THEN 1 END ) as assentosLivres,
      ROUND(
        (COUNT( CASE WHEN free = false THEN 1 END ) * 100.0 / COUNT(*)), 2
      ) as taxaOcupacaoPercentual
    FROM tb_seats
    WHERE tb_seats.grid_id = ?1
    GROUP BY tb_seats.seat_row
    ORDER BY tb_seats.seat_row;
    """,
  nativeQuery = true)
  List<RowOccupacyProjection> getOccupacyByRow(UUID gridId);

  void deleteById(UUID seatIdToDelete);

  @Query(value = """
    SELECT * FROM tb_seats
    WHERE tb_seats.grid_id = ?1
    ORDER BY tb_seats.seat_row ASC, tb_seats.seat_column ASC;
    """,
    nativeQuery = true)
  List<Seat> findSeatsByGridId(UUID grid_uuid);


  @Query(value = "SELECT * FROM tb_seats WHERE tb_seats.seat_row = ?1 AND tb_seats.seat_column = ?2;", nativeQuery = true)
  Optional<Seat> findByPosition(Integer row, Integer column);

  @Query(value = "SELECT COUNT(*) FROM tb_seats WHERE tb_seats.free = false;", nativeQuery = true)
  Integer countSeatsOccupied();

  @Query(value = "SELECT COUNT(*) FROM tb_seats WHERE tb_seats.free = true;", nativeQuery = true)
  Integer countSeatsUnoccupied();

  @Query(value = "SELECT COUNT(*) FROM tb_seats;", nativeQuery = true)
  Integer countAllSeats();

  @Query(value = "SELECT * FROM tb_seats WHERE seat_column = ?1 AND seat_row = ?2;", nativeQuery = true)
  Optional<Seat> getSeatByColumnAndRow(Integer column, Integer row);


  @Query(value = "SELECT * FROM tb_seats WHERE grid_id = ?1;", nativeQuery = true)
  List<Seat> findSeatsByGrid(UUID uuid);
}
