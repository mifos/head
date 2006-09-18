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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanPrdActionForm extends BaseActionForm {
	private MifosLogger prdDefLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

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
		super();
		prdOfferinFees = null;
		loanOfferingFunds = null;
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

	public String getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	public void setPrdApplicableMaster(String prdApplicableMaster) {
		this.prdApplicableMaster = prdApplicableMaster;
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

	public Short getPrdApplicableMasterValue() {
		return getShortValue(getPrdApplicableMaster());
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
		return getMoney(getDefaultLoanAmount());
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

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		prdDefLogger
				.debug("start reset method of Savings Product Action form method ");
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		if (method != null && method.equals(Methods.load.toString())) {
			startDate = DateHelper.getCurrentDate(getUserContext(request)
					.getPereferedLocale());
		}
		if (method != null && method.equals(Methods.preview.toString())) {
			intDedDisbursementFlag = null;
			prinDueLastInstFlag = null;
			loanCounter = null;
		}
		prdDefLogger
				.debug("reset method of Savings Product Action form method called ");
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		prdDefLogger
				.debug("validate method of Savings Product Action form method called :"
						+ method);
		if (method != null && method.equals(Methods.preview.toString())) {
			errors.add(super.validate(mapping, request));
			Date startingDate = getStartDateValue(getUserContext(request)
					.getPereferedLocale());
			Date endingDate = getEndDateValue(getUserContext(request)
					.getPereferedLocale());
			if (startingDate != null
					&& DateUtils
							.getDateWithoutTimeStamp(startingDate.getTime())
							.compareTo(
									DateUtils.getCurrentDateWithoutTimeStamp()) < 0)
				addError(errors, "startDate",
						ProductDefinitionConstants.INVALIDSTARTDATE);
			if (startingDate != null && endingDate != null
					&& startingDate.compareTo(endingDate) >= 0)
				addError(errors, "endDate",
						ProductDefinitionConstants.INVALIDENDDATE);
		}
		if (method != null && !method.equals(Methods.validate.toString())) {
			request.setAttribute(ProductDefinitionConstants.METHODCALLED,
					method);
		}
		prdDefLogger
				.debug("validate method of Savings Product Action form called and error size:"
						+ errors.size());
		return errors;

	}

}
