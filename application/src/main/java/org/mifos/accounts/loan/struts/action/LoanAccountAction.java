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

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.ADMINISTRATIVE_DOCUMENT_IS_ENABLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.CLIENT_LIST;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.CUSTOM_FIELDS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOANFUNDS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOANOFFERING;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOAN_ACCOUNT_OWNER_IS_A_GROUP;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOAN_ALL_ACTIVITY_VIEW;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.NEXTMEETING_DATE;
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
import org.mifos.accounts.business.AccountOverpaymentEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.MaxMinInterestRate;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.persistance.LoanDaoHibernate;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.struts.action.AccountAppAction;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
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
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.platform.validations.Errors;
import org.mifos.reports.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.security.util.UserContext;

/**
 * management of loan accounts.
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
                "clientsAndAccounts.ftl", questionnaireServiceFacadeLocator, questionGroupFilter);
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

    private List<FundBO> getFunds(final LoanOfferingBO loanOffering) {
        List<FundBO> funds = new ArrayList<FundBO>();
        if (loanOffering.getLoanOfferingFunds() != null && loanOffering.getLoanOfferingFunds().size() > 0) {
            for (LoanOfferingFundEntity loanOfferingFund : loanOffering.getLoanOfferingFunds()) {
                funds.add(loanOfferingFund.getFund());
            }
        }
        return funds;
    }

    public ActionForward viewAndEditAdditionalInformation(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response)
            throws Exception {
        Integer entityId = Integer.valueOf(request.getParameter("entityId"));
        questionGroupFilter.setLoanOfferingBO(getLoan(entityId).getLoanOffering());
        List<QuestionGroupInstanceDetail> questionGroupInstances = createLoanQuestionnaire.
                getQuestionGroupInstances(request, entityId);
        request.getSession().setAttribute(LoanConstants.QUESTION_GROUP_INSTANCES, questionGroupInstances);
        ActionForward forward = mapping.findForward(ActionForwards.viewAndEditAdditionalInformation.toString());
        return new ActionForward(forward.getPath(), forward.getRedirect());
    }

    private void setPerspective(HttpServletRequest request, String perspective) {
        if (perspective != null) {
            request.setAttribute(PERSPECTIVE, perspective);
        }
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

        final String accountStateNameLocalised = ApplicationContextProvider.getBean(MessageLookup.class).lookup(loanInformationDto.getAccountStateName());
        SessionUtils.removeThenSetAttribute("accountStateNameLocalised", accountStateNameLocalised, request);

        final String gracePeriodTypeNameLocalised = ApplicationContextProvider.getBean(MessageLookup.class).lookup(loanInformationDto.getGracePeriodTypeName());
        SessionUtils.removeThenSetAttribute("gracePeriodTypeNameLocalised", gracePeriodTypeNameLocalised, request);
        final String interestTypeNameLocalised = ApplicationContextProvider.getBean(MessageLookup.class).lookup(loanInformationDto.getInterestTypeName());
        SessionUtils.removeThenSetAttribute("interestTypeNameLocalised", interestTypeNameLocalised, request);

        final Set<String> accountFlagStateEntityNamesLocalised = new HashSet<String>();
        for (String name : loanInformationDto.getAccountFlagNames()) {
            accountFlagStateEntityNamesLocalised.add(ApplicationContextProvider.getBean(MessageLookup.class).lookup(name));
        }
        SessionUtils.setCollectionAttribute("accountFlagNamesLocalised", accountFlagStateEntityNamesLocalised, request);

        SessionUtils.removeAttribute(BUSINESS_KEY, request);

        Integer loanIndividualMonitoringIsEnabled = configurationPersistence.getConfigurationValueInteger(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED);

        if (null != loanIndividualMonitoringIsEnabled && loanIndividualMonitoringIsEnabled.intValue() != 0) {
            SessionUtils.setAttribute(LOAN_INDIVIDUAL_MONITORING_IS_ENABLED, loanIndividualMonitoringIsEnabled.intValue(), request);
        }

        List<ValueListElement> allLoanPurposes = this.loanProductDao.findAllLoanPurposes();
//        List<BusinessActivityEntity> loanPurposes = (List<BusinessActivityEntity>)masterDataService.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, getUserContext(request).getLocaleId());
        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES, allLoanPurposes, request);

        if (null != loanIndividualMonitoringIsEnabled && 0 != loanIndividualMonitoringIsEnabled.intValue()
                && loanInformationDto.isGroup()) {

            List<LoanAccountDetailsDto> loanAccountDetails = this.loanAccountServiceFacade.retrieveLoanAccountDetails(loanInformationDto);
            addEmptyBuisnessActivities(loanAccountDetails);
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

        Integer administrativeDocumentsIsEnabled = configurationPersistence.getConfigurationValueInteger(ADMINISTRATIVE_DOCUMENT_IS_ENABLED);

        if (null != administrativeDocumentsIsEnabled && administrativeDocumentsIsEnabled.intValue() == 1) {
            SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSLIST,
                    legacyAdminDocumentDao.getAllActiveAdminDocuments(), request);

            SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSACCSTATEMIXLIST,
                    legacyAdminDocAccStateMixDao.getAllMixedAdminDocuments(), request);

        }
        
        // John W - temporarily put back because needed in applychargeaction - update
        // keithW - and for recentAccountNotes
        LoanBO loan = getLoan(loanInformationDto.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setCurrentPageUrl(request, loan);
        setQuestionGroupInstances(request, loan);
        setOverpayments(request, loan);
        List<RepaymentScheduleInstallment> installments = loan.toRepaymentScheduleDto(userContext.getPreferredLocale());
        loanAccountActionForm.initializeInstallments(installments);
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private void setOverpayments(HttpServletRequest request, LoanBO loan) throws PageExpiredException {
        List<AccountOverpaymentEntity> overpayments = new ArrayList<AccountOverpaymentEntity>();
        // filter cleared overpayments
        for (AccountOverpaymentEntity overpayment : loan.getAccountOverpayments()) {
            if (overpayment.isNotCleared()) {
                overpayments.add(overpayment);
            }
        }
        SessionUtils.setCollectionAttribute("overpayments", overpayments, request);
    }

    private void addEmptyBuisnessActivities(List<LoanAccountDetailsDto> loanAccountDetails) {
        for (LoanAccountDetailsDto details : loanAccountDetails) {
            if (details.getBusinessActivity() == null || details.getBusinessActivity().equals("")) {
                details.setBusinessActivity("-");
                details.setBusinessActivityName("-");
            }
        }
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
        return url;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getLoanRepaymentSchedule(final ActionMapping mapping,
                                                  final ActionForm form, final HttpServletRequest request,
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

        OriginalScheduleInfoDto originalSchedule = this.loanServiceFacade.retrieveOriginalLoanSchedule(loanId);

        String memberAccountIdParam = request.getParameter("memberAccountId");
        if (StringUtils.isNotBlank(memberAccountIdParam)) {
            Integer memberId = Integer.valueOf(memberAccountIdParam);
            LoanBO member = loan.findMemberById(memberId);
            if (member != null) {
                SessionUtils.setAttribute(CustomerConstants.CUSTOMER_NAME, member.getCustomer().getDisplayName(), request);
                SessionUtils.setAttribute(CustomerConstants.GLOBAL_CUST_NUM, member.getCustomer().getGlobalCustNum(), request);
            }
        }

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
        LoanBO loan = (LoanBO) SessionUtils.getAttribute(BUSINESS_KEY, request);
        boolean originalSchedule = ( request.getParameter("individual") == null ? true : false);

        OriginalScheduleInfoDto dto = loanServiceFacade.retrieveOriginalLoanSchedule(loanId);
        
        SessionUtils.setAttribute("originalSchedule", originalSchedule, request);
        SessionUtils.setAttribute(CustomerConstants.DISBURSEMENT_DATE, dto.getDisbursementDate(), request);
        SessionUtils.setAttribute(CustomerConstants.LOAN_AMOUNT, dto.getLoanAmount(), request);
        SessionUtils.setCollectionAttribute(LoanConstants.ORIGINAL_INSTALLMENTS, dto.getOriginalLoanScheduleInstallment(), request);
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
        int minDaysInterval = configurationPersistence.getConfigurationValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY);

        final GregorianCalendar repaymentStartDate = new GregorianCalendar();
        repaymentStartDate.setTime(disbursementDate);
        repaymentStartDate.add(Calendar.DAY_OF_WEEK, minDaysInterval);
        return repaymentStartDate.getTime();
    }

    @SuppressWarnings("unchecked")
    private List<LoanAccountDetailsDto> getLoanAccountDetailsFromSession(final HttpServletRequest request)
            throws PageExpiredException {
        return (List<LoanAccountDetailsDto>) SessionUtils.getAttribute("loanAccountDetailsView", request);
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
        if (isGlimEnabled()) {
            performGlimSpecificOnManagePreview(request, loanAccountForm);
            addEmptyBuisnessActivities(loanAccountForm.getClientDetails());
        }
        if (null != getFund(loanAccountForm)) {
            request.setAttribute("sourceOfFunds", getFund(loanAccountForm).getFundName());
        }

        resetBusinessActivity(request, (LoanAccountActionForm) form);
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

    private void performGlimSpecificOnManagePreview(final HttpServletRequest request,final LoanAccountActionForm loanAccountForm) throws PageExpiredException {
        CustomerBO customer = getCustomer(loanAccountForm.getCustomerIdValue());
        setGlimEnabledSessionAttributes(request, customer.isGroup());
        if (customer.isGroup()) {
            List<LoanAccountDetailsDto> loanAccountDetailsView = populateDetailsForSelectedClients(loanAccountForm.getClientDetails(), loanAccountForm.getClients());
            SessionUtils.setCollectionAttribute("loanAccountDetailsView", loanAccountDetailsView, request);
        }
    }

    private List<LoanAccountDetailsDto> populateDetailsForSelectedClients(final List<LoanAccountDetailsDto> clientDetails, final List<String> selectedClients) {
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
                    setGovernmentIdAndPurpose(matchingClientDetail);
                    loanAccountDetailsView.add(matchingClientDetail);
                }
            }
        }
        return loanAccountDetailsView;
    }

    private void setGovernmentIdAndPurpose(final LoanAccountDetailsDto clientDetail) {
        clientDetail.setBusinessActivityName(findBusinessActivityName(clientDetail.getBusinessActivity()));
        clientDetail.setGovermentId(findGovernmentId(getIntegerValue(clientDetail.getClientId())));
    }

    private void resetBusinessActivity(final HttpServletRequest request,final LoanAccountActionForm loanAccountActionForm) throws PageExpiredException, Exception {
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
        	try{
            newMeetingForRepaymentDay = this.createNewMeetingForRepaymentDay(request, loanAccountActionForm, customer);
        	}
        	catch (MeetingException er){}
        	catch  (InvalidDateException er){}
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
                    loanAccountDetailsList, individualLoans, getUserContext(request).getPreferredLocale());
            request.setAttribute(CUSTOMER_ID, loanBO.getCustomer().getCustomerId().toString());
        }

        loanBOInSession = null;
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);

        return mapping.findForward(ActionForwards.update_success.toString());
    }

    void handleIndividualLoans(final LoanBO loanBO, final LoanAccountActionForm loanAccountActionForm,
                               final boolean isRepaymentIndepOfMeetingEnabled, final List<LoanAccountDetailsDto> loanAccountDetailsList,
                               final List<LoanBO> individualLoans, final Locale locale) throws AccountException, ServiceException {
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
//                glimLoanUpdater.createIndividualLoan(loanAccountActionForm, loanBO, isRepaymentIndepOfMeetingEnabled,
//                        loanAccountDetail);
            } else {
                foundLoans.add(individualLoan.getAccountId());
                try {
                    glimLoanUpdater.updateIndividualLoan(
                            loanAccountActionForm.getDisbursementDateValue(locale), loanAccountActionForm.getNoOfInstallmentsValue(),loanAccountDetail, individualLoan);
                } catch (InvalidDateException e) {
                    e.printStackTrace();
                }
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
                weekDay.setWeekdayName(ApplicationContextProvider.getBean(MessageLookup.class).lookup(weekDay.getPropertiesKey()));
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
