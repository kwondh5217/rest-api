package com.example.restapi.events;

import com.example.restapi.common.ErrorsResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;


    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) throws JsonProcessingException {
        if (errors.hasErrors()) { // 바인딩에서 에러 발생하면
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class); // Deserialization : eventDto에 있는 것을 이벤트 클래스의 인스턴스로 만들기
        event.update(); // free 여부 변경
        Event newEvent = this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        EntityModel eventResource = EntityModel.of(newEvent); // 이벤트를 이벤트 리소스로 변환 → 링크를 추가할 수 있음
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel()); // self 링크 추가
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource); // createdUri를 헤더로 가지는 201 응답
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {

        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedResource = assembler.toModel(page, e -> new EventResource(e));
        pagedResource.add(Link.of("/docs/index.html#resources-query-events").withRel("profile"));
        return ResponseEntity.ok().body(pagedResource);

    }

    @GetMapping("/{id}")
    public ResponseEntity getEvents(@PathVariable("id") Long id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable("id") Long id,
                                      @RequestBody @Valid EventDto updateDto,
                                      Errors errors) throws JsonProcessingException {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        eventValidator.validate(updateDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = optionalEvent.get();
        this.modelMapper.map(updateDto, event);
        Event save = this.eventRepository.save(event);
        EventResource eventResource = new EventResource(save);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }







    /*========================*/

    private ResponseEntity badRequest(Errors errors) throws JsonProcessingException {
        ErrorsResource resource = new ErrorsResource(errors);
        return ResponseEntity.badRequest().body(resource); // 에러를 리소스로 변환
    }
}