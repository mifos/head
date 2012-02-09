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

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
                                                  justification = "should disable at filter level and also for pmd - not important for us")
public class AccountPenaltiesDto implements Serializable {
    private final Short penaltyFrequencyId;
    private final Short penaltyStatus;
    private final String penaltyName;
    private final String accountPenaltyAmount;
    private final String penaltyFrequencyName;
    private final Short penaltyId;

    public AccountPenaltiesDto(final Short penaltyFrequencyId, final Short penaltyStatus, final String penaltyName,
            final String accountPenaltyAmount, final String penaltyFrequencyName, final Short penaltyId) {
        this.penaltyFrequencyId = penaltyFrequencyId;
        this.penaltyStatus = penaltyStatus;
        this.penaltyName = penaltyName;
        this.accountPenaltyAmount = accountPenaltyAmount;
        this.penaltyFrequencyName = penaltyFrequencyName;
        this.penaltyId = penaltyId;
    }

    public Short getPenaltyFrequencyId() {
        return penaltyFrequencyId;
    }

    public Short getPenaltyStatus() {
        return penaltyStatus;
    }

    public String getPenaltyName() {
        return penaltyName;
    }

    public String getAccountPenaltyAmount() {
        return accountPenaltyAmount;
    }

    public String getPenaltyFrequencyName() {
        return penaltyFrequencyName;
    }

    public Short getPenaltyId() {
        return penaltyId;
    }
}
