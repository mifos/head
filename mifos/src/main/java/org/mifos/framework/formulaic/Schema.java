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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMessages;
import org.mifos.framework.struts.actionforms.GenericActionForm;
/*
 * A schema is a special kind of validator intended for forms... its contains a mapping of
 * validators for various input keys, and validates each field of an input map against them
 */
public class Schema extends BaseValidator {
	
	public enum FieldType {SIMPLE, MAP, COMPLEX};
	
	private class FieldInfo {
		Validator validator;
		FieldType type;
		
		public FieldInfo(Validator validator, FieldType type) {
			this.validator = validator;
			this.type = type;
		}
	}
	
	private Map<String, FieldInfo> fieldValidators = new HashMap<String, FieldInfo>();
	
	public void setSimpleValidator(String field, Validator validator) {
		FieldInfo info = new FieldInfo(validator, FieldType.SIMPLE);
		fieldValidators.put(field, info);
	}
	
	public void setComplexValidator(String field, Validator validator) {
		FieldInfo info = new FieldInfo(validator, FieldType.COMPLEX);
		fieldValidators.put(field, info);
	}
	
	public void setMapValidator(String field, Validator validator) {
		FieldInfo info = new FieldInfo(validator, FieldType.MAP);
		fieldValidators.put(field, info);
	}
	
	public FieldInfo getValidator(String field) {
		return fieldValidators.get(field);
	}
	
	public Map<String, Object> validate(GenericActionForm actionForm) throws ValidationError {
		return validate(actionForm.getMap());
	}
	
	public Map<String, Object> validate(ServletRequest request) throws ValidationError {
		return validate(convertRequest(request));
	}
	
	public static Map<String, Object> convertRequest(ServletRequest request) {
		Map<String, Object> input = new HashMap<String, Object>();
		for (String key : (Set<String>) request.getParameterMap().keySet()) {
			input.put(key, request.getParameter(key));
		}
		return input;
	}
	
	public ActionMessages getErrors(Object objectData) throws ValidationError {
		ActionMessages errors = new ActionMessages();
		try {
			validate(objectData);
		}
		catch (SchemaValidationError e) {
			errors = makeActionMessages(e);
		}
		return errors;
	}

	public Map<String, Object> validate(HttpSession session) throws ValidationError {
		return validate(convertSession(session));
	}
	
	public static Map<String, Object> convertSession(HttpSession session) {
		Map<String, Object> input = new HashMap<String, Object>();
		for (Enumeration<String> e = session.getAttributeNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			input.put(key, session.getAttribute(key));
		}
		return input;
	}
	
	public static ActionMessages makeActionMessages(SchemaValidationError schemaErrors) {
		ActionMessages errors = new ActionMessages();
		List<String> keys = new LinkedList<String>(schemaErrors.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			schemaErrors.getFieldMsg(key);
			errors.add(key, schemaErrors.getFieldError(key).getActionMessage());
		}
		
		return errors;
	}

	@Override
	public Map<String, Object> validate(Object objectData) throws ValidationError {
		Map<String, Object> data;
		
		try {
			data = (Map<String, Object>) objectData;
		} catch (ClassCastException e) {
			throw makeError(objectData, ErrorType.WRONG_TYPE);
		}
		
		Map results = new HashMap<String, Object>();
		// inputs that don't have a validator assigned should just be copied over
		for (String key : data.keySet()) {
			results.put(key, data.get(key));
		}
		Map fieldErrors = new HashMap<String, ValidationError>();
		for (String field : fieldValidators.keySet()) {
			try {
				// if the field isn't in the input, its value becomes null
				FieldInfo fieldInfo = fieldValidators.get(field);
				Validator validator = fieldInfo.validator;
				Object input = parseField(field, fieldInfo.type, data);
				results.put(field, validator.validate(input));
			}
			catch (ValidationError e) {
				fieldErrors.put(field, e);
			}
		}
		
		if (fieldErrors.size() > 0) {
			throw new SchemaValidationError(data, fieldErrors);
		}
		return results;
	}
	
	public static Object parseField(String field, FieldType type, Map<String, Object> data) {
		if (type == FieldType.SIMPLE) {
			return data.containsKey(field) ? data.get(field) : null;
		} else if (type == FieldType.COMPLEX) {
			Map<String, Object> parsedContents = new HashMap<String, Object>();
			for (String key : data.keySet()) {
				int openParen = key.indexOf('(');
				int closeParen = key.lastIndexOf(')');
				if (openParen > -1 && closeParen > -1 && closeParen == key.length() - 1) {
					if (key.substring(openParen + 1, closeParen).startsWith(field)) {
						int delimPosition = key.lastIndexOf("_");
						if (key.substring(openParen + 1, delimPosition).equals(field)) {
							String subKey = key.substring(delimPosition + 1, closeParen);
							parsedContents.put(subKey, data.get(key));
						}
					}
				}
			}
			return parsedContents;
		} else { // map type
			Map<String, Object> parsedContents = new HashMap<String, Object>();
			for (String key : data.keySet()) {
				int delimPosition = key.lastIndexOf("_");
				if (delimPosition > 0) {
					if (key.substring(0, delimPosition).equals(field)) {
						String subKey = key.substring(delimPosition + 1);
						parsedContents.put(subKey, data.get(key));
					}
				}
			}
			return parsedContents;
		}
	}
}
