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

package org.mifos.accounts.fees.business.service;

import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FeeBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public FeeBusinessServiceIntegrationTest() throws Exception {
        super();
        // FIXME Some previous tests leaves Fee objects in table (dirty state)
        // Which causes failure at this test, when executed with all the tests
        // under surefire on a windows xp based systems
        TestDatabase.resetMySQLDatabase();
    }

    private FeeBO fee1;
    private FeeBO fee2;

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(fee2);
        TestObjectFactory.cleanUp(fee1);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetFee() {
        fee1 = TestObjectFactory.createOneTimeAmountFee("Customer_OneTime_AmountFee", FeeCategory.CENTER, "100",
                FeePayment.UPFRONT);
        StaticHibernateUtil.commitTransaction();

        FeeBO dbFee = new FeeBusinessService().getFee(fee1.getFeeId());
        Assert.assertNotNull(dbFee);
        Assert.assertEquals(dbFee.getFeeName(), fee1.getFeeName());
        fee1 = dbFee;
    }

    public void testRetrieveFeesForCustomer() throws Exception {
        fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        StaticHibernateUtil.commitTransaction();

        List<FeeBO> feeList = new FeeBusinessService().retrieveCustomerFees();
        Assert.assertNotNull(feeList);
        Assert.assertEquals(1, feeList.size());
        Assert.assertEquals("CustomerFee1", feeList.get(0).getFeeName());
    }

    public void testRetrieveFeesForCustomerFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new FeeBusinessService().retrieveCustomerFees();
            Assert.fail();
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
    }

    public void testRetrieveFeesForProduct() throws Exception {
        fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        StaticHibernateUtil.commitTransaction();

        List<FeeBO> feeList = new FeeBusinessService().retrieveProductFees();
        Assert.assertNotNull(feeList);
        Assert.assertEquals(1, feeList.size());
        Assert.assertEquals("ProductFee1", feeList.get(0).getFeeName());
    }

    public void testRetrieveFeesForProductFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new FeeBusinessService().retrieveProductFees();
            Assert.fail();
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
    }

    public void testRetrieveCustomerFeesByCategaroyType() throws Exception {
        fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        StaticHibernateUtil.commitTransaction();

        List<FeeBO> feeList = new FeeBusinessService().retrieveCustomerFeesByCategaroyType(FeeCategory.CENTER);
        Assert.assertNotNull(feeList);
        Assert.assertEquals(1, feeList.size());
        Assert.assertEquals("CustomerFee1", feeList.get(0).getFeeName());
    }

    public void testRetrieveCustomerFeesByCategaroyTypeFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new FeeBusinessService().retrieveCustomerFeesByCategaroyType(FeeCategory.ALLCUSTOMERS);
            Assert.fail();
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        }
    }
}
