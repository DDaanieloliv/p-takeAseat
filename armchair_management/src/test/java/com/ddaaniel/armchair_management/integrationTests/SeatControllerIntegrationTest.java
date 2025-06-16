package com.ddaaniel.armchair_management.integrationTests;

import com.ddaaniel.armchair_management.controller.SeatController;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.utilsObjects.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SeatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SeatController seatController;


    @BeforeEach
    void setUp() {
        // Initialize test data if needed
        Utils.setUpDatabase();
    }

    @Test
    void getAllStatusPoltronas_ShouldReturnAllSeats() throws Exception {
        // Act & Assert using MockMvc
        mockMvc.perform(get("/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(15)); // Assuming 15 seats in test data
    }

    @Test
    void getAllStatusPoltronas_DirectControllerCall_ShouldReturnCorrectData() {
        // Act
        var response = seatController.getAllStatusPoltronas();
        List<SeatResponseDTO> seats = response.getBody();

        // Assert
        assertEquals(15, seats.size()); // Verify number of seats
        assertEquals(1, seats.get(0).position()); // Verify first seat position
        assertEquals(true, seats.get(0).free()); // Verify first seat status
    }

    @Test
    public void getBySeat_ShouldReturnCorrectSeat() throws Exception {
        mockMvc.perform(get("/seats/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value(2))
                .andExpect(jsonPath("$.free").value(true)) // Alterado para true
                .andExpect(jsonPath("$.occupant").isEmpty());
    }

    @Test
    public void getBySeat_InvalidPosition_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/seats/999"))
                .andExpect(status().isBadRequest()) // Alterado para isBadRequest()
                .andExpect(jsonPath("$.error").value("Assento Inválido"))
                .andExpect(jsonPath("$.message").value("O assento informado é inválido."));
    }}