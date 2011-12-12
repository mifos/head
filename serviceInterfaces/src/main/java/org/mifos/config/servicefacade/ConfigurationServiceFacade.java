package org.mifos.config.servicefacade;

import org.mifos.config.servicefacade.dto.AccountingConfigurationDto;

public interface ConfigurationServiceFacade {

    AccountingConfigurationDto getAccountingConfiguration();

    String getConfig(String key);

}
