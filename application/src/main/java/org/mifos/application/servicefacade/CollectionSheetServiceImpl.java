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
package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 *
 */
public class CollectionSheetServiceImpl implements CollectionSheetService {

    private final ClientAttendanceDao clientAttendanceDao;
    private final LoanPersistence loanPersistence;
    private final AccountPersistence accountPersistence;
    private final SavingsPersistence savingsPersistence;

    public CollectionSheetServiceImpl(final ClientAttendanceDao clientAttendanceDao,
            final LoanPersistence loanPersistence, final AccountPersistence accountPersistence,
            final SavingsPersistence savingsPersistence) {
        this.clientAttendanceDao = clientAttendanceDao;
        this.loanPersistence = loanPersistence;
        this.accountPersistence = accountPersistence;
        this.savingsPersistence = savingsPersistence;
    }

    public void saveCollectionSheet(final List<ClientAttendanceBO> clientAttendances, final List<LoanBO> loanAccounts,
            final List<AccountBO> customerAccountList, final List<SavingsBO> savingAccounts) {

        try {
            StaticHibernateUtil.startTransaction();

            clientAttendanceDao.save(clientAttendances);

            loanPersistence.save(loanAccounts);

            accountPersistence.save(customerAccountList);

            savingsPersistence.save(savingAccounts);

            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}
