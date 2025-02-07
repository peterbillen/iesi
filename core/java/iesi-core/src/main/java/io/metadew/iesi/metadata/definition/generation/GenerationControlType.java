package io.metadew.iesi.metadata.definition.generation;

import java.util.List;

public class GenerationControlType {

    private String name;
    private String description;
    private List<GenerationControlTypeParameter> parameters;

    //Constructors
    public GenerationControlType() {

    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GenerationControlTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<GenerationControlTypeParameter> parameters) {
        this.parameters = parameters;
    }


}