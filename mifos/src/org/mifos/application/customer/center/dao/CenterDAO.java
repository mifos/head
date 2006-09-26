/**




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

package org.mifos.application.customer.center.dao;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;

public class CenterDAO extends DAO {
	/**
	>>>>>>> .r10916
		 * This method is used to search for a center under a particular office or with a center with a particular name.
		 * If the office id is that of an area office and the search string is not entered, then the centers for all 
		 * the branches under that area office is retrieved. If the search string is entered then all the centers with 
		 * a name like that of the search strig is retrieved
		 * @param officeId Office id of the user
		 * @param searchString The name of the center on the basis of which the search will be done
		 * @return A query result object containg the results
		 * @throws SystemException
		 * @throws ApplicationException
		 */
		
		public QueryResult search(short officeId , String searchString , Short userId, Short userLevelId) throws SystemException, ApplicationException {
			Session session = null;
			try{
				 QueryResult queryResult = QueryFactory.getQueryResult("CenterSearch");
				 session = queryResult.getSession();

				Office office = null;
				Query query = null;
				//retrieving the office with the officeId which has been passed
				query = session.getNamedQuery(NamedQueryConstants.GET_OFFICE_SEARCHID);
				query.setShort("OFFICE_ID", officeId);
				String searchId = "";
				//System.out.println("Inside CENTER DAO ..Search ..Before fetching office");
				List l = query.list();
				//System.out.println("Inside CENTER DAO ..Search ..After fetching office");
				if(l.size() > 0)
				{
					 searchId = (String)l.get(0);

				}
				query = session.getNamedQuery(NamedQueryConstants.SEARCH_CENTERS);;			
				query.setString("SEARCH_ID", searchId+"%");
				query.setString("CENTER_NAME", searchString+"%");
				query.setShort("LEVEL_ID" , CustomerConstants.CENTER_LEVEL_ID);
				query.setShort("STATUS_ID" , CustomerConstants.CENTER_ACTIVE_STATE);
				query.setShort("USER_ID" , userId);
				query.setShort("USER_LEVEL_ID" , userLevelId);
				query.setShort("LO_LEVEL_ID" , PersonnelConstants.LOAN_OFFICER);
				
				
				//building the search results object. 
				 String[] aliasNames = {"parentOfficeId" , "parentOfficeName" , "centerSystemId" , "centerName"};
				 QueryInputs inputs = new QueryInputs();
				 inputs.setPath("org.mifos.application.customer.center.util.valueobjects.CenterSearchResults");
				 inputs.setAliasNames(aliasNames);
				 inputs.setTypes(query.getReturnTypes());

				queryResult.setQueryInputs(inputs);
				queryResult.executeQuery(query);
				//System.out.println("---------SIZE OF QUERY: "+queryResult.getSize());
				
				return queryResult;
			}catch(HibernateProcessException he){
				throw new SystemException(he);
			}
					
			
		}
	
	
	
	
}



