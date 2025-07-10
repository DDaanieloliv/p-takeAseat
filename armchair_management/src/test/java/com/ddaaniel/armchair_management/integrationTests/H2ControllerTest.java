package com.ddaaniel.armchair_management.integrationTests;

import com.ddaaniel.armchair_management.controller.SeatController;
import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.RequestAllocationDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import com.ddaaniel.armchair_management.utilsTestObjects.Utils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



// ##########################################################################
// ##                                                                      ##
// ## Ideal: Esses testes serem executados em ambiente de desnevolvimento, ##
// ## devido dos mesmos estarem na classes TestContainerPostgresSQL.       ##
// ##                                                                      ##
// ##########################################################################
@Disabled("Test class H2ControllerTest disable on build.")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class H2ControllerTest {


    @LocalServerPort
    private Integer port;

    private final MockMvc mockMvc;
    private final ISeatRepository seatRepository;
    private final IPersonRepository personRepository;
    private final SeatController seatController;
    private final ServiceSeatImpl serviceSeat;
    private final ServicePersonImpl servicePerson;

    @Autowired
    public H2ControllerTest(MockMvc mockMvc, SeatController seatController, ISeatRepository seatRepository, IPersonRepository personRepository, ServiceSeatImpl serviceSeat, ServicePersonImpl servicePerson) {
        this.mockMvc = mockMvc;
        this.seatController = seatController;
        this.seatRepository = seatRepository;
        this.personRepository = personRepository;
        this.serviceSeat = serviceSeat;
        this.servicePerson = servicePerson;
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


    @Nested
    class TestsContainer {

        @Test
        void getAllStatusSeats () {

            // ARRANGE / ACT
            RestAssured.baseURI =  "http://localhost:" + port;
            serviceSeat.listStatusOfAllSeats();

            // ASSERT
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/seats")
                    .then()
                    .statusCode(200)
                    .body("size()", Matchers.equalTo(15));
        }
    }

    @Nested
    class GetMapping_getAllStatusPoltronas {

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
    }

    @Nested
    class GetMapping_getBySeat {

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
    }

    @Nested
    class PutMapping_addPersonToSeat {

        @Test
        void addPersonToSeat_ShouldReturnSuccess() throws Exception {

            // ARRANGE
            var randomPosition = 2;
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
        void addPersonToSeat_ShouldReturnBadRequest_Name() throws Exception {

            // ARRANGE
            String nonValidName = null;
            var randomPosition = 3;
            var randomCPF = Utils.randomCpf();
            RequestAllocationDTO dto = new RequestAllocationDTO(randomPosition, nonValidName, randomCPF);

            // ACT / ASSERT
            mockMvc.perform(put("/seats/allocate")
                            .content(Utils.asJsonString(dto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("O nome deve ter no máximo 50 caracteres."));
        }

        @Test
        void addPersonToSeat_ShouldReturnBadRequest_Position() throws Exception {

            // ARRANGE
            var ValidName = Utils.randomNameString();
            var nonValidPosition = 16;
            var randomCPF = Utils.randomCpf();
            RequestAllocationDTO dto = new RequestAllocationDTO(nonValidPosition, ValidName, randomCPF);

            // ACT / ASSERT
            mockMvc.perform(put("/seats/allocate")
                            .content(Utils.asJsonString(dto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("O assento informado é inválido."));
        }

        @Test
        void addPersonToSeat_ShouldReturnBadRequest_Cpf() throws Exception {

            // ARRANGE
            var ValidName = Utils.randomNameString();
            var validPosition = 1;
            var nonValidCPF = "111111111111";
            RequestAllocationDTO dto = new RequestAllocationDTO(validPosition, ValidName, nonValidCPF);

            // ACT / ASSERT
            mockMvc.perform(put("/seats/allocate")
                            .content(Utils.asJsonString(dto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("O CPF deve conter exatamente 11 dígitos numéricos."));
        }
    }

    @Nested
    class PutMapping_removePersonFromSeat {

        @Test
        void removePersonFromSeat_ShouldReturnSuccess () throws Exception {

            // ARRANGE
            var occupiedPosition = 5;

            // ACT / ASSERT
            mockMvc.perform(put("/seats/remove/{position}", occupiedPosition)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Pessoa removida da Poltrona."));
        }


        @Test
        void removePersonFromSeat_ShouldReturnBadRequest () throws Exception {

            // ARRANGE
            var nonValidPosition = 999;

            // ACT / ASSERT
            mockMvc.perform(put("/seats/remove/{position}", nonValidPosition)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("O assento informado é inválido."));
        }
    }
}