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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CloseLoanActionTest extends MifosMockStrutsTestCase {

	public CloseLoanActionTest() throws SystemException, ApplicationException {
        super();
    }

    protected UserContext userContext = null;
	
	protected LoanBO loanBO = null;
	
	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private Date currentDate = null;
	
	private String flowKey;
	
	@Override
	protected void setUp() throws Exception {

		super.setUp();
		
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, LoanDisbursmentAction.class);
		setRequestPathInfo("/editStatusAction");
		currentDate=new Date(System.currentTimeMillis());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}
	
	private LoanBO getLoanAccount(AccountState state, Date startDate) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("424231423415", group, 
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
				loanOffering);
	}

	private LoanBO getLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
				loanOffering);
	}

	public void testRescheduleLoan() throws Exception{
		Session session =StaticHibernateUtil.openSession();
		loanBO = getLoanAccount();
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO,request);
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
		addRequestParameter("notes", "reschedule");
		addRequestParameter("newStatusId", "8");
		addRequestParameter("input", "loan");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.loan_detail_page.toString());
		AccountBO  closedAccount = group.getCustomerAccount();
		session.refresh(loanBO);
		List<TransactionHistoryView> history = loanBO.getTransactionHistoryView();

		for(TransactionHistoryView entry : history) {
			assertEquals(AccountConstants.LOAN_RESCHEDULED, entry.getType());
		}
		StaticHibernateUtil.closeSession(session);
	}

	private LoanBO createInitialObjects(int disbursalType) {
		loanBO = getLoanAccount(AccountState.LOAN_APPROVED,
				currentDate);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		return loanBO;
	}
}
