package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class FwkDummy {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkDummy() {

    }

    public FwkDummy(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl, ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {

    }

    public boolean execute() {
        try {
            this.getExecutionControl().logMessage(this.getActionExecution(), "Not doing anything", Level.TRACE);
            this.getActionExecution().getActionControl().increaseSuccessCount();
            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
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

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

}