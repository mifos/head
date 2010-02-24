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

package org.mifos.accounts.loan.struts.action;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.ViewInstallmentDetails;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanActivityView;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.CenterTemplate;
import org.mifos.customers.center.CenterTemplateImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.GroupTemplate;
import org.mifos.customers.group.GroupTemplateImpl;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.MeetingTemplateImpl;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanAmountSameForAllLoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFeesEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.business.LoanOfferingTestUtils;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanAccountActionStrutsTest extends AbstractLoanActionTestCase {
    public LoanAccountActionStrutsTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;
    private String flowKey1;
    private HashMap<String, String> schedulePreviewPageParams;
    private HashMap<String, String> prdOfferingPageParams;
    private CustomerBO customerMock;
    private LoanBusinessService loanBusinessServiceMock;
    private ConfigurationBusinessService configurationBusinessServiceMock;
    private HttpServletRequest requestMock;
    private CustomerBO groupForLoanRedo = null;
    private CustomerBO clientForLoanRedo = null;
    private LoanOfferingBO loanOfferingForLoanRedo = null;
    private CenterBO centerForLoanRedo = null;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/accounts-struts-config.xml");
    }
        
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prdOfferingPageParams = new HashMap<String, String>();
        initPageParams();
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
    }

    @Override
    protected void tearDown() throws Exception {
        TestDatabase.resetMySQLDatabase();
        super.tearDown();
    }

    private void initSchedulePreviewPageParamsForLoanRedo(LoanOfferingBO loanOffering, String disbursementDate) {
        schedulePreviewPageParams = new HashMap<String, String>();
        schedulePreviewPageParams.put("loanAmount", loanOffering.eligibleLoanAmount(
                group.getMaxLoanAmount(loanOffering), group.getMaxLoanCycleForProduct(loanOffering))
                .getDefaultLoanAmount().toString());
        schedulePreviewPageParams.put("interestRate", loanOffering.getMaxInterestRate().toString());
        schedulePreviewPageParams.put("noOfInstallments", loanOffering.eligibleNoOfInstall(
                group.getMaxLoanAmount(loanOffering), group.getMaxLoanCycleForProduct(loanOffering))
                .getDefaultNoOfInstall().toString());
        schedulePreviewPageParams.put("disbursementDate", disbursementDate);
        schedulePreviewPageParams.put("gracePeriodDuration", "1");
        schedulePreviewPageParams.put("businessActivityId", "1");
        schedulePreviewPageParams.put("loanOfferingFund", "1");
    }

    private void createPayment(LoanBO loan, Money amountPaid) throws Exception {
        Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, 6);
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(TestObjectFactory.getContext().getId());
        LoanScheduleEntity loanSchedule = paymentsArray[0];
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        PaymentData paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                .getActionDate());
        loan.applyPayment(paymentData, true);
        paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                .getActionDate());
        loan.applyPayment(paymentData, true);
    }

    private Date createPreviousDate(int numberOfDays) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -numberOfDays);
        Date pastDate = DateUtils.getDateWithoutTimeStamp(calendar.getTime());
        return pastDate;
    }

    private GroupBO createGroup(UserContext userContext, GroupTemplate groupTemplate, final Date disbursementDate)
            throws PersistenceException, ValidationException, CustomerException {
        CenterBO center = null;
        if (groupTemplate.getParentCenterId() != null) {
            center = new GroupPersistence().getCenterPersistence().getCenter(groupTemplate.getParentCenterId());
            if (center == null) {
                throw new ValidationException(GroupConstants.PARENT_OFFICE_ID);
            }
        }
        GroupBO group = TestObjectFactory.createInstanceForTest(userContext, groupTemplate, center, disbursementDate);
        new GroupPersistence().saveGroup(group);
        return group;
    }

    private LoanOfferingBO createLoanOffering(UserContext userContext, MeetingBO meeting, Date loanProductStartDate)
            throws ProductDefinitionException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.GROUPS);
        ProductCategoryBO productCategory = TestObjectFactory.getLoanPrdCategory();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        InterestTypesEntity interestType = new InterestTypesEntity(InterestType.FLAT);
        GLCodeEntity glCodePrincipal = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.LOANS_TO_CLIENTS);
        GLCodeEntity glCodeInterest = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.INTEREST_ON_LOANS);

        boolean interestDeductedAtDisbursement = false;
        boolean principalDueInLastInstallment = false;
        Money loanAmount = new Money(getCurrency(), "300");
        Double interestRate = new Double(1.2);
        Short installments = new Short((short) 6);
        LoanOfferingBO loanOffering = LoanOfferingTestUtils.createInstanceForTest(userContext, "TestLoanOffering",
                "TLO", productCategory, prdApplicableMaster, loanProductStartDate, null, null, gracePeriodType,
                (short) 0, interestType, loanAmount, loanAmount, loanAmount, interestRate, interestRate, interestRate,
                installments, installments, installments, true, interestDeductedAtDisbursement,
                principalDueInLastInstallment, new ArrayList<FundBO>(), new ArrayList<FeeBO>(), meeting,
                glCodePrincipal, glCodeInterest);

        PrdStatusEntity prdStatus = new TestObjectPersistence().retrievePrdStatus(PrdStatus.LOAN_ACTIVE);
        LoanOfferingTestUtils.setStatus(loanOffering, prdStatus);
        LoanOfferingTestUtils.setGracePeriodType(loanOffering, gracePeriodType);
        loanOffering.save();

        return loanOffering;
    }

    private void initPageParamsForLoanRedo(LoanOfferingBO loanOffering, String disbursementDate) {
        initPrdOfferingPageParams(loanOffering);
        initSchedulePreviewPageParamsForLoanRedo(loanOffering, disbursementDate);
    }

    private void createInitialObjectsForLoanRedo() throws Exception {
        // groupForLoanRedo =
        // TestObjectFactory.createGroupUnderCenter("MyGroup",
        // CustomerStatus.GROUP_ACTIVE, center);
        // String newDate = offSetCurrentDate(-2,
        // userContext.getPreferredLocale());
        // groupForLoanRedo.setCreatedDate(DateUtils.getDate(newDate));
        // clientForLoanRedo = TestObjectFactory.createClient("MyClient",
        // CustomerStatus.CLIENT_ACTIVE, groupForLoanRedo);
        // clientForLoanRedo.setCreatedDate(DateUtils.getDate(newDate));
        int loanStartDaysAgo = 14;
        Date disbursementDate = createPreviousDate(loanStartDaysAgo);
        OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        MeetingBO meeting = new MeetingBO(MeetingTemplateImpl.createWeeklyMeetingTemplateStartingFrom(disbursementDate));
        // .createWeeklyMeetingTemplateOnMondaysStartingFrom(disbursementDate));

        CenterTemplate centerTemplate = new CenterTemplateImpl(meeting, office.getOfficeId());
        centerForLoanRedo = new CenterPersistence().createCenter(userContext, centerTemplate);

        GroupTemplate groupTemplate = GroupTemplateImpl.createNonUniqueGroupTemplate(centerForLoanRedo.getCustomerId());

        groupForLoanRedo = createGroup(userContext, groupTemplate, disbursementDate);

        loanOfferingForLoanRedo = createLoanOffering(userContext, meeting, disbursementDate);
        initPageParamsForLoanRedo(loanOfferingForLoanRedo, disbursementDate.toString());
    }

    private void goToLoanAccountInputPageForLoanRedo() {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "load");
        addRequestParameter("customerId", groupForLoanRedo.getCustomerId().toString());
        Set<Entry<String, String>> entrySet = prdOfferingPageParams.entrySet();
        for (Entry<String, String> entry : entrySet) {
            addRequestParameter(entry.getKey(), entry.getValue());
        }
    }

    private void goToPrdOfferingPageForLoanRedo() throws Exception {
        request.getSession().setAttribute(Constants.BUSINESS_KEY, groupForLoanRedo);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", groupForLoanRedo.getCustomerId().toString());
    }

    private void goToSchedulePreviewPageForLoanRedo() throws InvalidDateException {
        setRequestPathInfo("/loanAccountAction.do");
        Set<Entry<String, String>> entrySet = schedulePreviewPageParams.entrySet();
        for (Entry<String, String> entry : entrySet) {
            addRequestParameter(entry.getKey(), entry.getValue());
        }
        addRequestParameter("method", "schedulePreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        int loanStartDaysAgo = 14;
        String disbursementDate = offSetCurrentDate(-loanStartDaysAgo, userContext.getPreferredLocale());
        addRequestParameter("disbursementDate", disbursementDate);

    }

    private void jumpToSchedulePreviewForLoanRedo() throws Exception {

        goToPrdOfferingPageForLoanRedo();
        actionPerform();
        goToLoanAccountInputPageForLoanRedo();
        actionPerform();
        goToSchedulePreviewPageForLoanRedo();
    }

    // repaymentSchedulesIndependentOfMeetingIsEnabled
    public void testLoadWithFeeForToday() throws Exception {
        // get rid of default objects
        tearDown();

        new DateTimeService().setCurrentDateTime(new DateMidnight(2010,1,6).toDateTime());
        new ConfigurationPersistence().updateConfigurationKeyValueInteger("repaymentSchedulesIndependentOfMeetingIsEnabled", 1);
        LoanAccountActionForm loanActionForm = null;
        try {
            // setup again after setting date and config parameters
            setUp();

            goToPrdOfferingPage();
            actionPerform();
            setRequestPathInfo("/loanAccountAction.do");
            addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
            addRequestParameter("method", "load");
            addRequestParameter("customerId", group.getCustomerId().toString());
            addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
            performNoErrors();
            verifyForward(ActionForwards.load_success.toString());
            loanActionForm = (LoanAccountActionForm) request.getSession().getAttribute("loanAccountActionForm");
        } finally {
            new ConfigurationPersistence().updateConfigurationKeyValueInteger("repaymentSchedulesIndependentOfMeetingIsEnabled", 0);
            new DateTimeService().resetToCurrentSystemDateTime();
        }
        Assert.assertNotNull(loanActionForm);
        Assert.assertEquals(WeekDay.WEDNESDAY.getValue().toString(), loanActionForm.getWeekDay());

        group = TestObjectFactory.getGroup(group.getCustomerId());
    }
        
    public void testRedoLoanThenApplyPayment() throws Exception {
        createInitialObjectsForLoanRedo();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        jumpToSchedulePreviewForLoanRedo();
        // actionPerform();
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "create");
        // addRequestParameter("stateSelected", "1");
        addRequestParameter("perspective", LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN);
        int loanStartDaysAgo = 14;
        String disbursementDate = offSetCurrentDate(-loanStartDaysAgo, userContext.getPreferredLocale());
        addRequestParameter("disbursementDate", disbursementDate);

        performNoErrors();
        verifyForward(ActionForwards.create_success.toString());
        LoanAccountActionForm actionForm = (LoanAccountActionForm) request.getSession().getAttribute(
                "loanAccountActionForm");
        LoanBO loan = TestObjectFactory.getObject(LoanBO.class, new Integer(actionForm.getAccountId()).intValue());
        createPayment(loan, new Money(getCurrency(), "100"));
        StaticHibernateUtil.commitTransaction();
        loan = TestObjectFactory.getObject(LoanBO.class, new Integer(actionForm.getAccountId()).intValue());
        Assert.assertEquals(new LocalDate(), new LocalDate(loan.getAccountApprovalDate().getTime()));
        Assert.assertEquals(new LocalDate(), new LocalDate(loan.getCreatedDate().getTime()));

        TestObjectFactory.cleanUp(loan);
        TestObjectFactory.cleanUp(clientForLoanRedo);
        TestObjectFactory.cleanUp(groupForLoanRedo);
        TestObjectFactory.cleanUp(centerForLoanRedo);

    }

    public void testCreateWithoutPermission() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        UserContext userContext = TestUtils.makeUser();
        userContext.setRoles(new HashSet());
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

        SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering, request);
        SessionUtils.setAttribute(LoanConstants.LOANFUNDS, new ArrayList<FundBO>(), request);
        SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group, request);
        SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES, new ArrayList<MasterDataEntity>(), request);
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "create");
        addRequestParameter("stateSelected", "1");
        actionPerform();
        verifyActionErrors(new String[] { SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED });
        verifyForward(ActionForwards.create_failure.toString());
    }

    public void testUpdateSuccessWithRegeneratingNewRepaymentSchedule() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        String newDate = offSetCurrentDate(14, userContext.getPreferredLocale());
        // This is a loan with weekly meetings every week. No interest is paid
        // at disbursement, so the first payment will be one week after the
        // disbursement date of the loan (14 days + 7 days = 21 days)
        String firstInstallmentDateWithNoInterestPaidAtDisbursementDate = offSetCurrentDate(21, userContext
                .getPreferredLocale());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        ((LoanBO) accountBO).setBusinessActivityId(1);
        accountBO.changeStatus(AccountState.LOAN_APPROVED.getValue(), null, "status changed");
        accountBO.update();

        StaticHibernateUtil.commitTransaction();
        LoanBO loan = (LoanBO) accountBO;
        LoanOfferingBO loanOffering = loan.getLoanOffering();
        // loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "manage");
        addRequestParameter("customerId", accountBO.getCustomer().getCustomerId().toString());
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "managePreview");
        addRequestParameter("loanAmount", loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount()
                .toString());
        addRequestParameter("interestRate", loan.getLoanOffering().getDefInterestRate().toString());
        addRequestParameter("noOfInstallments", loanOffering.getEligibleInstallmentSameForAllLoan()
                .getDefaultNoOfInstall().toString());
        addRequestParameter("disbursementDate", newDate);
        addRequestParameter("gracePeriodDuration", "0");
        addRequestParameter("intDedDisbursement", "0");
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "update");
        addRequestParameter("collateralNote", "test");
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        Assert.assertEquals("test", loan.getCollateralNote());
        Assert.assertEquals(300.0, loan.getLoanAmount().getAmountDoubleValue(), DELTA);
        Assert.assertFalse(loan.isInterestDeductedAtDisbursement());
        Assert.assertEquals(0, loan.getGracePeriodDuration().intValue());
        Assert.assertEquals(firstInstallmentDateWithNoInterestPaidAtDisbursementDate, DateUtils.getUserLocaleDate(
                TestObjectFactory.getContext().getPreferredLocale(), DateUtils.toDatabaseFormat(loan
                        .getAccountActionDate(Short.valueOf("1")).getActionDate())));

    }

    public void testSchedulePreviewFailureWhenLoanProductFrequencyChanges() throws Exception {
        request.getSession().setAttribute(Constants.BUSINESS_KEY, group);
        LoanPrdActionForm loanPrdActionForm = new LoanPrdActionForm();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        actionPerform();

        LoanAmountSameForAllLoanBO eligibleLoanAmountRange = loanOffering.getEligibleLoanAmountSameForAllLoan();
        LoanOfferingInstallmentRange eligibleInstallmentRange = loanOffering.getEligibleInstallmentSameForAllLoan();
        populateLoanAmountSameForAllLoan("1", eligibleLoanAmountRange.getMaxLoanAmount(), eligibleLoanAmountRange
                .getMinLoanAmount(), eligibleLoanAmountRange.getDefaultLoanAmount(), loanPrdActionForm);
        populateNoOfInstallSameForAllLoan("1", eligibleInstallmentRange.getMaxNoOfInstall().toString(),
                eligibleInstallmentRange.getMinNoOfInstall().toString(), eligibleInstallmentRange
                        .getDefaultNoOfInstall().toString(), loanPrdActionForm);
        loanOffering.update(Short.valueOf("1"), loanOffering.getPrdOfferingName(), loanOffering
                .getPrdOfferingShortName(), loanOffering.getPrdCategory(), loanOffering.getPrdApplicableMaster(),
                loanOffering.getStartDate(), loanOffering.getEndDate(), loanOffering.getDescription(),
                PrdStatus.LOAN_ACTIVE, loanOffering.getGracePeriodType(), loanOffering.getInterestTypes(), loanOffering
                        .getGracePeriodDuration(), loanOffering.getMaxInterestRate(),
                loanOffering.getMinInterestRate(), loanOffering.getDefInterestRate(), loanOffering
                        .isIncludeInLoanCounter(), loanOffering.isIntDedDisbursement(), loanOffering
                        .isPrinDueLastInst(), new ArrayList<FundBO>(), new ArrayList<FeeBO>(), Short.valueOf("1"),
                RecurrenceType.MONTHLY, loanPrdActionForm);
        StaticHibernateUtil.commitTransaction();

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("loanAmount", loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount()
                .toString());
        addRequestParameter("interestRate", loanOffering.getDefInterestRate().toString());
        addRequestParameter("noOfInstallments", loanOffering.getEligibleInstallmentSameForAllLoan()
                .getDefaultNoOfInstall().toString());
        addRequestParameter("disbursementDate", DateUtils.getCurrentDate(((UserContext) request.getSession()
                .getAttribute("UserContext")).getPreferredLocale()));
        addRequestParameter("gracePeriodDuration", "1");
        addRequestParameter("businessActivityId", "1");
        addRequestParameter("loanOfferingFund", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(LoanConstants.CUSTOM_FIELDS, request);
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter("method", "schedulePreview");
        actionPerform();
        verifyActionErrors(new String[] { "exception.accounts.changeInLoanMeeting" });
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testGetForCancelledLoanAccount() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        accountBO.changeStatus(AccountState.LOAN_CANCELLED.getValue(), AccountStateFlag.LOAN_WITHDRAW.getValue(),
                "status changed");
        accountBO.update();
        StaticHibernateUtil.commitTransaction();
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();
        verifyForward("get_success");

        Assert.assertEquals(0, loan.getPerformanceHistory().getNoOfPayments().intValue());
        Assert.assertEquals(((LoanBO) accountBO).getTotalAmountDue().getAmountDoubleValue(), 212.0);
        modifyActionDateForFirstInstallment();
        Assert.assertEquals("Total no. of notes should be 6", 6, accountBO.getAccountNotes().size());
        Assert.assertEquals("Total no. of recent notes should be 3", 3, ((List<AccountNotesEntity>) SessionUtils
                .getAttribute(LoanConstants.NOTES, request)).size());
        Assert.assertEquals("Total no. of flags should be 1", 1, accountBO.getAccountFlags().size());
    }

    public void testPrevious() {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "previous");
        performNoErrors();
        verifyForward("load_success");
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testPreview() {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("method", "preview");
        performNoErrors();
        verifyForward("preview_success");
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testforwardWaiveCharge() {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "forwardWaiveCharge");
        addRequestParameter("type", "LoanAccount");
        performNoErrors();
        verifyForward("waiveLoanAccountCharges_Success");
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoadChangeLog() {
        accountBO = getLoanAccount();
        AuditLog auditLog = new AuditLog(accountBO.getAccountId(), EntityType.LOAN.getValue(), "Mifos",
                new java.sql.Date(System.currentTimeMillis()), Short.valueOf("3"));
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1", "test_1", "new_test_1", auditLog);
        auditLogRecords.add(auditLogRecord);
        auditLog.addAuditLogRecords(auditLogRecords);
        auditLog.save();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "loadChangeLog");
        addRequestParameter("entityType", "Loan");
        addRequestParameter("entityId", accountBO.getAccountId().toString());
        actionPerform();
        Assert.assertEquals(1, ((List) request.getSession().getAttribute(AuditConstants.AUDITLOGRECORDS)).size());
        verifyForward("viewLoanChangeLog");
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testCancelChangeLog() {
        accountBO = getLoanAccount();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("method", "cancelChangeLog");
        addRequestParameter("entityType", "Loan");
        actionPerform();
        verifyForward("cancelLoanChangeLog");
    }

    public void testGetAllActivity() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getAllActivity");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("getAllActivity_success");
        Assert.assertEquals(1, ((List<LoanActivityView>) SessionUtils.getAttribute(
                LoanConstants.LOAN_ALL_ACTIVITY_VIEW, request)).size());
    }

    public void testGetInstallmentDetails() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;
        for (AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()) {
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1")))
                LoanBOTestUtils.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                        .getActionDate(), -14));
            else if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
                LoanBOTestUtils.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                        .getActionDate(), -7));
        }
        TestObjectFactory.updateObject(loan);
        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getInstallmentDetails");
        addRequestParameter("accountId", String.valueOf(loan.getAccountId()));
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("viewInstmentDetails_success");

        ViewInstallmentDetails view = (ViewInstallmentDetails) SessionUtils.getAttribute(
                LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS, request);
        Assert.assertEquals(new Money(getCurrency(), "12.0"), view.getInterest());
        Assert.assertEquals(new Money(getCurrency(), "100.0"), view.getFees());
        Assert.assertEquals(new Money(getCurrency(), "0.0"), view.getPenalty());
        Assert.assertEquals(new Money(getCurrency(), "100.0"), view.getPrincipal());
    }

    public void testGet() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();
        verifyForward("get_success");

        Assert.assertEquals(0, loan.getPerformanceHistory().getNoOfPayments().intValue());
        Assert.assertEquals(((LoanBO) accountBO).getTotalAmountDue().getAmountDoubleValue(), 212.0);
        modifyActionDateForFirstInstallment();
        Assert.assertEquals("Total no. of notes should be 5", 5, accountBO.getAccountNotes().size());
        Assert.assertEquals("Total no. of recent notes should be 3", 3, ((List<AccountNotesEntity>) SessionUtils
                .getAttribute(LoanConstants.NOTES, request)).size());
    }

    public void testGetWithPayment() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        disburseLoan(startDate);
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();
        verifyForward("get_success");

        Assert.assertEquals("Total no. of notes should be 5", 5, accountBO.getAccountNotes().size());
        Assert.assertEquals("Total no. of recent notes should be 3", 3, ((List<AccountNotesEntity>) SessionUtils
                .getAttribute(LoanConstants.NOTES, request)).size());

        Assert.assertEquals("Last payment action should be 'PAYMENT'", AccountActionTypes.DISBURSAL.getValue(),
                SessionUtils.getAttribute(AccountConstants.LAST_PAYMENT_ACTION, request));
        client = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, client.getCustomerId());
        group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
    }

    public void testGetLoanRepaymentSchedule() {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getLoanRepaymentSchedule");
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.getLoanRepaymentSchedule.toString());
    }

    public void testViewStatusHistory() {
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
        actionPerform();

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "viewStatusHistory");
        actionPerform();
        verifyForward(ActionForwards.viewStatusHistory.toString());
    }

    public void testGetPrdOfferingsWithoutCustomer() throws Exception {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        actionPerform();
        verifyActionErrors(new String[] { LoanConstants.CUSTOMERNOTSELECTEDERROR });
        verifyInputForward();
    }

    public void testGetPrdOfferings() throws Exception {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", group.getCustomerId().toString());
        performNoErrors();
        verifyForward(ActionForwards.getPrdOfferigs_success.toString());
        Assert.assertEquals("Group", ((CustomerBO) SessionUtils.getAttribute(LoanConstants.LOANACCOUNTOWNER, request))
                .getDisplayName());
    }

    public void testGetPrdOfferingsApplicableForCustomer() throws Exception {
        LoanOfferingBO loanOffering2 = getLoanOffering("rwrfdb", "1qsd", ApplicableTo.GROUPS, WEEKLY, EVERY_WEEK);
        LoanOfferingBO loanOffering3 = getLoanOffering("mksgfgfd", "9u78", ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", group.getCustomerId().toString());
        performNoErrors();
        verifyForward(ActionForwards.getPrdOfferigs_success.toString());

        Assert.assertEquals("Group", ((CustomerBO) SessionUtils.getAttribute(LoanConstants.LOANACCOUNTOWNER, request))
                .getDisplayName());
        Assert.assertEquals(2, ((List<LoanOfferingBO>) SessionUtils.getAttribute(LoanConstants.LOANPRDOFFERINGS,
                request)).size());

        TestObjectFactory.removeObject(loanOffering2);
        TestObjectFactory.removeObject(loanOffering3);
    }

    public void testGetPrdOfferingsApplicableForCustomersWithMeeting() throws Exception {
        LoanOfferingBO loanOffering1 = getLoanOffering("vcxvxc", "a123", ApplicableTo.GROUPS, WEEKLY, EVERY_WEEK);
        LoanOfferingBO loanOffering2 = getLoanOffering("fgdsghdh", "4fdh", ApplicableTo.GROUPS, WEEKLY, EVERY_WEEK);
        LoanOfferingBO loanOffering3 = getLoanOffering("mgkkkj", "6tyu", ApplicableTo.CLIENTS, WEEKLY, EVERY_WEEK);
        LoanOfferingBO loanOffering4 = getLoanOffering("aq12sfdsf", "456j", ApplicableTo.GROUPS, MONTHLY, EVERY_MONTH);
        LoanOfferingBO loanOffering5 = getLoanOffering("bdfhgfh", "6yu7", ApplicableTo.GROUPS, WEEKLY, (short) 3); // every
        // third
        // week

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", group.getCustomerId().toString());
        performNoErrors();
        verifyForward(ActionForwards.getPrdOfferigs_success.toString());

        Assert.assertEquals("Group", ((CustomerBO) SessionUtils.getAttribute(LoanConstants.LOANACCOUNTOWNER, request))
                .getDisplayName());
        Assert.assertEquals(4, ((List<LoanOfferingBO>) SessionUtils.getAttribute(LoanConstants.LOANPRDOFFERINGS,
                request)).size());

        TestObjectFactory.removeObject(loanOffering1);
        TestObjectFactory.removeObject(loanOffering2);
        TestObjectFactory.removeObject(loanOffering3);
        TestObjectFactory.removeObject(loanOffering4);
        TestObjectFactory.removeObject(loanOffering5);
    }

    public void testLoadWithoutCustomerAndPrdOfferingId() throws Exception {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "load");
        actionPerform();
        verifyActionErrors(new String[] { LoanConstants.CUSTOMERNOTSELECTEDERROR,
                LoanConstants.LOANOFFERINGNOTSELECTEDERROR });
        verifyInputForward();
    }

    public void testLoadWithoutCustomer() throws Exception {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        actionPerform();
        verifyActionErrors(new String[] { LoanConstants.CUSTOMERNOTSELECTEDERROR });
        verifyInputForward();
    }

    public void testLoadWithoutPrdOfferingId() throws Exception {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("prdOfferingId", "");
        actionPerform();
        verifyActionErrors(new String[] { LoanConstants.LOANOFFERINGNOTSELECTEDERROR });
        verifyInputForward();
    }

    public void testLoad() throws Exception {
        goToPrdOfferingPage();
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        performNoErrors();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANOFFERING, request));
        Assert.assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANFUNDS, request));
        Assert.assertNotNull(SessionUtils.getAttribute(LoanConstants.CUSTOM_FIELDS, request));
    }

    public void testLoadForMasterData() throws Exception {
        request.getSession().setAttribute(Constants.BUSINESS_KEY, group);
        LoanOfferingBO loanOffering = getCompleteLoanOfferingObject();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        performNoErrors();
        verifyForward(ActionForwards.load_success.toString());

        Assert.assertEquals(2, ((List) SessionUtils.getAttribute(MasterConstants.COLLATERAL_TYPES, request)).size());
        // after the empty lookup name 259 and 263 are removed this will go down
        // to 129
        Assert.assertEquals(131, ((List) SessionUtils.getAttribute(MasterConstants.BUSINESS_ACTIVITIES, request))
                .size());

        TestObjectFactory.removeObject(loanOffering);
    }

    public void testLoadWithFee() throws Exception {
        goToPrdOfferingPage();
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        performNoErrors();
        verifyForward(ActionForwards.load_success.toString());
        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) request.getSession().getAttribute(
                "loanAccountActionForm");
        Assert.assertEquals(2, ((List) SessionUtils.getAttribute(LoanConstants.ADDITIONAL_FEES_LIST, request)).size());
        // Assert.assertEquals(loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount().toString(),
        // loanActionForm.getLoanAmount());

        // Assert.assertEquals(loanOffering.getEligibleInstallmentSameForAllLoan().getDefaultNoOfInstall().toString(),
        // loanActionForm.getNoOfInstallments());
        Assert.assertEquals(loanOffering.getDefInterestRate().toString(), loanActionForm.getInterestRate());
        Assert.assertEquals(loanOffering.isIntDedDisbursement(), loanActionForm.isInterestDedAtDisbValue());
        Assert.assertEquals(loanOffering.getGracePeriodDuration().toString(), loanActionForm.getGracePeriodDuration());
        Assert.assertEquals(DateUtils.getCurrentDate(((UserContext) request.getSession().getAttribute("UserContext"))
                .getPreferredLocale()), loanActionForm.getDisbursementDate());
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }


    public void testLoadWithFeeForLoanOffering() throws Exception {
        request.getSession().setAttribute(Constants.BUSINESS_KEY, group);
        loanOffering.addPrdOfferingFee(new LoanOfferingFeesEntity(loanOffering, fees.get(0)));
        TestObjectFactory.updateObject(loanOffering);
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("prdOfferingId", loanOffering.getPrdOfferingId().toString());
        performNoErrors();
        verifyForward(ActionForwards.load_success.toString());
        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) request.getSession().getAttribute(
                "loanAccountActionForm");
        Assert.assertEquals(1, ((List) SessionUtils.getAttribute(LoanConstants.ADDITIONAL_FEES_LIST, request)).size());
        Assert.assertEquals(1, loanActionForm.getDefaultFees().size());

        Assert.assertEquals(loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount().toString(),
                loanActionForm.getLoanAmount());

        Assert.assertEquals(loanOffering.getEligibleInstallmentSameForAllLoan().getDefaultNoOfInstall().toString(),
                loanActionForm.getNoOfInstallments());
        Assert.assertEquals(loanOffering.getDefInterestRate().toString(), loanActionForm.getInterestRate());
        Assert.assertEquals(loanOffering.isIntDedDisbursement(), loanActionForm.isInterestDedAtDisbValue());
        Assert.assertEquals(loanOffering.getGracePeriodDuration().toString(), loanActionForm.getGracePeriodDuration());
        Assert.assertEquals(DateUtils.getCurrentDate(((UserContext) request.getSession().getAttribute("UserContext"))
                .getPreferredLocale()), loanActionForm.getDisbursementDate());

        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testSchedulePreview() throws Exception {
        goToPrdOfferingPage();
        actionPerform();
        goToLoanAccountInputPage();
        actionPerform();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("loanAmount", loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount()
                .toString());
        addRequestParameter("interestRate", loanOffering.getDefInterestRate().toString());
        addRequestParameter("noOfInstallments", loanOffering.getEligibleInstallmentSameForAllLoan()
                .getDefaultNoOfInstall().toString());
        addRequestParameter("disbursementDate", DateUtils.getCurrentDate(((UserContext) request.getSession()
                .getAttribute("UserContext")).getPreferredLocale()));
        addRequestParameter("gracePeriodDuration", "1");
        addRequestParameter("businessActivityId", "1");
        addRequestParameter("loanOfferingFund", "1");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(LoanConstants.CUSTOM_FIELDS, request);
        int i = 0;
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            addRequestParameter("customField[" + i + "].fieldId", customFieldDef.getFieldId().toString());
            addRequestParameter("customField[" + i + "].fieldValue", "11");
            i++;
        }
        addRequestParameter("method", "schedulePreview");
        performNoErrors();
        verifyForward(ActionForwards.schedulePreview_success.toString());

        LoanBO loan = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Assert.assertNotNull(loan);

        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testSchedulePreviewWithoutData() throws Exception {
        // make sure that everything needed to resolve hidden/mandatory fields is loaded
        EntityMasterData.getInstance().init();        
        FieldConfig fieldConfig = FieldConfig.getInstance();
        fieldConfig.init();
        getActionServlet().getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
                fieldConfig.getEntityMandatoryFieldMap());
        
        schedulePreviewPageParams.put("loanAmount", "");
        schedulePreviewPageParams.put("interestRate", "");
        schedulePreviewPageParams.put("noOfInstallments", "");
        schedulePreviewPageParams.put("gracePeriodDuration", "");
        schedulePreviewPageParams.put("disbursementDate", "");
        schedulePreviewPageParams.put("loanOfferingFund", "");
        jumpToSchedulePreview();
        actionPerform();
        verifyActionErrors(new String[] { "errors.defMinMax", "errors.defMinMax", "errors.defMinMax",
                "errors.validandmandatory", "errors.graceper", "errors.generic", "errors.generic",
                "errors.individualsourceoffundfield" });
        verifyInputForward();
    }

    public void testSchedulePreviewWithDataWithNoGracePer() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        // request.getSession().setAttribute(Constants.BUSINESS_KEY, group);
        // SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
        // request);
        // SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
        // new ArrayList<FundBO>(), request);
        // SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, group,
        // request);
        schedulePreviewPageParams.put("gracePeriodDuration", "");
        jumpToSchedulePreview();
        actionPerform();
        verifyActionErrors(new String[] { "errors.graceper" });
        verifyInputForward();

        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    private void jumpToSchedulePreview() {
        goToPrdOfferingPage();
        actionPerform();
        goToLoanAccountInputPage();
        actionPerform();
        goToSchedulePreviewPage();
    }

    public void testSchedulePreviewWithData() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, group);
        jumpToSchedulePreview();
        performNoErrors();
        verifyForward(ActionForwards.schedulePreview_success.toString());
    }

    public void testSchedulePreviewWithLoanOfferingFundsData() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, group);
        LoanOfferingBO loanOffering = getCompleteLoanOfferingObject();
        initPageParams(loanOffering);
        jumpToSchedulePreview();
        performNoErrors();
        verifyForward(ActionForwards.schedulePreview_success.toString());
        TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, loanOffering
                .getPrdOfferingId()));
    }

    /*
     * TODO: turn back on when IntersetDeductedAtDisbursement is re-enabled
     * 
     * 
     * public void testSchedulePreviewWithDataForIntDedAtDisb() throws
     * Exception { request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
     * schedulePreviewPageParams.put("intDedDisbursement", "1");
     * jumpToSchedulePreview(); performNoErrors();
     * verifyForward(ActionForwards.schedulePreview_success.toString());
     * LoanAccountActionForm actionForm = (LoanAccountActionForm) request
     * .getSession
     * ().getAttribute("loanAccountActionForm");Assert.assertEquals("0",
     * actionForm.getGracePeriodDuration()); }
     */
    public void testCreate() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        jumpToSchedulePreview();
        actionPerform();
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "create");
        addRequestParameter("stateSelected", "1");

        performNoErrors();
        verifyForward(ActionForwards.create_success.toString());
        LoanAccountActionForm actionForm = (LoanAccountActionForm) request.getSession().getAttribute(
                "loanAccountActionForm");
        LoanBO loan = TestObjectFactory.getObject(LoanBO.class, new Integer(actionForm.getAccountId()).intValue());
        Assert.assertEquals(loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount().toString(), loan
                .getLoanAmount().toString());

        Assert.assertEquals(loanOffering.getDefInterestRate(), loan.getInterestRate());
        Assert.assertEquals(loanOffering.getEligibleInstallmentSameForAllLoan().getDefaultNoOfInstall(), loan
                .getNoOfInstallments());
        // Assert.assertEquals(new java.sql.Date(DateUtils
        // .getCurrentDateWithoutTimeStamp().getTime()).toString(),
        // loan.getDisbursementDate().toString());
        Assert.assertEquals(new LocalDate(), new LocalDate(loan.getDisbursementDate().getTime()));

        Assert.assertEquals(Short.valueOf("1"), loan.getGracePeriodDuration());
        Assert.assertEquals(Short.valueOf("1"), loan.getAccountState().getId());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        TestObjectFactory.cleanUp(loan);
    }

    public void testManage() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        Date startDate = new Date(System.currentTimeMillis());
        accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
        LoanBO loan = (LoanBO) accountBO;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "manage");
        addRequestParameter("customerId", accountBO.getCustomer().getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(LoanConstants.LOANOFFERING, request));
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.COLLATERAL_TYPES, request));
        Assert.assertNotNull(SessionUtils.getAttribute(MasterConstants.BUSINESS_ACTIVITIES, request));
        Assert.assertNotNull(SessionUtils.getAttribute(LoanConstants.CUSTOM_FIELDS, request));
    }

    /*
     * this test appears to be confirming that an unauthenticated user gets a
     * "page expired" exception, but the catch() block doesn't appear to be
     * reached.
     */
    public void testManageWithoutFlow() throws Exception {
        try {
            request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
            Date startDate = new Date(System.currentTimeMillis());
            accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
            LoanBO loan = (LoanBO) accountBO;
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
            setRequestPathInfo("/loanAccountAction.do");
            addRequestParameter("method", "manage");
            addRequestParameter("customerId", accountBO.getCustomer().getCustomerId().toString());
            actionPerform();
            // I'd normally expect to see a call to JUnit 3's fail() here
        } catch (PageExpiredException pe) {
            Assert.assertTrue(true);
            Assert.assertEquals(ExceptionConstants.PAGEEXPIREDEXCEPTION, pe.getKey());
        }

    }

    /*
     * public void testManagePreview() throws ServiceException,
     * SystemException, ApplicationException {
     * request.setAttribute(Constants.CURRENTFLOWKEY, flowKey); Date startDate =
     * new Date(System.currentTimeMillis()); accountBO =
     * getLoanAccount(AccountState.LOANACC_APPROVED, startDate, 1); ((LoanBO)
     * accountBO).setBusinessActivityId(1); accountBO.update();
     * StaticHibernateUtil.commitTransaction(); LoanBO loan = (LoanBO)
     * accountBO; LoanOfferingBO loanOffering = loan.getLoanOffering();
     * loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
     * SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
     * 
     * setRequestPathInfo("/loanAccountAction.do");
     * addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
     * .getAttribute(Constants.CURRENTFLOWKEY)); addRequestParameter("method",
     * "manage"); actionPerform();
     * 
     * setRequestPathInfo("/loanAccountAction.do");
     * addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
     * .getAttribute(Constants.CURRENTFLOWKEY)); addRequestParameter("method",
     * "managePreview"); addRequestParameter("loanAmount",
     * loan.getLoanOffering() .getDefaultLoanAmount().toString());
     * addRequestParameter("interestRate", loan.getLoanOffering()
     * .getDefInterestRate().toString());
     * addRequestParameter("noOfInstallments", loan.getLoanOffering()
     * .getDefNoInstallments().toString());
     * addRequestParameter("disbursementDate", DateUtils
     * .getCurrentDate(((UserContext) request.getSession()
     * .getAttribute("UserContext")).getPreferredLocale()));
     * addRequestParameter("gracePeriodDuration", "1");
     * addRequestParameter("intDedDisbursement", "1"); actionPerform();
     * verifyForward(ActionForwards.managepreview_success.toString());
     * 
     * Assert.assertNotNull(SessionUtils.getAttribute(
     * MasterConstants.COLLATERAL_TYPE_NAME, request));
     * Assert.assertNotNull(SessionUtils.getAttribute(
     * MasterConstants.BUSINESS_ACTIVITIE_NAME, request)); }
     */

    // Commented because not able to inject LoanBO as a dependency into the
    // action servlet
    // public void testManagePrevious() {
    // request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    // LoanBO loanMock = createMock(LoanBO.class);
    // request.getSession().setAttribute(Constants.BUSINESS_KEY, loanMock);
    // setRequestPathInfo("/loanAccountAction.do");
    // addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
    // .getAttribute(Constants.CURRENTFLOWKEY));
    // addRequestParameter("method", "managePrevious");
    // actionPerform();
    // verifyForward(ActionForwards.manageprevious_success.toString());
    // }
    public void testCancel() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward(ActionForwards.loan_detail_page.toString());
    }

    public void testUpdateSuccessWithoutRegeneratingNewRepaymentSchedule() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        accountBO = getLoanAccount();
        LoanBO loan = (LoanBO) accountBO;
        LoanOfferingBO loanOffering = loan.getLoanOffering();
        Date firstInstallmentDate = loan.getDetailsOfNextInstallment().getActionDate();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        Date newDate = DateUtils.addWeeks(loan.getDisbursementDate(), 1);
        Date originalDate = loan.getDisbursementDate();
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "update");
        addRequestParameter("loanAmount", loanOffering.getEligibleLoanAmountSameForAllLoan().getDefaultLoanAmount()
                .toString());
        addRequestParameter("interestRate", loan.getLoanOffering().getDefInterestRate().toString());
        addRequestParameter("noOfInstallments", loanOffering.getEligibleInstallmentSameForAllLoan()
                .getDefaultNoOfInstall().toString());
        addRequestParameter("disbursementDate", DateUtils.format(newDate));
        addRequestParameter("businessActivityId", "1");
        addRequestParameter("intDedDisbursement", "0");
        addRequestParameter("gracePeriodDuration", "1");
        addRequestParameter("collateralNote", "test");
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());

        loan = TestObjectFactory.getObject(LoanBO.class, loan.getAccountId());
        Assert.assertEquals("test", loan.getCollateralNote());
        Assert.assertEquals(300.0, loan.getLoanAmount().getAmountDoubleValue(), DELTA);
        Assert.assertFalse(loan.isInterestDeductedAtDisbursement());
        Assert.assertEquals(1, loan.getGracePeriodDuration().intValue());
        Assert.assertEquals(DateUtils.format(originalDate), DateUtils.getUserLocaleDate(TestObjectFactory.getContext()
                .getPreferredLocale(), DateUtils.toDatabaseFormat(loan.getDisbursementDate())));
        Assert.assertEquals(firstInstallmentDate, loan.getAccountActionDate(Short.valueOf("1")).getActionDate());
    }

    public void testLoanAccountFromLastLoan() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        LoanOfferingBO loanOffering = getLoanOfferingFromLastLoan("lastLoan", "lamt", ApplicableTo.GROUPS, WEEKLY,
                EVERY_WEEK);
        initPageParams(loanOffering);
        jumpToSchedulePreview();
        actionPerform();
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "create");
        addRequestParameter("stateSelected", "1");
        actionPerform();
        LoanAccountActionForm actionForm = (LoanAccountActionForm) request.getSession().getAttribute(
                "loanAccountActionForm");
        LoanBO loan = TestObjectFactory.getObject(LoanBO.class, new Integer(actionForm.getAccountId()).intValue());

        Assert.assertEquals(new Money(getCurrency(), "200"), loan.getLoanAmount());
        Assert.assertEquals(new Short("20"), loan.getNoOfInstallments());
        TestObjectFactory.cleanUp(loan);
        TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, loanOffering
                .getPrdOfferingId()));
        accountBO = null;
    }

    public void testLoanAccountFromLoanCycle() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        LoanOfferingBO loanOffering = getLoanOfferingFromLoanCycle("loanCycle", "lcyc", ApplicableTo.GROUPS, WEEKLY,
                EVERY_WEEK);
        initPageParams(loanOffering);
        jumpToSchedulePreview();
        actionPerform();
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "create");
        addRequestParameter("stateSelected", "1");
        actionPerform();
        LoanAccountActionForm actionForm = (LoanAccountActionForm) request.getSession().getAttribute(
                "loanAccountActionForm");
        LoanBO loan = TestObjectFactory.getObject(LoanBO.class, new Integer(actionForm.getAccountId()).intValue());
        Assert.assertEquals(new Money(getCurrency(), "2000"), loan.getLoanAmount());
        Assert.assertEquals(new Short("20"), loan.getNoOfInstallments());
        TestObjectFactory.cleanUp(loan);
        TestObjectFactory.removeObject((LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, loanOffering
                .getPrdOfferingId()));
    }

    private void modifyActionDateForFirstInstallment() throws Exception {
        LoanScheduleEntity installment = (LoanScheduleEntity) accountBO.getAccountActionDate((short) 1);
        LoanBOTestUtils.modifyData(installment, new Money(getCurrency(), "5.0"), installment.getPenaltyPaid(),
                installment.getMiscPenalty(), installment.getMiscPenaltyPaid(), installment.getMiscFee(), installment
                        .getMiscFeePaid(), new Money(getCurrency(), "20.0"), installment.getPrincipalPaid(), new Money(
                        getCurrency(), "10.0"), installment.getInterestPaid());
        LoanBOTestUtils.setActionDate(installment, offSetCurrentDate(1));
        accountBO = saveAndFetch(accountBO);
    }

    private java.sql.Date offSetCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    private AccountBO saveAndFetch(AccountBO account) throws Exception {
        AccountPersistence accountPersistence = new AccountPersistence();
        TestObjectFactory.updateObject(account);
        return accountPersistence.getAccount(account.getAccountId());
    }

    private AccountNotesEntity createAccountNotes(String comment, AccountBO account) {
        AccountNotesEntity accountNotes = new AccountNotesEntity(new java.sql.Date(System.currentTimeMillis()),
                comment, TestObjectFactory.getPersonnel(userContext.getId()), account);
        return accountNotes;
    }

    private void addNotes() {
        accountBO.addAccountNotes(createAccountNotes("Notes1", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes2", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes3", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes4", accountBO));
        TestObjectFactory.updateObject(accountBO);
        accountBO.addAccountNotes(createAccountNotes("Notes5", accountBO));
        TestObjectFactory.updateObject(accountBO);
    }

    private void disburseLoan(Date startDate) throws Exception {
        ((LoanBO) accountBO).disburseLoan("1234", startDate, Short.valueOf("1"), accountBO.getPersonnel(), startDate,
                Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
    }

    private AccountBO getLoanAccount(AccountState state, Date startDate, int disbursalType) {
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        accountBO = TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOffering, disbursalType);
        LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO, TestObjectFactory.getPersonnel(userContext
                .getId()), "testing", new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(
                getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(
                getCurrency(), "100"), new Money(getCurrency(), "100"), new Money(getCurrency(), "100"), startDate);
        ((LoanBO) accountBO).addLoanActivity(loanActivity);
        addNotes();
        TestObjectFactory.updateObject(accountBO);
        return accountBO;
    }

    private LoanOfferingBO getLoanOfferingFromLastLoan(String name, String shortName, ApplicableTo applicableTo,
            RecurrenceType meetingFrequency, short recurAfter) throws SystemException, ApplicationException {
        LoanPrdActionForm loanPrdActionForm = new LoanPrdActionForm();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(meetingFrequency,
                recurAfter, CUSTOMER_MEETING, MONDAY));
        Date currentDate = new Date(System.currentTimeMillis());
        loanPrdActionForm = LoanOfferingTestUtils.populateNoOfInstallFromLastLoanAmount("2", new Integer("0"),
                new Integer("100"), new Integer("101"), new Integer("200"), new Integer("201"), new Integer("300"),
                new Integer("301"), new Integer("400"), new Integer("401"), new Integer("500"), new Integer("501"),
                new Integer("600"), "10", "30", "20", "20", "40", "30", "30", "50", "40", "40", "60", "50", "50", "70",
                "60", "60", "80", "70", LoanOfferingTestUtils.populateLoanAmountFromLastLoanAmount("2",
                        new Integer("0"), new Integer("100"), new Integer("101"), new Integer("200"),
                        new Integer("201"), new Integer("300"), new Integer("301"), new Integer("400"), new Integer(
                                "401"), new Integer("500"), new Integer("501"), new Integer("600"), new Double("100"),
                        new Double("300"), new Double("200"), new Double("200"), new Double("400"), new Double("300"),
                        new Double("300"), new Double("500"), new Double("400"), new Double("400"), new Double("600"),
                        new Double("500"), new Double("500"), new Double("700"), new Double("600"), new Double("600"),
                        new Double("800"), new Double("700"), loanPrdActionForm));
        return TestObjectFactory.createLoanOfferingFromLastLoan(name, shortName, applicableTo, currentDate,
                PrdStatus.LOAN_ACTIVE, 1.2, InterestType.FLAT, false, false, meeting, GraceType.GRACEONALLREPAYMENTS,
                loanPrdActionForm);
    }

    private LoanOfferingBO getLoanOfferingFromLoanCycle(String name, String shortName, ApplicableTo applicableTo,
            RecurrenceType meetingFrequency, short recurAfter) throws SystemException, ApplicationException {
        LoanPrdActionForm loanPrdActionForm = new LoanPrdActionForm();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(meetingFrequency,
                recurAfter, CUSTOMER_MEETING, MONDAY));
        Date currentDate = new Date(System.currentTimeMillis());
        loanPrdActionForm = LoanOfferingTestUtils.populateNoOfInstallFromLoanCycle("3", "10", "30", "20", "20", "40",
                "30", "30", "50", "40", "40", "60", "50", "50", "70", "60", "60", "80", "70", LoanOfferingTestUtils
                        .populateLoanAmountFromLoanCycle("3", new Double("1000"), new Double("3000"),
                                new Double("2000"), new Double("2000"), new Double("4000"), new Double("3000"),
                                new Double("3000"), new Double("5000"), new Double("4000"), new Double("4000"),
                                new Double("6000"), new Double("5000"), new Double("5000"), new Double("7000"),
                                new Double("6000"), new Double("6000"), new Double("8000"), new Double("7000"),
                                loanPrdActionForm));
        return TestObjectFactory.createLoanOfferingFromLastLoan(name, shortName, applicableTo, currentDate,
                PrdStatus.LOAN_ACTIVE, 1.2, InterestType.FLAT, false, false, meeting, GraceType.GRACEONALLREPAYMENTS,
                loanPrdActionForm);
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

    private java.sql.Date offSetDate(Date date, int noOfDays) {
        Calendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTimeInMillis(date.getTime());
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        dateCalendar = new GregorianCalendar(year, month, day + noOfDays);
        return new java.sql.Date(dateCalendar.getTime().getTime());
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, center.getCustomerMeeting()
                .getMeeting());
        return TestObjectFactory.createLoanAccount("42423142341", client, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private LoanOfferingBO getCompleteLoanOfferingObject() throws Exception {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.GROUPS);
        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        GLCodeEntity principalglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.BANK_ACCOUNT_ONE);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.BANK_ACCOUNT_ONE);
        ProductCategoryBO productCategory = TestObjectFactory.getLoanPrdCategory();
        InterestTypesEntity interestTypes = new InterestTypesEntity(InterestType.FLAT);
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        Date startDate = offSetCurrentDate(0);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        List<FundBO> funds = new ArrayList<FundBO>();
        FundBO fundBO = (FundBO) StaticHibernateUtil.getSessionTL().get(FundBO.class, Short.valueOf("2"));
        funds.add(fundBO);
        LoanOfferingBO loanOfferingBO = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, null, null, gracePeriodType, (short) 2, interestTypes,
                new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"),
                12.0, 2.0, 3.0, (short) 20, (short) 11, (short) 17, false, false, false, funds, fees, frequency,
                principalglCodeEntity, intglCodeEntity);
        loanOfferingBO.save();
        return loanOfferingBO;
    }

    private LoanPrdActionForm populateLoanAmountSameForAllLoan(String loanAmtCalcType, Double maxLoanAmount,
            Double minLoanAmount, Double defLoanAmount, LoanPrdActionForm loanPrdActionForm) {
        if (loanAmtCalcType.equals("1")) {
            loanPrdActionForm.setLoanAmtCalcType(loanAmtCalcType);
            loanPrdActionForm.setMaxLoanAmount(maxLoanAmount.toString());
            loanPrdActionForm.setMinLoanAmount(minLoanAmount.toString());
            loanPrdActionForm.setDefaultLoanAmount(defLoanAmount.toString());
        }
        return loanPrdActionForm;
    }

    private LoanPrdActionForm populateNoOfInstallSameForAllLoan(String calcInstallmentType, String maxNoOfInstall,
            String minNoOfInstall, String defNoOfInstall, LoanPrdActionForm loanPrdActionForm) {
        if (calcInstallmentType.equals("1")) {
            loanPrdActionForm.setCalcInstallmentType(calcInstallmentType);
            loanPrdActionForm.setMaxNoInstallments(maxNoOfInstall);
            loanPrdActionForm.setMinNoInstallments(minNoOfInstall);
            loanPrdActionForm.setDefNoInstallments(defNoOfInstall);

        }
        return loanPrdActionForm;
    }

    public void makeRepaymentForLastLoanAmount() throws Exception {
        flowKey1 = createFlow(request, RepayLoanAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey1);
        accountBO = getLoanAccountFromLastLoanAmount();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        setRequestPathInfo("/repayLoanAction");
        addRequestParameter("method", "makeRepayment");
        addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
        addRequestParameter("paymentTypeId", "1");
        actionPerform();
    }

    private AccountBO getLoanAccountFromLastLoanAmount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private void goToLoanAccountInputPage() {
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        Set<Entry<String, String>> entrySet = prdOfferingPageParams.entrySet();
        for (Entry<String, String> entry : entrySet) {
            addRequestParameter(entry.getKey(), entry.getValue());
        }
    }

    private void goToPrdOfferingPage() {
        request.getSession().setAttribute(Constants.BUSINESS_KEY, group);
        setRequestPathInfo("/loanAccountAction.do");
        addRequestParameter("method", "getPrdOfferings");
        addRequestParameter("customerId", group.getCustomerId().toString());
    }

    private void goToSchedulePreviewPage() {
        setRequestPathInfo("/loanAccountAction.do");
        Set<Entry<String, String>> entrySet = schedulePreviewPageParams.entrySet();
        for (Entry<String, String> entry : entrySet) {
            addRequestParameter(entry.getKey(), entry.getValue());
        }
        addRequestParameter("method", "schedulePreview");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));

    }

    private void initPageParams() throws InvalidDateException {
        initPageParams(loanOffering);
    }

    private void initPageParams(LoanOfferingBO loanOffering) throws InvalidDateException {
        initPrdOfferingPageParams(loanOffering);
        initSchedulePreviewPageParams(loanOffering);
    }

    private void initSchedulePreviewPageParams(LoanOfferingBO loanOffering) throws InvalidDateException {
        schedulePreviewPageParams = new HashMap<String, String>();
        schedulePreviewPageParams.put("loanAmount", loanOffering.eligibleLoanAmount(
                group.getMaxLoanAmount(loanOffering), group.getMaxLoanCycleForProduct(loanOffering))
                .getDefaultLoanAmount().toString());
        schedulePreviewPageParams.put("interestRate", loanOffering.getMaxInterestRate().toString());
        schedulePreviewPageParams.put("noOfInstallments", loanOffering.eligibleNoOfInstall(
                group.getMaxLoanAmount(loanOffering), group.getMaxLoanCycleForProduct(loanOffering))
                .getDefaultNoOfInstall().toString());
        schedulePreviewPageParams.put("disbursementDate", DateUtils.getCurrentDate(((UserContext) request.getSession()
                .getAttribute("UserContext")).getPreferredLocale()));
        schedulePreviewPageParams.put("gracePeriodDuration", "1");
        schedulePreviewPageParams.put("businessActivityId", "1");
        schedulePreviewPageParams.put("loanOfferingFund", "1");

    }

    private void initPrdOfferingPageParams(LoanOfferingBO loanOffering) {
        prdOfferingPageParams.put("prdOfferingId", loanOffering.getPrdOfferingId().toString());
    }

    public void testShouldGetGlimSpecificAttributes() throws Exception {
        customerMock = createMock(CustomerBO.class);
        expect(customerMock.isGroup()).andReturn(true);
        expect(customerMock.getCustomerId()).andReturn(1);
        LoanBusinessService loanBusinessServiceMock = createMock(LoanBusinessService.class);
        expect(loanBusinessServiceMock.getAllChildrenForParentGlobalAccountNum("1")).andReturn(Collections.EMPTY_LIST);
        ConfigurationBusinessService configurationBusinessServiceMock = createMock(ConfigurationBusinessService.class);
        expect(configurationBusinessServiceMock.isGlimEnabled()).andReturn(true);
        LoanAccountAction loanAccountAction = new LoanAccountAction(loanBusinessServiceMock,
                configurationBusinessServiceMock, new GlimLoanUpdater());
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);

        LoanAccountAction.GlimSessionAttributes glimSessionAttributes = new LoanAccountAction.GlimSessionAttributes(
                LoanConstants.GLIM_ENABLED_VALUE, Collections.EMPTY_LIST, LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);
        replay(loanBusinessServiceMock, configurationBusinessServiceMock, customerMock, requestMock);

        Assert.assertEquals(glimSessionAttributes, loanAccountAction.getGlimSpecificPropertiesToSet(
                new LoanAccountActionForm(), "1", customerMock, new ArrayList<ValueListElement>()));
        verify(loanBusinessServiceMock, configurationBusinessServiceMock, customerMock, requestMock);
    }

    public void testShouldNotSetAnyGlimSpecificAttributesIfGlimDisabled() throws Exception {
        loanBusinessServiceMock = createMock(LoanBusinessService.class);
        customerMock = createMock(CustomerBO.class);
        expect(customerMock.isGroup()).andReturn(false);
        configurationBusinessServiceMock = createMock(ConfigurationBusinessService.class);
        expect(configurationBusinessServiceMock.isGlimEnabled()).andReturn(true);
        LoanAccountAction loanAccountAction = new LoanAccountAction(loanBusinessServiceMock,
                configurationBusinessServiceMock, new GlimLoanUpdater());
        requestMock = createMock(HttpServletRequest.class);
        replay(loanBusinessServiceMock, configurationBusinessServiceMock, customerMock, requestMock);
        LoanAccountAction.GlimSessionAttributes glimSessionAttributes = new LoanAccountAction.GlimSessionAttributes(
                LoanConstants.GLIM_DISABLED_VALUE);
        Assert.assertEquals(glimSessionAttributes, loanAccountAction.getGlimSpecificPropertiesToSet(
                new LoanAccountActionForm(), "1", customerMock, new ArrayList<ValueListElement>()));
        verify(loanBusinessServiceMock, configurationBusinessServiceMock, customerMock, requestMock);
    }

    public void testShouldPopulateClientDetailsFromLoan() throws Exception {

        ClientBO clientMock1 = createMock(ClientBO.class);
        expect(clientMock1.getCustomerId()).andReturn(1).anyTimes();
        expect(clientMock1.getDisplayName()).andReturn("client 1");

        ClientBO clientMock2 = createMock(ClientBO.class);
        expect(clientMock2.getCustomerId()).andReturn(2).anyTimes();
        expect(clientMock2.getDisplayName()).andReturn("client 2");

        LoanBO loanMock = createMock(LoanBO.class);
        expect(loanMock.getCustomer()).andReturn(clientMock1).anyTimes();
        expect(loanMock.getBusinessActivityId()).andReturn(3);
        expect(loanMock.getLoanAmount()).andReturn(new Money(getCurrency(), "100")).anyTimes();

        LoanAccountDetailsViewHelper clientDetails1 = new LoanAccountDetailsViewHelper();
        clientDetails1.setClientId("1");
        clientDetails1.setClientName("client 1");
        clientDetails1.setBusinessActivity("3");
        clientDetails1.setLoanAmount("100.0");

        LoanAccountDetailsViewHelper clientDetails2 = new LoanAccountDetailsViewHelper();
        clientDetails2.setClientId("2");
        clientDetails2.setClientName("client 2");

        replay(clientMock1, clientMock2, loanMock);

        List<LoanAccountDetailsViewHelper> clientDetails = new LoanAccountAction().populateClientDetailsFromLoan(Arrays
                .asList(clientMock1, clientMock2), Arrays.asList(loanMock), new ArrayList<ValueListElement>());
        Assert.assertEquals(Arrays.asList(clientDetails1, clientDetails2), clientDetails);
        verify(clientMock1, clientMock2, loanMock);
    }

}
