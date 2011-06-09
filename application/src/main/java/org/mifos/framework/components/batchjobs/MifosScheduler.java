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

package org.mifos.framework.components.batchjobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.util.ConfigurationLocator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class MifosScheduler {

    private static final String BATCH_JOB_CLASS_PATH_PREFIX = "org.mifos.framework.components.batchjobs.helpers.";
    private Scheduler scheduler = null;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;
    private JobRepository jobRepository;
    private JobLocator jobLocator;
    private ConfigurableApplicationContext springTaskContext = null;
    private static final Logger logger = LoggerFactory.getLogger(MifosScheduler.class);
    private ConfigurationLocator configurationLocator;

    public void initialize() throws TaskSystemException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(getTaskConfigurationResource().getURI().toString());
            NodeList rootElement = document.getElementsByTagName(SchedulerConstants.SPRING_BEANS_FILE_ROOT_TAG);
            if(rootElement.getLength() > 0) { // new Quartz scheduler
                springTaskContext = ApplicationContextFactory.createApplicationContext(getTaskConfigurationResource());
                scheduler = (Scheduler)springTaskContext.getBean(SchedulerConstants.SPRING_SCHEDULER_BEAN_NAME);
                jobExplorer = (JobExplorer)springTaskContext.getBean(SchedulerConstants.JOB_EXPLORER_BEAN_NAME);
                jobRepository = (JobRepository)springTaskContext.getBean(SchedulerConstants.JOB_REPOSITORY_BEAN_NAME);
                jobLauncher = (JobLauncher)springTaskContext.getBean(SchedulerConstants.JOB_LAUNCHER_BEAN_NAME);
                jobLocator = (JobLocator)springTaskContext.getBean(SchedulerConstants.JOB_LOCATOR_BEAN_NAME);
            } else { // old legacy Mifos Scheduler
                throw new MifosRuntimeException("The legacy custom Mifos format for task.xml is no longer supported.  Please convert your custom tasks.xml file to the new format.  The tasks.xml that ships with Mifos can be used as an example.");
            }
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Deprecated
    public void schedule(final String jobName, Date initialTime, long delay,
                         JobRegistry jobRegistry, final JobRepository jobRepository,
                         Map<String, Object> jobData, ResourcelessTransactionManager transactionManager) throws TaskSystemException {

        try {
            final TaskletStep step = new TaskletStep();
            step.setName(jobName);
            Tasklet tasklet = (Tasklet) Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + getHelperName(jobName)).newInstance();
            step.setTasklet(tasklet);
            step.setJobRepository(jobRepository);
            step.setTransactionManager(transactionManager);
            step.afterPropertiesSet();
            jobRegistry.register(new JobFactory() {

                @Override
                public Job createJob() {
                    SimpleJob job = new SimpleJob(jobName+"Job");
                    job.setJobRepository(jobRepository);
                    job.setRestartable(true);
                    job.registerJobExecutionListener(new BatchJobListener());
                    job.addStep(step);
                    return job;
                }

                @Override
                public String getJobName() {
                    return jobName+"Job";
                }
            });
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }

        JobDetailBean jobDetailBean = new JobDetailBean();
        jobDetailBean.setJobDataAsMap(jobData);
        try {
            jobDetailBean.setJobClass(Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + jobName));
        } catch (ClassNotFoundException cnfe) {
            throw new TaskSystemException(cnfe);
        }
        jobDetailBean.setName(jobName+"Job");
        jobDetailBean.setGroup(Scheduler.DEFAULT_GROUP);
        jobDetailBean.afterPropertiesSet();

        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setName(jobName+"Job");
        trigger.setGroup(Scheduler.DEFAULT_GROUP);
        trigger.setStartTime(initialTime);
        trigger.setRepeatInterval(delay);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        try {
            scheduler.scheduleJob(jobDetailBean, trigger);
        } catch(SchedulerException se) {
            throw new TaskSystemException(se);
        }
    }

    @Deprecated
    public void scheduleLoanArrearsAndPortfolioAtRisk(Date initialTime, long delay,
                         JobRegistry jobRegistry, final JobRepository jobRepository,
                         Map<String, Object> jobData, ResourcelessTransactionManager transactionManager) throws TaskSystemException {
        final String jobName = "LoanArrearsAndPortfolioAtRiskTask";
        try {
            final TaskletStep step1 = new TaskletStep();
            step1.setName("LoanArrearsAndPortfolioAtRiskTask-step-1");
            step1.setTasklet((Tasklet) Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + getHelperName("LoanArrearsTask")).newInstance());
            step1.setJobRepository(jobRepository);
            step1.setTransactionManager(transactionManager);
            step1.afterPropertiesSet();

            final TaskletStep step2 = new TaskletStep();
            step2.setName("LoanArrearsAndPortfolioAtRiskTask-step-2");
            step2.setTasklet((Tasklet) Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + getHelperName("PortfolioAtRiskTask")).newInstance());
            step2.setJobRepository(jobRepository);
            step2.setTransactionManager(transactionManager);
            step2.afterPropertiesSet();
            jobRegistry.register(new JobFactory() {

                @Override
                public Job createJob() {
                    SimpleJob job = new SimpleJob(jobName+"Job");
                    job.setJobRepository(jobRepository);
                    job.setRestartable(true);
                    job.registerJobExecutionListener(new BatchJobListener());
                    job.addStep(step1);
                    job.addStep(step2);
                    return job;
                }

                @Override
                public String getJobName() {
                    return jobName+"Job";
                }
            });
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }

        JobDetailBean jobDetailBean = new JobDetailBean();
        jobDetailBean.setJobDataAsMap(jobData);
        try {
            jobDetailBean.setJobClass(Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + "PortfolioAtRiskTask"));
        } catch (ClassNotFoundException cnfe) {
            throw new TaskSystemException(cnfe);
        }
        jobDetailBean.setName(jobName+"Job");
        jobDetailBean.setGroup(Scheduler.DEFAULT_GROUP);
        jobDetailBean.afterPropertiesSet();

        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setName(jobName+"Job");
        trigger.setGroup(Scheduler.DEFAULT_GROUP);
        trigger.setStartTime(initialTime);
        trigger.setRepeatInterval(delay);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        try {
            scheduler.scheduleJob(jobDetailBean, trigger);
        } catch(SchedulerException se) {
            throw new TaskSystemException(se);
        }
    }

    private String getHelperName(String jobName) throws TaskSystemException {
        if ("ProductStatus".equals(jobName)) {
            return "ProductStatusHelper";
        }
        if ("LoanArrearsTask".equals(jobName)) {
            return "LoanArrearsHelper";
        }
        if ("SavingsIntPostingTask".equals(jobName)) {
            return "SavingsIntPostingHelper";
        }
        if ("ApplyCustomerFeeChangesTask".equals(jobName)) {
            return "ApplyCustomerFeeChangesHelper";
        }
        if ("LoanArrearsAgingTask".equals(jobName)) {
            return "LoanArrearsAgingHelper";
        }
        if ("ApplyHolidayChangesTask".equals(jobName)) {
            return "ApplyHolidayChangesHelper";
        }
        if ("PortfolioAtRiskTask".equals(jobName)) {
            return "PortfolioAtRiskHelper";
        }
        if ("GenerateMeetingsForCustomerAndSavingsTask".equals(jobName)) {
            return "GenerateMeetingsForCustomerAndSavingsHelper";
        }
        if ("BranchReportTask".equals(jobName)) {
            return "BranchReportHelper";
        }

        throw new TaskSystemException("Unknown helper for " + jobName);
    }

    public void shutdown() throws TaskSystemException {
        if(scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException se) {
                throw new TaskSystemException(se);
            }
        }
    }

    public List<String> getTaskNames() throws TaskSystemException {
        try {
            List<String> taskNames = new ArrayList<String>();
            String[] groupNames = scheduler.getJobGroupNames();
            for(String group : groupNames) {
                Collections.addAll(taskNames, scheduler.getJobNames(group));
            }
            return taskNames;
        }
        catch (SchedulerException e) {
            throw new TaskSystemException(e);
        }

    }

    public Date getJobsPreviousRunTime(String jobName) {
        JobExplorer explorer = getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 1);
        if(jobInstances.size() == 0) {
            return new Date(0);
        }
        JobInstance jobInstance = jobInstances.get(0);
        long executionTimeInMillis = jobInstance.getJobParameters().getLong(MifosBatchJob.JOB_EXECUTION_TIME_KEY);
        return new Date(executionTimeInMillis);
    }

    public Date getJobsLastSuccessfulRunTime(String jobName) {
        JobExplorer explorer = getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 100);

        for (JobInstance job : jobInstances) {
            for (JobExecution execution : explorer.getJobExecutions(job)) {
                if (BatchStatus.COMPLETED.equals(execution.getStatus())) {
                    long executionTimeInMillis = job.getJobParameters().getLong(MifosBatchJob.JOB_EXECUTION_TIME_KEY);
                    return new Date(executionTimeInMillis);
                }
            }
        }

        return new Date(0);
    }

    public String getJobsPreviousRunStatus(String jobName) {
        JobExplorer explorer = getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 1);
        if(jobInstances.size() == 0) {
            return "Never executed yet";
        }
        List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstances.get(0));
        if(jobExecutions.size() == 0) {
            return "Never executed yet";
        }
        String runStatus = jobExecutions.get(0).getStatus().toString();
        runStatus = runStatus.substring(0, 1) + runStatus.substring(1).toLowerCase();

        return runStatus;
    }

    public String getJobFailDescription(String jobName) {
        String failDescription = null;
        JobExplorer explorer = getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 1);
        if (jobInstances.size() > 0) {
            List<JobExecution> jobExecutions = explorer.getJobExecutions(jobInstances.get(0));
            if (jobExecutions.size() > 0) {
                Collection<StepExecution> steps = jobExecutions.get(0).getStepExecutions();
                if (steps.size() > 0) {
                    StepExecution step = steps.iterator().next();
                    if (!step.getExitStatus().getExitDescription().isEmpty()) {
                        failDescription = step.getExitStatus().getExitDescription();
                    }
                }
            }
        }
        return failDescription;
    }

    @SuppressWarnings("unchecked")
    public void runAllTasks() throws TaskSystemException {
        try {
            List<String> triggerNames;
            List<Trigger> triggers = new ArrayList<Trigger>();
            String[] groupNames = scheduler.getTriggerGroupNames();
            for(String group : groupNames) {
                triggerNames = new ArrayList<String>(Arrays.asList(scheduler.getTriggerNames(group)));
                for(String triggerName : triggerNames) {
                    triggers.add(scheduler.getTrigger(triggerName, group));
                }
            }
            Collections.sort(triggers, new Comparator<Trigger>() {
                @Override
                public int compare(Trigger trigger1, Trigger trigger2) {
                    if(trigger1.getPriority() < trigger2.getPriority()) {
                        return -1;
                    } else if(trigger1.getPriority() > trigger2.getPriority()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            for(Trigger trigger : triggers) {
                scheduler.triggerJob(trigger.getJobName(), trigger.getJobGroup());
            }
        } catch (SchedulerException se) {
            throw new TaskSystemException(se);
        }
    }

    public void runIndividualTask(String taskName) throws TaskSystemException {
        try {
            String[] scheduledTasksGroups = scheduler.getJobGroupNames();
            OUTER: for(String groupName : scheduledTasksGroups) {
                String[] groupsTaskNames = scheduler.getJobNames(groupName);
                for(String groupsTaskName : groupsTaskNames) {
                    if(groupsTaskName.equals(taskName)) {
                        scheduler.triggerJob(taskName, groupName);
                        break OUTER;
                    }
                }
            }
        } catch (SchedulerException se) {
            throw new TaskSystemException(se);
        }
    }

    public JobExplorer getBatchJobExplorer() {
        return jobExplorer;
    }

    public JobRepository getBatchJobRepository() {
        return jobRepository;
    }

    public JobLauncher getBatchJobLauncher() {
        return jobLauncher;
    }

    public JobLocator getBatchJobLocator() {
        return jobLocator;
    }

    private Resource getTaskConfigurationResource() throws IOException {
        Resource configuration = getConfigurationLocator().getResource(SchedulerConstants.CONFIGURATION_FILE_NAME);
        logger.info("Reading task configuration from: " + configuration.getDescription());
        return configuration;
    }

    public ConfigurationLocator getConfigurationLocator() {
        if (configurationLocator == null) {
            configurationLocator = new ConfigurationLocator();
        }
        return this.configurationLocator;
    }

    public void setConfigurationLocator(ConfigurationLocator configurationLocator) {
        this.configurationLocator = configurationLocator;
    }

}
