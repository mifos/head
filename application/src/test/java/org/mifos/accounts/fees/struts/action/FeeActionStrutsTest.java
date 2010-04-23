/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.fees.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.accounts.fees.struts.actionforms.FeeActionForm;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;

public class FeeActionStrutsTest extends MifosMockStrutsTestCase {

    public FeeActionStrutsTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;

    private final static String GLOCDE_ID = "47";

    private FeeBO fee;

    private FeeBO fee1;

    private FeeBO fee2;

    private FeeBO fee3;

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, FeeAction.class);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(fee);
            TestObjectFactory.cleanUp(fee1);
            TestObjectFactory.cleanUp(fee2);
            TestObjectFactory.cleanUp(fee3);
            StaticHibernateUtil.closeSession();
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        super.tearDown();
    }

    public void testLoad() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "load");

        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        FeeParameters feeParameters = (FeeParameters)request.getAttribute(FeeParameters.class.getSimpleName());
        Assert.assertEquals("The size of master data for categories", feeParameters.getCategories().size(), 5);
        Assert.assertEquals("The size of master data for loan time of charges for one time fees  : ",
                feeParameters.getTimesOfCharging().size(), 3);
        Assert.assertEquals("The size of master data for customer  time of charges for one time fees master : ",
                feeParameters.getTimesOfChargingCustomers().size(), 1);
        Assert.assertEquals("The size of master data for loan formula : ", feeParameters.getFormulas().size(), 3);
        Assert.assertEquals("The size of master data for GLCodes of fees : ", feeParameters.getGlCodes().size(), 7);
    }

    public void testFailurePreviewWithAllValuesNull() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals(5, getErrorSize());
        Assert.assertEquals("Fee Name", 1, getErrorSize("feeName"));
        Assert.assertEquals("Fee Applies to Product/Customer", 1, getErrorSize("categoryType"));
        Assert.assertEquals("Periodic or OneTime Fee", 1, getErrorSize("feeFrequencyType"));
        Assert.assertEquals("Fee Amount", 1, getErrorSize(FeeConstants.AMOUNT));
        Assert.assertEquals("Fee GlCode", 1, getErrorSize(FeeConstants.INVALID_GLCODE));
        verifyInputForward();
    }

    public void testFailurePreviewWithFeeNameNotNull() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "CustomerFee");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        Assert.assertEquals(4, getErrorSize());
        Assert.assertEquals("Fee Name", 0, getErrorSize("feeName"));
        Assert.assertEquals("Fee Applies to Product/Customer", 1, getErrorSize("categoryType"));
        Assert.assertEquals("Periodic or OneTime Fee", 1, getErrorSize("feeFrequencyType"));
        Assert.assertEquals("Fee Amount", 1, getErrorSize(FeeConstants.AMOUNT));
        Assert.assertEquals("Fee GlCode", 1, getErrorSize(FeeConstants.INVALID_GLCODE));
        verifyInputForward();
    }

    public void testFailurePreviewWithFeeCategoryNotNull() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "CustomerFee");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
        actionPerform();

        Assert.assertEquals(3, getErrorSize());
        Assert.assertEquals("Periodic or OneTime Fee", 1, getErrorSize("feeFrequencyType"));
        Assert.assertEquals("Fee Amount", 1, getErrorSize(FeeConstants.AMOUNT));
        Assert.assertEquals("Fee GlCode", 1, getErrorSize(FeeConstants.INVALID_GLCODE));
        verifyInputForward();
    }

    public void testFailurePreviewWith_FeeFrequencyOneTime() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "CustomerFee");
        addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
        addRequestParameter("feeFrequencyType", FeeFrequencyType.ONETIME.getValue().toString());
        addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        Assert.assertEquals(2, getErrorSize());
        Assert.assertEquals("Fee Amount", 1, getErrorSize(FeeConstants.AMOUNT));
        Assert.assertEquals("Fee GlCode", 1, getErrorSize(FeeConstants.INVALID_GLCODE));
        verifyInputForward();
    }

    public void testFailurePreviewWith_FeeFrequencyPeriodic() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "CustomerFee");
        addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
        addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
        addRequestParameter("feeRecurrenceType", RecurrenceType.MONTHLY.getValue().toString());
        addRequestParameter("monthRecurAfter", "2");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        Assert.assertEquals(2, getErrorSize());
        Assert.assertEquals("Fee Amount", 1, getErrorSize(FeeConstants.AMOUNT));
        Assert.assertEquals("Fee GlCode", 1, getErrorSize(FeeConstants.INVALID_GLCODE));
        verifyInputForward();
    }

    public void testFailurePreviewWith_RateEnteredWithoutFormula() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "CustomerFee");
        addRequestParameter("categoryType", FeeCategory.LOAN.getValue().toString());
        addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
        addRequestParameter("feeRecurrenceType", RecurrenceType.WEEKLY.getValue().toString());
        addRequestParameter("weekRecurAfter", "2");
        addRequestParameter("rate", "10");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        Assert.assertEquals(2, getErrorSize());
        Assert.assertEquals("Fee GlCode", 1, getErrorSize(FeeConstants.INVALID_GLCODE));
        Assert.assertEquals("Fee Rate or Formula", 1, getErrorSize("RateAndFormula"));
        verifyInputForward();
    }

    public void testFailurePreviewWith_RateAndAmountEnteredWithoutFormula() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "LoanFee");
        addRequestParameter("categoryType", FeeCategory.LOAN.getValue().toString());
        addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
        addRequestParameter("feeRecurrenceType", RecurrenceType.WEEKLY.getValue().toString());
        addRequestParameter("weekRecurAfter", "2");
        addRequestParameter("amount", "200");
        addRequestParameter("rate", "10");
        addRequestParameter("glCode", GLOCDE_ID);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        Assert.assertEquals(1, getErrorSize());
        Assert.assertEquals("Fee Rate or Formula", 1, getErrorSize(FeeConstants.RATE_OR_AMOUNT));
        verifyInputForward();
    }

    public void testFailurePreviewWith_AmountNotNull() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "CustomerFee");
        addRequestParameter("categoryType", FeeCategory.CENTER.getValue().toString());
        addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
        addRequestParameter("feeRecurrenceType", RecurrenceType.MONTHLY.getValue().toString());
        addRequestParameter("monthRecurAfter", "2");
        addRequestParameter("amount", "200");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        Assert.assertEquals(1, getErrorSize());
        Assert.assertEquals("Fee GlCode", 1, getErrorSize(FeeConstants.INVALID_GLCODE));
        verifyInputForward();
    }

    public void testSuccessfulPreview() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("feeName", "CustomerFee");
        addRequestParameter("categoryType", FeeCategory.LOAN.getValue().toString());
        addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
        addRequestParameter("feeRecurrenceType", RecurrenceType.WEEKLY.getValue().toString());
        addRequestParameter("weekRecurAfter", "2");
        addRequestParameter("currencyId", TestUtils.RUPEE.getCurrencyId().toString());
        addRequestParameter("rate", "10");
        addRequestParameter("feeFormula", FeeFormula.AMOUNT.getValue().toString());
        addRequestParameter("glCode", GLOCDE_ID);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals(0, getErrorSize());
        verifyForward(ActionForwards.preview_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testSuccessfulCreateOneTimeFee() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "load");
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("categoryType", FeeCategory.ALLCUSTOMERS.getValue().toString());
        addRequestParameter("currencyId", TestUtils.RUPEE.getCurrencyId().toString());
        addRequestParameter("amount", "100");
        addRequestParameter("feeName", "Customer_One_time");
        addRequestParameter("feeFrequencyType", FeeFrequencyType.ONETIME.getValue().toString());
        addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue().toString());
        addRequestParameter("glCode", GLOCDE_ID);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());

        FeeActionForm actionForm = (FeeActionForm) request.getSession().getAttribute("feeactionform");
        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());
        Assert.assertEquals("Customer_One_time", fee.getFeeName());
        Assert.assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType().getId());
        Assert.assertEquals(RateAmountFlag.AMOUNT, fee.getFeeType());
        Assert.assertEquals(new Money(getCurrency(), "100.0"), ((AmountFeeBO) fee).getFeeAmount());
        Assert.assertTrue(fee.isOneTime());
        Assert.assertFalse(fee.isCustomerDefaultFee());
        Assert.assertTrue(fee.isActive());
    }

    public void testSuccessfulCreateOneTimeAdminFee() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("categoryType", FeeCategory.ALLCUSTOMERS.getValue().toString());
        addRequestParameter("currencyId", TestUtils.RUPEE.getCurrencyId().toString());
        addRequestParameter("amount", "100");
        addRequestParameter("customerDefaultFee", "1");
        addRequestParameter("feeName", "Customer_One_time_Default_Fee");
        addRequestParameter("feeFrequencyType", FeeFrequencyType.ONETIME.getValue().toString());
        addRequestParameter("customerCharge", FeePayment.UPFRONT.getValue().toString());
        addRequestParameter("glCode", GLOCDE_ID);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "create");
        addRequestParameter("org.apache.struts.taglib.html.TOKEN", (String) request.getSession().getAttribute(
                "org.apache.struts.action.TOKEN"));
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());

        FeeActionForm actionForm = (FeeActionForm) request.getSession().getAttribute("feeactionform");
        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());
        Assert.assertEquals("Customer_One_time_Default_Fee", fee.getFeeName());
        Assert.assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType().getId());
        Assert.assertEquals(RateAmountFlag.AMOUNT, fee.getFeeType());
        Assert.assertEquals(new Money(getCurrency(), "100.0"), ((AmountFeeBO) fee).getFeeAmount());
        Assert.assertTrue(fee.isOneTime());
        Assert.assertTrue(fee.isCustomerDefaultFee());
        Assert.assertTrue(fee.isActive());
    }

    public void testSuccessfulCreatePeriodicFee() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("categoryType", FeeCategory.ALLCUSTOMERS.getValue().toString());
        addRequestParameter("currencyId", TestUtils.RUPEE.getCurrencyId().toString());
        addRequestParameter("amount", "100");
        addRequestParameter("customerDefaultFee", "1");
        addRequestParameter("feeName", "Customer Periodic Fee");
        addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
        addRequestParameter("feeRecurrenceType", RecurrenceType.WEEKLY.getValue().toString());
        addRequestParameter("weekRecurAfter", "2");
        addRequestParameter("glCode", GLOCDE_ID);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "create");
        addRequestParameter("org.apache.struts.taglib.html.TOKEN", (String) request.getSession().getAttribute(
                "org.apache.struts.action.TOKEN"));
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());

        FeeActionForm actionForm = (FeeActionForm) request.getSession().getAttribute("feeactionform");
        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());

        Assert.assertEquals("Customer Periodic Fee", fee.getFeeName());
        Assert.assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType().getId());
        Assert.assertEquals(RateAmountFlag.AMOUNT, fee.getFeeType());
        Assert.assertEquals(new Money(getCurrency(), "100.0"), ((AmountFeeBO) fee).getFeeAmount());
        Assert.assertTrue(fee.isPeriodic());
        Assert.assertTrue(fee.isCustomerDefaultFee());
        Assert.assertTrue(fee.isActive());
    }

    public void testSuccessfulCreatePeriodicFeeWithFormula() throws Exception {
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("categoryType", FeeCategory.LOAN.getValue().toString());
        addRequestParameter("currencyId", TestUtils.RUPEE.getCurrencyId().toString());
        addRequestParameter("rate", "23");
        addRequestParameter("amount", "");
        addRequestParameter("feeFormula", FeeFormula.AMOUNT.getValue().toString());
        addRequestParameter("feeName", "Loan_Periodic_Fee");
        addRequestParameter("customerDefaultFee", "0");
        addRequestParameter("feeFrequencyType", FeeFrequencyType.PERIODIC.getValue().toString());
        addRequestParameter("feeRecurrenceType", RecurrenceType.WEEKLY.getValue().toString());
        addRequestParameter("weekRecurAfter", "2");
        addRequestParameter("glCode", GLOCDE_ID);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "create");
        addRequestParameter("org.apache.struts.taglib.html.TOKEN", (String) request.getSession().getAttribute(
                "org.apache.struts.action.TOKEN"));
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_success.toString());

        FeeActionForm actionForm = (FeeActionForm) request.getSession().getAttribute("feeactionform");
        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, actionForm.getFeeIdValue());

        Assert.assertEquals("Loan_Periodic_Fee", fee.getFeeName());
        Assert.assertEquals(FeeCategory.LOAN.getValue(), fee.getCategoryType().getId());
        Assert.assertEquals(RateAmountFlag.RATE, fee.getFeeType());
        Assert.assertEquals(23.0, ((RateFeeBO) fee).getRate(), DELTA);
        Assert.assertEquals(((RateFeeBO) fee).getFeeFormula().getId(), FeeFormula.AMOUNT.getValue(), DELTA);
        Assert.assertTrue(fee.isPeriodic());
        Assert.assertTrue(fee.isActive());
    }

    public void testSuccessfulManage_AmountFee() throws Exception {
        fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS, "100.0",
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());

        FeeActionForm actionForm = (FeeActionForm) request.getSession().getAttribute("feeactionform");
        Assert.assertEquals("100.0", actionForm.getAmount());
        Assert.assertNull(actionForm.getRate());
        Assert.assertNull(actionForm.getFeeFormula());

        Assert.assertEquals("The size of master data for status", 2, ((List<MasterDataEntity>) SessionUtils
                .getAttribute(FeeConstants.STATUSLIST, request)).size());
    }

    public void testSuccessfulManage_RateFee() throws Exception {
        fee = TestObjectFactory.createOneTimeRateFee("One Time Fee", FeeCategory.ALLCUSTOMERS, 24.0, FeeFormula.AMOUNT,
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());

        FeeActionForm actionForm = (FeeActionForm) request.getSession().getAttribute("feeactionform");
        Assert.assertEquals("24.0", actionForm.getRate());
        Assert.assertEquals(FeeFormula.AMOUNT.getValue().toString(), actionForm.getFeeFormula());
        Assert.assertNull(actionForm.getAmount());

        Assert.assertEquals("The size of master data for status", 2, ((List<MasterDataEntity>) SessionUtils
                .getAttribute(FeeConstants.STATUSLIST, request)).size());
    }

    public void testFailureEditPreviewForAmount() throws PageExpiredException {
        fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS, "100",
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("amount", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals(1, getErrorSize());
        Assert.assertEquals("Fee Amount", 1, getErrorSize(FeeConstants.RATE_OR_AMOUNT));
        verifyInputForward();
    }

    public void testFailureEditPreviewForZeroAmount() throws PageExpiredException {
        fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS, "100",
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter("amount", "0");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertEquals(1, getErrorSize());
        Assert.assertEquals("Fee Amount", 1, getErrorSize(FeeConstants.AMOUNT));
        verifyInputForward();
    }

    public void testFailureEditPreviewForRate() throws Exception {
        fee = TestObjectFactory.createOneTimeRateFee("One Time Fee", FeeCategory.ALLCUSTOMERS, 24.0, FeeFormula.AMOUNT,
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("rate", "");
        actionPerform();
        Assert.assertEquals(1, getErrorSize());
        Assert.assertEquals("RateAndFormula", 1, getErrorSize(FeeConstants.RATE_OR_AMOUNT));
        verifyInputForward();
    }

    public void testSuccessfulEditPreview() throws Exception {
        fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS, "100",
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("amount", "200.0");
        addRequestParameter("feeStatus", FeeStatus.INACTIVE.getValue().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.editPreview_success.toString());

        FeeActionForm actionForm = (FeeActionForm) request.getSession().getAttribute("feeactionform");
        Assert.assertEquals("200.0", actionForm.getAmount());
        Assert.assertEquals(FeeStatus.INACTIVE, actionForm.getFeeStatusValue());
        Assert.assertNull(actionForm.getRate());
        Assert.assertNull(actionForm.getFeeFormula());
    }

    public void testSuccessfulUpdate_AmountFee() throws Exception {
        fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS, "100",
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("amount", "200.0");
        addRequestParameter("feeStatus", FeeStatus.INACTIVE.getValue().toString());
        actionPerform();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.update_success.toString());

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
        Assert.assertFalse(fee.isActive());
        Assert.assertEquals(new Money(getCurrency(), "200.0"), ((AmountFeeBO) fee).getFeeAmount());
    }

    public void testSuccessfulGetFee() throws Exception {
        fee = TestObjectFactory.createOneTimeAmountFee("One Time Fee", FeeCategory.ALLCUSTOMERS, "100",
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "get");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("feeId", fee.getFeeId().toString());
        actionPerform();
        fee = (FeeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Assert.assertNotNull(fee);
    }

    public void testSuccessfulUpdate_RateFee() throws Exception {
        fee = TestObjectFactory.createOneTimeRateFee("One Time Fee", FeeCategory.ALLCUSTOMERS, 24.0, FeeFormula.AMOUNT,
                FeePayment.UPFRONT);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "editPreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("rate", "30");
        actionPerform();

        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.update_success.toString());

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
        Assert.assertTrue(fee.isActive());
        Assert.assertEquals(30.0, ((RateFeeBO) fee).getRate(), DELTA);
    }

    public void testSuccessfulViewAllFees() throws Exception {
        StaticHibernateUtil.startTransaction();
        fee = TestObjectFactory.createOneTimeRateFee("Group_Fee", FeeCategory.GROUP, 10.0, FeeFormula.AMOUNT,
                FeePayment.UPFRONT);
        fee1 = TestObjectFactory.createOneTimeRateFee("Customer_Fee", FeeCategory.ALLCUSTOMERS, 20.0,
                FeeFormula.AMOUNT, FeePayment.UPFRONT);
        fee2 = TestObjectFactory.createOneTimeRateFee("Loan_Fee1", FeeCategory.LOAN, 30.0, FeeFormula.AMOUNT,
                FeePayment.UPFRONT);
        fee3 = TestObjectFactory.createOneTimeRateFee("Center_Fee", FeeCategory.CENTER, 40.0, FeeFormula.AMOUNT,
                FeePayment.UPFRONT);
        fee3.updateStatus(FeeStatus.INACTIVE);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/feeaction.do");
        addRequestParameter("method", "viewAll");

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.viewAll_success.toString());
        flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        List<FeeDto> customerFees = (List<FeeDto>) SessionUtils.getAttribute(FeeConstants.CUSTOMER_FEES, request);
        List<FeeDto> productFees = (List<FeeDto>) SessionUtils.getAttribute(FeeConstants.PRODUCT_FEES, request);
        Assert.assertEquals(3, customerFees.size());
        Assert.assertEquals(1, productFees.size());

        Assert.assertEquals("Center_Fee", customerFees.get(0).getName());
        Assert.assertFalse(customerFees.get(0).isActive());
        Assert.assertEquals("Customer_Fee", customerFees.get(1).getName());
        Assert.assertTrue(customerFees.get(1).isActive());
        Assert.assertEquals("Group_Fee", customerFees.get(2).getName());
        Assert.assertTrue(customerFees.get(2).isActive());

        Assert.assertEquals("Loan_Fee1", productFees.get(0).getName());
        Assert.assertTrue(productFees.get(0).isActive());

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
        fee1 = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee1.getFeeId());
        fee2 = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee2.getFeeId());
        fee3 = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee3.getFeeId());
    }

    public void testFeeCategory() throws Exception {
        try {
            FeeCategory.getFeeCategory(Short.valueOf((short) 999));
            Assert.fail();
        } catch (PropertyNotFoundException pnfe) {
        }
    }
}
