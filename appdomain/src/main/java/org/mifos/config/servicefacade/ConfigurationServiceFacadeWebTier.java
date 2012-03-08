package org.mifos.config.servicefacade;

import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.config.service.AccountingConfigurationService;
import org.mifos.config.servicefacade.dto.AccountingConfigurationDto;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigurationServiceFacadeWebTier implements ConfigurationServiceFacade {

    private AccountingConfigurationService accountingConfigurationService;

    @Override
    public AccountingConfigurationDto getAccountingConfiguration() {
        AccountingConfigurationDto dto = new AccountingConfigurationDto();
        dto.setDigitsBeforeDecimal(accountingConfigurationService.getDigitsBeforeDecimal());
        dto.setDigitsAfterDecimal(accountingConfigurationService.getDigitsAfterDecimal());
        dto.setGlCodeMode(accountingConfigurationService.getGlNameMode());
        return dto;
    }

    @Override
    public String getConfig(String key) {
        return MifosConfigurationManager.getInstance().getString(key);
    }

    @Override
    public boolean getBooleanConfig(String key) {
        return MifosConfigurationManager.getInstance().getBoolean(key);
    }

    @Autowired
    public void setAccountingConfigurationService(AccountingConfigurationService accountingConfigurationService) {
        this.accountingConfigurationService = accountingConfigurationService;
    }

}
