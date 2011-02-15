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
package org.mifos.platform.cashflow.service;

import java.io.Serializable;

public class CashFlowBoundary implements Serializable {
    private static final long serialVersionUID = 5780910966337994597L;
    private final int startYear;
    private final int startMonth;
    private final int numberOfMonths;

    public CashFlowBoundary(int startMonth, int startYear, int numberOfMonths) {
        this.startMonth = startMonth;
        this.startYear = startYear;
        this.numberOfMonths = numberOfMonths;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getNumberOfMonths() {
        return numberOfMonths;
    }
}
