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
package org.mifos.platform.cashflow.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class MonthlyCashFlow implements Serializable {
    private Integer id;
    private Double expenses;
    private java.sql.Date monthYear;
    private Double revenue;
    private String notes;
    private static final long serialVersionUID = -6682346815568286708L;

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public MonthlyCashFlow() {
    }

    public MonthlyCashFlow(DateTime monthYear, double revenue, double expenses, String notes) {
        this.monthYear = new java.sql.Date(monthYear.getMillis());
        this.revenue = revenue;
        this.expenses = expenses;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public Double getExpenses() {
        return expenses;
    }

    public Double getRevenue() {
        return revenue;
    }

    public Integer getId() {
        return id;
    }

    public DateTime getMonthYear() {
        return new DateTime(monthYear.getTime());
    }
}
