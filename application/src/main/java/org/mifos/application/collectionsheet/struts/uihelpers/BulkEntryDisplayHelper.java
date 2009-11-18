/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.collectionsheet.struts.uihelpers;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.servicefacade.ProductDto;
import org.mifos.config.ClientRules;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;

public class BulkEntryDisplayHelper {

    private int columnIndex;

    public StringBuilder buildTableHeadings(final List<ProductDto> loanProducts, final List<ProductDto> savingsProducts,
            final Locale locale) {
        StringBuilder builder = buildStartTable(loanProducts.size() + savingsProducts.size(), locale);
        buildProductsHeading(loanProducts, savingsProducts, builder, locale);
        return builder;
    }

    private StringBuilder buildStartTable(final int totalProductSize, final Locale locale) {
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String dueCollections = resources.getString(CollectionSheetEntryConstants.DUE_COLLECTION);
        String issueWithdrawal = resources.getString(CollectionSheetEntryConstants.ISSUE_WITHDRAWAL);
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\">");
        builder.append("<tr class=\"fontnormalbold\">");
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        builder.append("<td height=\"30\">&nbsp;&nbsp;</td>");
        builder.append("<td align=\"center\" colspan=\"" + totalProductSize + "\">" + dueCollections + "</td>");
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        builder.append("<td align=\"center\" colspan=\"" + totalProductSize + "\">" + issueWithdrawal + "</td>");

        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
        return builder;
    }

    private void buildProductsHeading(final List<ProductDto> loanProducts, final List<ProductDto> savingsProducts,
            final StringBuilder builder, final Locale locale) {

        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String clientName = resources.getString(CollectionSheetEntryConstants.CLIENT_NAME);
        String acCollection = resources.getString(CollectionSheetEntryConstants.AC_COLLECTION);
        String attn = resources.getString(CollectionSheetEntryConstants.ATTN);
        BulkEntryTagUIHelper.getInstance().generateStartTR(builder, "fontnormal8ptbold");
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, clientName, false);
        builder.append("<td height=\"30\">&nbsp;&nbsp;</td>");
        buildProductNames(loanProducts, savingsProducts, builder);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        buildProductNames(loanProducts, savingsProducts, builder);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 14, acCollection);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "", false);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 14, attn);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
    }

    private void buildProductNames(final List<ProductDto> loanProducts, final List<ProductDto> savingsProducts,
            final StringBuilder builder) {
        for (ProductDto prdOffering : loanProducts) {
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 4, prdOffering.getShortName());
        }
        for (ProductDto prdOffering : savingsProducts) {
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 4, prdOffering.getShortName());
        }
    }

    public StringBuilder getEndTable(final int columns) {
        StringBuilder builder = new StringBuilder();
        BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
        for (int i = 0; i < columns; i++) {
            BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
        }
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
        builder.append("</table>");
        return builder;
    }

    public Double[] buildForGroup(final CollectionSheetEntryView parent, final List<ProductDto> loanProducts,
            final List<ProductDto> savingsProducts, final List<CustomValueListElement> custAttTypes,
            final StringBuilder builder, final String method, final UserContext userContext) {
        int rowIndex = 0;
        int totalProductsSize = 2 * (loanProducts.size() + savingsProducts.size());
        Double[] centerTotals = new Double[(totalProductsSize + 1)];
        Double[] groupTotals = new Double[(totalProductsSize + 1)];
        MifosCurrency currency = parent.getCurrency();
        Locale locale = userContext.getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String account = resources.getString(CollectionSheetEntryConstants.ACCOUNT_GROUP_CENTER);
        String group = getLabel(ConfigurationConstants.GROUP, userContext);
        String groupAccountStr = account.format(account, group);
        groupAccountStr = " " + groupAccountStr + " ";

        for (CollectionSheetEntryView child : parent.getCollectionSheetEntryChildren()) {
            buildCompleteRow(child, loanProducts, savingsProducts, parent.getCollectionSheetEntryChildren().size(), 0,
                    rowIndex, groupTotals, centerTotals, child.getCustomerDetail().getDisplayName(), builder, method,
                    1, currency);
            generateAttendance(builder, custAttTypes, rowIndex, child, method);
            BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
            rowIndex++;
        }
        buildCompleteRow(parent, loanProducts, savingsProducts, parent.getCollectionSheetEntryChildren().size(), 0,
                rowIndex, groupTotals, centerTotals, groupAccountStr, builder, method, 2, currency);
        BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
        rowIndex++;
        getTotalForRow(builder, parent, groupTotals, rowIndex, method, userContext, loanProducts
                .size(), savingsProducts.size());
        return groupTotals;
    }

    public Double[] buildForCenter(final CollectionSheetEntryView centerEntry, final List<ProductDto> loanProducts,
            final List<ProductDto> savingsProducts, final List<CustomValueListElement> custAttTypes,
            final StringBuilder builder, final String method, final UserContext userContext) {

        int rowIndex = 0;
        final int totalProductsSize = 2 * (loanProducts.size() + savingsProducts.size());
        final Double[] centerTotals = new Double[(totalProductsSize + 1)];
        Double[] groupTotals = new Double[(totalProductsSize + 1)];
        
        final MifosCurrency currency = centerEntry.getCurrency();
        final ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, userContext
                .getPreferredLocale());
        final String accountLabel = resources.getString(CollectionSheetEntryConstants.ACCOUNT_GROUP_CENTER);
        
        final List<CollectionSheetEntryView> groupEntries = centerEntry.getCollectionSheetEntryChildren();

        for (CollectionSheetEntryView groupEntry : groupEntries) {
            
            groupTotals = new Double[(totalProductsSize + 1)];
            int groupInitialAccNum = rowIndex;
            
            final List<CollectionSheetEntryView> clientEntries = groupEntry.getCollectionSheetEntryChildren();
            final int clientCount = clientEntries.size();
            final String groupName = groupEntry.getCustomerDetail().getDisplayName();
            buildGroupName(builder, groupName, totalProductsSize + 1);
            
            for (CollectionSheetEntryView clientEntry : clientEntries) {
                final int levelId = 1;
                buildCompleteRow(clientEntry, loanProducts, savingsProducts, clientCount, groupInitialAccNum, rowIndex,
                        groupTotals, centerTotals, clientEntry.getCustomerDetail().getDisplayName(), builder, method,
                        levelId,
                        currency);
                generateAttendance(builder, custAttTypes, rowIndex, clientEntry, method);
                BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
                rowIndex++;
            }
            
            final int levelId = 2;
            final String groupLabel = getLabel(ConfigurationConstants.GROUP, userContext);
            final String formattedGroupAccountStr = accountLabel.format(accountLabel, groupLabel);
            final String groupAccountStr = " " + formattedGroupAccountStr + " ";
            buildCompleteRow(groupEntry, loanProducts, savingsProducts, clientCount, groupInitialAccNum, rowIndex,
                    groupTotals, centerTotals, groupAccountStr, builder, method, levelId, currency);
            BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
            BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
            rowIndex++;
            getTotalForRow(builder, groupEntry, groupTotals, rowIndex, method, userContext, loanProducts
                    .size(), savingsProducts.size());

        }
        
        final int levelId = 3;
        final String center = getLabel(ConfigurationConstants.CENTER, userContext);
        final String formattedCenterAccountStr = accountLabel.format(accountLabel, center);
        final String centerAccountStr = " " + formattedCenterAccountStr + " ";
        buildCompleteRow(centerEntry, loanProducts, savingsProducts, 0, 0, rowIndex, groupTotals, centerTotals,
                centerAccountStr, builder, method, levelId, currency);
        BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
        getTotalForRow(builder, centerEntry, centerTotals, rowIndex, method, userContext, loanProducts
                .size(), savingsProducts.size());
        return centerTotals;
    }

    private void buildCompleteRow(final CollectionSheetEntryView collectionSheetEntryView, final List<ProductDto> loanProducts,
            final List<ProductDto> savingsProducts, final int groupChildSize, final int groupInitialAccNum, final int rowIndex,
            final Double[] groupTotals, final Double[] centerTotals, final String customerName, final StringBuilder builder, final String method,
            final int levelId,
            final MifosCurrency currency) {
        List<LoanAccountsProductView> bulkEntryLoanAccounts = collectionSheetEntryView.getLoanAccountDetails();
        List<SavingsAccountView> bulkEntrySavingsAccounts = collectionSheetEntryView.getSavingsAccountDetails();

        columnIndex = 0;
        generateStartRow(builder, customerName);
        getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex, groupTotals, centerTotals,
                groupChildSize,
                groupInitialAccNum, savingsProducts.size(), method, true);
        getDepositSavingsRow(builder, bulkEntrySavingsAccounts, savingsProducts, rowIndex, groupTotals, centerTotals,
                groupChildSize, groupInitialAccNum, loanProducts.size(), method, levelId,
                currency);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex, groupTotals, centerTotals,
                groupChildSize,
                groupInitialAccNum, savingsProducts.size(), method, false);
        getWithdrawalSavingsRow(builder, bulkEntrySavingsAccounts, savingsProducts, rowIndex, groupTotals,
                centerTotals, groupChildSize, groupInitialAccNum, loanProducts.size(),
                method, levelId, currency);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        buildCustomerAccount(collectionSheetEntryView.getCustomerAccountDetails(), builder, method, currency, rowIndex,
                groupTotals, centerTotals, groupChildSize, groupInitialAccNum, loanProducts.size(), savingsProducts
                        .size(), levelId);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
    }

    private void buildGroupName(final StringBuilder builder, final String groupName, final int totalProductsSize) {
        BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
        builder.append(" <td class=\"drawtablerow\"><span class=\"fontnormal8ptbold\">" + groupName + "</span></td>");
        for (int count = 0; count < totalProductsSize + 8; count++) {
            builder.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;</td>");
        }
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
    }

    private void generateStartRow(final StringBuilder builder, final String customerName) {
        BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, customerName);
        builder.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");
    }

    private void getLoanRow(final StringBuilder builder, final List<LoanAccountsProductView> bulkEntryAccountList,
            final List<ProductDto> loanProducts, final int rows, final Double[] groupTotals, final Double[] centerTotals,
            final int size, final int initialAccNo, final int savingsProductSize,
            final String method, final boolean isShowingDue) {
        for (ProductDto prdOffering : loanProducts) {
            boolean isIdMatched = false;
            builder.append("<td class=\"drawtablerow\">");
            for (LoanAccountsProductView accountViewBO : bulkEntryAccountList) {
                isIdMatched = prdOffering.getId().equals(accountViewBO.getPrdOfferingId());
                if (isIdMatched) {

                    generateLoanValues(builder, rows, columnIndex, accountViewBO, groupTotals, centerTotals, size,
                            initialAccNo, loanProducts.size(), savingsProductSize, method, isShowingDue);
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

    private void getDepositSavingsRow(final StringBuilder builder, final List<SavingsAccountView> bulkEntryAccountList,
            final List<ProductDto> savingsProducts, final int rows, final Double[] groupTotals, final Double[] centerTotals,
            final int size, final int initialAccNo, final int loanProductsSize,
            final String method, final int levelId, final MifosCurrency currency) {
        for (ProductDto prdOffering : savingsProducts) {
            boolean isIdMatched = false;
            builder.append("<td class=\"drawtablerow\">");
            for (SavingsAccountView accountView : bulkEntryAccountList) {
                if (levelId == 1) {
                    isIdMatched = prdOffering.getId().equals(
                            accountView.getSavingsOfferingId())
                            && (null == accountView.getRecommendedAmntUnitId() || accountView
                                    .getRecommendedAmntUnitId().equals(
                                            RecommendedAmountUnit.PER_INDIVIDUAL.getValue()));
                } else {
                    isIdMatched = prdOffering.getId().equals(
                            accountView.getSavingsOfferingId());
                }
                if (isIdMatched) {
                    generateSavingsValues(builder, rows, columnIndex, accountView, groupTotals, centerTotals, size,
                            initialAccNo, method, true, columnIndex, loanProductsSize, savingsProducts.size(), levelId,
                            currency);
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

    private void getWithdrawalSavingsRow(final StringBuilder builder, final List<SavingsAccountView> bulkEntryAccountList,
            final List<ProductDto> savingsProducts, final int rows, final Double[] groupTotals, final Double[] centerTotals,
            final int size,
            final int initialAccNo, final int loanProductsSize,
            final String method, final int levelId,
            final MifosCurrency currency) {
        for (ProductDto prdOffering : savingsProducts) {
            boolean isIdMatched = false;
            builder.append("<td class=\"drawtablerow\">");
            for (SavingsAccountView accountView : bulkEntryAccountList) {
                isIdMatched = prdOffering.getId()
                        .equals(accountView.getSavingsOfferingId());
                if (isIdMatched) {
                    generateSavingsValues(builder, rows, columnIndex, accountView, groupTotals, centerTotals, size,
                            initialAccNo, method, false, columnIndex, loanProductsSize, savingsProducts.size(),
                            levelId, currency);
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

    private void generateAttendance(final StringBuilder builder,
            final List<CustomValueListElement> custAttTypes, final int row, final CollectionSheetEntryView collectionSheetEntryView,
            final String method) {
        Short collectionSheetEntryViewAttendence = collectionSheetEntryView.getAttendence();
        if (method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
            builder.append("<td class=\"drawtablerow\">");
            builder.append("<select name=\"attendanceSelected[" + row
                    + "]\"  style=\"width:80px;\" class=\"fontnormal8pt\">");
            for (CustomValueListElement attendance : custAttTypes) {
                builder.append("<option value=\"" + attendance.getAssociatedId() + "\"");
                if (attendancesAreEqual(collectionSheetEntryViewAttendence, attendance)) {
                    builder.append(" selected=\"selected\"");
                }
                builder.append(">" + attendance.getLookUpValue() + "</option>");
            }
            builder.append("</select>");
            builder.append("</td>");
        } else if (method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            builder.append("<td class=\"drawtablerow\">");
            for (CustomValueListElement attendance : custAttTypes) {
                if (attendancesAreEqual(collectionSheetEntryViewAttendence, attendance)) {
                    if (!collectionSheetEntryViewAttendence.toString().equals("1")) {
                        builder.append("<font color=\"#FF0000\">" + attendance.getLookUpValue() + "</font>");
                    } else {
                        builder.append(attendance.getLookUpValue());
                    }
                }
            }
            builder.append("</td>");

        } else if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)) {
            builder.append("<td class=\"drawtablerow\">");
            builder.append("<select name=\"attendanceSelected[" + row
                    + "]\"  style=\"width:80px;\" class=\"fontnormal8pt\">");
            for (CustomValueListElement attendance : custAttTypes) {
                builder.append("<option value=\"" + attendance.getAssociatedId() + "\"");
                if (null != collectionSheetEntryViewAttendence
                        && attendance.getAssociatedId().equals(Integer.valueOf(collectionSheetEntryViewAttendence))) {
                    builder.append(" selected ");
                }
                builder.append(">" + attendance.getLookUpValue() + "</option>");
            }
            builder.append("</select>");
            builder.append("</td>");
        }
    }

    private boolean attendancesAreEqual(final Short collectionSheetEntryViewAttendence, final CustomValueListElement attendance) {
        return null != collectionSheetEntryViewAttendence
                && attendance.getAssociatedId().intValue() == collectionSheetEntryViewAttendence;
    }

    protected Double getDoubleValue(final String str) {
        return StringUtils.isNotBlank(str) ? new LocalizationConverter()
                .getDoubleValueForCurrentLocale(str) : null;
    }

    private void generateLoanValues(final StringBuilder builder, final int rows, final int columns,
            final LoanAccountsProductView accountViewBO, final Double[] groupTotals, final Double[] centerTotals, final int size,
            final int initialAccNo, final int loanproductSize, final int savingsProductSize,
            final String method, final boolean isShowingDue) {
        Double amountToBeShown = 0.0;
        if (isShowingDue) {
            amountToBeShown = accountViewBO.getTotalAmountDue();
            if (amountToBeShown.doubleValue() <= 0.0 && accountViewBO.isDisburseLoanAccountPresent()) {
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
        if (method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
            if (ClientRules.getCenterHierarchyExists()) {
                BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
                        "enteredAmount[" + rows + "][" + columns + "]", amountToBeShown, rows, columns, size,
                        initialAccNo, columns, loanproductSize, savingsProductSize);
            } else {
                BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
                        "enteredAmount[" + rows + "][" + columns + "]", amountToBeShown, columns, size,
                        loanproductSize, savingsProductSize);
            }

            Double totalAmount = amountToBeShown;
            groupTotals[columns] = groupTotals[columns] == null ? 0.0 + totalAmount : groupTotals[columns]
                    + totalAmount;
            centerTotals[columns] = centerTotals[columns] == null ? 0.0 + totalAmount : centerTotals[columns]
                    + totalAmount;
        } else if (method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
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
                builder.append("<font color=\"#FF0000\">" + enteredAmount + "</font>");
            } else {
                builder.append(totalAmount);
            }
            groupTotals[columns] = groupTotals[columns] == null ? 0.0 + totalAmount : groupTotals[columns]
                    + totalAmount;
            centerTotals[columns] = centerTotals[columns] == null ? 0.0 + totalAmount : centerTotals[columns]
                    + totalAmount;
        } else if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)) {

            String enteredAmount = "";
            if (isShowingDue) {
                enteredAmount = accountViewBO.getEnteredAmount();
            } else {
                enteredAmount = accountViewBO.getDisBursementAmountEntered();
            }

            if (ClientRules.getCenterHierarchyExists()) {
                BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
                        "enteredAmount[" + rows + "][" + columns + "]", enteredAmount, rows, columns, size,
                        initialAccNo, columns, loanproductSize, savingsProductSize);
            } else {
                BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
                        "enteredAmount[" + rows + "][" + columns + "]", enteredAmount, columns, size, loanproductSize,
                        savingsProductSize);
            }

            Double totalAmount = 0.0;
            boolean isValidAmountEntered;
            if (isShowingDue) {
                isValidAmountEntered = accountViewBO.isValidAmountEntered();
            } else {
                isValidAmountEntered = accountViewBO.isValidDisbursementAmount();
            }
            if (isValidAmountEntered) {
                if (isShowingDue) {
                    totalAmount = getDoubleValue(accountViewBO.getEnteredAmount());
                } else {
                    totalAmount = getDoubleValue(accountViewBO.getDisBursementAmountEntered());
                }
            }
            groupTotals[columns] = groupTotals[columns] == null ? 0.0 + totalAmount : groupTotals[columns]
                    + totalAmount;
            centerTotals[columns] = centerTotals[columns] == null ? 0.0 + totalAmount : centerTotals[columns]
                    + totalAmount;
        }
    }

    private void generateSavingsValues(final StringBuilder builder, final int rows, final int columns, final SavingsAccountView accountView,
            final Double[] groupTotals, final Double[] centerTotals, final int size, final int initialAccNo, final String method, final boolean isDeposit,
            final int totalsColumn, final int loanProductsSize, final int savingsProductSize, final int levelId,
            final MifosCurrency currency) {
        String name = isDeposit ? "depositAmountEntered" : "withDrawalAmountEntered";
        String amount = "";
        Double totalAmount = 0.0;
        int depWithFlag = isDeposit ? 1 : 2;
        if (isDeposit) {
            if (method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
                totalAmount = accountView.getTotalDepositDue();
                amount = totalAmount.toString();
            } else if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                    || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)
                    || method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
                if (accountView.getDepositAmountEntered() != null) {
                    amount = accountView.getDepositAmountEntered();
                    if (!"".equals(accountView.getDepositAmountEntered().trim())
                            && accountView.isValidDepositAmountEntered()) {
                        Money total = new Money(currency, accountView.getDepositAmountEntered());
                        totalAmount = total.getAmountDoubleValue();
                        amount = total.toString();
                        accountView.setDepositAmountEntered(amount);
                    }
                }
            }
        } else {
            if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                    || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)
                    || method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
                if (accountView.getWithDrawalAmountEntered() != null) {
                    amount = accountView.getWithDrawalAmountEntered();
                    if (!"".equals(accountView.getWithDrawalAmountEntered().trim())
                            && accountView.isValidWithDrawalAmountEntered()) {
                        Money total = new Money(currency, accountView.getWithDrawalAmountEntered());
                        totalAmount = total.getAmountDoubleValue();
                        amount = total.toString();
                        accountView.setWithDrawalAmountEntered(amount);
                    }
                }
            }
        }
        if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)
                || method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
            if (ClientRules.getCenterHierarchyExists()) {
                BulkEntryTagUIHelper.getInstance().generateSavingsTextInput(builder,
                        name + "[" + rows + "][" + columns + "]", amount, rows, columns, size, initialAccNo,
                        loanProductsSize, savingsProductSize, depWithFlag, totalsColumn, levelId);
            } else {
                BulkEntryTagUIHelper.getInstance().generateSavingsTextInput(builder,
                        name + "[" + rows + "][" + columns + "]", amount, columns, size, depWithFlag, loanProductsSize,
                        savingsProductSize);
            }

        } else if (method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            if (isDeposit
                    && totalAmount.doubleValue() < accountView.getTotalDepositDue().doubleValue()
                    && accountView.getSavingsTypeId().equals(
                            SavingsType.MANDATORY.getValue())) {
                builder.append("<font color=\"#FF0000\">" + amount + "</font>");
            } else if ("".equals(amount)) {
                builder.append("&nbsp;");
            } else {
                builder.append(amount);
            }
        }
        groupTotals[totalsColumn] = groupTotals[totalsColumn] == null ? 0.0 + totalAmount : groupTotals[totalsColumn]
                + totalAmount;
        centerTotals[totalsColumn] = centerTotals[totalsColumn] == null ? 0.0 + totalAmount
                : centerTotals[totalsColumn] + totalAmount;
    }

    private void buildCustomerAccount(final CustomerAccountView customerAccountView, final StringBuilder builder, final String method,
            final MifosCurrency currency, final int rows, final Double[] groupTotals, final Double[] centerTotals, final int size, final int initialAccNo,
            final int loanProductSize,
            final int savingsProductSize, final int levelId) {
        builder.append("<td class=\"drawtablerow\">");
        generateCustomerAccountVaues(customerAccountView, method, builder, currency, rows, groupTotals, centerTotals,
                size, initialAccNo, loanProductSize, savingsProductSize, levelId);
        builder.append("</td>");
        columnIndex++;
    }

    private void generateCustomerAccountVaues(final CustomerAccountView customerAccountView, final String method,
            final StringBuilder builder, final MifosCurrency currency, final int rows, final Double[] groupTotals, final Double[] centerTotals,
            final int size, final int initialAccNo, final int loanProductSize,
            final int savingsProductSize, final int levelId) {
        String amount = "";
        Double totalAmount = 0.0;
        if (method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
            totalAmount = customerAccountView.getTotalAmountDue().getAmountDoubleValue();
            amount = new Money(totalAmount.toString()).toString();
        } else if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)
                || method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            if (customerAccountView.getCustomerAccountAmountEntered() != null) {
                amount = customerAccountView.getCustomerAccountAmountEntered();
                if (!"".equals(amount.trim()) && customerAccountView.isValidCustomerAccountAmountEntered()) {
                    Money total = new Money(currency, amount);
                    totalAmount = total.getAmountDoubleValue();
                    amount = total.toString();
                    customerAccountView.setCustomerAccountAmountEntered(amount);
                }
            }
        }
        if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)
                || method.equals(CollectionSheetEntryConstants.GETMETHOD)) {

            if (ClientRules.getCenterHierarchyExists()) {
                BulkEntryTagUIHelper.getInstance().generateCustomerAccountTextInput(builder,
                        "customerAccountAmountEntered" + "[" + rows + "][" + columnIndex + "]", amount, rows,
                        columnIndex, size, initialAccNo, loanProductSize, savingsProductSize, levelId);
            } else {
                BulkEntryTagUIHelper.getInstance().generateCustomerAccountTextInput(builder,
                        "customerAccountAmountEntered" + "[" + rows + "][" + columnIndex + "]", amount, columnIndex,
                        size, loanProductSize, savingsProductSize);
            }

        } else if (method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            if (totalAmount.doubleValue() != customerAccountView.getTotalAmountDue().getAmountDoubleValue()) {
                builder.append("<font color=\"#FF0000\">" + amount + "</font>");
            } else {
                builder.append(amount);
            }
        }
        groupTotals[columnIndex] = groupTotals[columnIndex] == null ? 0.0 + totalAmount : groupTotals[columnIndex]
                + totalAmount;
        centerTotals[columnIndex] = centerTotals[columnIndex] == null ? 0.0 + totalAmount : centerTotals[columnIndex]
                + totalAmount;

    }

    private void getTotalForRow(final StringBuilder builder, final CollectionSheetEntryView collectionSheetEntryView,
            final Double[] totals, final int rows, final String method, final UserContext userContext,
            final int loanProductsSize,
            final int savingsProductSize) {
        Short customerLevel = collectionSheetEntryView.getCustomerDetail().getCustomerLevelId();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, userContext
                .getPreferredLocale());
        String totalStr = resources.getString(CollectionSheetEntryConstants.TOTAL_GROUP_CENTER);
        String group = getLabel(ConfigurationConstants.GROUP, userContext);
        String center = getLabel(ConfigurationConstants.CENTER, userContext);
        String groupTotalStr = totalStr.format(totalStr, group);
        groupTotalStr = " " + groupTotalStr + " ";
        String centerTotalStr = totalStr.format(totalStr, center);
        centerTotalStr = " " + centerTotalStr + " ";
        if (!method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            if (customerLevel.equals(CustomerLevel.GROUP.getValue())) {
                BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
                builder.append("<td align=\"right\" class=\"drawtablerowSmall\">"
                        + "<span class=\"fontnormal8pt\"><em>" + groupTotalStr + "</em></span></td>");
                builder.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");
                for (int i = 0; i < loanProductsSize + savingsProductSize; i++) {
                    Double groupTotal = totals[i] == null ? 0.0 : totals[i];
                    Money groupTotalMoney = new Money(groupTotal.toString());
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"group[" + rows + "][" + i + "]\" type=\"text\" style=\"width:40px\""
                            + " value=\"" + groupTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                for (int i = loanProductsSize + savingsProductSize; i < 2 * (loanProductsSize + savingsProductSize); i++) {
                    Double groupTotal = totals[i] == null ? 0.0 : totals[i];
                    Money groupTotalMoney = new Money(groupTotal.toString());
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"group[" + rows + "][" + i + "]\" type=\"text\" style=\"width:40px\""
                            + " value=\"" + groupTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                Double groupTotal = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
                        : totals[(2 * (loanProductsSize + savingsProductSize))];
                Money groupTotalMoney = new Money(groupTotal.toString());
                builder.append("<td class=\"drawtablerow\">");
                builder.append("<input name=\"group[" + rows + "][" + 2 * (loanProductsSize + savingsProductSize)
                        + "]\" type=\"text\" style=\"width:40px\"" + " value=\"" + groupTotalMoney
                        + "\" size=\"6\" disabled>");
                builder.append("</td>");
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            } else if (customerLevel.equals(CustomerLevel.CENTER.getValue())) {
                BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
                builder.append("<td align=\"right\" class=\"drawtablerowSmall\">"
                        + "<span class=\"fontnormal8pt\"><em>" + centerTotalStr + "</em></span></td>");
                builder.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");

                for (int i = 0; i < loanProductsSize + savingsProductSize; i++) {
                    Double centerTotal = totals[i] == null ? 0.0 : totals[i];
                    Money centerTotalMoney = new Money(centerTotal.toString());
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"center[" + i + "]\" type=\"text\" style=\"width:40px\"" + " value=\""
                            + centerTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                for (int i = loanProductsSize + savingsProductSize; i < 2 * (loanProductsSize + savingsProductSize); i++) {
                    Double centerTotal = totals[i] == null ? 0.0 : totals[i];
                    Money centerTotalMoney = new Money(centerTotal.toString());
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"center[" + i + "]\" type=\"text\" style=\"width:40px\"" + " value=\""
                            + centerTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                Double centerTotal = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
                        : totals[(2 * (loanProductsSize + savingsProductSize))];
                Money centerTotalMoney = new Money(centerTotal.toString());
                builder.append("<td class=\"drawtablerow\">");
                builder.append("<input name=\"center[" + 2 * (loanProductsSize + savingsProductSize)
                        + "]\" type=\"text\" style=\"width:40px\"" + " value=\"" + centerTotalMoney
                        + "\" size=\"6\" disabled>");
                builder.append("</td>");
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            }
        } else {
            BulkEntryTagUIHelper.getInstance().generateStartTR(builder);
            builder.append("<td align=\"right\" class=\"drawtablerowSmall\">" + "<span class=\"fontnormal8pt\"><em>");
            if (customerLevel.equals(CustomerLevel.GROUP.getValue())) {
                builder.append(groupTotalStr);
            } else if (customerLevel.equals(CustomerLevel.CENTER.getValue())) {
                builder.append(centerTotalStr);
            }
            builder.append("</em></span></td>");
            builder.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");

            for (int i = 0; i < loanProductsSize + savingsProductSize; i++) {
                Double total = totals[i] == null ? 0.0 : totals[i];
                Money totalMoney = new Money(total.toString());
                builder.append("<td class=\"drawtablerow\">");
                builder.append(totalMoney);
                builder.append("</td>");
            }
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            for (int i = loanProductsSize + savingsProductSize; i < 2 * (loanProductsSize + savingsProductSize); i++) {
                Double total = totals[i] == null ? 0.0 : totals[i];
                Money totalMoney = new Money(total.toString());
                builder.append("<td class=\"drawtablerow\">");
                builder.append(totalMoney);
                builder.append("</td>");
            }
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            Double total = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? 0.0
                    : totals[(2 * (loanProductsSize + savingsProductSize))];
            Money totalMoney = new Money(total.toString());
            builder.append("<td class=\"drawtablerow\">");
            builder.append(totalMoney);
            builder.append("</td>");
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        }
        BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
    }

    public StringBuilder buildTotals(final Double[] totals, final int loanProductsSize, final int savingsPoductsSize, final String method,
            final UserContext userContext) {
        Double dueColl = 0.0;
        Double withDrawals = 0.0;
        Double loanDisb = 0.0;
        Double otherColl = 0.0;
        for (int i = 0; i < loanProductsSize + savingsPoductsSize; i++) {
            dueColl = totals[i] == null ? 0.0 + dueColl : totals[i] + dueColl;
        }
        for (int i = loanProductsSize + savingsPoductsSize; i < 2 * loanProductsSize + savingsPoductsSize; i++) {
            loanDisb = totals[i] == null ? 0.0 + loanDisb : totals[i] + loanDisb;
        }

        for (int i = 2 * loanProductsSize + savingsPoductsSize; i < 2 * (loanProductsSize + savingsPoductsSize); i++) {
            withDrawals = totals[i] == null ? 0.0 + withDrawals : totals[i] + withDrawals;
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

        return buildTotalstable(totalDueCollection, totalLoanDisburesed, otherCollection, totalWithDrawals,
                totalCollection, totalIssue, netCashAvailable, method, userContext);
    }

    private StringBuilder buildTotalstable(final Money dueColl, final Money loanDisb, final Money otherColl, final Money withDrawals,
            final Money totColl, final Money totIssue, final Money netCash, final String method, final UserContext userContext) {
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, userContext
                .getPreferredLocale());
        String totalCollections = resources.getString(CollectionSheetEntryConstants.TOTAL_COLLECTION);
        String totalIssuesWithdrawals = resources.getString(CollectionSheetEntryConstants.TOTAL_ISSUE_WITHDRAWAL);
        String dueCollections2 = resources.getString(CollectionSheetEntryConstants.DUE_COLLECTION2);
        String loanDisbursements = resources.getString(CollectionSheetEntryConstants.LOAN_DISBURSEMENT);
        String total = resources.getString(CollectionSheetEntryConstants.TOTAL);
        String netCashStr = resources.getString(CollectionSheetEntryConstants.NET_CASH);
        String withdrawals = resources.getString(CollectionSheetEntryConstants.WITHDRAWAL);
        String otherCollections = resources.getString(CollectionSheetEntryConstants.OTHER_COLLECTION);
        StringBuilder builder = new StringBuilder();
        builder.append("<table width=\"95%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\">");
        builder.append("<tr class=\"fontnormal\">");
        builder.append("<td colspan=\"2\"class=\"fontnormal8ptbold\">" + totalCollections + "</td>");
        builder.append("<td colspan=\"4\" class=\"fontnormal8ptbold\">" + totalIssuesWithdrawals + "</td>");
        builder.append("</tr>");
        if (!method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            builder.append("<tr class=\"fontnormal\">");
            builder.append("<td class=\"fontnormal8pt\">" + dueCollections2 + "</td>");
            builder.append("<td><input name=\"dueColl\" type=\"text\" disabled style=\"width:40px\"" + " value=\""
                    + dueColl + "\" size=\"6\" class=\"fontnormal8pt\"></td>");
            builder.append("<td class=\"fontnormal8pt\">" + loanDisbursements + "</td>");
            builder.append("<td colspan=\"3\"><input name=\"loanDisb\" type=\"text\" disabled style=\"width:40px\""
                    + " value=\"" + loanDisb + "\" size=\"6\" class=\"fontnormal8pt\"></td>");
            builder.append(" </tr>");
            builder.append("<tr class=\"fontnormal\">");
            builder.append("<td width=\"10%\" class=\"fontnormal8pt\">" + otherCollections + "</td>");
            builder.append("<td width=\"9%\"><input name=\"otherColl\" type=\"text\" disabled style=\"width:40px\""
                    + " value=\"" + otherColl + "\" size=\"6\" class=\"fontnormal8pt\"></td>");
            builder.append("<td width=\"11%\" class=\"fontnormal8pt\">" + withdrawals + "</td>");
            builder.append("<td colspan=\"3\"><input name=\"Withdrawals\" type=\"text\" disabled style=\"width:40px\""
                    + " value=\"" + withDrawals + "\" size=\"6\" class=\"fontnormal8pt\"></td>");
            builder.append(" </tr>");

            builder.append(" <tr class=\"fontnormal\">");
            builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
            builder.append("<td><input name=\"totColl\" type=\"text\" disabled style=\"width:40px\"" + " value=\""
                    + totColl + "\" size=\"6\" class=\"fontnormal8pt\"></td>");
            builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
            builder.append("<td width=\"10%\"><input name=\"totIssue\" type=\"text\" disabled style=\"width:40px\""
                    + " value=\"" + totIssue + "\" size=\"6\" class=\"fontnormal8pt\"></td>");
            builder.append("<td width=\"6%\" class=\"fontnormal8ptbold\">" + netCashStr + "</td>");
            builder.append("<td width=\"54%\"><input name=\"netCash\" type=\"text\" disabled style=\"width:40px\""
                    + " value=\"" + netCash + "\" size=\"6\" class=\"fontnormal8pt\"></td>");
            builder.append(" </tr>");
        } else {
            builder.append("<tr class=\"fontnormal\">");
            builder.append("<td class=\"fontnormal8pt\">" + dueCollections2 + "</td>");
            builder.append("<td class=\"fontnormal8pt\">" + dueColl + "</td>");
            builder.append("<td class=\"fontnormal8pt\">" + loanDisbursements + "</td>");
            builder.append("<td colspan=\"3\">" + loanDisb + "</td>");
            builder.append(" </tr>");
            builder.append("<tr class=\"fontnormal\">");
            builder.append("<td width=\"10%\" class=\"fontnormal8pt\">" + otherCollections + "</td>");
            builder.append("<td width=\"9%\" class=\"fontnormal8pt\">" + otherColl + "</td>");
            builder.append("<td width=\"11%\" class=\"fontnormal8pt\">" + withdrawals + "</td>");
            builder.append("<td colspan=\"3\">" + withDrawals + "</td>");
            builder.append(" </tr>");

            builder.append(" <tr class=\"fontnormal\">");
            builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
            builder.append("<td class=\"fontnormal8ptbold\">" + totColl + "</td>");
            builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
            builder.append("<td width=\"10%\" class=\"fontnormal8ptbold\">" + totIssue + "</td>");
            builder.append("<td width=\"6%\" class=\"fontnormal8ptbold\">" + netCashStr + "</td>");
            if (Double.valueOf(netCash.toString()).doubleValue() < 0) {
                builder.append("<td width=\"54%\" class=\"fontnormal8ptbold\">" + "<font color=\"#FF0000\">" + netCash
                        + "</font></td>");
            } else {
                builder.append("<td width=\"54%\" class=\"fontnormal8ptbold\">" + netCash + "</td>");
            }
            builder.append(" </tr>");
        }
        builder.append(" </table>");
        return builder;
    }

    private String getLabel(final String key, final UserContext userContext) {
        return MessageLookup.getInstance().lookupLabel(key, userContext);
    }

}
