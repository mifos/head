package org.mifos.framework.formulaic;

public class ConstantValidator extends BaseValidator {
	
	public static final String WRONG_VALUE_ERROR = "errors.formulaic.ConstantValidator.wrongvalue";
	
	private Object expectedValue;
	
	public ConstantValidator(Object expectedValue) {
		this.expectedValue = expectedValue;
	}

	@Override
	public Object validate(Object value) throws ValidationError {
		checkNull(value);
		if (value.equals(expectedValue)) {
			return value;
		}
		else {
			throw new ValidationError(value, WRONG_VALUE_ERROR);
		}
	}

}
