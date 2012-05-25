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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.struts.actionforms.RepayLoanActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.dto.screen.RepayLoanInfoDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepayLoanAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(RepayLoanAction.class);

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadRepayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.info("Loading repay loan page");
        RepayLoanActionForm actionForm = (RepayLoanActionForm) form;
        actionForm.setReceiptNumber(null);
        actionForm.setReceiptDate(null);
        actionForm.setPaymentTypeId(null);
        actionForm.setWaiverInterest(true);
        actionForm.setDateOfPayment(DateUtils.makeDateAsSentFromBrowser());
        actionForm.setTransferPaymentTypeId(this.legacyAcceptedPaymentTypeDao.getSavingsTransferId());
        UserContext userContext = getUserContext(request);

        String globalAccountNum = request.getParameter("globalAccountNum");
        RepayLoanDto repayLoanDto = this.loanAccountServiceFacade.retrieveLoanRepaymentDetails(globalAccountNum);

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        java.util.Date lastPaymentDate = new java.util.Date(0);
        AccountPaymentEntity lastPayment = loan.findMostRecentNonzeroPaymentByPaymentDate();
        if(lastPayment != null){
            lastPaymentDate = lastPayment.getPaymentDate();
        }
        actionForm.setLastPaymentDate(lastPaymentDate);

        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST, repayLoanDto.shouldWaiverInterest(), request);
        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST_SELECTED, repayLoanDto.shouldWaiverInterest(), request);
        SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, new Money(loan.getCurrency(), repayLoanDto.getEarlyRepaymentMoney()), request);
        SessionUtils.setAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, new Money(loan.getCurrency(), repayLoanDto.getWaivedRepaymentMoney()), request);
        SessionUtils.setCollectionAttribute(Constants.ACCOUNTS_FOR_TRANSFER, repayLoanDto.getSavingsAccountsForTransfer(), request);

        List<PaymentTypeEntity> loanPaymentTypes = legacyAcceptedPaymentTypeDao.getAcceptedPaymentTypesForATransaction(userContext.getLocaleId(), TrxnTypes.loan_repayment.getValue());
        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, loanPaymentTypes, request);
        return mapping.findForward(Constants.LOAD_SUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward makeRepayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.info("Performing loan repayment");

        UserContext userContext = getUserContext(request);

        RepayLoanActionForm repayLoanActionForm = (RepayLoanActionForm) form;
        Date receiptDate = null;
        if (StringUtils.isNotEmpty(repayLoanActionForm.getReceiptDate())) {
            receiptDate = repayLoanActionForm.getReceiptDateValue(userContext.getPreferredLocale());
        }
        String globalAccountNum = request.getParameter("globalAccountNum");

        String forward = Constants.UPDATE_SUCCESS;
        BigDecimal totalRepaymentAmount =((Money) SessionUtils.getAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, request)).getAmount();
        BigDecimal waivedAmount = ((Money) SessionUtils.getAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, request)).getAmount();
        RepayLoanInfoDto repayLoanInfoDto = new RepayLoanInfoDto(globalAccountNum,
                repayLoanActionForm.getAmount(), repayLoanActionForm.getReceiptNumber(),
                receiptDate, repayLoanActionForm.getPaymentTypeId(), userContext.getId(),
                repayLoanActionForm.isWaiverInterest(),
                repayLoanActionForm.getDateOfPaymentValue(userContext.getPreferredLocale()),totalRepaymentAmount,waivedAmount);

        if (repayLoanActionForm.isSavingsTransfer()) {
            this.loanAccountServiceFacade.makeEarlyRepaymentFromSavings(repayLoanInfoDto, repayLoanActionForm.getAccountForTransfer());
        } else {
            this.loanAccountServiceFacade.makeEarlyRepayment(repayLoanInfoDto);
        }

        SessionUtils.removeAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, request);
        SessionUtils.removeAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, request);
        SessionUtils.removeAttribute(Constants.ACCOUNTS_FOR_TRANSFER, request);
        return mapping.findForward(forward);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST_SELECTED, ((RepayLoanActionForm) form).isWaiverInterest(), request);
        return mapping.findForward(Constants.PREVIEW_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST_SELECTED, ((RepayLoanActionForm) form).isWaiverInterest(), request);
        return mapping.findForward(Constants.PREVIOUS_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        logger.debug("In RepayLoanAction::validate(), method: " + method);
        String forward = null;
        if (method != null && method.equals("preview")) {
            forward = ActionForwards.preview_failure.toString();
        }
        else {
            forward = ActionForwards.update_failure.toString();
        }
        return mapping.findForward(forward);
    }
}