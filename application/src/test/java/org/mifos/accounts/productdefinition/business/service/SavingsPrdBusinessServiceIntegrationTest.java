/*
 * Copyright Grameen Foundation USA
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

package org.mifos.accounts.productdefinition.business.service;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsPrdBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetActiveSavingsProductCategories() throws ServiceException {
       Assert.assertEquals(1, new SavingsPrdBusinessService().getActiveSavingsProductCategories().size());
    }

    @Test
    public void testGetSavingsApplicableRecurrenceTypes() throws Exception {
       Assert.assertEquals(2, new SavingsPrdBusinessService().getSavingsApplicableRecurrenceTypes().size());
    }

    @Test
    public void testGetAllSavingsProducts() throws Exception {
        SavingsOfferingBO savingsOffering = createSavingsOfferingBO();
       Assert.assertEquals(1, new SavingsPrdBusinessService().getAllSavingsProducts().size());
        savingsOffering = null;
    }

    @Test
    public void testGetApplicablePrdStatus() throws ServiceException {
        List<PrdStatusEntity> prdStatusList = new SavingsPrdBusinessService().getApplicablePrdStatus((short) 1);
        StaticHibernateUtil.flushSession();
       Assert.assertEquals(2, prdStatusList.size());
        for (PrdStatusEntity prdStatus : prdStatusList) {
            if (prdStatus.getPrdState().equals("1")) {
                Assert.assertEquals("Active", prdStatus.getPrdState().getName());
            }
            if (prdStatus.getPrdState().equals("2")) {
                Assert.assertEquals("InActive", prdStatus.getPrdState().getName());
            }
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
