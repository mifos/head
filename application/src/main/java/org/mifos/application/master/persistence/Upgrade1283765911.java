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

import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.components.batchjobs.MifosBatchJob;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.BatchStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class Upgrade1283765911 extends Upgrade {

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        new AddActivity("Permissions-CanRunBatchJobsOnDemand", SecurityConstants.CAN_RUN_BATCH_JOBS_ON_DEMAND,
                SecurityConstants.SYSTEM_INFORMATION).upgrade(connection);
        new AddActivity("Permissions-CanUpdateBatchJobsConfiguration", SecurityConstants.CAN_UPDATE_BATCH_JOBS_CONFIGURATION,
                SecurityConstants.SYSTEM_INFORMATION).upgrade(connection);

        ResultSet rs;
        rs = connection.createStatement().executeQuery("select taskname, max(starttime) from scheduled_tasks where status = 1 and description = 'Finished Successfully' group by taskname");
        if (rs.first()) {
            JdbcJobInstanceDao jobInstanceDao;
            JdbcJobExecutionDao jobExecutionDao;
            try {
                DataSource dataSource = SessionFactoryUtils.getDataSource(StaticHibernateUtil.getSessionFactory());
                SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(dataSource);

                jobInstanceDao = new JdbcJobInstanceDao();
                jobInstanceDao.setJdbcTemplate(jdbcTemplate);
                jobInstanceDao.setJobIncrementer(new MySQLMaxValueIncrementer(dataSource, "BATCH_JOB_SEQ", "id"));
                jobInstanceDao.afterPropertiesSet();

                jobExecutionDao = new JdbcJobExecutionDao();
                jobExecutionDao.setJdbcTemplate(jdbcTemplate);
                jobExecutionDao.setJobExecutionIncrementer(new MySQLMaxValueIncrementer(dataSource, "BATCH_JOB_EXECUTION_SEQ", "id"));
                jobExecutionDao.afterPropertiesSet();

            } catch (Exception e) {
                throw new IOException(e);
            }
            createSpringBatchJobInstance(jobInstanceDao, jobExecutionDao, rs.getString(1), rs.getTimestamp(2));
            while (rs.next()) {
                createSpringBatchJobInstance(jobInstanceDao, jobExecutionDao, rs.getString(1), rs.getTimestamp(2));
            }
        }

        connection.createStatement().executeUpdate("drop table scheduled_tasks");
    }

    private void createSpringBatchJobInstance(JdbcJobInstanceDao jobInstanceDao, JdbcJobExecutionDao jobExecutionDao, String jobName, Timestamp startTime) {
        if ("PortfolioAtRiskTask".equals(jobName)) {
            createSpringBatchJobInstance(jobInstanceDao, jobExecutionDao, "LoanArrearsAndPortfolioAtRiskTask", startTime);
        }
        JobParameter jobParameter = new JobParameter(startTime.getTime());
        Map<String, JobParameter> parameters = new HashMap<String, JobParameter>();
        parameters.put(MifosBatchJob.JOB_EXECUTION_TIME_KEY, jobParameter);
        JobParameters jobParameters = new JobParameters(parameters);
        JobInstance job = jobInstanceDao.createJobInstance(jobName + "Job", jobParameters);

        JobExecution jobExecution = new JobExecution(job);
        Date jobExecutionTime = new Date(startTime.getTime());
        jobExecution.setCreateTime(jobExecutionTime);
        jobExecution.setStartTime(jobExecutionTime);
        jobExecution.setEndTime(jobExecutionTime);
        jobExecution.setExitStatus(ExitStatus.COMPLETED);
        jobExecution.setStatus(BatchStatus.COMPLETED);
        jobExecutionDao.saveJobExecution(jobExecution);
    }

}
