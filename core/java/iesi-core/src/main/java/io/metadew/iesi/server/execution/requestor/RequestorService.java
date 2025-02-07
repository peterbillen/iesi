//package io.metadew.iesi.server.execution.requestor;
//
//import io.metadew.iesi.framework.instance.FrameworkInstance;
//
//import javax.sql.rowset.CachedRowSet;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.sql.SQLException;
//
//public class RequestorService {
//
//	public CachedRowSet crs;
//
//	// fields
//	public String requestId;
//
//	public RequestorService() {
//		this.clearProcessors();
//		this.createProcessors();
//	}
//
//	// Initialize
//	public void clearProcessors() {
//		String QueryString = "delete from " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
//				.getTableNameByLabel("RequestExecutions");
//		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
//	}
//
//	public void createProcessors() {
//		String QueryString = "insert into " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
//				.getTableNameByLabel("RequestExecutions") + " (exe_id,request_id) values (1,-1)";
//		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeUpdate(QueryString);
//	}
//
//	public int getAvailableProcessor() {
//		int executionId = -1;
//
//		String QueryString = "select min(exe_id) exe_id from " + FrameworkInstance.getInstance()
//				.getExecutionServerRepositoryConfiguration().getTableNameByLabel("RequestExecutions")
//				+ " where request_id = -1";
//		this.crs = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString,
//				"reader");
//
//		try {
//			while (crs.next()) {
//				executionId = crs.getInt("EXE_ID");
//			}
//
//			crs.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		return executionId;
//	}
//
//	public void getNextQueID() {
//		String QueryString = "";
//		CachedRowSet crs = null;
//		QueryString = "select min(load_tms) LOAD_TMS from " + FrameworkInstance.getInstance()
//				.getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests") + " where exe_id = -1";
//		crs = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString,
//				"reader");
//		String loadTimestamp = "";
//		try {
//			while (crs.next()) {
//				loadTimestamp = crs.getString("LOAD_TMS");
//				System.out.println(loadTimestamp);
//			}
//			crs.close();
//
//			// Get the request identifier
//			QueryString = "select request_id from " + FrameworkInstance.getInstance()
//					.getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests") + " where load_tms = '"
//					+ loadTimestamp + "'";
//			crs = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString,
//					"reader");
//			while (crs.next()) {
//				this.requestId = crs.getString("REQUEST_ID");
//			}
//			crs.close();
//
//		} catch (SQLException e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//	}
//
//	public void execute() {
//		// Move to the runnable
//
//		// Get next que_id
//		this.getNextQueID();
//
//		// check for processor
//		int i = 1;
//		int availableExecutionId = -1;
//		boolean prcFound = false;
//		while (i == 1 && prcFound == false) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException ex) {
//				Thread.currentThread().interrupt();
//			}
//
//			availableExecutionId = this.getAvailableProcessor();
//			if (availableExecutionId > 0)
//				prcFound = true;
//		}
//
//		// set processor
//		RequestorProcessor prc = new RequestorProcessor(availableExecutionId, this.requestId);
//		prc.execute();
//		System.out.println(this.requestId);
//
//	}
//
//	public boolean execListen() {
//		int avaiableRequests = -1;
//		int availableProcesses = -1;
//
//		String QueryString = "select count(request_id) 'REQUEST_NB' from " + FrameworkInstance.getInstance()
//				.getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests") + " where exe_id = -1";
//		this.crs = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString,
//				"reader");
//
//		try {
//			while (crs.next()) {
//				avaiableRequests = crs.getInt("REQUEST_NB");
//			}
//
//			// Get available processes
//			QueryString = "select count(exe_id) 'PRC_NB' from " + FrameworkInstance.getInstance()
//					.getExecutionServerRepositoryConfiguration().getTableNameByLabel("RequestExecutions")
//					+ " where request_id = -1";
//			this.crs = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeQuery(QueryString,
//					"reader");
//			while (crs.next()) {
//				availableProcesses = crs.getInt("PRC_NB");
//			}
//
//			this.crs.close();
//		} catch (Exception e) {
//			StringWriter StackTrace = new StringWriter();
//			e.printStackTrace(new PrintWriter(StackTrace));
//		}
//
//		if (avaiableRequests > 0 && availableProcesses > 0) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//}
