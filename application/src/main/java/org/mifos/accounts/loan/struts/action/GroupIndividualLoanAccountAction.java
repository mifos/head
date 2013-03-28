package org.mifos.accounts.loan.struts.action;
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
import static org.mifos.accounts.loan.util.helpers.LoanConstants.ADMINISTRATIVE_DOCUMENT_IS_ENABLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.CUSTOM_FIELDS;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.RECENTACCOUNTACTIVITIES;
import static org.mifos.framework.util.helpers.Constants.BUSINESS_KEY;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountOverpaymentEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.struts.action.AccountAppAction;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.platform.questionnaire.domain.InformationOrderService;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.reports.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.security.util.UserContext;


public class GroupIndividualLoanAccountAction extends AccountAppAction {
    private final LoanBusinessService loanBusinessService;
    private final ConfigurationPersistence configurationPersistence;
    
    private InformationOrderService informationOrderServiceFacade;
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    public static final String CUSTOMER_ID = "customerId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String GLOBAL_ACCOUNT_NUM = "globalAccountNum";

    public GroupIndividualLoanAccountAction() {
        this(new ConfigurationBusinessService(), ApplicationContextProvider.getBean(LoanBusinessService.class), new GlimLoanUpdater(),
                new LoanPrdBusinessService(),
                new ConfigurationPersistence(), new AccountBusinessService(),
                ApplicationContextProvider.getBean(InformationOrderService.class),
                ApplicationContextProvider.getBean(QuestionnaireServiceFacade.class));
    }

    public GroupIndividualLoanAccountAction(final ConfigurationBusinessService configService,
                             final LoanBusinessService loanBusinessService, final GlimLoanUpdater glimLoanUpdater,
                             final LoanPrdBusinessService loanPrdBusinessService,
                             final ConfigurationPersistence configurationPersistence,
                             final AccountBusinessService accountBusinessService,
                             InformationOrderService informationOrderServiceFacade,
                             QuestionnaireServiceFacade questionnaireServiceFacade) {
        super(accountBusinessService);

        this.loanBusinessService = loanBusinessService;
        this.configurationPersistence = configurationPersistence;
        this.informationOrderServiceFacade = informationOrderServiceFacade;
        this.questionnaireServiceFacade = questionnaireServiceFacade;
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

        List<QuestionGroupInstanceDetail> questions = new ArrayList<QuestionGroupInstanceDetail>();

        questions.addAll(questionnaireServiceFacade.getQuestionGroupInstancesWithUnansweredQuestionGroups(loanInformationDto.getAccountId(), "Create", "Loan"));
        questions.addAll(questionnaireServiceFacade.getQuestionGroupInstancesWithUnansweredQuestionGroups(loanInformationDto.getAccountId(), "Approve", "Loan"));
        questions.addAll(questionnaireServiceFacade.getQuestionGroupInstancesWithUnansweredQuestionGroups(loanInformationDto.getAccountId(), "View", "Loan"));
        questions.addAll(questionnaireServiceFacade.getQuestionGroupInstancesWithUnansweredQuestionGroups(loanInformationDto.getAccountId(), "Disburse", "Loan"));
        questions.addAll(questionnaireServiceFacade.getQuestionGroupInstancesWithUnansweredQuestionGroups(loanInformationDto.getAccountId(), "Close", "Loan"));
        SessionUtils.setCollectionAttribute("questionGroups", questions, request);
        SessionUtils.setCollectionAttribute("personalInformationOrder", informationOrderServiceFacade.getInformationOrder("Loan"), request);
        
        LoanBO loan = getLoan(loanInformationDto.getAccountId());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        setCurrentPageUrl(request, loan);
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
    
    private void setCurrentPageUrl(HttpServletRequest request, LoanBO loanBO) throws PageExpiredException, UnsupportedEncodingException {
        SessionUtils.removeThenSetAttribute("currentPageUrl", constructCurrentPageUrl(request, loanBO), request);
    }

    private String constructCurrentPageUrl(HttpServletRequest request, LoanBO loanBO) throws UnsupportedEncodingException {
        String globalAccountNum = request.getParameter("globalAccountNum");
        String officerId = request.getParameter("recordOfficeId");
        String loanOfficerId = request.getParameter("recordLoanOfficerId");
        String url = String.format("groupLoanAccountAction.do?globalAccountNum=%s&customerId=%s&recordOfficeId=%s&recordLoanOfficerId=%s",
                globalAccountNum, Integer.toString(loanBO.getAccountId()), officerId, loanOfficerId);
        return url;
    }

    LoanBO getLoan(Integer loanId) {
        return loanDao.findById(loanId);
    }

}