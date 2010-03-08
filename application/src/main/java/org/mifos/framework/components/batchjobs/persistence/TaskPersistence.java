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

package org.mifos.framework.components.batchjobs.persistence;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.DateUtils;

public class TaskPersistence extends Persistence {

    public void saveAndCommitTask(Task task) throws PersistenceException {
        createOrUpdate(task);
        try {
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new PersistenceException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private Task getSuccessfulTask(Integer taskId) throws PersistenceException {
        Task task = null;
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("taskId", taskId);
        Object queryResult = execUniqueResultNamedQuery(NamedQueryConstants.SCHEDULED_TASK_GET_SUCCESSFUL_TASK,
                queryParameters);
        if (queryResult != null) {
            task = (Task) queryResult;
        }
        return task;
    }

    public boolean hasLoanArrearsTaskRunSuccessfully() throws PersistenceException {
        boolean result = false;
        String taskName = "LoanArrearsTask";
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("taskName", taskName);
        Object queryResult = execUniqueResultNamedQuery(NamedQueryConstants.SCHEDULED_TASK_GET_LATEST_TASK,
                queryParameters);
        if (queryResult != null) {
            Integer taskId = (Integer) queryResult;
            Task task = getSuccessfulTask(taskId);
            if (task != null) {
                Timestamp taskDate = task.getEndTime();
                Date dbDate = DateUtils.getDateWithoutTimeStamp(taskDate.getTime());
                Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
                if (dbDate.equals(currentDate)) {
                    result = true;
                }
            }
        }

        return result;
    }
}
