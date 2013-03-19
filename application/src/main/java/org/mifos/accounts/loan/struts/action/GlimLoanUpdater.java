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

package org.mifos.accounts.loan.struts.action;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;

public class GlimLoanUpdater {

    void updateIndividualLoan(final Date disbursementDate, final Double interestRate, final Short noOfInstallments, final LoanAccountDetailsDto loanAccountDetail, LoanBO individualLoan)
            throws AccountException {
        String loanAmount = loanAccountDetail.getLoanAmount();
        Money loanMoney = new Money(individualLoan.getCurrency(), !loanAmount.equals("-") ? loanAmount : "0");
        individualLoan.setInterestRate(interestRate);
        individualLoan.updateLoan(disbursementDate, noOfInstallments, loanMoney, !businessActivityIsEmpty(loanAccountDetail) ? Integer
                .valueOf(loanAccountDetail.getBusinessActivity()) : null);
        individualLoan.updateLoanSummary();
    }

    private boolean businessActivityIsEmpty(LoanAccountDetailsDto loanAccountDetail) {
        String businessActivity = loanAccountDetail.getBusinessActivity();
        return StringUtils.isBlank(businessActivity) || businessActivity.equals("-");
    }

    public void delete(LoanBO loan) throws AccountException {
        try {
            ApplicationContextProvider.getBean(LegacyLoanDao.class).delete(loan);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }
}
