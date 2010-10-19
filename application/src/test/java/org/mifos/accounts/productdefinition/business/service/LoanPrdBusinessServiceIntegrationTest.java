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

package org.mifos.accounts.productdefinition.business.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

import java.sql.Date;
import java.util.List;

public class LoanPrdBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    private LoanOfferingBO loanOffering;


    @After
    public void tearDown() throws Exception {
        TestObjectFactory.removeObject(loanOffering);
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetBusinessObject() {
        Assert.assertNull(new LoanPrdBusinessService().getBusinessObject(null));
    }

    @Test @org.junit.Ignore
    public void testGetActiveLoanProductCategoriesForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
           Assert.assertEquals(1, new LoanPrdBusinessService().getActiveLoanProductCategories().size());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetActiveLoanProductCategories() throws ServiceException {
       Assert.assertEquals(1, new LoanPrdBusinessService().getActiveLoanProductCategories().size());
    }

    @Test @org.junit.Ignore
    public void testGetLoanApplicableCustomerTypesForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getLoanApplicableCustomerTypes((short) 1);
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetLoanApplicableCustomerTypes() throws ServiceException {
       Assert.assertEquals(2, new LoanPrdBusinessService().getLoanApplicableCustomerTypes((short) 1).size());
    }

    @Test
    public void testGetLoanOffering() throws ServiceException {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.flushSession();

        loanOffering = new LoanPrdBusinessService().getLoanOffering(loanOffering.getPrdOfferingId());
        Assert.assertNotNull(loanOffering);
       Assert.assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
       Assert.assertEquals("Loan", loanOffering.getPrdOfferingShortName());
    }

    @Test @org.junit.Ignore
    public void testGetLoanOfferingForInvalidConnection() {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.flushSession();

        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getLoanOffering(loanOffering.getPrdOfferingId());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetApplicablePrdStatus() throws ServiceException {
        List<PrdStatusEntity> prdStatusList = new LoanPrdBusinessService().getApplicablePrdStatus((short) 1);
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

    @Test @org.junit.Ignore
    public void testGetApplicablePrdStatusForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getApplicablePrdStatus((short) 1);
            Assert.fail();
        } catch (ServiceException e) {
        }
    }

    @Test
    public void testGetLoanOfferingWithLocaleId() throws ServiceException {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.flushAndClearSession();

        short localeId = 1;
        loanOffering = new LoanPrdBusinessService().getLoanOffering(loanOffering.getPrdOfferingId(), localeId);
        Assert.assertNotNull(loanOffering);
        Assert.assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
        Assert.assertEquals("Loan", loanOffering.getPrdOfferingShortName());

        Assert.assertEquals("Other", loanOffering.getPrdCategory().getProductCategoryName());
        Assert.assertEquals(ApplicableTo.GROUPS, loanOffering.getPrdApplicableMasterEnum());
        Assert.assertEquals("Active", loanOffering.getPrdStatus().getPrdState().getName());
        Assert.assertEquals("Grace on all repayments", loanOffering.getGracePeriodType().getName());
        Assert.assertEquals("Flat", loanOffering.getInterestTypes().getName());
    }

    @Test @org.junit.Ignore
    public void testGetLoanOfferingWithLocaleIdForInvalidConnection() {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.flushSession();

        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getLoanOffering(loanOffering.getPrdOfferingId(), (short) 1);
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetAllLoanOfferings() throws ServiceException {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Offering1", "Loa1");
        StaticHibernateUtil.flushSession();

        List<LoanOfferingBO> loanOfferings = new LoanPrdBusinessService().getAllLoanOfferings((short) 1);
        Assert.assertNotNull(loanOfferings);
       Assert.assertEquals(2, loanOfferings.size());
        for (LoanOfferingBO loanOfferingBO : loanOfferings) {
            Assert.assertNotNull(loanOfferingBO.getPrdOfferingName());
            Assert.assertNotNull(loanOfferingBO.getPrdOfferingId());
            Assert.assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
        }
        StaticHibernateUtil.flushSession();
        TestObjectFactory.removeObject(loanOffering1);
    }

    @Test @org.junit.Ignore
    public void testGetAllLoanOfferingsForInvalidConnection() {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.flushSession();

        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getAllLoanOfferings((short) 1);
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.flushSession();
    }

    private LoanOfferingBO createLoanOfferingBO(String prdOfferingName, String shortName) {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createLoanOffering(prdOfferingName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency);
    }
}
