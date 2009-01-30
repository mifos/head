/**

 * BulkEntryDisplayHelper.java    version: 1.0

 

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

package org.mifos.application.bulkentry.struts.uihelpers;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;

import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.bulkentry.business.CollectionSheetEntryView;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.config.ClientRules;
import org.mifos.config.Localization;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;

public class BulkEntryDisplayHelper {

	private int columnIndex;

	public StringBuilder buildTableHeadings(List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts, Locale locale) {
		StringBuilder builder = buildStartTable(loanProducts.size()
				+ savingsProducts.size(), locale);
		buildProductsHeading(loanProducts, savingsProducts, builder, locale);
		return builder;
	}

	private StringBuilder buildStartTable(int totalProductSize, Locale locale) {
		ResourceBundle resources = ResourceBundle.getBundle
		(FilePaths.BULKENTRY_RESOURCE, locale);
		String dueCollections = resources.getString(BulkEntryConstants.DUE_COLLECTION);
		String issueWithdrawal = resources.getString(BulkEntryConstants.ISSUE_WITHDRAWAL);
		StringBuilder builder = new StringBuilder();
		builder
				.append("<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\">");
		builder.append("<tr class=\"fontnormalbold\">");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		builder.append("<td height=\"30\">&nbsp;&nbsp;</td>");
		builder.append("<td align=\"center\" colspan=\"" + totalProductSize
				+ "\">" + dueCollections + "</td>");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		builder.append("<td align=\"center\" colspan=\"" + totalProductSize
				+ "\">" + issueWithdrawal + "</td>");
				
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
			List<PrdOfferingBO> savingsProducts, StringBuilder builder, Locale locale) {
		
		ResourceBundle resources = ResourceBundle.getBundle
		(FilePaths.BULKENTRY_RESOURCE, locale);
		String clientName = resources.getString(BulkEntryConstants.CLIENT_NAME);
		String acCollection = resources.getString(BulkEntryConstants.AC_COLLECTION);
		String attn = resources.getString(BulkEntryConstants.ATTN);
		BulkEntryTagUIHelper.getInstance().generateStartTR(builder,
				"fontnormal8ptbold");
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
				clientName, false);
		builder.append("<td height=\"30\">&nbsp;&nbsp;</td>");
		buildProductNames(loanProducts, savingsProducts, builder);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		buildProductNames(loanProducts, savingsProducts, builder);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 14,
				acCollection);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 14, attn);
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

	public Double[] buildForGroup(CollectionSheetEntryView parent,
			List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts,
			List<CustomValueListElement> custAttTypes, StringBuilder builder,
			String method, UserContext userContext, Short officeId) throws JspException {
		int rowIndex = 0;
		int totalProductsSize = (2 * (loanProducts.size() + savingsProducts
				.size()));
		Double[] centerTotals = new Double[(totalProductsSize + 1)];
		Double[] groupTotals = new Double[(totalProductsSize + 1)];
		MifosCurrency currency = parent.getCurrency();
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
		String account = resources.getString(BulkEntryConstants.ACCOUNT_GROUP_CENTER);
		String group = getLabel(ConfigurationConstants.GROUP, userContext);
		String groupAccountStr = account.format(account, group);
		groupAccountStr = " " + groupAccountStr + " ";
		
		for (CollectionSheetEntryView child : parent.getCollectionSheetEntryChildren()) {
			buildCompleteRow(child, loanProducts, savingsProducts, parent
					.getCollectionSheetEntryChildren().size(), 0, rowIndex, groupTotals,
					centerTotals, child.getCustomerDetail().getDisplayName(),
					builder, method, 1, currency, officeId);
			generateAttendence(builder, custAttTypes, rowIndex, child, method);
			BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
			rowIndex++;
		}
		buildCompleteRow(parent, loanProducts, savingsProducts, parent
				.getCollectionSheetEntryChildren().size(), 0, rowIndex, groupTotals,
				centerTotals, groupAccountStr, builder, method, 2, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
		rowIndex++;
		getTotalForRow(builder, parent, totalProductsSize, groupTotals,
				rowIndex, method, userContext, loanProducts.size(), savingsProducts
						.size());
		return groupTotals;
	}

	public Double[] buildForCenter(CollectionSheetEntryView parent,
			List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts,
			List<CustomValueListElement> custAttTypes, StringBuilder builder,
			String method, UserContext userContext, Short officeId) throws JspException {

		int rowIndex = 0;
		int totalProductsSize = (2 * (loanProducts.size() + savingsProducts
				.size()));
		Double[] centerTotals = new Double[(totalProductsSize + 1)];
		Double[] groupTotals = new Double[(totalProductsSize + 1)];
		MifosCurrency currency = parent.getCurrency();
		List<CollectionSheetEntryView> children = parent.getCollectionSheetEntryChildren();
		ResourceBundle resources = ResourceBundle.getBundle
		(FilePaths.BULKENTRY_RESOURCE, userContext.getPreferredLocale());
		String account = resources.getString(BulkEntryConstants.ACCOUNT_GROUP_CENTER);
		String group = getLabel(ConfigurationConstants.GROUP, userContext);
		String center = getLabel(ConfigurationConstants.CENTER, userContext);
		String groupAccountStr = account.format(account, group);
		groupAccountStr = " " + groupAccountStr + " ";
		String centerAccountStr = account.format(account, center);
		centerAccountStr = " " + centerAccountStr + " ";

		for (CollectionSheetEntryView child : children) {
			groupTotals = new Double[(totalProductsSize + 1)];
			int groupInitialAccNum = rowIndex;
			List<CollectionSheetEntryView> subChildren = child.getCollectionSheetEntryChildren();
			int groupChildSize = subChildren.size();
			buildGroupName(builder, child.getCustomerDetail().getDisplayName(),
					totalProductsSize + 1);
			for (CollectionSheetEntryView subChild : subChildren) {
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
					centerTotals, groupAccountStr, builder, method, 2, currency,
					officeId);
			BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
			BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
			rowIndex++;
			getTotalForRow(builder, child, totalProductsSize, groupTotals,
					rowIndex, method, userContext, loanProducts.size(),
					savingsProducts.size());

		}
		buildCompleteRow(parent, loanProducts, savingsProducts, 0, 0, rowIndex,
				groupTotals, centerTotals, centerAccountStr, builder, method, 3, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
		BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
		getTotalForRow(builder, parent, totalProductsSize, centerTotals,
				rowIndex, method, userContext, loanProducts.size(), savingsProducts
						.size());
		return centerTotals;
	}

	private void buildCompleteRow(CollectionSheetEntryView collectionSheetEntryView,
			List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts, int groupChildSize,
			int groupInitialAccNum, int rowIndex, Double[] groupTotals,
			Double[] centerTotals, String customerName, StringBuilder builder,
			String method, int levelId, MifosCurrency currency, Short officeId)
			throws JspException {
		List<LoanAccountsProductView> bulkEntryLoanAccounts = collectionSheetEntryView
				.getLoanAccountDetails();
		List<SavingsAccountView> bulkEntrySavingsAccounts = collectionSheetEntryView
				.getSavingsAccountDetails();

		columnIndex = 0;
		generateStartRow(builder, customerName, collectionSheetEntryView);
		getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex,
				groupTotals, centerTotals, collectionSheetEntryView, groupChildSize,
				groupInitialAccNum, savingsProducts.size(), method, true,
				officeId);
		getDepositSavingsRow(builder, bulkEntrySavingsAccounts,
				savingsProducts, rowIndex, groupTotals, centerTotals,
				collectionSheetEntryView, groupChildSize, groupInitialAccNum, loanProducts
						.size(), method, levelId, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex,
				groupTotals, centerTotals, collectionSheetEntryView, groupChildSize,
				groupInitialAccNum, savingsProducts.size(), method, false,
				officeId);
		getWithdrawalSavingsRow(builder, bulkEntrySavingsAccounts,
				savingsProducts, rowIndex, groupTotals, centerTotals,
				collectionSheetEntryView, groupChildSize, groupInitialAccNum, loanProducts
						.size(), method, levelId, currency, officeId);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;",
				true);
		buildCustomerAccount(collectionSheetEntryView.getCustomerAccountDetails(),
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
			CollectionSheetEntryView collectionSheetEntryView) {
		BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
		BulkEntryTagUIHelper.getInstance().generateTD(builder, customerName);
		builder
				.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");
	}

	private void getLoanRow(StringBuilder builder,
			List<LoanAccountsProductView> bulkEntryAccountList,
			List<PrdOfferingBO> loanProducts, int rows, Double[] groupTotals,
			Double[] centerTotals, CollectionSheetEntryView collectionSheetEntryView, int size,
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
			CollectionSheetEntryView collectionSheetEntryView, int size, int initialAccNo,
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
									.getId().equals(RecommendedAmountUnit.PER_INDIVIDUAL.getValue()));
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
			CollectionSheetEntryView collectionSheetEntryView, int size, int initialAccNo,
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
			List<CustomValueListElement> custAttTypes, int rows,
			CollectionSheetEntryView collectionSheetEntryView, String method) {
		if (method.equals(BulkEntryConstants.GETMETHOD)) {
			builder.append("<td class=\"drawtablerow\">");
			builder.append("<select name=\"attendenceSelected[" + rows
					+ "]\"  style=\"width:80px;\" class=\"fontnormal8pt\">");
			for (CustomValueListElement attendence : custAttTypes) {
				builder.append("<option value=\"" + attendence.getAssociatedId() + "\"");
                if (collectionSheetEntryView.getAttendence() != null && (attendence.getAssociatedId().intValue()== collectionSheetEntryView.getAttendence().intValue()))
                {
                    builder.append(" selected=\"selected\"");
                }
                builder.append( ">" 	+ attendence.getLookUpValue() + "</option>");
			}
			builder.append("</select>");
			builder.append("</td>");
		} else if (method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			builder.append("<td class=\"drawtablerow\">");
			for (CustomValueListElement attendence : custAttTypes) {

				if (null != collectionSheetEntryView.getAttendence()
						&& attendence.getAssociatedId().equals(
								Integer.valueOf(collectionSheetEntryView.getAttendence()))) {
					if (!collectionSheetEntryView.getAttendence().toString().equals("1")) {
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
					+ "]\"  style=\"width:80px;\" class=\"fontnormal8pt\">");
			for (CustomValueListElement attendence : custAttTypes) {
				builder.append("<option value=\"" + attendence.getAssociatedId() + "\"");
				if (null != collectionSheetEntryView.getAttendence()
						&& attendence.getAssociatedId().equals(
								Integer.valueOf(collectionSheetEntryView.getAttendence()))) {
					builder.append(" selected ");
				}
				builder.append(">" + attendence.getLookUpValue() + "</option>");
			}
			builder.append("</select>");
			builder.append("</td>");
		}
	}
	protected Double getDoubleValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(str) : null;
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
			if (ClientRules.getCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
						"enteredAmount[" + rows + "][" + columns + "]",
						amountToBeShown, rows, columns, size, initialAccNo,
						columns, loanproductSize, savingsProductSize);
			} else {
				BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
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
					totalAmount = getDoubleValue(enteredAmount);
				}
			} else {
				enteredAmount = accountViewBO.getDisBursementAmountEntered();
				if (accountViewBO.isValidDisbursementAmount()) {
					totalAmount = getDoubleValue(enteredAmount);
				}
			}
			if (totalAmount.doubleValue() < amountToBeShown.doubleValue()) {
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

			if (ClientRules.getCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
						"enteredAmount[" + rows + "][" + columns + "]",
						enteredAmount, rows, columns, size, initialAccNo,
						columns, loanproductSize, savingsProductSize);
			} else {
				BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
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
					totalAmount = getDoubleValue(accountViewBO
							.getEnteredAmount());
				else
					totalAmount = getDoubleValue(accountViewBO
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
			if (ClientRules.getCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance().generateSavingsTextInput(
						builder, name + "[" + rows + "][" + columns + "]",
						amount, rows, columns, size, initialAccNo,
						loanProductsSize, savingsProductSize, depWithFlag,
						totalsColumn, levelId);
			} else {
				BulkEntryTagUIHelper.getInstance().generateSavingsTextInput(
						builder, name + "[" + rows + "][" + columns + "]",
						amount, columns, size, depWithFlag, loanProductsSize,
						savingsProductSize);
			}

		} else if (method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			if (isDeposit
					&& totalAmount.doubleValue() < accountView
							.getTotalDepositDue().doubleValue()
					&& accountView.getSavingsOffering().getSavingsType()
							.getId().equals(
									SavingsType.MANDATORY.getValue())) {
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
			amount = new Money(totalAmount.toString()).toString();
		} else if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
				|| method.equals(BulkEntryConstants.VALIDATEMETHOD)
				|| method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			if (customerAccountView.getCustomerAccountAmountEntered() != null) {
				amount = customerAccountView.getCustomerAccountAmountEntered();
				if (!"".equals(amount.trim())
						&& customerAccountView
								.isValidCustomerAccountAmountEntered()) {
					Money total = new Money(currency, amount);
					totalAmount = total.getAmountDoubleValue();
					amount = total.toString();
					customerAccountView.setCustomerAccountAmountEntered(amount);
				}
			}
		}
		if (method.equals(BulkEntryConstants.PREVIOUSMETHOD)
				|| method.equals(BulkEntryConstants.VALIDATEMETHOD)
				|| method.equals(BulkEntryConstants.GETMETHOD)) {

			if (ClientRules.getCenterHierarchyExists()) {
				BulkEntryTagUIHelper.getInstance()
						.generateCustomerAccountTextInput(
								builder,
								"customerAccountAmountEntered" + "[" + rows
										+ "][" + columnIndex + "]", amount,
								rows, columnIndex, size, initialAccNo,
								loanProductSize, savingsProductSize, levelId);
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
			CollectionSheetEntryView collectionSheetEntryView, int productsSize, Double[] totals,
			int rows, String method, UserContext userContext, int loanProductsSize,
			int savingsProductSize) {
		Short customerLevel = collectionSheetEntryView.getCustomerDetail()
				.getCustomerLevelId();
		ResourceBundle resources = ResourceBundle.getBundle
		(FilePaths.BULKENTRY_RESOURCE, userContext.getPreferredLocale());
		String totalStr = resources.getString(BulkEntryConstants.TOTAL_GROUP_CENTER);
		String group = getLabel(ConfigurationConstants.GROUP, userContext);
		String center = getLabel(ConfigurationConstants.CENTER, userContext);
		String groupTotalStr = totalStr.format(totalStr, group);
		groupTotalStr = " " + groupTotalStr + " ";
		String centerTotalStr = totalStr.format(totalStr, center);
		centerTotalStr = " " + centerTotalStr + " ";
		if (!method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			if (customerLevel.equals(CustomerLevel.GROUP.getValue())) {
				BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
				builder
						.append("<td align=\"right\" class=\"drawtablerowSmall\">"
								+ "<span class=\"fontnormal8pt\"><em>"
								+ groupTotalStr + "</em></span></td>");
				builder
						.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");
				for (int i = 0; i < (loanProductsSize + savingsProductSize); i++) {
					Double groupTotal = totals[i] == null ? 0.0 : totals[i];
					Money groupTotalMoney = new Money(groupTotal.toString());
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"group[" + rows + "][" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + groupTotalMoney
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				for (int i = (loanProductsSize + savingsProductSize); i < (2 * (loanProductsSize + savingsProductSize)); i++) {
					Double groupTotal = totals[i] == null ? 0.0 : totals[i];
					Money groupTotalMoney = new Money(groupTotal.toString());
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"group[" + rows + "][" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + groupTotalMoney
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				Double groupTotal = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
						: totals[(2 * (loanProductsSize + savingsProductSize))];
				Money groupTotalMoney = new Money(groupTotal.toString());
				builder.append("<td class=\"drawtablerow\">");
				builder.append("<input name=\"group[" + rows + "]["
						+ (2 * (loanProductsSize + savingsProductSize))
						+ "]\" type=\"text\" style=\"width:40px\""
						+ " value=\"" + groupTotalMoney
						+ "\" size=\"6\" disabled>");
				builder.append("</td>");
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
			} else if (customerLevel.equals(CustomerLevel.CENTER.getValue())) {
				BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
				builder
						.append("<td align=\"right\" class=\"drawtablerowSmall\">"
								+ "<span class=\"fontnormal8pt\"><em>"+
								centerTotalStr + "</em></span></td>");
				builder
						.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");

				for (int i = 0; i < (loanProductsSize + savingsProductSize); i++) {
					Double centerTotal = totals[i] == null ? 0.0 : totals[i];
					Money centerTotalMoney = new Money(centerTotal.toString());
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"center[" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + centerTotalMoney
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				for (int i = (loanProductsSize + savingsProductSize); i < (2 * (loanProductsSize + savingsProductSize)); i++) {
					Double centerTotal = totals[i] == null ? 0.0 : totals[i];
					Money centerTotalMoney = new Money(centerTotal.toString());
					builder.append("<td class=\"drawtablerow\">");
					builder.append("<input name=\"center[" + i
							+ "]\" type=\"text\" style=\"width:40px\""
							+ " value=\"" + centerTotalMoney
							+ "\" size=\"6\" disabled>");
					builder.append("</td>");
				}
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
						"&nbsp;", true);
				Double centerTotal = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
						: totals[(2 * (loanProductsSize + savingsProductSize))];
				Money centerTotalMoney = new Money(centerTotal.toString());
				builder.append("<td class=\"drawtablerow\">");
				builder.append("<input name=\"center["
						+ (2 * (loanProductsSize + savingsProductSize))
						+ "]\" type=\"text\" style=\"width:40px\""
						+ " value=\"" + centerTotalMoney
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
			if (customerLevel.equals(CustomerLevel.GROUP.getValue())) {
				builder.append( groupTotalStr );
			} else if (customerLevel.equals(CustomerLevel.CENTER.getValue())) {
				builder.append(centerTotalStr);
			}
			builder.append("</em></span></td>");
			builder
					.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");

			for (int i = 0; i < (loanProductsSize + savingsProductSize); i++) {
				Double total = totals[i] == null ? 0.0 : totals[i];
				Money totalMoney = new Money(total.toString());
				builder.append("<td class=\"drawtablerow\">");
				builder.append(totalMoney);
				builder.append("</td>");
			}
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			for (int i = (loanProductsSize + savingsProductSize); i < (2 * (loanProductsSize + savingsProductSize)); i++) {
				Double total = totals[i] == null ? 0.0 : totals[i];
				Money totalMoney = new Money(total.toString());
				builder.append("<td class=\"drawtablerow\">");
				builder.append(totalMoney);
				builder.append("</td>");
			}
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			BulkEntryTagUIHelper.getInstance().generateTD(builder, 19,
					"&nbsp;", true);
			Double total = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
					: totals[(2 * (loanProductsSize + savingsProductSize))];
			Money totalMoney = new Money(total.toString());
			builder.append("<td class=\"drawtablerow\">");
			builder.append(totalMoney);
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
			int savingsPoductsSize, String method, UserContext userContext) {
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

		Money totalDueCollection = new Money(dueColl.toString());
		Money totalLoanDisburesed = new Money(loanDisb.toString());
		Money otherCollection = new Money(otherColl.toString());
		Money totalWithDrawals = new Money(withDrawals.toString());
		Money totalCollection = new Money(totColl.toString());
		Money totalIssue = new Money(totIssue.toString());
		Money netCashAvailable = new Money(netCash.toString());

		return buildTotalstable(totalDueCollection, totalLoanDisburesed,
				otherCollection, totalWithDrawals, totalCollection, totalIssue,
				netCashAvailable, method, userContext);
	}

	private StringBuilder buildTotalstable(Money dueColl, Money loanDisb,
			Money otherColl, Money withDrawals, Money totColl, Money totIssue,
			Money netCash, String method, UserContext userContext) {
		ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, userContext.getPreferredLocale());
		String totalCollections = resources.getString(BulkEntryConstants.TOTAL_COLLECTION);
		String totalIssuesWithdrawals = resources.getString(BulkEntryConstants.TOTAL_ISSUE_WITHDRAWAL);
		String dueCollections2 = resources.getString(BulkEntryConstants.DUE_COLLECTION2);
		String loanDisbursements = resources.getString(BulkEntryConstants.LOAN_DISBURSEMENT);
		String total = resources.getString(BulkEntryConstants.TOTAL);
		String netCashStr = resources.getString(BulkEntryConstants.NET_CASH);
		String withdrawals = resources.getString(BulkEntryConstants.WITHDRAWAL);
		String otherCollections = resources.getString(BulkEntryConstants.OTHER_COLLECTION);		
		StringBuilder builder = new StringBuilder();
		builder
				.append("<table width=\"95%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\">");
		builder.append("<tr class=\"fontnormal\">");
		builder
				.append("<td colspan=\"2\"class=\"fontnormal8ptbold\">" + totalCollections + "</td>");
		builder
				.append("<td colspan=\"4\" class=\"fontnormal8ptbold\">" + totalIssuesWithdrawals + "</td>");
		builder.append("</tr>");
		if (!method.equals(BulkEntryConstants.PREVIEWMETHOD)) {
			builder.append("<tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8pt\">" + dueCollections2 + "</td>");
			builder
					.append("<td><input name=\"dueColl\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ dueColl
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder
					.append("<td class=\"fontnormal8pt\">" + loanDisbursements + "</td>");
			builder
					.append("<td colspan=\"3\"><input name=\"loanDisb\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ loanDisb
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append(" </tr>");
			builder.append("<tr class=\"fontnormal\">");
			builder
					.append("<td width=\"10%\" class=\"fontnormal8pt\">" + otherCollections + "</td>");
			builder
					.append("<td width=\"9%\"><input name=\"otherColl\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ otherColl
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder
					.append("<td width=\"11%\" class=\"fontnormal8pt\">" + withdrawals + "</td>");
			builder
					.append("<td colspan=\"3\"><input name=\"Withdrawals\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ withDrawals
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append(" </tr>");

			builder.append(" <tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
			builder
					.append("<td><input name=\"totColl\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ totColl
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
			builder
					.append("<td width=\"10%\"><input name=\"totIssue\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ totIssue
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder
					.append("<td width=\"6%\" class=\"fontnormal8ptbold\">" + netCashStr + "</td>");
			builder
					.append("<td width=\"54%\"><input name=\"netCash\" type=\"text\" disabled style=\"width:40px\""
							+ " value=\""
							+ netCash
							+ "\" size=\"6\" class=\"fontnormal8pt\"></td>");
			builder.append(" </tr>");
		} else {
			builder.append("<tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8pt\">" + dueCollections2 + "</td>");
			builder.append("<td class=\"fontnormal8pt\">" + dueColl + "</td>");
			builder
					.append("<td class=\"fontnormal8pt\">" + loanDisbursements + "</td>");
			builder.append("<td colspan=\"3\">" + loanDisb + "</td>");
			builder.append(" </tr>");
			builder.append("<tr class=\"fontnormal\">");
			builder
					.append("<td width=\"10%\" class=\"fontnormal8pt\">" + otherCollections + "</td>");
			builder.append("<td width=\"9%\" class=\"fontnormal8pt\">"
					+ otherColl + "</td>");
			builder
					.append("<td width=\"11%\" class=\"fontnormal8pt\">" + withdrawals + "</td>");
			builder.append("<td colspan=\"3\">" + withDrawals + "</td>");
			builder.append(" </tr>");

			builder.append(" <tr class=\"fontnormal\">");
			builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
			builder.append("<td class=\"fontnormal8ptbold\">" + totColl
					+ "</td>");
			builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
			builder.append("<td width=\"10%\" class=\"fontnormal8ptbold\">"
					+ totIssue + "</td>");
			builder
					.append("<td width=\"6%\" class=\"fontnormal8ptbold\">" + netCashStr + "</td>");
			if (Double.valueOf(netCash.toString()).doubleValue() < 0) {
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

	private String getLabel(String key, UserContext userContext) {
			return MessageLookup.getInstance().lookupLabel(key, userContext);
	}

}
