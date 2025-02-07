package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.Optional;

public class FileStoreRepositoryConfiguration extends RepositoryConfiguration {

    private String path;


    public FileStoreRepositoryConfiguration(ConfigFile configFile) {
        super(configFile);
    }

    @Override
    void fromConfigFile(ConfigFile configFile) {
    	path = getSettingValue(configFile, "metadata.repository.filestore.path");
    }

    @Override
    public RepositoryCoordinator toRepository() {
        return null;
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }
}
