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
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.bulkentry.business.BulkEntryAccountFeeActionView;
import org.mifos.application.bulkentry.business.BulkEntryCustomerAccountInstallmentView;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.business.BulkEntryLoanInstallmentView;
import org.mifos.application.bulkentry.business.BulkEntrySavingsInstallmentView;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CheckListDetailEntity;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollSheetLnDetailsEntity;
import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntity;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerNoteEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.PrdOfferingMeetingEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.SavingsTypeEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.InterestTypeConstants;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.util.ActivityContext;
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
 */
public class TestObjectFactory {

	private static TestObjectPersistence testObjectPersistence = 
		new TestObjectPersistence();

	/**
	 * @return - Returns the office created by test data scripts. If the row
	 *         does not already exist in the database it returns null. defaults
	 *         created are 1- Head Office , 2 - Area Office , 3 - BranchOffice.
	 */
	public static OfficeBO getOffice(Short officeId) {
		return (OfficeBO)addObject(testObjectPersistence.getOffice(officeId));
	}

	public static void removeObject(PersistentObject obj) {
		if (obj != null) {
			testObjectPersistence.removeObject(obj);
			obj = null;
		}
	}

	/**
	 * @return - Returns the personnel created by master data scripts. This
	 *         record does not have any custom fields or roles associated with
	 *         it. If the row does not already exist in the database it returns
	 *         null.
	 */

	public static PersonnelBO getPersonnel(Short personnelId) {
		return (PersonnelBO)addObject(testObjectPersistence.getPersonnel(personnelId));
	}

	public static CenterBO createCenter(String customerName, Short statusId,
			String searchId, MeetingBO meeting, Date startDate) {
		CenterBO center = null;
		try {
			Short officeId = new Short("3");
			Short personnel = new Short("1");		
			center = new CenterBO(getUserContext(), customerName, null, null, getFees(),  null, null, officeId, meeting, personnel);
			center.save();
			HibernateUtil.commitTransaction();
			//TODO: throw Exception
		}catch(Exception e){
			e.printStackTrace();
		}
		addObject(center);
		return center;
	}
	
	//TODO: remove extra parameters from references
	public static CenterBO createCenter(String customerName, Short statusId,
			String searchId, MeetingBO meeting, Short officeId, Short personnelId, Date startDate) {
		return createCenter(customerName, meeting, officeId, personnelId);
	}
	
	public static CenterBO createCenter(String customerName, MeetingBO meeting, Short officeId, Short personnelId) {
		CenterBO center = null;
		try {
			center = new CenterBO(getUserContext(), customerName, null, null, getFees(),  null, null, officeId, meeting, personnelId);
			center.save();
			HibernateUtil.commitTransaction();
			//TODO: throw Exception
		}catch(Exception e){
			e.printStackTrace();
		}
		addObject(center);
		return center;
	}
	
	public static CenterBO createCenter(String customerName, Short statusId,
			String searchId, MeetingBO meeting, Date startDate,List<FeeView> fees) {
		CenterBO center = null;
		try {
			Short officeId = new Short("3");
			Short personnel = new Short("1");		
			center = new CenterBO(getUserContext(), customerName, null, null, fees,  null, null, officeId, meeting, personnel);
			center.save();
			HibernateUtil.commitTransaction();
			//TODO: throw Exception
		}catch(Exception e){
			e.printStackTrace();
		}
		addObject(center);
		return center;
	}
		
	private static List<FeeView> getFees(){
		List<FeeView> fees = new ArrayList<FeeView>();		
		AmountFeeBO maintanenceFee = (AmountFeeBO)createPeriodicAmountFee("Mainatnence Fee", FeeCategory.ALLCUSTOMERS, "100",RecurrenceType.WEEKLY,
				Short.valueOf("1"));
		FeeView fee = new FeeView(getContext(),maintanenceFee);
		fees.add(fee);
		return fees;
	}
	
