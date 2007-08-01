package org.mifos.framework.formulaic;

/*
 * If input is null, this validator returns null.  Otherwise, returns
 * the output of passing the input through the contained validator.
 * Useful for indicating that field is optional, but if it is given it must 
 * meet certain constraints
 */
public class NotNullEmptyValidator extends IsInstanceValidator {

	
	private Validator other;

	public NotNullEmptyValidator() {
		super(String.class);
	}
	
	public NotNullEmptyValidator(Validator other) {
		super(String.class);
		this.other = other;
	}
	
	@Override
	public String validate(Object value) throws ValidationError {
		super.validate(value);
		if (other != null)
			other.validate(value);
		if (((String)value).trim().equals("")) {
			throw makeError(value, ErrorType.MISSING);
		}
		return (String) value;
	}
}
