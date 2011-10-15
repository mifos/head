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

package org.mifos.accounts.fees.business;

import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AmountFeeBoTest {

    @Mock
    GLCodeEntity mockedBankGLCode;
    @Mock
    MeetingBO mockedMeeting;
    @Mock
    OfficeBO mockedOffice;

    @Test
    public void testEqualsAndHasCode() throws Exception {
        mockedBankGLCode = mock(GLCodeEntity.class);
        mockedMeeting = mock(MeetingBO.class);
        mockedOffice = mock(OfficeBO.class);

        String feeName = "fee name";
        Money feeAmount = TestUtils.createMoney(10);
        Short userId = (short)1;

        AmountFeeBO x = new AmountFeeBO(feeAmount, feeName, FeeCategory.ALLCUSTOMERS, FeeFrequencyType.PERIODIC,
                mockedBankGLCode, mockedMeeting, mockedOffice, new Date(), userId);
        AmountFeeBO notx = new AmountFeeBO(TestUtils.createMoney(11), feeName, FeeCategory.ALLCUSTOMERS, FeeFrequencyType.PERIODIC,
                mockedBankGLCode, mockedMeeting, mockedOffice, new Date(), userId);
        AmountFeeBO sameAsx1 = new AmountFeeBO(feeAmount, feeName, FeeCategory.ALLCUSTOMERS, FeeFrequencyType.PERIODIC,
                mockedBankGLCode, mockedMeeting, mockedOffice, new Date(), userId);
        AmountFeeBO sameAsx2 = new AmountFeeBO(feeAmount, feeName, FeeCategory.ALLCUSTOMERS, FeeFrequencyType.PERIODIC,
                mockedBankGLCode, mockedMeeting, mockedOffice, new Date(), userId);

        TestUtils.assertEqualsAndHashContract(x, notx, sameAsx1, sameAsx2);
    }

}
