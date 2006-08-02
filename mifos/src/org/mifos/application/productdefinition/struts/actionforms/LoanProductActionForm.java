/**

 * LoanProductActionForm.java    version: 1.0

 

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

package org.mifos.application.productdefinition.struts.actionforms;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.util.valueobjects.InterestCalcRule;
import org.mifos.application.master.util.valueobjects.InterestTypes;
import org.mifos.application.master.util.valueobjects.PrdApplicableMaster;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.penalty.util.valueobjects.Penalty;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.GracePeriodType;
import org.mifos.application.productdefinition.util.valueobjects.LoanOfferingFund;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingFees;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingMeeting;
import org.mifos.application.productdefinition.util.valueobjects.PrdStatus;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is the Action Form for the Loan Product Offering.
 * 
 * @author ashishsm
 * 
 */
public class LoanProductActionForm extends MifosSearchActionForm {

	/**
	 * Default Constructor
	 */
	public LoanProductActionForm() {
		super();
		this.prdCategory = new ProductCategory();
		this.gracePeriodType = new GracePeriodType();
		this.interestTypes = new InterestTypes();
		this.interestCalcRule = new InterestCalcRule();
		this.prdApplicableMaster = new PrdApplicableMaster();
		this.penalty = new Penalty();
		this.prdStatus = new PrdStatus();
		this.prdOfferingMeeting = new PrdOfferingMeeting();
		this.principalGLCode = new GLCodeEntity();
		this.interestGLCode = new GLCodeEntity();
		this.penaltyGLCode = new GLCodeEntity();
	}

	/**
	 * serial version uid for serialization
	 */
	private static final long serialVersionUID = 18787632344650988L;

	private MifosLogger prdDefLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	/**
	 * The id of the product offering
	 */
	private String prdOfferingId;

	/**
	 * The name of the product offering
	 */
	private String prdOfferingName;

	/**
	 * The short name of the product offering
	 */
	private String prdOfferingShortName;

	/**
	 * The description of the product offering
	 */
	private String description;

	/**
	 * The prdCategory of the product offering
	 */
	private ProductCategory prdCategory;

	/**
	 * The startDate of the product offering
	 */
	private String startDate;

	/**
	 * The endDate of the product offering
	 */
	private String endDate;

	/**
	 * The minLoanAmount of the product offering
	 */
	private Money minLoanAmount;

	/**
	 * The maxLoanAmount of the product offering
	 */
	private Money maxLoanAmount;

	/**
	 * The defaultLoanAmount of the product offering
	 */
	private Money defaultLoanAmount;

	/**
	 * The maxNoInstallments of the product offering
	 */
	private String maxNoInstallments;

	/**
	 * The minNoInstallments of the product offering
	 */
	private String minNoInstallments;

	/**
	 * The defNoInstallments of the product offering
	 */
	private String defNoInstallments;

	/**
	 * The gracePeriodType of the product offering
	 */
	private GracePeriodType gracePeriodType;

	/**
	 * The gracePeriodDuration of the product offering
	 */
	private String gracePeriodDuration;

	/**
	 * The maxInterestRate of the product offering
	 */
	private String maxInterestRate;

	/**
	 * The minInterestRate of the product offering
	 */
	private String minInterestRate;

	/**
	 * The defInterestRate of the product offering
	 */
	private String defInterestRate;

	/**
	 * The interestTypes of the product offering
	 */
	private InterestTypes interestTypes;

	/**
	 * The interestCalcRule of the product offering
	 */
	private InterestCalcRule interestCalcRule;

	/**
	 * The prdApplicableMaster of the product offering
	 */
	private PrdApplicableMaster prdApplicableMaster;

	/**
	 * The loanCounterFlag of the product offering
	 */
	private String loanCounterFlag;

	/**
	 * The intDedDisbursementFlag of the product offering
	 */
	private String intDedDisbursementFlag;

	/**
	 * The prinDueLastInstFlag of the product offering
	 */
	private String prinDueLastInstFlag;

	/**
	 * The penaltyGrace of the product offering
	 */
	private String penaltyGrace;

	/**
	 * The penaltyRate of the product offering
	 */
	private String penaltyRate;

