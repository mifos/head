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
package org.mifos.application.collectionsheet.struts.action;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.loan.util.helpers.LoanAccountsProductDto;
import org.mifos.accounts.loan.util.helpers.LoanAccountDto;
import org.mifos.accounts.savings.util.helpers.SavingsAccountDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryDto;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.customers.util.helpers.CustomerAccountDto;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class CollectionSheetEntryDtoPostPreviewValidator {

    public ActionErrors validate(final CollectionSheetEntryDto collectionSheetEntry, final ActionErrors errors,
            final Locale locale) {

        return validatePopulatedData(collectionSheetEntry, errors, locale);
    }

    private ActionErrors validatePopulatedData(final CollectionSheetEntryDto parent, final ActionErrors errors,
            final Locale locale) {
        List<CollectionSheetEntryDto> children = parent.getCollectionSheetEntryChildren();

        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String acCollections = resources.getString(CollectionSheetEntryConstants.AC_COLLECTION);
        if (null != children) {
            for (CollectionSheetEntryDto collectionSheetEntryDto : children) {
                validatePopulatedData(collectionSheetEntryDto, errors, locale);
            }
        }
        for (LoanAccountsProductDto accountView : parent.getLoanAccountDetails()) {
            if (accountView.isDisburseLoanAccountPresent() || accountView.getLoanAccountViews().size() > 1) {
                Money enteredAmount = new Money(Money.getDefaultCurrency(), 0.0);
                if (null != accountView.getEnteredAmount() && accountView.isValidAmountEntered()) {
                    enteredAmount = new Money(Money.getDefaultCurrency(), getDoubleValue(accountView.getEnteredAmount()));
                }
                Money enteredDisbursalAmount = new Money(Money.getDefaultCurrency(), 0.0);
                if (null != accountView.getDisBursementAmountEntered() && accountView.isValidDisbursementAmount()) {
                    enteredDisbursalAmount = new Money(Money.getDefaultCurrency(), getDoubleValue(accountView.getDisBursementAmountEntered()));
                }
                Money totalDueAmount = new Money(Money.getDefaultCurrency(), accountView.getTotalAmountDue());
                Money totalDisburtialAmount = new Money(Money.getDefaultCurrency(), accountView.getTotalDisburseAmount());
                if (totalDisburtialAmount.isGreaterThanZero()) {
                    if ((!accountView.isValidDisbursementAmount()
                            || accountView.getDisBursementAmountEntered() == null
                            || !enteredDisbursalAmount.toString().equals(totalDisburtialAmount.toString()))
                            && !enteredDisbursalAmount.isZero()) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    }
                }
                if (totalDueAmount.isGreaterThanZero()) {
                    if ((!accountView.isValidAmountEntered()
                            || accountView.getEnteredAmount() == null
                            || !enteredAmount.toString().equals(totalDueAmount.toString()))
                            && !enteredAmount.isZero()) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    }
                }
                boolean moreThanTwoLoanAccountsToPrepaid = isThereMoreThanTwoLoanAccountsToPrepaid(accountView.getLoanAccountViews());
                if (totalDisburtialAmount.isLessThanOrEqualZero() && totalDueAmount.isLessThanOrEqualZero()) {
                    if (!accountView.isValidAmountEntered() || !accountView.isValidDisbursementAmount()
                            || !enteredDisbursalAmount.isZero()
                            || (!moreThanTwoLoanAccountsToPrepaid && !enteredAmount.isZero())) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    } else if (moreThanTwoLoanAccountsToPrepaid && !enteredAmount.isZero()) {
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDPREPAYAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDPREPAYAMOUNT, accountView
                                        .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
                    }
                }
            }
        }
        for (SavingsAccountDto savingsAccountDto : parent.getSavingsAccountDetails()) {
            if (!savingsAccountDto.isValidDepositAmountEntered() || !savingsAccountDto.isValidWithDrawalAmountEntered()) {
                errors.add(CollectionSheetEntryConstants.ERRORINVALIDAMOUNT, new ActionMessage(
                        CollectionSheetEntryConstants.ERRORINVALIDAMOUNT, savingsAccountDto
                                .getSavingsOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
            }
        }
        CustomerAccountDto customerAccountDto = parent.getCustomerAccountDetails();
        Double customerAccountAmountEntered = 0.0;
        if (null != customerAccountDto.getCustomerAccountAmountEntered()
                && customerAccountDto.isValidCustomerAccountAmountEntered()) {
            customerAccountAmountEntered = getDoubleValue(customerAccountDto.getCustomerAccountAmountEntered());
        }

        if (!customerAccountDto.isValidCustomerAccountAmountEntered() ||
                customerAccountAmountEntered < 0.0) {
            errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDACCOLLECTIONS, new ActionMessage(
                    CollectionSheetEntryConstants.BULKENTRYINVALIDACCOLLECTIONS, acCollections, parent
                            .getCustomerDetail().getDisplayName()));
        }
        return errors;
    }

    private boolean isThereMoreThanTwoLoanAccountsToPrepaid(List<LoanAccountDto> loanAccountViews) {
        int counter = 0;

        for (LoanAccountDto loanAccount : loanAccountViews) {
            if (loanAccount.getTotalAmountDue().equals(0.0) && loanAccount.getTotalDisburseAmount().equals(0.0)) {
                counter++;
            }
        }

        return counter >= 2;
    }

    public static Double getDoubleValue(final String str) {
        return StringUtils.isNotBlank(str) ? new LocalizationConverter().getDoubleValueForCurrentLocale(str) : null;
    }

}
