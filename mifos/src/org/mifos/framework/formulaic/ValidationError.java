package org.mifos.framework.formulaic;

public class ValidationError extends Exception {
	
	private Object value; // the original value that failed validation
	private String msg;
	
	public ValidationError(Object value, String msg) {
		this.value = value;
		this.msg = msg;
	}
	
	public ValidationError(Object value) {
		this(value, Validator.DEFAULT_ERROR);
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getMsg() {
		return msg;
	}
	
	@Override
	public String toString() {
		return "<ValidationError " + msg + ">";
	}
	
}
