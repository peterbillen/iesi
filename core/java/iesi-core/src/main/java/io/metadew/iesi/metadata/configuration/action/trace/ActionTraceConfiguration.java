package io.metadew.iesi.metadata.configuration.action.trace;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.trace.exception.ActionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.trace.exception.ActionTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionTraceKey;
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

public class ActionTraceConfiguration extends Configuration<ActionTrace, ActionTraceKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionTraceConfiguration INSTANCE;

    public synchronized static ActionTraceConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionTraceConfiguration();
        }
        return INSTANCE;
    }

    private ActionTraceConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ActionTrace> get(ActionTraceKey actionTraceKey) {
        try {
            String query = "SELECT ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionTraces") +
                    " WHERE " +
                    " RUN_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getRunId()) + " AND " +
                    " PRC_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getProcessId()) + " AND " +
                    " ACTION_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getActionId()) + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", actionTraceKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionTrace(actionTraceKey,
                    cachedRowSet.getLong("ACTION_NB"),
                    cachedRowSet.getString("ACTION_TYP_NM"),
                    cachedRowSet.getString("ACTION_NM"),
                    cachedRowSet.getString("ACTION_DSC"),
                    cachedRowSet.getString("COMP_NM"),
                    cachedRowSet.getString("ITERATION_VAL"),
                    cachedRowSet.getString("CONDITION_VAL"),
                    cachedRowSet.getInt("RETRIES_VAL"),
                    cachedRowSet.getString("EXP_ERR_FL"),
                    cachedRowSet.getString("STOP_ERR_FL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionTrace> getAll() {
        try {
            List<ActionTrace> actionTraces = new ArrayList<>();
            String query = "SELECT RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL FROM " +
                    getMetadataRepository().getTableNameByLabel("ActionTraces") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                actionTraces.add(new ActionTrace(new ActionTraceKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("PRC_ID"),
                        cachedRowSet.getString("ACTION_ID")),
                        cachedRowSet.getLong("ACTION_NB"),
                        cachedRowSet.getString("ACTION_TYP_NM"),
                        cachedRowSet.getString("ACTION_NM"),
                        cachedRowSet.getString("ACTION_DSC"),
                        cachedRowSet.getString("COMP_NM"),
                        cachedRowSet.getString("ITERATION_VAL"),
                        cachedRowSet.getString("CONDITION_VAL"),
                        cachedRowSet.getInt("RETRIES_VAL"),
                        cachedRowSet.getString("EXP_ERR_FL"),
                        cachedRowSet.getString("STOP_ERR_FL")));
            }
            return actionTraces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionTraceKey actionTraceKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", actionTraceKey.toString()));
        if (!exists(actionTraceKey)) {
            throw new ActionTraceDoesNotExistException(MessageFormat.format(
                    "ActionTrace {0} does not exists", actionTraceKey.toString()));
        }
        String deleteStatement = deleteStatement(actionTraceKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionTraceKey actionTraceKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionTraces") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getRunId()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getProcessId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(actionTraceKey.getActionId()) + ";";
    }

    @Override
    public void insert(ActionTrace actionTrace) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionTrace {0}.", actionTrace.getMetadataKey().toString()));
        if (exists(actionTrace.getMetadataKey())) {
            throw new ActionTraceAlreadyExistsException(MessageFormat.format(
                    "ActionParameterTrace {0} already exists", actionTrace.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionTrace);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionTrace actionTrace) {
        return "INSERT INTO " + getMetadataRepository().getTableNameByLabel("ActionTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM," +
                " ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.GetStringForSQL(actionTrace.getMetadataKey().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getMetadataKey().getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getMetadataKey().getActionId()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getNumber()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getType()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getName()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getDescription()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getComponent()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getIteration()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getCondition()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getRetries()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getErrorExpected()) + "," +
                SQLTools.GetStringForSQL(actionTrace.getErrorStop()) + ");";
    }


    public void insert(List<ActionTrace> actionTraces) throws MetadataAlreadyExistsException, SQLException {
        LOGGER.trace(MessageFormat.format("Inserting ActionParameterTraces {0}.", actionTraces.stream().map(ActionTrace::getMetadataKey).collect(Collectors.toList()).toString()));
        List<String> insertQueries = new ArrayList<>();
        for (ActionTrace actionTrace : actionTraces) {
            if (exists(actionTrace.getMetadataKey())) {
                LOGGER.info(MessageFormat.format("ActionParameterTrace {0} already exists. Skipping", actionTrace.getMetadataKey().toString()));
            } else {
                insertQueries.add(insertStatement(actionTrace));
            }
        }
        getMetadataRepository().executeBatch(insertQueries);
    }
}