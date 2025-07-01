package com.ddaaniel.armchair_management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Profile("!test") // Só executa quando NÃO estiver no perfil "test"
@Configuration
public class Config implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se a tabela está vazia para evitar duplicações em reinicializações
        Integer totalSeats = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_seats", Integer.class);

        if (totalSeats == 0) {
            // Insere todos os 15 assentos de uma vez usando batch update
            jdbcTemplate.batchUpdate(
                    "INSERT INTO tb_seats (seatid, position, free) VALUES (gen_random_uuid(), ?, true)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, i + 1); // Posições de 1 a 15
                        }

                        @Override
                        public int getBatchSize() {
                            return 15;
                        }
                    }
            );
        }
    }
}
