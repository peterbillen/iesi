package io.metadew.iesi.runtime.script;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptFileExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import io.metadew.iesi.script.operation.JsonInputOperation;
import io.metadew.iesi.script.operation.YamlInputOperation;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ScriptFileExecutor implements ScriptExecutor<ScriptFileExecutionRequest> {

    private static ScriptFileExecutor INSTANCE;

    public synchronized static ScriptFileExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptFileExecutor();
        }
        return INSTANCE;
    }

    private ScriptFileExecutor() {
        ScriptResultConfiguration.getInstance();
    }

    @Override
    public Class<ScriptFileExecutionRequest> appliesTo() {
        return ScriptFileExecutionRequest.class;
    }

    @Override
    public void execute(ScriptFileExecutionRequest scriptExecutionRequest) throws ScriptDoesNotExistException, ScriptExecutionBuildException, MetadataAlreadyExistsException {

        File file = new File(scriptExecutionRequest.getFileName());
        Script script = null;
        if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
            JsonInputOperation jsonInputOperation = new JsonInputOperation(scriptExecutionRequest.getFileName());
            script = jsonInputOperation.getScript()
                    .orElseThrow(() -> new ScriptDoesNotExistException(""));
        } else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
            YamlInputOperation yamlInputOperation = new YamlInputOperation(scriptExecutionRequest.getFileName());
            script = yamlInputOperation.getScript()
                    .orElseThrow(() -> new ScriptDoesNotExistException(""));
        }

        // TODO: ActionSelection?
        ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
                .script(script)
                .exitOnCompletion(scriptExecutionRequest.isExit())
                .parameters(scriptExecutionRequest.getParameters())
                .impersonations(scriptExecutionRequest.getImpersonations().orElse(new HashMap<>()))
                // .actionSelectOperation(new ActionSelectOperation(scriptExecutionRequest.getActionSelect()))
                .environment(scriptExecutionRequest.getEnvironment())
                .build();

        io.metadew.iesi.metadata.definition.execution.script.ScriptExecution scriptExecution1 = new io.metadew.iesi.metadata.definition.execution.script.ScriptExecution(new ScriptExecutionKey(), scriptExecutionRequest.getMetadataKey(), scriptExecution.getExecutionControl().getRunId(), ScriptRunStatus.RUNNING, LocalDateTime.now(), null);
        ScriptExecutionConfiguration.getInstance().insert(scriptExecution1);

        scriptExecution.execute();

        scriptExecution1.updateScriptRunStatus(ScriptResultConfiguration.getInstance().get(new ScriptResultKey(scriptExecution1.getRunId(), -1L))
                .map(scriptResult -> ScriptRunStatus.valueOf(scriptResult.getStatus()))
                .orElseThrow(() -> new RuntimeException("Cannot find result of run id: " + scriptExecution1.getRunId())));
        scriptExecution1.setEndTimestamp(LocalDateTime.now());
        ScriptExecutionConfiguration.getInstance().insert(scriptExecution1);
    }
}
