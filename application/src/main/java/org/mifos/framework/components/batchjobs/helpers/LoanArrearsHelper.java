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
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.config.GeneralConfig;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;

public class LoanArrearsHelper extends TaskHelper {

    public LoanArrearsHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        long time1 = new DateTimeService().getCurrentDateTime().getMillis();
        AccountPersistence accountPersistence = new AccountPersistence();
        List<String> errorList = new ArrayList<String>();
        List<Integer> listAccountIds = null;
        int accountNumber = 0;
        try {
            Short latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
            long time3 = new DateTimeService().getCurrentDateTime().getMillis();
            listAccountIds = new LoanPersistence().getLoanAccountsInArrearsInGoodStanding(latenessDays);
            long duration2 = new DateTimeService().getCurrentDateTime().getMillis() - time3;
            accountNumber = listAccountIds.size();
            getLogger().info(
                    "LoanArrearsTask: getLoanAccountsInArrearsInGoodStanding ran in " + duration2 + " milliseconds"
                            + " got " + accountNumber + " accounts to update.");
        } catch (Exception e) {
            throw new BatchJobException(e);
        }
        LoanBO loanBO = null;
        int i = 1;
        int batchSize = GeneralConfig.getBatchSizeForBatchJobs();
        int recordCommittingSize = GeneralConfig.getRecordCommittingSizeForBatchJobs();

        try {
            long startTime = new DateTimeService().getCurrentDateTime().getMillis();
            for (Integer accountId : listAccountIds) {
                loanBO = (LoanBO) accountPersistence.getAccount(accountId);
                assert (loanBO.getAccountState().getId().shortValue() == AccountState.LOAN_ACTIVE_IN_GOOD_STANDING
                        .getValue().shortValue());

                loanBO.handleArrears();
                if (i % batchSize == 0) {
                    StaticHibernateUtil.flushAndClearSession();
                }
                if (i % recordCommittingSize == 0) {
                    StaticHibernateUtil.commitTransaction();
                }
                if (i % 1000 == 0) {
                    long time = new DateTimeService().getCurrentDateTime().getMillis();
                    getLogger().info(
                            "1000 accounts updated in " + (time - startTime) + " milliseconds. There are "
                                    + (accountNumber - i) + " more accounts to be updated.");
                    startTime = time;
                }
                i++;
            }
            StaticHibernateUtil.commitTransaction();

        } catch (Exception e) {
            getLogger().debug("LoanArrearsTask " + e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
            if (loanBO != null) {
                errorList.add(loanBO.getAccountId().toString());
            }
        } finally {
            StaticHibernateUtil.closeSession();
        }

        long time2 = new DateTimeService().getCurrentDateTime().getMillis();
        long duration = time2 - time1;
        getLogger().info("LoanArrearsTask ran in " + duration + " milliseconds");
        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

}
