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
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class FeeBuilder {
    
    private final GLCodeEntity feeGLCode = new GLCodeEntity(Short.valueOf("1"), "10000");
    private final MeetingBuilder meetingPeriodicity = new MeetingBuilder().periodicFeeMeeting().weekly().every(1);
    private final FeeFrequencyType feeFrequencyType = FeeFrequencyType.PERIODIC;
    private String name = "weekly-client-periodic-fee";
    private FeeCategory category = FeeCategory.CLIENT;
    private Money feeAmount = new Money(TestUtils.getCurrency(), "12.5");
    
    private final Date createdDate = new DateTime().minusDays(14).toDate();
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private OfficeBO office;
    
    public AmountFeeBO build() {
        
        final AmountFeeBO fee = new AmountFeeBO(feeAmount, name, category, feeFrequencyType, feeGLCode,
                meetingPeriodicity.build(), office, createdDate, createdByUserId);
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
    
    public FeeBuilder withFeeAmount(final String withFeeAmount) {
        this.feeAmount = new Money(TestUtils.getCurrency(), withFeeAmount);
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

    public FeeBuilder withOffice(final OfficeBO withOffice) {
        this.office = withOffice;
        return this;
    }
}
