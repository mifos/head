package org.mifos.application.customer.group.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class GroupPersistence extends Persistence {

	public GroupBO getGroupBySystemId(String globalCustNum){
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
		try{
			return (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class, customerId);
					
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
	}
	
	public boolean isGroupExists(String name, Short officeId){
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerConstants.DISPLAY_NAME, name);
		queryParameters.put(CustomerConstants.OFFICE_ID, officeId);
		List queryResult = executeNamedQuery(NamedQueryConstants.GET_GROUP_COUNT_BY_NAME, queryParameters);
		return ((Integer)queryResult.get(0)).intValue()>0;
	}
}
