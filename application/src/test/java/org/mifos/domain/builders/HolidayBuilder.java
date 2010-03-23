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

package org.mifos.domain.builders;

import org.joda.time.DateTime;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.ApplicationException;

public class HolidayBuilder {

    private DateTime from;
    private DateTime to;
    private RepaymentRuleTypes repaymentRule = RepaymentRuleTypes.NEXT_WORKING_DAY;

    private final Short officeId = Short.valueOf("1");

    public Holiday build() {

        HolidayPK holidayPK = new HolidayPK(officeId, from.toDate());
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity(repaymentRule.getValue(), "lookup.value.key");

        try {
            return new HolidayBO(holidayPK, to.toDate(), "builderCreatedHoliday", repaymentRuleEntity);
        } catch (ApplicationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    public HolidayBuilder from(final DateTime withFrom) {
        this.from = withFrom;
        return this;
    }

    public HolidayBuilder to(final DateTime withTo) {
        this.to = withTo;
        return this;
    }

    public HolidayBuilder withRepaymentRule (RepaymentRuleTypes rule) {
        this.repaymentRule = rule;
        return this;
    }

    public HolidayBuilder withNextMeetingRule() {
        repaymentRule = RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT;
        return this;
    }

    public HolidayBuilder withNextWorkingDayRule() {
        repaymentRule = RepaymentRuleTypes.NEXT_WORKING_DAY;
        return this;
    }

    public HolidayBuilder withSameDayAsRule() {
        repaymentRule = RepaymentRuleTypes.SAME_DAY;
        return this;
    }

    public HolidayBuilder withRepaymentMoratoriumRule() {
        repaymentRule = RepaymentRuleTypes.REPAYMENT_MORATORIUM;
        return this;
    }
}
