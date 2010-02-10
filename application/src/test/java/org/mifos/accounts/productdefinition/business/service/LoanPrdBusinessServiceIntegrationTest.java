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

package org.mifos.accounts.productdefinition.business.service;

import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public LoanPrdBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    private LoanOfferingBO loanOffering;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(loanOffering);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetBusinessObject() {
        Assert.assertNull(new LoanPrdBusinessService().getBusinessObject(null));
    }

    public void testGetActiveLoanProductCategoriesForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
           Assert.assertEquals(1, new LoanPrdBusinessService().getActiveLoanProductCategories().size());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    public void testGetActiveLoanProductCategories() throws ServiceException {
       Assert.assertEquals(1, new LoanPrdBusinessService().getActiveLoanProductCategories().size());
    }

    public void testGetLoanApplicableCustomerTypesForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getLoanApplicableCustomerTypes((short) 1);
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    public void testGetLoanApplicableCustomerTypes() throws ServiceException {
       Assert.assertEquals(2, new LoanPrdBusinessService().getLoanApplicableCustomerTypes((short) 1).size());
    }

    public void testGetLoanOffering() throws ServiceException {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.closeSession();

        loanOffering = new LoanPrdBusinessService().getLoanOffering(loanOffering.getPrdOfferingId());
        Assert.assertNotNull(loanOffering);
       Assert.assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
       Assert.assertEquals("Loan", loanOffering.getPrdOfferingShortName());
    }

    public void testGetLoanOfferingForInvalidConnection() {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.closeSession();

        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getLoanOffering(loanOffering.getPrdOfferingId());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetApplicablePrdStatus() throws ServiceException {
        List<PrdStatusEntity> prdStatusList = new LoanPrdBusinessService().getApplicablePrdStatus((short) 1);
        StaticHibernateUtil.closeSession();
       Assert.assertEquals(2, prdStatusList.size());
        for (PrdStatusEntity prdStatus : prdStatusList) {
            if (prdStatus.getPrdState().equals("1"))
               Assert.assertEquals("Active", prdStatus.getPrdState().getName());
            if (prdStatus.getPrdState().equals("2"))
               Assert.assertEquals("InActive", prdStatus.getPrdState().getName());
        }
    }

    public void testGetApplicablePrdStatusForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getApplicablePrdStatus((short) 1);
            Assert.fail();
        } catch (ServiceException e) {
        }
    }

    public void testGetLoanOfferingWithLocaleId() throws ServiceException {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.closeSession();

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

    public void testGetLoanOfferingWithLocaleIdForInvalidConnection() {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.closeSession();

        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getLoanOffering(loanOffering.getPrdOfferingId(), (short) 1);
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testGetAllLoanOfferings() throws ServiceException {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Offering1", "Loa1");
        StaticHibernateUtil.closeSession();

        List<LoanOfferingBO> loanOfferings = new LoanPrdBusinessService().getAllLoanOfferings((short) 1);
        Assert.assertNotNull(loanOfferings);
       Assert.assertEquals(2, loanOfferings.size());
        for (LoanOfferingBO loanOfferingBO : loanOfferings) {
            Assert.assertNotNull(loanOfferingBO.getPrdOfferingName());
            Assert.assertNotNull(loanOfferingBO.getPrdOfferingId());
            Assert.assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
        }
        StaticHibernateUtil.closeSession();
        TestObjectFactory.removeObject(loanOffering1);
    }

    public void testGetAllLoanOfferingsForInvalidConnection() {
        loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.closeSession();

        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().getAllLoanOfferings((short) 1);
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testRetrieveLatenessForPrd() throws Exception {
        try {
            Short latenessDays = new LoanPrdBusinessService().retrieveLatenessForPrd();
           Assert.assertEquals(latenessDays, Short.valueOf("10"));
        } catch (ServiceException e) {
           Assert.assertTrue(false);
        }
        StaticHibernateUtil.closeSession();

    }

    public void testRetrieveLatenessForPrdForInvalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new LoanPrdBusinessService().retrieveLatenessForPrd();
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

    }

    private LoanOfferingBO createLoanOfferingBO(String prdOfferingName, String shortName) {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createLoanOffering(prdOfferingName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency);
    }
}
