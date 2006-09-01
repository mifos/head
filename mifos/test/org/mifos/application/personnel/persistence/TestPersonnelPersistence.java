package org.mifos.application.personnel.persistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonnelPersistence extends MifosTestCase {

	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO client;

	private CustomerBO group;

	PersonnelPersistence persistence;

	public void setUp() {
		persistence = new PersonnelPersistence();
	}

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testActiveLoanOfficersInBranch() throws Exception {
		List<PersonnelView> personnels = persistence
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), Short.valueOf("3"),
						PersonnelConstants.LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

	public void testNonLoanOfficerInBranch() throws Exception {
		List<PersonnelView> personnels = persistence
				.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
						Short.valueOf("3"), Short.valueOf("2"),
						PersonnelConstants.NON_LOAN_OFFICER);
		assertEquals(1, personnels.size());
	}

	public void testIsUserExistSucess() {
		assertTrue(persistence.isUserExist("mifos"));
	}

	public void testIsUserExistFailure() {
		assertFalse(persistence.isUserExist("XXX"));
	}

	public void testIsUserExistWithGovernmentIdSucess() {
		assertTrue(persistence.isUserExistWithGovernmentId("123"));
	}

	public void testIsUserExistWithGovernmentIdFailure() {
		assertFalse(persistence.isUserExistWithGovernmentId("XXX"));
	}

	public void testIsUserExistWithDobAndDisplayNameSucess() throws Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertTrue(persistence.isUserExist("mifos", dateFormat
				.parse("1979-12-12")));
	}

	public void testIsUserExistWithDobAndDisplayNameFailure() {
		assertFalse(persistence.isUserExist("mifos", new GregorianCalendar(
				1989, 12, 12, 0, 0, 0).getTime()));
	}
	
	public void testActiveCustomerUnderLO() {
		createCustomers(CustomerStatus.CENTER_ACTIVE.getValue() ,CustomerStatus.GROUP_ACTIVE,CustomerStatus.CLIENT_ACTIVE.getValue());
		assertTrue(persistence.getActiveChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
	}
	
	public void testClosedCustomerUnderLO() throws Exception{
		createCustomers(CustomerStatus.CENTER_ACTIVE.getValue() ,CustomerStatus.GROUP_CLOSED ,CustomerStatus.CLIENT_CANCELLED.getValue());
		center.changeStatus(CustomerStatus.CENTER_INACTIVE.getValue(),null,"check inactive");
		center.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		assertTrue(!persistence.getActiveChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
	}
	
	public void testGetAllCustomerUnderLO() throws Exception{
		createCustomers(CustomerStatus.CENTER_ACTIVE.getValue() ,CustomerStatus.GROUP_CLOSED ,CustomerStatus.CLIENT_CANCELLED.getValue());
		assertTrue(persistence.getAllChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
		center.changeStatus(CustomerStatus.CENTER_INACTIVE.getValue(),null,"check inactive");
		center.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		assertTrue(persistence.getAllChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
	}
	
	private void createCustomers(Short centerStatus, CustomerStatus groupStatus, Short clientStatus){
		meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", centerStatus, "1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroupUnderCenter("group", groupStatus, center);
		client = TestObjectFactory.createClient("client",clientStatus,group,new Date());
	}
}
