package org.mifos.framework.exceptions;

public class TableTagTypeParserException extends SystemException {

	public TableTagTypeParserException(Throwable cause) {
		super(cause);
	}
	
	public TableTagTypeParserException(String key) {
		super(key);
	}

	@Override
	public String getKey() {
		return "exception.framework.SystemException.TypeParseException";
	}
}
