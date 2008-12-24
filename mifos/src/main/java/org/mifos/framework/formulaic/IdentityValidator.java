package org.mifos.framework.formulaic;

public class IdentityValidator extends BaseValidator {

	/*
	 * This validator peforms no validation, and returns whatever it's given, 
	 * including null.  Could be used to specify that a field is optional, but 
	 * it's probably clearer to use the Null validator (with the default constructor)
	 * instead
	 */
	@Override
	public Object validate(Object value) throws ValidationError {
		return value;
	}

}
