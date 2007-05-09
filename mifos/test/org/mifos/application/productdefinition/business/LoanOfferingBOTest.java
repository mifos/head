/**

 * LoanOfferingBOTest.java    version: 1.0

 

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
package org.mifos.application.productdefinition.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanOfferingBOTest extends MifosTestCase {

	private LoanOfferingBO product;

	private FeeBO periodicFee;

	private FeeBO oneTimeFee;

	private PrdApplicableMasterEntity prdApplicableMaster;

	private MeetingBO frequency;

	private GLCodeEntity principalglCodeEntity;

	private GLCodeEntity intglCodeEntity;

	private ProductCategoryBO productCategory;

	private InterestTypesEntity interestTypes;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(product);
		TestObjectFactory.cleanUp(periodicFee);
		TestObjectFactory.cleanUp(oneTimeFee);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public static void setStatus(LoanOfferingBO loanOffering,PrdStatusEntity prdStatus) {
		loanOffering.setPrdStatus(prdStatus);
	}
	
	public static void setGracePeriodType(LoanOfferingBO loanOffering,GracePeriodTypeEntity gracePeriodType) {
		loanOffering.setGracePeriodType(gracePeriodType);
	}
	
	public void testUpdateLoanOfferingFeeTypesForLogging() throws Exception {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Loan Periodic", FeeCategory.LOAN, "100",
				RecurrenceType.MONTHLY, (short) 1);
		
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(periodicFee);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		HibernateUtil.getInterceptor().createInitialValueMap(product);
		product.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, false, false, null, fees, (short) 2,
				RecurrenceType.MONTHLY);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		product = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, product.getPrdOfferingId());
		fees = new ArrayList<FeeBO>();
		oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time",
				FeeCategory.LOAN, "100", FeePayment.UPFRONT);
		fees.add(oneTimeFee);
		product.setUserContext(TestUtils.makeUser());
		HibernateUtil.getInterceptor().createInitialValueMap(product);
		product.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, null, fees, (short) 2,
				RecurrenceType.MONTHLY);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
				
		product = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, product.getPrdOfferingId());
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(
				EntityType.LOANPRODUCT,new Integer(product.getPrdOfferingId().toString()));
		assertEquals(2,auditLogList.size());
		assertEquals(EntityType.LOANPRODUCT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(19,auditLogList.get(0).getAuditLogRecords().size());
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Fee Types") && auditLogRecord.getNewValue().equalsIgnoreCase("Loan Periodic")){
				assertEquals("-",auditLogRecord.getOldValue());
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Fee Types") && auditLogRecord.getNewValue().equalsIgnoreCase("Loan One time")){
				assertEquals("Loan Periodic",auditLogRecord.getOldValue());
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Service Charge deducted At Disbursement")){
				assertEquals("1",auditLogRecord.getOldValue());
				assertEquals("0",auditLogRecord.getNewValue());
			}
		}
		TestObjectFactory.cleanUpChangeLog();

	}
	
	public void testUpdateLoanOfferingForLogging() throws ProductDefinitionException,
			FeeException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Loan Periodic", FeeCategory.LOAN, "100",
				RecurrenceType.MONTHLY, (short) 1);
		oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time",
				FeeCategory.LOAN, "100", FeePayment.UPFRONT);
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(periodicFee);
		fees.add(oneTimeFee);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		HibernateUtil.getInterceptor().createInitialValueMap(product);
		product.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, null, fees, (short) 2,
				RecurrenceType.MONTHLY);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		product = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, product.getPrdOfferingId());
		
		
		List<AuditLog> auditLogList=TestObjectFactory.getChangeLog(
				EntityType.LOANPRODUCT,new Integer(product.getPrdOfferingId().toString()));
		assertEquals(1,auditLogList.size());
		assertEquals(EntityType.LOANPRODUCT.getValue(),auditLogList.get(0).getEntityType());
		assertEquals(18,auditLogList.get(0).getAuditLogRecords().size());
		for(AuditLogRecord auditLogRecord :  auditLogList.get(0).getAuditLogRecords()){
			if(auditLogRecord.getFieldName().equalsIgnoreCase("Min Loan Amount")){
				assertEquals("300.0",auditLogRecord.getOldValue());
				assertEquals("1000.0",auditLogRecord.getNewValue());
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Description")){
				assertEquals("-",auditLogRecord.getOldValue());
				assertEquals("Loan Product updated",auditLogRecord.getNewValue());
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Applicable For")){
				assertEquals("Groups",auditLogRecord.getOldValue());
				assertEquals("Clients",auditLogRecord.getNewValue());
			}else if(auditLogRecord.getFieldName().equalsIgnoreCase("Frequency Of Installments")){
				assertEquals("Week(s)",auditLogRecord.getOldValue());
				assertEquals("Month(s)",auditLogRecord.getNewValue());
			}
		}
		TestObjectFactory.cleanUpChangeLog();

	}


	public void testBuildloanOfferingWithoutDataForMandatoryFields() {
		try {
			new LoanOfferingBO(null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, false, false, false, null, null, null);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithoutDataForAllFields() {
		try {
			new LoanOfferingBO(null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, false, false,
					false, null, null, null, null, null);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithoutName() throws 
			SystemException, ApplicationException {
		createIntitalObjects();
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), null, "S", productCategory,
					prdApplicableMaster, new Date(System.currentTimeMillis()),
					interestTypes, new Money("1000"), new Money("3000"), 12.0,
					2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
					false, frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithShortNameGreaterThanFourDig()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOANOFF", productCategory,
					prdApplicableMaster, new Date(System.currentTimeMillis()),
					interestTypes, new Money("1000"), new Money("3000"), 12.0,
					2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
					false, frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithStartDateLessThanCurrentDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(-2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAN", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildWithStartDateEqualToCurrentDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		LoanOfferingBO product = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAN", productCategory,
				prdApplicableMaster, startDate, interestTypes,
				new Money("1000"), new Money("3000"), 12.0, 2.0, 3.0,
				(short) 20, (short) 1, (short) 12, false, true, false,
				frequency, principalglCodeEntity, intglCodeEntity);
		assertNotNull(product.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
	}

	public void testStartDateGreaterThanCurrentDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(2);
		LoanOfferingBO product = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAN", productCategory,
				prdApplicableMaster, startDate, interestTypes,
				new Money("1000"), new Money("3000"), 12.0, 2.0, 3.0,
				(short) 20, (short) 1, (short) 12, false, true, false,
				frequency, principalglCodeEntity, intglCodeEntity);
		assertNotNull(product.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.LOAN_INACTIVE, product.getStatus());

	}

	public void testBuildloanOfferingWithDuplicatePrdOfferingName()
			throws  SystemException, ApplicationException {
		product = createLoanOfferingBO("Loan Product", "LOAP");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Product", "LOAN", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithDuplicatePrdOfferingShortName()
			throws  SystemException, ApplicationException {
		product = createLoanOfferingBO("Loan Product", "LOAP");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithEndDateLessThanStartDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(-2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"), null,
					12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
					false, false, null, null, frequency, principalglCodeEntity,
					intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithNoInterestTypes()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Product", "LOAN", productCategory,
					prdApplicableMaster, startDate, null, new Money("1000"),
					new Money("3000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, frequency,
					principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithNoMaxAmount()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000.0"), null, 12.0, 2.0, 3.0, (short) 20,
					(short) 1, (short) 12, false, false, false, frequency,
					principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildloanOfferingWithoutGLCode()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000.0"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, null, null);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testDefAmountNotBetweenMinMaxAmounts() {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("200.0"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, null, null, frequency,
					principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testMinAmountGreaterThanMaxAmount() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("10000"), new Money("3000"), null,
					12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
					false, false, null, null, frequency, principalglCodeEntity,
					intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testDefInterestRateNotBetweenMinMaxRates() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 2.0, 13.0, (short) 20,
					(short) 1, (short) 12, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testMinInterestRateGretaterThanMaxRate() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 20.0, 13.0, (short) 20,
					(short) 1, (short) 12, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testMinInstallmentsGreaterThanMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20,
					(short) 31, (short) 21, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testDefInstallmentsNotBetweenMinMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20,
					(short) 11, (short) 7, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testGracePeriodForIntDedAtDisb()
			throws ProductDefinitionException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAP", productCategory,
				prdApplicableMaster, startDate, endDate, null, null, null,
				interestTypes, new Money("1000"), new Money("3000"), new Money(
						"2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
				(short) 17, false, true, false, null, null, frequency,
				principalglCodeEntity, intglCodeEntity);
		assertNotNull(loanOffering.getGracePeriodType());
		assertNotNull(loanOffering.getGracePeriodDuration());
		assertEquals(GraceType.NONE.getValue(), loanOffering
				.getGracePeriodType().getId());
		assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
	}

	public void testNullGracePeriodDurationWithGraceType()
			throws ProductDefinitionException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.PRINCIPALONLYGRACE);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null,
					gracePeriodType, null, interestTypes, new Money("1000"),
					new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0,
					(short) 20, (short) 11, (short) 17, false, false, false,
					null, null, frequency, principalglCodeEntity,
					intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testNullGracePeriodDurationForNoneGraceType()
			throws ProductDefinitionException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.NONE);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAP", productCategory,
				prdApplicableMaster, startDate, endDate, null, gracePeriodType,
				null, interestTypes, new Money("1000"), new Money("3000"),
				new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
				(short) 17, false, false, false, null, null, frequency,
				principalglCodeEntity, intglCodeEntity);
		assertNotNull(loanOffering.getGracePeriodDuration());
		assertEquals(GraceType.NONE.getValue(), loanOffering
				.getGracePeriodType().getId());
		assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
	}

	public void testFeeNotMatchingFrequencyOfLoanOffering()
			throws ProductDefinitionException, FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.NONE);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory
						.getNewMeeting(MONTHLY, EVERY_MONTH, CUSTOMER_MEETING, MONDAY)));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		try {
			new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null,
					gracePeriodType, null, interestTypes, new Money("1000"),
					new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0,
					(short) 20, (short) 11, (short) 17, false, false, false,
					null, fees, frequency, principalglCodeEntity,
					intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testWithFundsAndOneTimeFee() throws ProductDefinitionException,
			FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.GRACEONALLREPAYMENTS);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory.getTypicalMeeting()));
		FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				intglCodeEntity, new Money("100"), false, new FeePaymentEntity(
						FeePayment.UPFRONT));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		fees.add(fee1);
		List<FundBO> funds = new ArrayList<FundBO>();
		funds.add(new FundBO());
		LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAP", productCategory,
				prdApplicableMaster, startDate, endDate, null, gracePeriodType,
				(short) 2, interestTypes, new Money("1000"), new Money("3000"),
				new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
				(short) 17, false, false, false, funds, fees, frequency,
				principalglCodeEntity, intglCodeEntity);
		assertEquals(2, loanOffering.getLoanOfferingFees().size());
		assertEquals(1, loanOffering.getLoanOfferingFunds().size());
	}

	public void testCreateLoanOffering() throws ProductDefinitionException,
			FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.GRACEONALLREPAYMENTS);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Loan Periodic", FeeCategory.LOAN, "100",
				RecurrenceType.WEEKLY, (short) 1);
		oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time",
				FeeCategory.LOAN, "100", FeePayment.UPFRONT);
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(periodicFee);
		fees.add(oneTimeFee);

		product = new LoanOfferingBO(TestObjectFactory.getContext(),
				"Loan Offering", "LOAP", productCategory, prdApplicableMaster,
				startDate, endDate, "1234", gracePeriodType, (short) 2,
				interestTypes, new Money("1000"), new Money("3000"), new Money(
						"2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
				(short) 17, false, false, false, null, fees, frequency,
				principalglCodeEntity, intglCodeEntity);
		product.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		product = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, product.getPrdOfferingId());

		assertEquals("Loan Offering", product.getPrdOfferingName());
		assertEquals("LOAP", product.getPrdOfferingShortName());
		assertEquals(Short.valueOf("1"), product.getPrdCategory()
				.getProductCategoryID());
		assertEquals(ApplicableTo.CLIENTS, 
				product.getPrdApplicableMasterEnum());
		assertEquals(startDate, product.getStartDate());
		assertEquals(endDate, product.getEndDate());
		assertEquals("1234", product.getDescription());
		assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(),
				product.getGracePeriodType().getId());
		assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
		assertEquals(InterestType.FLAT.getValue(), product
				.getInterestTypes().getId());
		assertEquals(new Money("1000"), product.getMinLoanAmount());
		assertEquals(new Money("3000"), product.getMaxLoanAmount());
		assertEquals(new Money("2000"), product.getDefaultLoanAmount());
		assertEquals(2.0, product.getMinInterestRate());
		assertEquals(12.0, product.getMaxInterestRate());
		assertEquals(3.0, product.getDefInterestRate());
		assertEquals(Short.valueOf("11"), product.getMinNoInstallments());
		assertEquals(Short.valueOf("20"), product.getMaxNoInstallments());
		assertEquals(Short.valueOf("17"), product.getDefNoInstallments());
		assertFalse(product.isIncludeInLoanCounter());
		assertFalse(product.isIntDedDisbursement());
		assertFalse(product.isPrinDueLastInst());
		assertEquals(2, product.getLoanOfferingFees().size());
		assertNotNull(product.getLoanOfferingMeeting());
		assertEquals(RecurrenceType.WEEKLY, product
				.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
				.getRecurrenceTypeEnum());
		assertNotNull(product.getPrincipalGLcode());
		assertNotNull(product.getInterestGLcode());
	}

	public void testUpdateloanOfferingWithoutDataForMandatoryFields() {
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update(null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null, false, false, false, null, null, null,
					null);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateloanOfferingWithoutName()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, null, "S", productCategory,
					prdApplicableMaster, new Date(System.currentTimeMillis()),
					null, "Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateloanOfferingWithShortNameGreaterThanFourDig()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOANS",
					productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testupdateloanOfferingWithStartDateLessThanCurrentDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		product = createLoanOfferingBO("Loan Product", "LOAP");
		Date startDate = offSetCurrentDate(-2);
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateloanOfferingWithStartDateEqualToCurrentDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		product.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, null, "Loan Product updated",
				PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0,
				new Money("3000"), new Money("1000"), new Money("2000"), 12.0,
				2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
				false, null, null, (short) 2, RecurrenceType.WEEKLY);
		HibernateUtil.commitTransaction();
		product = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, product.getPrdOfferingId());
		assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());

	}

	public void testUpdateloanOfferingWithStartDateGreaterThanCurrentDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}

	}

	public void testUpdateloanOfferingWithDuplicatePrdOfferingName()
			throws  SystemException, ApplicationException {
		product = createLoanOfferingBO("Loan Product", "LOAP");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Product1",
				"LOA1");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			loanOffering1.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testUpdateloanOfferingWithDuplicatePrdOfferingShortName()
			throws  SystemException, ApplicationException {
		product = createLoanOfferingBO("Loan Product", "LOAP");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Product1",
				"LOA1");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			loanOffering1.update((short) 1, "Loan Product1", "LOAP",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testUpdateloanOfferingWithEndDateLessThanStartDate()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(-2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateloanOfferingWithNoInterestTypes()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null, null,
					(short) 0, new Money("3000"), new Money("1000"), new Money(
							"2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, null, null, (short) 2,
					RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testupdateloanOfferingWithNoMaxAmount()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, null, new Money("1000"),
					new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, null, null, (short) 2,
					RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}
	
	public void testupdateloanOfferingInvalidConnection()
			throws  SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		TestObjectFactory.simulateInvalidConnection();
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, null, new Money("1000"),
					new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, null, null, (short) 2,
					RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
}

	public void testUpdateDefAmountNotBetweenMinMaxAmounts() {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("1000"), new Money(
							"3000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateMinAmountGreaterThanMaxAmount() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("1000"), new Money(
							"3000"), new Money("1000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateDefInterestRateNotBetweenMinMaxRates() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 2.0, 13.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateMinInterestRateGretaterThanMaxRate() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 22.0, 12.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateMinInstallmentsGreaterThanMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 2.0, 12.0,
					(short) 2, (short) 12, (short) 2, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateDefInstallmentsNotBetweenMinMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 2.0, 12.0,
					(short) 12, (short) 1, (short) 22, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateGracePeriodForIntDedAtDisb()
			throws ProductDefinitionException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		product.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, null, null, (short) 2,
				RecurrenceType.WEEKLY);
		HibernateUtil.commitTransaction();
		product = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, product.getPrdOfferingId());

		assertNotNull(product.getGracePeriodType());
		assertNotNull(product.getGracePeriodDuration());
		assertEquals(GraceType.NONE.getValue(), product
				.getGracePeriodType().getId());
		assertEquals(Short.valueOf("0"), product.getGracePeriodDuration());
	}

	public void testUpdateFeeNotMatchingFrequencyOfLoanOffering()
			throws ProductDefinitionException, FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.NONE);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory
						.getNewMeeting(MONTHLY, EVERY_MONTH, CUSTOMER_MEETING, MONDAY)));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			product.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOAN_ACTIVE,
					gracePeriodType, interestTypes, (short) 0,
					new Money("3000"), new Money("1000"), new Money("1000"),
					12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 2, false,
					true, false, null, fees, (short) 2, RecurrenceType.WEEKLY);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testUpdateWithFundsAndOneTimeFee()
			throws ProductDefinitionException, FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.GRACEONALLREPAYMENTS);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory
						.getTypicalMeeting()));
		FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				intglCodeEntity, new Money("100"), false, new FeePaymentEntity(
						FeePayment.UPFRONT));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		fees.add(fee1);
		List<FundBO> funds = new ArrayList<FundBO>();
		funds.add(new FundBO());
		product = createLoanOfferingBO("Loan Product", "LOAP");
		product.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOAN_ACTIVE, gracePeriodType,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, funds, fees, (short) 2,
				RecurrenceType.WEEKLY);
		assertEquals(2, product.getLoanOfferingFees().size());
		assertEquals(1, product.getLoanOfferingFunds().size());
	}

	public void testUpdateLoanOffering() throws ProductDefinitionException,
			FeeException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Loan Periodic", FeeCategory.LOAN, "100",
				RecurrenceType.MONTHLY, (short) 1);
		oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time",
				FeeCategory.LOAN, "100", FeePayment.UPFRONT);
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(periodicFee);
		fees.add(oneTimeFee);
		product = createLoanOfferingBO("Loan Product", "LOAP");
		product.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOAN_ACTIVE, null,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, null, fees, (short) 2,
				RecurrenceType.MONTHLY);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		product = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, product.getPrdOfferingId());

		assertEquals("Loan Product", product.getPrdOfferingName());
		assertEquals("LOAN", product.getPrdOfferingShortName());
		assertEquals(Short.valueOf("1"), product.getPrdCategory()
				.getProductCategoryID());
		assertEquals(ApplicableTo.CLIENTS, 
				product.getPrdApplicableMasterEnum());
		assertEquals(startDate, product.getStartDate());
		assertEquals(endDate, product.getEndDate());
		assertEquals("Loan Product updated", product.getDescription());
		assertEquals(GraceType.NONE, product.getGraceType());
		assertEquals(Short.valueOf("0"), product.getGracePeriodDuration());
		assertEquals(InterestType.FLAT, product.getInterestType());
		assertEquals(new Money("1000"), product.getMinLoanAmount());
		assertEquals(new Money("3000"), product.getMaxLoanAmount());
		assertEquals(new Money("1000"), product.getDefaultLoanAmount());
		assertEquals(2.0, product.getMinInterestRate());
		assertEquals(12.0, product.getMaxInterestRate());
		assertEquals(12.0, product.getDefInterestRate());
		assertEquals(Short.valueOf("1"), product.getMinNoInstallments());
		assertEquals(Short.valueOf("12"), product.getMaxNoInstallments());
		assertEquals(Short.valueOf("2"), product.getDefNoInstallments());
		assertFalse(product.isIncludeInLoanCounter());
		assertTrue(product.isIntDedDisbursement());
		assertFalse(product.isPrinDueLastInst());
		assertEquals(2, product.getLoanOfferingFees().size());
		assertNotNull(product.getLoanOfferingMeeting());
		assertEquals(RecurrenceType.MONTHLY, product
				.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
				.getRecurrenceTypeEnum());
		assertNotNull(product.getPrincipalGLcode());
		assertNotNull(product.getInterestGLcode());
	}

        
	public void testLoanOfferingWithDecliningInterestDeductionAtDisbursement() {
	    try {
	        createIntitalObjects();
	        interestTypes = new InterestTypesEntity(
	                InterestType.DECLINING);
	        Date startDate = offSetCurrentDate(0);
	        Date endDate = offSetCurrentDate(2);
	        new LoanOfferingBO(TestObjectFactory
	                .getContext(), "Loan Offering", "LOAP", productCategory,
	                prdApplicableMaster, startDate, endDate, null, null, null,
	                interestTypes, new Money("1000"), new Money("3000"), new Money(
	                "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
	                (short) 17, false, true, false, null, null, frequency,
	                principalglCodeEntity, intglCodeEntity);
	        fail();
	    } catch (ProductDefinitionException e) {
	    	assertEquals("exceptions.declineinterestdisbursementdeduction", 
	    		e.getKey());
	    }
	}
	
	public void testLoanOfferingWithDecliningInterestNoDeductionAtDisbursement() 
	throws Exception {
        createIntitalObjects();
        interestTypes = new InterestTypesEntity(
                InterestType.DECLINING);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
                .getContext(), "Loan Offering", "LOAP", productCategory,
                prdApplicableMaster, startDate, endDate, null, null, null,
                interestTypes, new Money("1000"), new Money("3000"), new Money(
                "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, null, frequency,
                principalglCodeEntity, intglCodeEntity);
		assertEquals(InterestType.DECLINING, loanOffering.getInterestType());
	}
	
       
	public void testPrdOfferingView(){
		PrdOfferingView prdOfferingView=new PrdOfferingView();
		prdOfferingView.setGlobalPrdOfferingNum("1234");
		assertEquals("1234",prdOfferingView.getGlobalPrdOfferingNum());
		prdOfferingView.setPrdOfferingId(Short.valueOf("1"));
		assertEquals(Short.valueOf("1"),prdOfferingView.getPrdOfferingId());
		prdOfferingView.setPrdOfferingName("name");
		assertEquals("name",prdOfferingView.getPrdOfferingName());
	}
    
	private MeetingBO getMeeting() {
		return TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}

	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeeting(WEEKLY, EVERY_WEEK, LOAN_INSTALLMENT, MONDAY));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, true, false,
				frequency);
	}

	private void createIntitalObjects() {
		prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		frequency = getMeeting();
		principalglCodeEntity = (GLCodeEntity) HibernateUtil.getSessionTL()
				.get(GLCodeEntity.class, (short) 7);
		intglCodeEntity = (GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, (short) 7);
		productCategory = TestObjectFactory.getLoanPrdCategory();
		interestTypes = new InterestTypesEntity(
				InterestType.FLAT);
	}

}
