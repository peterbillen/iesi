package io.metadew.iesi.metadata.configuration.action.result;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.action.result.exception.ActionResultOutputAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.action.result.exception.ActionResultOutputDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.result.ActionResultOutput;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultOutputKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionResultOutputConfiguration extends Configuration<ActionResultOutput, ActionResultOutputKey> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ActionResultOutputConfiguration INSTANCE;

    public synchronized static ActionResultOutputConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActionResultOutputConfiguration();
        }
        return INSTANCE;
    }

    private ActionResultOutputConfiguration() {
    }

    public void init(MetadataRepository metadataRepository) {
        setMetadataRepository(metadataRepository);
    }

    @Override
    public Optional<ActionResultOutput> get(ActionResultOutputKey actionResultOutputKey) {
        try {
            String query = "select RUN_ID, PRC_ID, ACTION_ID, OUT_NM, OUT_VAL from " + getMetadataRepository().getTableNameByLabel("ActionResultOutputs")
                    + " where RUN_ID = " + SQLTools.GetStringForSQL(actionResultOutputKey.getRunId())
                    + " and ACTION_ID = " + SQLTools.GetStringForSQL(actionResultOutputKey.getActionId())
                    + " and OUT_NM = " + SQLTools.GetStringForSQL(actionResultOutputKey.getOutputName())
                    + " and PRC_ID = " + actionResultOutputKey.getProcessId() + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            if (cachedRowSet.size() == 0) {
                return Optional.empty();
            } else if (cachedRowSet.size() > 1) {
                LOGGER.warn(MessageFormat.format("Found multiple implementations for ActionResultOutput {0}. Returning first implementation", actionResultOutputKey.toString()));
            }
            cachedRowSet.next();
            return Optional.of(new ActionResultOutput(actionResultOutputKey,
                    cachedRowSet.getString("OUT_VAL")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActionResultOutput> getAll() {
        try {
            List<ActionResultOutput> actionResultOutputs = new ArrayList<>();
            String query = "select RUN_ID, PRC_ID, ACTION_ID, OUT_NM, OUT_VAL from "
                    + getMetadataRepository().getTableNameByLabel("ActionResultOutputs") + ";";
            CachedRowSet cachedRowSet = getMetadataRepository().executeQuery(query, "reader");
            while (cachedRowSet.next()) {
                actionResultOutputs.add(new ActionResultOutput(new ActionResultOutputKey(
                        cachedRowSet.getString("RUN_ID"),
                        cachedRowSet.getLong("SCRIPT_PRC_ID"),
                        cachedRowSet.getString("ACTION_ID"),
                        cachedRowSet.getString("OUT_NM")),
                        cachedRowSet.getString("OUT_VAL")));
            }
            return actionResultOutputs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActionResultOutputKey actionResultOutputKey) throws MetadataDoesNotExistException {
        LOGGER.trace(MessageFormat.format("Deleting ActionResultOutput {0}.", actionResultOutputKey.toString()));
        if (!exists(actionResultOutputKey)) {
            throw new ActionResultOutputDoesNotExistException(MessageFormat.format(
                    "ActionResultOutput {0} does not exists", actionResultOutputKey.toString()));
        }
        String deleteStatement = deleteStatement(actionResultOutputKey);
        getMetadataRepository().executeUpdate(deleteStatement);
    }

    private String deleteStatement(ActionResultOutputKey resultOutputKey) {
        return "DELETE FROM " + getMetadataRepository().getTableNameByLabel("ActionResultOutputs") +
                " WHERE " +
                " RUN_ID = " + SQLTools.GetStringForSQL(resultOutputKey.getRunId()) + " AND " +
                " ACTION_ID = " + SQLTools.GetStringForSQL(resultOutputKey.getActionId()) + " AND " +
                " OUT_NM = " + SQLTools.GetStringForSQL(resultOutputKey.getOutputName()) + " AND " +
                " PRC_ID = " + SQLTools.GetStringForSQL(resultOutputKey.getProcessId()) + ";";
    }

    @Override
    public void insert(ActionResultOutput actionResultOutput) throws MetadataAlreadyExistsException {
        LOGGER.trace(MessageFormat.format("Inserting ActionResultOutput {0}.", actionResultOutput.getMetadataKey().toString()));
        if (exists(actionResultOutput.getMetadataKey())) {
            throw new ActionResultOutputAlreadyExistsException(MessageFormat.format(
                    "ActionResult {0} already exists", actionResultOutput.getMetadataKey().toString()));
        }
        String insertStatement = insertStatement(actionResultOutput);
        getMetadataRepository().executeUpdate(insertStatement);
    }

    private String insertStatement(ActionResultOutput actionResultOutput) {
        return "INSERT INTO "
                + getMetadataRepository().getTableNameByLabel("ActionResultOutputs")
                + " (RUN_ID, PRC_ID, ACTION_ID, OUT_NM, OUT_VAL) VALUES ("
                + SQLTools.GetStringForSQL(actionResultOutput.getMetadataKey().getRunId()) + ","
                + SQLTools.GetStringForSQL(actionResultOutput.getMetadataKey().getProcessId()) + ","
                + SQLTools.GetStringForSQL(actionResultOutput.getMetadataKey().getActionId()) + ","
                + SQLTools.GetStringForSQL(actionResultOutput.getMetadataKey().getOutputName()) + ","
                + SQLTools.GetStringForSQL(actionResultOutput.getValue()) + ");";
    }

}