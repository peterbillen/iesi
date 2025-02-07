package io.metadew.iesi.server.rest.controller;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.exception.ExecutionRequestDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestBuilderException;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.server.rest.error.DataNotFoundException;
import io.metadew.iesi.server.rest.resource.HalMultipleEmbeddedResource;
import io.metadew.iesi.server.rest.resource.execution_request.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.resource.execution_request.resource.ExecutionRequestDtoResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/execution_request")
public class ExecutionRequestController {

    private final ExecutionRequestDtoResourceAssembler executionRequestDtoResourceAssembler;
    private final ExecutionRequestConfiguration executionRequestConfiguration;

    @Autowired
    ExecutionRequestController(ExecutionRequestConfiguration executionRequestConfiguration,
                               ExecutionRequestDtoResourceAssembler executionRequestDtoResourceAssembler) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.executionRequestDtoResourceAssembler = executionRequestDtoResourceAssembler;
    }

    @GetMapping("")
    public HalMultipleEmbeddedResource<ExecutionRequestDto> getAll() {
        return new HalMultipleEmbeddedResource<>(executionRequestConfiguration.getAll()
                .stream()
                .parallel()
                .map(executionRequestDtoResourceAssembler::toResource)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ExecutionRequestDto getById(@PathVariable String id) {
        return executionRequestConfiguration.get(new ExecutionRequestKey(id))
                .map(executionRequestDtoResourceAssembler::toResource)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find ExecutionRequest {0}", id)));
    }

    @PostMapping("")
    public ExecutionRequestDto post(@RequestBody ExecutionRequestDto executionRequestDto) {
        try {
            ExecutionRequest executionRequest = executionRequestDto.convertToNewEntity();
            executionRequestConfiguration.insert(executionRequest);
            return executionRequestDtoResourceAssembler.toResource(executionRequest);
        } catch (MetadataAlreadyExistsException | ExecutionRequestBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("")
    public HalMultipleEmbeddedResource<ExecutionRequestDto> putAll(@RequestBody List<ExecutionRequestDto> executionRequestDtos) {
        HalMultipleEmbeddedResource<ExecutionRequestDto> halMultipleEmbeddedResource = new HalMultipleEmbeddedResource<>();
        for (ExecutionRequestDto executionRequestDto : executionRequestDtos) {
            try {
                executionRequestConfiguration.update(executionRequestDto.convertToEntity());
                halMultipleEmbeddedResource.embedResource(executionRequestDto);
                halMultipleEmbeddedResource.add(linkTo(methodOn(ScriptController.class)
                        .getByName(executionRequestDto.getName()))
                        .withRel(executionRequestDto.getName()));
            } catch (ExecutionRequestDoesNotExistException e) {
                e.printStackTrace();
                throw new DataNotFoundException(executionRequestDto.getName());
            }
        }

        return halMultipleEmbeddedResource;
    }

    @PutMapping("/{id}")
    public ExecutionRequestDto put(@PathVariable String id, @RequestBody ExecutionRequestDto executionRequestDto) {
        try {
            executionRequestConfiguration.update(executionRequestDto.convertToEntity());
            return executionRequestDtoResourceAssembler.toResource(executionRequestDto.convertToEntity());
        } catch (ExecutionRequestDoesNotExistException e) {
            throw new DataNotFoundException(executionRequestDto.getExecutionRequestId());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteByName(@PathVariable String id) {
        try {
            executionRequestConfiguration.delete(new ExecutionRequestKey(id));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MetadataDoesNotExistException e) {
            throw new DataNotFoundException(id);
        }
    }
}