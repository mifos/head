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
package org.mifos.application.collectionsheet.struts.action;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.StringUtils;

/**
 *
 */
public class CollectionSheetEntryViewPostPreviewValidator {

    public ActionErrors validate(final CollectionSheetEntryView collectionSheetEntry, final ActionErrors errors,
            final Locale locale) {
        
        // FIXME - keithw - simplify and unit test validation
        return validatePopulatedData(collectionSheetEntry, errors, locale);
    }

    private ActionErrors validatePopulatedData(final CollectionSheetEntryView parent, final ActionErrors errors, final Locale locale) {
        List<CollectionSheetEntryView> children = parent.getCollectionSheetEntryChildren();

        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String acCollections = resources.getString(CollectionSheetEntryConstants.AC_COLLECTION);
        if (null != children) {
            for (CollectionSheetEntryView collectionSheetEntryView : children) {
                validatePopulatedData(collectionSheetEntryView, errors, locale);
            }
        }
        for (LoanAccountsProductView accountView : parent.getLoanAccountDetails()) {
            if (accountView.isDisburseLoanAccountPresent() || accountView.getLoanAccountViews().size() > 1) {
                Double enteredAmount = 0.0;
                if (null != accountView.getEnteredAmount() && accountView.isValidAmountEntered()) {
                    enteredAmount = getDoubleValue(accountView.getEnteredAmount());
                }
                Double enteredDisbursalAmount = 0.0;
                if (null != accountView.getDisBursementAmountEntered() && accountView.isValidDisbursementAmount()) {
                    enteredDisbursalAmount = getDoubleValue(accountView.getDisBursementAmountEntered());
                }
                Double totalDueAmount = accountView.getTotalAmountDue();
                Double totalDisburtialAmount = accountView.getTotalDisburseAmount();
                if (totalDueAmount.doubleValue() <= 0.0 && totalDisburtialAmount > 0.0) {
                    if (!accountView.isValidDisbursementAmount()
                            || !enteredDisbursalAmount.equals(totalDisburtialAmount) && !enteredDisbursalAmount
                                    .equals(0.0)) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    }
                }
                if (totalDisburtialAmount <= 0.0 && totalDueAmount > 0.0) {
                    if (!accountView.isValidAmountEntered()
                            || !enteredAmount.equals(totalDueAmount) && !enteredAmount.equals(0.0)) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    }
                }
                if (totalDueAmount.doubleValue() > 0.0 && totalDisburtialAmount > 0.0) {
                    if (!accountView.isValidAmountEntered()
                            || !accountView.isValidDisbursementAmount()
                            || accountView.getEnteredAmount() == null
                            || accountView.getDisBursementAmountEntered() == null
                            || enteredAmount.equals(0.0) && !enteredDisbursalAmount.equals(0.0)
                            || enteredDisbursalAmount.equals(0.0) && !enteredAmount.equals(0.0)
                            || enteredDisbursalAmount.equals(totalDisburtialAmount) && !enteredAmount
                                    .equals(totalDueAmount)
                            || enteredAmount.equals(totalDueAmount) && !enteredDisbursalAmount
                                    .equals(totalDisburtialAmount)
                            || !enteredAmount.equals(totalDueAmount)
                                    && !enteredDisbursalAmount.equals(totalDisburtialAmount)
                                    && !enteredDisbursalAmount.equals(0.0) && !enteredAmount.equals(0.0)) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    }
                }
                if (totalDisburtialAmount <= 0.0 && totalDueAmount <= 0.0) {
                    if (!accountView.isValidAmountEntered() || !accountView.isValidDisbursementAmount()
                            || !enteredDisbursalAmount.equals(0.0) || !enteredAmount.equals(0.0)) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    }
                }
            }
        }
        for (SavingsAccountView savingsAccountView : parent.getSavingsAccountDetails()) {
            if (!savingsAccountView.isValidDepositAmountEntered()
                    || !savingsAccountView.isValidWithDrawalAmountEntered()) {
                errors.add(CollectionSheetEntryConstants.ERRORINVALIDAMOUNT, new ActionMessage(
                        CollectionSheetEntryConstants.ERRORINVALIDAMOUNT, savingsAccountView
                                .getSavingsOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
            }
        }
        CustomerAccountView customerAccountView = parent.getCustomerAccountDetails();
        Double customerAccountAmountEntered = 0.0;
        if (null != customerAccountView.getCustomerAccountAmountEntered()
                && customerAccountView.isValidCustomerAccountAmountEntered()) {
            customerAccountAmountEntered = getDoubleValue(customerAccountView.getCustomerAccountAmountEntered());
        }
        if (!customerAccountView.isValidCustomerAccountAmountEntered()
                || !customerAccountAmountEntered.equals(customerAccountView.getTotalAmountDue()
                        .getAmountDoubleValue()) && !customerAccountAmountEntered.equals(0.0)) {
            errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                    CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, acCollections, parent.getCustomerDetail()
                            .getDisplayName()));
        }
        return errors;
    }
    
    public static Double getDoubleValue(final String str) {
        return StringUtils.isNullAndEmptySafe(str) ? LocalizationConverter.getInstance()
                .getDoubleValueForCurrentLocale(str) : null;
    }

}
