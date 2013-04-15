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

package org.mifos.accounts.productdefinition.struts.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.productdefinition.business.CashFlowDetail;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanAmountFromLastLoanAmountBO;
import org.mifos.accounts.productdefinition.business.LoanAmountFromLoanCycleBO;
import org.mifos.accounts.productdefinition.business.LoanAmountSameForAllLoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFeesEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.PrdOfferingPenaltiesEntity;
import org.mifos.accounts.productdefinition.business.NoOfInstallFromLastLoanAmountBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallFromLoanCycleBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallSameForAllLoanBO;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.QuestionGroupReference;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.AccountingRules;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.dto.domain.PenaltyDto;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.security.util.UserContext;
import org.mifos.service.MifosServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated this entire class will soon be no longer used and will be deleted after localisation of messages.properties files is complete.
 *
 * note: all struts/jsp and tests around this will also be removed when view and define loan products links are switched over to use ftl/spring.
 */
@Deprecated
public class LoanPrdAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanOfferingBO.class);

    private LoanPrdBusinessService loanPrdBusinessService;

    public LoanPrdAction() {
        loanPrdBusinessService = new LoanPrdBusinessService();
    }

    @Override
    protected BusinessService getService() {
        return ApplicationContextProvider.getBean(LoanBusinessService.class);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start Load method of loan Product Action");
        request.getSession().setAttribute(ProductDefinitionConstants.LOANPRODUCTACTIONFORM, null);
        loadMasterData(request);
        setRepaymentIndepOfMeetingEnabledFlag(request);
        loadSelectedFeesPenaltiesAndFunds(new ArrayList<FeeDto>(), new ArrayList<FundBO>(), new ArrayList<PenaltyDto>(), request);
        logger.debug("Load method of loan Product Action called");
        request.getSession().setAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled());
        request.getSession().setAttribute("currencies", AccountingRules.getCurrencies());
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    private void setRepaymentIndepOfMeetingEnabledFlag(HttpServletRequest request) throws PageExpiredException {
        boolean repaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        SessionUtils.setAttribute(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED,
                repaymentIndepOfMeetingEnabled ? 1 : 0, request);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start preview method of loan Product Action");
        request.getSession().setAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled());
        if (AccountingRules.isMultiCurrencyEnabled()) {
            Short currencyId = ((LoanPrdActionForm) form).getCurrencyId();
            request.getSession().setAttribute("currencyCode",
                    getCurrency(currencyId).getCurrencyCode());
        }
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start previous method of Loan Product Action");
        return mapping.findForward(ActionForwards.previous_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancelCreate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start cancelCreate method of loan Product Action");
        return mapping.findForward(ActionForwards.cancelCreate_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute(ProductDefinitionConstants.METHODCALLED);
        logger.debug("start validate method of Loan Product Action" + method);
        if (method != null) {
            return mapping.findForward(method + "_failure");
        }
        return mapping.findForward(ActionForwards.preview_failure.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start create method of Loan Product Action");
        LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
        UserContext userContext = getUserContext(request);
        Locale locale = getLocale(userContext);

        LoanOfferingBO loanOffering = new LoanOfferingBO(
                userContext,
                loanPrdActionForm.getPrdOfferingName(),
                loanPrdActionForm.getPrdOfferingShortName(),
                getProductCategory(((List<ProductCategoryBO>) SessionUtils.getAttribute(
                        ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST, request)), loanPrdActionForm
                        .getPrdCategoryValue()),
                (PrdApplicableMasterEntity) findMasterEntity(request, ProductDefinitionConstants.LOANAPPLFORLIST,
                        loanPrdActionForm.getPrdApplicableMasterEnum().getValue()),
                loanPrdActionForm.getStartDateValue(locale),
                loanPrdActionForm.getEndDateValue(locale),
                loanPrdActionForm.getDescription(),
                (GracePeriodTypeEntity) findMasterEntity(request, ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
                        loanPrdActionForm.getGracePeriodTypeValue()),
                loanPrdActionForm.getGracePeriodDurationValue(),
                (InterestTypesEntity) findMasterEntity(request, ProductDefinitionConstants.INTERESTTYPESLIST,
                        loanPrdActionForm.getInterestTypesValue()),
                loanPrdActionForm.getMaxInterestRateValue(),
                loanPrdActionForm.getMinInterestRateValue(),
                loanPrdActionForm.getDefInterestRateValue(),
                loanPrdActionForm.isLoanCounterValue(),
                loanPrdActionForm.isIntDedAtDisbValue(),
                loanPrdActionForm.isPrinDueLastInstValue(),
                getFundsFromList(
                        (List<FundBO>) SessionUtils.getAttribute(ProductDefinitionConstants.SRCFUNDSLIST, request),
                        loanPrdActionForm.getLoanOfferingFunds()),
                getFeeList((List<FeeBO>) SessionUtils.getAttribute(ProductDefinitionConstants.LOANPRDFEE, request),
                        loanPrdActionForm.getPrdOfferinFees()),
                getPenaltyList(
                        (List<PenaltyBO>) SessionUtils.getAttribute(ProductDefinitionConstants.LOANPRDPENALTY, request),
                        loanPrdActionForm.getPrdOfferinPenalties()), new MeetingBO(
                        RecurrenceType.fromInt(loanPrdActionForm.getFreqOfInstallmentsValue()),
                        loanPrdActionForm.getRecurAfterValue(), loanPrdActionForm.getStartDateValue(locale),
                        MeetingType.LOAN_INSTALLMENT), findGLCodeEntity(request,
                        ProductDefinitionConstants.LOANPRICIPALGLCODELIST, loanPrdActionForm.getPrincipalGLCode()),
                findGLCodeEntity(request, ProductDefinitionConstants.LOANINTERESTGLCODELIST,
                        loanPrdActionForm.getInterestGLCode()), loanPrdActionForm,
                loanPrdActionForm.shouldWaiverInterest());

        loanOffering.setQuestionGroups(getQuestionGroups(request));
        loanOffering.setCurrency(getCurrency(loanPrdActionForm.getCurrencyId()));


        mapCashFlowDetail(loanPrdActionForm, loanOffering);
        mapVariableInstallmentDetails(loanOffering, loanPrdActionForm);
        loanOffering.setFixedRepaymentSchedule(loanPrdActionForm.isFixedRepaymentSchedule());

        loanOffering.save();
        request.setAttribute(ProductDefinitionConstants.LOANPRODUCTID, loanOffering.getPrdOfferingId());
        request.setAttribute(ProductDefinitionConstants.LOANPRDGLOBALOFFERINGNUM, loanOffering.getGlobalPrdOfferingNum());

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    // Intentionally made public to aid testing !!!
    @SuppressWarnings("unchecked")
    public Set<QuestionGroupReference> getQuestionGroups(HttpServletRequest request) throws PageExpiredException {
        Set<QuestionGroupReference> questionGroupReferences = null;
        List<QuestionGroupDetail> questionGroups = (List<QuestionGroupDetail>) SessionUtils.getAttribute(ProductDefinitionConstants.SELECTEDQGLIST, request);
        if (CollectionUtils.isNotEmpty(questionGroups)) {
            questionGroupReferences = new LinkedHashSet<QuestionGroupReference>(questionGroups.size());
            for (QuestionGroupDetail questionGroupDetail : questionGroups) {
                QuestionGroupReference questionGroupReference = new QuestionGroupReference();
                questionGroupReference.setQuestionGroupId(questionGroupDetail.getId());
                questionGroupReferences.add(questionGroupReference);
            }
        }
        return questionGroupReferences;
    }

    private void mapCashFlowDetail(LoanPrdActionForm loanPrdActionForm, LoanOfferingBO loanOffering) {
        boolean cashFlowValidationEnabled = loanPrdActionForm.getCashFlowValidation();
        loanOffering.setCashFlowCheckEnabled(cashFlowValidationEnabled);
        if (cashFlowValidationEnabled) {
            loanOffering.setCashFlowDetail(mapToCashFlowDetails(loanPrdActionForm));
        } else {
            loanOffering.setCashFlowDetail(null);
        }
    }

    private CashFlowDetail mapToCashFlowDetails(LoanPrdActionForm loanPrdActionForm) {
        CashFlowDetail cashFlowDetail = new CashFlowDetail();
        cashFlowDetail.setCashFlowThreshold(loanPrdActionForm.getCashFlowThresholdValue());
        cashFlowDetail.setIndebtednessRatio(loanPrdActionForm.getIndebtednessRatioValue());
        cashFlowDetail.setRepaymentCapacity(loanPrdActionForm.getRepaymentCapacityValue());
        return cashFlowDetail;
    }

    private void mapVariableInstallmentDetails(LoanOfferingBO loanOffering, LoanPrdActionForm loanPrdActionForm) {
        boolean variableInstallmentsAllowed = loanPrdActionForm.canConfigureVariableInstallments();
        loanOffering.setVariableInstallmentsAllowed(variableInstallmentsAllowed);
        if (variableInstallmentsAllowed) {
            loanOffering.setVariableInstallmentDetails(mapToVariableInstallmentDetails(loanOffering, loanPrdActionForm));
        } else {
            loanOffering.setVariableInstallmentDetails(null);
        }
    }

    private VariableInstallmentDetailsBO mapToVariableInstallmentDetails(LoanOfferingBO loanOffering, LoanPrdActionForm loanPrdActionForm) {
        VariableInstallmentDetailsBO variableInstallmentDetails = new VariableInstallmentDetailsBO();
        variableInstallmentDetails.setMinGapInDays(loanPrdActionForm.getMinimumGapBetweenInstallments());
        variableInstallmentDetails.setMaxGapInDays(loanPrdActionForm.getMaximumGapBetweenInstallments());
        String minInstallmentAmountStr = loanPrdActionForm.getMinimumInstallmentAmount();
        if (StringUtils.isNotEmpty(minInstallmentAmountStr)) {
            Money minInstallmentAmount = new Money(loanOffering.getCurrency(), minInstallmentAmountStr);
            variableInstallmentDetails.setMinInstallmentAmount(minInstallmentAmount);
        }
        return variableInstallmentDetails;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
        logger.debug("start manage of Loan Product Action " + loanPrdActionForm.getPrdOfferingId());
        Short prdOfferingId = loanPrdActionForm.getPrdOfferingIdValue();
        loanPrdActionForm.clear();
        LoanOfferingBO loanOffering = getLoanOffering(prdOfferingId);
        loadMasterData(request);
        loadSelectedFeesPenaltiesAndFunds(getFeesSelected(request, loanOffering), getFundsSelected(loanOffering), getPenaltiesSelected(loanOffering), request);
        loadStatusList(request);
        setDataIntoActionForm(loanOffering, loanPrdActionForm, request);
        setSelectedQuestionGroupsOnSession(request, loanOffering, getQuestionnaireServiceFacade(request));
        setCurrencyOnSession(request, loanOffering);
        setRepaymentIndepOfMeetingEnabledFlag(request);
        request.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        logger.debug("manage of Loan Product Action called" + prdOfferingId);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    private LoanOfferingBO getLoanOffering(Short prdOfferingId) throws ServiceException {
        return ((LoanPrdBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.LoanProduct)).getLoanOffering(prdOfferingId);
    }

    private void setCurrencyOnSession(HttpServletRequest request, LoanOfferingBO loanOffering) {
        request.getSession().setAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled());
        if (AccountingRules.isMultiCurrencyEnabled()) {
            request.getSession().setAttribute("currencyCode", loanOffering.getCurrency().getCurrencyCode());
        }
    }

    private List<FeeDto> getFeesSelected(HttpServletRequest request, LoanOfferingBO loanOffering) {
        List<FeeDto> feeSelected = new ArrayList<FeeDto>();
        for (LoanOfferingFeesEntity prdOfferingFees : loanOffering.getLoanOfferingFees()) {
            FeeBO fee = prdOfferingFees.getFees();
            fee = feeDao.initializeAndUnproxy(feeDao.findById(fee.getFeeId()));
            feeSelected.add(new FeeDto(getUserContext(request), fee));
        }
        return feeSelected;
    }

    private List<FundBO> getFundsSelected(LoanOfferingBO loanOffering) {
        List<FundBO> fundsSelected = new ArrayList<FundBO>();
        for (LoanOfferingFundEntity loanOfferingFund : loanOffering.getLoanOfferingFunds()) {
            fundsSelected.add(loanOfferingFund.getFund());
        }
        return fundsSelected;
    }
    
    private List<PenaltyDto> getPenaltiesSelected(LoanOfferingBO loanOffering) {
        List<PenaltyDto> penaltySelected = new ArrayList<PenaltyDto>();
        
        for (PrdOfferingPenaltiesEntity prdOfferingPenalties : loanOffering.getLoanOfferingPenalties()) {
            PenaltyBO penalty = prdOfferingPenalties.getPenalty();
            penalty = penaltyDao.findPenaltyById(penalty.getPenaltyId());
            penaltySelected.add(penalty.toDto());
        }
        
        return penaltySelected;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPreview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
    	request.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        logger.debug("start editPreview of Loan Product Action ");
        return mapping.findForward(ActionForwards.editPreview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editPrevious(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
    	request.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        logger.debug("start editPrevious of Loan Product Action ");
        return mapping.findForward(ActionForwards.editPrevious_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward editCancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
    	request.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        logger.debug("start cancelCreate method of loan Product Action");
        return mapping.findForward(ActionForwards.editcancel_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
        logger.debug("start update method of Loan Product Action" + loanPrdActionForm.getPrdOfferingId());
        UserContext userContext = getUserContext(request);
        Locale locale = getLocale(userContext);
        LoanOfferingBO loanOffering = getLoanOffering(loanPrdActionForm.getPrdOfferingIdValue());
        loanOffering.setUserContext(userContext);
        setInitialObjectForAuditLogging(loanOffering);
        mapVariableInstallmentDetails(loanOffering, loanPrdActionForm);
        loanOffering.setFixedRepaymentSchedule(loanPrdActionForm.isFixedRepaymentSchedule());
        mapCashFlowDetail(loanPrdActionForm, loanOffering);
        loanOffering
                .update(userContext.getId(),
                        loanPrdActionForm.getPrdOfferingName(),
                        loanPrdActionForm.getPrdOfferingShortName(),
                        getProductCategory(((List<ProductCategoryBO>) SessionUtils.getAttribute(
                                ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST, request)), loanPrdActionForm
                                .getPrdCategoryValue()),
                        (PrdApplicableMasterEntity) findMasterEntity(request,
                                ProductDefinitionConstants.LOANAPPLFORLIST, loanPrdActionForm
                                        .getPrdApplicableMasterEnum().getValue()),
                        loanPrdActionForm.getStartDateValue(locale),
                        loanPrdActionForm.getEndDateValue(locale),
                        loanPrdActionForm.getDescription(),
                        PrdStatus.fromInt(loanPrdActionForm.getPrdStatusValue()),
                        (GracePeriodTypeEntity) findMasterEntity(request,
                                ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
                                loanPrdActionForm.getGracePeriodTypeValue()),
                        (InterestTypesEntity) findMasterEntity(request, ProductDefinitionConstants.INTERESTTYPESLIST,
                                loanPrdActionForm.getInterestTypesValue()),
                        loanPrdActionForm.getGracePeriodDurationValue(),
                        loanPrdActionForm.getMaxInterestRateValue(),
                        loanPrdActionForm.getMinInterestRateValue(),
                        loanPrdActionForm.getDefInterestRateValue(),
                        loanPrdActionForm.isLoanCounterValue(),
                        loanPrdActionForm.isIntDedAtDisbValue(),
                        loanPrdActionForm.isPrinDueLastInstValue(),
                        getFundsFromList((List<FundBO>) SessionUtils.getAttribute(
                                ProductDefinitionConstants.SRCFUNDSLIST, request), loanPrdActionForm
                                .getLoanOfferingFunds()),
                        getFeeList(
                                (List<FeeBO>) SessionUtils.getAttribute(ProductDefinitionConstants.LOANPRDFEE, request),
                                loanPrdActionForm.getPrdOfferinFees()),
                        getPenaltyList((List<PenaltyBO>) SessionUtils.getAttribute(
                                ProductDefinitionConstants.LOANPRDPENALTY, request), loanPrdActionForm
                                .getPrdOfferinPenalties()), loanPrdActionForm.getRecurAfterValue(), RecurrenceType
                                .fromInt(loanPrdActionForm.getFreqOfInstallmentsValue()), loanPrdActionForm,
                        loanPrdActionForm.shouldWaiverInterest(), getQuestionGroups(request));

        logger.debug("update method of Loan Product Action called" + loanPrdActionForm.getPrdOfferingId());
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
        logger.debug("start get method of Loan Product Action" + loanPrdActionForm.getPrdOfferingId());
        LoanOfferingBO loanOffering = loanPrdBusinessService.getLoanOffering(loanPrdActionForm.getPrdOfferingIdValue(),
                getUserContext(request).getLocaleId());
        SessionUtils.setAttribute(ProductDefinitionConstants.LOANAMOUNTTYPE, loanOffering.checkLoanAmountType(),
                request);
        SessionUtils.setAttribute(ProductDefinitionConstants.INSTALLTYPE, loanOffering.checkNoOfInstallType(), request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanOffering, request);
        loanPrdActionForm.clear();
        loanPrdActionForm.setPrdOfferingId(getStringValue(loanOffering.getPrdOfferingId()));
        request.getSession().setAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled());
        request.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode());
        setSelectedQuestionGroupsOnSession(request, loanOffering, getQuestionnaireServiceFacade(request));
        logger.debug("get method of Loan Product Action called" + loanOffering.getPrdOfferingId());
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    // Intentionally made public to aid testing !!!
    public void setSelectedQuestionGroupsOnSession(HttpServletRequest request, LoanOfferingBO loanOffering,
                                    QuestionnaireServiceFacade questionnaireServiceFacade) throws PageExpiredException {
        Set<QuestionGroupReference> questionGroupReferences = loanOffering.getQuestionGroups();
        if (questionnaireServiceFacade != null && CollectionUtils.isNotEmpty(questionGroupReferences)) {
            List<QuestionGroupDetail> questionGroupDetails = new ArrayList<QuestionGroupDetail>();
            for (QuestionGroupReference questionGroupReference : questionGroupReferences) {
                Integer questionGroupId = questionGroupReference.getQuestionGroupId();
                QuestionGroupDetail questionGroupDetail = questionnaireServiceFacade.getQuestionGroupDetailForLoanPrd(questionGroupId);
                if (questionGroupDetail != null && questionGroupDetail.isActive()) {
                    questionGroupDetails.add(questionGroupDetail);
                }
            }
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.SELECTEDQGLIST, questionGroupDetails, request);
        }
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewAllLoanProducts(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start viewAllLoanProducts method of Loan Product Action");
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRODUCTLIST,
                ((LoanPrdBusinessService) ServiceFactory.getInstance().getBusinessService(
                        BusinessServiceName.LoanProduct)).getAllLoanOfferings(getUserContext(request).getLocaleId()),
                request);
        request.getSession().setAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled());
        request.getSession().setAttribute("currencies", AccountingRules.getCurrencies());
        return mapping.findForward(ActionForwards.viewAllLoanProducts_success.toString());
    }

    private void loadMasterData(HttpServletRequest request) throws Exception {
        logger.debug("start Load master data method of Loan Product Action ");
        LoanPrdBusinessService service = new LoanPrdBusinessService();

        List<FeeBO> fees = feeDao.getAllAppllicableFeeForLoanCreation();
        List<PenaltyBO> penalties = penaltyDao.getAllAppllicablePenaltyForLoanCreation();
        Short localeId = getUserContext(request).getLocaleId();

        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST, service
                .getActiveLoanProductCategories(), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANAPPLFORLIST, service
                .getLoanApplicableCustomerTypes(localeId), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANGRACEPERIODTYPELIST, legacyMasterDao.findMasterDataEntitiesWithLocale(
                GracePeriodTypeEntity.class), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.INTERESTTYPESLIST, legacyMasterDao.findMasterDataEntitiesWithLocale(
                InterestTypesEntity.class), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.INTCALCTYPESLIST, legacyMasterDao.findMasterDataEntitiesWithLocale(
                InterestCalcTypeEntity.class), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.SRCFUNDSLIST, this.fundDao.findAllFunds(),
                request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANFEESLIST, getFeeViewList(
                getUserContext(request), fees), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPENALTIESLIST, getPenaltyViewList(penalties), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRICIPALGLCODELIST, getGLCodes(
                FinancialActionConstants.PRINCIPALPOSTING, FinancialConstants.CREDIT), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANINTERESTGLCODELIST, getGLCodes(
                FinancialActionConstants.INTERESTPOSTING, FinancialConstants.CREDIT), request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDFEE, fees, request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDPENALTY, penalties, request);
        SessionUtils.setAttribute("GlNamesMode", AccountingRules.getGlNamesMode(), request);
        setQuestionGroupsOnSession(request, getQuestionnaireServiceFacade(request));
        logger.debug("Load master data method of Loan Product Action called");
    }

    // Intentionally made public to aid testing !!!
    public void setQuestionGroupsOnSession(HttpServletRequest request, QuestionnaireServiceFacade questionnaireServiceFacade) throws PageExpiredException {
        if (questionnaireServiceFacade != null) {
            List<QuestionGroupDetail> questionGroups = questionnaireServiceFacade.getQuestionGroups("Create", "Loan");
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.SRCQGLIST, questionGroups, request);
        }
    }

    private void loadSelectedFeesPenaltiesAndFunds(List<FeeDto> feesSelected, List<FundBO> fundsSelected, List<PenaltyDto> penaltiesSelected,
            HttpServletRequest request) throws Exception {
        logger.debug("start loadSelectedFeesAndFunds method of Loan Product Action ");
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDFEESELECTEDLIST, feesSelected, request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDPENALTYSELECTEDLIST, penaltiesSelected, request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST, fundsSelected, request);
        SessionUtils.setCollectionAttribute("king", fundsSelected, request);
        logger.debug("loadSelectedFeesAndFunds method of Loan Product Action called ");
    }

    private void loadStatusList(HttpServletRequest request) throws Exception {
        logger.debug("start Load Status list method of Loan Product Action ");
        LoanPrdBusinessService service = (LoanPrdBusinessService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.LoanProduct);
        Short localeId = getUserContext(request).getLocaleId();
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDSTATUSLIST, service
                .getApplicablePrdStatus(localeId), request);
        logger.debug("Load Status list method of Loan Product Action called");
    }

    private List<GLCodeEntity> getGLCodes(FinancialActionConstants financialAction, FinancialConstants debitCredit)
            throws Exception {
        logger.debug("getGLCodes method of Loan Product Action called");
        List<GLCodeEntity> glCodes = new FinancialBusinessService().getGLCodes(financialAction, debitCredit);
		return glCodes;
    }

    @SuppressWarnings("unchecked")
    private MasterDataEntity findMasterEntity(HttpServletRequest request, String collectionName, Short value)
            throws Exception {
        logger.debug("start findMasterEntity method of Loan Product Action ");
        if (value != null) {
            List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils.getAttribute(collectionName,
                    request);
            for (MasterDataEntity entity : entities) {
                if (entity.getId().equals(value)) {
                    return entity;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private GLCodeEntity findGLCodeEntity(HttpServletRequest request, String collectionName, String value)
            throws PageExpiredException {
        logger.debug("start findGLCodeEntity method of Loan Product Action ");
        List<GLCodeEntity> glCodeList = (List<GLCodeEntity>) SessionUtils.getAttribute(collectionName, request);
        for (GLCodeEntity glCode : glCodeList) {
            if (glCode.getGlcodeId().equals(getShortValue(value))) {
                return glCode;
            }
        }
        return null;
    }

    private ProductCategoryBO getProductCategory(List<ProductCategoryBO> productCategories, Short productCategoryId) {
        logger.debug("start getProductCategory method of Loan Product Action ");
        for (ProductCategoryBO productCategory : productCategories) {
            if (productCategory.getProductCategoryID().equals(productCategoryId)) {
                return productCategory;
            }
        }
        return null;
    }

    private List<FundBO> getFundsFromList(List<FundBO> funds, String[] fundsSelected) {
        logger.debug("start getFundsFromList method of Loan Product Action ");
        List<FundBO> fundList = new ArrayList<FundBO>();
        if (fundsSelected != null && fundsSelected.length > 0 && funds != null && funds.size() > 0) {
            for (String fundSelected : fundsSelected) {
                FundBO fund = getFundFromList(funds, fundSelected);
                if (fund != null) {
                    fundList.add(fund);
                }
            }
        }
        logger.debug("getFundsFromList method of Loan Product Action called");
        return fundList;
    }

    private FundBO getFundFromList(List<FundBO> funds, String fundSelected) {
        logger.debug("start getFundFromList method of Loan Product Action ");
        for (FundBO fund : funds) {
            if (fund.getFundId().equals(getShortValue(fundSelected))) {
                return fund;
            }
        }
        return null;
    }

    private List<FeeDto> getFeeViewList(UserContext userContext, List<FeeBO> fees) {
        logger.debug("start getFeeViewList method of Loan Product Action ");
        List<FeeDto> feeDtos = new ArrayList<FeeDto>();
        if (fees != null && fees.size() > 0) {
            for (FeeBO fee : fees) {
                feeDtos.add(new FeeDto(userContext, fee));
            }
        }
        logger.debug("getFeeViewList method of Loan Product Action called");
        return feeDtos.size() > 0 ? feeDtos : null;
    }
    
    private List<PenaltyDto> getPenaltyViewList(List<PenaltyBO> penalties) {
        logger.debug("start getPenaltyViewList method of Loan Product Action ");
        List<PenaltyDto> penaltyDtos = new ArrayList<PenaltyDto>();
        if (penalties != null && penalties.size() > 0) {
            for (PenaltyBO penalty : penalties) {
                penaltyDtos.add(penalty.toDto());
            }
        }
        logger.debug("getPenaltyViewList method of Loan Product Action called");
        return penaltyDtos.size() > 0 ? penaltyDtos : null;
    }

    private List<FeeBO> getFeeList(List<FeeBO> fees, String[] feesSelected) {
        logger.debug("start getFeeList method of Loan Product Action ");
        List<FeeBO> feeList = new ArrayList<FeeBO>();
        if (feesSelected != null && feesSelected.length > 0 && fees != null && fees.size() > 0) {
            for (String feeSelected : feesSelected) {
                FeeBO fee = getFeeFromList(fees, feeSelected);
                if (fee != null) {
                    feeList.add(fee);
                }
            }
        }
        logger.debug("getFeeList method of Loan Product Action called");
        return feeList;
    }
    
    private List<PenaltyBO> getPenaltyList(List<PenaltyBO> penalties, String[] penaltiesSelected) {
        logger.debug("start getPenaltyList method of Loan Product Action ");
        List<PenaltyBO> penaltyList = new ArrayList<PenaltyBO>();
        if (penaltiesSelected != null && penaltiesSelected.length > 0 && penalties != null && penalties.size() > 0) {
            for (String penaltySelected : penaltiesSelected) {
                PenaltyBO penalty = getPenaltyFromList(penalties, penaltySelected);
                if (penalty != null) {
                    penaltyList.add(penalty);
                }
            }
        }
        logger.debug("getPenaltyList method of Loan Product Action called");
        return penaltyList;
    }

    private FeeBO getFeeFromList(List<FeeBO> fees, String feeSelected) {
        logger.debug("start getFeeFromList method of Loan Product Action ");
        for (FeeBO fee : fees) {
            if (fee.getFeeId().equals(getShortValue(feeSelected))) {
                return fee;
            }
        }
        return null;
    }
    
    private PenaltyBO getPenaltyFromList(List<PenaltyBO> penalties, String penaltySelected) {
        logger.debug("start getPenaltyFromList method of Loan Product Action ");
        for (PenaltyBO penalty : penalties) {
            if (penalty.getPenaltyId().equals(getShortValue(penaltySelected))) {
                return penalty;
            }
        }
        
        return null;
    }

    private void setDataIntoActionForm(LoanOfferingBO loanProduct, LoanPrdActionForm loanPrdActionForm,
            HttpServletRequest request) throws Exception {
        logger.debug("start setDataIntoActionForm method of Loan Product Action ");
        loanPrdActionForm.setPrdOfferingId(getStringValue(loanProduct.getPrdOfferingId()));
        loanPrdActionForm.setPrdOfferingName(loanProduct.getPrdOfferingName());
        loanPrdActionForm.setPrdOfferingShortName(loanProduct.getPrdOfferingShortName());
        loanPrdActionForm.setPrdCategory(getStringValue(loanProduct.getPrdCategory().getProductCategoryID()));
        loanPrdActionForm.setPrdStatus(getStringValue(loanProduct.getStatus().getValue()));
        loanPrdActionForm.setPrdApplicableMaster(loanProduct.getPrdApplicableMasterEnum());
        loanPrdActionForm.setStartDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(),
                DateUtils.toDatabaseFormat(loanProduct.getStartDate())));
        loanPrdActionForm.setEndDate(loanProduct.getEndDate() != null ? DateUtils.getUserLocaleDate(getUserContext(
                request).getPreferredLocale(), DateUtils.toDatabaseFormat(loanProduct.getEndDate())) : null);
        loanPrdActionForm.setDescription(loanProduct.getDescription());
        loanPrdActionForm.setGracePeriodType(getStringValue(loanProduct.getGracePeriodType().getId()));
        loanPrdActionForm.setGracePeriodDuration(getStringValue(loanProduct.getGracePeriodDuration()));
        loanPrdActionForm.setInterestTypes(getStringValue(loanProduct.getInterestTypes().getId()));
        loanPrdActionForm.setMaxInterestRate(getDoubleStringForInterest(loanProduct.getMaxInterestRate()));
        loanPrdActionForm.setMinInterestRate(getDoubleStringForInterest(loanProduct.getMinInterestRate()));
        loanPrdActionForm.setDefInterestRate(getDoubleStringForInterest(loanProduct.getDefInterestRate()));
        loanPrdActionForm.setIntDedDisbursementFlag(getStringValue(loanProduct.isIntDedDisbursement()));
        loanPrdActionForm.setPrinDueLastInstFlag(getStringValue(loanProduct.isPrinDueLastInst()));
        loanPrdActionForm.setLoanCounter(getStringValue(loanProduct.isIncludeInLoanCounter()));
        loanPrdActionForm.setWaiverInterest(getStringValue(loanProduct.isInterestWaived()));
        loanPrdActionForm.setRecurAfter(getStringValue(loanProduct.getLoanOfferingMeeting().getMeeting()
                .getMeetingDetails().getRecurAfter()));
        loanPrdActionForm.setFreqOfInstallments(getStringValue(loanProduct.getLoanOfferingMeeting().getMeeting()
                .getMeetingDetails().getRecurrenceType().getRecurrenceId()));
        loanPrdActionForm.setPrincipalGLCode(getStringValue(loanProduct.getPrincipalGLcode().getGlcodeId()));
        loanPrdActionForm.setInterestGLCode(getStringValue(loanProduct.getInterestGLcode().getGlcodeId()));
        setVariableInstallmentDetailsOnLoanProductForm(loanPrdActionForm, loanProduct);
        loanPrdActionForm.setIsFixedRepaymentSchedule(loanProduct.isFixedRepaymentSchedule());

        loanPrdActionForm.setCashFlowValidation(loanProduct.isCashFlowCheckEnabled());
        CashFlowDetail cashFlowDetail = loanProduct.getCashFlowDetail();
        if(cashFlowDetail != null) {
            Double cashFlowThreshold = cashFlowDetail.getCashFlowThreshold();
            if(cashFlowThreshold != null) {
                loanPrdActionForm.setCashFlowThreshold(String.valueOf(cashFlowThreshold));
            }
            Double indebtednessRatio = cashFlowDetail.getIndebtednessRatio();
            if (indebtednessRatio != null) {
                loanPrdActionForm.setIndebtednessRatio(String.valueOf(indebtednessRatio));
            }
            Double repaymentCapacity = cashFlowDetail.getRepaymentCapacity();
            if (repaymentCapacity != null) {
                loanPrdActionForm.setRepaymentCapacity(String.valueOf(repaymentCapacity));
            }
        }

        if (loanProduct.isLoanAmountTypeSameForAllLoan()) {
            loanPrdActionForm.setLoanAmtCalcType(getStringValue(ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN));
            Iterator<LoanAmountSameForAllLoanBO> loanAmountSameForAllItr = loanProduct.getLoanAmountSameForAllLoan()
                    .iterator();
            while (loanAmountSameForAllItr.hasNext()) {
                LoanAmountSameForAllLoanBO loanAmountSameForAllLoanBO = loanAmountSameForAllItr.next();
                loanPrdActionForm.setMaxLoanAmount(getDoubleStringForMoney(loanAmountSameForAllLoanBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setMinLoanAmount(getDoubleStringForMoney(loanAmountSameForAllLoanBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setDefaultLoanAmount(getDoubleStringForMoney(loanAmountSameForAllLoanBO
                        .getDefaultLoanAmount()));

            }
        }
        if (loanProduct.isLoanAmountTypeAsOfLastLoanAmount()) {
            loanPrdActionForm.setLoanAmtCalcType(getStringValue(ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN));
            Iterator<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoanAmountItr = loanProduct
                    .getLoanAmountFromLastLoan().iterator();
            while (loanAmountFromLastLoanAmountItr.hasNext()) {
                LoanAmountFromLastLoanAmountBO loanAmountFromLastLoanAmountBO = loanAmountFromLastLoanAmountItr.next();
                loanPrdActionForm.setLastLoanMinLoanAmt1(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setLastLoanMaxLoanAmt1(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setLastLoanDefaultLoanAmt1(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getDefaultLoanAmount()));
                loanPrdActionForm.setStartRangeLoanAmt1(loanAmountFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndRangeLoanAmt1(loanAmountFromLastLoanAmountBO.getEndRange().intValue());
                loanAmountFromLastLoanAmountBO = loanAmountFromLastLoanAmountItr.next();
                loanPrdActionForm.setLastLoanMinLoanAmt2(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setLastLoanMaxLoanAmt2(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setLastLoanDefaultLoanAmt2(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getDefaultLoanAmount()));
                loanPrdActionForm.setStartRangeLoanAmt2(loanAmountFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndRangeLoanAmt2(loanAmountFromLastLoanAmountBO.getEndRange().intValue());
                loanAmountFromLastLoanAmountBO = loanAmountFromLastLoanAmountItr.next();
                loanPrdActionForm.setLastLoanMinLoanAmt3(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setLastLoanMaxLoanAmt3(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setLastLoanDefaultLoanAmt3(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getDefaultLoanAmount()));
                loanPrdActionForm.setStartRangeLoanAmt3(loanAmountFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndRangeLoanAmt3(loanAmountFromLastLoanAmountBO.getEndRange().intValue());
                loanAmountFromLastLoanAmountBO = loanAmountFromLastLoanAmountItr.next();
                loanPrdActionForm.setLastLoanMinLoanAmt4(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setLastLoanMaxLoanAmt4(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setLastLoanDefaultLoanAmt4(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getDefaultLoanAmount()));
                loanPrdActionForm.setStartRangeLoanAmt4(loanAmountFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndRangeLoanAmt4(loanAmountFromLastLoanAmountBO.getEndRange().intValue());
                loanAmountFromLastLoanAmountBO = loanAmountFromLastLoanAmountItr.next();
                loanPrdActionForm.setLastLoanMinLoanAmt5(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setLastLoanMaxLoanAmt5(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setLastLoanDefaultLoanAmt5(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getDefaultLoanAmount()));
                loanPrdActionForm.setStartRangeLoanAmt5(loanAmountFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndRangeLoanAmt5(loanAmountFromLastLoanAmountBO.getEndRange().intValue());
                loanAmountFromLastLoanAmountBO = loanAmountFromLastLoanAmountItr.next();
                loanPrdActionForm.setLastLoanMinLoanAmt6(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setLastLoanMaxLoanAmt6(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setLastLoanDefaultLoanAmt6(getDoubleStringForMoney(loanAmountFromLastLoanAmountBO
                        .getDefaultLoanAmount()));
                loanPrdActionForm.setStartRangeLoanAmt6(loanAmountFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndRangeLoanAmt6(loanAmountFromLastLoanAmountBO.getEndRange().intValue());
            }
        }
        if (loanProduct.isLoanAmountTypeFromLoanCycle()) {
            loanPrdActionForm.setLoanAmtCalcType(getStringValue(ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE));
            Iterator<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycleItr = loanProduct.getLoanAmountFromLoanCycle()
                    .iterator();
            while (loanAmountFromLoanCycleItr.hasNext()) {
                LoanAmountFromLoanCycleBO loanAmountFromLoanCycleBO = loanAmountFromLoanCycleItr.next();
                loanPrdActionForm.setCycleLoanMinLoanAmt1(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setCycleLoanMaxLoanAmt1(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setCycleLoanDefaultLoanAmt1(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getDefaultLoanAmount()));
                loanAmountFromLoanCycleBO = loanAmountFromLoanCycleItr.next();
                loanPrdActionForm.setCycleLoanMinLoanAmt2(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setCycleLoanMaxLoanAmt2(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setCycleLoanDefaultLoanAmt2(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getDefaultLoanAmount()));
                loanAmountFromLoanCycleBO = loanAmountFromLoanCycleItr.next();
                loanPrdActionForm.setCycleLoanMinLoanAmt3(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setCycleLoanMaxLoanAmt3(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setCycleLoanDefaultLoanAmt3(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getDefaultLoanAmount()));
                loanAmountFromLoanCycleBO = loanAmountFromLoanCycleItr.next();
                loanPrdActionForm.setCycleLoanMinLoanAmt4(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setCycleLoanMaxLoanAmt4(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setCycleLoanDefaultLoanAmt4(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getDefaultLoanAmount()));
                loanAmountFromLoanCycleBO = loanAmountFromLoanCycleItr.next();
                loanPrdActionForm.setCycleLoanMinLoanAmt5(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setCycleLoanMaxLoanAmt5(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setCycleLoanDefaultLoanAmt5(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getDefaultLoanAmount()));
                loanAmountFromLoanCycleBO = loanAmountFromLoanCycleItr.next();
                loanPrdActionForm.setCycleLoanMinLoanAmt6(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMinLoanAmount()));
                loanPrdActionForm.setCycleLoanMaxLoanAmt6(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getMaxLoanAmount()));
                loanPrdActionForm.setCycleLoanDefaultLoanAmt6(getDoubleStringForMoney(loanAmountFromLoanCycleBO
                        .getDefaultLoanAmount()));
            }
        }

        if (loanProduct.isNoOfInstallTypeSameForAllLoan()) {
            loanPrdActionForm
                    .setCalcInstallmentType(getStringValue(ProductDefinitionConstants.NOOFINSTALLSAMEFORALLLOAN));
            Iterator<NoOfInstallSameForAllLoanBO> noOfInstallSameForAllLoanItr = loanProduct
                    .getNoOfInstallSameForAllLoan().iterator();
            while (noOfInstallSameForAllLoanItr.hasNext()) {
                NoOfInstallSameForAllLoanBO noOfInstallSameForAllLoanBO = noOfInstallSameForAllLoanItr.next();
                loanPrdActionForm.setMaxNoInstallments(getStringValue(noOfInstallSameForAllLoanBO.getMaxNoOfInstall()));
                loanPrdActionForm.setMinNoInstallments(getStringValue(noOfInstallSameForAllLoanBO.getMinNoOfInstall()));
                loanPrdActionForm.setDefNoInstallments(getStringValue(noOfInstallSameForAllLoanBO
                        .getDefaultNoOfInstall()));
            }
        }
        if (loanProduct.isNoOfInstallTypeFromLastLoan()) {
            loanPrdActionForm
                    .setCalcInstallmentType(getStringValue(ProductDefinitionConstants.NOOFINSTALLFROMLASTLOAN));
            Iterator<NoOfInstallFromLastLoanAmountBO> noOfInstallFromLastAmountItr = loanProduct
                    .getNoOfInstallFromLastLoan().iterator();
            while (noOfInstallFromLastAmountItr.hasNext()) {
                NoOfInstallFromLastLoanAmountBO noOfInstallFromLastLoanAmountBO = noOfInstallFromLastAmountItr.next();
                loanPrdActionForm.setMinLoanInstallment1(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMinNoOfInstall()));
                loanPrdActionForm.setMaxLoanInstallment1(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMaxNoOfInstall()));
                loanPrdActionForm.setDefLoanInstallment1(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getDefaultNoOfInstall()));
                loanPrdActionForm.setStartInstallmentRange1(noOfInstallFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndInstallmentRange1(noOfInstallFromLastLoanAmountBO.getEndRange().intValue());
                noOfInstallFromLastLoanAmountBO = noOfInstallFromLastAmountItr.next();
                loanPrdActionForm.setMinLoanInstallment2(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMinNoOfInstall()));
                loanPrdActionForm.setMaxLoanInstallment2(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMaxNoOfInstall()));
                loanPrdActionForm.setDefLoanInstallment2(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getDefaultNoOfInstall()));
                loanPrdActionForm.setStartInstallmentRange2(noOfInstallFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndInstallmentRange2(noOfInstallFromLastLoanAmountBO.getEndRange().intValue());
                noOfInstallFromLastLoanAmountBO = noOfInstallFromLastAmountItr.next();
                loanPrdActionForm.setMinLoanInstallment3(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMinNoOfInstall()));
                loanPrdActionForm.setMaxLoanInstallment3(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMaxNoOfInstall()));
                loanPrdActionForm.setDefLoanInstallment3(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getDefaultNoOfInstall()));
                loanPrdActionForm.setStartInstallmentRange3(noOfInstallFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndInstallmentRange3(noOfInstallFromLastLoanAmountBO.getEndRange().intValue());
                noOfInstallFromLastLoanAmountBO = noOfInstallFromLastAmountItr.next();
                loanPrdActionForm.setMinLoanInstallment4(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMinNoOfInstall()));
                loanPrdActionForm.setMaxLoanInstallment4(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMaxNoOfInstall()));
                loanPrdActionForm.setDefLoanInstallment4(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getDefaultNoOfInstall()));
                loanPrdActionForm.setStartInstallmentRange4(noOfInstallFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndInstallmentRange4(noOfInstallFromLastLoanAmountBO.getEndRange().intValue());
                noOfInstallFromLastLoanAmountBO = noOfInstallFromLastAmountItr.next();
                loanPrdActionForm.setMinLoanInstallment5(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMinNoOfInstall()));
                loanPrdActionForm.setMaxLoanInstallment5(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMaxNoOfInstall()));
                loanPrdActionForm.setDefLoanInstallment5(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getDefaultNoOfInstall()));
                loanPrdActionForm.setStartInstallmentRange5(noOfInstallFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndInstallmentRange5(noOfInstallFromLastLoanAmountBO.getEndRange().intValue());
                noOfInstallFromLastLoanAmountBO = noOfInstallFromLastAmountItr.next();
                loanPrdActionForm.setMinLoanInstallment6(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMinNoOfInstall()));
                loanPrdActionForm.setMaxLoanInstallment6(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getMaxNoOfInstall()));
                loanPrdActionForm.setDefLoanInstallment6(getStringValue(noOfInstallFromLastLoanAmountBO
                        .getDefaultNoOfInstall()));
                loanPrdActionForm.setStartInstallmentRange6(noOfInstallFromLastLoanAmountBO.getStartRange().intValue());
                loanPrdActionForm.setEndInstallmentRange6(noOfInstallFromLastLoanAmountBO.getEndRange().intValue());
            }
        }
        if (loanProduct.isNoOfInstallTypeFromLoanCycle()) {
            loanPrdActionForm
                    .setCalcInstallmentType(getStringValue(ProductDefinitionConstants.NOOFINSTALLFROMLOANCYCLLE));
            Iterator<NoOfInstallFromLoanCycleBO> noOfInstallFromLoanCycleItr = loanProduct
                    .getNoOfInstallFromLoanCycle().iterator();
            while (noOfInstallFromLoanCycleItr.hasNext()) {
                NoOfInstallFromLoanCycleBO noOfInstallFromLoanCycleBO = noOfInstallFromLoanCycleItr.next();
                loanPrdActionForm
                        .setMinCycleInstallment1(getStringValue(noOfInstallFromLoanCycleBO.getMinNoOfInstall()));
                loanPrdActionForm
                        .setMaxCycleInstallment1(getStringValue(noOfInstallFromLoanCycleBO.getMaxNoOfInstall()));
                loanPrdActionForm.setDefCycleInstallment1(getStringValue(noOfInstallFromLoanCycleBO
                        .getDefaultNoOfInstall()));
                noOfInstallFromLoanCycleBO = noOfInstallFromLoanCycleItr.next();
                loanPrdActionForm
                        .setMinCycleInstallment2(getStringValue(noOfInstallFromLoanCycleBO.getMinNoOfInstall()));
                loanPrdActionForm
                        .setMaxCycleInstallment2(getStringValue(noOfInstallFromLoanCycleBO.getMaxNoOfInstall()));
                loanPrdActionForm.setDefCycleInstallment2(getStringValue(noOfInstallFromLoanCycleBO
                        .getDefaultNoOfInstall()));
                noOfInstallFromLoanCycleBO = noOfInstallFromLoanCycleItr.next();
                loanPrdActionForm
                        .setMinCycleInstallment3(getStringValue(noOfInstallFromLoanCycleBO.getMinNoOfInstall()));
                loanPrdActionForm
                        .setMaxCycleInstallment3(getStringValue(noOfInstallFromLoanCycleBO.getMaxNoOfInstall()));
                loanPrdActionForm.setDefCycleInstallment3(getStringValue(noOfInstallFromLoanCycleBO
                        .getDefaultNoOfInstall()));
                noOfInstallFromLoanCycleBO = noOfInstallFromLoanCycleItr.next();
                loanPrdActionForm
                        .setMinCycleInstallment4(getStringValue(noOfInstallFromLoanCycleBO.getMinNoOfInstall()));
                loanPrdActionForm
                        .setMaxCycleInstallment4(getStringValue(noOfInstallFromLoanCycleBO.getMaxNoOfInstall()));
                loanPrdActionForm.setDefCycleInstallment4(getStringValue(noOfInstallFromLoanCycleBO
                        .getDefaultNoOfInstall()));
                noOfInstallFromLoanCycleBO = noOfInstallFromLoanCycleItr.next();
                loanPrdActionForm
                        .setMinCycleInstallment5(getStringValue(noOfInstallFromLoanCycleBO.getMinNoOfInstall()));
                loanPrdActionForm
                        .setMaxCycleInstallment5(getStringValue(noOfInstallFromLoanCycleBO.getMaxNoOfInstall()));
                loanPrdActionForm.setDefCycleInstallment5(getStringValue(noOfInstallFromLoanCycleBO
                        .getDefaultNoOfInstall()));
                noOfInstallFromLoanCycleBO = noOfInstallFromLoanCycleItr.next();
                loanPrdActionForm
                        .setMinCycleInstallment6(getStringValue(noOfInstallFromLoanCycleBO.getMinNoOfInstall()));
                loanPrdActionForm
                        .setMaxCycleInstallment6(getStringValue(noOfInstallFromLoanCycleBO.getMaxNoOfInstall()));
                loanPrdActionForm.setDefCycleInstallment6(getStringValue(noOfInstallFromLoanCycleBO
                        .getDefaultNoOfInstall()));

            }

        }
        SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE, loanProduct.getStartDate(), request);
        logger.debug("setDataIntoActionForm method of Loan Product Action called");

    }
    private QuestionnaireServiceFacade getQuestionnaireServiceFacade(HttpServletRequest request) {
        return MifosServiceFactory.getQuestionnaireServiceFacade(request);
    }
    private void setVariableInstallmentDetailsOnLoanProductForm(LoanPrdActionForm loanPrdActionForm, LoanOfferingBO loanOfferingBO) {
        boolean variableInstallmentsAllowed = loanOfferingBO.isVariableInstallmentsAllowed();
        loanPrdActionForm.setCanConfigureVariableInstallments(variableInstallmentsAllowed);
        if (variableInstallmentsAllowed) {
            VariableInstallmentDetailsBO variableInstallmentDetails = loanOfferingBO.getVariableInstallmentDetails();
            loanPrdActionForm.setMinimumGapBetweenInstallments(variableInstallmentDetails.getMinGapInDays());
            loanPrdActionForm.setMaximumGapBetweenInstallments(variableInstallmentDetails.getMaxGapInDays());
            double amount = variableInstallmentDetails.getMinInstallmentAmount().getAmountDoubleValue();
            loanPrdActionForm.setMinimumInstallmentAmount(String.valueOf(amount));
        }
    }
}
