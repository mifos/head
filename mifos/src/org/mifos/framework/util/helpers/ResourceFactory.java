/**

 * ResourceFactoryFactory.java    version: 1.0

 

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
package org.mifos.framework.util.helpers;

import java.io.File;
import java.net.URISyntaxException;


import org.mifos.framework.business.handlers.BusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.util.valueobjects.ValueObject;


/**
 * Base class for all factories in the class. it has got default implementations for methods to read the xml and put them in cache.
 * @author ashishsm
 *
 */
public abstract class ResourceFactory 
{
	
   private File dependencyFile = null;
	
   public void setDependencyFile(File dependencyFile)
   {
	   this.dependencyFile = dependencyFile;
   }
   
   public File getDependencyFile() throws URISyntaxException
   {
	   if(dependencyFile == null)
		   dependencyFile = new File(ResourceLoader.getURI(FilePaths.DEPENDENCYFILE));
	   
	   return dependencyFile;
   
   }
   
   
	
	/**
	 * It returns the object corresponding to the specified path.
	 * It first tries to find the object in the cache and if it is not available 
	 * in the cache it reads from the xml and also puts it in the cache.
	 * @param path
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws XMLReaderException
	 * @throws URISyntaxException
	 */
	protected  Object getObject(String path) 
	throws ClassNotFoundException, IllegalAccessException, InstantiationException, XMLReaderException, URISyntaxException {	
		Object obj = getFromCache( path);
		if(null == obj){
			loadFromXML(path);
		}
		obj = getFromCache(path);
		return obj;
	}	
	
	/**
	 * Reads the xml specified by the path. It uses JAXB to read the xml.
	 * It uses DependenciesReader to read the dependency.xml. It also updates the cache from the values obtained by reading the xml.
	 * @param path
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws XMLReaderException
	 * @throws URISyntaxException
	 */
	protected void loadFromXML(String path) throws ClassNotFoundException, IllegalAccessException, InstantiationException, XMLReaderException, URISyntaxException {
		
		MifosNode mifosNode = null;
		String valueObjectClass = null;
		String businessProcessorClass = null;
		String daoClass = null;
		ValueObject valueObjectObj = null;
		DAO daoObj = null;
		BusinessProcessor businessProcessorObj = null;
		DependenciesReader dependenciesReader = new DependenciesReader();
		
		mifosNode = dependenciesReader.getElement(getDependencyFile(), path);
		valueObjectClass = mifosNode.getElement(Constants.VALUEOBJECT);
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The value object class is" + valueObjectClass);
		businessProcessorClass = mifosNode.getElement(Constants.BUSINESSPROCESSOR);
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The businessprocessor  class is" + businessProcessorClass);
		daoClass = mifosNode.getElement(Constants.DAO);
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The dao class is" + daoClass);
		if(null != valueObjectClass && !valueObjectClass.equals("")){
			valueObjectObj = (ValueObject)Class.forName(valueObjectClass).newInstance();
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("The value object class instantiated successfully" );
		}
		if(null != businessProcessorClass && !businessProcessorClass.equals("")){
			businessProcessorObj = (BusinessProcessor)Class.forName(businessProcessorClass).newInstance();
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("The business processor class instantiated successfully" );
		}
		if(null != daoClass && !daoClass.equals("")){
			daoObj = (DAO)Class.forName(daoClass).newInstance();
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("The dao class instantiated successfully" );
		}
		
		updateValueObjectCache(path,valueObjectObj);
		updateBusinessProcessorCache(path,businessProcessorObj);
		updateDAOCache(path,daoObj);
		
	}
	
	/**
	 * Updates the BusineProcessorCache.
	 * @param path - key for the cache
	 * @param businessProcessorObj - value to be put in the cache
	 */
	protected void updateBusinessProcessorCache(String path, BusinessProcessor businessProcessorObj) {
		
		
	}

	/**
	 * Updates the ValueObjectCache.
	 * @param path - key for the cache.
	 * @param valueObjectObj - value to be put in the cache.
	 */
	protected void updateValueObjectCache(String path, ValueObject valueObjectObj) {
		
		
	}
	
/**
	 * Updates the DAOCache.
	 * @param path - key for the cache.
	 * @param daotObj - value to be put in the cache.
	 */
	protected void updateDAOCache(String path, DAO daoObj) {
		
		
	}
	
	

	/**
	 * Abstarct method to be implemented by the corresponding Factories.
	 * This should return the object from theire local caches which is stored corresponding to the path as key in the cache. 
	 * @param path
	 * @return
	 */
	protected abstract Object getFromCache(String path);
	
	/**
	 * This method is the interface for the factory to the outside world where everybody requiring the specific resource will call get on the corresponding resource factory.
	 * @param moduleName
	 * @param subModuleName
	 * @return
	 */
	public abstract Object get(String path)throws ResourceNotCreatedException;
	
	
	
	
}
