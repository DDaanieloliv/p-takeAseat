package com.ddaaniel.armchair_management.fakerObjects;

import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.RequestAllocationDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.javafaker.Faker;

import java.util.*;


public class Utils {

    private static final Faker faker = Faker.instance();
    private static final Random randomGenerator = new Random();


    public static Seat createSeatWithoutPerson(Integer position) {

        return Seat.builder()
                .seatID(UUID.randomUUID())
                .position(position)
                .free(true)
                .build();
    }

    public static Seat createSeatWithParameterWithoutPersonID(UUID usedSeatId, Integer position, String name, String cpf) {

        return Seat.builder()
                .seatID(usedSeatId)
                .position(position)
                .free(false)
                .person(
                        Person.builder()
                                .name(name)
                                .cpf(cpf)
                                .build()
                ).build();
    }

    public static Seat createSeatWithParameterWithPersonID(UUID usedSeatId, Integer position, String name, String cpf) {

        return Seat.builder()
                .seatID(usedSeatId)
                .position(position)
                .free(false)
                .person(
                        Person.builder()
                                .personID(UUID.randomUUID())
                                .name(name)
                                .cpf(cpf)
                                .build()
                ).build();
    }


    public static List<SeatResponseDTO> convertSeatListToDTO (List<Seat> seats) {

        List<SeatResponseDTO> dto = new ArrayList<>();

        for (Seat seat : seats){
            dto.add(moveToDTO(seat));
        }

        return dto;
    }


    public static SeatResponseDTO moveToDTO(Optional<Seat> optionalSeat) {

        if (optionalSeat.get().getPerson() != null){
            Optional<SeatResponseDTO.PersonDTO> personDTO =
                    Optional.of(new SeatResponseDTO.PersonDTO(
                            optionalSeat.get().getPerson().getName(),
                            optionalSeat.get().getPerson().getCpf()
                    ));

            SeatResponseDTO dto = new SeatResponseDTO(
                    optionalSeat.get().getPosition(),
                    optionalSeat.get().getFree(),
                    personDTO);

            return dto;
        } else {

            Optional<SeatResponseDTO.PersonDTO> personDTO = Optional.empty();

            SeatResponseDTO dto = new SeatResponseDTO(
                    optionalSeat.get().getPosition(),
                    optionalSeat.get().getFree(),
                    personDTO
            );

            return dto;
        }
    }

    public static SeatResponseDTO moveToDTO(Seat seat) {

        if (seat.getPerson() != null) {

            Optional<SeatResponseDTO.PersonDTO> personDTO =
                    Optional.of(new SeatResponseDTO.PersonDTO(
                            seat.getPerson().getName(),
                            seat.getPerson().getCpf()));

            SeatResponseDTO dto = new SeatResponseDTO(
                    seat.getPosition(),
                    seat.getFree(),
                    personDTO
                    );

            return dto;
        }
        else {

            Optional<SeatResponseDTO.PersonDTO> personDTO = Optional.empty();

            SeatResponseDTO dto = new SeatResponseDTO(
                    seat.getPosition(),
                    seat.getFree(),
                    personDTO
            );

            return dto;
        }
    }





    public static List<Seat> seatListGenerate() {

        List<Seat> seatList = new ArrayList<>();
        for (int i = 1; i <= 15; i++){

            seatList.add(randomlyCreateSeatEntity(i));

        }

        return seatList;
    }

    public static Seat randomlyCreateSeatEntity (Integer position) {

        Seat seat = new Seat();

        seat.setSeatID(UUID.randomUUID());
        seat.setPosition(position);
        seat.setFree(randomGenerator.nextBoolean());

        if (!seat.getFree()){
            Person person = new Person();

            person.setPersonID(UUID.randomUUID());
            person.setName(faker.name().username());
            person.setCpf(faker.regexify("[0-9]{11}"));

            seat.setPerson(person);
        }

        return seat;
    }

    public static RequestAllocationDTO createRandomAllocateDTO() {

        return new RequestAllocationDTO(
                randomIntegerWithRange15(), randomNameString(), randomCpf());
    }

    public static Integer randomIntegerWithRange15() {
        return randomGenerator.nextInt(1, 16);
    }

    public static String randomNameString() {
        return faker.artist().name();
    }

    public static String randomCpf() {
        return faker.regexify("[0-9]{11}");
    }

    public static Person buildPerson() {

        return Person.builder()
                .personID(UUID.randomUUID())
                .name("Filho da puta")
                .cpf("00000000000")
                .build();
    }

    public static Seat buildSeatEntityByPositionWithPerson(Integer position) {


        return Seat.builder()
                .seatID(UUID.randomUUID())
                .position(position)
                .free(false)
                .person(Utils.buildPerson())
                .build();
    }


    public static Seat seatBuiltRemovingPerson (Seat seat){

        return Seat.builder()
                .seatID(seat.getSeatID())
                .position(seat.getPosition())
                .free(true)
                .build();
    }


    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Jdk8Module()); // Suporte a Optional<T>
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
