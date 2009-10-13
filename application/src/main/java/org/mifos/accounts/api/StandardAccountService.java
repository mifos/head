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

package org.mifos.accounts.api;

import java.util.List;

import org.mifos.api.accounts.AccountPaymentParametersDTO;
import org.mifos.api.accounts.AccountReferenceDTO;
import org.mifos.api.accounts.AccountService;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;

/**
 * A service class implementation to expose basic functions on loans.
 * As an external API, this class should not expose business objects, only DTOs. 
 */
public class StandardAccountService implements AccountService {
    private AccountPersistence accountPersistence;
    private LoanPersistence loanPersistence;
    
    public LoanPersistence getLoanPersistence() {
        return this.loanPersistence;
    }

    public void setLoanPersistence(LoanPersistence loanPersistence) {
        this.loanPersistence = loanPersistence;
    }

    public AccountPersistence getAccountPersistence() {
        return this.accountPersistence;
    }

    public void setAccountPersistence(AccountPersistence accountPersistence) {
        this.accountPersistence = accountPersistence;
    }

    public void makePayment(AccountPaymentParametersDTO accountPaymentParametersDTO) throws PersistenceException, AccountException {
        StaticHibernateUtil.startTransaction();
        makePaymentNoCommit(accountPaymentParametersDTO);            
        StaticHibernateUtil.commitTransaction();
    }
    
    public void makePayments(List<AccountPaymentParametersDTO> accountPaymentParametersDTOs) throws PersistenceException, AccountException {
        StaticHibernateUtil.startTransaction();
        for (AccountPaymentParametersDTO accountPaymentParametersDTO: accountPaymentParametersDTOs) {
            makePaymentNoCommit(accountPaymentParametersDTO);            
        }
        StaticHibernateUtil.commitTransaction();
    }
    
    public void makePaymentNoCommit(AccountPaymentParametersDTO accountPaymentParametersDTO) throws PersistenceException, AccountException {
        AccountBO account = getAccountPersistence().getAccount(accountPaymentParametersDTO.account.getAccountId());
        
        if (!account.isTrxnDateValid(accountPaymentParametersDTO.paymentDate.toDateMidnight().toDate()))
            throw new AccountException("errors.invalidTxndate");

        Money amount = new Money(accountPaymentParametersDTO.paymentAmount);

        PaymentData paymentData = account.createPaymentData(accountPaymentParametersDTO.userMakingPayment.getUserId(), amount, accountPaymentParametersDTO.paymentDate.toDateMidnight().toDate(), null,
                null, accountPaymentParametersDTO.paymentType.getValue());
        paymentData.setComment(accountPaymentParametersDTO.comment);
                
        account.applyPayment(paymentData);
        
        getAccountPersistence().createOrUpdate(account);

    }

    public AccountReferenceDTO lookupLoanAccountReferenceFromExternalId(String externalId) throws PersistenceException {
        LoanBO loan = getLoanPersistence().findByExternalId(externalId);
        return new AccountReferenceDTO(loan.getAccountId());
    }

}
