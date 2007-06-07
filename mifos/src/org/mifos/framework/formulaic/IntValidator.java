package org.mifos.framework.formulaic;

/*
 * This validator converts strings into integers
 */
public class IntValidator extends IsInstanceValidator {
	
	public static final String PARSE_ERROR = "errors.formulaic.IntValidator.parse";
	
	public IntValidator() {
		super(String.class);
	}
	
	@Override
	public Integer validate(Object input) throws ValidationError {
		input = super.validate(input);
		checkNull(input);
		try {
			String inputString = (String) input;
			int result = Integer.parseInt(inputString);
			return result;
		}
		
		catch (NumberFormatException e) {
			throw new ValidationError(input, IntValidator.PARSE_ERROR);
		}
	}

}
