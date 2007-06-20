package org.mifos.framework.formulaic;

import java.util.Map;
import java.util.Set;

public class SchemaValidationError extends ValidationError {
	
	private static final String FORM_MSG = "errors.formulaic.invalidform";
	private Map<String, ValidationError> fieldErrors;
	
	public SchemaValidationError(Map<String, String> data, Map<String, ValidationError> fieldErrors) {
		super(data, SchemaValidationError.FORM_MSG);
		this.fieldErrors = fieldErrors;
	}
	
	public int size() {
		return fieldErrors.size();
	}
	
	public Set<String> keySet() {
		return fieldErrors.keySet();
	}
	
	public ValidationError getFieldError(String key) {
		return fieldErrors.get(key);
	}
	
	public String getFieldMsg(String key) {
		return fieldErrors.get(key).getMsg();
	}
	
	public boolean containsField(String key) {
		return fieldErrors.containsKey(key);
	}

}
