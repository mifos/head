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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.DateTimeService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class MifosScheduler {

    private static final String BATCH_JOB_CLASS_PATH_PREFIX = "org.mifos.framework.components.batchjobs.helpers.";
    Timer timer = null;
    ArrayList<MifosTask> tasks = new ArrayList<MifosTask>();
    private static MifosLogger logger = MifosLogManager.getLogger(MifosScheduler.class.getName());
    private ConfigurationLocator configurationLocator;

    public MifosScheduler() {
        boolean isDaemonThread = true;
        timer = new Timer("Mifos Task Scheduler Thread", isDaemonThread);
    }

    /**
     * This method schedules a specified task to run at a specified time after a
     * specified delay
     */
    public void schedule(MifosTask task, Date initial, long delay) {
        timer.schedule(task, initial, delay);
        tasks.add(task);
    }
    
    public void shutdown() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * This method reads all the task from an xml file and registers them with
     * the MifosScheduler"
     */
    public void registerTasks() throws Exception {
        MifosTask mifosTask;
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
            mifosTask = (MifosTask) Class.forName(BATCH_JOB_CLASS_PATH_PREFIX + taskName).newInstance();
            mifosTask.name = taskName;
            if (delayTime != null) {
                if (Long.parseLong(delayTime) < 86400) {
                    throw new IllegalArgumentException("Please specify the delay time >= 86400(1 day)");
                }
                schedule(mifosTask, parseInitialTime(initialTime), Long.parseLong(delayTime) * 1000);
            } else {
                schedule(mifosTask, parseInitialTime(initialTime));
            }
        }
    }

    /**
     * These are the non-regular jobs which do not recur but have to be run
     * infrequently on user request. Hence there is no delay input. An example:
     * Change in center meeting schedule which needs to change all inherited
     * meetings as well as reschedule loans, these tasks typically require a few
     * input params as well.
     * 
     * @deprecated YAGNI. Not currently used, and not known to be necessary. No
     *             replacement has been identified.
     */
    public void schedule(MifosTask task, Date initial) {
        timer.schedule(task, initial);
        tasks.add(task);
    }

    /**
     * This is a helper method that parses the initialtime string and returns a
     * valid Date time.
     */
    public Date parseInitialTime(String initialTime) {
        int firstIndex = initialTime.indexOf(":");
        int lastIndex = initialTime.indexOf(":", firstIndex);
        Calendar time = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        int hourOfTheDay = Integer.parseInt(initialTime.substring(0, firstIndex));
        int minutes = Integer.parseInt(initialTime.substring(firstIndex + 1, lastIndex + firstIndex + 1));
        int seconds = Integer.parseInt(initialTime.substring(lastIndex + firstIndex + 2, initialTime.length()));
        time.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
        time.set(Calendar.MINUTE, minutes);
        time.set(Calendar.SECOND, seconds);
        return time.getTime();
    }

    public List<String> getTaskNames() {
        List<String> taskNames = new ArrayList<String>();
        for (MifosTask task : tasks) {
            taskNames.add(task.name);
        }
        return taskNames;
    }
    
    public void runAllTasks() {
        /* should this method only exist in BatchJobController? */
        /* BatchJobController could probably figure this out on its own... */
        for (MifosTask task : tasks) {
            task.run();
        }
    }

    private InputSource getTaskConfigurationInputSource() throws FileNotFoundException, IOException {
        File configurationFile = getConfigurationLocator().getFile(SchedulerConstants.CONFIGURATION_FILE_NAME);
        FileReader fileReader = new FileReader(configurationFile);
        logger.info("Reading task configuration from: " + configurationFile.getAbsolutePath());
        return new InputSource(fileReader);
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

    public List<MifosTask> getTasks() {
        return tasks;
    }

}
