package org.mifos.application.personnel.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonnelBO extends MifosTestCase {

	UserContext userContext;

	private OfficeBO office;

	private OfficeBO branchOffice;

	private OfficeBO createdBranchOffice;

	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private MeetingBO meeting;

	Name name;

	PersonnelBO personnel;

	@Override
	protected void setUp() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		office = TestObjectFactory.getOffice(Short.valueOf("1"));
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		name = new Name("XYZ", null, null, null);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
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

	public void testCreateFailureWithNullName() {
		try {

			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer
					.valueOf("1"), Short.valueOf("1"), "ABCD", null, null,
					null, null, name, null, null, null, null, null, null, null,
					userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.ERRORMANDATORY, e.getKey());
		}
	}

	public void testCreateFailureWithDuplicateName() {
		try {

			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer
					.valueOf("1"), Short.valueOf("1"), "ABCD", "mifos", null,
					null, null, name, null, null, null, null, null, null, null,
					userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.DUPLICATE_USER, e.getKey());
		}
	}

	public void testCreateFailureWithDuplicateGovernMentId() {
		try {
			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer
					.valueOf("1"), Short.valueOf("1"), "ABCD", "Raj", null,
					null, null, name, "123", null, null, null, null, null,
					null, userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.DUPLICATE_GOVT_ID, e.getKey());
		}
	}

	public void testCreateFailureWithDuplicateDisplayNameAndDOB()
			throws Exception {
		try {

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Name name1 = new Name("mifos", null, null, null);
			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office, Integer
					.valueOf("1"), Short.valueOf("1"), "ABCD", "RAJ", null,
					null, null, name1, null, dateFormat.parse("1979-12-12"),
					null, null, null, null, null, userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.DUPLICATE_USER_NAME_OR_DOB, e
					.getKey());
		}
	}

	public void testGetDateSucess()throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("1"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		PersonnelBO personnel = new PersonnelBO(
				PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"),
				Short.valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com",
				((PersonnelBusinessService) ServiceFactory.getInstance()
						.getBusinessService(BusinessServiceName.Personnel))
						.getRoles(), customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, userContext.getId());
		assertEquals("0" ,personnel.getAge());

	}
	
	public void testGetDateFailure()throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("1"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		PersonnelBO personnel = new PersonnelBO(
				PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"),
				Short.valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com",
				((PersonnelBusinessService) ServiceFactory.getInstance()
						.getBusinessService(BusinessServiceName.Personnel))
						.getRoles(), customFieldView, name, "111111", null,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, userContext.getId());
		assertEquals("" ,personnel.getAge());

	}

	public void testSaveFailure() throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("1"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		
		PersonnelBO personnel = new PersonnelBO(
				PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"),
				Short.valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com",
				((PersonnelBusinessService) ServiceFactory.getInstance()
						.getBusinessService(BusinessServiceName.Personnel))
						.getRoles(), customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, userContext.getId());
		HibernateUtil.getSessionTL().close();
		try{
				personnel.save();
				assertEquals(true,false);
		}
		catch (PersonnelException e) {
			assertEquals(true,true);
		}
	}
	public void testCreateSucess() throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("1"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();

		PersonnelBO personnel = new PersonnelBO(
				PersonnelLevel.NON_LOAN_OFFICER, office, Integer.valueOf("1"),
				Short.valueOf("1"), "ABCD", "RAJ", "rajendersaini@yahoo.com",
				((PersonnelBusinessService) ServiceFactory.getInstance()
						.getBusinessService(BusinessServiceName.Personnel))
						.getRoles(), customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, userContext.getId());
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeandFlushSession();
		PersonnelBO personnelSaved = (PersonnelBO) HibernateUtil.getSessionTL()
				.get(PersonnelBO.class, personnel.getPersonnelId());
		assertEquals("RAJ", personnelSaved.getUserName());
		assertEquals("rajendersaini@yahoo.com", personnelSaved.getEmailId());
		assertEquals("XYZ", personnelSaved.getPersonnelDetails().getName()
				.getFirstName());
		assertEquals(generateGlobalPersonnelNum(office.getGlobalOfficeNum(),
				personnel.getPersonnelId()), personnelSaved
				.getGlobalPersonnelNum());
		assertEquals(PersonnelLevel.NON_LOAN_OFFICER.getValue(), personnelSaved
				.getLevel().getId());
		assertEquals(1, personnelSaved.getLevel().getParent().getId()
				.intValue());
		assertFalse(personnelSaved.getLevel().isInteractionFlag());
		assertEquals(null, personnelSaved.getMaxChildCount());
		assertEquals(office.getOfficeId(), personnelSaved.getOffice()
				.getOfficeId());
		assertFalse(personnelSaved.isPasswordChanged());
		assertEquals(1, personnelSaved.getPreferredLocale().getLocaleId()
				.intValue());
		assertEquals(1, personnelSaved.getTitle().intValue());
		assertEquals("XYZ", personnelSaved.getDisplayName());
		assertFalse(personnelSaved.isLocked());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		assertEquals(dateFormat.parse(dateFormat.format(date)), personnelSaved
				.getPersonnelDetails().getDob());
		assertEquals("XYZ", personnelSaved.getPersonnelDetails()
				.getDisplayName());
		assertEquals(personnel.getPersonnelId(), personnelSaved
				.getPersonnelDetails().getPersonnel().getPersonnelId());
		for (PersonnelCustomFieldEntity personnelCustomField : personnelSaved
				.getCustomFields()) {
			assertEquals("123456", personnelCustomField.getFieldValue());
			assertEquals(1, personnelCustomField.getFieldId().intValue());
		}

		TestObjectFactory.cleanUp(personnelSaved);

	}

	public void testUpdateFailureForBranchTransferWithActiveCustomer()
			throws Exception {

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
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		try {
			personnel.update(PersonnelStatus.ACTIVE,
					PersonnelLevel.LOAN_OFFICER, createdBranchOffice, Integer
							.valueOf("1"), Short.valueOf("1"), "ABCD",
					"rajendersaini@yahoo.com", null, getCustomFields(), name,
					Integer.valueOf("1"), Integer.valueOf("1"), getAddress(),
					Short.valueOf("1"));
			assertFalse(true);
		} catch (PersonnelException pe) {
			personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
					PersonnelBO.class, personnel.getPersonnelId());
			createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
					OfficeBO.class, createdBranchOffice.getOfficeId());
			assertTrue(true);
			assertEquals(pe.getKey(),
					PersonnelConstants.TRANSFER_NOT_POSSIBLE_EXCEPTION);
		}
	}

	public void testUpdateFailureForBranchTransferWithLoanOfficerInNonBranch()
			throws Exception {

		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		assertEquals(branchOffice.getOfficeId(), personnel.getOffice()
				.getOfficeId());
		createInitialObjects(branchOffice.getOfficeId(), personnel
				.getPersonnelId());
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		try {
			personnel.update(PersonnelStatus.ACTIVE,
					PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"),
					Short.valueOf("1"), "ABCD", "rajendersaini@yahoo.com",
					null, getCustomFields(), name, Integer.valueOf("1"),
					Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
			assertFalse(true);
		} catch (PersonnelException pe) {
			assertTrue(true);
			assertEquals(pe.getKey(), PersonnelConstants.LO_ONLY_IN_BRANCHES);
		}
	}

	public void testUpdateFailureForUserHierarchyChangeWithLoanOfficerInNonBranch()
			throws Exception {

		createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
		try {
			personnel.update(PersonnelStatus.ACTIVE,
					PersonnelLevel.LOAN_OFFICER, office, Integer.valueOf("1"),
					Short.valueOf("1"), "ABCD", "rajendersaini@yahoo.com",
					null, getCustomFields(), name, Integer.valueOf("1"),
					Integer.valueOf("1"), getAddress(), Short.valueOf("1"));
			assertFalse(true);
		} catch (PersonnelException pe) {
			assertTrue(true);
			assertEquals(pe.getKey(), PersonnelConstants.LO_ONLY_IN_BRANCHES);
		}
	}

	public void testUpdateFailureForUserHierarchyChangeWithCustomersPresentForLoanOfficer()
			throws Exception {

		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		createInitialObjects(branchOffice.getOfficeId(), personnel
				.getPersonnelId());
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		try {
			personnel.update(PersonnelStatus.ACTIVE,
					PersonnelLevel.NON_LOAN_OFFICER, office, Integer
							.valueOf("1"), Short.valueOf("1"), "ABCD",
					"rajendersaini@yahoo.com", null, getCustomFields(), name,
					Integer.valueOf("1"), Integer.valueOf("1"), getAddress(),
					Short.valueOf("1"));
			assertFalse(true);
		} catch (PersonnelException pe) {
			assertTrue(true);
			assertEquals(pe.getKey(),
					PersonnelConstants.HIERARCHY_CHANGE_EXCEPTION);
		}
	}

	public void testUpdateFailureForStatusChangeWithCustomersPresentForLoanOfficer()
			throws Exception {

		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		createInitialObjects(branchOffice.getOfficeId(), personnel
				.getPersonnelId());
		client = (ClientBO) HibernateUtil.getSessionTL().get(ClientBO.class,
				client.getCustomerId());
		group = (GroupBO) HibernateUtil.getSessionTL().get(GroupBO.class,
				group.getCustomerId());
		center = (CenterBO) HibernateUtil.getSessionTL().get(CenterBO.class,
				center.getCustomerId());
		try {
			personnel.update(PersonnelStatus.INACTIVE,
					PersonnelLevel.LOAN_OFFICER, branchOffice, Integer
							.valueOf("1"), Short.valueOf("1"), "ABCD",
					"rajendersaini@yahoo.com", null, getCustomFields(), name,
					Integer.valueOf("1"), Integer.valueOf("1"), getAddress(),
					Short.valueOf("1"));
			assertFalse(true);
		} catch (PersonnelException pe) {
			assertTrue(true);
			assertEquals(pe.getKey(),
					PersonnelConstants.STATUS_CHANGE_EXCEPTION);
		}
	}

	public void testUpdateSucess() throws Exception {

		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		HibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
		assertEquals(office.getOfficeId(), personnel.getOffice().getOfficeId());
		assertEquals("XYZ", personnel.getPersonnelDetails().getName()
				.getFirstName());
		assertEquals(Integer.valueOf("1"), personnel.getPersonnelDetails()
				.getGender());
		assertEquals(Integer.valueOf("1"), personnel.getPersonnelDetails()
				.getMaritalStatus());
		assertEquals(PersonnelLevel.NON_LOAN_OFFICER.getValue(), personnel
				.getLevel().getId());
		assertEquals(PersonnelStatus.ACTIVE.getValue(), personnel.getStatus()
				.getId());
		assertEquals(0, personnel.getPersonnelMovements().size());
		personnel.update(PersonnelStatus.INACTIVE,
					PersonnelLevel.LOAN_OFFICER, createdBranchOffice, Integer
							.valueOf("2"), Short.valueOf("1"), "ABCD",
					"abc@yahoo.com", getNewRoles(), getCustomFields(),
					getPersonnelName(), Integer.valueOf("2"), Integer
							.valueOf("2"), getAddress(), Short.valueOf("1"));
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		assertEquals(createdBranchOffice.getOfficeId(), personnel.getOffice()
				.getOfficeId());
		assertEquals(getPersonnelName().getFirstName(), personnel
				.getPersonnelDetails().getName().getFirstName());
		assertEquals(getPersonnelName().getLastName(), personnel
				.getPersonnelDetails().getName().getLastName());
		assertEquals(getPersonnelName().getMiddleName(), personnel
				.getPersonnelDetails().getName().getMiddleName());
		assertEquals(getPersonnelName().getSecondLastName(), personnel
				.getPersonnelDetails().getName().getSecondLastName());
		assertEquals(getPersonnelName().getDisplayName() ,personnel.getDisplayName());
		assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
		assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus()
				.intValue());
		assertEquals("abc@yahoo.com", personnel.getEmailId());
		assertTrue(personnel.isPasswordChanged());
		assertEquals(2, personnel.getTitle().intValue());
		assertEquals(PersonnelLevel.LOAN_OFFICER.getValue(), personnel
				.getLevel().getId());
		assertEquals(PersonnelStatus.INACTIVE.getValue(), personnel.getStatus()
				.getId());
		assertEquals(2, personnel.getPersonnelMovements().size());
		assertEquals(1, personnel.getPersonnelRoles().size());
		
	}
	
	public void testSuccessUpdateUserSettings() throws Exception {
		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		HibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
		
		personnel.update("xyz@aditi.com",getPersonnelName(),2,2,getAddress(),Short.valueOf("1"),Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		assertEquals(getPersonnelName().getFirstName(), personnel
				.getPersonnelDetails().getName().getFirstName());
		assertEquals(getPersonnelName().getLastName(), personnel
				.getPersonnelDetails().getName().getLastName());
		assertEquals(getPersonnelName().getMiddleName(), personnel
				.getPersonnelDetails().getName().getMiddleName());
		assertEquals(getPersonnelName().getSecondLastName(), personnel
				.getPersonnelDetails().getName().getSecondLastName());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getCity());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getCountry());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine1());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine2());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getLine3());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getPhoneNumber());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getState());
		assertEquals("changed", personnel.getPersonnelDetails().getAddress().getZip());
		assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus()
				.intValue());
		assertEquals("xyz@aditi.com", personnel.getEmailId());
	}

	public void testAddNotes() throws Exception {
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
		personnel.addNotes(userContext.getId(),
				createNotes("1.Personnel notes created"));
		personnel.addNotes(userContext.getId(),
				createNotes("2.Personnel notes created"));
		personnel.addNotes(userContext.getId(),
				createNotes("3.Personnel notes created"));
		personnel.addNotes(userContext.getId(),
				createNotes("4.Personnel notes created"));
		personnel.addNotes(userContext.getId(),
				createNotes("5.Personnel notes created"));
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
		assertEquals("Size of notes should be 5", 5, personnel
				.getPersonnelNotes().size());
		assertEquals("Size of recent notes should be 3", 3, personnel
				.getRecentPersonnelNotes().size());
		for (PersonnelNotesEntity notes : personnel.getPersonnelNotes()) {
			assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), notes
					.getCommentDate());
			assertEquals("The most recent note should be the last one added",
					"5.Personnel notes created", notes.getComment());
			break;
		}
	}
	
	public void testSuccessfullLogin() throws Exception {
		personnel = createPersonnel();
		String password = "ABCD";
		userContext = personnel.login(password);
		assertTrue(true);
		assertFalse(personnel.isLocked());

		assertEquals(personnel.getPersonnelId(), userContext.getId());
		assertEquals(personnel.getDisplayName(), userContext.getName());
		assertEquals(personnel.getLevel().getId(), userContext.getLevelId());
		assertEquals(personnel.getLastLogin(), userContext.getLastLogin());
		assertEquals(personnel.getPasswordChanged(), userContext
				.getPasswordChanged());
		assertEquals(personnel.getPreferredLocale().getLocaleId(), userContext
				.getLocaleId());
		assertEquals(Configuration.getInstance().getSystemConfig()
				.getMFILocaleId(), userContext.getMfiLocaleId());
		assertEquals(personnel.getPreferredLocale().getLanguage()
				.getLanguageName(), userContext.getPereferedLocale()
				.getDisplayLanguage());
		assertEquals(personnel.getPreferredLocale().getCountry()
				.getCountryName(), userContext.getPereferedLocale()
				.getDisplayCountry());
		assertEquals(personnel.getOffice().getOfficeId(), userContext
				.getBranchId());
		assertEquals(personnel.getOffice().getGlobalOfficeNum(), userContext
				.getBranchGlobalNum());
		assertEquals(0, personnel.getNoOfTries().intValue());
		assertFalse(personnel.isPasswordChanged());
		assertEquals(getRoles(personnel).size(),userContext.getRoles().size());
	}

	public void testLoginForInvalidPassword()
			throws Exception {
		personnel = createPersonnel();
		String password = "WRONG_PASSWORD";
		try {
			userContext = personnel.login(password);
			assertFalse(true);
		} catch (PersonnelException le) {
			assertTrue(true);
			assertEquals(1,personnel.getNoOfTries().intValue());
			assertFalse(personnel.isLocked());
			assertEquals(LoginConstants.INVALIDOLDPASSWORD, le.getKey());
		}
	}

	public void testLoginForInactivePersonnel()
			throws Exception {
		personnel = createPersonnel();
		personnel.update(PersonnelStatus.INACTIVE, PersonnelLevel
				.getPersonnelLevel(personnel.getLevel().getId()), personnel
				.getOffice(), personnel.getTitle(), personnel
				.getPreferredLocale().getLocaleId(), "PASSWORD", personnel
				.getEmailId(), null, null, personnel.getPersonnelDetails()
				.getName(), personnel.getPersonnelDetails().getMaritalStatus(),
				personnel.getPersonnelDetails().getGender(), personnel
						.getPersonnelDetails().getAddress(), personnel
						.getUpdatedBy());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		String password = "ABCD";
		try {
			userContext = personnel.login(password);
			HibernateUtil.commitTransaction();
			assertFalse(true);
		} catch (PersonnelException le) {
			assertTrue(true);
			assertEquals(0, personnel.getNoOfTries().intValue());
			assertFalse(personnel.isLocked());
			assertEquals(LoginConstants.KEYUSERINACTIVE, le.getKey());
		}
	}

	public void testLoginFourConsecutiveWrongPasswordEntered()
			throws Exception {
		personnel = createPersonnel();
		String password = "WRONG_PASSWORD";
		try {
			login(password);
			login(password);
			login(password);
			login(password);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			personnel = TestObjectFactory.getPersonnel(personnel
					.getPersonnelId());
			userContext = personnel.login(password);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
					PersonnelBO.class, personnel.getPersonnelId());
			assertFalse(true);
		} catch (PersonnelException le) {
			assertTrue(true);
			assertEquals(5, personnel.getNoOfTries().intValue());
			assertTrue(personnel.isLocked());
			assertEquals(LoginConstants.INVALIDOLDPASSWORD, le.getKey());
		}
	}
	
	public void testLoginFourConsecutiveWrongPasswordEnteredFifthOneCorrect()
			throws Exception {
		personnel = createPersonnel();
		String password = "WRONG_PASSWORD";
		login(password);
		login(password);
		login(password);
		login(password);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = TestObjectFactory.getPersonnel(personnel
				.getPersonnelId());
		userContext = personnel.login("ABCD");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		assertTrue(true);
		assertFalse(personnel.isLocked());
		assertEquals("No of tries should be reseted to 0",0, personnel.getNoOfTries().intValue());
	}

	public void testLoginForLockedPersonnel()
			throws Exception {
		personnel = createPersonnel();
		String password = "WRONG_PASSWORD";
		try {
			login(password);
			login(password);
			login(password);
			login(password);
			login(password);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			personnel = TestObjectFactory.getPersonnel(personnel
					.getPersonnelId());
			userContext = personnel.login(password);
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
					PersonnelBO.class, personnel.getPersonnelId());
			assertFalse(true);
		} catch (PersonnelException le) {
			assertTrue(true);
			assertEquals(5, personnel.getNoOfTries().intValue());
			assertTrue(personnel.isLocked());
			assertEquals(LoginConstants.KEYUSERLOCKED, le.getKey());
		}
	}
	
	public void testUpdatePassword() throws Exception{
		personnel = createPersonnel();
		assertNull(personnel.getLastLogin());
		personnel.updatePassword("ABCD","NEW_PASSWORD");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		assertTrue(personnel.isPasswordChanged());
		assertNotNull(personnel.getLastLogin());
	}
	
	public void testUpdatePasswordWithWrongOldPassword() throws Exception{
		personnel = createPersonnel();
		assertNull(personnel.getLastLogin());
		try {
			personnel.updatePassword("WRONGOLD_PASSWORD","NEW_PASSWORD");
			assertFalse(true);
		} catch (PersonnelException le) {
			assertTrue(true);
			assertEquals(LoginConstants.INVALIDOLDPASSWORD, le.getKey());
			assertFalse(personnel.isPasswordChanged());
		}
	}

	private PersonnelNotesEntity createNotes(String comment) throws Exception{
		return new PersonnelNotesEntity(comment, new PersonnelPersistence()
				.getPersonnel(userContext.getId()), personnel);
	}

	private String generateGlobalPersonnelNum(String officeGlobalNum,
			int maxPersonnelId) {
		String userId = "";
		int numberOfZeros = 5 - String.valueOf(maxPersonnelId).length();
		for (int i = 0; i < numberOfZeros; i++) {
			userId = userId + "0";
		}
		userId = userId + maxPersonnelId;
		String userGlobalNum = officeGlobalNum + "-" + userId;

		return userGlobalNum;
	}

	private void createInitialObjects(Short officeId, Short personnelId) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));

		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.4", meeting, officeId, personnelId, new Date(System
						.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE,
				"1.4.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
	}

	private PersonnelBO createPersonnel(OfficeBO office,
			PersonnelLevel personnelLevel) throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("1"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		personnel = new PersonnelBO(personnelLevel, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", getRoles(), customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, userContext.getId());
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
	}

	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
		customFields.add(new CustomFieldView(Short.valueOf("1"), "123456",
				Short.valueOf("1")));
		return customFields;
	}

	private Address getAddress() {
		return new Address("changed", "changed", "changed", "changed",
				"changed", "changed", "changed", "changed");
	}

	private Name getPersonnelName() {
		return new Name("first", "middle", "secondLast", "last");
	}
	private List<RoleBO> getRoles() throws Exception{
		return ((PersonnelBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Personnel))
				.getRoles();
	}
	
	private List<RoleBO> getNewRoles() throws Exception{
		List<RoleBO> roles = new ArrayList<RoleBO>();
		roles.add((RoleBO)TestObjectFactory.getObject(RoleBO.class, Short.valueOf("1")));
		return roles;
	}
	
	private PersonnelBO createPersonnel() throws Exception {
		createdBranchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
		HibernateUtil.closeSession();
		createdBranchOffice = (OfficeBO) HibernateUtil.getSessionTL().get(
				OfficeBO.class, createdBranchOffice.getOfficeId());
		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		return new PersonnelPersistence().getPersonnel(personnel.getUserName());
	}

	private void login(String password) {
		try {
			personnel.login(password);
		} catch (PersonnelException e) {
		}
	}
	
	private Set<Short> getRoles(PersonnelBO personnelBO) {
		Set<Short> roles = new HashSet<Short>();
		for(PersonnelRoleEntity personnelRole : personnelBO.getPersonnelRoles()){
			roles.add(personnelRole.getRole().getId());
		}
		return roles;
	}
}
