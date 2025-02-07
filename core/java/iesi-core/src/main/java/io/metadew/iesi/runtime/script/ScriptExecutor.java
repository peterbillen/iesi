package io.metadew.iesi.runtime.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.script.ScriptExecutionBuildException;

public interface ScriptExecutor <T extends ScriptExecutionRequest> {

    public Class<T> appliesTo();
    public void execute(T scriptExecutionRequest) throws MetadataDoesNotExistException, ScriptExecutionBuildException, MetadataAlreadyExistsException;
}
