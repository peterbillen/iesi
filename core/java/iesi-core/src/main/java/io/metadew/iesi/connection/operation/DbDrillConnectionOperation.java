package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DrillDatabase;
import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnection;
import io.metadew.iesi.connection.tools.ConnectionTools;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import io.metadew.iesi.metadata.definition.connection.ConnectionTypeParameter;

import java.util.ArrayList;
import java.util.List;

public class DbDrillConnectionOperation {

	private boolean missingMandatoryFields;
	private List<String> missingMandatoryFieldsList;

	public DbDrillConnectionOperation() {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Database getDatabase(Connection connection) {
		this.setMissingMandatoryFieldsList(new ArrayList());

		Database database = null;

		String connectionMode = "";
		String clusterNames = "";
		String directoryName = "";
		String clusterId = "";
		String schemaName = "";
		String triesParameter = "";
		String userName = "";
		String userPassword = "";

		for (ConnectionParameter connectionParameter : connection.getParameters()) {
			if (connectionParameter.getName().equalsIgnoreCase("mode")) {
				connectionMode = connectionParameter.getValue();
				connectionMode = FrameworkControl.getInstance()
						.resolveConfiguration(connectionMode);
			} else if (connectionParameter.getName().equalsIgnoreCase("cluster")) {
				clusterNames = connectionParameter.getValue();
				clusterNames = FrameworkControl.getInstance().resolveConfiguration(clusterNames);
			} else if (connectionParameter.getName().equalsIgnoreCase("directory")) {
				directoryName = connectionParameter.getValue();
				directoryName = FrameworkControl.getInstance().resolveConfiguration(directoryName);
			} else if (connectionParameter.getName().equalsIgnoreCase("clusterId")) {
				clusterId = connectionParameter.getValue();
				clusterId = FrameworkControl.getInstance().resolveConfiguration(clusterId);
			} else if (connectionParameter.getName().equalsIgnoreCase("schema")) {
				schemaName = connectionParameter.getValue();
				schemaName = FrameworkControl.getInstance().resolveConfiguration(schemaName);
			} else if (connectionParameter.getName().equalsIgnoreCase("tries")) {
				triesParameter = connectionParameter.getValue();
				triesParameter = FrameworkControl.getInstance()
						.resolveConfiguration(triesParameter);
			}
		}

		// Check Mandatory Parameters
		this.setMissingMandatoryFields(false);
		ConnectionType connectionType = ConnectionTools.getConnectionType(connection.getType());
		for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
			if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
				if (connectionTypeParameter.getName().equalsIgnoreCase("mode")) {
					if (connectionMode.trim().equalsIgnoreCase(""))
						this.addMissingField("mode");
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("cluster")) {
					if (clusterNames.trim().equalsIgnoreCase(""))
						this.addMissingField("cluster");
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("directory")) {
					if (directoryName.trim().equalsIgnoreCase(""))
						this.addMissingField("directory");
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("clusterId")) {
					if (clusterId.trim().equalsIgnoreCase(""))
						this.addMissingField("clusterId");
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("schema")) {
					if (schemaName.trim().equalsIgnoreCase(""))
						this.addMissingField("schema");
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("tries")) {
					if (triesParameter.trim().equalsIgnoreCase(""))
						this.addMissingField("tries");
				}
			}
		}

		if (this.isMissingMandatoryFields()) {
			String message = "Mandatory fields missing for connection " + connection.getName();
			throw new RuntimeException(message);
		}

		// Decrypt Parameters
		for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
			if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
				if (connectionTypeParameter.getName().equalsIgnoreCase("mode")) {
					connectionMode = FrameworkCrypto.getInstance().decrypt(connectionMode);
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("cluster")) {
					clusterNames = FrameworkCrypto.getInstance().decrypt(clusterNames);
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("directory")) {
					directoryName = FrameworkCrypto.getInstance().decrypt(directoryName);
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("clusterId")) {
					clusterId = FrameworkCrypto.getInstance().decrypt(clusterId);
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("schema")) {
					schemaName = FrameworkCrypto.getInstance().decrypt(triesParameter);
				} else if (connectionTypeParameter.getName().equalsIgnoreCase("tries")) {
					triesParameter = FrameworkCrypto.getInstance().decrypt(userName);
				}
			}
		}

		DrillDatabaseConnection drillDatabaseConnection = new DrillDatabaseConnection(connectionMode, clusterNames,
				directoryName, clusterId, schemaName, triesParameter, userName, userPassword);
		database = new DrillDatabase(drillDatabaseConnection, "");

		return database;
	}

	protected void addMissingField(String fieldName) {
		this.setMissingMandatoryFields(true);
		this.getMissingMandatoryFieldsList().add(fieldName);
	}

	public List<String> getMissingMandatoryFieldsList() {
		return missingMandatoryFieldsList;
	}

	public void setMissingMandatoryFieldsList(List<String> missingMandatoryFieldsList) {
		this.missingMandatoryFieldsList = missingMandatoryFieldsList;
	}

	public boolean isMissingMandatoryFields() {
		return missingMandatoryFields;
	}

	public void setMissingMandatoryFields(boolean missingMandatoryFields) {
		this.missingMandatoryFields = missingMandatoryFields;
	}

}