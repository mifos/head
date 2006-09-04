/**

 * SavingsProductActionForm.java    version: 1.0

 

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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.PrdApplicableMaster;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
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

/**
 * This class is the Action Form associated with Savings Product Definition. It
 * extends from MifosSearchActionForm
 */
public class SavingsProductActionForm extends MifosSearchActionForm {

	// ----------------------constructors----------------
	public SavingsProductActionForm() {
		super();
		this.prdCategory = new ProductCategory();
		this.prdApplicableMaster = new PrdApplicableMaster();
		this.prdStatus = new PrdStatus();
		this.recommendedAmntUnit = new RecommendedAmntUnit();
		this.savingsType = new SavingsType();
		this.interestCalcType = new InterestCalcType();
		this.timePerForInstcalc = new PrdOfferingMeeting();
		this.freqOfPostIntcalc = new PrdOfferingMeeting();
		this.depositGLCode = new GLCodeEntity();
		this.interestGLCode = new GLCodeEntity();
	}

	// --------------------------instance variables-----------------
	/**
	 * Serail Version UID for Serialization
	 */
	private static final long serialVersionUID = 342342532677561L;

	private MifosLogger prdDefLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	/**
	 * Savings Offering Id
	 */
	private String prdOfferingId;

	/**
	 * Savings Offering Name
	 */
	private String prdOfferingName;

	/**
	 * Savings Offering Short Name
	 */
	private String prdOfferingShortName;

	/**
	 * Savings Offering Description
	 */
	private String description;

	/**
	 * Product Category
	 */
	private ProductCategory prdCategory;

	/**
	 * start Date of Savings Offering
	 */
	private String startDate;

	/**
	 * End Date of Savings Offering
	 */
	private String endDate;

	/**
	 * Savings Offering Applicable Master
	 */
	private PrdApplicableMaster prdApplicableMaster;

	/**
	 * Savings Offering Status Master
	 */
	private PrdStatus prdStatus;

	/**
	 * Recommended Amount
	 */
	private Money recommendedAmount;

	/**
	 * Maximum withdrawl Amount
	 */
	private Money maxAmntWithdrawl;

	/**
	 * Minimum Amount for Interest Calculation
	 */
	private Money minAmntForInt;

	/**
	 * Interest Rate
	 */
	private String interestRate;

	/**
	 * Recommended Amount Unti Master
	 */
	private RecommendedAmntUnit recommendedAmntUnit;

	/**
	 * Deposit Types
	 */
	private SavingsType savingsType;

	/**
	 * Interest Calculation Types
	 */
	private InterestCalcType interestCalcType;

	/**
	 * frequency of interest posting to accounts
	 */
	private String freqOfInterest;

	/**
	 * time for interest calculation
	 */
	private String timeForInterestCacl;

	private String recurTypeFortimeForInterestCacl;

	/**
	 * time for interest calculation
	 */
	private PrdOfferingMeeting timePerForInstcalc;

	/**
	 * frequency of interest posting to accounts set
	 */
	private PrdOfferingMeeting freqOfPostIntcalc;

	private GLCodeEntity depositGLCode;

	private GLCodeEntity interestGLCode;

	/**
	 * @return Returns the freqOfPostIntcalc.
	 */
	public PrdOfferingMeeting getFreqOfPostIntcalc() {
		Meeting meeting = null;
		if (null != freqOfInterest && !"".equals(freqOfInterest.trim())) {
			meeting = MeetingHelper.geMeeting(Short
					.toString(MeetingConstants.MONTH), freqOfInterest,
					MeetingType.SAVINGSFRQINTPOSTACC.getValue());
		}
		this.freqOfPostIntcalc.setMeeting(meeting);
		this.freqOfPostIntcalc
				.setMeetingType(MeetingType.SAVINGSFRQINTPOSTACC.getValue());
		return freqOfPostIntcalc;
	}

	/**
	 * @param freqOfPostIntcalc
	 *            The freqOfPostIntcalc to set.
	 */
	public void setFreqOfPostIntcalc(PrdOfferingMeeting freqOfPostIntcalc) {
		this.freqOfPostIntcalc = freqOfPostIntcalc;
	}

	/**
	 * @return Returns the timePerForInstcalc.
	 */
	public PrdOfferingMeeting getTimePerForInstcalc() {
		Meeting meeting = null;
		if (null != timeForInterestCacl
				&& !"".equals(timeForInterestCacl.trim())) {
			meeting = MeetingHelper.geMeeting(recurTypeFortimeForInterestCacl,
					timeForInterestCacl,
					MeetingType.SAVINGSTIMEPERFORINTCALC.getValue());
		}
		this.timePerForInstcalc.setMeeting(meeting);
		this.timePerForInstcalc
				.setMeetingType(MeetingType.SAVINGSTIMEPERFORINTCALC.getValue());
		return timePerForInstcalc;
	}

	/**
	 * @param timePerForInstcalc
	 *            The timePerForInstcalc to set.
	 */
	public void setTimePerForInstcalc(PrdOfferingMeeting timePerForInstcalc) {
		this.timePerForInstcalc = timePerForInstcalc;
	}

	/**
	 * @return Returns the freqOfInterest.
	 */
	public String getFreqOfInterest() {
		return freqOfInterest;
	}

	/**
	 * @param freqOfInterest
	 *            The freqOfInterest to set.
	 */
	public void setFreqOfInterest(String freqOfInterest) {
		this.freqOfInterest = freqOfInterest;
	}

