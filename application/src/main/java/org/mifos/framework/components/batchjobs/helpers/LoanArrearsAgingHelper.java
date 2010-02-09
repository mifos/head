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

package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class LoanArrearsAgingHelper extends TaskHelper {

    public LoanArrearsAgingHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        List<Integer> listAccountIds = null;
        List<String> errorList = new ArrayList<String>();
        try {
            listAccountIds = new LoanPersistence().getLoanAccountsInArrears(Short.valueOf("1"));
        } catch (Exception e) {
            throw new BatchJobException(e);
        }

        for (Integer accountId : listAccountIds) {
            try {
                LoanBO loanBO = new LoanPersistence().getAccount(accountId);
                loanBO.handleArrearsAging();
                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                errorList.add(accountId.toString());
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }
        if (errorList.size() > 0)
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }
}
