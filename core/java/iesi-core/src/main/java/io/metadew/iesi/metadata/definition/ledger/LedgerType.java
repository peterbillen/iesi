package io.metadew.iesi.metadata.definition.ledger;

import java.util.List;

public class LedgerType {

    private String name;
    private String description;
    private List<LedgerTypeParameter> parameters;

    //Constructors
    public LedgerType() {

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

    public List<LedgerTypeParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<LedgerTypeParameter> parameters) {
        this.parameters = parameters;
    }


}