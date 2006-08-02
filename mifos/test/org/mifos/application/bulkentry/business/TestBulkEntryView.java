package org.mifos.application.bulkentry.business;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.bulkentry.persistance.service.BulkEntryPersistanceService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestBulkEntryView extends MifosTestCase {

	private CustomerBO center;

	private CustomerBO group;

	private LoanBO account1;

	private LoanBO account2;

	private LoanOfferingBO loanOffering;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUpWithoutDeletetingProduct(account1);
		TestObjectFactory.cleanUpWithoutDeletetingProduct(account2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.removeObject(loanOffering);
		HibernateUtil.closeSession();
	}

	public void testMultipleRepaymentAccountsForSingleProduct()
			throws SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		loanOffering = TestObjectFactory.createLoanOffering("Loan", Short
				.valueOf("2"), startDate, Short.valueOf("1"), 300.0, 1.2, Short
				.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"), meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		account2 = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();

		BulkEntryBO bulkEntry = new BulkEntryBO();
		bulkEntry.setOffice(getOfficeView(center.getOffice()));
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));
		CustomerView parentCustomer = getCusomerView(center);
		bulkEntry.buildBulkEntryView(parentCustomer);

		BulkEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
		BulkEntryView groupBulkEntryView = parentBulkEntryView
				.getBulkEntryChildren().get(0);
		List<LoanAccountsProductView> loanProducts = groupBulkEntryView
				.getLoanAccountDetails();
		LoanAccountsProductView loanAccountsProductView = loanProducts.get(0);
		assertEquals("The Size of products ", loanProducts.size(), 1);
		assertEquals("The size of loan Accounts", loanAccountsProductView
				.getLoanAccountViews().size(), 2);
		assertEquals("The amount due for loan account1", account1
				.getTotalAmountDue().getAmountDoubleValue(), 212.0);
		assertEquals("The amount due for loan account2", account2
				.getTotalAmountDue().getAmountDoubleValue(), 212.0);
		assertEquals("The toal amount due for the product",
				loanAccountsProductView.getTotalAmountDue().doubleValue(),
				424.0);
		HibernateUtil.closeSession();
		account1 = (LoanBO) new AccountPersistanceService().getAccount(account1
				.getAccountId());
		account2 = (LoanBO) new AccountPersistanceService().getAccount(account2
				.getAccountId());
	}

	public void testMultipleRepaymentDisbursalAccountsForSingleProduct()
			throws SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		loanOffering = TestObjectFactory.createLoanOffering("Loan", Short
				.valueOf("2"), startDate, Short.valueOf("1"), 300.0, 1.2, Short
				.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"), meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		account2 = TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", group, Short.valueOf("3"), new Date(System
						.currentTimeMillis()), loanOffering, 1);
		HibernateUtil.closeSession();

		BulkEntryBO bulkEntry = new BulkEntryBO();
		bulkEntry.setOffice(getOfficeView(center.getOffice()));
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));
		CustomerView parentCustomer = getCusomerView(center);
		bulkEntry.buildBulkEntryView(parentCustomer);

		BulkEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
		BulkEntryView groupBulkEntryView = parentBulkEntryView
				.getBulkEntryChildren().get(0);
		List<LoanAccountsProductView> loanProducts = groupBulkEntryView
				.getLoanAccountDetails();
		LoanAccountsProductView loanAccountsProductView = loanProducts.get(0);
		assertEquals("The Size of products ", loanProducts.size(), 1);
		assertEquals("The size of loan Accounts", loanAccountsProductView
				.getLoanAccountViews().size(), 2);
		assertEquals("The amount due for loan account1", account1
				.getTotalAmountDue().getAmountDoubleValue(), 212.0);
		assertEquals("The toal amount due for the product",
				loanAccountsProductView.getTotalAmountDue().doubleValue(),
				242.0);
		assertEquals("The toal amount to be disbursed for the product",
				loanAccountsProductView.getTotalDisburseAmount().doubleValue(),
				300.0);
		HibernateUtil.closeSession();
		account1 = (LoanBO) new AccountPersistanceService().getAccount(account1
				.getAccountId());
		account2 = (LoanBO) new AccountPersistanceService().getAccount(account2
				.getAccountId());
	}

	public void testMultipleDisbursalAccountsForSingleProduct()
			throws SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		loanOffering = TestObjectFactory.createLoanOffering("Loan", Short
				.valueOf("2"), startDate, Short.valueOf("1"), 300.0, 1.2, Short
				.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"), Short
				.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"), meeting);
		account1 = TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", group, Short.valueOf("3"), new Date(System
						.currentTimeMillis()), loanOffering, 1);
		account2 = TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", group, Short.valueOf("3"), new Date(System
						.currentTimeMillis()), loanOffering, 1);
		HibernateUtil.closeSession();

		BulkEntryBO bulkEntry = new BulkEntryBO();
		bulkEntry.setOffice(getOfficeView(center.getOffice()));
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));
		CustomerView parentCustomer = getCusomerView(center);
		bulkEntry.buildBulkEntryView(parentCustomer);

		BulkEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
		BulkEntryView groupBulkEntryView = parentBulkEntryView
				.getBulkEntryChildren().get(0);
		List<LoanAccountsProductView> loanProducts = groupBulkEntryView
				.getLoanAccountDetails();
		LoanAccountsProductView loanAccountsProductView = loanProducts.get(0);
		assertEquals("The Size of products ", loanProducts.size(), 1);
		assertEquals("The size of loan Accounts", loanAccountsProductView
				.getLoanAccountViews().size(), 2);
		assertEquals("The toal amount due for the product",
				loanAccountsProductView.getTotalAmountDue().doubleValue(), 60.0);
		assertEquals("The toal amount to be disbursed for the product",
				loanAccountsProductView.getTotalDisburseAmount().doubleValue(),
				600.0);
		HibernateUtil.closeSession();
		account1 = (LoanBO) new AccountPersistanceService().getAccount(account1
				.getAccountId());
		account2 = (LoanBO) new AccountPersistanceService().getAccount(account2
				.getAccountId());
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

	private OfficeView getOfficeView(OfficeBO office) {
		OfficeView officeView = new OfficeView(office.getOfficeId(), office
				.getOfficeName(), office.getLevel().getLevelId(), office
				.getVersionNo());
		return officeView;
	}

	private CustomerView getCusomerView(CustomerBO customer) {
		CustomerView customerView = new CustomerView();
		customerView.setCustomerId(customer.getCustomerId());
		customerView.setCustomerLevelId(customer.getCustomerLevel()
				.getId());
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

	public void testPopulateForCustomerAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		BulkEntryPersistanceService bulkEntryPersistanceService = new BulkEntryPersistanceService();
		List<BulkEntryAccountActionView> bulkEntryAccountActionViews = bulkEntryPersistanceService
				.getBulkEntryActionView(DateUtils
						.getCurrentDateWithoutTimeStamp(),
						center.getSearchId(), center.getOffice().getOfficeId());
		List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews = bulkEntryPersistanceService
				.getBulkEntryFeeActionView(DateUtils
						.getCurrentDateWithoutTimeStamp(),
						center.getSearchId(), center.getOffice().getOfficeId());
		assertNotNull(center.getCustomerAccount());
		BulkEntryView bulkEntryView = new BulkEntryView(getCusomerView(center));
		bulkEntryView.populateCustomerAccountInformation(center,
				bulkEntryAccountActionViews, bulkEntryAccountFeeActionViews);
		CustomerAccountView customerAccountView = bulkEntryView
				.getCustomerAccountDetails();
		assertEquals(
				"The retrieved accountId of the customer account should be equal to the created",
				center.getCustomerAccount().getAccountId(), customerAccountView
						.getAccountId());
		assertEquals("The size of the due insallments is ", customerAccountView
				.getAccountActionDates().size(), 1);
		assertEquals("The amount due is ", customerAccountView
				.getTotalAmountDue().getAmountDoubleValue(), 100.0);
	}

}
