package io.metadew.iesi.framework.operation;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.configuration.FrameworkPluginConfiguration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class FrameworkPluginOperation {

    private String pluginConfigurationFile;

    public FrameworkPluginOperation() {
    }

    public boolean verifyPlugins(String configurationToVerify) {
        boolean result = false;
        for (FrameworkPluginConfiguration frameworkPluginConfiguration : FrameworkControl.getInstance().getFrameworkPluginConfigurationList()) {
            StringBuilder configurationFile = new StringBuilder();
            configurationFile.append(frameworkPluginConfiguration.getFrameworkPlugin().getPath());
            configurationFile.append(FrameworkFolderConfiguration.getInstance().getFolderPath("metadata.conf"));
            configurationFile.append(File.separator);
            configurationFile.append(configurationToVerify);
            String filePath = FilenameUtils.normalize(configurationFile.toString());
            if (FileTools.exists(filePath)) {
                this.setPluginConfigurationFile(filePath);
                result = true;
                break;
            }
        }
        return result;
    }

    // Getters and setters
    public String getPluginConfigurationFile() {
        return pluginConfigurationFile;
    }

    public void setPluginConfigurationFile(String pluginConfigurationFile) {
        this.pluginConfigurationFile = pluginConfigurationFile;
    }

}