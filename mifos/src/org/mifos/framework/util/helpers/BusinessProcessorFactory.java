/**

 * BusinessProcessorFactory.java    version: 1.0

 

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

import java.net.URISyntaxException;
import java.util.HashMap;

import org.mifos.framework.business.handlers.BusinessProcessor;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.util.helpers.ResourceFactory;



/**
 * This is a singleton BusinessProcessorFactory and is used to return 
 * an instance of the corresponding 
 * {@link BusinessProcessor} based on the path passed to it as parameter 
 */
public class BusinessProcessorFactory extends ResourceFactory {
	
	private static BusinessProcessorFactory instance = new BusinessProcessorFactory();	
	
	/**
	 * This is a hash map which acts a cache for the instances of BusinessProcessor.
	 * 
	 */
	private HashMap<String,BusinessProcessor> businessProcessorRepository;
	
	/**
	 * In the constructor create an object of BusinessProcessorRepository.
	 */
	private BusinessProcessorFactory(){
		businessProcessorRepository = new HashMap<String, BusinessProcessor>();
	}	
	
	/**
	 * Returns an instance of <code>BusinessProcessorFactory</code> 
	 * @return
	 */
	public static BusinessProcessorFactory getInstance(){
		
		return instance;
	}
	
	/**
	 * It calls the getObject of the parent passing the path as parameter.
	 * @throws ResourceNotCreatedException - If there is any problem reading the dependency.xml file 
	 * @see org.mifos.framework.util.helpers.ResourceFactory#get(java.lang.String)
	 */
	
	public Object get(String path)
	throws ResourceNotCreatedException {
		
		BusinessProcessor businessProcessor = null;
		
		try{
			businessProcessor =(BusinessProcessor)getObject(path); 
		}catch(ClassNotFoundException cnfe){
			throw new ResourceNotCreatedException(cnfe);
		}catch(IllegalAccessException iae){
			throw new ResourceNotCreatedException(iae);
		}catch(InstantiationException ie){
			throw new ResourceNotCreatedException(ie);
		} catch (XMLReaderException xre) {
			throw new ResourceNotCreatedException(xre);
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
			throw new ResourceNotCreatedException(urise);
		}finally{
		}
		return businessProcessor;
	}
	
	/**
	 * Returns the Object from the cache.
	 * @see org.mifos.framework.util.helpers.ResourceFactory#getFromCache(java.lang.String)
	 */
	protected Object getFromCache(String path) {
		
		return businessProcessorRepository.get(path);
	}
	
/**
	 * @param path
	 * @param businessProcessorObj
	 */
	protected void updateBusinessProcessorCache(String path, BusinessProcessor businessProcessorObj) {
		businessProcessorRepository.put(path, businessProcessorObj);
		
	}
	
}
