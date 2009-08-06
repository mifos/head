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

package org.mifos.application.productdefinition.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyFactory;
import org.mifos.framework.util.helpers.TestObjectFactory;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

public class LoanOfferingBOIntegrationTest extends MifosIntegrationTestCase {

    public LoanOfferingBOIntegrationTest() throws SystemException, ApplicationException {
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

    public static void setStatus(LoanOfferingBO loanOffering, PrdStatusEntity prdStatus) {
        loanOffering.setPrdStatus(prdStatus);
    }

    public static void setGracePeriodType(LoanOfferingBO loanOffering, GracePeriodTypeEntity gracePeriodType) {
        loanOffering.setGracePeriodType(gracePeriodType);
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
        assertEquals(2, auditLogList.size());
        assertEquals(EntityType.LOANPRODUCT.getValue(), auditLogList.get(0).getEntityType());
        assertEquals(13, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Fee Types")
                    && auditLogRecord.getNewValue().equalsIgnoreCase("Loan Periodic")) {
                assertEquals("-", auditLogRecord.getOldValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Fee Types")
                    && auditLogRecord.getNewValue().equalsIgnoreCase("Loan One time")) {
                assertEquals("Loan Periodic", auditLogRecord.getOldValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Service Charge deducted At Disbursement")) {
                assertEquals("1", auditLogRecord.getOldValue());
                assertEquals("0", auditLogRecord.getNewValue());
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
        assertEquals(1, auditLogList.size());
        assertEquals(EntityType.LOANPRODUCT.getValue(), auditLogList.get(0).getEntityType());
        assertEquals(14, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Min Loan Amount")) {
                assertEquals("300.0", auditLogRecord.getOldValue());
                assertEquals("1000.0", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Description")) {
                assertEquals("-", auditLogRecord.getOldValue());
                assertEquals("Loan Product updated", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Applicable For")) {
                assertEquals("Groups", auditLogRecord.getOldValue());
                assertEquals("Clients", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Frequency Of Installments")) {
                assertEquals("Week(s)", auditLogRecord.getOldValue());
                assertEquals("Month(s)", auditLogRecord.getNewValue());
            }
        }
        TestObjectFactory.cleanUpChangeLog();

    }

    public void testBuildloanOfferingWithoutDataForMandatoryFields() {
        try {
            new LoanOfferingBO(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, false, false, false, null, null, null);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithoutDataForAllFields() {
        try {
            new LoanOfferingBO(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, false, false, false, null, null, null, null, null);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithoutName() throws SystemException, ApplicationException {
        createIntitalObjects();
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), null, "S", productCategory, prdApplicableMaster,
                    new Date(System.currentTimeMillis()), interestTypes, new Money("1000"), new Money("3000"), 12.0,
                    2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithShortNameGreaterThanFourDig() throws SystemException, ApplicationException {
        createIntitalObjects();
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOANOFF", productCategory,
                    prdApplicableMaster, new Date(System.currentTimeMillis()), interestTypes, new Money("1000"),
                    new Money("3000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false,
                    frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithStartDateLessThanCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(-2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAN", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money("1000"), new Money("3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildWithStartDateEqualToCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        LoanOfferingBO product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAN",
                productCategory, prdApplicableMaster, startDate, interestTypes, new Money("1000"), new Money("3000"),
                12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, true, false, frequency,
                principalglCodeEntity, intglCodeEntity);
        assertNotNull(product.getGlobalPrdOfferingNum());
        assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
    }

    public void testStartDateGreaterThanCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(2);
        LoanOfferingBO product = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAN",
                productCategory, prdApplicableMaster, startDate, interestTypes, new Money("1000"), new Money("3000"),
                12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, true, false, frequency,
                principalglCodeEntity, intglCodeEntity);
        assertNotNull(product.getGlobalPrdOfferingNum());
        assertEquals(PrdStatus.LOAN_INACTIVE, product.getStatus());

    }

    public void testBuildloanOfferingWithDuplicatePrdOfferingName() throws SystemException, ApplicationException {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Product", "LOAN", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money("1000"), new Money("3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithDuplicatePrdOfferingShortName() throws SystemException, ApplicationException {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money("1000"), new Money("3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithEndDateLessThanStartDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(-2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), null, 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false,
                    null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithNoInterestTypes() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Product", "LOAN", productCategory,
                    prdApplicableMaster, startDate, null, new Money("1000"), new Money("3000"), 12.0, 2.0, 3.0,
                    (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithNoMaxAmount() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money("1000.0"), null, 12.0, 2.0, 3.0,
                    (short) 20, (short) 1, (short) 12, false, false, false, frequency, principalglCodeEntity,
                    intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildloanOfferingWithoutGLCode() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, interestTypes, new Money("1000.0"), new Money("3000"), 12.0, 2.0,
                    3.0, (short) 20, (short) 1, (short) 12, false, false, false, frequency, null, null);
            fail();
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
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), new Money("200.0"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testMinAmountGreaterThanMaxAmount() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("10000"),
                    new Money("3000"), null, 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false, false,
                    null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testDefInterestRateNotBetweenMinMaxRates() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), new Money("2000.0"), 12.0, 2.0, 13.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testMinInterestRateGretaterThanMaxRate() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), new Money("2000.0"), 12.0, 20.0, 13.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testMinInstallmentsGreaterThanMaxInstallments() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 31, (short) 21, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testDefInstallmentsNotBetweenMinMaxInstallments() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11, (short) 7, false,
                    false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testGracePeriodForIntDedAtDisb() throws ProductDefinitionException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(
                        "1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, true, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
        assertNotNull(loanOffering.getGracePeriodType());
        assertNotNull(loanOffering.getGracePeriodDuration());
        assertEquals(GraceType.NONE.getValue(), loanOffering.getGracePeriodType().getId());
        assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
    }

    public void testNullGracePeriodDurationWithGraceType() throws ProductDefinitionException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.PRINCIPALONLYGRACE);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, gracePeriodType, null, interestTypes, new Money(
                            "1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                    (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
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
                new Money("1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
        assertNotNull(loanOffering.getGracePeriodDuration());
        assertEquals(GraceType.NONE.getValue(), loanOffering.getGracePeriodType().getId());
        assertEquals(Short.valueOf("0"), loanOffering.getGracePeriodDuration());
    }

    public void testFeeNotMatchingFrequencyOfLoanOffering() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), intglCodeEntity, new Money(
                "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, EVERY_MONTH,
                CUSTOMER_MEETING, MONDAY)));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        try {
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, gracePeriodType, null, interestTypes, new Money(
                            "1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                    (short) 17, false, false, false, null, fees, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
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
                "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting()));
        FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), intglCodeEntity, new Money(
                "100"), false, new FeePaymentEntity(FeePayment.UPFRONT));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        fees.add(fee1);
        List<FundBO> funds = new ArrayList<FundBO>();
        funds.add(new FundBO());
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, gracePeriodType, (short) 2,
                interestTypes, new Money("1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20,
                (short) 11, (short) 17, false, false, false, funds, fees, frequency, principalglCodeEntity,
                intglCodeEntity);
        assertEquals(2, loanOffering.getLoanOfferingFees().size());
        assertEquals(1, loanOffering.getLoanOfferingFunds().size());
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
                        "1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, fees, frequency, principalglCodeEntity, intglCodeEntity,
                ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN.toString(),
                ProductDefinitionConstants.NOOFINSTALLSAMEFORALLLOAN.toString());
        product.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

        assertEquals("Loan Offering", product.getPrdOfferingName());
        assertEquals("LOAP", product.getPrdOfferingShortName());
        assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
        assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
        assertEquals(startDate, product.getStartDate());
        assertEquals(endDate, product.getEndDate());
        assertEquals("1234", product.getDescription());
        assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
        assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
        assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        LoanAmountOption eligibleLoanAmount = product.eligibleLoanAmount(null, null);
        assertEquals(new Money("1000").getAmountDoubleValue(), eligibleLoanAmount.getMinLoanAmount());
        assertEquals(new Money("3000").getAmountDoubleValue(), eligibleLoanAmount.getMaxLoanAmount());
        assertEquals(new Money("2000").getAmountDoubleValue(), eligibleLoanAmount.getDefaultLoanAmount());
        assertEquals(2.0, product.getMinInterestRate(), DELTA);
        assertEquals(12.0, product.getMaxInterestRate(), DELTA);
        assertEquals(3.0, product.getDefInterestRate(), DELTA);
        LoanOfferingInstallmentRange eligibleNoOfInstall = product.eligibleNoOfInstall(null, null);
        assertEquals(Short.valueOf("11"), eligibleNoOfInstall.getMinNoOfInstall());
        assertEquals(Short.valueOf("20"), eligibleNoOfInstall.getMaxNoOfInstall());
        assertEquals(Short.valueOf("17"), eligibleNoOfInstall.getDefaultNoOfInstall());
        assertFalse(product.isIncludeInLoanCounter());
        assertFalse(product.isIntDedDisbursement());
        assertFalse(product.isPrinDueLastInst());
        assertEquals(2, product.getLoanOfferingFees().size());
        assertNotNull(product.getLoanOfferingMeeting());
        assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        assertNotNull(product.getPrincipalGLcode());
        assertNotNull(product.getInterestGLcode());
    }

    public void testUpdateloanOfferingWithoutDataForMandatoryFields() {
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, false, false, false, null, null, null, null);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithoutName() throws SystemException, ApplicationException {
        createIntitalObjects();
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, null, "S", productCategory, prdApplicableMaster, new Date(System
                    .currentTimeMillis()), null, "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes,
                    (short) 0, new Money("3000"), new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20,
                    (short) 1, (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithShortNameGreaterThanFourDig() throws SystemException, ApplicationException {
        createIntitalObjects();
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOANS", productCategory, prdApplicableMaster, new Date(System
                    .currentTimeMillis()), null, "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes,
                    (short) 0, new Money("3000"), new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20,
                    (short) 1, (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testupdateloanOfferingWithStartDateLessThanCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        product = createLoanOfferingBO("Loan Product", "LOAP");
        Date startDate = offSetCurrentDate(-2);
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                    new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithStartDateEqualToCurrentDate() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false, false,
                false, null, null, (short) 2, RecurrenceType.WEEKLY);
        StaticHibernateUtil.commitTransaction();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());

    }

    public void testUpdateloanOfferingWithStartDateGreaterThanCurrentDate() throws SystemException,
            ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                    new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                            "3000"), new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
                    (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                            "3000"), new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1,
                    (short) 12, false, false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                    new Money("1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateloanOfferingWithNoInterestTypes() throws SystemException, ApplicationException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, null, (short) 0, new Money("3000"), new Money(
                            "1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                            "1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                            "1000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
        } catch (ProductDefinitionException e) {
            assertTrue(true);
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
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("1000"),
                    new Money("3000"), new Money("2000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("1000"),
                    new Money("3000"), new Money("1000"), 12.0, 2.0, 3.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateDefInterestRateNotBetweenMinMaxRates() {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, null,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                    new Money("1000"), new Money("1000"), 12.0, 2.0, 13.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                    new Money("1000"), new Money("1000"), 12.0, 22.0, 12.0, (short) 20, (short) 1, (short) 12, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                    new Money("1000"), new Money("1000"), 12.0, 2.0, 12.0, (short) 2, (short) 12, (short) 2, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                    new Money("1000"), new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 22, false,
                    false, false, null, null, (short) 2, RecurrenceType.WEEKLY);
            fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testUpdateGracePeriodForIntDedAtDisb() throws ProductDefinitionException {
        createIntitalObjects();
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                new Money("1000"), new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 2, false, true,
                false, null, null, (short) 2, RecurrenceType.WEEKLY);
        StaticHibernateUtil.commitTransaction();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

        assertNotNull(product.getGracePeriodType());
        assertNotNull(product.getGracePeriodDuration());
        assertEquals(GraceType.NONE.getValue(), product.getGracePeriodType().getId());
        assertEquals(Short.valueOf("0"), product.getGracePeriodDuration());
    }

    public void testUpdateFeeNotMatchingFrequencyOfLoanOffering() throws ProductDefinitionException, FeeException {
        createIntitalObjects();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        FeeBO fee = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), intglCodeEntity, new Money(
                "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY, EVERY_MONTH,
                CUSTOMER_MEETING, MONDAY)));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        product = createLoanOfferingBO("Loan Product", "LOAP");
        try {
            product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                    "Loan Product updated", PrdStatus.LOAN_ACTIVE, gracePeriodType, interestTypes, (short) 0,
                    new Money("3000"), new Money("1000"), new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
                    (short) 2, false, true, false, null, fees, (short) 2, RecurrenceType.WEEKLY);
            fail();
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
                "100"), false, TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting()));
        FeeBO fee1 = new AmountFeeBO(TestObjectFactory.getContext(), "Loan Periodic", new CategoryTypeEntity(
                FeeCategory.LOAN), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), intglCodeEntity, new Money(
                "100"), false, new FeePaymentEntity(FeePayment.UPFRONT));
        List<FeeBO> fees = new ArrayList<FeeBO>();
        fees.add(fee);
        fees.add(fee1);
        List<FundBO> funds = new ArrayList<FundBO>();
        funds.add(new FundBO());
        product = createLoanOfferingBO("Loan Product", "LOAP");
        product.update((short) 1, "Loan Product", "LOAN", productCategory, prdApplicableMaster, startDate, endDate,
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, gracePeriodType, interestTypes, (short) 0, new Money(
                        "3000"), new Money("1000"), new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1,
                (short) 2, false, true, false, funds, fees, (short) 2, RecurrenceType.WEEKLY);
        assertEquals(2, product.getLoanOfferingFees().size());
        assertEquals(1, product.getLoanOfferingFunds().size());
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
                "Loan Product updated", PrdStatus.LOAN_ACTIVE, null, interestTypes, (short) 0, new Money("3000"),
                new Money("1000"), new Money("1000"), 12.0, 2.0, 12.0, (short) 12, (short) 1, (short) 2, false, true,
                false, null, fees, (short) 2, RecurrenceType.MONTHLY);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

        assertEquals("Loan Product", product.getPrdOfferingName());
        assertEquals("LOAN", product.getPrdOfferingShortName());
        assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
        assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
        assertEquals(startDate, product.getStartDate());
        assertEquals(endDate, product.getEndDate());
        assertEquals("Loan Product updated", product.getDescription());
        assertEquals(GraceType.NONE, product.getGraceType());
        assertEquals(Short.valueOf("0"), product.getGracePeriodDuration());
        assertEquals(InterestType.FLAT, product.getInterestType());
        assertEquals(2.0, product.getMinInterestRate(), DELTA);
        assertEquals(12.0, product.getMaxInterestRate(), DELTA);
        assertEquals(12.0, product.getDefInterestRate(), DELTA);
        assertFalse(product.isIncludeInLoanCounter());
        assertTrue(product.isIntDedDisbursement());
        assertFalse(product.isPrinDueLastInst());
        assertEquals(2, product.getLoanOfferingFees().size());
        assertNotNull(product.getLoanOfferingMeeting());
        assertEquals(RecurrenceType.MONTHLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        assertNotNull(product.getPrincipalGLcode());
        assertNotNull(product.getInterestGLcode());
    }

    public void testLoanOfferingWithDecliningInterestDeductionAtDisbursement() {
        try {
            createIntitalObjects();
            interestTypes = new InterestTypesEntity(InterestType.DECLINING);
            Date startDate = offSetCurrentDate(0);
            Date endDate = offSetCurrentDate(2);
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11, (short) 17, false,
                    true, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
            assertEquals("exceptions.declineinterestdisbursementdeduction", e.getKey());
        }
    }

    public void testLoanOfferingWithDecliningInterestNoDeductionAtDisbursement() throws Exception {
        createIntitalObjects();
        interestTypes = new InterestTypesEntity(InterestType.DECLINING);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(
                        "1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
        assertEquals(InterestType.DECLINING, loanOffering.getInterestType());
    }

    public void testLoanOfferingWithEqualPrincipalDecliningInterestDeductionAtDisbursement() {
        try {
            createIntitalObjects();
            interestTypes = new InterestTypesEntity(InterestType.DECLINING_EPI);
            Date startDate = offSetCurrentDate(0);
            Date endDate = offSetCurrentDate(2);
            new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP", productCategory,
                    prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money("1000"),
                    new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11, (short) 17, false,
                    true, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
            fail();
        } catch (ProductDefinitionException e) {
            assertEquals("exceptions.declineinterestdisbursementdeduction", e.getKey());
        }
    }

    public void testLoanOfferingWithEqualPrincipalDecliningInterestNoDeductionAtDisbursement() throws Exception {
        createIntitalObjects();
        interestTypes = new InterestTypesEntity(InterestType.DECLINING_EPI);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(2);
        LoanOfferingBO loanOffering = new LoanOfferingBO(TestObjectFactory.getContext(), "Loan Offering", "LOAP",
                productCategory, prdApplicableMaster, startDate, endDate, null, null, null, interestTypes, new Money(
                        "1000"), new Money("3000"), new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
                (short) 17, false, false, false, null, null, frequency, principalglCodeEntity, intglCodeEntity);
        assertEquals(InterestType.DECLINING_EPI, loanOffering.getInterestType());
    }

    public void testPrdOfferingView() {
        PrdOfferingView prdOfferingView = new PrdOfferingView();
        prdOfferingView.setGlobalPrdOfferingNum("1234");
        assertEquals("1234", prdOfferingView.getGlobalPrdOfferingNum());
        prdOfferingView.setPrdOfferingId(Short.valueOf("1"));
        assertEquals(Short.valueOf("1"), prdOfferingView.getPrdOfferingId());
        prdOfferingView.setPrdOfferingName("name");
        assertEquals("name", prdOfferingView.getPrdOfferingName());
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

        assertEquals("Loan Offering", product.getPrdOfferingName());
        assertEquals("LOAP", product.getPrdOfferingShortName());
        assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
        assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
        assertEquals(startDate, product.getStartDate());
        assertEquals(endDate, product.getEndDate());
        assertEquals("1234", product.getDescription());
        assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
        assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
        assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        for (Iterator<LoanAmountSameForAllLoanBO> itr = product.getLoanAmountSameForAllLoan().iterator(); itr.hasNext();) {
            LoanAmountSameForAllLoanBO loanAmountSameForAllLoanBO = itr.next();
            assertEquals(new Double("3000"), loanAmountSameForAllLoanBO.getMaxLoanAmount());
            assertEquals(new Double("1000"), loanAmountSameForAllLoanBO.getMinLoanAmount());
            assertEquals(new Double("2000"), loanAmountSameForAllLoanBO.getDefaultLoanAmount());
        }
        for (Iterator<NoOfInstallSameForAllLoanBO> itr = product.getNoOfInstallSameForAllLoan().iterator(); itr
                .hasNext();) {
            NoOfInstallSameForAllLoanBO noofInstallSameForAllLoanBO = itr.next();
            assertEquals(new Short("12"), noofInstallSameForAllLoanBO.getMaxNoOfInstall());
            assertEquals(new Short("1"), noofInstallSameForAllLoanBO.getMinNoOfInstall());
            assertEquals(new Short("2"), noofInstallSameForAllLoanBO.getDefaultNoOfInstall());
        }
        assertEquals(2.0, product.getMinInterestRate(), DELTA);
        assertEquals(12.0, product.getMaxInterestRate(), DELTA);
        assertEquals(3.0, product.getDefInterestRate(), DELTA);
        assertFalse(product.isIncludeInLoanCounter());
        assertFalse(product.isIntDedDisbursement());
        assertFalse(product.isPrinDueLastInst());
        assertEquals(2, product.getLoanOfferingFees().size());
        assertNotNull(product.getLoanOfferingMeeting());
        assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        assertNotNull(product.getPrincipalGLcode());
        assertNotNull(product.getInterestGLcode());
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
                populateNoOfInstallFromLastLoanAmount("2", new Integer("0"), new Integer("1000"), new Integer("1001"),
                        new Integer("2000"), new Integer("2001"), new Integer("3000"), new Integer("3001"),
                        new Integer("4000"), new Integer("4001"), new Integer("5000"), new Integer("5001"),
                        new Integer("6000"), "10", "30", "20", "20", "40", "30", "30", "50", "40", "40", "60", "50",
                        "50", "70", "60", "60", "80", "70", populateLoanAmountFromLastLoanAmount("2", new Integer("0"),
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

        assertEquals("Loan Offering", product.getPrdOfferingName());
        assertEquals("LOAP", product.getPrdOfferingShortName());
        assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
        assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
        assertEquals(startDate, product.getStartDate());
        assertEquals(endDate, product.getEndDate());
        assertEquals("1234", product.getDescription());
        assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
        assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
        assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        for (Iterator<LoanAmountFromLastLoanAmountBO> itr = product.getLoanAmountFromLastLoan().iterator(); itr
                .hasNext();) {
            LoanAmountFromLastLoanAmountBO loanAmountFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("0"), loanAmountFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("1000"), loanAmountFromLastLoanAmountBO.getEndRange());
            assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
            assertEquals(new Double("1000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
            assertEquals(new Double("2000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("1001"), loanAmountFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("2000"), loanAmountFromLastLoanAmountBO.getEndRange());
            assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
            assertEquals(new Double("2000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
            assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("2001"), loanAmountFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getEndRange());
            assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
            assertEquals(new Double("3000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
            assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("3001"), loanAmountFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getEndRange());
            assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
            assertEquals(new Double("4000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
            assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("4001"), loanAmountFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getEndRange());
            assertEquals(new Double("7000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
            assertEquals(new Double("5000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
            assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
            loanAmountFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("5001"), loanAmountFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getEndRange());
            assertEquals(new Double("8000"), loanAmountFromLastLoanAmountBO.getMaxLoanAmount());
            assertEquals(new Double("6000"), loanAmountFromLastLoanAmountBO.getMinLoanAmount());
            assertEquals(new Double("7000"), loanAmountFromLastLoanAmountBO.getDefaultLoanAmount());
        }
        for (Iterator<NoOfInstallFromLastLoanAmountBO> itr = product.getNoOfInstallFromLastLoan().iterator(); itr
                .hasNext();) {
            NoOfInstallFromLastLoanAmountBO noOfInstallFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("0"), noOfInstallFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("1000"), noOfInstallFromLastLoanAmountBO.getEndRange());
            assertEquals(new Short("30"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
            assertEquals(new Short("10"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
            assertEquals(new Short("20"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("1001"), noOfInstallFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("2000"), noOfInstallFromLastLoanAmountBO.getEndRange());
            assertEquals(new Short("40"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
            assertEquals(new Short("20"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
            assertEquals(new Short("30"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("2001"), noOfInstallFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("3000"), noOfInstallFromLastLoanAmountBO.getEndRange());
            assertEquals(new Short("50"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
            assertEquals(new Short("30"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
            assertEquals(new Short("40"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("3001"), noOfInstallFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("4000"), noOfInstallFromLastLoanAmountBO.getEndRange());
            assertEquals(new Short("60"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
            assertEquals(new Short("40"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
            assertEquals(new Short("50"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("4001"), noOfInstallFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("5000"), noOfInstallFromLastLoanAmountBO.getEndRange());
            assertEquals(new Short("70"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
            assertEquals(new Short("50"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
            assertEquals(new Short("60"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
            noOfInstallFromLastLoanAmountBO = itr.next();
            assertEquals(new Double("5001"), noOfInstallFromLastLoanAmountBO.getStartRange());
            assertEquals(new Double("6000"), noOfInstallFromLastLoanAmountBO.getEndRange());
            assertEquals(new Short("80"), noOfInstallFromLastLoanAmountBO.getMaxNoOfInstall());
            assertEquals(new Short("60"), noOfInstallFromLastLoanAmountBO.getMinNoOfInstall());
            assertEquals(new Short("70"), noOfInstallFromLastLoanAmountBO.getDefaultNoOfInstall());
        }
        assertEquals(2.0, product.getMinInterestRate(), DELTA);
        assertEquals(12.0, product.getMaxInterestRate(), DELTA);
        assertEquals(3.0, product.getDefInterestRate(), DELTA);
        assertFalse(product.isIncludeInLoanCounter());
        assertFalse(product.isIntDedDisbursement());
        assertFalse(product.isPrinDueLastInst());
        assertEquals(2, product.getLoanOfferingFees().size());
        assertNotNull(product.getLoanOfferingMeeting());
        assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        assertNotNull(product.getPrincipalGLcode());
        assertNotNull(product.getInterestGLcode());
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
                populateNoOfInstallFromLoanCycle("3", "10", "30", "20", "20", "40", "30", "30", "50", "40", "40", "60",
                        "50", "50", "70", "60", "60", "80", "70", populateLoanAmountFromLoanCycle("3", new Double(
                                "1000"), new Double("3000"), new Double("2000"), new Double("2000"),
                                new Double("4000"), new Double("3000"), new Double("3000"), new Double("5000"),
                                new Double("4000"), new Double("4000"), new Double("6000"), new Double("5000"),
                                new Double("5000"), new Double("7000"), new Double("6000"), new Double("6000"),
                                new Double("8000"), new Double("7000"), loanPrdActionForm)));
        product.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());

        assertEquals("Loan Offering", product.getPrdOfferingName());
        assertEquals("LOAP", product.getPrdOfferingShortName());
        assertEquals(Short.valueOf("1"), product.getPrdCategory().getProductCategoryID());
        assertEquals(ApplicableTo.CLIENTS, product.getPrdApplicableMasterEnum());
        assertEquals(startDate, product.getStartDate());
        assertEquals(endDate, product.getEndDate());
        assertEquals("1234", product.getDescription());
        assertEquals(GraceType.GRACEONALLREPAYMENTS.getValue(), product.getGracePeriodType().getId());
        assertEquals(Short.valueOf("2"), product.getGracePeriodDuration());
        assertEquals(InterestType.FLAT.getValue(), product.getInterestTypes().getId());
        for (Iterator<LoanAmountFromLoanCycleBO> itr = product.getLoanAmountFromLoanCycle().iterator(); itr.hasNext();) {
            LoanAmountFromLoanCycleBO loanAmountFromLoanCycleBO = itr.next();
            assertEquals(new Double("3000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
            assertEquals(new Double("1000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
            assertEquals(new Double("2000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
            assertEquals(new Double("4000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
            assertEquals(new Double("2000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
            assertEquals(new Double("3000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
            assertEquals(new Double("5000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
            assertEquals(new Double("3000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
            assertEquals(new Double("4000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
            assertEquals(new Double("6000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
            assertEquals(new Double("4000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
            assertEquals(new Double("5000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
            assertEquals(new Double("7000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
            assertEquals(new Double("5000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
            assertEquals(new Double("6000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
            loanAmountFromLoanCycleBO = itr.next();
            assertEquals(new Double("8000"), loanAmountFromLoanCycleBO.getMaxLoanAmount());
            assertEquals(new Double("6000"), loanAmountFromLoanCycleBO.getMinLoanAmount());
            assertEquals(new Double("7000"), loanAmountFromLoanCycleBO.getDefaultLoanAmount());
        }
        for (Iterator<NoOfInstallFromLoanCycleBO> itr = product.getNoOfInstallFromLoanCycle().iterator(); itr.hasNext();) {
            NoOfInstallFromLoanCycleBO noOfInstallFromLoanCycleBO = itr.next();
            assertEquals(new Short("30"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
            assertEquals(new Short("10"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
            assertEquals(new Short("20"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
            assertEquals(new Short("40"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
            assertEquals(new Short("20"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
            assertEquals(new Short("30"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
            assertEquals(new Short("50"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
            assertEquals(new Short("30"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
            assertEquals(new Short("40"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
            assertEquals(new Short("60"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
            assertEquals(new Short("40"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
            assertEquals(new Short("50"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
            assertEquals(new Short("70"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
            assertEquals(new Short("50"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
            assertEquals(new Short("60"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());
            noOfInstallFromLoanCycleBO = itr.next();
            assertEquals(new Short("80"), noOfInstallFromLoanCycleBO.getMaxNoOfInstall());
            assertEquals(new Short("60"), noOfInstallFromLoanCycleBO.getMinNoOfInstall());
            assertEquals(new Short("70"), noOfInstallFromLoanCycleBO.getDefaultNoOfInstall());

        }
        assertEquals(2.0, product.getMinInterestRate(), DELTA);
        assertEquals(12.0, product.getMaxInterestRate(), DELTA);
        assertEquals(3.0, product.getDefInterestRate(), DELTA);
        assertFalse(product.isIncludeInLoanCounter());
        assertFalse(product.isIntDedDisbursement());
        assertFalse(product.isPrinDueLastInst());
        assertEquals(2, product.getLoanOfferingFees().size());
        assertNotNull(product.getLoanOfferingMeeting());
        assertEquals(RecurrenceType.WEEKLY, product.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
                .getRecurrenceTypeEnum());
        assertNotNull(product.getPrincipalGLcode());
        assertNotNull(product.getInterestGLcode());
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

    public LoanPrdActionForm populateLoanAmountFromLastLoanAmount(String loanAmtCalcType, Integer startRangeLoanAmt1,
            Integer endRangeLoanAmt1, Integer startRangeLoanAmt2, Integer endRangeLoanAmt2, Integer startRangeLoanAmt3,
            Integer endRangeLoanAmt3, Integer startRangeLoanAmt4, Integer endRangeLoanAmt4, Integer startRangeLoanAmt5,
            Integer endRangeLoanAmt5, Integer startRangeLoanAmt6, Integer endRangeLoanAmt6, Double lastLoanMinLoanAmt1,
            Double lastLoanMaxLoanAmt1, Double lastLoanDefaultLoanAmt1, Double lastLoanMinLoanAmt2,
            Double lastLoanMaxLoanAmt2, Double lastLoanDefaultLoanAmt2, Double lastLoanMinLoanAmt3,
            Double lastLoanMaxLoanAmt3, Double lastLoanDefaultLoanAmt3, Double lastLoanMinLoanAmt4,
            Double lastLoanMaxLoanAmt4, Double lastLoanDefaultLoanAmt4, Double lastLoanMinLoanAmt5,
            Double lastLoanMaxLoanAmt5, Double lastLoanDefaultLoanAmt5, Double lastLoanMinLoanAmt6,
            Double lastLoanMaxLoanAmt6, Double lastLoanDefaultLoanAmt6, LoanPrdActionForm loanPrdActionForm) {

        if (loanAmtCalcType.equals("2")) {
            loanPrdActionForm.setLoanAmtCalcType(loanAmtCalcType);
            loanPrdActionForm.setStartRangeLoanAmt1(startRangeLoanAmt1);
            loanPrdActionForm.setStartRangeLoanAmt2(startRangeLoanAmt2);
            loanPrdActionForm.setStartRangeLoanAmt3(startRangeLoanAmt3);
            loanPrdActionForm.setStartRangeLoanAmt4(startRangeLoanAmt4);
            loanPrdActionForm.setStartRangeLoanAmt5(startRangeLoanAmt5);
            loanPrdActionForm.setStartRangeLoanAmt6(startRangeLoanAmt6);
            loanPrdActionForm.setEndRangeLoanAmt1(endRangeLoanAmt1);
            loanPrdActionForm.setEndRangeLoanAmt2(endRangeLoanAmt2);
            loanPrdActionForm.setEndRangeLoanAmt3(endRangeLoanAmt3);
            loanPrdActionForm.setEndRangeLoanAmt4(endRangeLoanAmt4);
            loanPrdActionForm.setEndRangeLoanAmt5(endRangeLoanAmt5);
            loanPrdActionForm.setEndRangeLoanAmt6(endRangeLoanAmt6);
            loanPrdActionForm.setLastLoanMaxLoanAmt1(lastLoanMaxLoanAmt1.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt2(lastLoanMaxLoanAmt2.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt3(lastLoanMaxLoanAmt3.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt4(lastLoanMaxLoanAmt4.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt5(lastLoanMaxLoanAmt5.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt6(lastLoanMaxLoanAmt6.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt1(lastLoanMinLoanAmt1.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt2(lastLoanMinLoanAmt2.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt3(lastLoanMinLoanAmt3.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt4(lastLoanMinLoanAmt4.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt5(lastLoanMinLoanAmt5.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt6(lastLoanMinLoanAmt6.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt1(lastLoanDefaultLoanAmt1.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt2(lastLoanDefaultLoanAmt2.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt3(lastLoanDefaultLoanAmt3.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt4(lastLoanDefaultLoanAmt4.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt5(lastLoanDefaultLoanAmt5.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt6(lastLoanDefaultLoanAmt6.toString());

        }
        return loanPrdActionForm;
    }

    public LoanPrdActionForm populateNoOfInstallFromLastLoanAmount(String calcInstallmentType,
            Integer startInstallmentRange1, Integer endInstallmentRange1, Integer startInstallmentRange2,
            Integer endInstallmentRange2, Integer startInstallmentRange3, Integer endInstallmentRange3,
            Integer startInstallmentRange4, Integer endInstallmentRange4, Integer startInstallmentRange5,
            Integer endInstallmentRange5, Integer startInstallmentRange6, Integer endInstallmentRange6,
            String minLoanInstallment1, String maxLoanInstallment1, String defLoanInstallment1,
            String minLoanInstallment2, String maxLoanInstallment2, String defLoanInstallment2,
            String minLoanInstallment3, String maxLoanInstallment3, String defLoanInstallment3,
            String minLoanInstallment4, String maxLoanInstallment4, String defLoanInstallment4,
            String minLoanInstallment5, String maxLoanInstallment5, String defLoanInstallment5,
            String minLoanInstallment6, String maxLoanInstallment6, String defLoanInstallment6,
            LoanPrdActionForm loanPrdActionForm) {

        if (calcInstallmentType.equals("2")) {
            loanPrdActionForm.setCalcInstallmentType(calcInstallmentType);
            loanPrdActionForm.setStartInstallmentRange1(startInstallmentRange1);
            loanPrdActionForm.setStartInstallmentRange2(startInstallmentRange2);
            loanPrdActionForm.setStartInstallmentRange3(startInstallmentRange3);
            loanPrdActionForm.setStartInstallmentRange4(startInstallmentRange4);
            loanPrdActionForm.setStartInstallmentRange5(startInstallmentRange5);
            loanPrdActionForm.setStartInstallmentRange6(startInstallmentRange6);
            loanPrdActionForm.setEndInstallmentRange1(endInstallmentRange1);
            loanPrdActionForm.setEndInstallmentRange2(endInstallmentRange2);
            loanPrdActionForm.setEndInstallmentRange3(endInstallmentRange3);
            loanPrdActionForm.setEndInstallmentRange4(endInstallmentRange4);
            loanPrdActionForm.setEndInstallmentRange5(endInstallmentRange5);
            loanPrdActionForm.setEndInstallmentRange6(endInstallmentRange6);
            loanPrdActionForm.setMaxLoanInstallment1(maxLoanInstallment1);
            loanPrdActionForm.setMaxLoanInstallment2(maxLoanInstallment2);
            loanPrdActionForm.setMaxLoanInstallment3(maxLoanInstallment3);
            loanPrdActionForm.setMaxLoanInstallment4(maxLoanInstallment4);
            loanPrdActionForm.setMaxLoanInstallment5(maxLoanInstallment5);
            loanPrdActionForm.setMaxLoanInstallment6(maxLoanInstallment6);
            loanPrdActionForm.setMinLoanInstallment1(minLoanInstallment1);
            loanPrdActionForm.setMinLoanInstallment2(minLoanInstallment2);
            loanPrdActionForm.setMinLoanInstallment3(minLoanInstallment3);
            loanPrdActionForm.setMinLoanInstallment4(minLoanInstallment4);
            loanPrdActionForm.setMinLoanInstallment5(minLoanInstallment5);
            loanPrdActionForm.setMinLoanInstallment6(minLoanInstallment6);
            loanPrdActionForm.setDefLoanInstallment1(defLoanInstallment1);
            loanPrdActionForm.setDefLoanInstallment2(defLoanInstallment2);
            loanPrdActionForm.setDefLoanInstallment3(defLoanInstallment3);
            loanPrdActionForm.setDefLoanInstallment4(defLoanInstallment4);
            loanPrdActionForm.setDefLoanInstallment5(defLoanInstallment5);
            loanPrdActionForm.setDefLoanInstallment6(defLoanInstallment6);
        }
        return loanPrdActionForm;
    }

    public LoanPrdActionForm populateLoanAmountFromLoanCycle(String loanAmtCalcType, Double cycleLoanMinLoanAmt1,
            Double cycleLoanMaxLoanAmt1, Double cycleLoanDefaultLoanAmt1, Double cycleLoanMinLoanAmt2,
            Double cycleLoanMaxLoanAmt2, Double cycleLoanDefaultLoanAmt2, Double cycleLoanMinLoanAmt3,
            Double cycleLoanMaxLoanAmt3, Double cycleLoanDefaultLoanAmt3, Double cycleLoanMinLoanAmt4,
            Double cycleLoanMaxLoanAmt4, Double cycleLoanDefaultLoanAmt4, Double cycleLoanMinLoanAmt5,
            Double cycleLoanMaxLoanAmt5, Double cycleLoanDefaultLoanAmt5, Double cycleLoanMinLoanAmt6,
            Double cycleLoanMaxLoanAmt6, Double cycleLoanDefaultLoanAmt6, LoanPrdActionForm loanPrdActionForm) {
        if (loanAmtCalcType.equals("3")) {
            loanPrdActionForm.setLoanAmtCalcType(loanAmtCalcType);
            loanPrdActionForm.setCycleLoanMaxLoanAmt1(cycleLoanMaxLoanAmt1.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt2(cycleLoanMaxLoanAmt2.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt3(cycleLoanMaxLoanAmt3.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt4(cycleLoanMaxLoanAmt4.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt5(cycleLoanMaxLoanAmt5.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt6(cycleLoanMaxLoanAmt6.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt1(cycleLoanMinLoanAmt1.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt2(cycleLoanMinLoanAmt2.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt3(cycleLoanMinLoanAmt3.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt4(cycleLoanMinLoanAmt4.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt5(cycleLoanMinLoanAmt5.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt6(cycleLoanMinLoanAmt6.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt1(cycleLoanDefaultLoanAmt1.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt2(cycleLoanDefaultLoanAmt2.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt3(cycleLoanDefaultLoanAmt3.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt4(cycleLoanDefaultLoanAmt4.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt5(cycleLoanDefaultLoanAmt5.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt6(cycleLoanDefaultLoanAmt6.toString());
        }
        return loanPrdActionForm;
    }

    public LoanPrdActionForm populateNoOfInstallFromLoanCycle(String calcInstallmentType, String minCycleInstallment1,
            String maxCycleInstallment1, String defCycleInstallment1, String minCycleInstallment2,
            String maxCycleInstallment2, String defCycleInstallment2, String minCycleInstallment3,
            String maxCycleInstallment3, String defCycleInstallment3, String minCycleInstallment4,
            String maxCycleInstallment4, String defCycleInstallment4, String minCycleInstallment5,
            String maxCycleInstallment5, String defCycleInstallment5, String minCycleInstallment6,
            String maxCycleInstallment6, String defCycleInstallment6, LoanPrdActionForm loanPrdActionForm) {

        if (calcInstallmentType.equals("3")) {
            loanPrdActionForm.setCalcInstallmentType(calcInstallmentType);
            loanPrdActionForm.setMaxCycleInstallment1(maxCycleInstallment1);
            loanPrdActionForm.setMaxCycleInstallment2(maxCycleInstallment2);
            loanPrdActionForm.setMaxCycleInstallment3(maxCycleInstallment3);
            loanPrdActionForm.setMaxCycleInstallment4(maxCycleInstallment4);
            loanPrdActionForm.setMaxCycleInstallment5(maxCycleInstallment5);
            loanPrdActionForm.setMaxCycleInstallment6(maxCycleInstallment6);
            loanPrdActionForm.setMinCycleInstallment1(minCycleInstallment1);
            loanPrdActionForm.setMinCycleInstallment2(minCycleInstallment2);
            loanPrdActionForm.setMinCycleInstallment3(minCycleInstallment3);
            loanPrdActionForm.setMinCycleInstallment4(minCycleInstallment4);
            loanPrdActionForm.setMinCycleInstallment5(minCycleInstallment5);
            loanPrdActionForm.setMinCycleInstallment6(minCycleInstallment6);
            loanPrdActionForm.setDefCycleInstallment1(defCycleInstallment1);
            loanPrdActionForm.setDefCycleInstallment2(defCycleInstallment2);
            loanPrdActionForm.setDefCycleInstallment3(defCycleInstallment3);
            loanPrdActionForm.setDefCycleInstallment4(defCycleInstallment4);
            loanPrdActionForm.setDefCycleInstallment5(defCycleInstallment5);
            loanPrdActionForm.setDefCycleInstallment6(defCycleInstallment6);

        }
        return loanPrdActionForm;

    }

}
