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

import org.apache.struts.action.ActionMessage;


public abstract class BaseValidator implements Validator {
	
	protected enum errors {MISSING};
	
	protected String errorPrefix;
	
	public BaseValidator() {
		errorPrefix = "errors." + this.getClass().getSimpleName();
	}
	
	public String getKey(String errorType) {
		return errorPrefix + "." + errorType;
	}

    protected ValidationError makeError(Object value, Enum errorType) {
		ActionMessage message = new ActionMessage(getKey(errorType.toString()));
		return new ValidationError(value, message);
	}

    protected ValidationError makeError(Object value, Enum errorType, String property) {
		ActionMessage message = new ActionMessage(getKey(errorType.toString()), property);
		return new ValidationError(value, message);
	}
	
	public void setErrorPrefix(String prefix) {
		this.errorPrefix = prefix;
	}
	
//	 thrown when an item is missing, which generally means it was null
	public static final String MISSING_ERROR = "errors.formulaic.BaseValidator.missing";
	
	public abstract Object validate(Object value) throws ValidationError;
	
	public boolean isValid(Object value) {
		try {
			validate(value);
			return true;
		}
		catch (ValidationError e) {
			return false;
		}
	}
	
	protected void checkNull(Object value) throws ValidationError {
		if (value == null) {
			throw makeError(value, errors.MISSING);
		}
	}
}
