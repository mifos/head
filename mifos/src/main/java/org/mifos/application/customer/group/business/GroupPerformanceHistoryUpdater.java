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

package org.mifos.application.customer.group.business;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;

public class GroupPerformanceHistoryUpdater {

    static class UpdateClientPerfHistoryForGroupLoanOnRepayment extends UpdateClientPerfHistory {

        public UpdateClientPerfHistoryForGroupLoanOnRepayment(LoanBO loan) {
            super(loan);
        }

        public void execute(Object arg0) {
            CustomerBO client = (CustomerBO) arg0;
            LoanBO matchingIndividualAccount = (LoanBO) CollectionUtils.find(client.getAccounts(),
                    new ClientAccountWithParentAccountMatcher(loan));
            getPerformanceHistory(arg0).updateOnRepayment(matchingIndividualAccount.getLoanAmount());
        }
    }

    static class UpdateClientPerfHistoryForGroupLoanOnReversal extends UpdateClientPerfHistory {

        public UpdateClientPerfHistoryForGroupLoanOnReversal(LoanBO loan) {
            super(loan);
        }

        public void execute(Object arg0) {
            getPerformanceHistory(arg0).updateCommonHistoryOnReversal(loan.getLoanOffering());
        }
    }

    static class UpdateClientPerfHistoryForGroupLoanOnDisbursement extends UpdateClientPerfHistory {

        UpdateClientPerfHistoryForGroupLoanOnDisbursement(LoanBO loan) {
            super(loan);
        }

        public void execute(Object arg0) {
            getPerformanceHistory(arg0).updateOnDisbursement(loan.getLoanOffering());
        }
    }

    static class UpdateClientPerfHistoryForGroupLoanOnWriteOff extends UpdateClientPerfHistory {

        UpdateClientPerfHistoryForGroupLoanOnWriteOff(LoanBO loan) {
            super(loan);
        }

        public void execute(Object arg0) {
            getPerformanceHistory(arg0).updateOnWriteOff(loan.getLoanOffering());
        }
    }

    abstract static class UpdateClientPerfHistory implements Closure {
        protected LoanBO loan;

        UpdateClientPerfHistory(LoanBO loan) {
            this.loan = loan;
        }

        protected ClientPerformanceHistoryEntity getPerformanceHistory(Object arg0) {
            CustomerBO clientBO = (CustomerBO) arg0;
            return ((ClientPerformanceHistoryEntity) clientBO.getPerformanceHistory());
        }
    }

    static class ClientAccountWithParentAccountMatcher implements Predicate {

        private final LoanBO loan;

        ClientAccountWithParentAccountMatcher(LoanBO loan) {
            this.loan = loan;
        }

        public boolean evaluate(Object arg0) {
            AccountBO account = (AccountBO) arg0;
            return account.isOfType(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT)
                    && loan.getAccountId().equals(((LoanBO) account).getParentAccount().getAccountId());
        }
    }
}
