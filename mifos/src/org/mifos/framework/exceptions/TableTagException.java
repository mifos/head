/**
 * 
 */
package org.mifos.framework.exceptions;

/**
 * @author rohitr
 *
 */
public class TableTagException extends ApplicationException {
	
	public TableTagException() {
		
	}
	
	public TableTagException(String key) {
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.exceptions.ApplicationException#getKey()
	 */
	@Override
	public String getKey() {
		return "exception.framework.TableTagException";
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.exceptions.ApplicationException#getValues()
	 */
	@Override
	public Object[] getValues() {
		// TODO Auto-generated method stub
		return super.getValues();
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.exceptions.ApplicationException#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String key) {
		// TODO Auto-generated method stub
		super.setKey(key);
	}

}
