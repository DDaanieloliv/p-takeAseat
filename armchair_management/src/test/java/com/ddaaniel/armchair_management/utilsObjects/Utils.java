package com.ddaaniel.armchair_management.utilsObjects;

import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.RequestAllocationDTO;
import com.ddaaniel.armchair_management.model.record.SeatResponseDTO;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;


public class Utils {

    private static final Faker faker = Faker.instance();
    private static final Random randomGenerator = new Random();

    @Autowired
    private static IPersonRepository personRepository;

//    @Autowired
//    private  JdbcTemplate jdbcTemplate;
//
//    @Transactional
//    public void setUpDatabase() {
//
//        UUID person1 = UUID.randomUUID();
//        UUID seat1 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person1, "Pessoa 1", "00000000001");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat1, 1, false, person1);
//
//        UUID person2 = UUID.randomUUID();
//        UUID seat2 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person2, "Pessoa 2", "00000000002");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat2, 2, false, person2);
//
//        UUID person3 = UUID.randomUUID();
//        UUID seat3 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person3, "Pessoa 3", "00000000003");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat3, 3, false, person3);
//
//        UUID person4 = UUID.randomUUID();
//        UUID seat4 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person3, "Pessoa 4", "00000000004");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat4, 4, false, person4);
//
//        UUID person5 = UUID.randomUUID();
//        UUID seat5 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person5, "Pessoa 5", "00000000005");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat5, 5, false, person5);
//
//        UUID person6 = UUID.randomUUID();
//        UUID seat6 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person6, "Pessoa 6", "00000000006");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat6, 6, false, person6);
//
//        UUID person7 = UUID.randomUUID();
//        UUID seat7 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person7, "Pessoa 7", "00000000007");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat7, 7, false, person7);
//
//        UUID seatId8 = UUID.randomUUID();
//        jdbcTemplate.update(
//                "INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seatId8, 8, true, null
//        );
//
//        UUID seatId9 = UUID.randomUUID();
//        jdbcTemplate.update(
//                "INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seatId9, 9, true, null
//        );
//
//        UUID seatId10 = UUID.randomUUID();
//        jdbcTemplate.update(
//                "INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seatId10, 10, true, null
//        );
//
//        UUID seatId11 = UUID.randomUUID();
//        jdbcTemplate.update(
//                "INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seatId11, 11, true, null
//        );
//
//        UUID seatId12 = UUID.randomUUID();
//        jdbcTemplate.update(
//                "INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seatId12, 12, true, null
//        );
//
//        UUID seatId13 = UUID.randomUUID();
//        jdbcTemplate.update(
//                "INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seatId13, 13, true, null
//        );
//
//        UUID seatId14 = UUID.randomUUID();
//        jdbcTemplate.update(
//                "INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seatId14, 14, true, null
//        );
//
//        UUID person15 = UUID.randomUUID();
//        UUID seat15 = UUID.randomUUID();
//        jdbcTemplate.update("INSERT INTO tb_persons (personid, name, cpf) VALUES (?, ?, ?)",
//                person15, "Pessoa 15", "00000000015");
//        jdbcTemplate.update("INSERT INTO tb_seats (seatid, position, free, person_ID) VALUES (?, ?, ?, ?)",
//                seat15, 15, false, person15);
//    }

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
