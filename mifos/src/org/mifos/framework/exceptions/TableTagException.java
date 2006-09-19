/**
 * 
 */
package org.mifos.framework.exceptions;

public class TableTagException extends ApplicationException {
	
	public TableTagException() {
	}
	
	// TODO: Existing callers are passing in a message rather
	// than a key.
	public TableTagException(String key) {
	}

	public TableTagException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public String getKey() {
		return "exception.framework.TableTagException";
	}

}
