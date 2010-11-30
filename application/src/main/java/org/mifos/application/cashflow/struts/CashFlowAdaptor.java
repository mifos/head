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
package org.mifos.application.cashflow.struts;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.service.CashFlowBoundary;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class CashFlowAdaptor {

    private CashFlowServiceLocator cashFlowServiceLocator;
    private String cashFlowUrl;

    public CashFlowAdaptor(String cashFlowUrl, CashFlowServiceLocator cashFlowServiceLocator) {
        this.cashFlowUrl = cashFlowUrl;
        this.cashFlowServiceLocator = cashFlowServiceLocator;
    }

    public ActionForward bindCashFlow(CashFlowCaptor cashFlowCaptor
            , String joinUrlAfterCashFlow, HttpSession session, ActionMapping mapping) {

        cashFlowCaptor.setCashFlowForm((CashFlowForm) session.getAttribute(CashFlowConstants.CASH_FLOW_FORM));

        return mapping.findForward(joinUrlAfterCashFlow);
    }

    public ActionForward renderCashFlow(DateTime firstInstallmentDueDate, DateTime lastInstallmentDueDate,
                                        String joinUrl, String cancelUrl, ActionMapping mapping,
                                        HttpServletRequest request, LoanOfferingBO loanOffering) {
        CashFlowService cashFlowService = cashFlowServiceLocator.getService(request);
        if (cashFlowService == null) {
            return null;
        }
        CashFlowBoundary cashFlowBoundary = cashFlowService.getCashFlowBoundary(firstInstallmentDueDate, lastInstallmentDueDate);
        prepareCashFlowContext(joinUrl, cancelUrl, cashFlowBoundary, request.getSession(), loanOffering);
        return mapping.findForward(cashFlowUrl);
    }

    public Integer save(CashFlowCaptor cashFlowCaptor, HttpServletRequest request) {
        Integer cashFlowId = null;
        if (cashFlowCaptor.getCashFlowForm() != null) {
            CashFlowService cashFlowService = cashFlowServiceLocator.getService(request);
            if (cashFlowService == null) {
                return null;
            }
            cashFlowId = cashFlowService.save(mapToCashFlowDetail(cashFlowCaptor));
        }
        cleanCashFlowContext(request.getSession());
        cashFlowCaptor.setCashFlowForm(null);
        return cashFlowId;
    }

    private void prepareCashFlowContext(String joinUrl, String cancelUrl, CashFlowBoundary cashFlowBoundary,
                                        HttpSession session, LoanOfferingBO loanOffering) {
        session.setAttribute(CashFlowConstants.START_MONTH, cashFlowBoundary.getStartMonth());
        session.setAttribute(CashFlowConstants.START_YEAR, cashFlowBoundary.getStartYear());
        session.setAttribute(CashFlowConstants.NO_OF_MONTHS, cashFlowBoundary.getNumberOfMonths());
        session.setAttribute(CashFlowConstants.JOIN_URL, joinUrl);
        session.setAttribute(CashFlowConstants.CANCEL_URL, cancelUrl);
        session.setAttribute(CashFlowConstants.CAPTURE_CAPITAL_LIABILITY_INFO, loanOffering.shouldCaptureCapitalAndLiabilityInformation());
        session.setAttribute(CashFlowConstants.INDEBTEDNESS_RATIO, loanOffering.getIndebtednessRatio());
    }

    private CashFlowDetail mapToCashFlowDetail(CashFlowCaptor cashFlowCaptor) {
        List<MonthlyCashFlowDetail> monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>();
        CashFlowForm cashFlowForm = cashFlowCaptor.getCashFlowForm();
        for (MonthlyCashFlowForm monthlyCashFlowForm : cashFlowForm.getMonthlyCashFlows()) {
            MonthlyCashFlowDetail monthlyCashFlowDetail = new MonthlyCashFlowDetail(monthlyCashFlowForm.getDateTime(),
                    monthlyCashFlowForm.getRevenue(),
                    monthlyCashFlowForm.getExpense(),
                    monthlyCashFlowForm.getNotes());
            monthlyCashFlowDetails.add(monthlyCashFlowDetail);
        }
        CashFlowDetail cashFlowDetail = new CashFlowDetail(monthlyCashFlowDetails);
        cashFlowDetail.setTotalCapital(cashFlowForm.getTotalCapital());
        cashFlowDetail.setTotalLiability(cashFlowForm.getTotalLiability());
        return cashFlowDetail;
    }

    public void cleanCashFlowContext(HttpSession session) {
        session.removeAttribute(CashFlowConstants.START_MONTH);
        session.removeAttribute(CashFlowConstants.START_YEAR);
        session.removeAttribute(CashFlowConstants.NO_OF_MONTHS);
        session.removeAttribute(CashFlowConstants.JOIN_URL);
        session.removeAttribute(CashFlowConstants.CANCEL_URL);
        session.removeAttribute(CashFlowConstants.CASH_FLOW_FORM);
    }
}
