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

package org.mifos.accounts.loan.struts.action;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.ADDITIONAL_FEES_LIST;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.ADMINISTRATIVE_DOCUMENT_IS_ENABLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.CLIENT_LIST;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.CUSTOM_FIELDS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOANACCOUNTOWNER;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOANFUNDS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOANOFFERING;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOAN_ACCOUNT_OWNER_IS_A_GROUP;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOAN_ALL_ACTIVITY_VIEW;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.NEXTMEETING_DATE;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.NOTES;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.PROPOSED_DISBURSAL_DATE;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECENTACCOUNTACTIVITIES;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECURRENCEID;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECURRENCENAME;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.REPAYMENTSCHEDULEINSTALLMENTS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.STATUS_HISTORY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.TOTAL_AMOUNT_OVERDUE;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.VIEW_UPCOMING_INSTALLMENT_DETAILS;
import static org.mifos.accounts.loan.util.helpers.RequestConstants.PERSPECTIVE;
import static org.mifos.framework.util.helpers.Constants.BUSINESS_KEY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.InstallmentDetailsDto;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundPersistence;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.MaxMinInterestRate;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.LoanService;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.LoanAccountDetailsDto;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.struts.action.AccountAppAction;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.LoanCreationLoanDetailsDto;
import org.mifos.application.servicefacade.LoanCreationLoanScheduleDetailsDto;
import org.mifos.application.servicefacade.LoanCreationPreviewDto;
import org.mifos.application.servicefacade.LoanCreationProductDetailsDto;
import org.mifos.application.servicefacade.LoanCreationResultDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.reports.admindocuments.persistence.AdminDocAccStateMixPersistence;
import org.mifos.reports.admindocuments.persistence.AdminDocumentPersistence;
import org.mifos.reports.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

/**
 * Creation and management of loan accounts.
 * <p>
 * The "repayment day" form fields provided by the frontend and manipulated in the form by this class are somewhat
 * confusing. Here's an attempt to add some clarity.
 * <p>
 * <h3>required for both weekly and monthly recurrence</h3>
 * <ul>
 * <li>frequency
 * <ul>
 * <li>ie: "weekly", "monthly" (corresponds to values in {@link RecurrenceType} ).</li>
 * </ul>
 * </li>
 * </ul>
 *
 * <h3>required for monthly recurrence</h3>
 * <ul>
 * <li>monthType=1 : "12th day of every 1 month"
 * <ul>
 * <li>monthDay
 * <ul>
 * <li>Xst/Xnd/Xth of every month</li>
 * </ul>
 * </li>
 * <li>dayRecurMonth
 * <ul>
 * <li>of every X months</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * <li>monthType=2 : "First Monday of every 1 month"
 * <ul>
 * <li>monthRank
 * <ul>
 * <li>First, Second, etc. (ordinal)</li>
 * </ul>
 * </li>
 * <li>monthWeek
 * <ul>
 * <li><strong>day</strong>, ie, Monday, Tuesday, etc. of the week that repayments should be made. Really.</li>
 * </ul>
 * </li>
 * <li>recurMonth
 * <ul>
 * <li>every X months</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 *
 * <h3>required for weekly recurrence</h3>
 * <ul>
 * <li>(only one kind of weekly schedule)
 * <ul>
 * <li>weekDay
 * <ul>
 * <li>day of the week repayments should be made</li>
 * </ul>
 * </li>
 * <li>recurWeek
 * <ul>
 * <li>every X weeks</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 */
public class LoanAccountAction extends AccountAppAction {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    private final LoanBusinessService loanBusinessService;
    private final FeeBusinessService feeService;
    private final LoanPrdBusinessService loanPrdBusinessService;
    private final ClientBusinessService clientBusinessService;
    private final MasterDataService masterDataService;
    private final ConfigurationPersistence configurationPersistence;
    private final ConfigurationBusinessService configService;
    private final GlimLoanUpdater glimLoanUpdater;
    private LoanProductService loanProductService;

    public static final String CUSTOMER_ID = "customerId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String GLOBAL_ACCOUNT_NUM = "globalAccountNum";

    public LoanAccountAction() throws Exception {
        this(new ConfigurationBusinessService(), new LoanBusinessService(), new GlimLoanUpdater(),
                new FeeBusinessService(), new LoanPrdBusinessService(), new ClientBusinessService(),
                new MasterDataService(), new ConfigurationPersistence(), null, new AccountBusinessService(),
                new LoanService());
        setLoanService(new LoanProductService(this.loanPrdBusinessService, this.feeService));
    }

    public LoanAccountAction(final ConfigurationBusinessService configService,
            final LoanBusinessService loanBusinessService, final GlimLoanUpdater glimLoanUpdater,
            final FeeBusinessService feeService, final LoanPrdBusinessService loanPrdBusinessService,
            final ClientBusinessService clientBusinessService, final MasterDataService masterDataService,
            final ConfigurationPersistence configurationPersistence, final LoanProductService loanProductService,
            final AccountBusinessService accountBusinessService, final LoanService loanService) {
        super(accountBusinessService);

        this.configService = configService;
        this.loanBusinessService = loanBusinessService;
        this.glimLoanUpdater = glimLoanUpdater;
        this.feeService = feeService;
        this.loanPrdBusinessService = loanPrdBusinessService;
        this.clientBusinessService = clientBusinessService;
        this.masterDataService = masterDataService;
        this.configurationPersistence = configurationPersistence;
        this.loanProductService = loanProductService;
    }

    /**
     * @deprecated - test only
     */
    @Deprecated
    LoanAccountAction(final LoanBusinessService loanBusinessService, final ConfigurationBusinessService configService,
            final GlimLoanUpdater glimLoanUpdater) {
        this(configService, loanBusinessService, glimLoanUpdater);
    }

    /**
     * test only constructor
     */
    @Deprecated
    private LoanAccountAction(final ConfigurationBusinessService configService,
            final LoanBusinessService loanBusinessService, final GlimLoanUpdater glimLoanUpdater) {
        this(configService, loanBusinessService, glimLoanUpdater, new FeeBusinessService(),
                new LoanPrdBusinessService(), new ClientBusinessService(), new MasterDataService(),
                new ConfigurationPersistence(), null, new AccountBusinessService(), new LoanService());
        setLoanService(new LoanProductService(this.loanPrdBusinessService, this.feeService));
    }

