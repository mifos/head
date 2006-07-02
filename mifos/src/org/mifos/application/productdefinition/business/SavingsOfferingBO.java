/**

 * SavingsOfferingBO.java    version: xxx

 

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

package org.mifos.application.productdefinition.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class SavingsOfferingBO extends PrdOfferingBO {

	private Money recommendedAmount;

	private Money maxAmntWithdrawl;

	private Money minAmntForInt;

	private Double interestRate;

	private RecommendedAmntUnit recommendedAmntUnit;

	private SavingsType savingsType;

	private InterestCalcType interestCalcType;

	private Set<PrdOfferingMeetingEntity> prdOfferingMeetings;

	private GLCodeEntity depositGLCode;

	private GLCodeEntity interestGLCode;

	public SavingsOfferingBO() {
		prdOfferingMeetings = new HashSet<PrdOfferingMeetingEntity>();
	}

	public SavingsOfferingBO(UserContext userContext) {
		super(userContext);
		prdOfferingMeetings = new HashSet<PrdOfferingMeetingEntity>();
	}

	public Set<PrdOfferingMeetingEntity> getPrdOfferingMeetings() {
		return prdOfferingMeetings;
	}

	private void setPrdOfferingMeetings(
			Set<PrdOfferingMeetingEntity> prdOfferingMeetings) {
		this.prdOfferingMeetings = prdOfferingMeetings;
	}

	private void addPrdOfferingMeeting(
			PrdOfferingMeetingEntity prdOfferingMeetingEntity) {
		prdOfferingMeetingEntity.setPrdOffering(this);
		this.prdOfferingMeetings.add(prdOfferingMeetingEntity);
	}

	public PrdOfferingMeetingEntity getFreqOfPostIntcalc() {
		if (getPrdOfferingMeetings() != null
				&& getPrdOfferingMeetings().size() > 0)
			for (PrdOfferingMeetingEntity prdOfferingMeeting : getPrdOfferingMeetings())
				if (prdOfferingMeeting.getMeetingType().equals(
						ProductDefinitionConstants.SAVINGSFRQINTPOSTACCID))
					return prdOfferingMeeting;
		return null;
	}

	public void setFreqOfPostIntcalc(PrdOfferingMeetingEntity freqOfPostIntcalc) {
		if (freqOfPostIntcalc != null) {
			freqOfPostIntcalc
					.setMeetingType(ProductDefinitionConstants.SAVINGSFRQINTPOSTACCID);
			addPrdOfferingMeeting(freqOfPostIntcalc);
		}
	}

	public PrdOfferingMeetingEntity getTimePerForInstcalc() {
		if (getPrdOfferingMeetings() != null
				&& getPrdOfferingMeetings().size() > 0)
			for (PrdOfferingMeetingEntity prdOfferingMeeting : getPrdOfferingMeetings())
				if (prdOfferingMeeting.getMeetingType().equals(
						ProductDefinitionConstants.SAVINGSTIMEPERINTCALCID))
					return prdOfferingMeeting;
		return null;
	}

	public void setTimePerForInstcalc(
			PrdOfferingMeetingEntity timePerForInstcalc) {
		if (timePerForInstcalc != null) {
			timePerForInstcalc
					.setMeetingType(ProductDefinitionConstants.SAVINGSTIMEPERINTCALCID);
			addPrdOfferingMeeting(timePerForInstcalc);
		}
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public Money getMaxAmntWithdrawl() {
		return maxAmntWithdrawl;
	}

	public void setMaxAmntWithdrawl(Money maxAmntWithdrawl) {
		this.maxAmntWithdrawl = maxAmntWithdrawl;
	}

	public Money getMinAmntForInt() {
		return minAmntForInt;
	}

	public void setMinAmntForInt(Money minAmntForInt) {
		this.minAmntForInt = minAmntForInt;
	}

	public Money getRecommendedAmount() {
		return recommendedAmount;
	}

	public void setRecommendedAmount(Money recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	public InterestCalcType getInterestCalcType() {
		return interestCalcType;
	}

	public void setInterestCalcType(InterestCalcType interestCalcType) {
		this.interestCalcType = interestCalcType;
	}

	public RecommendedAmntUnit getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	public void setRecommendedAmntUnit(RecommendedAmntUnit recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	public SavingsType getSavingsType() {
		return savingsType;
	}

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
