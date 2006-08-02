/**

 * TestFeeAction.java    version: xxx



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

package org.mifos.application.fees.struts.action;

import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.struts.actionforms.FeeActionForm;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FeeActionTest extends MifosMockStrutsTestCase {


	private final static String GLOCDE_ID = "47";
	private FeeBO fee;
	private FeeBO fee1;
	private FeeBO fee2;
	private FeeBO fee3;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(fee);
		TestObjectFactory.cleanUp(fee1);
		TestObjectFactory.cleanUp(fee2);
		TestObjectFactory.cleanUp(fee3);
		super.tearDown();
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		HttpSession session = request.getSession();
		assertEquals("The size of master data for categories",
				((List<MasterDataEntity>) session
						.getAttribute(FeeConstants.CATEGORYLIST)).size(), 5);
		assertEquals(
				"The size of master data for loan time of charges for one time fees  : ",
				((List<MasterDataEntity>) session
						.getAttribute(FeeConstants.TIMEOFCHARGES)).size(),
				3);
		assertEquals(
				"The size of master data for customer  time of charges for one time fees master : ",
				((List<MasterDataEntity>) session
						.getAttribute(FeeConstants.CUSTOMERTIMEOFCHARGES))
						.size(), 1);
		assertEquals("The size of master data for loan formula : ",
				((List<MasterDataEntity>) session
						.getAttribute(FeeConstants.FORMULALIST)).size(), 3);
		assertEquals("The size of master data for GLCodes of fees : ",
				((List<MasterDataEntity>) session
						.getAttribute(FeeConstants.GLCODE_LIST)).size(), 7);

	}

	public void testFailurePreviewWithAllValuesNull() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		actionPerform();

		assertEquals(5, getErrrorSize());
		assertEquals("Fee Name",1, getErrrorSize("feeName"));
		assertEquals("Fee Applies to Product/Customer",1, getErrrorSize("categoryType"));
		assertEquals("Periodic or OneTime Fee", 1, getErrrorSize("feeFrequencyType"));
		assertEquals("Fee Amount",1, getErrrorSize("amount"));		
		assertEquals("Fee GlCode", 1, getErrrorSize("glCode"));						
		verifyInputForward();
	}

	public void testFailurePreviewWithFeeNameNotNull()  throws Exception{
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("feeName", "CustomerFee");
		actionPerform();
		
		assertEquals(4, getErrrorSize());
		assertEquals("Fee Name",0, getErrrorSize("feeName"));
		assertEquals("Fee Applies to Product/Customer",1, getErrrorSize("categoryType"));
		assertEquals("Periodic or OneTime Fee", 1, getErrrorSize("feeFrequencyType"));
		assertEquals("Fee Amount",1, getErrrorSize("amount"));		
		assertEquals("Fee GlCode", 1, getErrrorSize("glCode"));				
		verifyInputForward();
	}
	
	public void testFailurePreviewWithFeeCategoryNotNull() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("feeName", "CustomerFee");
		addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
		actionPerform();

		assertEquals(3, getErrrorSize());
		assertEquals("Periodic or OneTime Fee", 1, getErrrorSize("feeFrequencyType"));
		assertEquals("Fee Amount",1, getErrrorSize("amount"));		
		assertEquals("Fee GlCode", 1, getErrrorSize("glCode"));				
		verifyInputForward();
	}
	
	public void testFailurePreviewWith_FeeFrequencyOneTime() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("feeName", "CustomerFee");
		addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
		addRequestParameter("feeFrequencyType", FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue().toString());
		actionPerform();

		assertEquals(2, getErrrorSize());
		assertEquals("Fee Amount",1, getErrrorSize("amount"));		
		assertEquals("Fee GlCode", 1, getErrrorSize("glCode"));			
		verifyInputForward();
	}
	
	public void testFailurePreviewWith_FeeFrequencyPeriodic() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("feeName", "CustomerFee");
		addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
		addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter("feeRecurrenceType", MeetingFrequency.MONTHLY.getValue().toString());
		addRequestParameter("monthRecurAfter", "2");
		actionPerform();

		assertEquals(2, getErrrorSize());
		assertEquals("Fee Amount",1, getErrrorSize("amount"));		
		assertEquals("Fee GlCode", 1, getErrrorSize("glCode"));			
		verifyInputForward();
	}
	
	public void testFailurePreviewWith_RateEnteredWithoutFormula() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("feeName", "CustomerFee");
		addRequestParameter("categoryType", FeeCategory.LOAN.getValue().toString());
		addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter("feeRecurrenceType", MeetingFrequency.WEEKLY.getValue().toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("rate", "10");
		actionPerform();

		assertEquals(2, getErrrorSize());
		assertEquals("Fee GlCode", 1, getErrrorSize("glCode"));			
		assertEquals("Fee Rate or Formula", 1, getErrrorSize("RateAndFormula"));
		verifyInputForward();
	}
	
	public void testFailurePreviewWith_AmountNotNull() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("feeName", "CustomerFee");
		addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
		addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter("feeRecurrenceType", MeetingFrequency.MONTHLY.getValue().toString());
		addRequestParameter("monthRecurAfter", "2");
		addRequestParameter("amount", "200");
		actionPerform();

		assertEquals(1, getErrrorSize());
		assertEquals("Fee GlCode", 1, getErrrorSize("glCode"));			
		verifyInputForward();
	}
	
	public void testSuccessfulPreview() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("feeName", "CustomerFee");
		addRequestParameter("categoryType", FeeCategory.LOAN.getValue().toString());
		addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter("feeRecurrenceType", MeetingFrequency.WEEKLY.getValue().toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("rate", "10");
		addRequestParameter("feeFormula", FeeFormula.AMOUNT.getValue().toString());
		addRequestParameter("glCode", GLOCDE_ID);
		actionPerform();
		assertEquals(0, getErrrorSize());
		verifyForward(ActionForwards.preview_success.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	
	public void testSuccessfulCreateOneTimeFee() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", FeeCategory.ALLCUSTOMERS.getValue().toString());
		addRequestParameter("amount", "100");
		addRequestParameter("feeName", "Customer_One_time");
		addRequestParameter("feeFrequencyType",	FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue().toString());
		addRequestParameter("glCode", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",	(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		FeeActionForm actionForm = (FeeActionForm)request.getSession().getAttribute("feeactionform");
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());
		assertEquals("Customer_One_time", fee.getFeeName());
		assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType().getId());
		assertEquals(RateAmountFlag.AMOUNT,fee.getFeeType());
		assertEquals(new Money("100.0"), ((AmountFeeBO)fee).getFeeAmount());
		assertTrue(fee.isOneTime());
		assertFalse(fee.isCustomerDefaultFee());
		assertTrue(fee.isActive());
	}
	
	public void testSuccessfulCreateOneTimeAdminFee() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", FeeCategory.ALLCUSTOMERS.getValue().toString());
		addRequestParameter("amount", "100");
		addRequestParameter("customerDefaultFee", "1");
		addRequestParameter("feeName", "Customer_One_time_Default_Fee");
		addRequestParameter("feeFrequencyType",	FeeFrequencyType.ONETIME.getValue().toString());
		addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue().toString());
		addRequestParameter("glCode", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		FeeActionForm actionForm = (FeeActionForm)request.getSession().getAttribute("feeactionform");
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());
		assertEquals("Customer_One_time_Default_Fee", fee.getFeeName());
		assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType().getId());
		assertEquals(RateAmountFlag.AMOUNT,fee.getFeeType());
		assertEquals(new Money("100.0"), ((AmountFeeBO)fee).getFeeAmount());
		assertTrue(fee.isOneTime());
		assertTrue(fee.isCustomerDefaultFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulCreatePeriodicFee() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", FeeCategory.ALLCUSTOMERS.getValue().toString());
		addRequestParameter("amount", "100");
		addRequestParameter("customerDefaultFee", "1");
		addRequestParameter("feeName", "Customer Periodic Fee");
		addRequestParameter("feeFrequencyType",	FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter("feeRecurrenceType",MeetingFrequency.WEEKLY.getValue().toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("glCode", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",
				(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		FeeActionForm actionForm = (FeeActionForm)request.getSession().getAttribute("feeactionform");
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());

		assertEquals("Customer Periodic Fee", fee.getFeeName());
		assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType()
				.getId());
		assertEquals(RateAmountFlag.AMOUNT,fee.getFeeType());
		assertEquals(new Money("100.0"), ((AmountFeeBO)fee).getFeeAmount());
		assertTrue(fee.isPeriodic());
		assertTrue(fee.isCustomerDefaultFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulCreatePeriodicFeeWithFormula() throws Exception {
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("categoryType", FeeCategory.LOAN.getValue().toString());
		addRequestParameter("rate", "23");
		addRequestParameter("amount", "");
		addRequestParameter("feeFormula", FeeFormula.AMOUNT.getValue().toString());
		addRequestParameter("feeName", "Loan_Periodic_Fee");
		addRequestParameter("customerDefaultFee", "0");
		addRequestParameter("feeFrequencyType",	FeeFrequencyType.PERIODIC.getValue().toString());
		addRequestParameter("feeRecurrenceType",MeetingFrequency.WEEKLY.getValue().toString());
		addRequestParameter("weekRecurAfter", "2");
		addRequestParameter("glCode", GLOCDE_ID);
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "create");
		addRequestParameter("org.apache.struts.taglib.html.TOKEN",
				(String) request.getSession().getAttribute(
						"org.apache.struts.action.TOKEN"));
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());

		FeeActionForm actionForm = (FeeActionForm)request.getSession().getAttribute("feeactionform");
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());

		assertEquals("Loan_Periodic_Fee", fee.getFeeName());
		assertEquals(FeeCategory.LOAN.getValue(), fee.getCategoryType().getId());
		assertEquals(RateAmountFlag.RATE,fee.getFeeType());
		assertEquals(23.0, ((RateFeeBO)fee).getRate());
		assertEquals(((RateFeeBO)fee).getFeeFormula().getId(), FeeFormula.AMOUNT.getValue());
		assertTrue(fee.isPeriodic());
		assertTrue(fee.isActive());
	}
	
	public void testSuccessfulManage_AmountFee() throws Exception {
		fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS,"100",FeePayment.UPFRONT);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,fee, request.getSession());
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());

		FeeActionForm actionForm = (FeeActionForm)request.getSession().getAttribute("feeactionform");
		assertEquals("100.0",actionForm.getAmount());
		assertNull(actionForm.getRate());
		assertNull(actionForm.getFeeFormula());
		
		assertEquals("The size of master data for status",2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(FeeConstants.STATUSLIST,request.getSession())).size());
	}

	public void testSuccessfulManage_RateFee() throws Exception {
		fee = TestObjectFactory.createOneTimeRateFee("One Time Fee", FeeCategory.ALLCUSTOMERS,24.0, FeeFormula.AMOUNT, FeePayment.UPFRONT);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,fee, request.getSession());
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());

		FeeActionForm actionForm = (FeeActionForm)request.getSession().getAttribute("feeactionform");
		assertEquals("24.0",actionForm.getRate());
		assertEquals(FeeFormula.AMOUNT.getValue().toString(),actionForm.getFeeFormula());
		assertNull(actionForm.getAmount());
		
		assertEquals("The size of master data for status",2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(FeeConstants.STATUSLIST,request.getSession())).size());
	}
	
	public void testFailureEditPreviewForAmount() {
		fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS,"100",FeePayment.UPFRONT);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,fee, request.getSession());
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		actionPerform();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("Fee Amount",1, getErrrorSize("amount"));	
		verifyInputForward();
	}

	public void testFailureEditPreviewForRate() throws Exception{
		fee = TestObjectFactory.createOneTimeRateFee("One Time Fee", FeeCategory.ALLCUSTOMERS,24.0, FeeFormula.AMOUNT, FeePayment.UPFRONT);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,fee, request.getSession());
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		actionPerform();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("rate", "");
		actionPerform();
		assertEquals(1, getErrrorSize());
		assertEquals("RateAndFormula",1, getErrrorSize("RateAndFormula"));
		verifyInputForward();
	}
	
	public void testSuccessfulEditPreview() throws Exception {
		fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS,"100",FeePayment.UPFRONT);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,fee, request.getSession());
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "200.0");
		addRequestParameter("feeStatus", FeeStatus.INACTIVE.getValue().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.editpreview_success.toString());

		FeeActionForm actionForm = (FeeActionForm)request.getSession().getAttribute("feeactionform");
		assertEquals(new Money("200"),actionForm.getAmountValue());
		assertEquals(FeeStatus.INACTIVE,actionForm.getFeeStatusValue());
		assertNull(actionForm.getRate());
		assertNull(actionForm.getFeeFormula());
	}

	public void testSuccessfulUpdate_AmountFee() throws Exception {
		fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS,"100",FeePayment.UPFRONT);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,fee, request.getSession());
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("amount", "200.0");
		addRequestParameter("feeStatus", FeeStatus.INACTIVE.getValue().toString());
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.update_success.toString());
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertFalse(fee.isActive());
		assertEquals(new Money("200.0"), ((AmountFeeBO)fee).getFeeAmount());
	}
	
	public void testSuccessfulUpdate_RateFee()  throws Exception{
		fee = TestObjectFactory.createOneTimeRateFee("One Time Fee", FeeCategory.ALLCUSTOMERS,24.0, FeeFormula.AMOUNT, FeePayment.UPFRONT);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,fee, request.getSession());
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "manage");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter("rate", "30");
		actionPerform();

		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "update");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.update_success.toString());
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertTrue(fee.isActive());
		assertEquals(30.0, ((RateFeeBO)fee).getRate());
	}
	
	public void testSuccessfulViewAllFees() throws Exception{
		fee = TestObjectFactory.createOneTimeRateFee("Group_Fee", FeeCategory.GROUP,10.0, FeeFormula.AMOUNT, FeePayment.UPFRONT);
		fee1 = TestObjectFactory.createOneTimeRateFee("Customer_Fee", FeeCategory.ALLCUSTOMERS,20.0, FeeFormula.AMOUNT, FeePayment.UPFRONT);
		fee2 = TestObjectFactory.createOneTimeRateFee("Loan_Fee1", FeeCategory.LOAN,30.0, FeeFormula.AMOUNT, FeePayment.UPFRONT);
		fee3 = TestObjectFactory.createOneTimeRateFee("Center_Fee", FeeCategory.CENTER,40.0, FeeFormula.AMOUNT, FeePayment.UPFRONT);
		HibernateUtil.closeSession();
		setRequestPathInfo("/feeaction.do");
		addRequestParameter("method", "viewAll");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.viewAll_success.toString());
		List<FeeBO> customerFees = (List<FeeBO>)SessionUtils.getAttribute(FeeConstants.CUSTOMER_FEES, request.getSession());
		List<FeeBO> productFees = (List<FeeBO>)SessionUtils.getAttribute(FeeConstants.PRODUCT_FEES,  request.getSession());
		assertEquals(3, customerFees.size());
		assertEquals(1, productFees.size());
		
		assertEquals("Center_Fee",customerFees.get(0).getFeeName());
		assertEquals("Customer_Fee",customerFees.get(1).getFeeName());
		assertEquals("Group_Fee",customerFees.get(2).getFeeName());
		
		assertEquals("Loan_Fee1",productFees.get(0).getFeeName());
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		fee1 = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee1.getFeeId());
		fee2 = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee2.getFeeId());
		fee3 = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee3.getFeeId());
	}
}
