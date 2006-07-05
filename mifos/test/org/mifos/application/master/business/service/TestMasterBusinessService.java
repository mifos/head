package org.mifos.application.master.business.service;

import java.sql.Date;
import java.util.List;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestMasterBusinessService extends MifosTestCase {

	MasterDataService masterService;

	public void setUp() throws Exception {
		super.setUp();
		masterService = new MasterDataService();
		HierarchyManager.getInstance().init();
	}

	public void tearDown() {
		HibernateUtil.closeSession();

	}

	public void testGetListOfActiveLoanOfficers() {
		List loanOfficers = masterService.getListOfActiveLoanOfficers(
				PersonnelConstants.LOAN_OFFICER, Short.valueOf("3"), Short
						.valueOf("3"), PersonnelConstants.LOAN_OFFICER);
		assertEquals(1, loanOfficers.size());
	}

	public void testGetActiveBranches() {
		List branches = masterService.getActiveBranches(Short.valueOf("1"));
		assertEquals(1, branches.size());
	}

	public void testGetListOfActiveParentsUnderLoanOfficer() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		CustomerBO center = TestObjectFactory.createCenter("Center_Active",
				Short.valueOf("13"), "1.1", meeting, new Date(System
						.currentTimeMillis()));
		List<CustomerView> customers = masterService
				.getListOfActiveParentsUnderLoanOfficer(Short.valueOf("1"),
						CustomerConstants.CENTER_LEVEL_ID, Short.valueOf("3"));
		assertEquals(1, customers.size());
		TestObjectFactory.cleanUp(center);
	}

	public void testGetMasterData() throws Exception {
		EntityMaster paymentTypes = masterService
				.getMasterData(
						MasterConstants.PAYMENT_TYPE,
						Short.valueOf("1"),
						"org.mifos.application.productdefinition.util.valueobjects.PaymentType",
						"paymentTypeId");
		List<LookUpMaster> paymentValues = paymentTypes.getLookUpMaster();
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER, paymentValues.size());

	}

	public void testGetSavingsProductsAsOfMeetingDate() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));

		Date startDate = new Date(System.currentTimeMillis());
		CustomerBO center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.1", meeting, startDate);
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd1", Short.valueOf("2"),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("2"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		AccountBO account = TestObjectFactory.createSavingsAccount("432434",
				center, Short.valueOf("16"), startDate, savingsOffering);

		List<PrdOfferingBO> productList = masterService
				.getSavingsProductsAsOfMeetingDate(startDate, "1.1", center
						.getPersonnel().getPersonnelId());
		assertEquals(1, productList.size());
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(center);
	}

	public void testRetrievePaymentTypes()throws Exception{
		List<PaymentTypeEntity> paymentTypeList = masterService.retrievePaymentTypes(Short.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER,paymentTypeList.size());
	}
	
	public void testGetSupportedPaymentModes()throws Exception{
		List<PaymentTypeEntity> paymentTypeList = masterService.getSupportedPaymentModes(Short.valueOf("1"),Short.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER,paymentTypeList.size());
	}
}
