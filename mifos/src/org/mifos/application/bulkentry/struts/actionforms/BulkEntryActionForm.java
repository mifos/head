/**

 * BulkEntryActionForm.java    version: 1.0

 

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

package org.mifos.application.bulkentry.struts.actionforms;

import java.sql.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.bulkentry.util.helpers.BulkEntryDataView;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.SessionUtils;

public class BulkEntryActionForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	private String customerId;

	private String loanOfficerId;

	private String paymentId;

	private String receiptId;

	private String receiptDate;

	private String transactionDate;

	private String officeId;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getLoanOfficerId() {
		return loanOfficerId;
	}

	public void setLoanOfficerId(String loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if (request.getParameter(BulkEntryConstants.METHOD).equalsIgnoreCase(
				BulkEntryConstants.PREVIEWMETHOD)) {
			request.setAttribute(Constants.CURRENTFLOWKEY, request
					.getParameter(Constants.CURRENTFLOWKEY));
			try {
				BulkEntryBO bulkEntry = (BulkEntryBO) SessionUtils
						.getAttribute(BulkEntryConstants.BULKENTRY, request);

				int customers = bulkEntry.getTotalCustomers();
				int loanProductsSize = bulkEntry.getLoanProducts().size();
				int savingsProductSize = bulkEntry.getSavingsProducts().size();
				BulkEntryDataView bulkEntryDataView = new BulkEntryDataView();
				String enteredAmount[][] = new String[customers + 1][loanProductsSize];
				String disBurtialAmount[][] = new String[customers + 1][loanProductsSize];
				String depositAmountEntered[][] = new String[customers + 1][savingsProductSize];
				String withDrawalAmountEntered[][] = new String[customers + 1][savingsProductSize];
				String attendance[] = new String[customers + 1];
				String customerAccountAmountEntered[] = new String[customers + 1];

				for (int rowIndex = 0; rowIndex <= customers; rowIndex++) {
					attendance[rowIndex] = request
							.getParameter("attendenceSelected[" + rowIndex
									+ "]");
					for (int columnIndex = 0; columnIndex < loanProductsSize; columnIndex++) {
						enteredAmount[rowIndex][columnIndex] = request
								.getParameter("enteredAmount[" + rowIndex
										+ "][" + columnIndex + "]");
						disBurtialAmount[rowIndex][columnIndex] = request
								.getParameter("enteredAmount["
										+ rowIndex
										+ "]["
										+ (loanProductsSize
												+ savingsProductSize + columnIndex)
										+ "]");

					}
					for (int columnIndex = 0; columnIndex < savingsProductSize; columnIndex++) {
						depositAmountEntered[rowIndex][columnIndex] = request
								.getParameter("depositAmountEntered["
										+ rowIndex + "]["
										+ (loanProductsSize + columnIndex)
										+ "]");
						withDrawalAmountEntered[rowIndex][columnIndex] = request
								.getParameter("withDrawalAmountEntered["
										+ rowIndex
										+ "]["
										+ ((2 * loanProductsSize)
												+ savingsProductSize + columnIndex)
										+ "]");
					}
					customerAccountAmountEntered[rowIndex] = request
							.getParameter("customerAccountAmountEntered["
									+ rowIndex
									+ "]["
									+ (2 * (loanProductsSize + savingsProductSize))
									+ "]");
				}
				bulkEntryDataView
						.setDisbursementAmountEntered(disBurtialAmount);
				bulkEntryDataView.setLoanAmountEntered(enteredAmount);

				bulkEntryDataView
						.setWithDrawalAmountEntered(withDrawalAmountEntered);
				bulkEntryDataView.setDepositAmountEntered(depositAmountEntered);
				bulkEntryDataView
						.setCustomerAccountAmountEntered(customerAccountAmountEntered);
				bulkEntryDataView.setAttendance(attendance);
				bulkEntry.setBulkEntryDataView(bulkEntryDataView);
			} catch (PageExpiredException e) {

			}
		}
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		ActionErrors errors = new ActionErrors();
		if (request.getParameter(BulkEntryConstants.METHOD).equalsIgnoreCase(
				BulkEntryConstants.PREVIEWMETHOD)) {
			try {
				BulkEntryBO bulkEntry = (BulkEntryBO) SessionUtils
						.getAttribute(BulkEntryConstants.BULKENTRY, request);
				return validatePopulatedData(bulkEntry.getBulkEntryParent(),
						errors);
			} catch (PageExpiredException e) {
				errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION,
						new ActionMessage(
								ExceptionConstants.PAGEEXPIREDEXCEPTION));
			}

		} else if (request.getParameter(BulkEntryConstants.METHOD)
				.equalsIgnoreCase(BulkEntryConstants.GETMETHOD)) {
			Locale userLocale = getUserLocale(request);
			java.sql.Date meetingDate = null;
			try {
				meetingDate = (Date) SessionUtils.getAttribute(
						"LastMeetingDate", request);

			} catch (PageExpiredException e) {
			}
			try {
				short isCenterHeirarchyExists = (Short) SessionUtils
						.getAttribute(
								BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
								request);
				return mandatoryCheck(meetingDate, userLocale,
						isCenterHeirarchyExists);
			} catch (PageExpiredException e) {
				errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION,
						new ActionMessage(
								ExceptionConstants.PAGEEXPIREDEXCEPTION));
			}
		}
		return errors;
	}

	private ActionErrors validatePopulatedData(BulkEntryView parent,
			ActionErrors errors) {
		List<BulkEntryView> children = parent.getBulkEntryChildren();
		if (null != children) {
			for (BulkEntryView bulkEntryView : children) {
				validatePopulatedData(bulkEntryView, errors);
			}
		}
		for (LoanAccountsProductView accountView : parent
				.getLoanAccountDetails()) {
			if (accountView.isDisburseLoanAccountPresent() || accountView.getLoanAccountViews().size() > 1) {
				Double enteredAmount = 0.0;
				if (null != accountView.getEnteredAmount()
						&& accountView.isValidAmountEntered())
					enteredAmount = Double.valueOf(accountView
							.getEnteredAmount());
				Double enteredDisbursalAmount = 0.0;
				if (null != accountView.getDisBursementAmountEntered()
						&& accountView.isValidDisbursementAmount())
					enteredDisbursalAmount = Double.valueOf(accountView
							.getDisBursementAmountEntered());
				Double totalDueAmount = accountView.getTotalAmountDue();
				Double totalDisburtialAmount = accountView
						.getTotalDisburseAmount();
				if (totalDueAmount.doubleValue() <= 0.0
						&& totalDisburtialAmount > 0.0) {
					if (!accountView.isValidDisbursementAmount()
							|| (!enteredDisbursalAmount
									.equals(totalDisburtialAmount) && !enteredDisbursalAmount
									.equals(0.0)))
						errors
								.add(
										BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
										new ActionMessage(
												BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
												accountView
														.getPrdOfferingShortName(),
												parent.getCustomerDetail()
														.getDisplayName()));
				}
				if (totalDisburtialAmount <= 0.0 && totalDueAmount > 0.0) {
					if (!accountView.isValidAmountEntered()
							|| (!enteredAmount.equals(totalDueAmount) && !enteredAmount
									.equals(0.0)))
						errors
								.add(
										BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
										new ActionMessage(
												BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
												accountView
														.getPrdOfferingShortName(),
												parent.getCustomerDetail()
														.getDisplayName()));
				}
				if (totalDueAmount.doubleValue() > 0.0
						&& totalDisburtialAmount > 0.0) {
					if (!accountView.isValidAmountEntered()
							|| !accountView.isValidDisbursementAmount()
							|| (accountView.getEnteredAmount() == null)
							|| (accountView.getDisBursementAmountEntered() == null)
							|| (enteredAmount.equals(0.0) && !enteredDisbursalAmount
									.equals(0.0))
							|| (enteredDisbursalAmount.equals(0.0) && !enteredAmount
									.equals(0.0))
							|| (enteredDisbursalAmount
									.equals(totalDisburtialAmount) && !enteredAmount
									.equals(totalDueAmount))
							|| (enteredAmount.equals(totalDueAmount) && !enteredDisbursalAmount
									.equals(totalDisburtialAmount))
							|| (!enteredAmount.equals(totalDueAmount)
									&& !enteredDisbursalAmount
											.equals(totalDisburtialAmount)
									&& !enteredDisbursalAmount.equals(0.0) && !enteredAmount
									.equals(0.0)))
						errors
								.add(
										BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
										new ActionMessage(
												BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
												accountView
														.getPrdOfferingShortName(),
												parent.getCustomerDetail()
														.getDisplayName()));
				}
				if (totalDisburtialAmount <= 0.0 && totalDueAmount <= 0.0) {
					if (!accountView.isValidAmountEntered()
							|| !accountView.isValidDisbursementAmount()
							|| !enteredDisbursalAmount.equals(0.0) || !enteredAmount
									.equals(0.0))
						errors
								.add(
										BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
										new ActionMessage(
												BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
												accountView
														.getPrdOfferingShortName(),
												parent.getCustomerDetail()
														.getDisplayName()));
				}
			}
		}
		for (SavingsAccountView savingsAccountView : parent
				.getSavingsAccountDetails()) {
			if (!savingsAccountView.isValidDepositAmountEntered()
					|| !savingsAccountView.isValidWithDrawalAmountEntered()) {
				errors.add(BulkEntryConstants.ERRORINVALIDAMOUNT,
						new ActionMessage(
								BulkEntryConstants.ERRORINVALIDAMOUNT,
								savingsAccountView.getSavingsOffering()
										.getPrdOfferingShortName(), parent
										.getCustomerDetail().getDisplayName()));
			}
		}
		CustomerAccountView customerAccountView = parent
				.getCustomerAccountDetails();
		Double customerAccountAmountEntered = 0.0;
		if (null != customerAccountView.getCustomerAccountAmountEntered()
				&& customerAccountView.isValidCustomerAccountAmountEntered())
			customerAccountAmountEntered = Double.valueOf(customerAccountView
					.getCustomerAccountAmountEntered());
		if (!customerAccountView.isValidCustomerAccountAmountEntered()
				|| ((!customerAccountAmountEntered.equals(customerAccountView
						.getTotalAmountDue().getAmountDoubleValue())) && (!customerAccountAmountEntered
						.equals(0.0)))) {
			errors.add(BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
					new ActionMessage(
							BulkEntryConstants.BULKENTRYINVALIDAMOUNT,
							"A/C Collections", parent.getCustomerDetail()
									.getDisplayName()));
		}
		return errors;
	}

	private ActionErrors mandatoryCheck(Date meetingDate, Locale userLocale,
			short isCenterHeirarchyExists) {
		ActionErrors errors = new ActionErrors();
		java.sql.Date currentDate = DateHelper.getLocaleDate(userLocale,
				DateHelper.getCurrentDate(userLocale));
		java.sql.Date trxnDate = null;
		String customerLabel = isCenterHeirarchyExists == Constants.YES ? ConfigurationConstants.CENTER
				: ConfigurationConstants.GROUP;
		if (transactionDate != null && !transactionDate.equals("")) {
			trxnDate = DateHelper.getLocaleDate(userLocale,
					getTransactionDate());
		}
		if (officeId == null || "".equals(officeId.trim())) {
			errors.add(BulkEntryConstants.MANDATORYFIELDS, new ActionMessage(
					BulkEntryConstants.MANDATORYFIELDS, getLabel(
							ConfigurationConstants.BRANCHOFFICE, userLocale)));
		}
		if (loanOfficerId == null || "".equals(loanOfficerId.trim())) {
			errors.add(BulkEntryConstants.MANDATORYFIELDS, new ActionMessage(
					BulkEntryConstants.MANDATORYFIELDS,
					BulkEntryConstants.LOANOFFICERS));
		}
		if (customerId == null || "".equals(customerId.trim())) {
			errors.add(BulkEntryConstants.MANDATORYFIELDS, new ActionMessage(
					BulkEntryConstants.MANDATORYFIELDS, getLabel(customerLabel,
							userLocale)));
		}
		if (transactionDate == null || "".equals(transactionDate.trim())) {
			errors.add(BulkEntryConstants.MANDATORYENTER, new ActionMessage(
					BulkEntryConstants.MANDATORYENTER,
					BulkEntryConstants.DATEOFTRXN));
		}
		if (currentDate != null
				&& meetingDate != null
				&& trxnDate != null
				&& (meetingDate.compareTo(trxnDate) > 0 || trxnDate
						.compareTo(currentDate) > 0)) {
			errors.add(BulkEntryConstants.INVALIDENDDATE, new ActionMessage(
					BulkEntryConstants.INVALIDENDDATE,
					BulkEntryConstants.DATEOFTRXN));
		} else if (meetingDate == null && trxnDate != null
				&& trxnDate.compareTo(currentDate) != 0) {
			errors.add(BulkEntryConstants.MEETINGDATEEXCEPTION,
					new ActionMessage(BulkEntryConstants.MEETINGDATEEXCEPTION,
							BulkEntryConstants.DATEOFTRXN));
		}
		if (paymentId == null || "".equals(paymentId.trim())) {
			errors.add(BulkEntryConstants.MANDATORYFIELDS, new ActionMessage(
					BulkEntryConstants.MANDATORYFIELDS,
					BulkEntryConstants.MODEOFPAYMENT));
		}
		return errors;
	}

	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;

		UserContext userContext = (UserContext) request.getSession()
				.getAttribute(LoginConstants.USERCONTEXT);
		if (null != userContext) {
			locale = userContext.getPereferedLocale();
			if (null == locale) {
				locale = userContext.getMfiLocale();
			}
		}

		return locale;
	}

	private String getLabel(String key, Locale locale) {
		try {
			return MifosConfiguration.getInstance().getLabel(key, locale);
		} catch (ConfigurationException e) {
		}
		return null;
	}

}
