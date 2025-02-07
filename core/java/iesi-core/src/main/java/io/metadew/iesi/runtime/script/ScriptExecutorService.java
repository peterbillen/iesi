package io.metadew.iesi.runtime.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ScriptExecutorService {

    private Map<Class<? extends ScriptExecutionRequest>, ScriptExecutor> scriptExecutorMap;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptExecutorService INSTANCE;

    public synchronized static ScriptExecutorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptExecutorService();
        }
        return INSTANCE;
    }

    private ScriptExecutorService() {
        scriptExecutorMap = new HashMap<>();
        ScriptFileExecutor scriptFileExecutor = ScriptFileExecutor.getInstance();
        ScriptNameExecutor scriptNameExecutor = ScriptNameExecutor.getInstance();

        scriptExecutorMap.put(scriptFileExecutor.appliesTo(), scriptFileExecutor);
        scriptExecutorMap.put(scriptNameExecutor.appliesTo(), scriptNameExecutor);
    }

    @SuppressWarnings("unchecked")
    public void execute(ScriptExecutionRequest scriptExecutionRequest) throws MetadataDoesNotExistException, MetadataAlreadyExistsException, ScriptExecutionBuildException {
        ScriptExecutor scriptExecutor = scriptExecutorMap.get(scriptExecutionRequest.getClass());

        scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.SUBMITTED);
        ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);

        if (scriptExecutor == null) {
            LOGGER.error(MessageFormat.format("No Executor found for request type {0}", scriptExecutionRequest.getClass()));
            scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.DECLINED);
            ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
        } else {
            scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED);
            ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);

            scriptExecutor.execute(scriptExecutionRequest);

            scriptExecutionRequest.updateScriptExecutionRequestStatus(ScriptExecutionRequestStatus.COMPLETED);
            ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
        }
    }
}
