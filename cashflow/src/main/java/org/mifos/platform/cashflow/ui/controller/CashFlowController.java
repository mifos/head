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
package org.mifos.platform.cashflow.ui.controller;

import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class CashFlowController {
    @Autowired
    private CashFlowService cashFlowService;

    public CashFlowController() {
        this(null);
    }

    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public CashFlowForm prepareCashFlowForm(int startYear, int startMonth, int noOfMonths, BigDecimal loanAmount,
                                            Double indebtednessRatio, boolean captureCapitalLiabilityInfo) {
        CashFlowDetail cashFlowDetail = cashFlowService.cashFlowFor(startYear, startMonth, noOfMonths);
        return new CashFlowForm(cashFlowDetail, captureCapitalLiabilityInfo, loanAmount, indebtednessRatio);
    }

}
