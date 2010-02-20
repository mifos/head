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
package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.config.AccountingRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.AttendanceType;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

/**
 * Validates customer and account structure for SaveCollectionSheetDto.
 * 
 * Note: Accounts with only Zero amount(s) are ignored and not validated (or processed by the save collection sheet
 * functionality)
 */
public class SaveCollectionSheetStructureValidator {

    private final CustomerPersistence customerPersistence;
    private final AccountPersistence accountPersistence;
    private final Short mifosCurrencyId;

    private List<InvalidSaveCollectionSheetReason> validationErrors = new ArrayList<InvalidSaveCollectionSheetReason>();
    private List<String> validationErrorsExtended = new ArrayList<String>();
    private final List<CustomerStatus> validCustomerStatuses = new ArrayList<CustomerStatus>();
    private final List<AccountState> validLoanAccountStates = new ArrayList<AccountState>();
    private final List<AccountState> validSavingsAccountStates = new ArrayList<AccountState>();
    private CustomerBO topCustomer;
    private List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers;

    private enum ValidationAccountTypes {
        CUSTOMER, LOAN, SAVINGS, INDIVIDUAL_SAVINGS;
    }

    public SaveCollectionSheetStructureValidator() {

        this.customerPersistence = new CustomerPersistence();
        this.accountPersistence = new AccountPersistence();
        this.mifosCurrencyId = Money.getDefaultCurrency().getCurrencyId();

        validCustomerStatuses.add(CustomerStatus.CLIENT_ACTIVE);
        validCustomerStatuses.add(CustomerStatus.CLIENT_HOLD);
        validCustomerStatuses.add(CustomerStatus.GROUP_ACTIVE);
        validCustomerStatuses.add(CustomerStatus.GROUP_HOLD);
        validCustomerStatuses.add(CustomerStatus.CENTER_ACTIVE);

        validLoanAccountStates.add(AccountState.LOAN_APPROVED);
        validLoanAccountStates.add(AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER);
        validLoanAccountStates.add(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
        validLoanAccountStates.add(AccountState.LOAN_ACTIVE_IN_BAD_STANDING);

        validSavingsAccountStates.add(AccountState.SAVINGS_INACTIVE);
        validSavingsAccountStates.add(AccountState.SAVINGS_ACTIVE);
    }

    public void execute(SaveCollectionSheetDto saveCollectionSheet) throws SaveCollectionSheetException {

        saveCollectionSheetCustomers = saveCollectionSheet.getSaveCollectionSheetCustomers();

        Boolean isTopCustomer = true;
        for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : saveCollectionSheetCustomers) {

            CustomerBO customer;
            Integer currentErrorCount = validationErrors.size();
            try {
                customer = customerPersistence.getCustomer(saveCollectionSheetCustomer.getCustomerId());
            } catch (PersistenceException e) {
                throw new MifosRuntimeException(e);
            }

            if (customer == null) {
                if (isTopCustomer) {
                    addValidationError(InvalidSaveCollectionSheetReason.INVALID_TOP_CUSTOMER, "Customer Id: "
                            + saveCollectionSheetCustomer.getCustomerId());
                    throw new SaveCollectionSheetException(validationErrors, validationErrorsExtended);
                }
                addValidationError(InvalidSaveCollectionSheetReason.CUSTOMER_NOT_FOUND, "Customer Id: "
                        + saveCollectionSheetCustomer.getCustomerId());
            } else {
                if (isTopCustomer) {
                    topCustomer = customer;
                }
                validateCustomer(customer, saveCollectionSheetCustomer);
            }

            if (currentErrorCount.compareTo(validationErrors.size()) == 0) {
                validateAttendanceType(customer, saveCollectionSheetCustomer.getAttendanceId());

                validateSaveCollectionSheetCustomerAccount(saveCollectionSheetCustomer.getCustomerId(),
                        saveCollectionSheetCustomer.getSaveCollectionSheetCustomerAccount());

                validateSaveCollectionSheetCustomerLoans(saveCollectionSheetCustomer.getCustomerId(),
                        saveCollectionSheetCustomer.getSaveCollectionSheetCustomerLoans());

                validateSaveCollectionSheetCustomerSavings(saveCollectionSheetCustomer.getCustomerId(),
                        saveCollectionSheetCustomer.getSaveCollectionSheetCustomerSavings(),
                        ValidationAccountTypes.SAVINGS);

                validateSaveCollectionSheetCustomerSavings(saveCollectionSheetCustomer.getCustomerId(),
                        saveCollectionSheetCustomer.getSaveCollectionSheetCustomerIndividualSavings(),
                        ValidationAccountTypes.INDIVIDUAL_SAVINGS);
            }

            isTopCustomer = false;
        }

        LocalDate validMeetingDate = getValidMeetingDateForTopCustomer(topCustomer.getCustomerId());
        if (saveCollectionSheet.getTransactionDate().compareTo(validMeetingDate) != 0) {
            addValidationError(InvalidSaveCollectionSheetReason.INVALID_DATE, "Transaction Date: "
                    + saveCollectionSheet.getTransactionDate());
            throw new SaveCollectionSheetException(validationErrors, validationErrorsExtended);
        }

        if (validationErrors.size() > 0) {
            throw new SaveCollectionSheetException(validationErrors, validationErrorsExtended);
        }
    }

