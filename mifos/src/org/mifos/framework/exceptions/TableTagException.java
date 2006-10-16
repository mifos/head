/**
 * 
 */
package org.mifos.framework.exceptions;

public class TableTagException extends ApplicationException {

	// TODO: Existing callers are passing in a message rather
	// than a key.
	public TableTagException(String key) {
		super(key);
	}

	public TableTagException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getKey() {
		return "exception.framework.TableTagException";
	}

}
