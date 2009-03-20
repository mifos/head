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
