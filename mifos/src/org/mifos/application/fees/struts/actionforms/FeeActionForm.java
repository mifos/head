/**

 * FeeActionForm.java    version: xxx



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

package org.mifos.application.fees.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;

public class FeeActionForm extends ValidatorActionForm {
	private String feeId;

	private String feeName;

	private String categoryType;

	private boolean customerDefaultFee = false;

	private String feeFrequencyType;

	private String feeRecurrenceType;

	private String amount;

	private String rate;

	private String feeFormula;

	private String glCode;

	private String loanCharge;

	private String customerCharge;

	private String weekRecurAfter;

	private String monthRecurAfter;

	private String feeStatus;

	public String getFeeId() {
		return feeId;
	}

	public Short getFeeIdValue() {
		return getShortValue(feeId);
	}
	
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Money getAmountValue() {
		return getMoney(amount);
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public FeeCategory getCategoryTypeValue() throws PropertyNotFoundException {
		return StringUtils.isNullAndEmptySafe(categoryType) ? FeeCategory
				.getFeeCategory(Short.valueOf(categoryType)) : null;
	}

	public String getCustomerCharge() {
		return customerCharge;
	}

	public void setCustomerCharge(String customerCharge) {
		this.customerCharge = customerCharge;
	}

	public void setCustomerDefaultFee(boolean customerDefaultFee) {
		this.customerDefaultFee = customerDefaultFee;
	}

	public boolean isCustomerDefaultFee() {
		return customerDefaultFee;
	}

	public String getFeeFormula() {
		return feeFormula;
	}

	public void setFeeFormula(String feeFormula) {
		this.feeFormula = feeFormula;
	}

	public FeeFormula getFeeFormulaValue() throws PropertyNotFoundException {
		return StringUtils.isNullAndEmptySafe(feeFormula) ? FeeFormula
				.getFeeFormula(Short.valueOf(feeFormula)) : null;
	}

	public String getFeeFrequencyType() {
		return feeFrequencyType;
	}

	public void setFeeFrequencyType(String feeFrequencyType) {
		this.feeFrequencyType = feeFrequencyType;
	}

	public FeeFrequencyType getFeeFrequencyTypeValue()
			throws PropertyNotFoundException {
		return StringUtils.isNullAndEmptySafe(feeFrequencyType) ? FeeFrequencyType
				.getFeeFrequencyType(Short.valueOf(feeFrequencyType))
				: null;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public String getFeePaymentType() {
		return StringUtils.isNullAndEmptySafe(categoryType) && isCategoryLoan() ? loanCharge
				: customerCharge;
	}

	public FeePayment getFeePaymentTypeValue() throws PropertyNotFoundException {
		return StringUtils.isNullAndEmptySafe(getFeePaymentType()) ? FeePayment
				.getFeePayment(Short.valueOf(getFeePaymentType())) : null;
	}

	public String getFeeRecurrenceType() {
		return feeRecurrenceType;
	}

	public void setFeeRecurrenceType(String feeRecurrenceType) {
		this.feeRecurrenceType = feeRecurrenceType;
	}

	public MeetingFrequency getFeeRecurrenceTypeValue()
			throws PropertyNotFoundException {
		return StringUtils.isNullAndEmptySafe(feeRecurrenceType) ? MeetingFrequency
				.getMeetingFrequency(Short.valueOf(feeRecurrenceType))
				: null;
	}

	public String getFeeStatus() {
		return feeStatus;
	}

	public void setFeeStatus(String feeStatus) {
		this.feeStatus = feeStatus;
	}

	public FeeStatus getFeeStatusValue() {
		return StringUtils.isNullAndEmptySafe(feeStatus) ? FeeStatus
				.getFeeStatus(Short.valueOf(feeStatus)) : null;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public Short getGlCodeValue() {
		return getShortValue(glCode);
	}

	public String getLoanCharge() {
		return loanCharge;
	}

	public void setLoanCharge(String loanCharge) {
		this.loanCharge = loanCharge;
	}

	public String getMonthRecurAfter() {
		return monthRecurAfter;
	}

	public void setMonthRecurAfter(String monthRecurAfter) {
		this.monthRecurAfter = monthRecurAfter;
	}

	public Short getMonthRecurAfterValue() {
		return getShortValue(monthRecurAfter);
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public Double getRateValue() {
		return StringUtils.isNullAndEmptySafe(rate) ? Double.valueOf(rate)
				: null;
	}

	public String getWeekRecurAfter() {
		return weekRecurAfter;
	}

	public void setWeekRecurAfter(String weekRecurAfter) {
		this.weekRecurAfter = weekRecurAfter;
	}

	public Short getWeekRecurAfterValue() {
		return getShortValue(weekRecurAfter);
	}

	public boolean isCategoryLoan() {
		return FeeCategory.LOAN.getValue().equals(Short.valueOf(categoryType));
	}

	public boolean isRateFee() {
		return StringUtils.isNullAndEmptySafe(rate)
				&& StringUtils.isNullAndEmptySafe(feeFormula);
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(Methods.method.toString());
		if (method != null && method.equals(Methods.preview.toString())) {
			customerDefaultFee = false;
			loanCharge = null;
			customerCharge = null;
			amount = null;
			rate = null;
		}
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter(Methods.method.toString());
		if (null != methodCalled) {
			if (methodCalled.equals(Methods.preview.toString())) {
				errors.add(super.validate(mapping, request));
				validateForPreview(errors);
			} else if (methodCalled.equalsIgnoreCase(Methods.editPreview
					.toString())) {
				validateForEditPreview(errors);
			}
		}
		
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}

	private void validateForPreview(ActionErrors errors) {
		if (StringUtils.isNullAndEmptySafe(categoryType) && isCategoryLoan()) {
			validateForPreviewLoanCategory(errors);
		} else if (getAmountValue().equals(new Money())) {
			addError(errors, FeeConstants.AMOUNT, FeeConstants.ERRORS_SPECIFY_VALUE);
		}
	}

	private void validateForPreviewLoanCategory(ActionErrors errors) {
		if (isBothRateAndAmountEmpty() || isBothRateAndAmountNotEmpty())
			addError(errors, FeeConstants.RATE_OR_AMOUNT, FeeConstants.ERRORS_SPECIFY_AMOUNT_OR_RATE);
		if (isRateEmptyAndFormulaNotNull() || isRateNotEmptyAndFormulaNull())
			addError(errors, FeeConstants.RATE_AND_FORMULA, FeeConstants.ERRORS_SPECIFY_RATE_AND_FORMULA);
	}

	private void validateForEditPreview(ActionErrors errors) {
		if (StringUtils.isNullAndEmptySafe(feeFormula)) {
			if (!StringUtils.isNullAndEmptySafe(rate))
				addError(errors, FeeConstants.RATE_AND_FORMULA, FeeConstants.ERRORS_SPECIFY_RATE_AND_FORMULA);
		} else if (!StringUtils.isNullAndEmptySafe(amount))
			addError(errors, FeeConstants.AMOUNT, FeeConstants.ERRORS_SPECIFY_VALUE);

		if (getFeeStatusValue() == null)
			addError(errors, FeeConstants.AMOUNT, FeeConstants.ERRORS_SELECT_STATUS);
	}

	private boolean isBothRateAndAmountEmpty() {
		return !StringUtils.isNullAndEmptySafe(rate)
				&& !StringUtils.isNullAndEmptySafe(amount);
	}

	private boolean isBothRateAndAmountNotEmpty() {
		return StringUtils.isNullAndEmptySafe(rate)
				&& StringUtils.isNullAndEmptySafe(amount)
				|| StringUtils.isNullAndEmptySafe(feeFormula)
				&& StringUtils.isNullAndEmptySafe(amount);
	}

	private boolean isRateEmptyAndFormulaNotNull() {
		return (!StringUtils.isNullAndEmptySafe(rate) && StringUtils
				.isNullAndEmptySafe(feeFormula));
	}

	private boolean isRateNotEmptyAndFormulaNull() {
		return (StringUtils.isNullAndEmptySafe(rate) && !StringUtils
				.isNullAndEmptySafe(feeFormula));
	}

	private void addError(ActionErrors errors, String property, String key,
			String... arg) {
		errors.add(property, new ActionMessage(key, arg));
	}

	private Short getShortValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? Short.valueOf(str) : null;
	}

	private Money getMoney(String str) {
		return (StringUtils.isNullAndEmptySafe(str) && !str.trim().equals(".")) ? new Money(
				str)
				: new Money();
	}
}
