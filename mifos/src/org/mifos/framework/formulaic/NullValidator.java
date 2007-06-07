package org.mifos.framework.formulaic;

/*
 * If input is null, this validator returns null.  Otherwise, returns
 * the output of passing the input through the contained validator.
 * Useful for indicating that field is optional, but if it is given it must 
 * meet certain constraints
 */
public class NullValidator extends BaseValidator {

	private Validator otherValidator;
	
	public NullValidator(Validator otherValidator) {
		this.otherValidator = otherValidator;
	}
	
	public NullValidator() {
		this(new IdentityValidator());
	}
	
	@Override
	public Object validate(Object value) throws ValidationError {
		if (value == null) {
			return null;
		}
		else {
			return otherValidator.validate(value);
		}
	}
}
