/**

 * LoanOffering.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.productdefinition.business;

import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.application.master.util.valueobjects.InterestCalcRule;
import org.mifos.application.master.util.valueobjects.InterestTypes;
import org.mifos.application.master.util.valueobjects.YesNoMaster;
import org.mifos.application.penalty.util.valueobjects.Penalty;
import org.mifos.framework.security.util.UserContext;

/**
 * @author ashishsm
 * 
 */
public class LoanOfferingBO extends PrdOfferingBO {


	private GracePeriodTypeEntity gracePeriodType;

	private Short gracePeriodDuration;

	private InterestTypes interestTypes;

	private Penalty penalty;

	private InterestCalcRule interestCalcRule;

	private Double minLoanAmount;

	private Double maxLoanAmount;

	private Double defaultLoanAmount;

	private Short minPeriod;

	private Short maxPeriod;

	private Short defaultPeriod;

	private String collateralNotes;

	private Short lateness;

	private Short repmtfreqId;

	private Double maxInterestRate;

	private Double minInterestRate;

	private Double defInterestRate;

	private Currency currency;

	private Short maxNoInstallments;

	private Short minNoInstallments;

	private Short defNoInstallments;

	private Short penaltyGrace;

	private YesNoMaster loanCounter;

	private Short intDedDisbursementFlag;

	private Short prinDueLastInstFlag;

	private Double penaltyRate;

	private Set<LoanOfferingFundEntity> loanOfferingFunds;

	private Set<PrdOfferingFeesEntity> prdOfferingFees;

	private PrdOfferingMeetingEntity prdOfferingMeeting;
	
	private GLCodeEntity principalGLcode;

	private GLCodeEntity interestGLcode;

	private GLCodeEntity penaltyGLcode;


	public LoanOfferingBO() {

	}

	public LoanOfferingBO(UserContext userContext) {
		super(userContext);
	}

	public PrdOfferingMeetingEntity getPrdOfferingMeeting() {
		return prdOfferingMeeting;
	}

	public void setPrdOfferingMeeting(
			PrdOfferingMeetingEntity prdOfferingMeeting) {
		if (prdOfferingMeeting != null) {
			prdOfferingMeeting.setPrdOffering(this);
		}
		this.prdOfferingMeeting = prdOfferingMeeting;
	}

	public Double getPenaltyRate() {
		return penaltyRate;
	}

