package org.mifos.application.productdefinition.persistence.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestProductDefinitionPersistenceService extends MifosTestCase{
	private SavingsPrdPersistenceService dbService = new SavingsPrdPersistenceService();
	private LoansPrdPersistenceService dbServiceLoans = new LoansPrdPersistenceService();
	private CustomerBO group;
	private CustomerBO center;
	private SavingsBO savings;
	private SavingsOfferingBO savingsOffering;
	private SavingsTestHelper helper = new SavingsTestHelper();
	
	protected void setUp() throws Exception {
		super.setUp();
		dbService = (SavingsPrdPersistenceService) ServiceFactory.getInstance()
				.getPersistenceService(PersistenceServiceName.SavingsProduct);
		dbServiceLoans = (LoansPrdPersistenceService) ServiceFactory.getInstance()
		.getPersistenceService(PersistenceServiceName.LoansProduct);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}
	
	public void testGetSavingsAccount() throws Exception{
		createInitialObjects();		
		savingsOffering = helper.createSavingsOffering("fsaf6","ads6");
		UserContext userContext = new UserContext();
		userContext.setId(Short.valueOf("1"));
		savings = helper.createSavingsAccount("000100000000017",savingsOffering,group,AccountStates.SAVINGS_ACC_APPROVED,userContext);
		HibernateUtil.closeSession();
		List<SavingsBO> savingsList=dbService.retrieveSavingsAccountsForPrd(savingsOffering.getPrdOfferingId());
		assertEquals(Integer.valueOf("1").intValue(),savingsList.size());
		savings=new SavingsPersistence().findById(savings.getAccountId());
	}

	public void testretrieveLatenessForPrd() throws Exception
	{
		Short latenessDays = null;
		latenessDays = dbServiceLoans.retrieveLatenessForPrd();
		assertNotNull(latenessDays);
		assertEquals(Short.valueOf("10"),latenessDays);
	}
	
	public void testDormancyDays()throws Exception{
		assertEquals(Short.valueOf("30"),dbService.retrieveDormancyDays());
	}

	private void createInitialObjects(){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group_Active_test",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
	}
	
}
