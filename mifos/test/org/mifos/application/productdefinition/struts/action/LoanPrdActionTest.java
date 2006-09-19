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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdActionTest extends MifosMockStrutsTestCase {

	private LoanOfferingBO loanOffering;

	private String flowKey;

	UserContext userContext = null;

	@Override
	protected void tearDown() throws Exception {
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
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
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
		assertEquals("The size of interest types list", 1,
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
		assertNotNull(((List<Fund>) SessionUtils.getAttribute(
				ProductDefinitionConstants.SRCFUNDSLIST, request)).size());
		assertNotNull(((List<FeeBO>) SessionUtils.getAttribute(
				ProductDefinitionConstants.LOANFEESLIST, request)).size());
	}

	public void testPreviewWithOutData() throws Exception {
		setRequestPathInfo("/loanproductaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

		actionPerform();
		verifyActionErrors(new String[] {
				"Please select the GL code for interest.",
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
				"errors.selectconfig" });
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
		verifyActionErrors(new String[] {
				"Please specify a valid Max loan amount. Max loan amount should be greater than or equal to Min loan amount.",
				"Please specify valid values for Default amount. Default amount should be a value between Min loan amount and Max loan amount, inclusive." });
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
		verifyForward(ActionForwards.preview_success.toString());
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
		TestObjectFactory
				.removeObject((LoanOfferingBO) TestObjectFactory
						.getObject(
								LoanOfferingBO.class,
								(Short) request
										.getAttribute(ProductDefinitionConstants.LOANPRODUCTID)));
		TestObjectFactory.cleanUp(fee);
	}

	private String offSetCurrentDate(int noOfDays, Locale locale) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar
				.getTimeInMillis());
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(
				DateFormat.SHORT, locale);
		String userfmt = DateHelper
				.convertToCurrentDateFormat(((SimpleDateFormat) sdf)
						.toPattern());
		return DateHelper.convertDbToUserFmt(currentDate.toString(), userfmt);
	}
}
