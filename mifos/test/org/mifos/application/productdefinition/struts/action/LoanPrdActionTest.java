/**

 * LoanPrdActionTest.java    version: 1.0

 

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
package org.mifos.application.productdefinition.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdActionTest extends MifosMockStrutsTestCase {

	private LoanOfferingBO loanOffering;

	private String flowKey;

	UserContext userContext = null;

	private FeeBO fee;

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.cleanUp(fee);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader
					.getURI(
							"org/mifos/application/productdefinition/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = TestObjectFactory.getActivityContext();
		request.getSession(false).setAttribute("ActivityContext", ac);
		flowKey = createFlow(request, LoanPrdAction.class);
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "load");

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		List<ProductCategoryBO> productCategories = (List<ProductCategoryBO>) SessionUtils
				.getAttribute(
						ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST,
						request);
		assertEquals("The size of master data for categories", 1,
				productCategories.size());
		for (ProductCategoryBO productCategory : productCategories) {
			assertNotNull(productCategory.getProductType());
		}
		assertEquals("The size of applicable list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANAPPLFORLIST, request))
						.size());

		assertEquals("The size of grace period types list", 3,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
						request)).size());
		assertEquals("The size of interest types list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.INTERESTTYPESLIST, request))
						.size());
		assertEquals("The size of applicable list", 10,
				((List<GLCodeEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRICIPALGLCODELIST,
						request)).size());
		assertEquals("The size of applicable list", 3,
				((List<GLCodeEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANINTERESTGLCODELIST,
						request)).size());
		List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(
				ProductDefinitionConstants.SRCFUNDSLIST, request);
		assertNotNull(funds);
		List<FeeView> loanFees = (List<FeeView>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANFEESLIST, request);
		assertNull(loanFees);
		List<FeeBO> productFees = (List<FeeBO>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANPRDFEE, request);
		assertNotNull(productFees);
		List<FeeView> selectedFees = (List<FeeView>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANPRDFEESELECTEDLIST, request);
		assertNotNull(selectedFees);
		List<FundBO> selectedFunds = (List<FundBO>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST, request);
		assertNotNull(selectedFunds);
		assertEquals(0, (selectedFees).size());
		assertEquals(0, (selectedFunds).size());
	}

	public void testPreviewWithOutData() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyActionErrors(new String[] {
				"Please select the GL code for principal.",
				"Please select the Product category.",
				"Please specify the Applicable for.",
				"Please specify the Default # of installments.",
				"Please specify the Frequency of installments.",
				"Please specify the Max # of installments.",
				"Please specify the Max loan amount.",
				"Please specify the Min # of installments.",
				"Please specify the Min loan amount.",
				"Please specify the Product instance name.",
				"Please specify the Recur every.",
				"Please specify the Short name.",
				"Please specify the Start date.", "errors.mandatoryconfig",
				"errors.mandatoryconfig", "errors.mandatoryconfig",
				"errors.select", "errors.selectconfig" });
		verifyInputForward();
	}

	public void testPreviewWithImproperMinMaxDefAmount() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "1000");
		addRequestParameter("defaultLoanAmount", "500");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "10");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "4");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { "errors.defLoanAmount",
				"errors.maxminLoanAmount" });
		verifyInputForward();
	}

	public void testPreviewWithImproperMinMaxDefInterestRates()
			throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "14");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "10");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "4");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { "errors.defIntRateconfig",
				"errors.maxminIntRateconfig" });
		verifyInputForward();
	}

	public void testPreviewWithImproperMinMaxInterestRates() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "99");
		addRequestParameter("minInterestRate", "999");
		addRequestParameter("defInterestRate", "999");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "10");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "4");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { "errors.defIntRateconfig",
				"errors.maxminIntRateconfig" });
		verifyInputForward();
	}

	public void testPreviewWithImproperMinMaxDefInstallments() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", DateHelper.getCurrentDate(userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "1");
		addRequestParameter("minNoInstallments", "20");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] {
				"Please specify a valid Max # of installments. Max # of installments should be greater than or equal to Min # of installments.",
				"Please specify valid values for Default # of installments. Default # of installments should be a value between Min # of installments and Max # of installments, inclusive." });
		verifyInputForward();
	}

	public void testPreviewWithGraceTypeNotNoneAndNoGraceDuration()
			throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("gracePeriodType", "2");
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { "Please specify the Grace period duration. Grace period duration should be less than or equal to Max number of installments." });
		verifyInputForward();
	}

	public void testPreviewWithStartDateLessThanCurrentDate() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(-1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.INVALIDSTARTDATE });
		verifyInputForward();
	}

	public void testPreviewWithEndDateLessThanStartDate() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(-1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.INVALIDENDDATE });
		verifyInputForward();
	}

	public void testPreviewWithFeeNotMatchingFeeFrequency() throws Exception {
		FeeBO fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic",
				FeeCategory.LOAN, "100.0", RecurrenceType.MONTHLY, (short) 1);
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "1");
		addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId()
				.toString() });
		addRequestParameter("loanOfferingFunds", new String[] { "1" });
		addRequestParameter("description", "Loan Product");
		addRequestParameter("gracePeriodDuration", "0");
		addRequestParameter("loanCounter", "1");
		addRequestParameter("prinDueLastInstFlag", "1");
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.ERRORFEEFREQUENCY });
		verifyInputForward();
		TestObjectFactory.cleanUp(fee);
	}

	public void testPreview() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_success.toString());
	}

	public void testPreviewForPricDueOnLastInstAndPrincGrace() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("gracePeriodType",
				GraceTypeConstants.PRINCIPALONLYGRACE.getValue().toString());
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("prinDueLastInstFlag", YesNoFlag.YES.getValue()
				.toString());
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE });
		verifyInputForward();
	}

	public void testPreviewForPageExpiration() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyForwardPath("/pages/framework/jsp/pageexpirederror.jsp");
	}

	public void testCancelCreate() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "cancelCreate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.cancelCreate_success.toString());
		assertNull(((FlowManager) request.getSession().getAttribute(
				Constants.FLOWMANAGER)).getFlow(flowKey));
	}

	public void testPrevious() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.previous_success.toString());
	}

	public void testValidate() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_failure.toString());
	}

	public void testValidateForPreview() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(ProductDefinitionConstants.METHODCALLED,
				Methods.preview.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_failure.toString());
	}

	public void testVaildateForCreate() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(ProductDefinitionConstants.METHODCALLED,
				Methods.create.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_failure.toString());
	}

	public void testCreate() throws Exception {
		FeeBO fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic",
				FeeCategory.LOAN, "100.0", RecurrenceType.MONTHLY, (short) 1);
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId()
				.toString() });
		addRequestParameter("loanOfferingFunds", new String[] { "1" });
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "35");
		addRequestParameter("interestGLCode", "45");
		actionPerform();
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());

		assertNotNull(request
				.getAttribute(ProductDefinitionConstants.LOANPRODUCTID));
		assertNotNull(request
				.getAttribute(ProductDefinitionConstants.LOANPRDGLOBALOFFERINGNUM));
		assertNull(((FlowManager) request.getSession().getAttribute(
				Constants.FLOWMANAGER)).getFlow(flowKey));
		TestObjectFactory
				.removeObject((LoanOfferingBO) TestObjectFactory
						.getObject(
								LoanOfferingBO.class,
								(Short) request
										.getAttribute(ProductDefinitionConstants.LOANPRODUCTID)));
		TestObjectFactory.cleanUp(fee);
	}

	public void testManage() throws Exception {
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());

		List<ProductCategoryBO> productCategories = (List<ProductCategoryBO>) SessionUtils
				.getAttribute(
						ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST,
						request);
		assertEquals("The size of master data for categories", 1,
				productCategories.size());
		for (ProductCategoryBO productCategory : productCategories) {
			assertNotNull(productCategory.getProductType());
		}
		assertEquals("The size of applicable list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANAPPLFORLIST, request))
						.size());

		assertEquals("The size of grace period types list", 3,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
						request)).size());
		assertEquals("The size of interest types list", 2,
				((List<MasterDataEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.INTERESTTYPESLIST, request))
						.size());
		assertEquals("The size of applicable list", 10,
				((List<GLCodeEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRICIPALGLCODELIST,
						request)).size());
		assertEquals("The size of applicable list", 3,
				((List<GLCodeEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANINTERESTGLCODELIST,
						request)).size());
		List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(
				ProductDefinitionConstants.SRCFUNDSLIST, request);
		assertNotNull(funds);
		List<FeeView> fees = (List<FeeView>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANFEESLIST, request);
		assertNull(fees);
		List<FeeBO> productFees = (List<FeeBO>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANPRDFEE, request);
		assertNotNull(productFees);
		List<FeeView> selectedFees = (List<FeeView>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANPRDFEESELECTEDLIST, request);
		assertNotNull(selectedFees);
		List<FundBO> selectedFunds = (List<FundBO>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST, request);
		assertNotNull(selectedFunds);
		assertEquals("The size of applicable status list", 2,
				((List<PrdStatusEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRDSTATUSLIST, request))
						.size());
		LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) request
				.getSession().getAttribute(
						ProductDefinitionConstants.LOANPRODUCTACTIONFORM);
		assertNotNull(loanPrdActionForm);

		assertEquals(loanOffering.getPrdOfferingId().toString(),
				loanPrdActionForm.getPrdOfferingId());
		assertEquals(loanOffering.getPrdOfferingName(), loanPrdActionForm
				.getPrdOfferingName());
		assertEquals(loanOffering.getPrdOfferingShortName(), loanPrdActionForm
				.getPrdOfferingShortName());
		assertEquals(loanOffering.getPrdCategory().getProductCategoryID()
				.toString(), loanPrdActionForm.getPrdCategory());
		assertEquals(loanOffering.getPrdStatus().getOfferingStatusId()
				.toString(), loanPrdActionForm.getPrdStatus());
		assertEquals(loanOffering.getPrdApplicableMaster().getId().toString(),
				loanPrdActionForm.getPrdApplicableMaster());
		assertEquals(DateHelper.getUserLocaleDate(TestObjectFactory
				.getContext().getPereferedLocale(), DateHelper
				.toDatabaseFormat(loanOffering.getStartDate())),
				loanPrdActionForm.getStartDate());
		if (loanOffering.getEndDate() != null)
			assertEquals(DateHelper.getUserLocaleDate(TestObjectFactory
					.getContext().getPereferedLocale(), DateHelper
					.toDatabaseFormat(loanOffering.getEndDate())),
					loanPrdActionForm.getEndDate());
		else
			assertNull(loanPrdActionForm.getEndDate());
		assertEquals(loanOffering.getDescription(), loanPrdActionForm
				.getDescription());
		assertEquals(loanOffering.getGracePeriodType().getId().toString(),
				loanPrdActionForm.getGracePeriodType());
		assertEquals(loanOffering.getGracePeriodDuration().toString(),
				loanPrdActionForm.getGracePeriodDuration());
		assertEquals(loanOffering.getInterestTypes().getId().toString(),
				loanPrdActionForm.getInterestTypes());
		assertEquals(loanOffering.getMaxLoanAmount().toString(),
				loanPrdActionForm.getMaxLoanAmount());
		assertEquals(loanOffering.getMinLoanAmount().toString(),
				loanPrdActionForm.getMinLoanAmount());
		assertEquals(loanOffering.getDefaultLoanAmount().toString(),
				loanPrdActionForm.getDefaultLoanAmount());
		assertEquals(loanOffering.getMaxInterestRate().toString(),
				loanPrdActionForm.getMaxInterestRate());
		assertEquals(loanOffering.getMinInterestRate().toString(),
				loanPrdActionForm.getMinInterestRate());
		assertEquals(loanOffering.getDefInterestRate().toString(),
				loanPrdActionForm.getDefInterestRate());
		assertEquals(loanOffering.getMaxNoInstallments().toString(),
				loanPrdActionForm.getMaxNoInstallments());
		assertEquals(loanOffering.getMinNoInstallments().toString(),
				loanPrdActionForm.getMinNoInstallments());
		assertEquals(loanOffering.getDefNoInstallments().toString(),
				loanPrdActionForm.getDefNoInstallments());
		assertEquals(loanOffering.isIntDedDisbursement(), loanPrdActionForm
				.isIntDedAtDisbValue());
		assertEquals(loanOffering.isPrinDueLastInst(), loanPrdActionForm
				.isPrinDueLastInstValue());
		assertEquals(loanOffering.isIncludeInLoanCounter(), loanPrdActionForm
				.isLoanCounterValue());
		assertEquals(loanOffering.getLoanOfferingMeeting().getMeeting()
				.getMeetingDetails().getRecurAfter().toString(),
				loanPrdActionForm.getRecurAfter());
		assertEquals(loanOffering.getLoanOfferingMeeting().getMeeting()
				.getMeetingDetails().getRecurrenceType().getRecurrenceId()
				.toString(), loanPrdActionForm.getFreqOfInstallments());
		assertEquals(
				loanOffering.getPrincipalGLcode().getGlcodeId().toString(),
				loanPrdActionForm.getPrincipalGLCode());
		assertEquals(loanOffering.getInterestGLcode().getGlcodeId().toString(),
				loanPrdActionForm.getInterestGLCode());

		assertEquals(loanOffering.getLoanOfferingFees().size(), (selectedFees)
				.size());
		assertEquals(loanOffering.getLoanOfferingFunds().size(),
				(selectedFunds).size());

	}

	public void testEditPreviewWithOutData() throws Exception {
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE,
				loanOffering.getStartDate(), request);
		actionPerform();
		verifyActionErrors(new String[] {
				"Please select the GL code for principal.",
				"Please select the Product category.",
				"Please specify the Applicable for.",
				"Please specify the Default # of installments.",
				"Please specify the Frequency of installments.",
				"Please specify the Max # of installments.",
				"Please specify the Max loan amount.",
				"Please specify the Min # of installments.",
				"Please specify the Min loan amount.",
				"Please specify the Product instance name.",
				"Please specify the Recur every.",
				"Please specify the Short name.",
				"Please specify the Start date.", "errors.mandatoryconfig",
				"errors.mandatoryconfig", "errors.mandatoryconfig",
				"errors.select", "errors.select", "errors.selectconfig" });
		verifyInputForward();
	}

	public void testEditPreviewWithoutStatus() throws Exception {
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE,
				loanOffering.getStartDate(), request);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { "errors.select" });
		verifyInputForward();
	}

	public void testEditPreview() throws Exception {
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE,
				loanOffering.getStartDate(), request);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("prdStatus", "2");

		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.editPreview_success.toString());
	}

	public void testEditPreviewForPricDueOnLastInstAndPrincGrace()
			throws Exception {
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE,
				loanOffering.getStartDate(), request);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("prdStatus", "2");
		addRequestParameter("gracePeriodType",
				GraceTypeConstants.PRINCIPALONLYGRACE.getValue().toString());
		addRequestParameter("gracePeriodDuration", "1");
		addRequestParameter("prinDueLastInstFlag", YesNoFlag.YES.getValue()
				.toString());
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "7");
		addRequestParameter("interestGLCode", "7");

		actionPerform();
		verifyActionErrors(new String[] { ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE });
		verifyInputForward();
	}

	public void testEditPrevious() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "editPrevious");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.editPrevious_success.toString());
	}

	public void testEditCancel() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "editCancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.editcancel_success.toString());
		assertNull(((FlowManager) request.getSession().getAttribute(
				Constants.FLOWMANAGER)).getFlow(flowKey));
	}

	public void testUpdate() throws Exception {
		FeeBO fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic",
				FeeCategory.LOAN, "100.0", RecurrenceType.MONTHLY, (short) 1);
		LoanOfferingBO loanOffering = createLoanOfferingBO("Loan Offering",
				"LOAN");
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "manage");
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "editPreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Product");
		addRequestParameter("prdOfferingShortName", "LOAP");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "1");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId()
				.toString() });
		addRequestParameter("loanOfferingFunds", new String[] { "1" });
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("prdStatus", "2");
		actionPerform();
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());

		HibernateUtil.closeSession();
		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());
		assertEquals("Loan Product", loanOffering.getPrdOfferingName());
		assertEquals("LOAP", loanOffering.getPrdOfferingShortName());
		assertEquals(Short.valueOf("2"), loanOffering.getPrdStatus()
				.getOfferingStatusId());
		assertEquals(new Money("11000"), loanOffering.getMaxLoanAmount());
		assertEquals(new Money("2000"), loanOffering.getMinLoanAmount());
		assertEquals(new Money("5000"), loanOffering.getDefaultLoanAmount());
		assertEquals(Short.valueOf("1"), loanOffering.getLoanOfferingMeeting()
				.getMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(Short.valueOf("2"), loanOffering.getLoanOfferingMeeting()
				.getMeeting().getMeetingDetails().getRecurrenceType()
				.getRecurrenceId());
		assertEquals(1, loanOffering.getLoanOfferingFees().size());

		assertNull(((FlowManager) request.getSession().getAttribute(
				Constants.FLOWMANAGER)).getFlow(flowKey));
		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.cleanUp(fee);
	}

	public void testGet() throws PageExpiredException {
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		HibernateUtil.closeSession();
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "get");
		addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.get_success.toString());

		LoanOfferingBO loanOffering1 = (LoanOfferingBO) SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request);
		assertNotNull(loanOffering1.getPrdOfferingId());
		assertNotNull(loanOffering1.getPrdOfferingName());
		assertNotNull(loanOffering1.getPrdOfferingShortName());
		assertNotNull(loanOffering1.getPrdCategory().getProductCategoryName());
		assertNotNull(loanOffering1.getPrdStatus().getPrdState().getName());
		assertNotNull(loanOffering1.getPrdApplicableMaster().getName());
		assertNotNull(loanOffering1.getStartDate());
		assertNotNull(loanOffering1.getGracePeriodType().getName());
		assertNotNull(loanOffering1.getGracePeriodDuration());
		assertNotNull(loanOffering1.getInterestTypes().getName());
		assertNotNull(loanOffering1.getMaxLoanAmount());
		assertNotNull(loanOffering1.getMinLoanAmount());
		assertNotNull(loanOffering1.getMaxInterestRate());
		assertNotNull(loanOffering1.getMinInterestRate());
		assertNotNull(loanOffering1.getDefInterestRate());
		assertNotNull(loanOffering1.getMaxNoInstallments());
		assertNotNull(loanOffering1.getMinNoInstallments());
		assertNotNull(loanOffering1.getDefNoInstallments());
		assertNotNull(loanOffering1.isIntDedDisbursement());
		assertNotNull(loanOffering1.isPrinDueLastInst());
		assertNotNull(loanOffering1.isIncludeInLoanCounter());
		assertNotNull(loanOffering1.getLoanOfferingMeeting().getMeeting()
				.getMeetingDetails().getRecurAfter());
		assertNotNull(loanOffering1.getLoanOfferingMeeting().getMeeting()
				.getMeetingDetails().getRecurrenceType().getRecurrenceId());
		assertNotNull(loanOffering1.getPrincipalGLcode().getGlcode());
		assertNotNull(loanOffering1.getInterestGLcode().getGlcode());
		assertNotNull(loanOffering1.getLoanOfferingFees());
		assertNotNull(loanOffering1.getLoanOfferingFunds());
		HibernateUtil.closeSession();
	}

	public void testViewAllLoanProducts() throws PageExpiredException {
		loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Offering1",
				"LOA1");
		HibernateUtil.closeSession();
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "viewAllLoanProducts");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.viewAllLoanProducts_success.toString());

		List<LoanOfferingBO> loanOfferings = (List<LoanOfferingBO>) SessionUtils
				.getAttribute(ProductDefinitionConstants.LOANPRODUCTLIST,
						request);
		assertNotNull(loanOfferings);
		assertEquals(2, loanOfferings.size());
		for (LoanOfferingBO loanOfferingBO : loanOfferings) {
			assertNotNull(loanOfferingBO.getPrdOfferingName());
			assertNotNull(loanOfferingBO.getPrdOfferingId());
			assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
		}
		HibernateUtil.closeSession();
		TestObjectFactory.removeObject(loanOffering1);

	}

	public void testCreateDecliningInterestDisbursementFail() throws Exception {
		fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic",
				FeeCategory.LOAN, "100.0", RecurrenceType.MONTHLY, (short) 1);
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "2");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId()
				.toString() });
		addRequestParameter("loanOfferingFunds", new String[] { "1" });
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		addRequestParameter("intDedDisbursementFlag", "1");
		addRequestParameter("principalGLCode", "35");
		addRequestParameter("interestGLCode", "45");
		actionPerform();
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { "exceptions.declineinterestdisbursementdeduction" });

	}

	public void testCreateDecliningInterestDisbursementSuccess()
			throws Exception {
		fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic",
				FeeCategory.LOAN, "100.0", RecurrenceType.MONTHLY, (short) 1);
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "load");
		actionPerform();

		flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		addRequestParameter("prdOfferingName", "Loan Offering");
		addRequestParameter("prdOfferingShortName", "LOAN");
		addRequestParameter("prdCategory", "1");
		addRequestParameter("startDate", offSetCurrentDate(0, userContext
				.getPereferedLocale()));
		addRequestParameter("endDate", offSetCurrentDate(1, userContext
				.getPereferedLocale()));
		addRequestParameter("prdApplicableMaster", "1");
		addRequestParameter("minLoanAmount", "2000");
		addRequestParameter("maxLoanAmount", "11000");
		addRequestParameter("defaultLoanAmount", "5000");
		addRequestParameter("interestTypes", "2");
		addRequestParameter("maxInterestRate", "12");
		addRequestParameter("minInterestRate", "1");
		addRequestParameter("defInterestRate", "4");
		addRequestParameter("freqOfInstallments", "2");
		addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId()
				.toString() });
		addRequestParameter("loanOfferingFunds", new String[] { "1" });
		addRequestParameter("recurAfter", "1");
		addRequestParameter("maxNoInstallments", "14");
		addRequestParameter("minNoInstallments", "2");
		addRequestParameter("defNoInstallments", "11");
		// addRequestParameter("intDedDisbursementFlag", "0");
		addRequestParameter("principalGLCode", "35");
		addRequestParameter("interestGLCode", "45");
		actionPerform();
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "create");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());

		assertNotNull(request
				.getAttribute(ProductDefinitionConstants.LOANPRODUCTID));
		assertNotNull(request
				.getAttribute(ProductDefinitionConstants.LOANPRDGLOBALOFFERINGNUM));
		assertNull(((FlowManager) request.getSession().getAttribute(
				Constants.FLOWMANAGER)).getFlow(flowKey));

		TestObjectFactory
				.removeObject((LoanOfferingBO) TestObjectFactory
						.getObject(
								LoanOfferingBO.class,
								(Short) request
										.getAttribute(ProductDefinitionConstants.LOANPRODUCTID)));
	}

	private String offSetCurrentDate(int noOfDays, Locale locale) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar
				.getTimeInMillis());
		SimpleDateFormat format = (SimpleDateFormat) DateFormat
				.getDateInstance(DateFormat.SHORT, locale);
		String userfmt = DateHelper.convertToCurrentDateFormat(format
				.toPattern());
		return DateHelper.convertDbToUserFmt(currentDate.toString(), userfmt);
	}

	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 1, 2));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("0"), Short.valueOf("1"), frequency);
	}
}
