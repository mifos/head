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
package org.mifos.platform.cashflow.ui.model;

import org.joda.time.DateTime;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

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

    public String getMonthInLocale() {
        return monthlyCashFlowDetail.getDateTime().monthOfYear().getAsText(Locale.ENGLISH);
    }

    public int getYear() {
        return monthlyCashFlowDetail.getDateTime().getYear();
    }

    public BigDecimal getRevenue() {
        return monthlyCashFlowDetail.getRevenue();
    }

    public BigDecimal getExpense() {
        return monthlyCashFlowDetail.getExpense();
    }

    public String getNotes() {
        return monthlyCashFlowDetail.getNotes();
    }

    public BigDecimal getCumulativeCashFlow() {
        return monthlyCashFlowDetail.getCumulativeCashFlow();
    }

    public DateTime getDateTime() {
        return monthlyCashFlowDetail.getDateTime();
    }

    public void setRevenue(BigDecimal revenue) {
        monthlyCashFlowDetail.setRevenue(revenue);
    }

    public void setExpense(BigDecimal expense) {
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

    public Date getDate() {
        return getDateTime().toDate();
    }

    boolean hasNoExpense() {
        return getExpense() == null;
    }

    boolean hasNoRevenue() {
        return getRevenue() == null;
    }

    boolean cumulativeCashFlowIsLessThanOrEqualToZero() {
        return getCumulativeCashFlow().compareTo(BigDecimal.ZERO) <= 0;
    }
}
