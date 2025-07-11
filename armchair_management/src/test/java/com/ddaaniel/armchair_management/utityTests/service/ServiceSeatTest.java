package com.ddaaniel.armchair_management.utityTests.service;

import com.ddaaniel.armchair_management.controller.exception.AssentoInvalidoException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.controller.exception.ValidationException;
import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.controller.service.mapper.SeatMapper;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import com.ddaaniel.armchair_management.fakerObjects.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ServiceSeatTest {


    @Mock
    ISeatRepository seatRepository;

    @Mock
    SeatMapper seatMapper;

    @InjectMocks
    ServiceSeatImpl serviceSeat;

    @Captor
    ArgumentCaptor<List<Seat>> seatsListCaptor;

    @Captor
    ArgumentCaptor<Seat> seatCaptor;




    @Nested
    class listStatusOfAllSeats {

        @Test
        void shouldMappingSeatCorrectly() {

            // ARRANGE
            var createdSeatListEntities = Utils.seatListGenerate();
            var seatListConvertedToDTO = Utils.convertSeatListToDTO(createdSeatListEntities);

            Mockito.doReturn(createdSeatListEntities).when(seatRepository).findAll();
            Mockito.doReturn(seatListConvertedToDTO).when(seatMapper)
                    .toDTOList(seatsListCaptor.capture());

            // ACT
            serviceSeat.listStatusOfAllSeats();

            // ASSERT
            Mockito.verify(seatRepository, Mockito.times(1)).findAll();
            Mockito.verify(seatMapper, Mockito.times(1))
                    .toDTOList(Mockito.eq(createdSeatListEntities));

            Assertions.assertEquals(seatsListCaptor.getValue().get(0).getFree(),
                    seatListConvertedToDTO.get(0).free());
            Assertions.assertEquals(seatsListCaptor.getValue().get(0).getPosition(),
                    seatListConvertedToDTO.get(0).position());
        }
    }

    @Nested
    class detailsFromSpecificSeat {

        @Test
        void shouldCallRightDependencies() {

            // ARRANGE
            var randomPosition = Utils.randomIntegerWithRange15();
            var seatEntityBuilt = Utils.randomlyCreateSeatEntity(randomPosition);
            var convertedToDto = Utils.moveToDTO(seatEntityBuilt);

            Mockito.doReturn(Optional.of(seatEntityBuilt))
                    .when(seatRepository).findByPosition(Mockito.eq(randomPosition));
            Mockito.doReturn(convertedToDto).when(seatMapper).toDTO(Mockito.eq(seatEntityBuilt));

            // ACT
            serviceSeat.detailsFromSpecificSeat(randomPosition);

            // ASSERT
            Mockito.verify(seatRepository, Mockito.times(1))
                    .findByPosition(Mockito.eq(randomPosition));
            Mockito.verify(seatMapper, Mockito.times(1))
                    .toDTO(Mockito.eq(seatEntityBuilt));

        }

        @Test
        void shouldThrowInvalidSeatException () {

            // ARRANGE
            var IntPosition = 16;

            // ACT
            Exception exception = Assertions.assertThrows(AssentoInvalidoException.class,
                    ()-> serviceSeat.detailsFromSpecificSeat(IntPosition/*Mockito.eq(IntPosition)*/));

            // ASSERT
            Assertions.assertEquals("O assento informado é inválido.", exception.getMessage());
        }

        @Test
        void shouldThrowNotFoundException (){

            // ARRANGE
            var IntPosition = 17;
            Mockito.when(seatRepository.findByPosition(IntPosition)).thenReturn(Optional.empty());

            // ACT
            Exception exception = Assertions.assertThrows(NotFoundException.class,
                    ()-> serviceSeat.detailsFromSpecificSeat(IntPosition));

            // ASSERT
            Assertions.assertEquals("Poltrona não encontrada.", exception.getMessage());
        }
    }


    @Nested
    class allocateSeatToPessoa {


        @Test
        void shouldAllocateSeatCorrectly() {

            // ARRANGE
            var randomPosition = Utils.randomIntegerWithRange15();
            var randomName = Utils.randomNameString();
            var randomCpf = Utils.randomCpf();

            var seatCreated = Utils.createSeatWithoutPerson(randomPosition);
            var expectedSeatEntitySaved =
                    Utils.createSeatWithParameterWithPersonID(seatCreated.getSeatID(), randomPosition, randomName, randomCpf);
            var expectedSeatEntityUnSaved =
                    Utils.createSeatWithParameterWithoutPersonID(seatCreated.getSeatID(), randomPosition, randomName, randomCpf);

            Mockito.doReturn(Optional.of(seatCreated))
                    .when(seatRepository).findByPosition(randomPosition);

            Mockito.doReturn(expectedSeatEntitySaved)
                    .when(seatRepository).save(seatCaptor.capture());

            // ACT
            serviceSeat.allocateSeatToPessoa(randomPosition, randomName, randomCpf);

            // ASSERT
            Mockito.verify(seatRepository, Mockito.times(1))
                    .findByPosition(Mockito.eq(randomPosition));
            Mockito.verify(seatRepository, Mockito.times(1))
                    .save(Mockito.eq(expectedSeatEntityUnSaved));

            Assertions.assertEquals(seatCaptor.getValue().getPosition(), seatCreated.getPosition());
            Assertions.assertEquals(seatCaptor.getValue().getFree(), seatCreated.getFree());
            Assertions.assertEquals(seatCaptor.getValue().getPerson(), seatCreated.getPerson());
            Assertions.assertEquals(seatCaptor.getValue().getPerson().getName(), seatCreated.getPerson().getName());
            Assertions.assertEquals(seatCaptor.getValue().getPerson().getCpf(), seatCreated.getPerson().getCpf());

        }

        @Test
        void shouldThrowValidationExceptionInName () {

            // ARRANGE
            String unAcceptableName = null;
            var validCPF = "11111111111";
            var validPosition = 7;

            // ACT
            Exception exception = Assertions.assertThrows(ValidationException.class,
                    ()-> serviceSeat.allocateSeatToPessoa(
                            validPosition , unAcceptableName, validCPF ));

            // ASSERT
            Assertions.assertEquals("O nome deve ter no máximo 50 caracteres.", exception.getMessage());
        }


        @Test
        void shouldThrowValidationExceptionInCPF () {


            // ARRANGE
            String ValidName = "someName";
            var UnValidCPF = "111111111111";
            var validPosition = 7;

            // ACT
            Exception exception = Assertions.assertThrows(ValidationException.class,
                    ()-> serviceSeat.allocateSeatToPessoa(
                            validPosition , ValidName, UnValidCPF));

            // ASSERT
            Assertions.assertEquals(
                    "O CPF deve conter exatamente 11 dígitos numéricos.", exception.getMessage());
        }

        @Test
        void shouldThrowInvalidSeatException() {

            // ARRANGE
            var randomName = Utils.randomNameString();
            var randomCPF = Utils.randomCpf();
            var unValidPosition = 17;

            // ACT
            Exception exception = Assertions.assertThrows(AssentoInvalidoException.class,
                    ()-> serviceSeat.allocateSeatToPessoa(unValidPosition, randomName, randomCPF));

            // ASSERT
            Assertions.assertEquals("O assento informado é inválido.", exception.getMessage());
        }
    }
}




















