/**

 * TestObjectFactory.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */
package org.mifos.framework.util.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.bulkentry.business.BulkEntryAccountActionView;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollSheetLnDetailsEntity;
import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntity;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.customer.business.CustomerAddressDetailEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerMeetingEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientNameDetailEntity;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.master.util.valueobjects.InterestCalcRule;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.InterestTypes;
import org.mifos.application.master.util.valueobjects.PrdApplicableMaster;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.MeetingRecurrenceEntity;
import org.mifos.application.meeting.business.MeetingTypeEntity;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingMeetingEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.valueobjects.GracePeriodType;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.scheduler.Constants;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.ScheduleInputsIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.util.UserContext;

/**
 * This class assumes that you are connected to the model database, which has
 * master data in it and also you have some default objects in it, for this you
 * can run the master data script and then the test scripts, this script has
 * statements for creating some default objects.
 * 
 * The convention followed here is that any method that starts with "get" is
 * returning an object already existing in the database , this object is not
 * meant to be modified and the method that starts with "create" creates a new
 * object inserts it into the database and returns that hence these objects are
 * meant to be cleaned up by the user.
 * 
 * @author ashishsm
 * 
 */
public class TestObjectFactory {

	private static TestObjectPersistence testObjectPersistence = new TestObjectPersistence();

	
	/**
	 * @return - Returns the office created by test data scripts. If the row
	 *         does not already exist in the database it returns null. defaults
	 *         created are 1- Head Office , 2 - Area Office , 3 - BranchOffice.
	 */
	public static OfficeBO getOffice(Short officeId) {
		return testObjectPersistence.getOffice(officeId);
	}

	public static void removeObject(PersistentObject obj) {
		if (obj != null)
			testObjectPersistence.removeObject(obj);
	}

	/**
	 * @return - Returns the personnel created by master data scripts. This
	 *         record does not have any custom fields or roles associated with
	 *         it. If the row does not already exist in the database it returns
	 *         null.
	 */

	public static PersonnelBO getPersonnel(Short personnelId) {
		return testObjectPersistence.getPersonnel(personnelId);
	}

	/**
	 * This is just a helper method which returns a new cust account object to
	 * be used else where . The method does not persist it.
	 */
	public static CustomerAccountBO getCustAccountsHelper(Short createdBy,
			CustomerBO customer, Date startDate) {
		CustomerAccountBO custAccount = new CustomerAccountBO();

		custAccount.setAccountState(new AccountStateEntity(
				AccountStates.CUSTOMERACCOUNT_ACTIVE));

		AccountType accountType = new AccountType();
		accountType.setAccountTypeId(Short
				.valueOf(AccountTypes.CUSTOMERACCOUNT));
		custAccount.setAccountType(accountType);

		OfficeBO office = customer.getOffice();
		custAccount.setOffice(office);
		custAccount.setCustomer(customer);
		PersonnelBO personnel = customer.getPersonnel();
		custAccount.setPersonnel(personnel);

		custAccount.setCreatedBy(createdBy);
		custAccount
				.setCreatedDate(new java.sql.Date(System.currentTimeMillis()));
		MifosCurrency currency = testObjectPersistence.getCurrency();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(custAccount);
		accountPeriodicFee.setAccountFeeAmount(new Money(currency, "100.0"));
		accountPeriodicFee.setFeeAmount(new Money(currency, "100.0"));
		FeeBO maintanenceFee = createPeriodicAmountFee("Mainatnence Fee", FeeCategory.ALLCUSTOMERS, "100",MeetingFrequency.WEEKLY,
		Short.valueOf("1"));
		accountPeriodicFee.setFees(maintanenceFee);
		custAccount.addAccountFees(accountPeriodicFee);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		customer.getCustomerMeeting().getMeeting()
				.setMeetingStartDate(calendar);
		List<Date> meetingDates = getMeetingDates(customer.getCustomerMeeting()
				.getMeeting(), 3);

		short i = 0;
		for (Date date : meetingDates) {
			AccountActionDateEntity actionDate = new AccountActionDateEntity();
			actionDate.setAccount(custAccount);
			actionDate.setActionDate(new java.sql.Date(date.getTime()));
			// actionDate.setCurrency(currency);
			actionDate.setCustomer(customer);
			actionDate.setInstallmentId(++i);
			actionDate.setDeposit(new Money(currency, "0.0"));
			actionDate.setDepositPaid(new Money(currency, "0.0"));
			actionDate.setPenalty(new Money(currency, "0.0"));
			actionDate.setPenaltyPaid(new Money(currency, "0.0"));
			actionDate.setMiscFee(new Money(currency, "0.0"));
			actionDate.setMiscFeePaid(new Money(currency, "0.0"));
			actionDate.setMiscPenalty(new Money(currency, "0.0"));
			actionDate.setMiscPenaltyPaid(new Money(currency, "0.0"));
			actionDate.setPrincipal(new Money(currency, "0.0"));
			actionDate.setPrincipalPaid(new Money(currency, "0.0"));
			actionDate.setInterest(new Money(currency, "0.0"));
			actionDate.setInterestPaid(new Money(currency, "0.0"));
			actionDate.setPaymentStatus(Short.valueOf("0"));

			custAccount.addAccountActionDate(actionDate);

			AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
			accountFeesaction.setAccountFee(accountPeriodicFee);
			accountFeesaction.setAccountActionDate(actionDate);
			accountFeesaction.setFee(maintanenceFee);
			accountFeesaction.setFeeAmount(new Money(currency, "100.0"));
			accountFeesaction.setFeeAmountPaid(new Money(currency, "0.0"));
			accountFeesaction.setInstallmentId(i);

			actionDate.addAccountFeesAction(accountFeesaction);
		}

		return custAccount;
	}

	/**
	 * This center is created belonging to branch office with its loan officer
	 * set .Global cust name is same as customerName
	 * 
	 * @param customerName -
	 *            name of the customer.
	 * @param statusId -
	 *            primary key of the customer status table.
	 * @param searchId -
	 *            searchId for the center being created.
	 * @param meeting -
	 *            Meeting object for the center,this could be obtained using
	 *            other helper methods in the class.
	 */
	public static CenterBO createCenter(String customerName, Short statusId,
			String searchId, MeetingBO meeting, Date startDate) {
		CenterBO center = new CenterBO();

		OfficeBO office = getOffice(new Short("3"));

		PersonnelBO personnel = getPersonnel(new Short("1"));
		CustomerMeetingEntity customerMeetingEntity = new CustomerMeetingEntity();
		customerMeetingEntity.setMeeting(meeting);
		customerMeetingEntity.setCustomer(center);
		customerMeetingEntity.setUpdatedFlag(YesNoFlag.NO.getValue());
		center.setCustomerMeeting(customerMeetingEntity);

		center.addCustomerAccount(getCustAccountsHelper(personnel
				.getPersonnelId(), center, startDate));

		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity();
		customerStatusEntity.setStatusId(statusId);

		CustomerLevelEntity custLevel = new CustomerLevelEntity();
		custLevel.setLevelId(CustomerConstants.CENTER_LEVEL_ID);

		center.setCreatedBy(personnel.getPersonnelId());
		center.setCreatedDate(new Date(System.currentTimeMillis()));

		center.setCustomerActivationDate(new Date(System.currentTimeMillis()));
		center.setDisplayName(customerName);
		center.setGlobalCustNum(customerName);
		center.setMfiJoiningDate(new Date(System.currentTimeMillis()));
		center.setOffice(office);
		center.setPersonnel(personnel);
		center.setCustomerStatus(customerStatusEntity);
		center.setSearchId(searchId);
		center.setCustomerLevel(custLevel);

		center.setCustomerAddressDetail(null);
		//center.setCustomerDetail(null);
		//center.setCustomerHierarchy(null);
		center.setCustomerHistoricalData(null);
		//center.setCustomerNote(null);

		center.setParentCustomer(null);
		return (CenterBO) testObjectPersistence.persist(center);
	}

	/**
	 * This is just a helper method which returns a address object , this is
	 * just a helper it does not persist any data.
	 */
	public static Address getAddressHelper() {
		Address address = new Address();
		address.setLine1("line1");
		address.setCity("city");
		address.setCountry("country");
		return address;
	}

