/**

 * MifosScheduler.java    version: 1.0



 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.framework.components.cronjobs;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.IOException;
import java.net.URISyntaxException;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.exceptions.XMLReaderException;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * @author krishankg
 *
 */
public class MifosScheduler extends Timer {

	Timer timer=null;
	public MifosScheduler() {
		timer=new Timer();
	}

	/**
	 * This method schedules a specified task to run at a specified time afer a specified delay
	 */
	public void schedule(MifosTask task, Date initial, long delay) {
		try{
			timer.schedule(task,initial,delay);
		}catch(IllegalArgumentException iAE){
			iAE.printStackTrace();
		}catch(IllegalStateException iSE){
			iSE.printStackTrace();
		}
	}


	/**
	 * This method reads all the task from a xml file and registers them with the MifosScheduler"
	 */
	public void registerTasks() {
		try {
			MifosTask mifosTask;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder
					.parse(ResourceLoader.getURI(SchedulerConstants.PATH).toString());

			NodeList rootSchedulerTasks = document.getElementsByTagName(SchedulerConstants.SCHEDULERTASKS);
			Element rootNodeName = (Element) rootSchedulerTasks.item(0);
			NodeList collectionOfScheduledTasks= rootNodeName.getElementsByTagName(SchedulerConstants.SCHEDULER);
			for(int i=0;i<collectionOfScheduledTasks.getLength();i++){
				Element scheduledTask = (Element) collectionOfScheduledTasks.item(i);

				Element subNodeName1 = (Element) scheduledTask.getElementsByTagName(SchedulerConstants.TASKCLASSNAME).item(0);
				String taskName = ((Text) subNodeName1.getFirstChild()).getData().trim();


				Element subNodeName2 = (Element) scheduledTask.getElementsByTagName(SchedulerConstants.INITIALTIME).item(0);
				String initialTime = ((Text) subNodeName2.getFirstChild()).getData().trim();


				Element subNodeName3=null;
				String delayTime=null;

				if((scheduledTask.getElementsByTagName(SchedulerConstants.DELAYTIME))!=null){
					subNodeName3 = (Element) scheduledTask.getElementsByTagName(SchedulerConstants.DELAYTIME).item(0);
					if(subNodeName3.getFirstChild()!=null){
						delayTime = ((Text) subNodeName3.getFirstChild()).getData().trim();
					}
				}

				try {
					mifosTask= (MifosTask) Class.forName("org.mifos.framework.components.cronjobs.helpers.".concat(taskName)).newInstance();
	                mifosTask.name=taskName;

	                if(delayTime!=null){
	                	if(Long.parseLong(delayTime)<86400){
	                		throw new IllegalArgumentException("Please specify the delay time >= 86400(1 day)");
	                	}
	                	 schedule(mifosTask,parseInitialTime(initialTime),Long.parseLong(delayTime)*1000);
	                }else{
	                	schedule(mifosTask,parseInitialTime(initialTime));
	                }

				}catch(IllegalArgumentException iIAE ){
					iIAE.printStackTrace();
				}catch (ClassNotFoundException cnfe) {
					cnfe.printStackTrace();
				} catch (InstantiationException ise) {
					ise.printStackTrace();
				} catch (IllegalAccessException iae) {
					iae.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (URISyntaxException use) {
			use.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * These are the non-reqular jobs which do not recure but have to be run infrequently on user request.
	 *  Hence there is no delay input. An example: Change in center meeting schedule which needs to
	 *  change all inherited meetings as  well as reschedule loans, these tasks typically require
	 *  a few input params as well.
	 */
	public void schedule(MifosTask task, Date initial) {
		try{
			timer.schedule(task,initial);
		}catch(IllegalArgumentException iAE){
			iAE.printStackTrace();
		}catch(IllegalStateException iSE){
			iSE.printStackTrace();
		}
	}

	/**
	 * This is a helper method that parses the initialtime string and returns a valid Date time.
	 */
	public Date parseInitialTime(String initialTime){
		int firstIndex=initialTime.indexOf(":");
		int lastIndex=initialTime.indexOf(":",firstIndex);

		Calendar time = Calendar.getInstance();

		int hourOfTheDay=Integer.parseInt(initialTime.substring(0,firstIndex));
		int minutes=Integer.parseInt(initialTime.substring(firstIndex+1,lastIndex+firstIndex+1));
		int seconds=Integer.parseInt(initialTime.substring(lastIndex+firstIndex+2,initialTime.length()));

		time.set(Calendar.HOUR_OF_DAY,hourOfTheDay);
		time.set(Calendar.MINUTE,minutes);
		time.set(Calendar.SECOND,seconds);

		return time.getTime();
	}


}