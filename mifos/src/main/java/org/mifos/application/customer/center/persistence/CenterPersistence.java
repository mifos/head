/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.customer.center.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.CenterTemplate;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.persistence.SessionPersistence;
import org.mifos.framework.security.util.UserContext;

public class CenterPersistence extends Persistence {

	public CenterPersistence() {
		super();
	}

	public boolean isCenterExists(String name) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerConstants.DISPLAY_NAME, name);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CENTER_COUNT_BY_NAME, queryParameters);
		return ((Number) queryResult.get(0)).longValue() > 0;
	}

	public CenterBO getCenter(Integer customerId) throws PersistenceException {
		return (CenterBO) getPersistentObject(CenterBO.class, customerId);
	}

	public CenterBO findBySystemId(String globalCustNum)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		CenterBO center = null;
		queryParameters.put("globalCustNum", globalCustNum);
		List<CenterBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CENTER_BY_SYSTEMID, queryParameters);
		if (null != queryResult && queryResult.size() > 0) {
			center = queryResult.get(0);
		}
		return center;
	}

	public QueryResult search(String searchString,Short userId) throws PersistenceException {
		String[] namedQuery = new String[2];
		List<Param> paramList = new ArrayList<Param>();
		QueryInputs queryInputs = new QueryInputs();
		QueryResult queryResult = QueryFactory
				.getQueryResult(PersonnelConstants.USER_LIST);
		PersonnelBO user = new PersonnelPersistence().getPersonnel(userId);
		String officeSearchId = user.getOffice().getSearchId();
		namedQuery[0]=NamedQueryConstants.CENTER_SEARCH_COUNT;
		namedQuery[1]=NamedQueryConstants.CENTER_SEARCH;			
		paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));	
		paramList.add(typeNameValue("String","CENTER_NAME",searchString+"%"));
		paramList.add(typeNameValue("Short","LEVEL_ID",CustomerLevel.CENTER.getValue()));
		paramList.add(
			typeNameValue("Short","STATUS_ID",
				CustomerStatus.CENTER_ACTIVE.getValue()));
		paramList.add(typeNameValue("Short","USER_ID",userId));
		paramList.add(typeNameValue("Short","USER_LEVEL_ID",user.getLevelEnum()
				.getValue()));
		paramList.add(typeNameValue("Short","LO_LEVEL_ID",PersonnelConstants.LOAN_OFFICER));
		String[] aliasNames = {"parentOfficeId" , "parentOfficeName" , "centerSystemId" , "centerName"};
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setPath("org.mifos.application.customer.center.util.helpers.CenterSearchResults");
		queryInputs.setAliasNames(aliasNames);	
		queryInputs.setParamList(paramList);
		try {
			queryResult.setQueryInputs(queryInputs);
		} catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}
		return queryResult;

	}

    public CenterBO createCenter(UserContext userContext, CenterTemplate template)
            throws CustomerException {
        CenterBO center = new CenterBO(userContext, template.getDisplayName(),
			    template.getAddress(), template.getCustomFieldViews(), template.getFees(),
                template.getExternalId(), template.getMfiJoiningDate(), template.getOfficeId(),
                template.getMeeting(), template.getLoanOfficerId());
        saveCenter(center);
        return center;
    }

    public void saveCenter(CenterBO center) throws CustomerException {
        try {
            createOrUpdate(center);
            center.generateGlobalCustomerNumber();
            createOrUpdate(center);
        } catch (PersistenceException e) {
            throw new CustomerException(
                    CustomerConstants.CREATE_FAILED_EXCEPTION, e);
        }
    }
}
