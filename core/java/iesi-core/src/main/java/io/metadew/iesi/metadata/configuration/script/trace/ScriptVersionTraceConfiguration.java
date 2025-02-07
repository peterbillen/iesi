package io.metadew.iesi.metadata.configuration.script.trace;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.trace.exception.ScriptVersionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.script.trace.exception.ScriptVersionTraceDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.trace.ScriptVersionTrace;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptVersionTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptVersionTraceConfiguration extends Configuration<ScriptVersionTrace, ScriptVersionTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptVersionTraceConfiguration INSTANCE;

    public synchronized static ScriptVersionTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptVersionTraceConfiguration();
        }
        return INSTANCE;
    }

    private ScriptVersionTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ScriptVersionTrace> get(ScriptVersionTraceKey scriptVersionTraceKey) {
        try {
            String query = "SELECT SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(scriptVersionTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(scriptVersionTraceKey.getProcessId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.info(MessageFormat.format("Found multiple implementations for ScriptVersionTrace {0}. Returning first implementation", scriptVersionTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ScriptVersionTrace(scriptVersionTraceKey,
                    cachedRowSet.getLong("SCRIPT_VRS_NB"),
                    cachedRowSet.getString("SCRIPT_VRS_DSC")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ScriptVersionTrace> getAll() {
        try {
            List<ScriptVersionTrace> scriptVersionTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC FROM " +
                    getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                scriptVersionTraces.add(new ScriptVersionTrace(new ScriptVersionTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID")),
                        cachedRowSet.getLong("SCRIPT_VRS_NB"),
                        cachedRowSet.getString("SCRIPT_VRS_DSC")));
            }
            return scriptVersionTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ScriptVersionTraceKey scriptVersionTraceKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ScriptVersionTrace {0}.", scriptVersionTraceKey.toString()));
        if (!exists(scriptVersionTraceKey)) {
            throw new ScriptVersionTraceDoesNotExistException(MessageFormat.format(
                    "ScriptVersionTrace {0} does not exists", scriptVersionTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(scriptVersionTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ScriptVersionTraceKey scriptTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ScriptVersionTrace scriptVersionTrace) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting scriptVersionTrace {0}.", scriptVersionTrace.getMetadataKey().toString()));
        if (exists(scriptVersionTrace.getMetadataKey())) {
            throw new ScriptVersionTraceAlreadyExistsException(MessageFormat.format(
                    "ScriptVersionTrace {0} already exists", scriptVersionTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(scriptVersionTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ScriptVersionTrace scriptVersionTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ScriptVersionTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_VRS_NB, SCRIPT_VRS_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptVersionTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptVersionTrace.getScriptVersionNumber()) + "," +
                SQLTools.GetStringForSQL(scriptVersionTrace.getScriptVersionDescription()) + ");";
    }
}