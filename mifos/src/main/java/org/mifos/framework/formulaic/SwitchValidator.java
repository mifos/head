package org.mifos.framework.formulaic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

public class SwitchValidator extends BaseValidator {
	
	public static final String MISSING_EXPECTED_VALUE_ERROR = "errors.formulaic.SwitchValidator.missingexpectedvalue";
	
	private Map<Object, Schema> cases = new HashMap<Object, Schema>();
	private String switchField;
	private Schema defaultCase;
	
	public void addCase(Object value, Schema schema) {
		cases.put(value, schema);
	}
	
	public SwitchValidator(String switchField) {
		this(switchField, null);
	}
	
	public SwitchValidator(String switchField, Schema defaultCase) {
		this.defaultCase = defaultCase;
		this.switchField = switchField;
	}
	
	public void setDefaultCase(Schema defaultCase) {
		this.defaultCase = defaultCase;
	}
	
	public Map<String, Object> validate(ServletRequest request) throws ValidationError {
		return validate(Schema.convertRequest(request));
	}

	@Override
	public Map<String, Object> validate(Object objectData) throws ValidationError {
		Map<String, String> data;
		
		try {
			data = (Map<String, String>) objectData;
		}
		
		catch (ClassCastException e) {
			throw makeError(objectData, ErrorType.WRONG_VALUE);
		}
		
		if (data.containsKey(switchField) && cases.containsKey(data.get(switchField))) {
			Schema correctSchema = cases.get(data.get(switchField));
			return correctSchema.validate(data);
		}
		else {
			if (defaultCase != null) {
				return defaultCase.validate(data);
			}
			// if a switch field value matching the available choices is not given, 
			// and there's no default
			else {
				throw makeError(objectData, ErrorType.MISSING_EXPECTED_VALUE); // the default, most generic validation error
			}
		}
	}

}
