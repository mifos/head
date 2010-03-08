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

package org.mifos.accounts.productdefinition.struts.action;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsPrdActionStrutsTest extends MifosMockStrutsTestCase {

    public SavingsPrdActionStrutsTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;

    private SavingsOfferingBO product;

    private String flowKey;

    UserContext userContext = null;

    AccountPersistence accountPersistence = new AccountPersistence();

    @Override
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/productdefinition-struts-config.xml");
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(product);
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
        flowKey = createFlow(request, SavingsPrdAction.class);
    }

    public void testLoad() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "load");

        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        List<ProductCategoryBO> productCategories = (List<ProductCategoryBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST, request);
       Assert.assertEquals("The size of master data for categories", 1, productCategories.size());
        for (ProductCategoryBO productCategory : productCategories) {
            Assert.assertNotNull(productCategory.getProductType());
        }
       Assert.assertEquals("The size of applicable list", 3, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSAPPLFORLIST, request)).size());
       Assert.assertEquals("The size of applicable list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSTYPELIST, request)).size());
       Assert.assertEquals("The size of applicable list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.RECAMNTUNITLIST, request)).size());
       Assert.assertEquals("The size of applicable list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.INTCALCTYPESLIST, request)).size());
       Assert.assertEquals("The size of applicable list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSRECURRENCETYPELIST, request)).size());
       Assert.assertEquals("The size of applicable list", 6, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSDEPOSITGLCODELIST, request)).size());
       Assert.assertEquals("The size of applicable list", 2, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSINTERESTGLCODELIST, request)).size());
    }

    public void testPreviewWithOutData() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        actionPerform();
        verifyActionErrors(new String[] { "Please select the Applicable for.",
                "Please select the GL code for deposits.", "Please select the Product category.",
                "Please select the Type of deposits.", "Please specify the Product instance name.",
                "Please specify the Short name.", "Please specify the Start date.", "errors.mandatory",
                "errors.mandatory", "errors.mandatory", "errors.select", "errors.select" });
        verifyInputForward();
    }

    public void testPreviewWithPrdApplToGroupAndAppliesToNotEntered() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "2");
        addRequestParameter("savingsType", "2");
        addRequestParameter("interestRate", "1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");

        actionPerform();
        verifyActionErrors(new String[] { "Please select the Amount Applies to." });
        verifyInputForward();
    }

    public void testPreviewWithMandPrdAndAmountNotEntered() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "");

        actionPerform();
        verifyActionErrors(new String[] { ProductDefinitionConstants.ERRORMANDAMOUNT });
        verifyInputForward();
    }

    public void testPreviewWithMandPrdAndZeroAmountEntered() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "0.0");
        addRequestParameter("description", "Savings");
        addRequestParameter("maxAmntWithdrawl", "10.0");
        addRequestParameter("minAmntForInt", "10.0");

        actionPerform();
        verifyActionErrors(new String[] { ProductDefinitionConstants.ERRORMANDAMOUNT });
        verifyInputForward();
    }

    public void testPreviewWithInterestRateGreaterThanHundred() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "120.0");

        actionPerform();
        verifyActionErrors(new String[] { ProductDefinitionConstants.ERRORINTRATE });
        verifyInputForward();
    }

    public void testPreviewWithStartDateLessThanCurrentDate() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(-1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "120.0");

        actionPerform();
        verifyActionErrors(new String[] { ProductDefinitionConstants.INVALIDSTARTDATE });
        verifyInputForward();
    }

    public void testPreviewWithEndDateLessThanStartDate() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(-1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "120.0");

        actionPerform();
        verifyActionErrors(new String[] { ProductDefinitionConstants.INVALIDENDDATE });
        verifyInputForward();
    }

    public void testPreview() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(+1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "120.0");

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.preview_success.toString());
    }

    public void testCreate() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "load");
        actionPerform();

        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(+1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", TestGeneralLedgerCode.MARGIN_MONEY_ONE.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST_CLIENT_MANDATORY_SAVINGS.toString());
        addRequestParameter("recommendedAmount", "120.0");
        actionPerform();

        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());

        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID));
        TestObjectFactory.removeObject((SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class,
                (Short) request.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID)));
    }

    public void testCreateForPrdApplicableToGroups() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "load");
        actionPerform();

        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(+1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "2");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", TestGeneralLedgerCode.MARGIN_MONEY_ONE.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST_CLIENT_MANDATORY_SAVINGS.toString());
        addRequestParameter("recommendedAmount", "120.0");
        addRequestParameter("recommendedAmntUnit", "1");
        actionPerform();

        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());

        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID));
        TestObjectFactory.removeObject((SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class,
                (Short) request.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID)));
    }

    public void testCreateForVolProducts() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "load");
        actionPerform();

        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);

        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(+1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "2");
        addRequestParameter("savingsType", "2");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", TestGeneralLedgerCode.MARGIN_MONEY_ONE.toString());
        addRequestParameter("interestGLCode", TestGeneralLedgerCode.INTEREST_CLIENT_MANDATORY_SAVINGS.toString());
        addRequestParameter("recommendedAmntUnit", "1");
        actionPerform();

        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "create");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());

        Assert.assertNotNull(request.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID));
        TestObjectFactory.removeObject((SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class,
                (Short) request.getAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID)));
    }

    public void testCancelCreate() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "cancelCreate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.cancelCreate_success.toString());
    }

    public void testCancelEdit() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "cancelEdit");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.cancelEdit_success.toString());
    }

    public void testPrevious() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previous");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previous_success.toString());
    }

    public void testValidate() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(null);
    }

    public void testValidateForPreview() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(ProductDefinitionConstants.METHODCALLED, Methods.preview.toString());

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.preview_failure.toString());
    }

    public void testVaildateForCreate() throws Exception {
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "validate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(ProductDefinitionConstants.METHODCALLED, Methods.create.toString());

        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.create_failure.toString());
    }

    public void testGet() throws Exception {
        String prdName = "Savings_Kendra";
        String prdShortName = "SSK";
        createSavingsOfferingBO(prdName, prdShortName);
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "get");
        addRequestParameter("prdOfferingId", product.getPrdOfferingId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.get_success.toString());
       Assert.assertEquals(prdName, product.getPrdOfferingName());
       Assert.assertEquals(prdShortName, product.getPrdOfferingShortName());
       Assert.assertEquals(prdShortName, product.getPrdOfferingShortName());
       Assert.assertEquals(PrdStatus.SAVINGS_ACTIVE, product.getStatus());
       Assert.assertEquals(2, product.getSavingsType().getId().shortValue());
    }

    public void testManage() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        List<ProductCategoryBO> productCategories = (List<ProductCategoryBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST, request);
       Assert.assertEquals("The size of master data for categories", 1, productCategories.size());
        for (ProductCategoryBO productCategory : productCategories) {
            Assert.assertNotNull(productCategory.getProductType());
        }
       Assert.assertEquals("The size of applicable list", 3, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSAPPLFORLIST, request)).size());
       Assert.assertEquals("The size of savings type list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSTYPELIST, request)).size());
       Assert.assertEquals("The size of reco amount unit list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.RECAMNTUNITLIST, request)).size());
       Assert.assertEquals("The size of interest calculation types list", 2, ((List<MasterDataEntity>) SessionUtils
                .getAttribute(ProductDefinitionConstants.INTCALCTYPESLIST, request)).size());
       Assert.assertEquals("The size of recurrence type list", 2, ((List<MasterDataEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSRECURRENCETYPELIST, request)).size());
       Assert.assertEquals("The size of applicable list", 6, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSDEPOSITGLCODELIST, request)).size());
       Assert.assertEquals("The size of gl codes list", 2, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSINTERESTGLCODELIST, request)).size());
       Assert.assertEquals("The size of status list", 2, ((List<GLCodeEntity>) SessionUtils.getAttribute(
                ProductDefinitionConstants.PRDCATEGORYSTATUSLIST, request)).size());

    }

    public void testPreviewManageWithOutData() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
       Assert.assertEquals("error size", 12, getErrorSize());
       Assert.assertEquals("prdOfferingName", 1, getErrorSize("prdOfferingName"));
       Assert.assertEquals("prdOfferingShortName", 1, getErrorSize("prdOfferingShortName"));
       Assert.assertEquals("prdCategory", 1, getErrorSize("prdCategory"));
       Assert.assertEquals("startDate", 1, getErrorSize("startDate"));
       Assert.assertEquals("prdApplicableMaster", 1, getErrorSize("prdApplicableMaster"));
       Assert.assertEquals("savingsType", 1, getErrorSize("savingsType"));
       Assert.assertEquals("interestRate", 1, getErrorSize("interestRate"));
       Assert.assertEquals("interestCalcType", 1, getErrorSize("interestCalcType"));
       Assert.assertEquals("timeForInterestCacl", 1, getErrorSize("timeForInterestCacl"));
       Assert.assertEquals("freqOfInterest", 1, getErrorSize("freqOfInterest"));
       Assert.assertEquals("depositGLCode", 1, getErrorSize("depositGLCode"));
       Assert.assertEquals("interest", 1, getErrorSize("interest"));
        verifyInputForward();
    }

    public void testPreviewManageWithPrdApplToGroupAndAppliesToNotEntered() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "2");
        addRequestParameter("savingsType", "2");
        addRequestParameter("interestRate", "1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");

        actionPerform();
       Assert.assertEquals("recommendedAmntUnit", 1, getErrorSize("recommendedAmntUnit"));
        verifyInputForward();
    }

    public void testPreviewManageWithMandPrdAndAmountNotEntered() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "");

        actionPerform();
       Assert.assertEquals("Manadatory amount", 1, getErrorSize("recommendedAmount"));
        verifyInputForward();
    }

    public void testPreviewManageWithMandPrdAndZeroAmountEntered() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "0.0");
        addRequestParameter("description", "Savings");
        addRequestParameter("maxAmntWithdrawl", "10.0");
        addRequestParameter("minAmntForInt", "10.0");

        actionPerform();
       Assert.assertEquals("Manadatory amount is 0", 1, getErrorSize("recommendedAmount"));
        verifyInputForward();
    }

    public void testPreviewManageWithInterestRateGreaterThanHundred() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", DateUtils.getCurrentDate(userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.1");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "120.0");

        actionPerform();
       Assert.assertEquals("interestRate >100", 1, getErrorSize("interestRate"));
        verifyInputForward();
    }

    public void testPreviewManageWithStartDateLessThanCurrentDate() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(-1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "120.0");

        actionPerform();
       Assert.assertEquals("start date", 1, getErrorSize("startDate"));
        verifyInputForward();
    }

    public void testPreviewManageWithEndDateLessThanStartDate() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);

        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(-1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("depositGLCode", "42");
        addRequestParameter("interestGLCode", "57");
        addRequestParameter("recommendedAmount", "120.0");

        actionPerform();
       Assert.assertEquals("endDate", 1, getErrorSize("endDate"));
        verifyInputForward();
    }

    public void testPreviewManage() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(+1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "100.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("recommendedAmount", "120.0");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previewManage_success.toString());
    }

    public void testPreviewManageFlowFailure() throws Exception {
        try {
            request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
            createSavingsOfferingAndPutInSession();
            setRequestPathInfo("/savingsproductaction.do");
            addRequestParameter("method", "previewManage");
            addRequestParameter("prdOfferingName", "Savings Offering");
            addRequestParameter("prdOfferingShortName", "SAVP");
            addRequestParameter("prdCategory", "2");
            addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
            addRequestParameter("endDate", offSetCurrentDate(+1, userContext.getPreferredLocale()));
            addRequestParameter("prdApplicableMaster", "1");
            addRequestParameter("savingsType", "1");
            addRequestParameter("interestRate", "100.0");
            addRequestParameter("interestCalcType", "1");
            addRequestParameter("timeForInterestCacl", "1");
            addRequestParameter("recurTypeFortimeForInterestCacl", "2");
            addRequestParameter("freqOfInterest", "1");
            addRequestParameter("depositGLCode", "42");
            addRequestParameter("interestGLCode", "57");
            addRequestParameter("recommendedAmount", "120.0");
            actionPerform();
        } catch (PageExpiredException pe) {
           Assert.assertTrue(true);
           Assert.assertEquals(ExceptionConstants.PAGEEXPIREDEXCEPTION, pe.getKey());
        }
    }

    public void testUpdate() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("prdOfferingName", "Savings Offering");
        addRequestParameter("prdOfferingShortName", "SAVP");
        addRequestParameter("prdCategory", "2");
        addRequestParameter("startDate", offSetCurrentDate(0, userContext.getPreferredLocale()));
        addRequestParameter("endDate", offSetCurrentDate(+1, userContext.getPreferredLocale()));
        addRequestParameter("prdApplicableMaster", "1");
        addRequestParameter("savingsType", "1");
        addRequestParameter("interestRate", "9.0");
        addRequestParameter("interestCalcType", "1");
        addRequestParameter("timeForInterestCacl", "1");
        addRequestParameter("recurTypeFortimeForInterestCacl", "2");
        addRequestParameter("freqOfInterest", "1");
        addRequestParameter("recommendedAmount", "120.0");
        addRequestParameter("status", "5");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previewManage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_success.toString());

        product = (SavingsOfferingBO) StaticHibernateUtil.getSessionTL().get(SavingsOfferingBO.class,
                product.getPrdOfferingId());
       Assert.assertEquals("Savings Offering", product.getPrdOfferingName());
       Assert.assertEquals("SAVP", product.getPrdOfferingShortName());
       Assert.assertEquals(2, product.getPrdCategory().getProductCategoryID().intValue());
       Assert.assertEquals(PrdStatus.SAVINGS_INACTIVE, product.getStatus());
       Assert.assertEquals(SavingsType.MANDATORY, product.getSavingsTypeAsEnum());
       Assert.assertEquals(1, product.getInterestCalcType().getId().intValue());
       Assert.assertEquals(1, product.getTimePerForInstcalc().getMeeting().getMeetingDetails().getRecurAfter().intValue());
       Assert.assertEquals(2, product.getTimePerForInstcalc().getMeeting().getMeetingDetails().getRecurrenceType()
                .getRecurrenceId().shortValue());
       Assert.assertEquals(1, product.getFreqOfPostIntcalc().getMeeting().getMeetingDetails().getRecurAfter().intValue());
       Assert.assertEquals("Recommended Amount", new Money(getCurrency(), "120"), product.getRecommendedAmount());
       Assert.assertEquals(9.0, product.getInterestRate(), DELTA);
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testPreviousManage() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previousManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.previousManage_success.toString());
    }

    public void testSearch() throws Exception {
        createSavingsOfferingBO("prdOfferingName", "SN");
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "search");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.search_success.toString());
        List<SavingsOfferingBO> savingsProducts = (List<SavingsOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSPRODUCTLIST, request);
       Assert.assertEquals("The size of savings products", 1, savingsProducts.size());

    }

    public void testSearch_Inactive() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createSavingsOfferingAndPutInSession();
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "previewManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("status", "5");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previewManage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_success.toString());

        product = (SavingsOfferingBO) StaticHibernateUtil.getSessionTL().get(SavingsOfferingBO.class,
                product.getPrdOfferingId());

       Assert.assertEquals(PrdStatus.SAVINGS_INACTIVE, product.getStatus());
        setRequestPathInfo("/savingsproductaction.do");
        addRequestParameter("method", "search");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.search_success.toString());
        List<SavingsOfferingBO> savingsProducts = (List<SavingsOfferingBO>) SessionUtils.getAttribute(
                ProductDefinitionConstants.SAVINGSPRODUCTLIST, request);
       Assert.assertEquals("The size of savings products", 1, savingsProducts.size());

        SavingsOfferingBO savingsProduct = (SavingsOfferingBO) StaticHibernateUtil.getSessionTL().get(
                SavingsOfferingBO.class, savingsProducts.get(0).getPrdOfferingId());

       Assert.assertEquals("Inactive", savingsProduct.getPrdStatus().getPrdState().getName());

        product = (SavingsOfferingBO) StaticHibernateUtil.getSessionTL().get(SavingsOfferingBO.class,
                product.getPrdOfferingId());

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

    private SavingsOfferingBO createSavingsOfferingBO(String productName, String shortName) {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        product = TestObjectFactory.createSavingsProduct(productName, shortName, ApplicableTo.CLIENTS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2,
                200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        return product;
    }

    private void createSavingsOfferingAndPutInSession() throws Exception {
        String prdName = "Savings_Kendra";
        String prdShortName = "SSK";
        createSavingsOfferingBO(prdName, prdShortName);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, product, request);
    }

}
