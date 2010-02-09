/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.loan.persistance;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.persistence.DataAccessObject;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

/**
 * The Loan Data Access Object (DAO) for create, read, update, delete (CRUD)
 * operations on loans.
 * 
 * Further refactoring: loan creation seems like a good candidate to make use of
 * the Factory pattern.
 */
public class LoanDao implements DataAccessObject {

    public LoanBO createLoan(UserContext userContext, LoanOfferingBO loanOffering, CustomerBO customer,
            AccountState accountState, Money loanAmount, Short noOfinstallments, Date disbursementDate,
            boolean interestDeductedAtDisbursement, Double interestRate, Short gracePeriodDuration, FundBO fund,
            List<FeeView> feeViews, List<CustomFieldView> customFields, Double maxLoanAmount, Double minLoanAmount,
            Short maxNoOfInstall, Short minNoOfInstall, boolean isRepaymentIndepOfMeetingEnabled) throws AccountException {

        if (isAnyLoanParamsNull(loanOffering, customer, loanAmount, noOfinstallments, disbursementDate, interestRate))
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION);

        if (!customer.isActive()) {

            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
        }

        if (!loanOffering.isActive())
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);

        if (isDisbursementDateLessThanCurrentDate(disbursementDate))
            throw new AccountException(LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);

        if (!isDisbursementDateValid(customer, disbursementDate))
            throw new AccountException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);

        if (interestDeductedAtDisbursement == true && noOfinstallments.shortValue() <= 1)
            throw new AccountException(LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);

        return new LoanBO(userContext, loanOffering, customer, accountState, loanAmount, noOfinstallments,
                disbursementDate, interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeViews,
                customFields, false, maxLoanAmount, minLoanAmount, maxNoOfInstall, minNoOfInstall, 
                isRepaymentIndepOfMeetingEnabled, null);
    }

    private boolean isAnyLoanParamsNull(Object... args) {
        return Arrays.asList(args).contains(null);
    }

    private boolean isDisbursementDateLessThanCurrentDate(Date disbursementDate) {
        if (DateUtils.dateFallsBeforeDate(disbursementDate, DateUtils.getCurrentDateWithoutTimeStamp()))
            return true;
        return false;
    }

    private Boolean isDisbursementDateValid(CustomerBO specifiedCustomer, Date disbursementDate)
            throws AccountException {
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("IsDisbursementDateValid invoked ");
        Boolean isValid = false;
        try {
            isValid = specifiedCustomer.getCustomerMeeting().getMeeting().isValidMeetingDate(disbursementDate,
                    DateUtils.getLastDayOfNextYear());
        } catch (MeetingException e) {
            throw new AccountException(e);
        }
        return isValid;
    }

}
