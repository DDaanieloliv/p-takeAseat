package com.ddaaniel.armchair_management.utilsObjects;

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



    public static List<Seat> setUpDatabase() {
        return List.of(
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000001")).position(1).free(true).person(null).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000002")).position(2).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Alice", "00000000001", null)).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000003")).position(3).free(true).person(null).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000004")).position(4).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Bob", "00000000002", null)).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000005")).position(5).free(true).person(null).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000006")).position(6).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Carol", "00000000003", null)).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000007")).position(7).free(true).person(null).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000008")).position(8).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000004"), "David", "00000000004", null)).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000009")).position(9).free(true).person(null).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000010")).position(10).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000005"), "Eva", "00000000005", null)).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000011")).position(11).free(true).person(null).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000012")).position(12).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000006"), "Frank", "00000000006", null)).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000013")).position(13).free(true).person(null).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000014")).position(14).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000007"), "Grace", "00000000007", null)).build(),
                Seat.builder().seatID(UUID.fromString("10000000-0000-0000-0000-000000000015")).position(15).free(false)
                        .person(new Person(UUID.fromString("00000000-0000-0000-0000-000000000008"), "Hank", "00000000008", null)).build()
        );
    }


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

    public static SeatResponseDTO moveToDTO(Seat seat) {

        if (seat.getPerson() != null) {

            Optional<SeatResponseDTO.PersonDTO> personDTO =
                    Optional.of(new SeatResponseDTO.PersonDTO(seat.getPerson().getName(), seat.getPerson().getCpf()));

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
