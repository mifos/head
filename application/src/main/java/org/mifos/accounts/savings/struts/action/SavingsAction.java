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

package org.mifos.accounts.savings.struts.action;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTransactionHistoryDto;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.accounts.savings.struts.actionforms.SavingsActionForm;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.struts.action.AccountAppAction;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.questionnaire.struts.QuestionnaireServiceFacadeLocator;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.ProcessFlowRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SavingsAccountCreationDto;
import org.mifos.dto.screen.SavingsAccountDepositDueDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavingsAction extends AccountAppAction {

    private SavingsBusinessService savingsService;

    private MasterDataService masterDataService;

    private static final Logger logger = LoggerFactory.getLogger(SavingsAction.class);

    private QuestionnaireServiceFacadeLocator questionnaireServiceFacadeLocator = new DefaultQuestionnaireServiceFacadeLocator();

    private QuestionnaireFlowAdapter createGroupQuestionnaire = new QuestionnaireFlowAdapter("Create", "Savings",
            ActionForwards.preview_success, "custSearchAction.do?method=loadMainSearch", questionnaireServiceFacadeLocator);

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("savingsAction");

        security.allow("getPrdOfferings", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("reLoad", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("getStatusHistory", SecurityConstants.VIEW);
        security.allow("edit", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("editPreview", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("editPrevious", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("update", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("getRecentActivity", SecurityConstants.VIEW);
        security.allow("getTransactionHistory", SecurityConstants.VIEW);
        security.allow("getDepositDueDetails", SecurityConstants.VIEW);
        security.allow("waiveAmountDue", SecurityConstants.SAVINGS_CANWAIVE_DUEAMOUNT);
        security.allow("waiveAmountOverDue", SecurityConstants.SAVINGS_CANWAIVE_OVERDUEAMOUNT);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    public SavingsAction() throws Exception {
        savingsService = new SavingsBusinessService();
        masterDataService = new MasterDataService();
    }

    @Override
    protected BusinessService getService() {
        return savingsService;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getPrdOfferings(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
        doCleanUp(savingsActionForm, request);

        Integer customerId = Integer.valueOf(savingsActionForm.getCustomerId());
        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        SessionUtils.setAttribute(SavingsConstants.CLIENT, customer, request);

        List<PrdOfferingDto> savingPrds = this.savingsServiceFacade.retrieveApplicableSavingsProductsForCustomer(customerId);

        SessionUtils.setCollectionAttribute(SavingsConstants.SAVINGS_PRD_OFFERINGS, savingPrds, request);

        return mapping.findForward(AccountConstants.GET_PRDOFFERINGS_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
        logger.debug(" selectedPrdOfferingId: " + savingsActionForm.getSelectedPrdOfferingId());
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());

        List<InterestCalcTypeEntity> interestCalculationTypes = this.savingsProductDao.retrieveInterestCalculationTypes();
        SessionUtils.setCollectionAttribute(MasterConstants.INTEREST_CAL_TYPES, interestCalculationTypes, request);

        Integer productId = Integer.valueOf(savingsActionForm.getSelectedPrdOfferingId());
        SavingsOfferingBO savingsOfferingBO = this.savingsProductDao.findById(productId);
        SessionUtils.setAttribute(SavingsConstants.PRDOFFERING, savingsOfferingBO, request);

        // NOTE - these details are included in SavingsProductReferenceDto but left as is to satisfy JSP at present
        SessionUtils.setCollectionAttribute(MasterConstants.SAVINGS_TYPE, masterDataService.retrieveMasterEntities(
                SavingsTypeEntity.class, uc.getLocaleId()), request);
        SessionUtils.setCollectionAttribute(MasterConstants.RECOMMENDED_AMOUNT_UNIT, masterDataService
                .retrieveMasterEntities(RecommendedAmntUnitEntity.class, uc.getLocaleId()), request);

        List<CustomFieldDefinitionEntity> customFieldDefinitions = savingsService.retrieveCustomFieldsDefinition();
        SessionUtils.setCollectionAttribute(SavingsConstants.CUSTOM_FIELDS, customFieldDefinitions, request);

        SavingsProductReferenceDto savingsProductReferenceDto = this.savingsServiceFacade.retrieveSavingsProductReferenceData(productId);

        savingsActionForm.setRecommendedAmount(savingsProductReferenceDto.getSavingsProductDetails().getAmountForDeposit().toString());

        List<CustomFieldDto> customFields = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, uc.getPreferredLocale());
        savingsActionForm.setAccountCustomFieldSet(customFields);

        return mapping.findForward("load_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward reLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
        logger.debug(" selectedPrdOfferingId: " + (savingsActionForm).getSelectedPrdOfferingId());

        Integer productId = Integer.valueOf(savingsActionForm.getSelectedPrdOfferingId());
        SavingsOfferingBO savingsOfferingBO = this.savingsProductDao.findById(productId);

        SessionUtils.setAttribute(SavingsConstants.PRDOFFERING, savingsOfferingBO, request);

        savingsActionForm.setRecommendedAmount(savingsOfferingBO.getRecommendedAmount().toString());
        logger.info("Data loaded successfully ");
        return mapping.findForward("load_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        SavingsActionForm savingActionForm = (SavingsActionForm) form;
        SessionUtils.setAttribute(SavingsConstants.IS_PENDING_APPROVAL, ProcessFlowRules.isSavingsPendingApprovalStateEnabled(), request.getSession());
        return createGroupQuestionnaire.fetchAppliedQuestions(mapping, savingActionForm, request, ActionForwards.preview_success);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward("previous_success");
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
        logger.debug("In SavingsAction::create(), accountStateId: " + savingsActionForm.getStateSelected());
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        CustomerBO customer = (CustomerBO) SessionUtils.getAttribute(SavingsConstants.CLIENT, request);

        Integer customerId = customer.getCustomerId();
        Integer productId = Integer.valueOf(savingsActionForm.getSelectedPrdOfferingId());
        String recommendedOrMandatoryAmount = savingsActionForm.getRecommendedAmount();
        Short accountState = Short.valueOf(savingsActionForm.getStateSelected());

        customer = this.customerDao.findCustomerById(customerId);
        checkPermissionForCreate(accountState, uc, customer.getOffice().getOfficeId(), customer.getPersonnel().getPersonnelId());

        List<CustomFieldDto> customFields = savingsActionForm.getAccountCustomFieldSet();

        SavingsAccountCreationDto savingsAccountCreation = new SavingsAccountCreationDto(productId, customerId, accountState, recommendedOrMandatoryAmount, customFields);
        Long savingsId = this.savingsServiceFacade.createSavingsAccount(savingsAccountCreation);

        SavingsBO saving = this.savingsDao.findById(savingsId);

        createGroupQuestionnaire.saveResponses(request, savingsActionForm, saving.getAccountId());

        request.setAttribute(SavingsConstants.GLOBALACCOUNTNUM, saving.getGlobalAccountNum());
        request.setAttribute(SavingsConstants.RECORD_OFFICE_ID, saving.getOffice().getOfficeId());
        request.setAttribute(SavingsConstants.CLIENT_NAME, customer.getDisplayName());
        request.setAttribute(SavingsConstants.CLIENT_ID, customer.getCustomerId());
        request.setAttribute(SavingsConstants.CLIENT_LEVEL, customer.getCustomerLevel().getId());

        logger.info("In SavingsAction::create(), Savings object saved successfully ");
        return mapping.findForward("create_success");
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        SavingsActionForm actionForm = (SavingsActionForm) form;
        actionForm.setInput(null);
        logger.debug(" Retrieving for globalAccountNum: " + actionForm.getGlobalAccountNum());

        SavingsBO savings = this.savingsDao.findBySystemId(actionForm.getGlobalAccountNum());
        savings.setUserContext(uc);

        savings.getAccountState().setLocaleId(uc.getLocaleId());
        for (AccountFlagMapping accountFlagMapping : savings.getAccountFlags()) {
            accountFlagMapping.getFlag().setLocaleId(uc.getLocaleId());
        }

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
        SessionUtils.setCollectionAttribute(MasterConstants.SAVINGS_TYPE, masterDataService.retrieveMasterEntities(SavingsTypeEntity.class, uc.getLocaleId()), request);
        SessionUtils.setCollectionAttribute(MasterConstants.RECOMMENDED_AMOUNT_UNIT, masterDataService.retrieveMasterEntities(RecommendedAmntUnitEntity.class, uc.getLocaleId()), request);
        SessionUtils.setCollectionAttribute(SavingsConstants.CUSTOM_FIELDS, savingsService.retrieveCustomFieldsDefinition(), request);

        SessionUtils.setAttribute(SavingsConstants.PRDOFFERING, savings.getSavingsOffering(), request);
        actionForm.setRecommendedAmount(savings.getSavingsOffering().getRecommendedAmount().toString());

        actionForm.clear();

        SessionUtils.setCollectionAttribute(SavingsConstants.RECENTY_ACTIVITY_DETAIL_PAGE, savings.getRecentAccountActivity(3), request);
        SessionUtils.setCollectionAttribute(SavingsConstants.NOTES, savings.getRecentAccountNotes(), request);
        logger.info(" Savings object retrieved successfully");

        setCurrentPageUrl(request, savings);
        setQuestionGroupInstances(request, savings);

        return mapping.findForward("get_success");
    }

    private void setQuestionGroupInstances(HttpServletRequest request, SavingsBO savingsBO) throws PageExpiredException {
        QuestionnaireServiceFacade questionnaireServiceFacade = questionnaireServiceFacadeLocator.getService(request);
        if (questionnaireServiceFacade != null) {
            setQuestionGroupInstances(questionnaireServiceFacade, request, savingsBO.getAccountId());
        }
    }

    // Intentionally made public to aid testing !
    public void setQuestionGroupInstances(QuestionnaireServiceFacade questionnaireServiceFacade, HttpServletRequest request, Integer savingsAccountId) throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = questionnaireServiceFacade.getQuestionGroupInstances(savingsAccountId, "View", "Savings");
        SessionUtils.setCollectionAttribute("questionGroupInstances", instanceDetails, request);
    }

    private void setCurrentPageUrl(HttpServletRequest request, SavingsBO savingsBO) throws PageExpiredException, UnsupportedEncodingException {
        SessionUtils.removeThenSetAttribute("currentPageUrl", constructCurrentPageUrl(request, savingsBO), request);
    }

    private String constructCurrentPageUrl(HttpServletRequest request, SavingsBO savingsBO) throws UnsupportedEncodingException {
        String globalAccountNum = request.getParameter("globalAccountNum");
        String officerId = request.getParameter("recordOfficeId");
        String loanOfficerId = request.getParameter("recordLoanOfficerId");
        String url = String.format("savingsAction.do?globalAccountNum=%s&customerId=%s&recordOfficeId=%s&recordLoanOfficerId=%s",
                globalAccountNum, Integer.toString(savingsBO.getAccountId()), officerId, loanOfficerId);
        return URLEncoder.encode(url, "UTF-8");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::edit()");
        SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        SavingsActionForm actionForm = (SavingsActionForm) form;
        actionForm.setRecommendedAmount(savings.getRecommendedAmount().toString());
        actionForm.setAccountCustomFieldSet(createCustomFieldViewsForEdit(savings.getAccountCustomFields(), request));
        return mapping.findForward("edit_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::editPreview()");
        return mapping.findForward("editPreview_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::editPrevious()");
        return mapping.findForward("editPrevious_success");
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::update()");
        SavingsActionForm actionForm = (SavingsActionForm) form;
        SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        Integer version = savings.getVersionNo();
        savings = new SavingsBusinessService().findById(savings.getAccountId());
        checkVersionMismatch(version, savings.getVersionNo());
        savings.setVersionNo(version);
        savings.setUserContext(uc);
        setInitialObjectForAuditLogging(savings);
        savings.update(new Money(savings.getCurrency(), actionForm.getRecommendedAmount()), actionForm.getAccountCustomFieldSet());
        request.setAttribute(SavingsConstants.GLOBALACCOUNTNUM, savings.getGlobalAccountNum());
        logger.info("In SavingsAction::update(), Savings object updated successfully");

        doCleanUp(actionForm, request);
        return mapping.findForward("update_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getRecentActivity(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::getRecentActivity()");
        // Check for order-by clause in AccountBO.hbm.xml and
        // AccountPayment.hbm.xml for accountPaymentSet and
        // accountTrxnSet. It should be set for the primay key column desc in
        // both. If stated is not there, the code
        // below will behave abnormally.
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        savings = savingsService.findById(savings.getAccountId());
        savings.setUserContext(uc);
        SessionUtils.setCollectionAttribute(SavingsConstants.RECENTY_ACTIVITY_LIST, savings
                .getRecentAccountActivity(null), request);
        return mapping.findForward("getRecentActivity_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getTransactionHistory(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::getRecentActivity()");
        String globalAccountNum = request.getParameter("globalAccountNum");
        SavingsBO savings = savingsService.findBySystemId(globalAccountNum);
        List<SavingsTransactionHistoryDto> savingsTransactionHistoryViewList = new ArrayList<SavingsTransactionHistoryDto>();
        // Check for order-by clause in AccountBO.hbm.xml,
        // AccountPayment.hbm.xml and AccountTrxnEntity.hbm.xml for
        // accountPaymentSet ,
        // accountTrxnSet and financialBoSet. They all should be set for their
        // primay key column desc in both. If stated is not there, the code
        // below will behave abnormally.
        List<AccountPaymentEntity> accountPaymentSet = savings.getAccountPayments();
        for (AccountPaymentEntity accountPaymentEntity : accountPaymentSet) {
            Set<AccountTrxnEntity> accountTrxnEntitySet = accountPaymentEntity.getAccountTrxns();
            for (AccountTrxnEntity accountTrxnEntity : accountTrxnEntitySet) {
                Set<FinancialTransactionBO> financialTransactionBOSet = accountTrxnEntity.getFinancialTransactions();
                for (FinancialTransactionBO financialTransactionBO : financialTransactionBOSet) {
                    SavingsTransactionHistoryDto savingsTransactionHistoryDto = new SavingsTransactionHistoryDto();
                    savingsTransactionHistoryDto.setTransactionDate(financialTransactionBO.getActionDate());
                    savingsTransactionHistoryDto.setPaymentId(accountTrxnEntity.getAccountPayment().getPaymentId());
                    savingsTransactionHistoryDto.setAccountTrxnId(accountTrxnEntity.getAccountTrxnId());
                    savingsTransactionHistoryDto.setType(financialTransactionBO.getFinancialAction().getName());
                    savingsTransactionHistoryDto.setGlcode(financialTransactionBO.getGlcode().getGlcode());
                    if (financialTransactionBO.isDebitEntry()) {
                        savingsTransactionHistoryDto.setDebit(String.valueOf(removeSign(financialTransactionBO
                                .getPostedAmount())));
                    } else if (financialTransactionBO.isCreditEntry()) {
                        savingsTransactionHistoryDto.setCredit(String.valueOf(removeSign(financialTransactionBO
                                .getPostedAmount())));
                    }
                    savingsTransactionHistoryDto.setBalance(String
                            .valueOf(removeSign(((SavingsTrxnDetailEntity) accountTrxnEntity).getBalance())));
                    savingsTransactionHistoryDto.setClientName(accountTrxnEntity.getCustomer().getDisplayName());
                    savingsTransactionHistoryDto.setPostedDate(financialTransactionBO.getPostedDate());
                    if (accountTrxnEntity.getPersonnel() != null) {
                        savingsTransactionHistoryDto.setPostedBy(accountTrxnEntity.getPersonnel().getDisplayName());
                    }
                    if (financialTransactionBO.getNotes() != null && !financialTransactionBO.getNotes().equals("")) {
                        savingsTransactionHistoryDto.setNotes(financialTransactionBO.getNotes());
                    }
                    savingsTransactionHistoryViewList.add(savingsTransactionHistoryDto);
                }
            }
        }
        SessionUtils.setCollectionAttribute(SavingsConstants.TRXN_HISTORY_LIST, savingsTransactionHistoryViewList,
                request);
        return mapping.findForward("getTransactionHistory_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getStatusHistory(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::getRecentActivity()");
        String globalAccountNum = request.getParameter("globalAccountNum");
        SavingsBO savings = savingsService.findBySystemId(globalAccountNum);
        savingsService.initialize(savings.getAccountStatusChangeHistory());
        savings.setUserContext((UserContext) SessionUtils
                .getAttribute(Constants.USER_CONTEXT_KEY, request.getSession()));
        List<AccountStatusChangeHistoryEntity> savingsStatusHistoryViewList = new ArrayList<AccountStatusChangeHistoryEntity>(
                savings.getAccountStatusChangeHistory());
        SessionUtils.setCollectionAttribute(SavingsConstants.STATUS_CHANGE_HISTORY_LIST, savingsStatusHistoryViewList,
                request);

        return mapping.findForward("getStatusHistory_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        logger.debug("In SavingsAction::validate(), method: " + method);
        String forward = null;
        if (method != null) {
            if (method.equals("preview")) {
                forward = "preview_faliure";
            } else if (method.equals("editPreview")) {
                forward = "editPreview_faliure";
            } else if (method.equals("load")) {
                forward = "load_faliure";
            } else if (method.equals("reLoad")) {
                forward = "reLoad_faliure";
            }
        }
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getDepositDueDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::getDepositDueDetails()");
        SavingsActionForm actionform = (SavingsActionForm) form;
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);

        String savingsSystemId = actionform.getGlobalAccountNum();

        SavingsBO savings = savingsService.findBySystemId(savingsSystemId);
        for (AccountActionDateEntity actionDate : savings.getAccountActionDates()) {
            savingsService.initialize(actionDate);
        }

        savingsService.initialize(savings.getAccountNotes());

        for (AccountFlagMapping accountFlagMapping : savings.getAccountFlags()) {
            savingsService.initialize(accountFlagMapping.getFlag());
            accountFlagMapping.getFlag().setLocaleId(uc.getLocaleId());
        }
        savingsService.initialize(savings.getAccountFlags());
        savings.getAccountState().setLocaleId(uc.getLocaleId());
        savings.setUserContext(uc);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);

        SavingsAccountDepositDueDto depositDueDetails = this.savingsServiceFacade.retrieveDepositDueDetails(savingsSystemId, uc.getLocaleId());

        return mapping.findForward("depositduedetails_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward waiveAmountDue(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::waiveAmountDue()");
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Integer versionNum = savings.getVersionNo();
        savings = savingsService.findBySystemId(((SavingsActionForm) form).getGlobalAccountNum());
        checkVersionMismatch(versionNum, savings.getVersionNo());
        savings.setUserContext(uc);
        savings.waiveAmountDue(WaiveEnum.ALL);
        return mapping.findForward("waiveAmount_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward waiveAmountOverDue(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In SavingsAction::waiveAmountOverDue()");
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        Integer versionNum = savings.getVersionNo();
        savings = savingsService.findBySystemId(((SavingsActionForm) form).getGlobalAccountNum());
        checkVersionMismatch(versionNum, savings.getVersionNo());
        savings.setUserContext(uc);
        savings.waiveAmountOverDue();
        return mapping.findForward("waiveAmount_success");
    }

    private void doCleanUp(SavingsActionForm savingsActionForm, HttpServletRequest request) throws Exception {
        savingsActionForm.clear();
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
    }

    private String removeSign(Money amount) {
        if (amount.isLessThanZero()) {
            return amount.negate().toString();
        }

        return amount.toString();
    }

    protected void checkPermissionForCreate(Short newState, UserContext userContext,
            Short officeId, Short loanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(newState, userContext, officeId, loanOfficerId)) {
            throw new AccountException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(Short newSate, UserContext userContext, Short officeId, Short loanOfficerId) {
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId,
                        loanOfficerId));
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createGroupQuestionnaire.validateResponses(request, (SavingsActionForm) form);
        if (errors != null && !errors.isEmpty()) {
            addErrors(request, errors);
            return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
        }
        return createGroupQuestionnaire.rejoinFlow(mapping);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editQuestionResponses(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "editQuestionResponses");
        return createGroupQuestionnaire.editResponses(mapping, request, (SavingsActionForm) form);
    }
}
