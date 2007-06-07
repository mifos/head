package org.mifos.framework.formulaic;

public abstract class BaseValidator implements Validator {
	
//	 thrown when an item is missing, which generally means it was null
	public static final String MISSING_ERROR = "errors.formulaic.missing";
	
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
	
	protected static void checkNull(Object value) throws ValidationError {
		if (value == null) {
			throw new ValidationError(value, MISSING_ERROR);
		}
	}
}
