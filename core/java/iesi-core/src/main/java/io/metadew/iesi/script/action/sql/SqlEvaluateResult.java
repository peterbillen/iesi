package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;


public class SqlEvaluateResult {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation sqlQuery;
    private ActionParameterOperation expectedResult;
    private ActionParameterOperation connectionName;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public SqlEvaluateResult() {

    }

    public SqlEvaluateResult(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setSqlQuery(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "query"));
        this.setExpectedResult(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "hasResult"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("query")) {
                this.getSqlQuery().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("hasresult")) {
                this.getExpectedResult().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("query", this.getSqlQuery());
        this.getActionParameterOperationMap().put("hasResult", this.getExpectedResult());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }

    public boolean execute() {
        try {
            String query = convertQuery(getSqlQuery().getValue());
            boolean expectedResult = convertHasResult(getExpectedResult().getValue());
            String connectionName = convertConnectionName(getConnectionName().getValue());
            return performAction(query, expectedResult, connectionName);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean performAction(String query, boolean hasResult, String connectionName) {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();

        Connection connection = connectionConfiguration.get(connectionName,
                this.getExecutionControl().getEnvName()).get();
        ConnectionOperation connectionOperation = new ConnectionOperation();
        Database database = connectionOperation.getDatabase(connection);

        // Run the action
        CachedRowSet crs;
        crs = database.executeQueryLimitRows(query, 10);
        int rowCount = SQLTools.getRowCount(crs);
        this.getActionExecution().getActionControl().logOutput("count", Integer.toString(rowCount));

        if (rowCount > 0) {
            if (hasResult) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
                return true;
            } else {
                this.getActionExecution().getActionControl().increaseErrorCount();
                return false;
            }
        } else {
            if (!hasResult) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
                return true;
            } else {
                this.getActionExecution().getActionControl().increaseErrorCount();
                return false;
            }
        }
    }


    private boolean convertHasResult(DataType hasResult) {
        if (hasResult instanceof Text) {
            return hasResult.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expect result",
                    hasResult.getClass()));
            return false;
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ActionParameterOperation getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(ActionParameterOperation expectedResult) {
        this.expectedResult = expectedResult;
    }

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(ActionParameterOperation sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

}