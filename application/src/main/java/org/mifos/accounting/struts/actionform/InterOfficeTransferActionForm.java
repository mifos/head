package org.mifos.accounting.struts.actionform;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterOfficeTransferActionForm extends BaseActionForm {
	private static final Logger logger = LoggerFactory
			.getLogger(InterOfficeTransferActionForm.class);


	private String trxnDate;
	private String trxnId;
	private String debitAccountHead;

	private String fromOfficeHierarchy;
	private String fromOffice;
	private String toOfficeHierarchy;
	private String toOffice;
	private String trxnType;

	private String creditAccountHead;
	private String amount;
	private String notes;
	private String chequeNo;
	private String chequeDateDD;
	private String chequeDateMM;
	private String chequeDateYY;
	private String bankName;
	private String bankBranch;


	public String getTrxnType() {
		return trxnType;
	}

	public void setTrxnType(String trxnType) {
		this.trxnType = trxnType;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getChequeDateDD() {
		return chequeDateDD;
	}

	public void setChequeDateDD(String chequeDateDD) {
		this.chequeDateDD = chequeDateDD;
	}

	public String getChequeDateMM() {
		return chequeDateMM;
	}

	public void setChequeDateMM(String chequeDateMM) {
		this.chequeDateMM = chequeDateMM;
	}

	public String getChequeDateYY() {
		return chequeDateYY;
	}

	public void setChequeDateYY(String chequeDateYY) {
		this.chequeDateYY = chequeDateYY;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getTrxnId() {
		return trxnId;
	}

	public void setTrxnId(String trxnId) {
		this.trxnId = trxnId;
	}




	public String getDebitAccountHead() {
		return debitAccountHead;
	}

	public void setDebitAccountHead(String debitAccountHead) {
		this.debitAccountHead = debitAccountHead;
	}

	public String getCreditAccountHead() {
		return creditAccountHead;
	}

	public void setCreditAccountHead(String creditAccountHead) {
		this.creditAccountHead = creditAccountHead;
	}

	public String getFromOfficeHierarchy() {
		return fromOfficeHierarchy;
	}

	public void setFromOfficeHierarchy(String fromOfficeHierarchy) {
		this.fromOfficeHierarchy = fromOfficeHierarchy;
	}

	public String getFromOffice() {
		return fromOffice;
	}

	public void setFromOffice(String fromOffice) {
		this.fromOffice = fromOffice;
	}

	public String getToOfficeHierarchy() {
		return toOfficeHierarchy;
	}

	public void setToOfficeHierarchy(String toOfficeHierarchy) {
		this.toOfficeHierarchy = toOfficeHierarchy;
	}

	public String getToOffice() {
		return toOffice;
	}

	public void setToOffice(String toOffice) {
		this.toOffice = toOffice;
	}



	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		// this.amount=amount.replaceAll(",", "");
		this.amount = amount;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setTrxnDate(String trxnDate) {
		this.trxnDate = trxnDate;
	}

	public void setTrxnDate(java.util.Date date) {
		this.trxnDate = DateUtils.format(date);
	}

	public String getTrxnDate() {
		return trxnDate;
	}

	//
	public void setChequeDate(String s) {
		setChequeDate(DateUtils.getDate(s));
	}

	public void setChequeDate(java.util.Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// note that Calendar retrieves 0-based month, so increment month field
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		setChequeDateDD(Integer.toString(day));
		setChequeDateMM(Integer.toString(month));
		setChequeDateYY(Integer.toString(year));
	}

	public String getChequeDate() {
		if (StringUtils.isBlank(chequeDateDD)
				|| StringUtils.isBlank(chequeDateMM)
				|| StringUtils.isBlank(chequeDateYY)) {
			return null;
		}
		return chequeDateDD + "/" + chequeDateMM + "/" + chequeDateYY;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("reset method called");
		if (request.getParameter(SimpleAccountingConstants.METHOD).equals(
				"load")) {
			this.trxnDate = null;
			this.fromOffice = null;
			this.fromOfficeHierarchy = null;
			this.toOfficeHierarchy = null;
			this.toOffice = null;
			this.debitAccountHead = null;
			this.creditAccountHead = null;
			this.amount = null;
			this.chequeNo = null;
			this.chequeDateDD = null;
			this.chequeDateMM = null;
			this.chequeDateYY = null;
			this.bankName = null;
			this.bankBranch = null;
			this.notes = null;
		}

	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("InterOfficeTransferForm.validate");
		// request.setAttribute(Constants.CURRENTFLOWKEY,
		// request.getParameter(Constants.CURRENTFLOWKEY));
		ActionErrors errors = new ActionErrors();

		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.PREVIEW)) {
			return mandatoryCheck(getUserContext(request));
		}
		return errors;
	}

	private ActionErrors trxnDateValidate(ActionErrors errors, Locale locale) {
		if (StringUtils.isNotBlank(getTrxnDate())
				&& !DateUtils.isValidDate(getTrxnDate())) {
			ResourceBundle resources = ResourceBundle.getBundle(
					FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
			String trxnDate = resources
					.getString(SimpleAccountingConstants.TRXNDATE);
			errors.add(SimpleAccountingConstants.INVALID_TRXN_DATE,
					new ActionMessage(
							SimpleAccountingConstants.INVALID_TRXN_DATE,
							trxnDate));
		}
		return errors;
	}

	private ActionErrors chequeDateValidate(ActionErrors errors, Locale locale) {
		if (StringUtils.isNotBlank(getChequeDate())
				&& !DateUtils.isValidDate(getChequeDate())) {
			ResourceBundle resources = ResourceBundle.getBundle(
					FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
			String chequeDate = resources
					.getString(SimpleAccountingConstants.CHEQUE_DATE);
			errors.add(SimpleAccountingConstants.INVALIDDATE,
					new ActionMessage(SimpleAccountingConstants.INVALIDDATE,
							chequeDate));
		}
		return errors;
	}

	private ActionErrors mandatoryCheck(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

		String trxn_Date = resources
				.getString(SimpleAccountingConstants.TRXNDATE);
		String trxn_Type = resources
				.getString(SimpleAccountingConstants.TRXNTYPE);
		String office_Hierarchy = resources
				.getString(SimpleAccountingConstants.OFFICE_HIERARCHY);
		String officeId = resources.getString(SimpleAccountingConstants.OFFICE);
		String main_Account = resources
				.getString(SimpleAccountingConstants.MAIN_ACCOUNT);
		String subAccount = resources
				.getString(SimpleAccountingConstants.ACCOUNT_HEAD);
		String Amount = resources.getString(SimpleAccountingConstants.AMOUNT);
		String Notes = resources
				.getString(SimpleAccountingConstants.TRXN_NOTES);

		ActionErrors errors = new ActionErrors();
		java.sql.Date currentDate = null;
		try {
			currentDate = DateUtils.getLocaleDate(
					userContext.getPreferredLocale(),
					DateUtils.getCurrentDate(userContext.getPreferredLocale()));
		} catch (InvalidDateException ide) {
			errors.add(SimpleAccountingConstants.INVALIDDATE,
					new ActionMessage(SimpleAccountingConstants.INVALIDDATE));
		}

		java.sql.Date trxnDate = null;

		if (getTrxnDate() == null || "".equals(getTrxnDate())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							trxn_Date));
		} else if (getTrxnDate() != null && !getTrxnDate().equals("")
				&& !DateUtils.isValidDate(getTrxnDate())) {
			errors = trxnDateValidate(errors, locale);
		} else if (DateUtils.isValidDate(getTrxnDate())) {
			try {
				trxnDate = DateUtils.getDateAsSentFromBrowser(getTrxnDate());
			} catch (InvalidDateException ide) {
				errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_TRXN_DATE,
								trxn_Date));
			}
			if (trxnDate.compareTo(currentDate) > 0) {
				errors.add(SimpleAccountingConstants.INVALID_FUTURE,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_FUTURE,
								trxn_Date));
			}
		}



		// from office

		if (fromOfficeHierarchy == null || "".equals(fromOfficeHierarchy.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							office_Hierarchy));
		}

		if (fromOffice == null || "".equals(fromOffice.trim())) {
			errors.add(
					SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, officeId));
		}

		// to office

		if (toOfficeHierarchy == null || "".equals(toOfficeHierarchy.trim())) {
					errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
							new ActionMessage(
									SimpleAccountingConstants.MANDATORYFIELDS,
									office_Hierarchy));
		}

		if (toOffice == null || "".equals(toOffice.trim())) {
					errors.add(
							SimpleAccountingConstants.MANDATORYFIELDS,
							new ActionMessage(
									SimpleAccountingConstants.MANDATORYFIELDS, officeId));
		}



		if (debitAccountHead == null || "".equals(debitAccountHead.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							main_Account));
		}

		if (creditAccountHead == null || "".equals(creditAccountHead.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							subAccount));
		}

		if (amount == null || "".equals(amount.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, Amount));
		}

		if (StringUtils.isNotBlank(getAmount())) {
			DoubleConversionResult conversionResult = validateAmount(
					getAmount(), Amount, errors);
			if (conversionResult.getErrors().size() == 0
					&& !(conversionResult.getDoubleValue() > 0.0)) {
				addError(
						errors,
						SimpleAccountingConstants.AMOUNT,
						SimpleAccountingConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
						Amount);
			}
		}

		if (notes == null || "".equals(notes.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, Notes));
		}

		if (getChequeDate() != null && !getChequeDate().equals("")
				&& !DateUtils.isValidDate(getChequeDate()))
			errors = chequeDateValidate(errors, locale);
		return errors;
	}

	protected DoubleConversionResult validateAmount(String amountString,
			MifosCurrency currency, String fieldPropertyKey,
			ActionErrors errors, String installmentNo) {
		String fieldName = fieldPropertyKey;
		DoubleConversionResult conversionResult = parseDoubleDecimalForMoney(
				amountString, currency);
		for (ConversionError error : conversionResult.getErrors()) {
			String errorText = error.toLocalizedMessage(currency);
			addError(errors, fieldName, "errors.generic", fieldName, errorText);
		}
		return conversionResult;
	}

	protected DoubleConversionResult validateAmount(String amountString,
			String fieldPropertyKey, ActionErrors errors) {
		return validateAmount(amountString, null, fieldPropertyKey, errors, "");
	}

}
