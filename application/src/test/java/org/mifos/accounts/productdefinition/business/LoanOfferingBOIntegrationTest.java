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

package org.mifos.accounts.productdefinition.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdOfferingDto;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanOfferingBOIntegrationTest extends MifosIntegrationTestCase {

    public LoanOfferingBOIntegrationTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;

    private LoanOfferingBO product;

    private FeeBO periodicFee;

    private FeeBO oneTimeFee;

    private PrdApplicableMasterEntity prdApplicableMaster;

    private MeetingBO frequency;

    private GLCodeEntity principalglCodeEntity;

    private GLCodeEntity intglCodeEntity;

    private ProductCategoryBO productCategory;

    private InterestTypesEntity interestTypes;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(product);
        TestObjectFactory.cleanUp(periodicFee);
        TestObjectFactory.cleanUp(oneTimeFee);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testUpdateLoanOfferingFeeTypesForLogging() throws Exception {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        periodicFee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100",
                RecurrenceType.MONTHLY, (short) 1);

        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(periodicFee);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        StaticHibernateUtil.getInterceptor().createInitialValueMap(product);
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, 12.0, 2.0, 12.0, false,
                false, true, null, fees, (short) 2, RecurrenceType.MONTHLY, populateLoanPrdActionForm("1", "1",
                        new Double("3000"), new Double("1000"), new Double("1000"), "12", "1", "2"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        fees = new ArrayList<FeeBO>();
        oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time", FeeCategory.LOAN, "100",
                FeePayment.UPFRONT);
        fees.add(oneTimeFee);
        product.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(product);
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, 12.0, 2.0, 12.0, false,
                true, false, null, fees, (short) 2, RecurrenceType.MONTHLY, populateLoanPrdActionForm("1", "1",
                        new Double("3000"), new Double("1000"), new Double("1000"), "12", "1", "2"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.LOANPRODUCT, new Integer(product
                .getPrdOfferingId().toString()));
       Assert.assertEquals(2, auditLogList.size());
       Assert.assertEquals(EntityType.LOANPRODUCT.getValue(), auditLogList.get(0).getEntityType());
       Assert.assertEquals(14, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Fee Types")
                    && auditLogRecord.getNewValue().equalsIgnoreCase("Loan Periodic")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Fee Types")
                    && auditLogRecord.getNewValue().equalsIgnoreCase("Loan One time")) {
               Assert.assertEquals("Loan Periodic", auditLogRecord.getOldValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Service Charge deducted At Disbursement")) {
               Assert.assertEquals("1", auditLogRecord.getOldValue());
               Assert.assertEquals("0", auditLogRecord.getNewValue());
            }
        }
        TestObjectFactory.cleanUpChangeLog();

    }

    public void testUpdateLoanOfferingForLogging() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        periodicFee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100",
                RecurrenceType.MONTHLY, (short) 1);
        oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time", FeeCategory.LOAN, "100",
                FeePayment.UPFRONT);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(periodicFee);
        fees.add(oneTimeFee);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        StaticHibernateUtil.getInterceptor().createInitialValueMap(product);
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, 12.0, 2.0, 12.0, false,
                true, true, null, fees, (short) 2, RecurrenceType.MONTHLY, populateLoanPrdActionForm("1", "1",
                        new Double("3000"), new Double("1000"), new Double("1000"), "12", "1", "2"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.LOANPRODUCT, new Integer(product
                .getPrdOfferingId().toString()));
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.LOANPRODUCT.getValue(), auditLogList.get(0).getEntityType());
       Assert.assertEquals(15, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Min Loan Amount")) {
               Assert.assertEquals("300.0", auditLogRecord.getOldValue());
               Assert.assertEquals("1000.0", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Description")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
               Assert.assertEquals("Loan Product updated", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Applicable For")) {
               Assert.assertEquals("Groups", auditLogRecord.getOldValue());
               Assert.assertEquals("Clients", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Frequency Of Installments")) {
               Assert.assertEquals("Week(s)", auditLogRecord.getOldValue());
               Assert.assertEquals("Month(s)", auditLogRecord.getNewValue());
            }
        }
        TestObjectFactory.cleanUpChangeLog();

    }

    public void testBuildloanOfferingWithoutDataForMandatoryFields() {
        try {
            new LoanOfferingBO(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, false, false, false, null, null, null);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithoutDataForAllFields() {
        try {
            new LoanOfferingBO(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, false, false, false, null, null, null, null, null);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithoutName() throws SystemException, ApplicationException {
        createIntitalObjects();
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), null, "S", productCategory, prdApplicableMaster,
                    new Date(System.currentTimeMillis()), interestTypes, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), 12.0,
                    2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithShortNameGreaterThanFourDig() throws SystemException, ApplicationException {
        createIntitalObjects();
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOANOFF", productCategory,
                    prdApplicableMaster, new Date(System.currentTimeMillis()), interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false,
                    frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithStartDateLessThanCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(-2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAN", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildWithStartDateEqualToCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        LoanOfferingBO product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAN",
                productCategory, prdApplicableMaster, startDate, interestTypes, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"),
                12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, true, false, frequency,
                principalglCodeEntity, intglCodeEntity);
        Assert.assertNotNull(product.getGlobalPrdOfferingNum());
       Assert.assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
    }

    public void testStartDateGreaterThanCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(2);
        LoanOfferingBO product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAN",
                productCategory, prdApplicableMaster, startDate, interestTypes, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"),
                12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, true, false, frequency,
                principalglCodeEntity, intglCodeEntity);
        Assert.assertNotNull(product.getGlobalPrdOfferingNum());
       Assert.assertEquals(PrdStatus.LOAN_INACTIVE, product.getStatus());

    }

    public void testBuildloanOfferingWithDuplicatePrdOfferingName() throws SystemException, ApplicationException {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Product", "LOAN", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithDuplicatePrdOfferingShortName() throws SystemException, ApplicationException {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithEndDateLessThanStartDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(-2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), null, 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false,
                    null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithNoInterestTypes() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Product", "LOAN", productCategory,
                    prdApplicableMaster, startDate, null, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), 12.0, 2.0, 3.0,
                    (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithNoMaxAmount() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money(getCurrency(), "1000.0"), null, 12.0, 2.0, 3.0,
                    (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithoutGLCode() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money(getCurrency(), "1000.0"), new Money(getCurrency(), "3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, null, null);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testDefAmountNotBetweenMinMaxAmounts() {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "200.0"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testMinAmountGreaterThanMaxAmount() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "10000"),
                    new Money(getCurrency(), "3000"), null, 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false,
                    null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testDefInterestRateNotBetweenMinMaxRates() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 13.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testMinInterestRateGretaterThanMaxRate() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 20.0, 13.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testMinInstallmentsGreaterThanMaxInstallments() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 31, (short) 21, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testDefInstallmentsNotBetweenMinMaxInstallments() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11, (short) 7, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testGracePeriodForIntDedAtDisb() throws ProductDefinitionException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(
                        getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, true, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
        Assert.assertNotNull(loanOffering.getGracePeriodType());
        Assert.assertNotNull(loanOffering.getGracePeriodDuration());
       Assert.assertEquals(GraceType.NONE.getValue(), loanOffering.getGracePeriodType().getId());
       Assert.assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
    }

    public void testNullGracePeriodDurationWithGraceType() throws ProductDefinitionException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.PRINCIPALONLYGRACE);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, gracePeriodType, null, interestTypes, new Money(
                            getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                    (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testNullGracePeriodDurationForNoneGraceType() throws ProductDefinitionException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, gracePeriodType, null, interestTypes,
                new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
        Assert.assertNotNull(loanOffering.getGracePeriodDuration());
       Assert.assertEquals(GraceType.NONE.getValue(), loanOffering.getGracePeriodType().getId());
       Assert.assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
    }

    public void testFeeNotMatchingFrequencyOfLoanOffering() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), intglCodeEntity, new Money(
                        getCurrency(), "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, EVERY_MONTH,
                CUSTOMER_MEETING, MONDAY)));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, gracePeriodType, null, interestTypes, new Money(
                            getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                    (short) 17, false, false, false, null, fees, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testWithFundsAndOneTimeFee() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), intglCodeEntity, new Money(
                        getCurrency(), "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting()));
        FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), intglCodeEntity, new Money(
                        getCurrency(), "100"), false, new FeePaymentEntity(FeePayment.UPFRONT));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        fees.add(fee1);
        List<FundBO> funds = new ArrayList<FundBO>();
        funds.add(null);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, gracePeriodType, (short) 2,
                interestTypes, new Money(getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20,
                (short) 11, (short) 17, false, false, false, funds, fees, frequency, principalglCodeEntity,
                intglCodeEntity);
       Assert.assertEquals(2, loanOffering.getLoanOfferingFees().size());
       Assert.assertEquals(1, loanOffering.getLoanOfferingFunds().size());
    }

    public void testCreateLoanOffering() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        periodicFee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, (short) 1);
        oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time", FeeCategory.LOAN, "100",
                FeePayment.UPFRONT);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(periodicFee);
        fees.add(oneTimeFee);

        product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                prdApplicableMaster, startDate, endDate, "1234", gracePeriodType, (short) 2, interestTypes, new Money(
                        getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, fees, frequency, principalglCodeEntity, intglCodeEntity,
                ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN.toString(),
                ProductDefinitionConstants.NOOFINSTALLSAMEFORALLLOAN.toString());
        product.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

       Assert.assertEquals("Loan Offering", product.getPrdOfferingName());
       Assert.assertEquals("LOAP", product.getPrdOfferingShortName());
       Assert.assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
       Assert.assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
       Assert.assertEquals(startDate, product.getStartDate());
       Assert.assertEquals(endDate, product.getEndDate());
       Assert.assertEquals("1234", product.getDescription());
       Assert.assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
       Assert.assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
       Assert.assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        LoanAmountOption eligibleLoanAmount = product.eligibleLoanAmount(null, null);
       Assert.assertEquals(1000.0, eligibleLoanAmount.getMinLoanAmount(), DELTA);
       Assert.assertEquals(3000.0, eligibleLoanAmount.getMaxLoanAmount(), DELTA);
       Assert.assertEquals(2000, eligibleLoanAmount.getDefaultLoanAmount(), DELTA);
       Assert.assertEquals(2.0, product.getMinInterestRate(), DELTA);
       Assert.assertEquals(12.0, product.getMaxInterestRate(), DELTA);
       Assert.assertEquals(3.0, product.getDefInterestRate(), DELTA);
        LoanOfferingInstallmentRange eligibleNoOfInstall = product.eligibleNoOfInstall(null, null);
       Assert.assertEquals(Short.valueOf("11"), eligibleNoOfInstall.getMinNoOfInstall());
       Assert.assertEquals(Short.valueOf("20"), eligibleNoOfInstall.getMaxNoOfInstall());
       Assert.assertEquals(Short.valueOf("17"), eligibleNoOfInstall.getDefaultNoOfInstall());
        Assert.assertFalse(product.isIncludeInLoanCounter());
        Assert.assertFalse(product.isIntDedDisbursement());
        Assert.assertFalse(product.isPrinDueLastInst());
       Assert.assertEquals(2, product.getLoanOfferingFees().size());
        Assert.assertNotNull(product.getLoanOfferingMeeting());
       Assert.assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        Assert.assertNotNull(product.getPrincipalGLcode());
        Assert.assertNotNull(product.getInterestGLcode());
    }

    public void testUpdateloanOfferingWithoutDataForMandatoryFields() {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, false, false, false, null, null, null, null);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithoutName() throws SystemException, ApplicationException {
        createIntitalObjects();
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, null, "S", productCategory, prdApplicableMaster, new Date(System
                    .currentTimeMillis()), null, "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes,
                    (short) 0, new Money(getCurrency(), "3000"), new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20,
                    (short) 1, (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithShortNameGreaterThanFourDig() throws SystemException, ApplicationException {
        createIntitalObjects();
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOANS", productCategory, prdApplicableMaster, new Date(System
                    .currentTimeMillis()), null, "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes,
                    (short) 0, new Money(getCurrency(), "3000"), new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20,
                    (short) 1, (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testupdateloanOfferingWithStartDateLessThanCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        product = createLoanOfferingBO("Loan Product", "LOAP");
        Date startDate = offSetCurrentDate(-2);
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                    new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithStartDateEqualToCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
                false, null, null, (short) 2, RecurrenceType.WEEKLY);
        StaticHibernateUtil.commitTransaction();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
       Assert.assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());

    }

    public void testUpdateloanOfferingWithStartDateGreaterThanCurrentDate() throws SystemException,
            ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                    new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }

    }

    public void testUpdateloanOfferingWithDuplicatePrdOfferingName() throws SystemException, ApplicationException {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Product1", "LOA1");
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            loanOffering1.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate,
                    null, "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(
                            getCurrency(), "3000"), new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
                    (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
        TestObjectFactory.removeObject(loanOffering1);
    }

    public void testUpdateloanOfferingWithDuplicatePrdOfferingShortName() throws SystemException, ApplicationException {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Product1", "LOA1");
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            loanOffering1.update((short) 1, "Loan Product1", "LOAP", productCategory, prdApplicableMaster, startDate,
                    null, "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(
                            getCurrency(), "3000"), new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
                    (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
        TestObjectFactory.removeObject(loanOffering1);
    }

    public void testUpdateloanOfferingWithEndDateLessThanStartDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(-2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                    new Money(getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithNoInterestTypes() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, null, (short) 0, new Money(getCurrency(), "3000"), new Money(
                            getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testupdateloanOfferingWithNoMaxAmount() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, null, new Money(
                            getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testupdateloanOfferingInvalidConnection() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        TestObjectFactory.simulateInvalidConnection();
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, null, new Money(
                            getCurrency(), "1000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testUpdateDefAmountNotBetweenMinMaxAmounts() {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateMinAmountGreaterThanMaxAmount() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateDefInterestRateNotBetweenMinMaxRates() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                    new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 13.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateMinInterestRateGretaterThanMaxRate() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                    new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 22.0, 12.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateMinInstallmentsGreaterThanMaxInstallments() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                    new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 12.0, (short) 2, (short) 12, (short) 2, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateDefInstallmentsNotBetweenMinMaxInstallments() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                    new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 22, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateGracePeriodForIntDedAtDisb() throws ProductDefinitionException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 2, false, true,
                false, null, null, (short) 2, RecurrenceType.WEEKLY);
        StaticHibernateUtil.commitTransaction();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

        Assert.assertNotNull(product.getGracePeriodType());
        Assert.assertNotNull(product.getGracePeriodDuration());
       Assert.assertEquals(GraceType.NONE.getValue(), product.getGracePeriodType().getId());
       Assert.assertEquals(Short.valueOf("0"), product.getGracePeriodDuration());
    }

    public void testUpdateFeeNotMatchingFrequencyOfLoanOffering() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), intglCodeEntity, new Money(
                        getCurrency(), "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, EVERY_MONTH,
                CUSTOMER_MEETING, MONDAY)));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, gracePeriodType, interestTypes, (short) 0,
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
                    (short) 2, false, true, false, null, fees, (short) 2, RecurrenceType.WEEKLY);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateWithFundsAndOneTimeFee() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), intglCodeEntity, new Money(
                        getCurrency(), "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting()));
        FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), intglCodeEntity, new Money(
                        getCurrency(), "100"), false, new FeePaymentEntity(FeePayment.UPFRONT));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        fees.add(fee1);
        List<FundBO> funds = new ArrayList<FundBO>();
        funds.add(null);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, gracePeriodType, interestTypes, (short) 0, new Money(
                        getCurrency(), "3000"), new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
                (short) 2, false, true, false, funds, fees, (short) 2, RecurrenceType.WEEKLY);
       Assert.assertEquals(2, product.getLoanOfferingFees().size());
       Assert.assertEquals(1, product.getLoanOfferingFunds().size());
    }

    public void testUpdateLoanOffering() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        periodicFee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100",
                RecurrenceType.MONTHLY, (short) 1);
        oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time", FeeCategory.LOAN, "100",
                FeePayment.UPFRONT);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(periodicFee);
        fees.add(oneTimeFee);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money(getCurrency(), "3000"),
                new Money(getCurrency(), "1000"), new Money(getCurrency(), "1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 2, false, true,
                false, null, fees, (short) 2, RecurrenceType.MONTHLY);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

       Assert.assertEquals("Loan Product", product.getPrdOfferingName());
       Assert.assertEquals("LOAN", product.getPrdOfferingShortName());
       Assert.assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
       Assert.assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
       Assert.assertEquals(startDate, product.getStartDate());
       Assert.assertEquals(endDate, product.getEndDate());
       Assert.assertEquals("Loan Product updated", product.getDescription());
       Assert.assertEquals(GraceType.NONE, product.getGraceType());
       Assert.assertEquals(Short.valueOf("0"), product.getGracePeriodDuration());
       Assert.assertEquals(InterestType.FLAT, product.getInterestType());
       Assert.assertEquals(2.0, product.getMinInterestRate(), DELTA);
       Assert.assertEquals(12.0, product.getMaxInterestRate(), DELTA);
       Assert.assertEquals(12.0, product.getDefInterestRate(), DELTA);
        Assert.assertFalse(product.isIncludeInLoanCounter());
       Assert.assertTrue(product.isIntDedDisbursement());
        Assert.assertFalse(product.isPrinDueLastInst());
       Assert.assertEquals(2, product.getLoanOfferingFees().size());
        Assert.assertNotNull(product.getLoanOfferingMeeting());
       Assert.assertEquals(RecurrenceType.MONTHLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        Assert.assertNotNull(product.getPrincipalGLcode());
        Assert.assertNotNull(product.getInterestGLcode());
    }

    public void testLoanOfferingWithDecliningInterestDeductionAtDisbursement() {
        try {
            createIntitalObjects();
            interestTypes = new InterestTypesEntity(InterestType.DECLINING);
            Date startDate = offSetCurrentDate(0);
            Date endDate = offSetCurrentDate(2);
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11, (short) 17, false,
                    true, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertEquals("exceptions.declineinterestdisbursementdeduction", e.getKey());
        }
    }

    public void testLoanOfferingWithDecliningInterestNoDeductionAtDisbursement() throws Exception {
        createIntitalObjects();
        interestTypes = new InterestTypesEntity(InterestType.DECLINING);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(
                        getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
       Assert.assertEquals(InterestType.DECLINING, loanOffering.getInterestType());
    }

    public void testLoanOfferingWithEqualPrincipalDecliningInterestDeductionAtDisbursement() {
        try {
            createIntitalObjects();
            interestTypes = new InterestTypesEntity(InterestType.DECLINING_EPI);
            Date startDate = offSetCurrentDate(0);
            Date endDate = offSetCurrentDate(2);
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(getCurrency(), "1000"),
                    new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11, (short) 17, false,
                    true, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertEquals("exceptions.declineinterestdisbursementdeduction", e.getKey());
        }
    }

    public void testLoanOfferingWithEqualPrincipalDecliningInterestNoDeductionAtDisbursement() throws Exception {
        createIntitalObjects();
        interestTypes = new InterestTypesEntity(InterestType.DECLINING_EPI);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(
                        getCurrency(), "1000"), new Money(getCurrency(), "3000"), new Money(getCurrency(), "2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
       Assert.assertEquals(InterestType.DECLINING_EPI, loanOffering.getInterestType());
    }

    public void testPrdOfferingView() {
        PrdOfferingDto prdOfferingDto = new PrdOfferingDto();
        prdOfferingDto.setGlobalPrdOfferingNum("1234");
       Assert.assertEquals("1234", prdOfferingDto.getGlobalPrdOfferingNum());
        prdOfferingDto.setPrdOfferingId(Short.valueOf("1"));
       Assert.assertEquals(Short.valueOf("1"), prdOfferingDto.getPrdOfferingId());
        prdOfferingDto.setPrdOfferingName("name");
       Assert.assertEquals("name", prdOfferingDto.getPrdOfferingName());
    }

    private MeetingBO getMeeting() {
        return TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
    }

    private java.sql.Date offSetCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    private LoanOfferingBO createLoanOfferingBO(String prdOfferingName, String shortName) {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(WEEKLY, EVERY_WEEK,
                LOAN_INSTALLMENT, MONDAY));
        return TestObjectFactory.createLoanOffering(prdOfferingName, shortName, ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency);
    }

    private void createIntitalObjects() {
        prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        frequency = getMeeting();
        principalglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class, (short) 7);
        intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class, (short) 7);
        productCategory = TestObjectFactory.getLoanPrdCategory();
        interestTypes = new InterestTypesEntity(InterestType.FLAT);
    }

    public void testCreateLoanOfferingSameForAllLoan() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        periodicFee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, (short) 1);
        oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time", FeeCategory.LOAN, "100",
                FeePayment.UPFRONT);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(periodicFee);
        fees.add(oneTimeFee);
        LoanPrdActionForm loanPrdActionForm = new LoanPrdActionForm();
        product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                prdApplicableMaster, startDate, endDate, "1234", gracePeriodType, (short) 2, interestTypes, 12.0, 2.0,
                3.0, false, false, false, null, fees, frequency, principalglCodeEntity, intglCodeEntity,
                populateNoOfInstallSameForAllLoan("1", "12", "1", "2", populateLoanAmountSameForAllLoan("1",
                        new Double("3000"), new Double("1000"), new Double("2000"), loanPrdActionForm)));
        product.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

       Assert.assertEquals("Loan Offering", product.getPrdOfferingName());
       Assert.assertEquals("LOAP", product.getPrdOfferingShortName());
       Assert.assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
       Assert.assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
       Assert.assertEquals(startDate, product.getStartDate());
       Assert.assertEquals(endDate, product.getEndDate());
       Assert.assertEquals("1234", product.getDescription());
       Assert.assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
       Assert.assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
       Assert.assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        for (LoanAmountSameForAllLoanBO loanAmountSameForAllLoanBO : product.getLoanAmountSameForAllLoan()) {
            Assert.assertEquals(new Double("3000"), loanAmountSameForAllLoanBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("1000"), loanAmountSameForAllLoanBO.getMinLoanAmount());
           Assert.assertEquals(new Double("2000"), loanAmountSameForAllLoanBO.getDefaultLoanAmount());
        }
        for (NoOfInstallSameForAllLoanBO noofInstallSameForAllLoanBO : product.getNoOfInstallSameForAllLoan()) {
         Assert.assertEquals(new Short("12"), noofInstallSameForAllLoanBO.getMaxNoOfInstall());
         Assert.assertEquals(new Short("1"), noofInstallSameForAllLoanBO.getMinNoOfInstall());
         Assert.assertEquals(new Short("2"), noofInstallSameForAllLoanBO.getDefaultNoOfInstall());
      }
       Assert.assertEquals(2.0, product.getMinInterestRate(), DELTA);
       Assert.assertEquals(12.0, product.getMaxInterestRate(), DELTA);
       Assert.assertEquals(3.0, product.getDefInterestRate(), DELTA);
        Assert.assertFalse(product.isIncludeInLoanCounter());
        Assert.assertFalse(product.isIntDedDisbursement());
        Assert.assertFalse(product.isPrinDueLastInst());
       Assert.assertEquals(2, product.getLoanOfferingFees().size());
        Assert.assertNotNull(product.getLoanOfferingMeeting());
       Assert.assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        Assert.assertNotNull(product.getPrincipalGLcode());
        Assert.assertNotNull(product.getInterestGLcode());
    }

    public void testCreateLoanOfferingByLastLoanAmount() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        periodicFee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, (short) 1);
        oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time", FeeCategory.LOAN, "100",
                FeePayment.UPFRONT);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(periodicFee);
        fees.add(oneTimeFee);
        LoanPrdActionForm loanPrdActionForm = new LoanPrdActionForm();
        product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                prdApplicableMaster, startDate, endDate, "1234", gracePeriodType, (short) 2, interestTypes, 12.0, 2.0,
                3.0, false, false, false, null, fees, frequency, principalglCodeEntity, intglCodeEntity,
                LoanOfferingTestUtils.populateNoOfInstallFromLastLoanAmount("2", new Integer("0"), new Integer("1000"), new Integer("1001"),
                        new Integer("2000"), new Integer("2001"), new Integer("3000"), new Integer("3001"),
                        new Integer("4000"), new Integer("4001"), new Integer("5000"), new Integer("5001"),
                        new Integer("6000"), "10", "30", "20", "20", "40", "30", "30", "50", "40", "40", "60", "50",
                        "50", "70", "60", "60", "80", "70", LoanOfferingTestUtils.populateLoanAmountFromLastLoanAmount("2", new Integer("0"),
                                new Integer("1000"), new Integer("1001"), new Integer("2000"), new Integer("2001"),
                                new Integer("3000"), new Integer("3001"), new Integer("4000"), new Integer("4001"),
                                new Integer("5000"), new Integer("5001"), new Integer("6000"), new Double("1000"),
                                new Double("3000"), new Double("2000"), new Double("2000"), new Double("4000"),
                                new Double("3000"), new Double("3000"), new Double("5000"), new Double("4000"),
                                new Double("4000"), new Double("6000"), new Double("5000"), new Double("5000"),
                                new Double("7000"), new Double("6000"), new Double("6000"), new Double("8000"),
                                new Double("7000"), loanPrdActionForm)));
        product.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

       Assert.assertEquals("Loan Offering", product.getPrdOfferingName());
       Assert.assertEquals("LOAP", product.getPrdOfferingShortName());
       Assert.assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
       Assert.assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
       Assert.assertEquals(startDate, product.getStartDate());
       Assert.assertEquals(endDate, product.getEndDate());
       Assert.assertEquals("1234", product.getDescription());
       Assert.assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
       Assert.assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
       Assert.assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        for (Iterator<LoanAmountFromLastLoanAmountBO> itr = product.getLoanAmountFromLastLoan().iterator(); itr
                .hasNext();) {
            LoanAmountFromLastLoanAmountBO loanAmountFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("0"), loanAmountFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("1000"), loanAmountFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("1000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
           Assert.assertEquals(new Double("2000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("1001"), loanAmountFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("2000"), loanAmountFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("2000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
           Assert.assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("2001"), loanAmountFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
           Assert.assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("3001"), loanAmountFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
           Assert.assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("4001"), loanAmountFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Double("7000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
           Assert.assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("5001"), loanAmountFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Double("8000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
           Assert.assertEquals(new Double("7000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
        }
        for (Iterator<NoOfInstallFromLastLoanAmountBO> itr = product.getNoOfInstallFromLastLoan().iterator(); itr
                .hasNext();) {
            NoOfInstallFromLastLoanAmountBO noOfInstallFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("0"), noOfInstallFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("1000"), noOfInstallFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Short("30"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("10"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("20"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("1001"), noOfInstallFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("2000"), noOfInstallFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Short("40"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("20"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("30"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("2001"), noOfInstallFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("3000"), noOfInstallFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Short("50"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("30"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("40"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("3001"), noOfInstallFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("4000"), noOfInstallFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Short("60"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("40"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("50"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("4001"), noOfInstallFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("5000"), noOfInstallFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Short("70"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("50"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("60"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
           Assert.assertEquals(new Double("5001"), noOfInstallFromLastLoanAmountBO.getStartRange());
           Assert.assertEquals(new Double("6000"), noOfInstallFromLastLoanAmountBO.getEndRange());
           Assert.assertEquals(new Short("80"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("60"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("70"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
        }
       Assert.assertEquals(2.0, product.getMinInterestRate(), DELTA);
       Assert.assertEquals(12.0, product.getMaxInterestRate(), DELTA);
       Assert.assertEquals(3.0, product.getDefInterestRate(), DELTA);
        Assert.assertFalse(product.isIncludeInLoanCounter());
        Assert.assertFalse(product.isIntDedDisbursement());
        Assert.assertFalse(product.isPrinDueLastInst());
       Assert.assertEquals(2, product.getLoanOfferingFees().size());
        Assert.assertNotNull(product.getLoanOfferingMeeting());
       Assert.assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        Assert.assertNotNull(product.getPrincipalGLcode());
        Assert.assertNotNull(product.getInterestGLcode());
    }

    public void testCreateLoanOfferingFromLoanCycle() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.GRACEONALLREPAYMENTS);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        periodicFee = TestObjectFactory.createPeriodicAmountFee("Loan Periodic", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, (short) 1);
        oneTimeFee = TestObjectFactory.createOneTimeAmountFee("Loan One time", FeeCategory.LOAN, "100",
                FeePayment.UPFRONT);
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(periodicFee);
        fees.add(oneTimeFee);
        LoanPrdActionForm loanPrdActionForm = new LoanPrdActionForm();
        product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                prdApplicableMaster, startDate, endDate, "1234", gracePeriodType, (short) 2, interestTypes, 12.0, 2.0,
                3.0, false, false, false, null, fees, frequency, principalglCodeEntity, intglCodeEntity,
                LoanOfferingTestUtils.populateNoOfInstallFromLoanCycle("3", "10", "30", "20", "20", "40", "30", "30", "50", "40", "40", "60",
                        "50", "50", "70", "60", "60", "80", "70", LoanOfferingTestUtils.populateLoanAmountFromLoanCycle("3", new Double(
                                "1000"), new Double("3000"), new Double("2000"), new Double("2000"),
                                new Double("4000"), new Double("3000"), new Double("3000"), new Double("5000"),
                                new Double("4000"), new Double("4000"), new Double("6000"), new Double("5000"),
                                new Double("5000"), new Double("7000"), new Double("6000"), new Double("6000"),
                                new Double("8000"), new Double("7000"), loanPrdActionForm)));
        product.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

       Assert.assertEquals("Loan Offering", product.getPrdOfferingName());
       Assert.assertEquals("LOAP", product.getPrdOfferingShortName());
       Assert.assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
       Assert.assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
       Assert.assertEquals(startDate, product.getStartDate());
       Assert.assertEquals(endDate, product.getEndDate());
       Assert.assertEquals("1234", product.getDescription());
       Assert.assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
       Assert.assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
       Assert.assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        for (Iterator<LoanAmountFromLoanCycleBO> itr = product.getLoanAmountFromLoanCycle().iterator(); itr.hasNext();) {
            LoanAmountFromLoanCycleBO loanAmountFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Double("3000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("1000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
           Assert.assertEquals(new Double("2000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Double("4000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("2000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
           Assert.assertEquals(new Double("3000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Double("5000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("3000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
           Assert.assertEquals(new Double("4000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Double("6000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("4000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
           Assert.assertEquals(new Double("5000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Double("7000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("5000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
           Assert.assertEquals(new Double("6000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Double("8000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
           Assert.assertEquals(new Double("6000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
           Assert.assertEquals(new Double("7000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
        }
        for (Iterator<NoOfInstallFromLoanCycleBO> itr = product.getNoOfInstallFromLoanCycle().iterator(); itr.hasNext();) {
            NoOfInstallFromLoanCycleBO noOfInstallFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Short("30"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("10"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("20"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Short("40"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("20"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("30"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Short("50"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("30"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("40"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Short("60"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("40"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("50"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Short("70"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("50"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("60"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
           Assert.assertEquals(new Short("80"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
           Assert.assertEquals(new Short("60"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
           Assert.assertEquals(new Short("70"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());

        }
       Assert.assertEquals(2.0, product.getMinInterestRate(), DELTA);
       Assert.assertEquals(12.0, product.getMaxInterestRate(), DELTA);
       Assert.assertEquals(3.0, product.getDefInterestRate(), DELTA);
        Assert.assertFalse(product.isIncludeInLoanCounter());
        Assert.assertFalse(product.isIntDedDisbursement());
        Assert.assertFalse(product.isPrinDueLastInst());
       Assert.assertEquals(2, product.getLoanOfferingFees().size());
        Assert.assertNotNull(product.getLoanOfferingMeeting());
       Assert.assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        Assert.assertNotNull(product.getPrincipalGLcode());
        Assert.assertNotNull(product.getInterestGLcode());
    }

    private LoanPrdActionForm populateLoanPrdActionForm(String loanAmtCalcType, String calcInstallmentType,
            Double maxLoanAmount, Double minLoanAmount, Double defLoanAmount, String maxNoOfInstall,
            String minNoOfInstall, String defNoOfInstall) {
        LoanPrdActionForm loanPrdActionForm = new LoanPrdActionForm();
        if (loanAmtCalcType.equals("1")) {
            loanPrdActionForm.setLoanAmtCalcType(loanAmtCalcType);
            loanPrdActionForm.setCalcInstallmentType(calcInstallmentType);
            loanPrdActionForm.setMaxLoanAmount(maxLoanAmount.toString());
            loanPrdActionForm.setMinLoanAmount(minLoanAmount.toString());
            loanPrdActionForm.setDefaultLoanAmount(defLoanAmount.toString());
            loanPrdActionForm.setMaxNoInstallments(maxNoOfInstall);
            loanPrdActionForm.setMinNoInstallments(minNoOfInstall);
            loanPrdActionForm.setDefNoInstallments(defNoOfInstall);
        }
        return loanPrdActionForm;
    }

    private LoanPrdActionForm populateLoanAmountSameForAllLoan(String loanAmtCalcType, Double maxLoanAmount,
            Double minLoanAmount, Double defLoanAmount, LoanPrdActionForm loanPrdActionForm) {
        if (loanAmtCalcType.equals("1")) {
            loanPrdActionForm.setLoanAmtCalcType(loanAmtCalcType);
            loanPrdActionForm.setMaxLoanAmount(maxLoanAmount.toString());
            loanPrdActionForm.setMinLoanAmount(minLoanAmount.toString());
            loanPrdActionForm.setDefaultLoanAmount(defLoanAmount.toString());
        }
        return loanPrdActionForm;
    }

    private LoanPrdActionForm populateNoOfInstallSameForAllLoan(String calcInstallmentType, String maxNoOfInstall,
            String minNoOfInstall, String defNoOfInstall, LoanPrdActionForm loanPrdActionForm) {
        if (calcInstallmentType.equals("1")) {
            loanPrdActionForm.setCalcInstallmentType(calcInstallmentType);
            loanPrdActionForm.setMaxNoInstallments(maxNoOfInstall);
            loanPrdActionForm.setMinNoInstallments(minNoOfInstall);
            loanPrdActionForm.setDefNoInstallments(defNoOfInstall);

        }
        return loanPrdActionForm;
    }

}
