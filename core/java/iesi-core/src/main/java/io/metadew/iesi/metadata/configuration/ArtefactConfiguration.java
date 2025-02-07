package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Artefact;
import io.metadew.iesi.metadata.definition.Classification;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ArtefactConfiguration {

    private Artefact artefact;

    // Constructors
    public ArtefactConfiguration() {
    }

    public ArtefactConfiguration(Artefact artefact, FrameworkInstance frameworkInstance) {
        this.setArtefact(artefact);
    }

    // Delete
    public String getDeleteStatement() {
        String sql = "";

        sql += "DELETE FROM " + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("Artefacts");
        sql += " WHERE ARTEFACT_NM = " + SQLTools.GetStringForSQL(this.getArtefact().getName());
        sql += " AND ARTEFACT_TYP_NM = ";
        sql += SQLTools.GetStringForSQL(this.getArtefact().getName());
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("Classifications");
        sql += " WHERE ARTEFACT_ID = (";
        sql += "select ARTEFACT_ID FROM " + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("Artefacts");
        sql += " WHERE ARTEFACT_NM = " + SQLTools.GetStringForSQL(this.getArtefact().getName());
        sql += " AND ARTEFACT_TYP_NM = ";
        sql += SQLTools.GetStringForSQL(this.getArtefact().getName());
        sql += ")";
        sql += ";";
        sql += "\n";

        return sql;

    }

    // Insert
    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += this.getDeleteStatement();
        }

        sql += "INSERT INTO " + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("Artefacts");
        sql += " (ARTEFACT_ID, ARTEFACT_NM, ARTEFACT_TYP_NM) ";
        sql += "VALUES ";
        sql += "(";
        sql += "(" + SQLTools.GetNextIdStatement(MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("Artefacts"), "ARTEFACT_ID") + ")";
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getArtefact().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getArtefact().getType());
        sql += ")";
        sql += ";";

        // add classifications
        String sqlClassifications = this.getClassificationInsertStatements(this.getArtefact().getName(),
                this.getArtefact().getType());
        if (!sqlClassifications.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlClassifications;
        }

        return sql;
    }

    private String getClassificationInsertStatements(String artefactName, String artefactType) {
        String result = "";

        // Catch null parameters
        if (this.getArtefact().getClassifications() == null) {
            return result;
        }

        for (Classification classification : this.getArtefact().getClassifications()) {
            ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration(classification);
            if (!result.equalsIgnoreCase("")) {
                result += "\n";
            }
            result += classificationConfiguration.getInsertStatement(artefactName, artefactType);
        }

        return result;
    }

    // GEt Artefact
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Artefact getArtefact(String artefactName, String artefactType) {
        Artefact artefact = new Artefact();
        CachedRowSet crsArtefact = null;
        String queryArtefact = "select ARTEFACT_ID, ARTEFACT_NM, ARTEFACT_TYP_NM from "
                + MetadataControl.getInstance().getCatalogMetadataRepository()
                .getTableNameByLabel("Artefacts")
                + " where ARTEFACT_NM = '" + artefactName + "' AND ARTEFACT_TYP_NM = '" + artefactType + "'";
        crsArtefact = MetadataControl.getInstance().getCatalogMetadataRepository()
                .executeQuery(queryArtefact, "reader");
        ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
        try {
            while (crsArtefact.next()) {
                artefact.setName(artefactName);
                artefact.setType(artefactType);
                artefact.setId(crsArtefact.getLong("ARTEFACT_ID"));

                // Get classifications
                CachedRowSet crsArtefactClassifications = null;
                String queryArtefactClassifications = "select ARTIFACT_ID, CLASSIF_ID from "
                        + MetadataControl.getInstance().getCatalogMetadataRepository()
                        .getTableNameByLabel("Classifications")
                        + " where ARTEFACT_ID = " + artefact.getId();
                crsArtefactClassifications = MetadataControl.getInstance().getCatalogMetadataRepository()
                        .executeQuery(queryArtefactClassifications, "reader");
                List<Classification> artefactClassificationList = new ArrayList();
                while (crsArtefactClassifications.next()) {
                    artefactClassificationList
                            .add(classificationConfiguration.getClassification(crsArtefactClassifications.getLong("CLASSIF_ID")));
                }
                artefact.setClassifications(artefactClassificationList);
                crsArtefactClassifications.close();
            }
            crsArtefact.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return artefact;
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Artefact getArtefact() {
        return artefact;
    }

    public void setArtefact(Artefact artefact) {
        this.artefact = artefact;
    }

}