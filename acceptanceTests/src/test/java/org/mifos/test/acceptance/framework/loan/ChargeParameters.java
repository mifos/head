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

package org.mifos.test.acceptance.framework.loan;

import java.util.List;

public class ChargeParameters {
    public static final String MISC_FEES = "Misc Fees";
    public static final String MISC_PENALTY = "Misc Penalty";

    //private static final boolean IS_RATE_TYPE = true;
    private static final boolean IS_NOT_RATE_TYPE = false;
    private static final boolean IS_NOT_PENALTY_TPE = false;
    private static final String CHARGE_TYPE_SEPARATOR = ":";

    private String constructChargeType(boolean isPenaltyType, int feeId, boolean isRateType) {
        String isRateTypeString = isRateType ? "1" : "0";
        String isPenaltyTypeString = isPenaltyType ? "1" : "0";
        return isPenaltyTypeString + CHARGE_TYPE_SEPARATOR + feeId + CHARGE_TYPE_SEPARATOR + isRateTypeString;
    }


    private String type;
    private String amount;
    private List<String> groupLoanIndividualAmounts;

    // this method is never(?) used. Get rid of it?
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAmount() {
        return this.amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public List<String> getGroupLoanIndividualAmounts() {
        return groupLoanIndividualAmounts;
    }
    public void setGroupLoanIndividualAmounts(List<String> groupLoanIndividualAmounts) {
        this.groupLoanIndividualAmounts = groupLoanIndividualAmounts;
    }

    /**
     * Maps the type string to a value that's used to choose the correct choice in the select box.
     * We do this to ensure that the parameters are independent of locale.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public String getTypeValue() {
        if (MISC_FEES.equals(type)) { return constructChargeType(IS_NOT_PENALTY_TPE, -1, IS_NOT_RATE_TYPE); }
        if (MISC_PENALTY.equals(type)) { return constructChargeType(IS_NOT_PENALTY_TPE, -2, IS_NOT_RATE_TYPE); }

        return constructChargeType(IS_NOT_PENALTY_TPE, 0, IS_NOT_RATE_TYPE);
    }
}
