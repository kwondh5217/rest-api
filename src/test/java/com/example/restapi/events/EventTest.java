package com.example.restapi.events;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class EventTest {

    @DisplayName("Event가 생성된다")
    @Test
    void builder() {
        Event event = Event.builder()
                .name("Infearn Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @DisplayName("값이 바인딩 된다")
    @Test
    void javaBean(){
        // given
        String name = "Event";
        String description = "Spring";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @DisplayName("입력된 값에 맞게 free가 업데이트 된다")
    @ParameterizedTest
    @CsvSource({
            "0, 0, true",
            "0, 100, false",
            "100, 0, false",
    })
    void testFree(int basePrice, int maxPrice, boolean isFree){
        // given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    @DisplayName("입력에 맞게 offline이 업데이트 된다")
    @ParameterizedTest
    @MethodSource(value = "isOffline")
    void testOffline(String location, boolean isOffline){
        // given
        Event event = Event.builder()
                .location(location)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private static Stream<Arguments> isOffline(){
        return Stream.of(
                Arguments.of("부산 삼성전기", true),
                Arguments.of(null, false),
                Arguments.of("", false)
        );
    }
}