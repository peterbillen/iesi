package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public abstract class Configuration2<T extends Metadata, V extends MetadataKey> {

    // TODO: once metadata control or framework instance become singleton, this class can become an interface
    private RepositoryCoordinator repositoryCoordinator;
    private String tableName;

    public Configuration2(RepositoryCoordinator repositoryCoordinator, String tableName) {
        this.repositoryCoordinator = repositoryCoordinator;
        this.tableName = tableName;
    }


    // TODO: change metadataControl to MetadataRepository
    // TODO: make singleton

    public abstract Optional<T> get(V metadataKey) throws SQLException;
    public abstract List<T> getAll() throws SQLException;
    public abstract void delete(V metadataKey) throws MetadataDoesNotExistException, SQLException;
    public abstract void insert(T metadata) throws MetadataAlreadyExistsException, SQLException;
	public abstract boolean exists(T metadata) throws SQLException;
    public abstract boolean exists(V key) throws SQLException;

	public void update(T metadata) throws SQLException, MetadataDoesNotExistException {
        try {
            delete((V) metadata.getMetadataKey());
            insert(metadata);
        } catch (MetadataDoesNotExistException e) {
            throw e;

        } catch (MetadataAlreadyExistsException e) {

        }
    }

    public RepositoryCoordinator getRepositoryCoordinator() {
        return repositoryCoordinator;
    }

    public String getTableName() {
        return tableName;
    }
}
