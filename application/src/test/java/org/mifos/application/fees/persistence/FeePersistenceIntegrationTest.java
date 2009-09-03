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

package org.mifos.application.fees.persistence;

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.fees.business.ApplicableAccountsTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FeePersistenceIntegrationTest extends MifosIntegrationTestCase {

    public FeePersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private FeePersistence feePersistence;

    private FeeBO fee1;

    private FeeBO fee2;

    FeeBO periodicFee;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        feePersistence = new FeePersistence();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.removeObject(fee1);
            TestObjectFactory.removeObject(fee2);
            TestObjectFactory.removeObject(periodicFee);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetUpdatedFeesForCustomer() throws Exception {

        // crate periodic fee
        periodicFee = TestObjectFactory.createPeriodicAmountFee("ClientPeridoicFee", FeeCategory.CLIENT, "5",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();

       Assert.assertEquals(0, feePersistence.getUpdatedFeesForCustomer().size());

        // get fee from db
        periodicFee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, periodicFee.getFeeId());
        periodicFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
        periodicFee.save();
        StaticHibernateUtil.commitTransaction();
       Assert.assertEquals(1, feePersistence.getUpdatedFeesForCustomer().size());

        // cleanup
        periodicFee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, periodicFee.getFeeId());
    }

    // Tests behavior which seems not to be used by anything.
    public void testGetUpdateTypeEntity() throws NumberFormatException, PersistenceException {
        ApplicableAccountsTypeEntity feeUpdateType = feePersistence.getUpdateTypeEntity(Short.valueOf("1"));
       Assert.assertEquals(1, feeUpdateType.getId().intValue());
    }

    public void testRetrieveFeesForCustomer() throws Exception {
        TestDatabase.resetMySQLDatabase();
        fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        StaticHibernateUtil.commitTransaction();

        List<FeeBO> feeList = feePersistence.retrieveCustomerFees();
       Assert.assertEquals(1, feeList.size());
       Assert.assertEquals("CustomerFee1", feeList.get(0).getFeeName());
    }

    public void testRetrieveFeesForProduct() throws Exception {
        fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400",
                RecurrenceType.MONTHLY, Short.valueOf("2"));
        List<FeeBO> feeList = feePersistence.retrieveProductFees();
       Assert.assertEquals(1, feeList.size());
       Assert.assertEquals("ProductFee1", feeList.get(0).getFeeName());
    }

    public void testGetFee() throws Exception {
        fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400", RecurrenceType.WEEKLY,
                Short.valueOf("2"));
        Short feeId = fee2.getFeeId();
        StaticHibernateUtil.commitTransaction();
        fee2 = feePersistence.getFee(fee2.getFeeId(), fee2.getFeeType());
       Assert.assertEquals(feeId.shortValue(), fee2.getFeeId().shortValue());

    }

    public void testGetRateFee() throws Exception {
        fee1 = TestObjectFactory.createOneTimeRateFee("Loanfee", FeeCategory.LOAN, 11.1, FeeFormula.AMOUNT,
                FeePayment.TIME_OF_DISBURSMENT);

        Short feeId = fee1.getFeeId();
        StaticHibernateUtil.commitTransaction();
        fee1 = feePersistence.getFee(fee1.getFeeId(), RateAmountFlag.RATE);
       Assert.assertEquals(feeId.shortValue(), fee1.getFeeId().shortValue());

    }
}