	/**
	 * The penalty of the product offering
	 */
	private Penalty penalty;

	/**
	 * The prdStatus of the product offering
	 */
	private PrdStatus prdStatus;

	/**
	 * The loanOfferingFunds of the product offering
	 */
	private String[] loanOfferingFunds;

	/**
	 * The loanOffeingFundSet of the product offering
	 */
	private Set<LoanOfferingFund> loanOfferFunds;

	/**
	 * The prdOfferinFees of the product offering
	 */
	private String[] prdOfferinFees;

	/**
	 * The prdOfferingFeesSet of the product offering
	 */
	private Set<PrdOfferingFees> prdOfferingFees;

	/**
	 * The freqOfInstallments of the product offering
	 */
	private String freqOfInstallments;

	/**
	 * The recurWeekDay of the product offering
	 */
	private String recurWeekDay;

	/**
	 * The recurMonthDay of the product offering
	 */
	private String recurMonthDay;

	/**
	 * The prdOfferingMeeting of the product offering
	 */
	private PrdOfferingMeeting prdOfferingMeeting;

	private GLCodeEntity principalGLCode;

	private GLCodeEntity interestGLCode;

	private GLCodeEntity penaltyGLCode;

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

	/**
	 * @return Returns the prdOfferingMeeting.
	 */
	public PrdOfferingMeeting getPrdOfferingMeeting() {
		Meeting meeting = null;
		if (null != freqOfInstallments && !"".equals(freqOfInstallments.trim())
				&& Short.valueOf(freqOfInstallments) == MeetingConstants.MONTH) {
			meeting = MeetingHelper.geMeeting(freqOfInstallments,
					recurMonthDay, ProductDefinitionConstants.LOANFRQINSTID);
		} else {
			meeting = MeetingHelper.geMeeting(freqOfInstallments, recurWeekDay,
					ProductDefinitionConstants.LOANFRQINSTID);
		}
		this.prdOfferingMeeting.setMeeting(meeting);
		this.prdOfferingMeeting
				.setMeetingType(ProductDefinitionConstants.LOANFRQINSTID);
		return this.prdOfferingMeeting;
	}

	/**
	 * @param prdOfferingMeeting
	 *            The prdOfferingMeeting to set.
	 */
	public void setPrdOfferingMeeting(PrdOfferingMeeting prdOfferingMeeting) {
		this.prdOfferingMeeting = prdOfferingMeeting;
	}

	/**
	 * @return Returns the prdOfferinFees.
	 */
	public String[] getPrdOfferinFees() {
		return prdOfferinFees;
	}

	/**
	 * @param prdOfferinFees
	 *            The prdOfferinFees to set.
	 */
	public void setPrdOfferinFees(String[] prdOfferinFees) {
		this.prdOfferinFees = prdOfferinFees;
		this.prdOfferingFees = new HashSet<PrdOfferingFees>();
		if (this.prdOfferinFees != null) {
			for (int i = 0; i < prdOfferinFees.length; i++) {
				Fees fees = new Fees();
				PrdOfferingFees prdOfferingFee = new PrdOfferingFees();
				fees.setFeeId(Short.valueOf(prdOfferinFees[i]));
				prdOfferingFee.setFees(fees);
				prdOfferingFees.add(prdOfferingFee);
			}
		}
	}

	/**
	 * @return Returns the prdOfferingFeesSet.
	 */
	public Set<PrdOfferingFees> getPrdOfferingFees() {
		this.prdOfferingFees = new HashSet<PrdOfferingFees>();
		if (this.prdOfferinFees != null) {
			for (int i = 0; i < prdOfferinFees.length; i++) {
				Fees fees = new Fees();
				PrdOfferingFees prdOfferingFee = new PrdOfferingFees();
				fees.setFeeId(Short.valueOf(prdOfferinFees[i]));
				prdOfferingFee.setFees(fees);
				prdOfferingFees.add(prdOfferingFee);
			}
		}
		return this.prdOfferingFees;
	}

	/**
	 * @param prdOfferingFeesSet
	 *            The prdOfferingFeesSet to set.
	 */
	public void setPrdOfferingFees(Set<PrdOfferingFees> prdOfferingFees) {
		this.prdOfferingFees = prdOfferingFees;
	}

