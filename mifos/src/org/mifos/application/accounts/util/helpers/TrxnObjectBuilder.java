package org.mifos.application.accounts.util.helpers;

import org.hibernate.Session;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountPayment;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

//import test.HibernateUtil; 

public abstract class TrxnObjectBuilder {
	
	protected Session session = null; 
	private void initSession() throws SystemException{
		
		try {
			session = HibernateUtil.getSession();
		} catch (HibernateProcessException e) {
			//TODO remove the print stack
			e.printStackTrace();
			throw new SystemException(e);
		}
	}	
	
	private void closeSession() throws SystemException{
		session.close();
	}
	
	public final AccountPayment build(AccountActionDate acctDate, short personnelId) throws SystemException, ApplicationException{
		initSession();
		AccountPayment pmnt  = buildSpecific(acctDate, personnelId);
		closeSession();
		return pmnt;
	}
	
	public abstract AccountPayment buildSpecific(AccountActionDate date, short personnelId);

}
