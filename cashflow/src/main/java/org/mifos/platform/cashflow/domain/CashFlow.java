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
package org.mifos.platform.cashflow.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashFlow implements Serializable {
    private Integer id;
    private final List<MonthlyCashFlow> monthlyCashFlows;
    private static final long serialVersionUID = 4972764856147916800L;
    private BigDecimal totalLiability;
    private BigDecimal totalCapital;

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public CashFlow() {
        this.monthlyCashFlows = new ArrayList<MonthlyCashFlow>();
    }

    public CashFlow(List<MonthlyCashFlow> monthlyCashFlows) {
        this.monthlyCashFlows = monthlyCashFlows;
    }

    public List<MonthlyCashFlow> getMonthlyCashFlows() {
        return monthlyCashFlows;
    }

    public Integer getId() {
        return id;
    }

    public void add(MonthlyCashFlow monthlyCashFlow) {
        monthlyCashFlows.add(monthlyCashFlow);
    }

    public BigDecimal getTotalLiability() {
        return totalLiability;
    }

    public BigDecimal getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(BigDecimal totalCapital) {
        this.totalCapital = totalCapital;
    }

    public void setTotalLiability(BigDecimal totalLiability) {
        this.totalLiability = totalLiability;
    }
}
