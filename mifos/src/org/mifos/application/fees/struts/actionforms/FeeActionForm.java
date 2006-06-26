/**

 * FeeActionForm.java    version: xxx



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

package org.mifos.application.fees.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeFrequencyEntity;
import org.mifos.application.fees.business.FeeStatusEntity;
import org.mifos.application.fees.util.helpers.FeesConstants;

public class FeeActionForm extends ValidatorActionForm {

	private String feeName;

	private CategoryTypeEntity categoryType;

	private String adminCheck;

	private FeeFrequencyEntity feeFrequency;

	private String amount;

	private String rate;

	private FeeFormulaEntity feeFormula;

	private GLCodeEntity glCodeEntity;

	private String loanCharge;

	private String customerCharge;

	private String weekRecurAfter;

	private String monthRecurAfter;

	private FeeStatusEntity feeStatus;

	@Deprecated
	private String rateFlatFlag;

	@Deprecated
	private String rateOrAmount;

	public FeeActionForm() {
		super();
		categoryType = new CategoryTypeEntity();
		feeFrequency = new FeeFrequencyEntity();
		feeFormula = new FeeFormulaEntity();
		glCodeEntity = new GLCodeEntity();
		feeStatus = new FeeStatusEntity();
	}

	public String getAdminCheck() {
		return adminCheck;
	}

	public void setAdminCheck(String adminCheck) {
		this.adminCheck = adminCheck;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public CategoryTypeEntity getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryTypeEntity categoryType) {
		this.categoryType = categoryType;
	}

	public FeeFormulaEntity getFeeFormula() {
		return feeFormula;
	}

	public void setFeeFormula(FeeFormulaEntity feeFormula) {
		this.feeFormula = feeFormula;
	}

	public FeeFrequencyEntity getFeeFrequency() {
		Short frequencyTypeId = feeFrequency.getFeeFrequencyType()
				.getFeeFrequencyTypeId();
		if (frequencyTypeId != null) {
			if (frequencyTypeId.equals(FeesConstants.PERIODIC))
				buildPeriodicFeeFrequency();
			else if (categoryType.getCategoryId() != null)
				buildOneTimeFeeFrequency(isCategoryLoan());
		}
		return feeFrequency;
	}

	public void setFeeFrequency(FeeFrequencyEntity feeFrequency) {
		this.feeFrequency = feeFrequency;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public GLCodeEntity getGlCodeEntity() {
		return glCodeEntity;
	}

	public void setGlCodeEntity(GLCodeEntity glCodeEntity) {
		this.glCodeEntity = glCodeEntity;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getRateFlatFlag() {
		if (rate != null && !"".equals(rate.trim())) {
			rateFlatFlag = FeesConstants.RATE;
		}
		if (amount != null && !"".equals(amount.trim())) {
			rateFlatFlag = FeesConstants.AMOUNT;
		}
		return rateFlatFlag;
	}

	public void setRateFlatFlag(String rateFlatFlag) {
		this.rateFlatFlag = rateFlatFlag;
	}

	public String getRateOrAmount() {
		if (rate != null && !"".equals(rate.trim())) {
			rateOrAmount = rate;
		}
		if (amount != null && !"".equals(amount.trim())) {
			rateOrAmount = amount;
		}
		return rateOrAmount;
	}

	public void setRateOrAmount(String rateOrAmount) {
		this.rateOrAmount = rateOrAmount;
	}

	public String getCustomerCharge() {
		return customerCharge;
	}

	public void setCustomerCharge(String customerCharge) {
		this.customerCharge = customerCharge;
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

	public String getWeekRecurAfter() {
		return weekRecurAfter;
	}

	public void setWeekRecurAfter(String weekRecurAfter) {
		this.weekRecurAfter = weekRecurAfter;
	}

	public FeeStatusEntity getFeeStatus() {
		return feeStatus;
	}

	public void setFeeStatus(FeeStatusEntity status) {
		this.feeStatus = status;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(FeesConstants.METHOD);
		if (method != null && method.equals(FeesConstants.PREVIEW_METHOD)) {
			adminCheck = null;
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
		String methodCalled = request.getParameter(FeesConstants.METHOD);
		if (null != methodCalled
				&& methodCalled.equals(FeesConstants.PREVIEW_METHOD)) {
			errors.add(super.validate(mapping, request));
			if (Short.valueOf(FeesConstants.LOAN).equals(
					categoryType.getCategoryId())) {
				if (("".equals(rate.trim()) && "".equals(amount.trim()))
						|| (!"".equals(rate.trim()) && !""
								.equals(amount.trim()))) {
					errors.add("RateOrAmount", new ActionMessage(
							"errors.amountOrRate"));
				}
				if ((feeFormula.getFeeFormulaId() != null && "".equals(rate
						.trim()))
						|| (!"".equals(rate.trim())
								&& feeFormula.getFeeFormulaId() == null && ""
								.equals(amount.trim()))) {
					errors.add("RateAndFormula", new ActionMessage(
							"errors.rateAndFormulaId"));
				}
			} else if (amount == null || "".equals(amount.trim())) {
				errors.add("amount", new ActionMessage("errors.enter"));
			}
		}
		if (null != methodCalled
				&& methodCalled
						.equalsIgnoreCase(FeesConstants.EDITPREVIEW_METHOD)) {
			if (rate == null && "".equals(amount.trim()))
				errors.add("amount", new ActionMessage("errors.enter"));
			if (amount == null && "".equals(rate.trim()))
				errors.add("RateAndFormula", new ActionMessage(
						"errors.rateAndFormulaId"));
			if (getFeeStatus().getStatusId() == null)
				errors.add("amount", new ActionMessage("errors.select",
						"Status"));
		}

		return errors;
	}

	private void buildPeriodicFeeFrequency() {
		Short recurrencId = feeFrequency.getFeeMeetingFrequency()
				.getMeetingDetails().getRecurrenceType().getRecurrenceId();
		if (recurrencId != null) {
			if (recurrencId.equals(FeesConstants.WEEKLY)
					&& getWeekRecurAfter() != null
					&& !"".equals(getWeekRecurAfter().trim()))
				feeFrequency.getFeeMeetingFrequency().getMeetingDetails()
						.setRecurAfter(Short.valueOf(getWeekRecurAfter()));
			else if (recurrencId.equals(FeesConstants.MONTHLY)
					&& getMonthRecurAfter() != null
					&& !"".equals(getMonthRecurAfter().trim()))
				feeFrequency.getFeeMeetingFrequency().getMeetingDetails()
						.setRecurAfter(Short.valueOf(getMonthRecurAfter()));

		}
	}

	private void buildOneTimeFeeFrequency(boolean isCategoryLoan) {
		if (loanCharge != null && !loanCharge.equals("") && isCategoryLoan)
			feeFrequency.getFeePayment().setFeePaymentId(
					Short.valueOf(loanCharge));
		else if (customerCharge != null && !customerCharge.equals("")
				&& !isCategoryLoan)
			feeFrequency.getFeePayment().setFeePaymentId(
					Short.valueOf(customerCharge));
	}

	private boolean isCategoryLoan() {
		return categoryType.getCategoryId().equals(
				Short.valueOf(FeesConstants.LOAN));
	}
}