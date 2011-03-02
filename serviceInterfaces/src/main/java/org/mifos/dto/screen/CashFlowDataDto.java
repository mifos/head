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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class CashFlowDataDto implements Serializable {
    
    private String month = "";
    private String year = "";
    private String cumulativeCashflow = "";
    private String diffCumulativeCashflowAndInstallment = "";
    private String diffCumulativeCashflowAndInstallmentPercent = "";
    private String notes = "";
    private Date monthYear;

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCumulativeCashFlow() {
        return this.cumulativeCashflow;
    }

    public void setCumulativeCashFlow(String cumulativeCashflow) {
        this.cumulativeCashflow = cumulativeCashflow;
    }

    public String getDiffCumulativeCashflowAndInstallment() {
        return this.diffCumulativeCashflowAndInstallment;
    }

    public void setDiffCumulativeCashflowAndInstallment(String diffCumulativeCashflowAndInstallment) {
        this.diffCumulativeCashflowAndInstallment = diffCumulativeCashflowAndInstallment;
    }

    public String getDiffCumulativeCashflowAndInstallmentPercent() {
        return this.diffCumulativeCashflowAndInstallmentPercent;
    }

    public void setDiffCumulativeCashflowAndInstallmentPercent(String diffCumulativeCashflowAndInstallmentPercent) {
        this.diffCumulativeCashflowAndInstallmentPercent = diffCumulativeCashflowAndInstallmentPercent;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setMonthYear(Date monthYear) {
        this.monthYear = monthYear;
    }

    public Date getMonthYear() {
        return monthYear;
    }

    public String getMonthYearAsString() {
        return getMonth() + " " + getYear();
    }
}