package org.mifos.framework.exceptions;

public class PropertyNotFoundException extends ApplicationException{
	public PropertyNotFoundException(){
		super();
	}
	public PropertyNotFoundException(String key){
		super(key);
	}
}
