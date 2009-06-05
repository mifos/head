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

package org.mifos.application.productdefinition.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsPrdBusinessServiceIntegrationTest extends MifosIntegrationTest {

    public SavingsPrdBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetActiveSavingsProductCategories() throws ServiceException {
        assertEquals(1, new SavingsPrdBusinessService().getActiveSavingsProductCategories().size());
    }

    public void testGetSavingsApplicableRecurrenceTypes() throws Exception {
        assertEquals(2, new SavingsPrdBusinessService().getSavingsApplicableRecurrenceTypes().size());
    }

    public void testGetAllSavingsProducts() throws Exception {
        SavingsOfferingBO savingsOffering = createSavingsOfferingBO();
        assertEquals(1, new SavingsPrdBusinessService().getAllSavingsProducts().size());
        TestObjectFactory.removeObject(savingsOffering);
    }

    public void testGetApplicablePrdStatus() throws ServiceException {
        List<PrdStatusEntity> prdStatusList = new SavingsPrdBusinessService().getApplicablePrdStatus((short) 1);
        StaticHibernateUtil.closeSession();
        assertEquals(2, prdStatusList.size());
        for (PrdStatusEntity prdStatus : prdStatusList) {
            if (prdStatus.getPrdState().equals("1"))
                assertEquals("Active", prdStatus.getPrdState().getName());
            if (prdStatus.getPrdState().equals("2"))
                assertEquals("InActive", prdStatus.getPrdState().getName());
        }
    }

    public void testGetApplicablePrdStatusForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new SavingsPrdBusinessService().getApplicablePrdStatus((short) 1);
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
    }

    public void testGetAllSavingsProductsFailure() throws Exception {
        SavingsOfferingBO savingsOffering = createSavingsOfferingBO();
        TestObjectFactory.simulateInvalidConnection();
        try {
            new SavingsPrdBusinessService().getAllSavingsProducts();
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
            StaticHibernateUtil.closeSession();
            TestObjectFactory.removeObject(savingsOffering);
        }
    }

    public void testGetSavingsApplicableRecurrenceTypesFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new SavingsPrdBusinessService().getSavingsApplicableRecurrenceTypes();
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
    }

    public void testGetActiveSavingsProductCategoriesFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new SavingsPrdBusinessService().getActiveSavingsProductCategories();
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
    }

    public void testRetrieveDormancyDays() throws Exception {
        Short dormancyDays = new SavingsPrdBusinessService().retrieveDormancyDays();
        assertEquals(dormancyDays, Short.valueOf("30"));
    }

    public void testRetrieveDormancyDaysForInvalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new SavingsPrdBusinessService().getActiveSavingsProductCategories();
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
    }

    private SavingsOfferingBO createSavingsOfferingBO() {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createSavingsProduct("Savings Product", "SAVP", ApplicableTo.CLIENTS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2,
                200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
    }

}
