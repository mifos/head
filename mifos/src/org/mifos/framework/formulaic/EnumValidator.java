package org.mifos.framework.formulaic;


/*
 * This validator transforms a string into its matching enum, given an Enum
 * class to look up values in.  The validator assumes that individual enums are
 * given uppercased names, and matches the given strings in a case-insensitive manner,
 * keeping with current mifos style.
 */
public class EnumValidator extends IsInstanceValidator {
	
	public static final String INVALID_ENUM_ERROR = "invalidenum";
	
	private Class enumType;
	
	public EnumValidator(Class enumType) {
		super(String.class);
		assert enumType != null;
		assert enumType.isEnum();
		this.enumType = enumType;
	}
	
	@Override
	public Enum validate(Object input) throws ValidationError {
		input = super.validate(input);
		
		try {
			String inputString = ((String) input).toUpperCase();
			return Enum.valueOf(enumType, inputString);
		}
		catch (IllegalArgumentException e) {
			throw makeError(input, ErrorType.INVALID_ENUM);
		}
		
	}

}
