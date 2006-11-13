package org.mifos.application.personnel.persistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelNotesEntity;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonnelPersistence extends MifosTestCase {
	
	UserContext userContext;

	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO client;

	private CustomerBO group;
	
	private OfficeBO office;

	private OfficeBO branchOffice;

	private OfficeBO createdBranchOffice;
	
	PersonnelBO personnel;
	
	Name name;

	PersonnelPersistence persistence;

	@Override
	public void setUp() throws Exception{
		persistence = new PersonnelPersistence();
	}

	@Override
	public void tearDown() throws Exception {
		userContext = null;
		office = null;
		branchOffice = null;
		name = null;
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(personnel);
		TestObjectFactory.cleanUp(createdBranchOffice);
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

	public void testIsUserExistSucess() throws Exception{
		assertTrue(persistence.isUserExist("mifos"));
	}

	public void testIsUserExistFailure() throws Exception{
		assertFalse(persistence.isUserExist("XXX"));
	}

	public void testIsUserExistWithGovernmentIdSucess()throws Exception {
		assertTrue(persistence.isUserExistWithGovernmentId("123"));
	}

	public void testIsUserExistWithGovernmentIdFailure()throws Exception {
		assertFalse(persistence.isUserExistWithGovernmentId("XXX"));
	}

	public void testIsUserExistWithDobAndDisplayNameSucess() throws Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertTrue(persistence.isUserExist("mifos", dateFormat
				.parse("1979-12-12")));
	}

	public void testIsUserExistWithDobAndDisplayNameFailure() throws Exception{
		assertFalse(persistence.isUserExist("mifos", new GregorianCalendar(
				1989, 12, 12, 0, 0, 0).getTime()));
	}
	
	public void testActiveCustomerUnderLO() throws Exception{
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
	
	public void testGetAllPersonnelNotes() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		office = TestObjectFactory.getOffice(Short.valueOf("1"));
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		HibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		assertEquals(branchOffice.getOfficeId(), personnel.getOffice()
				.getOfficeId());
		createInitialObjects(branchOffice.getOfficeId(), personnel
				.getPersonnelId());

		PersonnelNotesEntity personnelNotes = new PersonnelNotesEntity(
				"Personnel notes created", new PersonnelPersistence()
						.getPersonnel(userContext.getId()), personnel);
		personnel.addNotes(userContext.getId(), personnelNotes);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		assertEquals(1,persistence.getAllPersonnelNotes(personnel.getPersonnelId()).getSize());
	}
	
	public void testSuccessfullGetPersonnel() throws Exception {
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		String oldUserName = personnel.getUserName();
		personnel = persistence.getPersonnel(personnel.getUserName());
		assertEquals(oldUserName,personnel.getUserName());
	}
	
	public void testGetPersonnelByGlobalPersonnelNum()throws Exception{
		assertNotNull(persistence.getPersonnelByGlobalPersonnelNum("1"));
	}
	
	public void testGetPersonnelRoleCount() throws Exception{
		Integer count = persistence.getPersonnelRoleCount((short)2);
		assertEquals(0,(int)count);
		count = persistence.getPersonnelRoleCount((short)1);
		assertEquals(3,(int)count);
	}
	
	
	public  void testSearch()throws Exception{
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		QueryResult queryResult = persistence.search(personnel.getUserName(),Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
	}
	public  void testSearchFirstNameAndLastName()throws Exception{
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		personnel = createPersonnelWithName(branchOffice, PersonnelLevel.LOAN_OFFICER,new Name("Rajender",null,"singh","saini"));
		QueryResult queryResult = persistence.search(personnel.getPersonnelDetails().getName().getFirstName()+" "+personnel.getPersonnelDetails().getName().getLastName(),Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
	}	
	public void  testGetActiveLoUnderUser()throws Exception{
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		List<PersonnelBO> loanOfficers = persistence.getActiveLoUnderUser(branchOffice.getOfficeId());
		assertNotNull(loanOfficers);
		assertEquals(2,loanOfficers.size());
	  assertEquals("loan officer",loanOfficers.get(0).getDisplayName());
	  assertEquals("XYZ",loanOfficers.get(1).getDisplayName());	
	}
	
	
	public void testGetSupportedLocale()throws Exception{
		//asserting only on not null as suppored locales can be added by user 
		assertNotNull(persistence.getSupportedLocales());
	}
	private void createInitialObjects(Short officeId, Short personnelId) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));

		center = TestObjectFactory.createCenter("Center", meeting, officeId, personnelId);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private PersonnelBO createPersonnel(OfficeBO office,
			PersonnelLevel personnelLevel) throws Exception {
		UserContext userContext = TestObjectFactory.getUserContext();
		name = new Name("XYZ", null, null, null);
		return create(personnelLevel,name,userContext.getId(), office);
	}
	private PersonnelBO createPersonnelWithName(OfficeBO office,
			PersonnelLevel personnelLevel,Name personnelName) throws Exception {
		UserContext userContext = TestObjectFactory.getUserContext();
		return create(personnelLevel,personnelName,userContext.getId(),office);
	}
  private 	PersonnelBO create(PersonnelLevel personnelLevel,Name name,Short createdBy,OfficeBO office)throws Exception{
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		personnel = new PersonnelBO(personnelLevel, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", null, customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, createdBy);
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
  }
	
	private void createCustomers(Short centerStatus, CustomerStatus groupStatus, Short clientStatus){
		meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("group", groupStatus, center);
		client = TestObjectFactory.createClient("client",clientStatus,group,new Date());
	}
	
	
}
