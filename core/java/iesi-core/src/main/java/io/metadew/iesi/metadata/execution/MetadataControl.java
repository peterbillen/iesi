package io.metadew.iesi.metadata.execution;

import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Class to centralize the shared metadata that will be used by the framework
 *
 * @author peter.billen
 */
public class MetadataControl {

    private boolean valid = false;

    private final static Logger LOGGER = LogManager.getLogger();

    private static MetadataControl INSTANCE;
    private List<MetadataRepository> metadataRepositories;

    public static MetadataControl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataControl();
        }
        return INSTANCE;
    }

    private MetadataControl() {}

    public void init(List<MetadataRepository> metadataRepositories) {
        if (metadataRepositories.isEmpty()) {
            LOGGER.warn("IESI instance does not contain any Metadata Repositories");
        }
        this.metadataRepositories = metadataRepositories;

//        // Set shared metadata
//        for (MetadataRepository metadataRepository : metadataRepositories) {
//            this.setMetadataRepository(metadataRepository);
//        }
//        // Check if repositories are correctly set
//        this.checkValidity();
//        if (!this.isValid()) {
//            throw new RuntimeException("framework.metadata.incomplete");
//        }
    }

//    private void checkValidity() {
//        boolean result = true;
//        // Mandatory repository settings
//        if (this.getCatalogMetadataRepository() == null) result = false;
//        if (this.getConnectivityMetadataRepository() == null) result = false;
//        if (this.getControlMetadataRepository() == null) result = false;
//        if (this.getDesignMetadataRepository() == null) result = false;
//        if (this.getResultMetadataRepository() == null) result = false;
//        if (this.getTraceMetadataRepository() == null) result = false;
//
//        this.setValid(result);
//    }

//    private void setMetadataRepository(MetadataRepository metadataRepository) {
//        if (metadataRepository.getCategory().equalsIgnoreCase("connectivity")) {
//            this.setConnectivityMetadataRepository((ConnectivityMetadataRepository) metadataRepository);
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("catalog")) {
//            this.setCatalogMetadataRepository((CatalogMetadataRepository) metadataRepository);
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("control")) {
//            this.setControlMetadataRepository((ControlMetadataRepository) metadataRepository);
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("design")) {
//            this.setDesignMetadataRepository((DesignMetadataRepository) metadataRepository);
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("trace")) {
//            this.setTraceMetadataRepository((TraceMetadataRepository) metadataRepository);
//        } else if (metadataRepository.getCategory().equalsIgnoreCase("result")) {
//            this.setResultMetadataRepository((ResultMetadataRepository) metadataRepository);
//        } else {
//            throw new RuntimeException(MessageFormat.format("No Metadata repository of type {0} can be set", metadataRepository.getCategory()));
//        }
//    }

    // Getters and Setters
    public Optional<MetadataRepository> getCatalogMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("catalog"))
                .findFirst();
    }

//    public void setCatalogMetadataRepository(CatalogMetadataRepository catalogMetadataRepository) {
//        this.catalogMetadataRepository = catalogMetadataRepository;
//    }
    
    public Optional<MetadataRepository> getConnectivityMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("connectivity"))
                .findFirst();
    }

//    public void setConnectivityMetadataRepository(
//            ConnectivityMetadataRepository connectivityMetadataRepository) {
//        this.connectivityMetadataRepository = connectivityMetadataRepository;
//    }

    public Optional<MetadataRepository> getTraceMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("trace"))
                .findFirst();
    }

//    public void setTraceMetadataRepository(TraceMetadataRepository traceMetadataRepository) {
//        this.traceMetadataRepository = traceMetadataRepository;
//    }

    public Optional<MetadataRepository> getResultMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("result"))
                .findFirst();
    }

    //    public void setResultMetadataRepository(ResultMetadataRepository resultMetadataRepository) {
//        this.resultMetadataRepository = resultMetadataRepository;
//    }

    public Optional<MetadataRepository> getDesignMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("design"))
                .findFirst();
    }

//    public void setDesignMetadataRepository(DesignMetadataRepository designMetadataRepository) {
//        this.designMetadataRepository = designMetadataRepository;
//    }

    public Optional<MetadataRepository> getMonitorMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("monitor"))
                .findFirst();
    }

//    public void setMonitorMetadataRepository(MonitorMetadataRepository monitorMetadataRepository) {
//        this.monitorMetadataRepository = monitorMetadataRepository;
//    }

//    public boolean isGeneral() {
//        return general;
//    }
//
//    public void setGeneral(boolean general) {
//        this.general = general;
//    }

    public Optional<MetadataRepository> getLedgerMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("ledger"))
                .findFirst();
    }

//    public void setLedgerMetadataRepository(LedgerMetadataRepository ledgerMetadataRepository) {
//        this.ledgerMetadataRepository = ledgerMetadataRepository;
//    }

    public Optional<MetadataRepository> getControlMetadataRepository() {
        return metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getCategory().equalsIgnoreCase("control"))
                .findFirst();
    }

//    public void setControlMetadataRepository(ControlMetadataRepository controlMetadataRepository) {
//        this.controlMetadataRepository = controlMetadataRepository;
//    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}