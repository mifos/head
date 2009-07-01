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

package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;

public class GlimLoanUpdater {

    public void createIndividualLoan(LoanAccountActionForm loanAccountActionForm, LoanBO loan,
            boolean isRepaymentIndepOfMeetingEnabled, LoanAccountDetailsViewHelper loanAccountDetail)
            throws AccountException, ServiceException, PropertyNotFoundException {
        LoanBO individualLoan = LoanBO.createIndividualLoan(loan.getUserContext(), loan.getLoanOffering(),
                new CustomerBusinessService().getCustomer(Integer.valueOf(loanAccountDetail.getClientId())),
                loanAccountActionForm.getState(), new Money(loanAccountDetail.getLoanAmount().toString()), loan
                        .getNoOfInstallments(), loan.getDisbursementDate(), false, isRepaymentIndepOfMeetingEnabled,
                loan.getInterestRate(), loan.getGracePeriodDuration(), loan.getFund(), new ArrayList<FeeView>(),
                new ArrayList<CustomFieldView>());

        individualLoan.setParentAccount(loan);

        if (null != loanAccountDetail.getBusinessActivity())
            individualLoan.setBusinessActivityId(Integer.valueOf(loanAccountDetail.getBusinessActivity()));

        individualLoan.save();
    }

    void updateIndividualLoan(final LoanAccountDetailsViewHelper loanAccountDetail, LoanBO individualLoan)
            throws AccountException {
        Double loanAmount = loanAccountDetail.getLoanAmount();
        Money loanMoney = new Money(!loanAmount.toString().equals("-") ? loanAmount.longValue() + "" : "0");
        individualLoan.updateLoan(loanMoney, !"-".equals(loanAccountDetail.getBusinessActivity()) ? Integer
                .valueOf(loanAccountDetail.getBusinessActivity()) : 0);
    }

    public void delete(LoanBO loan) throws AccountException {
        try {
            new LoanPersistence().delete(loan);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }
}
