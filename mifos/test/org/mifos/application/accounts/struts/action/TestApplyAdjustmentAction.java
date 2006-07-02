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


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;




import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;


import servletunit.struts.MockStrutsTestCase;

/**
 * This class tests methods of ApplyAdjustment action class.
 * @author ashishsm
 *
 */
public class TestApplyAdjustmentAction extends MockStrutsTestCase {
	private CenterBO center;
	private GroupBO group;
	private LoanBO loan;

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
	 *
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp()throws Exception{
		super.setUp();
		try {	setServletConfigFile("WEB-INF/web.xml");
				setConfigFile("org/mifos/framework/util/helpers/struts-config.xml");
		} catch (Exception e) {

			e.printStackTrace();
		}
		// create the user context with the preferred locale,branch id and userId.
		UserContext userContext=new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		userContext.setBranchId(Short.valueOf("1"));
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		// prepare the roles and add to the user context.
		HashSet hashSet = new HashSet();
		hashSet.add(new Short("1"));
		userContext.setRoles(hashSet);
		// prepare activity context
		ActivityContext activityContext = new ActivityContext(Short.valueOf("116"),Short.valueOf("0"),Short.valueOf("0"));
		getRequest().getSession().setAttribute(LoginConstants.ACTIVITYCONTEXT, activityContext);

		request.getSession().setAttribute(Constants.USERCONTEXT,userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");

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
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}

	private void applyPayment(LoanBO loan,double amnt)throws Exception{
		Date currentDate = new Date(System.currentTimeMillis());
		FinancialInitializer.initialize();
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.add(loan.getAccountActionDate(Short.valueOf("1")));
		PaymentData accountPaymentDataView = TestObjectFactory.getLoanAccountPaymentData(accntActionDates,TestObjectFactory.getMoneyForMFICurrency(amnt),null,Short.valueOf("1"),"receiptNum",Short.valueOf("1"),currentDate,currentDate );
		
		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.updateObject(loan);
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
	}

	public void testLoadAdjustment()throws Exception {
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,212*6);

		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());

		addRequestParameter("method", "loadAdjustment");
		setRequestPathInfo("/applyAdjustment");

		actionPerform();

		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("loadadjustment_success");
	}

	public void testPreviewAdjustment()throws Exception{
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,212*6);
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("adjustmentNote", "adjusting the last payment");
		addRequestParameter("adjustcheckbox", "true");
		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("previewadj_success");
	}

	public void testApplyAdjustment()throws Exception{
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,212*6);
		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "applyAdjustment");
		addRequestParameter("adjustmentNote", "Loan adjustment testing");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());

		getRequest().getSession().setAttribute(Constants.USERCONTEXT, TestObjectFactory.getUserContext());

		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("applyadj_success");
	}

	public void testCancelAdjustment()throws Exception{
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "cancelAdjustment");
		actionPerform();
		verifyForward("canceladj_success");
	}

	/**
	 *This methods tries to check the result of  applying adjustment
	 *to a loan account which has no payment made to it.
	*/
	public void testLoadAdjustmentWithNoPmnts()throws Exception{
		loan =(LoanBO)getLoanAccount();
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		addRequestParameter("method", "loadAdjustment");
		setRequestPathInfo("/applyAdjustment");
		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("loadAdjustment_failure");

	}

	public void testAdjustmentForZeroPmnt()throws Exception{

		loan =(LoanBO)getLoanAccount();
		applyPayment(loan, 0);
		TestObjectFactory.flushandCloseSession();
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "applyAdjustment");
		addRequestParameter("adjustmentNote", "Loan adjustment testing");
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		getRequest().getSession().setAttribute(Constants.BUSINESS_KEY, loan);
		actionPerform();
		loan = (LoanBO)TestObjectFactory.getObject(AccountBO.class, loan.getAccountId());
		verifyForward("applyAdjustment_failure");

	}

	public void testValidation()throws Exception{
		loan =(LoanBO)getLoanAccount();
		applyPayment(loan,212*6);
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("adjustcheckbox", "true");
		actionPerform();
		verifyActionErrors(new String[]{"errors.mandatorytextarea"});
	} 
	
	public void testValidationAdjustmentNoteSize()throws Exception{
		loan =(LoanBO)getLoanAccount();
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		setRequestPathInfo("/applyAdjustment");
		addRequestParameter("method", "previewAdjustment");
		addRequestParameter("adjustmentNote", "This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.This is to test errors in case adjustment note size exceeds 200 characters.");
		addRequestParameter("adjustcheckbox", "true");
		actionPerform();
		verifyActionErrors(new String[]{"errors.adjustmentNoteTooBig"});
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loan);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		super.tearDown();
	}

	
}