	/**
	 * @return Returns the timeForInterestCacl.
	 */
	public String getTimeForInterestCacl() {
		return timeForInterestCacl;
	}

	/**
	 * @param timeForInterestCacl
	 *            The timeForInterestCacl to set.
	 */
	public void setTimeForInterestCacl(String timeForInterestCacl) {
		this.timeForInterestCacl = timeForInterestCacl;
	}

	public String getRecurTypeFortimeForInterestCacl() {
		return recurTypeFortimeForInterestCacl;
	}

	public void setRecurTypeFortimeForInterestCacl(
			String recurTypeFortimeForInterestCacl) {
		this.recurTypeFortimeForInterestCacl = recurTypeFortimeForInterestCacl;
	}

	// -------------------------public methods--------------------
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
	 * @return Returns the interestRate.
	 */
	public String getInterestRate() {
		return interestRate;
	}

	/**
	 * @param interestRate
	 *            The interestRate to set.
	 */
	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	/**
	 * @return Returns the maxAmntWithdrawl.
	 */
	public Money getMaxAmntWithdrawl() {
		return maxAmntWithdrawl;
	}

	public double getMaxAmntWithdrawlDoubleValue() {
		return maxAmntWithdrawl.getAmountDoubleValue();
	}

	/**
	 * @param maxAmntWithdrawl
	 *            The maxAmntWithdrawl to set.
	 */
	public void setMaxAmntWithdrawl(Money maxAmntWithdrawl) {
		this.maxAmntWithdrawl = maxAmntWithdrawl;
	}

	/**
	 * @return Returns the minAmntForInt.
	 */
	public Money getMinAmntForInt() {
		return minAmntForInt;
	}

	public double getMinAmntForIntDoubleValue() {
		return minAmntForInt.getAmountDoubleValue();
	}

	/**
	 * @param minAmntForInt
	 *            The minAmntForInt to set.
	 */
	public void setMinAmntForInt(Money minAmntForInt) {
		this.minAmntForInt = minAmntForInt;
	}

	/**
	 * @return Returns the recommendedAmount.
	 */
	public Money getRecommendedAmount() {
		return recommendedAmount;
	}

	public double getRecommendedAmountDoubleValue() {
		return recommendedAmount.getAmountDoubleValue();
	}

	/**
	 * @param recommendedAmount
	 *            The recommendedAmount to set.
	 */
	public void setRecommendedAmount(Money recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	/**
	 * @return Returns the interestCalcType.
	 */
	public InterestCalcType getInterestCalcType() {
		return interestCalcType;
	}

	/**
	 * @param interestCalcType
	 *            The interestCalcType to set.
	 */
	public void setInterestCalcType(InterestCalcType interestCalcType) {
		this.interestCalcType = interestCalcType;
	}

	/**
	 * @return Returns the recommendedAmntUnit.
	 */
	public RecommendedAmntUnit getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	/**
	 * @param recommendedAmntUnit
	 *            The recommendedAmntUnit to set.
	 */
	public void setRecommendedAmntUnit(RecommendedAmntUnit recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	/**
	 * @return Returns the savingsType.
	 */
	public SavingsType getSavingsType() {
		return savingsType;
	}

	/**
	 * @param savingsType
	 *            The savingsType to set.
	 */
	public void setSavingsType(SavingsType savingsType) {
		this.savingsType = savingsType;
	}

	public GLCodeEntity getDepositGLCode() {
		return depositGLCode;
	}

	public void setDepositGLCode(GLCodeEntity depositGLCode) {
		this.depositGLCode = depositGLCode;
	}

	public GLCodeEntity getInterestGLCode() {
		return interestGLCode;
	}

	public void setInterestGLCode(GLCodeEntity interestGLCode) {
		this.interestGLCode = interestGLCode;
	}

	/**
	 * This is the custom method to do any custom validations. This method is
	 * also used to specify the methods where validation is to be skipped.
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
			if (ProductDefinitionConstants.PREVIEWMETHOD.equals(methodCalled)) {
				if (savingsType.getSavingsTypeId() != null
						&& savingsType.getSavingsTypeId().equals(
								org.mifos.application.productdefinition.util.helpers.SavingsType.MANDATORY.getValue())
						&& getRecommendedAmountDoubleValue() <= 0.0)
					errors
							.add(
									ProductDefinitionConstants.ERRORMANDAMOUNT,
									new ActionMessage(
											ProductDefinitionConstants.ERRORMANDAMOUNT));

				if (null != interestRate && !"".equals(interestRate.trim())) {
					double defIntRate = Double.parseDouble(interestRate);
					if (defIntRate < 0 || defIntRate > 100) {
						errors
								.add(
										ProductDefinitionConstants.ERRORINTRATE,
										new ActionMessage(
												ProductDefinitionConstants.ERRORINTRATE));
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
							.setOfferingStatusId(ProductDefinitionConstants.SAVINGSACTIVE);
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

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		if (method != null
				&& method.equals(ProductDefinitionConstants.LOADMETHOD)) {
			startDate = DateHelper.getCurrentDate(getUserLocale(request));
		}
		// Bug id 27423 added the if condition
		if (method != null
				&& method.equals(ProductDefinitionConstants.PREVIEWMETHOD)) {
			recommendedAmntUnit = new RecommendedAmntUnit();
		}
	}
}
