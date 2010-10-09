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

package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class SavingsIntCalcHelper extends TaskHelper {

    protected SavingsServiceFacade savingsServiceFacade = DependencyInjectedServiceLocator.locateSavingsServiceFacade();
    protected SavingsDao savingsDao = DependencyInjectedServiceLocator.locateSavingsDao();

    public SavingsIntCalcHelper() {
        super();
    }

    @Override
    public void execute(@SuppressWarnings("unused") long timeInMillis) throws BatchJobException {
        List<String> errorList = new ArrayList<String>();
        List<Integer> accountList;
        try {
            accountList = savingsDao.retreiveAccountsPendingForInterestCalculation(new LocalDate());
        } catch (Exception e) {
            throw new BatchJobException(e);
        }
        for (Integer accountId : accountList) {
            try {
                savingsServiceFacade.calculateInterestForPostingInterval(accountId.longValue() , null);
                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                errorList.add(accountId.toString());
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }
        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }
}