	/**
	 * @return Returns the loanOfferingFunds.
	 */
	public String[] getLoanOfferingFunds() {
		return loanOfferingFunds;
	}

	/**
	 * @param loanOfferingFunds
	 *            The loanOfferingFunds to set.
	 */
	public void setLoanOfferingFunds(String[] loanOfferingFunds) {
		this.loanOfferingFunds = loanOfferingFunds;
		this.loanOfferFunds = new HashSet<LoanOfferingFund>();
		if (this.loanOfferingFunds != null) {
			for (int i = 0; i < loanOfferingFunds.length; i++) {
				Fund fund = new Fund();
				LoanOfferingFund loanOfferingFund = new LoanOfferingFund();
				fund.setFundId(Short.valueOf(loanOfferingFunds[i]));
				loanOfferingFund.setFund(fund);
				loanOfferFunds.add(loanOfferingFund);
			}
		}
	}

	/**
	 * @return Returns the loanOffeingFundSet.
	 */
	public Set<LoanOfferingFund> getLoanOfferFunds() {
		this.loanOfferFunds = new HashSet<LoanOfferingFund>();
		if (this.loanOfferingFunds != null) {
			for (int i = 0; i < loanOfferingFunds.length; i++) {
				Fund fund = new Fund();
				LoanOfferingFund loanOfferingFund = new LoanOfferingFund();
				fund.setFundId(Short.valueOf(loanOfferingFunds[i]));
				loanOfferingFund.setFund(fund);
				loanOfferFunds.add(loanOfferingFund);
			}
		}
		return loanOfferFunds;
	}

	/**
	 * @param loanOffeingFundSet
	 *            The loanOffeingFundSet to set.
	 */
	public void setLoanOfferFunds(Set<LoanOfferingFund> loanOfferFunds) {
		this.loanOfferFunds = loanOfferFunds;
	}

	public String getRecurMonthDay() {
		return recurMonthDay;
	}

	public void setRecurMonthDay(String recurMonthDay) {
		this.recurMonthDay = recurMonthDay;
	}

	public String getRecurWeekDay() {
		return recurWeekDay;
	}

	public void setRecurWeekDay(String recurWeekDay) {
		this.recurWeekDay = recurWeekDay;
	}

	/**
	 * @return Returns the freqOfInstallments.
	 */
	public String getFreqOfInstallments() {
		return freqOfInstallments;
	}

