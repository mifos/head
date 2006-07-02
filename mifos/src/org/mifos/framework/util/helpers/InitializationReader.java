/**

 * InitializationReader.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.framework.util.helpers;

import java.io.File;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.util.jaxb.initialization.Initialization;
import org.mifos.framework.util.jaxb.initialization.ParameterType;

/**
 * This class is used to read Initialization.xml
 * @author ashishsm
 *
 */
public class InitializationReader extends XMLReader {

	/* (non-Javadoc)
	 * @see org.mifos.framework.util.helpers.XMLReader#getElement(java.io.File, java.lang.String)
	 */
	
	public MifosNode getElement(File f, String elementName) throws XMLReaderException{
		Object rootNode = null;
		Initialization initializations = null;
		MifosNode mifosNode = null;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("inside getElement of initialization reader", false, null);
		rootNode = readXML(f);
		if(null != rootNode){
			initializations = (Initialization)rootNode;
			for(Object obj : initializations.getParameter()){
				ParameterType parameter = (ParameterType)obj;
				if(parameter.getType().equalsIgnoreCase(elementName )){
					mifosNode = new MifosNode();
					mifosNode.putElement(parameter.getName(), parameter.getValue());
				}
				
			}
		}
		return mifosNode;
	}

}
