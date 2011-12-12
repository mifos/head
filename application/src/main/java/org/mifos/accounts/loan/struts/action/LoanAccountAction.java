/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.apache.commons.lang.StringUtils.isNotBlank;
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
import static org.mifos.accounts.loan.util.helpers.LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.PROPOSED_DISBURSAL_DATE;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECENTACCOUNTACTIVITIES;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECURRENCEID;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECURRENCENAME;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.STATUS_HISTORY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.TOTAL_AMOUNT_OVERDUE;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.VIEW_UPCOMING_INSTALLMENT_DETAILS;
import static org.mifos.accounts.loan.util.helpers.RequestConstants.PERSPECTIVE;
import static org.mifos.framework.util.helpers.Constants.BUSINESS_KEY;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.business.CashFlowDataAdaptor;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.MaxMinInterestRate;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.LoanScheduleGenerationDto;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.persistance.LoanDaoHibernate;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.struts.action.AccountAppAction;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.cashflow.struts.CashFlowAdaptor;
import org.mifos.application.cashflow.struts.CashFlowCaptor;
import org.mifos.application.cashflow.struts.DefaultCashFlowServiceLocator;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
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
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireAction;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.questionnaire.struts.QuestionnaireServiceFacadeLocator;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.LoanCreationLoanScheduleDetailsDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.CashFlowDataDto;
import org.mifos.dto.screen.LoanAccountInfoDto;
import org.mifos.dto.screen.LoanAccountMeetingDto;
import org.mifos.dto.screen.LoanCreationGlimDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationPreviewDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.dto.screen.LoanScheduledInstallmentDto;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.helpers.Transformer;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.platform.validations.Errors;
import org.mifos.reports.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;

