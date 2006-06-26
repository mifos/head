package org.mifos.framework.components.cronjob.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.cronjobs.helpers.ApplyCustomerFeeHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerFeeHelper extends TestCase{	
	
	private CustomerBO center;	 
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() {		
		TestObjectFactory.cleanUp(center);		
		HibernateUtil.closeSession();

	}
	public void testExecute() throws Exception{		
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.4", meeting, new Date(System.currentTimeMillis()));		
		Set<AccountFeesEntity> accountFeeSet = center.getCustomerAccount().getAccountFees();
		Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.add(calendar.WEEK_OF_MONTH,-1);
        Date lastAppliedFeeDate = new Date(calendar.getTimeInMillis());
		assertEquals(1,accountFeeSet.size());	
		for (Iterator iter = accountFeeSet.iterator(); iter.hasNext();) {
			AccountFeesEntity accountFeesEntity = (AccountFeesEntity) iter.next();
			accountFeesEntity.setLastAppliedDate(lastAppliedFeeDate);			
		}
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
		ApplyCustomerFeeHelper customerFeeHelper = new ApplyCustomerFeeHelper();
		customerFeeHelper.execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		Set<AccountFeesEntity> periodicFeeSet = center.getCustomerAccount().getAccountFees();
		for (Iterator iter = periodicFeeSet.iterator(); iter.hasNext();) {
			AccountFeesEntity element = (AccountFeesEntity) iter.next();		
			Calendar modifiedDate = new GregorianCalendar();
			modifiedDate.setTime(element.getLastAppliedDate());
			int year = modifiedDate.get(Calendar.YEAR);
			int month = modifiedDate.get(Calendar.MONTH);
			int day = modifiedDate.get(Calendar.DAY_OF_MONTH);			
			modifiedDate = new GregorianCalendar(year, month, day);
			
			Date cDate = new Date(System.currentTimeMillis());			
			Calendar currentDate = new GregorianCalendar();
			currentDate.setTime(cDate);
			int year1 = currentDate.get(Calendar.YEAR);
			int month1 = currentDate.get(Calendar.MONTH);
			int day1 = currentDate.get(Calendar.DAY_OF_MONTH);
			currentDate = new GregorianCalendar(year1, month1, day1);
			assertEquals(currentDate,modifiedDate);
		}
		
	}
}