	public void setPenaltyRate(Double penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	public String getCollateralNotes() {
		return collateralNotes;
	}

	public void setCollateralNotes(String collateralNotes) {
		this.collateralNotes = collateralNotes;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Double getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	public void setDefaultLoanAmount(Double defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	public Short getDefaultPeriod() {
		return defaultPeriod;
	}

	public void setDefaultPeriod(Short defaultPeriod) {
		this.defaultPeriod = defaultPeriod;
	}

	public Double getDefInterestRate() {
		return defInterestRate;
	}

	public void setDefInterestRate(Double defInterestRate) {
		this.defInterestRate = defInterestRate;
	}

	public Short getDefNoInstallments() {
		return defNoInstallments;
	}

	public void setDefNoInstallments(Short defNoInstallments) {
		this.defNoInstallments = defNoInstallments;
	}

	public GracePeriodTypeEntity getGracePeriodType() {
		return gracePeriodType;
	}

	public void setGracePeriodType(GracePeriodTypeEntity gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	private Short getIntDedDisbursementFlag() {
		return intDedDisbursementFlag;
	}

	private void setIntDedDisbursementFlag(Short intDedDisbursementFlag) {
		this.intDedDisbursementFlag = intDedDisbursementFlag;
	}

	public boolean isIntDedDisbursement(){
		return this.intDedDisbursementFlag>0;
	}
	public void setIntDedDisbursement(boolean intDedDisbursementFlag){
		this.intDedDisbursementFlag=(short)(intDedDisbursementFlag?1:0);
	}
	
	public InterestCalcRule getInterestCalcRule() {
		return interestCalcRule;
	}

	public void setInterestCalcRule(InterestCalcRule interestCalcRule) {
		this.interestCalcRule = interestCalcRule;
	}

	public InterestTypes getInterestTypes() {
		return interestTypes;
	}

	public void setInterestTypes(InterestTypes interestTypes) {
		this.interestTypes = interestTypes;
	}

	public Short getLateness() {
		return lateness;
	}

	public void setLateness(Short lateness) {
		this.lateness = lateness;
	}


	public Double getMaxInterestRate() {
		return maxInterestRate;
	}

	public void setMaxInterestRate(Double maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}

	public Double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMaxLoanAmount(Double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public Short getMaxNoInstallments() {
		return maxNoInstallments;
	}

	public void setMaxNoInstallments(Short maxNoInstallments) {
		this.maxNoInstallments = maxNoInstallments;
	}

	public Short getMaxPeriod() {
		return maxPeriod;
	}

	public void setMaxPeriod(Short maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	public Double getMinInterestRate() {
		return minInterestRate;
	}

	public void setMinInterestRate(Double minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	public Double getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setMinLoanAmount(Double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public Short getMinNoInstallments() {
		return minNoInstallments;
	}

	public void setMinNoInstallments(Short minNoInstallments) {
		this.minNoInstallments = minNoInstallments;
	}

	public Short getMinPeriod() {
		return minPeriod;
	}

	public void setMinPeriod(Short minPeriod) {
		this.minPeriod = minPeriod;
	}

	public Penalty getPenalty() {
		return penalty;
	}

	public void setPenalty(Penalty penalty) {
		this.penalty = penalty;
	}

	public Short getPenaltyGrace() {
		return penaltyGrace;
	}

	public void setPenaltyGrace(Short penaltyGrace) {
		this.penaltyGrace = penaltyGrace;
	}


	private Short getPrinDueLastInstFlag() {
		return prinDueLastInstFlag;
	}

	private void setPrinDueLastInstFlag(Short prinDueLastInstFlag) {
		this.prinDueLastInstFlag = prinDueLastInstFlag;
	}

	public boolean isPrinDueLastInst(){
		return this.prinDueLastInstFlag>0;
	}
	public void setPrinDueLastInst(boolean prinDueLastInst){
		this.prinDueLastInstFlag=(short)(prinDueLastInst?1:0);
	}
	public Short getRepmtfreqId() {
		return repmtfreqId;
	}

	public void setRepmtfreqId(Short repmtfreqId) {
		this.repmtfreqId = repmtfreqId;
	}

	public Short getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	public void setGracePeriodDuration(Short gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	public Set<LoanOfferingFundEntity> getLoanOfferingFunds() {
		return loanOfferingFunds;
	}

	private void setLoanOfferingFunds(
			Set<LoanOfferingFundEntity> loanOfferingFunds) {
		this.loanOfferingFunds = loanOfferingFunds;
	}

	public void addLoanOfferingFund(LoanOfferingFundEntity loanOfferingFund) {
		if (null != loanOfferingFund) {
			loanOfferingFund.setLoanOffering(this);
			this.loanOfferingFunds.add(loanOfferingFund);
		}

	}

	public Set<PrdOfferingFeesEntity> getPrdOfferingFees() {
		return prdOfferingFees;
	}

	private void setPrdOfferingFees(Set<PrdOfferingFeesEntity> prdOfferingFees) {
		this.prdOfferingFees = prdOfferingFees;
	}

	public void addPrdOfferingFee(PrdOfferingFeesEntity prdOfferingFeesEntity) {
		if (null != prdOfferingFeesEntity) {
			prdOfferingFeesEntity.setLoanOffering(this);
			this.prdOfferingFees.add(prdOfferingFeesEntity);
		}
	}

	public YesNoMaster getLoanCounter() {
		return loanCounter;
	}

	public void setLoanCounter(YesNoMaster loanCounter) {
		this.loanCounter = loanCounter;
	}

	public GLCodeEntity getInterestGLcode() {
		return interestGLcode;
	}

	public void setInterestGLcode(GLCodeEntity interestGLcode) {
		this.interestGLcode = interestGLcode;
	}

	public GLCodeEntity getPenaltyGLcode() {
		return penaltyGLcode;
	}

	public void setPenaltyGLcode(GLCodeEntity penaltyGLcode) {
		this.penaltyGLcode = penaltyGLcode;
	}

	public GLCodeEntity getPrincipalGLcode() {
		return principalGLcode;
	}

	public void setPrincipalGLcode(GLCodeEntity principalGLcode) {
		this.principalGLcode = principalGLcode;
	}
}
