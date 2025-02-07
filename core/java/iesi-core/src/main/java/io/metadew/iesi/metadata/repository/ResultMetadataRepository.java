package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.action.performance.ActionPerformanceConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.request.RequestResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ResultMetadataRepository extends MetadataRepository {
    private static final Logger LOGGER = LogManager.getLogger();

    public ResultMetadataRepository(String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, scope, instanceName, repositoryCoordinator);
        ScriptResultConfiguration.getInstance().init(this);
        ActionResultConfiguration.getInstance().init(this);
        ActionResultOutputConfiguration.getInstance().init(this);
        ScriptResultOutputConfiguration.getInstance().init(this);
        ActionPerformanceConfiguration.getInstance().init(this);
        RequestResultConfiguration.getInstance().init(this);
    }

    @Override
    public String getDefinitionFileName() {
        return "ResultTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ResultObjects.json";
    }

    @Override
    public String getCategory() {
        return "result";
    }

    @Override
    public String getCategoryPrefix() {
        return "RES";
    }

    @Override
    public void save(DataObject dataObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("log")) {
//            Script script = objectMapper.convertValue(dataObject.getData(), Script.class);
//            ScriptConfiguration scriptConfiguration = new ScriptConfiguration(script,
//                    frameworkExecution.getFrameworkInstance());
//            executeUpdate(scriptConfiguration.getInsertStatement());
        } else {
            LOGGER.trace(MessageFormat.format("Result repository is not responsible for loading saving {0}", dataObject.getType()));
        }
    }
}
