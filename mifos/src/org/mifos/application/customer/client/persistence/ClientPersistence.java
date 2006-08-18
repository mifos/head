/**

* ClientPersistence.java version: 1.0



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

package org.mifos.application.customer.client.persistence;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class ClientPersistence extends Persistence {

	public ClientBO getClient(Integer customerId) {
		Session session = HibernateUtil.getSessionTL();
		ClientBO client = (ClientBO) session.get(ClientBO.class,customerId);
		return client;
	}
	
	public boolean checkForDuplicacyOnGovtId(String governmentId) {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("LEVEL_ID", CustomerConstants.CLIENT_LEVEL_ID);
			queryParameters.put("GOVT_ID",governmentId);
			List queryResult = executeNamedQuery(NamedQueryConstants.GET_CLIENT_BASEDON_GOVTID, queryParameters);
			return ((Integer)queryResult.get(0)).intValue()>0;
	}
	
	public boolean checkForDuplicacyOnName(String name, Date dob, Integer customerId) {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("clientName",name);
			queryParameters.put("LEVELID", CustomerConstants.CLIENT_LEVEL_ID);
			queryParameters.put("DATE_OFBIRTH", dob);
			queryParameters.put("customerId", customerId);
			List queryResult = executeNamedQuery(NamedQueryConstants.GET_CLIENT_BASEDON_NAME_DOB, queryParameters);
			return ((Integer)queryResult.get(0)).intValue()>0;
			
	}
	
}
