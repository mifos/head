package org.mifos.application.customer.center.persistence;

import java.util.GregorianCalendar;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCenterPersistence extends MifosTestCase{
	private CustomerBO center;
	
	@Override
	public void tearDown() {
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}
	
	public void testIsCenterExists_true()throws Exception{
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		HibernateUtil.closeSession();
		assertTrue(new CenterPersistence().isCenterExists(centerName));
	}
	
	public void testIsCenterExists_false() throws PersistenceException{
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		HibernateUtil.closeSession();
		assertFalse(new CenterPersistence().isCenterExists("NewCenter11"));
	}
	
	public void testGetCenter()throws Exception{
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		HibernateUtil.closeSession();
		center = new CenterPersistence().getCenter(center.getCustomerId());
		assertEquals(centerName, center.getDisplayName());
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
	public void testSearch() throws Exception{
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
	   QueryResult queryResult=	new CenterPersistence().search(center.getDisplayName(),Short.valueOf("1"));
	   assertNotNull(queryResult);
	   assertEquals(1,queryResult.getSize());
	   assertEquals(1,queryResult.get(0,10).size());
	}
}
