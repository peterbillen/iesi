package io.metadew.iesi.metadata.configuration.action.design.exception;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;

public class ActionParameterDesignTraceAlreadyExistsException extends MetadataAlreadyExistsException {

	private static final long serialVersionUID = 1L;

	public ActionParameterDesignTraceAlreadyExistsException(String message) {
        super(message);
    }

}
