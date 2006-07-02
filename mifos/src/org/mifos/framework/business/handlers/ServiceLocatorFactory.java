/**

 * ServiceLocatorFactory.java    version: 1.0

 

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




/**
 * Used to return the desired instance of the ServiceLocator and then ServiceLocator in turn returns the correct BusinessProcessor.
 * The service locator type to return is decided based on the <code>businessProcessorImplementation</code> parameter passed to the method.
 * @author ashishsm
 *
 */
public class ServiceLocatorFactory {

	/** 
	 *  static variable of ServiceLocatoryFactory, this is returned whenever getInstance() method is called.
	 */
	private static ServiceLocatorFactory instance = new ServiceLocatorFactory();
	
	/**
	 * An enum of ServiceLocatorTypes possible in the system 
	 * @author ashishsm
	 *
	 */
	private enum ServiceLocatorTypes {MifosSession,MifosPlainJava};
	
	private ServiceLocatorFactory(){
	}
	

	/** 
	 *  This is a static method used to return an instance of ServiceLocatorFactory.If the value of instance is null it instantiates a new ServiceLocatorFactory and returns it else it returns the same reference.
	 */
	public static ServiceLocatorFactory getInstance() {
		return instance;
	}

	
	/**
	 * Returns the ServiceLocator instance based on the type of the ServiceLocator desired which is indicated by the parameter passed to the method.
	 * @param businessProcessorImplementation - It decides the ServiceLocator Type to return .Possible values can be one among the ones defined by the enum <code>ServiceLocatorTypes</code>.
	 * @return
	 */
	public ServiceLocator getServiceLocator(String businessProcessorImplementation) {
		
		
		if(!ServiceLocatorTypes.MifosSession.toString().equalsIgnoreCase(businessProcessorImplementation)){
			return new BusinessLocator();
		}else{
			return new SessionBusinessLocator();
		}
		
	}

	
}
