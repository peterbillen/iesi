package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ConnectionTypeConfiguration {

    private ConnectionType connectionType;
    private String dataObjectType = "ConnectionType";

    // Constructors
    public ConnectionTypeConfiguration(ConnectionType connectionType) {
        this.setConnectionType(connectionType);
    }

    public ConnectionTypeConfiguration() {
    }

    public ConnectionType getConnectionType(String connectionTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), connectionTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        ConnectionType connectionType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                ConnectionType.class);
        return connectionType;
    }

    // Getters and Setters
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}