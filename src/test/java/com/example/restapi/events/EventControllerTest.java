package com.example.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@DisplayName("Event 테스트")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;


    @BeforeEach
    void setup(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(modifyUris().host("localhost").removePort(), prettyPrint())
                        .withResponseDefaults(modifyUris().host("localhost").removePort(), prettyPrint()))
                .build();
    }


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
                .location("부산 삼성전기")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("link to update event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("offline").description("it tells if this event is offline or online"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("eventStatus").description("status of new event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update events"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
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

        this.mockMvc.perform(post("/api/events")
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
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2023, 11, 14, 11, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2023, 11, 12, 11, 11))
                .beginEventDateTime(LocalDateTime.of(2023, 11, 24, 11, 11))
                .endEventDateTime(LocalDateTime.of(2023, 11, 23, 11, 11))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("이화여자대학교 신공학관")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    void queryEvent() throws Exception {
        // given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // when
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        links(
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("prev").description("link to previous page"),
                                linkWithRel("self").description("link to self"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        responseFields(
                                subsectionWithPath("_embedded.eventList").description("An array of events"),
                                subsectionWithPath("_links").description("Links for pagination and profile"),
                                subsectionWithPath("page").description("Page of query events")
                        )
                ))
        ;

    }


    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    void getEvent() throws Exception {
        // given
        Event event = generateEvent(100);
        Long eventId = event.getId();

        // when
        ResultActions resultActions = this.mockMvc.perform(get("/api/events/{id}", eventId));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }


    @Test
    @DisplayName("없는 이벤트는 조회했을 때 404 응답 받기")
    void getEvent404() throws Exception {
        // When
        ResultActions resultActions = this.mockMvc.perform(get("/api/events/1245"));

        // Then
        resultActions
                .andExpect(status().isNotFound())
        ;
    }



    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .build();
        return this.eventRepository.save(event);
    }
}
