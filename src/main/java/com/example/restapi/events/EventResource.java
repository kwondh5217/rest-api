package com.example.restapi.events;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// BeanSerializer 사용함
public class EventResource extends EntityModel<Event> {

    public EventResource(Event event){
        super(event);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

}
