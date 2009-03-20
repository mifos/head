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
 
package org.mifos.application.accounts.loan.struts.action;

import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.framework.business.service.BusinessService;

public class LoanActivityAction extends AccountAppAction {
	
	private LoanBusinessService loanService;
//	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public LoanActivityAction()throws Exception {
		loanService = new LoanBusinessService();
	}
	
	@Override
	protected BusinessService getService() {
		return loanService;
	}	

}
