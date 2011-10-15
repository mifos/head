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

package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanServiceFacadeWebTier implements LoanServiceFacade {

    private final LoanDao loanDao;
    private final LoanBusinessService loanBusinessService;

    @Autowired
    public LoanServiceFacadeWebTier(final LoanDao loanDao, LoanBusinessService loanBusinessService) {
        this.loanDao = loanDao;
        this.loanBusinessService = loanBusinessService;
    }

    @Override
    public OriginalScheduleInfoDto retrieveOriginalLoanSchedule(Integer accountId) {
        try {
            List<OriginalLoanScheduleEntity> loanScheduleEntities = loanBusinessService.retrieveOriginalLoanSchedule(accountId);
            ArrayList<RepaymentScheduleInstallment> repaymentScheduleInstallments = new ArrayList<RepaymentScheduleInstallment>();
            for (OriginalLoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
                  repaymentScheduleInstallments.add(loanScheduleEntity.toDto());
            }

            LoanBO loan = this.loanDao.findById(accountId);
            return new OriginalScheduleInfoDto(loan.getLoanAmount().toString(),loan.getDisbursementDate(),repaymentScheduleInstallments);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}