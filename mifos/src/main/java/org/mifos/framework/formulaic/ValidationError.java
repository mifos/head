package org.mifos.framework.formulaic;

import org.apache.struts.action.ActionMessage;

public class ValidationError extends Exception {
	
	private Object value; // the original value that failed validation
	private ActionMessage actionMessage;
	
	public ActionMessage getActionMessage() {
		return actionMessage;
	}
	
	public ValidationError(Object value, ActionMessage actionMessage) {
		this.value = value;
		this.actionMessage = actionMessage;
	}
	

	public Object getValue() {
		return value;
	}
	
	public String getMsg() {
		return actionMessage.getKey();
	}
	
	@Override
	public String toString() {
		return "<ValidationError " + getMsg() + ">";
	}
	
}
