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
