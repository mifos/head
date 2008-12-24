package org.mifos.framework.components.batchjobs.persistence;

import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.helpers.TaskStatus;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.DateUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TaskPersistence extends Persistence {

	public void saveAndCommitTask(Task task) throws PersistenceException {
		createOrUpdate(task);
		try {
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			throw new PersistenceException(e);
		} finally {
			HibernateUtil.closeSession();
		}
	}
	
	private Task getSuccessfulTask(Integer taskId) throws PersistenceException
	{
		Task task = null;
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("taskId", taskId);
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.SCHEDULED_TASK_GET_SUCCESSFUL_TASK,
				queryParameters);
		if (queryResult != null)
		{
			task = (Task)queryResult;
		}
		return task;
	}
	
	public boolean hasLoanArrearsTaskRunSuccessfully() throws PersistenceException
	{
		boolean result = false;
		String taskName ="LoanArrearsTask";
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("taskName", taskName);
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.SCHEDULED_TASK_GET_LATEST_TASK,
				queryParameters);
		if (queryResult != null)
		{
			Integer taskId = (Integer)queryResult;
			Task task = getSuccessfulTask(taskId);
			if (task != null)
			{
				Timestamp taskDate = task.getEndTime();
				Date dbDate = DateUtils.getDateWithoutTimeStamp(taskDate.getTime());
				Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
				if (dbDate.equals(currentDate))
				{
					result = true;
				}
			}
		}
		
		return result;
	}
}
