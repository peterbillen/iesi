package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public abstract class Action {

    FrameworkExecution frameworkExecution;
    ExecutionControl executionControl;
    ScriptExecution scriptExecution;
    ActionExecution actionExecution;
    Map<String, ActionParameter> actionParameterMap;

    public Action(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        // TODO: read from file to set/check all input parameters
        this.frameworkExecution = frameworkExecution;
        this.executionControl = executionControl;
        this.scriptExecution = scriptExecution;
        this.actionExecution = actionExecution;
    }


    public abstract String actionDefinitionFilename();

    public abstract void prepareAction();

    public abstract DataType performAction();

    public void prepare() {
        // TODO: log input parameters
    }

    public boolean execute() {
        try {
            DataType output = performAction();
            logOutput(output);
            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private void logOutput(DataType output) {
        // TODO: log output
    }
}
