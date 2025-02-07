package io.metadew.iesi.metadata.configuration.repository;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.repository.Repository;
import io.metadew.iesi.metadata.definition.repository.RepositoryInstance;
import io.metadew.iesi.metadata.definition.repository.RepositoryInstanceLabel;
import io.metadew.iesi.metadata.definition.repository.RepositoryInstanceParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class RepositoryInstanceConfiguration {

    private RepositoryInstance repositoryInstance;

    // Constructors
    public RepositoryInstanceConfiguration(RepositoryInstance repositoryInstance) {
        this.setRepositoryInstance(repositoryInstance);
    }

    public RepositoryInstanceConfiguration() {
    }

    // Insert
    public String getInsertStatement(String repositoryName) {
        String sql = "";

        sql += "INSERT INTO "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstances");
        sql += " (REPO_ID, REPO_INST_ID, REPO_INST_NM, REPO_INST_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetLookupIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("Repositories"),
                "REPO_ID", "where REPO_NM = '" + repositoryName) + "')";
        sql += ",";
        sql += "("
                + SQLTools.GetNextIdStatement(
                MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("RepositoryInstances"),
                "REPO_INST_ID")
                + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getRepositoryInstance().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getRepositoryInstance().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(repositoryName);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        // add Lables
        String sqlLabels = this.getLabelInsertStatements(repositoryName);
        if (!sqlLabels.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlLabels;
        }

        return sql;
    }

    private String getParameterInsertStatements(String repositoryName) {
        String result = "";

        // Catch null parameters
        if (this.getRepositoryInstance().getParameters() == null)
            return result;

        for (RepositoryInstanceParameter repositoryInstanceParameter : this.getRepositoryInstance().getParameters()) {
            RepositoryInstanceParameterConfiguration repositoryInstanceParameterConfiguration = new RepositoryInstanceParameterConfiguration(repositoryInstanceParameter);
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += repositoryInstanceParameterConfiguration.getInsertStatement(repositoryName, this.getRepositoryInstance().getName());
        }

        return result;
    }


    private String getLabelInsertStatements(String repositoryName) {
        String result = "";

        // Catch null labels
        if (this.getRepositoryInstance().getLabels() == null)
            return result;

        for (RepositoryInstanceLabel repositoryInstanceLabel : this.getRepositoryInstance().getLabels()) {
            RepositoryInstanceLabelConfiguration repositoryInstanceLabelConfiguration = new RepositoryInstanceLabelConfiguration(repositoryInstanceLabel);
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += repositoryInstanceLabelConfiguration.getInsertStatement(repositoryName, this.getRepositoryInstance().getName());
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public RepositoryInstance getRepositoryInstance(long repositoryId, String repositoryInstanceName) {
        RepositoryInstance repositoryInstance = new RepositoryInstance();
        CachedRowSet crsRepositoryInstance = null;
        String queryRepositoryInstance = "select REPO_ID, REPO_INST_ID, REPO_INST_NM, REPO_INST_DSC from "
                + MetadataControl.getInstance().getConnectivityMetadataRepository()
                .getTableNameByLabel("RepositoryInstances")
                + " where REPO_ID = " + repositoryId + " and REPO_INST_NM = '" + repositoryInstanceName + "'";
        crsRepositoryInstance = MetadataControl.getInstance().getConnectivityMetadataRepository()
                .executeQuery(queryRepositoryInstance, "reader");
        RepositoryInstanceParameterConfiguration repositoryInstanceParameterConfiguration = new RepositoryInstanceParameterConfiguration();
        RepositoryInstanceLabelConfiguration repositoryInstanceLabelConfiguration = new RepositoryInstanceLabelConfiguration();
        try {
            while (crsRepositoryInstance.next()) {
                repositoryInstance.setName(repositoryInstanceName);
                repositoryInstance.setId(crsRepositoryInstance.getLong("REPO_INST_ID"));
                repositoryInstance.setDescription(crsRepositoryInstance.getString("REPO_INST_DSC"));

                // Get parameters
                CachedRowSet crsRepositoryInstanceParameters = null;
                String queryRepositoryInstanceParameters = "select REPO_ID, REPO_INST_ID, REPO_INST_PAR_NM, REPO_INST_PAR_VAL from "
                        + MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("RepositoryInstanceParameters")
                        + " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstance.getId();
                crsRepositoryInstanceParameters = MetadataControl.getInstance()
                        .getConnectivityMetadataRepository().executeQuery(queryRepositoryInstanceParameters, "reader");
                List<RepositoryInstanceParameter> repositoryInstanceParameterList = new ArrayList();
                while (crsRepositoryInstanceParameters.next()) {
                    repositoryInstanceParameterList.add(repositoryInstanceParameterConfiguration.getRepositoryInstanceParameter(
                            repositoryId, repositoryInstance.getId(), crsRepositoryInstanceParameters.getString("REPO_INST_PAR_NM")));
                }
                repositoryInstance.setParameters(repositoryInstanceParameterList);
                crsRepositoryInstanceParameters.close();

                // Get labels
                CachedRowSet crsRepositoryInstanceLabels = null;
                String queryRepositoryInstanceLabels = "select REPO_ID, REPO_INST_ID, REPO_INST_LBL_VAL from "
                        + MetadataControl.getInstance().getConnectivityMetadataRepository()
                        .getTableNameByLabel("RepositoryInstanceLabels")
                        + " where REPO_ID = " + repositoryId + " and REPO_INST_ID = " + repositoryInstance.getId();
                crsRepositoryInstanceLabels = MetadataControl.getInstance()
                        .getConnectivityMetadataRepository().executeQuery(queryRepositoryInstanceLabels, "reader");
                List<RepositoryInstanceLabel> repositoryInstanceLabelList = new ArrayList();
                while (crsRepositoryInstanceLabels.next()) {
                    repositoryInstanceLabelList.add(repositoryInstanceLabelConfiguration.getRepositoryInstanceLabel(
                            repositoryId, repositoryInstance.getId(), crsRepositoryInstanceLabels.getString("REPO_INST_LBL_VAL")));
                }
                repositoryInstance.setLabels(repositoryInstanceLabelList);
                crsRepositoryInstanceLabels.close();
            }
            crsRepositoryInstance.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return repositoryInstance;
    }

    public RepositoryInstance getRepositoryInstance(String repositoryName, String repositoryInstanceName) {
        RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();
        return this.getRepositoryInstance(repositoryConfiguration.getRepositoryId(repositoryName), repositoryInstanceName);
    }

    public RepositoryInstance getRepositoryInstance(Repository repository, String repositoryInstanceName) {
        RepositoryInstance repositoryInstanceResult = null;
        for (RepositoryInstance repositoryInstance : repository.getInstances()) {
            if (repositoryInstance.getName().equalsIgnoreCase(repositoryInstanceName)) {
                repositoryInstanceResult = repositoryInstance;
                break;
            }
        }

        return repositoryInstanceResult;
    }

    // Getters and Setters
    public RepositoryInstance getRepositoryInstance() {
        return repositoryInstance;
    }

    public void setRepositoryInstance(RepositoryInstance repositoryInstance) {
        this.repositoryInstance = repositoryInstance;
    }

}