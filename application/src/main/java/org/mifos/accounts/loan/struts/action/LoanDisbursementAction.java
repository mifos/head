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

import static org.mifos.accounts.loan.util.helpers.LoanConstants.METHODCALLED;
import static org.mifos.framework.util.helpers.DateUtils.getUserLocaleDate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.struts.actionforms.LoanDisbursementActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.questionnaire.struts.DefaultQuestionnaireServiceFacadeLocator;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.dto.screen.LoanDisbursalDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoanDisbursementAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(LoanDisbursementAction.class);

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanDisbursementActionForm loanDisbursementActionForm = (LoanDisbursementActionForm) form;
        loanDisbursementActionForm.clear();
        loanDisbursementActionForm.setAmountCannotBeZero(false);

        Integer loanAccountId = Integer.valueOf(loanDisbursementActionForm.getAccountId());
        LoanDisbursalDto loanDisbursalDto = loanAccountServiceFacade.retrieveLoanDisbursalDetails(loanAccountId);

        UserContext uc = getUserContext(request);
        SessionUtils.setAttribute(LoanConstants.PROPOSED_DISBURSAL_DATE, loanDisbursalDto.getProposedDate(), request);
        loanDisbursementActionForm.setTransactionDate(getUserLocaleDate(uc.getPreferredLocale(), loanDisbursalDto.getProposedDate()));

        loanDisbursementActionForm.setAmount(loanDisbursalDto.getAmountPaidAtDisbursement());
        loanDisbursementActionForm.setLoanAmount(loanDisbursalDto.getLoanAmount());
        if (loanDisbursalDto.isMultiCurrencyEnabled()) {
            loanDisbursementActionForm.setCurrencyId(loanDisbursalDto.getCurrencyId());
        }

        Short repaymentIndependentOfMeetingScheduleValue = loanDisbursalDto.isRepaymentIndependentOfMeetingSchedule() ? Short.valueOf("1") : Short.valueOf("0");
        SessionUtils.setAttribute(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED, repaymentIndependentOfMeetingScheduleValue, request);
        SessionUtils.setAttribute(AccountingRulesConstants.BACKDATED_TRANSACTIONS_ALLOWED, loanDisbursalDto.isBackDatedTransactionsAllowed(), request);

        List<PaymentTypeEntity> paymentTypes = legacyAcceptedPaymentTypeDao.getAcceptedPaymentTypesForATransaction(uc.getLocaleId(), TrxnTypes.loan_disbursement.getValue());
        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, paymentTypes, request);

        return mapping.findForward(Constants.LOAD_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        LoanDisbursementActionForm loanDisbursementActionForm = (LoanDisbursementActionForm) form;
        return createGroupQuestionnaire.fetchAppliedQuestions(mapping, loanDisbursementActionForm, request, ActionForwards.preview_success);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, @SuppressWarnings("unused") final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.PREVIOUS_SUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward update(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {

        LoanDisbursementActionForm actionForm = (LoanDisbursementActionForm) form;

        UserContext uc = getUserContext(request);
        Date trxnDate = getDateFromString(actionForm.getTransactionDate(), uc.getPreferredLocale());
        trxnDate = DateUtils.getDateWithoutTimeStamp(trxnDate.getTime());
        Date receiptDate = getDateFromString(actionForm.getReceiptDate(), uc.getPreferredLocale());

        Integer loanAccountId = Integer.valueOf(actionForm.getAccountId());
        AccountBO accountBO = new AccountBusinessService().getAccount(loanAccountId);
        
        createGroupQuestionnaire.saveResponses(request, actionForm, loanAccountId);

        try {
            String paymentTypeIdStringForDisbursement = actionForm.getPaymentTypeId();
            Short paymentTypeIdForDisbursement = StringUtils.isEmpty(paymentTypeIdStringForDisbursement) ? PaymentTypes.CASH.getValue() : Short
                    .valueOf(paymentTypeIdStringForDisbursement);


            Short paymentTypeId = Short.valueOf(paymentTypeIdForDisbursement);
            final String comment = "";
            final BigDecimal disbursalAmount = new BigDecimal(actionForm.getLoanAmount());
            CustomerDto customerDto = null;

            PaymentTypeDto paymentType = null;
            AccountPaymentParametersDto loanDisbursement = new AccountPaymentParametersDto(new UserReferenceDto(uc.getId()), new AccountReferenceDto(
                    loanAccountId), disbursalAmount, new LocalDate(trxnDate), paymentType, comment,
                    new LocalDate(receiptDate), actionForm.getReceiptId(), customerDto);

            // GLIM
            List<LoanBO> individualLoans = this.loanDao.findIndividualLoans(loanAccountId);
            for (LoanBO individual : individualLoans) {
                if (!loanAccountServiceFacade.isTrxnDateValid(Integer.valueOf(individual.getAccountId()), trxnDate)) {
                    throw new BusinessRuleException("errors.invalidTxndateOfDisbursal");
                }
            }

            this.loanAccountServiceFacade.disburseLoan(loanDisbursement, paymentTypeId);

            for (LoanBO individual : individualLoans) {
                loanDisbursement = new AccountPaymentParametersDto(new UserReferenceDto(uc.getId()), new AccountReferenceDto(
                        individual.getAccountId()), individual.getLoanAmount().getAmount(), new LocalDate(trxnDate), paymentType, comment,
                        new LocalDate(receiptDate), actionForm.getReceiptId(), customerDto);
                
                this.loanAccountServiceFacade.disburseLoan(loanDisbursement, paymentTypeId);
            }
        } catch (BusinessRuleException e) {
            throw new AccountException(e.getMessage());
        } catch (MifosRuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof AccountException) {
                throw new AccountException(e.getCause().getMessage());
            }
            String msg = "errors.cannotDisburseLoan.because.disburseFailed";
            logger.error(msg, e);
            throw new AccountException(msg);
        } catch (Exception e) {
            String msg = "errors.cannotDisburseLoan.because.disburseFailed";
            logger.error(msg, e);
            throw new AccountException(msg);
        }

        return mapping.findForward(Constants.UPDATE_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        String forward = null;
        if (method != null) {
            forward = method + "_failure";
        }
        return mapping.findForward(forward);
    }

    @Override
    protected boolean isNewBizRequired(@SuppressWarnings("unused") final HttpServletRequest request) throws ServiceException {
        return false;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward captureQuestionResponses(
            final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse response) throws Exception {
        request.setAttribute(METHODCALLED, "captureQuestionResponses");
        ActionErrors errors = createGroupQuestionnaire.validateResponses(request, (LoanDisbursementActionForm) form);
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
        return createGroupQuestionnaire.editResponses(mapping, request, (LoanDisbursementActionForm) form);
    }

    private QuestionnaireFlowAdapter createGroupQuestionnaire = new QuestionnaireFlowAdapter("Disburse", "Loan",
            ActionForwards.preview_success, "custSearchAction.do?method=loadMainSearch",
            new DefaultQuestionnaireServiceFacadeLocator());
}