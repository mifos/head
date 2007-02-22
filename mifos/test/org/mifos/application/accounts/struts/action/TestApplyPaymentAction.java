/**

 * TestApplyPaymentAction.java    version: xxx

 

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
package org.mifos.application.accounts.struts.action;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.net.URISyntaxException;
import java.util.Date;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestApplyPaymentAction extends MifosMockStrutsTestCase{
	protected AccountBO accountBO;
	private CustomerBO center;
	private CustomerBO group;
	private UserContext userContext ;
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI(
					"WEB-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/accounts/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, AccountApplyPaymentAction.class);
	}
	
	@Override
	public void tearDown()throws Exception{
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testApplyPaymentLoad()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		accountBO = createLoanAccount();
		setRequestPathInfo("/applyPaymentAction");
		addRequestParameter("method", "load");
		addRequestParameter("input","loan");
		addRequestParameter("accountId",accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.LOAD_SUCCESS);
		verifyNoActionErrors();
		assertNotNull(SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE,request));
		AccountApplyPaymentActionForm actionForm = (AccountApplyPaymentActionForm)request.getSession().getAttribute("applyPaymentActionForm");
		assertEquals(actionForm.getAmount(),accountBO.getTotalPaymentDue());
	}
	
	public void testApplyPaymentPreview(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/applyPaymentAction");
		String currentDate = DateHelper.getCurrentDate(userContext.getPereferedLocale());
		addRequestParameter("receiptDate",currentDate);
		addRequestParameter("transactionDate",currentDate);		
		addRequestParameter("paymentTypeId","1");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.PREVIEW_SUCCESS);
	}
	
	public void testApplyPaymentPreviewWithNoAmount(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/applyPaymentAction");
		String currentDate = DateHelper.getCurrentDate(userContext.getPereferedLocale());
		addRequestParameter("receiptDate",currentDate);
		addRequestParameter("transactionDate",currentDate);		
		addRequestParameter("paymentTypeId","1");
		addRequestParameter("method", "preview");
		addRequestParameter("accountType", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyInputForward();
	}
	
	public void testApplyPaymentForLoan()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		accountBO = createLoanAccount();
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(AccountState.LOANACC_BADSTANDING.getValue(),
				null, "");
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		AccountApplyPaymentActionForm accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
		accountApplyPaymentActionForm.setAmount(new Money("212"));
		request.getSession().setAttribute("applyPaymentActionForm",accountApplyPaymentActionForm);
		setRequestPathInfo("/applyPaymentAction");
		addRequestParameter("input","loan");
		addRequestParameter("method", "applyPayment");
		addRequestParameter("accountId",accountBO.getAccountId().toString());
		addRequestParameter("receiptId","101");

		String currentDate = DateHelper.getCurrentDate(userContext.getPereferedLocale());
		addRequestParameter("receiptDate",currentDate);
		addRequestParameter("transactionDate",currentDate);
		addRequestParameter("paymentTypeId","1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("loan_detail_page");
		assertEquals(new Money(), accountBO.getTotalPaymentDue());
		assertEquals(0, accountBO.getTotalInstallmentsDue().size());
		assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, accountBO.getAccountState().getId().shortValue());
	}
	
	public void testApplyPaymentAndRetrievalForLoanWhenStatusIsChanged()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		accountBO = createLoanAccount();
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(AccountState.LOANACC_BADSTANDING.getValue(),
				null, "");
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		AccountApplyPaymentActionForm accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
		accountApplyPaymentActionForm.setAmount(new Money("212"));
		request.getSession().setAttribute("applyPaymentActionForm",accountApplyPaymentActionForm);
		setRequestPathInfo("/applyPaymentAction");
		addRequestParameter("input","loan");
		addRequestParameter("method", "applyPayment");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("accountId",accountBO.getAccountId().toString());
		addRequestParameter("receiptId","101");

		String currentDate = DateHelper.getCurrentDate(userContext.getPereferedLocale());
		addRequestParameter("receiptDate",currentDate);
		addRequestParameter("transactionDate",currentDate);
		addRequestParameter("paymentTypeId","1");
		actionPerform();
		verifyForward("loan_detail_page");
		
		center = TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		assertEquals(new Money(), accountBO.getTotalPaymentDue());
		assertEquals(0, accountBO.getTotalInstallmentsDue().size());
		assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, accountBO.getAccountState().getId().shortValue());
		
		setRequestPathInfo("/loanAccountAction");
		addRequestParameter("globalAccountNum",accountBO.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "get");
		actionPerform();
		LoanBO loan =(LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,request);
		assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, loan.getAccountState().getId().shortValue());
		assertNotNull(loan.getAccountState().getName());
	}
	
	public void testApplyPaymentForLoanWhenReceiptDateisNull()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		accountBO = createLoanAccount();
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(AccountState.LOANACC_BADSTANDING.getValue(),
				null, "");
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		AccountApplyPaymentActionForm accountApplyPaymentActionForm = new AccountApplyPaymentActionForm();
		accountApplyPaymentActionForm.setAmount(new Money("212"));
		request.getSession().setAttribute("applyPaymentActionForm",accountApplyPaymentActionForm);
		setRequestPathInfo("/applyPaymentAction");
		addRequestParameter("input","loan");
		addRequestParameter("method", "applyPayment");
		addRequestParameter("accountId",accountBO.getAccountId().toString());
		addRequestParameter("receiptId","101");
		String currentDate = DateHelper.getCurrentDate(userContext.getPereferedLocale());
		addRequestParameter("receiptDate","");
		addRequestParameter("transactionDate",currentDate);

		addRequestParameter("paymentTypeId","1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("loan_detail_page");
		assertEquals(new Money(), accountBO.getTotalPaymentDue());
		assertEquals(0, accountBO.getTotalInstallmentsDue().size());
		assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING, accountBO.getAccountState().getId().shortValue());
	}
	public void testApplyPaymentPrevious(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/applyPaymentAction");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(Constants.PREVIOUS_SUCCESS);
	}
	
	public void testApplyPaymentCancel(){
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/applyPaymentAction");
		addRequestParameter("method", "cancel");
		addRequestParameter("input","loan");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("loan_detail_page");
	}
	
	private AccountBO createLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOANACC_ACTIVEINGOODSTANDING, startDate,
				loanOffering);
	}
}
