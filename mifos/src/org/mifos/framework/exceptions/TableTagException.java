/**
 * 
 */
package org.mifos.framework.exceptions;

public class TableTagException extends ApplicationException {
	
	public TableTagException() {
		
	}
	
	public TableTagException(String key) {
	}

	public TableTagException(Throwable th) {
		super(th);
	}
	
	@Override
	public String getKey() {
		return "exception.framework.TableTagException";
	}

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