	/**
	 * @param freqOfInstallments
	 *            The freqOfInstallments to set.
	 */
	public void setFreqOfInstallments(String freqOfInstallments) {
		this.freqOfInstallments = freqOfInstallments;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the endDate.
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            The endDate to set.
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return Returns the startDate.
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            The startDate to set.
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Returns the prdCategory.
	 */
	public ProductCategory getPrdCategory() {
		return prdCategory;
	}

	/**
	 * @param prdCategory
	 *            The prdCategory to set.
	 */
	public void setPrdCategory(ProductCategory prdCategory) {
		this.prdCategory = prdCategory;
	}

	/**
	 * @return Returns the prdOfferingName.
	 */
	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	/**
	 * @param prdOfferingName
	 *            The prdOfferingName to set.
	 */
	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	/**
	 * @return Returns the prdOfferingShortName.
	 */
	public String getPrdOfferingShortName() {
		return prdOfferingShortName;
	}

	/**
	 * @param prdOfferingShortName
	 *            The prdOfferingShortName to set.
	 */
	public void setPrdOfferingShortName(String prdOfferingShortName) {
		this.prdOfferingShortName = prdOfferingShortName;
	}

	/**
	 * @return Returns the defaultLoanAmount.
	 */
	public Money getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	public double getDefaultLoanAmountDoubleValue() {
		return defaultLoanAmount.getAmountDoubleValue();
	}

	/**
	 * @param defaultLoanAmount
	 *            The defaultLoanAmount to set.
	 */
	public void setDefaultLoanAmount(Money defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	/**
	 * @return Returns the maxLoanAmount.
	 */
	public Money getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public double getMaxLoanAmountDoubleValue() {
		return maxLoanAmount.getAmountDoubleValue();
	}

	/**
	 * @param maxLoanAmount
	 *            The maxLoanAmount to set.
	 */
	public void setMaxLoanAmount(Money maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	/**
	 * @return Returns the minLoanAmount.
	 */
	public Money getMinLoanAmount() {
		return minLoanAmount;
	}

	public double getMinLoanAmountDoubleValue() {
		return minLoanAmount.getAmountDoubleValue();
	}

	/**
	 * @param minLoanAmount
	 *            The minLoanAmount to set.
	 */
	public void setMinLoanAmount(Money minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	/**
	 * @return Returns the defNoInstallments.
	 */
	public String getDefNoInstallments() {
		return defNoInstallments;
	}

	/**
	 * @param defNoInstallments
	 *            The defNoInstallments to set.
	 */
	public void setDefNoInstallments(String defNoInstallments) {
		this.defNoInstallments = defNoInstallments;
	}

	/**
	 * @return Returns the maxNoInstallments.
	 */
	public String getMaxNoInstallments() {
		return maxNoInstallments;
	}

	/**
	 * @param maxNoInstallments
	 *            The maxNoInstallments to set.
	 */
	public void setMaxNoInstallments(String maxNoInstallments) {
		this.maxNoInstallments = maxNoInstallments;
	}

	/**
	 * @return Returns the minNoInstallments.
	 */
	public String getMinNoInstallments() {
		return minNoInstallments;
	}

	/**
	 * @param minNoInstallments
	 *            The minNoInstallments to set.
	 */
	public void setMinNoInstallments(String minNoInstallments) {
		this.minNoInstallments = minNoInstallments;
	}

	/**
	 * @return Returns the gracePeriodDuration.
	 */
	public String getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	/**
	 * @param gracePeriodDuration
	 *            The gracePeriodDuration to set.
	 */
	public void setGracePeriodDuration(String gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	/**
	 * @return Returns the gracePeriodType.
	 */
	public GracePeriodType getGracePeriodType() {
		return gracePeriodType;
	}

	/**
	 * @param gracePeriodType
	 *            The gracePeriodType to set.
	 */
	public void setGracePeriodType(GracePeriodType gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	/**
	 * @return Returns the interestCalcRule.
	 */
	public InterestCalcRule getInterestCalcRule() {
		return interestCalcRule;
	}

	/**
	 * @param interestCalcRule
	 *            The interestCalcRule to set.
	 */
	public void setInterestCalcRule(InterestCalcRule interestCalcRule) {
		this.interestCalcRule = interestCalcRule;
	}

	/**
	 * @return Returns the interestTypes.
	 */
	public InterestTypes getInterestTypes() {
		return interestTypes;
	}

	/**
	 * @param interestTypes
	 *            The interestTypes to set.
	 */
	public void setInterestTypes(InterestTypes interestTypes) {
		this.interestTypes = interestTypes;
	}

	/**
	 * @return Returns the defInterestRate.
	 */
	public String getDefInterestRate() {
		return defInterestRate;
	}

	/**
	 * @param defInterestRate
	 *            The defInterestRate to set.
	 */
	public void setDefInterestRate(String defInterestRate) {
		this.defInterestRate = defInterestRate;
	}

	/**
	 * @return Returns the intDedDisbursementFlag.
	 */
	public String getIntDedDisbursementFlag() {
		return intDedDisbursementFlag;
	}

	/**
	 * @param intDedDisbursementFlag
	 *            The intDedDisbursementFlag to set.
	 */
	public void setIntDedDisbursementFlag(String intDedDisbursementFlag) {
		this.intDedDisbursementFlag = intDedDisbursementFlag;
	}

	/**
	 * @return Returns the loanCounterFlag.
	 */
	public String getLoanCounterFlag() {
		return loanCounterFlag;
	}

	/**
	 * @param loanCounterFlag
	 *            The loanCounterFlag to set.
	 */
	public void setLoanCounterFlag(String loanCounterFlag) {
		this.loanCounterFlag = loanCounterFlag;
	}

	/**
	 * @return Returns the prinDueLastInstFlag.
	 */
	public String getPrinDueLastInstFlag() {
		return prinDueLastInstFlag;
	}

	/**
	 * @param prinDueLastInstFlag
	 *            The prinDueLastInstFlag to set.
	 */
	public void setPrinDueLastInstFlag(String prinDueLastInstFlag) {
		this.prinDueLastInstFlag = prinDueLastInstFlag;
	}

	/**
	 * @return Returns the maxInterestRate.
	 */
	public String getMaxInterestRate() {
		return maxInterestRate;
	}

	/**
	 * @param maxInterestRate
	 *            The maxInterestRate to set.
	 */
	public void setMaxInterestRate(String maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}

	/**
	 * @return Returns the minInterestRate.
	 */
	public String getMinInterestRate() {
		return minInterestRate;
	}

	/**
	 * @param minInterestRate
	 *            The minInterestRate to set.
	 */
	public void setMinInterestRate(String minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	/**
	 * @return Returns the prdApplicableMaster.
	 */
	public PrdApplicableMaster getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	/**
	 * @param prdApplicableMaster
	 *            The prdApplicableMaster to set.
	 */
	public void setPrdApplicableMaster(PrdApplicableMaster prdApplicableMaster) {
		this.prdApplicableMaster = prdApplicableMaster;
	}

	/**
	 * @return Returns the penalty.
	 */
	public Penalty getPenalty() {
		return penalty;
	}

	/**
	 * @param penalty
	 *            The penalty to set.
	 */
	public void setPenalty(Penalty penalty) {
		this.penalty = penalty;
	}

	/**
	 * @return Returns the penaltyGrace.
	 */
	public String getPenaltyGrace() {
		return penaltyGrace;
	}

	/**
	 * @param penaltyGrace
	 *            The penaltyGrace to set.
	 */
	public void setPenaltyGrace(String penaltyGrace) {
		this.penaltyGrace = penaltyGrace;
	}

	/**
	 * @return Returns the penaltyRate.
	 */
	public String getPenaltyRate() {
		return penaltyRate;
	}

	/**
	 * @param penaltyRate
	 *            The penaltyRate to set.
	 */
	public void setPenaltyRate(String penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	/**
	 * @return Returns the prdStatus.
	 */
	public PrdStatus getPrdStatus() {
		return prdStatus;
	}

	/**
	 * @param prdStatus
	 *            The prdStatus to set.
	 */
	public void setPrdStatus(PrdStatus prdStatus) {
		this.prdStatus = prdStatus;
	}

	/**
	 * @return Returns the prdOfferingId.
	 */
	public String getPrdOfferingId() {
		return prdOfferingId;
	}

	/**
	 * @param prdOfferingId
	 *            The prdOfferingId to set.
	 */
	public void setPrdOfferingId(String prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}

	/**
	 * This is the custom method to do any custom validations. This method is
	 * also used to specify the methods where validation from the validation.xml
	 * is to be skipped.
	 * 
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request
				.getParameter(ProductDefinitionConstants.METHOD);
		if (null != methodCalled) {
			if (ProductDefinitionConstants.CANCELMETHOD.equals(methodCalled)
					|| ProductDefinitionConstants.GETMETHOD
							.equals(methodCalled)
					|| ProductDefinitionConstants.LOADMETHOD
							.equals(methodCalled)
					|| ProductDefinitionConstants.SEARCHMETHOD
							.equals(methodCalled)
					|| ProductDefinitionConstants.MANAGEMETHOD
							.equals(methodCalled)) {
				prdDefLogger.info("Skipping validation for " + methodCalled
						+ "method");
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			}
			// Bug Id 25589. Added the logic for comparision of min, max and
			// default loan amount and interest rate
			if (ProductDefinitionConstants.PREVIEWMETHOD.equals(methodCalled)) {
				try {
					if (null != maxLoanAmount && null != minLoanAmount) {
						double maxLoanAmnt = maxLoanAmount
								.getAmountDoubleValue();
						double minLoanAmnt = minLoanAmount
								.getAmountDoubleValue();
						String defLoan = request
								.getParameter("defaultLoanAmount");
						if (maxLoanAmnt <= 0)
							errors
									.add(
											ProductDefinitionConstants.INVALIDFIELD,
											new ActionMessage(
													ProductDefinitionConstants.INVALIDFIELD,
													ProductDefinitionConstants.LOANPRDACTIONMAXAMOUNT));
						else {
							if (minLoanAmnt > maxLoanAmnt) {
								errors
										.add(
												ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT,
												new ActionMessage(
														ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT));
							}
							if (maxLoanAmount.equals(minLoanAmount)
									&& defLoan != null
									&& defLoan.trim().equals("")) {
								defaultLoanAmount = maxLoanAmount;
							}
							if (null != defLoan) {
								double defLoanAmnt = Double
										.parseDouble(defLoan);
								if (defLoanAmnt < minLoanAmnt
										|| defLoanAmnt > maxLoanAmnt) {
									errors
											.add(
													ProductDefinitionConstants.ERRORDEFLOANAMOUNT,
													new ActionMessage(
															ProductDefinitionConstants.ERRORDEFLOANAMOUNT));
								}
							}
						}
					}
				} catch (Exception ne) {
				}
				try {
					if (null != maxInterestRate && null != minInterestRate
							&& null != defInterestRate) {
						double maxIntRate = Double.parseDouble(maxInterestRate);
						double minIntRate = Double.parseDouble(minInterestRate);
						double defIntRate = Double.parseDouble(defInterestRate);
						if (maxIntRate <= 0)
							errors
									.add(
											ProductDefinitionConstants.INVALIDFIELD,
											new ActionMessage(
													ProductDefinitionConstants.INVALIDFIELD,
													ProductDefinitionConstants.LOANPRDACTIONMAXINTERESTRATE));
						else {
							if (minIntRate > maxIntRate) {
								errors
										.add(
												ProductDefinitionConstants.ERRORMAXMININTRATE,
												new ActionMessage(
														ProductDefinitionConstants.ERRORMAXMININTRATE));
							}
							if (defIntRate < minIntRate
									|| defIntRate > maxIntRate) {
								errors
										.add(
												ProductDefinitionConstants.ERRORDEFINTRATE,
												new ActionMessage(
														ProductDefinitionConstants.ERRORDEFINTRATE));
							}
						}
					}
				} catch (Exception ne) {
				}
				if (null != penaltyRate && !"".equals(penaltyRate.trim())) {
					try {
						double penrate = Double.parseDouble(penaltyRate);
						if (penrate > 999) {
							errors
									.add(
											ProductDefinitionConstants.ERRORMAXPENALTYRATE,
											new ActionMessage(
													ProductDefinitionConstants.ERRORMAXPENALTYRATE));
						}
					} catch (Exception ne) {
					}
				}

				Context context = (Context) SessionUtils.getAttribute(
						Constants.CONTEXT, request.getSession());
				SearchResults searchResultsLoanFees = context
						.getSearchResultBasedOnName(ProductDefinitionConstants.LOANFEESLIST);
				List<Fees> prdFees = (List<Fees>) searchResultsLoanFees
						.getValue();
				Set<PrdOfferingFees> loanOfferingFeesSet = getPrdOfferingFees();
				if (null != loanOfferingFeesSet) {
					for (PrdOfferingFees prdOfferingFees : loanOfferingFeesSet) {
						for (Fees fees : prdFees) {
							if (prdOfferingFees.getFees().getFeeId().equals(
									fees.getFeeId())) {
								prdOfferingFees.setFees(fees);
							}
						}
					}
				}
				for (PrdOfferingFees prdOfferingFee : loanOfferingFeesSet) {
					if (prdOfferingFee.getFees().getFeeFrequency()
							.getFeeFrequencyTypeId().equals(
									FeeFrequencyType.PERIODIC.getValue())) {
						if (!prdOfferingFee.getFees().getFeeFrequency()
								.getFeeMeetingFrequency().getMeetingDetails()
								.getRecurrenceType().getRecurrenceId().equals(
										getPrdOfferingMeeting().getMeeting()
												.getMeetingDetails()
												.getRecurrenceType()
												.getRecurrenceId())) {
							errors
									.add(
											ProductDefinitionConstants.ERRORFEEFREQUENCY,
											new ActionMessage(
													ProductDefinitionConstants.ERRORFEEFREQUENCY,
													prdOfferingFee.getFees()
															.getFeeName()));
						}
					}
				}
			}
			if (ProductDefinitionConstants.PREVIEWMETHOD.equals(methodCalled)
					&& ProductDefinitionConstants.INPUTADMIN
							.equalsIgnoreCase(input)) {
				java.sql.Date startingDate = null;
				java.sql.Date endingDate = null;
				if (startDate != null && !startDate.equals("")) {
					startingDate = DateHelper.getLocaleDate(
							getUserLocale(request), startDate);
				}
				if (endDate != null && !endDate.equals("")) {
					endingDate = DateHelper.getLocaleDate(
							getUserLocale(request), endDate);
				}
				Calendar currentCalendar = new GregorianCalendar();
				int year = currentCalendar.get(Calendar.YEAR);
				int month = currentCalendar.get(Calendar.MONTH);
				int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
				currentCalendar = new GregorianCalendar(year, month, day);
				Calendar nextCalendar = new GregorianCalendar(year + 1, month,
						day);
				java.sql.Date currentDate = new java.sql.Date(currentCalendar
						.getTimeInMillis());
				java.sql.Date nextYearDate = new java.sql.Date(nextCalendar
						.getTimeInMillis());

				if (startingDate != null) {
					if (currentDate.compareTo(startingDate) > 0) {
						errors
								.add(
										ProductDefinitionConstants.INVALIDSTARTDATE,
										new ActionMessage(
												ProductDefinitionConstants.INVALIDSTARTDATE));
					}
					if (startingDate.after(nextYearDate)) {
						errors
								.add(
										ProductDefinitionConstants.INVALIDSTARTDATE,
										new ActionMessage(
												ProductDefinitionConstants.INVALIDSTARTDATE));
					}
				}
				if (startingDate != null && endingDate != null
						&& startingDate.compareTo(endingDate) >= 0) {
					errors.add(ProductDefinitionConstants.INVALIDENDDATE,
							new ActionMessage(
									ProductDefinitionConstants.INVALIDENDDATE));
				}
				if (startingDate != null
						&& currentDate.compareTo(startingDate) == 0) {
					prdStatus
							.setOfferingStatusId(ProductDefinitionConstants.LOANACTIVE);
				}
			}

			if (ProductDefinitionConstants.PREVIEWMETHOD.equals(methodCalled)
					&& ProductDefinitionConstants.INPUTDETAILS
							.equalsIgnoreCase(input)) {
				java.sql.Date startingDate = null;
				java.sql.Date endingDate = null;
				if (startDate != null && !startDate.equals("")) {
					startingDate = DateHelper.getLocaleDate(
							getUserLocale(request), startDate);
				}
				if (endDate != null && !endDate.equals("")) {
					endingDate = DateHelper.getLocaleDate(
							getUserLocale(request), endDate);
				}
				if (startingDate != null && endingDate != null
						&& startingDate.compareTo(endingDate) >= 0) {
					errors.add(ProductDefinitionConstants.INVALIDENDDATE,
							new ActionMessage(
									ProductDefinitionConstants.INVALIDENDDATE));
				}
			}
		}
		return errors;
	}

	/**
	 * The reset method is called before setting the values into the
	 * actionform.In this method, the values are cleared and the default values
	 * are set.
	 * 
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		if (method != null
				&& method.equals(ProductDefinitionConstants.PREVIEWMETHOD)) {
			prinDueLastInstFlag = ProductDefinitionConstants.DEFAULTPRINDUELASTINSTFLAG;
			intDedDisbursementFlag = ProductDefinitionConstants.DEFAULTINTDEDDISBURSEMENTFLAG;
			loanOfferingFunds = new String[] {};
			// Bug id 28070. Reset the values.
			prdOfferinFees = new String[] {};
			penaltyRate = "";
			gracePeriodType = new GracePeriodType();
			gracePeriodDuration = "";
			interestCalcRule = new InterestCalcRule();
		}
		if (method != null
				&& method.equals(ProductDefinitionConstants.LOADMETHOD)) {
			startDate = DateHelper.getCurrentDate(getUserLocale(request));
			recurWeekDay = ProductDefinitionConstants.DEFAULTRECURFREQUENCY;
			minNoInstallments = ProductDefinitionConstants.DEFAULTMINNOINSTALLMENTS;
		}

	}

}
