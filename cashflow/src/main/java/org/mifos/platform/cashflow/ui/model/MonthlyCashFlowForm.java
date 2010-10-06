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
package org.mifos.platform.cashflow.ui.model;

import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;

import java.io.Serializable;

public class MonthlyCashFlowForm implements Serializable {
    private static final long serialVersionUID = 6876855921528555322L;
    private MonthlyCashFlowDetail monthlyCashFlowDetail;

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public MonthlyCashFlowForm() {
    }

    public MonthlyCashFlowForm(MonthlyCashFlowDetail monthlyCashFlowDetail) {
        this.monthlyCashFlowDetail = monthlyCashFlowDetail;
    }

    public String getMonth() {
        return monthlyCashFlowDetail.getDateTime().monthOfYear().getAsText();
    }

    public int getYear() {
        return monthlyCashFlowDetail.getDateTime().getYear();
    }

    @javax.validation.constraints.NotNull()
    public Double getRevenue() {
        return monthlyCashFlowDetail.getRevenue();
    }

    @javax.validation.constraints.NotNull
    public Double getExpense() {
        return monthlyCashFlowDetail.getExpense();
    }

    @javax.validation.constraints.Size(min = 1, max = 300)
    public String getNotes() {
        return monthlyCashFlowDetail.getNotes();
    }

    public void setRevenue(Double revenue) {
        monthlyCashFlowDetail.setRevenue(revenue);
    }

    public void setExpense(Double expense) {
        monthlyCashFlowDetail.setExpense(expense);
    }

    public void setNotes(String notes) {
        monthlyCashFlowDetail.setNotes(notes);
    }

    @Override
    public String toString() {
        return "MonthlyCashFlowForm{" +
                "Revenue=" + getRevenue() +
                "Expense=" + getExpense() +
                "Notes=" + getNotes() +
                "Month=" + getMonth() +
                "Year=" + getYear() +
                '}';
    }
}
