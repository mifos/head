package org.mifos.application.accounts.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import org.mifos.framework.MifosTestCase;

import org.hibernate.Session;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountActionEntity extends MifosTestCase {	
	
	private Session session;
	private AccountActionEntity accountActionEntity;
	private CustomerBO center=null;
	private CustomerBO group=null;
	private AccountPersistence accountPersistence;

	protected void setUp() throws Exception {
		session = HibernateUtil.getSessionTL();
	}

	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		session=null;
	}

	public void testGetAccountAction(){
		accountActionEntity=getAccountActionEntityObject(Short.valueOf("1"));
		assertEquals("Loan Repayment",accountActionEntity.getName(Short.valueOf("1")));
	}

	private AccountActionEntity getAccountActionEntityObject(Short id) {
		return (AccountActionEntity)session.get(AccountActionEntity.class,id);
	}
	
}
