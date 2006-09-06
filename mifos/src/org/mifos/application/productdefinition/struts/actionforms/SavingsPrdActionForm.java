package org.mifos.application.productdefinition.struts.actionforms;

import java.sql.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class SavingsPrdActionForm extends BaseActionForm {
	private MifosLogger prdDefLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

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

	public MifosLogger getPrdDefLogger() {
		return prdDefLogger;
	}

	public void setPrdDefLogger(MifosLogger prdDefLogger) {
		this.prdDefLogger = prdDefLogger;
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

	public SavingsType getSavingsTypeValue() {
		try {
			return getSavingsType() != null ? SavingsType
					.getSavingsType(getShortValue(getSavingsType())) : null;
		} catch (PropertyNotFoundException e) {
			return null;
		}
	}

	public Money getRecommendedAmountValue() {
		return getMoney(getRecommendedAmount());
	}

	public Double getInterestRateValue() {
		return getDoubleValue(getInterestRate());
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

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		if (method != null && method.equals(Methods.load.toString())) {
			startDate = DateHelper.getCurrentDate(getUserContext(request)
					.getPereferedLocale());
		}
		if (method != null && method.equals(Methods.preview.toString())) {
			recommendedAmntUnit = null;
		}

	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
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
			if (getSavingsTypeValue() != null
					&& getSavingsTypeValue().equals(SavingsType.MANDATORY)
					&& getRecommendedAmountValue().getAmountDoubleValue() <= 0.0)
				addError(errors, "recommendedAmount",
						ProductDefinitionConstants.ERRORMANDAMOUNT);
			Double intRate = getInterestRateValue();
			if (intRate != null && intRate > 100)
				addError(errors, "interestRate",
						ProductDefinitionConstants.ERRORINTRATE);
		}
		if (method != null && !method.equals(Methods.validate.toString())) {
			request.setAttribute(ProductDefinitionConstants.METHODCALLED,
					method);
		}
		return errors;
	}

}
