/**

 * ServiceLocator.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.framework.business.handlers;

import org.mifos.framework.exceptions.ResourceNotCreatedException;


/**
 * ServiceLocator is used to return the BusinessProcessor object corresponding
 * to the path passed as parameter to the getBusinessProcessor() method. 
 * @author ashishsm
 * 
 */
public interface ServiceLocator {

	
	/**
	 * Returns the object of class which implements the BusinessProcessor interface.
	 * If the parameter passed is null it throws a ResourceNotCreatedException 
	 * @param path - should be a valid path same as that mentioned in dependencies.xml
	 * @return  the BusinessProcess object
	 * @throws ResourceNotCreatedException - if it is not able to create an object of the BusinessProcessor as indicated by the path parameter
	 */
	public BusinessProcessor getBusinessProcessor(String path)throws ResourceNotCreatedException;
}