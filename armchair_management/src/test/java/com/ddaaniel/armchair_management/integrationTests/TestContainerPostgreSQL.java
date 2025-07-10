package com.ddaaniel.armchair_management.integrationTests;

import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import io.restassured.RestAssured;
import static org.hamcrest.Matchers.equalTo;
import java.util.List;

@Testcontainers
@ActiveProfiles("testContainer")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestContainerPostgreSQL {


    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgresUnderTest =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    /*.withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")*/;

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
        RestAssured.baseURI =  "http://localhost:" + port;

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


    private final IPersonRepository personRepository;
    private final ISeatRepository seatRepository;
    private final ServicePersonImpl servicePerson;
    private final ServiceSeatImpl serviceSeat;

    @Autowired
    public TestContainerPostgreSQL(IPersonRepository personRepository, ISeatRepository seatRepository, ServicePersonImpl servicePerson, ServiceSeatImpl serviceSeat) {
        this.personRepository = personRepository;
        this.seatRepository = seatRepository;
        this.servicePerson = servicePerson;
        this.serviceSeat = serviceSeat;
    }

    @Nested
    class TestsContainer {

        @Test
        void getAllStatusSeats () {

            // ARRANGE / ACT
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

}
