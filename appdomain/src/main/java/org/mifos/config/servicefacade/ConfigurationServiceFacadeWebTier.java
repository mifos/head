package org.mifos.config.servicefacade;

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
        return dto;
    }

    @Autowired
    public void setAccountingConfigurationService(AccountingConfigurationService accountingConfigurationService) {
        this.accountingConfigurationService = accountingConfigurationService;
    }

}
