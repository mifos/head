package org.mifos.framework.formulaic;

public class MaxLengthValidator extends IsInstanceValidator {
	
	private int max;
	public static final String TOO_LONG_ERROR = "errors.formulaic.MaxLengthValidator.toolong";
	
	public MaxLengthValidator(int max) {
		super(String.class);
		assert max > 0;
		this.max = max;
	}
	
	@Override
	public Object validate(Object input) throws ValidationError {
		String inputString = (String) super.validate(input);
		if (inputString.length() > max) {
			throw makeError(input, ErrorType.STRING_TOO_LONG);
		}
		return inputString;
	}

}
