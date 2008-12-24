package org.mifos.framework.formulaic;

import org.apache.struts.action.ActionMessage;


public abstract class BaseValidator implements Validator {
	
	protected enum errors {MISSING};
	
	protected String errorPrefix;
	
	public BaseValidator() {
		errorPrefix = "errors." + this.getClass().getSimpleName();
	}
	
	public String getKey(String errorType) {
		return errorPrefix + "." + errorType;
	}

    protected ValidationError makeError(Object value, Enum errorType) {
		ActionMessage message = new ActionMessage(getKey(errorType.toString()));
		return new ValidationError(value, message);
	}

    protected ValidationError makeError(Object value, Enum errorType, String property) {
		ActionMessage message = new ActionMessage(getKey(errorType.toString()), property);
		return new ValidationError(value, message);
	}
	
	public void setErrorPrefix(String prefix) {
		this.errorPrefix = prefix;
	}
	
//	 thrown when an item is missing, which generally means it was null
	public static final String MISSING_ERROR = "errors.formulaic.BaseValidator.missing";
	
	public abstract Object validate(Object value) throws ValidationError;
	
	public boolean isValid(Object value) {
		try {
			validate(value);
			return true;
		}
		catch (ValidationError e) {
			return false;
		}
	}
	
	protected void checkNull(Object value) throws ValidationError {
		if (value == null) {
			throw makeError(value, errors.MISSING);
		}
	}
}
