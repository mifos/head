/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class FeeBuilder {

    private final GLCodeEntity feeGLCode = new GLCodeEntity(Short.valueOf("1"), "10000");
    private MeetingBuilder meetingPeriodicity = new MeetingBuilder().periodicFeeMeeting().weekly().every(1);
    private FeeFrequencyType feeFrequencyType = FeeFrequencyType.PERIODIC;
    private String name = "weekly-client-periodic-fee";
    private FeeCategory category = FeeCategory.CLIENT;
    private Money feeAmount = new Money(TestUtils.RUPEE, "12.5");
    private FeePayment feePayment = FeePayment.UPFRONT;
    private final Date createdDate = new DateTime().minusDays(14).toDate();
    private final UserContext createdByUser = TestUtils.makeUserWithLocales();
    private OfficeBO office;

    public AmountFeeBO build() {
        AmountFeeBO fee = null;
        try {
        if (feeFrequencyType == FeeFrequencyType.PERIODIC) {
            fee = new AmountFeeBO(feeAmount, name, category, feeFrequencyType, feeGLCode,
                meetingPeriodicity.build(), office, createdDate, createdByUser.getId());
        } else { //one-time defaults to up-front payment
            fee = new AmountFeeBO(TestUtils.makeUserWithLocales(), name, new CategoryTypeEntity(category),
                    new FeeFrequencyTypeEntity(feeFrequencyType), feeGLCode, feeAmount, false,
                    new FeePaymentEntity(feePayment));
        }
        } catch (FeeException e) {
            throw new MifosRuntimeException("Error building AmountFeeBO", e);
        }
        return fee;
    }

    public FeeBuilder appliesToAllCustomers() {
        this.category = FeeCategory.ALLCUSTOMERS;
        return this;
    }

    public FeeBuilder appliesToCenterOnly() {
        this.category = FeeCategory.CENTER;
        return this;
    }

    public FeeBuilder appliesToGroupsOnly() {
        this.category = FeeCategory.GROUP;
        return this;
    }

    public FeeBuilder appliesToClientsOnly() {
        this.category = FeeCategory.CLIENT;
        return this;
    }

    public FeeBuilder appliesToLoans() {
        this.category = FeeCategory.LOAN;
        return this;
    }

    public FeeBuilder withFeeAmount(final String withFeeAmount) {
        this.feeAmount = new Money(TestUtils.RUPEE, withFeeAmount);
        return this;
    }

    public FeeBuilder withName(final String withName) {
        this.name = withName;
        return this;
    }

    public FeeBuilder withSameRecurrenceAs(final MeetingBO meeting) {
        this.meetingPeriodicity.withSameRecurrenceAs(meeting);
        return this;
    }

    public FeeBuilder with(final OfficeBO withOffice) {
        this.office = withOffice;
        return this;
    }

    public FeeBuilder with(MeetingBuilder withMeeting) {
        this.meetingPeriodicity = withMeeting;
        return this;
    }

    public FeeBuilder withFeeFrequency(FeeFrequencyType withFeeFrequency) {
        this.feeFrequencyType = withFeeFrequency;
        return this;
    }

    public FeeBuilder withFeePayment (FeePayment withFeePayment) {
        this.feePayment = withFeePayment;
        return this;
    }
}
