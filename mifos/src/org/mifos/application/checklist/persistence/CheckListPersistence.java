package org.mifos.application.checklist.persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class CheckListPersistence extends MasterPersistence {
	
	public CheckListPersistence(){		
	}
	
	public void save(CheckListBO checkListBO) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.save(checkListBO);
		transaction.commit();
	}
	
	public CheckListBO get(Short checkListId)
	{
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		CheckListBO checkListBO = (CheckListBO) session.get(CheckListBO.class,checkListId);
		transaction.commit();
		return checkListBO;
	}
	
	public void delete(CheckListBO checkListBO)
	{
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.delete(checkListBO);
		transaction.commit();		
	}
	
	public void update(CheckListBO checkListBO) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.update(checkListBO);
		transaction.commit();
	}
	
}
