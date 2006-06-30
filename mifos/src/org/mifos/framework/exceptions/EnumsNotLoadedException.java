package org.mifos.framework.exceptions;

public class EnumsNotLoadedException extends SystemException{
	public EnumsNotLoadedException(){
		super();
	}
	
	public EnumsNotLoadedException(String key, Throwable cause){
		super(key, cause);
	}
	
	public EnumsNotLoadedException(Throwable cause){
		super(cause);
	}
	
	public EnumsNotLoadedException(String key){
		this.key = key;
	}
	
	public EnumsNotLoadedException(String key ,Throwable cause,Object[] values) {
		this.key = key;
		super.initCause(cause);
		this.values = values;
	}
}
