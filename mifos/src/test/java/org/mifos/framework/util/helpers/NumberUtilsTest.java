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
 
package org.mifos.framework.util.helpers;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class NumberUtilsTest extends TestCase {
	public void testPercentageReturnsZeroIfFullValueIsZero() throws Exception {
		BigDecimal percentage = NumberUtils.getPercentage(null, Integer
				.valueOf(0));
		assertEquals(0d, percentage.doubleValue());
	}

	public void testPercentage() throws Exception {
		assertEquals(25d, NumberUtils.getPercentage(Integer.valueOf(1),
				Integer.valueOf(4)).doubleValue());
	}
}
