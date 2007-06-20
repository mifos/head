package org.mifos.framework.formulaic;


public abstract class BaseValidator implements Validator {
	
	private String errorPrefix = this.getClass().getName();
	
	public String getMsg(String errorType) {
		return errorPrefix + "." + errorType;
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
			throw new ValidationError(value, MISSING_ERROR);
		}
	}
}
