/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
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
			throw makeError(input, ErrorType.DATE_FORMAT);
		}
	}

}
