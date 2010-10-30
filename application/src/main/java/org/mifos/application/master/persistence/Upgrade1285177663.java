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

package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.config.GeneralConfig;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.DateTimeService;

/**
 * Run the fix for MIFOS-3712 to ensure that fee schedules are caught up.
 * This does a check to see if this fix was already run by Mifos 1.6.1
 * and does not rerun the fix if it was already run.
 *
 */
public class Upgrade1285177663 extends Upgrade {

    private final AccountPersistence accountPersistence = new AccountPersistence();

    public Upgrade1285177663() {
        super();
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {

		// MIFOS-4004 - workaround for problem with incompatible Hibernate mappings and DB state
		Upgrade1286195484.conditionalAlter(connection);

        String key = "Recurring fees cleanup done for MIFOS-3712";

        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        if (configurationPersistence.getConfigurationKeyValueInteger(key) == null) {
            try {
                execute();
            } catch (BatchJobException e) {
                throw new MifosRuntimeException(e);
            }
        }
    }

    public void execute() throws BatchJobException {

        long taskStartTime = new DateTimeService().getCurrentDateTime().getMillis();

        List<Integer> customerAccountIds;

        customerAccountIds = findAccountIdsToFix();


        int accountCount = customerAccountIds.size();
        if (accountCount == 0) {
            return;
        }

        List<String> errorList = new ArrayList<String>();
        int currentRecordNumber = 0;
        int outputIntervalForBatchJobs = GeneralConfig.getOutputIntervalForBatchJobs();
        int batchSize = GeneralConfig.getBatchSizeForBatchJobs();
        // jpw - hardcoded recordCommittingSize to 500 because now only accounts that need more schedules are returned
        int recordCommittingSize = 500;

        infoLogBatchParameters(accountCount, outputIntervalForBatchJobs, batchSize, recordCommittingSize);

        long startTime = new DateTimeService().getCurrentDateTime().getMillis();
        Integer currentAccountId = null;
        int updatedRecordCount = 0;

        try {
            StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();

            for (Integer accountId : customerAccountIds) {
                currentRecordNumber++;
                currentAccountId = accountId;
                AccountBO accountBO = accountPersistence.getAccount(accountId);

                if (accountBO instanceof CustomerAccountBO) {
                    ((CustomerAccountBO) accountBO).applyPeriodicFeesToNextSetOfMeetingDates();
                    updatedRecordCount++;
                }

                if (currentRecordNumber % batchSize == 0) {
                    StaticHibernateUtil.flushAndClearSession();
                    getLogger().debug("completed HibernateUtil.flushAndClearSession()");
                }
                if (updatedRecordCount > 0) {
                    if (updatedRecordCount % recordCommittingSize == 0) {
                        StaticHibernateUtil.commitTransaction();
                        StaticHibernateUtil.getSessionTL();
                        StaticHibernateUtil.startTransaction();
                    }
                }

                if (currentRecordNumber % outputIntervalForBatchJobs == 0) {
                    long time = System.currentTimeMillis();
                    String message = "" + currentRecordNumber + " processed, " + (accountCount - currentRecordNumber)
                    + " remaining, " + updatedRecordCount + " updated, batch time: " + (time - startTime)
                    + " ms";
                    logMessage(message);
                    startTime = time;
                }
            }
            StaticHibernateUtil.commitTransaction();
            long time = System.currentTimeMillis();
            String message = "" + currentRecordNumber + " processed, " + (accountCount - currentRecordNumber)
            + " remaining, " + updatedRecordCount + " updated, batch time: " + (time - startTime) + " ms";
            logMessage(message);

        } catch (Exception e) {
            logMessage("account " + currentAccountId.intValue() + " exception " + e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
            errorList.add(currentAccountId.toString());
            getLogger().error("Unable to generate schedules for account with ID " + currentAccountId, e);
        } finally {
            StaticHibernateUtil.flushAndClearSession();
        }

        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }

        logMessage("Upgrade1285177663 ran in "
                + (new DateTimeService().getCurrentDateTime().getMillis() - taskStartTime));

    }

    private void infoLogBatchParameters(int accountCount, int outputIntervalForBatchJobs, int batchSize,
            int recordCommittingSize) {
        logMessage("Using parameters:" + "\n  OutputIntervalForBatchJobs: " + outputIntervalForBatchJobs
                + "\n  BatchSizeForBatchJobs: " + batchSize + "\n  RecordCommittingSizeForBatchJobs: "
                + recordCommittingSize);
        String initial_message = "" + accountCount + " accounts to process, results output every "
        + outputIntervalForBatchJobs + " accounts";
        logMessage(initial_message);
    }

    @SuppressWarnings("unchecked")
    private List<Integer> findAccountIdsToFix() throws BatchJobException {
        List<Integer> AccountIds = new ArrayList<Integer>();
        long time1 = new DateTimeService().getCurrentDateTime().getMillis();

        Map<String, Object> parameters = new HashMap<String, Object>();
        try {
            AccountIds =  new AccountPersistence().executeNamedQuery("getCustomerAccountsWithSchedulesMissingPeriodicFees", parameters);
        } catch (PersistenceException e) {
            throw new BatchJobException(e);
        }

        long duration = new DateTimeService().getCurrentDateTime().getMillis() - time1;
        logMessage("Time to execute the query " + duration + " . Got " + AccountIds.size()
                + " accounts.");

        return AccountIds;
    }


    private void logMessage(String finalMessage) {
        System.out.println(finalMessage);
        getLogger().info(finalMessage);
    }


}
