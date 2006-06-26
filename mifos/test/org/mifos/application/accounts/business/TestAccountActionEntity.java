package org.mifos.application.accounts.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountActionEntity extends TestCase {	
	
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
	
	public void testIsApplicable() throws RepaymentScheduleException, SchedulerException{
		accountPersistence = new AccountPersistence();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1,1,4,2));
        center=TestObjectFactory.createCenter("Center",Short.valueOf("13"),"1.1",meeting,new Date(System.currentTimeMillis()));
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(calendar.WEEK_OF_MONTH,-1);
        Date lastAppliedFeeDate = new Date(calendar.getTimeInMillis());
        group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));       		
		Set<AccountFeesEntity> accountFeeSet = group.getCustomerAccount().getAccountFees();
		assertEquals(1,accountFeeSet.size());
		for (Iterator iter = accountFeeSet.iterator(); iter.hasNext();) {
			AccountFeesEntity accountFeesEntity = (AccountFeesEntity) iter.next();
			accountFeesEntity.setLastAppliedDate(lastAppliedFeeDate);
			assertEquals(true,accountFeesEntity.isApplicable(System.currentTimeMillis()));			
		}
	}
}
