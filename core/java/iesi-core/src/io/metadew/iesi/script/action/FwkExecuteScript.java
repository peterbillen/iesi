package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.metadew.iesi.datatypes.Array;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.configuration.FrameworkStatus;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

public class FwkExecuteScript {

    private ActionExecution actionExecution;

    private ScriptExecution scriptExecution;

    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;
    private final Pattern keyValuePattern = Pattern.compile("\\s*(?<parameter>.+)\\s*=\\s*(?<value>.+)\\s*");

    // Parameters
    private ActionParameterOperation scriptName;

    private ActionParameterOperation scriptVersion;

    private ActionParameterOperation environmentName;

    private ActionParameterOperation paramList;

    private ActionParameterOperation paramFile;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkExecuteScript() {

    }

    public FwkExecuteScript(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,
                            ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,
                     ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setScriptExecution(scriptExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setScriptName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "script"));
        this.setScriptVersion(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "version"));
        this.setEnvironmentName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "environment"));
        this.setParamList(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "paramList"));
        this.setParamFile(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "paramFile"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("script")) {
                this.getScriptName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("version")) {
                this.getScriptVersion().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("environment")) {
                this.getEnvironmentName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("paramlist")) {
                this.getParamList().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("paramfile")) {
                this.getParamFile().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("script", this.getScriptName());
        this.getActionParameterOperationMap().put("version", this.getScriptVersion());
        this.getActionParameterOperationMap().put("environment", this.getEnvironmentName());
        this.getActionParameterOperationMap().put("paramList", this.getParamList());
        this.getActionParameterOperationMap().put("paramFile", this.getParamFile());
    }

    private boolean execute(DataType script, DataType version, DataType environment, DataType paramList, DataType paramFile) {
        throw new RuntimeException(MessageFormat.format("Cannot execute cli.executeCommand for arguments '{0}-{1}-{2}-{3}-{4}'",
                script.toString(), version.toString(), environment.toString(), paramList.toString(), paramFile.toString()));
    }

    private boolean execute(Text scriptName, Text version, Text environment, Text paramList, Text paramFile) {
        // Add parameter allow recursive
        // Add reuse options in a script

        // Check on Running a script in a loop
        if (this.getScriptExecution().getScript().getName().equalsIgnoreCase(scriptName.toString())) {
            throw new RuntimeException("Not allowed to run the script recursively");
        }

        try {
            ScriptConfiguration scriptConfiguration = new ScriptConfiguration(this.getFrameworkExecution());
            // Script script = scriptConfiguration.getScript(this.getScriptName().getValue());
            Script script = null;
            if (version.toString().equalsIgnoreCase("")) {
                script = scriptConfiguration.getScript(scriptName.toString());
            } else {
                script = scriptConfiguration.getScript(scriptName.toString(),
                        Long.parseLong(version.toString()));
            }
            ScriptExecution scriptExecution = new ScriptExecution(this.getFrameworkExecution(), script);
            scriptExecution.initializeAsNonRootExecution(this.getExecutionControl(), this.getScriptExecution());

            if (!paramList.toString().equalsIgnoreCase("")) {
                scriptExecution.setParamList(paramList.toString());
            }
            if (!paramFile.toString().equalsIgnoreCase("")) {
                scriptExecution.setParamFile(paramFile.toString());
            }

            scriptExecution.execute();

            if (scriptExecution.getResult().equalsIgnoreCase(FrameworkStatus.SUCCESS.value())) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
            } else if (scriptExecution.getResult()
                    .equalsIgnoreCase(FrameworkStatus.WARNING.value())) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
            } else if (scriptExecution.getResult()
                    .equalsIgnoreCase(FrameworkStatus.ERROR.value())) {
                this.getActionExecution().getActionControl().increaseErrorCount();
            } else {
                this.getActionExecution().getActionControl().increaseErrorCount();
            }

        } catch (Exception e) {
            throw new RuntimeException("Issue setting runtime variables: " + e, e);
        }
        return true;
    }

    public boolean execute() {
        try {
            String scriptName = convertScriptName(getScriptName().getValue());
            long version = convertScriptVersion(getScriptVersion().getValue());
            String environmentName = convertEnvironmentName(getEnvironmentName().getValue());
            Map<String, String> parameterList = convertParameterList(getParamList().getValue());
            String parameterFileName = convertParameterFileName(getParamFile().getValue());

            return execute();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private Map<String, String> convertParameterEntry(DataType parameterEntry) {
        Map<String, String> parameterMap = new HashMap<>();
        if (parameterEntry instanceof Text) {
            Matcher matcher = keyValuePattern.matcher(parameterEntry.toString());
            if (matcher.find()) {
                parameterMap.put(matcher.group("parameter"), matcher.group("value"));
            } else {
                frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.setParameterList: parameter entry ''{0}'' does not follow correct syntax",
                        parameterEntry), Level.WARN);
            }
            return parameterMap;
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.setParameterList does not accept {0} as type for parameter entry",
                    parameterEntry.getClass()), Level.WARN);
            return parameterMap;
        }
    }

    private Map<String, String> convertParameterList(DataType list) {
        Map<String, String> parameterMap = new HashMap<>();
        if (list instanceof Text) {
            Arrays.stream(list.toString().split(","))
                    .forEach(parameterEntry -> parameterMap.putAll(convertParameterEntry(DataTypeResolver.resolveToDatatype(parameterEntry))));
            return parameterMap;
        } else if (list instanceof Array) {
            for  (DataType parameterEntry : ((Array) list).getList()){
                parameterMap.putAll(convertParameterEntry(parameterEntry));
            }
            return parameterMap;
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.setParameterList does not accept {0} as type for list",
                    list.getClass()), Level.WARN);
            return parameterMap;
        }
    }

    private long convertScriptVersion(DataType scriptVersion) {
        if (scriptVersion instanceof Text) {
            return Long.parseLong(scriptVersion.toString());
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptVersion.getClass()), Level.WARN);
            return -1;
        }
    }

    private String convertParameterFileName(DataType parameterFileName) {
        if (parameterFileName instanceof Text) {
            return parameterFileName.toString();
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for parameter file name",
                    parameterFileName.getClass()), Level.WARN);
            return parameterFileName.toString();
        }
    }

    private String convertEnvironmentName(DataType environmentName) {
        if (environmentName instanceof Text) {
            return environmentName.toString();
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for environment name",
                    environmentName.getClass()), Level.WARN);
            return environmentName.toString();
        }
    }

    private String convertScriptName(DataType scriptName) {
        if (scriptName instanceof Text) {
            return scriptName.toString();
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptName.getClass()), Level.WARN);
            return scriptName.toString();
        }
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
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

    public ScriptExecution getScriptExecution() {
        return scriptExecution;
    }

    public void setScriptExecution(ScriptExecution scriptExecution) {
        this.scriptExecution = scriptExecution;
    }

    public ActionParameterOperation getScriptName() {
        return scriptName;
    }

    public void setScriptName(ActionParameterOperation scriptName) {
        this.scriptName = scriptName;
    }

    public ActionParameterOperation getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(ActionParameterOperation environmentName) {
        this.environmentName = environmentName;
    }

    public ActionParameterOperation getParamList() {
        return paramList;
    }

    public void setParamList(ActionParameterOperation paramList) {
        this.paramList = paramList;
    }

    public ActionParameterOperation getParamFile() {
        return paramFile;
    }

    public void setParamFile(ActionParameterOperation paramFile) {
        this.paramFile = paramFile;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getScriptVersion() {
        return scriptVersion;
    }

    public void setScriptVersion(ActionParameterOperation scriptVersion) {
        this.scriptVersion = scriptVersion;
    }
}