package com.ddaaniel.armchair_management.unityTests.controller;


import com.ddaaniel.armchair_management.controller.SeatController;
import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.fakerObjects.Utils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(MockitoExtension.class)
public class ControllerUnitTest {


    @Mock
    ServiceSeatImpl serviceSeat;

    @Mock
    ServicePersonImpl servicePerson;

    @InjectMocks
    SeatController seatController;

    MockMvc mockMvc;

    private final String URI_CONTROLLER = "/seats";

    @BeforeEach
    void setUp (){

        mockMvc = MockMvcBuilders.standaloneSetup(seatController)
                .setViewResolvers(((viewName, locale) -> new MappingJackson2JsonView()))
                .build();
    }

    @Nested
    class getAllStatusPoltronasTestClass {

        @Test
        void shouldReturnHTTP200 () throws Exception {

            // ARRANGE
            var allStatusOfSeatsListed = Utils.convertSeatListToDTO(Utils.seatListGenerate());

            Mockito.doReturn(allStatusOfSeatsListed).when(serviceSeat).listStatusOfAllSeats();

            // ACT & ASSERT
            mockMvc.perform(MockMvcRequestBuilders.get("/seats")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(Utils.asJsonString(allStatusOfSeatsListed) ) )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(Utils.asJsonString(allStatusOfSeatsListed)));


        }
    }

    @Nested
    class getBySeatTestClass {

        @Test
        void shouldMapCurrently() throws Exception {

            // ARRANGE
            var randomInt = Utils.randomIntegerWithRange15();
            var seatEntity = Utils.moveToDTO(Utils.randomlyCreateSeatEntity(randomInt));

            Mockito.doReturn(seatEntity).when(serviceSeat).detailsFromSpecificSeat(Mockito.eq(randomInt));

            // ACT & ASSERT
            mockMvc.perform(MockMvcRequestBuilders.get(URI_CONTROLLER + "/{position}", randomInt)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(Utils.asJsonString(seatEntity)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(Utils.asJsonString(seatEntity)));
        }
    }


    @Nested
    class addPersonToSeatTestClass {

        @Test
        void shouldMapCurrently() throws Exception {

            // ARRANGE
            var dto = Utils.createRandomAllocateDTO();
            var messageConfirmation = "Poltrona alocada com sucesso.";

            Mockito.doNothing().when(serviceSeat).allocateSeatToPessoa(
                    Mockito.eq(dto.position()), Mockito.eq(dto.name()), Mockito.eq(dto.cpf())
            );

            // ACT & ASSERT
            mockMvc.perform(MockMvcRequestBuilders.put(URI_CONTROLLER + "/allocate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(Utils.asJsonString(dto)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .jsonPath("$.message", Is.is(messageConfirmation)));
        }
    }

    @Nested
    class removePersonFromSeatTestClass {

        @Test
        void shouldMapCurrently () throws Exception {

            // ARRANGE
            var randomInt = Utils.randomIntegerWithRange15();
            var messageResult = "Pessoa removida da Poltrona.";

            Mockito.doNothing().when(servicePerson).removePessoaFromSeat(Mockito.eq(randomInt));

            // ACT && ASSERT
            mockMvc.perform(MockMvcRequestBuilders.put(URI_CONTROLLER + "/remove/{position}", randomInt)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is(messageResult)));
        }
    }
}
