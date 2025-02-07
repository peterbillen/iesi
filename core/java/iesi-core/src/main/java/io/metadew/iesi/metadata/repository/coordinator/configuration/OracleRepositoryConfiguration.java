package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.OracleDatabase;
import io.metadew.iesi.connection.database.connection.oracle.OracleDatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OracleRepositoryConfiguration extends RepositoryConfiguration {

    private final static Logger LOGGER = LogManager.getLogger();

    private String jdbcConnectionString;
    private String host;
    private String port;
    private String name;
    private String service;
    private String tnsAlias;
    private String schema;
    private String schemaUser;
    private String schemaUserPassword;
    private String writerUser;
    private String writerUserPassword;
    private String readerUser;
    private String readerUserPassword;


    public OracleRepositoryConfiguration(ConfigFile configFile) {
       super(configFile);
    }

    @Override
    void fromConfigFile(ConfigFile configFile) {
    	host = getSettingValue(configFile, "metadata.repository.oracle.host");
    	port = getSettingValue(configFile, "metadata.repository.oracle.port");
    	service = getSettingValue(configFile, "metadata.repository.oracle.service");
    	tnsAlias = getSettingValue(configFile, "metadata.repository.oracle.tnsalias");
    	schema = getSettingValue(configFile, "metadata.repository.oracle.schema");
    	name = getSettingValue(configFile, "metadata.repository.oracle.name");
    	schemaUser = getSettingValue(configFile, "metadata.repository.oracle.schema.user");
    	schemaUserPassword = getSettingValue(configFile, "metadata.repository.oracle.schema.user.password");
    	writerUser = getSettingValue(configFile, "metadata.repository.oracle.writer");
    	writerUserPassword = getSettingValue(configFile, "metadata.repository.oracle.writer.password");
    	readerUser = getSettingValue(configFile, "metadata.repository.oracle.reader");
    	readerUserPassword = getSettingValue(configFile, "metadata.repository.oracle.reader.password");
    	jdbcConnectionString = getSettingValue(configFile, "metadata.repository.connection.string");
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString;
        if (getJdbcConnectionString().isPresent()) {
        	actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
        	actualJdbcConnectionString = OracleDatabaseConnection.getConnectionUrl(getHost().orElse(""), Integer.parseInt(getPort().orElse("0")), getService().orElse(""), getTnsAlias().orElse(""));
        }

        final String finalJdbcConnectionString = actualJdbcConnectionString;
        if (getUser().isPresent()) {
                    OracleDatabaseConnection oracleDatabaseConnection = new OracleDatabaseConnection(finalJdbcConnectionString, getUser().get(), FrameworkCrypto.getInstance().decrypt(getUserPassword().orElse("")));
                    getSchema().ifPresent(oracleDatabaseConnection::setSchema);
                    OracleDatabase oracleDatabase = new OracleDatabase(oracleDatabaseConnection, getSchema().orElse(""));
                    databases.put("owner", oracleDatabase);
                    databases.put("writer", oracleDatabase);
                    databases.put("reader", oracleDatabase);
                }
        if (getWriter().isPresent()) {
            OracleDatabaseConnection oracleDatabaseConnection = new OracleDatabaseConnection(finalJdbcConnectionString, getWriter().get(), FrameworkCrypto.getInstance().decrypt(getWriterPassword().orElse("")));
            getSchema().ifPresent(oracleDatabaseConnection::setSchema);
            OracleDatabase oracleDatabase = new OracleDatabase(oracleDatabaseConnection, getSchema().orElse(""));
            databases.put("writer", oracleDatabase);
            databases.put("reader", oracleDatabase);
        }

        if(getReader().isPresent()) {
                OracleDatabaseConnection oracleDatabaseConnection = new OracleDatabaseConnection(finalJdbcConnectionString, getReader().get(), FrameworkCrypto.getInstance().decrypt(getReaderPassword().orElse("")));
                getSchema().ifPresent(oracleDatabaseConnection::setSchema);
                OracleDatabase oracleDatabase = new OracleDatabase(oracleDatabaseConnection, getSchema().orElse(""));
                databases.put("reader", oracleDatabase);
        }

        return new RepositoryCoordinator(databases);
    }

    public Optional<String> getJdbcConnectionString() {
        return Optional.ofNullable(jdbcConnectionString);
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public Optional<String> getPort() {
        return Optional.ofNullable(port);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getService() {
        return Optional.ofNullable(service);
    }

    public Optional<String> getTnsAlias() {
        return Optional.ofNullable(tnsAlias);
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    public Optional<String> getUser() {
        return Optional.ofNullable(schemaUser);
    }

    public Optional<String> getUserPassword() {
        return Optional.ofNullable(schemaUserPassword);
    }

    public Optional<String> getWriter() {
        return Optional.ofNullable(writerUser);
    }

    public Optional<String> getWriterPassword() {
        return Optional.ofNullable(writerUserPassword);
    }

    public Optional<String> getReader() {
        return Optional.ofNullable(readerUser);
    }

    public Optional<String> getReaderPassword() {
        return Optional.ofNullable(readerUserPassword);
    }
}
