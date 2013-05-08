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

package org.mifos.application.collectionsheet.struts.uihelpers;

import static org.mifos.application.master.MessageLookup.getLocalizedMessage;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.loan.util.helpers.LoanAccountsProductDto;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.util.helpers.SavingsAccountDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryDto;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.ProductDto;
import org.mifos.config.ClientRules;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerAccountDto;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.ConversionUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class BulkEntryDisplayHelper {

    private int columnIndex;

    public StringBuilder buildTableHeadings(final List<ProductDto> loanProducts, final List<ProductDto> savingsProducts,
            final Locale locale) {
        StringBuilder builder = buildStartTable(loanProducts.size() + savingsProducts.size(), locale);
        buildProductsHeading(loanProducts, savingsProducts, builder, locale);
        return builder;
    }

    private StringBuilder buildStartTable(final int totalProductSize, final Locale locale) {
        String dueCollections = getLocalizedMessage(CollectionSheetEntryConstants.DUE_COLLECTION);
        String issueWithdrawal = getLocalizedMessage(CollectionSheetEntryConstants.ISSUE_WITHDRAWAL);
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

        String clientName = getLocalizedMessage(CollectionSheetEntryConstants.CLIENT_NAME);
        String acCollection = getLocalizedMessage(CollectionSheetEntryConstants.AC_COLLECTION);
        String attn = getLocalizedMessage(CollectionSheetEntryConstants.ATTN);
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

    public Money[] buildForGroup(final CollectionSheetEntryDto parent, final List<ProductDto> loanProducts,
            final List<ProductDto> savingsProducts, final List<CustomValueListElementDto> custAttTypes,
            final StringBuilder builder, final String method, final UserContext userContext) {
        int rowIndex = 0;
        int totalProductsSize = 2 * (loanProducts.size() + savingsProducts.size());
        Money[] groupTotals = new Money[(totalProductsSize + 1)];
        MifosCurrency currency = parent.getCurrency();
        Locale locale = userContext.getPreferredLocale();
        String account = getLocalizedMessage(CollectionSheetEntryConstants.ACCOUNTS_GROUP_CENTER);
        String group = getLabel(ConfigurationConstants.GROUP, userContext);
        String groupAccountStr = account.format(account, group);
        groupAccountStr = " " + groupAccountStr + " ";

        for (CollectionSheetEntryDto child : parent.getCollectionSheetEntryChildren()) {
            buildClientRow(child, loanProducts, savingsProducts, parent.getCollectionSheetEntryChildren().size(), 0,
                    rowIndex, groupTotals, child.getCustomerDetail().getDisplayName(), builder, method,
                    1, currency);
            generateAttendance(builder, custAttTypes, rowIndex, child, method);
            BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
            rowIndex++;
        }
        buildClientRow(parent, loanProducts, savingsProducts, parent.getCollectionSheetEntryChildren().size(), 0,
                rowIndex, groupTotals, groupAccountStr, builder, method, 2, currency);
        BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
        rowIndex++;
        getTotalForRow(builder, parent, groupTotals, rowIndex, method, userContext, loanProducts
                .size(), savingsProducts.size());
        return groupTotals;
    }

    public Money[] buildForCenter(final CollectionSheetEntryDto centerEntry, final List<ProductDto> loanProducts,
            final List<ProductDto> savingsProducts, final List<CustomValueListElementDto> custAttTypes,
            final StringBuilder builder, final String method, final UserContext userContext) {

        int rowIndex = 0;
        final int totalProductsSize = 2 * (loanProducts.size() + savingsProducts.size());
        final Money[] centerTotals = new Money[(totalProductsSize + 1)];
        Money[] groupTotals = new Money[(totalProductsSize + 1)];
        
        for (int i = 0; i < centerTotals.length; i++) {
            centerTotals[i] = new Money(centerEntry.getCurrency());
        }

        final MifosCurrency currency = centerEntry.getCurrency();
        final String accountLabel = getLocalizedMessage(CollectionSheetEntryConstants.ACCOUNTS_GROUP_CENTER);

        final List<CollectionSheetEntryDto> groupEntries = centerEntry.getCollectionSheetEntryChildren();

        for (CollectionSheetEntryDto groupEntry : groupEntries) {

            groupTotals = new Money[(totalProductsSize + 1)];
            int groupInitialAccNum = rowIndex;

            final List<CollectionSheetEntryDto> clientEntries = groupEntry.getCollectionSheetEntryChildren();
            final int clientCount = clientEntries.size();
            final String groupName = groupEntry.getCustomerDetail().getDisplayName();
            buildGroupName(builder, groupName, totalProductsSize + 1);

            for (CollectionSheetEntryDto clientEntry : clientEntries) {
                final int levelId = 1;
                buildClientRow(clientEntry, loanProducts, savingsProducts, clientCount, groupInitialAccNum, rowIndex,
                        groupTotals, clientEntry.getCustomerDetail().getDisplayName(), builder, method,
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
            buildClientRow(groupEntry, loanProducts, savingsProducts, clientCount, groupInitialAccNum, rowIndex,
                    groupTotals, groupAccountStr, builder, method, levelId, currency);
            BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
            BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
            rowIndex++;
            getTotalForRow(builder, groupEntry, groupTotals, rowIndex, method, userContext, loanProducts
                    .size(), savingsProducts.size());
            
            for (int i = 0; i < groupTotals.length; i++) {
                if (groupTotals[i] != null) {
                    centerTotals[i] = centerTotals[i].add(groupTotals[i]);
                }
            }

        }

        final int levelId = 3;
        final String center = getLabel(ConfigurationConstants.CENTER, userContext);
        final String formattedCenterAccountStr = accountLabel.format(accountLabel, center);
        final String centerAccountStr = " " + formattedCenterAccountStr + " ";
        buildClientRow(centerEntry, loanProducts, savingsProducts, 0, 0, rowIndex, groupTotals,
                centerAccountStr, builder, method, levelId, currency);
        BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
        getTotalForRow(builder, centerEntry, centerTotals, rowIndex, method, userContext, loanProducts
                .size(), savingsProducts.size());
        
        return centerTotals;
    }

    private void buildClientRow(final CollectionSheetEntryDto collectionSheetEntryDto, final List<ProductDto> loanProducts,
            final List<ProductDto> savingsProducts, final int groupChildSize, final int groupInitialAccNum, final int rowIndex,
            final Money[] groupTotals, final String customerName, final StringBuilder builder, final String method,
            final int levelId,
            final MifosCurrency currency) {
        List<LoanAccountsProductDto> bulkEntryLoanAccounts = collectionSheetEntryDto.getLoanAccountDetails();
        List<SavingsAccountDto> bulkEntrySavingsAccounts = collectionSheetEntryDto.getSavingsAccountDetails();

        columnIndex = 0;
        generateStartRow(builder, customerName);
        getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex, groupTotals,
                groupChildSize,
                groupInitialAccNum, savingsProducts.size(), method, true);

        getDepositSavingsRow(builder, bulkEntrySavingsAccounts, savingsProducts, rowIndex, groupTotals, groupChildSize, groupInitialAccNum, loanProducts.size(), method, levelId, currency);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        getLoanRow(builder, bulkEntryLoanAccounts, loanProducts, rowIndex, groupTotals,
                groupChildSize,
                groupInitialAccNum, savingsProducts.size(), method, false);
        getWithdrawalSavingsRow(builder, bulkEntrySavingsAccounts, savingsProducts, rowIndex, groupTotals,
                groupChildSize, groupInitialAccNum, loanProducts.size(),
                method, levelId, currency);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        buildCustomerAccount(collectionSheetEntryDto.getCustomerAccountDetails(), builder, method, currency, rowIndex,
                groupTotals, groupChildSize, groupInitialAccNum, loanProducts.size(), savingsProducts
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

    private void getLoanRow(final StringBuilder builder, final List<LoanAccountsProductDto> bulkEntryAccountList,
            final List<ProductDto> loanProducts, final int rows, final Money[] groupTotals,
            final int size, final int initialAccNo, final int savingsProductSize,
            final String method, final boolean isShowingDue) {
        for (ProductDto prdOffering : loanProducts) {
            boolean isIdMatched = false;
            builder.append("<td class=\"drawtablerow\">");
            for (LoanAccountsProductDto accountViewBO : bulkEntryAccountList) {
                isIdMatched = prdOffering.getId().equals(accountViewBO.getPrdOfferingId());
                if (isIdMatched) {

                    generateLoanValues(builder, rows, columnIndex, accountViewBO, groupTotals, size,
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

    private void getDepositSavingsRow(final StringBuilder builder, final List<SavingsAccountDto> bulkEntryAccountList,
            final List<ProductDto> savingsProducts, final int rows, final Money[] groupTotals,
            final int size, final int initialAccNo, final int loanProductsSize,
            final String method, final int levelId, final MifosCurrency currency) {

        for (ProductDto prdOffering : savingsProducts) {
            boolean isIdMatched = false;
            builder.append("<td class=\"drawtablerow\">");
            for (SavingsAccountDto accountView : bulkEntryAccountList) {
                if (levelId == 1) {
                    isIdMatched = prdOffering.getId().equals(accountView.getSavingsOfferingId()) && !accountView.getAccountTrxnDetails().isEmpty();
                } else {
                    isIdMatched = prdOffering.getId().equals(
                            accountView.getSavingsOfferingId());
                }
                if (isIdMatched) {
                    generateSavingsValues(builder, rows, columnIndex, accountView, groupTotals, size,
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

    private void getWithdrawalSavingsRow(final StringBuilder builder, final List<SavingsAccountDto> bulkEntryAccountList,
            final List<ProductDto> savingsProducts, final int rows, final Money[] groupTotals,
            final int size,
            final int initialAccNo, final int loanProductsSize,
            final String method, final int levelId,
            final MifosCurrency currency) {
        for (ProductDto prdOffering : savingsProducts) {
            boolean isIdMatched = false;
            builder.append("<td class=\"drawtablerow\">");
            for (SavingsAccountDto accountView : bulkEntryAccountList) {
                isIdMatched = prdOffering.getId()
                        .equals(accountView.getSavingsOfferingId());
                if (isIdMatched) {
                    generateSavingsValues(builder, rows, columnIndex, accountView, groupTotals, size,
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
            final List<CustomValueListElementDto> custAttTypes, final int row, final CollectionSheetEntryDto collectionSheetEntryDto,
            final String method) {
        Short collectionSheetEntryViewAttendence = collectionSheetEntryDto.getAttendence();
        if (method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
            builder.append("<td class=\"drawtablerow\">");
            builder.append("<select name=\"attendanceSelected[" + row
                    + "]\"  style=\"width:80px;\" class=\"fontnormal8pt\">");
            for (CustomValueListElementDto attendance : custAttTypes) {
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
            for (CustomValueListElementDto attendance : custAttTypes) {
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
            for (CustomValueListElementDto attendance : custAttTypes) {
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

    private boolean attendancesAreEqual(final Short collectionSheetEntryViewAttendence, final CustomValueListElementDto attendance) {
        return null != collectionSheetEntryViewAttendence
                && attendance.getAssociatedId().intValue() == collectionSheetEntryViewAttendence;
    }

    protected Double getDoubleValue(final String str) {
        return StringUtils.isNotBlank(str) ? new LocalizationConverter()
                .getDoubleValueForCurrentLocale(str) : null;
    }

    private void generateLoanValues(final StringBuilder builder, final int rows, final int columns,
            final LoanAccountsProductDto accountViewBO, final Money[] groupTotals, final int size,
            final int initialAccNo, final int loanproductSize, final int savingsProductSize,
            final String method, final boolean isShowingDue) {
        Money amountToBeShown;
        if (isShowingDue) {
            if (rows == size && groupTotals[columns] != null) {
                amountToBeShown = groupTotals[columns];
            } else {
                amountToBeShown = new Money(Money.getDefaultCurrency(), accountViewBO.getTotalAmountDue());
            }
            if (amountToBeShown.isLessThanOrEqualZero() && accountViewBO.isDisburseLoanAccountPresent()) {
                builder.append("&nbsp;");
                return;
            }
        } else {
            amountToBeShown = new Money(Money.getDefaultCurrency(), accountViewBO.getTotalDisburseAmount());
            if (amountToBeShown.isLessThanOrEqualZero()) {
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
                        "enteredAmount[" + rows + "][" + columns + "]", amountToBeShown, rows, columns, size,
                        loanproductSize, savingsProductSize);
            }

            Money actualMoneyValue = new Money(amountToBeShown.getCurrency(), amountToBeShown.toString());
            if (groupTotals[columns] == null) {
                groupTotals[columns] = actualMoneyValue;
            } else if (rows != size + initialAccNo) {
                groupTotals[columns] = groupTotals[columns].add(actualMoneyValue);
            }
        } else if (method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            Money totalAmount = new Money(Money.getDefaultCurrency(), "0");
            Money enteredAmount;
            if (isShowingDue) {
                enteredAmount = new Money(Money.getDefaultCurrency(), accountViewBO.getEnteredAmount());
                if (accountViewBO.isValidAmountEntered()) {
                    totalAmount = enteredAmount;
                }
            } else {
                enteredAmount = new Money(Money.getDefaultCurrency(), accountViewBO.getDisBursementAmountEntered());
                if (accountViewBO.isValidDisbursementAmount()) {
                    totalAmount = enteredAmount;
                }
            }
            if (amountToBeShown.subtract(totalAmount).isNonZero()) {
                builder.append("<font color=\"#FF0000\">");
                builder.append(ConversionUtil.formatNumber(enteredAmount.toString()));
                builder.append("</font>");
            } else {
                builder.append(ConversionUtil.formatNumber(totalAmount.toString()));
            }
            Money actualMoneyValue = new Money(amountToBeShown.getCurrency(), totalAmount.toString());
            if (groupTotals[columns] == null) {
                groupTotals[columns] = actualMoneyValue;
            } else if (rows != size + initialAccNo) {
                groupTotals[columns] = groupTotals[columns].add(actualMoneyValue);
            }
        } else if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)) {

            Money enteredAmount = new Money(Money.getDefaultCurrency(), "0");
            if (isShowingDue) {
                enteredAmount = new Money(Money.getDefaultCurrency(), accountViewBO.getEnteredAmount());
            } else {
                enteredAmount = new Money(Money.getDefaultCurrency(), accountViewBO.getDisBursementAmountEntered());
            }

            if (ClientRules.getCenterHierarchyExists()) {
                BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
                        "enteredAmount[" + rows + "][" + columns + "]", enteredAmount, rows, columns, size,
                        initialAccNo, columns, loanproductSize, savingsProductSize);
            } else {
                BulkEntryTagUIHelper.getInstance().generateTextInput(builder,
                        "enteredAmount[" + rows + "][" + columns + "]", enteredAmount, rows, columns, size, loanproductSize,
                        savingsProductSize);
            }

            Money totalAmount = new Money(Money.getDefaultCurrency(), "0");
            boolean isValidAmountEntered;
            if (isShowingDue) {
                isValidAmountEntered = accountViewBO.isValidAmountEntered();
            } else {
                isValidAmountEntered = accountViewBO.isValidDisbursementAmount();
            }
            if (isValidAmountEntered) {
                if (isShowingDue) {
                    totalAmount = new Money(Money.getDefaultCurrency(), accountViewBO.getEnteredAmount());
                } else {
                    totalAmount = new Money(Money.getDefaultCurrency(), accountViewBO.getDisBursementAmountEntered());
                }
            }
            Money actualMoneyValue = new Money(amountToBeShown.getCurrency(), totalAmount.toString());
            if (groupTotals[columns] == null) {
                groupTotals[columns] = actualMoneyValue;
            } else if (rows != size + initialAccNo) {
                groupTotals[columns] = groupTotals[columns].add(actualMoneyValue);
            }
        }
    }

    private void generateSavingsValues(final StringBuilder builder, final int rows, final int columns, final SavingsAccountDto accountView,
            final Money[] groupTotals, final int size, final int initialAccNo, final String method, final boolean isDeposit,
            final int totalsColumn, final int loanProductsSize, final int savingsProductSize, final int levelId,
            final MifosCurrency currency) {
        String name = isDeposit ? "depositAmountEntered" : "withDrawalAmountEntered";
        String amount = "";
        Money totalAmount = new Money(Money.getDefaultCurrency(), "0.0");
        int depWithFlag = isDeposit ? 1 : 2;
        if (isDeposit) {
            if (method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
                totalAmount = new Money(Money.getDefaultCurrency(), accountView.getTotalDepositDue());
                amount = totalAmount.toString();
            } else if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                    || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)
                    || method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
                if (accountView.getDepositAmountEntered() != null) {
                    amount = accountView.getDepositAmountEntered();
                    if (!"".equals(accountView.getDepositAmountEntered().trim())
                            && accountView.isValidDepositAmountEntered()) {
                        totalAmount = new Money(currency, accountView.getDepositAmountEntered());
                        amount = totalAmount.toString();
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
                        totalAmount = new Money(currency, accountView.getWithDrawalAmountEntered());
                        amount = totalAmount.toString();
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
            Money depositDue = new Money(Money.getDefaultCurrency(), accountView.getTotalDepositDue());
            if (isDeposit
                    && totalAmount.subtract(depositDue).isNonZero()
                    && accountView.getSavingsTypeId().equals(
                            SavingsType.MANDATORY.getValue())) {
                builder.append("<font color=\"#FF0000\">");
                builder.append(ConversionUtil.formatNumber(amount.toString()));
                builder.append("</font>");
            } else if ("".equals(amount)) {
                builder.append("&nbsp;");
            } else {
                builder.append(ConversionUtil.formatNumber(amount.toString()));
            }
        }
        groupTotals[totalsColumn] = groupTotals[totalsColumn] == null ? totalAmount : groupTotals[totalsColumn].add(totalAmount);
    }

    private void buildCustomerAccount(final CustomerAccountDto customerAccountDto, final StringBuilder builder, final String method,
            final MifosCurrency currency, final int rows, final Money[] groupTotals, final int size, final int initialAccNo,
            final int loanProductSize,
            final int savingsProductSize, final int levelId) {
        builder.append("<td class=\"drawtablerow\">");
        generateCustomerAccountVaues(customerAccountDto, method, builder, currency, rows, groupTotals,
                size, initialAccNo, loanProductSize, savingsProductSize, levelId);
        builder.append("</td>");
        columnIndex++;
    }

    private void generateCustomerAccountVaues(final CustomerAccountDto customerAccountDto, final String method,
            final StringBuilder builder, final MifosCurrency currency, final int rows, final Money[] groupTotals,
            final int size, final int initialAccNo, final int loanProductSize,
            final int savingsProductSize, final int levelId) {
        String amount = "";
        Money totalAmount = new Money(Money.getDefaultCurrency(), "0.0");
        if (method.equals(CollectionSheetEntryConstants.GETMETHOD)) {
            totalAmount = customerAccountDto.getTotalAmountDue();
            amount = new Money(currency, totalAmount.toString()).toString();
        } else if (method.equals(CollectionSheetEntryConstants.PREVIOUSMETHOD)
                || method.equals(CollectionSheetEntryConstants.VALIDATEMETHOD)
                || method.equals(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            if (customerAccountDto.getCustomerAccountAmountEntered() != null) {
                amount = customerAccountDto.getCustomerAccountAmountEntered();
                if (!"".equals(amount.trim()) && customerAccountDto.isValidCustomerAccountAmountEntered()) {
                    totalAmount = new Money(currency, amount);
                    amount = totalAmount.toString();
                    customerAccountDto.setCustomerAccountAmountEntered(amount);
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
            if (totalAmount.subtract(customerAccountDto.getTotalAmountDue()).isNonZero()) {
                builder.append("<font color=\"#FF0000\">");
                builder.append(ConversionUtil.formatNumber(amount.toString()));
                builder.append("</font>");
            } else {
                builder.append(ConversionUtil.formatNumber(amount));
            }
        }
        groupTotals[columnIndex] = groupTotals[columnIndex] == null ? totalAmount : groupTotals[columnIndex].add(totalAmount);
    }

    private void getTotalForRow(final StringBuilder builder, final CollectionSheetEntryDto collectionSheetEntryDto,
            final Money[] totals, final int rows, final String method, final UserContext userContext,
            final int loanProductsSize,
            final int savingsProductSize) {
        Short customerLevel = collectionSheetEntryDto.getCustomerDetail().getCustomerLevelId();
        String totalStr = getLocalizedMessage(CollectionSheetEntryConstants.TOTAL_GROUP_CENTER);
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
                    Money groupTotalMoney = totals[i] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0.0") : totals[i];
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"group[" + rows + "][" + i + "]\" type=\"text\" style=\"width:40px\""
                            + " value=\"" + groupTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                for (int i = loanProductsSize + savingsProductSize; i < 2 * (loanProductsSize + savingsProductSize); i++) {
                    Money groupTotalMoney = totals[i] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0.0") : totals[i];
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"group[" + rows + "][" + i + "]\" type=\"text\" style=\"width:40px\""
                            + " value=\"" + groupTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                Money groupTotalMoney = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0.0")
                        : totals[(2 * (loanProductsSize + savingsProductSize))];
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
                        + "<span class=\"fontnormal8pt\"><em>" +  centerTotalStr + "</em></span></td>");
                builder.append("<td height=\"30\" class=\"drawtablerow\">&nbsp;&nbsp;</td>");

                for (int i = 0; i < loanProductsSize + savingsProductSize; i++) {
                    Money centerTotalMoney = totals[i] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0.0") : totals[i];
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"center[" + i + "]\" type=\"text\" style=\"width:40px\"" + " value=\""
                            + centerTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                for (int i = loanProductsSize + savingsProductSize; i < 2 * (loanProductsSize + savingsProductSize); i++) {
                    Money centerTotalMoney = totals[i] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0.0") : totals[i];
                    builder.append("<td class=\"drawtablerow\">");
                    builder.append("<input name=\"center[" + i + "]\" type=\"text\" style=\"width:40px\"" + " value=\""
                            + centerTotalMoney + "\" size=\"6\" disabled>");
                    builder.append("</td>");
                }
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
                Money centerTotalMoney = totals[(2 * (loanProductsSize + savingsProductSize))] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0.0")
                        : totals[(2 * (loanProductsSize + savingsProductSize))];
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
                Money totalMoney = totals[i] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0") : totals[i];
                builder.append("<td class=\"drawtablerow\">");
                builder.append(ConversionUtil.formatNumber(totalMoney.toString()));
                builder.append("</td>");
            }
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            for (int i = loanProductsSize + savingsProductSize; i < 2 * (loanProductsSize + savingsProductSize); i++) {
                Money totalMoney = totals[i] == null ? new Money(collectionSheetEntryDto.getCurrency(), "0") : totals[i];
                builder.append("<td class=\"drawtablerow\">");
                builder.append(ConversionUtil.formatNumber(totalMoney.toString()));
                builder.append("</td>");
            }
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            Money totalMoney = totals[(2 * (loanProductsSize + savingsProductSize))] == null ?new Money(collectionSheetEntryDto.getCurrency(), "0")
                    : totals[(2 * (loanProductsSize + savingsProductSize))];
            builder.append("<td class=\"drawtablerow\">");
            builder.append(ConversionUtil.formatNumber(totalMoney.toString()));
            builder.append("</td>");
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
            BulkEntryTagUIHelper.getInstance().generateTD(builder, 19, "&nbsp;", true);
        }
        BulkEntryTagUIHelper.getInstance().generateEmptyTD(builder, true);
        BulkEntryTagUIHelper.getInstance().generateEndTR(builder);
    }

    public StringBuilder buildTotals(final MifosCurrency currency, final Money[] totals, final int loanProductsSize, final int savingsPoductsSize, final String method,
            final UserContext userContext) {

        Money dueColl = new Money(Money.getDefaultCurrency(), "0.0");
        Money withDrawals = new Money(Money.getDefaultCurrency(), "0.0");
        Money loanDisb = new Money(Money.getDefaultCurrency(), "0.0");
        Money otherColl = new Money(Money.getDefaultCurrency(), "0.0");

        for (int i = 0; i < loanProductsSize + savingsPoductsSize; i++) {
            dueColl = totals[i] == null ? dueColl : totals[i].add(dueColl);
        }
        for (int i = loanProductsSize + savingsPoductsSize; i < 2 * loanProductsSize + savingsPoductsSize; i++) {
            loanDisb = totals[i] == null ? loanDisb : totals[i].add(loanDisb);
        }

        for (int i = 2 * loanProductsSize + savingsPoductsSize; i < 2 * (loanProductsSize + savingsPoductsSize); i++) {
            withDrawals = totals[i] == null ? withDrawals : totals[i].add(withDrawals);
        }
        otherColl = totals[(2 * (loanProductsSize + savingsPoductsSize))] == null ? new Money(Money.getDefaultCurrency(), "0.0")
                : totals[(2 * (loanProductsSize + savingsPoductsSize))];
        Money totColl = dueColl.add(otherColl);
        Money totIssue = withDrawals.add(loanDisb);
        Money netCash = totColl.subtract(totIssue);

        Money totalDueCollection = new Money(currency, dueColl.toString());
        Money totalLoanDisburesed = new Money(currency, loanDisb.toString());
        Money otherCollection = new Money(currency, otherColl.toString());
        Money totalWithDrawals = new Money(currency, withDrawals.toString());
        Money totalCollection = new Money(currency, totColl.toString());
        Money totalIssue = new Money(currency, totIssue.toString());
        Money netCashAvailable = new Money(currency, netCash.toString());

        return buildTotalstable(totalDueCollection, totalLoanDisburesed, otherCollection, totalWithDrawals,
                totalCollection, totalIssue, netCashAvailable, method, userContext);
    }

    private StringBuilder buildTotalstable(final Money dueColl, final Money loanDisb, final Money otherColl, final Money withDrawals,
            final Money totColl, final Money totIssue, final Money netCash, final String method, final UserContext userContext) {
        String totalCollections = getLocalizedMessage(CollectionSheetEntryConstants.TOTAL_COLLECTION);
        String totalIssuesWithdrawals = getLocalizedMessage(CollectionSheetEntryConstants.TOTAL_ISSUE_WITHDRAWAL);
        String dueCollections2 = getLocalizedMessage(CollectionSheetEntryConstants.DUE_COLLECTION2);
        String loanDisbursements = getLocalizedMessage(CollectionSheetEntryConstants.LOAN_DISBURSEMENT);
        String total = getLocalizedMessage(CollectionSheetEntryConstants.TOTAL);
        String netCashStr = getLocalizedMessage(CollectionSheetEntryConstants.NET_CASH);
        String withdrawals = getLocalizedMessage(CollectionSheetEntryConstants.WITHDRAWAL);
        String otherCollections = getLocalizedMessage(CollectionSheetEntryConstants.OTHER_COLLECTION);
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
            builder.append("<td class=\"fontnormal8pt\">" + ConversionUtil.formatNumber(dueColl.toString()) + "</td>");
            builder.append("<td class=\"fontnormal8pt\">" + loanDisbursements + "</td>");
            builder.append("<td colspan=\"3\">" + ConversionUtil.formatNumber(loanDisb.toString()) + "</td>");
            builder.append(" </tr>");
            builder.append("<tr class=\"fontnormal\">");
            builder.append("<td width=\"10%\" class=\"fontnormal8pt\">" + otherCollections + "</td>");
            builder.append("<td width=\"9%\" class=\"fontnormal8pt\">" + ConversionUtil.formatNumber(otherColl.toString()) + "</td>");
            builder.append("<td width=\"11%\" class=\"fontnormal8pt\">" + withdrawals + "</td>");
            builder.append("<td colspan=\"3\">" + ConversionUtil.formatNumber(withDrawals.toString()) + "</td>");
            builder.append(" </tr>");

            builder.append(" <tr class=\"fontnormal\">");
            builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
            builder.append("<td class=\"fontnormal8ptbold\">" + ConversionUtil.formatNumber(totColl.toString()) + "</td>");
            builder.append("<td class=\"fontnormal8ptbold\">" + total + "</td>");
            builder.append("<td width=\"10%\" class=\"fontnormal8ptbold\">" + ConversionUtil.formatNumber(totIssue.toString()) + "</td>");
            builder.append("<td width=\"6%\" class=\"fontnormal8ptbold\">" + netCashStr + "</td>");
            if (Double.valueOf(netCash.toString()) < 0) {
                builder.append("<td width=\"54%\" class=\"fontnormal8ptbold\">" + "<font color=\"#FF0000\">" + ConversionUtil.formatNumber(netCash.toString())
                        + "</font></td>");
            } else {
                builder.append("<td width=\"54%\" class=\"fontnormal8ptbold\">" + ConversionUtil.formatNumber(netCash.toString()) + "</td>");
            }
            builder.append(" </tr>");
        }
        builder.append(" </table>");
        return builder;
    }

    private String getLabel(final String key, final UserContext userContext) {
        return ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(key);
    }

    private boolean almostEqual(double x, double y) {
        return Math.abs(x - y) <= 0.0001;
    }
}
