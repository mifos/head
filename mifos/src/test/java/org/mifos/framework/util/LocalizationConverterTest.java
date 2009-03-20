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
 
package org.mifos.framework.util;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.util.Calendar;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mifos.framework.util.helpers.DateUtils;


public class LocalizationConverterTest {
	@Test public void testDateFormattingWithFourDigitsInYear() throws Exception {
		LocalizationConverter instance = LocalizationConverter.getInstance();
		DateFormat dateFormat = instance.getDateFormatWithFullYear();
		assertEquals("13/12/2008", dateFormat.format(DateUtils.getDate(2008, Calendar.DECEMBER, 13))); 
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LocalizationConverterTest.class);
	}
}
