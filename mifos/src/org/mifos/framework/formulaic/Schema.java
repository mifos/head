package org.mifos.framework.formulaic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
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
				FieldInfo fieldInfo = fieldValidators.get(field);
				Validator validator = fieldInfo.validator;
				Object input = parseField(field, fieldInfo.type, data);
				results.put(field, validator.validate(input));
			}
			catch (ValidationError e) {
				e.printStackTrace();
				fieldErrors.put(field, e);
			}
		}
		
		if (fieldErrors.size() > 0) {
			throw new SchemaValidationError(data, fieldErrors);
		}
		return results;
	}
	
	public static Object parseField(String field, FieldType type, Map<String, String> data) {
		if (type == FieldType.SIMPLE) {
			return data.containsKey(field) ? data.get(field) : null;
		} else if (type == FieldType.COMPLEX) {
			Map<String, String> parsedContents = new HashMap<String, String>();
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
			Map<String, String> parsedContents = new HashMap<String, String>();
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