    @Override
    protected BusinessService getService() {
        return loanBusinessService;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("loanAccountAction");
        security.allow("getAllActivity", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("getLoanRepaymentSchedule", SecurityConstants.VIEW);
        security.allow("viewStatusHistory", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.LOAN_UPDATE_LOAN);
        security.allow("managePreview", SecurityConstants.VIEW);
        security.allow("managePrevious", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.LOAN_UPDATE_LOAN);

        security.allow("getPrdOfferings", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("schedulePreview", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);

        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        security.allow("waiveChargeDue", SecurityConstants.VIEW);
        security.allow("forwardWaiveCharge", SecurityConstants.VIEW);
        security.allow("waiveChargeOverDue", SecurityConstants.VIEW);
        security.allow("redoLoanBegin", SecurityConstants.CAN_REDO_LOAN_DISPURSAL);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getPrdOfferings(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response)
            throws Exception {

        final LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;

        Integer customerId = loanActionForm.getCustomerIdValue();
        LoanCreationProductDetailsDto loanCreationProductDetailsDto = this.loanServiceFacade.retrieveGetProductDetailsForLoanAccountCreation(customerId);

        storeCollectionOnSessionForUseInJspPage(request, LoanConstants.LOANPRDOFFERINGS, loanCreationProductDetailsDto.getLoanProductDtos());
        storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOANACCOUNTOWNER, loanCreationProductDetailsDto.getCustomerDetailDto());
        storeObjectOnSessionForUseInJspPage(request, LoanConstants.PROPOSED_DISBURSAL_DATE, loanCreationProductDetailsDto.getNextMeetingDate());

        storeRedoLoanSettingOnRequestForUseInJspIfPerspectiveParamaterOnQueryString(request);

        if (loanCreationProductDetailsDto.isGlimEnabled()) {
            setGlimEnabledSessionAttributes(request, loanCreationProductDetailsDto.isGroup());
            request.setAttribute(METHODCALLED, "getPrdOfferings");

            if (loanCreationProductDetailsDto.isGroup()) {
                loanActionForm.setClientDetails(loanCreationProductDetailsDto.getClientDetails());

                LoanCreationGlimDto loanCreationGlimDto = loanCreationProductDetailsDto.getLoanCreationGlimDto();
                storeCollectionOnSessionForUseInJspPage(request, MasterConstants.BUSINESS_ACTIVITIES, loanCreationGlimDto.getLoanPurposes());
                storeCollectionOnSessionForUseInJspPage(request, LoanConstants.CLIENT_LIST, loanCreationGlimDto.getActiveClientsOfGroup());
                storeObjectOnSessionForUseInJspPage(request, "clientListSize", loanCreationGlimDto.getActiveClientsOfGroup().size());
            }
        }

        handleRepaymentsIndependentOfMeetingIfConfigured(request, loanActionForm, loanCreationProductDetailsDto.getRecurMonth());

        return mapping.findForward(ActionForwards.getPrdOfferigs_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        loanActionForm.clearDetailsForLoan();

        UserContext userContext = getUserContext(request);
        Integer customerId = loanActionForm.getCustomerIdValue();
        Short productId = loanActionForm.getPrdOfferingIdValue();

        LoanCreationLoanDetailsDto loanCreationDetailsDto = this.loanServiceFacade.retrieveLoanDetailsForLoanAccountCreation(userContext, customerId, productId);

        if (loanCreationDetailsDto.isRepaymentIndependentOfMeetingEnabled()) {
            setRepaymentDayFieldsOnFormForLoad(loanActionForm, loanCreationDetailsDto.getLoanOfferingMeetingDetail(), loanCreationDetailsDto.getCustomerMeetingDetail());
        }

        loanActionForm.setLoanAmountRange(loanCreationDetailsDto.getEligibleLoanAmount());
        loanActionForm.setLoanAmount(getDoubleStringForMoney(loanCreationDetailsDto.getEligibleLoanAmount().getDefaultLoanAmount(), loanCreationDetailsDto.getLoanOffering().getCurrency()));
        loanActionForm.setMaxInterestRate(loanCreationDetailsDto.getLoanOffering().getMaxInterestRate());
        loanActionForm.setMinInterestRate(loanCreationDetailsDto.getLoanOffering().getMinInterestRate());

        loanActionForm.setInstallmentRange(loanCreationDetailsDto.getEligibleNoOfInstall());
        loanActionForm.setNoOfInstallments(getStringValue(loanCreationDetailsDto.getEligibleNoOfInstall().getDefaultNoOfInstall()));
        loanActionForm.setInterestRate(getDoubleStringForInterest(loanCreationDetailsDto.getLoanOffering().getDefInterestRate()));
        loanActionForm.setIntDedDisbursement(getStringValue(loanCreationDetailsDto.getLoanOffering().isIntDedDisbursement()));
        loanActionForm.setGracePeriodDuration(getStringValue(loanCreationDetailsDto.getLoanOffering().getGracePeriodDuration()));
        loanActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), SessionUtils.getAttribute(PROPOSED_DISBURSAL_DATE, request).toString()));

        loanActionForm.setCustomFields(loanCreationDetailsDto.getCustomFields());
        loanActionForm.setDefaultFees(loanCreationDetailsDto.getDefaultFees());

        SessionUtils.setCollectionAttribute(LoanConstants.CUSTOM_FIELDS, loanCreationDetailsDto.getCustomFieldDefs(), request);
        SessionUtils.setCollectionAttribute(ADDITIONAL_FEES_LIST, loanCreationDetailsDto.getAdditionalFees(), request);
        SessionUtils.setCollectionAttribute(MasterConstants.COLLATERAL_TYPES, loanCreationDetailsDto.getCollateralTypes(), request);
        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES, loanCreationDetailsDto.getLoanPurposes(), request);
        SessionUtils.setAttribute(RECURRENCEID, loanCreationDetailsDto.getLoanProductRecurrenceType().getValue(), request);
        request.setAttribute(RECURRENCEID, loanCreationDetailsDto.getLoanProductRecurrenceType().getValue());

        SessionUtils.removeAttribute(LOANOFFERING, request);
        SessionUtils.setAttribute(LOANOFFERING, loanCreationDetailsDto.getLoanOffering(), request);
        SessionUtils.setCollectionAttribute(LOANFUNDS, loanCreationDetailsDto.getFunds(), request);

        storeRedoLoanSettingOnRequestForUseInJspIfPerspectiveParamaterOnQueryString(request);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    private void setRepaymentDayFieldsOnFormForLoad(LoanAccountActionForm loanActionForm,
            MeetingDetailsEntity meetingDetail, MeetingDetailsEntity customerMeetingDetail) {
        loanActionForm.setMonthDay("");
        loanActionForm.setMonthWeek("0");
        loanActionForm.setMonthRank("0");

        if (meetingDetail.getRecurrenceTypeEnum() == RecurrenceType.MONTHLY) {
            setMonthlySchedule(loanActionForm, meetingDetail);
        } else {
            setWeeklySchedule(loanActionForm, customerMeetingDetail);
        }
    }

