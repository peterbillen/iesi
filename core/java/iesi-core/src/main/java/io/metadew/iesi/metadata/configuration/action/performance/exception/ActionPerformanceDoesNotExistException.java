package io.metadew.iesi.metadata.configuration.action.performance.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;

public class ActionPerformanceDoesNotExistException extends MetadataDoesNotExistException {

	private static final long serialVersionUID = 1L;

	public ActionPerformanceDoesNotExistException(String message) {
        super(message);
    }

}
