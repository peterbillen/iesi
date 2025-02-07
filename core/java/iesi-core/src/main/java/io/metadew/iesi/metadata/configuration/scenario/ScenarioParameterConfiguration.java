package io.metadew.iesi.metadata.configuration.scenario;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.feature.Feature;
import io.metadew.iesi.metadata.definition.scenario.Scenario;
import io.metadew.iesi.metadata.definition.scenario.ScenarioParameter;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ScenarioParameterConfiguration {

    private ScenarioParameter scenarioParameter;

    // Constructors
    public ScenarioParameterConfiguration(ScenarioParameter scenarioParameter) {
        this.setScenarioParameter(scenarioParameter);
    }

    public ScenarioParameterConfiguration() {
    }

    public String getInsertStatement(String featureId, long featureVersionNumber, String scenarioId, ScenarioParameter scenarioParameter) {
        return "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("ScenarioParameters") +
                " (FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_PAR_NM, SCENARIO_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(featureId) + "," +
                featureVersionNumber + "," +
                SQLTools.GetStringForSQL(scenarioId) + "," +
                SQLTools.GetStringForSQL(scenarioParameter.getName()) + "," +
                SQLTools.GetStringForSQL(scenarioParameter.getValue()) + ");";
    }

    // Insert
    public String getInsertStatement(Feature feature, Scenario scenario) {
        String sql = "";

        sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("ScenarioParameters");
        sql += " (FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_PAR_NM, SCENARIO_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(feature.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(feature.getVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scenario.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenarioParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getScenarioParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ScenarioParameter getScenarioParameter(Feature feature, String scenarioId, String scenarioParameterName) {
        ScenarioParameter scenarioParameter = new ScenarioParameter();
        CachedRowSet crsScenarioParameter = null;
        String queryScenarioParameter = "select FEATURE_ID, FEATURE_VRS_NB, SCENARIO_ID, SCENARIO_PAR_NM, SCENARIO_PAR_VAL from "
                + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("ScenarioParameters")
                + " where FEATURE_ID = " + SQLTools.GetStringForSQL(feature.getId()) + " and FEATURE_VRS_NB = " + feature.getVersion().getNumber()
                + " AND SCENARIO_ID = " + SQLTools.GetStringForSQL(scenarioId) + "' and SCENARIO_PAR_NM = '" + scenarioParameterName + "'";
        crsScenarioParameter = MetadataControl.getInstance().getCatalogMetadataRepository()
                .executeQuery(queryScenarioParameter, "reader");
        try {
            while (crsScenarioParameter.next()) {
                scenarioParameter.setName(scenarioParameterName);
                scenarioParameter.setValue(crsScenarioParameter.getString("SCENARIO_PAR_VAL"));
            }
            crsScenarioParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return scenarioParameter;
    }

    // Getters and Setters
    public ScenarioParameter getScenarioParameter() {
        return scenarioParameter;
    }

    public void setScenarioParameter(ScenarioParameter scenarioParameter) {
        this.scenarioParameter = scenarioParameter;
    }

}