package org.mifos.framework.exceptions;

public class PropertyNotFoundException extends ApplicationException{
	
	public PropertyNotFoundException(String key){
		super(key);
	}
}
