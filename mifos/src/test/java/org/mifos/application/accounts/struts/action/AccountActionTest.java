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
 
package org.mifos.application.accounts.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountActionTest extends MifosMockStrutsTestCase {

	public AccountActionTest() throws SystemException, ApplicationException {
        super();
    }

    protected AccountBO accountBO = null;

	private CustomerBO center = null;

	private CustomerBO group = null;

	private UserContext userContext;
	
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, AccountAppAction.class);
		accountBO = getLoanAccount();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfulRemoveFees() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Short feeId = null;
		Set<AccountFeesEntity> accountFeesSet = accountBO.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesSet) {
			feeId = accountFeesEntity.getFees().getFeeId();
		}
		setRequestPathInfo("/accountAppAction");
		addRequestParameter("method", "removeFees");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("feeId", feeId.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("remove_success");
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	private AccountBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
	}

	public void testGetTrxnHistorySucess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Date currentDate = new Date(System.currentTimeMillis());
		setRequestPathInfo("/accountAppAction");
		addRequestParameter("method", "getTrxnHistory");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("feeId", "123");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
		LoanBO loan = (LoanBO) accountBO;
		loan.setUserContext(TestUtils.makeUser());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(0), null, loan.getPersonnel(),
						"receiptNum", Short.valueOf("1"), currentDate,
						currentDate);
		loan.applyPaymentWithPersist(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		actionPerform();
		verifyForward("getTransactionHistory_success");
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
		List<TransactionHistoryView> trxnHistoryList = (List<TransactionHistoryView>) SessionUtils
				.getAttribute(SavingsConstants.TRXN_HISTORY_LIST, request);
		for (TransactionHistoryView transactionHistoryView : trxnHistoryList)
			assertEquals(accountBO.getUserContext().getName(),
					transactionHistoryView.getPostedBy());
	}
}
