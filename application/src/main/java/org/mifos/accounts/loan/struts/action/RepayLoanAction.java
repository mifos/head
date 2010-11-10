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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.loan.struts.actionforms.RepayLoanActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.servicefacade.RepayLoanDto;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;

public class RepayLoanAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(RepayLoanAction.class);

    @Override
    protected BusinessService getService() throws ServiceException {
        return null;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("repayLoanAction");
        security.allow("loadRepayment", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        security.allow("preview", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        security.allow("previous", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        security.allow("makeRepayment", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadRepayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        logger.info("Loading repay loan page");
        clearActionForm(form);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        RepayLoanDto repayLoanDto = loanServiceFacade.getRepaymentDetails(request.getParameter("globalAccountNum"), userContext.getLocaleId(), new AcceptedPaymentTypePersistence());
        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST, repayLoanDto.shouldWaiverInterest(), request);
        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST_SELECTED, repayLoanDto.shouldWaiverInterest(), request);
        SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, repayLoanDto.getEarlyRepaymentMoney(), request);
        SessionUtils.setAttribute(LoanConstants.WAIVED_REPAYMENT_AMOUNT, repayLoanDto.getWaivedRepaymentMoney(), request);
        SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE, repayLoanDto.getPaymentTypeEntities(), request);
        return mapping.findForward(Constants.LOAD_SUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward makeRepayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("Performing loan repayment");

        SessionUtils.removeAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, request);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());

        RepayLoanActionForm repayLoanActionForm = (RepayLoanActionForm) form;
        Date receiptDate = null;
        if (StringUtils.isNotEmpty(repayLoanActionForm.getReceiptDate())) {
            receiptDate = new Date(DateUtils.getLocaleDate(userContext.getPreferredLocale(),
                    repayLoanActionForm.getReceiptDate()).getTime());
        }

        String globalAccountNum = request.getParameter("globalAccountNum");
        loanServiceFacade
                .makeEarlyRepayment(globalAccountNum, repayLoanActionForm.getAmount(), repayLoanActionForm.getReceiptNumber(),
                        receiptDate, repayLoanActionForm.getPaymentTypeId(), userContext.getId(), repayLoanActionForm.isWaiverInterest());

        return mapping.findForward(Constants.UPDATE_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST_SELECTED, ((RepayLoanActionForm) form).isWaiverInterest(), request);
        return mapping.findForward(Constants.PREVIEW_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.setAttribute(LoanConstants.WAIVER_INTEREST_SELECTED, ((RepayLoanActionForm) form).isWaiverInterest(), request);
        return mapping.findForward(Constants.PREVIOUS_SUCCESS);
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        logger.debug("In RepayLoanAction::validate(), method: " + method);
        String forward = null;
        if (method != null && method.equals("preview")) {
            forward = ActionForwards.preview_failure.toString();
        }
        return mapping.findForward(forward);
    }

    private void clearActionForm(ActionForm form) {
        RepayLoanActionForm actionForm = (RepayLoanActionForm) form;
        actionForm.setReceiptNumber(null);
        actionForm.setReceiptDate(null);
        actionForm.setPaymentTypeId(null);
        actionForm.setWaiverInterest(true);
    }
}
