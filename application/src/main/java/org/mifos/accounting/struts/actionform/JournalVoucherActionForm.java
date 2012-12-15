/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounting.struts.actionform;

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

public class JournalVoucherActionForm extends BaseActionForm {
	private static final Logger logger = LoggerFactory
			.getLogger(GeneralLedgerActionForm.class);

	private String voucherDate;
	private String voucherId;
	private String trxnType;
	private String debitAccountHead;
	private String officeHierarchy;
	private String office;
	private String creditAccountHead;
	private String amount;
	private String voucherNotes;
	private String officeLevelId;
	
	

	public String getOfficeLevelId() {
		return officeLevelId;
	}

	public void setOfficeLevelId(String officeLevelId) {
		this.officeLevelId = officeLevelId;
	}

	public String getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}

	public String getTrxnType() {
		return trxnType;
	}

	public void setTrxnType(String trxnType) {
		this.trxnType = trxnType;
	}

	public String getDebitAccountHead() {
		return debitAccountHead;
	}

	public void setDebitAccountHead(String debitAccountHead) {
		this.debitAccountHead = debitAccountHead;
	}

	public String getOfficeHierarchy() {
		return officeHierarchy;
	}

	public void setOfficeHierarchy(String officeHierarchy) {
		this.officeHierarchy = officeHierarchy;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getCreditAccountHead() {
		return creditAccountHead;
	}

	public void setCreditAccountHead(String creditAccountHead) {
		this.creditAccountHead = creditAccountHead;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount.replaceAll(",", "");
	}

	public String getVoucherNotes() {
		return voucherNotes;
	}

	public void setVoucherNotes(String voucherNotes) {
		this.voucherNotes = voucherNotes;
	}

	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}

	public void setVoucherDate(java.util.Date date){
		this.voucherDate = DateUtils.format(date);
	}

	public String getVoucherDate() {
		return voucherDate;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("reset method called");
		if (request.getParameter(SimpleAccountingConstants.METHOD).equals(
				"load")) {
			this.voucherDate = null;
			this.office = null;
			this.officeHierarchy = null;
			this.trxnType = null;
			this.debitAccountHead = null;
			this.creditAccountHead = null;
			this.amount = null;
			this.voucherNotes = null;

		}

	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("JournalVoucherActionForm.validate");
		ActionErrors errors = new ActionErrors();

		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.PREVIEW)) {
			return mandatoryCheck(getUserContext(request));
		}
		return errors;
	}

	private ActionErrors voucherDateValidate(ActionErrors errors, Locale locale) {
		if (StringUtils.isNotBlank(getVoucherDate())
				&& !DateUtils.isValidDate(getVoucherDate())) {
			ResourceBundle resources = ResourceBundle.getBundle(
					FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
			String voucherDate = resources
					.getString(SimpleAccountingConstants.VOUCHERDATE);
			errors.add(SimpleAccountingConstants.INVALIDDATE,
					new ActionMessage(SimpleAccountingConstants.INVALIDDATE,
							voucherDate));
		}
		return errors;
	}

	private ActionErrors mandatoryCheck(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

		String voucher_date = resources
				.getString(SimpleAccountingConstants.VOUCHERDATE);
		String office_Hierarchy = resources
				.getString(SimpleAccountingConstants.OFFICE_HIERARCHY);
		String officeId = resources.getString(SimpleAccountingConstants.OFFICE);
		String main_Account = resources
				.getString(SimpleAccountingConstants.DEBIT_ACCOUNT);
		String subAccount = resources
				.getString(SimpleAccountingConstants.CREDIT_ACCOUNT);
		String Amount = resources.getString(SimpleAccountingConstants.AMOUNT);
		String Notes = resources
				.getString(SimpleAccountingConstants.VOUCHER_NOTES);

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

		java.sql.Date voucherDate = null;

		if (getVoucherDate() == null || "".equals(getVoucherDate())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							voucher_date));
		} else if (getVoucherDate() != null && !getVoucherDate().equals("")
				&& !DateUtils.isValidDate(getVoucherDate())) {
			errors = voucherDateValidate(errors, locale);
		} else if (DateUtils.isValidDate(getVoucherDate())) {
			try {
				voucherDate = DateUtils
						.getDateAsSentFromBrowser(getVoucherDate());
			} catch (InvalidDateException ide) {
				errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
						new ActionMessage(
								SimpleAccountingConstants.INVALIDDATE,
								voucher_date));
			}
			if (voucherDate.compareTo(currentDate) > 0) {
				errors.add(SimpleAccountingConstants.INVALID_FUTURE,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_FUTURE,
								voucher_date));
			}

		}

		if (officeHierarchy == null || "".equals(officeHierarchy.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							office_Hierarchy));
		}

		if (office == null || "".equals(office.trim())) {
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

		if (voucherNotes == null || "".equals(voucherNotes.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, Notes));
		}

		return errors;
	}

	protected DoubleConversionResult validateAmount(String amountString,
			MifosCurrency currency, String fieldPropertyKey,
			ActionErrors errors, String installmentNo) {
		String fieldName = fieldPropertyKey;
		DoubleConversionResult conversionResult = parseDoubleForMoney(
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
