package org.mifos.framework.components.cronjob.helpers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.TestAccountActionDateEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.GenerateMeetingsForCustomerAndSavingsTask;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestGenerateMeetingsForCustomerAndSavingsHelper extends
		MifosTestCase {
	
	private CustomerBO group;

	private CustomerBO center;

	private CustomerBO client1;

	private CustomerBO client2;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		userContext = TestObjectFactory.getContext();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testExecuteForCustomerAccount() throws Exception{
		createCenter();
		int noOfInstallments=center.getCustomerAccount().getAccountActionDates().size();
		new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper().execute(System.currentTimeMillis());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		assertEquals(noOfInstallments+10,center.getCustomerAccount().getAccountActionDates().size());
		
	}
	
	public void testExecuteForSavingsAccount() throws Exception{
		savings=getSavingsAccountForCenter();
		int noOfInstallments=savings.getAccountActionDates().size();
		TestAccountActionDateEntity.changeInstallmentDatesToPreviousDate(savings);
		TestObjectFactory.flushandCloseSession();
		savings=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper().execute(System.currentTimeMillis());
		savings=(SavingsBO)TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		assertEquals(noOfInstallments+20,savings.getAccountActionDates().size());
	}
	
	private void createCenter() {
		List<FeeView> feeView = new ArrayList<FeeView>();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test",
				CustomerStatus.CENTER_ACTIVE.getValue(), "1.1", meeting,
				new Date(System.currentTimeMillis()), feeView);
		TestAccountActionDateEntity.changeInstallmentDatesToPreviousDate(center.getCustomerAccount());	
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
	}
	
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
	}
	
	private SavingsBO getSavingsAccountForCenter() throws Exception {
		createInitialObjects();
		client1 = TestObjectFactory.createClient("client1",
				ClientConstants.STATUS_ACTIVE, "1.1.1.1", group, new Date(
						System.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client2",
				ClientConstants.STATUS_ACTIVE, "1.1.1.2", group, new Date(
						System.currentTimeMillis()));
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
		return helper.createSavingsAccount(savingsOffering, center,
				AccountStates.SAVINGS_ACC_APPROVED, userContext);
	}
	
	
	
}
