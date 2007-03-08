/**

 * LoanPrdActionForm.java    version: 1.0

 

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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class LoanPrdActionForm extends BaseActionForm {
	private final MifosLogger logger;

	private String prdOfferingId;

	private String prdOfferingName;

	private String prdOfferingShortName;

	private String description;

	private String prdCategory;

	private String startDate;

	private String endDate;

	private String prdApplicableMaster;

	private String loanCounter;

	private String minLoanAmount;

	private String maxLoanAmount;

	private String defaultLoanAmount;

	private String interestTypes;

	private String maxInterestRate;

	private String minInterestRate;

	private String defInterestRate;

	private String freqOfInstallments;

	private String recurAfter;

	private String maxNoInstallments;

	private String minNoInstallments;

	private String defNoInstallments;

	private String intDedDisbursementFlag;

	private String prinDueLastInstFlag;

	private String gracePeriodType;

	private String gracePeriodDuration;

	private String[] prdOfferinFees;

	private String[] loanOfferingFunds;

	private String principalGLCode;

	private String interestGLCode;

	private String prdStatus;

	public LoanPrdActionForm() {
		this(MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER));
	}

	public LoanPrdActionForm(MifosLogger logger) {
		super();
		prdOfferinFees = null;
		loanOfferingFunds = null;
		this.logger = logger;
	}

	public String getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	public void setDefaultLoanAmount(String defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	public String getDefInterestRate() {
		return defInterestRate;
	}

	public void setDefInterestRate(String defInterestRate) {
		this.defInterestRate = defInterestRate;
	}

	public String getDefNoInstallments() {
		return defNoInstallments;
	}

	public void setDefNoInstallments(String defNoInstallments) {
		this.defNoInstallments = defNoInstallments;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getFreqOfInstallments() {
		return freqOfInstallments;
	}

	public void setFreqOfInstallments(String freqOfInstallments) {
		this.freqOfInstallments = freqOfInstallments;
	}

	public String getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	public void setGracePeriodDuration(String gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	public String getGracePeriodType() {
		return gracePeriodType;
	}

	public void setGracePeriodType(String gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	public String getIntDedDisbursementFlag() {
		return intDedDisbursementFlag;
	}

	public void setIntDedDisbursementFlag(String intDedDisbursementFlag) {
		this.intDedDisbursementFlag = intDedDisbursementFlag;
	}

	public String getInterestGLCode() {
		return interestGLCode;
	}

	public void setInterestGLCode(String interestGLCode) {
		this.interestGLCode = interestGLCode;
	}

	public String getInterestTypes() {
		return interestTypes;
	}

	public void setInterestTypes(String interestTypes) {
		this.interestTypes = interestTypes;
	}

	public String getLoanCounter() {
		return loanCounter;
	}

	public void setLoanCounter(String loanCounter) {
		this.loanCounter = loanCounter;
	}

	public String[] getLoanOfferingFunds() {
		return loanOfferingFunds;
	}

	public void setLoanOfferingFunds(String[] loanOfferingFunds) {
		this.loanOfferingFunds = loanOfferingFunds;
	}

	public String getMaxInterestRate() {
		return maxInterestRate;
	}

	public void setMaxInterestRate(String maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}

	public String getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMaxLoanAmount(String maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public String getMaxNoInstallments() {
		return maxNoInstallments;
	}

	public void setMaxNoInstallments(String maxNoInstallments) {
		this.maxNoInstallments = maxNoInstallments;
	}

	public String getMinInterestRate() {
		return minInterestRate;
	}

	public void setMinInterestRate(String minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	public String getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setMinLoanAmount(String minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public String getMinNoInstallments() {
		return minNoInstallments;
	}

	public void setMinNoInstallments(String minNoInstallments) {
		this.minNoInstallments = minNoInstallments;
	}

	/** Called via reflection from jsp's (I think).
	 * Most/all java code should instead call 
	 * {@link #getPrdApplicableMasterEnum()}
	 */
	public String getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	public ApplicableTo getPrdApplicableMasterEnum() {
		return ApplicableTo.fromInt(
			Integer.parseInt(prdApplicableMaster));
	}

	/** Called via reflection from jsp's (I think).
	 * Most/all java code should instead call
	 * {@link #setPrdApplicableMaster(ApplicableTo)}
	 */
	public void setPrdApplicableMaster(String prdApplicableMaster) {
		this.prdApplicableMaster = prdApplicableMaster;
	}

	public void setPrdApplicableMaster(ApplicableTo applicableTo) {
		this.prdApplicableMaster = "" + applicableTo.getValue();
	}

	public String getPrdCategory() {
		return prdCategory;
	}

	public void setPrdCategory(String prdCategory) {
		this.prdCategory = prdCategory;
	}

	public String[] getPrdOfferinFees() {
		return prdOfferinFees;
	}

	public void setPrdOfferinFees(String[] prdOfferinFees) {
		this.prdOfferinFees = prdOfferinFees;
	}

	public String getPrdOfferingId() {
		return prdOfferingId;
	}

	public void setPrdOfferingId(String prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public String getPrdOfferingShortName() {
		return prdOfferingShortName;
	}

	public void setPrdOfferingShortName(String prdOfferingShortName) {
		this.prdOfferingShortName = prdOfferingShortName;
	}

	public String getPrdStatus() {
		return prdStatus;
	}

	public void setPrdStatus(String prdStatus) {
		this.prdStatus = prdStatus;
	}

	public String getPrincipalGLCode() {
		return principalGLCode;
	}

	public void setPrincipalGLCode(String principalGLCode) {
		this.principalGLCode = principalGLCode;
	}

	public String getPrinDueLastInstFlag() {
		return prinDueLastInstFlag;
	}

	public void setPrinDueLastInstFlag(String prinDueLastInstFlag) {
		this.prinDueLastInstFlag = prinDueLastInstFlag;
	}

	public String getRecurAfter() {
		return recurAfter;
	}

	public void setRecurAfter(String recurAfter) {
		this.recurAfter = recurAfter;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Date getStartDateValue(Locale locale) {
		return DateHelper.getLocaleDate(locale, getStartDate());
	}

	public Date getEndDateValue(Locale locale) {
		return DateHelper.getLocaleDate(locale, getEndDate());
	}

	public Short getPrdCategoryValue() {
		return getShortValue(getPrdCategory());
	}

	public Short getGracePeriodTypeValue() {
		return getShortValue(getGracePeriodType());
	}

	public Short getGracePeriodDurationValue() {
		return getShortValue(getGracePeriodDuration());
	}

	public Short getInterestTypesValue() {
		return getShortValue(getInterestTypes());
	}

	public Money getMaxLoanAmountValue() {
		return getMoney(getMaxLoanAmount());
	}

	public Money getMinLoanAmountValue() {
		return getMoney(getMinLoanAmount());
	}

	public Money getDefaultLoanAmountValue() {
		return (StringUtils.isNullAndEmptySafe(getDefaultLoanAmount()) && !getDefaultLoanAmount()
				.trim().equals(".")) ? new Money(getDefaultLoanAmount()) : null;
	}

	public Double getMaxInterestRateValue() {
		return getDoubleValue(getMaxInterestRate());
	}

	public Double getMinInterestRateValue() {
		return getDoubleValue(getMinInterestRate());
	}

	public Double getDefInterestRateValue() {
		return getDoubleValue(getDefInterestRate());
	}

	public Short getMaxNoInstallmentsValue() {
		return getShortValue(getMaxNoInstallments());
	}

	public Short getMinNoInstallmentsValue() {
		return getShortValue(getMinNoInstallments());
	}

	public Short getDefNoInstallmentsValue() {
		return getShortValue(getDefNoInstallments());
	}

	public boolean isLoanCounterValue() {
		return getBooleanValue(getLoanCounter());
	}

	public boolean isIntDedAtDisbValue() {
		return getBooleanValue(getIntDedDisbursementFlag());
	}

	public boolean isPrinDueLastInstValue() {
		return getBooleanValue(getPrinDueLastInstFlag());
	}

	public Short getRecurAfterValue() {
		return getShortValue(getRecurAfter());
	}

	public Short getFreqOfInstallmentsValue() {
		return getShortValue(getFreqOfInstallments());
	}

	public Short getPrdStatusValue() {
		return getShortValue(getPrdStatus());
	}

	public Short getPrdOfferingIdValue() {
		return getShortValue(getPrdOfferingId());
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		logger
				.debug("start reset method of Savings Product Action form method :"
						+ method);
		if (method != null && method.equals(Methods.load.toString())) {
			startDate = DateHelper.getCurrentDate(getUserContext(request)
					.getPereferedLocale());
			recurAfter = "1";
			minNoInstallments = "1";
		}
		if (method != null
				&& (method.equals(Methods.preview.toString()) || method
						.equals(Methods.editPreview.toString()))) {
			intDedDisbursementFlag = null;
			prinDueLastInstFlag = null;
			loanCounter = null;
			prdOfferinFees = null;
			loanOfferingFunds = null;
			gracePeriodType = null;
			gracePeriodDuration = null;
		}
		logger
				.debug("reset method of Savings Product Action form method called ");
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		logger
				.debug("validate method of Savings Product Action form method called :"
						+ method);
		if (method != null && method.equals(Methods.preview.toString())) {
			errors.add(super.validate(mapping, request));
			validateForPreview(request, errors);
		}
		if (method != null && method.equals(Methods.editPreview.toString())) {
			errors.add(super.validate(mapping, request));
			validateForEditPreview(request, errors);
		}
		if (method != null && !method.equals(Methods.validate.toString())) {
			request.setAttribute(ProductDefinitionConstants.METHODCALLED,
					method);
		}
		logger
				.debug("validate method of Savings Product Action form called and error size:"
						+ errors.size());
		return errors;
	}

	public void clear() {
		logger
				.debug("start clear method of Loan Product Action form method :"
						+ prdOfferingId);
		this.prdOfferingId = null;
		this.prdOfferingName = null;
		this.prdOfferingShortName = null;
		this.description = null;
		this.prdCategory = null;
		this.startDate = null;
		this.endDate = null;
		this.prdApplicableMaster = null;
		this.loanCounter = null;
		this.minLoanAmount = null;
		this.maxLoanAmount = null;
		this.defaultLoanAmount = null;
		this.interestTypes = null;
		this.maxInterestRate = null;
		this.minInterestRate = null;
		this.defInterestRate = null;
		this.freqOfInstallments = null;
		this.recurAfter = null;
		this.maxNoInstallments = null;
		this.minNoInstallments = null;
		this.defNoInstallments = null;
		this.intDedDisbursementFlag = null;
		this.prinDueLastInstFlag = null;
		this.gracePeriodType = null;
		this.gracePeriodDuration = null;
		this.prdOfferinFees = null;
		this.loanOfferingFunds = null;
		this.principalGLCode = null;
		this.interestGLCode = null;
		this.prdStatus = null;
		logger
				.debug("clear method of Loan Product Action form method called :"
						+ prdOfferingId);
	}

	private void validateForPreview(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateForPreview method of Loan Product Action form method :"
						+ prdOfferingName);
		validateStartDate(request, errors);
		validateEndDate(request, errors);
		validateMinMaxDefLoanAmounts(errors);
		if (StringUtils.isNullOrEmpty(getInterestTypes()))
			addError(errors, "interestTypes",
					ProductDefinitionConstants.ERRORSSELECTCONFIG, getLabel(
							ConfigurationConstants.INTEREST, request),
					ProductDefinitionConstants.RATETYPE);
		validateMinMaxDefInterestRates(errors, request);
		vaildateDecliningInterestSvcChargeDeductedAtDisbursement(errors,
				request);
		validatePrincDueOnLastInstAndPrincGraceType(errors);
		setSelectedFeesAndFundsAndValidateForFrequency(request, errors);
		validateInterestGLCode(request,errors);
		logger
				.debug("validateForPreview method of Loan Product Action form method called :"
						+ prdOfferingName);
	}

	private void validateForEditPreview(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateForEditPreview method of Loan Product Action form method :"
						+ prdOfferingName);
		validateStartDateForEditPreview(request, errors);
		validateEndDate(request, errors);
		validateMinMaxDefLoanAmounts(errors);
		if (StringUtils.isNullOrEmpty(getInterestTypes()))
			addError(errors, "interestTypes",
					ProductDefinitionConstants.ERRORSSELECTCONFIG, getLabel(
							ConfigurationConstants.INTEREST, request),
					ProductDefinitionConstants.RATETYPE);
		if (StringUtils.isNullOrEmpty(getPrdStatus()))
			addError(errors, "prdStatus",
					ProductDefinitionConstants.ERROR_SELECT,
					ProductDefinitionConstants.STATUS);
		validateMinMaxDefInterestRates(errors, request);
		vaildateDecliningInterestSvcChargeDeductedAtDisbursement(errors,
				request);
		validatePrincDueOnLastInstAndPrincGraceType(errors);
		setSelectedFeesAndFundsAndValidateForFrequency(request, errors);
		validateInterestGLCode(request,errors);
		logger
				.debug("validateForEditPreview method of Loan Product Action form method called :"
						+ prdOfferingName);
	}

	private void validateStartDateForEditPreview(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateStartDateForEditPreview method of Loan Product Action form method :"
						+ startDate);
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		java.util.Date oldStartDate = null;
		try {
			oldStartDate = (java.util.Date) SessionUtils.getAttribute(
					ProductDefinitionConstants.LOANPRDSTARTDATE, request);
		} catch (PageExpiredException e) {
		}
		Date changedStartDate = getStartDateValue(getUserContext(request)
				.getPereferedLocale());
		if (oldStartDate != null && changedStartDate != null) {
			if (DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime())
					.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) <= 0
					&& (changedStartDate != null && DateUtils
							.getDateWithoutTimeStamp(oldStartDate.getTime())
							.compareTo(
									DateUtils
											.getDateWithoutTimeStamp(changedStartDate
													.getTime())) != 0)) {
				addError(errors, "startDate",
						ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
			}
		} else if (changedStartDate != null
				&& DateUtils
						.getDateWithoutTimeStamp(changedStartDate.getTime())
						.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
			validateStartDate(request, errors);

		}
		logger
				.debug("validateStartDateForEditPreview method of Loan Product Action form method called :"
						+ startDate + "---" + oldStartDate);
	}

	private void validateStartDate(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateStartDate method of Loan Product Action form method :"
						+ startDate);
		Date startingDate = getStartDateValue(getUserContext(request)
				.getPereferedLocale());
		if (startingDate != null
				&& ((DateUtils.getDateWithoutTimeStamp(startingDate.getTime())
						.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0) || (DateUtils
						.getDateWithoutTimeStamp(startingDate.getTime())
						.compareTo(
								DateUtils
										.getCurrentDateOfNextYearWithOutTimeStamp()) > 0)))
			addError(errors, "startDate",
					ProductDefinitionConstants.INVALIDSTARTDATE);
		logger
				.debug("validateStartDate method of Loan Product Action form method called :"
						+ startDate);
	}

	private void validateEndDate(HttpServletRequest request, ActionErrors errors) {
		logger
				.debug("start validateEndDate method of Loan Product Action form method :"
						+ startDate + "---" + endDate);
		Date startingDate = getStartDateValue(getUserContext(request)
				.getPereferedLocale());
		Date endingDate = getEndDateValue(getUserContext(request)
				.getPereferedLocale());
		if (startingDate != null && endingDate != null
				&& startingDate.compareTo(endingDate) >= 0)
			addError(errors, "endDate",
					ProductDefinitionConstants.INVALIDENDDATE);
		logger
				.debug("validateEndDate method of Loan Product Action form method called :"
						+ startDate + "---" + endDate);
	}

	private void setSelectedFeesAndFundsAndValidateForFrequency(
			HttpServletRequest request, ActionErrors errors) {
		logger
				.debug("start setSelectedFeesAndFundsAndValidateForFrequency method "
						+ "of Loan Product Action form method :");
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		List<FeeView> feeViews = new ArrayList<FeeView>();
		try {
			if (getPrdOfferinFees() != null && getPrdOfferinFees().length > 0) {

				List<FeeBO> fees = (List<FeeBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRDFEE, request);
				for (String selectedFee : getPrdOfferinFees()) {
					FeeBO fee = getFeeFromList(fees, selectedFee);
					if (fee != null) {
						isFrequencyMatchingOfferingFrequency(fee, errors);
						feeViews.add(new FeeView(getUserContext(request), fee));

					}
				}
			}
			SessionUtils.setCollectionAttribute(
					ProductDefinitionConstants.LOANPRDFEESELECTEDLIST,
					feeViews, request);
		} catch (PageExpiredException e) {
		}
		List<FundBO> selectedFunds = new ArrayList<FundBO>();
		try {
			if (getLoanOfferingFunds() != null
					&& getLoanOfferingFunds().length > 0) {

				List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.SRCFUNDSLIST, request);
				for (String selectedFund : getLoanOfferingFunds()) {
					FundBO fund = getFundFromList(funds, selectedFund);
					if (fund != null)
						selectedFunds.add(fund);
				}
			}
			SessionUtils.setCollectionAttribute(
					ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST,
					selectedFunds, request);
		} catch (PageExpiredException e) {
		}
		logger
				.debug("setSelectedFeesAndFundsAndValidateForFrequency method "
						+ "of Loan Product Action form method called :");
	}

	private FeeBO getFeeFromList(List<FeeBO> fees, String feeSelected) {
		logger
				.debug("getFeeFromList method of Loan Product Action form method called :"
						+ feeSelected);
		for (FeeBO fee : fees)
			if (fee.getFeeId().equals(getShortValue(feeSelected)))
				return fee;
		return null;
	}

	private FundBO getFundFromList(List<FundBO> funds, String fundSelected) {
		logger
				.debug("getFundFromList method of Loan Product Action form method called :"
						+ fundSelected);
		for (FundBO fund : funds)
			if (fund.getFundId().equals(getShortValue(fundSelected)))
				return fund;
		return null;
	}

	private void isFrequencyMatchingOfferingFrequency(FeeBO fee,
			ActionErrors errors) {
		logger
				.debug("start Loan prd Action Form isFrequencyMatchingOfferingFrequency - fee:"
						+ fee);
		if (getFreqOfInstallmentsValue() != null
				&& fee.isPeriodic()
				&& !(fee.getFeeFrequency().getFeeMeetingFrequency()
						.getMeetingDetails().getRecurrenceType()
						.getRecurrenceId().equals(getFreqOfInstallmentsValue())))
			addError(errors, "Fee",
					ProductDefinitionConstants.ERRORFEEFREQUENCY, fee
							.getFeeName());
		logger
				.debug("Loan prd Action Form isFrequencyMatchingOfferingFrequency called - fee:"
						+ fee);
	}

	private void validateMinMaxDefInterestRates(ActionErrors errors,
			HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form validateMinMaxDefInterestRates :"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
		validateForMand(getMaxInterestRate(), errors, "maxInterestRate",
				ProductDefinitionConstants.ERRORSENTERCONFIG,
				ProductDefinitionConstants.MAX, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE);
		validateForMand(getMinInterestRate(), errors, "minInterestRate",
				ProductDefinitionConstants.ERRORSENTERCONFIG,
				ProductDefinitionConstants.MIN, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE);
		validateForMand(getDefInterestRate(), errors, "defInterestRate",
				ProductDefinitionConstants.ERRORSENTERCONFIG,
				ProductDefinitionConstants.DEFAULT, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE);
		validateForRange(getMaxInterestRate(), 0, 999, errors,
				"maxInterestRate",
				ProductDefinitionConstants.ERRORSDEFMINMAXCONFIG,
				ProductDefinitionConstants.MAX, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE, "0", "999");
		validateForRange(getMinInterestRate(), 0, 999, errors,
				"minInterestRate",
				ProductDefinitionConstants.ERRORSDEFMINMAXCONFIG,
				ProductDefinitionConstants.MIN, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE, "0", "999");
		validateForRange(getDefInterestRate(), 0, 999, errors,
				"defInterestRate",
				ProductDefinitionConstants.ERRORSDEFMINMAXCONFIG,
				ProductDefinitionConstants.DEFAULT, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE, "0", "999");
		vaildateMinMaxInterestRate(errors, request);
		vaildateDefMinMaxInterestRate(errors, request);
		logger
				.debug("Loan prd Action Form validateMinMaxDefInterestRates called:"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
	}

	private void validateForMand(String value, ActionErrors errors,
			String property, String key, String... arg) {
		logger.debug("Start Loan prd Action Form validateForMand :"
				+ value);
		if (StringUtils.isNullOrEmpty(value))
			addError(errors, property, key, arg);
		logger.debug("Loan prd Action Form validateForMand called:"
				+ value);
	}

	private void validateForRange(String value, double min, double max,
			ActionErrors errors, String property, String key, String... arg) {
		logger.debug("start Loan prd Action Form validateForRange :"
				+ value);
		if (StringUtils.isNullAndEmptySafe(value)) {
			double valueToBeChecked = Double.valueOf(value);
			if (valueToBeChecked < min || valueToBeChecked > max) {
				addError(errors, property, key, arg);
			}
		}
		logger.debug("Loan prd Action Form validateForRange called:"
				+ value);
	}

	private void vaildateMinMaxInterestRate(ActionErrors errors,
			HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form vaildateMinMaxInterestRate :"
						+ maxInterestRate + "---" + minInterestRate);
		if (StringUtils.isNullAndEmptySafe(getMaxInterestRate())
				&& StringUtils.isNullAndEmptySafe(getMinInterestRate())) {
			double maximumInterestRate = Double.valueOf(getMaxInterestRate());
			double minimumInterestRate = Double.valueOf(getMinInterestRate());
			if (maximumInterestRate <= 999.0 && minimumInterestRate <= 999.0
					&& maximumInterestRate < minimumInterestRate)
				addError(errors, "MinMaxInterestRate",
						ProductDefinitionConstants.ERRORSMINMAXINTCONFIG,
						ProductDefinitionConstants.MAX, getLabel(
								ConfigurationConstants.INTEREST, request),
						ProductDefinitionConstants.RATE,
						ProductDefinitionConstants.MIN);
		}
		logger
				.debug("Loan prd Action Form vaildateMinMaxInterestRate called:"
						+ maxInterestRate + "---" + minInterestRate);
	}

	private void vaildateDefMinMaxInterestRate(ActionErrors errors,
			HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form vaildateDefMinMaxInterestRate :"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
		if (StringUtils.isNullAndEmptySafe(getMaxInterestRate())
				&& StringUtils.isNullAndEmptySafe(getMinInterestRate())
				&& StringUtils.isNullAndEmptySafe(getDefInterestRate())) {
			double maximumInterestRate = Double.valueOf(getMaxInterestRate());
			double minimumInterestRate = Double.valueOf(getMinInterestRate());
			double defaultInterestRate = Double.valueOf(getDefInterestRate());
			if (maximumInterestRate <= 999.0 && minimumInterestRate <= 999.0
					&& defaultInterestRate <= 999.0) {
				if (defaultInterestRate < minimumInterestRate
						|| defaultInterestRate > maximumInterestRate)
					addError(errors, "DefInterestRate",
							ProductDefinitionConstants.ERRORSDEFINTCONFIG,
							ProductDefinitionConstants.DEFAULT, getLabel(
									ConfigurationConstants.INTEREST, request),
							ProductDefinitionConstants.RATE,
							ProductDefinitionConstants.MIN,
							ProductDefinitionConstants.MAX);
			}

		}
		logger
				.debug("Loan prd Action Form vaildateDefMinMaxInterestRate called :"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
	}

	private void validateMinMaxDefLoanAmounts(ActionErrors errors) {
		try {
			if (StringUtils.isNullAndEmptySafe(getMaxLoanAmount())
					&& StringUtils.isNullAndEmptySafe(getMinLoanAmount())) {
				double maxLoanAmnt = Double.valueOf(getMaxLoanAmount());
				double minLoanAmnt = Double.valueOf(getMinLoanAmount());
				if (minLoanAmnt > maxLoanAmnt)
					addError(errors,
							ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT,
							ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT);
				if (StringUtils.isNullAndEmptySafe(getDefaultLoanAmount())) {
					double defLoanAmnt = Double
							.parseDouble(getDefaultLoanAmount());

					if (defLoanAmnt < minLoanAmnt || defLoanAmnt > maxLoanAmnt) {
						addError(errors,
								ProductDefinitionConstants.ERRORDEFLOANAMOUNT,
								ProductDefinitionConstants.ERRORDEFLOANAMOUNT);
					}
				}
			}
		} catch (Exception ne) {
		}
	}

	private void vaildateDecliningInterestSvcChargeDeductedAtDisbursement(
			ActionErrors errors, HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form vaildateDecliningInterestSvcChargeDeductedAtDisbursement :"
						+ getInterestTypes()
						+ "---"
						+ getIntDedDisbursementFlag());

		if (getInterestTypes() != null
				&& getInterestTypes().equals(
						InterestType.DECLINING.getValue()
								.toString())) {

			if (null != getIntDedDisbursementFlag()
					&& getIntDedDisbursementFlag().equals("1")) {
				errors
						.add(
								ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION,
								new ActionMessage(
										ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION));
			}
		}

		logger
				.debug("Loan prd Action Form vaildateDecliningInterestSvcChargeDeductedAtDisbursement called ");
	}

	private void validatePrincDueOnLastInstAndPrincGraceType(ActionErrors errors) {
		if (getGracePeriodTypeValue() != null
				&& getGracePeriodTypeValue().equals(
						GraceType.PRINCIPALONLYGRACE.getValue())
				&& isPrinDueLastInstValue()) {
			addError(
					errors,
					ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE,
					ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
		}
	}
	
	private void validateInterestGLCode(HttpServletRequest request,
			ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(getInterestGLCode()))
			addError(
					errors,
					ProductDefinitionConstants.INTERESTGLCODE,
					ProductDefinitionConstants.ERROR_SELECT,
					ProductDefinitionConstants.GLCODE_FOR
							+ getLabel(ConfigurationConstants.INTEREST, request));
	}

}
