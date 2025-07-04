package com.ddaaniel.armchair_management.integrationTests;

import com.ddaaniel.armchair_management.controller.SeatController;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.RequestAllocationDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import com.ddaaniel.armchair_management.utilsObjects.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ControllerIntegrationTestAllContext {

    private final MockMvc mockMvc;
    private final SeatController seatController;
    private final ISeatRepository seatRepository;
    private final IPersonRepository personRepository;

    @Autowired
    public ControllerIntegrationTestAllContext(MockMvc mockMvc, SeatController seatController, ISeatRepository seatRepository, IPersonRepository personRepository) {
        this.mockMvc = mockMvc;
        this.seatController = seatController;
        this.seatRepository = seatRepository;
        this.personRepository = personRepository;
    }

    @BeforeEach
    void setupTestData() {
        for (int i = 1; i <= 15; i++) {
            Seat seat = new Seat();
            seat.setPosition(i);

            // Para simular algumas poltronas ocupadas
            if (i % 5 == 0) { // Ex: posições 5, 10, 15 estarão ocupadas
                Person person = Person.builder()
                        .name("Pessoa " + i)
                        .cpf(String.format("%011d", i)) // CPF com 11 dígitos, baseado na posição
                        .build();

                personRepository.save(person);

                seat.setFree(false);
                seat.setPerson(person);
            } else {
                seat.setFree(true);
                seat.setPerson(null);
            }

            seatRepository.save(seat);
        }
    }

    @AfterEach
    void cleanUp() {
        List<Seat> seats = seatRepository.findAll();
        for (Seat seat : seats) {
            seat.setPerson(null);
        }
        seatRepository.saveAll(seats);
        seatRepository.deleteAll();
        personRepository.deleteAll();
    }


    @Test
    void getAllStatusPoltronas_ShouldReturnAllSeats() throws Exception {
        // Act & Assert using MockMvc
        mockMvc.perform(get("/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(15)); // Assumindo que há 15 Seats nos dados de Teste.
    }

    @Test
    void getAllStatusPoltronas_DirectControllerCall_ShouldReturnCorrectData() {
        // Act
        var response = seatController.getAllStatusPoltronas();
        List<SeatResponseDTO> seats = response.getBody();

        // Assert
        assertEquals(15, seats.size()); // Verify number of seats
        assertEquals(1, seats.get(0).position()); // Verify first seat position
        assertTrue(seats.get(0).free()); // Verify first seat status
    }

    @Test
    public void getBySeat_ShouldReturnCorrectSeat() throws Exception {
        mockMvc.perform(get("/seats/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value(8))
                .andExpect(jsonPath("$.free").value(true)) // Alterado para true
                .andExpect(jsonPath("$.occupant").isEmpty());
    }

    @Test
    public void getBySeat_InvalidPosition_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/seats/999"))
                .andExpect(status().isBadRequest()) // Alterado para isBadRequest()
                .andExpect(jsonPath("$.error").value("Assento Inválido"))
                .andExpect(jsonPath("$.message").value("O assento informado é inválido."));
    }

    @Test
    void addPersonToSeat_ShouldReturnSuccess () throws Exception {

        // ARRANGE
        var randomPosition = Utils.randomIntegerWithRange15();
        var randomName = Utils.randomNameString();
        var randomCPF = Utils.randomCpf();
        RequestAllocationDTO dto = new RequestAllocationDTO(randomPosition, randomName, randomCPF);


        // ACT / ASSERT
        mockMvc.perform(put("/seats/allocate")
                .content(Utils.asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("Poltrona alocada com sucesso."));
    }

    @Test
    void addPersonToSeat_ShouldReturnBadRequest () throws Exception {

        // ARRANGE
        String nonValidName = null;
        var randomPosition = Utils.randomIntegerWithRange15();
        var randomCPF = Utils.randomCpf();
        RequestAllocationDTO dto = new RequestAllocationDTO(randomPosition, nonValidName, randomCPF);

        // ACT / ASSERT
        mockMvc.perform(put("/seats/allocate")
                .content(Utils.asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("O nome deve ter no máximo 50 caracteres."));
    }
}