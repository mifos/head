/**

 * DependenciesReader.java    version: 1.0

 

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

import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.util.jaxb.dependency.Dependencies;
import org.mifos.framework.util.jaxb.dependency.DependencyType;

/**
 * Reads the Dependency.xml file.
 * @author ashishsm
 *
 */
public class DependenciesReader extends XMLReader {
	
	/* (non-Javadoc)
	 * @see org.mifos.framework.util.helpers.XMLReader#getElement(java.io.File, java.lang.String)
	 */
	public MifosNode getElement(File f, String elementName)throws XMLReaderException {
		Object rootNode = null;
		Dependencies dependencies = null;
		MifosNode mifosNode = null;
		rootNode = readXML(f);
		if(null != rootNode){
			dependencies = (Dependencies)rootNode;
			for(Object obj : dependencies.getDependency()){
				DependencyType dependencyType = (DependencyType)obj;
				if(dependencyType.getType().equalsIgnoreCase(elementName )){
					mifosNode = new MifosNode();
					mifosNode.putElement(Constants.VALUEOBJECT, dependencyType.getValueobject());
					mifosNode.putElement(Constants.BUSINESSPROCESSOR, dependencyType.getBusinessprocessor());
					mifosNode.putElement(Constants.DAO, dependencyType.getDao());
				}
				
			}
		}
		return mifosNode;
	}
	
}
