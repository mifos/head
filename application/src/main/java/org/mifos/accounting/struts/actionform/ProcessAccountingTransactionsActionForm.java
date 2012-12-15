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
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessAccountingTransactionsActionForm extends BaseActionForm {
	private static final Logger logger = LoggerFactory
			.getLogger(GeneralLedgerActionForm.class);

	private String processTillDate;
	private String groupBy;
	private String lastProcessDateDD;
	private String lastProcessDateMM;
	private String lastProcessDateYY;
	private String officeLevelId;
	private String office;
	private String officeHierarchy;
	

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

	public String getOfficeLevelId() {
		return officeLevelId;
	}

	public void setOfficeLevelId(String officeLevelId) {
		this.officeLevelId = officeLevelId;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getLastProcessDateDD() {
		return lastProcessDateDD;
	}

	public void setLastProcessDateDD(String lastProcessDateDD) {
		this.lastProcessDateDD = lastProcessDateDD;
	}

	public String getLastProcessDateMM() {
		return lastProcessDateMM;
	}

	public void setLastProcessDateMM(String lastProcessDateMM) {
		this.lastProcessDateMM = lastProcessDateMM;
	}

	public String getLastProcessDateYY() {
		return lastProcessDateYY;
	}

	public void setLastProcessDateYY(String lastProcessDateYY) {
		this.lastProcessDateYY = lastProcessDateYY;
	}

	public void setLastProcessDate(String s) {
		setLastProcessDate(DateUtils.getDate(s));
	}

	public void setLastProcessDate(java.util.Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// note that Calendar retrieves 0-based month, so increment month field
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		setLastProcessDateDD(Integer.toString(day));
		setLastProcessDateMM(Integer.toString(month));
		setLastProcessDateYY(Integer.toString(year));
	}

	public String getLastProcessDate() {
		if (StringUtils.isBlank(lastProcessDateDD)
				|| StringUtils.isBlank(lastProcessDateMM)
				|| StringUtils.isBlank(lastProcessDateYY)) {
			return null;
		}
		return lastProcessDateDD + "/" + lastProcessDateMM + "/"
				+ lastProcessDateYY;
	}

	public void setProcessTillDate(String processTillDate) {
		this.processTillDate = processTillDate;
	}

	public void setProcessTillDate(java.util.Date date) {
		this.processTillDate = DateUtils.format(date);
	}

	public String getProcessTillDate() {
		return processTillDate;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("reset method called");
		if (request.getParameter(SimpleAccountingConstants.METHOD).equals(
				"load") || request.getParameter(SimpleAccountingConstants.METHOD).equalsIgnoreCase("loadLastUpdatedDate")) {
			
			this.processTillDate = null;
			this.groupBy = null;
			this.office = null;
			this.officeLevelId = null;
			this.lastProcessDateDD = null;
			this.lastProcessDateMM = null;
			this.lastProcessDateYY = null;
		}

	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("ProcessAccountingTransactionActionForm.validate");
		ActionErrors errors = new ActionErrors();
		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.PROCESS)) {
			return mandatoryCheck(getUserContext(request));
		}
		return errors;
	}

	private ActionErrors processTillDateValidate(ActionErrors errors,
			Locale locale) {
		if (StringUtils.isNotBlank(getProcessTillDate())
				&& !DateUtils.isValidDate(getProcessTillDate())) {
			ResourceBundle resources = ResourceBundle.getBundle(
					FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
			String processTillDate = resources
					.getString(SimpleAccountingConstants.PROCESSTILLDATE);
			errors.add(SimpleAccountingConstants.INVALIDDATE,
					new ActionMessage(SimpleAccountingConstants.INVALIDDATE,
							processTillDate));
		}
		return errors;
	}

	private ActionErrors lastProcessDateValidate(ActionErrors errors,
			Locale locale) {
		if (StringUtils.isNotBlank(getLastProcessDate())
				&& !DateUtils.isValidDate(getLastProcessDate())) {
			ResourceBundle resources = ResourceBundle.getBundle(
					FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
			String lastProcessDate = resources
					.getString(SimpleAccountingConstants.LASTPROCESSDATE);
			errors.add(SimpleAccountingConstants.INVALIDDATE,
					new ActionMessage(SimpleAccountingConstants.INVALIDDATE,
							lastProcessDate));
		}
		return errors;
	}

	private ActionErrors mandatoryCheck(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

		String last_process_date = resources
				.getString(SimpleAccountingConstants.LASTPROCESSDATE);
		String process_till_date = resources
				.getString(SimpleAccountingConstants.PROCESSTILLDATE);
		String groupBy = resources.getString(SimpleAccountingConstants.GROUPBY);

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

		if (getProcessTillDate() == null || "".equals(getProcessTillDate())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							process_till_date));
		} else if (getProcessTillDate() != null
				&& !getProcessTillDate().equals("")
				&& !DateUtils.isValidDate(getProcessTillDate())) {
			errors = processTillDateValidate(errors, locale);
		}

		else if (DateUtils.isValidDate(getProcessTillDate())) {
			try {
				trxnDate = DateUtils
						.getDateAsSentFromBrowser(getProcessTillDate());
			} catch (InvalidDateException ide) {
				errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_TRXN_DATE,
								process_till_date));
			}
			if (trxnDate.compareTo(currentDate) > 0) {
				errors.add(SimpleAccountingConstants.INVALID_FUTURE,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_FUTURE,
								process_till_date));
			}

		}

		else if (DateUtils.isValidDate(getProcessTillDate())) {
			try {
				trxnDate = DateUtils
						.getDateAsSentFromBrowser(getProcessTillDate());
			} catch (InvalidDateException ide) {
				errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_TRXN_DATE,
								process_till_date));
			}
			if (trxnDate.compareTo(currentDate) < 0
					&& trxnDate.compareTo(DateUtils
							.getDate(getLastProcessDate())) < 0) {
				errors.add(SimpleAccountingConstants.INVALID_PAST,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_PAST,
								process_till_date, last_process_date));
			}

		}

		if (groupBy == null || "".equals(groupBy.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, groupBy));
		}

		if (getLastProcessDate() == null) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							last_process_date));
		}

		if (getLastProcessDate() != null && !getLastProcessDate().equals("")
				&& !DateUtils.isValidDate(getLastProcessDate()))
			errors = lastProcessDateValidate(errors, locale);
		return errors;
	}
	
	
	
}
