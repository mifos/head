package org.mifos.rest.approval.stub;

import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.config.servicefacade.dto.AccountingConfigurationDto;
import org.mifos.rest.config.RESTConfigKey;
import org.springframework.stereotype.Service;

@Service
public class StubConfigurationServiceFacade implements ConfigurationServiceFacade {

    @Override
    public AccountingConfigurationDto getAccountingConfiguration() {
        return null;
    }

    @Override
    public String getConfig(String key) {
        return RESTConfigKey.TRUE;
    }

}
