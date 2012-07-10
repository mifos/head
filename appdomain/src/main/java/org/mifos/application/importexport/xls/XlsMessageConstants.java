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

package org.mifos.application.importexport.xls;

public enum XlsMessageConstants {

    ERROR_READING_DOCUMENT("admin.importexport.xls.error.loadError"),

    NOT_ENOUGH_INPUT_ROW("admin.importexport.xls.error.notEnoughInputRow"), NOT_ENOUGH_INPUT_CELL(
            "admin.importexport.xls.error.notEnoughInputCell"),

    ROW_ERROR("admin.importexport.xls.error.row"), CELL_ERROR("admin.importexport.xls.error.cell"),

    MANDATORY_ERROR("admin.importexport.xls.error.mandatory"), NOT_FOUND_ERROR("admin.importexport.xls.error.notFound"), DUPLICATE_GLOBAL_NUM_ERROR(
            "admin.importexport.xls.error.duplicateGlobalNum"), DUPLICATE_CLIENT_ERROR(
            "admin.importexport.xls.error.duplicateClient"), OFFICE_NOT_FOUND_ERROR(
            "admin.importexport.xls.error.officeNotFound"), NO_OFFICERS_ERROR("admin.importexport.xls.error.noOfficers"), INVALID_DATE(
            "admin.importexport.xls.error.invalidDate"), FUTURE_DATE("admin.importexport.xls.error.futureDate"), INVALID_AGE(
            "admin.importexport.xls.error.invalidAge"), NO_MEETING_ERROR("admin.importexport.xls.error.noMeeting"),

    GROUP_NOT_FOUND_ERROR("admin.importexport.xls.error.groupNotFound"), GROUP_BAD_STATUS(
            "admin.importexport.xls.error.groupBadStatus"), GROUP_CANCELED("admin.importexport.xls.error.groupCanceled"), GROUP_CLOSED(
            "admin.importexport.xls.error.groupClosed"),

    OFFICE_AND_BRANCH("admin.importexport.xls.error.officeAndBranch"),

    INVALID_MONTH("admin.importexport.xls.error.invalidMonth"), NOT_POSITIVE_NUMBER(
            "admin.importexport.xls.error.notPositiveNumber"), SUNDAY_MEETING(
            "admin.importexport.xls.error.sundayMeeting"), INVALID_DAY("admin.importexport.xls.error.invalidDay"), OPTIONS_EXCLUSIVE(
            "admin.importexport.xls.error.meetingOptionsExclusive"), WEEKLY_MEETING_DETAILS_NOT_EMPTY(
            "admin.importexport.xls.error.weeklyMeetingNotEmpty"), MONTHLY_MEETING_DETAILS_NOT_EMPTY(
            "admin.importexport.xls.error.monthlyMeetingNotEmpty"), INCOMPLETE_MEETING_DATA(
            "admin.importexport.xls.error.incompleteMeetingData"), LOAN_OFFICER_FOR_GROUP_CLIENT(
            "admin.importexport.xls.error.loanOfficerForGroupClient"), MEETING_FOR_GROUP(
            "admin.importexport.xls.error.meetingForGroup"),

    SPOUSE("admin.importexport.xls.spouse"), FATHER("admin.importexport.xls.father"),

    PENDING_APPROVAL("ClientStatus-ApplicationPendingApproval"), PARTIALLY_APPROVED("ClientStatus-PartialApplication"), ACTIVE(
            "ClientStatus-Active"),

    DAYRANK_FIRST("DayRank-First"), DAYRANK_SECOND("DayRank-Second"), DAYRANK_THIRD("DayRank-Third"), DAYRANK_FOURTH(
            "DayRank-Fourth"), DAYRANK_LAST("DayRank-Last"),

    WEEKDAYS_MONDAY("WeekDays-Monday"), WEEKDAYS_TUESDAY("WeekDays-Tuesday"), WEEKDAYS_WEDNESDAY("WeekDays-Wednesday"), WEEKDAYS_THURSDAY(
            "WeekDays-Thursday"), WEEKDAYS_FRIDAY("WeekDays-Friday"), WEEKDAYS_SATURDAY("WeekDays-Saturday"), WEEKDAYS_SUNDAY(
            "WeekDays-Sunday"),
            MISSING_ACCOUNT_NUMBER("admin.importexport.xls.error.missingAccountNumber"),
            CUSTOMER_NOT_FOUND("admin.importexport.xls.error.customerNotFound"),
            MISSING_PRODUCT_NAME("admin.importexport.xls.error.missingProductName"),
            PRODUCT_NOT_FOUND("admin.importexport.xls.error.productNotFound"),
            CUSTOMER_FOR_LOAN_INACTIVE("admin.importexport.xls.error.customerForLoanInactive"),
            MISSING_ACCOUNT_STATUS("admin.importexport.xls.error.missingAccountStatus"),
            LOAN_STATUS_NOT_FOUND("admin.importexport.xls.error.loanStatusNotFound"),
            SAVINGS_STATUS_NOT_FOUND("admin.importexport.xls.error.savingsStatusNotFound"),
            MISSING_STATUS_REASON("admin.importexport.xls.error.missingStatusReason"),
            DIFFERENT_MEETING_FREQUENCY("admin.importexport.xls.error.differentMeetingFrequency"),
            WRONG_STATUS_REASON("admin.importexport.xls.error.wrongStatusReason"),
            MISSING_LOAN_AMOUNT("admin.importexport.xls.error.missingLoanAmount"),
            LOAN_AMOUNT_OUF_OF_RANGE("admin.importexport.xls.error.loanAmountOutOfBonds"),
            DIGITS_BEFORE_DECIMAL("loanAccountFormBean.amount.digitsBeforeDecimal.invalid"),
            DIGITS_AFTER_DECIMAL("loanAccountFormBean.amount.digitsAfterDecimal.invalid"),
            MISSING_INTEREST_RATE("admin.importexport.xls.error.missingInterestRate"),
            INTEREST_RATE_OUT_OF_RANGE("admin.importexport.xls.error.interestRateOutOfBonds"),
            NOT_A_NUMBER("admin.importexport.xls.error.notANumber"),
            MISSING_NUMBER_OF_INSTALLMENTS("admin.importexport.xls.error.missingNumberOfInstallments"),
            NUMBER_OF_INSTALLMENTS_OUT_OF_RANGE("admin.importexport.xls.error.numberOfInstallmentsOutOfRange"),
            MISSING_DISBURSAL_DATE("admin.importexport.xls.error.missingDisbursalDate"),
            MISSING_GRACE_PERIOD("admin.importexport.xls.error.missingGracePeriod"),
            GRACE_PERIOD_IS_GREATER_THAN_INSTALLMENTS("loanAccountFormBean.gracePeriodDurationInRelationToInstallments.invalid"),
            GRACE_PERIOD_EXCEEDS_PRODUCT_DEFINITION("loanAccountFormBean.gracePeriodDuration.invalid"),
            WRONG_ACCOUNT_NUMBER("admin.importexport.xls.error.wrongAccountNumber"),
            UNKNOWN_SOURCE_OF_FUND("admin.importexport.xls.error.unknownSourceOfFound"),
            UNKNOWN_LOAN_PURPOSE("admin.importexport.xls.error.unknownLoanPurpose"),
            UNKNOWN_COLLATERAL_TYPE("admin.importexport.xls.error.unknownCollateralType");

    private final String text;

    XlsMessageConstants(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static XlsMessageConstants fromString(String text) {
        if (text != null) {
            for (XlsMessageConstants b : XlsMessageConstants.values()) {
                if (text.equals(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

}