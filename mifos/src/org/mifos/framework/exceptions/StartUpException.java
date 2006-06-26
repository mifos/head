package org.mifos.framework.exceptions;

import org.mifos.framework.util.helpers.ExceptionConstants;

public class StartUpException extends RuntimeException{
	protected String key = null;
	
	public StartUpException() {
	}
	
	public StartUpException(String key ) {
		this.key = key;
	}
	
	public StartUpException(Throwable cause) {
		super.initCause(cause);
	}
	
	public StartUpException(String key ,Throwable cause) {
		this.key = key;
		super.initCause(cause);
	}
	
	public String getKey() {
		if(null == key){
			return ExceptionConstants.STARTUP_EXCEPTION;
		}else{
			return this.key;
		}		
	}
	
	public void setKey(String key) {
		this.key = key;
	}
}