    private void validateCustomer(CustomerBO customer, SaveCollectionSheetCustomerDto saveCollectionSheetCustomer) {

        if (!(validCustomerStatuses.contains(customer.getStatus()))) {
            addValidationError(InvalidSaveCollectionSheetReason.INVALID_CUSTOMER_STATUS, "Customer Id: "
                    + saveCollectionSheetCustomer.getCustomerId() + " Customer Status: "
                    + customer.getStatus().toString());
        }
        if (customer.getLevel().compareTo(CustomerLevel.CLIENT) != 0
                && saveCollectionSheetCustomer.getSaveCollectionSheetCustomerIndividualSavings() != null
                && saveCollectionSheetCustomer.getSaveCollectionSheetCustomerIndividualSavings().size() > 0) {
            addValidationError(InvalidSaveCollectionSheetReason.INDIVIDUAL_SAVINGS_ACCOUNTS_ONLY_VALID_FOR_CLIENTS,
                    "Customer Id: " + saveCollectionSheetCustomer.getCustomerId() + " Customer Level: "
                            + customer.getLevel().toString());
        }

        Integer customerParentId = null;
        if (customer.getParentCustomer() != null) {
            customerParentId = customer.getParentCustomer().getCustomerId();
        }
        if (customerParentInvalid(customerParentId, saveCollectionSheetCustomer.getParentCustomerId())) {
            addValidationError(InvalidSaveCollectionSheetReason.INVALID_CUSTOMER_PARENT, "Customer Id: "
                    + saveCollectionSheetCustomer.getCustomerId() + " Customer Parent Id Should be: "
                    + customerParentId);
        }

        if (customerNotPartOfTopCustomerHierarchy(customer.getOffice().getOfficeId(), customer.getSearchId())) {
            addValidationError(InvalidSaveCollectionSheetReason.CUSTOMER_IS_NOT_PART_OF_TOPCUSTOMER_HIERARCHY,
                    "Customer Id: " + saveCollectionSheetCustomer.getCustomerId());
        }

    }

    private boolean customerParentInvalid(Integer parentId, Integer parentIdDto) {
        if (parentId == null && parentIdDto == null) {
            return false;
        }
        if (parentId != null && parentIdDto == null) {
            return true;
        }
        if (parentId == null && parentIdDto != null) {
            return true;
        }

        if (parentId.compareTo(parentIdDto) == 0) {
            return false;
        }
        return true;
    }

    private boolean customerNotPartOfTopCustomerHierarchy(Short branchId, String searchId) {
        // must have the same branchId and either the same searchId or the 'top
        // searchid' plus a dot must match

        if (branchId == null || searchId == null) {
            return true;
        }
        if (branchId.compareTo(topCustomer.getOffice().getOfficeId()) != 0) {
            return true;
        }
        if ((searchId.compareTo(topCustomer.getSearchId()) != 0)
                && (!(searchId.startsWith(topCustomer.getSearchId() + ".")))) {
            return true;
        }
        return false;
    }

    private void validateSaveCollectionSheetCustomerAccount(Integer customerId,
            SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount) {

        if (null != saveCollectionSheetCustomerAccount) {
            
            if (saveCollectionSheetCustomerAccount.getTotalCustomerAccountCollectionFee().compareTo(BigDecimal.ZERO) > 0) {
                validateAccount(customerId, saveCollectionSheetCustomerAccount.getAccountId(),
                        saveCollectionSheetCustomerAccount.getCurrencyId(), ValidationAccountTypes.CUSTOMER);
            }
        }
    }