    private void setRepaymentDayFieldsOnForm(LoanAccountActionForm loanActionForm, MeetingDetailsEntity meetingDetail) {
        loanActionForm.setMonthDay("");
        loanActionForm.setMonthWeek("0");
        loanActionForm.setMonthRank("0");

        if (meetingDetail.getRecurrenceTypeEnum() == RecurrenceType.MONTHLY) {
            setMonthlySchedule(loanActionForm, meetingDetail);
        } else {
            setWeeklySchedule(loanActionForm, meetingDetail);
        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward schedulePreview(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        UserContext userContext = getUserContext(request);
        CustomerDetailDto oldCustomer = (CustomerDetailDto) SessionUtils.getAttribute(LOANACCOUNTOWNER, request);

        DateTime disbursementDate = new DateTime(loanActionForm.getDisbursementDateValue(userContext.getPreferredLocale()));
        FundBO fund = getFund(request, loanActionForm.getLoanOfferingFundValue());

        LoanCreationLoanScheduleDetailsDto loanScheduleDetailsDto = null;
        if (isRedoOperation(request.getParameter(PERSPECTIVE))) {
            throw new UnsupportedOperationException("REDO was not implemented for now...");
        }

        loanScheduleDetailsDto = loanServiceFacade.retrieveScheduleDetailsForLoanCreation(userContext, oldCustomer.getCustomerId(), disbursementDate, fund, loanActionForm);

        if (loanScheduleDetailsDto.isGlimApplicable()) {
            setGlimEnabledSessionAttributes(request, loanScheduleDetailsDto.isGroup());
            loanActionForm.setLoanAmount(Double.toString(loanScheduleDetailsDto.getGlimLoanAmount()));
        }

        String perspective = request.getParameter(PERSPECTIVE);
        if (perspective != null) {
            request.setAttribute(PERSPECTIVE, request.getParameter(PERSPECTIVE));
        }

        SessionUtils.setCollectionAttribute(REPAYMENTSCHEDULEINSTALLMENTS, loanScheduleDetailsDto.getInstallments(), request);
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, loanScheduleDetailsDto.isLoanPendingApprovalDefined(), request);

        // FIXME - keithw - REDO LOAN - seems to be only for redoLoan functionality..
//        loanActionForm.initializeTransactionFields(loanScheduleDetailsDto.getPaymentDataBeans());

        return mapping.findForward(ActionForwards.schedulePreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws PageExpiredException,
            AccountException, ServiceException, PersistenceException, NumberFormatException, MeetingException,
            InvalidDateException {

        LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;

        String perspective = loanAccountForm.getPerspective();
        if (perspective != null) {
            if (perspective.equals(PERSPECTIVE_VALUE_REDO_LOAN)) {
                LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
                LoanBO loan = redoLoan(loanActionForm, request, SaveLoan.NO, new CustomerPersistence());
                SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
            }

            request.setAttribute(PERSPECTIVE, perspective);

            List<LoanAccountDetailsDto> accountDetails = loanAccountForm.getClientDetails();
            List<String> selectedClientIds = loanAccountForm.getClients();
            Integer customerId = loanAccountForm.getCustomerIdValue();
            List<BusinessActivityEntity> businessActEntity = retrieveLoanPurposesFromSession(request);

            LoanCreationPreviewDto loanPreviewDto = this.loanServiceFacade.previewLoanCreationDetails(customerId, accountDetails, selectedClientIds, businessActEntity);

            if (loanPreviewDto.isGlimEnabled()) {
                SessionUtils.setAttribute(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, 1, request);

                if (loanPreviewDto.isGroup()) {
                    SessionUtils.setAttribute(LOAN_ACCOUNT_OWNER_IS_A_GROUP, LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES, request);
                    SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanPreviewDto.getLoanAccountDetailsView(), request);
                }
            }
        }

        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @SuppressWarnings("unchecked")
    private List<BusinessActivityEntity> retrieveLoanPurposesFromSession(final HttpServletRequest request)
            throws PageExpiredException {
        return (List<BusinessActivityEntity>) SessionUtils.getAttribute(MasterConstants.BUSINESS_ACTIVITIES, request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getInstallmentDetails(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        Integer accountId = Integer.valueOf(request.getParameter(ACCOUNT_ID));
        LoanBO loanBO = loanBusinessService.getAccount(accountId);
        InstallmentDetailsDto viewUpcomingInstallmentDetails = getUpcomingInstallmentDetails(loanBO
                .getDetailsOfNextInstallment(), loanBO.getCurrency());
        InstallmentDetailsDto viewOverDueInstallmentDetails = getOverDueInstallmentDetails(loanBO
                .getDetailsOfInstallmentsInArrears(), loanBO.getCurrency());
        Money totalAmountDue = viewUpcomingInstallmentDetails.getSubTotal().add(
                viewOverDueInstallmentDetails.getSubTotal());
        SessionUtils.setAttribute(VIEW_UPCOMING_INSTALLMENT_DETAILS, viewUpcomingInstallmentDetails, request);
        SessionUtils.setAttribute(VIEW_OVERDUE_INSTALLMENT_DETAILS, viewOverDueInstallmentDetails, request);
        SessionUtils.setAttribute(TOTAL_AMOUNT_OVERDUE, totalAmountDue, request);

        SessionUtils.setAttribute(NEXTMEETING_DATE, loanBO.getNextMeetingDate(), request);
        loanBO = null;
        return mapping.findForward(VIEWINSTALLMENTDETAILS_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getAllActivity(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        logger.debug("In loanAccountAction::getAllActivity()");
        String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);
        SessionUtils.setCollectionAttribute(LOAN_ALL_ACTIVITY_VIEW, loanBusinessService
                .getAllActivityView(globalAccountNum), request);
        return mapping.findForward(MethodNameConstants.GETALLACTIVITY_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward forwardWaiveCharge(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        return mapping.findForward("waive" + request.getParameter("type") + "Charges_Success");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        String customerId = request.getParameter(CUSTOMER_ID);
        SessionUtils.removeAttribute(BUSINESS_KEY, request);
        LoanBO loanBO = loanBusinessService.findBySystemId(request.getParameter(GLOBAL_ACCOUNT_NUM));
        if (customerId == null) {
            customerId = loanBO.getCustomer().getCustomerId().toString();
        }
        CustomerBO customer = null;
        if (null != customerId) {
            customer = getCustomer(Integer.valueOf(customerId));
        }

        Integer loanIndividualMonitoringIsEnabled = configurationPersistence.getConfigurationKeyValueInteger(
                LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();

        if (null != loanIndividualMonitoringIsEnabled && loanIndividualMonitoringIsEnabled.intValue() != 0) {
            SessionUtils.setAttribute(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, loanIndividualMonitoringIsEnabled
                    .intValue(), request);
            if (customer.isGroup()) {
                SessionUtils.setAttribute(LOAN_ACCOUNT_OWNER_IS_A_GROUP, LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES,
                        request);
            }
        }

        setBusinessActivitiesIntoSession(request);

        if (null != loanIndividualMonitoringIsEnabled && 0 != loanIndividualMonitoringIsEnabled.intValue()
                && customer.isGroup()) {

            List<LoanBO> individualLoans = loanBusinessService.findIndividualLoans(Integer.valueOf(
                    loanBO.getAccountId()).toString());

            List<LoanAccountDetailsDto> loanAccountDetailsViewList = new ArrayList<LoanAccountDetailsDto>();

            for (LoanBO individualLoan : individualLoans) {
                LoanAccountDetailsDto loandetails = new LoanAccountDetailsDto();
                loandetails.setClientId(individualLoan.getCustomer().getCustomerId().toString());
                loandetails.setClientName(individualLoan.getCustomer().getDisplayName());
                loandetails.setLoanAmount(null != individualLoan.getLoanAmount()
                        && !EMPTY.equals(individualLoan.getLoanAmount().toString()) ? individualLoan.getLoanAmount()
                        .toString() : "0.0");

                if (null != individualLoan.getBusinessActivityId()) {
                    loandetails.setBusinessActivity(individualLoan.getBusinessActivityId().toString());

                    List<BusinessActivityEntity> businessActEntity = (List<BusinessActivityEntity>) SessionUtils
                            .getAttribute("BusinessActivities", request);

                    for (ValueListElement busact : businessActEntity) {
                        if (busact.getId().toString().equals(individualLoan.getBusinessActivityId().toString())) {
                            loandetails.setBusinessActivityName(busact.getName());
                        }
                    }
                }
                String governmentId = clientBusinessService.getClient(individualLoan.getCustomer().getCustomerId())
                        .getGovernmentId();
                loandetails.setGovermentId(StringUtils.isNotBlank(governmentId) ? governmentId : "-");
                loanAccountDetailsViewList.add(loandetails);
            }
            SessionUtils.setAttribute(CUSTOMER_ID, customerId, request);
            SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanAccountDetailsViewList, request);
        }

        loanBusinessService.initialize(loanBO.getLoanMeeting());
        for (AccountActionDateEntity accountActionDateEntity : loanBO.getAccountActionDates()) {
            loanBusinessService.initialize(accountActionDateEntity);
            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity) accountActionDateEntity)
                    .getAccountFeesActionDetails()) {
                loanBusinessService.initialize(accountFeesActionDetailEntity);
            }
        }
        setLocaleForMasterEntities(loanBO, getUserContext(request).getLocaleId());
        loadLoanDetailPageInfo(loanBO, request);
        loadMasterData(request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);

        SurveysPersistence surveysPersistence = new SurveysPersistence();
        List<SurveyInstance> surveys = surveysPersistence.retrieveInstancesByAccount(loanBO);
        boolean activeSurveys = surveysPersistence.isActiveSurveysForSurveyType(SurveyType.LOAN);
        request.setAttribute(CustomerConstants.SURVEY_KEY, surveys);
        request.setAttribute(CustomerConstants.SURVEY_COUNT, activeSurveys);
        request.setAttribute(AccountConstants.SURVEY_KEY, surveys);

        Integer administrativeDocumentsIsEnabled = configurationPersistence.getConfigurationKeyValueInteger(
                ADMINISTRATIVE_DOCUMENT_IS_ENABLED).getValue();

        if (null != administrativeDocumentsIsEnabled && administrativeDocumentsIsEnabled.intValue() == 1) {
            SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSLIST,
                    new AdminDocumentPersistence().getAllAdminDocuments(), request);

            SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSACCSTATEMIXLIST,
                    new AdminDocAccStateMixPersistence().getAllMixedAdminDocuments(), request);

        }

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getLoanRepaymentSchedule(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LoanBO loanBO = loanBusinessService.getAccount(getIntegerValue(request.getParameter(ACCOUNT_ID)));
        loanBO.setUserContext(getUserContext(request));
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
        return mapping.findForward(ActionForwards.getLoanRepaymentSchedule.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward viewStatusHistory(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);
        LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
        loanBusinessService.initialize(loanBO.getAccountStatusChangeHistory());
        loanBO.setUserContext(getUserContext(request));
        List<AccountStatusChangeHistoryEntity> accStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>(
                loanBO.getAccountStatusChangeHistory());
        SessionUtils.setCollectionAttribute(STATUS_HISTORY, accStatusChangeHistory, request);
        loanBO = null;
        return mapping.findForward(ActionForwards.viewStatusHistory.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
        String perspective = loanAccountForm.getPerspective();
        if (perspective != null) {
            request.setAttribute(PERSPECTIVE, perspective);
        }
        ActionForwards actionForward = null;
        String method = (String) request.getAttribute(METHODCALLED);
        if (method.equals(Methods.getPrdOfferings.toString())) {
            actionForward = ActionForwards.getPrdOfferigs_failure;
        } else if (method.equals(Methods.load.toString())) {
            actionForward = ActionForwards.getPrdOfferigs_success;
        } else if (method.equals(Methods.schedulePreview.toString())) {
            actionForward = ActionForwards.load_success;
        } else if (method.equals(Methods.managePreview.toString())) {
            actionForward = ActionForwards.managepreview_failure;
        } else if (method.equals(Methods.preview.toString())) {
            actionForward = ActionForwards.preview_failure;
        }
        return mapping.findForward(actionForward.toString());
    }

    private void setMonthlySchedule(final LoanAccountActionForm loanActionForm,
            final MeetingDetailsEntity meetingDetails) {
        // 2 is signaled as the schedule is monthly on jsp page (Monthradio
        // button is clicked)
        loanActionForm.setFrequency("2");
        loanActionForm.setRecurMonth(meetingDetails.getRecurAfter().toString());
        loanActionForm.setDayRecurMonth(meetingDetails.getRecurAfter().toString());
        if (meetingDetails.getWeekRank() != null) {
            // 2 is signaled as the day of week is chosen on jsp page. For ex,
            // First Monday of every 2 months
            loanActionForm.setMonthType("2");
            loanActionForm.setMonthRank(meetingDetails.getWeekRank().getValue().toString());
            loanActionForm.setMonthWeek(meetingDetails.getWeekDay().getValue().toString());

        } else {
            // 1 is signaled as the day of month is chosen on jsp page. For ex,
            // 12 th day of every 1 month
            loanActionForm.setMonthType("1");
            loanActionForm.setMonthDay(meetingDetails.getDayNumber().toString());
        }
    }

    private void setWeeklySchedule(final LoanAccountActionForm loanActionForm, final MeetingDetailsEntity meetingDetail) {
        // 1 is signaled as the schedule is weekly on jsp page. Week radio
        // button is clicked
        loanActionForm.setFrequency("1");
        loanActionForm.setRecurWeek(meetingDetail.getRecurAfter().toString());
        loanActionForm.setWeekDay(meetingDetail.getWeekDay().getValue().toString());
    }

    public ActionForward redoLoanBegin(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.beginRedoLoanDisbursal_success.toString());
    }

    private CustomerBO retrieveCustomerDetailFromSession(final HttpServletRequest request) throws PageExpiredException, ServiceException {
        CustomerDetailDto oldCustomer = (CustomerDetailDto) SessionUtils.getAttribute(LOANACCOUNTOWNER, request);
        CustomerBO customer = getCustomer(oldCustomer.getCustomerId());
        return customer;
    }

    private LoanBO constructLoan(final LoanAccountActionForm loanActionForm, final HttpServletRequest request)
            throws AccountException, ServiceException, PageExpiredException, PersistenceException,
            NumberFormatException, MeetingException, InvalidDateException {

        boolean isRepaymentIndepOfMeetingEnabled = configService.isRepaymentIndepOfMeetingEnabled();
        CustomerBO customer = retrieveCustomerDetailFromSession(request);

        // Resolve new meeting for repayment day
        MeetingBO newMeetingForRepaymentDay = null;
        if (isRepaymentIndepOfMeetingEnabled) {
            newMeetingForRepaymentDay = this.createNewMeetingForRepaymentDay(request, loanActionForm, customer);
        }

        LoanOfferingBO loanOffering = loanPrdBusinessService.getLoanOffering(((LoanOfferingBO) SessionUtils
                .getAttribute(LOANOFFERING, request)).getPrdOfferingId(), getUserContext(request).getLocaleId());
        LoanBO loan;
        if (isRedoOperation(request.getParameter(PERSPECTIVE))) {
            loan = LoanBO.redoLoan(getUserContext(request), loanOffering, customer,
                    AccountState.LOAN_PARTIAL_APPLICATION, new Money(loanOffering.getCurrency(), loanActionForm
                            .getLoanAmount()), loanActionForm.getNoOfInstallmentsValue(), loanActionForm
                            .getDisbursementDateValue(getUserContext(request).getPreferredLocale()), loanActionForm
                            .isInterestDedAtDisbValue(), loanActionForm.getInterestDoubleValue(), loanActionForm
                            .getGracePeriodDurationValue(),
                    getFund(request, loanActionForm.getLoanOfferingFundValue()), loanActionForm.getFeesToApply(),
                    loanActionForm.getCustomFields(), loanActionForm.getMaxLoanAmountValue(), loanActionForm
                            .getMinLoanAmountValue(), loanActionForm.getMaxNoInstallmentsValue(), loanActionForm
                            .getMinNoInstallmentsValue(), isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
            loan.setExternalId(loanActionForm.getExternalId());
            logger.debug("Loan redo, External account ID = " + loan.getExternalId());
        } else {
            loan = LoanBO.createLoan(getUserContext(request), loanOffering, customer,
                    AccountState.LOAN_PARTIAL_APPLICATION, new Money(loanOffering.getCurrency(), loanActionForm
                            .getLoanAmount()), loanActionForm.getNoOfInstallmentsValue(), loanActionForm
                            .getDisbursementDateValue(getUserContext(request).getPreferredLocale()), loanActionForm
                            .isInterestDedAtDisbValue(), loanActionForm.getInterestDoubleValue(), loanActionForm
                            .getGracePeriodDurationValue(),
                    getFund(request, loanActionForm.getLoanOfferingFundValue()), loanActionForm.getFeesToApply(),
                    loanActionForm.getCustomFields(), loanActionForm.getMaxLoanAmountValue(), loanActionForm
                            .getMinLoanAmountValue(), loanActionForm.getMaxNoInstallmentsValue(), loanActionForm
                            .getMinNoInstallmentsValue(), isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
            loan.setExternalId(loanActionForm.getExternalId());
            logger.debug("Loan create, External account ID = " + loan.getExternalId());
        }
        loan.setBusinessActivityId(loanActionForm.getBusinessActivityIdValue());
        loan.setCollateralNote(loanActionForm.getCollateralNote());
        loan.setCollateralTypeId(loanActionForm.getCollateralTypeIdValue());
        return loan;
    }

    /**
     * Resolve repayment start date according to given disbursement date
     *
     * The resulting date equates to the disbursement date plus MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY: e.g.
     * If disbursement date is 18 June 2008, and MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY is 1 then the
     * repayment start date would be 19 June 2008
     *
     * @return Date repaymentStartDate
     * @throws PersistenceException
     */
    private Date resolveRepaymentStartDate(final Date disbursementDate) {
        int minDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        final GregorianCalendar repaymentStartDate = new GregorianCalendar();
        repaymentStartDate.setTime(disbursementDate);
        repaymentStartDate.add(Calendar.DAY_OF_WEEK, minDaysInterval);
        return repaymentStartDate.getTime();
    }

    private LoanBO redoLoan(final LoanAccountActionForm loanActionForm, final HttpServletRequest request,
            final SaveLoan save, final CustomerPersistence customerPersistence) throws PageExpiredException,
            AccountException, ServiceException, PersistenceException, NumberFormatException, MeetingException,
            InvalidDateException {
        LoanBO loan = constructLoan(loanActionForm, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

        loan.changeStatus(AccountState.LOAN_APPROVED, null, "Automatic Status Update (Redo Loan)");

        PersonnelBO personnel = getPersonnel(request);

        if (save.equals(SaveLoan.YES)) {
            loan.save();
        } else {
            loanDisbursementAndPayments(loanActionForm, request, save, loan, personnel);
        }

        return loan;
    }

    private PersonnelBO getPersonnel(final HttpServletRequest request) {
        PersonnelBO personnel = null;
        try {
            personnel = new PersonnelPersistence().getPersonnel(getUserContext(request).getId());
        } catch (PersistenceException e) {
            throw new IllegalStateException(e);
        }
        return personnel;
    }

    private void loanDisbursementAndPayments(final LoanAccountActionForm loanActionForm,
            final HttpServletRequest request, final SaveLoan save, final LoanBO loan, final PersonnelBO personnel)
            throws AccountException, PageExpiredException, ServiceException, PersistenceException {
        // We're assuming cash disbursal for this situation right now
        loan.disburseLoan(personnel, PaymentTypes.CASH.getValue(), save.equals(SaveLoan.YES));

        List<PaymentDataHtmlBean> paymentDataBeans = loanActionForm.getPaymentDataBeans();
        PaymentData payment;
        CustomerBO customer = retrieveCustomerDetailFromSession(request);
        try {
            for (PaymentDataTemplate template : paymentDataBeans) {
                if (template.hasValidAmount() && template.getTransactionDate() != null) {
                    if (!customer.getCustomerMeeting().getMeeting().isValidMeetingDate(template.getTransactionDate(),
                            DateUtils.getLastDayOfNextYear())) {
                        throw new AccountException("errors.invalidTxndate");
                    }
                    payment = PaymentData.createPaymentData(template);
                    loan.applyPayment(payment, false);
                }
            }
        } catch (InvalidDateException ide) {
            throw new AccountException(ide);
        } catch (MeetingException e) {
            throw new ServiceException(e);
        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, @SuppressWarnings("unused") final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        String perspective = loanActionForm.getPerspective();
        if (perspective != null) {
            request.setAttribute(PERSPECTIVE, perspective);
        }

        UserContext userContext = getUserContext(request);
        List<LoanAccountDetailsDto> loanAccountDetailsList = getLoanAccountDetailsFromSession(request);
        DateTime disbursementDate = new DateTime(loanActionForm.getDisbursementDateValue(userContext.getPreferredLocale()));
        FundBO fund = getFund(request, loanActionForm.getLoanOfferingFundValue());
        Integer customerId = ((CustomerDetailDto) SessionUtils.getAttribute(LOANACCOUNTOWNER, request)).getCustomerId();

        LoanCreationResultDto loanCreationResultDto = this.loanServiceFacade.createLoan(userContext, customerId, disbursementDate, fund, loanActionForm);

        LoanBO loan;
        if (isRedoOperation(perspective)) {
            loan = redoLoan(loanActionForm, request, SaveLoan.YES, new CustomerPersistence());
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        }

        if (loanCreationResultDto.isGlimApplicable()) {
            boolean isRepaymentIndepOfMeetingEnabled = configService.isRepaymentIndepOfMeetingEnabled();
            for (LoanAccountDetailsDto loanAccountDetail : loanAccountDetailsList) {
                createIndividualLoanAccount(loanActionForm, loanCreationResultDto.getLoan(), isRepaymentIndepOfMeetingEnabled, loanAccountDetail, isRedoOperation(perspective));
            }
        }

        if (isRedoOperation(perspective)) {
            loanDisbursementAndPayments(loanActionForm, request, SaveLoan.YES, loanCreationResultDto.getLoan(), getPersonnel(request));
        }

        loanActionForm.setAccountId(loanCreationResultDto.getAccountId().toString());
        request.setAttribute(GLOBAL_ACCOUNT_NUM, loanCreationResultDto.getGlobalAccountNum());

        // FIXME - keithw - why set these
//        request.setAttribute("customer", customer);
//        request.setAttribute("loan", loan);

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @SuppressWarnings("unchecked")
    private List<LoanAccountDetailsDto> getLoanAccountDetailsFromSession(final HttpServletRequest request)
            throws PageExpiredException {
        return (List<LoanAccountDetailsDto>) SessionUtils.getAttribute("loanAccountDetailsView", request);
    }

    private boolean isRedoOperation(final String perspective) {
        return PERSPECTIVE_VALUE_REDO_LOAN.equals(perspective);
    }

    // TODO: merge this with GlimLoanUpdater.createIndividualLoan. But note that
    // this method
    // depends on findSystemId which expects globalCustNum. The other one
    // expects actual client id
    // This is because the LoanAccountActionForm.java is implemented such that
    // client id becomes globalcustnum
    // somewhere during the Create Account flow
    private void createIndividualLoanAccount(final LoanAccountActionForm loanActionForm, final LoanBO loan,
            final boolean isRepaymentIndepOfMeetingEnabled, final LoanAccountDetailsDto loanAccountDetail,
            final boolean isRedoOperation) throws AccountException, ServiceException, PropertyNotFoundException {
        LoanBO individualLoan;
        if (isRedoOperation) {
            individualLoan = LoanBO.redoIndividualLoan(loan.getUserContext(), loan.getLoanOffering(),
                    getCustomerBusinessService().findBySystemId(loanAccountDetail.getClientId()), loanActionForm
                            .getState(), new Money(loan.getCurrency(), loanAccountDetail.getLoanAmount().toString()),
                    loan.getNoOfInstallments(), loan.getDisbursementDate(), false, isRepaymentIndepOfMeetingEnabled,
                    loan.getInterestRate(), loan.getGracePeriodDuration(), loan.getFund(), new ArrayList<FeeDto>(),
                    new ArrayList<CustomFieldDto>());

        } else {
            individualLoan = LoanBO.createIndividualLoan(loan.getUserContext(), loan.getLoanOffering(),
                    getCustomerBusinessService().findBySystemId(loanAccountDetail.getClientId()), loanActionForm
                            .getState(), new Money(loan.getCurrency(), loanAccountDetail.getLoanAmount().toString()),
                    loan.getNoOfInstallments(), loan.getDisbursementDate(), false, isRepaymentIndepOfMeetingEnabled,
                    loan.getInterestRate(), loan.getGracePeriodDuration(), loan.getFund(), new ArrayList<FeeDto>(),
                    new ArrayList<CustomFieldDto>(), false);
        }

        individualLoan.setParentAccount(loan);

        if (!StringUtils.isBlank(loanAccountDetail.getBusinessActivity())) {
            individualLoan.setBusinessActivityId(Integer.valueOf(loanAccountDetail.getBusinessActivity()));
        }

        individualLoan.save();
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);
        CustomerBO customer = getCustomerFromRequest(request);

        if (isGlimEnabled()) {
            populateGlimAttributes(request, loanActionForm, globalAccountNum, customer);
        }

        String recurMonth = customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter().toString();
        handleRepaymentsIndependentOfMeetingIfConfigured(request, loanActionForm, recurMonth);

        LoanBO loanBO = getLoanBO(request);
        loanBO.setUserContext(getUserContext(request));
        SessionUtils.setAttribute(PROPOSED_DISBURSAL_DATE, loanBO.getDisbursementDate(), request);
        List<ValueListElement> businessActivitiesFromDatabase = getBusinessActivitiesFromDatabase(request);

        SessionUtils.removeAttribute(LOANOFFERING, request);
        LoanOfferingBO loanOffering = getLoanOffering(loanBO.getLoanOffering().getPrdOfferingId(), getUserContext(
                request).getLocaleId());
        loanActionForm.setInstallmentRange(loanBO.getMaxMinNoOfInstall());
        loanActionForm.setLoanAmountRange(loanBO.getMaxMinLoanAmount());
        MaxMinInterestRate interestRateRange = loanBO.getMaxMinInterestRate();
        loanActionForm.setMaxInterestRate(interestRateRange.getMaxLoanAmount());
        loanActionForm.setMinInterestRate(interestRateRange.getMinLoanAmount());
        loanActionForm.setExternalId(loanBO.getExternalId());
        if (null != loanBO.getFund()) {
            loanActionForm.setLoanOfferingFund(loanBO.getFund().getFundId().toString());
        }
        if (configService.isRepaymentIndepOfMeetingEnabled()) {
            setRepaymentDayFieldsOnForm(loanActionForm, loanBO.getLoanMeeting().getMeetingDetails());
        }
        SessionUtils.setAttribute(LOANOFFERING, loanOffering, request);
        loadUpdateMasterData(request);

        SessionUtils.setAttribute(RECURRENCEID, loanBO.getLoanMeeting().getMeetingDetails().getRecurrenceTypeEnum()
                .getValue(), request);
        SessionUtils.setAttribute(RECURRENCENAME, loanBO.getLoanMeeting().getMeetingDetails().getRecurrenceType()
                .getRecurrenceName(), request);
        SessionUtils.setCollectionAttribute(LOANFUNDS, getFunds(loanOffering), request);
        setRequestAttributesForEditPage(request, loanBO);

        setFormAttributes(loanBO, form, request);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    private LoanBO getLoanBO(final HttpServletRequest request) throws PageExpiredException, ServiceException {
        LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        LoanBO loanBO = loanBusinessService.getAccount(loanBOInSession.getAccountId());
        return loanBO;
    }

    private void setRequestAttributesForEditPage(final HttpServletRequest request, final LoanBO loanBO)
            throws ApplicationException {
        request.setAttribute("accountState", loanBO.getState());
        request.setAttribute(MasterConstants.COLLATERAL_TYPES, new MasterPersistence().getLookUpEntity(
                MasterConstants.COLLATERAL_TYPES, getUserContext(request).getLocaleId()).getCustomValueListElements());
        request.setAttribute("collateralTypeId", loanBO.getCollateralTypeId());
    }

    private CustomerBO getCustomerFromRequest(final HttpServletRequest request) throws ServiceException {
        String customerId = request.getParameter(CUSTOMER_ID);
        if (isNotEmpty(customerId)) {
            return getCustomer(Integer.valueOf(customerId));
        }
        return null;
    }

    private void populateGlimAttributes(final HttpServletRequest request, final LoanAccountActionForm loanActionForm,
            final String globalAccountNum, final CustomerBO customer) throws PageExpiredException, ServiceException {
        GlimSessionAttributes glimSessionAttributes = getGlimSpecificPropertiesToSet(loanActionForm, globalAccountNum,
                customer, getBusinessActivitiesFromDatabase(request));
        glimSessionAttributes.putIntoSession(request);
    }

    GlimSessionAttributes getGlimSpecificPropertiesToSet(final LoanAccountActionForm loanActionForm,
            final String globalAccountNum, final CustomerBO customer, final List<ValueListElement> businessActivities)
            throws ServiceException, PageExpiredException {
        if (configService.isGlimEnabled() && customer.isGroup()) {
            List<LoanBO> individualLoans = loanBusinessService
                    .getAllChildrenForParentGlobalAccountNum(globalAccountNum);
            List<ClientBO> activeClientsUnderGroup = clientBusinessService.getActiveClientsUnderGroup(customer
                    .getCustomerId());
            List<LoanAccountDetailsDto> clientDetails = populateClientDetailsFromLoan(activeClientsUnderGroup,
                    individualLoans, businessActivities);
            loanActionForm.setClientDetails(clientDetails);
            loanActionForm.setClients(fetchClientIdsWithMatchingLoans(individualLoans, clientDetails));
            return new GlimSessionAttributes(LoanConstants.GLIM_ENABLED_VALUE, activeClientsUnderGroup,
                    LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);
        } else {
            return new GlimSessionAttributes(LoanConstants.GLIM_DISABLED_VALUE);
        }
    }

    private List<String> fetchClientIdsWithMatchingLoans(final List<LoanBO> individualLoans,
            final List<LoanAccountDetailsDto> clientDetails) {
        List<String> clientIds = new ArrayList<String>();
        for (final LoanAccountDetailsDto clientDetail : clientDetails) {
            LoanBO loanMatchingClientDetail = (LoanBO) CollectionUtils.find(individualLoans, new Predicate() {
                public boolean evaluate(final Object object) {
                    return ((LoanBO) object).getCustomer().getCustomerId().toString()
                            .equals(clientDetail.getClientId());
                }
            });
            if (loanMatchingClientDetail != null) {
                clientIds.add(clientDetail.getClientId());
            } else {
                clientIds.add("");
            }
        }
        return clientIds;
    }

    List<LoanAccountDetailsDto> populateClientDetailsFromLoan(final List<ClientBO> activeClientsUnderGroup,
            final List<LoanBO> individualLoans, final List<ValueListElement> businessActivities)
            throws ServiceException {
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();
        for (final ClientBO client : activeClientsUnderGroup) {
            LoanAccountDetailsDto clientDetail = new LoanAccountDetailsDto();
            clientDetail.setClientId(getStringValue(client.getCustomerId()));
            clientDetail.setClientName(client.getDisplayName());
            LoanBO loanAccount = (LoanBO) CollectionUtils.find(individualLoans, new Predicate() {
                public boolean evaluate(final Object object) {
                    return client.getCustomerId().equals(((LoanBO) object).getCustomer().getCustomerId());
                }

            });
            if (loanAccount != null) {
                final Integer businessActivityId = loanAccount.getBusinessActivityId();
                if (businessActivityId != null) {
                    clientDetail.setBusinessActivity(Integer.toString(businessActivityId));

                    ValueListElement businessActivityElement = (ValueListElement) CollectionUtils.find(
                            businessActivities, new Predicate() {

                                public boolean evaluate(final Object object) {
                                    return ((ValueListElement) object).getId().equals(businessActivityId);
                                }

                            });
                    if (businessActivityElement != null) {
                        clientDetail.setBusinessActivityName(businessActivityElement.getName());
                    }
                }

                clientDetail.setLoanAmount(loanAccount.getLoanAmount() != null ? loanAccount.getLoanAmount().toString()
                        : "0.0");
            }
            clientDetails.add(clientDetail);
        }
        return clientDetails;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePrevious(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        setRequestAttributesForEditPage(request, getLoanBO(request));
        return mapping.findForward(ActionForwards.manageprevious_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePreview(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
        Short localeId = getUserContext(request).getLocaleId();
        if (isGlimEnabled()) {
            performGlimSpecificOnManagePreview(request, loanAccountForm, localeId);
        }
        if (null != getFund(loanAccountForm)) {
            request.setAttribute("sourceOfFunds", getFund(loanAccountForm).getFundName());
        }

        resetBusinessActivity(request, localeId, (LoanAccountActionForm) form);
        return mapping.findForward(ActionForwards.managepreview_success.toString());
    }

    private FundBO getFund(LoanAccountActionForm loanAccountActionForm) throws PersistenceException {
        FundBO fund = null;
        if (!StringUtils.isBlank(loanAccountActionForm.getLoanOfferingFund())) {
            Short fundId = loanAccountActionForm.getLoanOfferingFundValue();
            if (fundId != 0) {
                fund = new FundPersistence().getFund(fundId);
            }
        }
        return fund;
    }

    private void performGlimSpecificOnManagePreview(final HttpServletRequest request,
            final LoanAccountActionForm loanAccountForm, final Short localeId) throws ServiceException,
            PageExpiredException {
        CustomerBO customer = getCustomer(loanAccountForm.getCustomerIdValue());
        setGlimEnabledSessionAttributes(request, customer.isGroup());
        if (customer.isGroup()) {
            List<LoanAccountDetailsDto> loanAccountDetailsView = populateDetailsForSelectedClients(localeId,
                    loanAccountForm.getClientDetails(), loanAccountForm.getClients());
            SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanAccountDetailsView, request);
        }
    }

    private List<LoanAccountDetailsDto> populateDetailsForSelectedClients(final Short localeId,
            final List<LoanAccountDetailsDto> clientDetails, final List<String> selectedClients)
            throws ServiceException {
        List<LoanAccountDetailsDto> loanAccountDetailsView = new ArrayList<LoanAccountDetailsDto>();
        for (final String clientId : selectedClients) {
            if (StringUtils.isNotEmpty(clientId)) {
                LoanAccountDetailsDto matchingClientDetail = (LoanAccountDetailsDto) CollectionUtils
                        .find(clientDetails, new Predicate() {
                            public boolean evaluate(final Object object) {
                                return ((LoanAccountDetailsDto) object).getClientId().equals(clientId);
                            }
                        });

                if (matchingClientDetail != null) {
                    setGovernmentIdAndPurpose(matchingClientDetail, localeId);
                    // matchingClientDetail
                    // .setClientId(clientBusinessService.getClient(
                    // getIntegerValue(matchingClientDetail
                    // .getClientId())).getGlobalCustNum());
                    loanAccountDetailsView.add(matchingClientDetail);
                }
            }
        }
        return loanAccountDetailsView;
    }

    private void setGovernmentIdAndPurpose(final LoanAccountDetailsDto clientDetail, final Short localeId)
            throws ServiceException {
        clientDetail.setBusinessActivityName(findBusinessActivityName(clientDetail.getBusinessActivity(), localeId));
        clientDetail.setGovermentId(findGovernmentId(getIntegerValue(clientDetail.getClientId())));
    }

    private void resetBusinessActivity(final HttpServletRequest request, final Short localeId,
            final LoanAccountActionForm loanAccountActionForm) throws PageExpiredException, Exception {
        SessionUtils.removeAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME, request);
        if (loanAccountActionForm.getBusinessActivityIdValue() != null) {
            SessionUtils.setAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME, getNameForBusinessActivityEntity(
                    loanAccountActionForm.getBusinessActivityIdValue(), localeId), request);
        }
    }

    private String findGovernmentId(final Integer clientId) throws ServiceException {
        ClientBO client = clientBusinessService.getClient(clientId);
        String governmentId = client.getGovernmentId();
        return StringUtils.isBlank(governmentId) ? "-" : governmentId;
    }

    private String findBusinessActivityName(final String businessActivity, final Short localeId)
            throws ServiceException {
        List<ValueListElement> businessActEntity = masterDataService.retrieveMasterEntities(
                MasterConstants.LOAN_PURPOSES, localeId);
        for (ValueListElement busact : businessActEntity) {

            if (busact.getId().toString().equals(businessActivity)) {
                return busact.getName();
            }
        }
        return null;
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loan_detail_page.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        LoanBO loanBO = loanBusinessService.findBySystemId(loanBOInSession.getGlobalAccountNum());
        checkVersionMismatch(loanBOInSession.getVersionNo(), loanBO.getVersionNo());
        loanBO.setVersionNo(loanBOInSession.getVersionNo());
        loanBO.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(loanBO);

        LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;

        // Set newMeetingForRepaymentDay if isRepaymentIndepOfMeetingEnabled
        // flag is true
        CustomerBO customer = loanBO.getCustomer();
        MeetingBO newMeetingForRepaymentDay = null;
        boolean isRepaymentIndepOfMeetingEnabled = configService.isRepaymentIndepOfMeetingEnabled();

        // Resolve new meeting for repayment day
        if (isRepaymentIndepOfMeetingEnabled) {
            newMeetingForRepaymentDay = this.createNewMeetingForRepaymentDay(request, loanAccountActionForm, customer);
        }

        loanBO.setExternalId(loanAccountActionForm.getExternalId());
        loanBO.updateLoan(loanAccountActionForm.isInterestDedAtDisbValue(), new Money(loanBO.getCurrency(),
                loanAccountActionForm.getLoanAmount()), loanAccountActionForm.getInterestDoubleValue(),
                loanAccountActionForm.getNoOfInstallmentsValue(), loanAccountActionForm
                        .getDisbursementDateValue(getUserContext(request).getPreferredLocale()), loanAccountActionForm
                        .getGracePeriodDurationValue(), loanAccountActionForm.getBusinessActivityIdValue(),
                loanAccountActionForm.getCollateralNote(), loanAccountActionForm.getCollateralTypeIdValue(),
                loanAccountActionForm.getCustomFields(), isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay,
                getFund(loanAccountActionForm));

        if (configService.isGlimEnabled() && customer.isGroup()) {
            List<LoanAccountDetailsDto> loanAccountDetailsList = getLoanAccountDetailsFromSession(request);
            List<LoanBO> individualLoans = loanBusinessService.findIndividualLoans(Integer.valueOf(
                    loanBO.getAccountId()).toString());
            handleIndividualLoans(loanBO, loanAccountActionForm, isRepaymentIndepOfMeetingEnabled,
                    loanAccountDetailsList, individualLoans);
            request.setAttribute(CUSTOMER_ID, loanBO.getCustomer().getCustomerId().toString());
        }

        loanBOInSession = null;
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);

        return mapping.findForward(ActionForwards.update_success.toString());
    }

    public LoanProductService getLoanService() {
        return this.loanProductService;
    }

    public void setLoanService(final LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    void handleIndividualLoans(final LoanBO loanBO, final LoanAccountActionForm loanAccountActionForm,
            final boolean isRepaymentIndepOfMeetingEnabled,
            final List<LoanAccountDetailsDto> loanAccountDetailsList, final List<LoanBO> individualLoans)
            throws AccountException, ServiceException, PropertyNotFoundException {
        List<Integer> foundLoans = new ArrayList<Integer>();
        for (final LoanAccountDetailsDto loanAccountDetail : loanAccountDetailsList) {
            Predicate predicate = new Predicate() {

                public boolean evaluate(final Object object) {
                    return ((LoanBO) object).getCustomer().getCustomerId().toString().equals(
                            loanAccountDetail.getClientId());
                }

            };
            LoanBO individualLoan = (LoanBO) CollectionUtils.find(individualLoans, predicate);
            if (individualLoan == null) {
                glimLoanUpdater.createIndividualLoan(loanAccountActionForm, loanBO, isRepaymentIndepOfMeetingEnabled,
                        loanAccountDetail);
            } else {
                foundLoans.add(individualLoan.getAccountId());
                glimLoanUpdater.updateIndividualLoan(loanAccountDetail, individualLoan);
            }
        }
        for (LoanBO loan : individualLoans) {
            if (!foundLoans.contains(loan.getAccountId())) {
                glimLoanUpdater.delete(loan);
            }
        }
    }

    /**
     * Create new meeting for the repayment day.
     *
     * Depending on the recurrence id (WEEKLY or MONTHLY) a MeetingBO will be created and returned
     *
     * @throws InvalidDateException
     *
     */
    private MeetingBO createNewMeetingForRepaymentDay(final HttpServletRequest request,
            final LoanAccountActionForm loanAccountActionForm, final CustomerBO customer) // ,
            // Short
            // recurrenceId)
            throws PersistenceException, MeetingException, InvalidDateException {
        MeetingBO newMeetingForRepaymentDay = null;
        Short recurrenceId = Short.valueOf(loanAccountActionForm.getRecurrenceId());
        final Date repaymentStartDate = this.resolveRepaymentStartDate(loanAccountActionForm
                .getDisbursementDateValue(getUserContext(request).getPreferredLocale()));
        if (RecurrenceType.WEEKLY.getValue().equals(recurrenceId)) {
            newMeetingForRepaymentDay = new MeetingBO(WeekDay.getWeekDay(Short.valueOf(loanAccountActionForm
                    .getWeekDay())), Short.valueOf(loanAccountActionForm.getRecurWeek()), repaymentStartDate,
                    MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
        } else if (RecurrenceType.MONTHLY.getValue().equals(recurrenceId)) {
            if (loanAccountActionForm.getMonthType().equals("1")) {
                newMeetingForRepaymentDay = new MeetingBO(Short.valueOf(loanAccountActionForm.getMonthDay()), Short
                        .valueOf(loanAccountActionForm.getDayRecurMonth()), repaymentStartDate,
                        MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
            } else {
                newMeetingForRepaymentDay = new MeetingBO(Short.valueOf(loanAccountActionForm.getMonthWeek()), Short
                        .valueOf(loanAccountActionForm.getRecurMonth()), repaymentStartDate,
                        MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace(),
                        Short.valueOf(loanAccountActionForm.getMonthRank()));
            }
        }
        return newMeetingForRepaymentDay;
    }

    private void setLocaleForMasterEntities(final LoanBO loanBO, final Short localeId) {
        if (loanBO.getGracePeriodType() != null) {
            // Is this locale ever consulted? I don't see a place...
            loanBO.getGracePeriodType().setLocaleId(localeId);
        }
        loanBO.getInterestType().setLocaleId(localeId);
        loanBO.getAccountState().setLocaleId(localeId);
        for (AccountFlagMapping accountFlagMapping : loanBO.getAccountFlags()) {
            accountFlagMapping.getFlag().setLocaleId(localeId);
        }
    }

    private void loadLoanDetailPageInfo(final LoanBO loanBO, final HttpServletRequest request) throws Exception {
        SessionUtils.setCollectionAttribute(RECENTACCOUNTACTIVITIES, loanBusinessService.getRecentActivityView(loanBO
                .getGlobalAccountNum()), request);
        SessionUtils.setAttribute(AccountConstants.LAST_PAYMENT_ACTION, loanBusinessService.getLastPaymentAction(loanBO
                .getAccountId()), request);
        SessionUtils.setCollectionAttribute(NOTES, loanBO.getRecentAccountNotes(), request);
        loadCustomFieldDefinitions(request);
    }

    private LoanOfferingBO getLoanOffering(final Short loanOfferingId, final short localeId) throws Exception {
        return loanPrdBusinessService.getLoanOffering(loanOfferingId, localeId);
    }

    private List<FundBO> getFunds(final LoanOfferingBO loanOffering) {
        List<FundBO> funds = new ArrayList<FundBO>();
        if (loanOffering.getLoanOfferingFunds() != null && loanOffering.getLoanOfferingFunds().size() > 0) {
            for (LoanOfferingFundEntity loanOfferingFund : loanOffering.getLoanOfferingFunds()) {
                funds.add(loanOfferingFund.getFund());
            }
        }
        return funds;
    }

    private void loadMasterData(final HttpServletRequest request) throws Exception {
        // Retrieve and set into the session all collateral types from the
        // lookup_value_locale table associated with the current user context
        // locale
        SessionUtils.setCollectionAttribute(MasterConstants.COLLATERAL_TYPES, new MasterPersistence().getLookUpEntity(
                MasterConstants.COLLATERAL_TYPES, getUserContext(request).getLocaleId()).getCustomValueListElements(),
                request);

        setBusinessActivitiesIntoSession(request);
    }

    private String getNameForBusinessActivityEntity(final Integer entityId, final Short localeId) throws Exception {
        if (entityId != null) {
            return masterDataService.retrieveMasterEntities(entityId, localeId);
        }
        return "";
    }

    private FundBO getFund(final HttpServletRequest request, final Short fundId) throws PageExpiredException {
        List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(LOANFUNDS, request);
        for (FundBO fund : funds) {
            if (fund.getFundId().equals(fundId)) {
                return fund;
            }
        }
        return null;
    }

    private void setFormAttributes(final LoanBO loan, final ActionForm form, final HttpServletRequest request)
            throws Exception {
        LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
        loanAccountActionForm.setStateSelected(getStringValue(loan.getAccountState().getId()));
        loanAccountActionForm.setLoanAmount(getStringValue(loan.getLoanAmount()));

        java.util.Date proposedDisbursement = (Date) SessionUtils.getAttribute(PROPOSED_DISBURSAL_DATE, request);
        loanAccountActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(getUserContext(request)
                .getPreferredLocale(), DateUtils.toDatabaseFormat(proposedDisbursement)));

        loanAccountActionForm.setIntDedDisbursement(loan.isInterestDeductedAtDisbursement() ? "1" : "0");
        loanAccountActionForm.setBusinessActivityId(getStringValue(loan.getBusinessActivityId()));
        if (loan.getCollateralTypeId() != null) {
            loanAccountActionForm.setCollateralTypeId(getStringValue(loan.getCollateralTypeId()));
        }
        loanAccountActionForm.setCollateralNote(loan.getCollateralNote());
        loanAccountActionForm.setInterestRate(getDoubleStringForInterest(loan.getInterestRate()));
        loanAccountActionForm.setNoOfInstallments(getStringValue(loan.getNoOfInstallments()));
        loanAccountActionForm.setGracePeriodDuration(getStringValue(loan.getGracePeriodDuration()));
        loanAccountActionForm.setCustomFields(createCustomFieldViews(loan.getAccountCustomFields(), request));

        loanAccountActionForm.setOriginalDisbursementDate(new java.sql.Date(loan.getDisbursementDate().getTime()));
    }

    private InstallmentDetailsDto getUpcomingInstallmentDetails(
            final AccountActionDateEntity upcomingAccountActionDate, final MifosCurrency currency) {
        if (upcomingAccountActionDate != null) {
            LoanScheduleEntity upcomingInstallment = (LoanScheduleEntity) upcomingAccountActionDate;
            return new InstallmentDetailsDto(upcomingInstallment.getPrincipalDue(), upcomingInstallment
                    .getInterestDue(), upcomingInstallment.getTotalFeeDueWithMiscFeeDue(), upcomingInstallment
                    .getPenaltyDue());
        }
        return new InstallmentDetailsDto(new Money(currency), new Money(currency), new Money(currency), new Money(
                currency));
    }

    private InstallmentDetailsDto getOverDueInstallmentDetails(
            final List<AccountActionDateEntity> overDueInstallmentList, final MifosCurrency currency) {
        Money principalDue = new Money(currency);
        Money interestDue = new Money(currency);
        Money feesDue = new Money(currency);
        Money penaltyDue = new Money(currency);
        for (AccountActionDateEntity accountActionDate : overDueInstallmentList) {
            LoanScheduleEntity installment = (LoanScheduleEntity) accountActionDate;
            principalDue = principalDue.add(installment.getPrincipalDue());
            interestDue = interestDue.add(installment.getInterestDue());
            feesDue = feesDue.add(installment.getTotalFeeDueWithMiscFeeDue());
            penaltyDue = penaltyDue.add(installment.getPenaltyDue());
        }
        return new InstallmentDetailsDto(principalDue, interestDue, feesDue, penaltyDue);
    }

    private void loadCustomFieldDefinitions(final HttpServletRequest request) throws Exception {
        SessionUtils.setCollectionAttribute(CUSTOM_FIELDS, getAccountBusinessService().retrieveCustomFieldsDefinition(
                EntityType.LOAN), request);
    }

    private List<CustomFieldDto> createCustomFieldViews(final Set<AccountCustomFieldEntity> customFieldEntities,
            final HttpServletRequest request) throws ApplicationException {
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils.getAttribute(CUSTOM_FIELDS, request);
        Locale locale = getUserContext(request).getPreferredLocale();
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            for (AccountCustomFieldEntity customFieldEntity : customFieldEntities) {
                if (customFieldDef.getFieldId().equals(customFieldEntity.getFieldId())) {
                    if (customFieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                        customFields.add(new CustomFieldDto(customFieldEntity.getFieldId(), DateUtils
                                .getUserLocaleDate(locale, customFieldEntity.getFieldValue()), customFieldDef
                                .getFieldType()));
                    } else {
                        customFields.add(new CustomFieldDto(customFieldEntity.getFieldId(), customFieldEntity
                                .getFieldValue(), customFieldDef.getFieldType()));
                    }
                }
            }
        }
        return customFields;
    }

    private void loadUpdateMasterData(final HttpServletRequest request) throws Exception {
        loadMasterData(request);
        loadCustomFieldDefinitions(request);
    }

    static class GlimSessionAttributes {

        private final Integer isGlimEnabled;
        private final List<ClientBO> clients;
        private final String loanAccountOwnerIsGroup;

        GlimSessionAttributes(final int isGlimEnabled, final List<ClientBO> clients,
                final String loanAccountOwnerIsGroup) {
            this.isGlimEnabled = isGlimEnabled;
            this.clients = clients;
            this.loanAccountOwnerIsGroup = loanAccountOwnerIsGroup;
        }

        GlimSessionAttributes(final int isGlimEnabled) {
            this(isGlimEnabled, Collections.EMPTY_LIST, "");
        }

        void putIntoSession(final HttpServletRequest request) throws PageExpiredException {
            SessionUtils.setAttribute(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, isGlimEnabled, request);
            SessionUtils.setCollectionAttribute(CLIENT_LIST, clients, request);
            SessionUtils.setAttribute(LOAN_ACCOUNT_OWNER_IS_A_GROUP, loanAccountOwnerIsGroup, request);
        }

        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + (clients == null ? 0 : clients.hashCode());
            result = PRIME * result + (isGlimEnabled == null ? 0 : isGlimEnabled.hashCode());
            result = PRIME * result + (loanAccountOwnerIsGroup == null ? 0 : loanAccountOwnerIsGroup.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final GlimSessionAttributes other = (GlimSessionAttributes) obj;
            if (clients == null) {
                if (other.clients != null) {
                    return false;
                }
            } else if (!clients.equals(other.clients)) {
                return false;
            }
            if (isGlimEnabled == null) {
                if (other.isGlimEnabled != null) {
                    return false;
                }
            } else if (!isGlimEnabled.equals(other.isGlimEnabled)) {
                return false;
            }
            if (loanAccountOwnerIsGroup == null) {
                if (other.loanAccountOwnerIsGroup != null) {
                    return false;
                }
            } else if (!loanAccountOwnerIsGroup.equals(other.loanAccountOwnerIsGroup)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "isGlimEnabled:" + isGlimEnabled + " loanAccountOwnerIsAGroup:" + loanAccountOwnerIsGroup
                    + " clients:" + clients;
        }

    }

    private static enum SaveLoan {
        YES, NO;
    }

    private void storeObjectOnSessionForUseInJspPage(final HttpServletRequest request, final String objectKey,
            final Serializable value) throws PageExpiredException {
        SessionUtils.setAttribute(objectKey, value, request);
    }

    private void storeCollectionOnSessionForUseInJspPage(final HttpServletRequest request, final String collectionKey,
            final Collection<? extends Serializable> collectionValue) throws PageExpiredException {
        SessionUtils.setCollectionAttribute(collectionKey, collectionValue, request);
    }

    private void storeRedoLoanSettingOnRequestForUseInJspIfPerspectiveParamaterOnQueryString(
            final HttpServletRequest request) {
        if (request.getParameter(PERSPECTIVE) != null) {
            request.setAttribute(PERSPECTIVE, request.getParameter(PERSPECTIVE));
        }
    }

    private boolean isGlimEnabled() throws ServiceException {
        return new ConfigurationBusinessService().isGlimEnabled();
    }

    private void setGlimEnabledSessionAttributes(final HttpServletRequest request, final boolean isGroup)
            throws PageExpiredException {
        storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, LoanConstants.GLIM_ENABLED_VALUE);
        if (isGroup) {
            storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOAN_ACCOUNT_OWNER_IS_A_GROUP, LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);
        }
    }

    private void handleRepaymentsIndependentOfMeetingIfConfigured(final HttpServletRequest request,
            final LoanAccountActionForm loanActionForm, final String recurMonth) throws ServiceException,
            PageExpiredException, Exception {

        if (configService.isRepaymentIndepOfMeetingEnabled()) {

            storeObjectOnSessionForUseInJspPage(request,
                    LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED, Integer.valueOf(1));
            storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOANACCOUNTOWNERISACLIENT,
                    LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);

            storeCollectionOnSessionForUseInJspPage(request, MeetingConstants.WEEKDAYSLIST, new FiscalCalendarRules()
                    .getWorkingDays());

            storeCollectionOnSessionForUseInJspPage(request, MeetingConstants.WEEKRANKLIST, RankOfDay.getRankOfDayList());

            loanActionForm.setRecurMonth(recurMonth);
        }
    }

    /**
     * @deprecated {@link LoanProductDao#findAllLoanPurposes()}
     */
    @Deprecated
    private void setBusinessActivitiesIntoSession(final HttpServletRequest request) throws PageExpiredException,
            ServiceException {
        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES,
                getBusinessActivitiesFromDatabase(request), request);
    }

    /**
     * use method from getPrdOfferings
     */
    @Deprecated
    private List<ValueListElement> getBusinessActivitiesFromDatabase(final HttpServletRequest request)
            throws ServiceException {
        return masterDataService.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, getUserContext(request)
                .getLocaleId());
    }
}