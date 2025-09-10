package com.ddaaniel.armchair_management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;

import com.ddaaniel.armchair_management.model.enums.SeatType;

import lombok.Getter;
import lombok.Setter;


// @Profile({"dev", "prod"})
@Configuration
@ConfigurationProperties(prefix = "app.seats.initial")
@Getter
@Setter
public class Config {

  private boolean enable = true;

  private Integer rows = 1;

  private Integer columns = 1;

  private SeatType defaultType = SeatType.AVAILABLE;

  private boolean free = true;
}










  // @Override
  // public void run(String... args) throws Exception {
  //
  //   // // Verifica se a tabela está vazia para evitar duplicações em reinicializações
  //   // Integer totalSeats = jdbcTemplate.queryForObject(
  //   //         "SELECT COUNT(*) FROM tb_seats", Integer.class);
  //   //
  //   // if (totalSeats == 0) {
  //   //     // Insere todos os 15 assentos de uma vez usando batch update
  //   //     jdbcTemplate.batchUpdate(
  //   //             "INSERT INTO tb_seats (seatid, position, free) VALUES (gen_random_uuid(), ?, true)",
  //   //             new BatchPreparedStatementSetter() {
  //   //                 @Override
  //   //                 public void setValues(PreparedStatement ps, int i) throws SQLException {
  //   //                     ps.setInt(1, i + 1); // Posições de 1 a 15
  //   //                 }
  //   //
  //   //                 @Override
  //   //                 public int getBatchSize() {
  //   //                     return 15;
  //   //                 }
  //   //             }
  //   //     );
  //   // }
  // }

