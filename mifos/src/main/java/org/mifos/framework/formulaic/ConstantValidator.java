package org.mifos.framework.formulaic;

public class ConstantValidator extends BaseValidator {
	
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
			return makeError(value, ErrorType.WRONG_VALUE);
		}
	}

}
