package org.mifos.framework.formulaic;

public interface Validator {
	public static final String DEFAULT_ERROR = "errors.formulaic.default";
	
	public Object validate(Object value) throws ValidationError;

}
