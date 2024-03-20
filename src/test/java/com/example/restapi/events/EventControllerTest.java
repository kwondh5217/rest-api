package com.example.restapi.events;

import com.example.restapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Event 테스트")
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2024, 03, 22, 23, 33))
                .closeEnrollmentDateTime(LocalDateTime.of(2024, 05, 30, 23, 33))
                .beginEventDateTime(LocalDateTime.of(2024, 04, 20, 23, 33))
                .endEventDateTime(LocalDateTime.of(2024, 06, 26, 23, 33))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("송정삼정그린코아 더시티")
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
    }

    @Test
    @DisplayName("입력할 수 없는 값이 들어왔을 때 에러를 발생하는 테스트")
    void createEvent_badRequest() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2024, 03, 19, 23, 33))
                .closeEnrollmentDateTime(LocalDateTime.of(2024, 05, 19, 23, 33))
                .beginEventDateTime(LocalDateTime.of(2024, 03, 20, 23, 33))
                .endEventDateTime(LocalDateTime.of(2024, 03, 19, 23, 33))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("송정삼정그린코아 더시티")
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 비어있을 때 에러를 발생하는 테스트")
    void createEvent_badRequest_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 잘못되었을 때 에러를 발생하는 테스트")
    void createEvent_badRequest_Wrong_Input() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2024, 03, 19, 23, 33))
                .closeEnrollmentDateTime(LocalDateTime.of(2024, 05, 14, 23, 33))
                .beginEventDateTime(LocalDateTime.of(2024, 03, 20, 23, 33))
                .endEventDateTime(LocalDateTime.of(2024, 03, 19, 23, 33))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("송정삼정그린코아 더시티")
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                ;
    }
}
