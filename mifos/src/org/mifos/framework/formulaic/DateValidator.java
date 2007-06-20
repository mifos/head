package org.mifos.framework.formulaic;

import java.util.Date;

import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;

public class DateValidator extends IsInstanceValidator {
	
	public static final String DATE_FORMAT_ERROR = "errors.formulaic.DateValidator.dateformat";
	
	public DateValidator() {
		super(String.class);
	}
	
	@Override
	public Date validate(Object input) throws ValidationError {
		input = super.validate(input);
		try {
			String inputString = (String) input;
			return DateUtils.getDateAsSentFromBrowser(inputString);
		}
		
		catch (InvalidDateException e) {
			throw new ValidationError(input, DATE_FORMAT_ERROR);
		}
	}

}
