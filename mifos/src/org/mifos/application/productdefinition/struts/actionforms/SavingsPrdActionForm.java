package org.mifos.application.productdefinition.struts.actionforms;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class SavingsPrdActionForm extends BaseActionForm {
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

	private String savingsType;

	private String recommendedAmount;

	private String recommendedAmntUnit;

	private String maxAmntWithdrawl;

	private String interestRate;

	private String interestCalcType;

	private String timeForInterestCacl;

	private String recurTypeFortimeForInterestCacl;

	private String freqOfInterest;

	private String minAmntForInt;

	private String depositGLCode;

	private String interestGLCode;

	private String status;

	private String input;

	public String getPrdOfferingId() {
		return prdOfferingId;
	}

	public void setPrdOfferingId(String prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}

	public String getDepositGLCode() {
		return depositGLCode;
	}

	public void setDepositGLCode(String depositGLCode) {
		this.depositGLCode = depositGLCode;
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

	public String getFreqOfInterest() {
		return freqOfInterest;
	}

	public void setFreqOfInterest(String freqOfInterest) {
		this.freqOfInterest = freqOfInterest;
	}

	public String getInterestCalcType() {
		return interestCalcType;
	}

	public void setInterestCalcType(String interestCalcType) {
		this.interestCalcType = interestCalcType;
	}

	public String getInterestGLCode() {
		return interestGLCode;
	}

	public void setInterestGLCode(String interestGLCode) {
		this.interestGLCode = interestGLCode;
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public String getMaxAmntWithdrawl() {
		return maxAmntWithdrawl;
	}

	public void setMaxAmntWithdrawl(String maxAmntWithdrawl) {
		this.maxAmntWithdrawl = maxAmntWithdrawl;
	}

	public String getMinAmntForInt() {
		return minAmntForInt;
	}

	public void setMinAmntForInt(String minAmntForInt) {
		this.minAmntForInt = minAmntForInt;
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

	public String getRecommendedAmntUnit() {
		return recommendedAmntUnit;
	}

	public void setRecommendedAmntUnit(String recommendedAmntUnit) {
		this.recommendedAmntUnit = recommendedAmntUnit;
	}

	public String getRecommendedAmount() {
		return recommendedAmount;
	}

	public void setRecommendedAmount(String recommendedAmount) {
		this.recommendedAmount = recommendedAmount;
	}

	public String getRecurTypeFortimeForInterestCacl() {
		return recurTypeFortimeForInterestCacl;
	}

	public void setRecurTypeFortimeForInterestCacl(
			String recurTypeFortimeForInterestCacl) {
		this.recurTypeFortimeForInterestCacl = recurTypeFortimeForInterestCacl;
	}

	public String getSavingsType() {
		return savingsType;
	}

	public void setSavingsType(String savingsType) {
		this.savingsType = savingsType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getTimeForInterestCacl() {
		return timeForInterestCacl;
	}

	public void setTimeForInterestCacl(String timeForInterestCacl) {
		this.timeForInterestCacl = timeForInterestCacl;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public SavingsType getSavingsTypeValue() {
		return getSavingsType() != null ? SavingsType
				.fromInt(getShortValue(getSavingsType())) : null;
	}

	public Money getRecommendedAmountValue() {
		return getMoney(getRecommendedAmount());
	}

	public Double getInterestRateValue() {
		return getDoubleValue(getInterestRate());
	}

	public Date getStartDateValue(Locale locale) {
		return DateUtils.getLocaleDate(locale, getStartDate());
	}

	public Date getEndDateValue(Locale locale) {
		return DateUtils.getLocaleDate(locale, getEndDate());
	}

	public Short getPrdCategoryValue() {
		return getShortValue(getPrdCategory());
	}

	public Short getPrdApplicableMasterValue() {
		return getShortValue(getPrdApplicableMaster());
	}

	public Short getRecommendedAmntUnitValue() {
		return getShortValue(getRecommendedAmntUnit());
	}

	public Short getInterestCalcTypeValue() {
		return getShortValue(getInterestCalcType());
	}

	public Short getRecurTypeFortimeForInterestCaclValue() {
		return getShortValue(getRecurTypeFortimeForInterestCacl());
	}

	public Short getTimeForInterestCalcValue() {
		return getShortValue(getTimeForInterestCacl());
	}

	public Short getFreqOfInterestValue() {
		return getShortValue(getFreqOfInterest());
	}

	public Money getMaxAmntWithdrawlValue() {
		return getMoney(getMaxAmntWithdrawl());
	}

	public Money getMinAmntForIntValue() {
		return getMoney(getMinAmntForInt());
	}

	public Short getDepositGLCodeValue() {
		return getShortValue(getDepositGLCode());
	}

	public Short getInterestGLCodeValue() {
		return getShortValue(getInterestGLCode());
	}

	public String getStatus() {
		return status;
	}

	public Short getStatusValue() {
		return getShortValue(getStatus());
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		prdDefLogger
				.debug("start reset method of Savings Product Action form method ");
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		if (method != null && method.equals(Methods.load.toString())) {
			startDate = DateUtils.getCurrentDate(getUserContext(request)
			.getPreferredLocale());
		}
		if (method != null && method.equals(Methods.preview.toString())) {
			recommendedAmntUnit = null;
		}
		prdDefLogger
				.debug("reset method of Savings Product Action form method called ");
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		prdDefLogger
				.debug("validate method of Savings Product Action form method called :"
						+ method);
		try {
			if (method != null) {

				if (method.equals(Methods.preview.toString())) {
					errors.add(super.validate(mapping, request));
					Date startingDate = getStartDateValue(getUserContext(
							request).getPreferredLocale());
					Date endingDate = getEndDateValue(getUserContext(request)
							.getPreferredLocale());
					if (startingDate != null
							&& ((DateUtils.getDateWithoutTimeStamp(
									startingDate.getTime()).compareTo(
									DateUtils.getCurrentDateWithoutTimeStamp()) < 0) || (DateUtils
									.getDateWithoutTimeStamp(
											startingDate.getTime())
									.compareTo(
											DateUtils
													.getCurrentDateOfNextYearWithOutTimeStamp()) > 0)))
						addError(errors, "startDate",
								ProductDefinitionConstants.INVALIDSTARTDATE);
					if (startingDate != null && endingDate != null
							&& startingDate.compareTo(endingDate) >= 0)
						addError(errors, "endDate",
								ProductDefinitionConstants.INVALIDENDDATE);
					if (getSavingsTypeValue() != null
							&& getSavingsTypeValue().equals(
									SavingsType.MANDATORY)
							&& getRecommendedAmountValue()
									.getAmountDoubleValue() <= 0.0)
						addError(errors, "recommendedAmount",
								ProductDefinitionConstants.ERRORMANDAMOUNT);
					validateInterestRate(errors, request);
					validateInterestGLCode( request,errors);
				} else if (method.equals(Methods.previewManage.toString())) {
					errors.add(super.validate(mapping, request));
					validateMandatoryAmount(errors);
					validateInterestRate(errors, request);
					Date startingDate = getStartDateValue(getUserContext(
							request).getPreferredLocale());
					Date endingDate = getEndDateValue(getUserContext(request)
							.getPreferredLocale());
					SavingsOfferingBO savingsOffering = (SavingsOfferingBO) SessionUtils
							.getAttribute(Constants.BUSINESS_KEY, request);
					validateStartDate(errors, savingsOffering.getStartDate(),
							startingDate);
					validateEndDateAgainstCurrentDate(errors, startingDate,
							savingsOffering.getEndDate(), endingDate);
					validateInterestGLCode( request,errors);
				}
			}
		} catch (ApplicationException ae) {
			errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae
					.getValues()));
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

	private void validateInterestRate(ActionErrors errors,
			HttpServletRequest request) {
		if (StringUtils.isNullOrEmpty(getInterestRate())) {
			addError(errors, "interestRate",
					ProductDefinitionConstants.ERROR_MANDATORY, getLabel(
							ConfigurationConstants.INTEREST, request)
							+ " " + ProductDefinitionConstants.RATE);
		} else {
			Double intRate = getInterestRateValue();
			if (intRate != null && intRate > 100)
				addError(errors, "interestRate",
						ProductDefinitionConstants.ERRORINTRATE, getLabel(
								ConfigurationConstants.INTEREST, request)
								+ " " + ProductDefinitionConstants.RATE);
		}
		if (StringUtils.isNullOrEmpty(getInterestCalcType()))
			addError(
					errors,
					"interestCalcType",
					ProductDefinitionConstants.ERROR_SELECT,
					ProductDefinitionConstants.BALANCE_INTEREST
							+ getLabel(ConfigurationConstants.INTEREST, request)
							+ " " + ProductDefinitionConstants.CALCULATION);
		if (StringUtils.isNullOrEmpty(getTimeForInterestCacl())) {
			addError(
					errors,
					"timeForInterestCacl",
					ProductDefinitionConstants.ERROR_MANDATORY,
					ProductDefinitionConstants.TIME_PERIOD
							+ getLabel(ConfigurationConstants.INTEREST, request)
							+ " " + ProductDefinitionConstants.CALCULATION);
		} else {
			int timePeriod = getTimeForInterestCalcValue();
			if (timePeriod <= 0 || timePeriod > 32767)
				addError(errors, "timeForInterestCacl",
						ProductDefinitionConstants.ERRORINTRATE,
						ProductDefinitionConstants.TIME_PERIOD
								+ getLabel(ConfigurationConstants.INTEREST,
										request) + " "
								+ ProductDefinitionConstants.CALCULATION);
		}
		if (StringUtils.isNullOrEmpty(getFreqOfInterest())) {
			addError(
					errors,
					"freqOfInterest",
					ProductDefinitionConstants.ERROR_MANDATORY,
					ProductDefinitionConstants.FREQUENCY
							+ getLabel(ConfigurationConstants.INTEREST, request)
							+ " " + ProductDefinitionConstants.POSTING_ACCOUNTS);
		} else {
			int frequency = getFreqOfInterestValue();
			if (frequency <= 0 || frequency > 32767)
				addError(errors, "freqOfInterest",
						ProductDefinitionConstants.ERRORINTRATE,
						ProductDefinitionConstants.FREQUENCY
								+ getLabel(ConfigurationConstants.INTEREST,
										request) + " "
								+ ProductDefinitionConstants.POSTING_ACCOUNTS);
		}

	}

	private void validateMandatoryAmount(ActionErrors errors) {
		if (getSavingsTypeValue() != null
				&& getSavingsTypeValue().equals(SavingsType.MANDATORY)
				&& getRecommendedAmountValue().getAmountDoubleValue() <= 0.0)
			addError(errors, "recommendedAmount",
					ProductDefinitionConstants.ERRORMANDAMOUNT);

	}

	private void validateStartDate(ActionErrors errors,
			java.util.Date oldStartDate, Date changedStartDate)
			throws ProductDefinitionException {

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

		} else if (changedStartDate != null
				&& DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime())
						.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
			validateStartDateAgainstCurrentDate(errors, changedStartDate);
			validateStartDateAgainstNextYearDate(errors, changedStartDate);
		}

	}

	private void validateStartDateAgainstNextYearDate(ActionErrors errors,
			Date changedStartDate) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		Calendar nextCalendar = new GregorianCalendar(year + 1, month, day);
		Date nextYearDate = new Date(nextCalendar.getTimeInMillis());
		if (DateUtils.getDateWithoutTimeStamp(changedStartDate.getTime())
				.compareTo(nextYearDate) > 0) {
			addError(errors, "startDate",
					ProductDefinitionConstants.INVALIDSTARTDATE);
		}

	}

	private void validateStartDateAgainstCurrentDate(ActionErrors errors,
			Date startDate) throws ProductDefinitionException {
		if (DateUtils.getDateWithoutTimeStamp(startDate.getTime()).compareTo(
				DateUtils.getCurrentDateWithoutTimeStamp()) < 0) {
			addError(errors, "startDate",
					ProductDefinitionConstants.INVALIDSTARTDATE);
		}
	}

	private void validateEndDateAgainstCurrentDate(ActionErrors errors,
			Date startDate, java.util.Date oldEndDate, Date endDate)
			throws ProductDefinitionException {
		if ((oldEndDate == null && endDate != null)
				|| (oldEndDate != null && endDate != null && DateUtils
						.getDateWithoutTimeStamp(oldEndDate.getTime())
						.compareTo(
								DateUtils.getDateWithoutTimeStamp(endDate
										.getTime())) != 0)) {
			if (endDate != null
					&& (DateUtils.getDateWithoutTimeStamp(endDate.getTime())
							.compareTo(
									DateUtils.getCurrentDateWithoutTimeStamp()) < 0 || DateUtils
							.getDateWithoutTimeStamp(startDate.getTime())
							.compareTo(
									DateUtils.getDateWithoutTimeStamp(endDate
											.getTime())) >= 0)) {
				addError(errors, "endDate",
						ProductDefinitionConstants.INVALIDENDDATE);
			}
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

	public void clear() {
		this.prdOfferingId = null;
		this.prdOfferingName = null;
		this.prdOfferingShortName = null;
		this.description = null;
		this.prdCategory = null;
		this.startDate = null;
		this.endDate = null;
		this.prdApplicableMaster = null;
		this.savingsType = null;
		this.recommendedAmount = null;
		this.recommendedAmntUnit = null;
		this.maxAmntWithdrawl = null;
		this.interestRate = null;
		this.interestCalcType = null;
		this.timeForInterestCacl = null;
		this.recurTypeFortimeForInterestCacl = null;
		this.freqOfInterest = null;
		this.minAmntForInt = null;
		this.depositGLCode = null;
		this.interestGLCode = null;
		this.status = null;

	}

}
