package org.mifos.application.bulkentry.struts.uihelpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junitx.framework.StringAssert;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.CollectionSheetEntryView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class BulkEntryDisplayHelperTest extends MifosIntegrationTest {

	public BulkEntryDisplayHelperTest() throws SystemException, ApplicationException {
        super();
    }

    CustomerBO center;

	CustomerBO group;

	ClientBO client;

	AccountBO account;

	LoanBO groupAccount;

	LoanBO clientAccount;

	private SavingsBO centerSavingsAccount;

	private SavingsBO groupSavingsAccount;

	private SavingsBO clientSavingsAccount;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(centerSavingsAccount);
		TestObjectFactory.cleanUp(groupSavingsAccount);
		TestObjectFactory.cleanUp(clientSavingsAccount);
		TestObjectFactory.cleanUp(groupAccount);
		TestObjectFactory.cleanUp(clientAccount);
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testBuildTableHeadings() {
		LoanOfferingBO loanOffering = createLoanOfferingBO("Loan Offering",
				"LOAN");
		java.util.Date currentDate = new java.util.Date(System.currentTimeMillis());
		SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Savings Offering", "SAVP", currentDate);
		List<PrdOfferingBO> loanProducts = new ArrayList<PrdOfferingBO>();
		List<PrdOfferingBO> savingsProducts = new ArrayList<PrdOfferingBO>();
		loanProducts.add(loanOffering);
		savingsProducts.add(savingsOffering);
		UserContext userContext = TestObjectFactory.getContext();
		String result = new BulkEntryDisplayHelper().buildTableHeadings(
				loanProducts, savingsProducts, userContext.getPreferredLocale()).toString();
		StringAssert.assertContains("LOAN", result);
		StringAssert.assertContains("SAVP", result);

		StringAssert.assertContains("Due/Collections", result);
		StringAssert.assertContains("Issues/Withdrawals", result);
		StringAssert.assertContains("Client Name", result);
		StringAssert.assertContains("A/C Collections", result);
		StringAssert.assertContains("Attn", result);

		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testGetEndTable() {
		String result = new BulkEntryDisplayHelper().getEndTable(3).toString();
		StringAssert.assertContains("</table>", result);
		StringAssert.assertContains("</tr>", result);
		StringAssert.assertContains("<tr>", result);
		StringAssert.assertContains("<td", result);
		StringAssert.assertContains("</td", result);
	}
	
	public void testBuildForCenterForGetMethod() throws Exception {
		BulkEntryBO bulkEntry = createBulkEntry();
		StringBuilder builder = new StringBuilder();
		
		// Assert that the extracted attendance types are the ones expected
		final String[] EXPECTED_ATTENDANCE_TYPES = {"P", "A", "AA", "L"};
		List<CustomValueListElement> attendanceTypesCustomValueList = new MasterDataService().getMasterData(
				MasterConstants.ATTENDENCETYPES, (short) 1,
				"org.mifos.application.master.business.CustomerAttendanceType",
				"attendanceId").getCustomValueListElements();
		List<String> attendanceTypesLookupValueList = new ArrayList<String>();
		for(CustomValueListElement attendanceTypeCustomValueListElement: attendanceTypesCustomValueList) {
			attendanceTypesLookupValueList.add(attendanceTypeCustomValueListElement.getLookUpValue());
		}
		assertEquals(Arrays.asList(EXPECTED_ATTENDANCE_TYPES), attendanceTypesLookupValueList);
		
		HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
		
		new BulkEntryDisplayHelper().buildForCenter(bulkEntry
				.getBulkEntryParent(), bulkEntry.getLoanProducts(), bulkEntry
				.getSavingsProducts(), clientAttendance, attendanceTypesCustomValueList, builder, Methods.get
				.toString(), TestObjectFactory.getContext(), (short) 1);
		String result = builder.toString();

		StringAssert.assertContains("Group", result);
		StringAssert.assertContains("Client", result);
		StringAssert
				.assertNotContains("<option value= \"\"></option>)", result);
		StringAssert.assertContains(groupAccount.getLoanBalance().toString(),
				result);
		StringAssert.assertContains(clientAccount.getLoanBalance().toString(),
				result);
		StringAssert.assertContains("0.0", result);

		StringAssert.assertContains("enteredAmount", result);
		StringAssert.assertContains("depositAmountEntered", result);
		StringAssert.assertContains("withDrawalAmountEntered", result);
	}

	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeeting(WEEKLY, EVERY_WEEK, LOAN_INSTALLMENT, MONDAY));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, frequency);
	}

	private BulkEntryBO createBulkEntry() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", 
				CustomerStatus.CLIENT_ACTIVE,
				group);
		LoanOfferingBO loanOffering1 = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		LoanOfferingBO loanOffering2 = TestObjectFactory.createLoanOffering(
				"Loan2345", "313f", startDate, meeting);
		groupAccount = TestObjectFactory.createLoanAccount("42423142341",
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
				startDate, loanOffering1);
		clientAccount = getLoanAccount(AccountState.LOAN_APPROVED, 
				startDate, 1,
				loanOffering2);
		java.util.Date currentDate = new java.util.Date(System.currentTimeMillis());
		SavingsOfferingBO savingsOffering1 = 
			TestObjectFactory.createSavingsProduct(
					"SavingPrd1", "ased", currentDate);
		SavingsOfferingBO savingsOffering2 = 
			TestObjectFactory.createSavingsProduct(
					"SavingPrd2", "cvdf", currentDate);
		SavingsOfferingBO savingsOffering3 = 
			TestObjectFactory.createSavingsProduct(
					"SavingPrd3", "zxsd", currentDate);

		centerSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43244334", center, Short.valueOf("16"), startDate,
				savingsOffering1);
		groupSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43234434", group, Short.valueOf("16"), startDate,
				savingsOffering2);
		clientSavingsAccount = TestObjectFactory.createSavingsAccount(
				"43245434", client, Short.valueOf("16"), startDate,
				savingsOffering3);

		BulkEntryBO bulkEntry = new BulkEntryBO();

		CollectionSheetEntryView bulkEntryParent = new CollectionSheetEntryView(
				getCusomerView(center));
		SavingsAccountView centerSavingsAccountView = getSavingsAccountView(centerSavingsAccount);
		centerSavingsAccountView.setDepositAmountEntered("100");
		centerSavingsAccountView.setWithDrawalAmountEntered("10");
		bulkEntryParent.addSavingsAccountDetail(centerSavingsAccountView);
		bulkEntryParent
				.setCustomerAccountDetails(getCustomerAccountView(center));

		CollectionSheetEntryView bulkEntryChild = new CollectionSheetEntryView(getCusomerView(group));
		LoanAccountView groupLoanAccountView = getLoanAccountView(groupAccount);
		SavingsAccountView groupSavingsAccountView = getSavingsAccountView(groupSavingsAccount);
		groupSavingsAccountView.setDepositAmountEntered("100");
		groupSavingsAccountView.setWithDrawalAmountEntered("10");
		bulkEntryChild.addLoanAccountDetails(groupLoanAccountView);
		bulkEntryChild.addSavingsAccountDetail(groupSavingsAccountView);
		bulkEntryChild.setCustomerAccountDetails(getCustomerAccountView(group));

		CollectionSheetEntryView bulkEntrySubChild = new CollectionSheetEntryView(
				getCusomerView(client));
		LoanAccountView clientLoanAccountView = getLoanAccountView(clientAccount);
		clientLoanAccountView.setAmountPaidAtDisbursement(0.0);
		SavingsAccountView clientSavingsAccountView = getSavingsAccountView(clientSavingsAccount);
		clientSavingsAccountView.setDepositAmountEntered("100");
		clientSavingsAccountView.setWithDrawalAmountEntered("10");
		bulkEntrySubChild.addLoanAccountDetails(clientLoanAccountView);
		bulkEntrySubChild.setAttendence(new Short("2"));
		bulkEntrySubChild.addSavingsAccountDetail(clientSavingsAccountView);
		bulkEntrySubChild
				.setCustomerAccountDetails(getCustomerAccountView(client));

		bulkEntryChild.addChildNode(bulkEntrySubChild);
		bulkEntryParent.addChildNode(bulkEntryChild);

		bulkEntryChild.getLoanAccountDetails().get(0).setPrdOfferingId(
				groupLoanAccountView.getPrdOfferingId());
		bulkEntryChild.getLoanAccountDetails().get(0).setEnteredAmount("100.0");
		bulkEntrySubChild.getLoanAccountDetails().get(0)
				.setDisBursementAmountEntered(
						clientAccount.getLoanAmount().toString());
		bulkEntrySubChild.getLoanAccountDetails().get(0).setPrdOfferingId(
				clientLoanAccountView.getPrdOfferingId());
		List<PrdOfferingBO> loanProducts = new ArrayList<PrdOfferingBO>();
		loanProducts.add(loanOffering1);
		loanProducts.add(loanOffering2);
		List<PrdOfferingBO> savingsProducts = new ArrayList<PrdOfferingBO>();
		savingsProducts.add(savingsOffering1);
		savingsProducts.add(savingsOffering2);
		savingsProducts.add(savingsOffering3);
		bulkEntry.setLoanProducts(loanProducts);
		bulkEntry.setSavingsProducts(savingsProducts);
		bulkEntry.setTotalCustomers(3);
		bulkEntry.setBulkEntryParent(bulkEntryParent);
		bulkEntry.setReceiptDate(new java.sql.Date(System.currentTimeMillis()));
		bulkEntry.setReceiptId("324343242");
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));

		return bulkEntry;

	}

	private LoanBO getLoanAccount(AccountState state, Date startDate,
			int disbursalType, LoanOfferingBO loanOfferingBO) {
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOfferingBO,
				disbursalType);

	}

	private LoanAccountView getLoanAccountView(LoanBO account) {
		LoanAccountView accountView = TestObjectFactory
				.getLoanAccountView(account);
		List<AccountActionDateEntity> actionDates = new ArrayList<AccountActionDateEntity>();
		actionDates.add(account.getAccountActionDate((short) 1));
		accountView.addTrxnDetails(TestObjectFactory
				.getBulkEntryAccountActionViews(actionDates));

		return accountView;
	}

	private SavingsAccountView getSavingsAccountView(SavingsBO account) {
		SavingsAccountView accountView = new SavingsAccountView(account
				.getAccountId(), account.getType(),
				account.getSavingsOffering());
		accountView.addAccountTrxnDetail(TestObjectFactory
				.getBulkEntryAccountActionView(account
						.getAccountActionDate((short) 1)));

		return accountView;
	}

	private CustomerView getCusomerView(CustomerBO customer) {
		CustomerView customerView = new CustomerView();
		customerView.setCustomerId(customer.getCustomerId());
		customerView.setCustomerLevelId(customer.getCustomerLevel().getId());
		customerView.setCustomerSearchId(customer.getSearchId());
		customerView.setDisplayName(customer.getDisplayName());
		customerView.setGlobalCustNum(customer.getGlobalCustNum());
		customerView.setOfficeId(customer.getOffice().getOfficeId());
		if (null != customer.getParentCustomer())
			customerView.setParentCustomerId(customer.getParentCustomer()
					.getCustomerId());
		customerView.setPersonnelId(customer.getPersonnel().getPersonnelId());
		customerView.setStatusId(customer.getCustomerStatus().getId());
		return customerView;
	}

	private PersonnelView getPersonnelView(PersonnelBO personnel) {
		PersonnelView personnelView = new PersonnelView(personnel
				.getPersonnelId(), personnel.getDisplayName());
		return personnelView;
	}

	private PaymentTypeView getPaymentTypeView() {
		PaymentTypeView paymentTypeView = new PaymentTypeView();
		paymentTypeView.setPaymentTypeId(Short.valueOf("1"));
		return paymentTypeView;
	}

	private CustomerAccountView getCustomerAccountView(CustomerBO customer) {
		CustomerAccountView customerAccountView = new CustomerAccountView(
				customer.getCustomerAccount().getAccountId());

		List<AccountActionDateEntity> accountAction = new ArrayList<AccountActionDateEntity>();
		accountAction.add(customer.getCustomerAccount().getAccountActionDate(
				Short.valueOf("1")));
		customerAccountView.setAccountActionDates(TestObjectFactory
				.getBulkEntryAccountActionViews(accountAction));
		customerAccountView.setCustomerAccountAmountEntered("100.0");
		customerAccountView.setValidCustomerAccountAmountEntered(true);
		return customerAccountView;
	}

}
