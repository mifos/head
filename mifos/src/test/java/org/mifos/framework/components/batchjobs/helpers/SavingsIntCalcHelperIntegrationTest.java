/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD;
import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_POSTING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;

import java.util.Calendar;
import java.util.Date;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountPaymentEntityIntegrationTest;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.config.AccountingRules;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import java.math.RoundingMode;

public class SavingsIntCalcHelperIntegrationTest extends MifosIntegrationTest {
	public SavingsIntCalcHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.000001;

    private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings1;

	private SavingsBO savings2;

	private SavingsBO savings3;

	private SavingsBO savings4;

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private SavingsOfferingBO savingsOffering3;

	private SavingsOfferingBO savingsOffering4;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private MifosCurrency currency = Configuration.getInstance()
			.getSystemConfig().getCurrency();

	SavingsPersistence persistence = new SavingsPersistence();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestUtils.makeUser();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings1);
		TestObjectFactory.cleanUp(savings2);
		TestObjectFactory.cleanUp(savings3);
		TestObjectFactory.cleanUp(savings4);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testIntCalculation() throws Exception {
		Short savedDigits = AccountingRules.getDigitsAfterDecimal();
		AccountingRules.setDigitsAfterDecimal(new Short("3"));
		createInitialObjects();
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		SavingsBOIntegrationTest.setNextIntCalcDate(savings1,helper.getDate("01/05/2006"));
		SavingsBOIntegrationTest.setActivationDate(savings1,helper.getDate("05/03/2006"));

		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				savings1, new Money(currency, "1000.0"), new Money(currency,
						"1000.0"), helper.getDate("10/03/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings1, createdBy,
				group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings1);
		
		savings1.update();
		StaticHibernateUtil.commitTransaction();

		payment = helper.createAccountPaymentToPersist(savings1, new Money(
				currency, "500.0"), new Money(currency, "1500.0"), helper
				.getDate("20/03/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings1, createdBy,
				group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings1);
		savings1.update();
		StaticHibernateUtil.commitTransaction();

		payment = helper.createAccountPaymentToPersist(savings1, new Money(
				currency, "600.0"), new Money(currency, "900.0"), helper
				.getDate("10/04/2006"),
				AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings1,
				createdBy, group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings1);
		savings1.update();
		StaticHibernateUtil.commitTransaction();

		payment = helper.createAccountPaymentToPersist(savings1, new Money(
				currency, "800.0"), new Money(currency, "1700.0"), helper
				.getDate("20/04/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings1, createdBy,
				group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings1);
		savings1.update();
		StaticHibernateUtil.commitTransaction();
		
		SavingsBOIntegrationTest.setNextIntCalcDate(savings4,helper.getDate("01/05/2006"));
		SavingsBOIntegrationTest.setActivationDate(savings4,helper.getDate("10/04/2006"));

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "1000.0"), new Money(currency, "1000.0"), helper
				.getDate("20/04/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings4, createdBy,
				group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings4);
		savings4.update();
		StaticHibernateUtil.commitTransaction();

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "500.0"), new Money(currency, "1500.0"), helper
				.getDate("25/04/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings4, createdBy,
				group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings4);
		savings4.update();
		StaticHibernateUtil.commitTransaction();

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "600.0"), new Money(currency, "900.0"), helper
				.getDate("28/04/2006"),
				AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings4,
				createdBy, group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings4);
		savings4.update();
		StaticHibernateUtil.commitTransaction();

		payment = helper.createAccountPaymentToPersist(savings4, new Money(
				currency, "800.0"), new Money(currency, "1700.0"), helper
				.getDate("30/04/2006"),
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings4, createdBy,
				group);
		AccountPaymentEntityIntegrationTest.addAccountPayment(payment,savings4);
		savings4.update();
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();

		Calendar cal = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		// the date has to be 02/05 for the task to pick up the savings accounts
		// because the nextIntCalcDate is set to 01/05/2006
		cal.setTime(helper.getDate("02/05/2006"));
		SavingsIntCalcTask savingsIntCalcTask = new SavingsIntCalcTask();
		((SavingsIntCalcHelper) savingsIntCalcTask.getTaskHelper()).execute(cal
				.getTimeInMillis());

		savings1 = persistence.findById(savings1.getAccountId());
		savings4 = persistence.findById(savings4.getAccountId());

		assertEquals(helper.getDate("31/05/2006"), savings1
				.getNextIntCalcDate());
		
		// using the old money class the result here was 15.4
        assertEquals(15.387, savings1.getInterestToBePosted().getAmountDoubleValue(), DELTA);

		// using the old money class the result here was 4.3
        assertEquals(4.274, savings4.getInterestToBePosted().getAmountDoubleValue(), DELTA);
		assertEquals(helper.getDate("31/05/2006"), savings4
				.getNextIntCalcDate());
		AccountingRules.setDigitsAfterDecimal(savedDigits);
		
	}

	private void createInitialObjects() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

		savingsOffering1 = createSavingsOffering("prd1", "34vf", 
				InterestCalcType.MINIMUM_BALANCE);
		savingsOffering2 = createSavingsOffering("prd2", "4frg", 
				InterestCalcType.MINIMUM_BALANCE);
		savingsOffering3 = createSavingsOffering("prd3", "c23e", 
				InterestCalcType.MINIMUM_BALANCE);
		savingsOffering4 = createSavingsOffering("prd4", "cwer", 
				InterestCalcType.AVERAGE_BALANCE);
		savings1 = helper.createSavingsAccount(savingsOffering1, group,
				AccountState.SAVINGS_ACTIVE, userContext);
		savings2 = helper.createSavingsAccount(savingsOffering2, group,
				AccountState.SAVINGS_PARTIAL_APPLICATION, userContext);
		savings3 = helper.createSavingsAccount(savingsOffering3, group,
				AccountState.SAVINGS_PENDING_APPROVAL, userContext);
		savings4 = helper.createSavingsAccount(savingsOffering4, group,
				AccountState.SAVINGS_ACTIVE, userContext);
	}

	private SavingsOfferingBO createSavingsOffering(String offeringName,
			String shortName, InterestCalcType interestCalcType) throws Exception {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getNewMeeting(
						MONTHLY, EVERY_MONTH, 
						SAVINGS_INTEREST_CALCULATION_TIME_PERIOD, MONDAY));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getNewMeeting(
						MONTHLY, EVERY_MONTH,
						SAVINGS_INTEREST_POSTING, MONDAY));
		return TestObjectFactory.createSavingsProduct(
				offeringName, shortName, ApplicableTo.GROUPS, 
				new Date(System.currentTimeMillis()), 
				PrdStatus.SAVINGS_ACTIVE, 300.0, 
				RecommendedAmountUnit.PER_INDIVIDUAL, 12.0, 
				200.0, 200.0, SavingsType.VOLUNTARY, interestCalcType, 
				meetingIntCalc, meetingIntPost);
	}

}
