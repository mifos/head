/**

 * DAOFactory.java    version: 1.0

 

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

import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.XMLReaderException;

public class DAOFactory extends ResourceFactory {

	private static DAOFactory instance = new DAOFactory();	
	
	private HashMap<String,DAO> daorepository = new HashMap<String,DAO>();
	
	@Override
	protected Object getFromCache(String path) {
		return daorepository.get(path);
	}

	@Override
	public Object get(String path) throws ResourceNotCreatedException {
		try{
			 return (DAO)getObject(path); 
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
	}
	
	/**
	 * Returns the DAOFactory instance.
	 */
	public static DAOFactory getInstance() {
		return instance;
	}
	
	@Override
	protected void updateDAOCache(String path, DAO daoObj) {
		daorepository.put(path, daoObj);
	}

}
