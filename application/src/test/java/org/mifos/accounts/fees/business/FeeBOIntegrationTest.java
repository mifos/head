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

package org.mifos.accounts.fees.business;

import java.util.Date;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeeLevel;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FeeBOIntegrationTest extends MifosIntegrationTestCase {

    public FeeBOIntegrationTest() throws Exception {
        super();
    }

    private FeeBO fee;

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(fee);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testCreateWithoutFeeName() throws Exception {
        try {
            fee = new AmountFeeBO(TestUtils.makeUser(), "", new CategoryTypeEntity(FeeCategory.CENTER),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"),
                    TestUtils.createMoney("100"), false, new FeePaymentEntity(FeePayment.UPFRONT));
            Assert.assertFalse("Fee is created without fee name", true);
        } catch (FeeException e) {
            Assert.assertNull(fee);
           Assert.assertEquals(e.getKey(), FeeConstants.INVALID_FEE_NAME);
        }
    }

    public void testCreateWithoutFeeCategory() throws Exception {
        try {
            fee = new AmountFeeBO(TestUtils.makeUser(), "Customer Fee", null, new FeeFrequencyTypeEntity(
                    FeeFrequencyType.ONETIME), getGLCode("7"), TestUtils.createMoney("100"), false,
                    new FeePaymentEntity(FeePayment.UPFRONT));
            Assert.assertFalse("Fee is created without fee category", true);
        } catch (FeeException e) {
            Assert.assertNull(fee);
           Assert.assertEquals(e.getKey(), FeeConstants.INVALID_FEE_CATEGORY);
        }
    }

    public void testCreateFeeWithoutFeeFrequency() throws Exception {
        try {
            fee = new AmountFeeBO(TestUtils.makeUser(), "Customer Fee", new CategoryTypeEntity(FeeCategory.CENTER),
                    null, getGLCode("7"), TestUtils.createMoney("100"), false, new FeePaymentEntity(
                            FeePayment.UPFRONT));
            Assert.assertFalse("Fee is created without frequency type", true);
        } catch (FeeException e) {
            Assert.assertNull(fee);
           Assert.assertEquals(e.getKey(), FeeConstants.INVALID_FEE_FREQUENCY_TYPE);
        }
    }

    public void testCreateAmountFeeWithoutAmount() throws Exception {
        try {
            fee = new AmountFeeBO(TestUtils.makeUser(), "Customer Fee", new CategoryTypeEntity(FeeCategory.CENTER),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), null, false,
                    new FeePaymentEntity(FeePayment.UPFRONT));
            Assert.assertFalse("Fee is created without Amount", true);
        } catch (FeeException e) {
            Assert.assertNull(fee);
           Assert.assertEquals(e.getKey(), FeeConstants.INVALID_FEE_AMOUNT);
        }
    }

    public void testCreateRateFeeWithoutRate() throws Exception {
        try {
            FeeFormulaEntity feeFormula = new FeeFormulaEntity(FeeFormula.AMOUNT);
            fee = new RateFeeBO(TestUtils.makeUser(), "Customer Fee", new CategoryTypeEntity(FeeCategory.CENTER),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), null, feeFormula, false,
                    new FeePaymentEntity(FeePayment.UPFRONT));
            Assert.assertFalse("Fee is created without Rate", true);
        } catch (FeeException e) {
            Assert.assertNull(fee);
           Assert.assertEquals(e.getKey(), FeeConstants.INVALID_FEE_RATE_OR_FORMULA);
        }
    }

    public void testCreateRateFeeWithoutFormula() throws Exception {
        try {
            fee = new RateFeeBO(TestUtils.makeUser(), "Customer Fee", new CategoryTypeEntity(FeeCategory.CENTER),
                    new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), 2.0, null, false,
                    new FeePaymentEntity(FeePayment.UPFRONT));
            Assert.assertFalse("Fee is created without Formula", true);
        } catch (FeeException e) {
            Assert.assertNull(fee);
           Assert.assertEquals(e.getKey(), FeeConstants.INVALID_FEE_RATE_OR_FORMULA);
        }
    }

    public void testCreateOneTimeAmountFee() throws Exception {
        String name = "Customer_OneTime_AmountFee";
        fee = createOneTimeAmountFee(name, FeeCategory.CENTER, "100", false, FeePayment.UPFRONT);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
       Assert.assertEquals(name, fee.getFeeName());
       Assert.assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
        StaticHibernateUtil.closeSession();
    }

    public void testDoesFeeInvolveFractionalAmountsForWholeAmountFee() throws Exception {
        String name = "Customer_OneTime_AmountFee";
        fee = createOneTimeAmountFee(name, FeeCategory.CENTER, "100", false, FeePayment.UPFRONT);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());

        Assert.assertFalse(fee.doesFeeInvolveFractionalAmounts());

        StaticHibernateUtil.closeSession();
    }

    public void testDoesFeeInvolveFractionalAmountsForFractionalAmountFee() throws Exception {
        String name = "Customer_OneTime_AmountFee";
        fee = createOneTimeAmountFee(name, FeeCategory.CENTER, "100.23", false, FeePayment.UPFRONT);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());

       Assert.assertTrue(fee.doesFeeInvolveFractionalAmounts());

        StaticHibernateUtil.closeSession();
    }

    public void testDoesFeeInvolveFractionalAmountsForRateFee() throws Exception {
        fee = createOneTimeRateFee("Customer_OneTime_RateFee", FeeCategory.CENTER, 100.0, FeeFormula.AMOUNT, false,
                FeePayment.UPFRONT);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
       Assert.assertTrue(fee.doesFeeInvolveFractionalAmounts());
        StaticHibernateUtil.closeSession();
    }

    public void testCreateOneTimeRateFee() throws Exception {
        fee = createOneTimeRateFee("Customer_OneTime_RateFee", FeeCategory.CENTER, 100.0, FeeFormula.AMOUNT, false,
                FeePayment.UPFRONT);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
       Assert.assertEquals("Customer_OneTime_RateFee", fee.getFeeName());
       Assert.assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
        StaticHibernateUtil.closeSession();
    }

    public void testCreatePeriodicAmountFee() throws Exception {
        MeetingBO feefrequency = new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(),
                MeetingType.PERIODIC_FEE);
        fee = createPeriodicAmountFee("Customer_Periodic_AmountFee", FeeCategory.CENTER, "100", false, feefrequency);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
       Assert.assertEquals("Customer_Periodic_AmountFee", fee.getFeeName());
       Assert.assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
        StaticHibernateUtil.closeSession();
    }

    public void testCreatePeriodicRateFee() throws Exception {
        MeetingBO feefrequency = new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(),
                MeetingType.PERIODIC_FEE);
        fee = createPeriodicRateFee("Customer_Periodic_RateFee", FeeCategory.CENTER, 100.0, FeeFormula.AMOUNT, false,
                feefrequency);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
       Assert.assertEquals("Customer_Periodic_RateFee", fee.getFeeName());
       Assert.assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
        StaticHibernateUtil.closeSession();
    }

    public void testCreateOneTimeDefaultFee() throws Exception {
        fee = createOneTimeAmountFee("Customer_OneTime_DefaultFee", FeeCategory.GROUP, "100", true, FeePayment.UPFRONT);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
       Assert.assertEquals("Customer_OneTime_DefaultFee", fee.getFeeName());
       Assert.assertEquals(FeeCategory.GROUP.getValue(), fee.getCategoryType().getId());
       Assert.assertTrue(fee.isCustomerDefaultFee());
       Assert.assertTrue(vaidateDefaultCustomerFee(fee.getFeeLevels(), fee.getCategoryType().getFeeCategory()));
        StaticHibernateUtil.closeSession();
    }

    public void testCreatePeriodicDefaultFee() throws Exception {
        MeetingBO feefrequency = new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(),
                MeetingType.PERIODIC_FEE);
        fee = createPeriodicRateFee("Customer_Periodic_DefaultFee", FeeCategory.ALLCUSTOMERS, 100.0, FeeFormula.AMOUNT,
                true, feefrequency);
        fee.save();
        StaticHibernateUtil.commitTransaction();

        fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
       Assert.assertEquals("Customer_Periodic_DefaultFee", fee.getFeeName());
       Assert.assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType().getId());
       Assert.assertEquals(true, fee.isCustomerDefaultFee());
       Assert.assertTrue(vaidateDefaultCustomerFee(fee.getFeeLevels(), fee.getCategoryType().getFeeCategory()));
        StaticHibernateUtil.closeSession();
    }

    public void testSaveFailure() throws Exception {

        try {
            FeeFormulaEntity feeFormula = new FeeFormulaEntity(FeeFormula.AMOUNT);

            FeeBO fees = new RateFeeBO(TestUtils.makeUser(), "Customer Fee",
                    new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
                    getGLCode("7"), 2.0, feeFormula, false, new FeePaymentEntity(FeePayment.UPFRONT));
            ;
            TestObjectFactory.simulateInvalidConnection();
            fees.save();
            Assert.fail();
        } catch (FeeException e) {
           Assert.assertTrue(true);
        }
    }

    public void testCreateFeeFailure() throws Exception {

        try {
            FeeFormulaEntity feeFormula = new FeeFormulaEntity(FeeFormula.AMOUNT);
            UserContext uc = TestUtils.makeUser();
            GLCodeEntity glcode = getGLCode("7");
            TestObjectFactory.simulateInvalidConnection();
            new RateFeeBO(uc, "Customer Fee", new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(
                    FeeFrequencyType.ONETIME), glcode, 2.0, feeFormula, false, new FeePaymentEntity(FeePayment.UPFRONT));
            Assert.fail();
        } catch (FeeException e) {
           Assert.assertTrue(true);
        }
    }

    public void testFeeLevel() {
       Assert.assertEquals(FeeLevel.CENTERLEVEL, FeeLevel.getFeeLevel(FeeLevel.CENTERLEVEL.getValue()));
       Assert.assertEquals(null, FeeLevel.getFeeLevel(Short.valueOf("99")));
    }

    private boolean vaidateDefaultCustomerFee(Set<FeeLevelEntity> defaultCustomers, FeeCategory feeCategory) {
        boolean bCenter = false;
        boolean bGroup = false;
        boolean bClient = false;

        for (FeeLevelEntity feeLevel : defaultCustomers) {
            if (feeLevel.getLevelId().equals(FeeLevel.CENTERLEVEL.getValue()))
                bCenter = true;
            if (feeLevel.getLevelId().equals(FeeLevel.GROUPLEVEL.getValue()))
                bGroup = true;
            if (feeLevel.getLevelId().equals(FeeLevel.CLIENTLEVEL.getValue()))
                bClient = true;
        }

        if (feeCategory.equals(FeeCategory.CENTER))
            return bCenter && !bGroup && !bClient;

        if (feeCategory.equals(FeeCategory.GROUP))
            return !bCenter && bGroup && !bClient;

        if (feeCategory.equals(FeeCategory.CLIENT))
            return !bCenter && !bGroup && bClient;

        if (feeCategory.equals(FeeCategory.ALLCUSTOMERS))
            return bCenter && bGroup && bClient;

        return !bCenter && !bGroup && !bClient;
    }

    private FeeBO createOneTimeRateFee(String feeName, FeeCategory feeCategory, Double rate, FeeFormula feeFormula,
            boolean isDefaultFee, FeePayment feePayment) throws Exception {
        return new RateFeeBO(TestUtils.makeUser(), feeName, new CategoryTypeEntity(feeCategory),
                new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), rate, new FeeFormulaEntity(
                        feeFormula), isDefaultFee, new FeePaymentEntity(feePayment));
    }

    private FeeBO createPeriodicRateFee(String feeName, FeeCategory feeCategory, Double rate, FeeFormula feeFormula,
            boolean isDefaultFee, MeetingBO feeFrequency) throws Exception {
        return new RateFeeBO(TestUtils.makeUser(), feeName, new CategoryTypeEntity(feeCategory),
                new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), getGLCode("7"), rate, new FeeFormulaEntity(
                        feeFormula), isDefaultFee, feeFrequency);
    }

    private FeeBO createOneTimeAmountFee(String feeName, FeeCategory feeCategory, String amount, boolean isDefaultFee,
            FeePayment feePayment) throws Exception {
        return new AmountFeeBO(TestUtils.makeUser(), feeName, new CategoryTypeEntity(feeCategory),
                new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"),
                TestUtils.createMoney(amount), isDefaultFee, new FeePaymentEntity(feePayment));
    }

    private FeeBO createPeriodicAmountFee(String feeName, FeeCategory feeCategory, String amount, boolean isDefaultFee,
            MeetingBO feeFrequency) throws Exception {
        return new AmountFeeBO(TestUtils.makeUser(), feeName, new CategoryTypeEntity(feeCategory),
                new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), getGLCode("7"),
                TestUtils.createMoney(amount), isDefaultFee, feeFrequency);
    }

    private GLCodeEntity getGLCode(String id) {
        return (GLCodeEntity) TestObjectFactory.getObject(GLCodeEntity.class, Short.valueOf(id));

    }

    public void testAmountFeeBOUpdateChangeTypeSetsNotUpdated() throws Exception {
        AmountFeeBO feeToChange = createAmountFeeToTestChangeFeeType();
        AmountFeeBO newFee = createAmountFeeToTestChangeFeeType();
        feeToChange.calculateNewFeeChangeType(newFee.getFeeAmount(), newFee.getFeeStatus());
        assertFeeChangeType(FeeChangeType.NOT_UPDATED, feeToChange);
    }

    public void testAmountFeeBOUpdateChangeTypeSetsAmountUpdated() throws Exception {
        AmountFeeBO feeToChange = createAmountFeeToTestChangeFeeType();
        AmountFeeBO newFee = (AmountFeeBO) createPeriodicAmountFee("fee", FeeCategory.ALLCUSTOMERS, "2000", true,
                createMeeting());
        feeToChange.updateFeeChangeType(feeToChange.calculateNewFeeChangeType(newFee.getFeeAmount(), newFee
                .getFeeStatus()));
        assertFeeChangeType(FeeChangeType.AMOUNT_UPDATED, feeToChange);
    }

    public void testAmountFeeBOUpdateChangeTypeSetsStatusUpdated() throws Exception {
        AmountFeeBO feeToChange = createAmountFeeToTestChangeFeeType();
        AmountFeeBO newFee = createAmountFeeToTestChangeFeeType();
        newFee.updateStatus(FeeStatus.INACTIVE);
        feeToChange.updateFeeChangeType(feeToChange.calculateNewFeeChangeType(newFee.getFeeAmount(), newFee
                .getFeeStatus()));
        assertFeeChangeType(FeeChangeType.STATUS_UPDATED, feeToChange);
    }

    public void testAmountFeeBOUpdateChangeTypeSetsAmountAndStatusUpdated() throws Exception {
        AmountFeeBO feeToChange = createAmountFeeToTestChangeFeeType();
        AmountFeeBO newFee = (AmountFeeBO) createPeriodicAmountFee("fee", FeeCategory.ALLCUSTOMERS, "2000", true,
                createMeeting());
        newFee.updateStatus(FeeStatus.INACTIVE);
        feeToChange.updateFeeChangeType(feeToChange.calculateNewFeeChangeType(newFee.getFeeAmount(), newFee
                .getFeeStatus()));
        assertFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED, feeToChange);
    }

    public void testRateFeeBOUpdateChangeTypeSetsNotUpdated() throws Exception {
        RateFeeBO feeToChange = createRateFeeToTestChangeFeeType();
        RateFeeBO newFee = createRateFeeToTestChangeFeeType();
        feeToChange.updateFeeChangeType(feeToChange.calculateNewFeeChangeType(newFee.getRate(), newFee.getFeeStatus()));
        assertFeeChangeType(FeeChangeType.NOT_UPDATED, feeToChange);
    }

    public void testRateFeeBOUpdateChangeTypeSetsAmountUpdated() throws Exception {
        RateFeeBO feeToChange = createRateFeeToTestChangeFeeType();
        RateFeeBO newFee = new RateFeeBO(TestUtils.makeUser(), "Customer Fee", new CategoryTypeEntity(
                FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), 1.0,
                new FeeFormulaEntity(FeeFormula.AMOUNT), false, new FeePaymentEntity(FeePayment.UPFRONT));
        feeToChange.updateFeeChangeType(feeToChange.calculateNewFeeChangeType(newFee.getRate(), newFee.getFeeStatus()));
        assertFeeChangeType(FeeChangeType.AMOUNT_UPDATED, feeToChange);
    }

    public void testRateFeeBOUpdateChangeTypeSetsStatusUpdated() throws Exception {
        RateFeeBO feeToChange = createRateFeeToTestChangeFeeType();
        RateFeeBO newFee = createRateFeeToTestChangeFeeType();
        newFee.updateStatus(FeeStatus.INACTIVE);
        feeToChange.updateFeeChangeType(feeToChange.calculateNewFeeChangeType(newFee.getRate(), newFee.getFeeStatus()));
        assertFeeChangeType(FeeChangeType.STATUS_UPDATED, feeToChange);
    }

    public void testRateFeeBOUpdateChangeTypeSetsAmountAndStatusUpdated() throws Exception {
        RateFeeBO feeToChange = createRateFeeToTestChangeFeeType();
        RateFeeBO newFee = new RateFeeBO(TestUtils.makeUser(), "Customer Fee", new CategoryTypeEntity(
                FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), 1.0,
                new FeeFormulaEntity(FeeFormula.AMOUNT), false, new FeePaymentEntity(FeePayment.UPFRONT));
        newFee.updateStatus(FeeStatus.INACTIVE);
        feeToChange.updateFeeChangeType(feeToChange.calculateNewFeeChangeType(newFee.getRate(), newFee.getFeeStatus()));
        assertFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED, feeToChange);
    }

    private void assertFeeChangeType(FeeChangeType feeChangeType, FeeBO feeToChange) throws PropertyNotFoundException {
       Assert.assertEquals(feeChangeType, feeToChange.getFeeChangeType());
    }

    private AmountFeeBO createAmountFeeToTestChangeFeeType() throws Exception, MeetingException {
        return (AmountFeeBO) createPeriodicAmountFee("fee", FeeCategory.ALLCUSTOMERS, "1000", true, createMeeting());
    }

    private RateFeeBO createRateFeeToTestChangeFeeType() throws FeeException {
        return new RateFeeBO(TestUtils.makeUser(), "Customer Fee", new CategoryTypeEntity(FeeCategory.CENTER),
                new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), 2.0, new FeeFormulaEntity(
                        FeeFormula.AMOUNT), false, new FeePaymentEntity(FeePayment.UPFRONT));
    }

    private MeetingBO createMeeting() throws MeetingException {
        return new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.PERIODIC_FEE);
    }
}
