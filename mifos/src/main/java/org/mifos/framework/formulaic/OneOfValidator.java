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

import java.util.Collection;
import java.util.LinkedList;

public class OneOfValidator extends BaseValidator {
	
	LinkedList foo;
	
	public static final String NOT_A_CHOICE_ERROR = "errors.formulaic.OneOfValidator.notachoice";
	private Collection choices;
	private OneOfValidatorSource source;
	
	public OneOfValidator(Collection choices) {
		assert choices != null;
		this.choices = choices;
	}
	
	public OneOfValidator(Object[] choices) {
		
		this.choices = new LinkedList();
		for (Object choice : choices) {
			this.choices.add(choice);
		}
	}
	
	public OneOfValidator(OneOfValidatorSource source) {
		assert source != null;
		this.source = source;
	}
	
	@Override
	public Object validate(Object value) throws ValidationError {
		checkNull(value);
		Collection items = source == null ? choices : source.getItems();
		if (!items.contains(value)) {
			throw makeError(value, ErrorType.NOT_A_CHOICE);
		}
		else {
			return value;
		}
	}

}
