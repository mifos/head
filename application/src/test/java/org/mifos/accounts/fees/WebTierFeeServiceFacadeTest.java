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

package org.mifos.accounts.fees;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.accounts.fees.servicefacade.FeeFrequencyDto;
import org.mifos.accounts.fees.servicefacade.FeeServiceFacade;
import org.mifos.accounts.fees.servicefacade.WebTierFeeServiceFacade;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class WebTierFeeServiceFacadeTest {
    @Mock
    FeeBusinessService feeBusinessService;
    @Mock
    RateFeeBO feeBo;
    @Mock
    CategoryTypeEntity categoryType;
    @Mock
    FeeFrequencyEntity feeFrequencyEntity;
    @Mock
    FeeFrequencyTypeEntity feeFrequencyTypeEntity;
    @Mock
    FeeStatusEntity feeStatusEntity;
    @Mock
    FeePaymentEntity feePaymentEntity;
    @Mock
    MeetingBO customerMeeting;
    @Mock
    GLCodeEntity glCodeEntity;
    @Mock
    FeeFormulaEntity feeFormulaEntity;
    FeeServiceFacade feeServiceFacade;
    Short fakeFeeId = (short) 1;

    @Before
    public void setUpBeforeTest() {
        feeServiceFacade = new WebTierFeeServiceFacade(feeBusinessService);
    }

    @After
    public void tearDownAfterTest() {
        verify(feeBusinessService, times(1)).getFee(fakeFeeId);
    }

    @Test
    public void getFeeDetailsDelegatesToFeeBusinessService() throws Exception {
        setUpFeeFrequencyExpectations();
        setUpFeeBOExpectations();
        setUpAdditionalExpectations();
        when(feeBusinessService.getFee(fakeFeeId)).thenReturn(feeBo);

        FeeDto feeDetails = feeServiceFacade.getFeeDetails(fakeFeeId);
        assertThat(feeDetails, is(notNullValue()));
        assertThat(feeDetails.getName(), is("My Fee"));
        assertThat(feeDetails.getGlCode(), is("My GL Code"));
        assertFeeFrequency(feeDetails.getFeeFrequency());
    }

    private void assertFeeFrequency(FeeFrequencyDto feeFrequency) {
        assertThat(feeFrequency, is(notNullValue()));
        assertThat(feeFrequency.getType(), is("Monthly Type"));
        assertThat(feeFrequency.isMonthly(), is(true));
    }

    private void setUpAdditionalExpectations() {
        when(glCodeEntity.getGlcode()).thenReturn("My GL Code");
        when(customerMeeting.isMonthly()).thenReturn(true);
    }

    private void setUpFeeFrequencyExpectations() {
        when(feeFrequencyEntity.getFeeFrequencyType()).thenReturn(feeFrequencyTypeEntity);
        when(feeFrequencyEntity.getFeeMeetingFrequency()).thenReturn(customerMeeting);
        when(feeFrequencyEntity.getFeePayment()).thenReturn(feePaymentEntity);
        when(feeFrequencyTypeEntity.getName()).thenReturn("Monthly Type");
    }

    private void setUpFeeBOExpectations() throws Exception {
        when(feeFormulaEntity.getName()).thenReturn("My Formula");
        when(feeBo.getFeeName()).thenReturn("My Fee");
        when(feeBo.getFeeFrequency()).thenReturn(feeFrequencyEntity);
        when(feeBo.getFeeFormula()).thenReturn(feeFormulaEntity);
        when(feeBo.getCategoryType()).thenReturn(categoryType);
        when(feeBo.getGlCode()).thenReturn(glCodeEntity);
        when(feeBo.getFeeStatus()).thenReturn(feeStatusEntity);
        when(feeBo.getFeeChangeType()).thenReturn(FeeChangeType.NOT_UPDATED);
    }
}