	/**
	 * This group is created belonging to branch office with its loan officer
	 * set .Global cust name is same as customerName
	 * 
	 * @param customerName -
	 *            name of the customer.
	 * @param statusId -
	 *            primary key of the customer status table.
	 * @param searchId -
	 *            searchId for the group being created.
	 * @param parentCustomer -
	 *            parentCustomer to be set for the group being created. This
	 *            helps in creating hierarchies.
	 */
	public static GroupBO createGroup(String customerName, Short statusId,
			String searchId, CustomerBO parentCustomer, Date startDate) {
		GroupBO group = new GroupBO();

		OfficeBO office = getOffice(new Short("3"));

		PersonnelBO personnel = getPersonnel(new Short("1"));

		CustomerMeetingEntity customerMeetingEntity = new CustomerMeetingEntity();
		customerMeetingEntity.setMeeting(parentCustomer.getCustomerMeeting()
				.getMeeting());
		customerMeetingEntity.setCustomer(group);
		customerMeetingEntity.setUpdatedFlag(YesNoFlag.NO.getValue());
		group.setCustomerMeeting(customerMeetingEntity);

		group.addCustomerAccount(getCustAccountsHelper(personnel
				.getPersonnelId(), group, startDate));

		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity();
		customerStatusEntity.setStatusId(statusId);

		CustomerLevelEntity custLevel = new CustomerLevelEntity();
		custLevel.setLevelId(CustomerConstants.GROUP_LEVEL_ID);

		CustomerAddressDetailEntity custAddressDetail = new CustomerAddressDetailEntity();
		custAddressDetail.setCustomer(group);
		custAddressDetail.setAddress(getAddressHelper());

		group.setCreatedBy(personnel.getPersonnelId());
		group.setCreatedDate(new Date(System.currentTimeMillis()));

		group.setCustomerActivationDate(new Date(System.currentTimeMillis()));
		group.setDisplayName(customerName);
		group.setCustomerAddressDetail(custAddressDetail);
		group.setGlobalCustNum(customerName);
		group.setMfiJoiningDate(new Date(System.currentTimeMillis()));
		group.setOffice(office);
		group.setPersonnel(personnel);
		group.setCustomerStatus(customerStatusEntity);
		group.setSearchId(searchId);
		group.setCustomerLevel(custLevel);

	//	group.setCustomerDetail(null);
		//group.setCustomerHierarchy(null);
		group.setCustomerHistoricalData(null);
		//group.setCustomerNote(null);
		group.setParentCustomer(parentCustomer);
		return (GroupBO) testObjectPersistence.persist(group);
	}

	/**
	 * This client is created belonging to branch office with its loan officer
	 * set .Global cust name is same as customerName
	 * 
	 * @param customerName -
	 *            name of the customer.
	 * @param statusId -
	 *            primary key of the customer status table.
	 * @param searchId -
	 *            searchId for the client being created.
	 * @param parentCustomer -
	 *            parentCustomer to be set for the client being created. This
	 *            helps in creating hierarchies.
	 */
	public static ClientBO createClient(String customerName, Short statusId,
			String searchId, CustomerBO parentCustomer, Date startDate) {

		ClientBO client = new ClientBO();

		OfficeBO office = getOffice(new Short("3"));

		PersonnelBO personnel = getPersonnel(new Short("1"));

		CustomerMeetingEntity customerMeetingEntity = new CustomerMeetingEntity();
		customerMeetingEntity.setMeeting(parentCustomer.getCustomerMeeting()
				.getMeeting());
		customerMeetingEntity.setCustomer(client);
		customerMeetingEntity.setUpdatedFlag(YesNoFlag.NO.getValue());
		client.setCustomerMeeting(customerMeetingEntity);

		client.addCustomerAccount(getCustAccountsHelper(personnel
				.getPersonnelId(), client, startDate));

		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity();
		customerStatusEntity.setStatusId(statusId);

		CustomerLevelEntity custLevel = new CustomerLevelEntity();
		custLevel.setLevelId(CustomerConstants.CLIENT_LEVEL_ID);

		CustomerAddressDetailEntity custAddressDetail = new CustomerAddressDetailEntity();
		custAddressDetail.setCustomer(client);
		custAddressDetail.setAddress(getAddressHelper());

		Name name = new Name();
		name.setFirstName(customerName);
		name.setLastName(customerName);

		ClientNameDetailEntity customerNameDetail = new ClientNameDetailEntity();
		customerNameDetail.setName(name);
		customerNameDetail.setClient(client);

		Set<ClientNameDetailEntity> custNameDetEnitites = new HashSet<ClientNameDetailEntity>();
		custNameDetEnitites.add(customerNameDetail);

		client.setCreatedBy(personnel.getPersonnelId());
		client.setCreatedDate(new Date(System.currentTimeMillis()));

		client.setCustomerActivationDate(new Date(System.currentTimeMillis()));
		client.setDisplayName(customerName);
		client.setCustomerAddressDetail(custAddressDetail);
		client.setGlobalCustNum(customerName);
		client.setMfiJoiningDate(new Date(System.currentTimeMillis()));
		client.setOffice(office);
		client.setPersonnel(personnel);
		client.setCustomerStatus(customerStatusEntity);
		client.setSearchId(searchId);
		client.setCustomerLevel(custLevel);

		client.setCustomerDetail(null);
		//client.setCustomerHierarchy(null);
		client.setCustomerHistoricalData(null);
		//client.setCustomerNote(null);
		client.setParentCustomer(parentCustomer);
		//client.setCustomerNameDetailSet(custNameDetEnitites);
		return (ClientBO) testObjectPersistence.persist(client);
	}

