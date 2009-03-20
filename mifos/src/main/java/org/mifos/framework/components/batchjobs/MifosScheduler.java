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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mifos.core.ClasspathResource;
import org.mifos.framework.util.DateTimeService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class MifosScheduler extends Timer {
	
	Timer timer = null;
	
	List<String> taskNames = new ArrayList<String>();

	public MifosScheduler() {
		timer = new Timer();
	}
	
	/**
	 * This method schedules a specified task to run at a specified time afer a
	 * specified delay
	 */
	public void schedule(MifosTask task, Date initial, long delay) {
		timer.schedule(task, initial, delay);
		taskNames.add(task.name);
	}

	/**
	 * This method reads all the task from a xml file and registers them with
	 * the MifosScheduler"
	 */
	public void registerTasks() throws Exception {
		MifosTask mifosTask;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(ClasspathResource.getURI(
				SchedulerConstants.PATH).toString());
		NodeList rootSchedulerTasks = document
				.getElementsByTagName(SchedulerConstants.SCHEDULER_TASKS);
		Element rootNodeName = (Element) rootSchedulerTasks.item(0);
		NodeList collectionOfScheduledTasks = rootNodeName
				.getElementsByTagName(SchedulerConstants.SCHEDULER);
		for (int i = 0; i < collectionOfScheduledTasks.getLength(); i++) {
			Element scheduledTask = (Element) collectionOfScheduledTasks
					.item(i);
			Element subNodeName1 = (Element) scheduledTask
					.getElementsByTagName(SchedulerConstants.TASK_CLASS_NAME)
					.item(0);
			String taskName = ((Text) subNodeName1.getFirstChild()).getData()
					.trim();
			Element subNodeName2 = (Element) scheduledTask
					.getElementsByTagName(SchedulerConstants.INITIAL_TIME).item(
							0);
			String initialTime = ((Text) subNodeName2.getFirstChild())
					.getData().trim();
			Element subNodeName3 = null;
			String delayTime = null;
			if ((scheduledTask
					.getElementsByTagName(SchedulerConstants.DELAY_TIME)) != null) {
				subNodeName3 = (Element) scheduledTask.getElementsByTagName(
						SchedulerConstants.DELAY_TIME).item(0);
				if (subNodeName3.getFirstChild() != null) {
					delayTime = ((Text) subNodeName3.getFirstChild()).getData()
							.trim();
				}
			}
			mifosTask = (MifosTask) Class.forName(
					"org.mifos.framework.components.batchjobs.helpers."
							.concat(taskName)).newInstance();
			mifosTask.name = taskName;
			if (delayTime != null) {
				if (Long.parseLong(delayTime) < 86400) {
					throw new IllegalArgumentException(
							"Please specify the delay time >= 86400(1 day)");
				}
				schedule(mifosTask, parseInitialTime(initialTime), Long
						.parseLong(delayTime) * 1000);
			} else {
				schedule(mifosTask, parseInitialTime(initialTime));
			}
		}
	}

	/**
	 * These are the non-reqular jobs which do not recure but have to be run
	 * infrequently on user request. Hence there is no delay input. An example:
	 * Change in center meeting schedule which needs to change all inherited
	 * meetings as well as reschedule loans, these tasks typically require a few
	 * input params as well.
	 */
	public void schedule(MifosTask task, Date initial) {
		timer.schedule(task, initial);
		taskNames.add(task.name);
	}

	/**
	 * This is a helper method that parses the initialtime string and returns a
	 * valid Date time.
	 */
	public Date parseInitialTime(String initialTime) {
		int firstIndex = initialTime.indexOf(":");
		int lastIndex = initialTime.indexOf(":", firstIndex);
		Calendar time = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
		int hourOfTheDay = Integer.parseInt(initialTime
				.substring(0, firstIndex));
		int minutes = Integer.parseInt(initialTime.substring(firstIndex + 1,
				lastIndex + firstIndex + 1));
		int seconds = Integer.parseInt(initialTime.substring(lastIndex
				+ firstIndex + 2, initialTime.length()));
		time.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
		time.set(Calendar.MINUTE, minutes);
		time.set(Calendar.SECOND, seconds);
		return time.getTime();
	}
	
	public List<String> getTaskNames() {
		return taskNames;
	}

}
