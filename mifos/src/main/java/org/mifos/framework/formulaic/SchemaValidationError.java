package org.mifos.framework.formulaic;

import java.util.Map;
import java.util.Set;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class SchemaValidationError extends ValidationError {
	
	private static final String FORM_MSG = "errors.formulaic.invalidform";
	private Map<String, ValidationError> fieldErrors;
	
	public SchemaValidationError(Map<String, Object> data, Map<String, ValidationError> fieldErrors) {
		super(data, new ActionMessage(SchemaValidationError.FORM_MSG));
		this.fieldErrors = fieldErrors;
	}
	
	public ActionMessages makeActionMessages() {
		return Schema.makeActionMessages(this);
	}
	
	public void addErrors(SchemaValidationError errors) {
		fieldErrors.putAll(errors.fieldErrors);
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
