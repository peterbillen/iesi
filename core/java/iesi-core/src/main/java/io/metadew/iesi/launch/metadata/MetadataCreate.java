package io.metadew.iesi.launch.metadata;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import org.apache.commons.cli.*;

public class MetadataCreate {
    private final String type;
    private final FrameworkInstance frameworkInstance;

    public MetadataCreate(FrameworkInstance frameworkInstance, String[] args) throws ParseException {
        this.frameworkInstance = frameworkInstance;

        Options createOptions = new Options()
                .addOption(Option.builder("type")
                        .hasArg(false)
                        .required()
                        .desc("type of repository to create")
                        .build());

        CommandLineParser createParser = new DefaultParser();
        CommandLine cmd = createParser.parse(createOptions, args);
        this.type = cmd.getOptionValue("type");
    }

    public void run() {
        frameworkInstance.getMetadataControl();
    }
}
