package org.mifos.migration;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

/**
 * A sample ValidationEventHandler to see how error messages could be
 * customized and accumulated for eventual presentation in an import
 * tool (GUI or otherwise).
 */
public class MifosValidationEventHandler implements ValidationEventHandler{
	private int errorCount = 0;
	private StringBuffer errorBuffer = new StringBuffer();

	private void logError(ValidationEvent ve) {
		ValidationEventLocator  locator = ve.getLocator();

		errorBuffer.append("Message is " + ve.getMessage() + "\n");
		errorBuffer.append("    Column is " + 
				locator.getColumnNumber() + 
				" at line number " + locator.getLineNumber() + "\n");
		++errorCount; 
	}

	public boolean handleEvent(ValidationEvent ve) {            
		if (ve.getSeverity()==ve.FATAL_ERROR) {
			logError(ve);
			return false;
		} else if (ve .getSeverity()==ve.ERROR){
			logError(ve);
			return true;
		}
		return true;
	}

	public int getErrorCount() {
		return errorCount;
	}
	
	public String getErrorString() {
		return errorBuffer.toString();
	}

}	
