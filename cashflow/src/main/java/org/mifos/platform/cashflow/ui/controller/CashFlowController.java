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

package org.mifos.platform.cashflow.ui.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.dto.domain.CashFlowDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.service.CashFlowBoundary;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CashFlowController {
    
    private final CashFlowService cashFlowService;

    @Autowired
    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }
    
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public LoanAccountCashFlow transformCashFlow(CashFlowForm cashFlow) {
        
        BigDecimal totalCapital = cashFlow.getTotalCapital();
        BigDecimal totalLiability = cashFlow.getTotalLiability();
        
        List<MonthlyCashFlowDto> cashflowDtos = new ArrayList<MonthlyCashFlowDto>();
        for (MonthlyCashFlowForm monthlyCashflowform : cashFlow.getMonthlyCashFlows()) {
            
            MonthlyCashFlowDto monthlyCashFlow = new MonthlyCashFlowDto(monthlyCashflowform.getDateTime(), 
                    monthlyCashflowform.getCumulativeCashFlow(), monthlyCashflowform.getNotes(), monthlyCashflowform.getRevenue(), monthlyCashflowform.getExpense());
            cashflowDtos.add(monthlyCashFlow);
        }
        
        return new LoanAccountCashFlow(cashflowDtos, totalCapital, totalLiability);
    }
    
    public CashFlowForm retrieveCashFlowForm(CashFlowDto cashFlowSettings) {

        CashFlowBoundary cashFlowBoundary = cashFlowService.getCashFlowBoundary(cashFlowSettings.getFirstInstallmentDueDate(), cashFlowSettings.getLastInstallmentDueDate());
        
        CashFlowDetail cashFlowDetail = cashFlowService.cashFlowFor(cashFlowBoundary.getStartYear(), cashFlowBoundary.getStartMonth(), cashFlowBoundary.getNumberOfMonths());
        return new CashFlowForm(cashFlowDetail, cashFlowSettings.isCaptureCapitalLiabilityInfo(), cashFlowSettings.getLoanAmount(), cashFlowSettings.getIndebtednessRatio(), Locale.getDefault());
    }
    
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<MonthlyCashFlowDto> retrieveMonthlyCashflowDetails(CashFlowForm cashFlowForm, Date disbursementDate, Double loanAmount) {
        
        List<MonthlyCashFlowDto> cashflowDtos = new ArrayList<MonthlyCashFlowDto>();
        
        for (MonthlyCashFlowForm monthlyCashFlowForm : cashFlowForm.getMonthlyCashFlows()) {
            if(isSameMonthYear(new LocalDate(monthlyCashFlowForm.getDate()), new LocalDate(disbursementDate))) {
                BigDecimal revenue = monthlyCashFlowForm.getRevenue();
                monthlyCashFlowForm.setRevenue((revenue == null)? BigDecimal.valueOf(loanAmount) : BigDecimal.valueOf(loanAmount).add(revenue));
                break;
            }         
        }
        
        for (MonthlyCashFlowForm monthlyCashflowform : cashFlowForm.getMonthlyCashFlows()) {
            
            MonthlyCashFlowDto monthlyCashFlow = new MonthlyCashFlowDto(monthlyCashflowform.getDateTime(), 
                    monthlyCashflowform.getCumulativeCashFlow(), monthlyCashflowform.getNotes(), monthlyCashflowform.getRevenue(), monthlyCashflowform.getExpense());
            cashflowDtos.add(monthlyCashFlow);
        }
        
        return cashflowDtos;
    }

    private boolean isSameMonthYear(LocalDate date, LocalDate comparedWith) {
        return date.getYear() == comparedWith.getYear() && (date.getMonthOfYear() == comparedWith.getMonthOfYear());
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public CashFlowForm prepareCashFlowForm(int startYear, int startMonth, int noOfMonths, BigDecimal loanAmount,
                                            Double indebtednessRatio, boolean captureCapitalLiabilityInfo, Locale locale) {
        CashFlowDetail cashFlowDetail = cashFlowService.cashFlowFor(startYear, startMonth, noOfMonths);
        return new CashFlowForm(cashFlowDetail, captureCapitalLiabilityInfo, loanAmount, indebtednessRatio, locale);
    }
}