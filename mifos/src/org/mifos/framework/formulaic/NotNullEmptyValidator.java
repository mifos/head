package org.mifos.framework.formulaic;

/*
 * If input is null, this validator returns null.  Otherwise, returns
 * the output of passing the input through the contained validator.
 * Useful for indicating that field is optional, but if it is given it must 
 * meet certain constraints
 */
public class NotNullEmptyValidator extends IsInstanceValidator {

	
	public NotNullEmptyValidator() {
		super(String.class);
	}
	
	@Override
	public String validate(Object value) throws ValidationError {
		super.validate(value);
		if (value.equals("")) {
			throw makeError(value, ErrorType.MISSING);
		}
		return (String) value;
	}
}
