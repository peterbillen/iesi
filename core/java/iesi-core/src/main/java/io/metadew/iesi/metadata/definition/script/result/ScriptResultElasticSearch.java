package io.metadew.iesi.metadata.definition.script.result;

import io.metadew.iesi.connection.elasticsearch.ElasticSearchDocument;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.time.LocalDateTime;

public class ScriptResultElasticSearch implements ElasticSearchDocument {

    private final String runId;
    private final Long processId;
    private final Long parentProcessId;
    private final String scriptId;
    private final String scriptName;
    private final Long scriptVersion;
    private final String environment;
    private final String status;
    private final LocalDateTime startTimestamp;
    private final LocalDateTime endTimestamp;

    public ScriptResultElasticSearch(ScriptResult scriptResult) {
        runId = scriptResult.getMetadataKey().getRunId();
        processId = scriptResult.getMetadataKey().getProcessId();
        parentProcessId = scriptResult.getParentProcessId();
        scriptId = scriptResult.getScriptId();
        scriptName = scriptResult.getScriptName();
        scriptVersion = scriptResult.getScriptVersion();
        environment = scriptResult.getEnvironment();
        status = scriptResult.getStatus();
        startTimestamp = scriptResult.getStartTimestamp();
        endTimestamp = scriptResult.getEndTimestamp();
    }

    public ScriptResultElasticSearch(String runId, Long processId, Long parentProcessId,
                                     String scriptId, String scriptName, Long scriptVersion, String environment,
                                     String status, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        this.runId = runId;
        this.processId = processId;
        this.parentProcessId = parentProcessId;
        this.scriptId = scriptId;
        this.scriptName = scriptName;
        this.scriptVersion = scriptVersion;
        this.environment = environment;
        this.status = status;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    @Override
    public Marker getLoggingMarker() {
        return MarkerManager.getMarker("scriptresults");
    }

    public String getRunId() {
        return runId;
    }

    public Long getProcessId() {
        return processId;
    }

    public Long getParentProcessId() {
        return parentProcessId;
    }

    public String getScriptId() {
        return scriptId;
    }

    public String getScriptName() {
        return scriptName;
    }

    public Long getScriptVersion() {
        return scriptVersion;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }
}
