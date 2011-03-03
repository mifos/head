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

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.messagecustomizer.MessageCustomizerServiceFacadeWebTier;
import org.mifos.dto.domain.AccountStatusesLabelDto;
import org.mifos.dto.domain.ConfigurableLookupLabelDto;
import org.mifos.dto.domain.GracePeriodDto;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.screen.ConfigureApplicationLabelsDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

@RunWith(MockitoJUnitRunner.class)
public class MessageCustomizerServiceFacadeWebTierTest {

	private MessageCustomizerServiceFacadeWebTier messageCustomizerServiceFacadeWebTier;

	@Mock
	private MessageCustomizerDao messageCustomizerDao;
	@Mock
	private MessageSource messageSource;	
	
	@Before
    public void setup() {
		messageCustomizerServiceFacadeWebTier = new MessageCustomizerServiceFacadeWebTier(
				messageSource);
	}
	
	@Test
	public void shouldRetrieveConfigurableLabels() {
		ConfigureApplicationLabelsDto labelsDto =
			messageCustomizerServiceFacadeWebTier.retrieveConfigurableLabels(new Locale("en"));
		
		AccountStatusesLabelDto accountStatusesLabelDto = labelsDto.getAccountStatusLabels();
		ConfigurableLookupLabelDto configurableLookupLabelDto = labelsDto.getLookupLabels();
		GracePeriodDto gracePeriodDto = labelsDto.getGracePeriodDto();
		OfficeLevelDto officeLevelDto = labelsDto.getOfficeLevels();
		
		//assertThat(accountStatusesLabelDto.getActive(),is())
	}
}
