package org.mifos.framework.components.taggenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestTagGenerator extends MifosTestCase {
	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	private UserContext userContext;

	private PersonnelBO personnel;

	private OfficeBO branchOffice;

	Object randomNum = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");

		randomNum = new Random().nextLong();
	}

	@Override
	protected void tearDown() throws Exception {
		branchOffice = null;
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(personnel);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSavingsAccountLinkWithoutSelfLink() throws Exception {
		createInitialObjectsForSavings();
		String createdLink = TagGenerator.createHeaderLinks(savings, false,
				randomNum);
		assertEquals(true, createdLink.contains("custSearchAction"));
		assertEquals(true, createdLink.contains("TestBranchOffice"));
		assertEquals(true, createdLink.contains("centerCustAction"));
		assertEquals(true, createdLink.contains("Center_Active_test"));
		assertEquals(true, createdLink.contains("groupCustAction"));
		assertEquals(true, createdLink.contains("Group_Active_test"));
		assertEquals(true, createdLink.contains("prd1"));
	}

	public void testSavingsAccountLinkWithSelfLink() throws Exception {
		createInitialObjectsForSavings();
		String createdLink = TagGenerator.createHeaderLinks(savings, true,
				randomNum);
		assertEquals(true, createdLink.contains("custSearchAction"));
		assertEquals(true, createdLink.contains("TestBranchOffice"));
		assertEquals(true, createdLink.contains("centerCustAction"));
		assertEquals(true, createdLink.contains("Center_Active_test"));
		assertEquals(true, createdLink.contains("groupCustAction"));
		assertEquals(true, createdLink.contains("Group_Active_test"));
		assertEquals(true, createdLink.contains("savingsAction"));
	}

	public void testPersonnelLinkWithoutSelfLink() throws Exception {
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		String createdLink = TagGenerator.createHeaderLinks(personnel, false,
				randomNum);
		assertEquals(false, createdLink.contains("PersonAction"));
		assertEquals(true, createdLink.contains("TestBranchOffice"));
	}

	public void testPersonnelLinkWithSelfLink() throws Exception {
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		String createdLink = TagGenerator.createHeaderLinks(personnel, true,
				randomNum);
		assertEquals(true, createdLink.contains("PersonAction"));
		assertEquals(true, createdLink.contains("TestBranchOffice"));
	}

	public void testTagGeneratorFactory() throws Exception {
		createInitialObjectsForSavings();
		TagGenerator tagGenerator = TagGeneratorFactory.getInstance()
				.getGenerator(center);
		if (tagGenerator instanceof CustomerTagGenerator)
			assertTrue(true);

		tagGenerator = TagGeneratorFactory.getInstance().getGenerator(group);
		if (tagGenerator instanceof CustomerTagGenerator)
			assertTrue(true);

		tagGenerator = TagGeneratorFactory.getInstance().getGenerator(savings);
		if (tagGenerator instanceof AccountTagGenerator)
			assertTrue(true);

	}

	public void testTagGeneratorFactoryPageExpired() throws Exception {
		try {
			TagGeneratorFactory.getInstance().getGenerator(null);
			fail();
		} catch (PageExpiredException e) {
			assertTrue(true);
		}
	}

	public void testTagGeneratorFactoryForPersonnel() throws Exception {
		branchOffice = TestObjectFactory.getOffice(Short.valueOf("3"));
		createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
		TagGenerator tagGenerator = TagGeneratorFactory.getInstance()
				.getGenerator(personnel);
		tagGenerator = TagGeneratorFactory.getInstance()
				.getGenerator(personnel);
		if (tagGenerator instanceof PersonnelTagGenerator)
			assertTrue(true);
	}

	private void createInitialObjectsForSavings() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering = helper.createSavingsOffering("prd1", "cdfg");
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
	}

	private PersonnelBO createPersonnel(OfficeBO office,
			PersonnelLevel personnelLevel) throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Name name = new Name("XYZ", null, null, null);
		java.util.Date date = new java.util.Date();
		personnel = new PersonnelBO(personnelLevel, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", null, customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, userContext.getId());
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
	}
}
