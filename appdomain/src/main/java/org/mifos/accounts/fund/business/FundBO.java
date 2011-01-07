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

package org.mifos.accounts.fund.business;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mifos.accounts.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.business.AbstractBusinessObject;

public class FundBO extends AbstractBusinessObject {

    private final Short fundId;
    private final FundCodeEntity fundCode;
    private String fundName;

    public FundBO(FundCodeEntity fundCode, String fundName) {
        this.fundId = null;
        validate(fundCode, fundName);
        this.fundCode = fundCode;
        this.fundName = fundName;
    }

    /**
     * Required by Hibernate.
     */
    protected FundBO() {
        this.fundId = null;
        this.fundCode = null;
    }

    public Short getFundId() {
        return fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public FundCodeEntity getFundCode() {
        return fundCode;
    }

    public void validate(FundCodeEntity fundCode, String fundName) {
        validateFundName(fundName);
        validateFundCode(fundCode);
    }

    public void validateFundName(String fundName) {
        if (StringUtils.isBlank(fundName)) {
            throw new org.mifos.service.BusinessRuleException(FundConstants.INVALID_FUND_NAME);
        }
    }

    private void validateFundCode(FundCodeEntity fundCode) {
        if (fundCode == null) {
            throw new org.mifos.service.BusinessRuleException(FundConstants.INVALID_FUND_CODE);
        }
    }

    public boolean isDifferent(String newFundName) {
        return !newFundName.equals(this.fundName);
    }

    @Override
    public boolean equals(Object obj) {
        FundBO rhs = (FundBO) obj;
        return new EqualsBuilder().append(this.fundId, rhs.fundId).append(this.fundName, rhs.fundName).isEquals();
    }

    @Override
    public int hashCode() {
        int initialNonZeroOddNumber = 7;
        int multiplierNonZeroOddNumber = 7;
        return new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber).append(this.fundId).append(this.fundName).hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder().append(this.fundId).append(" : ").append(this.fundName).toString();
    }
}
