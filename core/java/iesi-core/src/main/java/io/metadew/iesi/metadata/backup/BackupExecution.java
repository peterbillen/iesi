package io.metadew.iesi.metadata.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.connection.tools.OutputTools;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.operation.DataObjectOperation;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BackupExecution {

	// Constructors
	public BackupExecution() {
	}

	// Methods
	public void execute(String path) {
		// TODO fix logging
		// this.getFrameworkExecution().getFrameworkLog().log("metadata.backup.start",
		// Level.INFO);

		// Log Start
		// this.getExecutionControl().logStart(this);
		// this.setProcessId(0L);

		// Create Target Folder
		if (FolderTools.exists(path)) {
			throw new RuntimeException("metadata.backup.folder.exists");
		} else {
			FolderTools.createFolder(path, true);
		}

		try {
			// TODO loop all available objects
			this.saveAllObjects(path, "Environment");
			this.saveAllObjects(path, "Connection");
			this.saveAllObjects(path, "Impersonation");

			this.saveAllObjects(path, "Component");
			this.saveAllObjects(path, "Script");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Log End
		// this.getExecutionControl().endExecution(this);
		// this.getFrameworkExecution().getFrameworkLog().log("metadata.backup.end",
		// Level.INFO);

		// Exit the execution
		// this.getEoControl().endExecution();
	}

	public void saveAllObjects(String path, String dataObjectName) {
		try {
			String subjectAreaPath = path + File.separator + dataObjectName.toLowerCase() + "s";
			FolderTools.createFolder(subjectAreaPath, true);

			String configurationClassName = "io.metadew.iesi.metadata.configuration" + "." + dataObjectName
					+ "Configuration";
			String objectClassName = "io.metadew.iesi.metadata.definition" + "." + dataObjectName;

			Class configurationClassRef = Class.forName(configurationClassName);
			Class instanceParams[] = {};
			Object instance = configurationClassRef.getDeclaredConstructor(instanceParams).newInstance();

			Class objectClassRef = Class.forName(objectClassName);
			Method getName = objectClassRef.getDeclaredMethod("getScriptName");

			Method getAllObjects = configurationClassRef.getDeclaredMethod("getAllObjects");
			List<?> objects = (List<?>) getAllObjects.invoke(instance);

			ObjectMapper mapper = new ObjectMapper();
			for (Object object : objects) {
				String name = (String) getName.invoke(object);
				String fileName = name + ".json";
				OutputTools.createOutputFile(fileName, subjectAreaPath, "",
						mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("issue occurred");
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	public void executeOld(String path) {
		// this.getFrameworkExecution().getFrameworkLog().log("metadata.backup.start",
		// Level.INFO);

		// Log Start
		// this.getExecutionControl().logStart(this);
		// this.setProcessId(0L);

		// Get source configuration
		DataObjectOperation dataObjectOperation = new DataObjectOperation(
				FrameworkFolderConfiguration.getInstance()
						.getFolderAbsolutePath("metadata.def") + File.separator + "MetadataTables.json");

		// Create backup location
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		String folderName = "";
		if (path.trim().equalsIgnoreCase("")) {
			folderName = FrameworkFolderConfiguration.getInstance()
					.getFolderAbsolutePath("metadata.def") + File.separator + sdf.format(timestamp);
			;

			// Ensure the base folder structure exists
			FolderTools.createFolder(FrameworkFolderConfiguration.getInstance()
					.getFolderAbsolutePath("data")); // Data
			FolderTools.createFolder(FrameworkFolderConfiguration.getInstance()
					.getFolderAbsolutePath("data")); // Backups
			FolderTools.createFolder(FrameworkFolderConfiguration.getInstance()
					.getFolderAbsolutePath("data")); // Backups Metadata
		} else {
			folderName = path;
		}

		// Create the folder name for the backup
		FolderTools.deleteFolder(folderName, true);
		FolderTools.createFolder(folderName);

		ObjectMapper objectMapper = new ObjectMapper();
		for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
			// Metadata Tables
			if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
				MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);

				// Get source data for migration
				// MetadataExtractOperation metadataExtractOperation = new
				// MetadataExtractOperation(this.getFrameworkInstance(),
				// this.getExecutionControl());
				// metadataExtractOperation.execute(metadataTable, folderName);

			} else {

			}
		}

		// Log End
		// this.getExecutionControl().endExecution(this);
		// this.getFrameworkExecution().getFrameworkLog().log("metadata.backup.end",
		// Level.INFO);

		// Exit the execution
		// this.getEoControl().endExecution();
	}

}