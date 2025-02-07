package io.metadew.iesi.launch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.execution.FrameworkRuntime;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import org.apache.commons.cli.*;

import java.io.InputStream;

/**
 * The connection launcher is the entry point to access a specific connection
 * operations.
 *
 * @author peter.billen
 */
public class ConnectionLauncher {

    private static boolean actionMatch = false;
    private static FrameworkExecution frameworkExecution;

    public static void main(String[] args) {

        Option oHelp = new Option("help", "print this message");
        Option oName = new Option("name", true, "define the connection name");
        Option oIni = new Option("ini", true, "define the initialization file");
        Option oConfig = new Option("config", true, "define the connection configuration file");
        Option oFile = new Option("file", true, "path to be used to for backup or restore");
        Option oExit = new Option("exit", true, "define if an explicit exit is required");

        // create Options object
        Options options = new Options();
        // add options
        options.addOption(oHelp);
        options.addOption(oName);
        options.addOption(oIni);
        options.addOption(oConfig);
        options.addOption(oFile);
        options.addOption(oExit);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("[command]", options);
                System.exit(0);
            }

            // Define the exit behaviour
            boolean exit = true;
            if (line.hasOption("exit")) {
                switch (line.getOptionValue("exit").trim().toLowerCase()) {
                    case "y":
                    case "true":
                        exit = true;
                        break;
                    case "n":
                    case "false":
                        exit = false;
                        break;
                    default:
                        break;
                }
            }

            // Create the framework instance
            FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
            String configFile = "";
            if (line.hasOption("ini")) {
                frameworkInitializationFile.setName(line.getOptionValue("ini"));
                System.out.println("Option -ini (ini) value = " + frameworkInitializationFile.getName());
            } else if (line.hasOption("config")) {
                configFile = line.getOptionValue("config");
            }

            // TODO: Combine FWK Isntance and FWK Exec
            FrameworkInstance.getInstance().init(frameworkInitializationFile, new FrameworkExecutionContext(new Context("connection", "")));

            // Execute
            writeHeaderMessage();
            DataObjectOperation dataObjectOperation = new DataObjectOperation(configFile);
            ObjectMapper objectMapper = new ObjectMapper();
            Connection connection = objectMapper.convertValue(dataObjectOperation.getDataObject(), Connection.class);
            // TODO rework getting the connection
            ConnectionOperation connectionOperation = new ConnectionOperation();
            Database database = connectionOperation.getDatabase(connection);

            if (database == null) {
                throw new RuntimeException("Error establishing DB connection");
            }

            String sqlStatement = "";
            // Run the action
            // Make sure the SQL statement is ended with a ;
            if (!sqlStatement.trim().endsWith(";")) {
                sqlStatement = sqlStatement + ";";
            }

            SqlScriptResult sqlScriptResult;
            InputStream inputStream = FileTools.convertToInputStream(sqlStatement,
                    null);
            sqlScriptResult = database.executeScript(inputStream);

            // Evaluate result
            //this.getActionExecution().getActionControl().logOutput("sys.out", sqlScriptResult.getSystemOutput());

            if (sqlScriptResult.getReturnCode() != 0) {
                //this.getActionExecution().getActionControl().logOutput("err.out", sqlScriptResult.getErrorOutput());
                throw new RuntimeException("Error execting SQL query");
            }

            writeFooterMessage();


            if (actionMatch) {
                System.out.println();
                System.out.println("metadata.launcher.end");
                endLauncher(0, exit);
            } else {
                System.out.println("No valid arguments have been provided, type -help for help.");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            endLauncher(1, true);
        }

    }

    private static void endLauncher(int status, boolean exit) {
        FrameworkRuntime.getInstance().terminate();
        if (exit) {
            System.exit(status);
        }
        FrameworkInstance.getInstance().shutdown();
    }

    private static void writeHeaderMessage() {
        if (!actionMatch) {
            System.out.println("metadata.launcher.start");
            System.out.println();
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    private static void writeFooterMessage() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    public static FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public static void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        ConnectionLauncher.frameworkExecution = frameworkExecution;
    }

}