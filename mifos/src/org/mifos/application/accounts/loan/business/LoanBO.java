/**

 * LoanBO.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */

package org.mifos.application.accounts.loan.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanPaymentTypes;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.FeeInstallment;
import org.mifos.application.accounts.util.helpers.InstallmentDate;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.RankOfDaysEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productsmix.persistence.ProductMixPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.config.AccountingRules;
import java.math.BigDecimal;

public class LoanBO extends AccountBO {

	private final LoanOfferingBO loanOffering;

	private final LoanSummaryEntity loanSummary;

	private final MaxMinLoanAmount maxMinLoanAmount;

	private final MaxMinNoOfInstall maxMinNoOfInstall;

	private LoanPrdPersistence loanPrdPersistence;

	private Money loanAmount;

	private Money loanBalance;

	private Short noOfInstallments;

	private Date disbursementDate;

	private MeetingBO loanMeeting;

	private Short intrestAtDisbursement;

	private GracePeriodTypeEntity gracePeriodType;

	private Short gracePeriodDuration;

	private Short gracePeriodPenalty;

	private final LoanPerformanceHistoryEntity performanceHistory;

	private InterestTypesEntity interestType;

	private Double interestRate;

	private FundBO fund;

	private Boolean redone;

	/**
	 Is this used?  Is it related to the activity IDs in places
	 like
	 {@link ActivityMapper#SAVING_CANCHANGESTATETO_PARTIALAPPLICATION} 
	 or {@link SecurityConstants#FUNDS_CREATE_FUNDS} ?
	 */
	private Integer businessActivityId;

	private CollateralTypeEntity collateralType;

	private String collateralNote;

	private Short groupFlag;

	private String stateSelected;

	private Set<LoanActivityEntity> loanActivityDetails;

	private LoanArrearsAgingEntity loanArrearsAgingEntity;

	// For Group loan with individual monitoring
	
	private LoanBO parentAccount;

	private Set<LoanBO> loanAccountDetails;

	// For Repayment Day
	
	private WeekDaysEntity monthWeek;

	private RankOfDaysEntity monthRank;

	private Short recurMonth;
	

	protected LoanBO() {
		super();
		this.loanOffering = null;
		this.loanSummary = null;
		this.loanPrdPersistence = null;
		this.performanceHistory = null;
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
        this.redone = false;
 		this.maxMinLoanAmount = null;
		this.maxMinNoOfInstall = null;
    	parentAccount=null;
    	loanAccountDetails=new HashSet<LoanBO>();
	}

	public static LoanBO redoLoan(UserContext userContext,
			LoanOfferingBO loanOffering, CustomerBO customer,
			AccountState accountState, Money loanAmount,
			Short noOfinstallments, Date disbursementDate,
			boolean interestDeductedAtDisbursement, Double interestRate,
			Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
			List<CustomFieldView> customFields,boolean isRepaymentIndepOfMeetingEnabled,MeetingBO newMeetingForRepaymentDay) throws AccountException {
		if (loanOffering == null || loanAmount == null
				|| noOfinstallments == null || disbursementDate == null
				|| interestRate == null)
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);

		if (!customer.isActive()) {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
		}

