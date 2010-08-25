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

package org.mifos.framework.components.batchjobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.DateTimeService;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;

public class MifosScheduler {

    private static final String BATCH_JOB_CLASS_PATH_PREFIX = "org.mifos.framework.components.batchjobs.helpers.";
    private static final String LISTENERS_CLASS_PATH_PREFIX = "org.mifos.framework.components.batchjobs.listeners.";
    Scheduler scheduler = null;
    private static MifosLogger logger = MifosLogManager.getLogger(MifosScheduler.class.getName());
    private ConfigurationLocator configurationLocator;

    public void initialize() throws TaskSystemException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(getTaskConfigurationInputSource());
            NodeList rootElement = document.getElementsByTagName(SchedulerConstants.SPRING_BEANS_FILE_ROOT_TAG);
            if(rootElement.getLength() > 0) {
                ConfigurableApplicationContext context = null;
                context = new FileSystemXmlApplicationContext("file:"+getTaskConfigurationFilePath());
                scheduler = (Scheduler)context.getBean(SchedulerConstants.SPRING_SCHEDULER_BEAN_NAME);
            } else {
                StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
                String configPath = getQuartzSchedulerConfigurationFilePath();
                schedulerFactory.initialize(configPath);
                scheduler = schedulerFactory.getScheduler();
            }
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    /**
     * Method schedules specified batch job to run accordingly to given cron expression, with given priority.
     * Since quartz scheduler used in mifos is configured to use a single working thread, it's ensured that
     * jobs with higher priority will execute before ones with lower priority.
     *
     * @param jobName - the name of job to be scheduled. It's read from batch jobs xml file and must be the same
     * as the name of the Job class used to execute job's behaviour
     * @param jobGroup - name of the batch jobs group to which this job will belong
     * @param cronExpression - a cron expression. Important note: specifying both day-of-week and day-of-month
     * is not completely implemented in current (1.8.3) quartz version - only one should be specified, with other
     * being replaced by '?' character.
     * @param jobPriority - can be any value between 1 and 10, the higher the value the higher job's priority
     * @param jobListeners - a list of names of job listeners to be bound to this job. Names must match class names of
     * listener implementations
     * @param triggerListeners - a list of names of trigger listeners to be bound to this job's trigger. Names must
     * match class names of listener implementations
     * @throws TaskSystemException
     */
    public void schedule(
            String jobName,
            String jobGroup,
            String cronExpression,
            int jobPriority,
            List<String> jobListeners,
            List<String> triggerListeners) throws TaskSystemException {

        try {
            JobDetail jobDetail = new JobDetail();
            jobDetail.setName(jobName);
            jobDetail.setGroup(jobGroup);
            jobDetail.setJobClass(Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + jobName));
            jobDetail.setRequestsRecovery(true);
            for(String jobListener : jobListeners) {
                jobDetail.addJobListener(jobListener);
                instantiateJobListener(jobListener, false);
            }
            CronTrigger trigger = new CronTrigger();
            trigger.setName(jobName);
            trigger.setGroup(jobGroup);
            trigger.setCronExpression(cronExpression);
            trigger.setPriority(jobPriority);
            for(String triggerListener : triggerListeners) {
                trigger.addTriggerListener(triggerListener);
                instantiateTriggerListener(triggerListener, false);
            }
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    @Deprecated
    public void schedule(String jobName, Date initialTime, long delay) throws TaskSystemException {
        JobDetail jobDetail = new JobDetail();
        jobDetail.setName(jobName);
        jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
        try {
            jobDetail.setJobClass(Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + jobName));
        } catch (ClassNotFoundException cnfe) {
            throw new TaskSystemException(cnfe);
        }
        jobDetail.setRequestsRecovery(true);
        try {
            String jobListenerName = Class.forName(LISTENERS_CLASS_PATH_PREFIX + jobName + "Listener").getSimpleName();
            jobDetail.addJobListener(jobListenerName);
            instantiateJobListener(jobListenerName, false);
        } catch (ClassNotFoundException cnfe) { }
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setName(jobName);
        trigger.setGroup(Scheduler.DEFAULT_GROUP);
        trigger.setStartTime(initialTime);
        trigger.setRepeatInterval(delay);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        try {
            String triggerListenerName = Class.forName(LISTENERS_CLASS_PATH_PREFIX + jobName + "TriggerListener").getSimpleName();
            trigger.addTriggerListener(triggerListenerName);
            instantiateTriggerListener(triggerListenerName, false);
        } catch (ClassNotFoundException cnfe) { }
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch(SchedulerException se) {
            throw new TaskSystemException(se);
        }
    }

