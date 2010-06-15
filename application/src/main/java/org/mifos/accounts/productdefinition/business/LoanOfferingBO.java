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

import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.ERRORFEEFREQUENCY;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.LOANAMOUNTTYPE_UNKNOWN;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.NOOFINSTALLFROMLASTLOAN;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.NOOFINSTALLFROMLOANCYCLLE;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.NOOFINSTALLSAMEFORALLLOAN;
import static org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants.NOOFINSTALL_UNKNOWN;
import static org.mifos.framework.util.CollectionUtils.find;
import static org.mifos.framework.util.CollectionUtils.first;
import static org.mifos.framework.util.CollectionUtils.last;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.Predicate;
import org.mifos.security.util.UserContext;

/**
 * A loan product is a set of rules (interest rate, number of installments, maximum amount, etc) which describes a
 * particular kind of loan that an MFI offers.
 *
 * Although we may sometimes call these "offerings", it is probably better to call them "loan products" (as that seems
 * to be the terminology in the functional spec and elsewhere).
 */
public class LoanOfferingBO extends PrdOfferingBO {

    private static final MifosLogger prdLogger = MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);
    public static LoanOfferingBO ALL_LOAN_PRD = new LoanOfferingBO();
    static {
        ALL_LOAN_PRD.setPrdOfferingName("ALL");
    }

    private GracePeriodTypeEntity gracePeriodType;
    private InterestTypesEntity interestTypes;
    private Short gracePeriodDuration;
    private Double maxInterestRate;
    private Double minInterestRate;
    private Double defInterestRate;
    private Short intDedDisbursement;
    private Short prinDueLastInst;
    private Short loanCounter;
    private PrdOfferingMeetingEntity loanOfferingMeeting;
    private final GLCodeEntity principalGLcode;
    private final GLCodeEntity interestGLcode;
    private final Set<LoanOfferingFundEntity> loanOfferingFunds;
    private final Set<LoanOfferingFeesEntity> loanOfferingFees;

    // FIXME: the implementation of defaults for loan amounts and number of
    // installments requires the inclusion of branching logic wherever loan
    // offerings are instantiated, such as LoanPrdActionForm,
    // LoanProductDetails.jsp,CreateMultipleLoanAccountsSearchResults.jsp, etc.
    private final Set<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoan;
    private final Set<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycle;
    private final Set<LoanAmountSameForAllLoanBO> loanAmountSameForAllLoan;
    private final Set<NoOfInstallFromLastLoanAmountBO> noOfInstallFromLastLoan;
    private final Set<NoOfInstallFromLoanCycleBO> noOfInstallFromLoanCycle;
    private final Set<NoOfInstallSameForAllLoanBO> noOfInstallSameForAllLoan;

    /**
     * used by builders to construct legal {@link LoanOfferingBO}'s
     */
    public LoanOfferingBO(final GLCodeEntity principalGLcode, final GLCodeEntity interestGLCode,
            final InterestType interestType, final Double minInterestRate, final Double maxInterestRate,
            final Double defaultInterestRate, final Short interestPaidAtDisbursement,
            final Short principalDueLastInstallment, final String name, final String shortName,
            final String globalProductNumber, final Date startDate, final ApplicableTo applicableToCustomer,
            final ProductCategoryBO category, final PrdStatusEntity productStatus, final Date createdDate,
            final Short createdByUserId) {

        super(name, shortName, globalProductNumber, startDate, applicableToCustomer, category, productStatus,
                createdDate, createdByUserId);
        this.interestTypes = new InterestTypesEntity(interestType);
        this.principalGLcode = principalGLcode;
        this.interestGLcode = interestGLCode;
        this.minInterestRate = minInterestRate;
        this.maxInterestRate = maxInterestRate;
        this.defInterestRate = defaultInterestRate;
        this.intDedDisbursement = interestPaidAtDisbursement;
        this.prinDueLastInst = principalDueLastInstallment;
        this.noOfInstallSameForAllLoan = new HashSet<NoOfInstallSameForAllLoanBO>();
        this.noOfInstallFromLoanCycle = null;
        this.noOfInstallFromLastLoan = null;
        this.loanOfferingFunds = null;
        this.loanOfferingFees = null;
        this.loanAmountFromLastLoan = null;
        this.loanAmountSameForAllLoan = new HashSet<LoanAmountSameForAllLoanBO>();
        this.loanAmountFromLoanCycle = null;
    }

    /**
     * @deprecated use {@link LoanProductBuilder} to construct legal {@link LoanOfferingBO}'s for tests.
     */
    @Deprecated
    public LoanOfferingBO(final UserContext userContext, final String prdOfferingName,
            final String prdOfferingShortName, final ProductCategoryBO prdCategory,
            final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate,
            final InterestTypesEntity interestTypes, final Money minLoanAmount, final Money maxLoanAmount,
            final Double maxInterestRate, final Double minInterestRate, final Double defInterestRate,
            final Short maxNoInstallments, final Short minNoInstallments, final Short defNoInstallments,
            final boolean loanCounter, final boolean intDedDisbursement, final boolean prinDueLastInst,
            final MeetingBO meeting, final GLCodeEntity principalGLcode, final GLCodeEntity interestGLcode)
            throws ProductDefinitionException {
        this(userContext, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate, null,
                null, null, null, interestTypes, minLoanAmount, maxLoanAmount, minLoanAmount, maxInterestRate,
                minInterestRate, defInterestRate, maxNoInstallments, minNoInstallments, defNoInstallments, loanCounter,
                intDedDisbursement, prinDueLastInst, null, null, meeting, principalGLcode, interestGLcode);
    }

    /**
     * @deprecated use {@link LoanProductBuilder} to construct legal {@link LoanOfferingBO}'s for tests.
     */
    @Deprecated
    public LoanOfferingBO(final UserContext userContext, final String prdOfferingName,
            final String prdOfferingShortName, final ProductCategoryBO prdCategory,
            final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate, final Date endDate,
            final String description, final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration,
            final InterestTypesEntity interestTypes, final Money minLoanAmount, final Money maxLoanAmount,
            final Money defaultLoanAmount, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final Short maxNoInstallments, final Short minNoInstallments,
            final Short defNoInstallments, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final MeetingBO meeting,
            final GLCodeEntity principalGLcode, final GLCodeEntity interestGLcode) throws ProductDefinitionException {
        this(userContext, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate, endDate,
                description, gracePeriodType, gracePeriodDuration, interestTypes, minLoanAmount, maxLoanAmount,
                defaultLoanAmount, maxInterestRate, minInterestRate, defInterestRate, maxNoInstallments,
                minNoInstallments, defNoInstallments, loanCounter, intDedDisbursement, prinDueLastInst, funds, fees,
                meeting, principalGLcode, interestGLcode, LOANAMOUNTSAMEFORALLLOAN.toString(),
                NOOFINSTALLSAMEFORALLLOAN.toString());
    }

    /**
     * @deprecated use {@link LoanProductBuilder} to construct legal {@link LoanOfferingBO}'s for tests.
     */
    @Deprecated
    public LoanOfferingBO(final UserContext userContext, final String prdOfferingName,
            final String prdOfferingShortName, final ProductCategoryBO prdCategory,
            final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate, final Date endDate,
            final String description, final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration,
            final InterestTypesEntity interestTypes, final Money minLoanAmount, final Money maxLoanAmount,
            final Money defaultLoanAmount, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final Short maxNoInstallments, final Short minNoInstallments,
            final Short defNoInstallments, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final MeetingBO meeting,
            final GLCodeEntity principalGLcode, final GLCodeEntity interestGLcode, final String loanAmtCalcType,
            final String calcInstallmentType) throws ProductDefinitionException {
        super(userContext, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate, endDate,
                description);
        prdLogger.debug("building Loan offering");
        validate(gracePeriodType, gracePeriodDuration, interestTypes, minLoanAmount, maxLoanAmount, defaultLoanAmount,
                maxInterestRate, minInterestRate, defInterestRate, maxNoInstallments, minNoInstallments,
                defNoInstallments, loanCounter, intDedDisbursement, prinDueLastInst, funds, fees, meeting,
                principalGLcode, interestGLcode);
        setCreateDetails();
        setLoanCounter(loanCounter);
        setIntDedDisbursement(intDedDisbursement);
        setPrinDueLastInst(prinDueLastInst);
        setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType, gracePeriodDuration);
        this.interestTypes = interestTypes;
        this.maxInterestRate = maxInterestRate;
        this.minInterestRate = minInterestRate;
        this.defInterestRate = defInterestRate;
        this.principalGLcode = principalGLcode;
        this.interestGLcode = interestGLcode;
        this.loanOfferingFunds = new HashSet<LoanOfferingFundEntity>();
        this.loanAmountFromLastLoan = new HashSet<LoanAmountFromLastLoanAmountBO>();
        this.loanAmountFromLoanCycle = new HashSet<LoanAmountFromLoanCycleBO>();
        this.noOfInstallFromLastLoan = new HashSet<NoOfInstallFromLastLoanAmountBO>();
        this.noOfInstallFromLoanCycle = new HashSet<NoOfInstallFromLoanCycleBO>();
        this.loanAmountSameForAllLoan = new HashSet<LoanAmountSameForAllLoanBO>();
        this.noOfInstallSameForAllLoan = new HashSet<NoOfInstallSameForAllLoanBO>();
        if (new Short(loanAmtCalcType).equals(LOANAMOUNTSAMEFORALLLOAN)) {
            loanAmountSameForAllLoan.add(new LoanAmountSameForAllLoanBO(Double.parseDouble(minLoanAmount.toString()),
                    Double.parseDouble(maxLoanAmount.toString()), Double.parseDouble(defaultLoanAmount.toString()),
                    this));
        }
        if (new Short(calcInstallmentType).equals(NOOFINSTALLSAMEFORALLLOAN)) {
            noOfInstallSameForAllLoan.add(new NoOfInstallSameForAllLoanBO(Short
                    .parseShort(minNoInstallments.toString()), Short.parseShort(maxNoInstallments.toString()), Short
                    .parseShort(defNoInstallments.toString()), this));
        }
        if (funds != null && funds.size() > 0) {
            for (FundBO fund : funds) {
                addLoanOfferingFund(new LoanOfferingFundEntity(fund, this));
            }
        }
        this.loanOfferingMeeting = new PrdOfferingMeetingEntity(meeting, this, MeetingType.LOAN_INSTALLMENT);
        this.loanOfferingFees = new HashSet<LoanOfferingFeesEntity>();
        if (fees != null && fees.size() > 0) {
            for (FeeBO fee : fees) {
                if (isFrequencyMatchingOfferingFrequency(fee, meeting)) {
                    addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
                }
            }
        }
        prdLogger.debug("Loan offering build :" + getGlobalPrdOfferingNum());
    }

    public LoanOfferingBO(final UserContext userContext, final String prdOfferingName,
            final String prdOfferingShortName, final ProductCategoryBO prdCategory,
            final PrdApplicableMasterEntity prdApplicableMaster, final Date startDate, final Date endDate,
            final String description, final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration,
            final InterestTypesEntity interestTypes, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final MeetingBO meeting,
            final GLCodeEntity principalGLcode, final GLCodeEntity interestGLcode,
            final LoanPrdActionForm loanPrdActionForm) throws ProductDefinitionException {

        super(userContext, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate, endDate,
                description);
        prdLogger.debug("building Loan offering");
        validate(gracePeriodType, gracePeriodDuration, interestTypes, maxInterestRate, minInterestRate,
                defInterestRate, loanCounter, intDedDisbursement, prinDueLastInst, funds, fees, meeting,
                principalGLcode, interestGLcode);
        setCreateDetails();
        setLoanCounter(loanCounter);
        setIntDedDisbursement(intDedDisbursement);
        setPrinDueLastInst(prinDueLastInst);
        setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType, gracePeriodDuration);
        this.interestTypes = interestTypes;
        this.maxInterestRate = maxInterestRate;
        this.minInterestRate = minInterestRate;
        this.defInterestRate = defInterestRate;
        this.principalGLcode = principalGLcode;
        this.interestGLcode = interestGLcode;
        this.loanOfferingFunds = new HashSet<LoanOfferingFundEntity>();
        this.loanAmountFromLastLoan = new HashSet<LoanAmountFromLastLoanAmountBO>();
        this.loanAmountFromLoanCycle = new HashSet<LoanAmountFromLoanCycleBO>();
        this.noOfInstallFromLastLoan = new HashSet<NoOfInstallFromLastLoanAmountBO>();
        this.noOfInstallFromLoanCycle = new HashSet<NoOfInstallFromLoanCycleBO>();
        this.loanAmountSameForAllLoan = new HashSet<LoanAmountSameForAllLoanBO>();
        this.noOfInstallSameForAllLoan = new HashSet<NoOfInstallSameForAllLoanBO>();
        populateLoanAmountAndInstall(loanPrdActionForm);
        if (funds != null && funds.size() > 0) {
            for (FundBO fund : funds) {
                addLoanOfferingFund(new LoanOfferingFundEntity(fund, this));
            }
        }
        this.loanOfferingMeeting = new PrdOfferingMeetingEntity(meeting, this, MeetingType.LOAN_INSTALLMENT);
        this.loanOfferingFees = new HashSet<LoanOfferingFeesEntity>();
        if (fees != null && fees.size() > 0) {
            for (FeeBO fee : fees) {
                if (isFrequencyMatchingOfferingFrequency(fee, meeting)) {
                    addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
                }
            }
        }
        prdLogger.debug("Loan offering build :" + getGlobalPrdOfferingNum());
    }

    /**
     * @deprecated use {@link LoanProductBuilder} to construct legal {@link LoanOfferingBO}'s for tests.
     */
    @Deprecated
    protected LoanOfferingBO() {
        this(null, null, null, null, null, null, new HashSet<LoanOfferingFundEntity>(),
                new HashSet<LoanOfferingFeesEntity>(), new HashSet<LoanAmountFromLastLoanAmountBO>(),
                new HashSet<LoanAmountFromLoanCycleBO>(), new HashSet<NoOfInstallFromLastLoanAmountBO>(),
                new HashSet<NoOfInstallFromLoanCycleBO>(), new HashSet<LoanAmountSameForAllLoanBO>(),
                new HashSet<NoOfInstallSameForAllLoanBO>());
    }

    private LoanOfferingBO(final Short prdOfferingId, final String globalPrdOfferingNum,
            final ProductTypeEntity prdType, final OfficeBO office, final GLCodeEntity principalGLcode,
            final GLCodeEntity interestGLcode, final Set<LoanOfferingFundEntity> loanOfferingFunds,
            final Set<LoanOfferingFeesEntity> loanOfferingFees,
            final Set<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoan,
            final Set<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycle,
            final Set<NoOfInstallFromLastLoanAmountBO> noOfInstallFromLastLoan,
            final Set<NoOfInstallFromLoanCycleBO> noOfInstallFromLoanCycle,
            final Set<LoanAmountSameForAllLoanBO> loanAmountSameForAllLoan,
            final Set<NoOfInstallSameForAllLoanBO> noOfInstallSameForAllLoan) {
        super(prdOfferingId, globalPrdOfferingNum, prdType, office);
        this.principalGLcode = principalGLcode;
        this.interestGLcode = interestGLcode;
        this.loanOfferingFunds = loanOfferingFunds;
        this.loanOfferingFees = loanOfferingFees;
        this.loanAmountFromLastLoan = loanAmountFromLastLoan;
        this.loanAmountFromLoanCycle = loanAmountFromLoanCycle;
        this.noOfInstallFromLastLoan = noOfInstallFromLastLoan;
        this.noOfInstallFromLoanCycle = noOfInstallFromLoanCycle;
        this.loanAmountSameForAllLoan = loanAmountSameForAllLoan;
        this.noOfInstallSameForAllLoan = noOfInstallSameForAllLoan;
    }

    /**
     * @deprecated use {@link LoanProductBuilder} to construct legal {@link LoanOfferingBO}'s for tests.
     */
    @Deprecated
    public static LoanOfferingBO createInstanceForTest(final Short prdOfferingId) {
        return new LoanOfferingBO(prdOfferingId, null, null, null, null, null, new HashSet<LoanOfferingFundEntity>(),
                new HashSet<LoanOfferingFeesEntity>(), new HashSet<LoanAmountFromLastLoanAmountBO>(),
                new HashSet<LoanAmountFromLoanCycleBO>(), new HashSet<NoOfInstallFromLastLoanAmountBO>(),
                new HashSet<NoOfInstallFromLoanCycleBO>(), new HashSet<LoanAmountSameForAllLoanBO>(),
                new HashSet<NoOfInstallSameForAllLoanBO>());
    }

    public GracePeriodTypeEntity getGracePeriodType() {
        return gracePeriodType;
    }

    public GraceType getGraceType() {
        return gracePeriodType.asEnum();
    }

    public InterestTypesEntity getInterestTypes() {
        return interestTypes;
    }

    public InterestType getInterestType() {
        return interestTypes.asEnum();
    }

    public Short getGracePeriodDuration() {
        return gracePeriodDuration;
    }

    public Double getMaxInterestRate() {
        return maxInterestRate;
    }

    public Double getMinInterestRate() {
        return minInterestRate;
    }

    public Double getDefInterestRate() {
        return defInterestRate;
    }

    public boolean isIntDedDisbursement() {
        return this.intDedDisbursement.equals(YesNoFlag.YES.getValue());
    }

    public boolean isPrinDueLastInst() {
        return this.prinDueLastInst.equals(YesNoFlag.YES.getValue());
    }

    public boolean isIncludeInLoanCounter() {
        return this.loanCounter.equals(YesNoFlag.YES.getValue());
    }

    public Set<LoanOfferingFundEntity> getLoanOfferingFunds() {
        return loanOfferingFunds;
    }

    public Set<LoanOfferingFeesEntity> getLoanOfferingFees() {
        return loanOfferingFees;
    }

    public PrdOfferingMeetingEntity getLoanOfferingMeeting() {
        return loanOfferingMeeting;
    }

    public MeetingBO getLoanOfferingMeetingValue() {
        return loanOfferingMeeting.getMeeting();
    }

    public GLCodeEntity getPrincipalGLcode() {
        return principalGLcode;
    }

    public GLCodeEntity getInterestGLcode() {
        return interestGLcode;
    }

    void setGracePeriodType(final GracePeriodTypeEntity gracePeriodType) {
        this.gracePeriodType = gracePeriodType;
    }

    public void setInterestTypes(final InterestTypesEntity interestTypes) {
        this.interestTypes = interestTypes;
    }

    void setGracePeriodDuration(final Short gracePeriodDuration) {
        this.gracePeriodDuration = gracePeriodDuration;
    }

    void setMaxInterestRate(final Double maxInterestRate) {
        this.maxInterestRate = maxInterestRate;
    }

    void setMinInterestRate(final Double minInterestRate) {
        this.minInterestRate = minInterestRate;
    }

    void setDefInterestRate(final Double defInterestRate) {
        this.defInterestRate = defInterestRate;
    }

    void setIntDedDisbursement(final boolean intDedDisbursementFlag) {
        this.intDedDisbursement = intDedDisbursementFlag ? YesNoFlag.YES.getValue() : YesNoFlag.NO.getValue();
    }

    public void setPrinDueLastInst(final boolean prinDueLastInst) {
        this.prinDueLastInst = prinDueLastInst ? YesNoFlag.YES.getValue() : YesNoFlag.NO.getValue();
    }

    public void setLoanCounter(final boolean loanCounter) {
        this.loanCounter = loanCounter ? YesNoFlag.YES.getValue() : YesNoFlag.NO.getValue();
    }

    public void addLoanOfferingFund(final LoanOfferingFundEntity loanOfferingFund) {
        if (null != loanOfferingFund) {
            this.loanOfferingFunds.add(loanOfferingFund);
        }
    }

    public void addLoanAmountFromLastLoanAmount(final LoanAmountFromLastLoanAmountBO loanAmountFromLastLoanAmountBO) {
        if (null != loanAmountFromLastLoanAmountBO) {
            this.loanAmountFromLastLoan.add(loanAmountFromLastLoanAmountBO);
        }
    }

    public void addNoOfInstallFromLastLoanAmount(final NoOfInstallFromLastLoanAmountBO noOfInstallFromLastLoanAmountBO) {
        if (null != noOfInstallFromLastLoanAmountBO) {
            this.noOfInstallFromLastLoan.add(noOfInstallFromLastLoanAmountBO);
        }
    }

    public void addLoanAmountFromLoanCycle(final LoanAmountFromLoanCycleBO loanAmountFromLoanCycleBO) {
        if (null != loanAmountFromLoanCycleBO) {
            this.loanAmountFromLoanCycle.add(loanAmountFromLoanCycleBO);
        }
    }

    public void addNoOfInstallFromLoanCycle(final NoOfInstallFromLoanCycleBO noOfInstallFromLoanCycleBO) {
        if (null != noOfInstallFromLoanCycleBO) {
            this.noOfInstallFromLoanCycle.add(noOfInstallFromLoanCycleBO);
        }
    }

    public void addPrdOfferingFee(final LoanOfferingFeesEntity prdOfferingFeesEntity) {
        if (null != prdOfferingFeesEntity) {
            this.loanOfferingFees.add(prdOfferingFeesEntity);
        }
    }

    void setLoanOfferingMeeting(final PrdOfferingMeetingEntity prdOfferingMeeting) {
        this.loanOfferingMeeting = prdOfferingMeeting;
    }

    public boolean isFeePresent(final FeeBO fee) {
        prdLogger.debug("checking isFeePresent :" + fee);
        if (loanOfferingFees != null && loanOfferingFees.size() > 0) {
            for (LoanOfferingFeesEntity prdOfferingFee : loanOfferingFees) {
                if (prdOfferingFee.isFeePresent(fee.getFeeId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void save() throws ProductDefinitionException {
        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
    }

    // FIXME this update is only used by tests, is not the right logic
    public void update(final Short userId, final String prdOfferingName, final String prdOfferingShortName,
            final ProductCategoryBO prdCategory, final PrdApplicableMasterEntity prdApplicableMaster,
            final Date startDate, final Date endDate, final String description, final PrdStatus status,
            final GracePeriodTypeEntity gracePeriodType, final InterestTypesEntity interestTypes,
            final Short gracePeriodDuration, final Money maxLoanAmount, final Money minLoanAmount,
            final Money defaultLoanAmount, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final Short maxNoInstallments, final Short minNoInstallments,
            final Short defNoInstallments, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final Short recurAfter,
            final RecurrenceType recurrenceType) throws ProductDefinitionException {
        prdLogger.debug("Updating loan Offering :" + prdOfferingName);
        super.update(userId, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate,
                endDate, description, status);
        validateForUpdate(gracePeriodType, gracePeriodDuration, interestTypes, minLoanAmount, maxLoanAmount,
                defaultLoanAmount, maxInterestRate, minInterestRate, defInterestRate, maxNoInstallments,
                minNoInstallments, defNoInstallments, loanCounter, intDedDisbursement, prinDueLastInst, funds, fees,
                recurAfter);
        setUpdateDetails(userId);
        setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType, gracePeriodDuration);
        this.interestTypes = interestTypes;
        this.maxInterestRate = maxInterestRate;
        this.minInterestRate = minInterestRate;
        this.defInterestRate = defInterestRate;
        setLoanCounter(loanCounter);
        setIntDedDisbursement(intDedDisbursement);
        setPrinDueLastInst(prinDueLastInst);
        if (this.loanOfferingMeeting.getMeeting().getMeetingDetails().getRecurrenceType().getRecurrenceId().equals(
                recurrenceType.getValue())) {
            this.loanOfferingMeeting.getMeeting().getMeetingDetails().setRecurAfter(recurAfter);
        } else {
            try {
                this.loanOfferingMeeting.setMeeting(new MeetingBO(recurrenceType, recurAfter, startDate,
                        MeetingType.LOAN_INSTALLMENT));
            } catch (MeetingException e) {
                throw new ProductDefinitionException(e);
            }
        }

        if (this.loanOfferingFunds != null) {
            this.loanOfferingFunds.clear();
            if (funds != null && funds.size() > 0) {
                for (FundBO fund : funds) {
                    addLoanOfferingFund(new LoanOfferingFundEntity(fund, this));
                }
            }
        }
        if (this.loanOfferingFees != null) {
            this.loanOfferingFees.clear();
            if (fees != null && fees.size() > 0) {
                for (FeeBO fee : fees) {
                    if (isFrequencyMatchingOfferingFrequency(fee, this.loanOfferingMeeting.getMeeting())) {
                        addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
                    }
                }
            }
        }
        try {
            new LoanPrdPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
        prdLogger.debug("Loan Offering updated:" + prdOfferingName);
    }

    public void update(final Short userId, final String prdOfferingName, final String prdOfferingShortName,
            final ProductCategoryBO prdCategory, final PrdApplicableMasterEntity prdApplicableMaster,
            final Date startDate, final Date endDate, final String description, final PrdStatus status,
            final GracePeriodTypeEntity gracePeriodType, final InterestTypesEntity interestTypes,
            final Short gracePeriodDuration, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final Short recurAfter,
            final RecurrenceType recurrenceType, final LoanPrdActionForm loanPrdActionForm)
            throws ProductDefinitionException {
        prdLogger.debug("Updating loan Offering :" + prdOfferingName);
        super.update(userId, prdOfferingName, prdOfferingShortName, prdCategory, prdApplicableMaster, startDate,
                endDate, description, status);
        validateForUpdate(gracePeriodType, gracePeriodDuration, interestTypes, maxInterestRate, minInterestRate,
                defInterestRate, loanCounter, intDedDisbursement, prinDueLastInst, funds, fees, recurAfter);
        setUpdateDetails(userId);
        setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType, gracePeriodDuration);
        this.interestTypes = interestTypes;
        this.maxInterestRate = maxInterestRate;
        this.minInterestRate = minInterestRate;
        this.defInterestRate = defInterestRate;
        setLoanCounter(loanCounter);
        setIntDedDisbursement(intDedDisbursement);
        setPrinDueLastInst(prinDueLastInst);
        populateLoanAmountAndInstall(loanPrdActionForm);
        if (this.loanOfferingMeeting.getMeeting().getMeetingDetails().getRecurrenceType().getRecurrenceId().equals(
                recurrenceType.getValue())) {
            this.loanOfferingMeeting.getMeeting().getMeetingDetails().setRecurAfter(recurAfter);
        } else {
            try {
                this.loanOfferingMeeting.setMeeting(new MeetingBO(recurrenceType, recurAfter, startDate,
                        MeetingType.LOAN_INSTALLMENT));
            } catch (MeetingException e) {
                throw new ProductDefinitionException(e);
            }
        }

        if (this.loanOfferingFunds != null) {
            this.loanOfferingFunds.clear();
            if (funds != null && funds.size() > 0) {
                for (FundBO fund : funds) {
                    addLoanOfferingFund(new LoanOfferingFundEntity(fund, this));
                }
            }
        }
        if (this.loanOfferingFees != null) {
            this.loanOfferingFees.clear();
            if (fees != null && fees.size() > 0) {
                for (FeeBO fee : fees) {
                    if (isFrequencyMatchingOfferingFrequency(fee, this.loanOfferingMeeting.getMeeting())) {
                        addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
                    }
                }
            }
        }
        try {
            new LoanPrdPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new ProductDefinitionException(e);
        }
        prdLogger.debug("Loan Offering updated:" + prdOfferingName);
    }

    private void validate(final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration,
            final InterestTypesEntity interestTypes, final Money minLoanAmount, final Money maxLoanAmount,
            final Money defaultLoanAmount, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final Short maxNoInstallments, final Short minNoInstallments,
            final Short defNoInstallments, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final MeetingBO meeting,
            final GLCodeEntity principalGLcode, final GLCodeEntity interestGLcode) throws ProductDefinitionException {
        prdLogger.debug("validating fields in Loan offering ");
        if (interestTypes == null || minLoanAmount == null || maxLoanAmount == null || maxInterestRate == null
                || minInterestRate == null || defInterestRate == null || maxNoInstallments == null
                || minNoInstallments == null || defNoInstallments == null || meeting == null || principalGLcode == null
                || interestGLcode == null || minLoanAmount.isGreaterThan(maxLoanAmount) || defaultLoanAmount != null
                && (defaultLoanAmount.isLessThan(minLoanAmount) || defaultLoanAmount.isGreaterThan(maxLoanAmount))
                || minInterestRate > maxInterestRate || defInterestRate < minInterestRate
                || defInterestRate > maxInterestRate || minNoInstallments > maxNoInstallments
                || defNoInstallments < minNoInstallments || defNoInstallments > maxNoInstallments
                || !intDedDisbursement && gracePeriodType != null
                && !gracePeriodType.getId().equals(GraceType.NONE.getValue()) && gracePeriodDuration == null) {
            throw new ProductDefinitionException("errors.create");
        }

        if ((interestTypes.getId().equals(InterestType.DECLINING.getValue()) || interestTypes.getId().equals(
                InterestType.DECLINING_EPI.getValue()))
                && intDedDisbursement) {
            throw new ProductDefinitionException(DECLINEINTERESTDISBURSEMENTDEDUCTION);
        }

    }

    private void validate(final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration,
            final InterestTypesEntity interestTypes, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final MeetingBO meeting,
            final GLCodeEntity principalGLcode, final GLCodeEntity interestGLcode) throws ProductDefinitionException {
        prdLogger.debug("validating fields in Loan offering ");
        if (interestTypes == null || maxInterestRate == null || minInterestRate == null || defInterestRate == null
                || meeting == null || principalGLcode == null || interestGLcode == null
                || minInterestRate > maxInterestRate || defInterestRate < minInterestRate
                || defInterestRate > maxInterestRate || !intDedDisbursement && gracePeriodType != null
                && !gracePeriodType.getId().equals(GraceType.NONE.getValue()) && gracePeriodDuration == null) {
            throw new ProductDefinitionException("errors.create");
        }

        if ((interestTypes.getId().equals(InterestType.DECLINING.getValue()) || interestTypes.getId().equals(
                InterestType.DECLINING_EPI.getValue()))
                && intDedDisbursement) {
            throw new ProductDefinitionException(DECLINEINTERESTDISBURSEMENTDEDUCTION);
        }

    }

    private void setGracePeriodTypeAndDuration(final boolean intDedDisbursement,
            final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration) {
        prdLogger
                .debug("Loan offering setGracePeriodTypeAndDuration called - intDedDisbursement:" + intDedDisbursement);
        if (intDedDisbursement || gracePeriodType == null || gracePeriodType.getId().equals(GraceType.NONE.getValue())) {
            this.gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
            this.gracePeriodDuration = (short) 0;
        } else {
            this.gracePeriodType = gracePeriodType;
            this.gracePeriodDuration = gracePeriodDuration;
        }
        prdLogger.debug("After Loan offering setGracePeriodTypeAndDuration called- gracePeriodType:"
                + this.gracePeriodType + "-gracePeriodDuration :" + this.gracePeriodDuration);
    }

    private boolean isFrequencyMatchingOfferingFrequency(final FeeBO fee, final MeetingBO meeting)
            throws ProductDefinitionException {
        prdLogger.debug("Loan offering isFrequencyMatchingOfferingFrequency called - fee:" + fee);
        if (fee.isOneTime()) {
            return true;
        } else if (fee.getFeeFrequency().getFeeMeetingFrequency().getMeetingDetails().getRecurrenceType()
                .getRecurrenceId().equals(meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId())) {
            return true;
        } else {
            throw new ProductDefinitionException(ERRORFEEFREQUENCY);
        }

    }

    private void validateForUpdate(final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration,
            final InterestTypesEntity interestTypes, final Money minLoanAmount, final Money maxLoanAmount,
            final Money defaultLoanAmount, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final Short maxNoInstallments, final Short minNoInstallments,
            final Short defNoInstallments, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final Short recurAfter)
            throws ProductDefinitionException {
        prdLogger.debug("validating fields in Loan offering for update");
        if (interestTypes == null || minLoanAmount == null || maxLoanAmount == null || maxInterestRate == null
                || minInterestRate == null || defInterestRate == null || maxNoInstallments == null
                || minNoInstallments == null || defNoInstallments == null || recurAfter == null
                || minLoanAmount.isGreaterThan(maxLoanAmount) || defaultLoanAmount != null
                && (defaultLoanAmount.isLessThan(minLoanAmount) || defaultLoanAmount.isGreaterThan(maxLoanAmount))
                || minInterestRate > maxInterestRate || defInterestRate < minInterestRate
                || defInterestRate > maxInterestRate || minNoInstallments > maxNoInstallments
                || defNoInstallments < minNoInstallments || defNoInstallments > maxNoInstallments
                || !intDedDisbursement && gracePeriodType != null
                && !gracePeriodType.getId().equals(GraceType.NONE.getValue()) && gracePeriodDuration == null) {
            throw new ProductDefinitionException("errors.update");
        }
    }

    private void validateForUpdate(final GracePeriodTypeEntity gracePeriodType, final Short gracePeriodDuration,
            final InterestTypesEntity interestTypes, final Double maxInterestRate, final Double minInterestRate,
            final Double defInterestRate, final boolean loanCounter, final boolean intDedDisbursement,
            final boolean prinDueLastInst, final List<FundBO> funds, final List<FeeBO> fees, final Short recurAfter)
            throws ProductDefinitionException {
        prdLogger.debug("validating fields in Loan offering for update");
        if (interestTypes == null || maxInterestRate == null || minInterestRate == null || defInterestRate == null
                || recurAfter == null || minInterestRate > maxInterestRate || defInterestRate < minInterestRate
                || defInterestRate > maxInterestRate || !intDedDisbursement && gracePeriodType != null
                && !gracePeriodType.getId().equals(GraceType.NONE.getValue()) && gracePeriodDuration == null) {
            throw new ProductDefinitionException("errors.update");
        }
    }

    /**
     * This method will return the max min def loan amount in array list for customer based on last loan amount
     */
    public LoanAmountOption eligibleLoanAmount(final Money maxLoanAmount, final Short customerLastLoanCycleCount) {
        // if loan amount type is same for all loan, send one of the
        // LoanAmountOption from list
        if (isLoanAmountTypeSameForAllLoan()) {
            return getEligibleLoanAmountSameForAllLoan();
        }
        if (isLoanAmountTypeAsOfLastLoanAmount()) {
            return getEligibleLoanAmountAsOfLastLoanAmount(maxLoanAmount);
        }
        if (isLoanAmountTypeFromLoanCycle()) {
            return getEligibleLoanAmountFromLoanCycle(customerLastLoanCycleCount);
        }
        return null;
    }

    // Extracted method for finding eligible Loan Amount Range, should only be
    // used when sure that
    // current loan offering is of type from loan cycle
    // returns null otherwise
    public LoanAmountOption getEligibleLoanAmountFromLoanCycle(final Short customerLastLoanCycleCount) {
        {
            if (!isLoanAmountTypeFromLoanCycle()) {
                return null;
            }
            LoanAmountFromLoanCycleBO requiredLoanAmountFromCycle = null;
            try {
                requiredLoanAmountFromCycle = find(getLoanAmountFromLoanCycle(),
                        new Predicate<LoanAmountFromLoanCycleBO>() {
                            public boolean evaluate(final LoanAmountFromLoanCycleBO loanAmountFromLoanCycle)
                                    throws Exception {
                                return loanAmountFromLoanCycle.sameRangeIndex(customerLastLoanCycleCount);
                            }
                        });
            } catch (Exception e) {
            }
            return requiredLoanAmountFromCycle == null ? last(getLoanAmountFromLoanCycle())
                    : requiredLoanAmountFromCycle;
        }
    }

    // Extracted method for finding eligible Loan Amount Range, should only be
    // used when sure that
    // current loan offering is of type from last loan amount
    // returns null otherwise
    public LoanAmountOption getEligibleLoanAmountAsOfLastLoanAmount(final Money maxLoanAmount) {
        {
            if (!isLoanAmountTypeAsOfLastLoanAmount()) {
                return null;
            }
            LoanAmountFromLastLoanAmountBO requiredLoanAmountRangeFromLastLoanAmount = null;
            try {
                requiredLoanAmountRangeFromLastLoanAmount = find(getLoanAmountFromLastLoan(),
                        new Predicate<LoanAmountFromLastLoanAmountBO>() {
                            public boolean evaluate(final LoanAmountFromLastLoanAmountBO lastLoanAmount)
                                    throws Exception {
                                return lastLoanAmount.isLoanAmountBetweenRange(maxLoanAmount.getAmountDoubleValue());
                            }
                        });
            } catch (Exception e) {
            }
            return requiredLoanAmountRangeFromLastLoanAmount == null ? last(getLoanAmountFromLastLoan())
                    : requiredLoanAmountRangeFromLastLoanAmount;
        }
    }

    // Extracted method for finding eligible Loan Amount Range, should only be
    // used when sure that
    // current loan offering is of type same for all loan
    // returns null otherwise
    public LoanAmountSameForAllLoanBO getEligibleLoanAmountSameForAllLoan() {
        if (isLoanAmountTypeSameForAllLoan()) {
            return first(getLoanAmountSameForAllLoan());
        }
        return null;
    }

    public LoanOfferingInstallmentRange eligibleNoOfInstall(final Money lastLoanAmount,
            final Short customerLastLoanCycleCount) {
        if (isNoOfInstallTypeSameForAllLoan()) {
            return getEligibleInstallmentSameForAllLoan();
        }
        if (isNoOfInstallTypeFromLastLoan()) {
            return getEligibleInstallmentFromLastLoanAmount(lastLoanAmount);
        }
        if (isNoOfInstallTypeFromLoanCycle()) {
            return getEligibleInstallmentFromLoanCycle(customerLastLoanCycleCount);
        }
        return null;
    }

    // Extracted method for finding eligible installment number, should only be
    // used when sure that
    // current loan offering is of type same for all loan
    // returns null otherwise
    public LoanOfferingInstallmentRange getEligibleInstallmentFromLoanCycle(final Short customerLastLoanCycleCount) {
        {
            if (!isNoOfInstallTypeFromLoanCycle()) {
                return null;
            }
            LoanOfferingInstallmentRange requiredInstallmentRange = null;
            try {
                requiredInstallmentRange = find(getNoOfInstallFromLoanCycle(),
                        new Predicate<NoOfInstallFromLoanCycleBO>() {
                            public boolean evaluate(final NoOfInstallFromLoanCycleBO noOfInstallFromLoanCycle)
                                    throws Exception {
                                return noOfInstallFromLoanCycle.isSameRange(customerLastLoanCycleCount);
                            }
                        });
            } catch (Exception e) {
            }
            return requiredInstallmentRange == null ? last(getNoOfInstallFromLoanCycle()) : requiredInstallmentRange;
        }
    }

    // Extracted method for finding eligible installment number, should only be
    // used when sure that
    // current loan offering is of type same for all loan
    // returns null otherwise
    public LoanOfferingInstallmentRange getEligibleInstallmentFromLastLoanAmount(final Money lastLoanAmount) {
        {
            if (!isNoOfInstallTypeFromLastLoan()) {
                return null;
            }
            LoanOfferingInstallmentRange requiredInstallmentRange = null;
            try {
                requiredInstallmentRange = find(getNoOfInstallFromLastLoan(),
                        new Predicate<NoOfInstallFromLastLoanAmountBO>() {
                            public boolean evaluate(
                                    final NoOfInstallFromLastLoanAmountBO noOfInstallmentFromLastLoanAmount)
                                    throws Exception {
                                return noOfInstallmentFromLastLoanAmount.loanAmountInRange(lastLoanAmount
                                        .getAmountDoubleValue());
                            }
                        });
            } catch (Exception e) {
            }
            return requiredInstallmentRange == null ? last(getNoOfInstallFromLastLoan()) : requiredInstallmentRange;
        }
    }

    // Extracted method for finding eligible installment number, should only be
    // used when sure that
    // current loan offering is of type same for all loan
    // returns null otherwise
    public LoanOfferingInstallmentRange getEligibleInstallmentSameForAllLoan() {
        if (isNoOfInstallTypeSameForAllLoan()) {
            return first(getNoOfInstallSameForAllLoan());
        }
        return null;
    }

    /**
     * this method will update the variable for save and update as input from loanprdactionform it will first clear all
     * sets of amount/install type than update one of them according to input this will make them mutually exclusive as
     * earlier data will be deleted using the delete-orphan property of hibernate
     */
    private void populateLoanAmountAndInstall(final LoanPrdActionForm loanPrdActionForm) {
        if (Short.parseShort(loanPrdActionForm.getLoanAmtCalcType()) == LOANAMOUNTFROMLASTLOAN) {
            clearLoanAmountData();
            // FIXME: does form logic belong in this class?
            addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(loanPrdActionForm
                    .getLastLoanMinLoanAmt1Value(), loanPrdActionForm.getLastLoanMaxLoanAmt1Value(), loanPrdActionForm
                    .getLastLoanDefaultLoanAmt1Value(), loanPrdActionForm.getStartRangeLoanAmt1().doubleValue(),
                    loanPrdActionForm.getEndRangeLoanAmt1().doubleValue(), this));
            addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(loanPrdActionForm
                    .getLastLoanMinLoanAmt2Value(), loanPrdActionForm.getLastLoanMaxLoanAmt2Value(), loanPrdActionForm
                    .getLastLoanDefaultLoanAmt2Value(), loanPrdActionForm.getStartRangeLoanAmt2().doubleValue(),
                    loanPrdActionForm.getEndRangeLoanAmt2().doubleValue(), this));
            addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(loanPrdActionForm
                    .getLastLoanMinLoanAmt3Value(), loanPrdActionForm.getLastLoanMaxLoanAmt3Value(), loanPrdActionForm
                    .getLastLoanDefaultLoanAmt3Value(), loanPrdActionForm.getStartRangeLoanAmt3().doubleValue(),
                    loanPrdActionForm.getEndRangeLoanAmt3().doubleValue(), this));
            addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(loanPrdActionForm
                    .getLastLoanMinLoanAmt4Value(), loanPrdActionForm.getLastLoanMaxLoanAmt4Value(), loanPrdActionForm
                    .getLastLoanDefaultLoanAmt4Value(), loanPrdActionForm.getStartRangeLoanAmt4().doubleValue(),
                    loanPrdActionForm.getEndRangeLoanAmt4().doubleValue(), this));
            addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(loanPrdActionForm
                    .getLastLoanMinLoanAmt5Value(), loanPrdActionForm.getLastLoanMaxLoanAmt5Value(), loanPrdActionForm
                    .getLastLoanDefaultLoanAmt5Value(), loanPrdActionForm.getStartRangeLoanAmt5().doubleValue(),
                    loanPrdActionForm.getEndRangeLoanAmt5().doubleValue(), this));
            addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(loanPrdActionForm
                    .getLastLoanMinLoanAmt6Value(), loanPrdActionForm.getLastLoanMaxLoanAmt6Value(), loanPrdActionForm
                    .getLastLoanDefaultLoanAmt6Value(), loanPrdActionForm.getStartRangeLoanAmt6().doubleValue(),
                    loanPrdActionForm.getEndRangeLoanAmt6().doubleValue(), this));
        }
        if (Short.parseShort(loanPrdActionForm.getCalcInstallmentType()) == LOANAMOUNTFROMLASTLOAN) {
            clearNoOfInstallmentsData();
            addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(Short.valueOf(loanPrdActionForm
                    .getMinLoanInstallment1()), Short.valueOf(loanPrdActionForm.getMaxLoanInstallment1()), Short
                    .valueOf(loanPrdActionForm.getDefLoanInstallment1()), loanPrdActionForm.getStartInstallmentRange1()
                    .doubleValue(), loanPrdActionForm.getEndInstallmentRange1().doubleValue(), this));
            addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(Short.valueOf(loanPrdActionForm
                    .getMinLoanInstallment2()), Short.valueOf(loanPrdActionForm.getMaxLoanInstallment2()), Short
                    .valueOf(loanPrdActionForm.getDefLoanInstallment2()), loanPrdActionForm.getStartInstallmentRange2()
                    .doubleValue(), loanPrdActionForm.getEndInstallmentRange2().doubleValue(), this));
            addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(Short.valueOf(loanPrdActionForm
                    .getMinLoanInstallment3()), Short.valueOf(loanPrdActionForm.getMaxLoanInstallment3()), Short
                    .valueOf(loanPrdActionForm.getDefLoanInstallment3()), loanPrdActionForm.getStartInstallmentRange3()
                    .doubleValue(), loanPrdActionForm.getEndInstallmentRange3().doubleValue(), this));
            addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(Short.valueOf(loanPrdActionForm
                    .getMinLoanInstallment4()), Short.valueOf(loanPrdActionForm.getMaxLoanInstallment4()), Short
                    .valueOf(loanPrdActionForm.getDefLoanInstallment4()), loanPrdActionForm.getStartInstallmentRange4()
                    .doubleValue(), loanPrdActionForm.getEndInstallmentRange4().doubleValue(), this));
            addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(Short.valueOf(loanPrdActionForm
                    .getMinLoanInstallment5()), Short.valueOf(loanPrdActionForm.getMaxLoanInstallment5()), Short
                    .valueOf(loanPrdActionForm.getDefLoanInstallment5()), loanPrdActionForm.getStartInstallmentRange5()
                    .doubleValue(), loanPrdActionForm.getEndInstallmentRange5().doubleValue(), this));
            addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(Short.valueOf(loanPrdActionForm
                    .getMinLoanInstallment6()), Short.valueOf(loanPrdActionForm.getMaxLoanInstallment6()), Short
                    .valueOf(loanPrdActionForm.getDefLoanInstallment6()), loanPrdActionForm.getStartInstallmentRange6()
                    .doubleValue(), loanPrdActionForm.getEndInstallmentRange6().doubleValue(), this));

        }
        if (Short.parseShort(loanPrdActionForm.getLoanAmtCalcType()) == LOANAMOUNTFROMLOANCYCLE) {
            clearLoanAmountData();
            // FIXME: what are these hardcoded values like new Short("0")? can
            // we use an enum instead?
            addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(loanPrdActionForm.getCycleLoanMinLoanAmt1Value(),
                    loanPrdActionForm.getCycleLoanMaxLoanAmt1Value(), loanPrdActionForm
                            .getCycleLoanDefaultLoanAmt1Value(), new Short("0"), this));
            addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(loanPrdActionForm.getCycleLoanMinLoanAmt2Value(),
                    loanPrdActionForm.getCycleLoanMaxLoanAmt2Value(), loanPrdActionForm
                            .getCycleLoanDefaultLoanAmt2Value(), new Short("1"), this));
            addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(loanPrdActionForm.getCycleLoanMinLoanAmt3Value(),
                    loanPrdActionForm.getCycleLoanMaxLoanAmt3Value(), loanPrdActionForm
                            .getCycleLoanDefaultLoanAmt3Value(), new Short("2"), this));
            addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(loanPrdActionForm.getCycleLoanMinLoanAmt4Value(),
                    loanPrdActionForm.getCycleLoanMaxLoanAmt4Value(), loanPrdActionForm
                            .getCycleLoanDefaultLoanAmt4Value(), new Short("3"), this));
            addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(loanPrdActionForm.getCycleLoanMinLoanAmt5Value(),
                    loanPrdActionForm.getCycleLoanMaxLoanAmt5Value(), loanPrdActionForm
                            .getCycleLoanDefaultLoanAmt5Value(), new Short("4"), this));
            addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(loanPrdActionForm.getCycleLoanMinLoanAmt6Value(),
                    loanPrdActionForm.getCycleLoanMaxLoanAmt6Value(), loanPrdActionForm
                            .getCycleLoanDefaultLoanAmt6Value(), new Short("5"), this));

        }
        if (Short.parseShort(loanPrdActionForm.getCalcInstallmentType()) == LOANAMOUNTFROMLOANCYCLE) {
            clearNoOfInstallmentsData();
            // FIXME: what are these hardcoded values like new Short("0")? can
            // we use an enum instead?
            addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short.valueOf(loanPrdActionForm
                    .getMinCycleInstallment1()), Short.valueOf(loanPrdActionForm.getMaxCycleInstallment1()), Short
                    .valueOf(loanPrdActionForm.getDefCycleInstallment1()), new Short("0"), this));
            addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short.valueOf(loanPrdActionForm
                    .getMinCycleInstallment2()), Short.valueOf(loanPrdActionForm.getMaxCycleInstallment2()), Short
                    .valueOf(loanPrdActionForm.getDefCycleInstallment2()), new Short("1"), this));
            addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short.valueOf(loanPrdActionForm
                    .getMinCycleInstallment3()), Short.valueOf(loanPrdActionForm.getMaxCycleInstallment3()), Short
                    .valueOf(loanPrdActionForm.getDefCycleInstallment3()), new Short("2"), this));
            addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short.valueOf(loanPrdActionForm
                    .getMinCycleInstallment4()), Short.valueOf(loanPrdActionForm.getMaxCycleInstallment4()), Short
                    .valueOf(loanPrdActionForm.getDefCycleInstallment4()), new Short("3"), this));
            addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short.valueOf(loanPrdActionForm
                    .getMinCycleInstallment5()), Short.valueOf(loanPrdActionForm.getMaxCycleInstallment5()), Short
                    .valueOf(loanPrdActionForm.getDefCycleInstallment5()), new Short("4"), this));
            addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short.valueOf(loanPrdActionForm
                    .getMinCycleInstallment6()), Short.valueOf(loanPrdActionForm.getMaxCycleInstallment6()), Short
                    .valueOf(loanPrdActionForm.getDefCycleInstallment6()), new Short("5"), this));
        }
        if (Short.parseShort(loanPrdActionForm.getLoanAmtCalcType()) == LOANAMOUNTSAMEFORALLLOAN) {
            clearLoanAmountData();
            loanAmountSameForAllLoan.add(new LoanAmountSameForAllLoanBO(loanPrdActionForm.getMinLoanAmountValue(),
                    loanPrdActionForm.getMaxLoanAmountValue(), loanPrdActionForm.getDefaultLoanAmountValue(), this));
        }
        if (Short.parseShort(loanPrdActionForm.getCalcInstallmentType()) == LOANAMOUNTSAMEFORALLLOAN) {
            clearNoOfInstallmentsData();
            noOfInstallSameForAllLoan.add(new NoOfInstallSameForAllLoanBO(Short.parseShort(loanPrdActionForm
                    .getMinNoInstallments()), Short.parseShort(loanPrdActionForm.getMaxNoInstallments()), Short
                    .parseShort(loanPrdActionForm.getDefNoInstallments()), this));
        }
    }

    private void clearLoanAmountData() {
        this.loanAmountFromLastLoan.clear();
        this.loanAmountFromLoanCycle.clear();
        this.loanAmountSameForAllLoan.clear();
    }

    private void clearNoOfInstallmentsData() {
        this.noOfInstallSameForAllLoan.clear();
        this.noOfInstallFromLastLoan.clear();
        this.noOfInstallFromLoanCycle.clear();
    }

    /**
     * Reveals method used to determine loan amount. For instance: same for all loans, based on last loan, or based on
     * loan cycle.
     */
    public Short checkLoanAmountType() {
        if (!getLoanAmountSameForAllLoan().isEmpty()) {
            return LOANAMOUNTSAMEFORALLLOAN;
        } else if (!getLoanAmountFromLastLoan().isEmpty()) {
            return LOANAMOUNTFROMLASTLOAN;
        } else if (!getLoanAmountFromLoanCycle().isEmpty()) {
            return LOANAMOUNTFROMLOANCYCLE;
        }
        return LOANAMOUNTTYPE_UNKNOWN;
    }

    /**
     * Reveals method used to determine number of loan installments. For instance: same for all loans, based on last
     * loan, or based on loan cycle.
     */
    public Short checkNoOfInstallType() {
        if (!getNoOfInstallSameForAllLoan().isEmpty()) {
            return NOOFINSTALLSAMEFORALLLOAN;
        } else if (!getNoOfInstallFromLastLoan().isEmpty()) {
            return NOOFINSTALLFROMLASTLOAN;
        } else if (!getNoOfInstallFromLoanCycle().isEmpty()) {
            return NOOFINSTALLFROMLOANCYCLLE;
        }
        return NOOFINSTALL_UNKNOWN;
    }

    @Override
    public boolean isActive() {
        return getStatus() == PrdStatus.LOAN_ACTIVE;
    }

    public Set<LoanAmountFromLastLoanAmountBO> getLoanAmountFromLastLoan() {
        return loanAmountFromLastLoan;
    }

    public Set<LoanAmountFromLoanCycleBO> getLoanAmountFromLoanCycle() {
        return loanAmountFromLoanCycle;
    }

    public Set<NoOfInstallFromLastLoanAmountBO> getNoOfInstallFromLastLoan() {
        return noOfInstallFromLastLoan;
    }

    public Set<NoOfInstallFromLoanCycleBO> getNoOfInstallFromLoanCycle() {
        return noOfInstallFromLoanCycle;
    }

    public Set<LoanAmountSameForAllLoanBO> getLoanAmountSameForAllLoan() {
        return loanAmountSameForAllLoan;
    }

    public Set<NoOfInstallSameForAllLoanBO> getNoOfInstallSameForAllLoan() {
        return noOfInstallSameForAllLoan;
    }

    public boolean isLoanAmountTypeAsOfLastLoanAmount() {
        return LOANAMOUNTFROMLASTLOAN.equals(checkLoanAmountType());
    }

    public boolean isLoanAmountTypeSameForAllLoan() {
        return LOANAMOUNTSAMEFORALLLOAN.equals(checkLoanAmountType());
    }

    public boolean isLoanAmountTypeFromLoanCycle() {
        return LOANAMOUNTFROMLOANCYCLE.equals(checkLoanAmountType());
    }

    public boolean isNoOfInstallTypeFromLastLoan() {
        return NOOFINSTALLFROMLASTLOAN.equals(checkNoOfInstallType());
    }

    public boolean isNoOfInstallTypeSameForAllLoan() {
        return NOOFINSTALLSAMEFORALLLOAN.equals(checkNoOfInstallType());
    }

    public boolean isNoOfInstallTypeFromLoanCycle() {
        return NOOFINSTALLFROMLOANCYCLLE.equals(checkNoOfInstallType());
    }

    public boolean isLoanAmountOfTypeUnknown() {
        return LOANAMOUNTTYPE_UNKNOWN.equals(checkLoanAmountType());

    }

    public boolean isNoOfInstallTypeUnknown() {
        return NOOFINSTALL_UNKNOWN.equals(checkNoOfInstallType());
    }

    public void setLoanAmountSameForAllLoan(final LoanAmountSameForAllLoanBO loanAmountSameForAllLoan) {
        getLoanAmountSameForAllLoan().clear();
        getLoanAmountSameForAllLoan().add(loanAmountSameForAllLoan);
    }

    public void setNoOfInstallSameForAllLoan(final NoOfInstallSameForAllLoanBO noOfInstallSameForAllLoan) {
        getNoOfInstallSameForAllLoan().clear();
        getNoOfInstallSameForAllLoan().add(noOfInstallSameForAllLoan);
    }
}