	public static List<CustomFieldView> getCustomFields(){
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();		
		CustomFieldView fee = new CustomFieldView(Short.valueOf("4"),"Custom",Short.valueOf("1"));
		customFields.add(fee);
		return customFields;
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

	//TODO: change references and remove this method
	public static GroupBO createGroup(String customerName, Short statusId,
			String searchId, CustomerBO parentCustomer, Date startDate) {
		return createGroupUnderCenter(customerName, CustomerStatus.getStatus(statusId), parentCustomer);
	}

	public static GroupBO createGroupUnderCenter(String customerName, CustomerStatus customerStatus, CustomerBO parentCustomer){
		Short formedBy = new Short("1");	
		return createGroupUnderCenter(customerName, customerStatus, null, false, null, null, getCustomFields(), getFees(), formedBy, parentCustomer);
	}
	
	public static GroupBO createGroupUnderCenter(String customerName, CustomerStatus customerStatus, String externalId, boolean trained, Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, CustomerBO parentCustomer){
		GroupBO group = null;
		try {
			group = new GroupBO(getUserContext(), customerName, customerStatus, externalId, trained, trainedDate, address, customFields, fees, formedById, parentCustomer);
			group.save();
			HibernateUtil.commitTransaction();
			//TODO: throw Exception
		}catch(Exception e){
			e.printStackTrace();
		}
		addObject(group);
		return group;
	}
	
	public static GroupBO createGroupUnderBranch(String customerName,
			CustomerStatus customerStatus, Short officeId, MeetingBO meeting,
			Short loanOfficerId){
		Short formedBy = new Short("1");
		return createGroupUnderBranch(customerName, customerStatus, null, false, null, null, null, getFees(), formedBy, officeId, meeting, loanOfficerId);
	}
	
	public static GroupBO createGroupUnderBranch(String customerName,
			CustomerStatus customerStatus, String externalId, boolean trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, MeetingBO meeting,
			Short loanOfficerId){
		GroupBO group = null;
		try {
			group = new GroupBO(getUserContext(), customerName, customerStatus, externalId, trained, trainedDate, address, customFields, fees, formedById, officeId, meeting, loanOfficerId);
			group.save();
			HibernateUtil.commitTransaction();
			//TODO: throw Exception
		}catch(Exception e){
			e.printStackTrace();
		}
		addObject(group);
		return group;
	}
	
	public static ClientBO createClient(String customerName, Short statusId,String searchId,
			CustomerBO parentCustomer, Date startDate) {
		ClientBO client = null;
		try {
			Short office = new Short("3");
			Short personnel = new Short("1");	
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder("testClientName"),customerName,"middle",customerName,"secondLast");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),customerName,"middle",customerName,"secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(getUserContext(), customerName, CustomerStatus.getStatus(statusId), null, null, null, null, getFees(), personnel, office, parentCustomer, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			client.save();
			HibernateUtil.commitTransaction();
			// TODO: throw Exception
		} catch (Exception e) {
			e.printStackTrace();
		}
		addObject(client);
		return client;
	}
	
	public static ClientBO createClient(String customerName, MeetingBO meeting , Short statusId, Date startDate) {
		ClientBO client = null;
		try {
			Short office = new Short("3");
			Short personnel = new Short("1");	
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("3"),1,new StringBuilder(customerName),customerName,"middle",customerName,"secondLast");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),customerName,"middle",customerName,"secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(statusId), null, null, null, null, getFees(), personnel, office,meeting,personnel, new Date(),
					null,null,null,YesNoFlag.NO.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			client.save();
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		addObject(client);
		return client;
	}
	
	public static ClientBO createClient(String customerName, Short statusId,CustomerBO parentCustomer, Date startDate) {
		ClientBO client = null;
		Short personnel = new Short("1");	
		try {
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(Short.valueOf("1"),1,new StringBuilder(customerName+customerName),customerName,"",customerName,"");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(Short.valueOf("2"),1,new StringBuilder("testSpouseName"),customerName,"middle",customerName,"secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1,1,1,1,1,1,Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("41"));
			client = new ClientBO(getUserContext(), clientNameDetailView.getDisplayName(), CustomerStatus.getStatus(statusId), null, null, null, null, getFees(), personnel, parentCustomer.getOffice().getOfficeId(), parentCustomer, null,
					null,null,null,YesNoFlag.YES.getValue(),clientNameDetailView,spouseNameDetailView,clientDetailView,null);
			
			client.save();
			HibernateUtil.commitTransaction();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		addObject(client);
		return client;
	}

	public static LoanOfferingBO createLoanOffering(String name,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double defLnAmnt, Double defIntRate, Short defInstallments,
			Short interestTypeId, Short penaltyGrace, Short intDedAtDisb,
			Short princDueLastInst, Short intCalcRuleId, MeetingBO meeting) {
		return createLoanOffering(name, name.substring(0, 1),applicableTo, startDate,
				offeringStatusId, defLnAmnt, defIntRate, defInstallments,
				interestTypeId, penaltyGrace, intDedAtDisb, princDueLastInst,
				intCalcRuleId, meeting, GraceTypeConstants.GRACEONALLREPAYMENTS
						.getValue());
	}
	
	public static LoanOfferingBO createLoanOffering(String name,String shortName,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double defLnAmnt, Double defIntRate, Short defInstallments,
			Short interestTypeId, Short penaltyGrace, Short intDedAtDisb,
			Short princDueLastInst, Short intCalcRuleId, MeetingBO meeting) {
		return createLoanOffering(name,shortName,applicableTo, startDate,
				offeringStatusId, defLnAmnt, defIntRate, defInstallments,
				interestTypeId, penaltyGrace, intDedAtDisb, princDueLastInst,
				intCalcRuleId, meeting, GraceTypeConstants.GRACEONALLREPAYMENTS
						.getValue());
	}
	
	public static LoanOfferingBO createLoanOffering(String name,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double defLnAmnt, Double defIntRate, Short defInstallments,
			Short interestTypeId, Short penaltyGrace, Short intDedAtDisb,
			Short princDueLastInst, Short intCalcRuleId, MeetingBO meeting,Short graceType) {
		return createLoanOffering(name, name.substring(0, 1),applicableTo, startDate,
				offeringStatusId, defLnAmnt, defIntRate, defInstallments,
				interestTypeId, penaltyGrace, intDedAtDisb, princDueLastInst,
				intCalcRuleId, meeting,graceType);
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
 * @param graceType -
 *            grace period Type associated with the product offering         
 */
	public static LoanOfferingBO createLoanOffering(String name,String shortName,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double defLnAmnt, Double defIntRate, Short defInstallments,
			Short interestTypeId, Short penaltyGrace, Short intDedAtDisb,
			Short princDueLastInst, Short intCalcRuleId, MeetingBO meeting,Short graceType) {

		LoanOfferingBO loanOffering = null;
		PrdApplicableMasterEntity prdApplicableMaster = null;
		try {
			prdApplicableMaster = new PrdApplicableMasterEntity(
					PrdApplicableMaster.getPrdApplicableMaster(applicableTo));
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getLoanPrdCategory();
		GracePeriodTypeEntity gracePeriodType = null;
		try {
			gracePeriodType = new GracePeriodTypeEntity(GraceTypeConstants
					.getGraceTypeConstants(graceType));
		} catch (PropertyNotFoundException e1) {
			e1.printStackTrace();
		}
		InterestTypesEntity interestTypes = null;
		try {
			interestTypes = new InterestTypesEntity(InterestTypeConstants
					.getInterestTypeConstants(interestTypeId));
		} catch (PropertyNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GLCodeEntity glCodePrincipal = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, Short.valueOf("11"));

		GLCodeEntity glCodeInterest = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, Short.valueOf("21"));
		boolean interestDisAtDisb = intDedAtDisb.equals(YesNoFlag.YES
				.getValue()) ? true : false;
		boolean principalDueLastInst = princDueLastInst.equals(YesNoFlag.YES
				.getValue()) ? true : false;
		try {
			loanOffering = new LoanOfferingBO(getContext(), name, shortName,
					productCategory, prdApplicableMaster, startDate, null,
					null, gracePeriodType, (short) 0, interestTypes, new Money(
							defLnAmnt.toString()), new Money(defLnAmnt
							.toString()), new Money(defLnAmnt.toString()),
					defIntRate, defIntRate, defIntRate, defInstallments,
					defInstallments, defInstallments, true, interestDisAtDisb,
					principalDueLastInst, new ArrayList<FundBO>(),
					new ArrayList<FeeBO>(), meeting, glCodePrincipal,
					glCodeInterest);
		} catch (ProductDefinitionException e) {
			e.printStackTrace();
		}

		PrdStatusEntity prdStatus = testObjectPersistence
				.retrievePrdStatus(offeringStatusId);
		loanOffering.setPrdStatus(prdStatus);
		loanOffering.setGracePeriodType(gracePeriodType);
		return (LoanOfferingBO) addObject(testObjectPersistence
				.persist(loanOffering));
	}


	public static ProductCategoryBO getLoanPrdCategory() {
		return (ProductCategoryBO) addObject(testObjectPersistence.getLoanPrdCategory());
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
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		MeetingBO meeting = createLoanMeeting(customer.getCustomerMeeting()
				.getMeeting());
		List<Date> meetingDates = getMeetingDates(meeting, 6);
		
		LoanBO loan = null;
		MifosCurrency currency = testObjectPersistence.getCurrency();
		try {
			loan = new LoanBO(TestObjectFactory.getUserContext(), loanOfering, customer,
						AccountState.getStatus(accountStateId),	new Money(currency, "300.0"),
						Short.valueOf("6"),meetingDates.get(0),true,0.0,(short) 0,
						new FundBO(),new ArrayList<FeeView>());
			
		} catch (NumberFormatException e) {
		} catch (AccountException e) {
			e.printStackTrace();
		} catch (InvalidUserException e) {
		} catch (PropertyNotFoundException e) {
		} catch (SystemException e) {
		} catch (ApplicationException e) {
		}
		FeeBO maintanenceFee = createPeriodicAmountFee("Mainatnence Fee",
				FeeCategory.LOAN, "100", RecurrenceType.WEEKLY, Short
						.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan,maintanenceFee,((AmountFeeBO)maintanenceFee).getFeeAmount().getAmountDoubleValue());	
		loan.addAccountFees(accountPeriodicFee);
		loan.setLoanMeeting(meeting);
		short i = 0;
		for (Date date : meetingDates) {
			LoanScheduleEntity actionDate = (LoanScheduleEntity)loan.getAccountActionDate(++i);
			actionDate.setPrincipal(new Money(currency, "100.0"));
			actionDate.setInterest(new Money(currency, "12.0"));
			actionDate.setActionDate(new java.sql.Date(date.getTime()));
			actionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
			loan.addAccountActionDate(actionDate);

			AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
					actionDate, maintanenceFee, accountPeriodicFee,
					new Money(currency, "100.0"));
			accountFeesaction.setFeeAmountPaid(new Money(currency, "0.0"));
			actionDate.addAccountFeesAction(accountFeesaction);
		}
		loan.setCreatedBy(Short.valueOf("1"));
		loan.setCreatedDate(new Date(System.currentTimeMillis()));

		LoanSummaryEntity loanSummary = loan.getLoanSummary();
		loanSummary.setOriginalPrincipal(new Money(currency, "300.0"));
		loanSummary.setOriginalInterest(new Money(currency, "36.0"));
		try {
			loan.save();
		} catch (AccountException e) {
			e.printStackTrace();
		}
		HibernateUtil.commitTransaction();
		return (LoanBO) addObject(getObject(LoanBO.class, loan.getAccountId()));
	}

	public static SavingsOfferingBO createSavingsOffering(String name,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double recommenededAmt, Short recomAmtUnitId, Double intRate,
			Double maxAmtWithdrawl, Double minAmtForInt, Short savingsTypeId,
			Short intCalTypeId, MeetingBO intCalcMeeting,
			MeetingBO intPostMeeting) {
		return createSavingsOffering(name,name.substring(0, 1), applicableTo, startDate,
				offeringStatusId, recommenededAmt, recomAmtUnitId, intRate,
				maxAmtWithdrawl, minAmtForInt, savingsTypeId, intCalTypeId,
				intCalcMeeting, intPostMeeting, (short) 7, (short) 7);
	}
	
	public static SavingsOfferingBO createSavingsOffering(String name,String shortName,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double recommenededAmt, Short recomAmtUnitId, Double intRate,
			Double maxAmtWithdrawl, Double minAmtForInt, Short savingsTypeId,
			Short intCalTypeId, MeetingBO intCalcMeeting,
			MeetingBO intPostMeeting) {
		return createSavingsOffering(name,shortName, applicableTo, startDate,
				offeringStatusId, recommenededAmt, recomAmtUnitId, intRate,
				maxAmtWithdrawl, minAmtForInt, savingsTypeId, intCalTypeId,
				intCalcMeeting, intPostMeeting, (short) 7, (short) 7);
	}

	public static SavingsOfferingBO createSavingsOffering(String name,String shortName,
			Short applicableTo, Date startDate, Short offeringStatusId,
			Double recommenededAmt, Short recomAmtUnitId, Double intRate,
			Double maxAmtWithdrawl, Double minAmtForInt, Short savingsTypeId,
			Short intCalTypeId, MeetingBO intCalcMeeting,
			MeetingBO intPostMeeting, Short depGLCode, Short withGLCode) {
		PrdApplicableMasterEntity prdApplicableMaster = null;
		try {
			prdApplicableMaster = new PrdApplicableMasterEntity(
					PrdApplicableMaster.getPrdApplicableMaster(applicableTo));
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}

		SavingsTypeEntity savingsType = null;
		try {
			savingsType = new SavingsTypeEntity(SavingsType
					.getSavingsType(savingsTypeId));
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}

		InterestCalcTypeEntity intCalType = null;
		try {
			intCalType = new InterestCalcTypeEntity(InterestCalcType
					.getInterestCalcType(intCalTypeId));
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		RecommendedAmntUnitEntity amountUnit = null;
		try {
			amountUnit = new RecommendedAmntUnitEntity(RecommendedAmountUnit
					.getRecommendedAmountUnit(recomAmtUnitId));
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, depGLCode);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, withGLCode);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		SavingsOfferingBO savingsOffering = null;
		try {
			savingsOffering = new SavingsOfferingBO(getUserContext(), name,
					shortName, productCategory, prdApplicableMaster,
					startDate, null,null,amountUnit,savingsType, intCalType, intCalcMeeting,
					intPostMeeting, new Money(recommenededAmt.toString()),new Money(maxAmtWithdrawl
							.toString()),new Money(minAmtForInt.toString()),
					intRate, depglCodeEntity, intglCodeEntity);
		} catch (InvalidUserException e1) {
			e1.printStackTrace();
		} catch (ProductDefinitionException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		} catch (ApplicationException e1) {
			e1.printStackTrace();
		}

		PrdStatusEntity prdStatus = testObjectPersistence
				.retrievePrdStatus(offeringStatusId);
		savingsOffering.setPrdStatus(prdStatus);
		return (SavingsOfferingBO) addObject(testObjectPersistence
				.persist(savingsOffering));
	}

	public static SavingsBO createSavingsAccount(String globalNum,
			CustomerBO customer, Short accountStateId, Date startDate,
			SavingsOfferingBO savingsOffering) throws Exception {
		UserContext userContext = null;
			userContext = getUserContext();
		MifosCurrency currency = testObjectPersistence.getCurrency();
		MeetingBO meeting = createLoanMeeting(customer.getCustomerMeeting()
				.getMeeting());
		SavingsBO savings = new SavingsBO(userContext,savingsOffering,customer,AccountState.SAVINGS_ACC_PARTIALAPPLICATION,new Money(currency, "300.0"),null);
		savings.save();
		savings.setAccountState(new AccountStateEntity(accountStateId));
		savings.setGlobalAccountNum(globalNum);
		savings.setActivationDate(new Date(System.currentTimeMillis()));
		List<Date> meetingDates = getMeetingDates(meeting, 3);
		short i = 0;
		for (Date date : meetingDates) {
			SavingsScheduleEntity actionDate = new SavingsScheduleEntity(
					savings, customer, ++i, new java.sql.Date(date.getTime()),
					PaymentStatus.UNPAID, new Money(currency, "200.0"));
			savings.addAccountActionDate(actionDate);
		}
		HibernateUtil.commitTransaction();
		return (SavingsBO) addObject(getObject(SavingsBO.class, savings.getAccountId()));
	}
	private static List<CustomFieldView> getCustomFieldView(){
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();
		customFields.add(new CustomFieldView(new Short("8"),"custom field value",null));
		return customFields;

	}
	public static SavingsBO createSavingsAccount(String globalNum,
			CustomerBO customer, Short accountStateId, Date startDate,
			SavingsOfferingBO savingsOffering, UserContext userContext) throws Exception{
			userContext = getUserContext();
			SavingsBO savings = new SavingsBO(userContext,savingsOffering,customer,AccountState.getStatus(accountStateId),savingsOffering.getRecommendedAmount(),getCustomFieldView());
			savings.save();
			savings.setGlobalAccountNum(globalNum);
			savings.setActivationDate(new Date(System.currentTimeMillis()));
			HibernateUtil.commitTransaction();
		return (SavingsBO) addObject(getObject(SavingsBO.class, savings.getAccountId()));
	}

	public static MeetingBO createLoanMeeting(MeetingBO customerMeeting) {
		MeetingBO meetingToReturn = null;
		try{			
			RecurrenceType recurrenceType = RecurrenceType.getRecurrenceType(customerMeeting.getMeetingDetails().getRecurrenceType().getRecurrenceId());
			MeetingType meetingType =  MeetingType.getMeetingType(customerMeeting.getMeetingType().getMeetingTypeId());
			Short recurAfter = customerMeeting.getMeetingDetails().getRecurAfter();
			
			if(recurrenceType.equals(RecurrenceType.MONTHLY)){
				if(customerMeeting.isMonthlyOnDate())
					meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getMeetingRecurrence().getDayNumber(),
							recurAfter, customerMeeting.getMeetingStartDate().getTime(), meetingType,"meetingPlace");
				else
					meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getWeekDay(),customerMeeting.getMeetingDetails().getWeekRank(),
							recurAfter, customerMeeting.getMeetingStartDate().getTime(), meetingType,"meetingPlace");
			}				
			else if(recurrenceType.equals(RecurrenceType.WEEKLY))
				meetingToReturn = new MeetingBO(WeekDay.getWeekDay(customerMeeting.getMeetingDetails()
						.getMeetingRecurrence().getWeekDayValue().getValue()), recurAfter, customerMeeting.getMeetingStartDate().getTime(), meetingType ,"meetingPlace");
			else
				meetingToReturn = new MeetingBO(recurrenceType, recurAfter, customerMeeting.getMeetingStartDate().getTime(), meetingType);
			
			meetingToReturn.setMeetingPlace(customerMeeting.getMeetingPlace());
		}catch(MeetingException me){}
		return meetingToReturn;
	}

	private static List<Date> getMeetingDates(MeetingBO meeting, int occurrences) {
		List<Date> dates = new ArrayList<Date>();
		try {
			dates = meeting.getAllDates(occurrences);
		} catch (MeetingException me) {
			me.printStackTrace();
		}
		return dates;
	}

	public static List<Date> getAllMeetingDates(MeetingBO meeting) {
		List<Date> dates = new ArrayList<Date>();
		try {
			dates = meeting.getAllDates(DateUtils.getLastDayOfCurrentYear());
		} catch (MeetingException e) {
			e.printStackTrace();
		}
		return dates;
	}



	public static FeeBO createPeriodicAmountFee(String feeName,
			FeeCategory feeCategory, String feeAmnt,
			RecurrenceType meetingFrequency, Short recurAfter) {
		GLCodeEntity glCode = (GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, Short.valueOf("24"));
		MeetingBO meeting = null;
		try{
			meeting = new MeetingBO(meetingFrequency, recurAfter, new Date(),MeetingType.FEEMEETING);
		}catch(MeetingException me){}
		FeeBO fee = null;
		// TODO: throw exception
		try {
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(), feeName,
					new CategoryTypeEntity(feeCategory),
					new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
					glCode, getMoneyForMFICurrency(feeAmnt), false, meeting);

		} catch (Exception e) {
		}
		return (FeeBO)addObject(testObjectPersistence.createFee(fee));
	}

	public static FeeBO createOneTimeAmountFee(String feeName,
			FeeCategory feeCategory, String feeAmnt, FeePayment feePayment) {
		GLCodeEntity glCode = (GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, Short.valueOf("24"));
		FeeBO fee = null;
		// TODO: throw exception
		try {
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(), feeName,
					new CategoryTypeEntity(feeCategory),
					new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
					glCode, getMoneyForMFICurrency(feeAmnt), false,
					new FeePaymentEntity(feePayment));

		} catch (Exception e) {
		}
		return (FeeBO)addObject(testObjectPersistence.createFee(fee));
	}

	public static FeeBO createOneTimeRateFee(String feeName,
			FeeCategory feeCategory, Double rate, FeeFormula feeFormula,
			FeePayment feePayment) {
		GLCodeEntity glCode = (GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, Short.valueOf("24"));
		FeeBO fee = null;
		// TODO: throw exception
		try {
			fee = new RateFeeBO(TestObjectFactory.getUserContext(), feeName,
					new CategoryTypeEntity(feeCategory),
					new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
					glCode, rate, new FeeFormulaEntity(feeFormula), false,
					new FeePaymentEntity(feePayment));

		} catch (Exception e) {
		}
		return (FeeBO)addObject(testObjectPersistence.createFee(fee));
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
		MeetingBO meeting = null;
		try{
		 meeting = getMeeting(Integer.toString(frequency), Integer
				.toString(recurAfter), new Integer(meetingTypeId).shortValue());
		}catch(Exception e){}
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
		Calendar cal = new GregorianCalendar();
		WeekDaysEntity weekDays = new WeekDaysEntity(WeekDay.getWeekDay(Short.valueOf(Integer.toString(cal
				.get(Calendar.DAY_OF_WEEK)))));		
		meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(weekDays);
		return meeting;
	}

	public static MeetingBO createMeeting(MeetingBO meeting) {
		return (MeetingBO) addObject(testObjectPersistence.persist(meeting));
	}

	/**
	 * This is a helper method to return MeetingBO object.
	 */
	public static MeetingBO getMeeting(String frequency, String recurAfter,
			Short meetingTypeId) throws Exception{
		MeetingBO meeting  = new MeetingBO(RecurrenceType.getRecurrenceType(Short.valueOf(frequency)), 
				Short.valueOf(recurAfter), new Date(), MeetingType.getMeetingType(meetingTypeId));
		meeting.setMeetingPlace("Loan Meeting Place");
		return meeting;
	}

	public static MeetingBO getMeeting(RecurrenceType recurrenceType, Short dayNumber, Short weekDay, Short dayRank, Short recurAfter,	MeetingType meetingType)throws Exception {
		MeetingBO meeting = null;
		if(recurrenceType.equals(RecurrenceType.WEEKLY))
		  meeting =  new MeetingBO(WeekDay.getWeekDay(weekDay), recurAfter, new Date(), meetingType, "meetingPlace");
		else
			if(recurrenceType.equals(RecurrenceType.MONTHLY) && dayNumber!=null)
				meeting =  new MeetingBO(dayNumber, recurAfter, new Date(), meetingType, "meetingPlace");
			else
				meeting = new MeetingBO(recurrenceType, recurAfter, new Date(), meetingType);
		
		meeting.setMeetingPlace("Loan Meeting Place");
		return meeting;
	}
	
	public static void cleanUp(CustomerBO customer) {
		
		if (null != customer) {
			deleteCustomer(customer);
			customer=null;
		}
		
	}

	public static void cleanUp(List<CustomerBO> customerList) {
		if (null != customerList) {
			deleteCustomers(customerList);
			customerList =null;
		}
	}

	public static void cleanUp(FeeBO fee) {
		if (null != fee) {
			deleteFee(fee);
			fee=null;
		}
	}

	public static void cleanUp(AccountBO account) {
		if (null != account) {
			deleteAccount(account, null);
			account=null;
		}
	}

	private static void deleteFee(FeeBO fee) {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = HibernateUtil.startTransaction();
		if (fee.isPeriodic()) {
			session.delete(fee.getFeeFrequency().getFeeMeetingFrequency());
		}
		session.delete(fee);
		transaction.commit();
	}

	private static void deleteFees(List<FeeBO> feeList) {
		Session session = HibernateUtil.getSessionTL();
		for (FeeBO fee : feeList) {
			if (fee.isPeriodic()) {
				session.delete(fee.getFeeFrequency().getFeeMeetingFrequency());
			}
			session.delete(fee);
		}
	}

	private static void deleteAccountPayments(AccountBO account) {
		Session session = HibernateUtil.getSessionTL();
		for (AccountPaymentEntity accountPayment : account.getAccountPayments()) {
			if (null != accountPayment) {
				deleteAccountPayment(accountPayment, session);
			}
		}
	}

	private static void deleteAccountActionDates(AccountBO account) {
		Session session = HibernateUtil.getSessionTL();
		for (AccountActionDateEntity actionDates : account
				.getAccountActionDates()) {
			if (account
					.getAccountType()
					.getAccountTypeId()
					.equals(
							org.mifos.application.accounts.util.helpers.AccountTypes.LOANACCOUNT)) {
				LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) actionDates;
				for (AccountFeesActionDetailEntity actionFees : loanScheduleEntity
						.getAccountFeesActionDetails()) {
					session.delete(actionFees);
				}
			}
			if (account
					.getAccountType()
					.getAccountTypeId()
					.equals(
							org.mifos.application.accounts.util.helpers.AccountTypes.CUSTOMERACCOUNT)) {
				CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) actionDates;
				for (AccountFeesActionDetailEntity actionFees : customerScheduleEntity
						.getAccountFeesActionDetails()) {
					session.delete(actionFees);
				}
			}
			session.delete(actionDates);
		}
	}

	private static void deleteAccountFees(AccountBO account) {
		Session session = HibernateUtil.getSessionTL();
		for (AccountFeesEntity accountFees : account.getAccountFees()) {
			session.delete(accountFees);
		}
	}

	private static void deleteSpecificAccount(AccountBO account) {
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
			try {
				PrdOfferingMeetingEntity prdOfferingMeeting1 = savings
						.getSavingsOffering().getTimePerForInstcalc();
				prdOfferingMeeting1.setMeeting(null);
				session.delete(prdOfferingMeeting1);
			} catch (ProductDefinitionException e) {
				e.printStackTrace();
			}
			try {
				PrdOfferingMeetingEntity prdOfferingMeeting2 = savings
						.getSavingsOffering().getFreqOfPostIntcalc();
				prdOfferingMeeting2.setMeeting(null);
				session.delete(prdOfferingMeeting2);
			} catch (ProductDefinitionException e) {
				e.printStackTrace();
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
		for (AccountFeesEntity accountFees : account.getAccountFees()) {
			if (!feeList.contains(accountFees.getFees()))
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

	private static void deleteCustomers(List<CustomerBO> customerList) {
		List<FeeBO> feeList = new ArrayList<FeeBO>();
		for (CustomerBO customer : customerList) {
			Session session = HibernateUtil.getSessionTL();
			session.lock(customer, LockMode.UPGRADE);
			for (AccountBO account : customer.getAccounts()) {
				for (AccountFeesEntity accountFees : account.getAccountFees()) {
					if (!feeList.contains(accountFees.getFees()))
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
		session.lock(customer, LockMode.NONE);
		deleteCenterMeeting(customer);
		deleteClientAttendence(customer);
		deleteCustomerNotes(customer);
		deleteClientOfferings(customer);
		
		List<FeeBO> feeList = new ArrayList<FeeBO>();
		for (AccountBO account : customer.getAccounts()) {
			if (null != account) {
				for (AccountFeesEntity accountFee : account.getAccountFees())
					if (!feeList.contains(accountFee.getFees()))
						feeList.add(accountFee.getFees());
				deleteAccountWithoutFee(account);
			}
		}
		session.delete(customer);
		deleteFees(feeList);		
		transaction.commit();
	}

	private static void deleteClientOfferings(CustomerBO customer){
		Session session = HibernateUtil.getSessionTL();
		if(customer instanceof ClientBO){
			Set<ClientInitialSavingsOfferingEntity> clientOfferings = ((ClientBO)customer).getOfferingsAssociatedInCreate();
			if(clientOfferings!=null){
				for(ClientInitialSavingsOfferingEntity clientOffering: clientOfferings)
					session.delete(clientOffering);
			}
		}
	}
	
	private static void deleteCustomerNotes(CustomerBO customer) {
		Session session = HibernateUtil.getSessionTL();
		Set<CustomerNoteEntity> customerNotes = customer.getCustomerNotes();
		if (customerNotes != null && customerNotes.size() > 0){
			for (CustomerNoteEntity customerNote : customerNotes) {
				session.delete(customerNote);
			}
		}
		
	}

	private static void deleteCenterMeeting(CustomerBO customer) {
		Session session = HibernateUtil.getSessionTL();
		if (customer instanceof CenterBO) {
			session.delete(customer.getCustomerMeeting());
		}
	}

	private static void deleteClientAttendence(CustomerBO customer) {
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
		return new Money(String.valueOf(amnt));
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

	private static UserContext userContext;
	public static UserContext getContext()
	{
		try
		{
			if(userContext == null)
			{
				userContext = getUserContext();
			}
			return userContext;
		}
		catch(Exception e)
		{
			
		}
		return null;
	}
	
	private static ActivityContext ac;
	public  static ActivityContext getActivityContext()
	{
		if(ac == null)
		{
			UserContext uc = getContext();
			 ac = new ActivityContext((short) 0, uc
				.getBranchId().shortValue(), uc.getId().shortValue());
		}
		return ac;

	}

	private static final ThreadLocal<TestObjectsHolder> threadLocal = new ThreadLocal<TestObjectsHolder>();
	
	public static Object addObject(Object obj)
	{
		TestObjectsHolder holder   = threadLocal.get();
		if(holder == null)
		{
			holder = new TestObjectsHolder();
			threadLocal.set(holder);
		}
		holder.addObject(obj);
		
		return obj;
	}
	
	public static void cleanUpTestObjects()
	{
		TestObjectsHolder holder   = threadLocal.get();
		if(holder != null)
		   holder.removeObjects();
		
		holder = null;
		threadLocal.set(null);
		
	}

	/**
	 * Also see {@link TestUtils#makeUser(int)} which should be
	 * faster (this method involves several database accesses).
	 */
	public static UserContext getUserContext() 
	throws SystemException, InvalidUserException, ApplicationException  {
		byte[] password = EncryptionService.getInstance()
				.createEncryptedPassword("mifos");
		PersonnelBO personnel = getPersonnel(Short.valueOf("1"));
		personnel.setEncriptedPassword(password);
		updateObject(personnel);
		return personnel.login("mifos");

	}

	public static void flushandCloseSession() {
		testObjectPersistence.flushandCloseSession();
	}

	public static Object getObject(Class clazz, Integer pk) {
		return addObject(testObjectPersistence.getObject(clazz, pk));

	}

	public static Object getObject(Class clazz, Short pk) {
		return addObject(testObjectPersistence.getObject(clazz, pk));

	}

	public static CustomerCheckListBO createCustomerChecklist(
			Short customerLevel, Short customerStatus, Short checklistStatus){
		Session session = HibernateUtil.getSessionTL();
		session.beginTransaction();
		CustomerCheckListBO customerChecklist = new CustomerCheckListBO();
		customerChecklist.setChecklistName("productchecklist");
		customerChecklist.setChecklistStatus(checklistStatus);

		SupportedLocalesEntity supportedLocales = new SupportedLocalesEntity();
		supportedLocales.setLocaleId(Short.valueOf("1"));
		customerChecklist.setSupportedLocales(supportedLocales);

		CheckListDetailEntity checkListDetailEntity = new CheckListDetailEntity();
		checkListDetailEntity.setDetailText("item1");
		checkListDetailEntity.setAnswerType(Short.valueOf("1"));
		checkListDetailEntity.setSupportedLocales(supportedLocales);
		customerChecklist.addChecklistDetail(checkListDetailEntity);
		CustomerLevelEntity customerLevelEntity = (CustomerLevelEntity) 
				session.get(CustomerLevelEntity.class, customerLevel);
		CustomerStatusEntity customerStatusEntity = (CustomerStatusEntity) 
				session.get(CustomerStatusEntity.class, customerStatus);
		customerChecklist.setCustomerLevel(customerLevelEntity); 
		customerChecklist.setCustomerStatus(customerStatusEntity);
		customerChecklist.create();
		HibernateUtil.commitTransaction();
		return customerChecklist;
	}
	
	public static void cleanUp(CheckListBO checkListBO) {
		if (null != checkListBO) {
			deleteChecklist(checkListBO);
			checkListBO=null;
		}
	}

	public static void deleteChecklist(CheckListBO checkListBO ) {
			Session session = HibernateUtil.getSessionTL();
			Transaction	transaction = HibernateUtil.startTransaction();
				
			
			if (checkListBO.getChecklistDetailSet() != null) {
				for (CheckListDetailEntity checklistDetail : checkListBO.getChecklistDetailSet()) {
					if (null != checklistDetail) {
						session.delete(checklistDetail);
					}
				}
			}
			session.delete(checkListBO);
			transaction.commit();
		
		}


	public static void cleanUp(ReportsCategoryBO reportsCategoryBO) {
		if (null != reportsCategoryBO) {
			deleteReportCategory(reportsCategoryBO);
			reportsCategoryBO=null;
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
			reportsBO=null;
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
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		MeetingBO meeting = createLoanMeeting(customer.getCustomerMeeting()
				.getMeeting());
		List<Date> meetingDates = getMeetingDates(meeting, 6);
		LoanBO loan = null;
		MifosCurrency currency = testObjectPersistence.getCurrency();
			try {
				loan = new LoanBO(TestObjectFactory.getUserContext(), loanOfering, customer,
						AccountState.getStatus(accountStateId),new Money(currency, "300.0"),
						Short.valueOf("6"),meetingDates.get(0),false,10.0,
						(short)0,new FundBO(),new ArrayList<FeeView>());
			} catch (NumberFormatException e) {
			} catch (AccountException e) {
				e.printStackTrace();
			} catch (InvalidUserException e) {
			} catch (PropertyNotFoundException e) {
			} catch (SystemException e) {
			} catch (ApplicationException e) {
			}
		FeeBO maintanenceFee = createPeriodicAmountFee("Mainatnence Fee",
				FeeCategory.LOAN, "100", RecurrenceType.WEEKLY, Short
						.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan,maintanenceFee,new Double("10.0"));
		loan.addAccountFees(accountPeriodicFee);
		AccountFeesEntity accountDisbursementFee = null;
		FeeBO disbursementFee = null;
		AccountFeesEntity accountDisbursementFee2 = null;
		FeeBO disbursementFee2 = null;

		if (disbursalType == 1 || disbursalType == 2) {
			disbursementFee = createOneTimeAmountFee("Disbursement Fee 1",
					FeeCategory.LOAN, "10", FeePayment.TIME_OF_DISBURSMENT);
			accountDisbursementFee = new AccountFeesEntity(loan,disbursementFee,new Double("10.0"));
			loan.addAccountFees(accountDisbursementFee);

			disbursementFee2 = createOneTimeAmountFee("Disbursement Fee 2",
					FeeCategory.LOAN, "20", FeePayment.TIME_OF_DISBURSMENT);
			accountDisbursementFee2 = new AccountFeesEntity(loan,disbursementFee2,new Double("20.0"));
			loan.addAccountFees(accountDisbursementFee2);
		}
		loan.setLoanMeeting(meeting);

		if (disbursalType == 2)// 2-Interest At Disbursment
		{
			loan.setInterestDeductedAtDisbursement(true);
			meetingDates = getMeetingDates(loan.getLoanMeeting(), 6);
			short i = 0;
			for (Date date : meetingDates) {
				if (i == 0) {
					i++;
					loan.setDisbursementDate(date);
					LoanScheduleEntity actionDate = (LoanScheduleEntity)loan.getAccountActionDate(i);
					actionDate.setActionDate(new java.sql.Date(date.getTime()));
					actionDate.setInterest(new Money(currency, "12.0"));
					actionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
					loan.addAccountActionDate(actionDate);

					// periodic fee
					AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
							actionDate,  maintanenceFee, accountPeriodicFee,
							new Money(currency, "10.0"));
					accountFeesaction.setFeeAmountPaid(new Money(currency,
							"0.0"));
					actionDate.addAccountFeesAction(accountFeesaction);

					// dibursement fee one
					AccountFeesActionDetailEntity accountFeesaction1 = new LoanFeeScheduleEntity(
							actionDate,  disbursementFee,
							accountDisbursementFee, new Money(currency, "10.0"));

					accountFeesaction1.setFeeAmountPaid(new Money(currency,
							"0.0"));
					actionDate.addAccountFeesAction(accountFeesaction1);

					// disbursementfee2
					AccountFeesActionDetailEntity accountFeesaction2 = new LoanFeeScheduleEntity(
							actionDate, disbursementFee2,
							accountDisbursementFee2,
							new Money(currency, "20.0"));
					accountFeesaction2.setFeeAmountPaid(new Money(currency,
							"0.0"));
					actionDate.addAccountFeesAction(accountFeesaction2);

					continue;
				}
				i++;
				LoanScheduleEntity actionDate = (LoanScheduleEntity)loan.getAccountActionDate(i);
				actionDate.setActionDate(new java.sql.Date(date.getTime()));
				actionDate.setPrincipal(new Money(currency, "100.0"));
				actionDate.setInterest(new Money(currency, "12.0"));
				actionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
				loan.addAccountActionDate(actionDate);
				AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
						actionDate, maintanenceFee, accountPeriodicFee,
						new Money(currency, "100.0"));
				accountFeesaction.setFeeAmountPaid(new Money(currency, "0.0"));
				actionDate.addAccountFeesAction(accountFeesaction);
			}

		} else if (disbursalType == 1 || disbursalType == 3) {
			loan.setInterestDeductedAtDisbursement(false);
			meetingDates = getMeetingDates(loan.getLoanMeeting(), 6);

			short i = 0;
			for (Date date : meetingDates) {

				if (i == 0) {
					i++;
					loan.setDisbursementDate(date);
					continue;
				} 
					LoanScheduleEntity actionDate = (LoanScheduleEntity)loan.getAccountActionDate(i++);
					actionDate.setActionDate(new java.sql.Date(date.getTime()));
					actionDate.setPrincipal(new Money(currency, "100.0"));
					actionDate.setInterest(new Money(currency, "12.0"));
					actionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
					loan.addAccountActionDate(actionDate);
					
/*				
				LoanScheduleEntity actionDate = new LoanScheduleEntity(loan,
						customer, i++, new java.sql.Date(date.getTime()),
						PaymentStatus.UNPAID, new Money(currency, "100.0"),
						new Money(currency, "12.0"));
				loan.addAccountActionDate(actionDate);
*/
				AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
						actionDate,maintanenceFee, accountPeriodicFee,
						new Money(currency, "100.0"));
				accountFeesaction.setFeeAmountPaid(new Money(currency, "0.0"));
				actionDate.addAccountFeesAction(accountFeesaction);
			}
		}
		GracePeriodTypeEntity gracePeriodType=null;
		try {
			gracePeriodType = new GracePeriodTypeEntity(GraceTypeConstants.getGraceTypeConstants(Short.valueOf("1")));
		}catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		loan.setGracePeriodType(gracePeriodType);
		loan.setCreatedBy(Short.valueOf("1"));
		
		CollateralTypeEntity collateralType = new CollateralTypeEntity(Short.valueOf("1"));
		loan.setCollateralType(collateralType);

		InterestTypesEntity interestTypes =null;
		try {
			interestTypes = new InterestTypesEntity(InterestTypeConstants.getInterestTypeConstants(Short.valueOf("1")));
		}catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		loan.setInterestType(interestTypes);
		loan.setInterestRate(10.0);
		loan.setCreatedDate(new Date(System.currentTimeMillis()));

		LoanSummaryEntity loanSummary = loan.getLoanSummary();
		loanSummary.setOriginalPrincipal(new Money(currency, "300.0"));
		loanSummary.setOriginalInterest(new Money(currency, "36.0"));
		
		try {
			loan.save();
		} catch (AccountException e) {
			e.printStackTrace();
		}
		HibernateUtil.commitTransaction();
		return (LoanBO) addObject(getObject(LoanBO.class, loan.getAccountId()));
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
			if (account
					.getAccountType()
					.getAccountTypeId()
					.equals(
							org.mifos.application.accounts.util.helpers.AccountTypes.LOANACCOUNT)) {
				LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) actionDates;
				for (AccountFeesActionDetailEntity actionFees : loanScheduleEntity
						.getAccountFeesActionDetails()) {
					session.delete(actionFees);
				}
			}
			if (account
					.getAccountType()
					.getAccountTypeId()
					.equals(
							org.mifos.application.accounts.util.helpers.AccountTypes.CUSTOMERACCOUNT)) {
				CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) actionDates;
				for (AccountFeesActionDetailEntity actionFees : customerScheduleEntity
						.getAccountFeesActionDetails()) {
					session.delete(actionFees);
				}
			}
			session.delete(actionDates);
		}

		List<FeeBO> feeList = new ArrayList<FeeBO>();
		for (AccountFeesEntity accountFees : account.getAccountFees()) {
			if (!feeList.contains(accountFees.getFees()))
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
		session.delete(account);

		if (newSession) {
			transaction.commit();
		}
	}

	public static void cleanUpWithoutDeletetingProduct(AccountBO account) {
		if (null != account) {
			deleteAccountWithoutDeletetingProduct(account, null);
			account=null;
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

	public static CustomerAccountView getCustomerAccountView(CustomerBO customer) throws Exception {
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
			Integer accountId, java.sql.Date transactionDate) throws Exception{
		List<AccountActionDateEntity> dueActionDates = new BulkEntryBusinessService()
				.retrieveCustomerAccountActionDetails(accountId,
						transactionDate);
		for (AccountActionDateEntity accountActionDate : dueActionDates) {
			Hibernate.initialize(accountActionDate);

			if (accountActionDate instanceof LoanScheduleEntity) {
				LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
				for (AccountFeesActionDetailEntity accountFeesActionDetail : loanScheduleEntity
						.getAccountFeesActionDetails()) {
					Hibernate.initialize(accountFeesActionDetail);
				}
			}
			if (accountActionDate instanceof CustomerScheduleEntity) {
				CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDate;
				for (AccountFeesActionDetailEntity accountFeesActionDetail : customerScheduleEntity
						.getAccountFeesActionDetails()) {
					Hibernate.initialize(accountFeesActionDetail);
				}
			}
		}
		HibernateUtil.closeSession();
		return dueActionDates;
	}

	public static void cleanUp(CollectionSheetBO collSheet) {
		if (null != collSheet) {
			deleteCollectionSheet(collSheet, null);
			collSheet=null;
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
		return paymentData;
	}

	public static LoanAccountView getLoanAccountView(LoanBO loan) {
		Short interestDedAtDisb = loan.isInterestDeductedAtDisbursement() ? 
				LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT : (short)0;
		return new LoanAccountView(loan.getAccountId(), loan.getLoanOffering()
				.getPrdOfferingName(),
				loan.getAccountType().getAccountTypeId(), loan
						.getLoanOffering().getPrdOfferingId(), loan
						.getAccountState().getId(), interestDedAtDisb, loan.getLoanBalance());

	}

	public static BulkEntryInstallmentView getBulkEntryAccountActionView(
			AccountActionDateEntity accountActionDateEntity) {
		BulkEntryInstallmentView bulkEntryAccountActionView =null;
		if(accountActionDateEntity instanceof LoanScheduleEntity) {
			LoanScheduleEntity actionDate = (LoanScheduleEntity)accountActionDateEntity;
			BulkEntryLoanInstallmentView installmentView = new BulkEntryLoanInstallmentView(
					actionDate.getAccount().getAccountId(), actionDate
							.getCustomer().getCustomerId(), actionDate
							.getInstallmentId(), actionDate.getActionDateId(),actionDate.getActionDate(),
					actionDate.getPrincipal(), actionDate.getPrincipalPaid(),
					actionDate.getInterest(), actionDate.getInterestPaid(),
					actionDate.getMiscFee(), actionDate.getMiscFeePaid(),
					actionDate.getPenalty(), actionDate.getPenaltyPaid(),
					actionDate.getMiscPenalty(), actionDate.getMiscPenaltyPaid());
			installmentView.setBulkEntryAccountFeeActions(getBulkEntryAccountFeeActionViews(accountActionDateEntity));
			bulkEntryAccountActionView = installmentView;
		} else if(accountActionDateEntity instanceof SavingsScheduleEntity) {
			SavingsScheduleEntity actionDate = (SavingsScheduleEntity)accountActionDateEntity;
			BulkEntrySavingsInstallmentView installmentView = new BulkEntrySavingsInstallmentView(
					actionDate.getAccount().getAccountId(), actionDate
							.getCustomer().getCustomerId(), actionDate
							.getInstallmentId(), actionDate.getActionDateId(),actionDate.getActionDate(),
							actionDate.getDeposit(), actionDate.getDepositPaid());
			bulkEntryAccountActionView = installmentView;
			
		}else if(accountActionDateEntity instanceof CustomerScheduleEntity) {
			CustomerScheduleEntity actionDate = (CustomerScheduleEntity)accountActionDateEntity;
			BulkEntryCustomerAccountInstallmentView installmentView = new BulkEntryCustomerAccountInstallmentView(
					actionDate.getAccount().getAccountId(), actionDate
							.getCustomer().getCustomerId(), actionDate
							.getInstallmentId(), actionDate.getActionDateId(),actionDate.getActionDate(),
							actionDate.getMiscFee(), actionDate.getMiscFeePaid(),
							actionDate.getMiscPenalty(), actionDate.getMiscPenaltyPaid());
			installmentView.setBulkEntryAccountFeeActions(getBulkEntryAccountFeeActionViews(accountActionDateEntity));
			bulkEntryAccountActionView = installmentView;				
		}
		return bulkEntryAccountActionView;
	}

	public static BulkEntryAccountFeeActionView getBulkEntryAccountFeeActionView(
			AccountFeesActionDetailEntity feeAction) {
		return new BulkEntryAccountFeeActionView(feeAction
				.getAccountActionDate().getActionDateId(), feeAction.getFee(),
				feeAction.getFeeAmount(), feeAction.getFeeAmountPaid());

	}

	public static List<BulkEntryAccountFeeActionView> getBulkEntryAccountFeeActionViews(
			AccountActionDateEntity accountActionDateEntity) {
		List<BulkEntryAccountFeeActionView> bulkEntryFeeViews = new ArrayList<BulkEntryAccountFeeActionView>();
		Set<AccountFeesActionDetailEntity> feeActions = null;
		if(accountActionDateEntity instanceof LoanScheduleEntity) {
			feeActions = ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails();
		} else if(accountActionDateEntity instanceof CustomerScheduleEntity) {
			feeActions = ((CustomerScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails();
		}
		if (feeActions != null	&& feeActions.size() > 0) {
			for (AccountFeesActionDetailEntity accountFeesActionDetail : feeActions) {
				bulkEntryFeeViews.add(getBulkEntryAccountFeeActionView(accountFeesActionDetail));
			}
		}
		return bulkEntryFeeViews;

	}

	public static List<BulkEntryInstallmentView> getBulkEntryAccountActionViews(
			List<AccountActionDateEntity> actionDates) {
		List<BulkEntryInstallmentView> bulkEntryActionViews = new ArrayList<BulkEntryInstallmentView>();
		if (actionDates != null && actionDates.size() > 0) {
			for (AccountActionDateEntity actionDate : actionDates) {
				bulkEntryActionViews
						.add(getBulkEntryAccountActionView(actionDate));
			}
		}
		return bulkEntryActionViews;

	}
	
	public static CustomerNoteEntity getCustomerNote (String comment , CustomerBO customer){
		java.sql.Date commentDate = new java.sql.Date(System.currentTimeMillis());
		CustomerNoteEntity notes = new CustomerNoteEntity(comment, commentDate , customer.getPersonnel() , customer );
		return (CustomerNoteEntity)addObject(notes);
	}
	
	public static OfficeBO createOffice(OfficeLevel level, OfficeBO parentOffice, String officeName, String shortName)throws Exception{
		OfficeBO officeBO=	new OfficeBO(TestObjectFactory.getUserContext(), level, parentOffice , null, officeName, shortName, null,
				OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		return (OfficeBO)addObject(officeBO);
	}
	
	public static void cleanUp(OfficeBO office){
		if(office!=null){
			Session session= HibernateUtil.getSessionTL();
			Transaction transaction = HibernateUtil.startTransaction();
			session.lock(office, LockMode.NONE);
			session.delete(office);
			transaction.commit();
		}
	}
	
	public static void removeCustomerFromPosition(CustomerBO customer) throws CustomerException {
		if(customer != null) {
			for(CustomerPositionEntity customerPositionEntity : customer.getCustomerPositions())
				customerPositionEntity.setCustomer(null);
			customer.update();
			HibernateUtil.commitTransaction();
		}
	}
	public static void cleanUp(PersonnelBO personnel){
		if(personnel!=null){
			Session session= HibernateUtil.getSessionTL();
			Transaction transaction = HibernateUtil.startTransaction();
			session.lock(personnel, LockMode.NONE);
			session.delete(personnel);
			transaction.commit();
		}
	}
	
	public static PersonnelBO createPersonnel(PersonnelLevel level,
			OfficeBO office, Integer title, Short preferredLocale,
			String password, String userName, String emailId,
			List<RoleBO> personnelRoles,
			List<CustomFieldView> customFields, Name name,
			String governmentIdNumber, Date dob, Integer maritalStatus,
			Integer gender, Date dateOfJoiningMFI, Date dateOfJoiningBranch,
			Address address) throws Exception{
		PersonnelBO personnelBO = new  PersonnelBO(level,  office,
				title,preferredLocale,password,
				userName,emailId,personnelRoles,
				customFields,
				name,governmentIdNumber,
				dob,maritalStatus,gender,
				dateOfJoiningMFI,dateOfJoiningBranch,address,
				null);
		
		personnelBO.save();
		HibernateUtil.commitTransaction();
		return personnelBO;
	}
	
	public static void simulateInvalidConnection() {
		HibernateUtil.getSessionTL().close();
	}
	
	public static void cleanUp(RoleBO roleBO){
		if(roleBO!=null){
			Session session= HibernateUtil.getSessionTL();
			Transaction transaction = HibernateUtil.startTransaction();
			session.lock(roleBO, LockMode.NONE);
			session.delete(roleBO);
			transaction.commit();
		}
	}
	
	public static RoleBO createRole(UserContext context, String roleName,
			List<ActivityEntity> activities) throws Exception {
		RoleBO roleBO = new RoleBO(context, roleName, activities);
		roleBO.save();
		HibernateUtil.commitTransaction();
		return roleBO;
	}
	
	public static void cleanUp(FundBO fundBO){
		if(fundBO!=null){
			Session session= HibernateUtil.getSessionTL();
			Transaction transaction = HibernateUtil.startTransaction();
			session.lock(fundBO, LockMode.NONE);
			session.delete(fundBO);
			transaction.commit();
		}
	}
	
	public static FundBO createFund(FundCodeEntity fundCode, String fundName) throws Exception {
		FundBO fundBO = new FundBO(fundCode,fundName);
		fundBO.save();
		HibernateUtil.commitTransaction();
		return fundBO;
	}
}
