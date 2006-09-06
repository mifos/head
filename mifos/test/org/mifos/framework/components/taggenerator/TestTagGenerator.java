package org.mifos.framework.components.taggenerator;

import java.sql.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

import org.mifos.framework.MifosTestCase;

public class TestTagGenerator extends MifosTestCase{
	private CustomerBO group;
	private CustomerBO center;
	private SavingsBO savings;
	private SavingsOfferingBO savingsOffering;
	private UserContext userContext;
	
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
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);		
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testSavingsAccountLinkWithoutSelfLink(){
		createInitialObjectsForSavings();
		String createdLink = TagGenerator.createHeaderLinks(savings,false);
		assertEquals(true,createdLink.contains("CustomerSearchAction"));
		assertEquals(true,createdLink.contains("TestBranchOffice"));
		assertEquals(true,createdLink.contains("centerCustAction"));
		assertEquals(true,createdLink.contains("Center_Active_test"));
		assertEquals(true,createdLink.contains("groupCustAction"));
		assertEquals(true,createdLink.contains("Group_Active_test"));
		assertEquals(true,createdLink.contains("prd1"));
	}
	
	public void testSavingsAccountLinkWithSelfLink(){
		createInitialObjectsForSavings();
		String createdLink = TagGenerator.createHeaderLinks(savings,true);
		assertEquals(true,createdLink.contains("CustomerSearchAction"));
		assertEquals(true,createdLink.contains("TestBranchOffice"));
		assertEquals(true,createdLink.contains("centerCustAction"));
		assertEquals(true,createdLink.contains("Center_Active_test"));
		assertEquals(true,createdLink.contains("groupCustAction"));
		assertEquals(true,createdLink.contains("Group_Active_test"));
		assertEquals(true,createdLink.contains("savingsAction"));
	}

	public void testTagGeneratorFactory(){
		createInitialObjectsForSavings();
		TagGenerator tagGenerator = TagGeneratorFactory.getInstance().getGenerator(center);
		if(tagGenerator instanceof CustomerTagGenerator)
			assertTrue(true);
		
		tagGenerator = TagGeneratorFactory.getInstance().getGenerator(group);
		if(tagGenerator instanceof CustomerTagGenerator)
			assertTrue(true);
		
		tagGenerator = TagGeneratorFactory.getInstance().getGenerator(savings);
		if(tagGenerator instanceof AccountTagGenerator)
			assertTrue(true);
	}
	
	private void createInitialObjectsForSavings(){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short.valueOf("9"), "1.1.1", center, new Date(System.currentTimeMillis()));
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering=helper.createSavingsOffering("prd1","cdfg");
		savings = helper.createSavingsAccount("000100000000017",savingsOffering, group,AccountStates.SAVINGS_ACC_APPROVED, userContext);
	}	
}
