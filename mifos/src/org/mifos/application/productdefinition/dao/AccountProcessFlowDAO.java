/**

 * AccountProcessFlowDAO.java    version: xxx

 

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

package org.mifos.application.productdefinition.dao;


import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

public class AccountProcessFlowDAO extends DAO {

	public AccountProcessFlowDAO() {
		super();
	}
	
	/**
	 * It does a get on the database using Hibernate.It might get all the rows from the table or only the ones required and hence it might need to write its own HQL.
	 * @param context
	 */
	public void get(Context context){
		
	}
	
/**
	 * This needs to be overidden because it has to update multiple rows in the database.This might require an HQL.
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void update(org.mifos.framework.util.valueobjects.Context context) throws ApplicationException,SystemException
	{
		
		super.update(context);
	}
}
