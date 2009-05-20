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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;


public class LoanAccountActionIndividualLoansIntegrationTest extends MifosIntegrationTest {


	public LoanAccountActionIndividualLoansIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testShouldCallCreateMethodIfNewMembersSelected()
			throws Exception {
		GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
		LoanAccountAction loanAccountAction = new LoanAccountAction(
				new LoanBusinessService(), new ConfigurationBusinessService(),
				glimLoanUpdaterMock);
		LoanBO parentLoanMock = createMock(LoanBO.class);
		LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
		List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
		LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300 = LoanAccountDetailsViewHelper
				.createInstanceForTest("3", "2", Double.valueOf(300), "2");
		clientDetails.add(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300);
		List<LoanBO> loans = new ArrayList<LoanBO>();
		glimLoanUpdaterMock.createIndividualLoan(loanAccountActionForm,
				parentLoanMock, true, LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300);
		expectLastCall().atLeastOnce();
		replay(glimLoanUpdaterMock, parentLoanMock);
		loanAccountAction.handleIndividualLoans(parentLoanMock,
				loanAccountActionForm, true, clientDetails, loans);
		verify(glimLoanUpdaterMock, parentLoanMock);

	}

	public void testShouldCallUpdateMethodIfExistingMembersChanged()
			throws Exception {
		GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
		LoanAccountAction loanAccountAction = new LoanAccountAction(
				new LoanBusinessService(), new ConfigurationBusinessService(),
				glimLoanUpdaterMock);
		LoanBO loanMock = createMock(LoanBO.class);
		expect(loanMock.getAccountId()).andReturn(2).anyTimes();
		ClientBO customerMock = createMock(ClientBO.class);
		expect(loanMock.getCustomer()).andReturn(customerMock).anyTimes();
		expect(customerMock.getGlobalCustNum()).andReturn("3").anyTimes();
		LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
		List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
		LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300 = LoanAccountDetailsViewHelper
				.createInstanceForTest("3", "2", Double.valueOf(300), "2");
		clientDetails.add(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300);
		List<LoanBO> loans = new ArrayList<LoanBO>();
		loans.add(loanMock);
		glimLoanUpdaterMock.updateIndividualLoan(
				LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300, loanMock);
		expectLastCall().atLeastOnce();
		replay(glimLoanUpdaterMock, loanMock, customerMock);
		loanAccountAction.handleIndividualLoans(loanMock,
				loanAccountActionForm, true, clientDetails, loans);
		verify(glimLoanUpdaterMock, loanMock, customerMock);
	}

	public void testShouldCallDeleteMethodIfExistingMembersRemoved()
			throws Exception {
		GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
		LoanAccountAction loanAccountAction = new LoanAccountAction(
				new LoanBusinessService(), new ConfigurationBusinessService(),
				glimLoanUpdaterMock);
		LoanBO loanMock = createMock(LoanBO.class);
		expect(loanMock.getAccountId()).andReturn(2).anyTimes();
		ClientBO customerMock = createMock(ClientBO.class);
		expect(loanMock.getCustomer()).andReturn(customerMock).anyTimes();
		expect(customerMock.getGlobalCustNum()).andReturn("3").anyTimes();
		LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
		List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
		List<LoanBO> loans = new ArrayList<LoanBO>();
		loans.add(loanMock);
		glimLoanUpdaterMock.delete(loanMock);
		expectLastCall().atLeastOnce();
		replay(glimLoanUpdaterMock, loanMock, customerMock);
		loanAccountAction.handleIndividualLoans(loanMock,
				loanAccountActionForm, true, clientDetails, loans);
		verify(glimLoanUpdaterMock, loanMock, customerMock);

	}
}
