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


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

/**
 * A loan product is a set of rules (interest rate, number of installments,
 * maximum amount, etc) which describes a particular kind of loan that
 * an MFI offers.
 * 
 * Although we may sometimes call these "offerings", it is probably better
 * to call them "loan products" (as that seems to be the terminology in
 * the functional spec and elsewhere).
 */
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

	private final Set<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoan;

	private final Set<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycle;

	private final Set<NoOfInstallFromLastLoanAmountBO> noOfInstallFromLastLoan;

	private final Set<NoOfInstallFromLoanCycleBO> noOfInstallFromLoanCycle;

	private final Set<LoanAmountSameForAllLoanBO> loanAmountSameForAllLoan;

	private final Set<NoOfInstallSameForAllLoanBO> noOfInstallSameForAllLoan;

	private PrdOfferingMeetingEntity loanOfferingMeeting;

	private final GLCodeEntity principalGLcode;

	private final GLCodeEntity interestGLcode;

	private MifosLogger prdLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	public static LoanOfferingBO ALL_LOAN_PRD = new LoanOfferingBO();
	static {
		ALL_LOAN_PRD.setPrdOfferingName("ALL");
	}

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
		this.loanAmountFromLastLoan = new HashSet<LoanAmountFromLastLoanAmountBO>();
		this.loanAmountFromLoanCycle = new HashSet<LoanAmountFromLoanCycleBO>();
		this.noOfInstallFromLastLoan = new HashSet<NoOfInstallFromLastLoanAmountBO>();
		this.noOfInstallFromLoanCycle = new HashSet<NoOfInstallFromLoanCycleBO>();
		this.loanAmountSameForAllLoan = new HashSet<LoanAmountSameForAllLoanBO>();
		this.noOfInstallSameForAllLoan = new HashSet<NoOfInstallSameForAllLoanBO>();
		if (funds != null && funds.size() > 0) {
			for (FundBO fund : funds) {
				addLoanOfferingFund(new LoanOfferingFundEntity(fund, this));
			}
		}
		this.loanOfferingMeeting = new PrdOfferingMeetingEntity(meeting, this,
				MeetingType.LOAN_INSTALLMENT);
		this.loanOfferingFees = new HashSet<LoanOfferingFeesEntity>();
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				if (isFrequencyMatchingOfferingFrequency(fee, meeting))
					addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
			}
		}
		prdLogger.debug("Loan offering build :" + getGlobalPrdOfferingNum());
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
			GLCodeEntity interestGLcode, String loanAmtCalcType,
			String calcInstallmentType) throws ProductDefinitionException {
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
		if (loanAmtCalcType.equals("1")) {
			loanAmountSameForAllLoan.add(new LoanAmountSameForAllLoanBO(Double
					.parseDouble(minLoanAmount.toString()), Double
					.parseDouble(maxLoanAmount.toString()), Double
					.parseDouble(defaultLoanAmount.toString()), this));
		}
		if (calcInstallmentType.equals("1")) {
			noOfInstallSameForAllLoan.add(new NoOfInstallSameForAllLoanBO(
					(Short.parseShort(minNoInstallments.toString())), Short
							.parseShort(maxNoInstallments.toString()), Short
							.parseShort(defNoInstallments.toString()), this));
		}
		if (funds != null && funds.size() > 0) {
			for (FundBO fund : funds) {
				addLoanOfferingFund(new LoanOfferingFundEntity(fund, this));
			}
		}
		this.loanOfferingMeeting = new PrdOfferingMeetingEntity(meeting, this,
				MeetingType.LOAN_INSTALLMENT);
		this.loanOfferingFees = new HashSet<LoanOfferingFeesEntity>();
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				if (isFrequencyMatchingOfferingFrequency(fee, meeting))
					addPrdOfferingFee(new LoanOfferingFeesEntity(this, fee));
			}
		}
		prdLogger.debug("Loan offering build :" + getGlobalPrdOfferingNum());
	}

	public LoanOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate, String description,
			GracePeriodTypeEntity gracePeriodType, Short gracePeriodDuration,
			InterestTypesEntity interestTypes, Double maxInterestRate,
			Double minInterestRate, Double defInterestRate,
			boolean loanCounter, boolean intDedDisbursement,
			boolean prinDueLastInst, List<FundBO> funds, List<FeeBO> fees,
			MeetingBO meeting, GLCodeEntity principalGLcode,
			GLCodeEntity interestGLcode, LoanPrdActionForm loanPrdActionForm)
			throws ProductDefinitionException {

		super(userContext, prdOfferingName, prdOfferingShortName, prdCategory,
				prdApplicableMaster, startDate, endDate, description);
		prdLogger.debug("building Loan offering");
		validate(gracePeriodType, gracePeriodDuration, interestTypes,
				maxInterestRate, minInterestRate, defInterestRate, loanCounter,
				intDedDisbursement, prinDueLastInst, funds, fees, meeting,
				principalGLcode, interestGLcode);
		setCreateDetails();
		setLoanCounter(loanCounter);
		setIntDedDisbursement(intDedDisbursement);
		setPrinDueLastInst(prinDueLastInst);
		setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType,
				gracePeriodDuration);
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
		this.loanOfferingMeeting = new PrdOfferingMeetingEntity(meeting, this,
				MeetingType.LOAN_INSTALLMENT);
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
		this(null, null, null, null, null, null, new HashSet<LoanOfferingFundEntity>(), 
				new HashSet<LoanOfferingFeesEntity>(), new HashSet<LoanAmountFromLastLoanAmountBO>(), 
				new HashSet<LoanAmountFromLoanCycleBO>(), new HashSet<NoOfInstallFromLastLoanAmountBO>(), 
				new HashSet<NoOfInstallFromLoanCycleBO>(), new HashSet<LoanAmountSameForAllLoanBO>(), 
				new HashSet<NoOfInstallSameForAllLoanBO>());
	}

	private LoanOfferingBO(Short prdOfferingId, String globalPrdOfferingNum, 
			ProductTypeEntity prdType, OfficeBO office,
			GLCodeEntity principalGLcode, 
			GLCodeEntity interestGLcode, 
			Set<LoanOfferingFundEntity> loanOfferingFunds, 
			Set<LoanOfferingFeesEntity> loanOfferingFees, 
			Set<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoan, 
			Set<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycle, Set<NoOfInstallFromLastLoanAmountBO> noOfInstallFromLastLoan, Set<NoOfInstallFromLoanCycleBO> noOfInstallFromLoanCycle, Set<LoanAmountSameForAllLoanBO> loanAmountSameForAllLoan, Set<NoOfInstallSameForAllLoanBO> noOfInstallSameForAllLoan) {
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

	public static LoanOfferingBO createInstanceForTest(Short prdOfferingId) {
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

	void setGracePeriodType(GracePeriodTypeEntity gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	public void setInterestTypes(InterestTypesEntity interestTypes) {
		this.interestTypes = interestTypes;
	}

	void setGracePeriodDuration(Short gracePeriodDuration) {
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

	void setMaxInterestRate(Double maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}

	void setMinInterestRate(Double minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	void setDefInterestRate(Double defInterestRate) {
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

	void setIntDedDisbursement(boolean intDedDisbursementFlag) {
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

	public void addLoanAmountFromLastLoanAmount(
			LoanAmountFromLastLoanAmountBO loanAmountFromLastLoanAmountBO) {
		if (null != loanAmountFromLastLoanAmountBO)
			this.loanAmountFromLastLoan.add(loanAmountFromLastLoanAmountBO);
	}

	public void addNoOfInstallFromLastLoanAmount(
			NoOfInstallFromLastLoanAmountBO noOfInstallFromLastLoanAmountBO) {
		if (null != noOfInstallFromLastLoanAmountBO)
			this.noOfInstallFromLastLoan.add(noOfInstallFromLastLoanAmountBO);
	}

	public void addLoanAmountFromLoanCycle(
			LoanAmountFromLoanCycleBO loanAmountFromLoanCycleBO) {
		if (null != loanAmountFromLoanCycleBO)
			this.loanAmountFromLoanCycle.add(loanAmountFromLoanCycleBO);
	}

	public void addNoOfInstallFromLoanCycle(
			NoOfInstallFromLoanCycleBO noOfInstallFromLoanCycleBO) {
		if (null != noOfInstallFromLoanCycleBO)
			this.noOfInstallFromLoanCycle.add(noOfInstallFromLoanCycleBO);
	}

	public void addPrdOfferingFee(LoanOfferingFeesEntity prdOfferingFeesEntity) {
		if (null != prdOfferingFeesEntity)
			this.loanOfferingFees.add(prdOfferingFeesEntity);
	}

	void setLoanOfferingMeeting(PrdOfferingMeetingEntity prdOfferingMeeting) {
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
			new LoanPersistence().createOrUpdate(this);
		}
		catch (PersistenceException e) {
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
		}
		else {
			try {
				this.loanOfferingMeeting.setMeeting(new MeetingBO(
						recurrenceType, recurAfter, startDate,
						MeetingType.LOAN_INSTALLMENT));
			}
			catch (MeetingException e) {
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
		}
		catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
		prdLogger.debug("Loan Offering updated:" + prdOfferingName);
	}

	public void update(Short userId, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate, String description, PrdStatus status,
			GracePeriodTypeEntity gracePeriodType,
			InterestTypesEntity interestTypes, Short gracePeriodDuration,
			Double maxInterestRate, Double minInterestRate,
			Double defInterestRate, boolean loanCounter,
			boolean intDedDisbursement, boolean prinDueLastInst,
			List<FundBO> funds, List<FeeBO> fees, Short recurAfter,
			RecurrenceType recurrenceType, LoanPrdActionForm loanPrdActionForm)
			throws ProductDefinitionException {
		prdLogger.debug("Updating loan Offering :" + prdOfferingName);
		super.update(userId, prdOfferingName, prdOfferingShortName,
				prdCategory, prdApplicableMaster, startDate, endDate,
				description, status);
		validateForUpdate(gracePeriodType, gracePeriodDuration, interestTypes,
				maxInterestRate, minInterestRate, defInterestRate, loanCounter,
				intDedDisbursement, prinDueLastInst, funds, fees, recurAfter);
		setUpdateDetails(userId);
		setGracePeriodTypeAndDuration(intDedDisbursement, gracePeriodType,
				gracePeriodDuration);
		this.interestTypes = interestTypes;
		this.maxInterestRate = maxInterestRate;
		this.minInterestRate = minInterestRate;
		this.defInterestRate = defInterestRate;
		setLoanCounter(loanCounter);
		setIntDedDisbursement(intDedDisbursement);
		setPrinDueLastInst(prinDueLastInst);
		populateLoanAmountAndInstall(loanPrdActionForm);
		if (this.loanOfferingMeeting.getMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId().equals(
						recurrenceType.getValue())) {
			this.loanOfferingMeeting.getMeeting().getMeetingDetails()
					.setRecurAfter(recurAfter);
		}
		else {
			try {
				this.loanOfferingMeeting.setMeeting(new MeetingBO(
						recurrenceType, recurAfter, startDate,
						MeetingType.LOAN_INSTALLMENT));
			}
			catch (MeetingException e) {
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
		}
		catch (PersistenceException e) {
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
								GraceType.NONE.getValue()) && gracePeriodDuration == null)) {
			throw new ProductDefinitionException("errors.create");
		}

		if ((interestTypes.getId().equals(InterestType.DECLINING.getValue())
				||(interestTypes.getId().equals(InterestType.DECLINING_EPI.getValue())))
				&& intDedDisbursement) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION);
		}

	}

	private void validate(GracePeriodTypeEntity gracePeriodType,
			Short gracePeriodDuration, InterestTypesEntity interestTypes,
			Double maxInterestRate, Double minInterestRate,
			Double defInterestRate, boolean loanCounter,
			boolean intDedDisbursement, boolean prinDueLastInst,
			List<FundBO> funds, List<FeeBO> fees, MeetingBO meeting,
			GLCodeEntity principalGLcode, GLCodeEntity interestGLcode)
			throws ProductDefinitionException {
		prdLogger.debug("validating fields in Loan offering ");
		if (interestTypes == null
				|| maxInterestRate == null
				|| minInterestRate == null
				|| defInterestRate == null
				|| meeting == null
				|| principalGLcode == null
				|| interestGLcode == null
				|| (minInterestRate > maxInterestRate)
				|| (defInterestRate < minInterestRate || defInterestRate > maxInterestRate)
				|| (!intDedDisbursement
						&& gracePeriodType != null
						&& !gracePeriodType.getId().equals(
								GraceType.NONE.getValue()) && gracePeriodDuration == null)) {
			throw new ProductDefinitionException("errors.create");
		}

		if ((interestTypes.getId().equals(InterestType.DECLINING.getValue())
				||(interestTypes.getId().equals(InterestType.DECLINING_EPI.getValue())))
				&& intDedDisbursement) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION);
		}

	}

	private void setGracePeriodTypeAndDuration(boolean intDedDisbursement,
			GracePeriodTypeEntity gracePeriodType, Short gracePeriodDuration) {
		prdLogger
				.debug("Loan offering setGracePeriodTypeAndDuration called - intDedDisbursement:"
						+ intDedDisbursement);
		if (intDedDisbursement || gracePeriodType == null
				|| gracePeriodType.getId().equals(GraceType.NONE.getValue())) {
			this.gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
			this.gracePeriodDuration = (short) 0;
		}
		else {
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
		else throw new ProductDefinitionException(
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
								GraceType.NONE.getValue()) && gracePeriodDuration == null)) {
			throw new ProductDefinitionException("errors.update");
		}
	}

	private void validateForUpdate(GracePeriodTypeEntity gracePeriodType,
			Short gracePeriodDuration, InterestTypesEntity interestTypes,
			Double maxInterestRate, Double minInterestRate,
			Double defInterestRate, boolean loanCounter,
			boolean intDedDisbursement, boolean prinDueLastInst,
			List<FundBO> funds, List<FeeBO> fees, Short recurAfter)
			throws ProductDefinitionException {
		prdLogger.debug("validating fields in Loan offering for update");
		if (interestTypes == null
				|| maxInterestRate == null
				|| minInterestRate == null
				|| defInterestRate == null
				|| recurAfter == null
				|| (minInterestRate > maxInterestRate)
				|| (defInterestRate < minInterestRate || defInterestRate > maxInterestRate)
				|| (!intDedDisbursement
						&& gracePeriodType != null
						&& !gracePeriodType.getId().equals(
								GraceType.NONE.getValue()) && gracePeriodDuration == null)) {
			throw new ProductDefinitionException("errors.update");
		}
	}

	/**
	 * This method will return the max min def loan amount in array list for customer
	 * based on last loan amount 
	 */
	public ArrayList eligibleLoanAmount(String calaculateValue,
			LoanOfferingBO loanOfferingBO) {
		ArrayList list = new ArrayList();
		if ((checkLoanAmountType(loanOfferingBO) == ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN)
				&& calaculateValue == null) {
			Iterator<LoanAmountSameForAllLoanBO> loanAmountSameForAllLoanItr = loanOfferingBO
					.getLoanAmountSameForAllLoan().iterator();
			while (loanAmountSameForAllLoanItr.hasNext()) {
				LoanAmountSameForAllLoanBO loanAmountSameForAllLoanBO = loanAmountSameForAllLoanItr
						.next();
				list.add(loanAmountSameForAllLoanBO.getMaxLoanAmount());
				list.add(loanAmountSameForAllLoanBO.getMinLoanAmount());
				list.add(loanAmountSameForAllLoanBO.getDefaultLoanAmount());
			}
		}
		if (checkLoanAmountType(loanOfferingBO) == ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN) {
			Iterator<LoanAmountFromLastLoanAmountBO> loanAmountFromLastLoanAmountBOItr = loanOfferingBO
					.getLoanAmountFromLastLoan().iterator();
			while (loanAmountFromLastLoanAmountBOItr.hasNext()) {
				for (int i = 0; i < loanOfferingBO.getLoanAmountFromLastLoan()
						.size(); i++) {
					LoanAmountFromLastLoanAmountBO loanAmountFromLastLoanAmountBO = loanAmountFromLastLoanAmountBOItr
							.next();
					if (((loanAmountFromLastLoanAmountBO.getStartRange() <= Double
							.parseDouble(calaculateValue)) && (loanAmountFromLastLoanAmountBO
							.getEndRange() >= Double
							.parseDouble(calaculateValue)))
							|| ((i == loanOfferingBO.loanAmountFromLastLoan
									.size() - 1) && list.isEmpty())) {
						list.add(loanAmountFromLastLoanAmountBO
								.getMaxLoanAmount());
						list.add(loanAmountFromLastLoanAmountBO
								.getMinLoanAmount());
						list.add(loanAmountFromLastLoanAmountBO
								.getDefaultLoanAmount());
					}
				}
			}
		}
		if (checkLoanAmountType(loanOfferingBO) == ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE) {
			Iterator<LoanAmountFromLoanCycleBO> loanAmountFromLoanCycleBOItr = loanOfferingBO
					.getLoanAmountFromLoanCycle().iterator();
			while (loanAmountFromLoanCycleBOItr.hasNext()) {
				for (int i = 0; i < loanOfferingBO.loanAmountFromLoanCycle
						.size(); i++) {
					LoanAmountFromLoanCycleBO loanAmountFromLoanCycleBO = loanAmountFromLoanCycleBOItr
							.next();
					if ((loanAmountFromLoanCycleBO.getRangeIndex() == Short
							.parseShort(calaculateValue))
							|| ((i == loanOfferingBO.loanAmountFromLoanCycle
									.size() - 1) && list.isEmpty())) {
						list.add(loanAmountFromLoanCycleBO.getMaxLoanAmount());
						list.add(loanAmountFromLoanCycleBO.getMinLoanAmount());
						list.add(loanAmountFromLoanCycleBO
								.getDefaultLoanAmount());
					}
				}
			}
		}
		return list;
	}

	/**
	 * This method will return max min def installment in array list 
	 * based on customer last loan amount
	 */
	public ArrayList eligibleNoOfInstall(String calaculateValue,
			LoanOfferingBO loanOfferingBO) {
		ArrayList list = new ArrayList();
		if ((checkNoOfInstallType(loanOfferingBO) == ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN)
				&& calaculateValue == null) {
			Iterator<NoOfInstallSameForAllLoanBO> noOfInstallSameForAllLoanItr = loanOfferingBO
					.getNoOfInstallSameForAllLoan().iterator();
			while (noOfInstallSameForAllLoanItr.hasNext()) {
				NoOfInstallSameForAllLoanBO noOfInstallSameForAllLoanBO = noOfInstallSameForAllLoanItr
						.next();
				list.add(noOfInstallSameForAllLoanBO.getMaxNoOfInstall());
				list.add(noOfInstallSameForAllLoanBO.getMinNoOfInstall());
				list.add(noOfInstallSameForAllLoanBO.getDefaultNoOfInstall());
			}
		}
		if (checkNoOfInstallType(loanOfferingBO) == ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN) {
			Iterator<NoOfInstallFromLastLoanAmountBO> noOfInstallFromLastLoanAmountBOItr = loanOfferingBO
					.getNoOfInstallFromLastLoan().iterator();
			while (noOfInstallFromLastLoanAmountBOItr.hasNext()) {
				for (int i = 0; i < loanOfferingBO.noOfInstallFromLastLoan
						.size(); i++) {
					NoOfInstallFromLastLoanAmountBO noOfInstallFromLastLoanAmountBO = noOfInstallFromLastLoanAmountBOItr
							.next();
					if (((noOfInstallFromLastLoanAmountBO.getStartRange() <= Double
							.parseDouble(calaculateValue)) && (noOfInstallFromLastLoanAmountBO
							.getEndRange() >= Double
							.parseDouble(calaculateValue)))
							|| ((i == loanOfferingBO.noOfInstallFromLastLoan
									.size() - 1) && list.isEmpty())) {
						list.add(noOfInstallFromLastLoanAmountBO
								.getMaxNoOfInstall());
						list.add(noOfInstallFromLastLoanAmountBO
								.getMinNoOfInstall());
						list.add(noOfInstallFromLastLoanAmountBO
								.getDefaultNoOfInstall());
					}
				}
			}
		}
		if (checkNoOfInstallType(loanOfferingBO) == ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE) {
			Iterator<NoOfInstallFromLoanCycleBO> noOfInstallFromFromLoanCycleBOItr = loanOfferingBO
					.getNoOfInstallFromLoanCycle().iterator();
			while (noOfInstallFromFromLoanCycleBOItr.hasNext()) {
				for (int i = 0; i < loanOfferingBO.noOfInstallFromLoanCycle
						.size(); i++) {
					NoOfInstallFromLoanCycleBO noOfInstallFromLoanCycleBO = noOfInstallFromFromLoanCycleBOItr
							.next();
					if ((noOfInstallFromLoanCycleBO.getRangeIndex() == Short
							.parseShort(calaculateValue))
							|| ((i == loanOfferingBO.noOfInstallFromLoanCycle
									.size() - 1) && list.isEmpty())) {
						list
								.add(noOfInstallFromLoanCycleBO
										.getMaxNoOfInstall());
						list
								.add(noOfInstallFromLoanCycleBO
										.getMinNoOfInstall());
						list.add(noOfInstallFromLoanCycleBO
								.getDefaultNoOfInstall());
					}
				}
			}
		}
		return list;
	}

	/**
	 * this method will update the variable for save and update as input from loanprdactionform
	 * it will first clear all sets of amount/install type than update one of them according to input
	 * this will make them mutually exclusive as earlier data will be deleted using 
	 * the delete-orphan property of hibernate
	 */
	private void populateLoanAmountAndInstall(
			LoanPrdActionForm loanPrdActionForm) {
		if (Short.parseShort(loanPrdActionForm.getLoanAmtCalcType()) == (ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN)) {
			this.loanAmountFromLastLoan.clear();
			this.loanAmountFromLoanCycle.clear();
			this.loanAmountSameForAllLoan.clear();
			addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(
					loanPrdActionForm.getLastLoanMinLoanAmt1Value(),
					loanPrdActionForm.getLastLoanMaxLoanAmt1Value(),
					loanPrdActionForm.getLastLoanDefaultLoanAmt1Value(),
					loanPrdActionForm.getStartRangeLoanAmt1().doubleValue(),
					loanPrdActionForm.getEndRangeLoanAmt1().doubleValue(), this));
			addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(
					loanPrdActionForm.getLastLoanMinLoanAmt2Value(),
					loanPrdActionForm.getLastLoanMaxLoanAmt2Value(),
					loanPrdActionForm.getLastLoanDefaultLoanAmt2Value(),
					loanPrdActionForm.getStartRangeLoanAmt2().doubleValue(),
					loanPrdActionForm.getEndRangeLoanAmt2().doubleValue(), this));
			addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(
					loanPrdActionForm.getLastLoanMinLoanAmt3Value(),
					loanPrdActionForm.getLastLoanMaxLoanAmt3Value(),
					loanPrdActionForm.getLastLoanDefaultLoanAmt3Value(),
					loanPrdActionForm.getStartRangeLoanAmt3().doubleValue(),
					loanPrdActionForm.getEndRangeLoanAmt3().doubleValue(), this));
			addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(
					loanPrdActionForm.getLastLoanMinLoanAmt4Value(),
					loanPrdActionForm.getLastLoanMaxLoanAmt4Value(),
					loanPrdActionForm.getLastLoanDefaultLoanAmt4Value(),
					loanPrdActionForm.getStartRangeLoanAmt4().doubleValue(),
					loanPrdActionForm.getEndRangeLoanAmt4().doubleValue(), this));
			addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(
					loanPrdActionForm.getLastLoanMinLoanAmt5Value(),
					loanPrdActionForm.getLastLoanMaxLoanAmt5Value(),
					loanPrdActionForm.getLastLoanDefaultLoanAmt5Value(),
					loanPrdActionForm.getStartRangeLoanAmt5().doubleValue(),
					loanPrdActionForm.getEndRangeLoanAmt5().doubleValue(), this));
			addLoanAmountFromLastLoanAmount(new LoanAmountFromLastLoanAmountBO(
					loanPrdActionForm.getLastLoanMinLoanAmt6Value(),
					loanPrdActionForm.getLastLoanMaxLoanAmt6Value(),
					loanPrdActionForm.getLastLoanDefaultLoanAmt6Value(),
					loanPrdActionForm.getStartRangeLoanAmt6().doubleValue(),
					loanPrdActionForm.getEndRangeLoanAmt6().doubleValue(), this));
		}
		if (Short.parseShort(loanPrdActionForm.getCalcInstallmentType()) == (ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN)) {
			this.noOfInstallFromLastLoan.clear();
			this.noOfInstallFromLoanCycle.clear();
			this.noOfInstallSameForAllLoan.clear();
			addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(
					Short.valueOf(loanPrdActionForm.getMinLoanInstallment1()),
					Short.valueOf(loanPrdActionForm.getMaxLoanInstallment1()),
					Short.valueOf(loanPrdActionForm.getDefLoanInstallment1()),
					loanPrdActionForm.getStartInstallmentRange1().doubleValue(),
					loanPrdActionForm.getEndInstallmentRange1().doubleValue(), this));
			addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(
					Short.valueOf(loanPrdActionForm.getMinLoanInstallment2()),
					Short.valueOf(loanPrdActionForm.getMaxLoanInstallment2()),
					Short.valueOf(loanPrdActionForm.getDefLoanInstallment2()),
					loanPrdActionForm.getStartInstallmentRange2().doubleValue(),
					loanPrdActionForm.getEndInstallmentRange2().doubleValue(), this));
			addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(
					Short.valueOf(loanPrdActionForm.getMinLoanInstallment3()),
					Short.valueOf(loanPrdActionForm.getMaxLoanInstallment3()),
					Short.valueOf(loanPrdActionForm.getDefLoanInstallment3()),
					loanPrdActionForm.getStartInstallmentRange3().doubleValue(),
					loanPrdActionForm.getEndInstallmentRange3().doubleValue(), this));
			addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(
					Short.valueOf(loanPrdActionForm.getMinLoanInstallment4()),
					Short.valueOf(loanPrdActionForm.getMaxLoanInstallment4()),
					Short.valueOf(loanPrdActionForm.getDefLoanInstallment4()),
					loanPrdActionForm.getStartInstallmentRange4().doubleValue(),
					loanPrdActionForm.getEndInstallmentRange4().doubleValue(), this));
			addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(
					Short.valueOf(loanPrdActionForm.getMinLoanInstallment5()),
					Short.valueOf(loanPrdActionForm.getMaxLoanInstallment5()),
					Short.valueOf(loanPrdActionForm.getDefLoanInstallment5()),
					loanPrdActionForm.getStartInstallmentRange5().doubleValue(),
					loanPrdActionForm.getEndInstallmentRange5().doubleValue(), this));
			addNoOfInstallFromLastLoanAmount(new NoOfInstallFromLastLoanAmountBO(
					Short.valueOf(loanPrdActionForm.getMinLoanInstallment6()),
					Short.valueOf(loanPrdActionForm.getMaxLoanInstallment6()),
					Short.valueOf(loanPrdActionForm.getDefLoanInstallment6()),
					loanPrdActionForm.getStartInstallmentRange6().doubleValue(),
					loanPrdActionForm.getEndInstallmentRange6().doubleValue(), this));

		}
		if (Short.parseShort(loanPrdActionForm.getLoanAmtCalcType()) == (ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE)) {
			this.loanAmountFromLoanCycle.clear();
			this.loanAmountFromLastLoan.clear();
			this.loanAmountSameForAllLoan.clear();
			addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(
					loanPrdActionForm.getCycleLoanMinLoanAmt1Value(),
					loanPrdActionForm.getCycleLoanMaxLoanAmt1Value(),
					loanPrdActionForm.getCycleLoanDefaultLoanAmt1Value(), new Short(
							"0"), this));
			addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(
					loanPrdActionForm.getCycleLoanMinLoanAmt2Value(),
					loanPrdActionForm.getCycleLoanMaxLoanAmt2Value(),
					loanPrdActionForm.getCycleLoanDefaultLoanAmt2Value(), new Short(
							"1"), this));
			addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(
					loanPrdActionForm.getCycleLoanMinLoanAmt3Value(),
					loanPrdActionForm.getCycleLoanMaxLoanAmt3Value(),
					loanPrdActionForm.getCycleLoanDefaultLoanAmt3Value(), new Short(
							"2"), this));
			addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(
					loanPrdActionForm.getCycleLoanMinLoanAmt4Value(),
					loanPrdActionForm.getCycleLoanMaxLoanAmt4Value(),
					loanPrdActionForm.getCycleLoanDefaultLoanAmt4Value(), new Short(
							"3"), this));
			addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(
					loanPrdActionForm.getCycleLoanMinLoanAmt5Value(),
					loanPrdActionForm.getCycleLoanMaxLoanAmt5Value(),
					loanPrdActionForm.getCycleLoanDefaultLoanAmt5Value(), new Short(
							"4"), this));
			addLoanAmountFromLoanCycle(new LoanAmountFromLoanCycleBO(
					loanPrdActionForm.getCycleLoanMinLoanAmt6Value(),
					loanPrdActionForm.getCycleLoanMaxLoanAmt6Value(),
					loanPrdActionForm.getCycleLoanDefaultLoanAmt6Value(), new Short(
							"5"), this));

		}
		if (Short.parseShort(loanPrdActionForm.getCalcInstallmentType()) == (ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE)) {
			this.noOfInstallFromLoanCycle.clear();
			this.noOfInstallFromLastLoan.clear();
			this.noOfInstallSameForAllLoan.clear();
			addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short
					.valueOf(loanPrdActionForm.getMinCycleInstallment1()),
					Short.valueOf(loanPrdActionForm.getMaxCycleInstallment1()),
					Short.valueOf(loanPrdActionForm.getDefCycleInstallment1()),
					new Short("0"), this));
			addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short
					.valueOf(loanPrdActionForm.getMinCycleInstallment2()),
					Short.valueOf(loanPrdActionForm.getMaxCycleInstallment2()),
					Short.valueOf(loanPrdActionForm.getDefCycleInstallment2()),
					new Short("1"), this));
			addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short
					.valueOf(loanPrdActionForm.getMinCycleInstallment3()),
					Short.valueOf(loanPrdActionForm.getMaxCycleInstallment3()),
					Short.valueOf(loanPrdActionForm.getDefCycleInstallment3()),
					new Short("2"), this));
			addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short
					.valueOf(loanPrdActionForm.getMinCycleInstallment4()),
					Short.valueOf(loanPrdActionForm.getMaxCycleInstallment4()),
					Short.valueOf(loanPrdActionForm.getDefCycleInstallment4()),
					new Short("3"), this));
			addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short
					.valueOf(loanPrdActionForm.getMinCycleInstallment5()),
					Short.valueOf(loanPrdActionForm.getMaxCycleInstallment5()),
					Short.valueOf(loanPrdActionForm.getDefCycleInstallment5()),
					new Short("4"), this));
			addNoOfInstallFromLoanCycle(new NoOfInstallFromLoanCycleBO(Short
					.valueOf(loanPrdActionForm.getMinCycleInstallment6()),
					Short.valueOf(loanPrdActionForm.getMaxCycleInstallment6()),
					Short.valueOf(loanPrdActionForm.getDefCycleInstallment6()),
					new Short("5"), this));
		}
		if (Short.parseShort(loanPrdActionForm.getLoanAmtCalcType()) == (ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN)) {
			this.loanAmountSameForAllLoan.clear();
			this.loanAmountFromLastLoan.clear();
			this.loanAmountFromLoanCycle.clear();
			loanAmountSameForAllLoan.add(new LoanAmountSameForAllLoanBO(
					loanPrdActionForm.getMinLoanAmountValue(), loanPrdActionForm
							.getMaxLoanAmountValue(), loanPrdActionForm
							.getDefaultLoanAmountValue(), this));
		}
		if (Short.parseShort(loanPrdActionForm.getCalcInstallmentType()) == (ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN)) {
			this.noOfInstallSameForAllLoan.clear();
			this.noOfInstallFromLastLoan.clear();
			this.noOfInstallFromLoanCycle.clear();
			noOfInstallSameForAllLoan
					.add(new NoOfInstallSameForAllLoanBO((Short
							.parseShort(loanPrdActionForm
									.getMinNoInstallments())), Short
							.parseShort(loanPrdActionForm
									.getMaxNoInstallments()), Short
							.parseShort(loanPrdActionForm
									.getDefNoInstallments()), this));
		}
	}

	/**
	 * it will check the type of product
	 */
	public int checkLoanAmountType(LoanOfferingBO loanOffering) {
		if (!(loanOffering.getLoanAmountSameForAllLoan().isEmpty())) {
			return ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN;
		}
		else if (!loanOffering.getLoanAmountFromLastLoan().isEmpty()) {
			return ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN;
		}
		else if (!(loanOffering.getLoanAmountFromLoanCycle().isEmpty())) {
			return ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE;
		}
		return 0;
	}

	/**
	 * it will check the type of installment 
	 */
	public int checkNoOfInstallType(LoanOfferingBO loanOffering) {
		if (!(loanOffering.getNoOfInstallSameForAllLoan().isEmpty())) {
			return ProductDefinitionConstants.NOOFINSTALLSAMEFORALLLOAN;
		}
		else if (!(loanOffering.getNoOfInstallFromLastLoan().isEmpty())) {
			return ProductDefinitionConstants.NOOFINSTALLFROMLASTLOAN;
		}
		else if (!(loanOffering.getNoOfInstallFromLoanCycle().isEmpty())) {
			return ProductDefinitionConstants.NOOFINSTALLFROMLOANCYCLLE;
		}
		return 0;
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

	/**
	 * this method is used in test cases
	 */
	public void updateLoanOfferingSameForAllLoan(LoanOfferingBO loanOffering) {
		Iterator<LoanAmountSameForAllLoanBO> itr = loanOffering
				.getLoanAmountSameForAllLoan().iterator();
		if (itr.hasNext()) {
			LoanAmountSameForAllLoanBO loanAmountSameForAllLoanBO = itr.next();
			Iterator<NoOfInstallSameForAllLoanBO> itrNoOfInstallSameForAllLoan = loanOffering
					.getNoOfInstallSameForAllLoan().iterator();
			NoOfInstallSameForAllLoanBO noOfInstallSameForAllLoanBO = itrNoOfInstallSameForAllLoan
					.next();
			loanOffering.setMaxLoanAmount(new Money(loanAmountSameForAllLoanBO
					.getMaxLoanAmount().toString()));
			loanOffering.setMinLoanAmount(new Money(loanAmountSameForAllLoanBO
					.getMinLoanAmount().toString()));
			loanOffering.setDefaultLoanAmount(new Money(loanAmountSameForAllLoanBO
					.getDefaultLoanAmount().toString()));
			loanOffering.setMinNoInstallments(noOfInstallSameForAllLoanBO
					.getMinNoOfInstall());
			loanOffering.setMaxNoInstallments(noOfInstallSameForAllLoanBO
					.getMaxNoOfInstall());
			loanOffering.setDefNoInstallments(noOfInstallSameForAllLoanBO
					.getDefaultNoOfInstall());
		}
	}
}
