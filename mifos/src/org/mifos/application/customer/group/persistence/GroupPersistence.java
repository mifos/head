package org.mifos.application.customer.group.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.Persistence;

public class GroupPersistence extends Persistence {

	public GroupBO findBySystemId(String globalCustNum) throws PersistenceException{
		Map<String, String> queryParameters = new HashMap<String, String>();
		GroupBO group = null;
		queryParameters.put("globalCustNum", globalCustNum);
			List<GroupBO> queryResult = executeNamedQuery(
						NamedQueryConstants.GET_GROUP_BY_SYSTEMID,
						queryParameters);
				if (null != queryResult && queryResult.size() > 0) {
					group = queryResult.get(0);
				}
		return group;
	}

	public GroupBO geGroup(Integer customerId) throws PersistenceException{
		return (GroupBO) getPersistentObject(GroupBO.class, customerId);
	}
	
	public boolean isGroupExists(String name, Short officeId) throws PersistenceException{
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerConstants.DISPLAY_NAME, name);
		queryParameters.put(CustomerConstants.OFFICE_ID, officeId);
		List queryResult = executeNamedQuery(NamedQueryConstants.GET_GROUP_COUNT_BY_NAME, queryParameters);
		return ((Number)queryResult.get(0)).intValue()>0;
	}
	
	public QueryResult search(String searchString,
			Short userId) throws PersistenceException {
		String[] namedQuery = new String[2];
		List<Param> paramList = new ArrayList<Param>();
		QueryInputs queryInputs = new QueryInputs();
		QueryResult queryResult = QueryFactory
				.getQueryResult(CustomerSearchConstants.GROUPLIST);
		
		
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);
		String officeSearchId = personnel.getOffice().getSearchId();
		if(Configuration.getInstance().getCustomerConfig(personnel.getOffice().getOfficeId()).isCenterHierarchyExists()){
			namedQuery[0]=NamedQueryConstants.GROUP_SEARCH_COUNT_WITH_CENTER;
			namedQuery[1]=NamedQueryConstants.GROUP_SEARCHWITH_CENTER;
			String[] aliasNames = {"officeName" , "groupName" , "centerName","groupId" };
			queryInputs.setAliasNames(aliasNames);
		}
		else
		{
			namedQuery[0]=NamedQueryConstants.GROUP_SEARCH_COUNT_WITHOUT_CENTER;
			namedQuery[1]=NamedQueryConstants.GROUP_SEARCH_WITHOUT_CENTER;
			String[] aliasNames = {"officeName" , "groupName" ,"groupId" };
			queryInputs.setAliasNames(aliasNames);
		}						
		paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));
		paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
		paramList.add(typeNameValue("Short","LEVEL_ID",CustomerConstants.GROUP_LEVEL_ID));
		paramList.add(typeNameValue("Short","USER_ID",userId));
		paramList.add(typeNameValue("Short","USER_LEVEL_ID",
				personnel.getLevelEnum().getValue()));
		paramList.add(typeNameValue("Short","LO_LEVEL_ID",PersonnelLevel.LOAN_OFFICER.getValue()));
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setPath("org.mifos.application.customer.group.util.helpers.GroupSearchResults");
		queryInputs.setParamList(paramList);
		try {
			queryResult.setQueryInputs(queryInputs);
		} catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}
		return queryResult;
	}
}
