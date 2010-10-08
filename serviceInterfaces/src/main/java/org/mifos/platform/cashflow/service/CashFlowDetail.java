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
package org.mifos.platform.cashflow.service;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CashFlowDetail implements Serializable {
    private static final long serialVersionUID = -6731316163493318834L;
    List<MonthlyCashFlowDetail> monthlyCashFlowDetails;

    public CashFlowDetail() {
        monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>();
    }

    public CashFlowDetail(List<MonthlyCashFlowDetail> monthlyCashFlows) {
        monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>(monthlyCashFlows);
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : monthlyCashFlowDetails) {
            monthlyCashFlowDetail.setCashFlowDetail(this);
        }
    }

    public List<MonthlyCashFlowDetail> getMonthlyCashFlowDetails() {
        return monthlyCashFlowDetails;
    }

    public Double getCumulativeCashFlowForMonth(DateTime dateTime) {
        Double cumulative = 0.0d;
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : getMonthlyCashFlowDetails()) {
            if (monthlyCashFlowDetail.getDateTime().compareTo(dateTime) <= 0) {
                cumulative += (monthlyCashFlowDetail.getRevenue() - monthlyCashFlowDetail.getExpense());
            }
        }
        return cumulative;
    }


}