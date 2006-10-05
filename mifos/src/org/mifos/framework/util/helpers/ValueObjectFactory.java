/**

 * ValueObjectFactory.java    version: 1.0

 

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

import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * Used to return instances of ValueObject.
 */
public class ValueObjectFactory extends ResourceFactory {

	private static ValueObjectFactory instance = new ValueObjectFactory();	
	
	private HashMap<String,ValueObject> valueObjectrepository;
	
	private ValueObjectFactory(){
		valueObjectrepository = new HashMap<String,ValueObject>();
	}
	
	public static ValueObjectFactory getInstance(){
		
		return instance;
	}
	
	@Override
	public Object get(String path)throws ResourceNotCreatedException  {
		ValueObject valueObject = null;
		try{
			 valueObject =(ValueObject)getObject(path); 
		}catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
			throw new ResourceNotCreatedException(cnfe);
		}catch(IllegalAccessException iae){
			iae.printStackTrace();
			throw new ResourceNotCreatedException(iae);
		}catch(InstantiationException ie){
			ie.printStackTrace();
			throw new ResourceNotCreatedException(ie);
		} catch (XMLReaderException xre) {
			xre.printStackTrace();
			throw new ResourceNotCreatedException(xre);
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
			throw new ResourceNotCreatedException(urise);
		}finally{
		}
		
		return valueObject;
	}

	@Override
	protected Object getFromCache(String path) {
		Object valueObject = null;
		try {
			 valueObject = valueObjectrepository.get(path);
			 if(null != valueObject){
				 valueObject = valueObject.getClass().newInstance();
			 }
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return valueObject;
	}
	
	@Override
	protected void updateValueObjectCache(String path, ValueObject valueObjectObj) {
		valueObjectrepository.put(path, valueObjectObj);
		
	}

}
