package io.metadew.iesi.launch;

import io.metadew.iesi.assembly.execution.AssemblyExecution;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * The assembly launcher is entry point to launch the assembly of the framework.
 * It is used only to prepare the packaging, not in a production mode.
 *
 * @author peter.billen
 */
public class AssemblyLauncher {

    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options()
                .addOption(Option.builder("help").desc("print this message").build())
                .addOption(Option.builder("repository").hasArg().desc("set repository location").required().build())
                .addOption(Option.builder("development").hasArg().desc("set development location").required().build())
                .addOption(Option.builder("sandbox").hasArg().desc("set sandbox location").required().build())
                .addOption(Option.builder("instance").hasArg().desc("provide target instance").required().build())
                .addOption(Option.builder("version").hasArg().desc("provide target version").required().build())
                .addOption(Option.builder("configuration").hasArg().desc("provide target configuration").required().build())
                .addOption(Option.builder("test").desc("test assembly flag").build())
                .addOption(Option.builder("distribution").desc("distribution flag").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("[command]", options);
            System.exit(0);
        }

        writeHeaderMessage();
        String repository = line.getOptionValue("repository");
        System.out.println("Option -repository (repository) value = " + repository);

        String development = line.getOptionValue("development");
        System.out.println("Option -development (development) value = " + development);

        String sandbox = line.getOptionValue("sandbox");
        System.out.println("Option -sandbox (sandbox) value = " + sandbox);

        String instance = line.getOptionValue("instance");
        System.out.println("Option -instance (instance) value = " + instance);

        String version = line.getOptionValue("version");
        System.out.println("Option -version (version) value = " + version);

        String configuration = line.getOptionValue("configuration");
        boolean applyConfiguration = true;
        System.out.println("Option -configuration (configuration) value = " + configuration);

        boolean testAssembly;
        if (line.hasOption("test")) {
            testAssembly = true;
            System.out.println("Option -test (test assembly) value = " + testAssembly);
        } else {
            testAssembly = false;
            System.out.println("Option -test (test assembly) value = " + testAssembly);
        }

        boolean distribution;
        if (line.hasOption("distribution")) {
            distribution = true;
            System.out.println("Option -distribution (distribution) value = " + distribution);
        } else {
            distribution = false;
            System.out.println("Option -distribution (distribution) value = " + distribution);
        }

        // FWK init
        FrameworkConfiguration frameworkConfiguration = FrameworkConfiguration.getInstance();
        frameworkConfiguration.initAssembly(repository);

        FrameworkCrypto.getInstance();

        FrameworkControl frameworkControl = FrameworkControl.getInstance();
        frameworkControl.init("assembly");

        AssemblyExecution assemblyExecution = new AssemblyExecution(repository, development, sandbox, instance,
                version, configuration, applyConfiguration, testAssembly, distribution);
        assemblyExecution.execute();

    }

    private static void writeHeaderMessage() {
        System.out.println("Invoking the assembly execution...");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println();
    }

}