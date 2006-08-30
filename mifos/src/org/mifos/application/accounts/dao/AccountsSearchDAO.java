/**

 * AccountSearchDAO.java    version: 1.0

 

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
package org.mifos.application.accounts.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;

public class AccountsSearchDAO extends DAO {

	public QueryResult search(String comingFrom, String searchString,
			Context context) throws SystemException, ApplicationException {

		Session session = null;
		Query query = null;

		try {
			QueryResult queryResult = QueryFactory
					.getQueryResult("AccountSearchResults");
			session = queryResult.getSession();
			UserContext uc = context.getUserContext();

			// get the office SearchId from the Authorization map
			 String searchId = HierarchyManager.getInstance().getSearchId(uc.getBranchId());
			if (null != searchString && null != searchId) {
/*				
				String queryString = "select distinct cust.displayName,cust.customerId,customerOne.displayName,"
					+ "customerTwo.displayName,off.officeName,cust.globalCustNum  from "
					+ "org.mifos.application.customer.util.valueobjects.Customer as cust "
					+ "left join cust.parentCustomer as customerOne left join customerOne.parentCustomer "
					+ "as customerTwo left join cust.office as off where cust.displayName like ('"
					+ searchString
					+ "%') "
					+ "and off.searchId like '"
					+ searchId
					+ "%' and cust.customerLevel.levelId in (1,2) "
					//bug - 28156 we need to search active only 
					+ " and cust.statusId in ( 9,3) ";
*/
				String queryString = "select distinct cust.displayName,cust.customerId,customerOne.displayName,"
					+ "customerTwo.displayName,off.officeName,cust.globalCustNum  from "
					+ "org.mifos.application.customer.util.valueobjects.Customer as cust "
					+ "left join cust.parentCustomer as customerOne left join customerOne.parentCustomer "
					+ "as customerTwo left join cust.office as off where cust.displayName like (:searchString) "
					+ "and off.searchId like '"
					+ searchId
					+ "%' and cust.customerLevel.levelId in (1,2) "
					//bug - 28156 we need to search active only 
					+ " and cust.statusId in ( 9,3)   ";
				if ( uc.getLevelId().shortValue()==PersonnelConstants.LOAN_OFFICER)
				{
					queryString += " and cust.personnel.personnelId=:personnelId ";
				}
				
				queryString+=" order by off.officeName,customerTwo.displayName,customerOne.displayName,cust.displayName ";
				
				query = session
						.createQuery(queryString).setString("searchString",(searchString+"%"));
				if (uc.getLevelId().shortValue()==PersonnelConstants.LOAN_OFFICER )
				{
					query.setShort("personnelId",uc.getId());
				}
				
				queryResult.executeQuery(query);

			}
			// building the search results object.
			String[] aliasNames = { "clientName", "clientId", "groupName",
					"centerName", "officeName","globelNo" };
			QueryInputs inputs = new QueryInputs();
			inputs
					.setPath("org.mifos.application.accounts.util.valueobjects.AccountSearchResults");
			inputs.setAliasNames(aliasNames);
			inputs.setTypes(query.getReturnTypes());
			queryResult.setQueryInputs(inputs);
			return queryResult;
		} catch (HibernateProcessException he) {
			throw new SystemException(he);
		}

	}

}
