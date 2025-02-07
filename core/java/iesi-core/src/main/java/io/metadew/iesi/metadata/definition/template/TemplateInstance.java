package io.metadew.iesi.metadata.definition.template;

public class TemplateInstance {

    private String name;
    private String description;

    // Constructors
    public TemplateInstance() {
    }

    public TemplateInstance(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
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

	public boolean isEmpty() {
		return (this.name == null || this.name.isEmpty()) ;
	}

}