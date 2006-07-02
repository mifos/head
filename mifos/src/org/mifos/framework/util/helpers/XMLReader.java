/**

 * XMLReader.java    version: 1.0

 

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


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.XMLReaderException;


/**
 * This class is used to read the xml using JAXB.
 * @author ashishsm
 *
 */
public abstract class XMLReader {
	
	/**
	 * This method reads the xml based on the File parameter passed to it.It reads the xml file using JAXB methodlogy.
	 * @param f
	 * @return
	 * @throws XMLReaderException
	 */
	protected Object readXML(File f)throws XMLReaderException {
		String name = f.getName();
		Object rootNode = null;
		
		String contextPath = FilePaths.JAXBPACKAGEPATH + name.toLowerCase();
		contextPath = contextPath.substring(0, contextPath.length()-4);
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("context path is  " + contextPath, false, null);
		try {
			JAXBContext jc = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			// need to think of validation mechanism
			//unmarshaller.setValidating(true );
			
			rootNode =  unmarshaller.unmarshal(f );
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("rootNode has been read using JAXB ", false, null);
		} catch (JAXBException je) {
			 je.printStackTrace();
			throw new XMLReaderException(je);
		} 
		return rootNode;
	}
	
	public abstract MifosNode getElement(File f, String elementName)throws XMLReaderException;
	
	
}
