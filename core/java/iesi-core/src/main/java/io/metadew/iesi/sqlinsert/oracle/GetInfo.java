package io.metadew.iesi.sqlinsert.oracle;

import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.sqlinsert.engine.ConfigFile;
import io.metadew.iesi.sqlinsert.engine.Engine;
import io.metadew.iesi.sqlinsert.engine.OutputFile;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;

public class GetInfo {

    public Engine tgt;
    // configuration
    public String schema;
    public String table;
    private FrameworkConfiguration frameworkConfiguration;

    public GetInfo(FrameworkConfiguration frameworkConfiguration) {
        this.tgt = new Engine("oracle");
        // Get ConfigFile
        this.setFrameworkConfiguration(frameworkConfiguration);
        ConfigFile cfg = new ConfigFile(FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "info.config");
        this.schema = cfg.getProperty("schema");
        this.table = cfg.getProperty("table");
    }

    public void doExec() {
        try {
            Class.forName(tgt.dbdriver);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver Not Available");
            e.printStackTrace();
            return;
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(tgt.connectionURL, tgt.user, tgt.password);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            try {
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                String QueryString = "";
                ResultSet rs = null;

                // QueryString = "select owner, table_name, column_name, data_type, char_length,
                // data_precision, data_scale" +
                // " " + "from all_tab_cols" +
                // " " + "where owner = \'" + this.schema.toUpperCase() + "\'" +
                // " " + "and table_name = \'" + this.table.toUpperCase() + "\'" +
                // " " + "order by column_id asc";

                QueryString = this.getInfoSQL();

                try {
                    rs = statement.executeQuery(QueryString);
                    // outputfile
                    OutputFile of = new OutputFile();
                    of.PrintToFile(rs, FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("modules.sqlinsert.run.tmp") + File.separator + "info.txt");
                    rs.close();
                } catch (Exception e) {
                    System.out.println(QueryString);
                    System.out.println("Query Actions Failed");
                    e.printStackTrace();
                }

                statement.close();

            } catch (SQLException e) {
                StringWriter StackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(StackTrace));
                System.out.println("database actions Failed");
                System.out.println(e.getMessage());
            } finally {
                // Close the connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Connection CLose Failed");
                    e.printStackTrace();
                }
            }

        } else {
            System.out.println("Connection Lost");
        }
    }

    public String getInfoSQL() {
        String QueryString = "";

        QueryString = "select owner, table_name, column_name, data_type, char_length, data_precision, data_scale" + " "
                + "from all_tab_cols" + " " + "where owner = \'" + this.schema.toUpperCase() + "\'" + " "
                + "and table_name = \'" + this.table.toUpperCase() + "\'" + " " + "order by column_id asc";

        return QueryString;
    }

    // Getters and Setters
    public FrameworkConfiguration getFrameworkConfiguration() {
        return frameworkConfiguration;
    }

    public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
    }
}