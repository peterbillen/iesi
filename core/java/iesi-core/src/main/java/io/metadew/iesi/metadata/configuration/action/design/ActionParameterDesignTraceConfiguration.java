package io.metadew.iesi.metadata.configuration.action.design;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.design.exception.ActionParameterDesignTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.design.exception.ActionParameterDesignTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.key.ActionParameterDesignTraceKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionParameterDesignTraceConfiguration extends Configuration<ActionParameterDesignTrace, ActionParameterDesignTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionParameterDesignTraceConfiguration INSTANCE;

    public synchronized static ActionParameterDesignTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionParameterDesignTraceConfiguration();
        }
        return INSTANCE;
    }

    private ActionParameterDesignTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ActionParameterDesignTrace> get(ActionParameterDesignTraceKey actionParameterDesignTraceKey) {
        try {
            String query = "SELECT ACTION_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getProcessId()) + " AND " +
                    " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getActionId()) + " AND " +
                    " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getName()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionParameterDesignTrace {0}. Returning first implementation", actionParameterDesignTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionParameterDesignTrace(actionParameterDesignTraceKey, cachedRowSet.getString("ACTION_PAR_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionParameterDesignTrace> getAll() {
        try {
            List<ActionParameterDesignTrace> actionParameterTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                actionParameterTraces.add(new ActionParameterDesignTrace(new ActionParameterDesignTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("ACTION_ID"),
                        cachedRowSet.getString("ACTION_PAR_NM")),
                        cachedRowSet.getString("ACTION_PAR_VAL")));
            }
            return actionParameterTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionParameterDesignTraceKey actionParameterDesignTraceKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionParameterDesignTrace {0}.", actionParameterDesignTraceKey.toString()));
        if (!exists(actionParameterDesignTraceKey)) {
            throw new ActionParameterDesignTraceDoesNotExistException(MessageFormat.format(
                    "ActionParameterTrace {0} does not exists", actionParameterDesignTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(actionParameterDesignTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionParameterDesignTraceKey actionParameterDesignTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getActionId()) + " AND " +
                " ACTION_PAR_NM = " + SQLTools.GetStringForSQL(actionParameterDesignTraceKey.getName()) + ";";
    }

    @Override
    public void insert(ActionParameterDesignTrace actionParameterDesignTrace) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterDesignTrace {0}.", actionParameterDesignTrace.toString()));
        if (exists(actionParameterDesignTrace.getMetadataKey())) {
            throw new ActionParameterDesignTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", actionParameterDesignTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionParameterDesignTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }


    public void insert(List<ActionParameterDesignTrace> actionParameterDesignTraces) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterDesignTraces {0}.", actionParameterDesignTraces.stream().map(ActionParameterDesignTrace::getMetadataKey).collect(Collectors.toList()).toString()));
        List<String> insertQueries = new ArrayList<>();
        for (ActionParameterDesignTrace actionParameterDesignTrace : actionParameterDesignTraces) {
            if (exists(actionParameterDesignTrace.getMetadataKey())) {
                LOGGER.info(MessageFormat.format("ActionParameterDesignTrace {0} already exists. Skipping", actionParameterDesignTrace.getMetadataKey().toString()));
            } else {
                insertQueries.add(insertStatement(actionParameterDesignTrace));
            }
        }
        getMetadataRepository().executeBatch(insertQueries);
    }

    public String insertStatement(ActionParameterDesignTrace actionParameterDesignTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionParameterDesignTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getMetadataKey().getName()) + "," +
                SQLTools.GetStringForSQL(actionParameterDesignTrace.getValue()) + ");";
    }
}