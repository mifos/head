package org.mifos.framework.components.cronjob.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.TestAccountActionDateEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.GenerateMeetingsForCustomerAndSavingsTask;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
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
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		assertEquals(noOfInstallments+10,center.getCustomerAccount().getAccountActionDates().size());
		
	}
	
	public void testExecuteForSavingsAccount() throws Exception{
		savings=getSavingsAccountForCenter();
		int noOfInstallments=savings.getAccountActionDates().size();
		TestAccountActionDateEntity.changeInstallmentDatesToPreviousDate(savings);
		TestObjectFactory.flushandCloseSession();
		savings=TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper().execute(System.currentTimeMillis());
		savings=TestObjectFactory.getObject(SavingsBO.class,savings.getAccountId());
		assertEquals(noOfInstallments+20,savings.getAccountActionDates().size());
	}
	
	public void testExecuteForSavingsAccountForGroup() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test",
				CustomerStatus.GROUP_ACTIVE, center);
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering = createSavingsOffering("dfasdasd1", "sad1", Short
				.valueOf("1"), Short.valueOf("2"), Short.valueOf("1"), Short
				.valueOf("2"), RecommendedAmountUnit.COMPLETEGROUP);
		savings = helper.createSavingsAccount(savingsOffering, group,
				AccountStates.SAVINGS_ACC_APPROVED, userContext);
		Date meetingStartDate = savings.getCustomer().getCustomerMeeting()
				.getMeeting().getStartDate();
		int noOfInstallments = savings.getAccountActionDates().size();
		TestAccountActionDateEntity
				.changeInstallmentDatesToPreviousDateExceptLastInstallment(
						savings, 6);
		TestObjectFactory.flushandCloseSession();
		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		new GenerateMeetingsForCustomerAndSavingsTask().getTaskHelper()
				.execute(System.currentTimeMillis());
		HibernateUtil.closeSession();
		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		assertEquals(noOfInstallments + 10, savings.getAccountActionDates()
				.size());
		assertEquals(new java.sql.Date(DateUtils.getDateWithoutTimeStamp(
				meetingStartDate.getTime()).getTime()).toString(), group
				.getCustomerMeeting().getMeeting().getStartDate().toString());
	}
	
	private void createCenter() {
		List<FeeView> feeView = new ArrayList<FeeView>();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test",
				meeting, feeView);
		TestAccountActionDateEntity.changeInstallmentDatesToPreviousDate(center.getCustomerAccount());	
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
	}
	
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
	}
	
	private SavingsBO getSavingsAccountForCenter() throws Exception {
		createInitialObjects();
		client1 = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_ACTIVE, group);
		SavingsTestHelper helper = new SavingsTestHelper();
		savingsOffering = helper.createSavingsOffering("dfasdasd1", "sad1");
		return helper.createSavingsAccount(savingsOffering, center,
				AccountStates.SAVINGS_ACC_APPROVED, userContext);
	}
	
	private SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName, Short interestCalcType, Short savingsTypeId,
			Short depGLCode, Short intGLCode,
			RecommendedAmountUnit recommendedAmountUnit) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, recommendedAmountUnit.getValue(),
				24.0, 200.0, 200.0, savingsTypeId, interestCalcType,
				meetingIntCalc, meetingIntPost, depGLCode, intGLCode);
	}
}
