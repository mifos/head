package org.mifos.application.bulkentry.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.bulkentry.persistance.service.BulkEntryPersistenceService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
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
    
    private ClientBO client;
    
    private CustomerPersistence customerPersistence;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        customerPersistence = new CustomerPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUpWithoutDeletingProduct(account1);
		TestObjectFactory.cleanUpWithoutDeletingProduct(account2);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.removeObject(loanOffering);
		HibernateUtil.closeSession();
		customerPersistence = null;
		super.tearDown();
	}

	public void testMultipleRepaymentAccountsForSingleProduct()
			throws SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
				loanOffering);
		account2 = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
				loanOffering);
		HibernateUtil.closeSession();

		BulkEntryBO bulkEntry = new BulkEntryBO();
		bulkEntry.setOffice(getOfficeView(center.getOffice()));
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));
		CustomerView parentCustomer = getCustomerView(center);
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
		account1 = (LoanBO) new AccountPersistence().getAccount(account1
				.getAccountId());
		account2 = (LoanBO) new AccountPersistence().getAccount(account2
				.getAccountId());
	}

	public void testMultipleRepaymentDisbursalAccountsForSingleProduct()
			throws SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		account1 = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		account2 = TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", group, AccountState.LOAN_APPROVED, 
				startDate, loanOffering, 1);
		HibernateUtil.closeSession();

		BulkEntryBO bulkEntry = new BulkEntryBO();
		bulkEntry.setOffice(getOfficeView(center.getOffice()));
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(startDate.getTime()));
		CustomerView parentCustomer = getCustomerView(center);
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
		account1 = (LoanBO) new AccountPersistence().getAccount(account1
				.getAccountId());
		account2 = (LoanBO) new AccountPersistence().getAccount(account2
				.getAccountId());
	}

	public void testMultipleDisbursalAccountsForSingleProduct()
			throws SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		account1 = TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", group, 
				AccountState.LOAN_APPROVED, 
				new Date(System.currentTimeMillis()), loanOffering, 1);
		account2 = TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", group, AccountState.LOAN_APPROVED, 
				new Date(System
						.currentTimeMillis()), loanOffering, 1);
		HibernateUtil.closeSession();

		BulkEntryBO bulkEntry = new BulkEntryBO();
		bulkEntry.setOffice(getOfficeView(center.getOffice()));
		bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
		bulkEntry.setPaymentType(getPaymentTypeView());
		bulkEntry.setTransactionDate(new java.sql.Date(System
				.currentTimeMillis()));
		CustomerView parentCustomer = getCustomerView(center);
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
		account1 = (LoanBO) new AccountPersistence().getAccount(account1
				.getAccountId());
		account2 = (LoanBO) new AccountPersistence().getAccount(account2
				.getAccountId());
	}
	
	public void testPopulateForCustomerAccount() throws PersistenceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		BulkEntryPersistenceService bulkEntryPersistanceService = new BulkEntryPersistenceService();
		List<BulkEntryInstallmentView> bulkEntryAccountActionViews = bulkEntryPersistanceService
				.getBulkEntryActionView(DateUtils
						.getCurrentDateWithoutTimeStamp(),
						center.getSearchId(), center.getOffice().getOfficeId(),
						AccountTypes.CUSTOMER_ACCOUNT);
		List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews = bulkEntryPersistanceService
				.getBulkEntryFeeActionView(DateUtils
						.getCurrentDateWithoutTimeStamp(),
						center.getSearchId(), center.getOffice().getOfficeId(),
						AccountTypes.CUSTOMER_ACCOUNT);
		assertNotNull(center.getCustomerAccount());
		BulkEntryView bulkEntryView = new BulkEntryView(getCustomerView(center));
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
				.getTotalAmountDue().getAmountDoubleValue(), 0.0);
	}

    public void testPopulateAttendance() 
    throws SystemException, ApplicationException{
        BulkEntryPersistenceService bulkEntryPersistanceService = new BulkEntryPersistenceService();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
                    .getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", 
        		CustomerStatus.CLIENT_ACTIVE,
                group);
         
        java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
         
        java.sql.Date sqlMeetingDate = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
         
        ClientAttendanceBO clientAttendance = new ClientAttendanceBO();
        clientAttendance.setAttendance(AttendanceType.ABSENT);
        clientAttendance.setMeetingDate(meetingDate);
        client.addClientAttendance(clientAttendance );
        customerPersistence.createOrUpdate(client);
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();
         
        List<BulkEntryClientAttendanceView> bulkEntryClientAttendanceView = bulkEntryPersistanceService
            .getBulkEntryClientAttendanceActionView(meetingDate, center.getOffice().getOfficeId() );
         
        BulkEntryView bulkEntryView = new BulkEntryView(getCustomerView(client));
        bulkEntryView.populateClientAttendance(client.getCustomerId(), sqlMeetingDate, bulkEntryClientAttendanceView);
         
        assertEquals(
                "Attendance was set",
                    clientAttendance.getAttendance().toString(), bulkEntryView.getAttendence().toString());
             
        BulkEntryBO bulkEntry = new BulkEntryBO();
        bulkEntry.setOffice(getOfficeView(center.getOffice()));
        bulkEntry.setLoanOfficer(getPersonnelView(center.getPersonnel()));
        bulkEntry.setPaymentType(getPaymentTypeView());
        bulkEntry.setTransactionDate(new java.sql.Date(System
                .currentTimeMillis()));
        CustomerView parentCustomer = getCustomerView(center);
        bulkEntry.buildBulkEntryView(parentCustomer);

        BulkEntryView parentBulkEntryView = bulkEntry.getBulkEntryParent();
        BulkEntryView groupBulkEntryView = parentBulkEntryView
                .getBulkEntryChildren().get(0);  
        BulkEntryView clientBulkEntryView = 
        	groupBulkEntryView.getBulkEntryChildren().get(0); 
             
        //System.out.println(clientBulkEntryView.getAttendence());
         
        assertEquals(
             "Testing BulkEntryBO.buildBulkEntryView",
             clientAttendance.getAttendance().toString(), 
             clientBulkEntryView.getAttendence().toString());
         
         HibernateUtil.closeSession();
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

	private OfficeView getOfficeView(OfficeBO office) throws OfficeException {
		OfficeView officeView = new OfficeView(office.getOfficeId(), office
				.getOfficeName(), office.getOfficeLevel().getValue(), office
				.getVersionNo());
		return officeView;
	}

	private CustomerView getCustomerView(CustomerBO customer) {
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

	
}
