package com.ddaaniel.armchair_management.integrationTests;

import com.ddaaniel.armchair_management.controller.SeatController;
import com.ddaaniel.armchair_management.controller.exception.AssentoInvalidoException;
import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.controller.exception.ValidationException;
import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.RequestAllocationDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import com.ddaaniel.armchair_management.fakerObjects.Utils;
import io.restassured.http.ContentType;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import io.restassured.RestAssured;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("testContainer")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestContainerPostgresSQL {

	@LocalServerPort
	private Integer port;

	static PostgreSQLContainer<?> postgresUnderTest = new PostgreSQLContainer<>("postgres:17-alpine")
	/*
	 * .withDatabaseName("testdb")
	 * .withUsername("test")
	 * .withPassword("test")
	 */;

	@BeforeAll
	static void beforeAll() {
		postgresUnderTest.start();
	}

	@AfterAll
	static void afterAll() {
		postgresUnderTest.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		System.out.println("✅ JDBC URL: " + postgresUnderTest.getJdbcUrl());
		registry.add("spring.datasource.url", postgresUnderTest::getJdbcUrl);
		registry.add("spring.datasource.username", postgresUnderTest::getUsername);
		registry.add("spring.datasource.password", postgresUnderTest::getPassword);
	}

	@BeforeEach
	void setUp() {
		// RestAssured.baseURI = "http://localhost:" + port;

		for (int i = 1; i <= 15; i++) {
			Seat seat = new Seat();
			seat.setPosition(i);

			// Para simular algumas poltronas ocupadas
			if (i % 5 == 0) { // Ex: posições 5, 10, 15 estarão ocupadas
				Person person = Person.builder()
						.name("Pessoa " + i)
						.cpf(String.format("%011d", i)) // CPF com 11 dígitos, baseado na posição
						.build();
				// Person person = new Person();
				// person.setName("Pessoa " + i);
				// person.setCpf(String.format("%011d", i));

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

	private final MockMvc mockMvc;
	private final SeatController seatController;
	private final IPersonRepository personRepository;
	private final ISeatRepository seatRepository;
	private final ServicePersonImpl servicePerson;
	private final ServiceSeatImpl serviceSeat;

	@Autowired
	public TestContainerPostgresSQL(MockMvc mockMvc, SeatController seatController, IPersonRepository personRepository,
			ISeatRepository seatRepository, ServicePersonImpl servicePerson, ServiceSeatImpl serviceSeat) {
		this.mockMvc = mockMvc;
		this.seatController = seatController;
		this.personRepository = personRepository;
		this.seatRepository = seatRepository;
		this.servicePerson = servicePerson;
		this.serviceSeat = serviceSeat;
	}


	private final Logger logger = LoggerFactory.getLogger(TestContainerPostgresSQL.class);

	private final String BASE_URI_CONTROLLER = "/seats";

	@Nested
	class TestsContainerRestAssure {

		@Test
		void getAllStatusPoltronas_Successfully() {

			// ARRANGE / ACT
			RestAssured.baseURI = "http://localhost:" + port;

			// ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.when()
					.get(BASE_URI_CONTROLLER)
					.then()
					.log().all()
					.statusCode(200)
					.body("size()", Matchers.equalTo(15));
		}

		@Test
		void getBySeat_ShouldReturnTheCorrectSeatByPosition() {

			// ARRANGE
			RestAssured.baseURI = "http://localhost:" + port;
			var IntegerPosition = 5;
			var seatExpected = Utils.moveToDTO(seatRepository.findByPosition(IntegerPosition));

			// ACT / ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.when()
					.get(BASE_URI_CONTROLLER + "/{position}", IntegerPosition)
					.then()
					.log().all()
					.statusCode(200)
					.body(Matchers.equalTo(Utils.asJsonString(seatExpected)));
		}

		@Test
		void addPersonToSeat_ShouldAllocateSeatSuccessfully() {

			// ARRANGE
			RestAssured.baseURI = "http://localhost:" + port;
			var IntegerPosition = 2;
			var fakerObject = new RequestAllocationDTO(
					IntegerPosition,
					Utils.randomNameString(),
					Utils.randomCpf());

			// ACT / ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(Utils.asJsonString(fakerObject))
					.when()
					.put(BASE_URI_CONTROLLER + "/allocate")
					.then()
					.log().all()
					.statusCode(200)
					.body("message", Matchers.equalTo("Poltrona alocada com sucesso."));
		}

		@Test
		void removePersonFromSeat_ShouldDeletePersonFromSeatSuccessfully() {

			// ARRANGE
			RestAssured.baseURI = "http://localhost:" + port;
			var IntegerPosition = 5;

			// ACT / ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.when()
					.put(BASE_URI_CONTROLLER + "/remove/{position}", IntegerPosition)
					.then()
					.log().all()
					.statusCode(200)
					.body("message", Matchers.equalTo("Pessoa removida da Poltrona."));
		}

	}


@Nested
	class getBySeat_ShouldThrowException_RestAssured{

		@Test
		void shouldThrowInvalidPosition_detailsFromSpecificSeat() {

			// ARRANGE
			RestAssured.baseURI = "http://localhost:" + port;
			var InvalidIntegerPosition = 999;

			// ACT / ASSERT
			RestAssured.when()
					.get(BASE_URI_CONTROLLER + "/{position}", InvalidIntegerPosition)
					.then()
					.log().all()
					.statusCode(400);
			Assertions.assertThatExceptionOfType(AssentoInvalidoException.class)
					.isThrownBy(() -> serviceSeat.detailsFromSpecificSeat(InvalidIntegerPosition))
					.withMessage("O assento informado é inválido.");

		}

		@Test
		void shouldThrowNotFoundException_detailsFromSpecificSeat() {

			// ARRANGE
			RestAssured.baseURI = "http://localhost:" + port;
			var ValidIntegerPosition = 1;
			var SeatIdToDelete = seatRepository.findByPosition(ValidIntegerPosition);
			var IdSeat = SeatIdToDelete.get().getSeatID();

			logger.info("ARRANGE phase done with success!");

			seatRepository.deleteById(IdSeat);

			// ACT / ASSERT
			RestAssured.when()
					.get(BASE_URI_CONTROLLER + "/{position}", ValidIntegerPosition)
					.then()
					.log().all()
					.statusCode(404);
			Assertions.assertThatExceptionOfType(NotFoundException.class)
					.isThrownBy(() -> serviceSeat.detailsFromSpecificSeat(ValidIntegerPosition))
					.withMessage("Poltrona não encontrada.");

		}
	}

@Nested
	class addPersonToSeat_ShouldThrowException_RestAssured {

		@Test
		void shouldThrowValidationException_toLenghName_allocateSeatToPessoa() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			String invalidName = "Aquiireidigitarumnomecommaisde50caracteresparaverificarseefeitootratamentoquantoaisso.";
			Integer validPosotion = 1;
			String validCpf = "12345678910";

			RequestAllocationDTO dto = new RequestAllocationDTO(validPosotion, invalidName, validCpf);

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured.given()
				.contentType(ContentType.JSON)
				.body(Utils.asJsonString(dto))
				.when()
				.put(BASE_URI_CONTROLLER + "/allocate")
				.then()
				.log().all()
				.statusCode(400);
			Assertions.assertThatExceptionOfType(ValidationException.class)
				.isThrownBy(() -> serviceSeat.allocateSeatToPessoa(validPosotion, invalidName, validCpf))
				.withMessage("O nome deve ter no máximo 50 caracteres.");

		}

		@Test
		void shouldThrowValidationException_toCpt_allocateSeatToPessoa() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			String validName = Utils.randomNameString();
			Integer validPosotion = 1;
			String invalidCpf = "1234567891011";

			RequestAllocationDTO dto = new RequestAllocationDTO(validPosotion, validName, invalidCpf);

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.body(Utils.asJsonString(dto))
					.when()
					.put(BASE_URI_CONTROLLER + "/allocate")
					.then()
					.log().all()
					.statusCode(400);
			Assertions.assertThatExceptionOfType(ValidationException.class)
					.isThrownBy(() -> serviceSeat.allocateSeatToPessoa(validPosotion, validName, invalidCpf))
					.withMessage("O CPF deve conter exatamente 11 dígitos numéricos.");

		}

		@Test
		void shouldThrowAssentoInvalidoException_toPosition_allocateSeatToPessoa() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			String validName = Utils.randomNameString();
			Integer invalidPosotion = 17;
			String validCpf = "12345678910";

			RequestAllocationDTO dto = new RequestAllocationDTO(invalidPosotion, validName, validCpf);

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.body(Utils.asJsonString(dto))
					.when()
					.put(BASE_URI_CONTROLLER + "/allocate")
					.then()
					.log().all()
					.statusCode(400);
			Assertions.assertThatExceptionOfType(AssentoInvalidoException.class)
					.isThrownBy(() -> serviceSeat.allocateSeatToPessoa(invalidPosotion, validName, validCpf))
					.withMessage("O assento informado é inválido.");

		}

		@Test
		void shouldThrowNotFoundException_whenSearchToPositionInBD_allocateSeatToPessoa() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			String validName = Utils.randomNameString();
			Integer validPositionNotPersisted = 1;
			String validCpf = "12345678910";

			RequestAllocationDTO dto = new RequestAllocationDTO(validPositionNotPersisted, validName, validCpf);

			var SeatIdToDelete = seatRepository.findByPosition(validPositionNotPersisted);
			var IdSeat = SeatIdToDelete.get().getSeatID();
			seatRepository.deleteById(IdSeat);

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.body(Utils.asJsonString(dto))
					.when()
					.put(BASE_URI_CONTROLLER + "/allocate")
					.then()
					.log().all()
					.statusCode(404);
			Assertions.assertThatExceptionOfType(NotFoundException.class)
					.isThrownBy(() -> serviceSeat.allocateSeatToPessoa(validPositionNotPersisted, validName, validCpf))
					.withMessage("Poltrona não encontrada.");

		}

		@Test
		void shouldThrowBadRequestException_whenSearchToOccupiedPositionInBD_allocateSeatToPessoa() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			String validName = Utils.randomNameString();
			Integer validPositionAlreadyOccupide = 5;
			String validCpf = "12345678910";

			RequestAllocationDTO dto = new RequestAllocationDTO(validPositionAlreadyOccupide, validName, validCpf);

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured.given()
					.contentType(ContentType.JSON)
					.body(Utils.asJsonString(dto))
					.when()
					.put(BASE_URI_CONTROLLER + "/allocate")
					.then()
					.log().all()
					.statusCode(400);
			Assertions.assertThatExceptionOfType(BadRequestException.class)
					.isThrownBy(() -> serviceSeat.allocateSeatToPessoa(validPositionAlreadyOccupide, validName, validCpf))
					.withMessage("Poltrona já está ocupada.");
		}
	}