    /**
     * Adds a single instance of specified job listener to the scheduler. Calling this method
     * with the same 'listener' argument more than once has no effect (there is a single instance of
     * each listener).<br />
     * If listener should be bound to a single job, it's name must be provided to the job
     * during scheduling.
     *
     * @param listener - name of the job listener, it must match the name of a class implementing
     * listeners's behavior.
     * @param isGlobal - determines whether this listener should be bound to all jobs, or only to a specific one.
     * @throws TaskSystemException
     * @see #schedule(String, String, String, int, List, List)
     */
    public void instantiateJobListener(String listener, boolean isGlobal) throws TaskSystemException  {
        try {
            JobListener newListener = (JobListener)Class.forName(LISTENERS_CLASS_PATH_PREFIX + listener).newInstance();
            if(isGlobal) {
                scheduler.addGlobalJobListener(newListener);
            } else {
                scheduler.addJobListener(newListener);
            }
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    /**
     * Adds a single instance of specified trigger listener to the scheduler. Calling this method
     * with the same 'listener' argument more than once has no effect (there is a single instance of
     * each listener).<br />
     * If listener should be bound to a single trigger, it's name must be provided to the job
     * during scheduling.
     *
     * @param listener - name of the trigger listener, it must match the name of a class implementing
     * listeners's behavior.
     * @param isGlobal - determines whether this listener should be bound to all triggers, or only to a specific one.
     * @throws TaskSystemException
     * @see #schedule(String, String, String, int, List, List)
     */
    public void instantiateTriggerListener(String listener, boolean isGlobal) throws TaskSystemException {
        try {
            TriggerListener newListener = (TriggerListener)Class.forName(LISTENERS_CLASS_PATH_PREFIX + listener).newInstance();
            if(isGlobal) {
                scheduler.addGlobalTriggerListener(newListener);
            } else {
                scheduler.addTriggerListener(newListener);
            }
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
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

    /**
     * This method reads all the task from an xml file and registers them with
     * the MifosScheduler"
     */
    @Deprecated
    private void registerTasksOldConfigurationFile() throws TaskSystemException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(getTaskConfigurationInputSource());
            NodeList rootSchedulerTasks = document.getElementsByTagName(SchedulerConstants.SCHEDULER_TASKS);
            Element rootNodeName = (Element) rootSchedulerTasks.item(0);
            NodeList collectionOfScheduledTasks = rootNodeName.getElementsByTagName(SchedulerConstants.SCHEDULER);
            for (int i = 0; i < collectionOfScheduledTasks.getLength(); i++) {
                Element scheduledTask = (Element) collectionOfScheduledTasks.item(i);
                Element subNodeName1 = (Element) scheduledTask.getElementsByTagName(SchedulerConstants.TASK_CLASS_NAME)
                        .item(0);
                String taskName = ((Text) subNodeName1.getFirstChild()).getData().trim();
                Element subNodeName2 = (Element) scheduledTask.getElementsByTagName(SchedulerConstants.INITIAL_TIME)
                        .item(0);
                String initialTime = ((Text) subNodeName2.getFirstChild()).getData().trim();
                Element subNodeName3 = null;
                String delayTime = null;
                if ((scheduledTask.getElementsByTagName(SchedulerConstants.DELAY_TIME)) != null) {
                    subNodeName3 = (Element) scheduledTask.getElementsByTagName(SchedulerConstants.DELAY_TIME).item(0);
                    if (subNodeName3.getFirstChild() != null) {
                        delayTime = ((Text) subNodeName3.getFirstChild()).getData().trim();
                    }
                }
                if (Long.parseLong(delayTime) < 86400) {
                    throw new IllegalArgumentException("Please specify the delay time >= 86400(1 day)");
                }
                if(scheduler.getJobDetail(taskName, Scheduler.DEFAULT_GROUP) == null) {
                    schedule(taskName, parseInitialTime(initialTime), Long.parseLong(delayTime) * 1000);
                }
            }
            // Hard-coded initialization of default global listeners, as they don't exist in old configuration file:
            instantiateJobListener(SchedulerConstants.GLOBAL_JOB_LISTENER_NAME, true);
            instantiateTriggerListener(SchedulerConstants.GLOBAL_TRIGGER_LISTENER_NAME, true);
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    /**
     * Method register batch jobs from an xml file, making sure that the scheduler is not running
     * until all initialization tasks have been completed.
     * @throws TaskSystemException
     */
    public void initializeBatchJobs() throws TaskSystemException {
        try {
            if(!scheduler.isInStandbyMode()) {
                return;
            }
            dispatchConfigurationFileProcessing();
            scheduler.start();
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    /**
     * Method required for compatibility with old task configuration files.
     */
    private void dispatchConfigurationFileProcessing() throws TaskSystemException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(getTaskConfigurationInputSource());
            NodeList rootElement = document.getElementsByTagName(SchedulerConstants.SCHEDULER_CONFIG);
            if(rootElement.getLength() == 0) {
                registerTasksOldConfigurationFile();
            } else {
                registerTasks();
            }
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    /**
     * Method used to read batch jobs configuration from an xml file, shedule jobs and instantiate listeners.
     */
    private void registerTasks() throws TaskSystemException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(getTaskConfigurationInputSource());
            NodeList rootSchedulerConfig = document.getElementsByTagName(SchedulerConstants.SCHEDULER_CONFIG);
            Element rootNode = (Element)rootSchedulerConfig.item(0);

            NodeList globalJobListeners = rootNode.getElementsByTagName(SchedulerConstants.GLOBAL_JOB_LISTENERS);
            Element globalJobListenersNode = (Element)globalJobListeners.item(0);
            NodeList collectionOfGlobalJobListeners = globalJobListenersNode.getElementsByTagName(SchedulerConstants.LISTENER);
            for(int i = 0; i < collectionOfGlobalJobListeners.getLength(); i++) {
                Element globalJobListenerNode = (Element)collectionOfGlobalJobListeners.item(i);
                String globalJobListenerName = ((Text)globalJobListenerNode.getFirstChild()).getData().trim();
                instantiateJobListener(globalJobListenerName, true);
            }

            NodeList globalTriggerListeners = rootNode.getElementsByTagName(SchedulerConstants.GLOBAL_TRIGGER_LISTENERS);
            Element globalTriggerListenersNode = (Element)globalTriggerListeners.item(0);
            NodeList collectionOfglobalTriggerListeners = globalTriggerListenersNode.getElementsByTagName(SchedulerConstants.LISTENER);
            for(int i = 0; i < collectionOfglobalTriggerListeners.getLength(); i++) {
                Element globalTriggerListenerNode = (Element)collectionOfglobalTriggerListeners.item(i);
                String globalTriggerListenerName = ((Text)globalTriggerListenerNode.getFirstChild()).getData().trim();
                instantiateTriggerListener(globalTriggerListenerName, true);
            }

            NodeList schedule = rootNode.getElementsByTagName(SchedulerConstants.SCHEDULE);
            Element scheduleNode = (Element)schedule.item(0);
            NodeList collectionOfBatchJobs = scheduleNode.getElementsByTagName(SchedulerConstants.BATCH_JOB);
            for(int i = 0; i < collectionOfBatchJobs.getLength(); i++) {
                Element batchJobNode = (Element)collectionOfBatchJobs.item(i);
                Element jobNameNode = (Element)batchJobNode.getElementsByTagName(SchedulerConstants.JOB_NAME).item(0);
                String jobName = ((Text)jobNameNode.getFirstChild()).getData().trim();
                Element jobGroupNameNode = (Element)batchJobNode.getElementsByTagName(SchedulerConstants.JOB_GROUP_NAME).item(0);
                String jobGroupName = ((Text)jobGroupNameNode.getFirstChild()).getData().trim();
                Element jobListenersNode = (Element)batchJobNode.getElementsByTagName(SchedulerConstants.JOB_LISTENERS).item(0);
                NodeList jobListeners = jobListenersNode.getElementsByTagName(SchedulerConstants.LISTENER);
                List<String> jobListenersList = new ArrayList<String>();
                for(int j = 0; j < jobListeners.getLength(); j++) {
                    Element jobListenerNode = (Element)jobListeners.item(j);
                    String jobListenerName = ((Text)jobListenerNode.getFirstChild()).getData().trim();
                    jobListenersList.add(jobListenerName);
                }
                Element triggerListenersNode = (Element)batchJobNode.getElementsByTagName(SchedulerConstants.TRIGGER_LISTENERS).item(0);
                NodeList triggerListeners = triggerListenersNode.getElementsByTagName(SchedulerConstants.LISTENER);
                List<String> triggerListenersList = new ArrayList<String>();
                for(int j = 0; j < triggerListeners.getLength(); j++) {
                    Element triggerListenerNode = (Element)triggerListeners.item(j);
                    String triggerListenerName = ((Text)triggerListenerNode.getFirstChild()).getData().trim();
                    triggerListenersList.add(triggerListenerName);
                }
                if(scheduler.getJobDetail(jobName, jobGroupName) == null) {
                    Element jobPriorityNode = (Element)batchJobNode.getElementsByTagName(SchedulerConstants.JOB_PRIORITY).item(0);
                    int jobPriority = Integer.parseInt(((Text)jobPriorityNode.getFirstChild()).getData().trim());
                    Element cronExpressionNode = (Element)batchJobNode.getElementsByTagName(SchedulerConstants.CRON_EXPRESSION).item(0);
                    String cronExpression = ((Text)cronExpressionNode.getFirstChild()).getData().trim();
                    schedule(jobName, jobGroupName, cronExpression, jobPriority, jobListenersList, triggerListenersList);
                }
                for(String jobListener : jobListenersList) {
                    instantiateJobListener(jobListener, false);
                }
                for(String triggerListener : triggerListenersList) {
                    instantiateTriggerListener(triggerListener, false);
                }
            }
        } catch (Exception e) {
            throw new TaskSystemException(e);
        }
    }

    /**
     * This is a helper method that parses the initialtime string and returns a
     * valid Date time.
     */
    private Date parseInitialTime(String initialTime) {
        int firstIndex = initialTime.indexOf(':');
        int lastIndex = initialTime.indexOf(':', firstIndex);
        Calendar time = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        int hourOfTheDay = Integer.parseInt(initialTime.substring(0, firstIndex));
        int minutes = Integer.parseInt(initialTime.substring(firstIndex + 1, lastIndex + firstIndex + 1));
        int seconds = Integer.parseInt(initialTime.substring(lastIndex + firstIndex + 2, initialTime.length()));
        time.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
        time.set(Calendar.MINUTE, minutes);
        time.set(Calendar.SECOND, seconds);
        return time.getTime();
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

    @SuppressWarnings("unchecked")
    public void runAllTasks() throws TaskSystemException {
        try {
            List<String> triggerNames = null;
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

    private InputSource getTaskConfigurationInputSource() throws FileNotFoundException, IOException {
        File configurationFile = getConfigurationLocator().getFile(SchedulerConstants.CONFIGURATION_FILE_NAME);
        FileReader fileReader = new FileReader(configurationFile);
        logger.info("Reading task configuration from: " + configurationFile.getAbsolutePath());
        return new InputSource(fileReader);
    }

    private String getTaskConfigurationFilePath() throws IOException {
        File configurationFile = getConfigurationLocator().getFile(SchedulerConstants.CONFIGURATION_FILE_NAME);
        logger.info("Reading task configuration from: " + configurationFile.getAbsolutePath());
        return configurationFile.getAbsolutePath();
    }

    public String getQuartzSchedulerConfigurationFilePath() throws FileNotFoundException, IOException {
        File configurationFile = getConfigurationLocator().getFile(SchedulerConstants.SCHEDULER_CONFIGURATION_FILE_NAME);
        logger.info("Reading scheduler configuration from: " + configurationFile.getAbsolutePath());
        return configurationFile.getAbsolutePath();
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
