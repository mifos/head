package org.mifos.framework.components.cronjobs.persistence;

import org.mifos.framework.components.cronjobs.business.Task;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

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
}