@Nested
	class removePersonFromSeat_ShouldThrowException_RestAssured {

		@Test
		void shouldThrowAssentoInvalidoException_toPosition_removePessoaFromSeat() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			Integer invalidPosition = 17;

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured
					.when()
					.put(BASE_URI_CONTROLLER + "/remove/{position}", invalidPosition)
					.then()
					.log().all()
					.statusCode(400);
			Assertions.assertThatExceptionOfType(AssentoInvalidoException.class)
					.isThrownBy(() -> servicePerson.removePessoaFromSeat(invalidPosition))
					.withMessage("O assento informado é inválido.");

		}

		@Test
		void shouldThrowNotFoundException_toPositionDeletedFromDB_removePessoaFromSeat() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			Integer validPositionDeleteFromDB = 1;

			var SeatIdToDelete = seatRepository.findByPosition(validPositionDeleteFromDB);
			var IdSeat = SeatIdToDelete.get().getSeatID();

			seatRepository.deleteById(IdSeat);

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured
					.when()
					.put(BASE_URI_CONTROLLER + "/remove/{position}", validPositionDeleteFromDB)
					.then()
					.log().all()
					.statusCode(404);
			Assertions.assertThatExceptionOfType(NotFoundException.class)
					.isThrownBy(() -> servicePerson.removePessoaFromSeat(validPositionDeleteFromDB))
					.withMessage("Poltrona não encontrada.");
		}

		@Test
		void shouldThrowBadRequestException_toPositionAlreadyOccupeid_removePessoaFromSeat() {

			// ARRANGER
			RestAssured.baseURI = "http://localhost:" + port;
			Integer validPositionAlreadyOccupeid = 1;

			logger.info("ARRANGE phase done with success!");

			// ACT / ASSERT
			RestAssured
					.when()
					.put(BASE_URI_CONTROLLER + "/remove/{position}", validPositionAlreadyOccupeid)
					.then()
					.log().all()
					.statusCode(400);
			Assertions.assertThatExceptionOfType(BadRequestException.class)
					.isThrownBy(() -> servicePerson.removePessoaFromSeat(validPositionAlreadyOccupeid))
					.withMessage("A Poltrona já está desocupada.");
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
		void removePersonFromSeat_ShouldReturnSuccess() throws Exception {

			// ARRANGE
			var occupiedPosition = 5;

			// ACT / ASSERT
			mockMvc.perform(put("/seats/remove/{position}", occupiedPosition)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.message").value("Pessoa removida da Poltrona."));
		}

		@Test
		void removePersonFromSeat_ShouldReturnBadRequest() throws Exception {

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
