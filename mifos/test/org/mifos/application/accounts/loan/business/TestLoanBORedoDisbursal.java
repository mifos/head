/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.application.accounts.loan.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.center.CenterTemplate;
import org.mifos.application.customer.center.CenterTemplateImpl;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.GroupTemplate;
import org.mifos.application.customer.group.GroupTemplateImpl;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.MeetingTemplateImpl;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeTemplate;
import org.mifos.application.office.business.OfficeTemplateImpl;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBOTest;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanBORedoDisbursal extends MifosTestCase {
    private OfficePersistence officePersistence;
    private CenterPersistence centerPersistence;
    private GroupPersistence groupPersistence;

    @Override
	protected void setUp() throws Exception {
		super.setUp();
        officePersistence = new OfficePersistence();
        centerPersistence = new CenterPersistence();
        groupPersistence = new GroupPersistence();
        initializeStatisticsService();
    }

    @Override
    protected void tearDown() throws Exception {
        HibernateUtil.closeSession();
        super.tearDown();
    }
    
    private LoanOfferingBO createLoanOffering(UserContext userContext, MeetingBO meeting, Date loanProductStartDate)
            throws ProductDefinitionException {
        PrdApplicableMasterEntity prdApplicableMaster =
			new PrdApplicableMasterEntity(ApplicableTo.GROUPS);
		ProductCategoryBO productCategory = TestObjectFactory
				.getLoanPrdCategory();
		GracePeriodTypeEntity gracePeriodType =
			new GracePeriodTypeEntity(GraceType.NONE) ;
		InterestTypesEntity interestType =
			new InterestTypesEntity(InterestType.FLAT);
		GLCodeEntity glCodePrincipal = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, TestGeneralLedgerCode.LOANS_TO_CLIENTS);
		GLCodeEntity glCodeInterest = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, TestGeneralLedgerCode.INTEREST_ON_LOANS);

        boolean interestDeductedAtDisbursement = false;
        boolean principalDueInLastInstallment = false;
        Money loanAmount = new Money("300");
        Double interestRate = new Double(1.2);
        Short installments = new Short((short) 6);
        LoanOfferingBO loanOffering = LoanOfferingBO.createInstanceForTest(
        		userContext, "TestLoanOffering", "TLO",
				productCategory, prdApplicableMaster, loanProductStartDate, null,
				null, gracePeriodType, (short) 0, interestType,
                loanAmount, loanAmount, loanAmount,
				interestRate, interestRate, interestRate,
                installments, installments, installments,
                true, interestDeductedAtDisbursement,
				principalDueInLastInstallment, new ArrayList<FundBO>(),
				new ArrayList<FeeBO>(), meeting, glCodePrincipal, glCodeInterest);
        
        PrdStatusEntity prdStatus = new TestObjectPersistence()
                .retrievePrdStatus(PrdStatus.LOAN_ACTIVE);
		LoanOfferingBOTest.setStatus(loanOffering,prdStatus);
		LoanOfferingBOTest.setGracePeriodType(loanOffering,gracePeriodType);
        loanOffering.save();

        return loanOffering;
    }

    private LoanBO createLoanAccount(GroupBO group,
			LoanOfferingBO loanOffering, MeetingBO meeting,
			Date disbursementDate) throws AccountException {
        MifosCurrency currency = TestObjectFactory.getCurrency();
        Short numberOfInstallments = Short.valueOf("6");
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, numberOfInstallments);
        LoanBO loan = LoanBO.redoLoan(TestUtils.makeUser(), loanOffering, group,
				AccountState.LOAN_APPROVED, new Money(currency, "300.0"),
                numberOfInstallments, meetingDates.get(0), false, 0.0, (short) 0,
                new FundBO(), new ArrayList<FeeView>(), null,false,null);
        loan.save();

        return loan;
    }

	private void disperseLoan(UserContext userContext, LoanBO loan, Date loanDispersalDate)
            throws AccountException, PersistenceException {
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userContext
				.getId());
		loan.disburseLoan(null, loanDispersalDate, Short.valueOf("1"),
                    personnel, null, Short.valueOf("1"));
	}

	private void applyPaymentForLoan(
            UserContext userContext, LoanBO loan, Date paymentDate, Money money)
            throws AccountException, ValidationException {
		loan.setUserContext(userContext);
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());

		PaymentData paymentData = loan.createPaymentData(userContext,
                money, paymentDate, null, null, Short.valueOf("1"));
		loan.applyPaymentWithPersist(paymentData);
    }

    private Date createPreviousDate(int numberOfDays) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -numberOfDays);
        Date pastDate =  calendar.getTime();
        return pastDate;
    }

    private LoanBO createLoan(UserContext userContext, Date disbursementDate)
            throws Exception {
        OfficeTemplate template =
                OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
        OfficeBO office = getOfficePersistence().createOffice(userContext, template);

        MeetingBO meeting = new MeetingBO(MeetingTemplateImpl.createWeeklyMeetingTemplateStartingFrom(
        		disbursementDate
        		));

        CenterTemplate centerTemplate = new CenterTemplateImpl(meeting, office.getOfficeId());
        CenterBO center = getCenterPersistence().createCenter(userContext, centerTemplate);

        GroupTemplate groupTemplate = GroupTemplateImpl.createNonUniqueGroupTemplate(center.getCustomerId());
		
        GroupBO group = createGroup(userContext, groupTemplate, disbursementDate);
        
        LoanOfferingBO loanOffering = createLoanOffering(userContext, meeting, disbursementDate);
        return createLoanAccount(group, loanOffering, meeting, disbursementDate);
    }

    // inlined the persistence method to set the activation/creation date of customer on or before disbursement date
	private GroupBO createGroup(UserContext userContext, GroupTemplate groupTemplate, final Date disbursementDate) 
		throws PersistenceException, ValidationException, CustomerException {
		CenterBO center = null;
		if (groupTemplate.getParentCenterId() != null) {
		    center = getGroupPersistence().getCenterPersistence().getCenter(groupTemplate.getParentCenterId());
		    if (center == null) {
		        throw new ValidationException(GroupConstants.PARENT_OFFICE_ID);
		    }
		}
		GroupBO group = GroupBO.createInstanceForTest(userContext, groupTemplate, center, disbursementDate);
		group.save();
		return group;
	}

	/*
	 * There must be at least one test case defined.  Since we have commented
	 * out the only test real test case below, a dummy test case has been added.
	 */
	public void testNothing() {
		
	}
	
	/*
	 * TODO: financial_calculation_fixme This test fails because when
	 * redoing the loan, the number of unpaid future installments does
	 * not equal the total number of installments (since the loan is created
	 * in the past).  There is an assert in the new applyRounding(_v2) method
	 * that catches this problem where in the previous version the problem 
	 * was not caught.
	 *
    public void testCreateAndDisperseLoanInThePast() throws Exception {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            UserContext userContext = TestUtils.makeUser();

            Date twoWeeksAgo = createPreviousDate(14);
            LoanBO loan = createLoan(userContext, twoWeeksAgo);

            disperseLoan(userContext, loan, twoWeeksAgo);
            long dispersementTimeInMil = loan.getDisbursementDate().getTime();
            long twoWeeksAgoInMil = twoWeeksAgo.getTime();
            assertEquals(twoWeeksAgoInMil, dispersementTimeInMil);

            // Validate dispersement information
            Iterator<AccountPaymentEntity> payments = loan.getAccountPayments().iterator();
            AccountPaymentEntity dispersment = payments.next();
            Iterator<AccountTrxnEntity> trxns = dispersment.getAccountTrxns().iterator();
            AccountTrxnEntity trxn;
            do {
                trxn = trxns.next();
                if (trxn.getAccountAction() == AccountActionTypes.DISBURSAL) {
                    break;
                }
            }
            while (trxns.hasNext());
            assertEquals(AccountActionTypes.DISBURSAL, trxn.getAccountAction());
            assertEquals(new Money("300.0").getAmount(), trxn.getAmount().getAmount());
            assertEquals(twoWeeksAgoInMil, trxn.getActionDate().getTime());

            Date oneWeekAgo = createPreviousDate(7);
            long oneWeekAgoInMil = oneWeekAgo.getTime();
            Money moneyPayment = TestObjectFactory.getMoneyForMFICurrency(50);
            applyPaymentForLoan(userContext, loan, oneWeekAgo, moneyPayment);

            // Validate payment information
            payments = loan.getAccountPayments().iterator();
            AccountPaymentEntity payment;
            trxns = null;
            trxn = null;
            boolean foundThePayment = false;
            do {
                payment = payments.next();
                trxns = payment.getAccountTrxns().iterator();
                do {
                    trxn = trxns.next();
                    if (trxn.getAccountAction() == AccountActionTypes.LOAN_REPAYMENT
                            && trxn.getAmount().equals(moneyPayment)) {
                        foundThePayment = true;
                        assertEquals(trxn.getActionDate().getTime(), oneWeekAgoInMil);
                        break;
                    }
                }
                while (trxns.hasNext());
            }
            while (payments.hasNext());
            assertTrue("Couldnt find a LOAN_REPAYMENT", foundThePayment);
        }
        finally {
            HibernateUtil.rollbackTransaction();
        }

        long numberOfTransactions =
            getStatisticsService().getSuccessfulTransactionCount() - transactionCount;
        assertEquals(numberOfTransactions, 0);
    }
*/
    private OfficePersistence getOfficePersistence() {
        return officePersistence;
    }

    private CenterPersistence getCenterPersistence() {
        return centerPersistence;
    }

    private GroupPersistence getGroupPersistence() {
        return groupPersistence;
    }
}
