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

package org.mifos.application.messagecustomizer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

@RunWith(MockitoJUnitRunner.class)
public class CustomizedTextServiceFacadeWebTierTest {

	private CustomizedTextServiceFacadeWebTier customizedTextServiceFacadeWebTier;

	@Mock
	private MessageSource messageSource;	
	
	@Mock
	private CustomizedTextDao messageCustomizerDao;
	
	@Before
    public void setup() {
		customizedTextServiceFacadeWebTier = new CustomizedTextServiceFacadeWebTier(
				messageCustomizerDao, messageSource);
	}
	
	@Test
	public void shouldConvertAndRemoveLoan() {
		Map<String,String> messageMap = new LinkedHashMap<String,String>();
		messageMap.put("Client.Label", "Borrower");
		messageMap.put("Loan.Label", "Loan");

		Map<String,String> messageMap2 = new LinkedHashMap<String,String>();
		messageMap2.put("Client.Label", "Borrower");
		messageMap2.put("Loan", "Loan");
		
		when(messageSource.getMessage(eq("Client.Label"), any(Object[].class), any(Locale.class))).thenReturn("Client");
		when(messageSource.getMessage(eq("Loan.Label"), any(Object[].class), any(Locale.class))).thenReturn("Loan");

		customizedTextServiceFacadeWebTier = spy(customizedTextServiceFacadeWebTier);
		when(customizedTextServiceFacadeWebTier.retrieveCustomizedText())
			.thenReturn(messageMap)
			.thenReturn(messageMap2);
		doNothing().when(customizedTextServiceFacadeWebTier).addOrUpdateCustomizedText(any(String.class), any(String.class));
		doNothing().when(customizedTextServiceFacadeWebTier).removeCustomizedText(any(String.class));
		
		customizedTextServiceFacadeWebTier.convertMigratedLabelKeysToLocalizedText(Locale.ENGLISH);
		
		verify(customizedTextServiceFacadeWebTier).addOrUpdateCustomizedText("Client", "Borrower");
		verify(customizedTextServiceFacadeWebTier).removeCustomizedText("Client.Label");

		verify(customizedTextServiceFacadeWebTier).addOrUpdateCustomizedText("Loan", "Loan");
		verify(customizedTextServiceFacadeWebTier).removeCustomizedText("Loan.Label");
		verify(customizedTextServiceFacadeWebTier).removeCustomizedText("Loan");
		
	}
	
}
