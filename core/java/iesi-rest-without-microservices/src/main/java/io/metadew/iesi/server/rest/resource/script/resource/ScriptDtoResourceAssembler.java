package io.metadew.iesi.server.rest.resource.script.resource;


import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.server.rest.controller.ScriptController;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptActionDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.resource.script.dto.ScriptVersionDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScriptDtoResourceAssembler extends ResourceAssemblerSupport<Script, ScriptDto> {

    public ScriptDtoResourceAssembler() {
        super(ScriptController.class, ScriptDto.class);
    }

    @Override
    public ScriptDto toResource(Script script) {
        ScriptDto scriptByNameDto = convertToDto(script);
        Link selfLink = linkTo(methodOn(ScriptController.class).get(script.getName(), script.getVersion().getNumber()))
                .withRel("script:" + scriptByNameDto.getName() + "-" + scriptByNameDto.getVersion().getNumber());
        scriptByNameDto.add(selfLink);
        Link versionLink = linkTo(methodOn(ScriptController.class).executeScript(null, scriptByNameDto.getName(), scriptByNameDto.getVersion().getNumber()))
                .withRel("script");
        scriptByNameDto.add(versionLink);
        return scriptByNameDto;
    }

    private ScriptDto convertToDto(Script script) {
        return new ScriptDto(script.getName(), script.getType(), script.getDescription(),
                ScriptVersionDto.convertToDto(script.getVersion()), script.getParameters(),
                script.getActions().stream().map(this::convertToDto).collect(Collectors.toList()));
    }


    private ScriptActionDto convertToDto(Action action){
        return new ScriptActionDto(action.getNumber(),action.getName(),action.getType(), action.getDescription(),action.getComponent(), action.getCondition(), action.getIteration(), action.getErrorExpected(),action.getErrorStop(),action.getRetries(),action.getParameters());
    }

}