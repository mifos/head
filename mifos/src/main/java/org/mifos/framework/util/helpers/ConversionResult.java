package org.mifos.framework.util.helpers;

import java.util.List;

public class ConversionResult {
	
	private List<ConversionError> errors;
	private Double doubleValue;
	
	public Double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public List<ConversionError> getErrors() {
		return errors;
	}
	
	public void setErrors(List<ConversionError> errors) {
		this.errors = errors;
	}

}
