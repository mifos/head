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

package org.mifos.accounts.loan.struts.uihelpers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;

/**
 * This class has got helper functions which could be called from jsp as part of
 * jsp2.0 specifications.
 */
public class LoanUIHelperFn {

    private static ConfigurationBusinessService configService = new ConfigurationBusinessService();

    public LoanUIHelperFn() {
        super();
    }

    public static String getCurrrentDate(Locale locale) throws InvalidDateException {
        return DateUtils.getCurrentDate(locale);
    }

    public static String getMeetingRecurrence(Object meeting, Object userContext) {
        return meeting != null ? new MeetingHelper().getMessageWithFrequency((MeetingBO) meeting,
                (UserContext) userContext) : null;
    }

    public static String getDoubleValue(Double value) {
        if (value != null) {
            return BigDecimal.valueOf(value).toString();
        }
        return "0.0";
    }

    static boolean isDisabledWhileEditingGlim(String fieldName, AccountState accountState,
            ConfigurationBusinessService configService) {
        if (!configService.isGlimEnabled()) {
            return false;
        }

        if (accountState == null) {
            return false;
        }

        if (Arrays.asList(AccountState.LOAN_PARTIAL_APPLICATION, AccountState.LOAN_PENDING_APPROVAL).contains(
                accountState)) {
            return false;
        }
        if (Arrays.asList(AccountState.LOAN_ACTIVE_IN_BAD_STANDING,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING).contains(accountState)){
            return Arrays.asList("clientDetails.loanAmount",
                    "clientDetails.clientId",
                    "repaymentDay"
                    ).contains(fieldName);
        }
        else if (accountState.equals(AccountState.LOAN_APPROVED)) {
            // disabling only the GLIM parts of the loan
            return Arrays.asList("clientDetails.loanAmount", "clientDetails.clientId"
            // ,"noOfInstallments",
                    // "disbursementDate",
                    // "gracePeriod",
                    // "collateralType",
                    // "collateralNotes",
                    // "customField","disbursementDate",
                    // "weekDayId", "ordinalOfMonth"
                    ).contains(fieldName);
        }
        return true;
    }

    public static boolean isDisabledWhileEditingGlim(String fieldName, AccountState accountState) {
        return isDisabledWhileEditingGlim(fieldName, accountState, configService);
    }

}
