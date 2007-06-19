package org.mifos.framework.formulaic;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
/*
 * A schema is a special kind of validator intended for forms... its contains a mapping of
 * validators for various input keys, and validates each field of an input map against them
 */
public class Schema extends BaseValidator {
	
	private Map<String, Validator> fieldValidators = new HashMap<String, Validator>();
	
	public void setValidator(String field, Validator validator) {
		fieldValidators.put(field, validator);
	}
	
	public Validator getValidator(String field) {
		return fieldValidators.get(field);
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
	
	public static ActionMessages makeActionMessages(SchemaValidationError schemaErrors) {
		ActionMessages errors = new ActionMessages();
		for (String key : schemaErrors.keySet()) {
			String msg = schemaErrors.getFieldMsg(key);
			errors.add(key, new ActionMessage(msg));
		}
		
		return errors;
	}

	@Override
	public Map<String, Object> validate(Object objectData) throws ValidationError {
		Map<String, String> data;
		
		try {
			data = (Map<String, String>) objectData;
		}
		
		catch (ClassCastException e) {
			throw new ValidationError(objectData, IsInstanceValidator.WRONG_TYPE_ERROR);
		}
		
		Map results = new HashMap<String, Object>();
		Map fieldErrors = new HashMap<String, ValidationError>();
		for (String field : fieldValidators.keySet()) {
			try {
				// if the field isn't in the input, its value becomes null
				data.get(field);
				Object input = data.containsKey(field) ? data.get(field) : null;
				Validator validator = fieldValidators.get(field);
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
}
