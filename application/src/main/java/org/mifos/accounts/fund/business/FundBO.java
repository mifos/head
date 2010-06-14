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
import org.mifos.accounts.fund.exception.FundException;
import org.mifos.accounts.fund.persistence.FundDaoHibernate;
import org.mifos.accounts.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;

public class FundBO extends AbstractBusinessObject {

    private final Short fundId;
    private final FundCodeEntity fundCode;
    private String fundName;

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER);

    public FundBO(FundCodeEntity fundCode, String fundName) throws FundException {
        logger.debug("building fund");
        this.fundId = null;
        validate(fundCode, fundName);
        validateDuplicateFundName(fundName);
        this.fundCode = fundCode;
        this.fundName = fundName;
        logger.debug("Fund build :" + getFundName());
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

    public void validate(FundCodeEntity fundCode, String fundName) throws FundException {
        logger.debug("Validating the fields in Fund");
        validateFundName(fundName);
        validateFundCode(fundCode);
        logger.debug("Validating the fields in Fund done");
    }

    /**
     * FIXME - keithw - loan refactoring - pull up to Dao or service level after refactoring fund creation flow.
     */
    @Deprecated
    public void validateDuplicateFundName(String fundName) throws FundException {
        if (new FundDaoHibernate().countOfFundByName(fundName.trim()) > 0) {
            throw new FundException(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION);
        }
    }

    public void validateFundName(String fundName) throws FundException {
        logger.debug("Checking for empty Fund name");
        if (StringUtils.isBlank(fundName)) {
            throw new FundException(FundConstants.INVALID_FUND_NAME);
        }
    }

    private void validateFundCode(FundCodeEntity fundCode) throws FundException {
        logger.debug("Checking for empty Fund Code");
        if (fundCode == null) {
            throw new FundException(FundConstants.INVALID_FUND_CODE);
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
