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

package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanServiceImpl implements LoanService {

    private final LoanDao loanDao;
    private final HibernateTransactionHelper transactionHelper;
    
    @Autowired
    public LoanServiceImpl(LoanDao loanDao, HibernateTransactionHelper transactionHelper) {
        this.loanDao = loanDao;
        this.transactionHelper = transactionHelper;
    }
    
    @Override
    public void create(Loan loan, String userOfficeGlobalOfficeNum) {
        
        // FIXME - keithw - should do all domain validation here
        // 
        try {
            LoanBO loanAccount = (LoanBO)loan;
            transactionHelper.startTransaction();
            this.loanDao.save(loanAccount);
            transactionHelper.flushSession();
            try {
                loanAccount.setGlobalAccountNum(loanAccount.generateId(userOfficeGlobalOfficeNum));
            } catch (AccountException e) {
                throw new BusinessRuleException(e.getMessage());
            }
            this.loanDao.save(loanAccount);
            transactionHelper.commitTransaction();
            
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }
}