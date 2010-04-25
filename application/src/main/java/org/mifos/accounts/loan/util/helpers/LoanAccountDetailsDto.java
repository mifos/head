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

package org.mifos.accounts.loan.util.helpers;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.LocalizationConverter;

public class LoanAccountDetailsDto implements DataTransferObject {

    public LoanAccountDetailsDto(Integer individualAccountId) {
        this.individualAccountId = individualAccountId;
    }

    public LoanAccountDetailsDto() {
        this(null);
    }

    LoanAccountDetailsDto(String clientId, String businessActivity, String loanAmount) {
        this(null);
        this.clientId = clientId;
        this.businessActivity = businessActivity;
        this.loanAmount = loanAmount;
    }

    LoanAccountDetailsDto(String clientId, String businessActivity, String loanAmount, String accountId) {
        this(null);
        this.clientId = clientId;
        this.businessActivity = businessActivity;
        this.loanAmount = loanAmount;
        this.accountId = accountId;
    }

    private final Integer individualAccountId;

    private String accountId;

    private String clientId;

    private String clientName;

    private String govermentId;

    private String loanPurpose;

    private String loanAmount;

    private String businessActivity;

    private String businessActivityName;

    public String getBusinessActivityName() {
        return businessActivityName;
    }

    public void setBusinessActivityName(String businessActivityName) {
        this.businessActivityName = businessActivityName;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(String businessActivity) {
        this.businessActivity = businessActivity;
    }

    public String getGovermentId() {
        return govermentId;
    }

    public void setGovermentId(String govermentId) {
        this.govermentId = govermentId;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getIndividualAccountId() {
        return individualAccountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(loanAmount) && StringUtils.isBlank(businessActivity);
    }

    public boolean isAmountZeroOrNull() {
        return loanAmount == null
                || (Double.compare(new LocalizationConverter().getDoubleValueForCurrentLocale(loanAmount),
                        NumberUtils.DOUBLE_ZERO) == 0);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((accountId == null) ? 0 : accountId.hashCode());
        result = PRIME * result + ((businessActivity == null) ? 0 : businessActivity.hashCode());
        result = PRIME * result + ((businessActivityName == null) ? 0 : businessActivityName.hashCode());
        result = PRIME * result + ((clientId == null) ? 0 : clientId.hashCode());
        result = PRIME * result + ((clientName == null) ? 0 : clientName.hashCode());
        result = PRIME * result + ((govermentId == null) ? 0 : govermentId.hashCode());
        result = PRIME * result + ((individualAccountId == null) ? 0 : individualAccountId.hashCode());
        result = PRIME * result + ((loanAmount == null) ? 0 : loanAmount.hashCode());
        result = PRIME * result + ((loanPurpose == null) ? 0 : loanPurpose.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LoanAccountDetailsDto other = (LoanAccountDetailsDto) obj;
        if (accountId == null) {
            if (other.accountId != null) {
                return false;
            }
        } else if (!accountId.equals(other.accountId)) {
            return false;
        }
        if (businessActivity == null) {
            if (other.businessActivity != null) {
                return false;
            }
        } else if (!businessActivity.equals(other.businessActivity)) {
            return false;
        }
        if (businessActivityName == null) {
            if (other.businessActivityName != null) {
                return false;
            }
        } else if (!businessActivityName.equals(other.businessActivityName)) {
            return false;
        }
        if (clientId == null) {
            if (other.clientId != null) {
                return false;
            }
        } else if (!clientId.equals(other.clientId)) {
            return false;
        }
        if (clientName == null) {
            if (other.clientName != null) {
                return false;
            }
        } else if (!clientName.equals(other.clientName)) {
            return false;
        }
        if (govermentId == null) {
            if (other.govermentId != null) {
                return false;
            }
        } else if (!govermentId.equals(other.govermentId)) {
            return false;
        }
        if (individualAccountId == null) {
            if (other.individualAccountId != null) {
                return false;
            }
        } else if (!individualAccountId.equals(other.individualAccountId)) {
            return false;
        }
        if (loanAmount == null) {
            if (other.loanAmount != null) {
                return false;
            }
        } else if (!loanAmount.equals(other.loanAmount)) {
            return false;
        }
        if (loanPurpose == null) {
            if (other.loanPurpose != null) {
                return false;
            }
        } else if (!loanPurpose.equals(other.loanPurpose)) {
            return false;
        }
        return true;
    }

    public static LoanAccountDetailsDto createInstanceForTest(String clientId, String businessActivity,
            String loanAmount, String accountId) {
        return new LoanAccountDetailsDto(clientId, businessActivity, loanAmount, accountId);
    }
}
