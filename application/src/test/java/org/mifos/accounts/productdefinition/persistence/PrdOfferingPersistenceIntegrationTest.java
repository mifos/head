/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.productsmix.business.ProductMixBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PrdOfferingPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public PrdOfferingPersistenceIntegrationTest() throws Exception {
        super();
    }

    private LoanOfferingBO loanOffering;
    private LoanOfferingBO loanOffering2;

    private PrdOfferingPersistence persistence;

    MeetingBO meetingIntCalc;
    MeetingBO meetingIntCalc2;
    ProductMixBO prdmix;
    ProductMixBO prdmix2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        persistence = new PrdOfferingPersistence();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(prdmix);
        TestObjectFactory.removeObject(prdmix2);
        TestObjectFactory.removeObject(loanOffering);
        TestObjectFactory.removeObject(loanOffering2);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testretrieveLatenessForPrd() throws Exception {
        Short latenessDays = null;
        latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
        Assert.assertNotNull(latenessDays);
       Assert.assertEquals(Short.valueOf("10"), latenessDays);
    }

    public void testGetAllPrdOffringByType() throws Exception {
        Assert.assertNotNull(new PrdOfferingPersistence().getAllPrdOffringByType(ProductType.LOAN.getValue().toString()));
    }

    public void testGetMaxPrdOfferingWithouProduct() throws PersistenceException {
        Assert.assertNull(new PrdOfferingPersistence().getMaxPrdOffering());
    }

    public void testGetAllowedPrdOfferingsByType() throws PersistenceException {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering("Eddikhar", "Edkh");
        Assert.assertNotNull(new PrdOfferingPersistence().getAllowedPrdOfferingsByType(savingsOffering.getPrdOfferingId()
                .toString(), ProductType.SAVINGS.getValue().toString()));
        TestObjectFactory.removeObject(savingsOffering);
    }

    public void testGetAllowedPrdOfferingsForMixProduct() throws PersistenceException {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering("Eddikhar", "Edkh");
        Assert.assertNotNull(new PrdOfferingPersistence().getAllowedPrdOfferingsForMixProduct(savingsOffering
                .getPrdOfferingId().toString(), ProductType.SAVINGS.getValue().toString()));
        TestObjectFactory.removeObject(savingsOffering);
    }

    public void testGetMaxPrdOfferingWithProduct() throws PersistenceException {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering("fsaf6", "ads6");
        Assert.assertNotNull(new PrdOfferingPersistence().getMaxPrdOffering());
        TestObjectFactory.removeObject(savingsOffering);
    }

    public void testGetPrdStatus() throws PersistenceException {
        PrdStatusEntity prdStatus = new PrdOfferingPersistence().getPrdStatus(PrdStatus.SAVINGS_ACTIVE);
        Assert.assertNotNull(prdStatus);
       Assert.assertEquals(ProductType.SAVINGS.getValue(), prdStatus.getPrdType().getProductTypeID());
       Assert.assertEquals(Short.valueOf("1"), prdStatus.getPrdState().getId());
    }

    public void testGetPrdOfferingNameCountWithoutData() throws PersistenceException {
       Assert.assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence().getProductOfferingNameCount("Savings product"));
    }

    public void testGetPrdOfferingNameCountWithDifferentName() throws PersistenceException {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering("fsaf6", "ads6");
       Assert.assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence().getProductOfferingNameCount("Savings product"));
        TestObjectFactory.removeObject(savingsOffering);

    }

    public void testGetPrdOfferingNameCountWithSameName() throws PersistenceException {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering("Savings product", "ads6");
       Assert.assertEquals(Integer.valueOf("1"), new PrdOfferingPersistence().getProductOfferingNameCount("Savings product"));
        TestObjectFactory.removeObject(savingsOffering);

    }

    public void testGetPrdOfferingShortNameCountWithoutData() throws PersistenceException {
       Assert.assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence().getProductOfferingShortNameCount("SAVP"));
    }

    public void testGetPrdOfferingShortNameCountWithDifferentName() throws PersistenceException {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering("fsaf6", "ads6");
       Assert.assertEquals(Integer.valueOf("0"), new PrdOfferingPersistence().getProductOfferingShortNameCount("SAVP"));
        TestObjectFactory.removeObject(savingsOffering);

    }

    public void testGetPrdOfferingShortNameCountWithSameName() throws PersistenceException {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering("Savings product", "SAVP");
       Assert.assertEquals(Integer.valueOf("1"), new PrdOfferingPersistence().getProductOfferingShortNameCount("SAVP"));
        TestObjectFactory.removeObject(savingsOffering);

    }

    public void testGetApplicableProductCategories() throws PersistenceException {
       Assert.assertEquals(1, new PrdOfferingPersistence().getApplicableProductCategories(ProductType.SAVINGS,
                PrdCategoryStatus.ACTIVE).size());
    }

    public void testGetApplicablePrdStatus() throws PersistenceException {
        List<PrdStatusEntity> prdStatusList = new PrdOfferingPersistence().getApplicablePrdStatus(ProductType.LOAN,
                (short) 1);
        StaticHibernateUtil.closeSession();
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

    public void testGetPrdOfferingMix() throws ServiceException, PersistenceException {
        createLoanProductMixed();
        createsecondLoanProductMixed();
        prdmix = createNotAllowedProductForAProductOffering(loanOffering, loanOffering);
       Assert.assertEquals(2, persistence.getPrdOfferingMix().size());
       Assert.assertTrue("Products Mix should be in alphabitical order:",
                (persistence.getPrdOfferingMix().get(0).getPrdOfferingName().compareToIgnoreCase(persistence
                        .getPrdOfferingMix().get(1).getPrdOfferingName())) < 0);
        StaticHibernateUtil.closeSession();

    }

    private void createLoanProductMixed() throws PersistenceException {

        Date startDate = new Date(System.currentTimeMillis());

        meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));

        loanOffering = TestObjectFactory.createLoanOffering("BLoan", "L", startDate, meetingIntCalc);
        loanOffering.updatePrdOfferingFlag();

    }

    private void createsecondLoanProductMixed() throws PersistenceException {

        Date startDate = new Date(System.currentTimeMillis());

        meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));

        loanOffering2 = TestObjectFactory.createLoanOffering("ALoan", "aL", startDate, meetingIntCalc);
        loanOffering2.updatePrdOfferingFlag();

    }

    private ProductMixBO createNotAllowedProductForAProductOffering(PrdOfferingBO prdOffering,
            PrdOfferingBO prdOfferingNotAllowedId) {
        return TestObjectFactory.createNotAllowedProductForAProductOffering(prdOffering, prdOfferingNotAllowedId);

    }
}
