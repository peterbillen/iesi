package io.metadew.iesi.metadata.definition.dataframe;

public class DataframeVersion {

    private long number;
    private String description;

    // Constructors
    public DataframeVersion() {

    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }


}