/**
 * Creation and management of loan accounts.
 * <p/>
 * The "repayment day" form fields provided by the frontend and manipulated in the form by this class are somewhat
 * confusing. Here's an attempt to add some clarity.
 * <p/>
 * <h3>required for both weekly and monthly recurrence</h3>
 * <ul>
 * <li>frequency
 * <ul>
 * <li>ie: "weekly", "monthly" (corresponds to values in {@link RecurrenceType} ).</li>
 * </ul>
 * </li>
 * </ul>
 * <p/>
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
 * <p/>
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
public class LoanAccountAction extends AccountAppAction implements QuestionnaireAction {

    private final LoanBusinessService loanBusinessService;
    private final LoanPrdBusinessService loanPrdBusinessService;
    private final ConfigurationPersistence configurationPersistence;
    private final ConfigurationBusinessService configService;
    private final GlimLoanUpdater glimLoanUpdater;

    public static final String CUSTOMER_ID = "customerId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String GLOBAL_ACCOUNT_NUM = "globalAccountNum";

    private QuestionnaireServiceFacadeLocator questionnaireServiceFacadeLocator;
    private QuestionGroupFilterForLoan questionGroupFilter;
    private QuestionnaireFlowAdapter createLoanQuestionnaire;
    private CashFlowAdaptor cashFlowAdaptor =
            new CashFlowAdaptor(ActionForwards.capture_cash_flow.toString(), new DefaultCashFlowServiceLocator());
    private CashFlowDataAdaptor cashFlowDataAdaptor = new CashFlowDataAdaptor();
    private static final String SHOW_PREVIEW = "loanAccountAction.do?method=showPreview";
    private static final String CUSTOMER_SEARCH_URL = "custSearchAction.do?method=loadMainSearch";

    public LoanAccountAction() {
        this(new ConfigurationBusinessService(), ApplicationContextProvider.getBean(LoanBusinessService.class), new GlimLoanUpdater(),
                new LoanPrdBusinessService(),
                new ConfigurationPersistence(), new AccountBusinessService());
    }

    public LoanAccountAction(final ConfigurationBusinessService configService,
                             final LoanBusinessService loanBusinessService, final GlimLoanUpdater glimLoanUpdater,
                             final LoanPrdBusinessService loanPrdBusinessService,
                             final ConfigurationPersistence configurationPersistence,
                             final AccountBusinessService accountBusinessService) {
        super(accountBusinessService);

        this.configService = configService;
        this.loanBusinessService = loanBusinessService;
        this.glimLoanUpdater = glimLoanUpdater;
        this.loanPrdBusinessService = loanPrdBusinessService;
        this.configurationPersistence = configurationPersistence;
        this.questionGroupFilter = new QuestionGroupFilterForLoan();
        this.questionnaireServiceFacadeLocator = new DefaultQuestionnaireServiceFacadeLocator();
        this.createLoanQuestionnaire = getCreateLoanQuestionnaire();
    }

    QuestionnaireFlowAdapter getCreateLoanQuestionnaire() {
        return new QuestionnaireFlowAdapter("Create", "Loan", ActionForwards.schedulePreview_success,
                "custSearchAction.do?method=loadMainSearch", questionnaireServiceFacadeLocator, questionGroupFilter);
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
        this(configService, loanBusinessService, glimLoanUpdater, new LoanPrdBusinessService(), new ConfigurationPersistence(),
                new AccountBusinessService());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getPrdOfferings(final ActionMapping mapping, final ActionForm form,
                                         final HttpServletRequest request,
                                         @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        try {
            final LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
    
            Integer customerId = loanActionForm.getCustomerIdValue();
            LoanCreationProductDetailsDto loanCreationProductDetailsDto = this.loanAccountServiceFacade.retrieveGetProductDetailsForLoanAccountCreation(customerId);
            
            if (loanCreationProductDetailsDto.getErrors().hasErrors()) {
                request.setAttribute(METHODCALLED, "getPrdOfferings");
                throw new ApplicationException(GroupConstants.IMPOSSIBLE_TO_CREATE_GROUP_LOAN);
            }
    
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
    
                    CustomerBO customer = this.customerDao.findCustomerById(customerId);
                    final List<ClientBO> activeClientsOfGroup = this.customerDao.findActiveClientsUnderGroup(customer);
    
                    storeCollectionOnSessionForUseInJspPage(request, LoanConstants.CLIENT_LIST, activeClientsOfGroup);
                    storeObjectOnSessionForUseInJspPage(request, "clientListSize", activeClientsOfGroup.size());
                }
            }
    
            handleRepaymentsIndependentOfMeetingIfConfigured(request, loanActionForm, loanCreationProductDetailsDto.getRecurMonth());
    
            return mapping.findForward(ActionForwards.getPrdOfferigs_success.toString());
        } catch (BusinessRuleException e) {
            request.setAttribute(METHODCALLED, "getPrdOfferings");
            throw new ApplicationException(e.getMessageKey(), e);
        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                              @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        loanActionForm.clearDetailsForLoan();
        SessionUtils.removeAttribute(LoanConstants.QUESTION_GROUP_INSTANCES, request);

        Integer customerId = loanActionForm.getCustomerIdValue();
        Short productId = loanActionForm.getPrdOfferingIdValue();

        LoanCreationLoanDetailsDto loanCreationDetailsDto = this.loanAccountServiceFacade.retrieveLoanDetailsForLoanAccountCreation(customerId, productId, false);

        MeetingDto meetingDetail = loanCreationDetailsDto.getLoanOfferingMeetingDetail();
        RecurrenceType loanOfferingRecurrence = RecurrenceType.fromInt(meetingDetail.getMeetingDetailsDto().getRecurrenceTypeId().shortValue());

        if (loanCreationDetailsDto.isRepaymentIndependentOfMeetingEnabled()) {
            loanActionForm.setMonthDay("");
            loanActionForm.setMonthWeek("0");
            loanActionForm.setMonthRank("0");

            if (RecurrenceType.MONTHLY.equals(loanOfferingRecurrence)) {
                // 2 is signaled as the schedule is monthly on jsp page (Monthradio button is clicked)
                loanActionForm.setFrequency("2");
                loanActionForm.setRecurMonth(meetingDetail.getMeetingDetailsDto().getEvery().toString());
                loanActionForm.setDayRecurMonth(meetingDetail.getMeetingDetailsDto().getEvery().toString());

                Integer weekOfMonth = meetingDetail.getMeetingDetailsDto().getRecurrenceDetails().getWeekOfMonth();
                if (weekOfMonth != null && weekOfMonth > 0) {
                    // 2 is signaled as the day of week is chosen on jsp page. For ex,
                    // First Monday of every 2 months
                    loanActionForm.setMonthType("2");
                    loanActionForm.setMonthRank(weekOfMonth.toString());
                    loanActionForm.setMonthWeek(meetingDetail.getMeetingDetailsDto().getRecurrenceDetails().getDayOfWeek().toString());

                } else {
                    // 1 is signaled as the day of month is chosen on jsp page. For ex,
                    // 12 th day of every 1 month
                    loanActionForm.setMonthType("1");
                    loanActionForm.setMonthDay(meetingDetail.getMeetingDetailsDto().getRecurrenceDetails().getDayNumber().toString());
                }
            } else {
                MeetingDto customerMeeting = loanCreationDetailsDto.getCustomerMeetingDetail();
                // 1 is signaled as the schedule is weekly on jsp page. Week radio
                // button is clicked
                loanActionForm.setFrequency("1");
                loanActionForm.setRecurWeek(customerMeeting.getMeetingDetailsDto().getEvery().toString());
                loanActionForm.setWeekDay(customerMeeting.getMeetingDetailsDto().getRecurrenceDetails().getDayOfWeek().toString());
            }
        }

        LoanOfferingBO loanProduct = this.loanProductDao.findById(productId.intValue());
        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        LoanAmountOption eligibleLoanAmount = loanProduct.eligibleLoanAmount(customer.getMaxLoanAmount(loanProduct),
                customer.getMaxLoanCycleForProduct(loanProduct));

        loanActionForm.setLoanAmountRange(eligibleLoanAmount);

        LoanOfferingInstallmentRange eligibleNoOfInstall = loanProduct.eligibleNoOfInstall(customer.getMaxLoanAmount(loanProduct), customer.getMaxLoanCycleForProduct(loanProduct));

        String loanAmount = getDoubleStringForMoney(eligibleLoanAmount.getDefaultLoanAmount(), loanProduct.getCurrency());
        loanActionForm.setLoanAmount(loanAmount);
        loanActionForm.setMaxInterestRate(loanProduct.getMaxInterestRate());
        loanActionForm.setMinInterestRate(loanProduct.getMinInterestRate());

        loanActionForm.setInstallmentRange(eligibleNoOfInstall);
        loanActionForm.setNoOfInstallments(getStringValue(eligibleNoOfInstall.getDefaultNoOfInstall()));
        loanActionForm.setInterestRate(getDoubleStringForInterest(loanProduct.getDefInterestRate()));
        loanActionForm.setIntDedDisbursement(getStringValue(loanProduct.isIntDedDisbursement()));
        loanActionForm.setGracePeriodDuration(getStringValue(loanProduct.getGracePeriodDuration()));
        loanActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(),
                SessionUtils.getAttribute(PROPOSED_DISBURSAL_DATE, request).toString()));

        if (isRedoOperation(request.getParameter(PERSPECTIVE))) {
            loanActionForm.setDisbursementDate("");
        }

        loanActionForm.setCustomFields(new ArrayList<CustomFieldDto>());
        UserContext userContext = getUserContext(request);
        List<FeeDto> additionalFees = new ArrayList<FeeDto>();
        List<FeeDto> defaultFees = new ArrayList<FeeDto>();

        new LoanProductService(new LoanPrdBusinessService()).getDefaultAndAdditionalFees(productId, userContext, defaultFees, additionalFees);

        LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext.getLocaleId());

        if (AccountingRules.isMultiCurrencyEnabled()) {
            defaultFees = getFilteredFeesByCurrency(defaultFees, loanOffering.getCurrency().getCurrencyId());
            additionalFees = getFilteredFeesByCurrency(additionalFees, loanOffering.getCurrency().getCurrencyId());
        }

        loanActionForm.setDefaultFees(defaultFees);

        CustomValueDto customValueDto = legacyMasterDao.getLookUpEntity(MasterConstants.COLLATERAL_TYPES);
        List<CustomValueListElementDto> collateralTypes = customValueDto.getCustomValueListElements();

        SessionUtils.setCollectionAttribute(LoanConstants.CUSTOM_FIELDS, new ArrayList<CustomFieldDefinitionEntity>(), request);
        SessionUtils.setCollectionAttribute(ADDITIONAL_FEES_LIST, additionalFees, request);
        SessionUtils.setCollectionAttribute(MasterConstants.COLLATERAL_TYPES, collateralTypes, request);
        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES, loanCreationDetailsDto.getLoanPurposes(), request);

        Short recurrenceType = loanOfferingRecurrence.getValue();
        SessionUtils.setAttribute(RECURRENCEID, recurrenceType, request);
        request.setAttribute(RECURRENCEID, recurrenceType);

        SessionUtils.removeAttribute(LOANOFFERING, request);
        SessionUtils.setAttribute(LOANOFFERING, loanProduct, request);
        SessionUtils.setCollectionAttribute(LOANFUNDS, getFunds(loanOffering), request);

        storeRedoLoanSettingOnRequestForUseInJspIfPerspectiveParamaterOnQueryString(request);
        setVariableInstallmentDetailsOnForm(loanProduct, loanActionForm);

        return mapping.findForward(ActionForwards.load_success.toString());
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

    private List<FeeDto> getFilteredFeesByCurrency(List<FeeDto> defaultFees, Short currencyId) {
        List<FeeDto> filteredFees = new ArrayList<FeeDto>();
        for (FeeDto feeDto : defaultFees) {
            if (feeDto.isValidForCurrency(currencyId)) {
                filteredFees.add(feeDto);
            }
        }
        return filteredFees;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validateInstallments(final ActionMapping mapping, final ActionForm form,
                                              final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response)
            throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        boolean validateInstallmentsPassed = validateInstallments(request, loanActionForm);
        boolean cashFlowBounded = bindCashFlowIfPresent(request, loanActionForm, false);

        ActionForwards forward = ActionForwards.validateInstallments_failure;
        if (validateInstallmentsPassed && cashFlowBounded) {
            forward = validateCashFlowForWarningAndErrors(loanActionForm, request, loanActionForm, ActionForwards.validateInstallments_success, ActionForwards.validateInstallments_failure);
        }

        return mapping.findForward(forward.name());
    }


    private ActionErrors validateCashFlowForInstallmentsForWarnings(LoanAccountActionForm loanActionForm) throws Exception {
        List<CashFlowDataDto> cashFlowDataDtos = loanActionForm.getCashflowDataDtos();
        return getActionErrors(loanAccountServiceFacade.validateCashFlowForInstallmentsForWarnings(cashFlowDataDtos, loanActionForm.getPrdOfferingIdValue().intValue()));
    }

    boolean validateInstallments(HttpServletRequest request, LoanAccountActionForm loanActionForm) throws Exception {
        boolean result = true;
        UserContext userContext = getUserContext(request);

        Integer productId = loanActionForm.getPrdOfferingIdValue().intValue();
        LoanOfferingBO loanOffering = this.loanProductDao.findById(productId);

        if (loanOffering.isVariableInstallmentsAllowed()) {
            List<LoanCreationInstallmentDto> dtoInstallments = new ArrayList<LoanCreationInstallmentDto>();
            List<RepaymentScheduleInstallment> installments = getInstallments(loanActionForm, request);
            for (RepaymentScheduleInstallment repaymentScheduleInstallment : installments) {

                LocalDate dueDate = new LocalDate(repaymentScheduleInstallment.getDueDateValue());
                LoanCreationInstallmentDto installmentDto = new LoanCreationInstallmentDto(repaymentScheduleInstallment.getInstallment(), dueDate,
                        repaymentScheduleInstallment.getPrincipal().getAmount().doubleValue(), repaymentScheduleInstallment.getInterest().getAmount().doubleValue(),
                        repaymentScheduleInstallment.getFees().getAmount().doubleValue(), Double.valueOf("0.0"), repaymentScheduleInstallment.getTotalValue().getAmount().doubleValue());
                dtoInstallments.add(installmentDto);
            }

            VariableInstallmentDetailsBO variableInstallmentDetails = loanOffering.getVariableInstallmentDetails();
            java.sql.Date disbursementDate = loanActionForm.getDisbursementDateValue(userContext.getPreferredLocale());

            Errors errors = loanAccountServiceFacade.validateInputInstallments(disbursementDate, variableInstallmentDetails.getMinGapInDays(), variableInstallmentDetails.getMaxGapInDays(), variableInstallmentDetails.getMinInstallmentAmount().getAmount(), dtoInstallments, loanActionForm.getCustomerIdValue());
            ActionErrors actionErrors = getActionErrors(errors);

            LoanInstallmentsDto loanInstallmentsDto = createLoanInstallmentDto(loanActionForm, installments);
            CashFlowForm cashFlowForm = loanActionForm.getCashFlowForm();

            List<MonthlyCashFlowDto> cashflowDtos = new ArrayList<MonthlyCashFlowDto>();
            BigDecimal totalBalance = BigDecimal.ZERO;
            if (cashFlowForm != null) {
                totalBalance = cashFlowForm.getTotalBalance();
                for (MonthlyCashFlowForm monthlyCashflowform : cashFlowForm.getMonthlyCashFlows()) {

                    MonthlyCashFlowDto monthlyCashFlow = new MonthlyCashFlowDto(monthlyCashflowform.getDateTime(),
                            monthlyCashflowform.getCumulativeCashFlow(), monthlyCashflowform.getNotes(), monthlyCashflowform.getRevenue(), monthlyCashflowform.getExpense());
                    cashflowDtos.add(monthlyCashFlow);
                }
            }
            Errors validationErrors = loanAccountServiceFacade.validateCashFlowForInstallments(loanInstallmentsDto, cashflowDtos, loanOffering.getRepaymentCapacity(), totalBalance);

            ActionErrors cashFlowAndInstallmentDateErrors = getActionErrors(validationErrors);
            actionErrors.add(cashFlowAndInstallmentDateErrors);

            if (actionErrors.isEmpty()) {
                Money loanAmount = new Money(loanOffering.getCurrency(), loanActionForm.getLoanAmountAsBigDecimal());
                loanBusinessService.applyDailyInterestRates(
                        new LoanScheduleGenerationDto(disbursementDate, loanAmount, loanActionForm.getInterestDoubleValue(), installments));
                // TODO need to figure out a way to avoid putting 'installments' onto session - required for mifostabletag in schedulePreview.jsp
                setInstallmentsOnSession(request, loanActionForm);
                actionErrors = getActionErrors(loanAccountServiceFacade.validateInstallmentSchedule(dtoInstallments, variableInstallmentDetails.getMinInstallmentAmount().getAmount()));
                if (!actionErrors.isEmpty()) {
                    addErrors(request, actionErrors);
                    result = false;
                }
            } else {
                addErrors(request, actionErrors);
                result = false;
            }
        }
        return result;
    }

    private LoanInstallmentsDto createLoanInstallmentDto(LoanAccountActionForm loanActionForm, List<RepaymentScheduleInstallment> installments) {
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        BigDecimal loanAmountBigDecimal = loanActionForm.getLoanAmountAsBigDecimal();

        Date firstInstallmentDueDate = new Date();
        Date lastInstallmentDueDate = new Date();
        if (!installments.isEmpty()) {
            firstInstallmentDueDate = installments.get(0).getDueDateValue();
            lastInstallmentDueDate = installments.get(installments.size() - 1).getDueDateValue();

            for (RepaymentScheduleInstallment installment : installments) {
                totalInstallmentAmount = totalInstallmentAmount.add(installment.getTotalValue().getAmount());
            }
        }
        return new LoanInstallmentsDto(loanAmountBigDecimal, totalInstallmentAmount, firstInstallmentDueDate, lastInstallmentDueDate);
    }

    private List<RepaymentScheduleInstallment> getInstallments(LoanAccountActionForm loanActionForm, HttpServletRequest request) {
        List<RepaymentScheduleInstallment> installments;
        if (isRedoOperation(request.getParameter(PERSPECTIVE))) {
            installments = org.mifos.framework.util.CollectionUtils.collect(loanActionForm.getPaymentDataBeans(),
                    new Transformer<PaymentDataHtmlBean, RepaymentScheduleInstallment>() {
                        @Override
                        public RepaymentScheduleInstallment transform(PaymentDataHtmlBean input) {
                            return input.getInstallment();
                        }
                    });
        } else {
            installments = loanActionForm.getInstallments();
        }

        UserContext userContext = getUserContext(request);
        for (RepaymentScheduleInstallment repaymentScheduleInstallment : installments) {
            if (org.springframework.util.StringUtils.hasText(repaymentScheduleInstallment.getDueDate())) {
                DateTimeFormatter formatter = DateTimeFormat.mediumDate();
                DateTime parsedDate = formatter.withLocale(userContext.getCurrentLocale()).parseDateTime(repaymentScheduleInstallment.getDueDate());
                repaymentScheduleInstallment.setDueDateValue(parsedDate.toDateMidnight().toDate());
            }
        }

        return installments;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward showPreview(final ActionMapping mapping, final ActionForm form,
                                     final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "showPreview");
        setPerspectiveOnRequest(request);
        ActionForward forwardAfterCashFlowBinding = cashFlowAdaptor.bindCashFlow((CashFlowCaptor) form,
                ActionForwards.schedulePreview_success.toString(), request.getSession(), mapping);
        boolean addLoanAmountToCashFlow = true;
        if ("edit".equals(request.getParameter("preview_mode"))) {
            addLoanAmountToCashFlow = false;
        }
        bindCashFlowIfPresent(request, form, addLoanAmountToCashFlow);
        return forwardAfterCashFlowBinding;
    }

    private boolean bindCashFlowIfPresent(final HttpServletRequest request, final ActionForm form, boolean addLoanAmountToCashFlow) throws Exception {
        boolean cashFlowBound = false;

        UserContext userContext = getUserContext(request);
        LoanAccountActionForm loanForm = (LoanAccountActionForm) form;
        LoanOfferingBO loanOffering = getLoanOffering(loanForm.getPrdOfferingIdValue(), userContext.getLocaleId());

        if (loanOffering != null && isCashFlowEnabled(request, loanOffering)) {
            cashFlowDataAdaptor.initialize(
                    loanForm.getInstallments(),
                    loanForm.getCashFlowForm().getMonthlyCashFlows(),
                    loanForm.getLoanAmountAsBigDecimal(),
                    loanForm.getDisbursementDateValue(userContext.getPreferredLocale()),
                    userContext.getPreferredLocale(),
                    addLoanAmountToCashFlow);
            loanForm.setCashflowDataDtos(cashFlowDataAdaptor.getCashflowDataDtos());
            cashFlowBound = true;
        }
        return cashFlowBound;
    }

    private boolean isCashFlowEnabled(HttpServletRequest request, LoanOfferingBO loanOffering) {
        return loanOffering.isCashFlowCheckEnabled() && !isRedoOperation(request.getParameter(PERSPECTIVE));
    }

    public ActionForward viewAndEditAdditionalInformation(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        Integer entityId = Integer.valueOf(request.getParameter("entityId"));
        questionGroupFilter.setLoanOfferingBO(getLoan(entityId).getLoanOffering());
        List<QuestionGroupInstanceDetail> questionGroupInstances = createLoanQuestionnaire.
                getQuestionGroupInstances(request, entityId);
        request.getSession().setAttribute(LoanConstants.QUESTION_GROUP_INSTANCES, questionGroupInstances);
        ActionForward forward = mapping.findForward(ActionForwards.viewAndEditAdditionalInformation.toString());
        return new ActionForward(forward.getPath(), forward.getRedirect());
    }


    @TransactionDemarcate(joinToken = true)
    public ActionForward schedulePreview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        Short productId = loanActionForm.getPrdOfferingIdValue();
        LoanOfferingBO loanOffering = this.loanProductDao.findById(productId.intValue());
        setVariableInstallmentDetailsOnForm(loanOffering, loanActionForm);

        CustomerDetailDto oldCustomer = getCustomer(request);
        CustomerBO customer = this.customerDao.findCustomerById(oldCustomer.getCustomerId());

        UserContext userContext = getUserContext(request);
        DateTime disbursementDate = getDisbursementDate(loanActionForm, userContext.getPreferredLocale());
        this.holidayServiceFacade.validateDisbursementDateForNewLoan(customer.getOfficeId(), disbursementDate);

        FundBO fund = getFund(loanActionForm);

        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

        MeetingBO newMeetingForRepaymentDay = null;
        if (isRepaymentIndependentOfMeetingEnabled) {
            newMeetingForRepaymentDay = this.createNewMeetingForRepaymentDay(disbursementDate, loanActionForm, customer);
        }
        LoanCreationLoanScheduleDetailsDto loanScheduleDetailsDto;
        try {
            if (isRedoOperation(request.getParameter(PERSPECTIVE))) {
                LoanBO loan = getLoanForRedoSchedulePreview(loanActionForm, loanOffering, customer,
                        userContext, disbursementDate, fund, isRepaymentIndependentOfMeetingEnabled,
                        newMeetingForRepaymentDay);

                List<RepaymentScheduleInstallment> installments = loanBusinessService.applyDailyInterestRatesWhereApplicable(
                        new LoanScheduleGenerationDto(disbursementDate.toDate(),
                                loan, loanActionForm.isVariableInstallmentsAllowed(), loanActionForm.getLoanAmountValue(),
                                loanActionForm.getInterestDoubleValue()), userContext.getPreferredLocale());

                PersonnelBO personnel = legacyPersonnelDao.getPersonnel(userContext.getId());
                if (personnel == null) {
                    throw new IllegalArgumentException("bad UserContext id");
                }

                List<PaymentDataHtmlBean> paymentDataBeans = new ArrayList<PaymentDataHtmlBean>(installments.size());
                for (RepaymentScheduleInstallment repaymentScheduleInstallment : installments) {
                    paymentDataBeans.add(new PaymentDataHtmlBean(userContext.getPreferredLocale(), personnel,
                            repaymentScheduleInstallment));
                }

                loanScheduleDetailsDto = getLoanScheduleDetailsDto(loanActionForm, disbursementDate,
                        customer, isRepaymentIndependentOfMeetingEnabled,
                        installments, paymentDataBeans);

                loanActionForm.initializeTransactionFields(loanScheduleDetailsDto.getPaymentDataBeans());
            } else {

                LoanBO loan = assembleLoan(userContext, customer, disbursementDate, fund,
                        isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay, loanActionForm);

                Money loanAmount = new Money(loan.getCurrency(), loanActionForm.getLoanAmountAsBigDecimal());
                List<RepaymentScheduleInstallment> installments = loanBusinessService.applyDailyInterestRatesWhereApplicable(
                        new LoanScheduleGenerationDto(disbursementDate.toDate(),
                                loan, loanActionForm.isVariableInstallmentsAllowed(), loanAmount,
                                loanActionForm.getInterestDoubleValue()), userContext.getPreferredLocale());

                loanScheduleDetailsDto = getLoanScheduleDetailsDto(loanActionForm, disbursementDate,
                        customer, isRepaymentIndependentOfMeetingEnabled,
                        installments, new ArrayList<PaymentDataHtmlBean>());

                loanActionForm.initializeInstallments(loanScheduleDetailsDto.getInstallments());
            }
        } finally {
            setPerspectiveOnRequest(request);
        }

        setAttributesForSchedulePreview(request, loanActionForm, disbursementDate, loanScheduleDetailsDto);
        questionGroupFilter.setLoanOfferingBO(loanOffering);
        BigDecimal loanAmountBigDecimal = loanActionForm.getLoanAmountAsBigDecimal();
        ActionForward pageAfterQuestionnaire = getPageAfterQuestionnaire(mapping, request, loanOffering, loanScheduleDetailsDto, cashFlowAdaptor, loanAmountBigDecimal);
        return createLoanQuestionnaire.fetchAppliedQuestions(mapping, loanActionForm, request, ActionForwards.valueOf(pageAfterQuestionnaire.getName()));
    }

    private LoanBO getLoanForRedoSchedulePreview(LoanAccountActionForm loanActionForm, LoanOfferingBO loanOffering, CustomerBO customer, UserContext userContext, DateTime disbursementDate, FundBO fund, boolean repaymentIndependentOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay) throws AccountException {
        Money loanAmount = new Money(loanOffering.getCurrency(), loanActionForm.getLoanAmount());
        Short numOfInstallments = loanActionForm.getNoOfInstallmentsValue();
        boolean isInterestDeductedAtDisbursement = loanActionForm.isInterestDedAtDisbValue();
        Double interest = loanActionForm.getInterestDoubleValue();
        Short gracePeriod = loanActionForm.getGracePeriodDurationValue();

        List<AccountFeesEntity> fees = new ArrayList<AccountFeesEntity>();
        List<FeeDto> accouontFees = loanActionForm.getFeesToApply();
        for (FeeDto accountFee : accouontFees) {
            FeeBO feeEntity = feeDao.findById(Short.valueOf(accountFee.getFeeId()));
            Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(accountFee.getAmount());
            fees.add(new AccountFeesEntity(null, feeEntity, feeAmount));
        }

        Double maxLoanAmount = loanActionForm.getMaxLoanAmountValue();
        Double minLoanAmount = loanActionForm.getMinLoanAmountValue();
        Short maxNumOfInstallments = loanActionForm.getMaxNoInstallmentsValue();
        Short minNumOfShortInstallments = loanActionForm.getMinNoInstallmentsValue();
        String externalId = loanActionForm.getExternalId();
        AccountState accountState = loanActionForm.getState();
        if (accountState == null) {
            accountState = AccountState.LOAN_PARTIAL_APPLICATION;
        }
        // FIXME - decouple ability for displaying repayment schedule from need to use LoanBO
        LoanOfferingBO loanProduct = this.loanProductDao.findBySystemId(loanOffering.getGlobalPrdOfferingNum());
        LoanBO loan = LoanBO.redoLoan(userContext, loanProduct, customer, accountState, loanAmount, numOfInstallments,
                disbursementDate.toDate(), isInterestDeductedAtDisbursement, interest, gracePeriod, fund, fees,
                maxLoanAmount, minLoanAmount, maxNumOfInstallments, minNumOfShortInstallments,
                repaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay);
        loan.setExternalId(externalId);
        return loan;
    }

    private LoanCreationLoanScheduleDetailsDto getLoanScheduleDetailsDto(LoanAccountActionForm loanActionForm,
                                                                         DateTime disbursementDate,
                                                                         CustomerBO customer,
                                                                         boolean repaymentIndependentOfMeetingEnabled, List<RepaymentScheduleInstallment> installments, List<PaymentDataHtmlBean> paymentDataBeans) throws AccountException {
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        LocalizationConverter localizationConverter = new LocalizationConverter();
        LoanCreationLoanScheduleDetailsDto loanScheduleDetailsDto;
        if (repaymentIndependentOfMeetingEnabled) {
            Date firstRepaymentDate = installments.get(0).getDueDateValue();
            validateFirstRepaymentDate(disbursementDate, configurationPersistence, firstRepaymentDate);
        }

        double glimLoanAmount = computeGLIMLoanAmount(loanActionForm, localizationConverter);
        boolean isLoanPendingApprovalDefined = ProcessFlowRules.isLoanPendingApprovalStateEnabled();
        final boolean isGlimApplicable = customer.isGroup() && configurationPersistence.isGlimEnabled();

        loanScheduleDetailsDto = new LoanCreationLoanScheduleDetailsDto(customer.isGroup(), isGlimApplicable, glimLoanAmount,
                isLoanPendingApprovalDefined, installments, paymentDataBeans);
        return loanScheduleDetailsDto;
    }

    private MeetingBO createNewMeetingForRepaymentDay(DateTime disbursementDate,
                                                      final LoanAccountActionForm loanAccountActionForm, final CustomerBO customer) throws NumberFormatException,
            MeetingException {

        MeetingBO newMeetingForRepaymentDay = null;
        Short recurrenceId = Short.valueOf(loanAccountActionForm.getRecurrenceId());

        final int minDaysInterval = new ConfigurationPersistence().getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        final Date repaymentStartDate = disbursementDate.plusDays(minDaysInterval).toDate();

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

    private double computeGLIMLoanAmount(LoanAccountActionForm loanActionForm, LocalizationConverter localizationConverter) {
        double glimLoanAmount = Double.valueOf("0");
        List<LoanAccountDetailsDto> loanAccountDetails = loanActionForm.getClientDetails();
        List<String> clientNames = loanActionForm.getClients();
        for (LoanAccountDetailsDto loanAccount : loanAccountDetails) {
            if (clientNames.contains(loanAccount.getClientId())) {
                if (loanAccount.getLoanAmount() != null) {
                    glimLoanAmount = glimLoanAmount
                            + localizationConverter.getDoubleValueForCurrentLocale(loanAccount.getLoanAmount());
                }
            }
        }
        return glimLoanAmount;
    }

    private void validateFirstRepaymentDate(DateTime disbursementDate, ConfigurationPersistence configurationPersistence, Date firstRepaymentDate) throws AccountException {
        Integer minDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();
        Integer maxDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils.getDateWithoutTimeStamp(firstRepaymentDate),
                DateUtils.getDateWithoutTimeStamp(disbursementDate.toDate())) < minDaysInterval) {
            throw new AccountException(MIN_RANGE_IS_NOT_MET, new String[]{minDaysInterval.toString()});
        } else if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils.getDateWithoutTimeStamp(firstRepaymentDate),
                DateUtils.getDateWithoutTimeStamp(disbursementDate.toDate())) > maxDaysInterval) {
            throw new AccountException(MAX_RANGE_IS_NOT_MET, new String[]{maxDaysInterval.toString()});
        }
    }

    private LoanBO assembleLoan(UserContext userContext, CustomerBO customer, DateTime disbursementDate, FundBO fund,
                                boolean isRepaymentIndependentOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay,
                                LoanAccountActionForm loanActionForm) throws ApplicationException {

        Short productId = loanActionForm.getPrdOfferingIdValue();
        LoanOfferingBO loanOffering = this.loanProductDao.findById(productId.intValue());

        Money loanAmount = new Money(loanOffering.getCurrency(), loanActionForm.getLoanAmount());
        Short numOfInstallments = loanActionForm.getNoOfInstallmentsValue();
        boolean isInterestDeductedAtDisbursement = loanActionForm.isInterestDedAtDisbValue();
        Double interest = loanActionForm.getInterestDoubleValue();
        Short gracePeriod = loanActionForm.getGracePeriodDurationValue();
        List<FeeDto> fees = loanActionForm.getFeesToApply();
        List<CustomFieldDto> customFields = loanActionForm.getCustomFields();
        Double maxLoanAmount = loanActionForm.getMaxLoanAmountValue();
        Double minLoanAmount = loanActionForm.getMinLoanAmountValue();
        Short maxNumOfInstallments = loanActionForm.getMaxNoInstallmentsValue();
        Short minNumOfShortInstallments = loanActionForm.getMinNoInstallmentsValue();
        String externalId = loanActionForm.getExternalId();
        Integer selectedLoanPurpose = loanActionForm.getBusinessActivityIdValue();
        String collateralNote = loanActionForm.getCollateralNote();
        Integer selectedCollateralType = loanActionForm.getCollateralTypeIdValue();
        AccountState accountState = loanActionForm.getState();
        if (accountState == null) {
            accountState = AccountState.LOAN_PARTIAL_APPLICATION;
        }

        LoanBO loan = LoanBO.createLoan(userContext, loanOffering, customer, accountState, loanAmount,
                numOfInstallments, disbursementDate.toDate(), isInterestDeductedAtDisbursement, interest, gracePeriod,
                fund, fees, customFields, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
                minNumOfShortInstallments, isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay);

        loan.setExternalId(externalId);
        loan.setBusinessActivityId(selectedLoanPurpose);
        loan.setCollateralNote(collateralNote);
        loan.setCollateralTypeId(selectedCollateralType);

        return loan;
    }

    private void setAttributesForSchedulePreview(HttpServletRequest request, LoanAccountActionForm loanActionForm, DateTime disbursementDate, LoanCreationLoanScheduleDetailsDto loanScheduleDetailsDto) throws PageExpiredException {
        setGlimOnSession(request, loanActionForm, loanScheduleDetailsDto);
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, loanScheduleDetailsDto.isLoanPendingApprovalDefined(), request);
        SessionUtils.setAttribute(CustomerConstants.DISBURSEMENT_DATE, disbursementDate.toString("yyyy-MM-dd"), request);
        SessionUtils.setAttribute(CustomerConstants.LOAN_AMOUNT, loanActionForm.getLoanAmount(), request);
        SessionUtils.setAttribute(CustomerConstants.INTEREST_RATE, loanActionForm.getInterestRate(), request);
        SessionUtils.setAttribute(CustomerConstants.NO_OF_INSTALLMENTS, loanActionForm.getNoOfInstallments(), request);
        SessionUtils.setAttribute(CustomerConstants.GRACE_PERIOD_DURATION, loanActionForm.getGracePeriodDuration(), request);
        FundBO fund = getFund(loanActionForm);
        SessionUtils.setAttribute(CustomerConstants.FUND_NAME, fund != null ? fund.getFundName() : "", request);
        SessionUtils.setAttribute(CustomerConstants.INTEREST_DEDUCTED_AT_DISBURSEMENT, loanActionForm.getIntDedDisbursement(), request);
        SessionUtils.setAttribute(CustomerConstants.BUSINESS_ACTIVITY_ID, loanActionForm.getBusinessActivityId(), request);
        SessionUtils.setAttribute(CustomerConstants.COLLATERAL_TYPE_ID, loanActionForm.getCollateralTypeId(), request);
        SessionUtils.setAttribute(CustomerConstants.COLLATERAL_NOTE, loanActionForm.getCollateralNote(), request);
        SessionUtils.setAttribute(CustomerConstants.EXTERNAL_ID, loanActionForm.getExternalId(), request);
        List<FeeDto> originalFees = (List<FeeDto>) SessionUtils.getAttribute(ADDITIONAL_FEES_LIST, request);
        for (FeeDto feeDto : loanActionForm.getAdditionalFees()) {
            if (!StringUtils.isEmpty(feeDto.getFeeId())) {
                for (FeeDto originalFee : originalFees) {
                    if (feeDto.getFeeId().equals(originalFee.getFeeId())) {
                        feeDto.setPeriodic(originalFee.isPeriodic());
                        feeDto.setFeeSchedule(originalFee.getFeeSchedule());
                    }
                }
            }
        }
        SessionUtils.setCollectionAttribute(CustomerConstants.ACCOUNT_FEES, loanActionForm.getFeesToApply(), request);
        // TODO need to figure out a way to avoid putting 'installments' onto session - required for mifostabletag in schedulePreview.jsp
        setInstallmentsOnSession(request, loanActionForm);
    }

    // Intentionally made 'public' to aid testing
    // CashFlowAdaptor passed to aid testing

    public ActionForward getPageAfterQuestionnaire(ActionMapping mapping, HttpServletRequest request,
                                                   LoanOfferingBO loanOffering,
                                                   LoanCreationLoanScheduleDetailsDto loanScheduleDetailsDto,
                                                   CashFlowAdaptor cashFlowAdaptor, BigDecimal loanAmount) {
        if (isCashFlowEnabled(request, loanOffering)) {
            return cashFlowAdaptor.renderCashFlow(
                    loanScheduleDetailsDto.firstInstallmentDueDate(),
                    loanScheduleDetailsDto.lastInstallmentDueDate(),
                    SHOW_PREVIEW, CUSTOMER_SEARCH_URL, mapping, request, loanOffering, loanAmount, getUserContext(request).getPreferredLocale());
        }
        return mapping.findForward(ActionForwards.schedulePreview_success.toString());
    }

    private DateTime getDisbursementDate(LoanAccountActionForm loanActionForm, Locale locale) throws InvalidDateException {
        return new DateTime(loanActionForm.getDisbursementDateValue(locale));
    }

    private void setGlimOnSession(HttpServletRequest request, LoanAccountActionForm loanActionForm,
                                  LoanCreationLoanScheduleDetailsDto loanScheduleDetailsDto) throws PageExpiredException {
        if (loanScheduleDetailsDto.isGlimApplicable()) {
            setGlimEnabledSessionAttributes(request, loanScheduleDetailsDto.isGroup());
            loanActionForm.setLoanAmount(Double.toString(loanScheduleDetailsDto.getGlimLoanAmount()));
        }
    }

    private void setPerspectiveOnRequest(HttpServletRequest request) {
        String perspective = request.getParameter(PERSPECTIVE);
        setPerspective(request, perspective);
    }

    private CustomerDetailDto getCustomer(HttpServletRequest request) throws PageExpiredException {
        return (CustomerDetailDto) SessionUtils.getAttribute(LOANACCOUNTOWNER, request);
    }

    private void setVariableInstallmentDetailsOnForm(LoanOfferingBO loanOffering, LoanAccountActionForm loanActionForm) {
        boolean variableInstallmentsAllowed = loanOffering.isVariableInstallmentsAllowed();
        loanActionForm.setVariableInstallmentsAllowed(variableInstallmentsAllowed);
        if (variableInstallmentsAllowed) {
            VariableInstallmentDetailsBO variableInstallmentDetails = loanOffering.getVariableInstallmentDetails();
            loanActionForm.setMinimumGapInDays(variableInstallmentDetails.getMinGapInDays());
            loanActionForm.setMaximumGapInDays(variableInstallmentDetails.getMaxGapInDays());
            loanActionForm.setMinInstallmentAmount(variableInstallmentDetails.getMinInstallmentAmount());
        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                                 @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
        boolean isInstallmentValid = validateInstallments(request, loanAccountForm);
        String perspective = loanAccountForm.getPerspective();
        if (perspective != null) {
            setPerspective(request, perspective);
            Integer customerId = loanAccountForm.getCustomerIdValue();

            if (perspective.equals(PERSPECTIVE_VALUE_REDO_LOAN) && isInstallmentValid) {
                UserContext userContext = getUserContext(request);
                DateTime disbursementDate = getDisbursementDate(loanAccountForm, userContext.getPreferredLocale());

                CustomerBO customer = this.customerDao.findCustomerById(customerId);
                LoanBO loan = redoLoan(customer, loanAccountForm, disbursementDate, userContext);
                loanBusinessService.computeExtraInterest(loan, DateUtils.currentDate());
                String loanDisbursementDate = DateUtils.getUserLocaleDate(null, disbursementDate.toDate());
                SessionUtils.setAttribute("loanDisbursementDate", loanDisbursementDate, request);
                SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
            }

            List<LoanAccountDetailsDto> accountDetails = loanAccountForm.getClientDetails();
            List<String> selectedClientIds = loanAccountForm.getClients();

            LoanCreationPreviewDto loanPreviewDto = getLoanCreatePreviewDto(customerId, accountDetails, selectedClientIds);

            List<BusinessActivityEntity> businessActEntity = retrieveLoanPurposesFromSession(request);
            for (LoanAccountDetailsDto loanAccountDetailsView : loanPreviewDto.getLoanAccountDetailsView()) {
                String businessActName = null;
                for (ValueListElement busact : businessActEntity) {
                    if (busact.getId().toString().equals(loanAccountDetailsView.getBusinessActivity())) {
                        businessActName = busact.getName();
                    }
                }
                loanAccountDetailsView.setBusinessActivityName((isNotBlank(businessActName) ? businessActName : "-").toString());
            }

            if (loanPreviewDto.isGlimEnabled()) {
                SessionUtils.setAttribute(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, 1, request);

                if (loanPreviewDto.isGroup()) {
                    SessionUtils.setAttribute(LOAN_ACCOUNT_OWNER_IS_A_GROUP, LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES, request);
                    SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanPreviewDto.getLoanAccountDetailsView(), request);
                }
            }
        }
        // TODO need to figure out a way to avoid putting 'installments' onto session - required for mifostabletag in createloanpreview.jsp
        setInstallmentsOnSession(request, loanAccountForm);
        ActionForwards forward = validateCashFlow(form, request, loanAccountForm, isInstallmentValid);
        return mapping.findForward(forward.name());
    }

    // Method exists to support unit testing
    LoanCreationPreviewDto getLoanCreatePreviewDto(Integer customerId, List<LoanAccountDetailsDto> accountDetails, List<String> selectedClientIds) {
        return loanAccountServiceFacade.previewLoanCreationDetails(customerId, accountDetails, selectedClientIds);
    }

    private void setPerspective(HttpServletRequest request, String perspective) {
        if (perspective != null) {
            request.setAttribute(PERSPECTIVE, perspective);
        }
    }

    LoanBO redoLoan(CustomerBO customer, LoanAccountActionForm loanAccountActionForm, DateTime disbursementDate, UserContext userContext) {

        try {
            boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

            MeetingBO newMeetingForRepaymentDay = null;
            if (isRepaymentIndepOfMeetingEnabled) {
                newMeetingForRepaymentDay = createNewMeetingForRepaymentDay(disbursementDate, loanAccountActionForm, customer);
            }

            Short productId = loanAccountActionForm.getPrdOfferingIdValue();

            LoanOfferingBO loanOffering = this.loanProductDao.findById(productId.intValue());

            Money loanAmount = new Money(loanOffering.getCurrency(), loanAccountActionForm.getLoanAmount());
            Short numOfInstallments = loanAccountActionForm.getNoOfInstallmentsValue();
            boolean isInterestDeductedAtDisbursement = loanAccountActionForm.isInterestDedAtDisbValue();
            Double interest = loanAccountActionForm.getInterestDoubleValue();
            Short gracePeriod = loanAccountActionForm.getGracePeriodDurationValue();

            List<AccountFeesEntity> fees = new ArrayList<AccountFeesEntity>();
            List<FeeDto> accouontFees = loanAccountActionForm.getFeesToApply();
            for (FeeDto accountFee : accouontFees) {
                FeeBO feeEntity = feeDao.findById(Short.valueOf(accountFee.getFeeId()));
                Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(accountFee.getAmount());
                fees.add(new AccountFeesEntity(null, feeEntity, feeAmount));
            }

            Double maxLoanAmount = loanAccountActionForm.getMaxLoanAmountValue();
            Double minLoanAmount = loanAccountActionForm.getMinLoanAmountValue();
            Short maxNumOfInstallments = loanAccountActionForm.getMaxNoInstallmentsValue();
            Short minNumOfShortInstallments = loanAccountActionForm.getMinNoInstallmentsValue();
            String externalId = loanAccountActionForm.getExternalId();
            Integer selectedLoanPurpose = loanAccountActionForm.getBusinessActivityIdValue();
            String collateralNote = loanAccountActionForm.getCollateralNote();
            Integer selectedCollateralType = loanAccountActionForm.getCollateralTypeIdValue();
            AccountState accountState = loanAccountActionForm.getState();
            if (accountState == null) {
                accountState = AccountState.LOAN_PARTIAL_APPLICATION;
            }

            FundBO fund = getFund(loanAccountActionForm);

            LoanBO redoLoan = LoanBO.redoLoan(userContext, loanOffering, customer, accountState, loanAmount,
                    numOfInstallments, disbursementDate.toDate(), isInterestDeductedAtDisbursement, interest, gracePeriod,
                    fund, fees, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
                    minNumOfShortInstallments, isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
            redoLoan.setExternalId(externalId);
            redoLoan.setBusinessActivityId(selectedLoanPurpose);
            redoLoan.setCollateralNote(collateralNote);
            redoLoan.setCollateralTypeId(selectedCollateralType);

            PersonnelBO user = legacyPersonnelDao.getPersonnel(userContext.getId());

            redoLoan.changeStatus(AccountState.LOAN_APPROVED, null, "Automatic Status Update (Redo Loan)", user);

            // We're assuming cash disbursal for this situation right now
            redoLoan.disburseLoan(user, PaymentTypes.CASH.getValue(), false);

            List<PaymentDataHtmlBean> paymentDataBeans = loanAccountActionForm.getPaymentDataBeans();
            PaymentData payment;
            List<RepaymentScheduleInstallment> installments = getInstallmentFromPaymentDataBeans(paymentDataBeans);
            loanBusinessService.applyDailyInterestRatesWhereApplicable(new LoanScheduleGenerationDto(redoLoan.getDisbursementDate(),
                    redoLoan, redoLoan.isVariableInstallmentsAllowed(), redoLoan.getLoanAmount(), redoLoan.getInterestRate()), installments);
            for (PaymentDataTemplate template : paymentDataBeans) {
                if (template.hasValidAmount() && template.getTransactionDate() != null) {
                    if (!customer.getCustomerMeeting().getMeeting().isValidMeetingDate(template.getTransactionDate(),
                            DateUtils.getLastDayOfNextYear())) {
                        throw new BusinessRuleException("errors.invalidTxndate");
                    }
                    payment = PaymentData.createPaymentData(template);
                    redoLoan.applyPayment(payment);
                }
            }
            return redoLoan;
        } catch (InvalidDateException ide) {
            throw new BusinessRuleException(ide.getMessage());
        } catch (MeetingException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e1) {
            throw new MifosRuntimeException(e1);
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private List<RepaymentScheduleInstallment> getInstallmentFromPaymentDataBeans(List<PaymentDataHtmlBean> paymentDataBeans) {
        return org.mifos.framework.util.CollectionUtils.collect(paymentDataBeans,
                new Transformer<PaymentDataHtmlBean, RepaymentScheduleInstallment>() {
                    @Override
                    public RepaymentScheduleInstallment transform(PaymentDataHtmlBean input) {
                        return input.getInstallment();
                    }
                });
    }

    private ActionForwards validateCashFlow(ActionForm form, HttpServletRequest request,
                                            LoanAccountActionForm loanAccountForm, boolean isInstallmentsValid) throws Exception {
        ActionForwards forward = isInstallmentsValid ?
                ActionForwards.preview_success :
                ActionForwards.preview_failure;

        boolean cashFlowBounded = bindCashFlowIfPresent(request, loanAccountForm, false);
        //----to display errors on second page
        if (forward.equals(ActionForwards.preview_success) && cashFlowBounded) {
            forward = validateCashFlowForWarningAndErrors(form, request, loanAccountForm, ActionForwards.preview_success, ActionForwards.preview_failure);
        }
        return forward;
    }

    private ActionForwards validateCashFlowForWarningAndErrors(ActionForm form, HttpServletRequest request, LoanAccountActionForm loanAccountForm, ActionForwards preview_success, ActionForwards failure) throws Exception {
        validateCashFlowForWarning(form, request);
        ActionForwards forward = validateCashFlowAndInstallmentDatesForErrors(loanAccountForm, request) ?
                preview_success :
                failure;
        return forward;
    }

    private boolean validateCashFlowAndInstallmentDatesForErrors(LoanAccountActionForm loanAccountForm, HttpServletRequest request) throws Exception {
        UserContext userContext = getUserContext(request);
        LoanOfferingBO loanOffering = getLoanOffering(loanAccountForm.getPrdOfferingIdValue(), userContext.getLocaleId());

        LoanInstallmentsDto loanInstallmentsDto = createLoanInstallmentDto(loanAccountForm, loanAccountForm.getInstallments());
        CashFlowForm cashFlowForm = loanAccountForm.getCashFlowForm();

        List<MonthlyCashFlowDto> cashflowDtos = new ArrayList<MonthlyCashFlowDto>();
        for (MonthlyCashFlowForm monthlyCashflowform : cashFlowForm.getMonthlyCashFlows()) {

            MonthlyCashFlowDto monthlyCashFlow = new MonthlyCashFlowDto(monthlyCashflowform.getDateTime(),
                    monthlyCashflowform.getCumulativeCashFlow(), monthlyCashflowform.getNotes(), monthlyCashflowform.getRevenue(), monthlyCashflowform.getExpense());
            cashflowDtos.add(monthlyCashFlow);
        }

        Errors validationErrors = loanAccountServiceFacade.validateCashFlowForInstallments(loanInstallmentsDto, cashflowDtos, loanOffering.getRepaymentCapacity(), cashFlowForm.getTotalBalance());
        ActionErrors cashflowAndInstallmentDateErrors = getActionErrors(validationErrors);

        return addErrorAndReturnResult(request, cashflowAndInstallmentDateErrors);
    }

    private boolean validateCashFlowForWarning(ActionForm form, HttpServletRequest request) throws Exception {
        return addErrorAndReturnResult(request, validateCashFlowForInstallmentsForWarnings((LoanAccountActionForm) form));
    }

    private boolean addErrorAndReturnResult(HttpServletRequest request, ActionErrors actionErrors) {
        boolean isEmpty = actionErrors.isEmpty();
        if (!isEmpty) {
            addErrors(request, actionErrors);
        }
        return isEmpty;
    }

    private void setInstallmentsOnSession(HttpServletRequest request, LoanAccountActionForm loanAccountForm) throws PageExpiredException {
        SessionUtils.setCollectionAttribute(LoanConstants.INSTALLMENTS, loanAccountForm.getInstallments(), request);
    }

    @SuppressWarnings("unchecked")
    private List<BusinessActivityEntity> retrieveLoanPurposesFromSession(final HttpServletRequest request)
            throws PageExpiredException {
        return (List<BusinessActivityEntity>) SessionUtils.getAttribute(MasterConstants.BUSINESS_ACTIVITIES, request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getInstallmentDetails(final ActionMapping mapping,
                                               @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
                                               @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        Integer accountId = Integer.valueOf(request.getParameter(ACCOUNT_ID));

        LoanInstallmentDetailsDto loanInstallmentDetailsDto = this.loanAccountServiceFacade.retrieveInstallmentDetails(accountId);

        SessionUtils.setAttribute(VIEW_UPCOMING_INSTALLMENT_DETAILS, loanInstallmentDetailsDto.getUpcomingInstallmentDetails(), request);
        SessionUtils.setAttribute(VIEW_OVERDUE_INSTALLMENT_DETAILS, loanInstallmentDetailsDto.getOverDueInstallmentDetails(), request);
        SessionUtils.setAttribute(TOTAL_AMOUNT_OVERDUE, loanInstallmentDetailsDto.getTotalAmountDue(), request);
        SessionUtils.setAttribute(NEXTMEETING_DATE, loanInstallmentDetailsDto.getNextMeetingDate(), request);

        return mapping.findForward(VIEWINSTALLMENTDETAILS_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getAllActivity(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form,
                                        final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response)
            throws Exception {

        String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);

        List<LoanActivityDto> allLoanAccountActivities = this.loanAccountServiceFacade.retrieveAllLoanAccountActivities(globalAccountNum);

        SessionUtils.setCollectionAttribute(LOAN_ALL_ACTIVITY_VIEW, allLoanAccountActivities, request);
        return mapping.findForward(MethodNameConstants.GETALLACTIVITY_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward forwardWaiveCharge(final ActionMapping mapping,
                                            @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
                                            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        return mapping.findForward("waive" + request.getParameter("type") + "Charges_Success");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                             @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
        loanAccountActionForm.clearDetailsForLoan();
        String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);
        UserContext userContext = getUserContext(request);
        LoanInformationDto loanInformationDto;
        try {
            loanInformationDto = this.loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        }
        catch (MifosRuntimeException e) {
            if (e.getCause() instanceof ApplicationException) {
                throw (ApplicationException) e.getCause();
            }
            throw e;
        }
        final String accountStateNameLocalised = MessageLookup.getInstance().lookup(loanInformationDto.getAccountStateName(), userContext);
        SessionUtils.removeThenSetAttribute("accountStateNameLocalised", accountStateNameLocalised, request);

        final String gracePeriodTypeNameLocalised = MessageLookup.getInstance().lookup(loanInformationDto.getGracePeriodTypeName(), userContext);
        SessionUtils.removeThenSetAttribute("gracePeriodTypeNameLocalised", gracePeriodTypeNameLocalised, request);
        final String interestTypeNameLocalised = MessageLookup.getInstance().lookup(loanInformationDto.getInterestTypeName(), userContext);
        SessionUtils.removeThenSetAttribute("interestTypeNameLocalised", interestTypeNameLocalised, request);

        final Set<String> accountFlagStateEntityNamesLocalised = new HashSet<String>();
        for (String name : loanInformationDto.getAccountFlagNames()) {
            accountFlagStateEntityNamesLocalised.add(MessageLookup.getInstance().lookup(name, userContext));
        }
        SessionUtils.setCollectionAttribute("accountFlagNamesLocalised", accountFlagStateEntityNamesLocalised, request);

        SessionUtils.removeAttribute(BUSINESS_KEY, request);

        Integer loanIndividualMonitoringIsEnabled = configurationPersistence.getConfigurationKeyValueInteger(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();

        if (null != loanIndividualMonitoringIsEnabled && loanIndividualMonitoringIsEnabled.intValue() != 0) {
            SessionUtils.setAttribute(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, loanIndividualMonitoringIsEnabled.intValue(), request);
        }

        List<ValueListElement> allLoanPurposes = this.loanProductDao.findAllLoanPurposes();
//        List<BusinessActivityEntity> loanPurposes = (List<BusinessActivityEntity>)masterDataService.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, getUserContext(request).getLocaleId());
        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES, allLoanPurposes, request);

        if (null != loanIndividualMonitoringIsEnabled && 0 != loanIndividualMonitoringIsEnabled.intValue()
                && loanInformationDto.isGroup()) {

            List<LoanAccountDetailsDto> loanAccountDetails = this.loanAccountServiceFacade.retrieveLoanAccountDetails(loanInformationDto);
            SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanAccountDetails, request);
        }
        SessionUtils.setCollectionAttribute(CUSTOM_FIELDS, new ArrayList<CustomFieldDefinitionEntity>(), request);
        // Retrieve and set into the session all collateral types from the
        // lookup_value_locale table associated with the current user context
        // locale
        SessionUtils.setCollectionAttribute(MasterConstants.COLLATERAL_TYPES, legacyMasterDao.getLookUpEntity(
                MasterConstants.COLLATERAL_TYPES).getCustomValueListElements(), request);
        SessionUtils.setAttribute(AccountConstants.LAST_PAYMENT_ACTION, loanBusinessService.getLastPaymentAction(loanInformationDto.getAccountId()), request);
        SessionUtils.removeThenSetAttribute("loanInformationDto", loanInformationDto, request);
        // inject preferred date
        List<LoanActivityDto> activities = loanInformationDto.getRecentAccountActivity();
        for (LoanActivityDto activity : activities) {
            activity.setUserPrefferedDate(DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), activity.getActionDate().toString()));
        }
        SessionUtils.setCollectionAttribute(RECENTACCOUNTACTIVITIES, activities, request);

        request.setAttribute(CustomerConstants.SURVEY_KEY, loanInformationDto.getAccountSurveys());
        request.setAttribute(CustomerConstants.SURVEY_COUNT, loanInformationDto.getActiveSurveys());
        request.setAttribute(AccountConstants.SURVEY_KEY, loanInformationDto.getAccountSurveys());

        Integer administrativeDocumentsIsEnabled = configurationPersistence.getConfigurationKeyValueInteger(ADMINISTRATIVE_DOCUMENT_IS_ENABLED).getValue();

        if (null != administrativeDocumentsIsEnabled && administrativeDocumentsIsEnabled.intValue() == 1) {
            SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSLIST,
                    legacyAdminDocumentDao.getAllAdminDocuments(), request);

            SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSACCSTATEMIXLIST,
                    legacyAdminDocAccStateMixDao.getAllMixedAdminDocuments(), request);

        }

        // John W - temporarily put back because needed in applychargeaction - update
        // keithW - and for recentAccountNotes
        LoanBO loan = getLoan(loanInformationDto.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setCurrentPageUrl(request, loan);
        setQuestionGroupInstances(request, loan);
        List<RepaymentScheduleInstallment> installments = loan.toRepaymentScheduleDto(userContext.getPreferredLocale());
        loanAccountActionForm.initializeInstallments(installments);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private void setQuestionGroupInstances(HttpServletRequest request, LoanBO loanBO) throws PageExpiredException {
        QuestionnaireServiceFacade questionnaireServiceFacade = questionnaireServiceFacadeLocator.getService(request);
        boolean containsQGForCloseLoan = false;
        if (questionnaireServiceFacade != null) {
            setQuestionGroupInstances(questionnaireServiceFacade, request, loanBO.getAccountId());
            if (loanBO.getAccountState().getId().equals(AccountState.LOAN_CLOSED_RESCHEDULED.getValue()) ||
                    loanBO.getAccountState().getId().equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue())) {
                containsQGForCloseLoan = questionnaireServiceFacade.getQuestionGroupInstances(loanBO.getAccountId(), "Close", "Loan").size() > 0;
            }
        }
        SessionUtils.removeThenSetAttribute("containsQGForCloseLoan", containsQGForCloseLoan, request);
    }

    // Intentionally made public to aid testing !

    public void setQuestionGroupInstances(QuestionnaireServiceFacade questionnaireServiceFacade, HttpServletRequest request, Integer loanAccountId) throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = questionnaireServiceFacade.getQuestionGroupInstances(loanAccountId, "View", "Loan");
        SessionUtils.setCollectionAttribute("questionGroupInstances", instanceDetails, request);
    }

    private void setCurrentPageUrl(HttpServletRequest request, LoanBO loanBO) throws PageExpiredException, UnsupportedEncodingException {
        SessionUtils.removeThenSetAttribute("currentPageUrl", constructCurrentPageUrl(request, loanBO), request);
    }

    private String constructCurrentPageUrl(HttpServletRequest request, LoanBO loanBO) throws UnsupportedEncodingException {
        String globalAccountNum = request.getParameter("globalAccountNum");
        String officerId = request.getParameter("recordOfficeId");
        String loanOfficerId = request.getParameter("recordLoanOfficerId");
        String url = String.format("loanAccountAction.do?globalAccountNum=%s&customerId=%s&recordOfficeId=%s&recordLoanOfficerId=%s",
                globalAccountNum, Integer.toString(loanBO.getAccountId()), officerId, loanOfficerId);
        return URLEncoder.encode(url, "UTF-8");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getLoanRepaymentSchedule(final ActionMapping mapping,
                                                  @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
                                                  @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
        UserContext userContext = getUserContext(request);
        Integer loanId = Integer.valueOf(request.getParameter(ACCOUNT_ID));
        Locale locale = userContext.getPreferredLocale();
        Date viewDate = loanAccountActionForm.getScheduleViewDateValue(locale);

        LoanBO loan = getLoan(loanId);
        loan.updateDetails(userContext);
        Errors errors = loanBusinessService.computeExtraInterest(loan, viewDate);
        if (errors.hasErrors()) {
            loanAccountActionForm.resetScheduleViewDate();
        }

        OriginalScheduleInfoDto originalSchedule = this.loanServiceFacade.retrieveOriginalLoanSchedule(loanId, locale);

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        SessionUtils.setAttribute(Constants.ORIGINAL_SCHEDULE_AVAILABLE, originalSchedule.hasOriginalInstallments(), request);
        SessionUtils.setAttribute(Constants.VIEW_DATE, viewDate, request);

        String forward = errors.hasErrors() ? ActionForwards.getLoanRepaymentScheduleFailure.toString() : ActionForwards.getLoanRepaymentSchedule.toString();
        addErrors(request, getActionErrors(errors));
        return mapping.findForward(forward);
    }

    // Method exists to support unit testing
    LoanBO getLoan(Integer loanId) {
        return loanDao.findById(loanId);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward viewOriginalSchedule(final ActionMapping mapping,
                                              @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
                                              @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        UserContext userContext = getUserContext(request);
        Integer loanId = Integer.valueOf(request.getParameter(ACCOUNT_ID));

        OriginalScheduleInfoDto dto = loanServiceFacade.retrieveOriginalLoanSchedule(loanId, userContext.getPreferredLocale());
        SessionUtils.setAttribute(CustomerConstants.DISBURSEMENT_DATE, dto.getDisbursementDate(), request);
        SessionUtils.setAttribute(CustomerConstants.LOAN_AMOUNT, dto.getLoanAmount(), request);
        SessionUtils.setCollectionAttribute(LoanConstants.ORIGINAL_INSTALLMENTS,
                dto.getOriginalLoanScheduleInstallment(), request);
        return mapping.findForward(ActionForwards.viewOriginalSchedule.name());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward viewStatusHistory(final ActionMapping mapping,
                                           @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
                                           @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        UserContext userContext = getUserContext(request);
        String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        loan.updateDetails(userContext);

        List<AccountStatusChangeHistoryEntity> accStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>(loan.getAccountStatusChangeHistory());

        SessionUtils.setCollectionAttribute(STATUS_HISTORY, accStatusChangeHistory, request);

        return mapping.findForward(ActionForwards.viewStatusHistory.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                                  @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
        String perspective = loanAccountForm.getPerspective();
        if (perspective != null) {
            setPerspective(request, perspective);
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

    public ActionForward redoLoanBegin(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form,
                                       @SuppressWarnings("unused") final HttpServletRequest request,
                                       @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.beginRedoLoanDisbursal_success.toString());
    }

    /**
     * Resolve repayment start date according to given disbursement date
     * <p/>
     * The resulting date equates to the disbursement date plus MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY: e.g.
     * If disbursement date is 18 June 2008, and MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY is 1 then the
     * repayment start date would be 19 June 2008
     *
     * @return Date repaymentStartDate
     * @throws PersistenceException
     */
    private Date resolveRepaymentStartDate(final Date disbursementDate) {
        int minDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        final GregorianCalendar repaymentStartDate = new GregorianCalendar();
        repaymentStartDate.setTime(disbursementDate);
        repaymentStartDate.add(Calendar.DAY_OF_WEEK, minDaysInterval);
        return repaymentStartDate.getTime();
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form,
                                  final HttpServletRequest request,
                                  @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        setPerspective(request, request.getParameter(PERSPECTIVE));
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                                @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        setPerspectiveOnRequest(request);
        Integer customerId = (getCustomer(request)).getCustomerId();
        UserContext userContext = getUserContext(request);

        LoanAccountInfoDto loanAccountInfo = createLoanAccountInfo(loanActionForm, getDisbursementDate(loanActionForm,
                userContext.getPreferredLocale()),
                customerId, loanActionForm.getLoanOfferingFundValue());

        LoanAccountMeetingDto loanAccountMeetingDto = createAccountMeetingDto(loanActionForm);

        LoanCreationResultDto loanCreationResultDto;
        String perspective = loanActionForm.getPerspective();
        if (isRedoOperation(perspective)) {
            loanCreationResultDto = getLoanCreationResultForRedo(loanActionForm, loanAccountMeetingDto, loanAccountInfo);
        } else {
            List<RepaymentScheduleInstallment> installments = loanActionForm.getInstallments();
            List<LoanScheduledInstallmentDto> loanScheduleDtos = getLoanScheduleInstallmentDtos(installments);
            loanCreationResultDto = loanAccountServiceFacade.createLoan(loanAccountMeetingDto, loanAccountInfo, loanScheduleDtos);
        }

        createLoanQuestionnaire.saveResponses(request, loanActionForm, loanCreationResultDto.getAccountId());
        cashFlowAdaptor.save((CashFlowCaptor) form, request);

        if (loanCreationResultDto.isGlimApplicable()) {
            List<LoanAccountDetailsDto> loanAccountDetailsList = getLoanAccountDetailsFromSession(request);
            for (LoanAccountDetailsDto loanAccountDetail : loanAccountDetailsList) {
                createIndividualLoanAccount(loanActionForm, configService.isRepaymentIndepOfMeetingEnabled(),
                        loanAccountDetail, isRedoOperation(perspective), loanCreationResultDto.getGlobalAccountNum(), userContext);
            }
        }

        loanActionForm.setAccountId(loanCreationResultDto.getAccountId().toString());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, this.loanDao.
                findByGlobalAccountNum(loanCreationResultDto.getGlobalAccountNum()), request);
        request.setAttribute(GLOBAL_ACCOUNT_NUM, loanCreationResultDto.getGlobalAccountNum());
        // NOTE: needed for link creation
        request.setAttribute("customer", this.customerDao.findCustomerById(customerId));

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    private List<LoanScheduledInstallmentDto> getLoanScheduleInstallmentDtos(List<RepaymentScheduleInstallment> installments) {
        List<LoanScheduledInstallmentDto> loanRepayments = new ArrayList<LoanScheduledInstallmentDto>();
        for (RepaymentScheduleInstallment installment : installments) {
            LoanScheduledInstallmentDto repaymentInstallment = new LoanScheduledInstallmentDto(installment.getInstallment(), installment.getPrincipal().toString(), installment.getInterest().toString(), new LocalDate(installment.getDueDateValue()));
            loanRepayments.add(repaymentInstallment);
        }
        return loanRepayments;
    }

    private LoanCreationResultDto getLoanCreationResultForRedo(LoanAccountActionForm loanActionForm, LoanAccountMeetingDto loanAccountMeetingDto, LoanAccountInfoDto loanAccountInfo) {
        List<LoanPaymentDto> loanRepayments = new ArrayList<LoanPaymentDto>();
        List<PaymentDataHtmlBean> paymentBeans = loanActionForm.getPaymentDataBeans();
        for (PaymentDataHtmlBean existingPayment : paymentBeans) {
            LoanPaymentDto loanPaymentDto = new LoanPaymentDto(existingPayment.getAmount(),
                    new LocalDate(existingPayment.getTransactionDate()),
                    existingPayment.getPaymentTypeId().longValue(),
                    existingPayment.getPersonnel().getPersonnelId());
            loanRepayments.add(loanPaymentDto);
        }
        List<LoanScheduledInstallmentDto> installmentDtos = getLoanScheduleInstallmentDtos(getInstallmentFromPaymentDataBeans(paymentBeans));
        return loanAccountServiceFacade.redoLoan(loanAccountMeetingDto, loanAccountInfo, loanRepayments, installmentDtos);
    }

    private LoanAccountMeetingDto createAccountMeetingDto(LoanAccountActionForm loanActionForm) {
        return new LoanAccountMeetingDto(loanActionForm.getRecurrenceId(),
                loanActionForm.getWeekDay(), loanActionForm.getRecurWeek(),
                loanActionForm.getMonthType(), loanActionForm.getMonthDay(),
                loanActionForm.getDayRecurMonth(), loanActionForm.getMonthWeek(), loanActionForm.getRecurMonth(), loanActionForm.getMonthRank());
    }

    private LoanAccountInfoDto createLoanAccountInfo(LoanAccountActionForm loanActionForm, DateTime disbursementDate, Integer customerId, Short fundId) {
        LoanAccountInfoDto loanAccountInfo = new LoanAccountInfoDto();
        loanAccountInfo.setCustomerId(customerId);
        loanAccountInfo.setFundId(fundId);
        loanAccountInfo.setDisbursementDate(disbursementDate.toLocalDate());

        loanAccountInfo.setProductId(loanActionForm.getPrdOfferingIdValue());
        loanAccountInfo.setLoanAmount(loanActionForm.getLoanAmount());
        loanAccountInfo.setInterestDeductedAtDisbursement(loanActionForm.isInterestDedAtDisbValue());
        loanAccountInfo.setInterest(loanActionForm.getInterestDoubleValue());
        loanAccountInfo.setGracePeriod(loanActionForm.getGracePeriodDurationValue());
        loanAccountInfo.setMaxLoanAmount(loanActionForm.getMaxLoanAmount());
        loanAccountInfo.setMinLoanAmount(loanActionForm.getMinLoanAmount());
        loanAccountInfo.setNumOfInstallments(loanActionForm.getNoOfInstallmentsValue());
        loanAccountInfo.setMaxNumOfInstallments(loanActionForm.getMaxNoInstallmentsValue());
        loanAccountInfo.setMinNumOfInstallments(loanActionForm.getMinNoInstallmentsValue());
        loanAccountInfo.setExternalId(loanActionForm.getExternalId());
        loanAccountInfo.setSelectedLoanPurpose(loanActionForm.getBusinessActivityIdValue());
        loanAccountInfo.setSelectedCollateralType(loanActionForm.getCollateralTypeIdValue());
        loanAccountInfo.setAccountState(loanActionForm.getState().getValue());

        List<CreateAccountFeeDto> accountFeesToCreate = new ArrayList<CreateAccountFeeDto>();
        for (FeeDto feeToApply : loanActionForm.getFeesToApply()) {
            accountFeesToCreate.add(new CreateAccountFeeDto(Integer.valueOf(feeToApply.getFeeId()), feeToApply.getAmount()));
        }
        loanAccountInfo.setFees(accountFeesToCreate);

        return loanAccountInfo;
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

    private void createIndividualLoanAccount(final LoanAccountActionForm loanActionForm,
                                             final boolean isRepaymentIndepOfMeetingEnabled, final LoanAccountDetailsDto loanAccountDetail,
                                             final boolean isRedoOperation, String globalAccountNum, UserContext userContext) throws AccountException, ServiceException {
        LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
        loan.updateDetails(userContext);
        LoanBO individualLoan;
        if (isRedoOperation) {
            individualLoan = LoanBO.redoIndividualLoan(loan.getUserContext(), loan.getLoanOffering(),
                    new CustomerBusinessService().findBySystemId(loanAccountDetail.getClientId()), loanActionForm
                    .getState(), new Money(loan.getCurrency(), loanAccountDetail.getLoanAmount().toString()),
                    loan.getNoOfInstallments(), loan.getDisbursementDate(), false, isRepaymentIndepOfMeetingEnabled,
                    loan.getInterestRate(), loan.getGracePeriodDuration(), loan.getFund(), new ArrayList<FeeDto>(),
                    new ArrayList<CustomFieldDto>());

        } else {
            individualLoan = LoanBO.createIndividualLoan(loan.getUserContext(), loan.getLoanOffering(),
                    new CustomerBusinessService().findBySystemId(loanAccountDetail.getClientId()), loanActionForm
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
                                @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);
        CustomerBO customer = getCustomerFromRequest(request);

        if (isGlimEnabled()) {
            populateGlimAttributes(request, loanActionForm, globalAccountNum, customer);
        }

        String recurMonth = customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter().toString();
        handleRepaymentsIndependentOfMeetingIfConfigured(request, loanActionForm, recurMonth);
        LoanBO loanBO = new LoanDaoHibernate(new GenericDaoHibernate()).findByGlobalAccountNum(globalAccountNum);

        UserContext userContext = getUserContext(request);
        loanBO.setUserContext(userContext);
        SessionUtils.setAttribute(PROPOSED_DISBURSAL_DATE, loanBO.getDisbursementDate(), request);
        SessionUtils.removeAttribute(LOANOFFERING, request);
        LoanOfferingBO loanOffering = getLoanOffering(loanBO.getLoanOffering().getPrdOfferingId(), userContext.getLocaleId());
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
            MeetingDetailsEntity meetingDetail = loanBO.getLoanMeeting().getMeetingDetails();
            loanActionForm.setMonthDay("");
            loanActionForm.setMonthWeek("0");
            loanActionForm.setMonthRank("0");

            if (meetingDetail.getRecurrenceTypeEnum() == RecurrenceType.MONTHLY) {
                setMonthlySchedule(loanActionForm, meetingDetail);
            } else {
                setWeeklySchedule(loanActionForm, meetingDetail);
            }
        }
        SessionUtils.setAttribute(LOANOFFERING, loanOffering, request);
        // Retrieve and set into the session all collateral types from the
        // lookup_value_locale table associated with the current user context
        // locale
        SessionUtils.setCollectionAttribute(MasterConstants.COLLATERAL_TYPES, legacyMasterDao.getLookUpEntity(
                MasterConstants.COLLATERAL_TYPES).getCustomValueListElements(),
                request);

        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES,
                legacyMasterDao.findValueListElements(MasterConstants.LOAN_PURPOSES), request);
        SessionUtils.setCollectionAttribute(CUSTOM_FIELDS, new ArrayList<CustomFieldDefinitionEntity>(), request);

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
        return loanBusinessService.getAccount(loanBOInSession.getAccountId());
    }

    private void setRequestAttributesForEditPage(final HttpServletRequest request, final LoanBO loanBO) {
        request.setAttribute("accountState", loanBO.getState());
        request.setAttribute(MasterConstants.COLLATERAL_TYPES, legacyMasterDao.getLookUpEntity(
                MasterConstants.COLLATERAL_TYPES).getCustomValueListElements());
        request.setAttribute("collateralTypeId", loanBO.getCollateralTypeId());
    }

    private CustomerBO getCustomerFromRequest(final HttpServletRequest request) {
        String customerId = request.getParameter(CUSTOMER_ID);
        if (isNotEmpty(customerId)) {
            return getCustomer(Integer.valueOf(customerId));
        }
        return null;
    }

    private void populateGlimAttributes(final HttpServletRequest request, final LoanAccountActionForm loanActionForm,
                                        final String globalAccountNum, final CustomerBO customer) throws PageExpiredException, ServiceException {
        GlimSessionAttributes glimSessionAttributes = getGlimSpecificPropertiesToSet(loanActionForm, globalAccountNum,
                customer, legacyMasterDao.findValueListElements(MasterConstants.LOAN_PURPOSES));
        glimSessionAttributes.putIntoSession(request);
    }

    GlimSessionAttributes getGlimSpecificPropertiesToSet(final LoanAccountActionForm loanActionForm,
                                                         final String globalAccountNum, final CustomerBO customer, final List<ValueListElement> businessActivities)
            throws ServiceException {
        if (configService.isGlimEnabled() && customer.isGroup()) {
            List<LoanBO> individualLoans = loanBusinessService.getAllChildrenForParentGlobalAccountNum(globalAccountNum);

            List<ClientBO> activeClientsUnderGroup = this.customerDao.findActiveClientsUnderGroup(customer);
            List<LoanAccountDetailsDto> clientDetails = populateClientDetailsFromLoan(activeClientsUnderGroup, individualLoans, businessActivities);
            loanActionForm.setClientDetails(clientDetails);
            loanActionForm.setClients(fetchClientIdsWithMatchingLoans(individualLoans, clientDetails));

            return new GlimSessionAttributes(LoanConstants.GLIM_ENABLED_VALUE, activeClientsUnderGroup, LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);
        }

        return new GlimSessionAttributes(LoanConstants.GLIM_DISABLED_VALUE);
    }

    private List<String> fetchClientIdsWithMatchingLoans(final List<LoanBO> individualLoans,
                                                         final List<LoanAccountDetailsDto> clientDetails) {
        List<String> clientIds = new ArrayList<String>();
        for (final LoanAccountDetailsDto clientDetail : clientDetails) {
            LoanBO loanMatchingClientDetail = (LoanBO) CollectionUtils.find(individualLoans, new Predicate() {
                @Override
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
                                                              final List<LoanBO> individualLoans, final List<ValueListElement> businessActivities) {
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();
        for (final ClientBO client : activeClientsUnderGroup) {
            LoanAccountDetailsDto clientDetail = new LoanAccountDetailsDto();
            clientDetail.setClientId(getStringValue(client.getCustomerId()));
            clientDetail.setClientName(client.getDisplayName());
            LoanBO loanAccount = (LoanBO) CollectionUtils.find(individualLoans, new Predicate() {
                @Override
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

                                @Override
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
    public ActionForward managePrevious(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form,
                                        final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response)
            throws Exception {
        setRequestAttributesForEditPage(request, getLoanBO(request));
        return mapping.findForward(ActionForwards.manageprevious_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward managePreview(final ActionMapping mapping, final ActionForm form,
                                       final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response)
            throws Exception {
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

    private FundBO getFund(LoanAccountActionForm loanAccountActionForm) {
        FundBO fund = null;
        if (!StringUtils.isBlank(loanAccountActionForm.getLoanOfferingFund())) {
            Short fundId = loanAccountActionForm.getLoanOfferingFundValue();
            if (fundId != 0) {
                fund = this.fundDao.findById(fundId);
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
                LoanAccountDetailsDto matchingClientDetail = (LoanAccountDetailsDto) CollectionUtils.find(
                        clientDetails, new Predicate() {
                            @Override
                            public boolean evaluate(final Object object) {
                                return ((LoanAccountDetailsDto) object).getClientId().equals(clientId);
                            }
                        });

                if (matchingClientDetail != null) {
                    setGovernmentIdAndPurpose(matchingClientDetail, localeId);
                    loanAccountDetailsView.add(matchingClientDetail);
                }
            }
        }
        return loanAccountDetailsView;
    }

    private void setGovernmentIdAndPurpose(final LoanAccountDetailsDto clientDetail, final Short localeId)
            throws ServiceException {
        clientDetail.setBusinessActivityName(findBusinessActivityName(clientDetail.getBusinessActivity()));
        clientDetail.setGovermentId(findGovernmentId(getIntegerValue(clientDetail.getClientId())));
    }

    private void resetBusinessActivity(final HttpServletRequest request, final Short localeId,
                                       final LoanAccountActionForm loanAccountActionForm) throws PageExpiredException, Exception {
        SessionUtils.removeAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME, request);
        if (loanAccountActionForm.getBusinessActivityIdValue() != null) {
            SessionUtils.setAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME, getNameForBusinessActivityEntity(
                    loanAccountActionForm.getBusinessActivityIdValue()), request);
        }
    }

    private String findGovernmentId(final Integer clientId) {
        ClientBO client = (ClientBO) this.customerDao.findCustomerById(clientId);
        String governmentId = client.getGovernmentId();
        return StringUtils.isBlank(governmentId) ? "-" : governmentId;
    }

    private String findBusinessActivityName(final String businessActivity) {
        List<ValueListElement> businessActEntity = legacyMasterDao.findValueListElements(MasterConstants.LOAN_PURPOSES);
        for (ValueListElement busact : businessActEntity) {

            if (busact.getId().toString().equals(businessActivity)) {
                return busact.getName();
            }
        }
        return null;
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form,
                                @SuppressWarnings("unused") final HttpServletRequest request,
                                @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loan_detail_page.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                                @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        LoanBO loanBO = loanBusinessService.findBySystemId(loanBOInSession.getGlobalAccountNum());
        checkVersionMismatch(loanBOInSession.getVersionNo(), loanBO.getVersionNo());
        loanBO.setVersionNo(loanBOInSession.getVersionNo());
        loanBO.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(loanBO);

        LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;

        CustomerBO customer = loanBO.getCustomer();
        MeetingBO newMeetingForRepaymentDay = null;
        boolean isRepaymentIndepOfMeetingEnabled = configService.isRepaymentIndepOfMeetingEnabled();

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

    void handleIndividualLoans(final LoanBO loanBO, final LoanAccountActionForm loanAccountActionForm,
                               final boolean isRepaymentIndepOfMeetingEnabled, final List<LoanAccountDetailsDto> loanAccountDetailsList,
                               final List<LoanBO> individualLoans) throws AccountException, ServiceException {
        List<Integer> foundLoans = new ArrayList<Integer>();
        for (final LoanAccountDetailsDto loanAccountDetail : loanAccountDetailsList) {
            Predicate predicate = new Predicate() {

                @Override
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
     * <p/>
     * Depending on the recurrence id (WEEKLY or MONTHLY) a MeetingBO will be created and returned
     *
     * @throws InvalidDateException
     */
    private MeetingBO createNewMeetingForRepaymentDay(final HttpServletRequest request,
                                                      final LoanAccountActionForm loanAccountActionForm, final CustomerBO customer) throws MeetingException,
            InvalidDateException {
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

    private LoanOfferingBO getLoanOffering(Short loanOfferingId, short localeId) throws Exception {
        return loanPrdBusinessService.getLoanOffering(loanOfferingId, localeId);
    }

    private String getNameForBusinessActivityEntity(final Integer entityId) throws Exception {
        if (entityId != null) {
            return legacyMasterDao.getMessageForLookupEntity(entityId);
        }
        return "";
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
        loanAccountActionForm.setCustomFields(new ArrayList<CustomFieldDto>());

        loanAccountActionForm.setOriginalDisbursementDate(new java.sql.Date(loan.getDisbursementDate().getTime()));
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

        @SuppressWarnings("unchecked")
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
            setPerspective(request, request.getParameter(PERSPECTIVE));
        }
    }

    private boolean isGlimEnabled() {
        return new ConfigurationBusinessService().isGlimEnabled();
    }

    private void setGlimEnabledSessionAttributes(final HttpServletRequest request, final boolean isGroup)
            throws PageExpiredException {
        storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED,
                LoanConstants.GLIM_ENABLED_VALUE);
        if (isGroup) {
            storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOAN_ACCOUNT_OWNER_IS_A_GROUP,
                    LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);
        }
    }

    private void handleRepaymentsIndependentOfMeetingIfConfigured(final HttpServletRequest request,
                                                                  final LoanAccountActionForm loanActionForm, final String recurMonth) throws PageExpiredException, Exception {

        if (configService.isRepaymentIndepOfMeetingEnabled()) {

            storeObjectOnSessionForUseInJspPage(request, LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED, Integer.valueOf(1));
            storeObjectOnSessionForUseInJspPage(request, LoanConstants.LOANACCOUNTOWNERISACLIENT, LoanConstants.LOAN_ACCOUNT_OWNER_IS_GROUP_YES);

            storeCollectionOnSessionForUseInJspPage(request, MeetingConstants.WEEKDAYSLIST, fillWeekDaysWithLocalizedName(new FiscalCalendarRules().getWorkingDays()));
            storeCollectionOnSessionForUseInJspPage(request, MeetingConstants.WEEKRANKLIST, RankOfDay.getRankOfDayList());

            loanActionForm.setRecurMonth(recurMonth);
        }
    }

    private List<WeekDay> fillWeekDaysWithLocalizedName(List<WeekDay> weekDays) {
        //FIXME localization from properties is being used
        // When Application is started and LSIM is enabled then WeekDay.getName() remains empty causing the list of the weekdays on loan creation page to be 1,2,3..7 instead of names
        // We have done localization of Months using Joda type in http://mifosforge.jira.com/browse/MIFOS-3970
        // see the usage of WeekDay#setName and it initializes with empty name.
        for (WeekDay weekDay : weekDays) {
            if (StringUtils.isBlank(weekDay.getName())) {
                weekDay.setWeekdayName(MessageLookup.getInstance().lookup(weekDay.getPropertiesKey()));
            }
        }
        return weekDays;
    }

    @TransactionDemarcate(joinToken = true)
    @Override
    public ActionForward captureQuestionResponses(final ActionMapping mapping, final ActionForm form,
                                                  final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response)
            throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        LoanAccountActionForm actionForm = (LoanAccountActionForm) form;
        setPerspective(request, actionForm.getPerspective());
        ActionErrors errors = createLoanQuestionnaire.validateResponses(request, actionForm);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return createLoanQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    @Override
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return createLoanQuestionnaire.editResponses(mapping, request, (LoanAccountActionForm) form);
    }

    private ActionErrors getActionErrors(Errors errors) {
        ActionErrors actionErrors = new ActionErrors();
        if (errors.hasErrors()) {
            for (ErrorEntry errorEntry : errors.getErrorEntries()) {
                ActionMessage actionMessage;
                if (errorEntry.hasErrorArgs()) {
                    actionMessage = new ActionMessage(errorEntry.getErrorCode(), errorEntry.getArgs().toArray());
                } else {
                    actionMessage = new ActionMessage(errorEntry.getErrorCode(), errorEntry.getFieldName());
                }
                actionErrors.add(errorEntry.getErrorCode(), actionMessage);
            }
        }
        return actionErrors;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
}