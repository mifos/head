/**

 * SavingsOffering.java    version: xxx

 

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

package org.mifos.application.productdefinition.util.valueobjects;

import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.framework.util.helpers.Money;

/**
 * @author ashishsm
 *
 */
public class SavingsOffering extends PrdOffering {
	/**
	 * default constructor
	 */
	public SavingsOffering() {
		super();
		this.setResultName("SavingsOffering");
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 634767587687681L;
	/**
	 * The value of the simple recommendedAmount property. 
	 */
	private Money recommendedAmount;
	/**
	 * The value of the simple maxAmntWithdrawl property. 
	 */
	private Money maxAmntWithdrawl;
	/**
	 * The value of the simple minAmntForInt property. 
	 */
	private Money minAmntForInt;
	/**
	 * The value of the simple interestRate property. 
	 */
	private Double interestRate;
	/**
	 * The value of the RecommendedAmntUnit association
	 */
	private RecommendedAmntUnit recommendedAmntUnit;
	/**
	 * The value of the savingsType association
	 */
	private SavingsType savingsType;
	/**
	 * The value of the interestCalcType association
	 */
	private InterestCalcType interestCalcType;
	/**
	 * The value of the PrdOfferingMeeting association
	 */
	private PrdOfferingMeeting timePerForInstcalc;
	/**
	 * The value of the PrdOfferingMeeting association
	 */
	private PrdOfferingMeeting freqOfPostIntcalc;
	/**
	 * The value of the PrdOfferingMeeting set association
	 */
	private Set<PrdOfferingMeeting> prdOfferingMeetingSet;

	private GLCodeEntity depositGLCode;
    private GLCodeEntity interestGLCode;
	
	/**
	 * @return Returns the prdOfferingMeetingSet.
	 */
	public Set<PrdOfferingMeeting> getPrdOfferingMeetingSet() {
		return prdOfferingMeetingSet;
	}

	/**
	 * @param prdOfferingMeetingSet The prdOfferingMeetingSet to set.
	 */
	public void setPrdOfferingMeetingSet(
			Set<PrdOfferingMeeting> prdOfferingMeetingSet) {
		this.prdOfferingMeetingSet = prdOfferingMeetingSet;
	}

	/**
	 * @return Returns the freqOfPostIntcalc.
	 */
	public PrdOfferingMeeting getFreqOfPostIntcalc() {
		return freqOfPostIntcalc;
	}

	/**
	 * @param freqOfPostIntcalc The freqOfPostIntcalc to set.
	 */
	public void setFreqOfPostIntcalc(PrdOfferingMeeting freqOfPostIntcalc) {
		if(freqOfPostIntcalc != null) {
			freqOfPostIntcalc.setPrdOffering(this);
		}
		this.freqOfPostIntcalc = freqOfPostIntcalc;
	}

	/**
	 * @return Returns the timePerForInstcalc.
	 */
	public PrdOfferingMeeting getTimePerForInstcalc() {
		return timePerForInstcalc;
	}

	/**
	 * @param timePerForInstcalc The timePerForInstcalc to set.
	 */
	public void setTimePerForInstcalc(PrdOfferingMeeting timePerForInstcalc) {
		if(timePerForInstcalc != null) {
			timePerForInstcalc.setPrdOffering(this);
		}
		this.timePerForInstcalc = timePerForInstcalc;
	}

	/**
	 * @return Returns the interestRate.
	 */
	public Double getInterestRate() {
		return interestRate;
	}

	/**
	 * @param interestRate The interestRate to set.
	 */
	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	/**
	 * @return Returns the maxAmntWithdrawl.
	 */
	public Money getMaxAmntWithdrawl() {
		return maxAmntWithdrawl;
	}

	/**
	 * @param maxAmntWithdrawl The maxAmntWithdrawl to set.
	 */
	public void setMaxAmntWithdrawl(Money maxAmntWithdrawl) {
		this.maxAmntWithdrawl = maxAmntWithdrawl;
	}

	/**
	 * @return Returns the minAmntForInt.
	 */
	public Money getMinAmntForInt() {
		return minAmntForInt;
	}

	/**
	 * @param minAmntForInt The minAmntForInt to set.
	 */
	public void setMinAmntForInt(Money minAmntForInt) {
		this.minAmntForInt = minAmntForInt;
	}

	/**
	 * @return Returns the recommendedAmount.
	 */
	public Money getRecommendedAmount() {
		return recommendedAmount;
	}

	/**
	 * @param recommendedAmount The recommendedAmount to set.
	 */
	public void setRecommendedAmount(Money recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	/**
	 * @return Returns the interestCalcType.
	 */
	public InterestCalcType getInterestCalcType() {
		return interestCalcType;
	}

	/**
	 * @param interestCalcType The interestCalcType to set.
	 */
	public void setInterestCalcType(InterestCalcType interestCalcType) {
		this.interestCalcType = interestCalcType;
	}

	/**
	 * @return Returns the recommendedAmntUnit.
	 */
	public RecommendedAmntUnit getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	/**
	 * @param recommendedAmntUnit The recommendedAmntUnit to set.
	 */
	public void setRecommendedAmntUnit(RecommendedAmntUnit recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	/**
	 * @return Returns the savingsType.
	 */
	public SavingsType getSavingsType() {
		return savingsType;
	}

	/**
	 * @param savingsType The savingsType to set.
	 */
	public void setSavingsType(SavingsType savingsType) {
		this.savingsType = savingsType;
	}

	public GLCodeEntity getDepositGLCode() {
		return depositGLCode;
	}

	public void setDepositGLCode(GLCodeEntity depositGLCode) {
		this.depositGLCode = depositGLCode;
	}

	public GLCodeEntity getInterestGLCode() {
		return interestGLCode;
	}

	public void setInterestGLCode(GLCodeEntity interestGLCode) {
		this.interestGLCode = interestGLCode;
	}
	
	
}
