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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class SavingsOfferingBO extends PrdOfferingBO {

	private RecommendedAmntUnitEntity recommendedAmntUnit;

	private SavingsTypeEntity savingsType;

	private InterestCalcTypeEntity interestCalcType;

	private final Set<PrdOfferingMeetingEntity> prdOfferingMeetings;

	private Money recommendedAmount;

	private Money maxAmntWithdrawl;

	private Money minAmntForInt;

	private Double interestRate;

	private final GLCodeEntity depositGLCode;

	private final GLCodeEntity interestGLCode;

	protected SavingsOfferingBO() {
		depositGLCode = null;
		interestGLCode = null;
		prdOfferingMeetings = new HashSet<PrdOfferingMeetingEntity>();
	}

	public SavingsOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductTypeEntity prdType,
			ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			SavingsTypeEntity savingsType,
			InterestCalcTypeEntity interestCalcType,
			MeetingBO timePerForInstcalc, MeetingBO freqOfPostIntcalc,
			Money recommendedAmount, Double interestRate,
			GLCodeEntity depositGLCode, GLCodeEntity interestGLCode) {
		super(userContext, prdOfferingName, prdOfferingShortName, prdType,
				prdCategory, prdApplicableMaster, startDate);
		this.savingsType = savingsType;
		this.interestCalcType = interestCalcType;
		prdOfferingMeetings = new HashSet<PrdOfferingMeetingEntity>();
		setTimePerForInstcalc(new PrdOfferingMeetingEntity(timePerForInstcalc,
				this, MeetingType.SAVINGSTIMEPERFORINTCALC));
		setFreqOfPostIntcalc(new PrdOfferingMeetingEntity(freqOfPostIntcalc,
				this, MeetingType.SAVINGSFRQINTPOSTACC));
		this.recommendedAmount = recommendedAmount;
		this.interestRate = interestRate;
		this.depositGLCode = depositGLCode;
		this.interestGLCode = interestGLCode;
	}

	private Set<PrdOfferingMeetingEntity> getPrdOfferingMeetings() {
		return prdOfferingMeetings;
	}

	public PrdOfferingMeetingEntity getFreqOfPostIntcalc()
			throws ProductDefinitionException {
		return getPrdOfferingMeeting(MeetingType.SAVINGSFRQINTPOSTACC);
	}

	public void setFreqOfPostIntcalc(PrdOfferingMeetingEntity freqOfPostIntcalc) {
		this.prdOfferingMeetings.add(freqOfPostIntcalc);
	}

	public PrdOfferingMeetingEntity getTimePerForInstcalc()
			throws ProductDefinitionException {
		return getPrdOfferingMeeting(MeetingType.SAVINGSTIMEPERFORINTCALC);
	}

	public void setTimePerForInstcalc(
			PrdOfferingMeetingEntity timePerForInstcalc) {
		this.prdOfferingMeetings.add(timePerForInstcalc);
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

	public InterestCalcTypeEntity getInterestCalcType() {
		return interestCalcType;
	}

	public void setInterestCalcType(InterestCalcTypeEntity interestCalcType) {
		this.interestCalcType = interestCalcType;
	}

	public RecommendedAmntUnitEntity getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	public void setRecommendedAmntUnit(
			RecommendedAmntUnitEntity recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	public SavingsTypeEntity getSavingsType() {
		return savingsType;
	}

	public void setSavingsType(SavingsTypeEntity savingsType) {
		this.savingsType = savingsType;
	}

	public GLCodeEntity getDepositGLCode() {
		return depositGLCode;
	}

	public GLCodeEntity getInterestGLCode() {
		return interestGLCode;
	}

	private PrdOfferingMeetingEntity getPrdOfferingMeeting(
			MeetingType meetingType) throws ProductDefinitionException {
		if (getPrdOfferingMeetings() != null
				&& getPrdOfferingMeetings().size() > 0)
			for (PrdOfferingMeetingEntity prdOfferingMeeting : getPrdOfferingMeetings())
				if (prdOfferingMeeting.getprdOfferingMeetingType().equals(meetingType))
					return prdOfferingMeeting;
		return null;
	}
}
