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

import org.mifos.customers.group.BasicGroupInfo;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;

public class PortfolioAtRiskHelper extends TaskHelper {

    public PortfolioAtRiskHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {

        long time1 = new DateTimeService().getCurrentDateTime().getMillis();
        List<BasicGroupInfo> groupInfos = null;
        List<String> errorList = new ArrayList<String>();

        try {
            groupInfos = new CustomerPersistence().getAllBasicGroupInfo();

        } catch (Exception e) {
            throw new BatchJobException(e);
        }

        if (groupInfos != null && !groupInfos.isEmpty()) {

            int groupCount = groupInfos.size();
            getLogger().info("PortfolioAtRisk: got " + groupCount + " groups to process.");
            long startTime = new DateTimeService().getCurrentDateTime().getMillis();
            int i = 1;
            Integer groupId = null;
            GroupPersistence groupPersistence = new GroupPersistence();
            try {
                for (BasicGroupInfo groupInfo : groupInfos) {
                    groupId = groupInfo.getGroupId();
                    String searchStr = groupInfo.getSearchId() + ".%";
                    double portfolioAtRisk = PortfolioAtRiskCalculation.generatePortfolioAtRiskForTask(groupId,
                            groupInfo.getBranchId(), searchStr);

                    // update group perf history and group table for the field
                    // updated_by and updated_date
                    if (portfolioAtRisk > -1) {
                        groupPersistence.updateGroupInfoAndGroupPerformanceHistoryForPortfolioAtRisk(portfolioAtRisk,
                                groupId);
                    }
                    if (i % 500 == 0) {
                        long time = new DateTimeService().getCurrentDateTime().getMillis();
                        getLogger().info(
                                "500 groups updated in " + (time - startTime) + " milliseconds. There are "
                                        + (groupCount - i) + " more groups to be updated.");
                        startTime = time;

                    }
                    i++;
                }

            } catch (Exception e) {
                getLogger().error(
                        "PortfolioAtRiskHelper execute failed with exception " + e.getClass().getName() + ": "
                                + e.getMessage() + " at group " + groupId.toString(), e);

                StaticHibernateUtil.rollbackTransaction();
                if (groupId != null)
                    errorList.add(groupId.toString());
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }

        long time2 = new DateTimeService().getCurrentDateTime().getMillis();
        getLogger().info("PortfolioAtRiskTask ran in " + (time2 - time1) + " milliseconds");

        if (errorList.size() > 0)
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }

}
