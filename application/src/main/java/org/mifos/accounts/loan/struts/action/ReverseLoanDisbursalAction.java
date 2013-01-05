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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.struts.actionforms.ReverseLoanDisbursalActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReverseLoanDisbursalAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ReverseLoanDisbursalAction.class);

    @TransactionDemarcate(saveToken = true)
    public ActionForward search(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside search method");
        request.getSession().setAttribute(LoanConstants.REVERSE_LOAN_DIBURSAL_ACTION_FORM, null);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, null);
        logger.debug("Outside search method");
        return mapping.findForward(ActionForwards.search_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside load method");
        ReverseLoanDisbursalActionForm actionForm = (ReverseLoanDisbursalActionForm) form;

        String searchString = actionForm.getSearchString();

        String globalAccountNum = searchString;
        if (StringUtils.isNotEmpty(globalAccountNum)) {
            globalAccountNum = globalAccountNum.trim();
        }

        List<LoanActivityDto> payments = this.loanAccountServiceFacade.retrieveLoanPaymentsForReversal(globalAccountNum);

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        SessionUtils.setCollectionAttribute(LoanConstants.PAYMENTS_LIST, payments, request);
        SessionUtils.setAttribute(LoanConstants.PAYMENTS_SIZE, payments.size(), request);

        logger.debug("Outside load method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("preview method called");
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside validate method");
        ActionForwards actionForward = ActionForwards.search_success;
        String method = (String) request.getAttribute("methodCalled");
        if (method != null) {
            if (method.equals(Methods.search.toString()) || method.equals(Methods.load.toString())) {
                actionForward = ActionForwards.search_success;
            } else if (method.equals(Methods.preview.toString())) {
                actionForward = ActionForwards.load_success;
            } else if (method.equals(Methods.update.toString())) {
                actionForward = ActionForwards.preview_success;
            }
        }
        logger.debug("outside validate method");
        return mapping.findForward(actionForward.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside update method");
        ReverseLoanDisbursalActionForm actionForm = (ReverseLoanDisbursalActionForm) form;

        LoanBO loan = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        this.loanAccountServiceFacade.reverseLoanDisbursal(loan.getGlobalAccountNum(), actionForm.getNote());
        
        if (loan.isGroupLoanAccountParent()) {
            for (LoanBO member : loan.getMemberAccounts()) {
                this.loanAccountServiceFacade.reverseLoanDisbursal(member.getGlobalAccountNum(), actionForm.getNote());
            }
        }

        logger.debug("Outside update method");
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("cancel method called");
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }
}