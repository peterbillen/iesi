package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ImpersonationParameterConfiguration {

    private ImpersonationParameter impersonationParameter;

    // Constructors
    public ImpersonationParameterConfiguration(ImpersonationParameter impersonationParameter) {
        this.setImpersonationParameter(impersonationParameter);
    }

    public ImpersonationParameterConfiguration() {
    }

    public String getInsertStatement(String impersonationName, ImpersonationParameter impersonationParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters") +
                " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) VALUES (" +
                SQLTools.GetStringForSQL(impersonationName) + "," +
                SQLTools.GetStringForSQL(impersonationParameter.getConnection()) + "," +
                SQLTools.GetStringForSQL(impersonationParameter.getImpersonatedConnection()) +  "," +
                SQLTools.GetStringForSQL(impersonationParameter.getDescription()) + ");";
    }

    // Insert
    public String getInsertStatement(String impersonationName) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += " (IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(impersonationName);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getConnection());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getImpersonatedConnection());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getImpersonationParameter().getDescription());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ImpersonationParameter getImpersonationParameter(String impersonationName, String impersonationParameterName) {
        ImpersonationParameter impersonationParameter = new ImpersonationParameter();
        CachedRowSet crsImpersonationParameter = null;
        String queryImpersonationParameter = "select IMP_NM, CONN_NM, CONN_IMP_NM, CONN_IMP_DSC from " + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters")
                + " where IMP_NM = '" + impersonationName + "' and CONN_NM = '" + impersonationParameterName + "'";
        crsImpersonationParameter = MetadataControl.getInstance().getConnectivityMetadataRepository().executeQuery(queryImpersonationParameter, "reader");
        try {
            while (crsImpersonationParameter.next()) {
                impersonationParameter.setConnection(impersonationParameterName);
                impersonationParameter.setImpersonatedConnection(crsImpersonationParameter.getString("CONN_IMP_NM"));
                impersonationParameter.setDescription(crsImpersonationParameter.getString("CONN_IMP_DSC"));
            }
            crsImpersonationParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return impersonationParameter;
    }


    // Getters and Setters
    public ImpersonationParameter getImpersonationParameter() {
        return impersonationParameter;
    }

    public void setImpersonationParameter(ImpersonationParameter impersonationParameter) {
        this.impersonationParameter = impersonationParameter;
    }

}