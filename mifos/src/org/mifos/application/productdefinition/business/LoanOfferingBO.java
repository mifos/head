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

package org.mifos.application.productdefinition.business;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class LoanOfferingBO extends PrdOfferingBO {

	private GracePeriodTypeEntity gracePeriodType;

	private InterestTypesEntity interestTypes;

	private Short gracePeriodDuration;

	private Money maxLoanAmount;

	private Money minLoanAmount;

	private Money defaultLoanAmount;

	private Double maxInterestRate;

	private Double minInterestRate;

	private Double defInterestRate;

	private Short maxNoInstallments;

	private Short minNoInstallments;

	private Short defNoInstallments;

	private Short intDedDisbursement;

	private Short prinDueLastInst;

	private Short loanCounter;

	private final Set<LoanOfferingFundEntity> loanOfferingFunds;

	private final Set<LoanOfferingFeesEntity> loanOfferingFees;

	private PrdOfferingMeetingEntity loanOfferingMeeting;

	private final GLCodeEntity principalGLcode;

	private final GLCodeEntity interestGLcode;

	private MifosLogger prdLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	public LoanOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			InterestTypesEntity interestTypes, Money minLoanAmount,
			Money maxLoanAmount, Double maxInterestRate,
			Double minInterestRate, Double defInterestRate,
			Short maxNoInstallments, Short minNoInstallments,
			Short defNoInstallments, boolean loanCounter,
			boolean intDedDisbursement, boolean prinDueLastInst,
			MeetingBO meeting, GLCodeEntity principalGLcode,
			GLCodeEntity interestGLcode) throws ProductDefinitionException {
		this(userContext, prdOfferingName, prdOfferingShortName, prdCategory,
				prdApplicableMaster, startDate, null, null, null, null,
				interestTypes, minLoanAmount, maxLoanAmount, null,
				maxInterestRate, minInterestRate, defInterestRate,
				maxNoInstallments, minNoInstallments, defNoInstallments,
				loanCounter, intDedDisbursement, prinDueLastInst, null, null,
				meeting, principalGLcode, interestGLcode);
	}

	public LoanOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate, String description,
			GracePeriodTypeEntity gracePeriodType, Short gracePeriodDuration,
			InterestTypesEntity interestTypes, Money minLoanAmount,
			Money maxLoanAmount, Money defaultLoanAmount,
			Double maxInterestRate, Double minInterestRate,
			Double defInterestRate, Short maxNoInstallments,
			Short minNoInstallments, Short defNoInstallments,
			boolean loanCounter, boolean intDedDisbursement,
			boolean prinDueLastInst, List<FundBO> funds, List<FeeBO> fees,
			MeetingBO meeting, GLCodeEntity principalGLcode,
			GLCodeEntity interestGLcode) throws ProductDefinitionException {
		super(userContext, prdOfferingName, prdOfferingShortName, prdCategory,
				prdApplicableMaster, startDate, endDate, description);
		prdLogger.debug("building Loan offering");
		validate(gracePeriodType, gracePeriodDuration, interestTypes,
				minLoanAmount, maxLoanAmount, defaultLoanAmount,
				maxInterestRate, minInterestRate, defInterestRate,
				maxNoInstallments, minNoInstallments, defNoInstallments,
				loanCounter, intDedDisbursement, prinDueLastInst, funds, fees,
				meeting, principalGLcode, interestGLcode);
		setCreateDetails();
		setLoanCounter(loanCounter);
		setIntDedDisbursement(intDedDisbursement);
		setPrinDueLastInst(prinDueLastInst);
		setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType,
				gracePeriodDuration);
		this.interestTypes = interestTypes;
		this.minLoanAmount = minLoanAmount;
		this.maxLoanAmount = maxLoanAmount;
		this.defaultLoanAmount = defaultLoanAmount;
		this.maxInterestRate = maxInterestRate;
		this.minInterestRate = minInterestRate;
		this.defInterestRate = defInterestRate;
		this.maxNoInstallments = maxNoInstallments;
		this.minNoInstallments = minNoInstallments;
		this.defNoInstallments = defNoInstallments;
		this.principalGLcode = principalGLcode;
		this.interestGLcode = interestGLcode;
		this.loanOfferingFunds = new HashSet<LoanOfferingFundEntity>();
		if (funds != null && funds.size() > 0) {
			for (FundBO fund : funds) {
				addLoanOfferingFund(new LoanOfferingFundEntity(fund, this));
			}
		}
		this.loanOfferingMeeting = new PrdOfferingMeetingEntity(meeting, this,
				MeetingType.LOANFREQUENCYOFINSTALLMENTS);
		this.loanOfferingFees = new HashSet<LoanOfferingFeesEntity>();
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				if (isFrequencyMatchingOfferingFrequency(fee, meeting))
					addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
			}
		}
		prdLogger.debug("Loan offering build :" + getGlobalPrdOfferingNum());
	}

	protected LoanOfferingBO() {
		principalGLcode = null;
		interestGLcode = null;
		loanOfferingFunds = new HashSet<LoanOfferingFundEntity>();
		loanOfferingFees = new HashSet<LoanOfferingFeesEntity>();
	}

	public GracePeriodTypeEntity getGracePeriodType() {
		return gracePeriodType;
	}

	public InterestTypesEntity getInterestTypes() {
		return interestTypes;
	}

	public Short getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	public Money getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMinLoanAmount(Money minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public Money getDefaultLoanAmount() {
		return defaultLoanAmount;
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

	public Short getMaxNoInstallments() {
		return maxNoInstallments;
	}

	public Short getMinNoInstallments() {
		return minNoInstallments;
	}

	public Short getDefNoInstallments() {
		return defNoInstallments;
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

	public GLCodeEntity getPrincipalGLcode() {
		return principalGLcode;
	}

	public GLCodeEntity getInterestGLcode() {
		return interestGLcode;
	}

	public void setGracePeriodType(GracePeriodTypeEntity gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	public void setInterestTypes(InterestTypesEntity interestTypes) {
		this.interestTypes = interestTypes;
	}

	public void setGracePeriodDuration(Short gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	public void setMaxLoanAmount(Money maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public Money getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setDefaultLoanAmount(Money defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	public void setMaxInterestRate(Double maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}

	public void setMinInterestRate(Double minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	public void setDefInterestRate(Double defInterestRate) {
		this.defInterestRate = defInterestRate;
	}

	public void setMaxNoInstallments(Short maxNoInstallments) {
		this.maxNoInstallments = maxNoInstallments;
	}

	public void setMinNoInstallments(Short minNoInstallments) {
		this.minNoInstallments = minNoInstallments;
	}

	public void setDefNoInstallments(Short defNoInstallments) {
		this.defNoInstallments = defNoInstallments;
	}

	public void setIntDedDisbursement(boolean intDedDisbursementFlag) {
		this.intDedDisbursement = intDedDisbursementFlag ? YesNoFlag.YES
				.getValue() : YesNoFlag.NO.getValue();
	}

	public void setPrinDueLastInst(boolean prinDueLastInst) {
		this.prinDueLastInst = prinDueLastInst ? YesNoFlag.YES.getValue()
				: YesNoFlag.NO.getValue();
	}

	public void setLoanCounter(boolean loanCounter) {
		this.loanCounter = loanCounter ? YesNoFlag.YES.getValue()
				: YesNoFlag.NO.getValue();
	}

	public void addLoanOfferingFund(LoanOfferingFundEntity loanOfferingFund) {
		if (null != loanOfferingFund)
			this.loanOfferingFunds.add(loanOfferingFund);
	}

	public void addPrdOfferingFee(LoanOfferingFeesEntity prdOfferingFeesEntity) {
		if (null != prdOfferingFeesEntity)
			this.loanOfferingFees.add(prdOfferingFeesEntity);
	}

	public void setLoanOfferingMeeting(
			PrdOfferingMeetingEntity prdOfferingMeeting) {
		this.loanOfferingMeeting = prdOfferingMeeting;
	}

	public boolean isFeePresent(FeeBO fee) {
		prdLogger.debug("checking isFeePresent :" + fee);
		if (loanOfferingFees != null && loanOfferingFees.size() > 0)
			for (LoanOfferingFeesEntity prdOfferingFee : loanOfferingFees) {
				if (prdOfferingFee.isFeePresent(fee.getFeeId()))
					return true;
			}
		return false;
	}

	public void save() throws ProductDefinitionException {
		try {
			new LoanPersistance().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}

	public void update(Short userId, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate, String description, PrdStatus status,
			GracePeriodTypeEntity gracePeriodType,
			InterestTypesEntity interestTypes, Short gracePeriodDuration,
			Money maxLoanAmount, Money minLoanAmount, Money defaultLoanAmount,
			Double maxInterestRate, Double minInterestRate,
			Double defInterestRate, Short maxNoInstallments,
			Short minNoInstallments, Short defNoInstallments,
			boolean loanCounter, boolean intDedDisbursement,
			boolean prinDueLastInst, List<FundBO> funds, List<FeeBO> fees,
			Short recurAfter, RecurrenceType recurrenceType)
			throws ProductDefinitionException {
		prdLogger.debug("Updating loan Offering :" + prdOfferingName);
		super.update(userId, prdOfferingName, prdOfferingShortName,
				prdCategory, prdApplicableMaster, startDate, endDate,
				description, status);
		validateForUpdate(gracePeriodType, gracePeriodDuration, interestTypes,
				minLoanAmount, maxLoanAmount, defaultLoanAmount,
				maxInterestRate, minInterestRate, defInterestRate,
				maxNoInstallments, minNoInstallments, defNoInstallments,
				loanCounter, intDedDisbursement, prinDueLastInst, funds, fees,
				recurAfter);
		setUpdateDetails(userId);
		setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType,
				gracePeriodDuration);
		this.interestTypes = interestTypes;
		this.maxLoanAmount = maxLoanAmount;
		this.minLoanAmount = minLoanAmount;
		this.defaultLoanAmount = defaultLoanAmount;
		this.maxInterestRate = maxInterestRate;
		this.minInterestRate = minInterestRate;
		this.defInterestRate = defInterestRate;
		this.maxNoInstallments = maxNoInstallments;
		this.minNoInstallments = minNoInstallments;
		this.defNoInstallments = defNoInstallments;
		setLoanCounter(loanCounter);
		setIntDedDisbursement(intDedDisbursement);
		setPrinDueLastInst(prinDueLastInst);
		if (this.loanOfferingMeeting.getMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId().equals(
						recurrenceType.getValue())) {
			this.loanOfferingMeeting.getMeeting().getMeetingDetails()
					.setRecurAfter(recurAfter);
		} else {
			try {
				this.loanOfferingMeeting.setMeeting(new MeetingBO(
						recurrenceType, recurAfter, startDate,
						MeetingType.LOANFREQUENCYOFINSTALLMENTS));
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
					if (isFrequencyMatchingOfferingFrequency(fee,
							this.loanOfferingMeeting.getMeeting()))
						addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
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

	private void validate(GracePeriodTypeEntity gracePeriodType,
			Short gracePeriodDuration, InterestTypesEntity interestTypes,
			Money minLoanAmount, Money maxLoanAmount, Money defaultLoanAmount,
			Double maxInterestRate, Double minInterestRate,
			Double defInterestRate, Short maxNoInstallments,
			Short minNoInstallments, Short defNoInstallments,
			boolean loanCounter, boolean intDedDisbursement,
			boolean prinDueLastInst, List<FundBO> funds, List<FeeBO> fees,
			MeetingBO meeting, GLCodeEntity principalGLcode,
			GLCodeEntity interestGLcode) throws ProductDefinitionException {
		prdLogger.debug("validating fields in Loan offering ");
		if (interestTypes == null
				|| minLoanAmount == null
				|| maxLoanAmount == null
				|| maxInterestRate == null
				|| minInterestRate == null
				|| defInterestRate == null
				|| maxNoInstallments == null
				|| minNoInstallments == null
				|| defNoInstallments == null
				|| meeting == null
				|| principalGLcode == null
				|| interestGLcode == null
				|| (minLoanAmount.getAmountDoubleValue() > maxLoanAmount
						.getAmountDoubleValue())
				|| (defaultLoanAmount != null && (defaultLoanAmount
						.getAmountDoubleValue() < minLoanAmount
						.getAmountDoubleValue() || defaultLoanAmount
						.getAmountDoubleValue() > maxLoanAmount
						.getAmountDoubleValue()))
				|| (minInterestRate > maxInterestRate)
				|| (defInterestRate < minInterestRate || defInterestRate > maxInterestRate)
				|| (minNoInstallments > maxNoInstallments)
				|| (defNoInstallments < minNoInstallments || defNoInstallments > maxNoInstallments)
				|| (!intDedDisbursement
						&& gracePeriodType != null
						&& !gracePeriodType.getId().equals(
								GraceTypeConstants.NONE.getValue()) && gracePeriodDuration == null)) {
			throw new ProductDefinitionException("errors.create");
		}
	}

	private void setGracePeriodTypeAndDuration(boolean intDedDisbursement,
			GracePeriodTypeEntity gracePeriodType, Short gracePeriodDuration) {
		prdLogger
				.debug("Loan offering setGracePeriodTypeAndDuration called - intDedDisbursement:"
						+ intDedDisbursement);
		if (intDedDisbursement
				|| gracePeriodType == null
				|| gracePeriodType.getId().equals(
						GraceTypeConstants.NONE.getValue())) {
			this.gracePeriodType = new GracePeriodTypeEntity(
					GraceTypeConstants.NONE);
			this.gracePeriodDuration = (short) 0;
		} else {
			this.gracePeriodType = gracePeriodType;
			this.gracePeriodDuration = gracePeriodDuration;
		}
		prdLogger
				.debug("After Loan offering setGracePeriodTypeAndDuration called- gracePeriodType:"
						+ this.gracePeriodType
						+ "-gracePeriodDuration :"
						+ this.gracePeriodDuration);
	}

	private boolean isFrequencyMatchingOfferingFrequency(FeeBO fee,
			MeetingBO meeting) throws ProductDefinitionException {
		prdLogger
				.debug("Loan offering isFrequencyMatchingOfferingFrequency called - fee:"
						+ fee);
		if (fee.isOneTime())
			return true;
		else if (fee.getFeeFrequency().getFeeMeetingFrequency()
				.getMeetingDetails().getRecurrenceType().getRecurrenceId()
				.equals(
						meeting.getMeetingDetails().getRecurrenceType()
								.getRecurrenceId()))
			return true;
		else
			throw new ProductDefinitionException(
					ProductDefinitionConstants.ERRORFEEFREQUENCY);

	}

	private void validateForUpdate(GracePeriodTypeEntity gracePeriodType,
			Short gracePeriodDuration, InterestTypesEntity interestTypes,
			Money minLoanAmount, Money maxLoanAmount, Money defaultLoanAmount,
			Double maxInterestRate, Double minInterestRate,
			Double defInterestRate, Short maxNoInstallments,
			Short minNoInstallments, Short defNoInstallments,
			boolean loanCounter, boolean intDedDisbursement,
			boolean prinDueLastInst, List<FundBO> funds, List<FeeBO> fees,
			Short recurAfter) throws ProductDefinitionException {
		prdLogger.debug("validating fields in Loan offering for update");
		if (interestTypes == null
				|| minLoanAmount == null
				|| maxLoanAmount == null
				|| maxInterestRate == null
				|| minInterestRate == null
				|| defInterestRate == null
				|| maxNoInstallments == null
				|| minNoInstallments == null
				|| defNoInstallments == null
				|| recurAfter == null
				|| (minLoanAmount.getAmountDoubleValue() > maxLoanAmount
						.getAmountDoubleValue())
				|| (defaultLoanAmount != null && (defaultLoanAmount
						.getAmountDoubleValue() < minLoanAmount
						.getAmountDoubleValue() || defaultLoanAmount
						.getAmountDoubleValue() > maxLoanAmount
						.getAmountDoubleValue()))
				|| (minInterestRate > maxInterestRate)
				|| (defInterestRate < minInterestRate || defInterestRate > maxInterestRate)
				|| (minNoInstallments > maxNoInstallments)
				|| (defNoInstallments < minNoInstallments || defNoInstallments > maxNoInstallments)
				|| (!intDedDisbursement
						&& gracePeriodType != null
						&& !gracePeriodType.getId().equals(
								GraceTypeConstants.NONE.getValue()) && gracePeriodDuration == null)) {
			throw new ProductDefinitionException("errors.update");
		}
	}

}
