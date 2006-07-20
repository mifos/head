/**

 * FeePersistence.java    version: 1.0

 

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
package org.mifos.application.fees.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.fees.business.FeeUpdateTypeEntity;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

/**
 * @author rajenders
 *
 */
public class FeePersistence extends Persistence {
	
	public FeesBO getFees( Short feeid){
		Session session = HibernateUtil.getSessionTL();
		return (FeesBO)session.get(FeesBO.class,feeid);
	}

	public List<FeesBO>  getUpdatedFeesForCustomer(){
		return executeNamedQuery(NamedQueryConstants.GET_UPDATED_FEES_FOR_CUSTOMERS,null);
	}
	public FeeUpdateTypeEntity getUpdateTypeEntity(Short id){
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ID",id);
		return (FeeUpdateTypeEntity)executeNamedQuery(NamedQueryConstants.GET_FEE_UPDATETYPE,queryParameters).get(0);
	}
}
