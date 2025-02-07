package io.metadew.iesi.metadata.configuration.script.design;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.design.exception.ScriptVersionDesignTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.design.exception.ScriptVersionDesignTraceDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.design.ScriptVersionDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptVersionDesignTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptVersionDesignTraceConfiguration extends Configuration<ScriptVersionDesignTrace, ScriptVersionDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptVersionDesignTraceConfiguration INSTANCE;

    public synchronized static ScriptVersionDesignTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptVersionDesignTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptVersionDesignTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptVersionDesignTrace> get(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        try {
            String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ScriptVersionDesignTrace {0}. Returning first implementation", scriptVersionDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptVersionDesignTrace(scriptVersionDesignTraceKey,
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("SCRIPT_VRS_DSC")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptVersionDesignTrace> getAll() {
        try {
            List<ScriptVersionDesignTrace> scriptVersionDesignTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptVersionDesignTraces.add(new ScriptVersionDesignTrace(new ScriptVersionDesignTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
                        cachedRowSet.getString("SCRIPT_VRS_DSC")));
            }
            return scriptVersionDesignTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptVersionDesignTrace {0}.", scriptVersionDesignTraceKey.toString()));
        if (!exists(scriptVersionDesignTraceKey)) {
            throw new ScriptVersionDesignTraceDoesNotExistException(MessageFormat.format(
                    "ScriptTrace {0} does not exists", scriptVersionDesignTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptVersionDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
    }

    public boolean exists(ScriptVersionDesignTraceKey scriptVersionDesignTraceKey) {
        String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptVersionDesignTraceKey.getProcessId()) + ";";
        CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
        return cachedRowSet.size() >= 1;
    }

    @Override
    public void insert(ScriptVersionDesignTrace scriptVersionDesignTrace) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ScriptVersionDesignTrace {0}.", scriptVersionDesignTrace.toString()));
        if (exists(scriptVersionDesignTrace.getMetadataKey())) {
            throw new ScriptVersionDesignTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", scriptVersionDesignTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptVersionDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptVersionDesignTrace scriptVersionDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersionDesignTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getScriptVersionNumber()) + "," +
                SQLTools.GetStringForSQL(scriptVersionDesignTrace.getScriptVersionDescription()) + ");";
    }
}