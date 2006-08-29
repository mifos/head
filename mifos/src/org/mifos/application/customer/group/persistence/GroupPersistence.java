package org.mifos.application.customer.group.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.group.business.GroupBO;
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

}
