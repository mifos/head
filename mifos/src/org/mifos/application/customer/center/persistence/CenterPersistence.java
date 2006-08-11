package org.mifos.application.customer.center.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class CenterPersistence extends  Persistence{
	
	public boolean isCenterExists(String name){
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerConstants.DISPLAY_NAME, name);
		List queryResult = executeNamedQuery(NamedQueryConstants.GET_CENTER_COUNT_BY_NAME, queryParameters);
		return ((Integer)queryResult.get(0)).intValue()>0;
	}

	public CenterBO getCenter(Integer customerId) {
		Session session = HibernateUtil.getSessionTL();
		CenterBO center = (CenterBO) session.get(CenterBO.class,
				customerId);
				return center;
	}
}
