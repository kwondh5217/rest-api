package com.example.restapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// BeanSerializer 사용함
public class EventResource extends RepresentationModel<EventResource> {

    @JsonUnwrapped
    private Event event;

    public EventResource(Event event){
        this.event = event;
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
    public Event getEvent(){
        return event;
    }

}
