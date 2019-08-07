package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration2;
import io.metadew.iesi.metadata.configuration.exception.ActionTraceAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ActionTraceDoesNotExistException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.ScriptTrace;
import io.metadew.iesi.metadata.definition.script.key.ScriptTraceKey;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptTraceConfiguration extends Configuration2<ScriptTrace, ScriptTraceKey> {

	private static final Logger LOGGER = LogManager.getLogger();

	public ScriptTraceConfiguration(RepositoryCoordinator repositoryCoordinator, String tableName) {
		super(repositoryCoordinator, tableName);
	}

	@Override
	public Optional<ScriptTrace> get(ScriptTraceKey scriptTraceKey) throws SQLException {
		String query = "SELECT PARENT_PRC_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC FROM " +
				getTableName() +
				" WHERE " +
				" RUN_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getRunId()) + " AND " +
				" PRC_ID = "  + SQLTools.GetStringForSQL(scriptTraceKey.getProcessId()) + ";";
		CachedRowSet cachedRowSet = getRepositoryCoordinator().executeQuery(query, "reader");
		if (cachedRowSet.size() == 0) {
			return Optional.empty();
		} else if (cachedRowSet.size() > 1) {
			LOGGER.info(MessageFormat.format("Found multiple implementations for ActionTrace {0}. Returning first implementation", scriptTraceKey.toString()));
		}
		cachedRowSet.next();
		return Optional.of(new ScriptTrace(scriptTraceKey,
				cachedRowSet.getString("SCRIPT_ID"),
				cachedRowSet.getLong("PARENT_PRC_ID"),
				cachedRowSet.getString("SCRIPT_TYP_NM"),
				cachedRowSet.getString("SCRIPT_NM"),
				cachedRowSet.getString("SCRIPT_DSC")));
	}

	@Override
	public List<ScriptTrace> getAll() throws SQLException {
		List<ScriptTrace> scriptTraces = new ArrayList<>();
		String query = "SELECT RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC FROM " +
				getTableName() + ";";
		CachedRowSet cachedRowSet = getRepositoryCoordinator().executeQuery(query, "reader");
		while (cachedRowSet.next()) {
			scriptTraces.add(new ScriptTrace(new ScriptTraceKey(
					cachedRowSet.getString("RUN_ID"),
					cachedRowSet.getLong("PRC_ID")),
					cachedRowSet.getString("SCRIPT_ID"),
					cachedRowSet.getLong("PARENT_PRC_ID"),
					cachedRowSet.getString("SCRIPT_TYP_NM"),
					cachedRowSet.getString("SCRIPT_NM"),
					cachedRowSet.getString("SCRIPT_DSC")));
		}
		return scriptTraces;
	}

	@Override
	public void delete(ScriptTraceKey scriptTraceKey) throws MetadataDoesNotExistException, SQLException {
		LOGGER.trace(MessageFormat.format("Deleting ActionTrace {0}.", scriptTraceKey.toString()));
		if (!exists(scriptTraceKey)) {
			throw new ActionTraceDoesNotExistException(MessageFormat.format(
					"ScriptTrace {0} does not exists", scriptTraceKey.toString()));
		}
		String deleteStatement = deleteStatement(scriptTraceKey);
		getRepositoryCoordinator().getTraceMetadataRepository().executeUpdate(deleteStatement);
	}

	private String deleteStatement(ScriptTraceKey scriptTraceKey) {
		return "DELETE FROM " + getTableName() +
				" WHERE " +
				" RUN_ID = " + SQLTools.GetStringForSQL(scriptTraceKey.getRunId()) + " AND " +
				" PRC_ID = "  + SQLTools.GetStringForSQL(scriptTraceKey.getProcessId()) + ";";
	}

	@Override
	public void insert(ScriptTrace scriptTrace) throws MetadataAlreadyExistsException, SQLException {
		LOGGER.trace(MessageFormat.format("Inserting ActionParameterTrace {0}.", scriptTrace.getMetadataKey().toString()));
		if (exists(scriptTrace.getMetadataKey())) {
			throw new ActionTraceAlreadyExistsException(MessageFormat.format(
					"ActionParameterTrace {0} already exists", scriptTrace.getMetadataKey().toString()));
		}
		String insertStatement = insertStatement(scriptTrace);
		getRepositoryCoordinator().getTraceMetadataRepository().executeUpdate(insertStatement);
	}

	@Override
	public boolean exists(ScriptTrace metadata) throws SQLException {
		return false;
	}

	@Override
	public boolean exists(ScriptTraceKey key) throws SQLException {
		return false;
	}

	private String insertStatement(ScriptTrace scriptTrace) {
		return "INSERT INTO " + getTableName() +
				" (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
				SQLTools.GetStringForSQL(scriptTrace.getMetadataKey().getRunId()) + "," +
				SQLTools.GetStringForSQL(scriptTrace.getMetadataKey().getProcessId()) + "," +
				SQLTools.GetStringForSQL(scriptTrace.getParentProcessId()) + "," +
				SQLTools.GetStringForSQL(scriptTrace.getScriptId()) + "," +
				SQLTools.GetStringForSQL(scriptTrace.getScriptType()) + "," +
				SQLTools.GetStringForSQL(scriptTrace.getScriptName()) + "," +
				SQLTools.GetStringForSQL(scriptTrace.getScriptDescription()) + ");";
	}
}