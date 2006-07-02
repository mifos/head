/**

 * TaskHelper.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

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



import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.components.cronjobs.valueobjects.Task;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;


/**
 * @author krishankg
 *
 */
public abstract class TaskHelper extends DAO {

	public MifosTask mifosTask;
	long timeInMillis=0;

	public TaskHelper() {
	}

	/**
	 * This method is responsible for inserting a row with the task name in the database.
	 * In cases that the task fails, the next day's task will not run till the
	 *  completion of the previous day's task.
	 */
	public void registerStartup(long timeInMillis) {

		Session session=null;
	    try{
			session = HibernateUtil.getSession();
			Transaction txn=session.beginTransaction();
			Task task=new Task();
			task.setDescription(SchedulerConstants.START);
			task.setTask(mifosTask.name);
			if(timeInMillis==0){
				task.setTime(new Timestamp(System.currentTimeMillis()));
			}else{
				task.setTime(new Timestamp(timeInMillis));
			}
			session.save(task);
			txn.commit();
		}catch(HibernateProcessException e){
			e.printStackTrace();
		}
		finally
		{
			try{
			    HibernateUtil.closeSession(session);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is responsible for inserting a row with the task name in the database,
	 * at end of task completion. In cases where the task fails, the next day's
	 *  task will not run till the completion of th previous day's task.
	 */
	public void registerCompletion(long timeInMillis) {
		Session session=null;
	    try{
			session = HibernateUtil.getSession();
			Transaction txn=session.beginTransaction();
			Task task=new Task();
			task.setDescription(SchedulerConstants.FINISHEDSUCCESSFULLY);
			task.setTask(mifosTask.name);
			if(timeInMillis==0){
				task.setTime(new Timestamp(System.currentTimeMillis()));
			}else{
				task.setTime(new Timestamp(timeInMillis));
			}
			session.save(task);
			txn.commit();
		}catch(HibernateProcessException e){
			e.printStackTrace();
		}
		finally
		{
			try{
			    HibernateUtil.closeSession(session);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}


	/**
	 * This method is called by run method of Mifostask. This calls registerStartUP,istaksAllowedToRun,
	 * execute and registerCompletion in the order. The class also ensures that
	 * no exception is thrown up.
	 */
	public void executeTask(MifosTask mifosTask) {
		this.mifosTask=mifosTask;
		if (!isTaskAllowedToRun()){
			while((System.currentTimeMillis()-timeInMillis)/(1000*60*60*24)!=1){
				timeInMillis=timeInMillis+(1000*60*60*24);
				registerStartup(timeInMillis);
				execute(timeInMillis);
				registerCompletion(timeInMillis);
			}
		}else{
			if(timeInMillis==0){
				timeInMillis=System.currentTimeMillis();
			}
			registerStartup(timeInMillis);
			execute(timeInMillis);
			registerCompletion(timeInMillis);
		}
	}

	/**
     * This methods, performs the job specific to each task.
	 */
	public abstract void execute(long timeInMillis) ;

	/**
	 * This method determines if the task is allowed to run the nextday.if the
	 * previous day's task has failed, the default mplementation suspends the current day's task and
	 *  runs the previous days task.
	 *
	 *  Override this method and return true, if it is not mandatory that task should run daily
	 *  i.e. In case yesterday's task has failed, you want it to continue running current days task.
	 *
	 */
	public boolean isTaskAllowedToRun() {
		Session session=null;
		Transaction txn=null;
		String hqlSelect=null;
		Query query=null;
		try{
			session = HibernateUtil.getSession();
			txn=session.beginTransaction();
			hqlSelect = "select max(t.time) from Task t where t.task=:taskName and t.description=:finishedSuccessfully";
			query= session.createQuery(hqlSelect);
			query.setString("taskName",mifosTask.name);
			query.setString("finishedSuccessfully","Finished Successfully");
			if(query.uniqueResult()==null){//When schedular starts for the first time
				timeInMillis=System.currentTimeMillis();
			    return true;
			}else{
				timeInMillis=((Timestamp)query.uniqueResult()).getTime();
			}
			txn.commit();
			if((System.currentTimeMillis()-timeInMillis)/(1000*60*60*24)<=1){
				timeInMillis=System.currentTimeMillis();
				return true;
			}
		}
		catch(HibernateProcessException hpe){
			hpe.printStackTrace();
		}catch(HibernateException he){
			he.printStackTrace();
		}catch(IllegalArgumentException iae){
			iae.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try{
			    HibernateUtil.closeSession(session);
			}catch(HibernateProcessException hpe){
				hpe.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}

}