    private void validateSaveCollectionSheetCustomerLoans(Integer customerId,
            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans) {

        if (null != saveCollectionSheetCustomerLoans && saveCollectionSheetCustomerLoans.size() > 0) {
            for (SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan : saveCollectionSheetCustomerLoans) {

                if ((saveCollectionSheetCustomerLoan.getTotalDisbursement().compareTo(BigDecimal.ZERO) > 0)
                        || (saveCollectionSheetCustomerLoan.getTotalLoanPayment().compareTo(BigDecimal.ZERO) > 0)) {
                    validateAccount(customerId, saveCollectionSheetCustomerLoan.getAccountId(),
                            saveCollectionSheetCustomerLoan.getCurrencyId(), ValidationAccountTypes.LOAN);
                }
            }
        }
    }

    private void validateSaveCollectionSheetCustomerSavings(Integer customerId,
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings,
            ValidationAccountTypes accountType) {

        if (null != saveCollectionSheetCustomerSavings && saveCollectionSheetCustomerSavings.size() > 0) {
            for (SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving : saveCollectionSheetCustomerSavings) {

                if ((saveCollectionSheetCustomerSaving.getTotalWithdrawal().compareTo(BigDecimal.ZERO) > 0)
                        || (saveCollectionSheetCustomerSaving.getTotalDeposit().compareTo(BigDecimal.ZERO) > 0)) {
                    validateAccount(customerId, saveCollectionSheetCustomerSaving.getAccountId(),
                            saveCollectionSheetCustomerSaving.getCurrencyId(), accountType);
                }
            }
        }
    }

