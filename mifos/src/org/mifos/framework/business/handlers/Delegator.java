/**

 * Delegator.java    version: 1.0

 

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

package org.mifos.framework.business.handlers;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;


/**
 * This is the class which is used to delegate calls from the action class 
 * to the respective businessprocessor.
 */
public class Delegator {
	
	private String businessProcessorImplementation;

	/**
	 * Delegates the call to the corresponding BusinessProcessor 
	 * which is found based on the path attribute in the context object.
	 * It calls the ServiceLocatorFactory to get the correct ServiceLocator 
	 * which then returns the instance of the corresponding BusinessProcessor 
	 * and process method of the BusinessProcessor is called.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void process(Context context)throws SystemException,ApplicationException {
		ServiceLocatorFactory serviceLocatorFactory = null;
		ServiceLocator serviceLocator = null;
		BusinessProcessor businessProcessor = null;
		try{
			serviceLocatorFactory = ServiceLocatorFactory.getInstance();
			serviceLocator = serviceLocatorFactory.getServiceLocator(businessProcessorImplementation);
			if(null != serviceLocator){
				businessProcessor = serviceLocator.getBusinessProcessor(context.getPath());	
			}
			businessProcessor.execute(context);
		}catch(ResourceNotCreatedException rnce){
			throw rnce;
		}
	}

	/**
	 * @return Returns the businessProcessorImplementation}.
	 */
	public String getBusinessProcessorImplementation() {
		return businessProcessorImplementation;
	}

	/**
	 * @param businessProcessorImplementation The businessProcessorImplementation to set.
	 */
	public void setBusinessProcessorImplementation(
			String businessProcessorImplementation) {
		this.businessProcessorImplementation = businessProcessorImplementation;
	}

	

	

	





	
}
	
