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

package org.mifos.framework.components.batchjobs;

import java.sql.Timestamp;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.batchjobs.helpers.TaskStatus;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;

public abstract class TaskHelper {

    private MifosTask mifosTask;

    private Task task;

    long timeInMillis = 0;

    private MifosLogger logger;

    public TaskHelper(MifosTask mifosTask) {
        this.mifosTask = mifosTask;
        this.logger = MifosLogManager.getLogger(LoggerConstants.BATCH_JOBS);
    }

    protected MifosLogger getLogger() {
        return logger;
    }

    protected void setLogger(MifosLogger logger) {
        this.logger = logger;
    }

    /**
     * This method is responsible for inserting a row with the task name in the
     * database. In cases that the task fails, the next day's task will not run
     * till the completion of the previous day's task.
     */
    public final void registerStartup(long timeInMillis) throws BatchJobException {
        try {
            MifosTask.batchJobStarted();
            task = new Task();
            task.setDescription(SchedulerConstants.START);
            task.setTask(mifosTask.name);
            task.setStatus(TaskStatus.INCOMPLETE);
            if (timeInMillis == 0) {
                task.setStartTime(new Timestamp(new DateTimeService().getCurrentDateTime().getMillis()));
            } else {
                task.setStartTime(new Timestamp(timeInMillis));
            }
            new TaskPersistence().saveAndCommitTask(task);
        } catch (PersistenceException e) {
            throw new BatchJobException(e);
        }
    }

    /**
     * This method is responsible for inserting a row with the task name in the
     * database, at end of task completion. In cases where the task fails, the
     * next day's task will not run till the completion of th previous day's
     * task.
     */
    public final void registerCompletion(long timeInMillis, String description, TaskStatus status) {
        try {
            task.setDescription(description);
            task.setStatus(status);
            if (timeInMillis == 0) {
                task.setEndTime(new Timestamp(new DateTimeService().getCurrentDateTime().getMillis()));
            } else {
                task.setEndTime(new Timestamp(timeInMillis));
            }
            new TaskPersistence().saveAndCommitTask(task);
        } catch (PersistenceException e) {
            getLogger().error("unable to register completion of " + mifosTask.name, e);
        } finally {
            MifosTask.batchJobFinished();
        }
    }

    /**
     * This method is called by {@link MifosTask#run()}.
     */
    public final void executeTask() {
        if (!isTaskAllowedToRun()) {
            while ((new DateTimeService().getCurrentDateTime().getMillis() - timeInMillis) / (1000 * 60 * 60 * 24) != 1) {
                getLogger().info(mifosTask.name + " will run catch-up execution for " + new java.util.Date(timeInMillis));
                perform(timeInMillis + (1000 * 60 * 60 * 24));
                timeInMillis += (1000 * 60 * 60 * 24);
            }
        } else {
            if (timeInMillis == 0) {
                timeInMillis = new DateTimeService().getCurrentDateTime().getMillis();
            }
            perform(timeInMillis);
        }
    }

    /**
     * This methods, performs the job specific to each task. Most batch jobs must be run daily.
     *
     * @param timeInMillis
     *            date the job is being run for. Useful for "catch up", ie: running batch jobs for dates past. Note
     *            some jobs ignore this value.
     */
    public abstract void execute(long timeInMillis) throws BatchJobException;

    /**
     * This method determines if catch-up should be performed. If the
     * previous day's task has failed, the default implementation suspends the
     * current day's task and runs the previous days task.
     * <p>
     * Override this method and return true, if it is not mandatory that task
     * should run daily i.e. In case yesterday's task has failed, you want it to
     * continue running current days task.
     */
    public boolean isTaskAllowedToRun() {
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();
            String hqlSelect = "select max(t.startTime) from Task t "
                    + "where t.task=:taskName and t.description=:finishedSuccessfully";
            Query query = session.createQuery(hqlSelect);
            query.setString("taskName", mifosTask.name);
            query.setString("finishedSuccessfully", SchedulerConstants.FINISHED_SUCCESSFULLY);
            if (query.uniqueResult() == null) {
                // When scheduler starts for the first time
                timeInMillis = new DateTimeService().getCurrentDateTime().getMillis();
                return true;
            } else {
                timeInMillis = ((Timestamp) query.uniqueResult()).getTime();
            }
            StaticHibernateUtil.commitTransaction();
            if ((new DateTimeService().getCurrentDateTime().getMillis() - timeInMillis) / (1000 * 60 * 60 * 24) <= 1) {
                timeInMillis = new DateTimeService().getCurrentDateTime().getMillis();
                return true;
            }
            return false;
        } catch (Exception e) {
            getLogger().error("ignored exception", e);
            return true;
        }
    }

    private boolean isPortfolioAtRiskAllowedToRun() throws BatchJobException {
        boolean isAllowedToRun = false;
        TaskPersistence p = new TaskPersistence();
        try {
            isAllowedToRun = p.hasLoanArrearsTaskRunSuccessfully();
            if (isAllowedToRun == false) {
                String message = "PortfolioAtRisk Task can't run because it requires the LoanArrearsTask to run successfully first.";
                getLogger().error(message);
                return isAllowedToRun;
            }
        } catch (PersistenceException ex) {
            throw new BatchJobException(ex);
        }
        return isAllowedToRun;

    }

    private void perform(long timeInMillis) {
        try {
            registerStartup(timeInMillis);
            if (mifosTask.name != null) {
                if (mifosTask.name.equals("PortfolioAtRiskTask")) {
                    if (!isPortfolioAtRiskAllowedToRun()) {
                        String description = "PortfolioAtRisk Task can't run because it requires the LoanArrearsTask to run successfully first.";
                        registerCompletion(0, description, TaskStatus.INCOMPLETE);
                        return;
                    }
                }
            }
            getLogger().info(mifosTask.name + " starting");
            execute(timeInMillis);
            getLogger().info(mifosTask.name + " finished");
            registerCompletion(0, SchedulerConstants.FINISHED_SUCCESSFULLY, TaskStatus.COMPLETE);
        } catch (BatchJobException e) {
            getLogger().error(mifosTask.name + " failed", e);
            registerCompletion(timeInMillis, e.getErrorMessage(), TaskStatus.FAILED);
        }
    }
}
