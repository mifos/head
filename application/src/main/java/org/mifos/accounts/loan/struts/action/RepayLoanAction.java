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

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.actionforms.RepayLoanActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class RepayLoanAction extends BaseAction {

    private LoanBusinessService loanBusinessService;

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    public RepayLoanAction() throws ServiceException {
        loanBusinessService = new LoanBusinessService();
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return loanBusinessService;
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
        clearActionForm(form);
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Loading repay loan page");
        String globalAccountNum = request.getParameter("globalAccountNum");
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        LoanBO loanBO = ((LoanBusinessService) getService()).findBySystemId(globalAccountNum);
        SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, loanBO.getTotalEarlyRepayAmount(), request);
        AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
        SessionUtils
                .setCollectionAttribute(MasterConstants.PAYMENT_TYPE, persistence
                        .getAcceptedPaymentTypesForATransaction(uc.getLocaleId(), TrxnTypes.loan_repayment.getValue()),
                        request);

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
        return mapping.findForward(Constants.LOAD_SUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward makeRepayment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SessionUtils.removeAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, request);
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Performing loan repayment");
        String globalAccountNum = request.getParameter("globalAccountNum");
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        LoanBO loanBO = ((LoanBusinessService) getService()).findBySystemId(globalAccountNum);
        checkVersionMismatch(loanBOInSession.getVersionNo(), loanBO.getVersionNo());
        RepayLoanActionForm repayLoanActionForm = (RepayLoanActionForm) form;
        Date receiptDate = null;
        if (repayLoanActionForm.getReceiptDate() != null && repayLoanActionForm.getReceiptDate() != "") {
            receiptDate = new Date(DateUtils.getLocaleDate(uc.getPreferredLocale(),
                    repayLoanActionForm.getReceiptDate()).getTime());
        }
        loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(), repayLoanActionForm.getReceiptNumber(),
                receiptDate, repayLoanActionForm.getPaymentTypeId(), uc.getId());
        return mapping.findForward(Constants.UPDATE_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.PREVIEW_SUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
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
    }
}
