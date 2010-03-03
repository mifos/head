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
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MAX_RANGE_IS_NOT_MET;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_RANGE_IS_NOT_MET;
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
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
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
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.ViewInstallmentDetails;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundPersistence;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
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
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.RankOfDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.helpers.SurveyState;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.CustomerConstants;
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
import org.mifos.framework.util.LocalizationConverter;
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
    private final LoanServiceFacade loanServiceFacade = DependencyInjectedServiceLocator.locateLoanServiceFacade();
    private final CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();

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
                new MasterDataService(), new ConfigurationPersistence(), null, new AccountBusinessService());
        setLoanService(new LoanProductService(this.loanPrdBusinessService, this.feeService));
    }

    public LoanAccountAction(final ConfigurationBusinessService configService,
            final LoanBusinessService loanBusinessService, final GlimLoanUpdater glimLoanUpdater,
            final FeeBusinessService feeService, final LoanPrdBusinessService loanPrdBusinessService,
            final ClientBusinessService clientBusinessService, final MasterDataService masterDataService,
            final ConfigurationPersistence configurationPersistence, final LoanProductService loanProductService,
            final AccountBusinessService accountBusinessService) {
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

    // FIXME - keithw - used in tests only.
    LoanAccountAction(final LoanBusinessService loanBusinessService, final ConfigurationBusinessService configService,
            final GlimLoanUpdater glimLoanUpdater) {
        this(configService, loanBusinessService, glimLoanUpdater);
    }

    // FIXME - keithw - used in tests only.
    private LoanAccountAction(final ConfigurationBusinessService configService,
            final LoanBusinessService loanBusinessService, final GlimLoanUpdater glimLoanUpdater) {
        this(configService, loanBusinessService, glimLoanUpdater, new FeeBusinessService(),
                new LoanPrdBusinessService(), new ClientBusinessService(), new MasterDataService(),
                new ConfigurationPersistence(), null, new AccountBusinessService());
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
        final CustomerBO customer = customerDao.findCustomerById(loanActionForm.getCustomerIdValue());
        final List<LoanOfferingBO> loanOfferings = loanServiceFacade.loadActiveProductsApplicableForCustomer(customer);

        storeCollectionOnSessionForUseInJspPage(request, LoanConstants.LOANPRDOFFERINGS, loanOfferings);
        storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOANACCOUNTOWNER, customer);
        storeObjectOnSessionForUseInJspPage(request, LoanConstants.PROPOSED_DISBURSAL_DATE, customer
                .getCustomerAccount().getNextMeetingDate());

        storeRedoLoanSettingOnRequestForUseInJspIfPerspectiveParamaterOnQueryString(request);

        if (isGlimEnabled()) {
            setGlimEnabledSessionAttributes(request, customer);
            request.setAttribute(METHODCALLED, "getPrdOfferings");

            if (customer.isGroup()) {

                final LoanCreationGlimDto loanCreationGlimDto = loanServiceFacade
                        .retrieveGlimSpecificDataForGroup(customer);

                final List<ClientBO> activeClientsOfGroup = loanCreationGlimDto.getActiveClientsOfGroup();

                if (activeClientsOfGroup == null || activeClientsOfGroup.isEmpty()) {
                    throw new ApplicationException(GroupConstants.IMPOSSIBLE_TO_CREATE_GROUP_LOAN);
                }

                updateLoanCreationFormWithActiveClientsOfGroupToSupportGlim(loanActionForm, activeClientsOfGroup);
                storeGlimDataOnSessionForUseInJsp(request, loanCreationGlimDto);
            }
        }

        handleRepaymentsIndependentOfMeetingIfConfigured(request, loanActionForm, customer);

        return mapping.findForward(ActionForwards.getPrdOfferigs_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        // loan fees
        loanActionForm.setAdditionalFees(new ArrayList<FeeView>()); // clear cached additional fees (MIFOS-2547)
        List<FeeView> additionalFees = new ArrayList<FeeView>();
        List<FeeView> defaultFees = new ArrayList<FeeView>();
        getDefaultAndAdditionalFees(loanActionForm.getPrdOfferingIdValue(), getUserContext(request), defaultFees,
                additionalFees);

        LoanOfferingBO loanOffering = getLoanOffering(loanActionForm.getPrdOfferingIdValue(), getUserContext(request)
                .getLocaleId());
        if (AccountingRules.isMultiCurrencyEnabled()) {
            defaultFees = getFilteredFeesByCurrency(defaultFees, loanOffering.getCurrency().getCurrencyId());
            additionalFees = getFilteredFeesByCurrency(additionalFees, loanOffering.getCurrency().getCurrencyId());
        }
        loanActionForm.setDefaultFees(defaultFees);
        SessionUtils.setCollectionAttribute(ADDITIONAL_FEES_LIST, additionalFees, request);
        // end of load fee
        
        // setDateIntoForm
        updateForm(loanOffering, loanActionForm);
        loanActionForm.setInterestRate(getDoubleStringForInterest(loanOffering.getDefInterestRate()));
        loanActionForm.setIntDedDisbursement(getStringValue(loanOffering.isIntDedDisbursement()));
        loanActionForm.setGracePeriodDuration(getStringValue(loanOffering.getGracePeriodDuration()));
        loanActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(),
                SessionUtils.getAttribute(PROPOSED_DISBURSAL_DATE, request).toString()));
        // clean up the cached externalId from previous loan creation
        // if required loanActionForm.clear() will be created
        loanActionForm.setExternalId(null);
        // end of setDateIntoForm

        loadMasterData(request);

        // loadCreateCustomFields
        loadCustomFieldDefinitions(request);
        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CUSTOM_FIELDS, request);
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

        for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
            if (StringUtils.isNotBlank(fieldDef.getDefaultValue())
                    && fieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                customFields.add(new CustomFieldView(fieldDef.getFieldId(), DateUtils.getUserLocaleDate(getUserContext(
                        request).getPreferredLocale(), fieldDef.getDefaultValue()), fieldDef.getFieldType()));
            } else {
                customFields.add(new CustomFieldView(fieldDef.getFieldId(), fieldDef.getDefaultValue(), fieldDef
                        .getFieldType()));
            }
        }
        loanActionForm.setCustomFields(customFields);
        // end of loadCreateCustomFields

        MeetingDetailsEntity loanOfferingMeetingDetail = loanOffering.getLoanOfferingMeeting().getMeeting()
                .getMeetingDetails();
        RecurrenceType recurrenceType = loanOfferingMeetingDetail.getRecurrenceTypeEnum();

        SessionUtils.setAttribute(RECURRENCEID, recurrenceType.getValue(), request);
        request.setAttribute(RECURRENCEID, recurrenceType.getValue());
        Object object = request.getSession().getAttribute(Constants.BUSINESS_KEY);
        CustomerBO customerBO = null;
        if (object instanceof CustomerBO) {
            customerBO = (CustomerBO) object;
        }
        if (customerBO == null) {
            customerBO = (CustomerBO) SessionUtils.getAttribute(LOANACCOUNTOWNER, request);

        }
        if (configService.isRepaymentIndepOfMeetingEnabled()) {
            setRepaymentDayFieldsOnFormForLoad(loanActionForm, loanOfferingMeetingDetail, 
                    customerBO.getCustomerMeeting().getMeeting().getMeetingDetails());
        }

        SessionUtils.removeAttribute(LOANOFFERING, request);
        SessionUtils.setAttribute(LOANOFFERING, loanOffering, request);
        SessionUtils.setCollectionAttribute(LOANFUNDS, getFunds(loanOffering), request);
        storeRedoLoanSettingOnRequestForUseInJspIfPerspectiveParamaterOnQueryString(request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    private void setRepaymentDayFieldsOnFormForLoad(LoanAccountActionForm loanActionForm, MeetingDetailsEntity meetingDetail, 
            MeetingDetailsEntity customerMeetingDetail) {
        loanActionForm.setMonthDay("");
        loanActionForm.setMonthWeek("0");
        loanActionForm.setMonthRank("0");

        if (meetingDetail.getRecurrenceTypeEnum() == RecurrenceType.MONTHLY) {
            setMonthlySchedule(loanActionForm, meetingDetail);
        } else {
            setWeeklySchedule(loanActionForm, customerMeetingDetail);
        }
    }
    
    private List<FeeView> getFilteredFeesByCurrency(List<FeeView> defaultFees, Short currencyId) {
        List<FeeView> filteredFees = new ArrayList<FeeView>();
        for(FeeView feeView:defaultFees) {
            if(feeView.isValidForCurrency(currencyId)) {
                filteredFees.add(feeView);
            }
        }
        return filteredFees;
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
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        CustomerBO customer = getCustomer(request);

        if (configService.isGlimEnabled() && customer.isGroup()) {
            setGlimEnabledSessionAttributes(request, customer);
            double totalAmount = calculateTotalAmountForGlim(loanActionForm.getClients(), loanActionForm
                    .getClientDetails());
            loanActionForm.setLoanAmount(Double.toString(totalAmount));
        }

        LoanBO loan = constructLoan(loanActionForm, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        List<RepaymentScheduleInstallment> installments = getLoanSchedule(loan);
        SessionUtils.setCollectionAttribute(REPAYMENTSCHEDULEINSTALLMENTS, installments, request);
        String perspective = request.getParameter(PERSPECTIVE);
        if (perspective != null) {
            request.setAttribute(PERSPECTIVE, request.getParameter(PERSPECTIVE));
        }
        loanActionForm.initializeTransactionFields(getUserContext(request), installments);

        boolean isPendingApprovalDefined = ProcessFlowRules.isLoanPendingApprovalStateEnabled();
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, isPendingApprovalDefined, request);
        if (new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled()) {
            checkIntervalBetweenTwoDates(getTheFirstRepaymentDay(installments), loanActionForm
                    .getDisbursementDateValue(getUserContext(request).getPreferredLocale()));
        }
        return mapping.findForward(ActionForwards.schedulePreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws PageExpiredException, AccountException, CustomerException,
            ServiceException, PersistenceException, NumberFormatException, MeetingException, InvalidDateException {
        LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
        String perspective = loanAccountForm.getPerspective();
        if (perspective != null) {
            if (perspective.equals(PERSPECTIVE_VALUE_REDO_LOAN)) {
                LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
                LoanBO loan = redoLoan(loanActionForm, request, SaveLoan.NO, new CustomerPersistence());
                SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
            }

            request.setAttribute(PERSPECTIVE, perspective);

            ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
            CustomerBO customer = getCustomer(loanAccountForm.getCustomerIdValue());
            Integer loanIndividualMonitoringIsEnabled;
            try {
                loanIndividualMonitoringIsEnabled = configurationPersistence.getConfigurationKeyValueInteger(
                        LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
            } catch (PersistenceException e) {
                throw new RuntimeException(e);
            }
            if (null != loanIndividualMonitoringIsEnabled && loanIndividualMonitoringIsEnabled.intValue() != 0) {
                SessionUtils.setAttribute(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, loanIndividualMonitoringIsEnabled
                        .intValue(), request);
                if (customer.getCustomerLevel().isGroup()) {
                    SessionUtils.setAttribute(LOAN_ACCOUNT_OWNER_IS_A_GROUP,
                            LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES, request);
                }
            }

            if (perspective != null) {
                request.setAttribute(PERSPECTIVE, perspective);

                if (null != loanIndividualMonitoringIsEnabled && loanIndividualMonitoringIsEnabled.intValue() != 0
                        && customer.getCustomerLevel().isGroup()) {

                    List<String> ids_clients_selected = loanAccountForm.getClients();

                    List<LoanAccountDetailsViewHelper> loanAccountDetailsView = new ArrayList<LoanAccountDetailsViewHelper>();
                    List<LoanAccountDetailsViewHelper> listdetail = loanAccountForm.getClientDetails();
                    for (String index : ids_clients_selected) {
                        if (isNotEmpty(index)) {
                            LoanAccountDetailsViewHelper tempLoanAccount = new LoanAccountDetailsViewHelper();
                            ClientBO clt = clientBusinessService.getClient(getIntegerValue(index));
                            LoanAccountDetailsViewHelper account = null;
                            for (LoanAccountDetailsViewHelper tempAccount : listdetail) {
                                if (tempAccount.getClientId().equals(index)) {
                                    account = tempAccount;
                                }
                            }
                            tempLoanAccount.setClientId(clt.getGlobalCustNum().toString());
                            tempLoanAccount.setClientName(clt.getDisplayName());
                            tempLoanAccount.setLoanAmount((null != account.getLoanAmount()
                                    && !EMPTY.equals(account.getLoanAmount().toString()) ? account.getLoanAmount()
                                    : "0.0"));

                            List<BusinessActivityEntity> businessActEntity = (List<BusinessActivityEntity>) SessionUtils
                                    .getAttribute("BusinessActivities", request);

                            String businessActName = null;
                            for (ValueListElement busact : businessActEntity) {

                                if (busact.getId().toString().equals(account.getBusinessActivity())) {
                                    businessActName = busact.getName();

                                }
                            }
                            tempLoanAccount.setBusinessActivity(account.getBusinessActivity());
                            tempLoanAccount
                                    .setBusinessActivityName((StringUtils.isNotBlank(businessActName) ? businessActName
                                            : "-").toString());
                            tempLoanAccount.setGovermentId((StringUtils.isNotBlank(clt.getGovernmentId()) ? clt
                                    .getGovernmentId() : "-").toString());
                            loanAccountDetailsView.add(tempLoanAccount);
                        }
                    }
                    SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanAccountDetailsView, request);
                }
            }
        }
        return mapping.findForward(ActionForwards.preview_success.toString());

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getInstallmentDetails(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        Integer accountId = Integer.valueOf(request.getParameter(ACCOUNT_ID));
        LoanBO loanBO = loanBusinessService.getAccount(accountId);
        ViewInstallmentDetails viewUpcomingInstallmentDetails = getUpcomingInstallmentDetails(loanBO
                .getDetailsOfNextInstallment(), loanBO.getCurrency());
        ViewInstallmentDetails viewOverDueInstallmentDetails = getOverDueInstallmentDetails(loanBO
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

            List<LoanAccountDetailsViewHelper> loanAccountDetailsViewList = new ArrayList<LoanAccountDetailsViewHelper>();

            for (LoanBO individualLoan : individualLoans) {
                LoanAccountDetailsViewHelper loandetails = new LoanAccountDetailsViewHelper();
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
        boolean activeSurveys = surveysPersistence.retrieveSurveysByTypeAndState(SurveyType.LOAN, SurveyState.ACTIVE)
                .size() > 0;
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

    private double calculateTotalAmountForGlim(final List<String> ids_clients_selected,
            final List<LoanAccountDetailsViewHelper> clientDetails) {
        double totalAmount = new Double(0);
        for (LoanAccountDetailsViewHelper loanAccount : clientDetails) {
            if (ids_clients_selected.contains(loanAccount.getClientId())) {
                if (loanAccount.getLoanAmount() != null) {
                    totalAmount = totalAmount
                            + new LocalizationConverter().getDoubleValueForCurrentLocale(loanAccount.getLoanAmount());
                }
            }

        }
        return totalAmount;
    }

    private static boolean checkIntervalBetweenTwoDates(final Date firstRepaymentDate, final Date disbursementDate)
            throws PersistenceException, AccountException {
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        Integer minDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();
        Integer maxDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();
        if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils.getDateWithoutTimeStamp(firstRepaymentDate), DateUtils
                .getDateWithoutTimeStamp(disbursementDate)) < minDaysInterval) {
            throw new AccountException(MIN_RANGE_IS_NOT_MET, new String[] { minDaysInterval.toString() });

        } else if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils.getDateWithoutTimeStamp(firstRepaymentDate),
                DateUtils.getDateWithoutTimeStamp(disbursementDate)) > maxDaysInterval) {
            throw new AccountException(MAX_RANGE_IS_NOT_MET, new String[] { maxDaysInterval.toString() });

        }

        return true;
    }

    private Date getTheFirstRepaymentDay(final List<RepaymentScheduleInstallment> installments) {
        for (Iterator<RepaymentScheduleInstallment> iter = installments.iterator(); iter.hasNext();) {
            return iter.next().getDueDate();
        }
        return null;

    }

    private CustomerBO getCustomer(final HttpServletRequest request) throws PageExpiredException, ServiceException {
        CustomerBO oldCustomer = (CustomerBO) SessionUtils.getAttribute(LOANACCOUNTOWNER, request);
        CustomerBO customer = getCustomer(oldCustomer.getCustomerId());
        // TODO: Why are these getters being called without the values used
        // anywhere?
        // To avoid lazy loading?
        customer.getPersonnel().getDisplayName();
        customer.getOffice().getOfficeName();
        // TODO: I'm not sure why we're resetting version number - need to
        // investigate this
        customer.setVersionNo(oldCustomer.getVersionNo());
        return customer;
    }

    private LoanBO constructLoan(final LoanAccountActionForm loanActionForm, final HttpServletRequest request)
            throws AccountException, ServiceException, PageExpiredException, PersistenceException,
            NumberFormatException, MeetingException, InvalidDateException {
        boolean isRepaymentIndepOfMeetingEnabled = configService.isRepaymentIndepOfMeetingEnabled();
        CustomerBO customer = getCustomer(request);
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
    private Date resolveRepaymentStartDate(final Date disbursementDate) throws PersistenceException {
        int minDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();
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
            throws AccountException, PageExpiredException, ServiceException {
        // We're assuming cash disbursal for this situation right now
        loan.disburseLoan(personnel, PaymentTypes.CASH.getValue(), save.equals(SaveLoan.YES));

        List<PaymentDataHtmlBean> paymentDataBeans = loanActionForm.getPaymentDataBeans();
        PaymentData payment;
        CustomerBO customer = getCustomer(request);
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
    public ActionForward previous(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        String perspective = loanActionForm.getPerspective();
        if (perspective != null) {
            request.setAttribute(PERSPECTIVE, perspective);
        }
        CustomerBO customer = getCustomer(((CustomerBO) SessionUtils.getAttribute(LOANACCOUNTOWNER, request))
                .getCustomerId());
        LoanBO loan;
        if (isRedoOperation(perspective)) {
            loan = redoLoan(loanActionForm, request, SaveLoan.YES, new CustomerPersistence());
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        } else {
            checkPermissionForCreate(loanActionForm.getState().getValue(), getUserContext(request), null, customer
                    .getOffice().getOfficeId(), customer.getPersonnel().getPersonnelId());
            loan = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
            loan.save(loanActionForm.getState());

        }
        boolean isRepaymentIndepOfMeetingEnabled = configService.isRepaymentIndepOfMeetingEnabled();
        if (configService.isGlimEnabled() && customer.isGroup()) {
            List<LoanAccountDetailsViewHelper> loanAccountDetailsList = (List<LoanAccountDetailsViewHelper>) SessionUtils
                    .getAttribute("loanAccountDetailsView", request);
            for (LoanAccountDetailsViewHelper loanAccountDetail : loanAccountDetailsList) {
                createIndividualLoanAccount(loanActionForm, loan, isRepaymentIndepOfMeetingEnabled, loanAccountDetail,
                        isRedoOperation(perspective));
            }
        }
        if (isRedoOperation(perspective)) {
            loanDisbursementAndPayments(loanActionForm, request, SaveLoan.YES, loan, getPersonnel(request));
        }

        loanActionForm.setAccountId(loan.getAccountId().toString());
        request.setAttribute("customer", customer);
        request.setAttribute(GLOBAL_ACCOUNT_NUM, loan.getGlobalAccountNum());
        request.setAttribute("loan", loan);
        return mapping.findForward(ActionForwards.create_success.toString());
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
            final boolean isRepaymentIndepOfMeetingEnabled, final LoanAccountDetailsViewHelper loanAccountDetail,
            final boolean isRedoOperation) throws AccountException, ServiceException, PropertyNotFoundException {
        LoanBO individualLoan;
        if (isRedoOperation) {
            individualLoan = LoanBO.redoIndividualLoan(loan.getUserContext(), loan.getLoanOffering(),
                    getCustomerBusinessService().findBySystemId(loanAccountDetail.getClientId()), loanActionForm
                            .getState(), new Money(loan.getCurrency(), loanAccountDetail.getLoanAmount().toString()),
                    loan.getNoOfInstallments(), loan.getDisbursementDate(), false, isRepaymentIndepOfMeetingEnabled,
                    loan.getInterestRate(), loan.getGracePeriodDuration(), loan.getFund(), new ArrayList<FeeView>(),
                    new ArrayList<CustomFieldView>());

        } else {
            individualLoan = LoanBO.createIndividualLoan(loan.getUserContext(), loan.getLoanOffering(),
                    getCustomerBusinessService().findBySystemId(loanAccountDetail.getClientId()), loanActionForm
                            .getState(), new Money(loan.getCurrency(), loanAccountDetail.getLoanAmount().toString()),
                    loan.getNoOfInstallments(), loan.getDisbursementDate(), false, isRepaymentIndepOfMeetingEnabled,
                    loan.getInterestRate(), loan.getGracePeriodDuration(), loan.getFund(), new ArrayList<FeeView>(),
                    new ArrayList<CustomFieldView>());
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

        handleRepaymentsIndependentOfMeetingIfConfigured(request, loanActionForm, customer);

        LoanBO loanBO = getLoanBO(request);
        loanBO.setUserContext(getUserContext(request));
        SessionUtils.setAttribute(PROPOSED_DISBURSAL_DATE, loanBO.getDisbursementDate(), request);
        List<ValueListElement> businessActivitiesFromDatabase = getBusinessActivitiesFromDatabase(request);

        SessionUtils.removeAttribute(LOANOFFERING, request);
        LoanOfferingBO loanOffering = getLoanOffering(loanBO.getLoanOffering().getPrdOfferingId(), getUserContext(
                request).getLocaleId());
        loanActionForm.setInstallmentRange(loanBO.getMaxMinNoOfInstall());
        loanActionForm.setLoanAmountRange(loanBO.getMaxMinLoanAmount());
        loanActionForm.setLoanAmountRange(loanBO.getMaxMinLoanAmount());
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
            List<LoanAccountDetailsViewHelper> clientDetails = populateClientDetailsFromLoan(activeClientsUnderGroup,
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
            final List<LoanAccountDetailsViewHelper> clientDetails) {
        List<String> clientIds = new ArrayList<String>();
        for (final LoanAccountDetailsViewHelper clientDetail : clientDetails) {
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

    List<LoanAccountDetailsViewHelper> populateClientDetailsFromLoan(final List<ClientBO> activeClientsUnderGroup,
            final List<LoanBO> individualLoans, final List<ValueListElement> businessActivities)
            throws ServiceException {
        List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
        for (final ClientBO client : activeClientsUnderGroup) {
            LoanAccountDetailsViewHelper clientDetail = new LoanAccountDetailsViewHelper();
            clientDetail.setClientId(getStringValue(client.getCustomerId()));
            clientDetail.setClientName(client.getDisplayName());
            LoanBO loanAccount = (LoanBO) CollectionUtils.find(individualLoans, new Predicate() {
                public boolean evaluate(final Object object) {
                    return client.getCustomerId().equals(((LoanBO) object).getCustomer().getCustomerId());
                }

            });
            if (loanAccount != null) {
                final Integer businessActivityId = loanAccount.getBusinessActivityId();
                clientDetail.setBusinessActivity(Integer.toString(businessActivityId));

                ValueListElement businessActivityElement = (ValueListElement) CollectionUtils.find(businessActivities,
                        new Predicate() {

                            public boolean evaluate(final Object object) {
                                return ((ValueListElement) object).getId().equals(businessActivityId);
                            }

                        });
                if (businessActivityElement != null) {
                    clientDetail.setBusinessActivityName(businessActivityElement.getName());
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
        if (null !=  getFund(loanAccountForm)) {
            request.setAttribute("sourceOfFunds", getFund(loanAccountForm).getFundName());
        }

        resetBusinessActivity(request, localeId, (LoanAccountActionForm) form);
        return mapping.findForward(ActionForwards.managepreview_success.toString());
    }

    private FundBO getFund(LoanAccountActionForm loanAccountActionForm) throws PersistenceException {
        FundBO fund = null;
        if (!StringUtils.isBlank(loanAccountActionForm.getLoanOfferingFund())) {
            Short fundId = loanAccountActionForm.getLoanOfferingFundValue();
            if (fundId != 0)
                fund = new FundPersistence().getFund(fundId);
        }
        return fund;
    }

    private void performGlimSpecificOnManagePreview(final HttpServletRequest request,
            final LoanAccountActionForm loanAccountForm, final Short localeId) throws ServiceException,
            PageExpiredException {
        CustomerBO customer = getCustomer(loanAccountForm.getCustomerIdValue());
        setGlimEnabledSessionAttributes(request, customer);
        if (customer.isGroup()) {
            List<LoanAccountDetailsViewHelper> loanAccountDetailsView = populateDetailsForSelectedClients(localeId,
                    loanAccountForm.getClientDetails(), loanAccountForm.getClients());
            SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanAccountDetailsView, request);
        }
    }

    private List<LoanAccountDetailsViewHelper> populateDetailsForSelectedClients(final Short localeId,
            final List<LoanAccountDetailsViewHelper> clientDetails, final List<String> selectedClients)
            throws ServiceException {
        List<LoanAccountDetailsViewHelper> loanAccountDetailsView = new ArrayList<LoanAccountDetailsViewHelper>();
        for (final String clientId : selectedClients) {
            if (StringUtils.isNotEmpty(clientId)) {
                LoanAccountDetailsViewHelper matchingClientDetail = (LoanAccountDetailsViewHelper) CollectionUtils
                        .find(clientDetails, new Predicate() {
                            public boolean evaluate(final Object object) {
                                return ((LoanAccountDetailsViewHelper) object).getClientId().equals(clientId);
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

    private void setGovernmentIdAndPurpose(final LoanAccountDetailsViewHelper clientDetail, final Short localeId)
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
        CustomerBO customer = loanBOInSession.getCustomer();
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
            List<LoanAccountDetailsViewHelper> loanAccountDetailsList = (List<LoanAccountDetailsViewHelper>) SessionUtils
                    .getAttribute("loanAccountDetailsView", request);
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
            final List<LoanAccountDetailsViewHelper> loanAccountDetailsList, final List<LoanBO> individualLoans)
            throws AccountException, ServiceException, PropertyNotFoundException {
        List<Integer> foundLoans = new ArrayList<Integer>();
        for (final LoanAccountDetailsViewHelper loanAccountDetail : loanAccountDetailsList) {
            LoanBO individualLoan = (LoanBO) CollectionUtils.find(individualLoans, new Predicate() {

                public boolean evaluate(final Object object) {
                    return ((LoanBO) object).getCustomer().getGlobalCustNum().equals(loanAccountDetail.getClientId());
                }

            });
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

    private void updateForm(final LoanOfferingBO loanOffering, final LoanAccountActionForm loanAccountActionForm)
            throws Exception {
        CustomerBO customer = getCustomer(loanAccountActionForm.getCustomerIdValue());
        LoanAmountOption eligibleLoanAmount = loanOffering.eligibleLoanAmount(customer.getMaxLoanAmount(loanOffering),
                customer.getMaxLoanCycleForProduct(loanOffering));
        loanAccountActionForm.setLoanAmountRange(eligibleLoanAmount);
        loanAccountActionForm.setLoanAmount(getDoubleStringForMoney(eligibleLoanAmount.getDefaultLoanAmount()));
        LoanOfferingInstallmentRange eligibleNoOfInstall = loanOffering.eligibleNoOfInstall(customer
                .getMaxLoanAmount(loanOffering), customer.getMaxLoanCycleForProduct(loanOffering));
        loanAccountActionForm.setInstallmentRange(eligibleNoOfInstall);
        loanAccountActionForm.setNoOfInstallments(getStringValue(eligibleNoOfInstall.getDefaultNoOfInstall()));
        loanAccountActionForm.setBusinessActivityId(null);
        loanAccountActionForm.setCollateralTypeId(null);
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

    protected void getDefaultAndAdditionalFees(final Short loanOfferingId, final UserContext userContext,
            final List<FeeView> defaultFees, final List<FeeView> additionalFees) throws ServiceException {
        loanProductService.getDefaultAndAdditionalFees(loanOfferingId, userContext, defaultFees, additionalFees);
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

    private List<RepaymentScheduleInstallment> getLoanSchedule(final LoanBO loan) {
        List<RepaymentScheduleInstallment> schedule = new ArrayList<RepaymentScheduleInstallment>();
        for (AccountActionDateEntity actionDate : loan.getAccountActionDates()) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) actionDate;
            schedule.add(getRepaymentScheduleInstallment(loanSchedule));
        }
        Collections.sort(schedule, new Comparator<RepaymentScheduleInstallment>() {
            public int compare(final RepaymentScheduleInstallment act1, final RepaymentScheduleInstallment act2) {
                return act1.getInstallment().compareTo(act2.getInstallment());
            }
        });
        return schedule;
    }

    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(final LoanScheduleEntity loanSchedule) {
        return new RepaymentScheduleInstallment(loanSchedule.getInstallmentId(), loanSchedule.getActionDate(),
                loanSchedule.getPrincipal(), loanSchedule.getInterest(), loanSchedule.getTotalFeeDue(), loanSchedule
                        .getMiscFee(), loanSchedule.getMiscPenalty());
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

    private ViewInstallmentDetails getUpcomingInstallmentDetails(
            final AccountActionDateEntity upcomingAccountActionDate, final MifosCurrency currency) {
        if (upcomingAccountActionDate != null) {
            LoanScheduleEntity upcomingInstallment = (LoanScheduleEntity) upcomingAccountActionDate;
            return new ViewInstallmentDetails(upcomingInstallment.getPrincipalDue(), upcomingInstallment
                    .getInterestDue(), upcomingInstallment.getTotalFeeDueWithMiscFeeDue(), upcomingInstallment
                    .getPenaltyDue());
        }
        return new ViewInstallmentDetails(new Money(currency), new Money(currency), new Money(currency), new Money(
                currency));
    }

    private ViewInstallmentDetails getOverDueInstallmentDetails(
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
        return new ViewInstallmentDetails(principalDue, interestDue, feesDue, penaltyDue);
    }

    protected void checkPermissionForCreate(final Short newState, final UserContext userContext,
            final Short flagSelected, final Short officeId, final Short loanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(newState, userContext, officeId, loanOfficerId, true)) {
            throw new AccountException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(final Short newSate, final UserContext userContext, final Short officeId,
            final Short loanOfficerId, final boolean saveFlag) {
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId,
                        loanOfficerId));
    }

    private void loadCustomFieldDefinitions(final HttpServletRequest request) throws Exception {
        SessionUtils.setCollectionAttribute(CUSTOM_FIELDS, getAccountBusinessService().retrieveCustomFieldsDefinition(
                EntityType.LOAN), request);
    }

    private List<CustomFieldView> createCustomFieldViews(final Set<AccountCustomFieldEntity> customFieldEntities,
            final HttpServletRequest request) throws ApplicationException {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(CUSTOM_FIELDS, request);
        Locale locale = getUserContext(request).getPreferredLocale();
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            for (AccountCustomFieldEntity customFieldEntity : customFieldEntities) {
                if (customFieldDef.getFieldId().equals(customFieldEntity.getFieldId())) {
                    if (customFieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                        customFields.add(new CustomFieldView(customFieldEntity.getFieldId(), DateUtils
                                .getUserLocaleDate(locale, customFieldEntity.getFieldValue()), customFieldDef
                                .getFieldType()));
                    } else {
                        customFields.add(new CustomFieldView(customFieldEntity.getFieldId(), customFieldEntity
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

    private void setGlimEnabledSessionAttributes(final HttpServletRequest request, final CustomerBO customer)
            throws PageExpiredException {
        storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED,
                LoanConstants.GLIM_ENABLED_VALUE);
        if (customer.isGroup()) {
            storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOAN_ACCOUNT_OWNER_IS_A_GROUP,
                    LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);
        }
    }

    private void updateLoanCreationFormWithActiveClientsOfGroupToSupportGlim(
            final LoanAccountActionForm loanActionForm, final List<ClientBO> activeClientsOfGroup) {
        List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
        for (ClientBO client : activeClientsOfGroup) {
            LoanAccountDetailsViewHelper clientDetail = new LoanAccountDetailsViewHelper();
            clientDetail.setClientId(getStringValue(client.getCustomerId()));
            clientDetail.setClientName(client.getDisplayName());

            clientDetails.add(clientDetail);
        }
        loanActionForm.setClientDetails(clientDetails);
    }

    private void storeGlimDataOnSessionForUseInJsp(final HttpServletRequest request,
            final LoanCreationGlimDto loanCreationGlimDto) throws PageExpiredException {
        storeCollectionOnSessionForUseInJspPage(request, MasterConstants.BUSINESS_ACTIVITIES, loanCreationGlimDto
                .getLoanPurposes());
        storeCollectionOnSessionForUseInJspPage(request, LoanConstants.CLIENT_LIST, loanCreationGlimDto
                .getActiveClientsOfGroup());
        storeObjectOnSessionForUseInJspPage(request, "clientListSize", loanCreationGlimDto.getActiveClientsOfGroup()
                .size());
    }

    private void handleRepaymentsIndependentOfMeetingIfConfigured(final HttpServletRequest request,
            final LoanAccountActionForm loanActionForm, final CustomerBO customer) throws ServiceException,
            PageExpiredException, Exception {

        if (configService.isRepaymentIndepOfMeetingEnabled()) {

            storeObjectOnSessionForUseInJspPage(request,
                    LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED, Integer.valueOf(1));
            storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOANACCOUNTOWNERISACLIENT,
                    LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);

            // FIXME - keithw - prevent real domain objects from being pushed up
            // to presentation
            storeCollectionOnSessionForUseInJspPage(request, MeetingConstants.WEEKDAYSLIST, new FiscalCalendarRules()
                    .getWorkingDays());

            try {
                List<MasterDataEntity> rankOfDays = new MasterPersistence().retrieveMasterEntities(
                        RankOfDaysEntity.class, Short.valueOf("1"));
                storeCollectionOnSessionForUseInJspPage(request, MeetingConstants.WEEKRANKLIST, rankOfDays);
            } catch (PersistenceException e) {
                throw new MifosRuntimeException(e);
            }

            loanActionForm.setRecurMonth(customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter()
                    .toString());
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
