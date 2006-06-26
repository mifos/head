/**
 * 
 */
package org.mifos.framework.exceptions;

/**
 * @author rohitr
 *
 */
public class TableTagTypeParserException extends SystemException {
	
	private static final long serialVersionUID = 17456575695856234L;
	/**
	 * 
	 */
	public TableTagTypeParserException() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public TableTagTypeParserException(Object[] values, Throwable cause) {
		super(values, cause);
		// TODO Auto-generated constructor stub
	}

	public TableTagTypeParserException(Object[] values) {
		super(values);
		// TODO Auto-generated constructor stub
	}

	public TableTagTypeParserException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	public String getKey(){
		return "exception.framework.SystemException.TypeParseException";
	}
}
