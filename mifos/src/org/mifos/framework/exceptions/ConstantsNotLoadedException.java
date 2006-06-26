package org.mifos.framework.exceptions;

public class ConstantsNotLoadedException extends SystemException{
	public ConstantsNotLoadedException(){
		super();
	}
	
	public ConstantsNotLoadedException(String key, Throwable cause){
		super(key, cause);
	}
	
	public ConstantsNotLoadedException(Throwable cause){
		super(cause);
	}
	
	public ConstantsNotLoadedException(String key){
		this.key = key;
	}
	
	public ConstantsNotLoadedException(String key ,Throwable cause,Object[] values) {
		this.key = key;
		super.initCause(cause);
		this.values = values;
	}
}
