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

package org.mifos.reports.business.dto;

import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntity;
import org.mifos.framework.util.helpers.MoneyUtils;

public class SavingsProductDetails {

    private final Double balance;
    private final Double due;
    private final String offeringShortName;

    public SavingsProductDetails(CollSheetSavingsDetailsEntity savingsDetails, String offeringShortName) {
        this.offeringShortName = offeringShortName;
        if (savingsDetails != null) {
            balance = MoneyUtils.getMoneyDoubleValue(savingsDetails.getAccountBalance());
            due = MoneyUtils.getMoneyDoubleValue(savingsDetails.getRecommendedAmntDue());
        } else {
            balance = due = 0d;
        }
    }

    public Double getBalance() {
        return balance;
    }

    public Double getDue() {
        return due;
    }

    public String getOfferingShortName() {
        return offeringShortName;
    }
}
