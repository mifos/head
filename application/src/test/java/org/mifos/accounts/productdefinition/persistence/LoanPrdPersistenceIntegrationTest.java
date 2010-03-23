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

import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public LoanPrdPersistenceIntegrationTest() throws Exception {
        super();
    }

    private LoanOfferingBO loanOffering1;
    private LoanOfferingBO loanOffering2;
    private LoanOfferingBO loanOffering3;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(loanOffering1);
        TestObjectFactory.removeObject(loanOffering2);
        TestObjectFactory.removeObject(loanOffering3);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testretrieveLatenessForPrd() throws Exception {
        Short latenessDays = null;
        latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
        Assert.assertNotNull(latenessDays);
       Assert.assertEquals(Short.valueOf("10"), latenessDays);
    }

    public void testGetAllActiveLoanOfferings() throws PersistenceException {
        List<LoanOfferingBO> loanOfferingList = new LoanPrdPersistence().getAllActiveLoanOfferings(Short.valueOf("1"));
        Assert.assertNotNull(loanOfferingList);
    }

    public void testGetLoanOfferingsNotMixed() throws PersistenceException {
        List<LoanOfferingBO> loanOfferingList = new LoanPrdPersistence().getLoanOfferingsNotMixed(Short.valueOf("1"));
        Assert.assertNotNull(loanOfferingList);
    }

    public void testGetLoanOffering() throws PersistenceException {
        loanOffering1 = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.closeSession();

        loanOffering1 = new LoanPrdPersistence().getLoanOffering(loanOffering1.getPrdOfferingId());
        Assert.assertNotNull(loanOffering1);
       Assert.assertEquals("Loan Offering", loanOffering1.getPrdOfferingName());
       Assert.assertEquals("Loan", loanOffering1.getPrdOfferingShortName());
    }

    public void testGetLoanOfferingWithLocaleId() throws PersistenceException {
        loanOffering1 = createLoanOfferingBO("Loan Offering", "Loan");
        StaticHibernateUtil.closeSession();

        short localeId = 1;
        loanOffering1 = new LoanPrdPersistence().getLoanOffering(loanOffering1.getPrdOfferingId(), localeId);
        Assert.assertNotNull(loanOffering1);
       Assert.assertEquals("Loan Offering", loanOffering1.getPrdOfferingName());
       Assert.assertEquals("Loan", loanOffering1.getPrdOfferingShortName());

       Assert.assertEquals("Other", loanOffering1.getPrdCategory().getProductCategoryName());
       Assert.assertEquals(ApplicableTo.GROUPS, loanOffering1.getPrdApplicableMasterEnum());
       Assert.assertEquals("Active", loanOffering1.getPrdStatus().getPrdState().getName());
       Assert.assertEquals("Grace on all repayments", loanOffering1.getGracePeriodType().getName());
       Assert.assertEquals("Flat", loanOffering1.getInterestTypes().getName());
    }

    public void testGetAllLoanOfferingsShouldReturnLoanOfferingListSortedByName() throws PersistenceException {
        String[] loanPrdNamesSortedByName = new String[] { "firstLoanOffering", "secondLoanOffering",
                "thirdLoanOffering" };
        loanOffering1 = createLoanOfferingBO(loanPrdNamesSortedByName[1], "Loa1");
        loanOffering2 = createLoanOfferingBO(loanPrdNamesSortedByName[2], "Loa2");
        loanOffering3 = createLoanOfferingBO(loanPrdNamesSortedByName[0], "Loa3");
        StaticHibernateUtil.closeSession();

        List<LoanOfferingBO> loanOfferings = new LoanPrdPersistence().getAllLoanOfferings((short) 1);
        Assert.assertNotNull(loanOfferings);
       Assert.assertEquals(3, loanOfferings.size());
        int i = 0;
        for (LoanOfferingBO loanOfferingBO : loanOfferings) {
           Assert.assertEquals(loanPrdNamesSortedByName[i++], loanOfferingBO.getPrdOfferingName());
            Assert.assertNotNull(loanOfferingBO.getPrdOfferingId());
            Assert.assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
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
