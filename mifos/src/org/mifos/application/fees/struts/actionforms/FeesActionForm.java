/**

 * FeesActionForm.java    version: xxx



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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.fees.util.valueobjects.FeeFrequency;
import org.mifos.application.fees.util.valueobjects.FeeLevel;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

public class FeesActionForm extends MifosActionForm {

	private String feeName;

	private String feeAppliesTo;

	private String timeOfCharge;

	private String daily;

	private String monthly;

	private String dailyNoOfDays;

	private String dayWeekly;

	private String sunday;

	private String monday;

	private String tuesday;

	private String wednesday;

	private String thursday;

	private String friday;

	private String saturday;

	private String dayselMonthly;

	private String everyMonthly;

	private String noOfDayMonthly;

	private String dayMonthly;

	private String everytxtMonthly;

	private String amount;

	private String rate;

	private String calculateFeeAs;

	private String percentageOf;

	private String setGlCode;

	private String previewBtn;

	private String cancelBtn;

	private String attr;

	private String status;

	private String frequency;

	private String freqRB;

	private String percentOf;

	private String typeId;

	private String categoryId;

	private String feeId;

	private String globalFeeNum;

	private String officeId;

	private GLCodeEntity glCodeEntity;

	private String rateOrAmount;

	private String rateFlatFalg;

	private String formulaId;

	private FeeFrequency feeFrequency;

	private String createdDate;

	private String createdBy;

	private String updatedDate;

	private String updatedBy;

	private String updatedOfficeId;

	private String versionNo;

	private String recurMonthDay;

	private String recurWeekDay;

	private String loanPaymentId;

	private String otherPaymentId;

	private Set<FeeLevel> feeLevelSet;

	private String adminCheck;

	public String getPercentOf() {
		return percentOf;
	}

	public void setPercentOf(String percentOf) {
		this.percentOf = percentOf;
	}

	public FeesActionForm() {
		this.feeFrequency = new FeeFrequency();
		this.glCodeEntity = new GLCodeEntity();
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCalculateFeeAs() {
		return calculateFeeAs;
	}

	public void setCalculateFeeAs(String calculateFeeAs) {
		this.calculateFeeAs = calculateFeeAs;
	}

	public String getCancelBtn() {
		return cancelBtn;
	}

	public void setCancelBtn(String cancelBtn) {
		this.cancelBtn = cancelBtn;
	}

	public String getDayselMonthly() {
		return dayselMonthly;
	}

	public void setDayselMonthly(String dayselMonthly) {
		this.dayselMonthly = dayselMonthly;
	}

	public String getEverytxtMonthly() {
		return everytxtMonthly;
	}

	public void setEverytxtMonthly(String everytxtMonthly) {
		this.everytxtMonthly = everytxtMonthly;
	}

	public String getPercentageOf() {
		return percentageOf;
	}

	public void setPercentageOf(String percentageOf) {
		this.percentageOf = percentageOf;
	}

	public String getPreviewBtn() {
		return previewBtn;
	}

	public void setPreviewBtn(String previewBtn) {
		this.previewBtn = previewBtn;
	}

	public String getSetGlCode() {
		return setGlCode;
	}

	public void setSetGlCode(String setGlCode) {
		this.setGlCode = setGlCode;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public String getFeeAppliesTo() {
		return feeAppliesTo;
	}

	public void setFeeAppliesTo(String feeAppliesTo) {
		this.feeAppliesTo = feeAppliesTo;
	}

	public String getTimeOfCharge() {
		return timeOfCharge;
	}

	public void setTimeOfCharge(String timeOfCharge) {
		this.timeOfCharge = timeOfCharge;
	}

	public String getDaily() {
		return daily;
	}

	public void setDaily(String daily) {
		this.daily = daily;
	}

	public String getMonthly() {
		return monthly;
	}

	public void setMonthly(String monthly) {
		this.monthly = monthly;
	}

	public String getDailyNoOfDays() {
		return dailyNoOfDays;
	}

	public void setDailyNoOfDays(String dailyNoOfDays) {
		this.dailyNoOfDays = dailyNoOfDays;
	}

	public String getDayWeekly() {
		return dayWeekly;
	}

	public void setDayWeekly(String dayWeekly) {
		this.dayWeekly = dayWeekly;
	}

	public String getSunday() {
		return sunday;
	}

	public void setSunday(String sunday) {
		this.sunday = sunday;
	}

	public String getMonday() {
		return monday;
	}

	public void setMonday(String monday) {
		this.monday = monday;
	}

	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}

	public String getWednesday() {
		return wednesday;
	}

	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}

	public String getThursday() {
		return thursday;
	}

	public void setThursday(String thursday) {
		this.thursday = thursday;
	}

	public String getTuesday() {
		return tuesday;
	}

	public String getFriday() {
		return friday;
	}

	public void setFriday(String friday) {
		this.friday = friday;
	}

	public String getSaturday() {
		return saturday;
	}

	public void setSaturday(String saturday) {
		this.saturday = saturday;
	}

	public String getDayMonthly() {
		return dayMonthly;
	}

	public void setDayMonthly(String dayMonthly) {
		this.dayMonthly = dayMonthly;
	}

	public String getEveryMonthly() {
		return everyMonthly;
	}

	public void setEveryMonthly(String everyMonthly) {
		this.everyMonthly = everyMonthly;
	}

	public String getNoOfDayMonthly() {
		return noOfDayMonthly;
	}

	public void setNoOfDayMonthly(String noOfDayMonthly) {
		this.noOfDayMonthly = noOfDayMonthly;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getstatus() {
		return status;
	}

	public void setstatus(String status) {
		this.status = status;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getfreqRB() {
		return freqRB;
	}

	public void setfreqRB(String freqRB) {
		this.freqRB = freqRB;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryId() {
		return this.categoryId;
	}

	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}

	public String getFeeId() {
		return this.feeId;
	}

	public void setGlobalFeeNum(String globalFeeNum) {
		this.globalFeeNum = globalFeeNum;
	}

	public String getGlobalFeeNum() {
		return this.globalFeeNum;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeId() {
		return this.officeId;
	}

	public void setRateOrAmount(String rateOrAmount) {
		this.rateOrAmount = rateOrAmount;
	}

	public String getRateOrAmount() {
		if (rate != null && !"".equals(rate.trim())) {
			this.rateOrAmount = rate;
		}
		if (amount != null && !"".equals(amount.trim())) {
			this.rateOrAmount = amount;
		}
		return this.rateOrAmount;
	}

	public void setRateFlatFalg(String rateFlatFalg) {
		this.rateFlatFalg = rateFlatFalg;
	}

	public String getRateFlatFalg() {
		if (rate != null && !"".equals(rate.trim())) {
			this.rateFlatFalg = RateAmountFlag.RATE.getValue().toString();
			;
		}
		if (amount != null && !"".equals(amount.trim())) {
			this.rateFlatFalg = RateAmountFlag.AMOUNT.getValue().toString();
		}
		return this.rateFlatFalg;
	}

	public void setFormulaId(String formulaId) {
		this.formulaId = formulaId;
	}

	public String getFormulaId() {
		return this.formulaId;
	}

	public void setFeeFrequency(FeeFrequency feeFrequency) {
		this.feeFrequency = feeFrequency;
	}

	public FeeFrequency getFeeFrequency() {
		if (getTypeId() != null)
			feeFrequency.setFeeFrequencyTypeId(new Short(getTypeId()));
		if (this.feeFrequency.getFeeFrequencyTypeId() != null
				&& this.feeFrequency.getFeeFrequencyTypeId().equals(
						FeeFrequencyType.PERIODIC.getValue())
				&& frequency != null) {
			this.feeFrequency.setFeeMeetingFrequency(getMeeting());
			this.feeFrequency.setFeePaymentId(null);
		} else if (this.feeFrequency.getFeeFrequencyTypeId() != null
				&& this.feeFrequency.getFeeFrequencyTypeId().equals(
						FeeFrequencyType.ONETIME.getValue())) {
			if (categoryId != null
					&& categoryId
							.equals(FeeCategory.LOAN.getValue().toString())
					&& loanPaymentId != null && !loanPaymentId.equals("")) {
				this.feeFrequency.setFeePaymentId(Short.valueOf(loanPaymentId));
			} else if (categoryId != null
					&& !categoryId.equals(FeeCategory.LOAN.getValue()
							.toString()) && otherPaymentId != null
					&& !otherPaymentId.equals("")) {
				this.feeFrequency
						.setFeePaymentId(Short.valueOf(otherPaymentId));
			}
			this.feeFrequency.setFeeMeetingFrequency(null);
		}

		return this.feeFrequency;
	}

	public Meeting getMeeting() {

		Meeting meeting = null;
		if (Short.valueOf(frequency).equals(RecurrenceType.WEEKLY.getValue())) {
			meeting = MeetingHelper.geMeeting(frequency, recurWeekDay,
					MeetingType.FEEMEETING.getValue());
			meeting.setMeetingPlace("");
			recurMonthDay = "";
		} else if (Short.valueOf(frequency).equals(
				RecurrenceType.MONTHLY.getValue())) {
			meeting = MeetingHelper.geMeeting(frequency, recurMonthDay,
					MeetingType.FEEMEETING.getValue());
			meeting.setMeetingPlace("");
			recurWeekDay = "";
		}
		return meeting;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedOfficeId() {
		return updatedOfficeId;
	}

	public void setUpdatedOfficeId(String updatedOfficeId) {
		this.updatedOfficeId = updatedOfficeId;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter("method");
		// Validation for create fee page
		if (null != methodCalled
				&& "preview".equals(methodCalled)
				&& (request.getParameter("input"))
						.equals(FeeConstants.CREATEFEES)
				&& (FeeCategory.LOAN.getValue().toString()).equals(categoryId)) {
			if (("".equals(rate.trim()) && "".equals(amount.trim()))
					|| (!"".equals(rate.trim()) && !"".equals(amount.trim()))) {
				errors.add("RateOrAmount", new ActionMessage(
						"errors.amountOrRate"));
				return errors;
			}
			if ((!formulaId.equals("") && "".equals(rate.trim()))
					|| (!"".equals(rate.trim()) && formulaId.equals("") && ""
							.equals(amount.trim()))) {
				errors.add("RateAndFormula", new ActionMessage(
						"errors.rateAndFormulaId"));
				return errors;
			}
		}
		if (null != methodCalled
				&& "preview".equals(methodCalled)
				&& (request.getParameter("input"))
						.equals(FeeConstants.CREATEFEES)
				&& !FeeCategory.LOAN.getValue().toString().equals(categoryId)) {
			if (amount == null || "".equals(amount.trim())) {
				errors.add("amount", new ActionMessage("errors.enter"));
				return errors;
			}
		}

		// Validation for edit page
		if (null != methodCalled
				&& "preview".equals(methodCalled)
				&& (request.getParameter("input"))
						.equals(FeeConstants.EDITFEEDETAILS)
				&& (RateAmountFlag.AMOUNT.getValue().toString())
						.equals(rateFlatFalg)) {
			if (amount == null || "".equals(amount.trim())) {
				errors.add("amount", new ActionMessage("errors.enter"));
				return errors;
			}
		}

		if (null != methodCalled
				&& "preview".equals(methodCalled)
				&& (request.getParameter("input"))
						.equals(FeeConstants.EDITFEEDETAILS)
				&& (RateAmountFlag.RATE.getValue().toString())
						.equals(rateFlatFalg)) {
			if ((!formulaId.equals("") && "".equals(rate.trim()))
					|| (!"".equals(rate.trim()) && formulaId.equals(""))
					|| ("".equals(rate.trim()) && formulaId.equals(""))) {
				errors.add("RateAndFormula", new ActionMessage(
						"errors.rateAndFormulaId"));
				return errors;
			}
		}

		if (null != methodCalled) {
			if ("load".equals(methodCalled) || "search".equals(methodCalled)
					|| "get".equals(methodCalled)
					|| "manage".equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			}
		}
		return null;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public Map<String, String> getSearchNodeMap() {
		Map<String, String> map = new java.util.HashMap<String, String>();
		map.put("search_name", "Fees");
		return map;

	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
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

	public String getLoanPaymentId() {
		return loanPaymentId;
	}

	public void setLoanPaymentId(String loanPaymentId) {
		this.loanPaymentId = loanPaymentId;
	}

	public String getOtherPaymentId() {
		return otherPaymentId;
	}

	public void setOtherPaymentId(String otherPaymentId) {
		this.otherPaymentId = otherPaymentId;
	}

	public Set<FeeLevel> getFeeLevelSet() {
		Set<FeeLevel> feeLevelSet = null;
		if (null != adminCheck
				&& adminCheck.equalsIgnoreCase(FeeConstants.YES)) {
			if (categoryId.equals(FeeCategory.CLIENT.getValue().toString())) {
				feeLevelSet = new HashSet<FeeLevel>();
				FeeLevel level = new FeeLevel();
				level
						.setLevelId(org.mifos.application.fees.util.helpers.FeeLevel.CLIENTLEVEL
								.getValue());
				feeLevelSet.add(level);
			} else if (categoryId.equals(FeeCategory.GROUP.getValue()
					.toString())) {
				feeLevelSet = new HashSet<FeeLevel>();
				FeeLevel level = new FeeLevel();
				level
						.setLevelId(org.mifos.application.fees.util.helpers.FeeLevel.GROUPLEVEL
								.getValue());
				feeLevelSet.add(level);
			} else if (categoryId.equals(FeeCategory.CENTER.getValue()
					.toString())) {
				feeLevelSet = new HashSet<FeeLevel>();
				FeeLevel level = new FeeLevel();
				level
						.setLevelId(org.mifos.application.fees.util.helpers.FeeLevel.CENTERLEVEL
								.getValue());
				feeLevelSet.add(level);
			} else if (categoryId.equals(FeeCategory.ALLCUSTOMERS.getValue()
					.toString())) {
				feeLevelSet = new HashSet<FeeLevel>();
				FeeLevel level = new FeeLevel();
				level
						.setLevelId(org.mifos.application.fees.util.helpers.FeeLevel.CLIENTLEVEL
								.getValue());
				feeLevelSet.add(level);
				level = new FeeLevel();
				level
						.setLevelId(org.mifos.application.fees.util.helpers.FeeLevel.GROUPLEVEL
								.getValue());
				feeLevelSet.add(level);
				level = new FeeLevel();
				level
						.setLevelId(org.mifos.application.fees.util.helpers.FeeLevel.CENTERLEVEL
								.getValue());
				feeLevelSet.add(level);
			}
		} else {
			return null;
		}
		return feeLevelSet;
	}

	public void setFeeLevelSet(Set<FeeLevel> feeLevelSet) {

		this.feeLevelSet = feeLevelSet;
	}

	public String getAdminCheck() {
		if (null == adminCheck && null != categoryId
				&& !categoryId.equals(FeeCategory.LOAN.getValue().toString())) {
			return FeeConstants.NO;
		}
		return adminCheck;
	}

	public void setAdminCheck(String adminCheck) {
		this.adminCheck = adminCheck;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		if (request.getParameter("method") != null
				&& request.getParameter("method").equals("preview")) {
			adminCheck = null;
		}
	}

	/**
	 * @return Returns the glCodeEntity.
	 */
	public GLCodeEntity getGlCodeEntity() {
		return glCodeEntity;
	}

	/**
	 * @param glCodeEntity
	 *            The glCodeEntity to set.
	 */
	public void setGlCodeEntity(GLCodeEntity glCodeEntity) {
		this.glCodeEntity = glCodeEntity;
	}

}
