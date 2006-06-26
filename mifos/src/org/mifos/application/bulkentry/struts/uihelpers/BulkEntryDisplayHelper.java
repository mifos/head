/**

 * BulkEntryDisplayHelper.java    version: 1.0

 

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

package org.mifos.application.bulkentry.struts.uihelpers;

import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;

public class BulkEntryDisplayHelper {

	private int columnIndex;

	public StringBuilder buildTableHeadings(List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts) {
		StringBuilder builder = buildStartTable(loanProducts.size()
				+ savingsProducts.size());
		buildProductsHeading(loanProducts, savingsProducts, builder);
		return builder;
	}

	private StringBuilder buildStartTable(int totalProductSize) {
		StringBuilder builder = new StringBuilder();
		builder
				.append("<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\">");
		builder.append("<tr class=\"fontnormalbold\">");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		builder.append("<td height=\"30\">&nbsp;&nbsp;</td>");
		builder.append("<td align=\"center\" colspan=\"" + totalProductSize
				+ "\">Due/Collections</td>");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		builder.append("<td align=\"center\" colspan=\"" + totalProductSize
				+ "\">Issues/Withdrawls</td>");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
		return builder;
	}

	private void buildProductsHeading(List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts, StringBuilder builder) {
		BulkEntryTagUIHelper.getInstance().generateStartTR(builder,
				"fontnormal8ptbold");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
				"Client Name", false);
		builder.append("<td height=\"30\">&nbsp;&nbsp;</td>");
		buildProductNames(loanProducts, savingsProducts, builder);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		buildProductNames(loanProducts, savingsProducts, builder);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 14,
				"A/C Collections");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 14, "Attn");
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
	}

	private void buildProductNames(List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts, StringBuilder builder) {
		for (PrdOfferingBO prdOffering : loanProducts) {
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 4,
					prdOffering.getPrdOfferingShortName());
		}
		for (PrdOfferingBO prdOffering : savingsProducts) {
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 4,
					prdOffering.getPrdOfferingShortName());
		}
	}

	public StringBuilder getEndTable(int columns) {
		StringBuilder builder = new StringBuilder();
		BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
		for (int i = 0; i < columns; i++) {
			BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
		}
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
		builder.append("</table>");
		return builder;
	}

	public Double[] buildForGroup(BulkEntryView parent,
			List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts,
			List<LookUpMaster> custAttTypes, StringBuilder builder,
			String method, Locale locale, Short officeId) throws JspException {
		int rowIndex = 0;
		int totalProductsSize = (2 * (loanProducts.size() + savingsProducts
				.size()));
		Double[] centerTotals = new Double[(totalProductsSize + 1)];
		Double[] groupTotals = new Double[(totalProductsSize + 1)];
		MifosCurrency currency = parent.getCurrency();
		for (BulkEntryView child : parent.getBulkEntryChildren()) {
			buildCompleteRow(child, loanProducts, savingsProducts, parent
					.getBulkEntryChildren().size(), 0, rowIndex, groupTotals,
					centerTotals, child.getCustomerDetail().getDisplayName(),
					builder, method, 1, currency, officeId);
			generateAttendence(builder, custAttTypes, rowIndex, child, method);
			BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
			rowIndex++;
		}
		buildCompleteRow(parent, loanProducts, savingsProducts, parent
				.getBulkEntryChildren().size(), 0, rowIndex, groupTotals,
				centerTotals, getLocaleLabel(ConfigurationConstants.GROUP,
						locale)
						+ " account", builder, method, 2, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
		rowIndex++;
		getTotalForRow(builder, parent, totalProductsSize, groupTotals,
				rowIndex, method, locale, loanProducts.size(), savingsProducts
						.size());
		return groupTotals;
	}

	public Double[] buildForCenter(BulkEntryView parent,
			List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts,
			List<LookUpMaster> custAttTypes, StringBuilder builder,
			String method, Locale locale, Short officeId) throws JspException {

		int rowIndex = 0;
		int totalProductsSize = (2 * (loanProducts.size() + savingsProducts
				.size()));
		Double[] centerTotals = new Double[(totalProductsSize + 1)];
		Double[] groupTotals = new Double[(totalProductsSize + 1)];
		MifosCurrency currency = parent.getCurrency();
		List<BulkEntryView> children = parent.getBulkEntryChildren();

		for (BulkEntryView child : children) {
			groupTotals = new Double[(totalProductsSize + 1)];
			int groupInitialAccNum = rowIndex;
			List<BulkEntryView> subChildren = child.getBulkEntryChildren();
			int groupChildSize = subChildren.size();
			buildGroupName(builder, child.getCustomerDetail().getDisplayName(),
					totalProductsSize + 1);
			for (BulkEntryView subChild : subChildren) {
				buildCompleteRow(subChild, loanProducts, savingsProducts,
						groupChildSize, groupInitialAccNum, rowIndex,
						groupTotals, centerTotals, subChild.getCustomerDetail()
								.getDisplayName(), builder, method, 1,
						currency, officeId);
				generateAttendence(builder, custAttTypes, rowIndex, subChild,
						method);
				BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
				rowIndex++;
			}
			buildCompleteRow(child, loanProducts, savingsProducts,
					groupChildSize, groupInitialAccNum, rowIndex, groupTotals,
					centerTotals, getLocaleLabel(ConfigurationConstants.GROUP,
							locale)
							+ " account", builder, method, 2, currency,
					officeId);
			BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
			BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
			rowIndex++;
			getTotalForRow(builder, child, totalProductsSize, groupTotals,
					rowIndex, method, locale, loanProducts.size(),
					savingsProducts.size());

		}
		buildCompleteRow(parent, loanProducts, savingsProducts, 0, 0, rowIndex,
				groupTotals, centerTotals, getLocaleLabel(
						ConfigurationConstants.CENTER, locale)
						+ " account", builder, method, 3, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
		getTotalForRow(builder, parent, totalProductsSize, centerTotals,
				rowIndex, method, locale, loanProducts.size(), savingsProducts
						.size());
		return centerTotals;
	}

	private void buildCompleteRow(BulkEntryView bulkEntryView,
			List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts, int groupChildSize,
			int groupInitialAccNum, int rowIndex, Double[] groupTotals,
			Double[] centerTotals, String customerName, StringBuilder builder,
			String method, int levelId, MifosCurrency currency, Short officeId)
			throws JspException {
		List<LoanAccountsProductView> bulkEntryLoanAccounts = bulkEntryView
				.getLoanAccountDetails();
		List<SavingsAccountView> bulkEntrySavingsAccounts = bulkEntryView
				.getSavingsAccountDetails();

		columnIndex = 0;
		generateStartRow(builder, customerName, bulkEntryView);
		getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex,
				groupTotals, centerTotals, bulkEntryView, groupChildSize,
				groupInitialAccNum, savingsProducts.size(), method, true,
				officeId);
		getDepositSavingsRow(builder, bulkEntrySavingsAccounts,
				savingsProducts, rowIndex, groupTotals, centerTotals,
				bulkEntryView, groupChildSize, groupInitialAccNum, loanProducts
						.size(), method, levelId, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex,
				groupTotals, centerTotals, bulkEntryView, groupChildSize,
				groupInitialAccNum, savingsProducts.size(), method, false,
				officeId);
		getWithdrawalSavingsRow(builder, bulkEntrySavingsAccounts,
				savingsProducts, rowIndex, groupTotals, centerTotals,
				bulkEntryView, groupChildSize, groupInitialAccNum, loanProducts
						.size(), method, levelId, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		buildCustomerAccount(bulkEntryView.getCustomerAccountDetails(),
				builder, method, currency, rowIndex, groupTotals, centerTotals,
				groupChildSize, groupInitialAccNum, loanProducts.size(),
				savingsProducts.size(), levelId, officeId);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
	}

	private void buildGroupName(StringBuilder builder, String groupName,
			int totalProductsSize) {
		BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
		builder
				.append(" <td class=\"drawtablerow\"><span class=\"fontnormal8ptbold\">"
						+ groupName + "</span></td>");
		for (int count = 0; count < (totalProductsSize + 8); count++) {
			builder
					.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;</td>");
		}
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
	}

	private void generateStartRow(StringBuilder builder, String customerName,
			BulkEntryView bulkEntryView) {
		BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, customerName);
		builder
				.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");
	}

	private void getLoanRow(StringBuilder builder,
			List<LoanAccountsProductView> bulkEntryAccountList,
			List<PrdOfferingBO> loanProducts, int rows, Double[] groupTotals,
			Double[] centerTotals, BulkEntryView bulkEntryView, int size,
			int initialAccNo, int savingsProductSize, String method,
			boolean isShowingDue, Short officeId) throws JspException {
		for (PrdOfferingBO prdOffering : loanProducts) {
			boolean isIdMatched = false;
			builder.append("<td class=\"drawtablerow\">");
			for (LoanAccountsProductView accountViewBO : bulkEntryAccountList) {
				isIdMatched = prdOffering.getPrdOfferingId().equals(
						accountViewBO.getPrdOfferingId());
				if (isIdMatched) {

					generateLoanValues(builder, rows, columnIndex,
							accountViewBO, groupTotals, centerTotals, size,
							initialAccNo, loanProducts.size(),
							savingsProductSize, method, isShowingDue, officeId);
					break;
				}
			}
			if (!isIdMatched) {
				builder.append("&nbsp;");
			}
			builder.append("</td>");
			columnIndex++;
		}
	}

	private void getDepositSavingsRow(StringBuilder builder,
			List<SavingsAccountView> bulkEntryAccountList,
			List<PrdOfferingBO> savingsProducts, int rows,
			Double[] groupTotals, Double[] centerTotals,
			BulkEntryView bulkEntryView, int size, int initialAccNo,
			int loanProductsSize, String method, int levelId,
			MifosCurrency currency, Short officeId) throws JspException {
		for (PrdOfferingBO prdOffering : savingsProducts) {
			boolean isIdMatched = false;
			builder.append("<td class=\"drawtablerow\">");
			for (SavingsAccountView accountView : bulkEntryAccountList) {
				if (levelId == 1) {
					isIdMatched = prdOffering.getPrdOfferingId()
							.equals(
									accountView.getSavingsOffering()
											.getPrdOfferingId())
							&& (null == accountView.getSavingsOffering()
									.getRecommendedAmntUnit() || accountView
									.getSavingsOffering()
									.getRecommendedAmntUnit()
									.getRecommendedAmntUnitId().equals(
											Short.valueOf("1")));
				} else {
					isIdMatched = prdOffering.getPrdOfferingId()
							.equals(
									accountView.getSavingsOffering()
											.getPrdOfferingId());
				}
				if (isIdMatched) {
					generateSavingsValues(builder, rows, columnIndex,
							accountView, groupTotals, centerTotals, size,
							initialAccNo, method, true, columnIndex,
							loanProductsSize, savingsProducts.size(), levelId,
							currency, officeId);
					break;
				}
			}
			if (!isIdMatched) {
				builder.append("&nbsp;");
			}
			builder.append("</td>");
			columnIndex++;
		}

	}

	private void getWithdrawalSavingsRow(StringBuilder builder,
			List<SavingsAccountView> bulkEntryAccountList,
			List<PrdOfferingBO> savingsProducts, int rows,
			Double[] groupTotals, Double[] centerTotals,
			BulkEntryView bulkEntryView, int size, int initialAccNo,
			int loanProductsSize, String method, int levelId,
			MifosCurrency currency, Short officeId) throws JspException {
		for (PrdOfferingBO prdOffering : savingsProducts) {
			boolean isIdMatched = false;
			builder.append("<td class=\"drawtablerow\">");
			for (SavingsAccountView accountView : bulkEntryAccountList) {
				isIdMatched = prdOffering.getPrdOfferingId().equals(
						accountView.getSavingsOffering().getPrdOfferingId());
				if (isIdMatched) {
					generateSavingsValues(builder, rows, columnIndex,
							accountView, groupTotals, centerTotals, size,
							initialAccNo, method, false, columnIndex,
							loanProductsSize, savingsProducts.size(), levelId,
							currency, officeId);
					break;
				}
			}
			if (!isIdMatched) {
				builder.append("&nbsp;");
			}
			builder.append("</td>");
			columnIndex++;
		}
	}

	private void generateAttendence(StringBuilder builder,
			List<LookUpMaster> custAttTypes, int rows,
			BulkEntryView bulkEntryView, String method) {
		if (method.equals(BulkEntryConstants.GETMETHOD)) {
			builder.append("<td class=\"drawtablerow\">");
			builder.append("<select name=\"attendenceSelected[" + rows
					+ "]\"  style=\"width:40px;\" class=\"fontnormal8pt\">");
			for (LookUpMaster attendence : custAttTypes) {
				builder.append("<option value=\"" + attendence.getId() + "\">"
						+ attendence.getLookUpValue() + "</option>");
			}
			builder.append("</select>");
			builder.append("</td>");
		} else if (method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			builder.append("<td class=\"drawtablerow\">");
			for (LookUpMaster attendence : custAttTypes) {

				if (null != bulkEntryView.getAttendence()
						&& attendence.getId().equals(
								Integer.valueOf(bulkEntryView.getAttendence()))) {
					if (!bulkEntryView.getAttendence().equals("1")) {
						builder.append("<font color=\"#FF0000\">"
								+ attendence.getLookUpValue() + "</font>");
					} else {
						builder.append(attendence.getLookUpValue());
					}
				}
			}
			builder.append("</td>");

		} else if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
				|| method.equals(BulkEntryConstants.VALIDATEMETHOD)) {
			builder.append("<td class=\"drawtablerow\">");
			builder.append("<select name=\"attendenceSelected[" + rows
					+ "]\"  style=\"width:40px;\" class=\"fontnormal8pt\">");
			for (LookUpMaster attendence : custAttTypes) {
				builder.append("<option value=\"" + attendence.getId() + "\"");
				if (null != bulkEntryView.getAttendence()
						&& attendence.getId().equals(
								Integer.valueOf(bulkEntryView.getAttendence()))) {
					builder.append(" selected ");
				}
				builder.append(">" + attendence.getLookUpValue() + "</option>");
			}
			builder.append("</select>");
			builder.append("</td>");
		}
	}

	private void generateLoanValues(StringBuilder builder, int rows,
			int columns, LoanAccountsProductView accountViewBO,
			Double[] groupTotals, Double[] centerTotals, int size,
			int initialAccNo, int loanproductSize, int savingsProductSize,
			String method, boolean isShowingDue, Short officeId)
			throws JspException {
		Double amountToBeShown = 0.0;
		if (isShowingDue) {
			amountToBeShown = accountViewBO.getTotalAmountDue();
			if (amountToBeShown.doubleValue() <= 0.0
					&& (accountViewBO.isDisburseLoanAccountPresent())) {
				builder.append("&nbsp;");
				return;
			}
		} else {
			amountToBeShown = accountViewBO.getTotalDisburseAmount();
			if (amountToBeShown.doubleValue() <= 0.0) {
				builder.append("&nbsp;");
				return;
			}
		}
		if (method.equals(BulkEntryConstants.GETMETHOD)) {
			if (Configuration.getInstance().getCustomerConfig(officeId)
					.isCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance().generateTextInput(
						builder,
						"enteredAmount[" + rows + "][" + columns + "]",
						amountToBeShown, rows, columns, size, initialAccNo,
						columns, loanproductSize, savingsProductSize);
			} else {
				BulkEntryTagUIHelper.getInstance().generateTextInput(
						builder,
						"enteredAmount[" + rows + "][" + columns + "]",
						amountToBeShown, columns, size, loanproductSize,
						savingsProductSize);
			}
		
			Double totalAmount = amountToBeShown;
			groupTotals[columns] = groupTotals[columns] == null ? 0.0 + totalAmount
					: groupTotals[columns] + totalAmount;
			centerTotals[columns] = centerTotals[columns] == null ? 0.0 + totalAmount
					: centerTotals[columns] + totalAmount;
		} else if (method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			Double totalAmount = 0.0;
			String enteredAmount = "";
			if (isShowingDue) {
				enteredAmount = accountViewBO.getEnteredAmount();
				if (accountViewBO.isValidAmountEntered()) {
					totalAmount = Double.valueOf(enteredAmount);
				}
			} else {
				enteredAmount = accountViewBO.getDisBursementAmountEntered();
				if (accountViewBO.isValidDisbursementAmount()) {
					totalAmount = Double.valueOf(enteredAmount);
				}
			}
			if (totalAmount.equals(0.0) && !amountToBeShown.equals(0.0)) {
				builder.append("<font color=\"#FF0000\">" + enteredAmount
						+ "</font>");
			} else {
				builder.append(totalAmount);
			}
			groupTotals[columns] = groupTotals[columns] == null ? 0.0 + totalAmount
					: groupTotals[columns] + totalAmount;
			centerTotals[columns] = centerTotals[columns] == null ? 0.0 + totalAmount
					: centerTotals[columns] + totalAmount;
		} else if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
				|| method.equals(BulkEntryConstants.VALIDATEMETHOD)) {

			String enteredAmount = "";
			if (isShowingDue)
				enteredAmount = accountViewBO.getEnteredAmount();
			else
				enteredAmount = accountViewBO.getDisBursementAmountEntered();

		
			if (Configuration.getInstance().getCustomerConfig(officeId)
					.isCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance().generateTextInput(
						builder,
						"enteredAmount[" + rows + "][" + columns + "]",
						enteredAmount, rows, columns, size, initialAccNo,
						columns, loanproductSize, savingsProductSize);
			} else {
				BulkEntryTagUIHelper.getInstance().generateTextInput(
						builder,
						"enteredAmount[" + rows + "][" + columns + "]",
						enteredAmount, columns, size, loanproductSize,
						savingsProductSize);
			}
		
			Double totalAmount = 0.0;
			boolean isValidAmountEntered;
			if (isShowingDue)
				isValidAmountEntered = accountViewBO.isValidAmountEntered();
			else
				isValidAmountEntered = accountViewBO
						.isValidDisbursementAmount();
			if (isValidAmountEntered) {
				if (isShowingDue)
					totalAmount = Double.valueOf(accountViewBO
							.getEnteredAmount());
				else
					totalAmount = Double.valueOf(accountViewBO
							.getDisBursementAmountEntered());
			}
			groupTotals[columns] = groupTotals[columns] == null ? 0.0 + totalAmount
					: groupTotals[columns] + totalAmount;
			centerTotals[columns] = centerTotals[columns] == null ? 0.0 + totalAmount
					: centerTotals[columns] + totalAmount;
		}
	}

	private void generateSavingsValues(StringBuilder builder, int rows,
			int columns, SavingsAccountView accountView, Double[] groupTotals,
			Double[] centerTotals, int size, int initialAccNo, String method,
			boolean isDeposit, int totalsColumn, int loanProductsSize,
			int savingsProductSize, int levelId, MifosCurrency currency,
			Short officeId) throws JspException {
		String name = isDeposit ? "depositAmountEntered"
				: "withDrawalAmountEntered";
		String amount = "";
		Double totalAmount = 0.0;
		int depWithFlag = isDeposit ? 1 : 2;
		if (isDeposit) {
			if (method.equals(BulkEntryConstants.GETMETHOD)) {
				totalAmount = accountView.getTotalDepositDue();
				amount = totalAmount.toString();
			} else if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
					|| method.equals(BulkEntryConstants.VALIDATEMETHOD)
					|| method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
				if (accountView.getDepositAmountEntered() != null) {
					amount = accountView.getDepositAmountEntered();
					if (!""
							.equals(accountView.getDepositAmountEntered()
									.trim())
							&& accountView.isValidDepositAmountEntered()) {
						Money total = new Money(currency, accountView
								.getDepositAmountEntered());
						Money.round(total);
						totalAmount = total.getAmountDoubleValue();
						amount = total.toString();
						accountView.setDepositAmountEntered(amount);
					}
				}
			}
		} else {
			if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
					|| method.equals(BulkEntryConstants.VALIDATEMETHOD)
					|| method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
				if (accountView.getWithDrawalAmountEntered() != null) {
					amount = accountView.getWithDrawalAmountEntered();
					if (!"".equals(accountView.getWithDrawalAmountEntered()
							.trim())
							&& accountView.isValidWithDrawalAmountEntered()) {
						Money total = new Money(currency, accountView
								.getWithDrawalAmountEntered());
						Money.round(total);
						totalAmount = total.getAmountDoubleValue();
						amount = total.toString();
						accountView.setWithDrawalAmountEntered(amount);
					}
				}
			}
		}
		if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
				|| method.equals(BulkEntryConstants.VALIDATEMETHOD)
				|| method.equals(BulkEntryConstants.GETMETHOD)) {
			if (Configuration.getInstance().getCustomerConfig(officeId)
					.isCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance()
						.generateSavingsTextInput(builder,
								name + "[" + rows + "][" + columns + "]",
								amount, rows, columns, size, initialAccNo,
								loanProductsSize, savingsProductSize,
								depWithFlag, totalsColumn, levelId);
			} else {
				BulkEntryTagUIHelper.getInstance()
						.generateSavingsTextInput(builder,
								name + "[" + rows + "][" + columns + "]",
								amount, columns, size, depWithFlag,
								loanProductsSize, savingsProductSize);
			}
		
		} else if (method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			if (isDeposit
					&& totalAmount.doubleValue() < accountView
							.getTotalDepositDue().doubleValue()
					&& accountView.getSavingsOffering().getSavingsType()
							.getSavingsTypeId().equals(
									ProductDefinitionConstants.MANDATORY)) {
				builder.append("<font color=\"#FF0000\">" + amount + "</font>");
			} else if ("".equals(amount)) {
				builder.append("&nbsp;");
			} else {
				builder.append(amount);
			}
		}
		groupTotals[totalsColumn] = groupTotals[totalsColumn] == null ? 0.0 + totalAmount
				: groupTotals[totalsColumn] + totalAmount;
		centerTotals[totalsColumn] = centerTotals[totalsColumn] == null ? 0.0 + totalAmount
				: centerTotals[totalsColumn] + totalAmount;
	}

	private void buildCustomerAccount(CustomerAccountView customerAccountView,
			StringBuilder builder, String method, MifosCurrency currency,
			int rows, Double[] groupTotals, Double[] centerTotals, int size,
			int initialAccNo, int loanProductSize, int savingsProductSize,
			int levelId, Short officeId) throws JspException {
		builder.append("<td class=\"drawtablerow\">");
		generateCustomerAccountVaues(customerAccountView, method, builder,
				currency, rows, groupTotals, centerTotals, size, initialAccNo,
				loanProductSize, savingsProductSize, levelId, officeId);
		builder.append("</td>");
		columnIndex++;
	}

	private void generateCustomerAccountVaues(
			CustomerAccountView customerAccountView, String method,
			StringBuilder builder, MifosCurrency currency, int rows,
			Double[] groupTotals, Double[] centerTotals, int size,
			int initialAccNo, int loanProductSize, int savingsProductSize,
			int levelId, Short officeId) throws JspException {
		String amount = "";
		Double totalAmount = 0.0;
		if (method.equals(BulkEntryConstants.GETMETHOD)) {
			totalAmount = customerAccountView.getTotalAmountDue()
					.getAmountDoubleValue();
			amount = totalAmount.toString();
		} else if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
				|| method.equals(BulkEntryConstants.VALIDATEMETHOD)
				|| method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			if (customerAccountView.getCustomerAccountAmountEntered() != null) {
				amount = customerAccountView.getCustomerAccountAmountEntered();
				if (!"".equals(amount.trim())
						&& customerAccountView
								.isValidCustomerAccountAmountEntered()) {
					Money total = new Money(currency, amount);
					Money.round(total);
					totalAmount = total.getAmountDoubleValue();
					amount = total.toString();
					customerAccountView.setCustomerAccountAmountEntered(amount);
				}
			}
		}
		if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
				|| method.equals(BulkEntryConstants.VALIDATEMETHOD)
				|| method.equals(BulkEntryConstants.GETMETHOD)) {
		
			if (Configuration.getInstance().getCustomerConfig(officeId)
					.isCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance()
						.generateCustomerAccountTextInput(
								builder,
								"customerAccountAmountEntered" + "[" + rows
										+ "][" + columnIndex + "]", amount,
								rows, columnIndex, size, initialAccNo,
								loanProductSize, savingsProductSize,
								levelId);
			} else {
				BulkEntryTagUIHelper.getInstance()
						.generateCustomerAccountTextInput(
								builder,
								"customerAccountAmountEntered" + "[" + rows
										+ "][" + columnIndex + "]", amount,
								columnIndex, size, loanProductSize,
								savingsProductSize);
			}
		
		} else if (method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			if (totalAmount.doubleValue() != customerAccountView
					.getTotalAmountDue().getAmountDoubleValue())
				builder.append("<font color=\"#FF0000\">" + amount + "</font>");
			else
				builder.append(amount);
		}
		groupTotals[columnIndex] = groupTotals[columnIndex] == null ? 0.0 + totalAmount
				: groupTotals[columnIndex] + totalAmount;
		centerTotals[columnIndex] = centerTotals[columnIndex] == null ? 0.0 + totalAmount
				: centerTotals[columnIndex] + totalAmount;

	}

	private void getTotalForRow(StringBuilder builder,
			BulkEntryView bulkEntryView, int productsSize, Double[] totals,
			int rows, String method, Locale locale, int loanProductsSize,
			int savingsProductSize) {
		Short customerLevel = bulkEntryView.getCustomerDetail()
				.getCustomerLevelId();

		if (!method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			if (customerLevel.equals(CustomerConstants.GROUP_LEVEL_ID)) {
				BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
				builder
						.append("<td align=\"right\" class=\"drawtablerowSmall\">"
								+ "<span class=\"fontnormal8pt\"><em>"
								+ getLocaleLabel(ConfigurationConstants.GROUP,
										locale) + " Total </em></span></td>");
				builder
						.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");
				for (int i = 0; i < (loanProductsSize + savingsProductSize); i++) {
					Double groupTotal = totals[i] == null ? 0.0 : totals[i];
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"group[" + rows + "][" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + groupTotal
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				for (int i = (loanProductsSize + savingsProductSize); i < (2 * (loanProductsSize + savingsProductSize)); i++) {
					Double groupTotal = totals[i] == null ? 0.0 : totals[i];
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"group[" + rows + "][" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + groupTotal
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				Double groupTotal = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
						: totals[(2 * (loanProductsSize + savingsProductSize))];
				builder.append("<td class=\"drawtablerow\">");
				builder.append("<input name=\"group[" + rows + "]["
						+ (2 * (loanProductsSize + savingsProductSize))
						+ "]\" type=\"text\" style=\"width:40px\""
						+ " value=\"" + groupTotal + "\" size=\"6\" disabled>");
				builder.append("</td>");
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
			} else if (customerLevel.equals(CustomerConstants.CENTER_LEVEL_ID)) {
				BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
				builder
						.append("<td align=\"right\" class=\"drawtablerowSmall\">"
								+ "<span class=\"fontnormal8pt\"><em>"
								+ getLocaleLabel(ConfigurationConstants.CENTER,
										locale) + " Total </em></span></td>");
				builder
						.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");

				for (int i = 0; i < (loanProductsSize + savingsProductSize); i++) {
					Double centerTotal = totals[i] == null ? 0.0 : totals[i];
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"center[" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + centerTotal
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				for (int i = (loanProductsSize + savingsProductSize); i < (2 * (loanProductsSize + savingsProductSize)); i++) {
					Double centerTotal = totals[i] == null ? 0.0 : totals[i];
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"center[" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + centerTotal
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				Double centerTotal = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
						: totals[(2 * (loanProductsSize + savingsProductSize))];
				builder.append("<td class=\"drawtablerow\">");
				builder
						.append("<input name=\"center["
								+ (2 * (loanProductsSize + savingsProductSize))
								+ "]\" type=\"text\" style=\"width:40px\""
								+ " value=\"" + centerTotal
								+ "\" size=\"6\" disabled>");
				builder.append("</td>");
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
			}
		} else {
			BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
			builder.append("<td align=\"right\" class=\"drawtablerowSmall\">"
					+ "<span class=\"fontnormal8pt\"><em>");
			if (customerLevel.equals(CustomerConstants.GROUP_LEVEL_ID)) {
				builder.append(getLocaleLabel(ConfigurationConstants.GROUP,
						locale)
						+ " Total ");
			} else if (customerLevel.equals(CustomerConstants.CENTER_LEVEL_ID)) {
				builder.append(getLocaleLabel(ConfigurationConstants.CENTER,
						locale)
						+ " Total ");
			}
			builder.append("</em></span></td>");
			builder
					.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");

			for (int i = 0; i < (loanProductsSize + savingsProductSize); i++) {
				Double groupTotal = totals[i] == null ? 0.0 : totals[i];
				builder.append("<td class=\"drawtablerow\">");
				builder.append(groupTotal);
				builder.append("</td>");
			}
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			for (int i = (loanProductsSize + savingsProductSize); i < (2 * (loanProductsSize + savingsProductSize)); i++) {
				Double total = totals[i] == null ? 0.0 : totals[i];
				builder.append("<td class=\"drawtablerow\">");
				builder.append(total);
				builder.append("</td>");
			}
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			Double total = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
					: totals[(2 * (loanProductsSize + savingsProductSize))];
			builder.append("<td class=\"drawtablerow\">");
			builder.append(total);
			builder.append("</td>");
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
		}
		BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);

	}

	public StringBuilder buildTotals(Double[] totals, int loanProductsSize,
			int savingsPoductsSize, String method) {
		Double dueColl = 0.0;
		Double withDrawals = 0.0;
		Double loanDisb = 0.0;
		Double otherColl = 0.0;
		for (int i = 0; i < (loanProductsSize + savingsPoductsSize); i++) {
			dueColl = totals[i] == null ? 0.0 + dueColl : totals[i] + dueColl;
		}
		for (int i = (loanProductsSize + savingsPoductsSize); i < ((2 * loanProductsSize) + savingsPoductsSize); i++) {
			loanDisb = totals[i] == null ? 0.0 + loanDisb : totals[i]
					+ loanDisb;
		}

		for (int i = ((2 * loanProductsSize) + savingsPoductsSize); i < (2 * (loanProductsSize + savingsPoductsSize)); i++) {
			withDrawals = totals[i] == null ? 0.0 + withDrawals : totals[i]
					+ withDrawals;
		}
		otherColl = totals[(2 * (loanProductsSize + savingsPoductsSize))] == null ? 0.0
				: totals[(2 * (loanProductsSize + savingsPoductsSize))];
		Double totColl = dueColl + otherColl;
		Double totIssue = withDrawals + loanDisb;
		Double netCash = totColl - totIssue;
		return buildTotalstable(dueColl, loanDisb, otherColl, withDrawals,
				totColl, totIssue, netCash, method);
	}

	private StringBuilder buildTotalstable(Double dueColl, Double loanDisb,
			Double otherColl, Double withDrawals, Double totColl,
			Double totIssue, Double netCash, String method) {
		StringBuilder builder = new StringBuilder();
		builder
				.append("<table width=\"95%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\">");
		builder.append("<tr class=\"fontnormal\">");
		builder
				.append("<td colspan=\"2\"class=\"fontnormal8ptbold\">Total Collections</td>");
		builder
				.append("<td colspan=\"4\" class=\"fontnormal8ptbold\">Total Issues/Withdrawals</td>");
		builder.append("</tr>");
		if (!method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			builder.append("<tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8pt\">Due collections:</td>");
			builder
					.append("<td><input name=\"dueColl\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ dueColl
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder
					.append("<td class=\"fontnormal8pt\">Loan disbursements:</td>");
			builder
					.append("<td colspan=\"3\"><input name=\"loanDisb\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ loanDisb
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append(" </tr>");
			builder.append("<tr class=\"fontnormal\">");
			builder
					.append("<td width=\"10%\" class=\"fontnormal8pt\">Other collections:</td>");
			builder
					.append("<td width=\"9%\"><input name=\"otherColl\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ otherColl
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder
					.append("<td width=\"11%\" class=\"fontnormal8pt\">Withdrawals:</td>");
			builder
					.append("<td colspan=\"3\"><input name=\"Withdrawals\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ withDrawals
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append(" </tr>");

			builder.append(" <tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8ptbold\">Total:</td>");
			builder
					.append("<td><input name=\"totColl\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ totColl
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append("<td class=\"fontnormal8ptbold\">Total:</td>");
			builder
					.append("<td width=\"10%\"><input name=\"totIssue\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ totIssue
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder
					.append("<td width=\"6%\" class=\"fontnormal8ptbold\">Net Cash:</td>");
			builder
					.append("<td width=\"54%\"><input name=\"netCash\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ netCash
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append(" </tr>");
		} else {
			builder.append("<tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8pt\">Due collections:</td>");
			builder.append("<td class=\"fontnormal8pt\">" + dueColl + "</td>");
			builder
					.append("<td class=\"fontnormal8pt\">Loan disbursements:</td>");
			builder.append("<td colspan=\"3\">" + loanDisb + "</td>");
			builder.append(" </tr>");
			builder.append("<tr class=\"fontnormal\">");
			builder
					.append("<td width=\"10%\" class=\"fontnormal8pt\">Other collections:</td>");
			builder.append("<td width=\"9%\" class=\"fontnormal8pt\">"
					+ otherColl + "</td>");
			builder
					.append("<td width=\"11%\" class=\"fontnormal8pt\">Withdrawals:</td>");
			builder.append("<td colspan=\"3\">" + withDrawals + "</td>");
			builder.append(" </tr>");

			builder.append(" <tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8ptbold\">Total:</td>");
			builder.append("<td class=\"fontnormal8ptbold\">" + totColl
					+ "</td>");
			builder.append("<td class=\"fontnormal8ptbold\">Total:</td>");
			builder.append("<td width=\"10%\" class=\"fontnormal8ptbold\">"
					+ totIssue + "</td>");
			builder
					.append("<td width=\"6%\" class=\"fontnormal8ptbold\">Net Cash:</td>");
			if (netCash < 0) {
				builder
						.append("<td width=\"54%\" class=\"fontnormal8ptbold\">"
								+ "<font color=\"#FF0000\">"
								+ netCash
								+ "</font></td>");
			} else {
				builder.append("<td width=\"54%\" class=\"fontnormal8ptbold\">"
						+ netCash + "</td>");
			}
			builder.append(" </tr>");
		}
		builder.append(" </table>");
		return builder;
	}

	private String getLocaleLabel(String key, Locale locale) {
		try {
			return MifosConfiguration.getInstance().getLabel(key, locale);
		} catch (ConfigurationException ce) {
		}
		return null;
	}

}