    private void validateAccount(Integer customerId, Integer accountId, Short currencyId,
            ValidationAccountTypes accountType) {

        AccountBO account;
        try {
            account = accountPersistence.getAccount(accountId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        if (account == null) {
            addValidationError(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_FOUND, "Customer Id: " + customerId
                    + " Account Id: " + accountId);
            return;
        }
        if (!validAccountOwnership(account, customerId, accountType)) {
            addValidationError(InvalidSaveCollectionSheetReason.ACCOUNT_DOESNT_BELONG_TO_CUSTOMER, "Customer Id: "
                    + customerId + " Account Id: " + accountId);
            return;
        }

        validateCurrency(currencyId);

        if (accountType.compareTo(ValidationAccountTypes.CUSTOMER) == 0) {
            if (!(account instanceof CustomerAccountBO)) {
                addValidationError(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_A_CUSTOMER_ACCOUNT, "Customer Id: "
                        + customerId + " Account Id: " + accountId);
            }
            return;
        }
        if (accountType.compareTo(ValidationAccountTypes.LOAN) == 0) {
            if (!(account instanceof LoanBO)) {
                addValidationError(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_A_LOAN_ACCOUNT, "Customer Id: "
                        + customerId + " Account Id: " + accountId);
            } else {

                if (!(validLoanAccountStates.contains(account.getState()))) {
                    addValidationError(InvalidSaveCollectionSheetReason.INVALID_LOAN_ACCOUNT_STATUS, "Customer Id: "
                            + customerId + " Account Id: " + accountId + " Account Status: "
                            + account.getState().toString());
                }
            }
            return;
        }
        if ((accountType.compareTo(ValidationAccountTypes.SAVINGS) == 0)
                || (accountType.compareTo(ValidationAccountTypes.INDIVIDUAL_SAVINGS) == 0)) {
            if (!(account instanceof SavingsBO)) {
                addValidationError(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_A_SAVINGS_ACCOUNT, "Customer Id: "
                        + customerId + " Account Id: " + accountId);
            } else {

                if (!(validSavingsAccountStates.contains(account.getState()))) {
                    addValidationError(InvalidSaveCollectionSheetReason.INVALID_SAVINGS_ACCOUNT_STATUS, "Customer Id: "
                            + customerId + " Account Id: " + accountId + " Account Status: "
                            + account.getState().toString());
                }
            }
            return;
        }

        throw new MifosRuntimeException("Account Type: " + accountType.toString() + " not recognised");
    }

    private boolean validAccountOwnership(AccountBO account, Integer customerIdDto, ValidationAccountTypes accountType) {

        Integer accountCustomerId = account.getCustomer().getCustomerId();

        if (accountType.compareTo(ValidationAccountTypes.INDIVIDUAL_SAVINGS) != 0) {
            if (accountCustomerId.compareTo(customerIdDto) == 0) {
                return true;
            }
            return false;
        }

        // For individual savings accounts, the persisted account id should
        // never match with
        // the customer id in the dto
        if (accountCustomerId.compareTo(customerIdDto) == 0) {
            return false;
        }

        // For individual savings accounts, the account id should appear in the
        // normal savings accounts of a higher level customer (GROUP or CENTER)
        if (!onHigherLevelCustomer(customerIdDto, account.getAccountId())) {
            return false;
        }

        return true;
    }

    private boolean onHigherLevelCustomer(Integer customerIdDto, Integer accountId) {

        SaveCollectionSheetCustomerDto currentCustomerDto = findSaveCollectionSheetCustomerDto(customerIdDto);

        if (currentCustomerDto == null) {
            throw new MifosRuntimeException("Couldn't find the SaveCollectionSheetCustomerDto for " + customerIdDto);
        }

        SaveCollectionSheetCustomerDto parentCustomerDto = findSaveCollectionSheetCustomerDto(currentCustomerDto
                .getParentCustomerId());

        if (parentCustomerDto == null) {
            return false;
        }

        if (accountIdOnCustomerDto(parentCustomerDto, accountId)) {
            return true;
        }

        SaveCollectionSheetCustomerDto granparentCustomerDto = findSaveCollectionSheetCustomerDto(parentCustomerDto
                .getParentCustomerId());

        if (granparentCustomerDto == null) {
            return false;
        }

        if (accountIdOnCustomerDto(granparentCustomerDto, accountId)) {
            return true;
        }

        return false;
    }

    private boolean accountIdOnCustomerDto(SaveCollectionSheetCustomerDto customerDto, Integer accountId) {

        if (customerDto.getSaveCollectionSheetCustomerSavings() != null
                && customerDto.getSaveCollectionSheetCustomerSavings().size() > 0) {

            for (SaveCollectionSheetCustomerSavingDto CustomerSaving : customerDto
                    .getSaveCollectionSheetCustomerSavings()) {
                if (CustomerSaving.getAccountId().compareTo(accountId) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private SaveCollectionSheetCustomerDto findSaveCollectionSheetCustomerDto(Integer customerIdDto) {

        if (customerIdDto == null) {
            return null;
        }

        for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : saveCollectionSheetCustomers) {
            if (saveCollectionSheetCustomer.getCustomerId().compareTo(customerIdDto) == 0) {
                return saveCollectionSheetCustomer;
            }
        }
        return null;
    }

    private void validateAttendanceType(CustomerBO customer, Short attendanceType) {

        if (customer.getLevel().compareTo(CustomerLevel.CLIENT) == 0) {
            if (attendanceType == null) {
                addValidationError(InvalidSaveCollectionSheetReason.ATTENDANCE_TYPE_NULL, "Client Id: "
                        + customer.getCustomerId());
            } else {

                for (AttendanceType at : AttendanceType.values()) {
                    if (at.getValue() == attendanceType) {
                        return;
                    }
                }
                addValidationError(InvalidSaveCollectionSheetReason.UNSUPPORTED_ATTENDANCE_TYPE, "Client Id: "
                        + customer.getCustomerId() + " Attendance Type Id: " + attendanceType);
            }
        } else {
            if (attendanceType != null) {
                addValidationError(InvalidSaveCollectionSheetReason.ATTENDANCE_TYPE_ONLY_VALID_FOR_CLIENTS,
                        "Customer Id: " + customer.getCustomerId() + " Attendance Type Id: " + attendanceType);
            }
        }
    }

    private void validateCurrency(Short currencyId) {
        // TODO This needs to change if multi-currency is implemented in
        // mifos
        if (currencyId.compareTo(mifosCurrencyId) != 0) {
            addValidationError(InvalidSaveCollectionSheetReason.INVALID_CURRENCY, "Currency Id: " + currencyId);
        }
    }

    private LocalDate getValidMeetingDateForTopCustomer(Integer customerId) {

        if (AccountingRules.isBackDatedTxnAllowed()) {

            try {
                Date meetingDate = customerPersistence.getLastMeetingDateForCustomer(customerId);

                if (meetingDate == null) {
                    // shouldn't occur but returning current date is okay here
                    return new LocalDate();
                }
                return DateUtils.getLocalDateFromDate(meetingDate);
            } catch (PersistenceException e) {
                throw new MifosRuntimeException(e);
            }
        }

        return new LocalDate();
    }

    private void addValidationError(InvalidSaveCollectionSheetReason invalidSaveCollectionSheetReason,
            String extendedMessage) {
        validationErrors.add(invalidSaveCollectionSheetReason);
        validationErrorsExtended.add(invalidSaveCollectionSheetReason.toString() + ": " + extendedMessage);
    }
}
