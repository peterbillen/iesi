package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class ConnectionParameterConfiguration {

    private ConnectionParameter connectionParameter;

    // Constructors
    public ConnectionParameterConfiguration(ConnectionParameter connectionParameter) {
    	this.setConnectionParameter(connectionParameter);
    }

    public ConnectionParameterConfiguration() {
    }

    // Insert
    public String getInsertStatement(String connectionName, String environmentName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters");
        sql += " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(connectionName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(environmentName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getConnectionParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getConnectionParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public String getInsertStatement(String connectionName, String environmentName, ConnectionParameter connectionParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") +
                " (CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(connectionName) + "," +
                SQLTools.GetStringForSQL(environmentName)+ "," +
                SQLTools.GetStringForSQL(connectionParameter.getName()) + "," +
                SQLTools.GetStringForSQL(connectionParameter.getValue()) + ");";
    }

    public ConnectionParameter getConnectionParameter(String cpnnectionName, String environmentName, String connectionParameterName) {
        ConnectionParameter connectionParameter = new ConnectionParameter();
        CachedRowSet crsConnectionParameter = null;
        String queryConnectionParameter = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters")
                + " where CONN_NM = '" + cpnnectionName + "' and CONN_PAR_NM = '" + connectionParameterName + "' and ENV_NM = '" + environmentName + "'";
        crsConnectionParameter = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnectionParameter, "reader");
        try {
            while (crsConnectionParameter.next()) {
                connectionParameter.setName(connectionParameterName);
                connectionParameter.setValue(crsConnectionParameter.getString("CONN_PAR_VAL"));

            }
            crsConnectionParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return connectionParameter;
    }

    public Optional<String> getConnectionParameterValue(String connectionName, String environmentName,
                                                        String connectionParameterName) {
        String output = null;
        CachedRowSet crsConnectionParameter;
        String queryConnectionParameter = "select CONN_NM, ENV_NM, CONN_PAR_NM, CONN_PAR_VAL from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ConnectionParameters") + " where CONN_NM = '"
                + connectionName + "' and ENV_NM = '" + environmentName + "' and CONN_PAR_NM = '" + connectionParameterName + "'";
        crsConnectionParameter = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryConnectionParameter, "reader");
        try {
            while (crsConnectionParameter.next()) {
                output = crsConnectionParameter.getString("CONN_PAR_VAL");
            }
            crsConnectionParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            return Optional.empty();
        }

        return Optional.ofNullable(output);
    }


    // Getters and Setters
    public ConnectionParameter getConnectionParameter() {
        return connectionParameter;
    }

    public void setConnectionParameter(ConnectionParameter connectionParameter) {
        this.connectionParameter = connectionParameter;
    }

}