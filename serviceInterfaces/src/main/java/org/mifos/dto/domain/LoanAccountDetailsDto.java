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

package org.mifos.dto.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanAccountDetailsDto implements Serializable {

    private String accountId;
    private String clientId;
    private String clientName;
    private String govermentId = "";
    private String loanPurpose;
    private String loanAmount;
    private String businessActivity;
    private String businessActivityName;
    private String accountState;
    
    private String loanAccountId;
    private String loanGlobalAccountNum;
    private String parentLoanGlobalAccountNum;
    private Integer parentLoanAccountId;
    /**
     * @deprecated use builder to create test doubles
     */
    @Deprecated
    public static LoanAccountDetailsDto createInstanceForTest(String clientId, String businessActivity,
            String loanAmount, String accountId) {
        return new LoanAccountDetailsDto(clientId, businessActivity, loanAmount, accountId);
    }

    public LoanAccountDetailsDto() {
        // empty constructor
    }

    /**
     * @deprecated use builder to create test doubles
     */
    @Deprecated
    public LoanAccountDetailsDto(String clientId, String businessActivity, String loanAmount) {
        this.clientId = clientId;
        this.businessActivity = businessActivity;
        this.loanAmount = loanAmount;
    }

    /**
     * @deprecated use builder to create test doubles
     */
    @Deprecated
    public LoanAccountDetailsDto(String clientId, String businessActivity, String loanAmount, String accountId) {
        this.clientId = clientId;
        this.businessActivity = businessActivity;
        this.loanAmount = loanAmount;
        this.accountId = accountId;
    }

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
    

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getLoanAccountId() {
        return loanAccountId;
    }

    public void setLoanAccountId(String loanAccountId) {
        this.loanAccountId = loanAccountId;
    }
    
	public String getLoanGlobalAccountNum() {
		return loanGlobalAccountNum;
	}

	public void setLoanGlobalAccountNum(String loanGlobalAccountNum) {
		this.loanGlobalAccountNum = loanGlobalAccountNum;
	}

	public String getParentLoanGlobalAccountNum() {
		return parentLoanGlobalAccountNum;
	}

	public void setParentLoanGlobalAccountNum(String parentLoanGlobalAccountNum) {
		this.parentLoanGlobalAccountNum = parentLoanGlobalAccountNum;
	}

	public Integer getParentLoanAccountId() {
        return parentLoanAccountId;
    }

    public void setParentLoanAccountId(Integer parentLoanAccountId) {
        this.parentLoanAccountId = parentLoanAccountId;
    }

    public String getAccountState() {
        return accountState;
    }

    public void setAccountState(String accountState) {
        this.accountState = accountState;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(loanAmount) && StringUtils.isBlank(businessActivity);
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
}