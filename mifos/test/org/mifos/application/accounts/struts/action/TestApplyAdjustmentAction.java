/**

 * TestApplyAdjustmentAction.java    version: 1.0



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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.TestAccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * This class tests methods of ApplyAdjustment action class.
 */
public class TestApplyAdjustmentAction extends MifosMockStrutsTestCase {
	private CenterBO center;
	private GroupBO group;
	private LoanBO loan;
	private UserContext userContext;
	private String flowKey;
	public TestApplyAdjustmentAction(){
		try {
			MifosLogManager.configure(FilePaths.LOGFILE);
				
		} catch (LoggerConfigurationException e) {

			e.printStackTrace();
		}
	}

	/**
	 * This sets the web.xml,struts-config.xml and prepares the userContext
	 * and activityContext and sets them in the session.
	 */
	@Override
	public void setUp()throws Exception{
		super.setUp();
		setServletConfigFile("WEB-INF/web.xml");
		setConfigFile("org/mifos/application/accounts/struts-config.xml");
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, ApplyAdjustment.class);
	}

	private AccountBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		LoanBO loan = TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();
		return (LoanBO)TestObjectFactory.getObject(LoanBO.class,loan.getAccountId());
	}

	private void applyPayment(LoanBO loan,double amnt)throws Exception{
		Date currentDate = new Date(System.currentTimeMillis());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.add(loan.getAccountActionDate(Short.valueOf("1")));
		PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,TestObjectFactory.getMoneyForMFICurrency(amnt),null,loan.getPersonnel(),"receiptNum",Short.valueOf("1"),currentDate,currentDate );
		
		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.updateObject(loan);
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
	}

	public void testLoadAdjustment()throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,700);

		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());

		addRequestParameter("method", "loadAdjustment");
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("loadadjustment_success");
	}

	public void testPreviewAdjustment()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,700);
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("adjustmentNote", "adjusting the last payment");
		addRequestParameter("adjustcheckbox", "true");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("previewadj_success");
	}
	
	public void testPreviewAdjustment_failure()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,700);
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("previewAdjustment_failure");
	}

	public void testApplyAdjustment()throws Exception{
		PersonnelBO personnel = new PersonnelPersistence()
		.getPersonnel(TestObjectFactory.getUserContext().getId());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,212);
		loan =(LoanBO) TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
		applyPayment(loan,700);
		loan =(LoanBO) TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
		AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
				new AccountStateEntity(AccountState.LOANACC_ACTIVEINGOODSTANDING),
				new AccountStateEntity(AccountState.LOANACC_ACTIVEINGOODSTANDING),
				personnel, loan);
		TestAccountBO.addToAccountStatusChangeHistory(loan,historyEntity);
		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		loan =(LoanBO) TestObjectFactory.getObject(AccountBO.class,	loan.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan,request);
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "applyAdjustment");
		addRequestParameter("adjustmentNote", "Loan adjustment testing");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		getRequest().getSession().setAttribute(Constants.USERCONTEXT, TestObjectFactory.getUserContext());

		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("applyadj_success");
	}
	
	public void testApplyAdjustmentWhenAccountsSecondLastStateWasBadStanding()throws Exception{
		PersonnelBO personnel = new PersonnelPersistence()
		.getPersonnel(TestObjectFactory.getUserContext().getId());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,212);
		loan =(LoanBO) TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
		applyPayment(loan,700);
		loan=(LoanBO)TestObjectFactory.getObject(LoanBO.class,loan.getAccountId());
		AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
				new AccountStateEntity(AccountState.LOANACC_ACTIVEINGOODSTANDING),
				new AccountStateEntity(AccountState.LOANACC_ACTIVEINGOODSTANDING),
				personnel, loan);
		TestAccountBO.addToAccountStatusChangeHistory(loan,historyEntity);
		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		loan=(LoanBO)TestObjectFactory.getObject(LoanBO.class,loan.getAccountId());		
		historyEntity = new AccountStatusChangeHistoryEntity(
				new AccountStateEntity(AccountState.LOANACC_BADSTANDING),
				new AccountStateEntity(AccountState.LOANACC_ACTIVEINGOODSTANDING),
				personnel, loan);
		TestAccountBO.addToAccountStatusChangeHistory(loan,historyEntity);
		TestObjectFactory.updateObject(loan);
		HibernateUtil.closeSession();
		loan=(LoanBO)TestObjectFactory.getObject(LoanBO.class,loan.getAccountId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan,request);
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "applyAdjustment");
		addRequestParameter("adjustmentNote", "Loan adjustment testing");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		getRequest().getSession().setAttribute(Constants.USERCONTEXT, TestObjectFactory.getUserContext());

		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		assertEquals(AccountState.LOANACC_BADSTANDING.getValue(),loan.getAccountState().getId());
		verifyForward("applyadj_success");
	}

	public void testCancelAdjustment()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "cancelAdjustment");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("canceladj_success");
	}

	public void testLoadAdjustmentWithNoPmnts()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)getLoanAccount();
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		addRequestParameter("method", "loadAdjustment");
		setRequestPathInfo("/applyAdjustment");
		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("loadAdjustment_failure");

	}

	public void testAdjustmentForZeroPmnt()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan, 0);
		TestObjectFactory.flushandCloseSession();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan,request);
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "applyAdjustment");
		addRequestParameter("adjustmentNote", "Loan adjustment testing");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan,request);
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("applyAdjustment_failure");

	}

	public void testValidation()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)createLoanAccount();
		applyPayment(loan,700);
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("adjustcheckbox", "true");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[]{"errors.mandatorytextarea"});
	} 
	
	public void testValidationAdjustmentNoteSize()throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		loan =(LoanBO)createLoanAccount();
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("adjustmentNote", "This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.");
		addRequestParameter("adjustcheckbox", "true");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[]{"errors.adjustmentNoteTooBig"});
	}

	private AccountBO createLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loan);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	
}
