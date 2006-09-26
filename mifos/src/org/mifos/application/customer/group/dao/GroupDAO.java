/**

 * GroupDAO.java    version: 1.0



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

package org.mifos.application.customer.group.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;


/**
 * This class denotes the DAO layer for the group module.
 */

public class GroupDAO extends DAO {
/**
	 * This method is used to search for a groups under a particular office and its child offices with a 
	 * given starting name.
	 * @param userId it is userId of the loggedInUser
	 * @param searchString The string from which name starts
	 * @return A query result object containg the results
	 * @throws SystemException
	 * @throws ApplicationException
	 */	
	public QueryResult search(short officeId , String searchString, Short userId, Short userLevelId) throws SystemException, ApplicationException {
		Session session = null;
		try{
			QueryResult queryResult = QueryFactory.getQueryResult("GroupList");
			session = queryResult.getSession();
			Query query = null;
			//retrieving the searchId of the office of loggedIn User
			String searchId = new CustomerUtilDAO().getOffice(officeId).getSearchId();
			if(Configuration.getInstance().getCustomerConfig(officeId).isCenterHierarchyExists()){
				query=session.getNamedQuery(NamedQueryConstants.GROUP_SEARCH_WITH_CENTER);
				//query = session.createQuery("select distinct customer.office.officeName,customer.displayName,customer.parentCustomer.displayName, customer.customerId from Customer customer where customer.office.searchId like :SEARCH_ID and customer.displayName like :SEARCH_STRING and customer.customerLevel.levelId =:LEVEL_ID and customer.statusId!=:STATUS1 and customer.statusId!=:STATUS2");			
				query.setString("SEARCH_ID", searchId+"%");
				query.setString("SEARCH_STRING", searchString+"%");
				query.setShort("LEVEL_ID" , CustomerConstants.GROUP_LEVEL_ID);
				query.setShort("STATUS1" , GroupConstants.CANCELLED);
				query.setShort("STATUS2" , GroupConstants.CLOSED);
				query.setShort("USER_ID" , userId);
				query.setShort("USER_LEVEL_ID" , userLevelId);
				query.setShort("LO_LEVEL_ID" , PersonnelConstants.LOAN_OFFICER);
				
				
				//building the search results object. 
				String[] aliasNames = {"officeName" , "groupName" , "centerName","groupId" };
				 QueryInputs inputs = new QueryInputs();
				 inputs.setPath("org.mifos.application.group.util.valueobjects.GroupSearchResults");
				 inputs.setAliasNames(aliasNames);
				 inputs.setTypes(query.getReturnTypes());
	
				queryResult.setQueryInputs(inputs);
				queryResult.executeQuery(query);
			}
			else{
				//query = session.createQuery("select distinct customer.office.officeName,customer.displayName,customer.customerId from Customer customer where ((customer.personnel.personnelId=:USER_ID and :USER_LEVEL_ID=:LO_LEVEL_ID)or(:USER_LEVEL_ID!=:LO_LEVEL_ID)) and customer.office.searchId like :SEARCH_ID and customer.displayName like :SEARCH_STRING and customer.customerLevel.levelId =:LEVEL_ID and customer.statusId!=:STATUS1 and customer.statusId!=:STATUS2");
				query=session.getNamedQuery(NamedQueryConstants.GROUP_SEARCH_WITHOUT_CENTER);
				query.setString("SEARCH_ID", searchId+"%");
				query.setString("SEARCH_STRING", searchString+"%");
				query.setShort("LEVEL_ID" , CustomerConstants.GROUP_LEVEL_ID);
				query.setShort("STATUS1" , GroupConstants.CANCELLED);
				query.setShort("STATUS2" , GroupConstants.CLOSED);
				query.setShort("USER_ID" , userId);
				query.setShort("USER_LEVEL_ID" , userLevelId);
				query.setShort("LO_LEVEL_ID" , PersonnelConstants.LOAN_OFFICER);
				
				//building the search results object. 
				String[] aliasNames = {"officeName" , "groupName" , "groupId" };
				QueryInputs inputs = new QueryInputs();
				inputs.setPath("org.mifos.application.group.util.valueobjects.GroupSearchResults");
				inputs.setAliasNames(aliasNames);
				inputs.setTypes(query.getReturnTypes());
	
				queryResult.setQueryInputs(inputs);
				queryResult.executeQuery(query);
			}
			return queryResult;
		}catch(HibernateProcessException he){
			throw new SystemException(he);
		}
	}
	
		
}
