package org.mifos.framework.formulaic;

import java.util.Date;
import java.util.Map;

import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;

public class DateComponentValidator extends IsInstanceValidator {
 	
	public DateComponentValidator() {
		super(Map.class);
	}
	
	@Override
	public Date validate(Object input) throws ValidationError {
		input = super.validate(input);
		Map inputMap = (Map) input;
		if (inputMap.size() == 0) {
			throw makeError(input, ErrorType.MISSING);
		}
		try {
			String dayValue = (String) inputMap.get("DD");
			String monthValue = (String) inputMap.get("MM");
			String yearValue = (String) inputMap.get("YY");
			return DateUtils.parseBrowserDateFields(yearValue, monthValue, dayValue);
		}
		catch (InvalidDateException e) {
			throw makeError(input, ErrorType.DATE_FORMAT);
		}
	}

}
