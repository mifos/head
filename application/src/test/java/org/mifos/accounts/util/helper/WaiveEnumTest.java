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

package org.mifos.accounts.util.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mifos.accounts.util.helpers.WaiveEnum;

public class WaiveEnumTest {

    @Test
    public void feeWaiveEnumHasOrdinalOfZero() {

        assertThat(WaiveEnum.FEES.ordinal(), is(0));
    }

    @Test
    public void penaltyWaiveEnumHasOrdinalOfOne() {

        assertThat(WaiveEnum.PENALTY.ordinal(), is(1));
    }

    @Test
    public void shouldReturnFeeWaiveEnum() {

        WaiveEnum result = WaiveEnum.fromInt(0);
        assertThat(result, is(WaiveEnum.FEES));
    }

    @Test
    public void shouldReturnPenaltyWaiveEnum() {

        WaiveEnum result = WaiveEnum.fromInt(1);
        assertThat(result, is(WaiveEnum.PENALTY));
    }

    @Test
    public void shouldReturnAllWaiveEnumForValuesNotEqualToZeroOrOne() {

        WaiveEnum result = WaiveEnum.fromInt(-1);
        assertThat(result, is(WaiveEnum.ALL));
    }

    @Test
    public void shouldReturnAllWaiveEnumForValuesNotEqualToZeroOrOne_2() {

        WaiveEnum result = WaiveEnum.fromInt(2);
        assertThat(result, is(WaiveEnum.ALL));
    }


}