		if (!loanOffering.isActive()) {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);
		}

		if (interestDeductedAtDisbursement == true
				&& noOfinstallments.shortValue() <= 1) {
			throw new AccountException(
					LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
		}

		if (isRepaymentIndepOfMeetingEnabled == false)
			if (!isDisbursementDateValid(customer, disbursementDate)) {
				throw new AccountException(
						LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
			}            
        return new LoanBO(userContext, loanOffering, customer, accountState, loanAmount,
                noOfinstallments, disbursementDate, interestDeductedAtDisbursement,
                interestRate, gracePeriodDuration, fund, feeViews, customFields, true,isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
    }

    public static LoanBO createLoan(UserContext userContext, LoanOfferingBO loanOffering,
                                      CustomerBO customer, AccountState accountState, Money loanAmount,
                                      Short noOfinstallments, Date disbursementDate,
                                      boolean interestDeductedAtDisbursement, Double interestRate,
                                      Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
                                      List<CustomFieldView> customFields)
            throws AccountException {
        if (loanOffering == null || loanAmount == null
                || noOfinstallments == null || disbursementDate == null
                || interestRate == null || customer == null)
            throw new AccountException(
                    AccountExceptionConstants.CREATEEXCEPTION);
              
        if (!customer.isActive()) {
        	
            throw new AccountException(
                    AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
        }

        if (!loanOffering.isActive()) 
        	throw new AccountException(
                    AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);
        

        if (isDisbursementDateLessThanCurrentDate(disbursementDate))
			throw new AccountException(
					LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);
        

        if (! isDisbursementDateValid(customer, disbursementDate))          
            throw new AccountException(
                LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
        

        if (interestDeductedAtDisbursement == true
                && noOfinstallments.shortValue() <= 1)       	
            throw new AccountException(
                    LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
    
        return new LoanBO(userContext, loanOffering, customer, accountState, loanAmount, 
                noOfinstallments, disbursementDate, interestDeductedAtDisbursement,
                interestRate, gracePeriodDuration, fund, feeViews, customFields, false,false,null);
    }






    
    public static LoanBO createIndividualLoan(UserContext userContext,
			LoanOfferingBO loanOffering, CustomerBO customer,
			AccountState accountState, Money loanAmount,
			Short noOfinstallments, Date disbursementDate,
			boolean interestDeductedAtDisbursement, Double interestRate,
			Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
			List<CustomFieldView> customFields) throws AccountException {
		if (loanOffering == null || loanAmount == null
				|| noOfinstallments == null || disbursementDate == null
				|| interestRate == null || customer == null)
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);

		if (!customer.isActive()) {

			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
		}

		if (!loanOffering.isActive())
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);


		if (isDisbursementDateLessThanCurrentDate(disbursementDate))
			throw new AccountException(
					LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);


		if (!isDisbursementDateValid(customer, disbursementDate))
			throw new AccountException(
					LoanExceptionConstants.INVALIDDISBURSEMENTDATE);


		if (interestDeductedAtDisbursement == true
				&& noOfinstallments.shortValue() <= 1)
			throw new AccountException(
					LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);

		return new LoanBO(userContext, loanOffering, customer, accountState,
				loanAmount, noOfinstallments, disbursementDate,
				interestDeductedAtDisbursement, interestRate,
				gracePeriodDuration, fund, feeViews, customFields, false,AccountTypes.INDIVIDUAL_LOAN_ACCOUNT,false,null);
	}

    
 
	public static LoanBO createLoan(UserContext userContext,
			LoanOfferingBO loanOffering, CustomerBO customer,
			AccountState accountState, Money loanAmount,
			Short noOfinstallments, Date disbursementDate,
			boolean interestDeductedAtDisbursement, Double interestRate,
			Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
			List<CustomFieldView> customFields, Double maxLoanAmount,
			Double minLoanAmount, Short maxNoOfInstall, Short minNoOfInstall,
			boolean isRepaymentIndepOfMeetingEnabled,MeetingBO newMeetingForRepaymentDay)
			throws AccountException {
		if (loanOffering == null || loanAmount == null
				|| noOfinstallments == null || disbursementDate == null
				|| interestRate == null || customer == null)
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);

		if (!customer.isActive()) {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
		}

		if (!loanOffering.isActive()) {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);
		}
		
		if (isDisbursementDateLessThanCurrentDate(disbursementDate)) {
				throw new AccountException(
						LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);
			}
		
		if (isRepaymentIndepOfMeetingEnabled == false)
			if (!isDisbursementDateValid(customer, disbursementDate)) {
				throw new AccountException(
						LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
			}

		if (interestDeductedAtDisbursement == true
				&& noOfinstallments.shortValue() <= 1) {
			throw new AccountException(
					LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
		}

		return new LoanBO(userContext, loanOffering, customer, accountState,
				loanAmount, noOfinstallments, disbursementDate,
				interestDeductedAtDisbursement, interestRate,
				gracePeriodDuration, fund, feeViews, customFields, false,
				maxLoanAmount, minLoanAmount, maxNoOfInstall, minNoOfInstall,isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
	}
        
    private LoanBO(UserContext userContext, LoanOfferingBO loanOffering,
			CustomerBO customer, AccountState accountState, Money loanAmount,
			Short noOfinstallments, Date disbursementDate,
			boolean interestDeductedAtDisbursement, Double interestRate,
			Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
			List<CustomFieldView> customFields, Boolean isRedone,boolean isRepaymentIndepOfMeetingEnabled,MeetingBO newMeetingForRepaymentDay)
			throws AccountException {
		super(userContext, customer, AccountTypes.LOAN_ACCOUNT, accountState);

        setCreateDetails();
		this.redone = isRedone;
        this.loanOffering = loanOffering;
		this.loanAmount = loanAmount;
		this.loanBalance = loanAmount;
		this.noOfInstallments = noOfinstallments;
		this.interestType = loanOffering.getInterestTypes();
		this.interestRate = interestRate;
		setInterestDeductedAtDisbursement(interestDeductedAtDisbursement);	 	
		setGracePeriodTypeAndDuration(interestDeductedAtDisbursement,
				gracePeriodDuration, noOfinstallments);
	    	
		this.gracePeriodPenalty = Short.valueOf("0");
		this.fund = fund;
		this.loanMeeting = buildLoanMeeting(customer.getCustomerMeeting()
				.getMeeting(), loanOffering.getLoanOfferingMeeting()
				.getMeeting(), disbursementDate);
	    buildAccountFee(feeViews);	    	
		this.disbursementDate = disbursementDate;
		this.performanceHistory = new LoanPerformanceHistoryEntity(this);
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
	    generateMeetingSchedule(isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
	    this.loanSummary = buildLoanSummary();
	    this.maxMinLoanAmount = null;
		this.maxMinNoOfInstall = null;
		addcustomFields(customFields);
	}
    
    private LoanBO(UserContext userContext, LoanOfferingBO loanOffering,
			CustomerBO customer, AccountState accountState, Money loanAmount,
			Short noOfinstallments, Date disbursementDate,
			boolean interestDeductedAtDisbursement, Double interestRate,
			Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
			List<CustomFieldView> customFields, Boolean isRedone,AccountTypes accountType,boolean isRepaymentIndepOfMeetingEnabled,MeetingBO newMeetingForRepaymentDay) throws AccountException {
		super(userContext, customer, accountType, accountState);

        setCreateDetails();
		
        this.redone = isRedone;
        this.loanOffering = loanOffering;
		this.loanAmount = loanAmount;
		this.loanBalance = loanAmount;
		this.noOfInstallments = noOfinstallments;
		this.interestType = loanOffering.getInterestTypes();
		this.interestRate = interestRate;
		setInterestDeductedAtDisbursement(interestDeductedAtDisbursement);	 	
		setGracePeriodTypeAndDuration(interestDeductedAtDisbursement,
				gracePeriodDuration, noOfinstallments);
	    	
		this.gracePeriodPenalty = Short.valueOf("0");
		this.fund = fund;
		this.loanMeeting = buildLoanMeeting(customer.getCustomerMeeting()
				.getMeeting(), loanOffering.getLoanOfferingMeeting()
				.getMeeting(), disbursementDate);
	    buildAccountFee(feeViews);
	    this.disbursementDate = disbursementDate;
		this.performanceHistory = new LoanPerformanceHistoryEntity(this);
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
	    generateMeetingSchedule(isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
	    this.loanSummary = buildLoanSummary();
		this.maxMinLoanAmount = null;
		this.maxMinNoOfInstall = null;
		addcustomFields(customFields);
	    
	}


	private LoanBO(UserContext userContext, LoanOfferingBO loanOffering,
			CustomerBO customer, AccountState accountState, Money loanAmount,
			Short noOfinstallments, Date disbursementDate,
			boolean interestDeductedAtDisbursement, Double interestRate,
			Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
			List<CustomFieldView> customFields, Boolean isRedone,
			Double maxLoanAmount, Double minLoanAmount, Short maxNoOfInstall,
			Short minNoOfInstall,boolean isRepaymentIndepOfMeetingEnabled,MeetingBO newMeetingForRepaymentDay) throws AccountException {
		super(userContext, customer, AccountTypes.LOAN_ACCOUNT, accountState);

		setCreateDetails();
		this.redone = isRedone;
		this.loanOffering = loanOffering;
		this.loanAmount = loanAmount;
		this.loanBalance = loanAmount;
		this.noOfInstallments = noOfinstallments;
		this.interestType = loanOffering.getInterestTypes();
		this.interestRate = interestRate;
		setInterestDeductedAtDisbursement(interestDeductedAtDisbursement);
		setGracePeriodTypeAndDuration(interestDeductedAtDisbursement,
				gracePeriodDuration, noOfinstallments);
		this.gracePeriodPenalty = Short.valueOf("0");
		this.fund = fund;
		this.loanMeeting = buildLoanMeeting(customer.getCustomerMeeting()
				.getMeeting(), loanOffering.getLoanOfferingMeeting()
				.getMeeting(), disbursementDate);
		buildAccountFee(feeViews);
		this.disbursementDate = disbursementDate;
		this.performanceHistory = new LoanPerformanceHistoryEntity(this);
		this.loanActivityDetails = new HashSet<LoanActivityEntity>();
		generateMeetingSchedule(isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
		this.loanSummary = buildLoanSummary();
		this.maxMinLoanAmount = new MaxMinLoanAmount(maxLoanAmount,
				minLoanAmount, this);
		this.maxMinNoOfInstall = new MaxMinNoOfInstall(maxNoOfInstall,
				minNoOfInstall, this);
		addcustomFields(customFields);
	}

	public Integer getBusinessActivityId() {
		return businessActivityId;
	}

	public void setBusinessActivityId(Integer businessActivityId) {
		this.businessActivityId = businessActivityId;
	}

	public String getCollateralNote() {
		return collateralNote;
	}

	public void setCollateralNote(String collateralNote) {
		this.collateralNote = collateralNote;
	}

	public CollateralTypeEntity getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(CollateralTypeEntity collateralType) {
		this.collateralType = collateralType;
	}

	public GracePeriodTypeEntity getGracePeriodType() {
		return gracePeriodType;
	}

	public GraceType getGraceType() {
		return gracePeriodType.asEnum();
	}

	void setGracePeriodType(GracePeriodTypeEntity gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	void setGracePeriodType(GraceType type) {
		setGracePeriodType(new GracePeriodTypeEntity(type));
	}

	public Date getDisbursementDate() {
		return disbursementDate;
	}

	void setDisbursementDate(Date disbursementDate) {
		this.disbursementDate = disbursementDate;
	}

	public FundBO getFund() {
		return fund;
	}

	public void setFund(FundBO fund) {
		this.fund = fund;
	}

	public Short getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	void setGracePeriodDuration(Short gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	public Short getGracePeriodPenalty() {
		return gracePeriodPenalty;
	}

	void setGracePeriodPenalty(Short gracePeriodPenalty) {
		this.gracePeriodPenalty = gracePeriodPenalty;
	}

	public Short getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(Short groupFlag) {
		this.groupFlag = groupFlag;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public InterestTypesEntity getInterestType() {
		return interestType;
	}

	void setInterestType(InterestTypesEntity interestType) {
		this.interestType = interestType;
	}

	public boolean isInterestDeductedAtDisbursement() {
		return (intrestAtDisbursement != null && intrestAtDisbursement
				.shortValue() == LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT
				.shortValue()) ? true : false;
	}

	void setInterestDeductedAtDisbursement(boolean interestDedAtDisb) {
		this.intrestAtDisbursement = interestDedAtDisb ? Constants.YES
				: Constants.NO;
	}

	public Money getLoanAmount() {
		return loanAmount;
	}

	void setLoanAmount(Money loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Money getLoanBalance() {
		return loanBalance;
	}

	void setLoanBalance(Money loanBalance) {
		this.loanBalance = loanBalance;
	}

	public MeetingBO getLoanMeeting() {
		return loanMeeting;
	}

	void setLoanMeeting(MeetingBO loanMeeting) {
		this.loanMeeting = loanMeeting;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public LoanSummaryEntity getLoanSummary() {
		return loanSummary;
	}

	public Short getNoOfInstallments() {
		return noOfInstallments;
	}

	void setNoOfInstallments(Short noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}

	public String getStateSelected() {
		return stateSelected;
	}

	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}

	public LoanPerformanceHistoryEntity getPerformanceHistory() {
		return performanceHistory;
	}

	public Set<LoanActivityEntity> getLoanActivityDetails() {
		return loanActivityDetails;
	}

	public void addLoanActivity(LoanActivityEntity loanActivity) {
		this.loanActivityDetails.add(loanActivity);
	}

	public LoanArrearsAgingEntity getLoanArrearsAgingEntity() {
		return loanArrearsAgingEntity;
	}

	public void setLoanArrearsAgingEntity(
			LoanArrearsAgingEntity loanArrearsAgingEntity) {
		this.loanArrearsAgingEntity = loanArrearsAgingEntity;
	}

	@Override
	public AccountTypes getType() {
		return AccountTypes.LOAN_ACCOUNT;
	}

	@Override
	public boolean isOpen() {
		return !(getAccountState().getId().equals(
				AccountState.LOAN_CANCELLED.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOAN_CLOSED_RESCHEDULED.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue()) || getAccountState()
				.getId().equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue()));
	}

	@Override
	protected void updateTotalFeeAmount(Money totalFeeAmount) {
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees()
				.subtract(totalFeeAmount));
	}

	@Override
	protected void updateTotalPenaltyAmount(Money totalPenaltyAmount) {
		LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
		loanSummaryEntity.setOriginalPenalty(loanSummaryEntity
				.getOriginalPenalty().subtract(totalPenaltyAmount));
	}

	@Override
	public boolean isAdjustPossibleOnLastTrxn() {
		// adjustment is possible only if account state is
		// 1. active in good standing.
		// 2. active in bad standing.
		// 3. Closed - Obligation Met : Check permisssion first ; Can adjust payment when account status is "closed-obligation met"

		if (!(getAccountState().getId().equals(
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue())
			|| getAccountState().getId().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue()) || getAccountState()
				.getId().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue()))) {

			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"State is not active hence adjustment is not possible");
			return false;
		}

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Total payments on this account is  "
						+ getAccountPayments().size());
		AccountPaymentEntity accountPayment = getLastPmntToBeAdjusted();
		if (accountPayment != null) {
			for (AccountTrxnEntity accntTrxn : accountPayment.getAccountTrxns()) {
				LoanTrxnDetailEntity lntrxn = (LoanTrxnDetailEntity) accntTrxn;
				if (lntrxn.getInstallmentId().equals(Short.valueOf("0"))
						|| isAdjustmentForInterestDedAtDisb(lntrxn
								.getInstallmentId())) {
					return false;
				}
			}
		}
		if (null != getLastPmntToBeAdjusted()
				&& getLastPmntAmntToBeAdjusted() != 0) {
			return true;
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Adjustment is not possible ");
		return false;
	}

	@Override
	protected void updateAccountActivity(Money principal, Money interest,
			Money fee, Money penalty, Short personnelId, String description)
			throws AccountException {
		try {
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(personnelId);
			LoanActivityEntity loanActivity = new LoanActivityEntity(this,
					personnel, description, principal, loanSummary
							.getOriginalPrincipal().subtract(
									loanSummary.getPrincipalPaid()), interest,
					loanSummary.getOriginalInterest().subtract(
							loanSummary.getInterestPaid()), fee, loanSummary
							.getOriginalFees().subtract(
									loanSummary.getFeesPaid()), penalty,
					loanSummary.getOriginalPenalty().subtract(
							loanSummary.getPenaltyPaid()), DateUtils
							.getCurrentDateWithoutTimeStamp());
			this.addLoanActivity(loanActivity);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	@Override
	public void waiveAmountDue(WaiveEnum waiveType) throws AccountException {
		if (waiveType.equals(WaiveEnum.FEES)) {
			waiveFeeAmountDue();
		}
		else if (waiveType.equals(WaiveEnum.PENALTY)) {
			waivePenaltyAmountDue();
		}
	}

	@Override
	public void waiveAmountOverDue(WaiveEnum waiveType) throws AccountException {
		if (waiveType.equals(WaiveEnum.FEES)) {
			waiveFeeAmountOverDue();
		}
		else if (waiveType.equals(WaiveEnum.PENALTY)) {
			waivePenaltyAmountOverDue();
		}
	}

	public Money getTotalPrincipalAmount() {
		Money amount = new Money();
		List<AccountActionDateEntity> installments = getAllInstallments();
		for (AccountActionDateEntity accountActionDateEntity : installments) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipal());
		}
		return amount;
	}

	public Money getTotalPrincipalAmountInArrears() {
		Money amount = new Money();
		List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipal());
		}
		return amount;
	}

	public Money getTotalPrincipalAmountInArrearsAndOutsideLateness()
			throws PersistenceException {
		Money amount = new Money();
		loanPrdPersistence = new LoanPrdPersistence();
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
			if (!accountActionDateEntity.isPaid()
					&& currentDate.getTime()
							- accountActionDateEntity.getActionDate().getTime() > loanPrdPersistence
							.retrieveLatenessForPrd().intValue()
							* 24 * 60 * 60 * 1000) {
				amount = amount
						.add(((LoanScheduleEntity) accountActionDateEntity)
								.getPrincipalDue());
			}
		}
		return amount;
	}

	public Money getTotalInterestAmountInArrears() {
		Money amount = new Money();
		List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getInterest());
		}
		return amount;
	}

	public Money getTotalInterestAmountInArrearsAndOutsideLateness()
			throws PersistenceException {
		Money amount = new Money();
		loanPrdPersistence = new LoanPrdPersistence();
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
			if (currentDate.getTime()
					- accountActionDateEntity.getActionDate().getTime() > loanPrdPersistence
					.retrieveLatenessForPrd().intValue()
					* 24 * 60 * 60 * 1000) {
				amount = amount
						.add(((LoanScheduleEntity) accountActionDateEntity)
								.getInterest());
			}
		}
		return amount;
	}

	@Override
	public final void removeFees(Short feeId, Short personnelId)
			throws AccountException {
		List<Short> installmentIds = getApplicableInstallmentIdsForRemoveFees();
		Money totalFeeAmount = new Money();
		if (installmentIds != null && installmentIds.size() != 0
				&& isFeeActive(feeId)) {
			totalFeeAmount = updateAccountActionDateEntity(installmentIds,
					feeId);
			updateAccountFeesEntity(feeId);
			updateTotalFeeAmount(totalFeeAmount);
			FeeBO feesBO = getAccountFeesObject(feeId);
			String description = feesBO.getFeeName() + " "
					+ AccountConstants.FEES_REMOVED;
			updateAccountActivity(null, null, totalFeeAmount, null,
					personnelId, description);
			applyRounding();
			try {
				(new AccountPersistence()).createOrUpdate(this);
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}

	}

	@Override
	public Money updateAccountActionDateEntity(List<Short> intallmentIdList,
			Short feeId) {
		Money totalFeeAmount = new Money();
		Set<AccountActionDateEntity> accountActionDateEntitySet = this
				.getAccountActionDates();
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
			if (intallmentIdList.contains(accountActionDateEntity
					.getInstallmentId())) {
				totalFeeAmount = totalFeeAmount
						.add(((LoanScheduleEntity) accountActionDateEntity)
								.removeFees(feeId));
			}
		}
		return totalFeeAmount;
	}

	@Override
	public void applyCharge(Short feeId, Double charge) throws AccountException {
		List<AccountActionDateEntity> dueInstallments = getTotalDueInstallments();
		if (dueInstallments.isEmpty())
			throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
		if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
				|| feeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
			applyMiscCharge(feeId, new Money(String.valueOf(charge)),
					dueInstallments.get(0));
			applyRounding();
		}
		else {
			FeeBO fee = new FeePersistence().getFee(feeId);
			if (fee.getFeeFrequency().getFeePayment() != null) {
				applyOneTimeFee(fee, charge, dueInstallments.get(0));
			}
			else {
				applyPeriodicFee(fee, charge, dueInstallments);
			}
			applyRounding();
		}
	}

	/**
	 * It calculates over due amounts till installment 1 less than the one
	 * passed,because whatever amount is associated with the current installment
	 * it is the due amount and not the over due amount. It calculates that by
	 * iterating over the accountActionDates associated and summing up all the
	 * principal and principalPaid till installment-1 and then returning the
	 * difference of the two.It also takes into consideration any miscellaneous
	 * fee or miscellaneous penalty.
	 * 
	 * @param installmentId -
	 *            Installment id till which we want over due amounts.
	 * 
	 */
	public OverDueAmounts getOverDueAmntsUptoInstallment(Short installmentId)
			throws AccountException {
		Set<AccountActionDateEntity> accountActionDateEntities = getAccountActionDates();
		OverDueAmounts totalOverDueAmounts = new OverDueAmounts();
		if (null != accountActionDateEntities
				&& accountActionDateEntities.size() > 0) {
			Iterator<AccountActionDateEntity> accountActionDatesIterator = accountActionDateEntities
					.iterator();
			while (accountActionDatesIterator.hasNext()) {
				LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDatesIterator
						.next();

				if (accountActionDateEntity.getInstallmentId() < installmentId) {
					OverDueAmounts dueAmounts = new OverDueAmounts();
					dueAmounts = accountActionDateEntity.getDueAmnts();
					totalOverDueAmounts.add(dueAmounts);
				}
			}
		}
		return totalOverDueAmounts;
	}

	public void disburseLoan(String recieptNum, Date transactionDate,
			Short paymentTypeId, PersonnelBO personnel, Date receiptDate,
			Short rcvdPaymentTypeId) throws AccountException {
		disburseLoan(recieptNum, transactionDate, paymentTypeId, personnel,
				receiptDate, rcvdPaymentTypeId, true);
	}

	public void disburseLoan(PersonnelBO personnel, Short rcvdPaymentTypeId,
			boolean persistChange) throws AccountException {
		disburseLoan(null, getDisbursementDate(), rcvdPaymentTypeId, personnel,
				null, rcvdPaymentTypeId, persistChange);
	}

	public void disburseLoan(String recieptNum, Date transactionDate,
			Short paymentTypeId, PersonnelBO personnel, Date receiptDate,
			Short rcvdPaymentTypeId, boolean persistChange)
			throws AccountException {
		AccountPaymentEntity accountPaymentEntity = null;
		addLoanActivity(buildLoanActivity(this.loanAmount, personnel,
				AccountConstants.LOAN_DISBURSAL, transactionDate));

		// if the trxn date is not equal to disbursementDate we need to
		// regenerate the installments
		if (!DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime())
				.equals(
						DateUtils.getDateWithoutTimeStamp(transactionDate
								.getTime()))) {
			this.disbursementDate = transactionDate;
			regeneratePaymentSchedule(false,null);
		}
		this.disbursementDate = transactionDate;
		AccountStateEntity newState;
		try {
			newState = (AccountStateEntity) (new MasterPersistence())
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_ACTIVEINGOODSTANDING);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}

		// update status change history also
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						this.getAccountState(), newState, personnel, this));
		this.setAccountState(newState);

		if (this.isInterestDeductedAtDisbursement()) {
			accountPaymentEntity = payInterestAtDisbursement(recieptNum,
					transactionDate, rcvdPaymentTypeId, personnel, receiptDate);
		}
		else {
			try {
				if (new LoanPersistence().getFeeAmountAtDisbursement(this
						.getAccountId()) > 0.0)
					accountPaymentEntity = insertOnlyFeeAtDisbursement(
							recieptNum, transactionDate, rcvdPaymentTypeId,
							personnel);
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
		if (null == accountPaymentEntity) {
			accountPaymentEntity = new AccountPaymentEntity(this,
					this.loanAmount, recieptNum, transactionDate,
					new PaymentTypeEntity(paymentTypeId));
		}
		else {
			accountPaymentEntity.setAmount(this.loanAmount
					.subtract(accountPaymentEntity.getAmount()));
		}

		// create trxn entry for disbursal
		LoanTrxnDetailEntity loanTrxnDetailEntity = null;
		try {
			loanTrxnDetailEntity = new LoanTrxnDetailEntity(
					accountPaymentEntity,
					(AccountActionEntity) new MasterPersistence()
							.getPersistentObject(AccountActionEntity.class,
									AccountActionTypes.DISBURSAL.getValue()),
					Short.valueOf("0"), transactionDate, personnel,
					transactionDate, this.loanAmount, "-", null,
					this.loanAmount, new Money(), new Money(), new Money(),
					new Money(), null);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}

		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);
		this.addAccountPayment(accountPaymentEntity);
		this.buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

		// Client performance entry
		updateCustomerHistoryOnDisbursement(this.loanAmount);
		if (getPerformanceHistory() != null)
			getPerformanceHistory().setLoanMaturityDate(
					getLastInstallmentAccountAction().getActionDate());

		if (persistChange) {
			try {
				new AccountPersistence().createOrUpdate(this);
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
	}

	public Money getTotalEarlyRepayAmount() {
		Money amount = new Money();
		List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
		List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getTotalDueWithFees());
		}

		for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipalDue());
		}
		return amount;
	}

	public void makeEarlyRepayment(Money totalAmount, String receiptNumber,
			Date recieptDate, String paymentTypeId, Short personnelId)
			throws AccountException {
		try {
			MasterPersistence masterPersistence = new MasterPersistence();
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(personnelId);
			this.setUpdatedBy(personnelId);
			this.setUpdatedDate(new Date(System.currentTimeMillis()));
			AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
					this, totalAmount, receiptNumber, recieptDate,
					new PaymentTypeEntity(Short.valueOf(paymentTypeId)));
			addAccountPayment(accountPaymentEntity);

			makeEarlyRepaymentForDueInstallments(accountPaymentEntity,
					AccountConstants.PAYMENT_RCVD,
					AccountActionTypes.LOAN_REPAYMENT);
			makeEarlyRepaymentForFutureInstallments(accountPaymentEntity,
					AccountConstants.PAYMENT_RCVD,
					AccountActionTypes.LOAN_REPAYMENT);

			if (getPerformanceHistory() != null) {
				getPerformanceHistory().setNoOfPayments(
						getPerformanceHistory().getNoOfPayments() + 1);
			}
			LoanActivityEntity loanActivity = buildLoanActivity(
					accountPaymentEntity.getAccountTrxns(), personnel,
					AccountConstants.LOAN_REPAYMENT, DateUtils
							.getCurrentDateWithoutTimeStamp());
			addLoanActivity(loanActivity);
			buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

			AccountStateEntity newAccountState = (AccountStateEntity) masterPersistence
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_OBLIGATIONSMET);
			addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
					getAccountState(), newAccountState,
					new PersonnelPersistence().getPersonnel(personnelId), this));
			setAccountState((AccountStateEntity) masterPersistence
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_OBLIGATIONSMET));
			setClosedDate(new Date(System.currentTimeMillis()));

			// Client performance entry
			updateCustomerHistoryOnRepayment(totalAmount);

			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void handleArrears() throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		AccountStateEntity stateEntity;
		try {
			stateEntity = (AccountStateEntity) masterPersistence
					.getPersistentObject(AccountStateEntity.class,
							AccountStates.LOANACC_BADSTANDING);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
		AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
				this.getAccountState(), stateEntity, this.getPersonnel(), this);
		this.addAccountStatusChangeHistory(historyEntity);
		this.setAccountState(stateEntity);
		//String systemDate = DateUtils.getCurrentDate(Configuration
		//		.getInstance().getSystemConfig().getMFILocale());
		//Date currrentDate = DateUtils.getLocaleDate(Configuration.getInstance()
		//		.getSystemConfig().getMFILocale(), systemDate);
		String systemDate = DateUtils.getCurrentDate();
		Date currrentDate = DateUtils.getLocaleDate(systemDate);
		this.setUpdatedDate(currrentDate);

		// Client performance entry
		updateCustomerHistoryOnArrears();

		try {
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public boolean isLastInstallment(Short installmentId) {
		Set<AccountActionDateEntity> accountActionDateSet = getAccountActionDates();
		List<Object> objectList = Arrays.asList(accountActionDateSet.toArray());
		AccountActionDateEntity accountActionDateEntity = (AccountActionDateEntity) objectList
				.get(objectList.size() - 1);
		if (installmentId.equals(accountActionDateEntity.getInstallmentId()))
			return true;
		return false;
	}

	@Override
	protected void writeOff() throws AccountException {
		try {
			Short personnelId = this.getUserContext().getId();
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(personnelId);

			this.setUpdatedBy(personnelId);
			this.setUpdatedDate(new Date(System.currentTimeMillis()));
			AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
					this, getEarlyClosureAmount(), null, null,
					new PaymentTypeEntity(Short.valueOf("1")));
			this.addAccountPayment(accountPaymentEntity);
			makeEarlyRepaymentForDueInstallments(accountPaymentEntity,
					AccountConstants.LOAN_WRITTEN_OFF,
					AccountActionTypes.WRITEOFF);
			makeEarlyRepaymentForFutureInstallments(accountPaymentEntity,
					AccountConstants.LOAN_WRITTEN_OFF,
					AccountActionTypes.WRITEOFF);
			addLoanActivity(buildLoanActivity(accountPaymentEntity
					.getAccountTrxns(), personnel,
					AccountConstants.LOAN_WRITTEN_OFF, DateUtils
							.getCurrentDateWithoutTimeStamp()));
			buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
			// Client performance entry
			updateCustomerHistoryOnWriteOff();
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private void waiveFeeAmountDue() throws AccountException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waiveFeeCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalFeeAmount(chargeWaived);
			updateAccountActivity(null, null, chargeWaived, null, userContext
					.getId(), LoanConstants.FEE_WAIVED);
		}
		try {
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private void waivePenaltyAmountDue() throws AccountException {
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
				.get(accountActionDateList.size() - 1);
		Money chargeWaived = accountActionDateEntity.waivePenaltyCharges();
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalPenaltyAmount(chargeWaived);
			updateAccountActivity(null, null, null, chargeWaived, userContext
					.getId(), LoanConstants.PENALTY_WAIVED);
		}
		try {
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private void waiveFeeAmountOverDue() throws AccountException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived
					.add(((LoanScheduleEntity) accountActionDateEntity)
							.waiveFeeCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalFeeAmount(chargeWaived);
			updateAccountActivity(null, null, chargeWaived, null, userContext
					.getId(), AccountConstants.AMOUNT + chargeWaived
					+ AccountConstants.WAIVED);
		}
		try {
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private void waivePenaltyAmountOverDue() throws AccountException {
		Money chargeWaived = new Money();
		List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
		accountActionDateList.remove(accountActionDateList.size() - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			chargeWaived = chargeWaived
					.add(((LoanScheduleEntity) accountActionDateEntity)
							.waivePenaltyCharges());
		}
		if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
			updateTotalPenaltyAmount(chargeWaived);
			updateAccountActivity(null, null, null, chargeWaived, userContext
					.getId(), AccountConstants.AMOUNT + chargeWaived
					+ AccountConstants.WAIVED);
		}
		try {
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public Money getAmountTobePaidAtdisburtail(Date disbursalDate)
			throws AccountException {
		if (this.isInterestDeductedAtDisbursement()) {
			return getDueAmount(getAccountActionDate(Short.valueOf("1")));
		}
		else {
			try {
				return new Money(new LoanPersistence()
						.getFeeAmountAtDisbursement(this.getAccountId())
						.toString());
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}

	}

	public Boolean hasPortfolioAtRisk() {
		List<AccountActionDateEntity> accountActionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			Calendar actionDate = new GregorianCalendar();
			actionDate.setTime(accountActionDateEntity.getActionDate());
			long diffInTermsOfDay = (Calendar.getInstance().getTimeInMillis() - actionDate
					.getTimeInMillis())
					/ (24 * 60 * 60 * 1000);
			if (diffInTermsOfDay > 30) {
				return true;
			}
		}
		return false;
	}

	public Money getRemainingPrincipalAmount() {
		return loanSummary.getOriginalPrincipal().subtract(
				loanSummary.getPrincipalPaid());
	}

	public boolean isAccountActive() {
		return getState() == AccountState.LOAN_ACTIVE_IN_GOOD_STANDING
				|| getState() == AccountState.LOAN_ACTIVE_IN_BAD_STANDING;
	}

	public void save() throws AccountException {
		try {
			this
					.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
							this.getAccountState(), this.getAccountState(),
							(new PersonnelPersistence())
									.getPersonnel(userContext.getId()), this));
			new LoanPersistence().createOrUpdate(this);
			this.globalAccountNum = generateId(userContext.getBranchGlobalNum());
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION, e);
		}
	}

	public void save(AccountState accountState) throws AccountException {
		this.setAccountState(new AccountStateEntity(accountState));
		save();
	}

	public void updateLoan(Boolean interestDeductedAtDisbursment,
			Money loanAmount, Double interestRate, Short noOfInstallments,
			Date disbursmentDate, Short gracePeriodDuration,
			Integer businessActivityId, String collateralNote,
			CollateralTypeEntity collateralTypeEntity,
			List<CustomFieldView> customFields) throws AccountException {
		if (interestDeductedAtDisbursment) {
			try {
				if (noOfInstallments <= 1)
					throw new AccountException(
							LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
				setGracePeriodType((GracePeriodTypeEntity) new MasterPersistence()
						.retrieveMasterEntity(GraceType.NONE.getValue(),
								GracePeriodTypeEntity.class, getUserContext()
										.getLocaleId()));
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
		else setGracePeriodType(getLoanOffering().getGracePeriodType());
		setLoanAmount(loanAmount);
		setInterestRate(interestRate);
		setNoOfInstallments(noOfInstallments);
		setDisbursementDate(disbursmentDate);
		setGracePeriodDuration(gracePeriodDuration);
		setInterestDeductedAtDisbursement(interestDeductedAtDisbursment);
		setBusinessActivityId(businessActivityId);
		setCollateralNote(collateralNote);
		setCollateralType(collateralTypeEntity);
		if (getAccountState().getId().equals(
				AccountState.LOAN_APPROVED.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOAN_PARTIAL_APPLICATION.getValue())
				|| getAccountState().getId().equals(
						AccountState.LOAN_PENDING_APPROVAL.getValue())) {
			if (isDisbursementDateLessThanCurrentDate(disbursementDate))
				throw new AccountException(
						LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);
			regeneratePaymentSchedule(false,null);
		}
		updateCustomFields(customFields);
		loanSummary.setOriginalPrincipal(loanAmount);
		update();
	}
	public void updateLoan(Money loanAmount,Integer businessActivityId) throws AccountException {
		
		
		setLoanAmount(loanAmount);
		setBusinessActivityId(businessActivityId);
		update();
	}

	public Short getDaysInArrears() {
		Short daysInArrears = 0;
		if (isAccountActive()) {
			if (!getDetailsOfInstallmentsInArrears().isEmpty()) {
				AccountActionDateEntity accountActionDateEntity = getDetailsOfInstallmentsInArrears()
						.get(0);
				daysInArrears = Short.valueOf(Long.valueOf(
						calculateDays(accountActionDateEntity.getActionDate(),
								DateUtils.getCurrentDateWithoutTimeStamp()))
						.toString());
			}
		}
		return daysInArrears;
	}

	public void handleArrearsAging() throws AccountException {
		if (this.loanArrearsAgingEntity == null)
			this.loanArrearsAgingEntity = new LoanArrearsAgingEntity(this,
					getDaysInArrears(), getLoanSummary().getPrincipalDue(),
					getLoanSummary().getInterestDue(),
					getTotalPrincipalAmountInArrears(),
					getTotalInterestAmountInArrears());
		else this.loanArrearsAgingEntity.update(getDaysInArrears(),
				getLoanSummary().getPrincipalDue(), getLoanSummary()
						.getInterestDue(), getTotalPrincipalAmountInArrears(),
				getTotalInterestAmountInArrears(), getCustomer());

		try {
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException pe) {
			throw new AccountException(pe);
		}
	}

	public final void reverseLoanDisbursal(final PersonnelBO loggedInUser,
			final String note) throws AccountException {
		changeStatus(AccountState.LOAN_CANCELLED.getValue(),
				AccountStateFlag.LOAN_REVERSAL.getValue(), note);
		if (getAccountPayments() != null && getAccountPayments().size() > 0) {
			for (AccountPaymentEntity accountPayment : getAccountPayments()) {
				if (accountPayment.getAmount().getAmountDoubleValue() > 0.0) {
					adjustPayment(accountPayment, loggedInUser, note);
				}
			}
		}
		addLoanActivity(buildLoanActivity(loanAmount, loggedInUser,
				"Disbursal Adjusted", DateUtils
						.getCurrentDateWithoutTimeStamp()));
		updateCustomerHistoryOnReverseLoan();
		try {
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException pe) {
			throw new AccountException(pe);
		}
	}

	protected void updatePerformanceHistoryOnAdjustment() {
		if (getPerformanceHistory() != null) {
			getPerformanceHistory().setNoOfPayments(
					getPerformanceHistory().getNoOfPayments() - 1);
		}
	}

	@Override
	protected AccountPaymentEntity makePayment(PaymentData paymentData)
			throws AccountException {
		LoanPaymentTypes loanPaymentTypes = getLoanPaymentType(paymentData
				.getTotalAmount());
		if (loanPaymentTypes == null)
			throw new AccountException("errors.makePayment",
					new String[] { getGlobalAccountNum() });
		else if (loanPaymentTypes.equals(LoanPaymentTypes.PARTIAL_PAYMENT)) {
			handlePartialPayment(paymentData);
		}
		else if (loanPaymentTypes.equals(LoanPaymentTypes.FULL_PAYMENT)) {
			handleFullPayment(paymentData);
		}
		else if (loanPaymentTypes.equals(LoanPaymentTypes.FUTURE_PAYMENT)) {
			handleFuturePayment(paymentData);
		}
		AccountActionDateEntity lastAccountAction = getLastInstallmentAccountAction();
		AccountPaymentEntity accountPayment = new AccountPaymentEntity(this,
				paymentData.getTotalAmount(), paymentData.getRecieptNum(),
				paymentData.getRecieptDate(), new PaymentTypeEntity(paymentData
						.getPaymentTypeId()));
		java.sql.Date paymentDate = new java.sql.Date(paymentData
				.getTransactionDate().getTime());
		for (AccountPaymentData accountPaymentData : paymentData
				.getAccountPayments()) {
			LoanScheduleEntity accountAction = (LoanScheduleEntity) getAccountActionDate(accountPaymentData
					.getInstallmentId());
			if (accountAction.isPaid()) {
				throw new AccountException("errors.update",
						new String[] { getGlobalAccountNum() });
			}
			if (accountAction.getInstallmentId().equals(
					lastAccountAction.getInstallmentId())
					&& accountPaymentData.isPaid()) {
				changeLoanStatus(AccountState.LOAN_CLOSED_OBLIGATIONS_MET,
						paymentData.getPersonnel());
				this.setClosedDate(new Date(System.currentTimeMillis()));
				// Client performance entry
				updateCustomerHistoryOnLastInstlPayment(paymentData
						.getTotalAmount());
			}
			if (getState().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING)
					&& (loanPaymentTypes.equals(LoanPaymentTypes.FULL_PAYMENT) || loanPaymentTypes
							.equals(LoanPaymentTypes.FUTURE_PAYMENT))) {
				changeLoanStatus(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
						paymentData.getPersonnel());
				// Client performance entry
				updateCustomerHistoryOnPayment();
			}
			LoanPaymentData loanPaymentData = (LoanPaymentData) accountPaymentData;
			accountAction.setPaymentDetails(loanPaymentData, paymentDate);
			accountPaymentData.setAccountActionDate(accountAction);
			LoanTrxnDetailEntity accountTrxnBO;
			try {
				accountTrxnBO = new LoanTrxnDetailEntity(accountPayment,
						loanPaymentData, paymentData.getPersonnel(),
						paymentData.getTransactionDate(),
						(AccountActionEntity) new MasterPersistence()
								.getPersistentObject(AccountActionEntity.class,
										AccountActionTypes.LOAN_REPAYMENT
												.getValue()), loanPaymentData
								.getAmountPaidWithFeeForInstallment(),
						AccountConstants.PAYMENT_RCVD);
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
			accountPayment.addAcountTrxn(accountTrxnBO);

			loanSummary.updatePaymentDetails(
					loanPaymentData.getPrincipalPaid(), loanPaymentData
							.getInterestPaid(), loanPaymentData
							.getPenaltyPaid().add(
									loanPaymentData.getMiscPenaltyPaid()),
					loanPaymentData.getFeeAmountPaidForInstallment().add(
							loanPaymentData.getMiscFeePaid()));
			if (loanPaymentData.isPaid())
				performanceHistory.setNoOfPayments(getPerformanceHistory()
						.getNoOfPayments() + 1);
		}
		addLoanActivity(buildLoanActivity(accountPayment.getAccountTrxns(),
				paymentData.getPersonnel(), AccountConstants.PAYMENT_RCVD,
				paymentData.getTransactionDate()));
		return accountPayment;
	}

	@Override
	protected Money getDueAmount(AccountActionDateEntity installment) {
		return ((LoanScheduleEntity) installment).getTotalDueWithFees();
	}

	@Override
	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) throws AccountException {
		if (null != reversedTrxns && reversedTrxns.size() > 0) {
			for (AccountTrxnEntity accntTrxn : reversedTrxns) {
				if (!accntTrxn.getAccountActionEntity().getId().equals(
						AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL
								.getValue())) {
					LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accntTrxn;

					loanSummary.updatePaymentDetails(loanTrxn
							.getPrincipalAmount(),
							loanTrxn.getInterestAmount(), loanTrxn
									.getPenaltyAmount().add(
											loanTrxn.getMiscPenaltyAmount()),
							loanTrxn.getFeeAmount().add(
									loanTrxn.getMiscFeeAmount()));
					if (loanTrxn.getInstallmentId() != null
							&& !loanTrxn.getInstallmentId().equals(
									Short.valueOf("0"))) {
						LoanScheduleEntity accntActionDate = (LoanScheduleEntity) getAccountActionDate(loanTrxn
								.getInstallmentId());
						accntActionDate.updatePaymentDetails(loanTrxn
								.getPrincipalAmount(), loanTrxn
								.getInterestAmount(), loanTrxn
								.getPenaltyAmount(), loanTrxn
								.getMiscPenaltyAmount(), loanTrxn
								.getMiscFeeAmount());
						if (accntActionDate.isPaid())
							updatePerformanceHistoryOnAdjustment();
						accntActionDate.setPaymentStatus(PaymentStatus.UNPAID);
						accntActionDate.setPaymentDate(null);
						if (null != accntActionDate
								.getAccountFeesActionDetails()
								&& accntActionDate
										.getAccountFeesActionDetails().size() > 0) {
							for (AccountFeesActionDetailEntity accntFeesAction : accntActionDate
									.getAccountFeesActionDetails()) {
								FeesTrxnDetailEntity feesTrxnDetailEntity = loanTrxn
										.getFeesTrxn(accntFeesAction
												.getAccountFee()
												.getAccountFeeId());
								if (feesTrxnDetailEntity != null) {
									Money feeAmntAdjusted = feesTrxnDetailEntity
											.getFeeAmount();
									((LoanFeeScheduleEntity) accntFeesAction)
											.setFeeAmountPaid(accntFeesAction
													.getFeeAmountPaid().add(
															feeAmntAdjusted));
								}
							}
						}
					}
				}
			}
			if (getAccountStatusChangeHistory() != null
					&& getAccountStatusChangeHistory().size() > 0
					&& !getAccountState().getId().equals(
							AccountState.LOAN_CANCELLED.getValue())) {
				List<Object> objectList = Arrays
						.asList(getAccountStatusChangeHistory().toArray());
				AccountStatusChangeHistoryEntity accountStatusChangeHistoryEntity = (AccountStatusChangeHistoryEntity) objectList
						.get(objectList.size() - 1);
				if (accountStatusChangeHistoryEntity.getOldStatus().getId()
						.equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue())) {
					setAccountState(new AccountStateEntity(
							AccountState.LOAN_ACTIVE_IN_BAD_STANDING));
				}
			}
			PersonnelBO personnel;
			try {
				personnel = new PersonnelPersistence()
						.getPersonnel(getUserContext().getId());

				addLoanActivity(buildLoanActivity(reversedTrxns, personnel,
						AccountConstants.LOAN_ADJUSTED, DateUtils
								.getCurrentDateWithoutTimeStamp()));
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
	}

	@Override
	protected void regenerateFutureInstallments(Short nextInstallmentId)
			throws AccountException {
		if ((!this.getAccountState().getId().equals(
				AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue()))
				&& (!this.getAccountState().getId().equals(
						AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue()))
				&& (!this.getAccountState().getId().equals(
						AccountState.LOAN_CANCELLED.getValue()))) {
			List<Date> meetingDates = null;
			int installmentSize = getLastInstallmentId();
			int totalInstallmentDatesToBeChanged = installmentSize
					- nextInstallmentId + 1;
			try {
				MeetingBO meeting = buildLoanMeeting(customer
						.getCustomerMeeting().getMeeting(), getLoanMeeting(),
						new Date(System.currentTimeMillis()));
				meetingDates = meeting
						.getAllDates(totalInstallmentDatesToBeChanged + 1);
				if (meetingDates.get(0).compareTo(
						DateUtils.getCurrentDateWithoutTimeStamp()) == 0) {
					meetingDates.remove(0);
				}
				else {
					meetingDates.remove(totalInstallmentDatesToBeChanged);
				}
			}
			catch (MeetingException me) {
				throw new AccountException(me);
			}
			for (int count = 0; count < meetingDates.size(); count++) {
				short installmentId = (short) (nextInstallmentId.intValue() + count);
				AccountActionDateEntity accountActionDate = getAccountActionDate(installmentId);
				if (accountActionDate != null)
					((LoanScheduleEntity) accountActionDate)
							.setActionDate(new java.sql.Date(meetingDates.get(
									count).getTime()));
			}
		}
	}

	protected final void applyRounding() {
		if (!isPricipalZeroInAnyInstallmemt()) {
			LoanScheduleEntity lastAccountActionDate = (LoanScheduleEntity) getLastInstallmentAccountAction();
			Money diffAmount = new Money();
			int count = 0;
			List<AccountActionDateEntity> unpaidInstallments = getListOfUnpaidFutureInstallments();
			for (AccountActionDateEntity accountActionDate : unpaidInstallments) {
				LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
				count++;
				if (lastAccountActionDate.getInstallmentId().equals(
						loanScheduleEntity.getInstallmentId())) {
					continue;
				}
				Money totalAmount = loanScheduleEntity.getTotalDueWithFees();
				Money roundedTotalAmount = Money.round(totalAmount);
				loanScheduleEntity.setPrincipal(loanScheduleEntity
						.getPrincipal().subtract(
								totalAmount.subtract(roundedTotalAmount)));
				diffAmount = diffAmount.add(totalAmount
						.subtract(roundedTotalAmount));

			}
			lastAccountActionDate.setPrincipal(lastAccountActionDate
					.getPrincipal().add(diffAmount));
		}
	}

	private int calculateDays(Date fromDate, Date toDate) {
		long y = 1000 * 60 * 60 * 24;
		long x = (getMFITime(toDate) / y) - (getMFITime(fromDate) / y);
		return (int) x;
	}

	private long getMFITime(Date date) {
		Calendar cal1 = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal1.setTime(date);
		return date.getTime() + cal1.get(Calendar.ZONE_OFFSET)
				+ cal1.get(Calendar.DST_OFFSET);
	}

	private Money getAccountFeeAmount(AccountFeesEntity accountFees,
			Money loanInterest) {
		Money accountFeeAmount = new Money();
		Double feeAmount = accountFees.getFeeAmount();

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Fee amount..." + feeAmount);

		if (accountFees.getFees().getFeeType().equals(RateAmountFlag.AMOUNT)) {
			accountFeeAmount = new Money(feeAmount.toString());
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"AccountFeeAmount for amount fee.." + feeAmount);
		}
		else if (accountFees.getFees().getFeeType().equals(RateAmountFlag.RATE)) {
			RateFeeBO rateFeeBO = new FeePersistence().getRateFee(accountFees
					.getFees().getFeeId());
			accountFeeAmount = new Money(getRateBasedOnFormula(feeAmount,
					rateFeeBO.getFeeFormula(), loanInterest));
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"AccountFeeAmount for Formula fee.." + feeAmount);
		}
		return accountFeeAmount;
	}

	@Override
	protected final List<FeeInstallment> handlePeriodic(
			AccountFeesEntity accountFees,
			List<InstallmentDate> installmentDates) throws AccountException {
		Money accountFeeAmount = accountFees.getAccountFeeAmount();
		MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency()
				.getFeeMeetingFrequency();
		List<Date> feeDates = getFeeDates(feeMeetingFrequency, installmentDates);
		ListIterator<Date> feeDatesIterator = feeDates.listIterator();
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		while (feeDatesIterator.hasNext()) {
			Date feeDate = feeDatesIterator.next();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Handling periodic fee.." + feeDate);
			Short installmentId = getMatchingInstallmentId(installmentDates,
					feeDate);
			feeInstallmentList.add(buildFeeInstallment(installmentId,
					accountFeeAmount, accountFees));
		}
		return feeInstallmentList;
	}

	private LoanActivityEntity buildLoanActivity(
			Collection<AccountTrxnEntity> accountTrxnDetails,
			PersonnelBO personnel, String comments, Date trxnDate) {
		Date activityDate = trxnDate;
		Money principal = new Money();
		Money interest = new Money();
		Money penalty = new Money();
		Money fees = new Money();
		for (AccountTrxnEntity accountTrxn : accountTrxnDetails) {
			if (!accountTrxn.getAccountActionEntity().getId().equals(
					AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL
							.getValue())) {
				LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accountTrxn;
				principal = principal.add(removeSign(loanTrxn
						.getPrincipalAmount()));
				interest = interest
						.add(removeSign(loanTrxn.getInterestAmount()));
				penalty = penalty.add(removeSign(loanTrxn.getPenaltyAmount()))
						.add(removeSign(loanTrxn.getMiscPenaltyAmount()));
				fees = fees.add(removeSign(loanTrxn.getMiscFeeAmount()));
				for (FeesTrxnDetailEntity feesTrxn : loanTrxn
						.getFeesTrxnDetails()) {
					fees = fees.add(removeSign(feesTrxn.getFeeAmount()));
				}
			}
			if (accountTrxn.getAccountActionEntity().getId().equals(
					AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL
							.getValue())
					|| accountTrxn.getAccountActionEntity().getId().equals(
							AccountActionTypes.LOAN_REVERSAL.getValue())) {
				comments = "Loan Reversal";
			}
		}
		return new LoanActivityEntity(this, personnel, comments, principal,
				loanSummary.getOriginalPrincipal().subtract(
						loanSummary.getPrincipalPaid()), interest, loanSummary
						.getOriginalInterest().subtract(
								loanSummary.getInterestPaid()), fees,
				loanSummary.getOriginalFees().subtract(
						loanSummary.getFeesPaid()), penalty, loanSummary
						.getOriginalPenalty().subtract(
								loanSummary.getPenaltyPaid()), activityDate);
	}

	private LoanActivityEntity buildLoanActivity(Money totalPrincipal,
			PersonnelBO personnel, String comments, Date trxnDate) {
		Date activityDate = trxnDate;
		Money principal = totalPrincipal;
		Money interest = new Money();
		Money penalty = new Money();
		Money fees = new Money();
		return new LoanActivityEntity(this, personnel, comments, principal,
				loanSummary.getOriginalPrincipal().subtract(
						loanSummary.getPrincipalPaid()), interest, loanSummary
						.getOriginalInterest().subtract(
								loanSummary.getInterestPaid()), fees,
				loanSummary.getOriginalFees().subtract(
						loanSummary.getFeesPaid()), penalty, loanSummary
						.getOriginalPenalty().subtract(
								loanSummary.getPenaltyPaid()), activityDate);
	}

	private Short getInstallmentSkipToStartRepayment() {
		if (isInterestDeductedAtDisbursement())
			return (short) 0;
		else {
			if (getGraceType() == GraceType.PRINCIPALONLYGRACE
					|| getGraceType() == GraceType.NONE) {
				return (short) 1;
			}
		}
		return (short) (getGracePeriodDuration() + 1);
	}

	private String getRateBasedOnFormula(Double rate, FeeFormulaEntity formula,
			Money loanInterest) {
		Double amountToCalculateOn = 1.0;
		if (formula.getId().equals(FeeFormula.AMOUNT.getValue())) {
			amountToCalculateOn = loanAmount.getAmountDoubleValue();
		}
		else if (formula.getId().equals(
				FeeFormula.AMOUNT_AND_INTEREST.getValue())) {
			amountToCalculateOn = (loanAmount.add(loanInterest))
					.getAmountDoubleValue();
		}
		else if (formula.getId().equals(FeeFormula.INTEREST.getValue())) {
			amountToCalculateOn = loanInterest.getAmountDoubleValue();
		}
		Double rateAmount = (rate * amountToCalculateOn) / 100;
		return rateAmount.toString();
	}

	private void generateMeetingSchedule(boolean isRepaymentIndepOfMeetingEnabled,MeetingBO newMeetingForRepaymentDay) throws AccountException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Generating meeting schedule... ");
		if(isRepaymentIndepOfMeetingEnabled){
			setLoanMeeting(newMeetingForRepaymentDay);
		}
		
		List<InstallmentDate> installmentDates = getInstallmentDates(
				getLoanMeeting(), noOfInstallments,
				getInstallmentSkipToStartRepayment());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Obtained intallments dates");
		Money loanInterest = getLoanInterest(installmentDates.get(
				installmentDates.size() - 1).getInstallmentDueDate());
		List<EMIInstallment> EMIInstallments = generateEMI(loanInterest);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Emi installment  obtained ");
		validateSize(installmentDates, EMIInstallments);
		List<FeeInstallment> feeInstallment = new ArrayList<FeeInstallment>();
		if (getAccountFees().size() != 0) {
			populateAccountFeeAmount(getAccountFees(), loanInterest);
			feeInstallment = mergeFeeInstallments(getFeeInstallment(installmentDates));
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Fee installment obtained ");
		generateRepaymentSchedule(installmentDates, EMIInstallments,
				feeInstallment);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Meeting schedule generated  ");
		applyRounding();
	}

	private void populateAccountFeeAmount(Set<AccountFeesEntity> accountFees,
			Money loanInterest) {
		for (AccountFeesEntity accountFeesEntity : accountFees) {
			accountFeesEntity.setAccountFeeAmount(getAccountFeeAmount(
					accountFeesEntity, loanInterest));
		}
	}

	private Boolean isPricipalZeroInAnyInstallmemt() {
		for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
			if (((LoanScheduleEntity) accountActionDate).isPrincipalZero()) {
				return true;
			}
		}
		return false;
	}

	private Money getLoanInterest(Date installmentEndDate)
			throws AccountException {
		if (getLoanOffering().getInterestTypes().getId().equals(
				InterestType.FLAT.getValue()))
			return getFlatInterestAmount(installmentEndDate);
		if (getLoanOffering().getInterestTypes().getId().equals(
				InterestType.DECLINING.getValue()))
			return getDecliningInterestAmount(installmentEndDate);

		return null;
	}

	// why not pass "100" as a string rather than Double.toString(100)?
	// seems like the Double.toString call could introduce small errors
	private Money getFlatInterestAmount(Date installmentEndDate)
			throws AccountException {
		Double interestRate = getInterestRate();
		Double durationInYears = getTotalDurationInYears(installmentEndDate);
		Money interestRateM = new Money(Double.toString(interestRate));
		Money durationInYearsM = new Money(Double.toString(durationInYears));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Get interest duration in years..." + durationInYears);
		Money interest = getLoanAmount().multiply(
				interestRateM.multiply(durationInYearsM)).divide(
				new Money(Double.toString(100)));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Get interest accumulated..." + interest);
		return interest;
	}

	private Money getDecliningInterestAmount(Date installmentEndDate)
			throws AccountException {

		double annualPeriod = getDecliningInterestAnnualPeriods();
		Double interestRate = getInterestRate();
		//i*P / [1- (1+i)^-n]

		Double durationInYears = getTotalDurationInYears(installmentEndDate);
		double interestRatePerPeriod = interestRate / 100 / annualPeriod;

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"DecliningInterestCalculator:getInterest duration in years..."
						+ durationInYears);

		int usedPeriods = getNoOfInstallments();
		if (getGraceType() == GraceType.PRINCIPALONLYGRACE) {
			usedPeriods = usedPeriods - getGracePeriodDuration();
		}

		double interest = getLoanAmount().getAmountDoubleValue()
				* (interestRate / 100 / annualPeriod)
				/ (1.00d - Math.pow(1.00d + interestRatePerPeriod, -1.00d
						* usedPeriods));


		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"DecliningInterestCalculator:getInterest interest accumulated..."
						+ interest);


		return new Money(Double.toString(interest));
	}

	private double getDecliningInterestAnnualPeriods() {
		RecurrenceType meetingFrequency = getLoanMeeting().getMeetingDetails()
				.getRecurrenceTypeEnum();

		short recurAfter = getLoanMeeting().getMeetingDetails().getRecurAfter();

		double period = 0;

		if (meetingFrequency.equals(RecurrenceType.WEEKLY)) {
			period = getInterestDays() / (getDaysInWeek() * recurAfter);

		}
		else if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
			period = getInterestDays() / (getDaysInMonth() * recurAfter);
		}

		return period;
	}


	private double getTotalDurationInYears(Date installmentEndDate)
			throws AccountException {
		int interestDays = getInterestDays();
		int daysInWeek = getDaysInWeek();
		int daysInMonth = getDaysInMonth();

		Short recurrenceType = this.getLoanMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId();
		int duration = getNoOfInstallments()
				* this.getLoanMeeting().getMeetingDetails().getRecurAfter();
		if (interestDays == AccountConstants.INTEREST_DAYS_360) {
			if (recurrenceType.equals(RecurrenceType.WEEKLY.getValue())) {
				double totalWeekDays = duration * daysInWeek;
				double durationInYears = totalWeekDays
						/ AccountConstants.INTEREST_DAYS_360;
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug("Get total week days.." + totalWeekDays);
				return durationInYears;
			}
			else if (recurrenceType.equals(RecurrenceType.MONTHLY.getValue())) {
				double totalMonthDays = duration * daysInMonth;
				double durationInYears = totalMonthDays
						/ AccountConstants.INTEREST_DAYS_360;
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug("Get total month days.." + totalMonthDays);
				return durationInYears;
			}
			throw new AccountException(
					AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
		}
		else if (interestDays == AccountConstants.INTEREST_DAYS_365) {
			if (recurrenceType.equals(RecurrenceType.WEEKLY.getValue())) {
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug("Get interest week 365 days");
				double totalWeekDays = duration * daysInWeek;
				double durationInYears = totalWeekDays
						/ AccountConstants.INTEREST_DAYS_365;
				return durationInYears;
			}
			else if (recurrenceType.equals(RecurrenceType.MONTHLY.getValue())) {
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug("Get interest month 365 days");

				// will have to consider inc/dec time in some countries
				Long installmentStartTime = getDisbursementDate().getTime();
				Long installmentEndTime = installmentEndDate.getTime();
				Long diffTime = installmentEndTime - installmentStartTime;
				double daysDiff = diffTime / (1000 * 60 * 60 * 24);
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug("Get start date..");
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug("Get end date..");
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
						.debug("Get diff in days..." + daysDiff);
				double durationInYears = daysDiff
						/ AccountConstants.INTEREST_DAYS_365;
				return durationInYears;
			}
			throw new AccountException(
					AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
		}
		else throw new AccountException(
				AccountConstants.NOT_SUPPORTED_INTEREST_DAYS);
	}

	// read from configuration
	private int getInterestDays() {
		return AccountingRules.getNumberOfInterestDays().shortValue();
	}

	// read from configuration
	private int getDaysInWeek() {
		return AccountConstants.DAYS_IN_WEEK;
	}

	// read from configuration
	private int getDaysInMonth() {
		return AccountConstants.DAYS_IN_MONTH;
	}

	private void generateRepaymentSchedule(
			List<InstallmentDate> installmentDates,
			List<EMIInstallment> EMIInstallments,
			List<FeeInstallment> feeInstallmentList) throws AccountException {
		int count = installmentDates.size();
		for (int i = 0; i < count; i++) {
			InstallmentDate installmentDate = installmentDates.get(i);
			EMIInstallment em = EMIInstallments.get(i);
			LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(
					this, getCustomer(), installmentDate.getInstallmentId(),
					new java.sql.Date(installmentDate.getInstallmentDueDate()
							.getTime()), PaymentStatus.UNPAID, em
							.getPrincipal(), em.getInterest());
			addAccountActionDate(loanScheduleEntity);
			for (FeeInstallment feeInstallment : feeInstallmentList) {
				if (feeInstallment.getInstallmentId().shortValue() == installmentDate
						.getInstallmentId().shortValue()
						&& !feeInstallment.getAccountFeesEntity().getFees()
								.isTimeOfDisbursement()) {
					LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(
							loanScheduleEntity, feeInstallment
									.getAccountFeesEntity().getFees(),
							feeInstallment.getAccountFeesEntity(),
							feeInstallment.getAccountFee());
					loanScheduleEntity
							.addAccountFeesAction(loanFeeScheduleEntity);
				}
				else if (feeInstallment.getInstallmentId() == installmentDate
						.getInstallmentId()
						&& isInterestDeductedAtDisbursement()
						&& feeInstallment.getAccountFeesEntity().getFees()
								.isTimeOfDisbursement()) {
					LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(
							loanScheduleEntity, feeInstallment
									.getAccountFeesEntity().getFees(),
							feeInstallment.getAccountFeesEntity(),
							feeInstallment.getAccountFee());
					loanScheduleEntity
							.addAccountFeesAction(loanFeeScheduleEntity);
				}
			}
		}
	}

	private void validateSize(List installmentDates, List EMIInstallments)
			throws AccountException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Validating installment size  " + installmentDates.size());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Validating emi installment size  " + EMIInstallments.size());
		if (installmentDates.size() != EMIInstallments.size())
			throw new AccountException(AccountConstants.DATES_MISMATCH);
	}

	private List<EMIInstallment> generateEMI(Money loanInterest)
			throws AccountException {
		if (isInterestDeductedAtDisbursement()
				&& !getLoanOffering().isPrinDueLastInst())
			return interestDeductedAtDisbursement(loanInterest);

		if (getLoanOffering().isPrinDueLastInst()
				&& !isInterestDeductedAtDisbursement()) {
			if (getLoanOffering().getInterestTypes().getId().equals(
					InterestType.FLAT.getValue())) {
				return principalInLastPayment(loanInterest);
			}
			else if (getLoanOffering().getInterestTypes().getId().equals(
					InterestType.DECLINING.getValue())) {
				return principalInLastPaymentDecliningInterest(loanInterest);
			}
		}

		if (!getLoanOffering().isPrinDueLastInst()
				&& !isInterestDeductedAtDisbursement()) {
			if (getLoanOffering().getInterestTypes().getId().equals(
					InterestType.FLAT.getValue())) {
				return allInstallments(loanInterest);
			}
			else if (getLoanOffering().getInterestTypes().getId().equals(
					InterestType.DECLINING.getValue())) {
				return allDecliningInstallments(loanInterest);
			}
		}
		if (getLoanOffering().isPrinDueLastInst()
				&& isInterestDeductedAtDisbursement())
			return interestDeductedFirstPrincipalLast(loanInterest);

		throw new AccountException(
				AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
	}

	private List<EMIInstallment> interestDeductedAtDisbursement(
			Money loanInterest) throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		// grace can only be none
		if (getGraceType() == GraceType.GRACEONALLREPAYMENTS
				|| getGraceType() == GraceType.PRINCIPALONLYGRACE)
			throw new AccountException(
					AccountConstants.INTERESTDEDUCTED_INVALIDGRACETYPE);

		if (getGraceType() == GraceType.NONE) {
			Money interestFirstInstallment = loanInterest;
			// principal starts only from the second installment
			Money principalPerInstallment = new Money(Double
					.toString(getLoanAmount().getAmountDoubleValue()
							/ (getNoOfInstallments() - 1)));
			EMIInstallment installment = new EMIInstallment();
			installment.setPrincipal(new Money());
			installment.setInterest(interestFirstInstallment);
			emiInstallments.add(installment);
			for (int i = 1; i < getNoOfInstallments(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(new Money());

				emiInstallments.add(installment);
			}
		}
		return emiInstallments;
	}

	private List<EMIInstallment> principalInLastPayment(Money loanInterest)
			throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		// grace can only be none
		if (getGraceType() == GraceType.PRINCIPALONLYGRACE)
			throw new AccountException(
					AccountConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
		if (getGraceType() == GraceType.NONE
				|| getGraceType() == GraceType.GRACEONALLREPAYMENTS) {
			Money principalLastInstallment = getLoanAmount();
			// principal starts only from the second installment
			Money interestPerInstallment = new Money(Double
					.toString(loanInterest.getAmountDoubleValue()
							/ getNoOfInstallments()));
			EMIInstallment installment = null;
			for (int i = 0; i < getNoOfInstallments() - 1; i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			// principal set in the last installment
			installment = new EMIInstallment();
			installment.setPrincipal(principalLastInstallment);
			installment.setInterest(interestPerInstallment);
			emiInstallments.add(installment);
			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}

	private List<EMIInstallment> principalInLastPaymentDecliningInterest(
			Money loanInterest) throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		// grace can only be none
		if (getGraceType() == GraceType.PRINCIPALONLYGRACE)
			throw new AccountException(
					AccountConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
		if (getGraceType() == GraceType.NONE
				|| getGraceType() == GraceType.GRACEONALLREPAYMENTS) {
			Money principalLastInstallment = getLoanAmount();

			Money interestPerInstallment = new Money(
					Double
							.toString(getLoanAmount().getAmountDoubleValue()
									* (getInterestRate() / 100 / getDecliningInterestAnnualPeriods())));
			EMIInstallment installment = null;
			for (int i = 0; i < getNoOfInstallments() - 1; i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			// principal set in the last installment
			installment = new EMIInstallment();
			installment.setPrincipal(principalLastInstallment);
			installment.setInterest(interestPerInstallment);
			emiInstallments.add(installment);
			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}


	private List<EMIInstallment> allInstallments(Money loanInterest)
			throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		if (getGraceType() == GraceType.GRACEONALLREPAYMENTS
				|| getGraceType() == GraceType.NONE) {
			Money principalPerInstallment = new Money(Double
					.toString(getLoanAmount().getAmountDoubleValue()
							/ getNoOfInstallments()));
			Money interestPerInstallment = new Money(Double
					.toString(loanInterest.getAmountDoubleValue()
							/ getNoOfInstallments()));
			EMIInstallment installment = null;
			for (int i = 0; i < getNoOfInstallments(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			return emiInstallments;
		}

		if (getGraceType() == GraceType.PRINCIPALONLYGRACE) {
			Money principalPerInstallment = new Money(
					Double
							.toString(getLoanAmount().getAmountDoubleValue()
									/ (getNoOfInstallments() - getGracePeriodDuration())));
			Money interestPerInstallment = new Money(Double
					.toString(loanInterest.getAmountDoubleValue()
							/ getNoOfInstallments()));
			EMIInstallment installment = null;
			for (int i = 0; i < getGracePeriodDuration(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			for (int i = getGracePeriodDuration(); i < getNoOfInstallments(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(interestPerInstallment);
				emiInstallments.add(installment);
			}
			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}

	private List<EMIInstallment> allDecliningInstallments(Money loanInterest)
			throws AccountException {

		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		if (getGraceType() == GraceType.GRACEONALLREPAYMENTS
				|| getGraceType() == GraceType.NONE) {

			double principalBalance = getLoanAmount().getAmountDoubleValue();

			EMIInstallment installment = null;
			double principalPaidCurrentPeriod = 0;
			double interestPerInstallment = 0;
			for (int i = 0; i < getNoOfInstallments(); i++) {

				installment = new EMIInstallment();

				if (principalBalance > 0) {
					interestPerInstallment = Math
							.abs(principalBalance
									* ((getInterestRate().doubleValue() / 100) / getDecliningInterestAnnualPeriods()));
				}

				Money interstPerInstallmentM = new Money(Double
						.toString(interestPerInstallment));
				installment.setInterest(interstPerInstallmentM);
				principalPaidCurrentPeriod = Math.abs(loanInterest
						.getAmountDoubleValue()
						- interestPerInstallment);
				installment.setPrincipal(new Money(Double
						.toString(principalPaidCurrentPeriod)));
				principalBalance = principalBalance
						- principalPaidCurrentPeriod;
				emiInstallments.add(installment);
			}

			return emiInstallments;
		}

		if (getGraceType() == GraceType.PRINCIPALONLYGRACE) {
			double principalBalance = getLoanAmount().getAmountDoubleValue();
			double interestPerInstallment = Math
					.abs(principalBalance
							* ((getInterestRate().doubleValue() / 100) / getDecliningInterestAnnualPeriods()));
			Money interestPerInstallmentM = new Money(Double
					.toString(interestPerInstallment));
			EMIInstallment installment = null;
			for (int i = 0; i < getGracePeriodDuration(); i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallmentM);
				emiInstallments.add(installment);
			}
			double principalPaidCurrentPeriod = 0;
			for (int i = getGracePeriodDuration(); i < getNoOfInstallments(); i++) {

				installment = new EMIInstallment();
				principalPaidCurrentPeriod = Math.abs(loanInterest
						.getAmountDoubleValue()
						- interestPerInstallment);
				if (principalBalance > 0) {
					interestPerInstallment = Math
							.abs(principalBalance
									* ((getInterestRate().doubleValue() / 100) / getDecliningInterestAnnualPeriods()));
				}
				installment.setPrincipal(new Money(Double
						.toString(principalPaidCurrentPeriod)));
				Money interstPerInstallmentM = new Money(Double
						.toString(interestPerInstallment));
				installment.setInterest(interstPerInstallmentM);

				principalPaidCurrentPeriod = Math.abs(loanInterest
						.getAmountDoubleValue()
						- interestPerInstallment);
				installment.setPrincipal(new Money(Double
						.toString(principalPaidCurrentPeriod)));
				principalBalance = principalBalance
						- principalPaidCurrentPeriod;
				emiInstallments.add(installment);
			}

			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}

	private List<EMIInstallment> interestDeductedFirstPrincipalLast(
			Money loanInterest) throws AccountException {
		List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
		if (getGraceType() == GraceType.GRACEONALLREPAYMENTS
				|| getGraceType() == GraceType.PRINCIPALONLYGRACE)
			throw new AccountException(
					AccountConstants.INTERESTDEDUCTED_PRINCIPALLAST);
		if (getGraceType() == GraceType.NONE) {
			Money principalLastInstallment = getLoanAmount();
			Money interestFirstInstallment = loanInterest;

			EMIInstallment installment = null;
			installment = new EMIInstallment();
			installment.setPrincipal(new Money());
			installment.setInterest(interestFirstInstallment);
			emiInstallments.add(installment);
			for (int i = 1; i < getNoOfInstallments() - 1; i++) {
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(new Money());
				emiInstallments.add(installment);
			}
			installment = new EMIInstallment();
			installment.setPrincipal(principalLastInstallment);
			installment.setInterest(new Money());
			emiInstallments.add(installment);
			return emiInstallments;
		}
		throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
	}

	private static Boolean isDisbursementDateValid(
			CustomerBO specifiedCustomer, Date disbursementDate)
			throws AccountException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"IsDisbursementDateValid invoked ");
		Boolean isValid = false;
		try {
			isValid = specifiedCustomer.getCustomerMeeting().getMeeting()
					.isValidMeetingDate(disbursementDate,
							DateUtils.getLastDayOfNextYear());
		}
		catch (MeetingException e) {
			throw new AccountException(e);
		}
		return isValid;
	}

	private static boolean isDisbursementDateLessThanCurrentDate(
			Date disbursementDate) {
		if (DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime())
				.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0)
			return true;
		return false;
	}

	private void applyPeriodicFee(FeeBO fee, Double charge,
			List<AccountActionDateEntity> dueInstallments)
			throws AccountException {
		AccountFeesEntity accountFee = getAccountFee(fee, charge);
		Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
		accountFeeSet.add(accountFee);
		populateAccountFeeAmount(accountFeeSet, loanSummary
				.getOriginalInterest());
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallments)
			installmentDates.add(new InstallmentDate(accountActionDateEntity
					.getInstallmentId(), accountActionDateEntity
					.getActionDate()));
		List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(handlePeriodic(
				accountFee, installmentDates));
		Money totalFeeAmountApplied = applyFeeToInstallments(
				feeInstallmentList, dueInstallments);
		updateLoanSummary(fee.getFeeId(), totalFeeAmountApplied);
		updateLoanActivity(fee.getFeeId(), totalFeeAmountApplied, fee
				.getFeeName()
				+ AccountConstants.APPLIED);
	}

	private void applyOneTimeFee(FeeBO fee, Double charge,
			AccountActionDateEntity accountActionDateEntity)
			throws AccountException {
		LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
		AccountFeesEntity accountFee = new AccountFeesEntity(this, fee, charge,
				FeeStatus.ACTIVE.getValue(), new Date(System
						.currentTimeMillis()), null);
		Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
		accountFeeSet.add(accountFee);
		populateAccountFeeAmount(accountFeeSet, loanSummary
				.getOriginalInterest());
		List<AccountActionDateEntity> loanScheduleEntityList = new ArrayList<AccountActionDateEntity>();
		loanScheduleEntityList.add(loanScheduleEntity);
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		installmentDates.add(new InstallmentDate(accountActionDateEntity
				.getInstallmentId(), accountActionDateEntity.getActionDate()));
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		feeInstallmentList.add(handleOneTime(accountFee, installmentDates));
		Money totalFeeAmountApplied = applyFeeToInstallments(
				feeInstallmentList, loanScheduleEntityList);
		filterTimeOfDisbursementFees(loanScheduleEntity, fee);
		updateLoanSummary(fee.getFeeId(), totalFeeAmountApplied);
		updateLoanActivity(fee.getFeeId(), totalFeeAmountApplied, fee
				.getFeeName()
				+ AccountConstants.APPLIED);
	}

	private void applyMiscCharge(Short chargeType, Money charge,
			AccountActionDateEntity accountActionDateEntity)
			throws AccountException {
		LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
		loanScheduleEntity.applyMiscCharge(chargeType, charge);
		updateLoanSummary(chargeType, charge);
		updateLoanActivity(chargeType, charge, "");
	}

	private void updateLoanSummary(Short chargeType, Money charge) {
		if (chargeType != null
				&& chargeType.equals(Short
						.valueOf(AccountConstants.MISC_PENALTY)))
			getLoanSummary().updateOriginalPenalty(charge);
		else getLoanSummary().updateOriginalFees(charge);
	}

	private void updateLoanActivity(Short chargeType, Money charge,
			String comments) throws AccountException {
		try {
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(getUserContext().getId());
			LoanActivityEntity loanActivityEntity = null;
			if (chargeType != null
					&& chargeType.equals(Short
							.valueOf(AccountConstants.MISC_PENALTY)))
				loanActivityEntity = new LoanActivityEntity(this, personnel,
						new Money(), new Money(), new Money(), charge,
						getLoanSummary(), AccountConstants.MISC_PENALTY_APPLIED);
			else if (chargeType != null
					&& chargeType.equals(Short
							.valueOf(AccountConstants.MISC_FEES)))
				loanActivityEntity = new LoanActivityEntity(this, personnel,
						new Money(), new Money(), charge, new Money(),
						getLoanSummary(), AccountConstants.MISC_FEES_APPLIED);
			else loanActivityEntity = new LoanActivityEntity(this, personnel,
					new Money(), new Money(), charge, new Money(),
					getLoanSummary(), comments);
			addLoanActivity(loanActivityEntity);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	private Money applyFeeToInstallments(
			List<FeeInstallment> feeInstallmentList,
			List<AccountActionDateEntity> accountActionDateList) {
		Date lastAppliedDate = null;
		Money totalFeeAmountApplied = new Money();
		AccountFeesEntity accountFeesEntity = null;
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
			for (FeeInstallment feeInstallment : feeInstallmentList)
				if (feeInstallment.getInstallmentId().equals(
						loanScheduleEntity.getInstallmentId())) {
					lastAppliedDate = loanScheduleEntity.getActionDate();
					totalFeeAmountApplied = totalFeeAmountApplied
							.add(feeInstallment.getAccountFee());
					/*
					 * AccountFeesActionDetailEntity
					 * accountFeesActionDetailEntity = new
					 * LoanFeeScheduleEntity( loanScheduleEntity, feeInstallment
					 * .getAccountFeesEntity().getFees(),
					 * feeInstallment.getAccountFeesEntity(),
					 * feeInstallment.getAccountFee()); loanScheduleEntity
					 * .addAccountFeesAction(accountFeesActionDetailEntity);
					 */
					if (feeInstallment.getAccountFeesEntity().getFees()
							.isPeriodic()
							&& loanScheduleEntity
									.isFeeAlreadyAttatched(feeInstallment
											.getAccountFeesEntity().getFees()
											.getFeeId())) {
						LoanFeeScheduleEntity loanFeeScheduleEntity = (LoanFeeScheduleEntity) loanScheduleEntity
								.getAccountFeesAction(feeInstallment
										.getAccountFeesEntity().getFees()
										.getFeeId());
						loanFeeScheduleEntity
								.setFeeAmount(loanFeeScheduleEntity
										.getFeeAmount().add(
												feeInstallment.getAccountFee()));
					}
					else {
						AccountFeesActionDetailEntity accountFeesActionDetailEntity = new LoanFeeScheduleEntity(
								loanScheduleEntity, feeInstallment
										.getAccountFeesEntity().getFees(),
								feeInstallment.getAccountFeesEntity(),
								feeInstallment.getAccountFee());
						loanScheduleEntity
								.addAccountFeesAction(accountFeesActionDetailEntity);
					}

					accountFeesEntity = feeInstallment.getAccountFeesEntity();
				}
		}
		accountFeesEntity.setLastAppliedDate(lastAppliedDate);
		addAccountFees(accountFeesEntity);
		return totalFeeAmountApplied;
	}

	private void filterTimeOfDisbursementFees(
			LoanScheduleEntity loanScheduleEntity, FeeBO fee) {
		Short paymentType = fee.getFeeFrequency().getFeePayment().getId();
		if (paymentType.equals(FeePayment.TIME_OF_DISBURSMENT.getValue())
				&& !isInterestDeductedAtDisbursement()) {
			Set<AccountFeesActionDetailEntity> accountFeesDetailSet = loanScheduleEntity
					.getAccountFeesActionDetails();
			for (Iterator<AccountFeesActionDetailEntity> iter = accountFeesDetailSet
					.iterator(); iter.hasNext();) {
				AccountFeesActionDetailEntity accountFeesActionDetailEntity = iter
						.next();
				if (fee.equals(accountFeesActionDetailEntity.getFee())) {
					iter.remove();
				}
			}
		}
	}

	private MeetingBO buildLoanMeeting(MeetingBO customerMeeting,
			MeetingBO loanOfferingMeeting, Date disbursementDate)
			throws AccountException {
		if (customerMeeting != null
				&& loanOfferingMeeting != null
				&& customerMeeting.getMeetingDetails().getRecurrenceType()
						.getRecurrenceId().equals(
								loanOfferingMeeting.getMeetingDetails()
										.getRecurrenceType().getRecurrenceId())
				&& isMultiple(loanOfferingMeeting.getMeetingDetails()
						.getRecurAfter(), customerMeeting.getMeetingDetails()
						.getRecurAfter())) {

			RecurrenceType meetingFrequency = customerMeeting
					.getMeetingDetails().getRecurrenceTypeEnum();
			MeetingType meetingType = MeetingType.fromInt(customerMeeting
					.getMeetingType().getMeetingTypeId());
			Short recurAfter = loanOfferingMeeting.getMeetingDetails()
					.getRecurAfter();
			try {
				MeetingBO meetingToReturn = null;
				if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
					if (customerMeeting.isMonthlyOnDate())
						meetingToReturn = new MeetingBO(customerMeeting
								.getMeetingDetails().getDayNumber(),
								recurAfter, disbursementDate, meetingType,
								customerMeeting.getMeetingPlace());
					else meetingToReturn = new MeetingBO(customerMeeting
							.getMeetingDetails().getWeekDay(), customerMeeting
							.getMeetingDetails().getWeekRank(), recurAfter,
							disbursementDate, meetingType, customerMeeting
									.getMeetingPlace());
				}
				else if (meetingFrequency.equals(RecurrenceType.WEEKLY))
					meetingToReturn = new MeetingBO(customerMeeting
							.getMeetingDetails().getMeetingRecurrence()
							.getWeekDayValue(), recurAfter, disbursementDate,
							meetingType, customerMeeting.getMeetingPlace());
				else meetingToReturn = new MeetingBO(meetingFrequency,
						recurAfter, disbursementDate, meetingType);
				return meetingToReturn;
			}
			catch (MeetingException me) {
				throw new AccountException(me);
			}
		}
		else {
			throw new AccountException(
					AccountExceptionConstants.CHANGEINLOANMEETING);
		}
	}

	private boolean isMultiple(Short valueToBeChecked,
			Short valueToBeCheckedWith) {
		return valueToBeChecked % valueToBeCheckedWith == 0;
	}

	private LoanSummaryEntity buildLoanSummary() {
		Money interest = new Money();
		Money fees = new Money();
		Set<AccountActionDateEntity> actionDates = getAccountActionDates();
		if (actionDates != null && actionDates.size() > 0) {
			for (AccountActionDateEntity accountActionDate : actionDates) {
				LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
				interest = interest.add(loanSchedule.getInterest());
				fees = fees.add(loanSchedule.getTotalFees());
			}
		}
		fees = fees.add(getDisbursementFeeAmount());
		return new LoanSummaryEntity(this, loanAmount, interest, fees);
	}

	private Money getDisbursementFeeAmount() {
		Money fees = new Money();
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			if (!isInterestDeductedAtDisbursement()
					&& accountFeesEntity.getFees().isTimeOfDisbursement()) {
				fees = fees.add(accountFeesEntity.getAccountFeeAmount());
			}
		}
		return fees;
	}

	private void buildAccountFee(List<FeeView> feeViews) {
		if (feeViews != null && feeViews.size() > 0) {
			for (FeeView feeView : feeViews) {
				FeeBO fee = new FeePersistence()
						.getFee(feeView.getFeeIdValue());
				this.addAccountFees(new AccountFeesEntity(this, fee, feeView
						.getAmountMoney()));
			}
		}
	}

	private void setGracePeriodTypeAndDuration(
			boolean interestDeductedAtDisbursement, Short gracePeriodDuration,
			Short noOfInstallments) throws AccountException {
		if (interestDeductedAtDisbursement) {
			setGracePeriodType(GraceType.NONE);
			this.gracePeriodDuration = (short) 0;
		}
		else {
			if (!loanOffering.getGracePeriodType().getId().equals(
					GraceType.NONE.getValue()))
				if (gracePeriodDuration == null
						|| gracePeriodDuration >= noOfInstallments)
					throw new AccountException("errors.gracePeriod");
			setGracePeriodType(loanOffering.getGracePeriodType());
			this.gracePeriodDuration = gracePeriodDuration;
		}
	}

	private void updateCustomerHistoryOnLastInstlPayment(Money totalAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setLastLoanAmount(totalAmount);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnPayment() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnDisbursement(Money disburseAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() + 1);
			clientPerfHistory.updateLoanCounter(getLoanOffering(),
					YesNoFlag.YES);
		}
		else if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.GROUP.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			groupPerformanceHistoryEntity
					.setLastGroupLoanAmount(disburseAmount);
		}
	}

	private void updateCustomerHistoryOnRepayment(Money totalAmount) {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setLastLoanAmount(totalAmount);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnArrears() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() + 1);
		}
	}

	private void updateCustomerHistoryOnWriteOff() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory
					.updateLoanCounter(getLoanOffering(), YesNoFlag.NO);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
		}
	}

	private void updateCustomerHistoryOnReverseLoan() {
		Money lastLoanAmount = new Money();
		try {
			lastLoanAmount = new LoanPersistence()
					.getLastLoanAmountForCustomer(getCustomer().getCustomerId());
		}
		catch (PersistenceException e) {
		}
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory
					.updateLoanCounter(getLoanOffering(), YesNoFlag.NO);
			clientPerfHistory.setNoOfActiveLoans(clientPerfHistory
					.getNoOfActiveLoans() - 1);
			clientPerfHistory.setLastLoanAmount(lastLoanAmount);
		}
		else if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.GROUP.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			groupPerformanceHistoryEntity
					.setLastGroupLoanAmount(lastLoanAmount);
		}
	}

	@Override
	protected void updateClientPerformanceOnRescheduleLoan() {
		if (getCustomer().getCustomerLevel().getId().equals(
				Short.valueOf(CustomerLevel.CLIENT.getValue()))
				&& getCustomer().getPerformanceHistory() != null) {
			ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
					.getPerformanceHistory();
			clientPerfHistory
					.updateLoanCounter(getLoanOffering(), YesNoFlag.NO);
		}
	}

	private void regeneratePaymentSchedule(boolean isRepaymentIndepOfMeetingEnabled,MeetingBO newMeetingForRepaymentDay) throws AccountException {
		Money miscFee = getMiscFee();
		Money miscPenalty = getMiscPenalty();
		try {
			new LoanPersistence().deleteInstallments(this
					.getAccountActionDates());
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
		this.resetAccountActionDates();
		Calendar date = new GregorianCalendar();
		date.setTime(disbursementDate);
		loanMeeting.setMeetingStartDate(date);
		generateMeetingSchedule(isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
		LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) getAccountActionDate(Short
				.valueOf("1"));
		loanScheduleEntity.setMiscFee(miscFee);
		loanScheduleEntity.setMiscPenalty(miscPenalty);
		Money interest = new Money();
		Money fees = new Money();
		Money penalty = new Money();
		Money principal = new Money();
		Set<AccountActionDateEntity> actionDates = getAccountActionDates();
		if (actionDates != null && actionDates.size() > 0) {
			for (AccountActionDateEntity accountActionDate : actionDates) {
				LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
				principal = principal.add(loanSchedule.getPrincipal());
				interest = interest.add(loanSchedule.getInterest());
				fees = fees.add(loanSchedule.getTotalFees());
				penalty = penalty.add(loanSchedule.getTotalPenalty());
			}
		}
		fees = fees.add(getDisbursementFeeAmount());
		loanSummary.setOriginalInterest(interest);
		loanSummary.setOriginalFees(fees);
		loanSummary.setOriginalPenalty(penalty);
	}

	private AccountPaymentEntity payInterestAtDisbursement(String recieptNum,
			Date transactionDate, Short paymentTypeId, PersonnelBO personnel,
			Date receiptDate) throws AccountException {
		AccountActionDateEntity firstInstallment = null;
		for (AccountActionDateEntity accountActionDate : this
				.getAccountActionDates()) {
			if (accountActionDate.getInstallmentId().shortValue() == 1) {
				firstInstallment = accountActionDate;
				break;
			}
		}
		List<AccountActionDateEntity> installmentsToBePaid = new ArrayList<AccountActionDateEntity>();
		installmentsToBePaid.add(firstInstallment);

		PaymentData paymentData = getLoanAccountPaymentData(
				((LoanScheduleEntity) firstInstallment).getTotalDueWithFees(),
				installmentsToBePaid, personnel, recieptNum, paymentTypeId,
				receiptDate, transactionDate);

		return makePayment(paymentData);

	}

	private AccountActionDateEntity getLastInstallmentAccountAction() {
		AccountActionDateEntity nextAccountAction = null;
		if (getAccountActionDates() != null
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accountAction : getAccountActionDates()) {
				if (null == nextAccountAction)
					nextAccountAction = accountAction;
				else if (nextAccountAction.getInstallmentId() < accountAction
						.getInstallmentId())
					nextAccountAction = accountAction;
			}
		}
		return nextAccountAction;
	}

	private Money getMiscFee() {
		Money miscFee = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			if (loanSchedule.getMiscFee() != null) {
				miscFee = miscFee.add(loanSchedule.getMiscFee());
			}
		}
		return miscFee;
	}

	private Money getMiscPenalty() {
		Money miscPenalty = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			if (loanSchedule.getMiscPenalty() != null) {
				miscPenalty = miscPenalty.add(loanSchedule.getMiscPenalty());
			}
		}
		return miscPenalty;
	}

	private List<AccountActionDateEntity> getListOfUnpaidInstallments() {
		List<AccountActionDateEntity> unpaidInstallmentList = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (!accountActionDateEntity.isPaid()) {
				unpaidInstallmentList.add(accountActionDateEntity);
			}
		}
		return unpaidInstallmentList;
	}

	private List<AccountActionDateEntity> getListOfUnpaidFutureInstallments() {
		List<AccountActionDateEntity> unpaidInstallmentList = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
			if (DateUtils.getCurrentDateWithoutTimeStamp().compareTo(
					DateUtils.getDateWithoutTimeStamp(accountActionDateEntity
							.getActionDate().getTime())) <= 0) {
				unpaidInstallmentList.add(accountActionDateEntity);
			}
		}
		return unpaidInstallmentList;
	}

	private Money getEarlyClosureAmount() {
		Money amount = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getPrincipal());
		}
		return amount;
	}

	private AccountPaymentEntity insertOnlyFeeAtDisbursement(String recieptNum,
			Date recieptDate, Short paymentTypeId, PersonnelBO personnel)
			throws AccountException {

		Money totalPayment = new Money();
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			if (accountFeesEntity.isTimeOfDisbursement()) {
				totalPayment = totalPayment.add(accountFeesEntity
						.getAccountFeeAmount());
			}
		}

		loanSummary.updateFeePaid(totalPayment);

		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(
				this, totalPayment, recieptNum, recieptDate,
				new PaymentTypeEntity(paymentTypeId));

		LoanTrxnDetailEntity loanTrxnDetailEntity = null;

		try {
			List<AccountFeesEntity> applicableAccountFees = new ArrayList<AccountFeesEntity>();
			for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
				if (accountFeesEntity.isTimeOfDisbursement()) {
					applicableAccountFees.add(accountFeesEntity);
				}
			}
			loanTrxnDetailEntity = new LoanTrxnDetailEntity(
					accountPaymentEntity,
					(AccountActionEntity) new MasterPersistence()
							.getPersistentObject(AccountActionEntity.class,
									AccountActionTypes.FEE_REPAYMENT.getValue()),
					Short.valueOf("0"), recieptDate, personnel, recieptDate,
					totalPayment, "-", null, new Money(), new Money(),
					new Money(), new Money(), new Money(),
					applicableAccountFees);
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}

		accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

		addLoanActivity(buildLoanActivity(accountPaymentEntity
				.getAccountTrxns(), personnel, AccountConstants.PAYMENT_RCVD,
				recieptDate));
		return accountPaymentEntity;
	}

	private PaymentData getLoanAccountPaymentData(Money totalAmount,
			List<AccountActionDateEntity> accountActions,
			PersonnelBO personnel, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate) {
		PaymentData paymentData = PaymentData.createPaymentData(totalAmount,
				personnel, paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptId);
		return paymentData;
	}

	private LoanPaymentTypes getLoanPaymentType(Money amount) {
		if (amount.getAmountDoubleValue() == getTotalPaymentDue()
				.getAmountDoubleValue())
			return LoanPaymentTypes.FULL_PAYMENT;
		else if (amount.getAmountDoubleValue() < getTotalPaymentDue()
				.getAmountDoubleValue())
			return LoanPaymentTypes.PARTIAL_PAYMENT;
		else if (amount.getAmountDoubleValue() > getTotalPaymentDue()
				.getAmountDoubleValue()
				&& amount.getAmountDoubleValue() <= getTotalRepayableAmount()
						.getAmountDoubleValue())
			return LoanPaymentTypes.FUTURE_PAYMENT;
		return null;
	}

	private void handlePartialPayment(PaymentData paymentData) {
		Money totalAmount = paymentData.getTotalAmount();
		for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
			if (totalAmount.getAmountDoubleValue() > 0.0) {
				LoanPaymentData loanPayment = new LoanPaymentData(
						accountActionDate, totalAmount);
				paymentData.addAccountPaymentData(loanPayment);
				totalAmount = totalAmount.subtract(loanPayment
						.getAmountPaidWithFeeForInstallment());
			}
		}
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& !nextInstallment.isPaid()
				&& DateUtils.getDateWithoutTimeStamp(
						nextInstallment.getActionDate().getTime()).equals(
						DateUtils.getCurrentDateWithoutTimeStamp()))
			paymentData.addAccountPaymentData(new LoanPaymentData(
					nextInstallment, totalAmount));
	}

	private void handleFullPayment(PaymentData paymentData) {
		for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
			paymentData.addAccountPaymentData(new LoanPaymentData(
					accountActionDate));
		}
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& !nextInstallment.isPaid()
				&& DateUtils.getDateWithoutTimeStamp(
						nextInstallment.getActionDate().getTime()).equals(
						DateUtils.getCurrentDateWithoutTimeStamp()))
			paymentData.addAccountPaymentData(new LoanPaymentData(
					nextInstallment));
	}

	private void handleFuturePayment(PaymentData paymentData) {
		Money totalAmount = paymentData.getTotalAmount();
		for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
			LoanPaymentData loanPayment = new LoanPaymentData(accountActionDate);
			paymentData.addAccountPaymentData(loanPayment);
			totalAmount = totalAmount.subtract(loanPayment
					.getAmountPaidWithFeeForInstallment());
		}
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null && !nextInstallment.isPaid()
				&& totalAmount.getAmountDoubleValue() > 0.0) {
			LoanPaymentData loanPayment;
			if (DateUtils.getDateWithoutTimeStamp(
					nextInstallment.getActionDate().getTime()).equals(
					DateUtils.getCurrentDateWithoutTimeStamp())) {
				loanPayment = new LoanPaymentData(nextInstallment);
			}
			else {
				loanPayment = new LoanPaymentData(nextInstallment, totalAmount);
			}
			paymentData.addAccountPaymentData(loanPayment);
			totalAmount = totalAmount.subtract(loanPayment
					.getAmountPaidWithFeeForInstallment());
		}
		for (AccountActionDateEntity accountActionDate : getApplicableIdsForFutureInstallments()) {
			if (totalAmount.getAmountDoubleValue() > 0.0) {
				LoanPaymentData loanPayment = new LoanPaymentData(
						accountActionDate, totalAmount);
				paymentData.addAccountPaymentData(loanPayment);
				totalAmount = totalAmount.subtract(loanPayment
						.getAmountPaidWithFeeForInstallment());
			}
		}
	}

	private void changeLoanStatus(AccountState newAccountState,
			PersonnelBO personnel) throws AccountException {
		AccountStateEntity accountState = this.getAccountState();
		try {
			setAccountState((AccountStateEntity) (new MasterPersistence())
					.getPersistentObject(AccountStateEntity.class,
							newAccountState.getValue()));
		}
		catch (PersistenceException e) {
			throw new AccountException(e);
		}
		this
				.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(
						accountState, this.getAccountState(), personnel, this));
	}

	private Money getTotalRepayableAmount() {
		Money amount = new Money();
		for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForDueInstallments()) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getTotalDueWithFees());
		}
		for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForFutureInstallments()) {
			amount = amount.add(((LoanScheduleEntity) accountActionDateEntity)
					.getTotalDueWithFees());
		}
		return amount;
	}

	private boolean isAdjustmentForInterestDedAtDisb(Short installmentId) {
		return installmentId.equals(Short.valueOf("1"))
				&& isInterestDeductedAtDisbursement();
	}

	public boolean isRedone() {
		return this.redone;
	}

	Boolean getRedone() {
		return this.redone;
	}
	
	void setRedone(Boolean val) {
		this.redone = val;
	}
	
	private void makeEarlyRepaymentForDueInstallments(
			AccountPaymentEntity accountPaymentEntity, String comments,
			AccountActionTypes accountActionTypes) throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
		for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			Money principal = loanSchedule.getPrincipalDue();
			Money interest = loanSchedule.getInterestDue();
			Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
			Money penalty = loanSchedule.getPenaltyDue();
			Money totalAmt = principal.add(interest).add(fees).add(penalty);

			LoanTrxnDetailEntity loanTrxnDetailEntity;
			try {
				loanTrxnDetailEntity = new LoanTrxnDetailEntity(
						accountPaymentEntity,
						(AccountActionEntity) masterPersistence
								.getPersistentObject(AccountActionEntity.class,
										accountActionTypes.getValue()),
						loanSchedule.getInstallmentId(), loanSchedule
								.getActionDate(), personnel, new Date(System
								.currentTimeMillis()), totalAmt, comments,
						null, principal, interest, loanSchedule.getPenalty()
								.subtract(loanSchedule.getPenaltyPaid()),
						loanSchedule.getMiscFeeDue(), loanSchedule
								.getMiscPenaltyDue(), null);
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanSchedule
					.getAccountFeesActionDetails()) {
				if (accountFeesActionDetailEntity.getFeeDue()
						.getAmountDoubleValue() > 0) {
					FeesTrxnDetailEntity feesTrxnDetailEntity = new FeesTrxnDetailEntity(
							loanTrxnDetailEntity, accountFeesActionDetailEntity
									.getAccountFee(),
							accountFeesActionDetailEntity.getFeeDue());
					loanTrxnDetailEntity
							.addFeesTrxnDetail(feesTrxnDetailEntity);
				}
			}

			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

			loanSchedule
					.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);

			loanSummary
					.updatePaymentDetails(principal, interest, penalty, fees);
		}
	}

	private void makeEarlyRepaymentForFutureInstallments(
			AccountPaymentEntity accountPaymentEntity, String comments,
			AccountActionTypes accountActionTypes) throws AccountException {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
			Money principal = loanSchedule.getPrincipalDue();
			Money interest = loanSchedule.getInterestDue();
			Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
			Money penalty = loanSchedule.getPenaltyDue();

			LoanTrxnDetailEntity loanTrxnDetailEntity;
			try {
				loanTrxnDetailEntity = new LoanTrxnDetailEntity(
						accountPaymentEntity,
						(AccountActionEntity) masterPersistence
								.getPersistentObject(AccountActionEntity.class,
										accountActionTypes.getValue()),
						loanSchedule.getInstallmentId(), loanSchedule
								.getActionDate(), personnel, new Date(System
								.currentTimeMillis()), principal, comments,
						null, principal, new Money(), new Money(), new Money(),
						new Money(), null);
			}
			catch (PersistenceException e) {
				throw new AccountException(e);
			}

			accountPaymentEntity.addAcountTrxn(loanTrxnDetailEntity);

			loanSchedule
					.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);

			loanSummary.decreaseBy(null, interest, penalty, fees);
			loanSummary.updatePaymentDetails(principal, null, null, null);

		}

	}

	public int getDisbursementTerm() {
		List<AccountActionDateEntity> pastInstallments = getPastInstallments();
		List<AccountActionDateEntity> installmentsInDisbursement = new ArrayList<AccountActionDateEntity>();
		if (!pastInstallments.isEmpty()) {
			for (AccountActionDateEntity accountAction : pastInstallments) {
				if (accountAction.isPaid())
					installmentsInDisbursement.add(accountAction);
			}
		}
		return installmentsInDisbursement.size();
	}

	public int getDaysWithoutPayment() throws PersistenceException {
		int daysWithoutPayment = 0;
		loanPrdPersistence = new LoanPrdPersistence();
		if (getDaysInArrears() > loanPrdPersistence.retrieveLatenessForPrd()) {
			daysWithoutPayment = getDaysInArrears().intValue();
		}
		return daysWithoutPayment;
	}

	
	
	public double getPaymentsInArrears() throws PersistenceException {
		Money principalInArrearsAndOutsideLateness = getTotalPrincipalAmountInArrearsAndOutsideLateness();
		Money totalPrincipal = getTotalPrincipalAmount();
		Money numOfInstallments = new Money(new BigDecimal(getNoOfInstallments()));
		return principalInArrearsAndOutsideLateness.multiply(numOfInstallments).divide(totalPrincipal).getAmountDoubleValue();
			
	}

	public Money getNetOfSaving() {
		return getRemainingPrincipalAmount().subtract(
				getCustomer().getSavingsBalance());
	}

	public boolean prdOfferingsCanCoexist(Short idPrdOff_B)
			throws PersistenceException {
		try {
			return new ProductMixPersistence().doesPrdOfferingsCanCoexist(this
					.getLoanOffering().getPrdOfferingId(), idPrdOff_B);
		}
		catch (PersistenceException e) {
			throw new PersistenceException(e);
		}
	}

	public Set<LoanBO> getLoanAccountDetails() {
		return loanAccountDetails;
	}

	public void setLoanAccountDetails(Set<LoanBO> loanAccountDetails) {
		this.loanAccountDetails = loanAccountDetails;
	}

	public LoanBO getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(LoanBO parentAccount) {
		this.parentAccount = parentAccount;
	}

	public MaxMinLoanAmount getMaxMinLoanAmount() {
		return maxMinLoanAmount;
	}

	public MaxMinNoOfInstall getMaxMinNoOfInstall() {
		return maxMinNoOfInstall;
	}

	public RankOfDaysEntity getMonthRank() {
		return monthRank;
	}

	public void setMonthRank(RankOfDaysEntity monthRank) {
		this.monthRank = monthRank;
	}

	public WeekDaysEntity getMonthWeek() {
		return monthWeek;
	}

	public void setMonthWeek(WeekDaysEntity monthWeek) {
		this.monthWeek = monthWeek;
	}

	public Short getRecurMonth() {
		return recurMonth;
	}

	public void setRecurMonth(Short recurMonth) {
		this.recurMonth = recurMonth;
	}
	
	public WeekDay getMonthWeekValue() {
		return monthWeek!=null ? WeekDay.getWeekDay(monthWeek.getId()) : null;
	}
	public RankType getWeekRank() {
		return monthRank!=null ? RankType.getRankType(monthRank.getId()) : null;
	}
	

	
}
