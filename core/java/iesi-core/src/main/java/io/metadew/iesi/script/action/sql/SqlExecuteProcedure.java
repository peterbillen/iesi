package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.sql.SQLDataTransfer;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.KeyValueDataset;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
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
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;

public class SqlExecuteProcedure {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation sqlProcedure;
    private ActionParameterOperation connectionName;
    private ActionParameterOperation sqlParameters;
    private ActionParameterOperation outputDataset;
    private ActionParameterOperation appendOutput;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public SqlExecuteProcedure() {

    }

    public SqlExecuteProcedure(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        // Set Parameters
        this.setSqlProcedure(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "procedure"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));
        this.setSqlParameters(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "parameters"));
        this.setOutputDataset(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "outputDataset"));
        this.setAppendOutput(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "appendOutput"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("procedure")) {
                this.getSqlProcedure().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("parameters")) {
                this.getSqlParameters().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("outputdataset")) {
                this.getOutputDataset().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("appendoutput")) {
                this.getAppendOutput().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("procedure", this.getSqlProcedure());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
        this.getActionParameterOperationMap().put("parameters", this.getSqlParameters());
        this.getActionParameterOperationMap().put("outputDataset", this.getOutputDataset());
        this.getActionParameterOperationMap().put("appendOutput", this.getAppendOutput());
    }

    public boolean execute() {
        try {

            String sqlProcedure = convertSqlProcedure(getSqlProcedure().getValue());
            String connectionName = convertConnectionName(getSqlProcedure().getValue());
            String sqlParameters = convertSqlParameters(getSqlProcedure().getValue());
            String outputDataset = convertDatasetReferenceName(getSqlProcedure().getValue());
            boolean appendOutput = convertAppendOutput(getSqlProcedure().getValue());

            return execute(sqlProcedure, connectionName, sqlParameters, outputDataset, appendOutput);

        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String sqlProcedure, String connectionName, String sqlParameters, String outputDatasetReferenceName, boolean appendOutput) throws SQLException {

        // Get Connection
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
        Connection connection = connectionConfiguration
                .get(connectionName, this.getExecutionControl().getEnvName()).get();
        ConnectionOperation connectionOperation = new ConnectionOperation();
        Database database = connectionOperation.getDatabase(connection);

        if (database == null) {
            throw new RuntimeException("Error establishing DB connection");
        }

        SqlScriptResult sqlScriptResult = null;
        CachedRowSet crs = null;
        crs = database.executeProcedure(sqlProcedure, sqlParameters);
        // TODO resolve for files and resolve inside
        // TODO Retrieve config from a file

        if (!outputDatasetReferenceName.isEmpty()) {
            Optional<KeyValueDataset> dataset = this.getExecutionControl().getExecutionRuntime()
                    .getDataset(outputDatasetReferenceName);

            // Perform the action
            SQLDataTransfer.transferData(crs, dataset.get().getDatasetDatabase(), dataset.get().getName(), !appendOutput);
            sqlScriptResult = new SqlScriptResult(0, "data.transfer.complete", "");

        } else {
            sqlScriptResult = new SqlScriptResult(0, "sql.procedure.complete", "");
        }

        // Evaluate result
        this.getActionExecution().getActionControl().logOutput("sys.out", sqlScriptResult.getSystemOutput());

        if (sqlScriptResult.getReturnCode() != 0) {
            this.getActionExecution().getActionControl().logOutput("err.out", sqlScriptResult.getErrorOutput());
            throw new RuntimeException("Error execting SQL query");
        }

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertSqlProcedure(DataType sqlProcedure) {
        if (sqlProcedure instanceof Text) {
            return sqlProcedure.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sqlProcedure",
                    sqlProcedure.getClass()));
            return sqlProcedure.toString();
        }
    }

    private String convertSqlParameters(DataType sqlParameters) {
        if (sqlParameters instanceof Text) {
            return sqlParameters.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sqlParameters",
                    sqlParameters.getClass()));
            return sqlParameters.toString();
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


    private boolean convertAppendOutput(DataType appendOutput) {
        if (appendOutput instanceof Text) {
            return appendOutput.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for appendOutput",
                    appendOutput.getClass()));
            return false;
        }
    }

    private String convertDatasetReferenceName(DataType datasetReferenceName) {
        if (datasetReferenceName instanceof Text) {
            return datasetReferenceName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset reference name",
                    datasetReferenceName.getClass()));
            return datasetReferenceName.toString();
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

    public ActionParameterOperation getActionParameterOperation(String key) {
        return this.getActionParameterOperationMap().get(key);
    }

    public ActionParameterOperation getOutputDataset() {
        return outputDataset;
    }

    public void setOutputDataset(ActionParameterOperation outputDataset) {
        this.outputDataset = outputDataset;
    }

    public ActionParameterOperation getAppendOutput() {
        return appendOutput;
    }

    public void setAppendOutput(ActionParameterOperation appendOutput) {
        this.appendOutput = appendOutput;
    }

    public ActionParameterOperation getSqlProcedure() {
        return sqlProcedure;
    }

    public void setSqlProcedure(ActionParameterOperation sqlProcedure) {
        this.sqlProcedure = sqlProcedure;
    }

    public ActionParameterOperation getSqlParameters() {
        return sqlParameters;
    }

    public void setSqlParameters(ActionParameterOperation sqlParameters) {
        this.sqlParameters = sqlParameters;
    }

}
