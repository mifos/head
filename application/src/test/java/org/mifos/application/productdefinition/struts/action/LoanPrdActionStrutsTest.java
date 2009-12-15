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

package org.mifos.application.productdefinition.struts.action;

import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanAmountSameForAllLoanBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.NoOfInstallSameForAllLoanBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdActionStrutsTest extends MifosMockStrutsTestCase {

    public LoanPrdActionStrutsTest() throws SystemException, ApplicationException {
        super();
    }

    private LoanOfferingBO loanOffering;

    private String flowKey;

    UserContext userContext = null;

    private FeeBO fee;

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(loanOffering);
        TestObjectFactory.cleanUp(fee);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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

        List<ProductCategoryBO> productCategories = (List<ProductCategoryBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST, request);
       Assert.assertEquals("The size of master data for categories", 1, productCategories.size());
        for (ProductCategoryBO productCategory : productCategories) {
            Assert.assertNotNull(productCategory.getProductType());
        }
       Assert.assertEquals("The size of applicable list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANAPPLFORLIST, request)).size());

       Assert.assertEquals("The size of grace period types list", 3, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANGRACEPERIODTYPELIST, request)).size());
       Assert.assertEquals("The size of interest types list", 3, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.INTERESTTYPESLIST, request)).size());
       Assert.assertEquals("The size of applicable list", 10, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRICIPALGLCODELIST, request)).size());
       Assert.assertEquals("The size of applicable list", 3, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANINTERESTGLCODELIST, request)).size());
        List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(ProductDefinitionConstants.SRCFUNDSLIST, request);
        Assert.assertNotNull(funds);
        List<FeeView> loanFees = (List<FeeView>) SessionUtils.getAttribute(ProductDefinitionConstants.LOANFEESLIST,
                request);
        Assert.assertNull(loanFees);
        List<FeeBO> productFees = (List<FeeBO>) SessionUtils.getAttribute(ProductDefinitionConstants.LOANPRDFEE,
                request);
        Assert.assertNotNull(productFees);
        List<FeeView> selectedFees = (List<FeeView>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRDFEESELECTEDLIST, request);
        Assert.assertNotNull(selectedFees);
        List<FundBO> selectedFunds = (List<FundBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST, request);
        Assert.assertNotNull(selectedFunds);
       Assert.assertEquals(0, (selectedFees).size());
       Assert.assertEquals(0, (selectedFunds).size());
    }

    public void testPreviewWithOutData() throws Exception {
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        actionPerform();
        verifyActionErrors(new String[] { "Please select the GL code for principal.",
                "Please select the Product category.", "Please specify the Applicable for.",
                "Please specify the Frequency of installments.", "Please specify the Product instance name.",
                "Please specify the Recur every.", "Please specify the Short name.", "Please specify the Start date.",
                "errors.mandatoryconfig", "errors.mandatoryconfig", "errors.mandatoryconfig", "errors.select",
                "errors.selectconfig", "errors.calcloanamounttype", "errors.calcinstallmenttype" });
        verifyInputForward();
    }

    public void testPreviewWithImproperMinMaxDefAmount() throws Exception {
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
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

        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");

        actionPerform();
        verifyActionErrors(new String[] { "errors.maxminLoanAmount", "errors.defLoanAmount" });
        verifyInputForward();
    }

    public void testPreviewWithImproperMinMaxDefInterestRates() throws Exception {
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "3000");
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        verifyActionErrors(new String[] { "errors.defIntRateconfig", "errors.maxminIntRateconfig" });
        verifyInputForward();
    }

    public void testPreviewWithImproperMinMaxInterestRates() throws Exception {
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "3000");
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        verifyActionErrors(new String[] { "errors.defIntRateconfig", "errors.maxminIntRateconfig" });
        verifyInputForward();
    }

    public void testPreviewWithImproperMinMaxDefInstallments() throws Exception {
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        verifyActionErrors(new String[] { "errors.maxminnoofinstall", "errors.defaultinstallments" });
        verifyInputForward();
    }

    public void testPreviewWithGraceTypeNotNoneAndNoGraceDuration() throws Exception {
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        addRequestParameter("startDate", offSetCurrentDate(-1, userContext.getPreferredLocale()));
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(-1, userContext.getPreferredLocale()));
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        verifyActionErrors(new String[] { ProductDefinitionConstants.INVALIDENDDATE });
        verifyInputForward();
    }

    public void testPreviewWithFeeNotMatchingFeeFrequency() throws Exception {
        FeeBO fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100.0",
                RecurrenceType.MONTHLY, (short) 1);
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "1");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "1");
        addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId().toString() });
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("currencyId", Money.getDefaultCurrency().getCurrencyId().toString());
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "1");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "2");
        addRequestParameter("gracePeriodType", GraceType.PRINCIPALONLYGRACE.getValue().toString());
        addRequestParameter("gracePeriodDuration", "1");
        addRequestParameter("prinDueLastInstFlag", YesNoFlag.YES.getValue().toString());
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        addRequestParameter("intDedDisbursementFlag", "1");
        addRequestParameter("principalGLCode", "7");
        addRequestParameter("interestGLCode", "7");
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
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
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
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
        request.setAttribute(ProductDefinitionConstants.METHODCALLED, Methods.preview.toString());

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.preview_failure.toString());
    }

    public void testVaildateForCreate() throws Exception {
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(ProductDefinitionConstants.METHODCALLED, Methods.create.toString());

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_failure.toString());
    }

    public void testCreate() throws Exception {
        FeeBO fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100.0",
                RecurrenceType.MONTHLY, (short) 1);
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("currencyId", Money.getDefaultCurrency().getCurrencyId().toString());
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "1");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "2");
        addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId().toString() });
        addRequestParameter("loanOfferingFunds", new String[] { "1" });
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        addRequestParameter("intDedDisbursementFlag", "1");
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.MANAGED_ICICI_SPLOAN.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());

        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.LOANPRODUCTID));
        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.LOANPRDGLOBALOFFERINGNUM));
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
        TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class,
                (Short) request.getAttribute(ProductDefinitionConstants.LOANPRODUCTID)));
        TestObjectFactory.cleanUp(fee);
    }

    public void testManage() throws Exception {
        loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        performNoErrors();
        verifyForward(ActionForwards.manage_success.toString());

        List<ProductCategoryBO> productCategories = (List<ProductCategoryBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST, request);
       Assert.assertEquals("The size of master data for categories", 1, productCategories.size());
        for (ProductCategoryBO productCategory : productCategories) {
            Assert.assertNotNull(productCategory.getProductType());
        }
       Assert.assertEquals("The size of applicable list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANAPPLFORLIST, request)).size());

       Assert.assertEquals("The size of grace period types list", 3, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANGRACEPERIODTYPELIST, request)).size());
       Assert.assertEquals("The size of interest types list", 3, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.INTERESTTYPESLIST, request)).size());
       Assert.assertEquals("The size of applicable list", 10, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRICIPALGLCODELIST, request)).size());
       Assert.assertEquals("The size of applicable list", 3, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANINTERESTGLCODELIST, request)).size());
        List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(ProductDefinitionConstants.SRCFUNDSLIST, request);
        Assert.assertNotNull(funds);
        List<FeeView> fees = (List<FeeView>) SessionUtils
                .getAttribute(ProductDefinitionConstants.LOANFEESLIST, request);
        Assert.assertNull(fees);
        List<FeeBO> productFees = (List<FeeBO>) SessionUtils.getAttribute(ProductDefinitionConstants.LOANPRDFEE,
                request);
        Assert.assertNotNull(productFees);
        List<FeeView> selectedFees = (List<FeeView>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRDFEESELECTEDLIST, request);
        Assert.assertNotNull(selectedFees);
        List<FundBO> selectedFunds = (List<FundBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST, request);
        Assert.assertNotNull(selectedFunds);
       Assert.assertEquals("The size of applicable status list", 2, ((List<PrdStatusEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRDSTATUSLIST, request)).size());
        LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) request.getSession().getAttribute(
                ProductDefinitionConstants.LOANPRODUCTACTIONFORM);
        Assert.assertNotNull(loanPrdActionForm);

       Assert.assertEquals(loanOffering.getPrdOfferingId().toString(), loanPrdActionForm.getPrdOfferingId());
       Assert.assertEquals(loanOffering.getPrdOfferingName(), loanPrdActionForm.getPrdOfferingName());
       Assert.assertEquals(loanOffering.getPrdOfferingShortName(), loanPrdActionForm.getPrdOfferingShortName());
       Assert.assertEquals(loanOffering.getPrdCategory().getProductCategoryID().toString(), loanPrdActionForm
                .getPrdCategory());
       Assert.assertEquals(loanOffering.getStatus().getValue().toString(), loanPrdActionForm.getPrdStatus());
       Assert.assertEquals(loanOffering.getPrdApplicableMasterEnum(), loanPrdActionForm.getPrdApplicableMasterEnum());
       Assert.assertEquals(DateUtils.getUserLocaleDate(TestObjectFactory.getContext().getPreferredLocale(), DateUtils
                .toDatabaseFormat(loanOffering.getStartDate())), loanPrdActionForm.getStartDate());
        if (loanOffering.getEndDate() != null) {
           Assert.assertEquals(DateUtils.getUserLocaleDate(TestObjectFactory.getContext().getPreferredLocale(), DateUtils
                    .toDatabaseFormat(loanOffering.getEndDate())), loanPrdActionForm.getEndDate());
        } else {
            Assert.assertNull(loanPrdActionForm.getEndDate());
        }
       Assert.assertEquals(loanOffering.getDescription(), loanPrdActionForm.getDescription());
       Assert.assertEquals(loanOffering.getGracePeriodType().getId().toString(), loanPrdActionForm.getGracePeriodType());
       Assert.assertEquals(loanOffering.getGracePeriodDuration().toString(), loanPrdActionForm.getGracePeriodDuration());
       Assert.assertEquals(loanOffering.getInterestTypes().getId().toString(), loanPrdActionForm.getInterestTypes());
        Set<LoanAmountSameForAllLoanBO> loanAmountSameForAllLoan = loanOffering.getLoanAmountSameForAllLoan();
        Assert.assertNotNull(loanAmountSameForAllLoan);
        Set<NoOfInstallSameForAllLoanBO> noOfInstallSameForAllLoan = loanOffering.getNoOfInstallSameForAllLoan();
        Assert.assertNotNull(noOfInstallSameForAllLoan);
       Assert.assertEquals(loanOffering.getMaxInterestRate().toString(), loanPrdActionForm.getMaxInterestRate());
       Assert.assertEquals(loanOffering.getMinInterestRate().toString(), loanPrdActionForm.getMinInterestRate());
       Assert.assertEquals(loanOffering.getDefInterestRate().toString(), loanPrdActionForm.getDefInterestRate());

       Assert.assertEquals(loanOffering.isIntDedDisbursement(), loanPrdActionForm.isIntDedAtDisbValue());
       Assert.assertEquals(loanOffering.isPrinDueLastInst(), loanPrdActionForm.isPrinDueLastInstValue());
       Assert.assertEquals(loanOffering.isIncludeInLoanCounter(), loanPrdActionForm.isLoanCounterValue());
       Assert.assertEquals(loanOffering.getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurAfter().toString(),
                loanPrdActionForm.getRecurAfter());
       Assert.assertEquals(loanOffering.getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurrenceType()
                .getRecurrenceId().toString(), loanPrdActionForm.getFreqOfInstallments());
       Assert.assertEquals(loanOffering.getPrincipalGLcode().getGlcodeId().toString(), loanPrdActionForm.getPrincipalGLCode());
       Assert.assertEquals(loanOffering.getInterestGLcode().getGlcodeId().toString(), loanPrdActionForm.getInterestGLCode());

       Assert.assertEquals(loanOffering.getLoanOfferingFees().size(), (selectedFees).size());
       Assert.assertEquals(loanOffering.getLoanOfferingFunds().size(), (selectedFunds).size());
    }

    public void testEditPreviewWithOutData() throws Exception {
        loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE, loanOffering.getStartDate(), request);
        actionPerform();
        verifyActionErrors(new String[] { "Please select the GL code for principal.",
                "Please select the Product category.", "Please specify the Applicable for.",
                "Please specify the Frequency of installments.", "Please specify the Product instance name.",
                "Please specify the Recur every.", "Please specify the Short name.", "Please specify the Start date.",
                "errors.mandatoryconfig", "errors.mandatoryconfig", "errors.mandatoryconfig", "errors.select",
                "errors.select", "errors.selectconfig", "errors.calcloanamounttype", "errors.calcinstallmenttype" });
        verifyInputForward();
    }

    public void testEditPreviewWithoutStatus() throws Exception {
        loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE, loanOffering.getStartDate(), request);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
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
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.BANK_ACCOUNT_ONE.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.BANK_ACCOUNT_ONE.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE, loanOffering.getStartDate(), request);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
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
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.BANK_ACCOUNT_ONE.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.BANK_ACCOUNT_ONE.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.editPreview_success.toString());
    }

    public void testEditPreviewForPricDueOnLastInstAndPrincGrace() throws Exception {
        loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE, loanOffering.getStartDate(), request);

        addRequestParameter("prdOfferingName", "Loan Offering");
        addRequestParameter("prdOfferingShortName", "LOAN");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
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
        addRequestParameter("gracePeriodType", GraceType.PRINCIPALONLYGRACE.getValue().toString());
        addRequestParameter("gracePeriodDuration", "1");
        addRequestParameter("prinDueLastInstFlag", YesNoFlag.YES.getValue().toString());
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        addRequestParameter("intDedDisbursementFlag", "1");
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.BANK_ACCOUNT_ONE.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.BANK_ACCOUNT_ONE.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
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
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
    }

    public void testUpdate() throws Exception {
        FeeBO fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100.0",
                RecurrenceType.MONTHLY, (short) 1);
        LoanOfferingBO product = createLoanOfferingBO("Loan Offering", "LOAN");
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter("prdOfferingId", product.getPrdOfferingId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Loan Product");
        addRequestParameter("prdOfferingShortName", "LOAP");
        addRequestParameter("prdCategory", "1");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "1");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "2");
        addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId().toString() });
        addRequestParameter("loanOfferingFunds", new String[] { "1" });
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        addRequestParameter("intDedDisbursementFlag", "1");
        addRequestParameter("prdStatus", "2");
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_success.toString());

        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
       Assert.assertEquals("Loan Product", product.getPrdOfferingName());
       Assert.assertEquals("LOAP", product.getPrdOfferingShortName());
       Assert.assertEquals(PrdStatus.SAVINGS_ACTIVE, product.getStatus());
        for (Iterator<LoanAmountSameForAllLoanBO> itr = product.getLoanAmountSameForAllLoan().iterator(); itr.hasNext();) {
            LoanAmountSameForAllLoanBO loanAmountSameForAllLoanBO = itr.next();
           Assert.assertEquals(new Double("11000"), loanAmountSameForAllLoanBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("2000"), loanAmountSameForAllLoanBO.getMinLoanAmount());
           Assert.assertEquals(new Double("5000"), loanAmountSameForAllLoanBO.getDefaultLoanAmount());
        }
       Assert.assertEquals(Short.valueOf("1"), product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurAfter());
       Assert.assertEquals(Short.valueOf("2"), product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceType().getRecurrenceId());
       Assert.assertEquals(1, product.getLoanOfferingFees().size());

        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));
        TestObjectFactory.removeObject(product);
        TestObjectFactory.cleanUp(fee);
    }

    public void testGet() throws PageExpiredException {
        loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "get");
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.get_success.toString());

        LoanOfferingBO loanOffering1 = (LoanOfferingBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        loanOffering1 = (LoanOfferingBO) StaticHibernateUtil.getSessionTL().get(LoanOfferingBO.class,
                loanOffering1.getPrdOfferingId());

        // TODO: the need to pass a locale id in to the getName methods below
        // points out something that may be a bug which will show up elsewhere.
        // In these cases, an object is loaded into the http session which is
        // associated with a given Hibernate session. When it is loaded, somehow
        // it seems to have the locale set (in MasterDataEntity). However the
        // locale is not persisted. When the object is accessed after the
        // Hibernate
        // session has been closed, then it needs to be reloaded or reattached
        // to the session. But in reloaded, the locale that has been set is
        // lost.

        Assert.assertNotNull(loanOffering1.getPrdOfferingId());
        Assert.assertNotNull(loanOffering1.getPrdOfferingName());
        Assert.assertNotNull(loanOffering1.getPrdOfferingShortName());
        Assert.assertNotNull(loanOffering1.getPrdCategory().getProductCategoryName());
        Assert.assertNotNull(loanOffering1.getPrdStatus().getPrdState().getName());
        Assert.assertNotNull(loanOffering1.getPrdApplicableMasterEnum());
        Assert.assertNotNull(loanOffering1.getStartDate());
        Assert.assertNotNull(loanOffering1.getGracePeriodType().getName());
        Assert.assertNotNull(loanOffering1.getGracePeriodDuration());
        Assert.assertNotNull(loanOffering1.getInterestTypes().getName());
        for (Iterator<LoanAmountSameForAllLoanBO> itr = loanOffering1.getLoanAmountSameForAllLoan().iterator(); itr
                .hasNext();) {
            LoanAmountSameForAllLoanBO loanAmountSameForAllLoanBO = itr.next();
            Assert.assertNotNull(loanAmountSameForAllLoanBO.getMaxLoanAmount());
            Assert.assertNotNull(loanAmountSameForAllLoanBO.getMinLoanAmount());
            Assert.assertNotNull(loanAmountSameForAllLoanBO.getDefaultLoanAmount());
        }
        Assert.assertNotNull(loanOffering1.getMaxInterestRate());
        Assert.assertNotNull(loanOffering1.getMinInterestRate());
        Assert.assertNotNull(loanOffering1.getDefInterestRate());
        for (Iterator<NoOfInstallSameForAllLoanBO> itr = loanOffering1.getNoOfInstallSameForAllLoan().iterator(); itr
                .hasNext();) {
            NoOfInstallSameForAllLoanBO noOfInstallSameForAllLoanBO = itr.next();
            Assert.assertNotNull(noOfInstallSameForAllLoanBO.getMaxNoOfInstall());
            Assert.assertNotNull(noOfInstallSameForAllLoanBO.getMinNoOfInstall());
            Assert.assertNotNull(noOfInstallSameForAllLoanBO.getDefaultNoOfInstall());
        }
        Assert.assertNotNull(loanOffering1.isIntDedDisbursement());
        Assert.assertNotNull(loanOffering1.isPrinDueLastInst());
        Assert.assertNotNull(loanOffering1.isIncludeInLoanCounter());
        Assert.assertNotNull(loanOffering1.getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurAfter());
        Assert.assertNotNull(loanOffering1.getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurrenceType()
                .getRecurrenceId());
        Assert.assertNotNull(loanOffering1.getPrincipalGLcode().getGlcode());
        Assert.assertNotNull(loanOffering1.getInterestGLcode().getGlcode());
        Assert.assertNotNull(loanOffering1.getLoanOfferingFees());
        Assert.assertNotNull(loanOffering1.getLoanOfferingFunds());
        StaticHibernateUtil.closeSession();
    }

    public void testViewAllLoanProducts() throws PageExpiredException {
        loanOffering = createLoanOfferingBO("Loan Offering", "LOAN");
        LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Offering1", "LOA1");
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "viewAllLoanProducts");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.viewAllLoanProducts_success.toString());

        List<LoanOfferingBO> loanOfferings = (List<LoanOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.LOANPRODUCTLIST, request);
        Assert.assertNotNull(loanOfferings);
       Assert.assertEquals(2, loanOfferings.size());
        Short DEFAULT_LOCALE_ID = (short) 1;
        for (LoanOfferingBO loanOfferingBO : loanOfferings) {
            loanOfferingBO = (LoanOfferingBO) StaticHibernateUtil.getSessionTL().get(LoanOfferingBO.class,
                    loanOfferingBO.getPrdOfferingId());
            Assert.assertNotNull(loanOfferingBO.getPrdOfferingName());
            Assert.assertNotNull(loanOfferingBO.getPrdOfferingId());
            Assert.assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
        }
        StaticHibernateUtil.closeSession();
        TestObjectFactory.removeObject(loanOffering1);

    }

    public void testCreateDecliningInterestDisbursementFail() throws Exception {
        fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100.0",
                RecurrenceType.MONTHLY, (short) 1);
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "2");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "2");
        addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId().toString() });
        addRequestParameter("loanOfferingFunds", new String[] { "1" });
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        addRequestParameter("intDedDisbursementFlag", "1");
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.MANAGED_ICICI_SPLOAN.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "exceptions.declineinterestdisbursementdeduction" });

    }

    public void testCreateDecliningInterestDisbursementSuccess() throws Exception {
        fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100.0",
                RecurrenceType.MONTHLY, (short) 1);
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("currencyId", Money.getDefaultCurrency().getCurrencyId().toString());
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "2");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "2");
        addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId().toString() });
        addRequestParameter("loanOfferingFunds", new String[] { "1" });
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        // addRequestParameter("intDedDisbursementFlag", "0");
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.MANAGED_ICICI_SPLOAN.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());

        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.LOANPRODUCTID));
        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.LOANPRDGLOBALOFFERINGNUM));
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));

        TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class,
                (Short) request.getAttribute(ProductDefinitionConstants.LOANPRODUCTID)));
    }

    public void testCreateDecliningInterestEqualPrincipalDisbursementFail() throws Exception {
        fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100.0",
                RecurrenceType.MONTHLY, (short) 1);
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "4");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "2");
        addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId().toString() });
        addRequestParameter("loanOfferingFunds", new String[] { "1" });
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        addRequestParameter("intDedDisbursementFlag", "1");
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.MANAGED_ICICI_SPLOAN.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { "exceptions.declineinterestdisbursementdeduction" });

    }

    public void testCreateDecliningInterestEqualPrincipalDisbursementSuccess() throws Exception {
        fee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100.0",
                RecurrenceType.MONTHLY, (short) 1);
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
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("currencyId", Money.getDefaultCurrency().getCurrencyId().toString());
        addRequestParameter("minLoanAmount", "2000");
        addRequestParameter("maxLoanAmount", "11000");
        addRequestParameter("defaultLoanAmount", "5000");
        addRequestParameter("interestTypes", "4");
        addRequestParameter("maxInterestRate", "12");
        addRequestParameter("minInterestRate", "1");
        addRequestParameter("defInterestRate", "4");
        addRequestParameter("freqOfInstallments", "2");
        addRequestParameter("prdOfferinFees", new String[] { fee.getFeeId().toString() });
        addRequestParameter("loanOfferingFunds", new String[] { "1" });
        addRequestParameter("recurAfter", "1");
        addRequestParameter("maxNoInstallments", "14");
        addRequestParameter("minNoInstallments", "2");
        addRequestParameter("defNoInstallments", "11");
        // addRequestParameter("intDedDisbursementFlag", "0");
        addRequestParameter("principalGLCode", TestGeneralLedgerCode.MANAGED_ICICI_SPLOAN.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST.toString());
        addRequestParameter("loanAmtCalcType", "1");
        addRequestParameter("calcInstallmentType", "1");
        actionPerform();
        setRequestPathInfo("/loanproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());

        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.LOANPRODUCTID));
        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.LOANPRDGLOBALOFFERINGNUM));
        Assert.assertNull(((FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER)).getFlow(flowKey));

        TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class,
                (Short) request.getAttribute(ProductDefinitionConstants.LOANPRODUCTID)));
    }

    private String offSetCurrentDate(int noOfDays, Locale locale) throws InvalidDateException {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
        java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());
        SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String userfmt = DateUtils.convertToCurrentDateFormat(format.toPattern());
        return DateUtils.convertDbToUserFmt(currentDate.toString(), userfmt);
    }

    private LoanOfferingBO createLoanOfferingBO(String prdOfferingName, String shortName) {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(WEEKLY, EVERY_WEEK,
                LOAN_INSTALLMENT, MONDAY));
        return TestObjectFactory.createLoanOffering(prdOfferingName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency, "1", "1");
    }
}