	/**
	 * @param name -
	 *            name of the prd offering
	 * @param applicableTo -
	 * @param startDate -
	 *            start date of the product
	 * @param offeringStatusId -
	 *            primary key of prd_status table
	 * @param defLnAmnt -
	 *            same would be set as min and max amounts
	 * @param defIntRate -
	 *            same would be set as min and max amounts
	 * @param defInstallments-be
	 *            set as min and max amounts
	 * @param interestTypeId -
	 *            primary key of interest types table
	 * @param penaltyGrace -
	 *            installments for penalty grace
	 * @param intDedAtDisb -
	 *            flag
	 * @param princDueLastInst -
	 *            flag
	 * @param intCalcRuleId -
	 *            primary key of int calc rule table
	 * @param meeting -
	 *            meeting associated with the product offering
	 */
	public static LoanOfferingBO createLoanOffering(String name,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double defLnAmnt, Double defIntRate, Short defInstallments,
			Short interestTypeId, Short penaltyGrace, Short intDedAtDisb,
			Short princDueLastInst, Short intCalcRuleId, MeetingBO meeting) {

		LoanOfferingBO loanOffering = new LoanOfferingBO();

		OfficeBO office = getOffice(new Short("3"));
		PrdOfferingMeetingEntity prdOfferingMeeting = new PrdOfferingMeetingEntity();
		prdOfferingMeeting.setMeeting(meeting);
		prdOfferingMeeting.setPrdOffering(loanOffering);
		prdOfferingMeeting.setMeetingType(Short.valueOf("1"));

		PrdStatusEntity prdStatus = new PrdStatusEntity();

		InterestTypes interestType = new InterestTypes();
		interestType.setInterestTypeId(interestTypeId);

		InterestCalcRule interestCalcRule = new InterestCalcRule();
		interestCalcRule.setInterestCalcRuleId(intCalcRuleId);

		PersonnelBO personnel = getPersonnel(new Short("1"));

		PrdApplicableMaster prdApplicableMaster = new PrdApplicableMaster();
		prdApplicableMaster.setPrdApplicableMasterId(applicableTo);

		prdStatus = testObjectPersistence.retrievePrdStatus(offeringStatusId);

		loanOffering.setPrdApplicableMaster(prdApplicableMaster);
		loanOffering.setPrdCategory(getLoanPrdCategory());

		loanOffering.setPrdType(prdStatus.getPrdType());
		loanOffering.setOffice(office);
		loanOffering.setStartDate(startDate);
		loanOffering.setPrdOfferingName(name);
		loanOffering.setGlobalPrdOfferingNum(name);
		loanOffering.setDescription(name);
		loanOffering.setPrdOfferingShortName(name);
		loanOffering.setPrdStatus(prdStatus);
		loanOffering.setCreatedDate(new Date(System.currentTimeMillis()));
		loanOffering.setCreatedBy(personnel.getPersonnelId());
		loanOffering.setMinLoanAmount(defLnAmnt);
		loanOffering.setDefaultLoanAmount(defLnAmnt);
		loanOffering.setMaxLoanAmount(defLnAmnt);
		loanOffering.setMinInterestRate(defIntRate);
		loanOffering.setDefInterestRate(defIntRate);
		loanOffering.setMaxInterestRate(defIntRate);
		loanOffering.setMinNoInstallments(defInstallments);
		loanOffering.setDefNoInstallments(defInstallments);
		loanOffering.setMaxNoInstallments(defInstallments);
		loanOffering.setPenaltyGrace(penaltyGrace);
		loanOffering.setIntDedDisbursement(intDedAtDisb.intValue() == 0 ? false
				: true);
		loanOffering
				.setPrinDueLastInst(princDueLastInst.intValue() == 0 ? false
						: true);
		loanOffering.setInterestTypes(interestType);
		loanOffering.setInterestCalcRule(interestCalcRule);
		loanOffering.setPrdOfferingMeeting(prdOfferingMeeting);

		GLCodeEntity glCodePrincipal = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, Short.valueOf("11"));
		loanOffering.setPrincipalGLcode(glCodePrincipal);
		GLCodeEntity glCodeInterest = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, Short.valueOf("21"));
		loanOffering.setInterestGLcode(glCodeInterest);
		loanOffering.setPenaltyGLcode(glCodePrincipal);
		return (LoanOfferingBO) testObjectPersistence.persist(loanOffering);

	}

	public static ProductCategoryBO getLoanPrdCategory() {
		return testObjectPersistence.getLoanPrdCategory();
	}

	/**
	 * This method is incomplete.
	 * 
	 * @param globalNum
	 * @param accountStateId
	 * @throws Exception 
	 */
	public static LoanBO createLoanAccount(String globalNum,
			CustomerBO customer, Short accountStateId, Date startDate,
			LoanOfferingBO loanOfering) {
		LoanBO loan=null;
		try{
			UserContext userContext = TestObjectFactory.getUserContext();
			AccountType accountType = new AccountType();
			accountType.setAccountTypeId(Short.valueOf(AccountTypes.LOANACCOUNT));
			loan = new LoanBO(userContext,loanOfering,customer,accountType);
		}catch(Exception e){}

		loan.setAccountState(new AccountStateEntity(accountStateId));

		MifosCurrency currency = testObjectPersistence.getCurrency();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(loan);
		accountPeriodicFee.setAccountFeeAmount(new Money(currency, "100.0"));
		accountPeriodicFee.setFeeAmount(new Money(currency, "100.0"));
		FeeBO maintanenceFee = createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",MeetingFrequency.WEEKLY,
				Short.valueOf("1"));
		accountPeriodicFee.setFees(maintanenceFee);
		loan.addAccountFees(accountPeriodicFee);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		customer.getCustomerMeeting().getMeeting()
				.setMeetingStartDate(calendar);
		MeetingBO meeting = createLoanMeeting(customer.getCustomerMeeting()
				.getMeeting());
		loan.setLoanMeeting(meeting);

		List<Date> meetingDates = getMeetingDates(loan.getLoanMeeting(), 6);

		short i = 0;
		for (Date date : meetingDates) {
			AccountActionDateEntity actionDate = new AccountActionDateEntity();
			actionDate.setAccount(loan);
			actionDate.setActionDate(new java.sql.Date(date.getTime()));
			actionDate.setCustomer(customer);
			actionDate.setInstallmentId(++i);
			actionDate.setDeposit(new Money(currency, "0.0"));
			actionDate.setDepositPaid(new Money(currency, "0.0"));
			actionDate.setPenalty(new Money(currency, "0.0"));
			actionDate.setInterest(new Money(currency, "12.0"));
			actionDate.setInterestPaid(new Money(currency, "0.0"));
			actionDate.setPrincipalPaid(new Money(currency, "0.0"));
			actionDate.setPenaltyPaid(new Money(currency, "0.0"));
			actionDate.setMiscFee(new Money(currency, "0.0"));
			actionDate.setMiscFeePaid(new Money(currency, "0.0"));
			actionDate.setMiscPenaltyPaid(new Money(currency, "0.0"));
			actionDate.setMiscPenalty(new Money(currency, "0.0"));
			actionDate.setPaymentStatus(Short.valueOf("0"));
			actionDate.setPrincipal(new Money(currency, "100.0"));
			loan.addAccountActionDate(actionDate);

			AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
			accountFeesaction.setAccountFee(accountPeriodicFee);
			accountFeesaction.setAccountActionDate(actionDate);
			accountFeesaction.setFee(maintanenceFee);
			accountFeesaction.setFeeAmount(new Money(currency, "100.0"));
			accountFeesaction.setFeeAmountPaid(new Money(currency, "0.0"));
			accountFeesaction.setInstallmentId(i);

			actionDate.addAccountFeesAction(accountFeesaction);
		}

		loan.setLoanAmount(new Money(currency, "300.0"));
		loan.setLoanBalance(new Money(currency, "300.0"));
		loan.setNoOfInstallments(Short.valueOf("6"));

		GracePeriodType gracePeriodType = new GracePeriodType();
		gracePeriodType.setGracePeriodTypeId(Short.valueOf("1"));
		loan.setGracePeriodType(gracePeriodType);
		loan.setCreatedBy(Short.valueOf("1"));
		loan.setCreatedDate(new Date(System.currentTimeMillis()));

		LoanSummaryEntity loanSummary = new LoanSummaryEntity();
		loanSummary.setLoan(loan);
		loanSummary.setOriginalFees(new Money(currency, "0.0"));
		loanSummary.setOriginalPrincipal(new Money(currency, "300.0"));
		loanSummary.setOriginalInterest(new Money(currency, "36.0"));
		loanSummary.setPenaltyPaid(new Money(currency, "0.0"));
		loanSummary.setOriginalPenalty(new Money(currency, "0.0"));
		loanSummary.setPrincipalPaid(new Money(currency, "0.0"));
		loanSummary.setInterestPaid(new Money(currency, "0.0"));
		loanSummary.setFeesPaid(new Money(currency, "0.0"));
		loan.setLoanSummary(loanSummary);

		return (LoanBO) testObjectPersistence.persist(loan);
	}

	public static SavingsOfferingBO createSavingsOffering(String name,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double recommenededAmt, Short recomAmtUnitId, Double intRate,
			Double maxAmtWithdrawl, Double minAmtForInt, Short savingsTypeId,
			Short intCalTypeId, MeetingBO intCalcMeeting,
			MeetingBO intPostMeeting) {

		SavingsOfferingBO savingsOffering = new SavingsOfferingBO();
		OfficeBO office = getOffice(new Short("3"));

		PrdOfferingMeetingEntity timeForIntCalc = new PrdOfferingMeetingEntity();
		timeForIntCalc.setMeeting(intCalcMeeting);
		timeForIntCalc.setPrdOffering(savingsOffering);
		timeForIntCalc.setMeetingType(Short.valueOf("1"));

		PrdOfferingMeetingEntity timeForIntPost = new PrdOfferingMeetingEntity();
		timeForIntPost.setMeeting(intPostMeeting);
		timeForIntPost.setPrdOffering(savingsOffering);
		timeForIntPost.setMeetingType(Short.valueOf("1"));

		savingsOffering.setTimePerForInstcalc(timeForIntCalc);
		savingsOffering.setFreqOfPostIntcalc(timeForIntPost);

		PrdStatusEntity prdStatus = new PrdStatusEntity();

		PersonnelBO personnel = getPersonnel(new Short("1"));

		PrdApplicableMaster prdApplicableMaster = new PrdApplicableMaster();
		prdApplicableMaster.setPrdApplicableMasterId(applicableTo);

		prdStatus = testObjectPersistence.retrievePrdStatus(offeringStatusId);
		savingsOffering.setPrdApplicableMaster(prdApplicableMaster);
		savingsOffering.setPrdCategory(getLoanPrdCategory());
		savingsOffering.setPrdType(prdStatus.getPrdType());
		savingsOffering.setOffice(office);
		savingsOffering.setStartDate(startDate);
		savingsOffering.setPrdOfferingName(name);
		savingsOffering.setGlobalPrdOfferingNum(name);
		savingsOffering.setDescription(name);
		savingsOffering.setPrdOfferingShortName(name);
		savingsOffering.setPrdStatus(prdStatus);
		savingsOffering.setCreatedDate(new Date(System.currentTimeMillis()));
		savingsOffering.setCreatedBy(personnel.getPersonnelId());
		MifosCurrency currency = testObjectPersistence.getCurrency();
		savingsOffering.setRecommendedAmount(new Money(recommenededAmt.toString()));
		RecommendedAmntUnit amountUnit = new RecommendedAmntUnit();
		amountUnit.setRecommendedAmntUnitId(recomAmtUnitId);
		savingsOffering.setRecommendedAmntUnit(amountUnit);

		savingsOffering.setInterestRate(intRate);
		savingsOffering
				.setMaxAmntWithdrawl(new Money(maxAmtWithdrawl.toString()));
		savingsOffering.setMinAmntForInt(new Money( minAmtForInt.toString()));

		SavingsType savingsType = new SavingsType();
		savingsType.setSavingsTypeId(savingsTypeId);
		savingsOffering.setSavingsType(savingsType);

		InterestCalcType intCalType = new InterestCalcType();
		intCalType.setInterestCalculationTypeID(intCalTypeId);
		savingsOffering.setInterestCalcType(intCalType);

		GLCodeEntity glCodeEntity = (GLCodeEntity) HibernateUtil.getSessionTL()
				.get(GLCodeEntity.class, Short.valueOf("7"));
		savingsOffering.setDepositGLCode(glCodeEntity);
		savingsOffering.setInterestGLCode(glCodeEntity);

		return (SavingsOfferingBO) testObjectPersistence
				.persist(savingsOffering);
	}

	public static SavingsBO createSavingsAccount(String globalNum,
			CustomerBO customer, Short accountStateId, Date startDate,
			SavingsOfferingBO savingsOffering) {
		UserContext userContext = null;
		try {
			userContext = getUserContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SavingsBO savings = new SavingsBO(userContext);
		savings.setCustomer(customer);
		savings.setPersonnel(customer.getPersonnel());
		savings.setAccountState(new AccountStateEntity(accountStateId));
		savings.setSavingsOffering(savingsOffering);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));
		MifosCurrency currency = testObjectPersistence.getCurrency();
		savings.setRecommendedAmount(new Money(currency, "300.0"));

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		customer.getCustomerMeeting().getMeeting()
				.setMeetingStartDate(calendar);
		MeetingBO meeting = createLoanMeeting(customer.getCustomerMeeting()
				.getMeeting());

		try {
			savings.save();
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		savings.setAccountState(new AccountStateEntity(accountStateId));
		savings.setGlobalAccountNum(globalNum);
		savings.setActivationDate(new Date(System.currentTimeMillis()));
		List<Date> meetingDates = getMeetingDates(meeting, 3);
		short i = 0;
		for (Date date : meetingDates) {
			AccountActionDateEntity actionDate = new AccountActionDateEntity();
			actionDate.setAccount(savings);
			actionDate.setActionDate(new java.sql.Date(date.getTime()));
			actionDate.setCustomer(customer);
			actionDate.setInstallmentId(++i);
			actionDate.setDeposit(new Money(currency, "200.0"));
			actionDate.setPenalty(new Money(currency, "0.0"));
			actionDate.setInterest(new Money(currency, "0.0"));
			actionDate.setInterestPaid(new Money(currency, "0.0"));
			actionDate.setPaymentStatus(Short.valueOf("0"));
			actionDate.setPrincipal(new Money(currency, "0.0"));
			savings.addAccountActionDate(actionDate);
		}
		HibernateUtil.commitTransaction();
		return (SavingsBO) getObject(SavingsBO.class, savings.getAccountId());
	}

	public static SavingsBO createSavingsAccount(String globalNum,
			CustomerBO customer, Short accountStateId, Date startDate,
			SavingsOfferingBO savingsOffering, UserContext userContext) {
		try {
			userContext = getUserContext();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		SavingsBO savings = new SavingsBO(userContext);

		savings.setCustomer(customer);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));
		savings.setPersonnel(customer.getPersonnel());
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.setSavingsOffering(savingsOffering);

		Set<AccountCustomFieldEntity> customFields = new HashSet<AccountCustomFieldEntity>();
		AccountCustomFieldEntity custField1 = new AccountCustomFieldEntity();
		custField1.setFieldId(new Short("1"));
		custField1.setFieldValue("custom field value");
		custField1.setAccount(savings);
		customFields.add(custField1);
		savings.setAccountCustomFields(customFields);

		try {
			savings.save();
			savings.setAccountState(new AccountStateEntity(accountStateId));
			savings.setGlobalAccountNum(globalNum);
			savings.setActivationDate(new Date(System.currentTimeMillis()));
			HibernateUtil.commitTransaction();
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return (SavingsBO) getObject(SavingsBO.class, savings.getAccountId());
	}

	public static MeetingBO createLoanMeeting(MeetingBO customerMeeting) {
		MeetingBO meetingToReturn = new MeetingBO();
		meetingToReturn.setMeetingStartDate(customerMeeting
				.getMeetingStartDate());
		meetingToReturn.setMeetingPlace("");
		meetingToReturn.setMeetingType(customerMeeting.getMeetingType());
		MeetingRecurrenceEntity meetingRecToReturn = new MeetingRecurrenceEntity();
		meetingRecToReturn.setDayNumber(customerMeeting.getMeetingDetails()
				.getMeetingRecurrence().getDayNumber());
		meetingRecToReturn.setRankOfDays(customerMeeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays());
		meetingRecToReturn.setWeekDay(customerMeeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDay());
		MeetingDetailsEntity meetingDetailsToReturn = new MeetingDetailsEntity();
		meetingDetailsToReturn.setMeetingRecurrence(meetingRecToReturn);
		meetingDetailsToReturn.setRecurAfter(customerMeeting
				.getMeetingDetails().getRecurAfter());
		meetingDetailsToReturn.setRecurrenceType(customerMeeting
				.getMeetingDetails().getRecurrenceType());
		meetingToReturn.setMeetingDetails(meetingDetailsToReturn);
		return meetingToReturn;

	}

	private static List<Date> getMeetingDates(MeetingBO meeting, int occurrences) {
		List<Date> dates = new ArrayList<Date>();
		ScheduleDataIntf scheduleData;
		SchedulerIntf scheduler;
		try {
			scheduleData = SchedulerFactory
					.getScheduleData(getReccurence(meeting.getMeetingDetails()
							.getRecurrenceType().getRecurrenceId()));
			scheduler = getScheduler(scheduleData, meeting);
			dates = scheduler.getAllDates(occurrences);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return dates;
	}

	public static List<Date> getAllMeetingDates(MeetingBO meeting) {
		List<Date> dates = new ArrayList<Date>();
		ScheduleDataIntf scheduleData;
		SchedulerIntf scheduler;
		try {
			scheduleData = SchedulerFactory
					.getScheduleData(getReccurence(meeting.getMeetingDetails()
							.getRecurrenceType().getRecurrenceId()));
			scheduler = getScheduler(scheduleData, meeting);
			dates = scheduler.getAllDates();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return dates;
	}

	private static String getReccurence(Short recurrenceId) {
		if (recurrenceId.intValue() == 1)
			return Constants.WEEK;
		if (recurrenceId.intValue() == 2)
			return Constants.MONTH;
		if (recurrenceId.intValue() == 3)
			return Constants.MONTH;
		return "";
	}

	private static SchedulerIntf getScheduler(ScheduleDataIntf scheduleData,
			MeetingBO meeting) {
		SchedulerIntf scheduler;
		ScheduleInputsIntf scheduleInputs;
		scheduler = SchedulerFactory.getScheduler();
		try {
			scheduleInputs = SchedulerFactory.getScheduleInputs();
			scheduleInputs
					.setStartDate(meeting.getMeetingStartDate().getTime());
			scheduleData.setRecurAfter(meeting.getMeetingDetails()
					.getRecurAfter().intValue());
			if (scheduleData.getClass().getName().equals(
					"org.mifos.framework.components.scheduler.WeekData")) {
				scheduleData.setWeekDay(meeting.getMeetingDetails()
						.getMeetingRecurrence().getWeekDay().getWeekDayId()
						.intValue());
			} else if (scheduleData.getClass().getName().equals(
					"org.mifos.framework.components.scheduler.MonthData")) {
				Short recurrenceId = meeting.getMeetingDetails()
						.getRecurrenceType().getRecurrenceId();
				if (meeting.getMeetingDetails().getMeetingRecurrence()
						.getDayNumber() != null)
					scheduleData.setDayNumber(meeting.getMeetingDetails()
							.getMeetingRecurrence().getDayNumber().intValue());
				else {
					scheduleData.setWeekDay(meeting.getMeetingDetails()
							.getMeetingRecurrence().getWeekDay().getWeekDayId()
							.intValue());

					scheduleData.setWeekRank(meeting.getMeetingDetails()
							.getMeetingRecurrence().getRankOfDays()
							.getRankOfDayId().intValue());
				}
			}
			scheduleInputs.setScheduleData(scheduleData);
			scheduler.setScheduleInputs(scheduleInputs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scheduler;
	}

	public static FeeBO createPeriodicAmountFee(String feeName, FeeCategory feeCategory, String feeAmnt,
			MeetingFrequency meetingFrequency, Short recurAfter) {
		GLCodeEntity glCode = (GLCodeEntity) HibernateUtil.getSessionTL().get(GLCodeEntity.class, Short.valueOf("24"));
		MeetingBO meeting  = new MeetingBO(meetingFrequency,recurAfter,MeetingType.FEEMEETING);
		FeeBO fee = null;
		//TODO: throw exception
		try{
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(),feeName, 
				new CategoryTypeEntity(feeCategory), new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				glCode, getMoneyForMFICurrency(feeAmnt),false, meeting);
		
		}catch(Exception e){}
		return testObjectPersistence.createFee(fee);
	}


	public static FeeBO createOneTimeAmountFee(String feeName, FeeCategory feeCategory, String feeAmnt, FeePayment feePayment) {
		GLCodeEntity glCode = (GLCodeEntity) HibernateUtil.getSessionTL().get(GLCodeEntity.class, Short.valueOf("24"));
		FeeBO fee = null;
		//TODO: throw exception
		try{
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(),feeName, 
				new CategoryTypeEntity(feeCategory), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				glCode, getMoneyForMFICurrency(feeAmnt),false, new FeePaymentEntity(feePayment));
		
		}catch(Exception e){}
		return testObjectPersistence.createFee(fee);
	}
	
	public static FeeBO createOneTimeRateFee(String feeName, FeeCategory feeCategory, Double rate, FeeFormula feeFormula, FeePayment feePayment) {
		GLCodeEntity glCode = (GLCodeEntity) HibernateUtil.getSessionTL().get(GLCodeEntity.class, Short.valueOf("24"));
		FeeBO fee = null;
		//TODO: throw exception
		try{
			fee = new RateFeeBO(TestObjectFactory.getUserContext(),feeName, 
				new CategoryTypeEntity(feeCategory), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				glCode, rate, new FeeFormulaEntity(feeFormula),false, new FeePaymentEntity(feePayment));
		
		}catch(Exception e){}
		return testObjectPersistence.createFee(fee);
	}
	
	/**
	 * This is a helper method which returns a fresh meeting object based on the
	 * parameters passed to it.
	 * 
	 * @param frequency -
	 *            Weekly(1)/Monthly(2)
	 * @param recurAfter -
	 *            specifying the recurrence pattern
	 * @param meetingTypeId -
	 *            1 -loan repayment,2 -savings interest calculation,3 -savings
	 *            interest posting 4 - customer meeting,5 - fees meeting.
	 */
	public static MeetingBO getMeetingHelper(int frequency, int recurAfter,
			int meetingTypeId) {
		MeetingBO meeting = getMeeting(Integer.toString(frequency), Integer
				.toString(recurAfter), new Integer(meetingTypeId).shortValue());
		return meeting;
	}

	/**
	 * This is a helper method which returns a fresh meeting object based on the
	 * parameters passed to it.
	 * 
	 * @param frequency -
	 *            Weekly(1)/Monthly(2)
	 * @param recurAfter -
	 *            specifying the recurrence pattern
	 * @param meetingTypeId -
	 *            1 -loan repayment,2 -savings interest calculation,3 -savings
	 *            interest posting 4 - customer meeting,5 - fees meeting.
	 * @param dayOfWeek -
	 *            1 - Sunday .... ,7 Saturday.
	 */
	public static MeetingBO getMeetingHelper(int frequency, int recurAfter,
			int meetingTypeId, int dayOfWeek) {
		MeetingBO meeting = getMeetingHelper(frequency, recurAfter,
				meetingTypeId);
		WeekDaysEntity weekDays = new WeekDaysEntity();
		Calendar cal = new GregorianCalendar();
		weekDays.setWeekDayId(Short.valueOf(Integer.toString(cal
				.get(Calendar.DAY_OF_WEEK))));
		meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(weekDays);
		return meeting;
	}

	public static MeetingBO createMeeting(MeetingBO meeting) {
		return (MeetingBO) testObjectPersistence.persist(meeting);
	}

	/**
	 * This is a helper method to return MeetingBO object.
	 */
	public static MeetingBO getMeeting(String frequency, String recurAfter,
			Short meetingTypeId) {

		MeetingBO meeting = new MeetingBO();

		MeetingTypeEntity meetingType = new MeetingTypeEntity();
		meetingType.setMeetingTypeId(meetingTypeId);
		meeting.setMeetingType(meetingType);

		MeetingDetailsEntity meetingDetails = new MeetingDetailsEntity();
		Short frequencyId = null;
		if (null != frequency) {
			frequencyId = Short.valueOf(frequency);
		}
		if (null != recurAfter) {
			meetingDetails.setRecurAfter(Short.valueOf(recurAfter));
		}

		RecurrenceTypeEntity recurrenceType = new RecurrenceTypeEntity();
		recurrenceType.setRecurrenceId(frequencyId);

		meetingDetails.setRecurrenceType(recurrenceType);
		meetingDetails.setMeetingRecurrence(new MeetingRecurrenceEntity());

		meeting.setMeetingDetails(meetingDetails);
		meeting.setMeetingPlace("Loan Meeting Place");

		return meeting;
	}

	public static void cleanUp(CustomerBO customer) {
		if (null != customer) {
			deleteCustomer(customer);
		}
	}

	public static void cleanUp(List<CustomerBO> customerList) {
		if (null != customerList) {
			deleteCustomers(customerList);
		}
	}
	
	public static void cleanUp(FeeBO fee) {
		if (null != fee) {
			deleteFee(fee);
		}
	}
	
	public static void cleanUp(AccountBO account) {
		if (null != account) {
			deleteAccount(account, null);
		}
	}

	private static void deleteFee(FeeBO fee) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		if(fee.isPeriodic()){
			session.delete(fee.getFeeFrequency().getFeeMeetingFrequency());
		}
		session.delete(fee);
		transaction.commit();
	}
	
	private static void deleteFees(List<FeeBO> feeList) {
		Session session = HibernateUtil.getSessionTL();
		for(FeeBO fee:feeList){
			if(fee.isPeriodic()){
				session.delete(fee.getFeeFrequency().getFeeMeetingFrequency());
			}
			session.delete(fee);
		}
	}
	
	
	
	private static void deleteAccountPayments(AccountBO account){
		Session session = HibernateUtil.getSessionTL();
		for (AccountPaymentEntity accountPayment : account.getAccountPayments()) {
			if (null != accountPayment) {
				deleteAccountPayment(accountPayment, session);
			}
		}
	}

	private static void deleteAccountActionDates(AccountBO account){
		Session session = HibernateUtil.getSessionTL();
		for (AccountActionDateEntity actionDates : account
				.getAccountActionDates()) {
			for (AccountFeesActionDetailEntity actionFees : actionDates
					.getAccountFeesActionDetails()) {
				session.delete(actionFees);
			}
			session.delete(actionDates);
		}
	}

	private static void deleteAccountFees(AccountBO account){
		Session session = HibernateUtil.getSessionTL();
		for (AccountFeesEntity accountFees : account.getAccountFees()) {
			session.delete(accountFees);
		}
	}
	
	private static void deleteSpecificAccount(AccountBO account){
		Session session = HibernateUtil.getSessionTL();
		if (account instanceof LoanBO) {

			LoanBO loan = (LoanBO) account;
			if (null != loan.getLoanSummary()) {
				session.delete(loan.getLoanSummary());
			}
			session.delete(account);
			loan.getLoanOffering().getPrdOfferingMeeting().setMeeting(null);
			session.delete(loan.getLoanOffering().getPrdOfferingMeeting());
			session.delete(loan.getLoanOffering());

		}
		if (account instanceof SavingsBO) {

			SavingsBO savings = (SavingsBO) account;
			session.delete(account);
			session.delete(savings.getTimePerForInstcalc());
			for (PrdOfferingMeetingEntity prdOfferingMeeting : savings
					.getSavingsOffering().getPrdOfferingMeetings()) {
				prdOfferingMeeting.setMeeting(null);
				session.delete(prdOfferingMeeting);
			}

			session.delete(savings.getSavingsOffering());
		} else {
			session.delete(account);
		}
	}
	
	private static void deleteAccountWithoutFee(AccountBO account) {
		deleteAccountPayments(account);
		deleteAccountActionDates(account);
		deleteAccountFees(account);
		deleteSpecificAccount(account);
	}
	
	private static void deleteAccount(AccountBO account, Session session) {
		boolean newSession = false;
		Transaction transaction = null;
		if (null == session) {
			session = HibernateUtil.getSessionTL();
			transaction = HibernateUtil.startTransaction();
			newSession = true;
		}
		
		List<FeeBO> feeList = new ArrayList<FeeBO>(); 
		for(AccountFeesEntity accountFees: account.getAccountFees()){
			if(!feeList.contains(accountFees.getFees()))
					feeList.add(accountFees.getFees());
		}
		
		deleteAccountPayments(account);
		deleteAccountActionDates(account);
		deleteAccountFees(account);
		deleteSpecificAccount(account);
		deleteFees(feeList);
		
		if (newSession) {
			transaction.commit();
		}
	}

	private static void deleteAccountPayment(
			AccountPaymentEntity accountPayment, Session session) {
		Set<AccountTrxnEntity> loanTrxns = accountPayment.getAccountTrxns();
		for (AccountTrxnEntity accountTrxn : loanTrxns) {
			if (accountTrxn instanceof LoanTrxnDetailEntity) {
				LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accountTrxn;
				for (FeesTrxnDetailEntity feesTrxn : loanTrxn
						.getFeesTrxnDetails()) {
					session.delete(feesTrxn);
				}
				for (FinancialTransactionBO financialTrxn : loanTrxn
						.getFinancialTransactions()) {
					session.delete(financialTrxn);
				}

				session.delete(loanTrxn);

			}
		}

		session.delete(accountPayment);
	}

	private static void deleteCustomers(List<CustomerBO> customerList){
		List<FeeBO> feeList = new ArrayList<FeeBO>();
		for(CustomerBO customer: customerList){
			Session session = HibernateUtil.getSessionTL();
			session.lock(customer, LockMode.UPGRADE);
			for(AccountBO account: customer.getAccounts()){
				for(AccountFeesEntity accountFees: account.getAccountFees()){
					if(!feeList.contains(accountFees.getFees()))
							feeList.add(accountFees.getFees());
				}
			}
			Transaction transaction = HibernateUtil.startTransaction();
			deleteCustomerWithoutFee(customer);			
			transaction.commit();
		}
		Transaction transaction = HibernateUtil.startTransaction();
		deleteFees(feeList);
		transaction.commit();
	}
	
	private static void deleteCustomerWithoutFee(CustomerBO customer) {
		Session session = HibernateUtil.getSessionTL();
		deleteCenterMeeting(customer);
		deleteClientAttendence(customer);
		for (AccountBO account : customer.getAccounts()) {
			if (null != account) {
				deleteAccountWithoutFee(account);
			}
		}
		session.delete(customer);
	}
	
	private static void deleteCustomer(CustomerBO customer) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.lock(customer, LockMode.UPGRADE);
		deleteCenterMeeting(customer);
		deleteClientAttendence(customer);
		
		List<FeeBO> feeList = new ArrayList<FeeBO>();
		for (AccountBO account : customer.getAccounts()) {
			if (null != account) {
				for(AccountFeesEntity accountFee: account.getAccountFees())
					if(!feeList.contains(accountFee.getFees()))
						feeList.add(accountFee.getFees());
				deleteAccountWithoutFee(account);
			}
		}
		session.delete(customer);
		deleteFees(feeList);
		transaction.commit();
	}

	private static void deleteCenterMeeting(CustomerBO customer){
		Session session = HibernateUtil.getSessionTL();
		if (customer instanceof CenterBO) {
			session.delete(customer.getCustomerMeeting());
		}
	}
	
	private static void deleteClientAttendence(CustomerBO customer){
		Session session = HibernateUtil.getSessionTL();
		if (customer instanceof ClientBO) {
			Set<ClientAttendanceBO> attendance = ((ClientBO) customer)
					.getClientAttendances();
			if (attendance != null && attendance.size() > 0)
				for (ClientAttendanceBO custAttendance : attendance) {
					// custAttendance.setCustomer(null);
					session.delete(custAttendance);
				}
		}
	}
	
	
	public static MifosCurrency getMFICurrency() {
		return testObjectPersistence.getCurrency();
	}

	public static MifosCurrency getCurrency(Short currencyId) {
		return testObjectPersistence.getCurrency(currencyId);
	}

	public static Money getMoneyForMFICurrency(double amnt) {
		return new Money( String.valueOf(amnt));
	}

	public static Money getMoneyForMFICurrency(String amnt) {
		return new Money(testObjectPersistence.getCurrency(), amnt);
	}
	
	public static Money getMoney(Short currencyId, double amnt) {
		return new Money(String.valueOf(amnt));
	}

	public static void updateObject(PersistentObject obj) {
		testObjectPersistence.update(obj);
	}

	public static UserContext getUserContext() throws Exception {

		byte[] password = EncryptionService.getInstance()
				.createEncryptedPassword("mifos");
		PersonnelBO personnel = getPersonnel(Short.valueOf("1"));
		personnel.setEncriptedPassword(password);
		updateObject(personnel);
		return org.mifos.framework.security.authentication.Authenticator
				.getInstance().validateUser("mifos", "mifos");

	}

	public static void flushandCloseSession() {
		testObjectPersistence.flushandCloseSession();
	}

	public static Object getObject(Class clazz, Integer pk) {
		return testObjectPersistence.getObject(clazz, pk);

	}

	public static Object getObject(Class clazz, Short pk) {
		return testObjectPersistence.getObject(clazz, pk);

	}

	public static void cleanUp(CheckListBO checkListBO) {
		if (null != checkListBO) {
			deleteChecklist(checkListBO);
		}
	}

	public static void deleteChecklist(CheckListBO checkListBO) {

		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();

		session.delete(checkListBO);
		transaction.commit();
	}

	public static void cleanUp(ReportsCategoryBO reportsCategoryBO) {
		if (null != reportsCategoryBO) {
			deleteReportCategory(reportsCategoryBO);
		}
	}

	public static void deleteReportCategory(ReportsCategoryBO reportsCategoryBO) {

		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.delete(reportsCategoryBO);
		transaction.commit();
	}

	public static void cleanUp(ReportsBO reportsBO) {
		if (null != reportsBO) {
			deleteReportCategory(reportsBO);
		}
	}

	public static void deleteReportCategory(ReportsBO reportsBO) {

		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		session.delete(reportsBO);
		transaction.commit();
	}

	public static LoanBO createLoanAccountWithDisbursement(String globalNum,
			CustomerBO customer, Short accountStateId, Date startDate,
			LoanOfferingBO loanOfering, int disbursalType) {
		LoanBO loan=null;
		try{
			UserContext userContext=TestObjectFactory.getUserContext();
			AccountType accountType = new AccountType();
			accountType.setAccountTypeId(Short.valueOf(AccountTypes.LOANACCOUNT));
			loan = new LoanBO(userContext,loanOfering,customer,accountType);
		}catch(Exception e){}
		loan.setAccountState(new AccountStateEntity(accountStateId));

		MifosCurrency currency = testObjectPersistence.getCurrency();
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity();
		accountPeriodicFee.setAccount(loan);
		accountPeriodicFee.setAccountFeeAmount(new Money(currency, "10.0"));
		accountPeriodicFee.setFeeAmount(new Money(currency, "10.0"));
		FeeBO maintanenceFee = createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",MeetingFrequency.WEEKLY,
				Short.valueOf("1"));
		accountPeriodicFee.setFees(maintanenceFee);
		loan.addAccountFees(accountPeriodicFee);
		AccountFeesEntity accountDisbursementFee = null;
		FeeBO disbursementFee = null;
		AccountFeesEntity accountDisbursementFee2 = null;
		FeeBO disbursementFee2 = null;

		if (disbursalType == 1 || disbursalType == 2) {
			accountDisbursementFee = new AccountFeesEntity();
			accountDisbursementFee.setAccountFeeAmount(new Money(currency,
					"10.0"));
			accountDisbursementFee.setFeeAmount(new Money(currency, "10.0"));
			disbursementFee = createOneTimeAmountFee("Disbursement Fee 1", FeeCategory.LOAN, "10",
					FeePayment.TIME_OF_DISBURSMENT);
			accountDisbursementFee.setFees(disbursementFee);
			accountDisbursementFee.setAccount(loan);
			loan.addAccountFees(accountDisbursementFee);

			accountDisbursementFee2 = new AccountFeesEntity();
			accountDisbursementFee2.setAccountFeeAmount(new Money(currency,
					"20.0"));
			accountDisbursementFee2.setFeeAmount(new Money(currency, "20.0"));
			disbursementFee2 = createOneTimeAmountFee("Disbursement Fee 2", FeeCategory.LOAN, "20",
					FeePayment.TIME_OF_DISBURSMENT);
			accountDisbursementFee2.setFees(disbursementFee2);
			accountDisbursementFee2.setAccount(loan);
			loan.addAccountFees(accountDisbursementFee2);
		}

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		customer.getCustomerMeeting().getMeeting()
				.setMeetingStartDate(calendar);
		MeetingBO meeting = createLoanMeeting(customer.getCustomerMeeting()
				.getMeeting());
		loan.setLoanMeeting(meeting);

		if (disbursalType == 2)// 2-Interest At Disbursment
		{
			loan
					.setIntrestAtDisbursement(LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT);
			List<Date> meetingDates = getMeetingDates(loan.getLoanMeeting(), 6);
			short i = 0;
			for (Date date : meetingDates) {

				if (i == 0) {
					i++;
					loan.setDisbursementDate(date);
					AccountActionDateEntity actionDate = new AccountActionDateEntity();
					actionDate.setAccount(loan);
					actionDate.setActionDate(new java.sql.Date(date.getTime()));
					// actionDate.setCurrency(currency);
					actionDate.setCustomer(customer);
					actionDate.setInstallmentId(i);
					actionDate.setDeposit(new Money(currency, "0.0"));
					actionDate.setDepositPaid(new Money(currency, "0.0"));
					actionDate.setPenalty(new Money(currency, "0.0"));
					actionDate.setInterest(new Money(currency, "12.0"));
					actionDate.setInterestPaid(new Money(currency, "0.0"));
					actionDate.setPrincipalPaid(new Money(currency, "0.0"));
					actionDate.setPenaltyPaid(new Money(currency, "0.0"));
					actionDate.setMiscFee(new Money(currency, "0.0"));
					actionDate.setMiscFeePaid(new Money(currency, "0.0"));
					actionDate.setMiscPenalty(new Money(currency, "0.0"));
					actionDate.setMiscPenaltyPaid(new Money(currency, "0.0"));
					actionDate.setPaymentStatus(Short.valueOf("0"));
					actionDate.setPrincipal(new Money(currency, "0.0"));
					loan.addAccountActionDate(actionDate);

					// periodic fee
					AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
					accountFeesaction.setAccountFee(accountPeriodicFee);
					accountFeesaction.setAccountActionDate(actionDate);
					accountFeesaction.setFee(maintanenceFee);
					accountFeesaction.setFeeAmount(new Money(currency, "10.0"));
					accountFeesaction.setFeeAmountPaid(new Money(currency,
							"0.0"));
					accountFeesaction.setInstallmentId(i);
					actionDate.addAccountFeesAction(accountFeesaction);

					// dibursement fee one
					AccountFeesActionDetailEntity accountFeesaction1 = new AccountFeesActionDetailEntity();
					accountFeesaction1.setAccountFee(accountDisbursementFee);
					accountFeesaction1.setAccountActionDate(actionDate);
					accountFeesaction1.setFee(disbursementFee);
					accountFeesaction1
							.setFeeAmount(new Money(currency, "10.0"));
					accountFeesaction1.setFeeAmountPaid(new Money(currency,
							"0.0"));
					accountFeesaction1.setInstallmentId(i);
					actionDate.addAccountFeesAction(accountFeesaction1);

					// disbursementfee2
					AccountFeesActionDetailEntity accountFeesaction2 = new AccountFeesActionDetailEntity();
					accountFeesaction2.setAccountFee(accountDisbursementFee2);
					accountFeesaction2.setAccountActionDate(actionDate);
					accountFeesaction2.setFee(disbursementFee2);
					accountFeesaction2
							.setFeeAmount(new Money(currency, "20.0"));
					accountFeesaction2.setFeeAmountPaid(new Money(currency,
							"0.0"));
					accountFeesaction2.setInstallmentId(i);
					actionDate.addAccountFeesAction(accountFeesaction2);

					continue;
				}
				AccountActionDateEntity actionDate = new AccountActionDateEntity();
				actionDate.setAccount(loan);
				actionDate.setActionDate(new java.sql.Date(date.getTime()));
				// actionDate.setCurrency(currency);
				actionDate.setCustomer(customer);
				i++;
				actionDate.setInstallmentId(i);
				actionDate.setDeposit(new Money(currency, "0.0"));
				actionDate.setDepositPaid(new Money(currency, "0.0"));
				actionDate.setPenalty(new Money(currency, "0.0"));
				actionDate.setInterest(new Money(currency, "12.0"));
				actionDate.setInterestPaid(new Money(currency, "0.0"));
				actionDate.setPrincipalPaid(new Money(currency, "0.0"));
				actionDate.setPenaltyPaid(new Money(currency, "0.0"));
				actionDate.setMiscFee(new Money(currency, "0.0"));
				actionDate.setMiscFeePaid(new Money(currency, "0.0"));
				actionDate.setMiscPenalty(new Money(currency, "0.0"));
				actionDate.setMiscPenaltyPaid(new Money(currency, "0.0"));
				actionDate.setPaymentStatus(Short.valueOf("0"));
				actionDate.setPrincipal(new Money(currency, "100.0"));
				loan.addAccountActionDate(actionDate);

				AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
				accountFeesaction.setAccountFee(accountPeriodicFee);
				accountFeesaction.setAccountActionDate(actionDate);
				accountFeesaction.setFee(maintanenceFee);
				accountFeesaction.setFeeAmount(new Money(currency, "100.0"));
				accountFeesaction.setFeeAmountPaid(new Money(currency, "0.0"));
				accountFeesaction.setInstallmentId(i);

				actionDate.addAccountFeesAction(accountFeesaction);
			}

		} else if (disbursalType == 1 || disbursalType == 3) {
			loan.setIntrestAtDisbursement(Short.valueOf("0"));
			List<Date> meetingDates = getMeetingDates(loan.getLoanMeeting(), 7);

			short i = 0;
			for (Date date : meetingDates) {

				if (i == 0) {
					i++;
					loan.setDisbursementDate(date);
					continue;
				}
				AccountActionDateEntity actionDate = new AccountActionDateEntity();
				actionDate.setAccount(loan);
				actionDate.setActionDate(new java.sql.Date(date.getTime()));
				// actionDate.setCurrency(currency);
				actionDate.setCustomer(customer);
				actionDate.setInstallmentId(i++);
				actionDate.setDeposit(new Money(currency, "0.0"));
				actionDate.setDepositPaid(new Money(currency, "0.0"));
				actionDate.setPenalty(new Money(currency, "0.0"));
				actionDate.setInterest(new Money(currency, "12.0"));
				actionDate.setInterestPaid(new Money(currency, "0.0"));
				actionDate.setPrincipalPaid(new Money(currency, "0.0"));
				actionDate.setPenaltyPaid(new Money(currency, "0.0"));
				actionDate.setMiscFee(new Money(currency, "0.0"));
				actionDate.setMiscFeePaid(new Money(currency, "0.0"));
				actionDate.setMiscPenalty(new Money(currency, "0.0"));
				actionDate.setMiscPenaltyPaid(new Money(currency, "0.0"));
				actionDate.setPaymentStatus(Short.valueOf("0"));
				actionDate.setPrincipal(new Money(currency, "100.0"));
				loan.addAccountActionDate(actionDate);

				AccountFeesActionDetailEntity accountFeesaction = new AccountFeesActionDetailEntity();
				accountFeesaction.setAccountFee(accountPeriodicFee);
				accountFeesaction.setAccountActionDate(actionDate);
				accountFeesaction.setFee(maintanenceFee);
				accountFeesaction.setFeeAmount(new Money(currency, "100.0"));
				accountFeesaction.setFeeAmountPaid(new Money(currency, "0.0"));
				accountFeesaction.setInstallmentId(i);

				actionDate.addAccountFeesAction(accountFeesaction);
			}
		}
		loan.setLoanAmount(new Money(currency, "300.0"));
		loan.setLoanBalance(new Money(currency, "300.0"));
		loan.setNoOfInstallments(Short.valueOf("6"));

		GracePeriodType gracePeriodType = new GracePeriodType();
		gracePeriodType.setGracePeriodTypeId(Short.valueOf("1"));
		loan.setGracePeriodType(gracePeriodType);
		loan.setCreatedBy(Short.valueOf("1"));
		loan.setGracePeriodDuration(Short.valueOf("0"));

		InterestTypes interestTypes = new InterestTypes();
		interestTypes.setInterestTypeId(Short.valueOf("1"));
		loan.setInterestType(interestTypes);
		loan.setInterestRateAmount(new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), "10.0"));
		loan.setCreatedDate(new Date(System.currentTimeMillis()));

		LoanSummaryEntity loanSummary = new LoanSummaryEntity();
		loanSummary.setLoan(loan);
		loanSummary.setOriginalFees(new Money(currency, "0.0"));
		loanSummary.setOriginalPrincipal(new Money(currency, "300.0"));
		loanSummary.setOriginalInterest(new Money(currency, "36.0"));
		loanSummary.setPenaltyPaid(new Money(currency, "0.0"));
		loanSummary.setOriginalPenalty(new Money(currency, "0.0"));
		loanSummary.setPrincipalPaid(new Money(currency, "0.0"));
		loanSummary.setInterestPaid(new Money(currency, "0.0"));
		loanSummary.setFeesPaid(new Money(currency, "0.0"));
		loan.setLoanSummary(loanSummary);

		return (LoanBO) testObjectPersistence.persist(loan);
	}

	private static void deleteAccountWithoutDeletetingProduct(
			AccountBO account, Session session) {
		boolean newSession = false;

		Transaction transaction = null;
		if (null == session) {
			session = HibernateUtil.getSessionTL();
			transaction = HibernateUtil.startTransaction();
			newSession = true;
		}
		for (AccountPaymentEntity accountPayment : account.getAccountPayments()) {
			if (null != accountPayment) {
				deleteAccountPayment(accountPayment, session);
			}
		}
		for (AccountActionDateEntity actionDates : account
				.getAccountActionDates()) {
			for (AccountFeesActionDetailEntity actionFees : actionDates
					.getAccountFeesActionDetails()) {
				session.delete(actionFees);
			}
			session.delete(actionDates);
		}
		
		List<FeeBO> feeList = new ArrayList<FeeBO>(); 
		for(AccountFeesEntity accountFees: account.getAccountFees()){
			if(!feeList.contains(accountFees.getFees()))
					feeList.add(accountFees.getFees());
		}
		
		for (AccountFeesEntity accountFees : account.getAccountFees()) {
			session.delete(accountFees);
		}

		deleteFees(feeList);
		if (account instanceof LoanBO) {

			LoanBO loan = (LoanBO) account;
			if (null != loan.getLoanSummary()) {
				session.delete(loan.getLoanSummary());
			}
			session.delete(account);

		}
		if (account instanceof SavingsBO) {

			SavingsBO savings = (SavingsBO) account;
			session.delete(account);
		} else {
			session.delete(account);
		}

		if (newSession) {
			transaction.commit();
		}
	}

	public static void cleanUpWithoutDeletetingProduct(AccountBO account) {
		if (null != account) {
			deleteAccountWithoutDeletetingProduct(account, null);
		}
	}

	public static PaymentData getCustomerAccountPaymentDataView(
			List<AccountActionDateEntity> accountActions, Money totalAmount,
			CustomerBO customer, PersonnelBO personnel, String recieptNum,
			Short paymentId, Date receiptDate, Date transactionDate) {
		PaymentData paymentData = new PaymentData(totalAmount, personnel,
				paymentId, transactionDate);
		paymentData.setCustomer(customer);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		for (AccountActionDateEntity actionDate : accountActions) {
			CustomerAccountPaymentData customerAccountPaymentData = new CustomerAccountPaymentData(
					actionDate);
			paymentData.addAccountPaymentData(customerAccountPaymentData);
		}

		return paymentData;
	}

	public static CustomerAccountView getCustomerAccountView(CustomerBO customer) {
		CustomerAccountView customerAccountView = new CustomerAccountView(
				customer.getCustomerAccount().getAccountId());
		List<AccountActionDateEntity> accountAction = getDueActionDatesForAccount(
				customer.getCustomerAccount().getAccountId(),
				new java.sql.Date(System.currentTimeMillis()));
		customerAccountView
				.setAccountActionDates(getBulkEntryAccountActionViews(accountAction));
		return customerAccountView;
	}

	public static List<AccountActionDateEntity> getDueActionDatesForAccount(
			Integer accountId, java.sql.Date transactionDate) {
		List<AccountActionDateEntity> dueActionDates = new BulkEntryBusinessService()
				.retrieveCustomerAccountActionDetails(accountId,
						transactionDate);
		for (AccountActionDateEntity accountActionDate : dueActionDates) {
			Hibernate.initialize(accountActionDate);
			for (AccountFeesActionDetailEntity accountFeesActionDetail : accountActionDate
					.getAccountFeesActionDetails()) {
				Hibernate.initialize(accountFeesActionDetail);
			}
		}
		HibernateUtil.closeSession();
		return dueActionDates;
	}

	public static void cleanUp(CollectionSheetBO collSheet) {
		if (null != collSheet) {
			deleteCollectionSheet(collSheet, null);
		}
	}

	private static void deleteCollectionSheet(CollectionSheetBO collSheet,
			Session session) {
		boolean newSession = false;

		Transaction transaction = null;
		if (null == session) {
			session = HibernateUtil.getSessionTL();
			transaction = HibernateUtil.startTransaction();
			newSession = true;
		}
		if (collSheet.getCollectionSheetCustomers() != null) {
			for (CollSheetCustBO collSheetCustomer : collSheet
					.getCollectionSheetCustomers()) {
				if (null != collSheetCustomer) {
					deleteCollSheetCustomer(collSheetCustomer, session);
				}
			}
		}
		session.delete(collSheet);

		if (newSession) {
			transaction.commit();
		}
	}

	private static void deleteCollSheetCustomer(
			CollSheetCustBO collSheetCustomer, Session session) {

		if (collSheetCustomer.getCollectionSheetLoanDetails() != null) {
			for (CollSheetLnDetailsEntity collSheetLoanDetails : collSheetCustomer
					.getCollectionSheetLoanDetails()) {
				if (null != collSheetLoanDetails) {
					session.delete(collSheetLoanDetails);
				}
			}
		}
		if (collSheetCustomer.getCollSheetSavingsDetails() != null) {
			for (CollSheetSavingsDetailsEntity collSheetSavingsDetails : collSheetCustomer
					.getCollSheetSavingsDetails()) {
				if (null != collSheetSavingsDetails) {
					session.delete(collSheetSavingsDetails);
				}
			}
		}
		session.delete(collSheetCustomer);

	}

	public static PaymentData getLoanAccountPaymentData(
			List<AccountActionDateEntity> accountActions, Money totalAmount,
			CustomerBO customer, PersonnelBO personnel, String recieptNum,
			Short paymentId, Date receiptDate, Date transactionDate) {
		PaymentData paymentData = new PaymentData(totalAmount, personnel,
				paymentId, transactionDate);
		paymentData.setCustomer(customer);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		for (AccountActionDateEntity actionDate : accountActions) {
			LoanPaymentData loanPaymentData = new LoanPaymentData(actionDate);
			paymentData.addAccountPaymentData(loanPaymentData);
		}
		return paymentData;
	}

	public static LoanAccountView getLoanAccountView(LoanBO loan) {
		return new LoanAccountView(loan.getAccountId(), loan.getLoanOffering()
				.getPrdOfferingName(),
				loan.getAccountType().getAccountTypeId(), loan
						.getLoanOffering().getPrdOfferingId(), loan
						.getAccountState().getId(), loan
						.getIntrestAtDisbursement(), loan.getLoanBalance());

	}

	public static BulkEntryAccountActionView getBulkEntryAccountActionView(
			AccountActionDateEntity actionDate) {
		BulkEntryAccountActionView bulkEntryAccountActionView = new BulkEntryAccountActionView(
				actionDate.getAccount().getAccountId(), actionDate
						.getCustomer().getCustomerId(), actionDate
						.getInstallmentId(), actionDate.getActionDate(),
				actionDate.getPrincipal(), actionDate.getPrincipalPaid(),
				actionDate.getInterest(), actionDate.getInterestPaid(),
				actionDate.getMiscFee(), actionDate.getMiscFeePaid(),
				actionDate.getPenalty(), actionDate.getPenaltyPaid(),
				actionDate.getMiscPenalty(), actionDate.getMiscPenaltyPaid(),
				actionDate.getDeposit(), actionDate.getDepositPaid(),
				actionDate.getActionDateId());
		bulkEntryAccountActionView
				.setBulkEntryAccountFeeActions(getBulkEntryAccountFeeActionViews(actionDate));
		return bulkEntryAccountActionView;

	}

	public static BulkEntryAccountFeeActionView getBulkEntryAccountFeeActionView(
			AccountFeesActionDetailEntity feeAction) {
		return new BulkEntryAccountFeeActionView(feeAction
				.getAccountActionDate().getActionDateId(), feeAction.getFee(),
				feeAction.getFeeAmount(), feeAction.getFeeAmountPaid());

	}

	public static List<BulkEntryAccountFeeActionView> getBulkEntryAccountFeeActionViews(
			AccountActionDateEntity actionDate) {
		List<BulkEntryAccountFeeActionView> bulkEntryFeeViews = new ArrayList<BulkEntryAccountFeeActionView>();
		if (actionDate.getAccountFeesActionDetails() != null
				&& actionDate.getAccountFeesActionDetails().size() > 0) {
			for (AccountFeesActionDetailEntity accountFeesActionDetail : actionDate
					.getAccountFeesActionDetails()) {
				bulkEntryFeeViews
						.add(getBulkEntryAccountFeeActionView(accountFeesActionDetail));
			}
		}
		return bulkEntryFeeViews;

	}

	public static List<BulkEntryAccountActionView> getBulkEntryAccountActionViews(
			List<AccountActionDateEntity> actionDates) {
		List<BulkEntryAccountActionView> bulkEntryActionViews = new ArrayList<BulkEntryAccountActionView>();
		if (actionDates != null && actionDates.size() > 0) {
			for (AccountActionDateEntity actionDate : actionDates) {
				bulkEntryActionViews
						.add(getBulkEntryAccountActionView(actionDate));
			}
		}
		return bulkEntryActionViews;

	}
}
