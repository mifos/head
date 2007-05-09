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
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class SavingsOfferingBO extends PrdOfferingBO {

	private RecommendedAmntUnitEntity recommendedAmntUnit;

	private SavingsTypeEntity savingsType;

	private InterestCalcTypeEntity interestCalcType;

	private final Set<PrdOfferingMeetingEntity> savingsOfferingMeetings;

	private Money recommendedAmount;

	private Money maxAmntWithdrawl;

	private Money minAmntForInt;

	private Double interestRate;

	private final GLCodeEntity depositGLCode;

	private final GLCodeEntity interestGLCode;

	private MifosLogger prdLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	public SavingsOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			SavingsTypeEntity savingsType,
			InterestCalcTypeEntity interestCalcType,
			MeetingBO timePerForInstcalc, MeetingBO freqOfPostIntcalc,
			Money recommendedAmount, Double interestRate,
			GLCodeEntity depositGLCode, GLCodeEntity interestGLCode)
			throws ProductDefinitionException {
		this(userContext, prdOfferingName, prdOfferingShortName, prdCategory,
				prdApplicableMaster, startDate, null, null, null, savingsType,
				interestCalcType, timePerForInstcalc, freqOfPostIntcalc,
				recommendedAmount, null, null, interestRate, depositGLCode,
				interestGLCode);
	}

	public SavingsOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate, String description,
			RecommendedAmntUnitEntity recommendedAmntUnit,
			SavingsTypeEntity savingsType,
			InterestCalcTypeEntity interestCalcType,
			MeetingBO timePerForInstcalc, MeetingBO freqOfPostIntcalc,
			Money recommendedAmount, Money maxAmntWithdrawl,
			Money minAmntForInt, Double interestRate,
			GLCodeEntity depositGLCode, GLCodeEntity interestGLCode)
			throws ProductDefinitionException {
		super(userContext, prdOfferingName, prdOfferingShortName, prdCategory,
				prdApplicableMaster, startDate, endDate, description);
		prdLogger.debug("creating savings product offering");
		validate(savingsType, interestCalcType, recommendedAmntUnit,
				timePerForInstcalc, freqOfPostIntcalc, recommendedAmount,
				interestRate, depositGLCode, interestGLCode);
		setCreateDetails();
		this.savingsType = savingsType;
		this.interestCalcType = interestCalcType;
		this.recommendedAmntUnit = recommendedAmntUnit;

		savingsOfferingMeetings = new HashSet<PrdOfferingMeetingEntity>();
		setTimePerForInstcalc(new PrdOfferingMeetingEntity(timePerForInstcalc,
				this, MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD));
		setFreqOfPostIntcalc(new PrdOfferingMeetingEntity(freqOfPostIntcalc,
				this, MeetingType.SAVINGS_INTEREST_POSTING));
		this.recommendedAmount = recommendedAmount;
		this.interestRate = interestRate;
		this.maxAmntWithdrawl = maxAmntWithdrawl;
		this.minAmntForInt = minAmntForInt;
		this.depositGLCode = depositGLCode;
		this.interestGLCode = interestGLCode;
		prdLogger.debug("creating savings product offering done :"
				+ getGlobalPrdOfferingNum());
	}

	protected SavingsOfferingBO() {
		depositGLCode = null;
		interestGLCode = null;
		savingsOfferingMeetings = new HashSet<PrdOfferingMeetingEntity>();
	}

	private Set<PrdOfferingMeetingEntity> getSavingsOfferingMeetings() {
		return savingsOfferingMeetings;
	}

	public PrdOfferingMeetingEntity getFreqOfPostIntcalc()
			throws ProductDefinitionException {
		return getPrdOfferingMeeting(MeetingType.SAVINGS_INTEREST_POSTING);
	}

	public void setFreqOfPostIntcalc(PrdOfferingMeetingEntity freqOfPostIntcalc) {
		this.savingsOfferingMeetings.add(freqOfPostIntcalc);
	}

	public PrdOfferingMeetingEntity getTimePerForInstcalc()
			throws ProductDefinitionException {
		return getPrdOfferingMeeting(MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD);
	}

	public void setTimePerForInstcalc(
			PrdOfferingMeetingEntity timePerForInstcalc) {
		this.savingsOfferingMeetings.add(timePerForInstcalc);
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

	void setRecommendedAmount(Money recommendedAmount) {
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

	void setRecommendedAmntUnit(
			RecommendedAmntUnitEntity recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	public void setRecommendedAmntUnit(RecommendedAmountUnit unit) {
		setRecommendedAmntUnit(new RecommendedAmntUnitEntity(unit));
	}

	public SavingsTypeEntity getSavingsType() {
		return savingsType;
	}
	
	public SavingsType getSavingsTypeAsEnum() {
		return SavingsType.fromInt(savingsType.getId());
	}

	void setSavingsType(SavingsTypeEntity savingsType) {
		this.savingsType = savingsType;
	}

	public GLCodeEntity getDepositGLCode() {
		return depositGLCode;
	}

	public GLCodeEntity getInterestGLCode() {
		return interestGLCode;
	}

	public void save() throws ProductDefinitionException {
		prdLogger.debug("creating the saving offering ");
		try {
			new SavingsPrdPersistence().createOrUpdate(this);

		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
		prdLogger.debug("creating the saving offering Done : "
				+ getPrdOfferingName());
	}

	@Override
	public boolean isActive(){
		return getStatus() == PrdStatus.SAVINGS_ACTIVE;
	}
	
	public void update(Short userId, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate, String description, PrdStatus prdStatus,
			RecommendedAmntUnitEntity recommendedAmntUnit,
			SavingsTypeEntity savingsType,
			InterestCalcTypeEntity interestCalcType,
			MeetingBO timePerForInstcalc, MeetingBO freqOfPostIntcalc,
			Money recommendedAmount, Money maxAmntWithdrawl,
			Money minAmntForInt, Double interestRate)
			throws ProductDefinitionException {
		super.update(userId, prdOfferingName, prdOfferingShortName,
				prdCategory, prdApplicableMaster, startDate, endDate,
				description, prdStatus);
		validate(savingsType, interestCalcType, recommendedAmntUnit,
				timePerForInstcalc, freqOfPostIntcalc, recommendedAmount,
				interestRate, depositGLCode, interestGLCode);
		setUpdateDetails(userId);
		this.savingsType = savingsType;
		this.interestCalcType = interestCalcType;
		this.recommendedAmntUnit = recommendedAmntUnit;
		this.savingsOfferingMeetings.clear();
		setTimePerForInstcalc(new PrdOfferingMeetingEntity(timePerForInstcalc,
				this, MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD));
		setFreqOfPostIntcalc(new PrdOfferingMeetingEntity(freqOfPostIntcalc,
				this, MeetingType.SAVINGS_INTEREST_POSTING));
		this.recommendedAmount = recommendedAmount;
		this.interestRate = interestRate;
		this.maxAmntWithdrawl = maxAmntWithdrawl;
		this.minAmntForInt = minAmntForInt;
		try {
			new SavingsPrdPersistence().createOrUpdate(this);

		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
		prdLogger.debug("updated savings product offering done :"
				+ getGlobalPrdOfferingNum());
	}

	private PrdOfferingMeetingEntity getPrdOfferingMeeting(
			MeetingType meetingType) throws ProductDefinitionException {
		prdLogger.debug("getting product offering meeting for :" + meetingType);
		if (getSavingsOfferingMeetings() != null
				&& getSavingsOfferingMeetings().size() > 0)
			for (PrdOfferingMeetingEntity prdOfferingMeeting : getSavingsOfferingMeetings())
				if (prdOfferingMeeting.getprdOfferingMeetingType().equals(
						meetingType))
					return prdOfferingMeeting;
		throw new ProductDefinitionException("errors.getmeeting");
	}

	private void validate(SavingsTypeEntity savingsType,
			InterestCalcTypeEntity interestCalcType,
			RecommendedAmntUnitEntity recommendedAmntUnit,
			MeetingBO timePerForInstcalc, MeetingBO freqOfPostIntcalc,
			Money recommendedAmount, Double interestRate,
			GLCodeEntity depositGLCode, GLCodeEntity interestGLCode)
			throws ProductDefinitionException {
		prdLogger.debug("Validating the fields in savings Offering");
		if (savingsType == null
				|| interestCalcType == null
				|| timePerForInstcalc == null
				|| freqOfPostIntcalc == null
				|| interestRate == null
				|| depositGLCode == null
				|| interestGLCode == null
				|| (savingsType.getId()
						.equals(SavingsType.MANDATORY.getValue()) && recommendedAmount == null)
				|| (getPrdApplicableMasterEnum() ==
						ApplicableTo.GROUPS && recommendedAmntUnit == null)) {
			throw new ProductDefinitionException("errors.create");
		}
		prdLogger.debug("Validating the fields in savings Offering done");
	}

}
