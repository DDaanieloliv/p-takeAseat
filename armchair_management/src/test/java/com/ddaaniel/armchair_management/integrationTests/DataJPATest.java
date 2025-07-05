package com.ddaaniel.armchair_management.integrationTests;

import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
@DataJpaTest
@ActiveProfiles("test")
@Import(ServicePersonImpl.class)
public class DataJPATest {

    private final ISeatRepository seatRepository;
    private final IPersonRepository personRepository;
    private final TestEntityManager entityManager;
    private final ServicePersonImpl servicePerson;

    @Autowired
    public DataJPATest(ISeatRepository seatRepository, IPersonRepository personRepository, TestEntityManager entityManager, ServicePersonImpl servicePerson) {
        this.seatRepository = seatRepository;
        this.personRepository = personRepository;
        this.entityManager = entityManager;
        this.servicePerson = servicePerson;
    }



    @Nested
    class JpaTestPersistWithEntityManager {

        @Test
        void testPersistWithEntityManager (){

            // Arrange
            Seat seat = new Seat();
            seat.setPosition(1);
            seat.setFree(false);

            Person person = new Person();
            person.setName("Jhon");
            person.setCpf("11111111111");

            // Relacionamento bidirecional
            seat.setPerson(person);
            person.setSeat(seat);

            // Persistência
            entityManager.persist(seat);
            entityManager.persistAndFlush(person); // Persiste e gera ID

            UUID generatedId = person.getPersonID(); // Deve estar preenchido agora

            // ACT
            var response = personRepository.findById(generatedId);

            // Assert
            Assertions.assertTrue(response.isPresent());
            Assertions.assertEquals("Jhon", response.get().getName());
        }

        @Test
        void testDeleteWithEntityManager () {

            // ARRANGE
            Person person = new Person();
            person.setName("Jhon");
            person.setCpf("11111111111");

            Seat seat = new Seat();
            seat.setPosition(1);
            seat.setFree(false);

            seat.setPerson(person);
            person.setSeat(seat);

            entityManager.persist(seat);
            entityManager.persistAndFlush(person);

//            Person person = new Person();
//            person.setName("Jhon");
//            person.setCpf("11111111111");
//
//            entityManager.persistAndFlush(person);
//
//            Seat seat = new Seat();
//            seat.setPosition(1);
//            seat.setFree(false);
//            person.setSeat(seat);
//            seat.setPerson(person);
//
//            entityManager.persist(seat);
//            entityManager.persist(person);
//
//            Assertions.assertEquals(person , personRepository.findAll());
//
//            Both ways the inserts are accepted, but the first is more clean.

            // ACT
            servicePerson.removePessoaFromSeat(seat.getPosition());

            // ASSERT
            var checkResults = seatRepository.findByPosition(seat.getPosition());
            Assertions.assertNull(checkResults.get().getPerson());
            Assertions.assertEquals(Optional.empty() , personRepository.findById(person.getPersonID()));
        }
    }
}
