package org.mifos.application.customer.client.business;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestClientBO extends MifosTestCase {

	private CustomerBO center;

	private CustomerBO group;

	private ClientBO client;

	private CustomerPersistence customerPersistence = new CustomerPersistence();
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		super.tearDown();
	}

	public void testAddClientAttendance() {
		createInitialObjects();
		Date meetingDate = getCurrentDateWithoutTIme();
		client.addClientAttendance(getClientAttendance(meetingDate));
		customerPersistence.createOrUpdate(client);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
	}

	public void testGetClientAttendanceForMeeting() {
		createInitialObjects();
		Date meetingDate = getCurrentDateWithoutTIme();
		client.addClientAttendance(getClientAttendance(meetingDate));
		customerPersistence.createOrUpdate(client);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
	}

	public void testHandleAttendance() throws NumberFormatException,
			ServiceException {
		createInitialObjects();
		Date meetingDate = getCurrentDateWithoutTIme();
		client.handleAttendance(meetingDate, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
		client.handleAttendance(meetingDate, Short.valueOf("2"));
		HibernateUtil.commitTransaction();
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("2"));
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
	}

	public void testHandleAttendanceForDifferentDates()
			throws NumberFormatException, ServiceException {
		createInitialObjects();
		Date meetingDate = getCurrentDateWithoutTIme();
		client.handleAttendance(meetingDate, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
		HibernateUtil.closeSession();
		Date offSetDate = getDateOffset(1);
		client.handleAttendance(offSetDate, Short.valueOf("2"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) TestObjectFactory.getObject(ClientBO.class, client
				.getCustomerId());
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 2);
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendance(), Short.valueOf("1"));
		assertEquals("The value of customer attendance for the meeting : ",
				client.getClientAttendanceForMeeting(offSetDate)
						.getAttendance(), Short.valueOf("2"));

	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
	}

	private ClientAttendanceBO getClientAttendance(Date meetingDate) {
		ClientAttendanceBO clientAttendance = new ClientAttendanceBO();
		clientAttendance.setAttendance(Short.valueOf("1"));
		clientAttendance.setMeetingDate(meetingDate);
		return clientAttendance;
	}

	private Date getDateOffset(int numberOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month,
				(day - numberOfDays));
		return new Date(currentDateCalendar.getTimeInMillis());
	}

	private Date getCurrentDateWithoutTIme() {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day);
		return new Date(currentDateCalendar.getTimeInMillis());
	}
}
