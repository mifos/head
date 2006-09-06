/**

* LoanActivityAction.java    version: xxx



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
package org.mifos.application.accounts.loan.struts.action;

import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.BusinessServiceName;

public class LoanActivityAction extends AccountAppAction {
	
	private LoanBusinessService loanService;
//	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public LoanActivityAction()throws Exception {
		loanService = (LoanBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Loan);
	}
	
	@Override
	protected BusinessService getService() {
		return loanService;
	}	

}
