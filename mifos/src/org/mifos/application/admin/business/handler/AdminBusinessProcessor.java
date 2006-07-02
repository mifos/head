/**

 * AdminBusinessProcessor.java    version: 1.0

 

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
package org.mifos.application.admin.business.handler;

import org.mifos.application.customer.dao.CustomerSearchDAO;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;

/**
 * @author rajenders
 *
 */
public class AdminBusinessProcessor extends MifosBusinessProcessor {

	public void loadInitial(Context context)throws SystemException,ApplicationException{
	CustomerSearchDAO customerSearchDAO = new CustomerSearchDAO();		
	UserContext userContext=context.getUserContext();
	if(null!=userContext) {		
		Short officeId=userContext.getBranchId();		
		Office office= customerSearchDAO.getOffice(officeId);		
		context.addBusinessResults(CustomerSearchConstants.OFFICE,office.getOfficeName());	
		}
	}
}
