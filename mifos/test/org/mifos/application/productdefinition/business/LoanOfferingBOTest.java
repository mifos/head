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
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.productdefinition.util.helpers.InterestTypeConstants;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanOfferingBOTest extends MifosTestCase {

	private LoanOfferingBO loanOffering;

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
		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.cleanUp(periodicFee);
		TestObjectFactory.cleanUp(oneTimeFee);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testBuildloanOfferingWithoutDataForMandatoryFields() {
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, false, false, false, null, null, null);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithoutDataForAllFields() {
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, false, false,
					false, null, null, null, null, null);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithoutName() throws InvalidUserException,
			SystemException, ApplicationException {
		createIntitalObjects();
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), null, "S", productCategory,
					prdApplicableMaster, new Date(System.currentTimeMillis()),
					interestTypes, new Money("1000"), new Money("3000"), 12.0,
					2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
					false, frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithShortNameGreaterThanFourDig()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOANOFF", productCategory,
					prdApplicableMaster, new Date(System.currentTimeMillis()),
					interestTypes, new Money("1000"), new Money("3000"), 12.0,
					2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
					false, frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithStartDateLessThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(-2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAN", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithStartDateEqualToCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAN", productCategory,
				prdApplicableMaster, startDate, interestTypes,
				new Money("1000"), new Money("3000"), 12.0, 2.0, 3.0,
				(short) 20, (short) 1, (short) 12, false, true, false,
				frequency, principalglCodeEntity, intglCodeEntity);
		assertNotNull(loanOffering.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.LOANACTIVE.getValue(), loanOffering
				.getPrdStatus().getOfferingStatusId());

	}

	public void testBuildloanOfferingWithStartDateGreaterThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(2);
		LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAN", productCategory,
				prdApplicableMaster, startDate, interestTypes,
				new Money("1000"), new Money("3000"), 12.0, 2.0, 3.0,
				(short) 20, (short) 1, (short) 12, false, true, false,
				frequency, principalglCodeEntity, intglCodeEntity);
		assertNotNull(loanOffering.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.LOANINACTIVE.getValue(), loanOffering
				.getPrdStatus().getOfferingStatusId());

	}

	public void testBuildloanOfferingWithDuplicatePrdOfferingName()
			throws InvalidUserException, SystemException, ApplicationException {
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Product", "LOAN", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithDuplicatePrdOfferingShortName()
			throws InvalidUserException, SystemException, ApplicationException {
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithEndDateLessThanStartDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(-2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"), null,
					12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
					false, false, null, null, frequency, principalglCodeEntity,
					intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithNoInterestTypes()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Product", "LOAN", productCategory,
					prdApplicableMaster, startDate, null, new Money("1000"),
					new Money("3000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, frequency,
					principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithNoMaxAmount()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000.0"), null, 12.0, 2.0, 3.0, (short) 20,
					(short) 1, (short) 12, false, false, false, frequency,
					principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildloanOfferingWithoutGLCode()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, interestTypes, new Money(
							"1000.0"), new Money("3000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					frequency, null, null);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testDefAmountNotBetweenMinMaxAmounts() {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("200.0"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, null, null, frequency,
					principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testMinAmountGreaterThanMaxAmount() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("10000"), new Money("3000"), null,
					12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
					false, false, null, null, frequency, principalglCodeEntity,
					intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testDefInterestRateNotBetweenMinMaxRates() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 2.0, 13.0, (short) 20,
					(short) 1, (short) 12, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testMinInterestRateGretaterThanMaxRate() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 20.0, 13.0, (short) 20,
					(short) 1, (short) 12, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testMinInstallmentsGreaterThanMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20,
					(short) 31, (short) 21, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testDefInstallmentsNotBetweenMinMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null, null, null,
					interestTypes, new Money("1000"), new Money("3000"),
					new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20,
					(short) 11, (short) 7, false, false, false, null, null,
					frequency, principalglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
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
		assertEquals(GraceTypeConstants.NONE.getValue(), loanOffering
				.getGracePeriodType().getId());
		assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
	}

	public void testNullGracePeriodDurationWithGraceType()
			throws ProductDefinitionException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.PRINCIPALONLYGRACE);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null,
					gracePeriodType, null, interestTypes, new Money("1000"),
					new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0,
					(short) 20, (short) 11, (short) 17, false, false, false,
					null, null, frequency, principalglCodeEntity,
					intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testNullGracePeriodDurationForNoneGraceType()
			throws ProductDefinitionException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.NONE);
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
		assertEquals(GraceTypeConstants.NONE.getValue(), loanOffering
				.getGracePeriodType().getId());
		assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
	}

	public void testFeeNotMatchingFrequencyOfLoanOffering()
			throws ProductDefinitionException, FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.NONE);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory.getMeetingHelper(2, 1,
								4, 2)));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		try {
			LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
					.getContext(), "Loan Offering", "LOAP", productCategory,
					prdApplicableMaster, startDate, endDate, null,
					gracePeriodType, null, interestTypes, new Money("1000"),
					new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0,
					(short) 20, (short) 11, (short) 17, false, false, false,
					null, fees, frequency, principalglCodeEntity,
					intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testWithFundsAndOneTimeFee() throws ProductDefinitionException,
			FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.GRACEONALLREPAYMENTS);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory.getMeetingHelper(1, 1,
								4, 2)));
		FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				intglCodeEntity, new Money("100"), false, new FeePaymentEntity(
						FeePayment.UPFRONT));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		fees.add(fee1);
		List<Fund> funds = new ArrayList<Fund>();
		funds.add(new Fund());
		LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAP", productCategory,
				prdApplicableMaster, startDate, endDate, null, gracePeriodType,
				(short) 2, interestTypes, new Money("1000"), new Money("3000"),
				new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
				(short) 17, false, false, false, funds, fees, frequency,
				principalglCodeEntity, intglCodeEntity);
		assertEquals(2, loanOffering.getPrdOfferingFees().size());
		assertEquals(1, loanOffering.getLoanOfferingFunds().size());
	}

	public void testCreateLoanOffering() throws ProductDefinitionException,
			FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.GRACEONALLREPAYMENTS);
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

		loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(),
				"Loan Offering", "LOAP", productCategory, prdApplicableMaster,
				startDate, endDate, "1234", gracePeriodType, (short) 2,
				interestTypes, new Money("1000"), new Money("3000"), new Money(
						"2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
				(short) 17, false, false, false, null, fees, frequency,
				principalglCodeEntity, intglCodeEntity);
		loanOffering.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());

		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("LOAP", loanOffering.getPrdOfferingShortName());
		assertEquals(Short.valueOf("1"), loanOffering.getPrdCategory()
				.getProductCategoryID());
		assertEquals(PrdApplicableMaster.CLIENTS.getValue(), loanOffering
				.getPrdApplicableMaster().getId());
		assertEquals(startDate, loanOffering.getStartDate());
		assertEquals(endDate, loanOffering.getEndDate());
		assertEquals("1234", loanOffering.getDescription());
		assertEquals(GraceTypeConstants.GRACEONALLREPAYMENTS.getValue(),
				loanOffering.getGracePeriodType().getId());
		assertEquals(Short.valueOf("2"), loanOffering.getGracePeriodDuration());
		assertEquals(InterestTypeConstants.FLATINTERST.getValue(), loanOffering
				.getInterestTypes().getId());
		assertEquals(new Money("1000"), loanOffering.getMinLoanAmount());
		assertEquals(new Money("3000"), loanOffering.getMaxLoanAmount());
		assertEquals(new Money("2000"), loanOffering.getDefaultLoanAmount());
		assertEquals(2.0, loanOffering.getMinInterestRate());
		assertEquals(12.0, loanOffering.getMaxInterestRate());
		assertEquals(3.0, loanOffering.getDefInterestRate());
		assertEquals(Short.valueOf("11"), loanOffering.getMinNoInstallments());
		assertEquals(Short.valueOf("20"), loanOffering.getMaxNoInstallments());
		assertEquals(Short.valueOf("17"), loanOffering.getDefNoInstallments());
		assertFalse(loanOffering.isIncludeInLoanCounter());
		assertFalse(loanOffering.isIntDedDisbursement());
		assertFalse(loanOffering.isPrinDueLastInst());
		assertEquals(2, loanOffering.getPrdOfferingFees().size());
		assertNotNull(loanOffering.getPrdOfferingMeeting());
		assertEquals(RecurrenceType.WEEKLY.getValue(), loanOffering
				.getPrdOfferingMeeting().getMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId());
		assertNotNull(loanOffering.getPrincipalGLcode());
		assertNotNull(loanOffering.getInterestGLcode());
	}

	public void testUpdateloanOfferingWithoutDataForMandatoryFields() {
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update(null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null, false, false, false, null, null, null,
					null);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateloanOfferingWithoutName()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, null, "S", productCategory,
					prdApplicableMaster, new Date(System.currentTimeMillis()),
					null, "Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateloanOfferingWithShortNameGreaterThanFourDig()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOANS",
					productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testupdateloanOfferingWithStartDateLessThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		Date startDate = offSetCurrentDate(-2);
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateloanOfferingWithStartDateEqualToCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		loanOffering.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, null, "Loan Product updated",
				PrdStatus.LOANACTIVE, null, interestTypes, (short) 0,
				new Money("3000"), new Money("1000"), new Money("2000"), 12.0,
				2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
				false, null, null, (short) 2, RecurrenceType.WEEKLY);
		HibernateUtil.commitTransaction();
		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());
		assertEquals(PrdStatus.LOANACTIVE.getValue(), loanOffering
				.getPrdStatus().getOfferingStatusId());

	}

	public void testUpdateloanOfferingWithStartDateGreaterThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}

	}

	public void testUpdateloanOfferingWithDuplicatePrdOfferingName()
			throws InvalidUserException, SystemException, ApplicationException {
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Product1",
				"LOA1");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			loanOffering1.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testUpdateloanOfferingWithDuplicatePrdOfferingShortName()
			throws InvalidUserException, SystemException, ApplicationException {
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Product1",
				"LOA1");
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		try {
			loanOffering1.update((short) 1, "Loan Product1", "LOAP",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testUpdateloanOfferingWithEndDateLessThanStartDate()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(-2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateloanOfferingWithNoInterestTypes()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null, null,
					(short) 0, new Money("3000"), new Money("1000"), new Money(
							"2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, null, null, (short) 2,
					RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testupdateloanOfferingWithNoMaxAmount()
			throws InvalidUserException, SystemException, ApplicationException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, null, new Money("1000"),
					new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
					(short) 12, false, false, false, null, null, (short) 2,
					RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateDefAmountNotBetweenMinMaxAmounts() {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("1000"), new Money(
							"3000"), new Money("2000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateMinAmountGreaterThanMaxAmount() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("1000"), new Money(
							"3000"), new Money("1000"), 12.0, 2.0, 3.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateDefInterestRateNotBetweenMinMaxRates() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, null,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 2.0, 13.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateMinInterestRateGretaterThanMaxRate() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 22.0, 12.0,
					(short) 20, (short) 1, (short) 12, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateMinInstallmentsGreaterThanMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 2.0, 12.0,
					(short) 2, (short) 12, (short) 2, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateDefInstallmentsNotBetweenMinMaxInstallments() {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOANACTIVE, null,
					interestTypes, (short) 0, new Money("3000"), new Money(
							"1000"), new Money("1000"), 12.0, 2.0, 12.0,
					(short) 12, (short) 1, (short) 22, false, false, false,
					null, null, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateGracePeriodForIntDedAtDisb()
			throws ProductDefinitionException {
		createIntitalObjects();
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		loanOffering.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOANACTIVE, null,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, null, null, (short) 2,
				RecurrenceType.WEEKLY);
		HibernateUtil.commitTransaction();
		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());

		assertNotNull(loanOffering.getGracePeriodType());
		assertNotNull(loanOffering.getGracePeriodDuration());
		assertEquals(GraceTypeConstants.NONE.getValue(), loanOffering
				.getGracePeriodType().getId());
		assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
	}

	public void testUpdateFeeNotMatchingFrequencyOfLoanOffering()
			throws ProductDefinitionException, FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.NONE);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory.getMeetingHelper(2, 1,
								4, 2)));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		try {
			loanOffering.update((short) 1, "Loan Product", "LOAN",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Loan Product updated", PrdStatus.LOANACTIVE,
					gracePeriodType, interestTypes, (short) 0,
					new Money("3000"), new Money("1000"), new Money("1000"),
					12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 2, false,
					true, false, null, fees, (short) 2, RecurrenceType.WEEKLY);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testUpdateWithFundsAndOneTimeFee()
			throws ProductDefinitionException, FeeException {
		createIntitalObjects();
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.GRACEONALLREPAYMENTS);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(2);
		FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC),
				intglCodeEntity, new Money("100"), false, TestObjectFactory
						.createMeeting(TestObjectFactory.getMeetingHelper(1, 1,
								4, 2)));
		FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(),
				"Loan Periodic", new CategoryTypeEntity(FeeCategory.LOAN),
				new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				intglCodeEntity, new Money("100"), false, new FeePaymentEntity(
						FeePayment.UPFRONT));
		List<FeeBO> fees = new ArrayList<FeeBO>();
		fees.add(fee);
		fees.add(fee1);
		List<Fund> funds = new ArrayList<Fund>();
		funds.add(new Fund());
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		loanOffering.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOANACTIVE, gracePeriodType,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, funds, fees, (short) 2,
				RecurrenceType.WEEKLY);
		assertEquals(2, loanOffering.getPrdOfferingFees().size());
		assertEquals(1, loanOffering.getLoanOfferingFunds().size());
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
		loanOffering = createLoanOfferingBO("Loan Product", "LOAP");
		loanOffering.update((short) 1, "Loan Product", "LOAN", productCategory,
				prdApplicableMaster, startDate, endDate,
				"Loan Product updated", PrdStatus.LOANACTIVE, null,
				interestTypes, (short) 0, new Money("3000"), new Money("1000"),
				new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
				(short) 2, false, true, false, null, fees, (short) 2,
				RecurrenceType.MONTHLY);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());

		assertEquals("Loan Product", loanOffering.getPrdOfferingName());
		assertEquals("LOAN", loanOffering.getPrdOfferingShortName());
		assertEquals(Short.valueOf("1"), loanOffering.getPrdCategory()
				.getProductCategoryID());
		assertEquals(PrdApplicableMaster.CLIENTS.getValue(), loanOffering
				.getPrdApplicableMaster().getId());
		assertEquals(startDate, loanOffering.getStartDate());
		assertEquals(endDate, loanOffering.getEndDate());
		assertEquals("Loan Product updated", loanOffering.getDescription());
		assertEquals(GraceTypeConstants.NONE.getValue(), loanOffering
				.getGracePeriodType().getId());
		assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
		assertEquals(InterestTypeConstants.FLATINTERST.getValue(), loanOffering
				.getInterestTypes().getId());
		assertEquals(new Money("1000"), loanOffering.getMinLoanAmount());
		assertEquals(new Money("3000"), loanOffering.getMaxLoanAmount());
		assertEquals(new Money("1000"), loanOffering.getDefaultLoanAmount());
		assertEquals(2.0, loanOffering.getMinInterestRate());
		assertEquals(12.0, loanOffering.getMaxInterestRate());
		assertEquals(12.0, loanOffering.getDefInterestRate());
		assertEquals(Short.valueOf("1"), loanOffering.getMinNoInstallments());
		assertEquals(Short.valueOf("12"), loanOffering.getMaxNoInstallments());
		assertEquals(Short.valueOf("2"), loanOffering.getDefNoInstallments());
		assertFalse(loanOffering.isIncludeInLoanCounter());
		assertTrue(loanOffering.isIntDedDisbursement());
		assertFalse(loanOffering.isPrinDueLastInst());
		assertEquals(2, loanOffering.getPrdOfferingFees().size());
		assertNotNull(loanOffering.getPrdOfferingMeeting());
		assertEquals(RecurrenceType.MONTHLY.getValue(), loanOffering
				.getPrdOfferingMeeting().getMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId());
		assertNotNull(loanOffering.getPrincipalGLcode());
		assertNotNull(loanOffering.getInterestGLcode());
	}

	private MeetingBO getMeeting() {
		return TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
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
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 1, 2));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("0"), Short.valueOf("1"), frequency);
	}

	private void createIntitalObjects() {
		prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		frequency = getMeeting();
		principalglCodeEntity = (GLCodeEntity) HibernateUtil.getSessionTL()
				.get(GLCodeEntity.class, (short) 7);
		intglCodeEntity = (GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, (short) 7);
		productCategory = (ProductCategoryBO) TestObjectFactory
				.getLoanPrdCategory();
		interestTypes = new InterestTypesEntity(
				InterestTypeConstants.FLATINTERST);
	}

}
