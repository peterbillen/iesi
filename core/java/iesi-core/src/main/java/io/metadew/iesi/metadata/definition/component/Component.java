package io.metadew.iesi.metadata.definition.component;

import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.util.ArrayList;
import java.util.List;

public class Component {

    private String id;
    private String type;
    private String name;
    private String description;
    private ComponentVersion version;
    private List<ComponentParameter> parameters = new ArrayList<>();
    private List<ComponentAttribute> attributes = new ArrayList<>();

    //Constructors
    public Component() {

    }

    public Component(String id, String type, String name, String description, ComponentVersion version,
                     List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.attributes = attributes;
    }

    public Component(String type, String name, String description, ComponentVersion version,
                     List<ComponentParameter> parameters, List<ComponentAttribute> attributes) {
        this.id = IdentifierTools.getComponentIdentifier(name);
        this.type = type;
        this.name = name;
        this.description = description;
        this.version = version;
        this.parameters = parameters;
        this.attributes = attributes;
    }

    //Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        if (id == null) this.id = IdentifierTools.getComponentIdentifier(this.getName());
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ComponentParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ComponentParameter> parameters) {
        this.parameters = parameters;
    }

    public List<ComponentAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ComponentAttribute> attributes) {
        this.attributes = attributes;
    }

    public ComponentVersion getVersion() {
        return version;
    }

    public void setVersion(ComponentVersion version) {
        this.version = version;
    }

	public boolean isEmpty() {
		return (this.name == null || this.name.isEmpty()) ;
	}

}