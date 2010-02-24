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

package org.mifos.accounts.productdefinition.persistence;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsPrdPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public SavingsPrdPersistenceIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO group;

    private CustomerBO center;

    private SavingsBO savings;

    private SavingsOfferingBO savingsOffering;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(savings);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testRetrieveSavingsAccountsForPrd() throws Exception {
        SavingsTestHelper helper = new SavingsTestHelper();
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering("fsaf6", "ads6");
        UserContext userContext = new UserContext();
        userContext.setId(PersonnelConstants.SYSTEM_USER);
        savings = helper.createSavingsAccount("000100000000017", savingsOffering, group,
                AccountStates.SAVINGS_ACC_APPROVED, userContext);
        StaticHibernateUtil.closeSession();
        List<SavingsBO> savingsList = new SavingsPrdPersistence().retrieveSavingsAccountsForPrd(savingsOffering
                .getPrdOfferingId());
       Assert.assertEquals(Integer.valueOf("1").intValue(), savingsList.size());
        savings = new SavingsPersistence().findById(savings.getAccountId());
    }

    public void testGetTimePerForIntCalcAndFreqPost() throws PersistenceException, ProductDefinitionException {
        savingsOffering = createSavingsOfferingBO();
        savingsOffering = new SavingsPrdPersistence().getSavingsProduct(savingsOffering.getPrdOfferingId());
        Assert.assertNotNull("The time period for Int calc should not be null", savingsOffering.getTimePerForInstcalc());
        Assert.assertNotNull("The freq for Int post should not be null", savingsOffering.getFreqOfPostIntcalc());
        TestObjectFactory.removeObject(savingsOffering);
    }

    public void testDormancyDays() throws Exception {
       Assert.assertEquals(Short.valueOf("30"), new SavingsPrdPersistence().retrieveDormancyDays());
    }

    public void testGetSavingsOfferingsNotMixed() throws Exception {
        savingsOffering = createSavingsOfferingBO();
       Assert.assertEquals(1, new SavingsPrdPersistence().getSavingsOfferingsNotMixed(Short.valueOf("1")).size());
        TestObjectFactory.removeObject(savingsOffering);
    }

    public void testGetAllActiveSavingsProducts() throws Exception {
        savingsOffering = createSavingsOfferingBO();
       Assert.assertEquals(1, new SavingsPrdPersistence().getAllActiveSavingsProducts().size());
        TestObjectFactory.removeObject(savingsOffering);
    }

    public void testGetSavingsApplicableRecurrenceTypes() throws Exception {
       Assert.assertEquals(2, new SavingsPrdPersistence().getSavingsApplicableRecurrenceTypes().size());
    }

    public void testGetAllSavingsProducts() throws Exception {
        savingsOffering = createSavingsOfferingBO();
       Assert.assertEquals(1, new SavingsPrdPersistence().getAllSavingsProducts().size());
        TestObjectFactory.removeObject(savingsOffering);
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    private SavingsOfferingBO createSavingsOfferingBO() {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createSavingsProduct("Savings Product", "SAVP", ApplicableTo.CLIENTS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2,
                200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
    }

}
