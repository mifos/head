/**

 * LoanOffering.java    version: xxx

 

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

package org.mifos.application.productdefinition.util.valueobjects;

import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.util.valueobjects.InterestCalcRule;
import org.mifos.application.master.util.valueobjects.InterestTypes;
import org.mifos.application.penalty.util.valueobjects.Penalty;
import org.mifos.framework.util.helpers.Money;

public class LoanOffering extends PrdOffering {
	
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 767657457657651L;
	
	/** The composite primary key value. */
    private PrdOffering prdOffering;

    /** The value of the gracePeriodType association. */
    private GracePeriodType gracePeriodType;
    
    /** The value of the gracePeriodDuration association. */
    private java.lang.Short gracePeriodDuration;

    /** The value of the interestTypes association. */
    private InterestTypes interestTypes;

    /** The value of the penalty association. */
    private Penalty penalty;

    /** The value of the interestCalcRule association. */
    private InterestCalcRule interestCalcRule;

    /** The value of the simple minLoanAmount property. */
    private Money minLoanAmount;

    /** The value of the simple maxLoanAmnt property. */
    private Money maxLoanAmount;

    /** The value of the simple defaultLoanAmount property. */
    private Money defaultLoanAmount;

    /** The value of the simple minPeriod property. */
    private java.lang.Short minPeriod;

    /** The value of the simple maxPeriod property. */
    private java.lang.Short maxPeriod;

    /** The value of the simple defaultPeriod property. */
    private java.lang.Short defaultPeriod;

    /** The value of the simple collateralTypeId property. */
    private java.lang.Short collateralTypeId;

    /** The value of the simple collateralNotes property. */
    private java.lang.String collateralNotes;

    /** The value of the simple lateness property. */
    private java.lang.Short lateness;

    /** The value of the simple repmtfreqId property. */
    private java.lang.Short repmtfreqId;

    /** The value of the simple maxInterestRate property. */
    private java.lang.Double maxInterestRate;

    /** The value of the simple minInterestRate property. */
    private java.lang.Double minInterestRate;

    /** The value of the simple defInterestRate property. */
    private java.lang.Double defInterestRate;

    /** The value of the simple maxNoInstallments property. */
    private java.lang.Short maxNoInstallments;

    /** The value of the simple minNoInstallments property. */
    private java.lang.Short minNoInstallments;

    /** The value of the simple defNoInstallments property. */
    private java.lang.Short defNoInstallments;

    /** The value of the simple penaltyGrace property. */
    private java.lang.Short penaltyGrace;

    /** The value of the simple loanCounterFlag property. */
    private java.lang.Short loanCounterFlag;

    /** The value of the simple intDedDisbursementFlag property. */
    private java.lang.Short intDedDisbursementFlag;

    /** The value of the simple prinDueLastInstFlag property. */
    private java.lang.Short prinDueLastInstFlag;
    /** The value of the simple penaltyRate property. */
    private Double penaltyRate;
    private GLCodeEntity principalGLCode;
    private GLCodeEntity interestGLCode;
    private GLCodeEntity penaltyGLCode;
    /**
     * This maps the funds associated with the loan offering to Loan_offering_fund table
     */
    private Set<LoanOfferingFund> loanOffeingFundSet;
    /**
     * This maps the fees associated with the loan offering to prd_offering_fees table
     */
    private Set<PrdOfferingFees> prdOfferingFeesSet;
    /**
     * This maps the meeting associated with the loan offering to prd_offering_meeting table
     */
    private PrdOfferingMeeting prdOfferingMeeting; 
    
	/**
	 * @param loanOfferingFunds The loanOfferingFunds to set.
	 */
	public void setLoanOfferFunds(Set<LoanOfferingFund> loanOfferFunds) {
		if(null != this.loanOffeingFundSet) {
			this.loanOffeingFundSet.clear();
		}
		//Bug id 28718.changed to set loanOfferFunds.
		setLoanOffeingFundSet(loanOfferFunds);
	}

	/**
	 * @param prdOfferingFees The prdOfferingFees to set.
	 */
	public void setPrdOfferingFees(Set<PrdOfferingFees> prdOfferingFees) {
		if(null != this.prdOfferingFeesSet) {
			this.prdOfferingFeesSet.clear();
		}
		setPrdOfferingFeesSet(prdOfferingFees);
	}

	/**
	 * @return Returns the loanOffeingFundSet.
	 */
	public Set<LoanOfferingFund> getLoanOffeingFundSet() {
		return loanOffeingFundSet;
	}

	/**
	 * @param loanOffeingFundSet The loanOffeingFundSet to set.
	 */
	public void setLoanOffeingFundSet(Set<LoanOfferingFund> loanOffeingFundSet) {
		if(loanOffeingFundSet !=null) {
			for (LoanOfferingFund loanOfferingFund : loanOffeingFundSet) {
				loanOfferingFund.setLoanOffering(this);
			}
		}
		this.loanOffeingFundSet = loanOffeingFundSet;
	}
 	/**
	 * @return Returns the prdOfferingMeeting.
	 */
	public PrdOfferingMeeting getPrdOfferingMeeting() {
		return prdOfferingMeeting;
	}

	/**
	 * @param prdOfferingMeeting The prdOfferingMeeting to set.
	 */
	public void setPrdOfferingMeeting(PrdOfferingMeeting prdOfferingMeeting) {
		if(prdOfferingMeeting != null) {
			prdOfferingMeeting.setPrdOffering(this);
		}
		this.prdOfferingMeeting = prdOfferingMeeting;
	}

	/**
	 * @return Returns the prdOfferingFeesSet.
	 */
	public Set<PrdOfferingFees> getPrdOfferingFeesSet() {
		return prdOfferingFeesSet;
	}

	/**
	 * @param prdOfferingFeesSet The prdOfferingFeesSet to set.
	 */
	public void setPrdOfferingFeesSet(Set<PrdOfferingFees> prdOfferingFeesSet) {
		this.prdOfferingFeesSet = prdOfferingFeesSet;
	}

	/**
	 * @return Returns the penaltyRate.
	 */
	public Double getPenaltyRate() {
		return penaltyRate;
	}

	/**
	 * @param penaltyRate The penaltyRate to set.
	 */
	public void setPenaltyRate(Double penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	/**
	 * @return Returns the collateralNotes}.
	 */
	public java.lang.String getCollateralNotes() {
		return collateralNotes;
	}

	/**
	 * @param collateralNotes The collateralNotes to set.
	 */
	public void setCollateralNotes(java.lang.String collateralNotes) {
		this.collateralNotes = collateralNotes;
	}

	/**
	 * @return Returns the collateralTypeId}.
	 */
	public java.lang.Short getCollateralTypeId() {
		return collateralTypeId;
	}

	/**
	 * @param collateralTypeId The collateralTypeId to set.
	 */
	public void setCollateralTypeId(java.lang.Short collateralTypeId) {
		this.collateralTypeId = collateralTypeId;
	}

	/**
	 * @return Returns the defaultLoanAmount}.
	 */
	public Money getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	/**
	 * @param defaultLoanAmount The defaultLoanAmount to set.
	 */
	public void setDefaultLoanAmount(Money defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	/**
	 * @return Returns the defaultPeriod}.
	 */
	public java.lang.Short getDefaultPeriod() {
		return defaultPeriod;
	}

	/**
	 * @param defaultPeriod The defaultPeriod to set.
	 */
	public void setDefaultPeriod(java.lang.Short defaultPeriod) {
		this.defaultPeriod = defaultPeriod;
	}

	/**
	 * @return Returns the defInterestRate}.
	 */
	public java.lang.Double getDefInterestRate() {
		return defInterestRate;
	}

	/**
	 * @param defInterestRate The defInterestRate to set.
	 */
	public void setDefInterestRate(java.lang.Double defInterestRate) {
		this.defInterestRate = defInterestRate;
	}

	/**
	 * @return Returns the defNoInstallments}.
	 */
	public java.lang.Short getDefNoInstallments() {
		return defNoInstallments;
	}

	/**
	 * @param defNoInstallments The defNoInstallments to set.
	 */
	public void setDefNoInstallments(java.lang.Short defNoInstallments) {
		this.defNoInstallments = defNoInstallments;
	}

	/**
	 * @return Returns the gracePeriodType}.
	 */
	public GracePeriodType getGracePeriodType() {
		return gracePeriodType;
	}

	/**
	 * @param gracePeriodType The gracePeriodType to set.
	 */
	public void setGracePeriodType(GracePeriodType gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	/**
	 * @return Returns the intDedDisbursementFlag}.
	 */
	public java.lang.Short getIntDedDisbursementFlag() {
		return intDedDisbursementFlag;
	}

	/**
	 * @param intDedDisbursementFlag The intDedDisbursementFlag to set.
	 */
	public void setIntDedDisbursementFlag(java.lang.Short intDedDisbursementFlag) {
		this.intDedDisbursementFlag = intDedDisbursementFlag;
	}

	/**
	 * @return Returns the interestCalcRule}.
	 */
	public InterestCalcRule getInterestCalcRule() {
		return interestCalcRule;
	}

	/**
	 * @param interestCalcRule The interestCalcRule to set.
	 */
	public void setInterestCalcRule(InterestCalcRule interestCalcRule) {
		this.interestCalcRule = interestCalcRule;
	}

	/**
	 * @return Returns the interestTypes}.
	 */
	public InterestTypes getInterestTypes() {
		return interestTypes;
	}

	/**
	 * @param interestTypes The interestTypes to set.
	 */
	public void setInterestTypes(InterestTypes interestTypes) {
		this.interestTypes = interestTypes;
	}

	/**
	 * @return Returns the lateness}.
	 */
	public java.lang.Short getLateness() {
		return lateness;
	}

	/**
	 * @param lateness The lateness to set.
	 */
	public void setLateness(java.lang.Short lateness) {
		this.lateness = lateness;
	}

	/**
	 * @return Returns the loanCounterFlag}.
	 */
	public java.lang.Short getLoanCounterFlag() {
		return loanCounterFlag;
	}

	/**
	 * @param loanCounterFlag The loanCounterFlag to set.
	 */
	public void setLoanCounterFlag(java.lang.Short loanCounterFlag) {
		this.loanCounterFlag = loanCounterFlag;
	}

	/**
	 * @return Returns the maxInterestRate}.
	 */
	public java.lang.Double getMaxInterestRate() {
		return maxInterestRate;
	}

	/**
	 * @param maxInterestRate The maxInterestRate to set.
	 */
	public void setMaxInterestRate(java.lang.Double maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}
	/**
	 * @return Returns the maxLoanAmount.
	 */
	public Money getMaxLoanAmount() {
		return maxLoanAmount;
	}

	/**
	 * @param maxLoanAmount The maxLoanAmount to set.
	 */
	public void setMaxLoanAmount(Money maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	/**
	 * @return Returns the maxNoInstallments}.
	 */
	public java.lang.Short getMaxNoInstallments() {
		return maxNoInstallments;
	}

	/**
	 * @param maxNoInstallments The maxNoInstallments to set.
	 */
	public void setMaxNoInstallments(java.lang.Short maxNoInstallments) {
		this.maxNoInstallments = maxNoInstallments;
	}

	/**
	 * @return Returns the maxPeriod}.
	 */
	public java.lang.Short getMaxPeriod() {
		return maxPeriod;
	}

	/**
	 * @param maxPeriod The maxPeriod to set.
	 */
	public void setMaxPeriod(java.lang.Short maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	/**
	 * @return Returns the minInterestRate}.
	 */
	public java.lang.Double getMinInterestRate() {
		return minInterestRate;
	}

	/**
	 * @param minInterestRate The minInterestRate to set.
	 */
	public void setMinInterestRate(java.lang.Double minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	/**
	 * @return Returns the minLoanAmount}.
	 */
	public Money getMinLoanAmount() {
		return minLoanAmount;
	}

	/**
	 * @param minLoanAmount The minLoanAmount to set.
	 */
	public void setMinLoanAmount(Money minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	/**
	 * @return Returns the minNoInstallments}.
	 */
	public java.lang.Short getMinNoInstallments() {
		return minNoInstallments;
	}

	/**
	 * @param minNoInstallments The minNoInstallments to set.
	 */
	public void setMinNoInstallments(java.lang.Short minNoInstallments) {
		this.minNoInstallments = minNoInstallments;
	}

	/**
	 * @return Returns the minPeriod}.
	 */
	public java.lang.Short getMinPeriod() {
		return minPeriod;
	}

	/**
	 * @param minPeriod The minPeriod to set.
	 */
	public void setMinPeriod(java.lang.Short minPeriod) {
		this.minPeriod = minPeriod;
	}

	/**
	 * @return Returns the penalty}.
	 */
	public Penalty getPenalty() {
		return penalty;
	}

	/**
	 * @param penalty The penalty to set.
	 */
	public void setPenalty(Penalty penalty) {
		this.penalty = penalty;
	}

	/**
	 * @return Returns the penaltyGrace}.
	 */
	public java.lang.Short getPenaltyGrace() {
		return penaltyGrace;
	}

	/**
	 * @param penaltyGrace The penaltyGrace to set.
	 */
	public void setPenaltyGrace(java.lang.Short penaltyGrace) {
		this.penaltyGrace = penaltyGrace;
	}

	/**
	 * @return Returns the prdOffering}.
	 */
	public PrdOffering getPrdOffering() {
		return prdOffering;
	}

	/**
	 * @param prdOffering The prdOffering to set.
	 */
	public void setPrdOffering(PrdOffering prdOffering) {
		this.prdOffering = prdOffering;
	}

	/**
	 * @return Returns the prinDueLastInstFlag}.
	 */
	public java.lang.Short getPrinDueLastInstFlag() {
		return prinDueLastInstFlag;
	}

	/**
	 * @param prinDueLastInstFlag The prinDueLastInstFlag to set.
	 */
	public void setPrinDueLastInstFlag(java.lang.Short prinDueLastInstFlag) {
		this.prinDueLastInstFlag = prinDueLastInstFlag;
	}

	/**
	 * @return Returns the repmtfreqId}.
	 */
	public java.lang.Short getRepmtfreqId() {
		return repmtfreqId;
	}

	/**
	 * @param repmtfreqId The repmtfreqId to set.
	 */
	public void setRepmtfreqId(java.lang.Short repmtfreqId) {
		this.repmtfreqId = repmtfreqId;
	}

	/**
	 * @return Returns the gracePeriodDuration.
	 */
	public java.lang.Short getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	/**
	 * @param gracePeriodDuration The gracePeriodDuration to set.
	 */
	public void setGracePeriodDuration(java.lang.Short gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	public GLCodeEntity getInterestGLCode() {
		return interestGLCode;
	}

	public void setInterestGLCode(GLCodeEntity interestGLCode) {
		this.interestGLCode = interestGLCode;
	}

	public GLCodeEntity getPenaltyGLCode() {
		return penaltyGLCode;
	}

	public void setPenaltyGLCode(GLCodeEntity penaltyGLCode) {
		this.penaltyGLCode = penaltyGLCode;
	}

	public GLCodeEntity getPrincipalGLCode() {
		return principalGLCode;
	}

	public void setPrincipalGLCode(GLCodeEntity principalGLCode) {
		this.principalGLCode = principalGLCode;
	}

	@Override
	public String getResultName() {
		return "LoanOffering";
	}

}
